# Modul 3: Builder Pattern & Repository Pattern Integration

## Lernziele
- Builder Pattern für komplexe Objekterstellung meistern
- Repository Pattern mit Builder kombinieren
- Fluent Interfaces designen und umsetzen
- Liskov Substitution Principle praktisch anwenden

## 1. Problem-Motivation

### Ausgangssituation: Telekom Datenbank-Queries
Telekom-Systeme arbeiten mit komplexen Datenbankabfragen - Kunden können nach vielen Kriterien gefiltert werden, verschiedene Joins sind nötig, Performance-Optimierungen müssen berücksichtigt werden.

### Herausforderungen
- Komplexe Query-Erstellung mit vielen optionalen Parametern
- Verschiedene Datenbanken (Oracle, PostgreSQL, MongoDB)
- Performance-kritische Abfragen mit Indizierung
- Type-sichere Query-Erstellung

### Problematischer Code
```java
public class CustomerRepository {
    
    // Code-Smell: Telescoping Constructor
    public List<Customer> findCustomers(String name, String contractType, 
                                       String tariff, Date contractStart, 
                                       Date contractEnd, String city, 
                                       String postalCode, Boolean isActive,
                                       String paymentMethod, Integer minRevenue,
                                       String sortBy, String sortOrder,
                                       Integer limit, Integer offset) {
        
        // Code-Smell: Complex Method mit StringBuilder-Chaos
        StringBuilder sql = new StringBuilder("SELECT * FROM customers c ");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;
        
        if (name != null && !name.isEmpty()) {
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("c.name LIKE ?");
            params.add("%" + name + "%");
            hasWhere = true;
        }
        
        if (contractType != null && !contractType.isEmpty()) {
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("c.contract_type = ?");
            params.add(contractType);
            hasWhere = true;
        }
        
        if (tariff != null && !tariff.isEmpty()) {
            sql.append(" JOIN tariffs t ON c.tariff_id = t.id");
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("t.name = ?");
            params.add(tariff);
            hasWhere = true;
        }
        
        // ... weitere 20 Zeilen if-Statements
        
        if (sortBy != null) {
            sql.append(" ORDER BY ").append(sortBy);
            if (sortOrder != null) {
                sql.append(" ").append(sortOrder);
            }
        }
        
        if (limit != null && limit > 0) {
            sql.append(" LIMIT ").append(limit);
            if (offset != null && offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        }
        
        return jdbcTemplate.query(sql.toString(), params.toArray(), customerRowMapper);
    }
}
```

### Code-Smell Analyse
1. **Telescoping Constructor**: Zu viele Parameter in Methode
2. **Complex Method**: Eine Methode macht zu viel
3. **Primitive Obsession**: Viele String/Integer Parameter statt Value Objects
4. **String Concatenation**: Unsichere SQL-Erstellung
5. **Boolean Parameters**: Schwer verständliche boolean-Flags
6. **Open/Closed Violation**: Neue Filter erfordern Methodenänderung

## 2. Builder Pattern - Struktur

### Konzept
Builder Pattern trennt die Konstruktion komplexer Objekte von deren Repräsentation, so dass der gleiche Konstruktionsprozess verschiedene Darstellungen erstellen kann.

### UML-Struktur
```
Director
  +construct(): Product
  
Builder (interface)
  +buildPartA()
  +buildPartB()
  +getResult(): Product
  
ConcreteBuilder implements Builder
  +buildPartA()
  +buildPartB() 
  +getResult(): ConcreteProduct
```

### Fluent Interface Variation
```java
QueryBuilder.forEntity("Customer")
    .where("name").like("Schmidt")
    .and("contractType").equals("BUSINESS")
    .join("tariffs").on("tariff_id")
    .orderBy("name").ascending()
    .limit(50)
    .build();
```

## 3. Repository Pattern Integration

### Repository Pattern Grundlagen
```java
public interface Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void delete(T entity);
}

public interface CustomerRepository extends Repository<Customer, String> {
    List<Customer> findByComplexCriteria(CustomerSearchCriteria criteria);
}
```

## 4. Refactoring-Lösung

### Schritt 1: Value Objects für Suchkriterien

```java
// Suchkriterien als Value Object
public class CustomerSearchCriteria {
    private final String name;
    private final String contractType;
    private final String tariff;
    private final DateRange contractPeriod;
    private final Address address;
    private final Boolean isActive;
    private final PaymentMethod paymentMethod;
    private final Revenue minRevenue;
    private final SortCriteria sortCriteria;
    private final Pagination pagination;
    
    // Private Constructor - nur über Builder erstellbar
    private CustomerSearchCriteria(Builder builder) {
        this.name = builder.name;
        this.contractType = builder.contractType;
        this.tariff = builder.tariff;
        this.contractPeriod = builder.contractPeriod;
        this.address = builder.address;
        this.isActive = builder.isActive;
        this.paymentMethod = builder.paymentMethod;
        this.minRevenue = builder.minRevenue;
        this.sortCriteria = builder.sortCriteria;
        this.pagination = builder.pagination;
    }
    
    // Immutable Getters
    public String getName() { return name; }
    public String getContractType() { return contractType; }
    // ... weitere Getters
    
    // Builder als static nested class
    public static class Builder {
        private String name;
        private String contractType;
        private String tariff;
        private DateRange contractPeriod;
        private Address address;
        private Boolean isActive;
        private PaymentMethod paymentMethod;
        private Revenue minRevenue;
        private SortCriteria sortCriteria;
        private Pagination pagination;
        
        // Fluent Interface Methods
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withContractType(String contractType) {
            this.contractType = contractType;
            return this;
        }
        
        public Builder withTariff(String tariff) {
            this.tariff = tariff;
            return this;
        }
        
        public Builder withContractPeriod(LocalDate start, LocalDate end) {
            this.contractPeriod = new DateRange(start, end);
            return this;
        }
        
        public Builder inCity(String city) {
            if (this.address == null) {
                this.address = new Address.Builder().build();
            }
            this.address = this.address.withCity(city);
            return this;
        }
        
        public Builder withPostalCode(String postalCode) {
            if (this.address == null) {
                this.address = new Address.Builder().build();
            }
            this.address = this.address.withPostalCode(postalCode);
            return this;
        }
        
        public Builder onlyActive() {
            this.isActive = true;
            return this;
        }
        
        public Builder includeInactive() {
            this.isActive = null; // null = alle
            return this;
        }
        
        public Builder withPaymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }
        
        public Builder withMinRevenue(int amount, Currency currency) {
            this.minRevenue = new Revenue(amount, currency);
            return this;
        }
        
        public Builder sortBy(String field, SortOrder order) {
            this.sortCriteria = new SortCriteria(field, order);
            return this;
        }
        
        public Builder sortByNameAscending() {
            return sortBy("name", SortOrder.ASC);
        }
        
        public Builder sortByRevenueDescending() {
            return sortBy("revenue", SortOrder.DESC);
        }
        
        public Builder limitTo(int limit) {
            if (this.pagination == null) {
                this.pagination = new Pagination(0, limit);
            } else {
                this.pagination = this.pagination.withLimit(limit);
            }
            return this;
        }
        
        public Builder withOffset(int offset) {
            if (this.pagination == null) {
                this.pagination = new Pagination(offset, 50); // Default limit
            } else {
                this.pagination = this.pagination.withOffset(offset);
            }
            return this;
        }
        
        public Builder page(int pageNumber, int pageSize) {
            int offset = (pageNumber - 1) * pageSize;
            this.pagination = new Pagination(offset, pageSize);
            return this;
        }
        
        // Validation im Builder
        public CustomerSearchCriteria build() {
            validate();
            return new CustomerSearchCriteria(this);
        }
        
        private void validate() {
            if (minRevenue != null && minRevenue.getAmount() < 0) {
                throw new IllegalArgumentException("Minimum revenue cannot be negative");
            }
            
            if (contractPeriod != null && contractPeriod.getStart().isAfter(contractPeriod.getEnd())) {
                throw new IllegalArgumentException("Contract start date cannot be after end date");
            }
            
            if (pagination != null && pagination.getLimit() > 1000) {
                throw new IllegalArgumentException("Limit cannot exceed 1000 records");
            }
        }
    }
    
    // Factory Method für häufige Anwendungsfälle
    public static Builder forBusinessCustomers() {
        return new Builder().withContractType("BUSINESS").onlyActive();
    }
    
    public static Builder forPrivateCustomers() {
        return new Builder().withContractType("PRIVATE").onlyActive();
    }
    
    public static Builder forCity(String city) {
        return new Builder().inCity(city).onlyActive();
    }
}
```

### Schritt 2: Query Builder für SQL-Erstellung

```java
public class CustomerQueryBuilder {
    private final StringBuilder select;
    private final StringBuilder joins;
    private final StringBuilder where;
    private final List<Object> parameters;
    private StringBuilder orderBy;
    private String limit;
    
    private CustomerQueryBuilder() {
        this.select = new StringBuilder("SELECT c.* FROM customers c");
        this.joins = new StringBuilder();
        this.where = new StringBuilder();
        this.parameters = new ArrayList<>();
    }
    
    public static CustomerQueryBuilder create() {
        return new CustomerQueryBuilder();
    }
    
    public CustomerQueryBuilder addNameFilter(String name) {
        if (name != null && !name.trim().isEmpty()) {
            addWhereClause("c.name LIKE ?");
            parameters.add("%" + name.trim() + "%");
        }
        return this;
    }
    
    public CustomerQueryBuilder addContractTypeFilter(String contractType) {
        if (contractType != null && !contractType.isEmpty()) {
            addWhereClause("c.contract_type = ?");
            parameters.add(contractType);
        }
        return this;
    }
    
    public CustomerQueryBuilder addTariffFilter(String tariff) {
        if (tariff != null && !tariff.isEmpty()) {
            ensureTariffJoin();
            addWhereClause("t.name = ?");
            parameters.add(tariff);
        }
        return this;
    }
    
    public CustomerQueryBuilder addDateRangeFilter(DateRange contractPeriod) {
        if (contractPeriod != null) {
            if (contractPeriod.getStart() != null) {
                addWhereClause("c.contract_start >= ?");
                parameters.add(contractPeriod.getStart());
            }
            if (contractPeriod.getEnd() != null) {
                addWhereClause("c.contract_start <= ?");
                parameters.add(contractPeriod.getEnd());
            }
        }
        return this;
    }
    
    public CustomerQueryBuilder addActiveFilter(Boolean isActive) {
        if (isActive != null) {
            addWhereClause("c.is_active = ?");
            parameters.add(isActive);
        }
        return this;
    }
    
    public CustomerQueryBuilder addCityFilter(String city) {
        if (city != null && !city.isEmpty()) {
            addWhereClause("c.city = ?");
            parameters.add(city);
        }
        return this;
    }
    
    public CustomerQueryBuilder addRevenueFilter(Revenue minRevenue) {
        if (minRevenue != null) {
            addWhereClause("c.monthly_revenue >= ?");
            parameters.add(minRevenue.getAmount());
        }
        return this;
    }
    
    public CustomerQueryBuilder addSorting(SortCriteria sortCriteria) {
        if (sortCriteria != null) {
            this.orderBy = new StringBuilder(" ORDER BY ")
                .append(sanitizeColumnName(sortCriteria.getField()))
                .append(" ")
                .append(sortCriteria.getOrder().name());
        }
        return this;
    }
    
    public CustomerQueryBuilder addPagination(Pagination pagination) {
        if (pagination != null) {
            this.limit = " LIMIT " + pagination.getLimit();
            if (pagination.getOffset() > 0) {
                this.limit += " OFFSET " + pagination.getOffset();
            }
        }
        return this;
    }
    
    // Template method für komplette Query-Erstellung
    public PreparedQuery build() {
        StringBuilder fullQuery = new StringBuilder();
        fullQuery.append(select);
        
        if (joins.length() > 0) {
            fullQuery.append(" ").append(joins);
        }
        
        if (where.length() > 0) {
            fullQuery.append(" WHERE ").append(where);
        }
        
        if (orderBy != null) {
            fullQuery.append(orderBy);
        }
        
        if (limit != null) {
            fullQuery.append(limit);
        }
        
        return new PreparedQuery(fullQuery.toString(), parameters);
    }
    
    // Private Helper Methods
    private void addWhereClause(String clause) {
        if (where.length() > 0) {
            where.append(" AND ");
        }
        where.append(clause);
    }
    
    private void ensureTariffJoin() {
        if (!joins.toString().contains("JOIN tariffs")) {
            joins.append(" JOIN tariffs t ON c.tariff_id = t.id");
        }
    }
    
    private String sanitizeColumnName(String columnName) {
        // SQL Injection Schutz
        if (!columnName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("Invalid column name: " + columnName);
        }
        return "c." + columnName;
    }
}
```

### Schritt 3: Repository Implementation

```java
@Repository
public class JdbcCustomerRepository implements CustomerRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;
    
    public JdbcCustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = new CustomerRowMapper();
    }
    
    @Override
    public List<Customer> findByComplexCriteria(CustomerSearchCriteria criteria) {
        PreparedQuery query = buildQuery(criteria);
        
        return jdbcTemplate.query(
            query.getSql(), 
            query.getParameters().toArray(), 
            customerRowMapper
        );
    }
    
    private PreparedQuery buildQuery(CustomerSearchCriteria criteria) {
        return CustomerQueryBuilder.create()
            .addNameFilter(criteria.getName())
            .addContractTypeFilter(criteria.getContractType())
            .addTariffFilter(criteria.getTariff())
            .addDateRangeFilter(criteria.getContractPeriod())
            .addActiveFilter(criteria.getIsActive())
            .addCityFilter(criteria.getAddress() != null ? criteria.getAddress().getCity() : null)
            .addRevenueFilter(criteria.getMinRevenue())
            .addSorting(criteria.getSortCriteria())
            .addPagination(criteria.getPagination())
            .build();
    }
    
    // Convenience Methods mit vorkonfigurierten Buildern
    public List<Customer> findActiveBusinessCustomers() {
        CustomerSearchCriteria criteria = CustomerSearchCriteria
            .forBusinessCustomers()
            .sortByRevenueDescending()
            .limitTo(100)
            .build();
            
        return findByComplexCriteria(criteria);
    }
    
    public List<Customer> findCustomersInCity(String city) {
        CustomerSearchCriteria criteria = CustomerSearchCriteria
            .forCity(city)
            .sortByNameAscending()
            .build();
            
        return findByComplexCriteria(criteria);
    }
}
```

## 5. SOLID-Prinzipien Integration

### Single Responsibility Principle (SRP)
- **CustomerSearchCriteria**: Nur für Suchkriterien zuständig
- **CustomerQueryBuilder**: Nur für SQL-Query Erstellung
- **CustomerRepository**: Nur für Datenbank-Zugriff

### Open/Closed Principle (OCP)
- Neue Filter durch Builder-Methoden hinzufügbar
- Query-Logik erweiterbar ohne Repository-Änderung

### Liskov Substitution Principle (LSP)
```java
// Verschiedene Repository-Implementierungen austauschbar
public class MongoCustomerRepository implements CustomerRepository {
    @Override
    public List<Customer> findByComplexCriteria(CustomerSearchCriteria criteria) {
        // MongoDB-spezifische Implementation
        Query mongoQuery = buildMongoQuery(criteria);
        return mongoTemplate.find(mongoQuery, Customer.class);
    }
}

public class ElasticsearchCustomerRepository implements CustomerRepository {
    @Override
    public List<Customer> findByComplexCriteria(CustomerSearchCriteria criteria) {
        // Elasticsearch-spezifische Implementation
        SearchRequest searchRequest = buildElasticsearchQuery(criteria);
        return searchCustomers(searchRequest);
    }
}
```

### Interface Segregation Principle (ISP)
```java
// Getrennte Interfaces für verschiedene Query-Arten
public interface CustomerSearchRepository {
    List<Customer> findByComplexCriteria(CustomerSearchCriteria criteria);
}

public interface CustomerAnalyticsRepository {
    CustomerStatistics getStatistics(CustomerSearchCriteria criteria);
    List<RevenueReport> getRevenueReport(DateRange period);
}

public interface CustomerRepository extends 
    Repository<Customer, String>,
    CustomerSearchRepository,
    CustomerAnalyticsRepository {
}
```

### Dependency Inversion Principle (DIP)
- Service Layer abhängig von Repository-Interface
- Konkrete Implementierung über Dependency Injection

## 6. Fluent Interface Design Prinzipien

### Method Chaining Guidelines
```java
// Gutes Fluent Interface Design
public class QueryBuilder {
    
    // 1. Methods return this für Method Chaining
    public QueryBuilder where(String field) {
        // ...
        return this;
    }
    
    // 2. Semantische Method-Namen
    public QueryBuilder equals(Object value) { /* */ return this; }
    public QueryBuilder like(String pattern) { /* */ return this; }
    public QueryBuilder between(Object start, Object end) { /* */ return this; }
    
    // 3. Logical Flow: where -> condition -> and/or -> next condition
    public QueryBuilder and(String field) { /* */ return this; }
    public QueryBuilder or(String field) { /* */ return this; }
    
    // 4. Terminal Operation
    public Query build() { /* */ return query; }
}

// Usage Example - reads like English
Query query = QueryBuilder.create()
    .where("name").like("Schmidt%")
    .and("age").between(25, 65)
    .or("status").equals("PREMIUM")
    .build();
```

### Type-Safe Builder Pattern
```java
// Builder States für Compile-Time Validation
public interface CustomerBuilderNameStep {
    CustomerBuilderContractStep withName(String name);
}

public interface CustomerBuilderContractStep {
    CustomerBuilderOptionalStep withContractType(String contractType);
}

public interface CustomerBuilderOptionalStep {
    CustomerBuilderOptionalStep withTariff(String tariff);
    CustomerBuilderOptionalStep inCity(String city);
    Customer build();
}

public class CustomerBuilder implements 
    CustomerBuilderNameStep, 
    CustomerBuilderContractStep, 
    CustomerBuilderOptionalStep {
    
    // Verhindert Compile-Time, dass build() ohne required fields aufgerufen wird
    public static CustomerBuilderNameStep create() {
        return new CustomerBuilder();
    }
}
```

## 7. Performance Optimierungen

### Query Plan Caching
```java
@Component
public class QueryPlanCache {
    private final Map<String, PreparedStatement> cache = new ConcurrentHashMap<>();
    
    public PreparedStatement getOrCreateStatement(Connection connection, String sql) {
        return cache.computeIfAbsent(sql, key -> {
            try {
                return connection.prepareStatement(key);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to prepare statement", e);
            }
        });
    }
}
```

### Lazy Query Execution
```java
public class LazyCustomerResultSet {
    private final CustomerSearchCriteria criteria;
    private final CustomerRepository repository;
    private List<Customer> cachedResults;
    
    public Stream<Customer> stream() {
        if (cachedResults == null) {
            cachedResults = repository.findByComplexCriteria(criteria);
        }
        return cachedResults.stream();
    }
    
    public boolean isEmpty() {
        // Optimierte Abfrage ohne vollständige Resultate
        return repository.countByCriteria(criteria) == 0;
    }
}
```

## 8. Testing Patterns

### Builder Testing
```java
@Test
public void shouldBuildValidSearchCriteria() {
    // Arrange & Act
    CustomerSearchCriteria criteria = CustomerSearchCriteria.forBusinessCustomers()
        .withName("Schmidt")
        .inCity("Berlin")
        .withMinRevenue(1000, Currency.EUR)
        .sortByRevenueDescending()
        .page(1, 20)
        .build();
    
    // Assert
    assertThat(criteria.getName()).isEqualTo("Schmidt");
    assertThat(criteria.getContractType()).isEqualTo("BUSINESS");
    assertThat(criteria.getIsActive()).isTrue();
    assertThat(criteria.getAddress().getCity()).isEqualTo("Berlin");
    assertThat(criteria.getMinRevenue().getAmount()).isEqualTo(1000);
}

@Test
public void shouldValidateInvalidCriteria() {
    // Act & Assert
    assertThatThrownBy(() -> {
        CustomerSearchCriteria.forPrivateCustomers()
            .withMinRevenue(-100, Currency.EUR)
            .build();
    }).isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Minimum revenue cannot be negative");
}
```

### Repository Testing mit TestContainers
```java
@SpringBootTest
@Testcontainers
class JdbcCustomerRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("telekom_test")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Test
    void shouldFindCustomersByComplexCriteria() {
        // Arrange
        CustomerSearchCriteria criteria = CustomerSearchCriteria.forBusinessCustomers()
            .inCity("München")
            .withMinRevenue(500, Currency.EUR)
            .limitTo(10)
            .build();
        
        // Act
        List<Customer> result = customerRepository.findByComplexCriteria(criteria);
        
        // Assert
        assertThat(result).hasSizeLessThanOrEqualTo(10);
        assertThat(result).allMatch(customer -> 
            customer.getContractType().equals("BUSINESS") &&
            customer.getAddress().getCity().equals("München") &&
            customer.getMonthlyRevenue().getAmount() >= 500
        );
    }
}
```

## 9. Diskussionspunkte

### Architektur-Diskussion
1. **Builder Complexity vs. Readability**
   - Wann wird Builder Pattern zu komplex?
   - Alternative Ansätze für einfache Objekte?

2. **Immutability Trade-offs**
   - Performance-Impact von immutable Value Objects
   - Memory-Overhead bei großen Objekten

3. **Query Builder vs. ORM**
   - Wann eigene Query Builder vs. JPA Criteria API?
   - Performance-Vergleich verschiedener Ansätze

### Telekom-spezifische Herausforderungen
1. **Legacy Database Schemas**
   - Integration mit bestehenden Tabellen-Strukturen
   - Migration existierender Query-Logik

2. **Multi-Tenant Scenarios**
   - Tenant-spezifische Filter in Builder integrieren
   - Performance bei mandanten-übergreifenden Abfragen

## 10. Praktische Übung (25 Minuten)

### Aufgabe: Tariff Search Builder

**Szenario:** Erstellen Sie einen Builder für komplexe Tarif-Suchen mit folgenden Kriterien:
- Tarif-Name (Like-Suche)
- Preis-Range (von/bis)
- Verfügbarkeit in Region
- Internet-Geschwindigkeit (min/max)
- Vertragslaufzeit
- Zusatz-Optionen (TV, Telefon)
- Sortierung nach Preis/Geschwindigkeit
- Pagination

### Gegeben: Einfache Tariff-Klasse
```java
public class Tariff {
    private String name;
    private BigDecimal monthlyPrice;
    private String region;
    private int downloadSpeedMbps;
    private int contractDurationMonths;
    private Set<String> options;
    // ... constructors, getters
}
```

### Aufgaben
1. **Search Criteria Builder** (15 Min)
   - Erstellen Sie `TariffSearchCriteria` mit Builder
   - Implementieren Sie Fluent Interface für alle Kriterien
   - Fügen Sie Validation hinzu

2. **Query Builder Integration** (7 Min)
   - Skizzieren Sie `TariffQueryBuilder` für SQL-Erstellung
   - Implementieren Sie 2-3 Filter-Methoden

3. **Usage Examples** (3 Min)
   - Schreiben Sie 3 realistische Anwendungsbeispiele

### Lösungsansatz
```java
public class TariffSearchCriteria {
    // ... fields
    
    public static class Builder {
        public Builder withNameLike(String name) { /* */ return this; }
        public Builder withPriceRange(BigDecimal min, BigDecimal max) { /* */ return this; }
        public Builder inRegion(String region) { /* */ return this; }
        public Builder withMinDownloadSpeed(int speedMbps) { /* */ return this; }
        public Builder withMaxContractDuration(int months) { /* */ return this; }
        public Builder withOption(String option) { /* */ return this; }
        public Builder sortByPriceAscending() { /* */ return this; }
        public Builder sortBySpeedDescending() { /* */ return this; }
        public Builder page(int pageNumber, int pageSize) { /* */ return this; }
        
        public TariffSearchCriteria build() {
            validate();
            return new TariffSearchCriteria(this);
        }
    }
    
    // Factory Methods
    public static Builder forHighSpeedInternet() {
        return new Builder().withMinDownloadSpeed(100);
    }
    
    public static Builder forBudgetTariffs() {
        return new Builder().withPriceRange(null, new BigDecimal("30"));
    }
}

// Usage Examples:
TariffSearchCriteria dslTariffs = TariffSearchCriteria
    .forHighSpeedInternet()
    .inRegion("Bayern")
    .withOption("TV")
    .sortByPriceAscending()
    .limitTo(20)
    .build();
```

## 11. Anti-Pattern Vermeidung

### Telescoping Constructor Anti-Pattern
```java
// Anti-Pattern: Zu viele Constructor-Parameter
public class TariffSearchCriteria {
    public TariffSearchCriteria(String name, BigDecimal minPrice, 
                               BigDecimal maxPrice, String region, 
                               Integer minSpeed, Integer maxSpeed,
                               Integer maxContractDuration, 
                               Set<String> options, String sortBy, 
                               String sortOrder, Integer limit, 
                               Integer offset) {
        // 12+ Parameter sind unhandhabbar
    }
}
```

### Mutable Builder Anti-Pattern
```java
// Anti-Pattern: Builder state changes after build()
public class BadBuilder {
    private String name;
    
    public BadBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public Product build() {
        return new Product(name); // Builder bleibt mutable
    }
}

// Problem: Builder kann wiederverwendet werden
BadBuilder builder = new BadBuilder().withName("Test");
Product p1 = builder.build();
Product p2 = builder.withName("Changed").build(); // p1 unverändert, aber verwirrend
```

**Lösung: Builder nach build() invalidieren oder immutable machen**

## 12. Moderne Java Features

### Record-Based Value Objects (Java 14+)
```java
// Kompakte Value Objects mit Records
public record TariffSearchCriteria(
    String name,
    PriceRange priceRange,
    String region,
    SpeedRange speedRange,
    Integer maxContractDuration,
    Set<String> options,
    SortCriteria sortCriteria,
    Pagination pagination
) {
    // Validation im Compact Constructor
    public TariffSearchCriteria {
        if (priceRange != null && priceRange.min().compareTo(priceRange.max()) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}

public record PriceRange(BigDecimal min, BigDecimal max) {}
public record SpeedRange(Integer minMbps, Integer maxMbps) {}
```

### Optional-Integration im Builder
```java
public class ModernTariffBuilder {
    public Builder withPriceRange(Optional<BigDecimal> min, Optional<BigDecimal> max) {
        min.ifPresent(this::withMinPrice);
        max.ifPresent(this::withMaxPrice);
        return this;
    }
    
    public Builder withOptionsIfAvailable(Set<String> options) {
        return Optional.ofNullable(options)
            .filter(opts -> !opts.isEmpty())
            .map(this::withOptions)
            .orElse(this);
    }
}
```

## 13. Zusammenfassung

### Schlüsselerkenntnisse
- Builder Pattern löst Telescoping Constructor Problem
- Fluent Interfaces verbessern Code-Readability
- Repository Pattern profitiert von strukturierten Suchkriterien
- Immutable Value Objects erhöhen Thread-Safety

### Vorteile
- Type-sichere Objekterstellung
- Bessere Testbarkeit durch klare Abhängigkeiten
- Flexibilität bei komplexen Query-Anforderungen
- Saubere Trennung von Objekterstellung und Business Logic

### Nachteile
- Mehr Code für Builder-Implementierung
- Memory-Overhead bei vielen kleinen Objekten
- Potentiell Über-Engineering bei einfachen Szenarien

### Nächste Schritte
- Im nächsten Modul: Singleton Pattern mit Adapter für Clean Architecture
- Integration von Builder Pattern in Microservice-Architektur
- Performance-Optimierung für große Datenmengen