# Modul 6: Singleton & Adapter Pattern in Clean Architecture

## Lernziele
- Singleton Pattern modern und thread-safe implementieren
- Adapter Pattern für Legacy-System Integration verstehen
- Clean Architecture Prinzipien praktisch anwenden
- Dependency Inversion mit Adaptern umsetzen

## 1. Problem-Motivation

### Ausgangssituation: Legacy-System Integration
Telekom betreibt verschiedene Legacy-Systeme, die in moderne Microservice-Architekturen integriert werden müssen. Diese Systeme haben unterschiedliche Schnittstellen und Datenformate, aber ähnliche fachliche Funktionalitäten.

### Herausforderungen
- **Legacy SOAP Services** mit veralteten WSDL-Definitionen
- **Mainframe-Systeme** mit proprietären Protokollen
- **Verschiedene Datenformate**: XML, Fixed-Length Records, proprietäre Binärformate
- **Performance-kritische Verbindungen** mit Connection Pooling
- **Einheitliche Domain-Schnittstelle** für moderne Services

### Problematischer Code - Tight Coupling
```java
@Service
public class CustomerManagementService {
    
    // Code-Smell: Tight Coupling zu Legacy-Systemen
    private LegacyMainframeClient mainframeClient;
    private OldCrmSoapClient crmSoapClient;
    private BillingSystemRestClient billingClient;
    
    public CustomerData getCompleteCustomerView(String customerId) {
        CustomerData result = new CustomerData();
        
        // Direkte Abhängigkeit zu Legacy-Format
        try {
            // Mainframe call mit proprietärem Protokoll
            MainframeCustomerRecord record = mainframeClient.getCustomer(customerId);
            result.setName(record.getName());
            result.setAddress(parseMainframeAddress(record.getAddressBlock()));
            result.setCustomerNumber(record.getKundenNr());
            
        } catch (MainframeConnectionException e) {
            // Fallback zu CRM System
            try {
                CrmCustomerSoap soapResponse = crmSoapClient.getCustomerData(customerId);
                result.setName(soapResponse.getFullName());
                result.setAddress(convertSoapAddress(soapResponse.getAddressData()));
                result.setCustomerNumber(soapResponse.getCustomerId());
                
            } catch (SOAPException e2) {
                throw new CustomerNotFoundException("Could not retrieve customer: " + customerId);
            }
        }
        
        // Billing Information from REST API
        try {
            BillingResponse billingData = billingClient.getBillingInfo(customerId);
            result.setBillingInfo(billingData);
        } catch (RestClientException e) {
            // Ignoriere Billing-Fehler
        }
        
        return result;
    }
    
    // Code-Smell: Format-Konvertierung in Business Logic
    private Address parseMainframeAddress(String addressBlock) {
        // 30 Zeilen Parsing-Code für Fixed-Length Format
        String street = addressBlock.substring(0, 30).trim();
        String city = addressBlock.substring(30, 55).trim();
        String postalCode = addressBlock.substring(55, 60).trim();
        return new Address(street, city, postalCode);
    }
}
```

### Code-Smell Analyse
1. **Tight Coupling**: Service kennt alle Legacy-Formate
2. **Multiple Responsibilities**: Business Logic + Format-Konvertierung
3. **Hard to Test**: Abhängigkeiten zu externen Systemen
4. **Inflexible**: Neue Systeme erfordern Service-Änderung
5. **Clean Architecture Violation**: Domain Layer kennt Infrastructure Details

## 2. Clean Architecture Grundlagen

### Clean Architecture Layers
```
┌─────────────────────────────────────┐
│            UI/Framework             │ <- Controllers, Web API
├─────────────────────────────────────┤
│          Use Cases                  │ <- Application Business Rules  
├─────────────────────────────────────┤
│            Domain                   │ <- Enterprise Business Rules
├─────────────────────────────────────┤
│          Adapters                   │ <- Interface Adapters
├─────────────────────────────────────┤
│        Infrastructure               │ <- Frameworks, Databases, External APIs
└─────────────────────────────────────┘
```

### Dependency Rule
**Kernprinzip**: Abhängigkeiten zeigen nur nach innen. Inner layers wissen nichts von outer layers.

### Domain-Zentric Design
```java
// Domain Layer - keine externe Abhängigkeiten
public interface CustomerRepository {
    Optional<Customer> findById(CustomerId id);
    Customer save(Customer customer);
}

public interface BillingService {
    BillingInformation getBilling(CustomerId customerId);
}

// Domain Service - reine Business Logic
@DomainService
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BillingService billingService;
    
    public CustomerProfile getCustomerProfile(CustomerId customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
            
        BillingInformation billing = billingService.getBilling(customerId);
        
        return new CustomerProfile(customer, billing);
    }
}
```

## 3. Singleton Pattern - Modern Implementation

### Problem: Configuration Manager
Telekom-Systeme benötigen einheitlichen Zugriff auf Konfiguration (Umgebungsvariablen, Properties, externe Config-Services).

### Thread-Safe Singleton Implementierungen

#### Enum Singleton (Empfohlen)
```java
public enum ConfigurationManager {
    INSTANCE;
    
    private final Map<String, String> config;
    private final long lastRefresh;
    
    ConfigurationManager() {
        this.config = loadConfiguration();
        this.lastRefresh = System.currentTimeMillis();
    }
    
    public String getProperty(String key) {
        return config.get(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }
    
    public synchronized void refresh() {
        if (System.currentTimeMillis() - lastRefresh > 300000) { // 5 min
            config.clear();
            config.putAll(loadConfiguration());
        }
    }
    
    private Map<String, String> loadConfiguration() {
        Map<String, String> props = new HashMap<>();
        
        // Load from multiple sources
        loadEnvironmentVariables(props);
        loadPropertyFiles(props);
        loadExternalConfigService(props);
        
        return Collections.unmodifiableMap(props);
    }
    
    private void loadEnvironmentVariables(Map<String, String> props) {
        System.getenv().forEach((key, value) -> {
            if (key.startsWith("TELEKOM_")) {
                props.put(key, value);
            }
        });
    }
    
    private void loadPropertyFiles(Map<String, String> props) {
        try {
            Properties fileProps = new Properties();
            fileProps.load(getClass().getResourceAsStream("/application.properties"));
            fileProps.forEach((key, value) -> props.put((String)key, (String)value));
        } catch (IOException e) {
            // Log error, continue with other sources
        }
    }
    
    private void loadExternalConfigService(Map<String, String> props) {
        // Integration mit externem Config-Service
        String configServiceUrl = props.get("CONFIG_SERVICE_URL");
        if (configServiceUrl != null) {
            // Externe Konfiguration laden
        }
    }
}

// Usage
String dbUrl = ConfigurationManager.INSTANCE.getProperty("DATABASE_URL");
```

#### Initialization-on-demand Singleton
```java
public class ConnectionPoolManager {
    
    private ConnectionPoolManager() {}
    
    private static class Holder {
        private static final ConnectionPoolManager INSTANCE = new ConnectionPoolManager();
        
        // Singleton wird erst bei erstem Zugriff erstellt
        static {
            INSTANCE.initializePool();
        }
    }
    
    public static ConnectionPoolManager getInstance() {
        return Holder.INSTANCE;
    }
    
    private HikariDataSource dataSource;
    
    private void initializePool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigurationManager.INSTANCE.getProperty("DATABASE_URL"));
        config.setUsername(ConfigurationManager.INSTANCE.getProperty("DATABASE_USER"));
        config.setPassword(ConfigurationManager.INSTANCE.getProperty("DATABASE_PASSWORD"));
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        this.dataSource = new HikariDataSource(config);
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
```

## 4. Adapter Pattern für Legacy Integration

### Adapter Pattern Struktur
```
Client -> Target Interface -> Adapter -> Adaptee (Legacy System)
```

### Domain Interface Definition
```java
// Domain Layer - Target Interface
public interface CustomerDataService {
    CustomerProfile getCustomerProfile(CustomerId customerId);
    void updateCustomerData(CustomerId customerId, CustomerUpdate update);
    List<CustomerProfile> searchCustomers(CustomerSearchCriteria criteria);
}

// Domain Value Objects
public class CustomerProfile {
    private final CustomerId customerId;
    private final PersonalData personalData;
    private final Address address;
    private final ContactInformation contact;
    private final ContractInformation contract;
    
    // Constructor, Getters - Immutable
}

public class PersonalData {
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    // ...
}
```

### Legacy System Adapters

#### Mainframe Adapter
```java
@Component
public class MainframeCustomerAdapter implements CustomerDataService {
    
    private final MainframeClient mainframeClient;
    private final MainframeDataMapper mapper;
    
    public MainframeCustomerAdapter(MainframeClient mainframeClient) {
        this.mainframeClient = mainframeClient;
        this.mapper = new MainframeDataMapper();
    }
    
    @Override
    public CustomerProfile getCustomerProfile(CustomerId customerId) {
        try {
            MainframeCustomerRecord record = mainframeClient.retrieveCustomer(customerId.getValue());
            return mapper.toDomainModel(record);
            
        } catch (MainframeConnectionException e) {
            throw new CustomerDataAccessException("Mainframe connection failed", e);
        } catch (CustomerNotFoundException e) {
            return null; // or Optional.empty() if interface returns Optional
        }
    }
    
    @Override
    public void updateCustomerData(CustomerId customerId, CustomerUpdate update) {
        try {
            MainframeUpdateRequest request = mapper.toMainframeUpdate(customerId, update);
            mainframeClient.updateCustomer(request);
            
        } catch (MainframeConnectionException e) {
            throw new CustomerDataAccessException("Failed to update customer in mainframe", e);
        }
    }
    
    @Override
    public List<CustomerProfile> searchCustomers(CustomerSearchCriteria criteria) {
        MainframeSearchRequest searchRequest = mapper.toMainframeSearch(criteria);
        
        try {
            List<MainframeCustomerRecord> records = mainframeClient.searchCustomers(searchRequest);
            return records.stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
                
        } catch (MainframeConnectionException e) {
            throw new CustomerDataAccessException("Mainframe search failed", e);
        }
    }
}
```

#### CRM SOAP Adapter
```java
@Component
public class CrmSoapCustomerAdapter implements CustomerDataService {
    
    private final CrmSoapClient soapClient;
    private final SoapDataMapper mapper;
    
    public CrmSoapCustomerAdapter(CrmSoapClient soapClient) {
        this.soapClient = soapClient;
        this.mapper = new SoapDataMapper();
    }
    
    @Override
    public CustomerProfile getCustomerProfile(CustomerId customerId) {
        try {
            GetCustomerRequest request = new GetCustomerRequest();
            request.setCustomerId(customerId.getValue());
            
            GetCustomerResponse response = soapClient.getCustomer(request);
            
            if (response.getCustomerData() == null) {
                return null;
            }
            
            return mapper.toDomainModel(response.getCustomerData());
            
        } catch (SOAPFaultException e) {
            if (e.getFault().getFaultCode().equals("CUSTOMER_NOT_FOUND")) {
                return null;
            }
            throw new CustomerDataAccessException("SOAP service error", e);
        }
    }
    
    @Override
    public void updateCustomerData(CustomerId customerId, CustomerUpdate update) {
        try {
            UpdateCustomerRequest request = mapper.toSoapUpdate(customerId, update);
            UpdateCustomerResponse response = soapClient.updateCustomer(request);
            
            if (!response.isSuccess()) {
                throw new CustomerDataAccessException("SOAP update failed: " + response.getErrorMessage());
            }
            
        } catch (SOAPException e) {
            throw new CustomerDataAccessException("SOAP communication failed", e);
        }
    }
    
    @Override
    public List<CustomerProfile> searchCustomers(CustomerSearchCriteria criteria) {
        try {
            SearchCustomersRequest request = mapper.toSoapSearch(criteria);
            SearchCustomersResponse response = soapClient.searchCustomers(request);
            
            return response.getCustomers().stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
                
        } catch (SOAPException e) {
            throw new CustomerDataAccessException("SOAP search failed", e);
        }
    }
}
```

### Data Mapping - Anti-Corruption Layer
```java
@Component
public class MainframeDataMapper {
    
    public CustomerProfile toDomainModel(MainframeCustomerRecord record) {
        // Mainframe -> Domain Mapping
        PersonalData personalData = new PersonalData(
            parseFirstName(record.getName()),
            parseLastName(record.getName()),
            parseDate(record.getBirthDate())
        );
        
        Address address = parseAddress(record.getAddressBlock());
        ContactInformation contact = parseContact(record.getContactBlock());
        ContractInformation contract = parseContract(record.getContractData());
        
        return new CustomerProfile(
            new CustomerId(record.getCustomerNumber()),
            personalData,
            address,
            contact,
            contract
        );
    }
    
    public MainframeUpdateRequest toMainframeUpdate(CustomerId customerId, CustomerUpdate update) {
        MainframeUpdateRequest request = new MainframeUpdateRequest();
        request.setCustomerNumber(customerId.getValue());
        
        // Domain -> Mainframe Mapping
        if (update.getNewAddress() != null) {
            request.setAddressBlock(formatAddress(update.getNewAddress()));
        }
        
        if (update.getNewContact() != null) {
            request.setContactBlock(formatContact(update.getNewContact()));
        }
        
        return request;
    }
    
    // Private Helper Methods für Format-Konvertierung
    private String parseFirstName(String fullName) {
        // Mainframe speichert "LASTNAME, FIRSTNAME"
        String[] parts = fullName.split(",");
        return parts.length > 1 ? parts[1].trim() : "";
    }
    
    private String parseLastName(String fullName) {
        String[] parts = fullName.split(",");
        return parts.length > 0 ? parts[0].trim() : fullName;
    }
    
    private LocalDate parseDate(String mainframeDate) {
        // Mainframe Format: YYYYMMDD
        if (mainframeDate != null && mainframeDate.length() == 8) {
            return LocalDate.parse(mainframeDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        return null;
    }
    
    private Address parseAddress(String addressBlock) {
        // Fixed-Length Format parsing
        if (addressBlock == null || addressBlock.length() < 60) {
            return null;
        }
        
        String street = addressBlock.substring(0, 30).trim();
        String city = addressBlock.substring(30, 55).trim();
        String postalCode = addressBlock.substring(55, 60).trim();
        
        return new Address(street, city, postalCode);
    }
    
    private String formatAddress(Address address) {
        // Domain -> Fixed-Length Format
        return String.format("%-30s%-25s%-5s",
            truncate(address.getStreet(), 30),
            truncate(address.getCity(), 25),
            truncate(address.getPostalCode(), 5)
        );
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }
}
```

## 5. Adapter Factory Pattern

### Multi-System Support
```java
@Component
public class CustomerDataServiceFactory {
    
    private final Map<String, CustomerDataService> adapters;
    
    public CustomerDataServiceFactory(
            @Qualifier("mainframe") CustomerDataService mainframeAdapter,
            @Qualifier("crm") CustomerDataService crmAdapter,
            @Qualifier("modern") CustomerDataService modernAdapter) {
        
        this.adapters = Map.of(
            "MAINFRAME", mainframeAdapter,
            "CRM", crmAdapter,
            "MODERN", modernAdapter
        );
    }
    
    public CustomerDataService getAdapter(String systemType) {
        CustomerDataService adapter = adapters.get(systemType);
        if (adapter == null) {
            throw new UnsupportedOperationException("No adapter for system: " + systemType);
        }
        return adapter;
    }
    
    public CustomerDataService getAdapterForCustomer(CustomerId customerId) {
        // Business Logic: Welches System für welche Kunden?
        if (customerId.getValue().startsWith("LEGACY_")) {
            return adapters.get("MAINFRAME");
        } else if (customerId.getValue().startsWith("CRM_")) {
            return adapters.get("CRM");
        } else {
            return adapters.get("MODERN");
        }
    }
}
```

## 6. Composite Adapter Pattern

### Multi-Source Data Aggregation
```java
@Component
@Primary
public class CompositeCustomerDataService implements CustomerDataService {
    
    private final List<CustomerDataService> adapters;
    private final CustomerDataMerger merger;
    
    public CompositeCustomerDataService(
            @Qualifier("mainframe") CustomerDataService mainframeAdapter,
            @Qualifier("crm") CustomerDataService crmAdapter,
            CustomerDataMerger merger) {
        
        this.adapters = List.of(mainframeAdapter, crmAdapter);
        this.merger = merger;
    }
    
    @Override
    public CustomerProfile getCustomerProfile(CustomerId customerId) {
        List<CustomerProfile> profiles = new ArrayList<>();
        
        // Parallel data retrieval from all systems
        CompletableFuture<?>[] futures = adapters.stream()
            .map(adapter -> CompletableFuture.supplyAsync(() -> {
                try {
                    CustomerProfile profile = adapter.getCustomerProfile(customerId);
                    if (profile != null) {
                        synchronized (profiles) {
                            profiles.add(profile);
                        }
                    }
                } catch (CustomerDataAccessException e) {
                    // Log error, continue with other systems
                    logger.warn("Failed to retrieve data from adapter: " + adapter.getClass().getSimpleName(), e);
                }
                return null;
            }))
            .toArray(CompletableFuture[]::new);
        
        // Wait for all systems to respond (with timeout)
        try {
            CompletableFuture.allOf(futures).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            logger.warn("Not all systems responded in time", e);
        }
        
        if (profiles.isEmpty()) {
            return null;
        }
        
        // Merge data from multiple sources
        return merger.merge(profiles);
    }
    
    @Override
    public void updateCustomerData(CustomerId customerId, CustomerUpdate update) {
        // Update in all relevant systems
        List<CompletableFuture<Void>> updateFutures = adapters.stream()
            .map(adapter -> CompletableFuture.runAsync(() -> {
                try {
                    adapter.updateCustomerData(customerId, update);
                } catch (CustomerDataAccessException e) {
                    logger.error("Failed to update in system: " + adapter.getClass().getSimpleName(), e);
                    // Consider: Should we fail the entire operation if one system fails?
                }
            }))
            .collect(Collectors.toList());
        
        // Wait for all updates to complete
        CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0]))
            .join(); // Block until all updates done
    }
    
    @Override
    public List<CustomerProfile> searchCustomers(CustomerSearchCriteria criteria) {
        // Search in all systems and merge results
        List<CustomerProfile> allResults = adapters.parallelStream()
            .flatMap(adapter -> {
                try {
                    return adapter.searchCustomers(criteria).stream();
                } catch (CustomerDataAccessException e) {
                    logger.warn("Search failed in system: " + adapter.getClass().getSimpleName(), e);
                    return Stream.empty();
                }
            })
            .collect(Collectors.toList());
        
        // Deduplicate and merge
        return merger.deduplicateAndMerge(allResults);
    }
}
```

## 7. SOLID-Prinzipien Integration

### Single Responsibility Principle (SRP)
- **Adapter**: Nur für ein Legacy-System zuständig
- **Mapper**: Nur für Datenkonvertierung
- **Domain Service**: Nur für Business Logic

### Open/Closed Principle (OCP)
- Neue Legacy-Systeme durch neue Adapter hinzufügbar
- Domain Layer bleibt unverändert

### Liskov Substitution Principle (LSP)
```java
// Alle Adapter implementieren identisches Interface
public void processCustomer(CustomerDataService service, CustomerId id) {
    CustomerProfile profile = service.getCustomerProfile(id);
    // Funktioniert mit jedem Adapter
}
```

### Interface Segregation Principle (ISP)
```java
// Separate Interfaces für verschiedene Aspekte
public interface CustomerReadService {
    CustomerProfile getCustomerProfile(CustomerId customerId);
    List<CustomerProfile> searchCustomers(CustomerSearchCriteria criteria);
}

public interface CustomerWriteService {
    void updateCustomerData(CustomerId customerId, CustomerUpdate update);
}

// Adapter implementiert nur benötigte Interfaces
public class ReadOnlyMainframeAdapter implements CustomerReadService {
    // Mainframe ist Read-Only
}
```

### Dependency Inversion Principle (DIP)
```java
// Use Case Layer abhängig von Domain Interface
@UseCase
public class GetCustomerProfileUseCase {
    private final CustomerDataService customerDataService; // Interface
    
    public CustomerProfileDto execute(String customerId) {
        CustomerId id = new CustomerId(customerId);
        CustomerProfile profile = customerDataService.getCustomerProfile(id);
        return CustomerProfileDto.from(profile);
    }
}
```

## 8. Testing Strategies

### Adapter Testing
```java
@ExtendWith(MockitoExtension.class)
class MainframeCustomerAdapterTest {
    
    @Mock
    private MainframeClient mainframeClient;
    
    @InjectMocks
    private MainframeCustomerAdapter adapter;
    
    @Test
    void shouldMapMainframeRecordToDomain() throws Exception {
        // Arrange
        MainframeCustomerRecord record = new MainframeCustomerRecord();
        record.setCustomerNumber("12345");
        record.setName("MUELLER, HANS");
        record.setBirthDate("19801215");
        record.setAddressBlock("HAUPTSTRASSE 1            BERLIN               12345");
        
        when(mainframeClient.retrieveCustomer("12345")).thenReturn(record);
        
        // Act
        CustomerProfile profile = adapter.getCustomerProfile(new CustomerId("12345"));
        
        // Assert
        assertThat(profile).isNotNull();
        assertThat(profile.getPersonalData().getFirstName()).isEqualTo("HANS");
        assertThat(profile.getPersonalData().getLastName()).isEqualTo("MUELLER");
        assertThat(profile.getAddress().getStreet()).isEqualTo("HAUPTSTRASSE 1");
        assertThat(profile.getAddress().getCity()).isEqualTo("BERLIN");
        assertThat(profile.getAddress().getPostalCode()).isEqualTo("12345");
    }
    
    @Test
    void shouldHandleMainframeConnectionException() throws Exception {
        // Arrange
        when(mainframeClient.retrieveCustomer("12345"))
            .thenThrow(new MainframeConnectionException("Connection timeout"));
        
        // Act & Assert
        assertThatThrownBy(() -> adapter.getCustomerProfile(new CustomerId("12345")))
            .isInstanceOf(CustomerDataAccessException.class)
            .hasMessage("Mainframe connection failed");
    }
}
```

### Integration Testing
```java
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class CustomerDataServiceIntegrationTest {
    
    @Autowired
    private CustomerDataService customerDataService;
    
    @MockBean
    private MainframeClient mainframeClient;
    
    @MockBean
    private CrmSoapClient crmSoapClient;
    
    @Test
    @Order(1)
    void shouldRetrieveCustomerFromMainframe() {
        // Test mit echten Mainframe-Mock Daten
    }
    
    @Test
    @Order(2) 
    void shouldFallbackToCrmWhenMainframeFails() {
        // Test Fallback-Mechanismus
    }
    
    @Test
    @Order(3)
    void shouldMergeDataFromMultipleSources() {
        // Test Composite Adapter
    }
}
```

## 9. Performance Considerations

### Connection Pooling mit Singleton
```java
@Component
public class LegacyConnectionPoolManager {
    
    private static final LegacyConnectionPoolManager INSTANCE = new LegacyConnectionPoolManager();
    
    private final Map<String, ObjectPool<Connection>> connectionPools;
    
    private LegacyConnectionPoolManager() {
        connectionPools = new ConcurrentHashMap<>();
        initializePools();
    }
    
    public static LegacyConnectionPoolManager getInstance() {
        return INSTANCE;
    }
    
    private void initializePools() {
        // Mainframe Connection Pool
        connectionPools.put("MAINFRAME", new GenericObjectPool<>(
            new MainframeConnectionFactory(),
            createPoolConfig(5, 20)
        ));
        
        // CRM SOAP Connection Pool  
        connectionPools.put("CRM", new GenericObjectPool<>(
            new CrmConnectionFactory(),
            createPoolConfig(3, 15)
        ));
    }
    
    public Connection borrowConnection(String systemType) throws Exception {
        ObjectPool<Connection> pool = connectionPools.get(systemType);
        if (pool == null) {
            throw new IllegalArgumentException("No pool for system: " + systemType);
        }
        return pool.borrowObject();
    }
    
    public void returnConnection(String systemType, Connection connection) {
        ObjectPool<Connection> pool = connectionPools.get(systemType);
        if (pool != null) {
            try {
                pool.returnObject(connection);
            } catch (Exception e) {
                // Log error
            }
        }
    }
}
```

### Caching Decorator
```java
@Component
public class CachedCustomerDataService implements CustomerDataService {
    
    private final CustomerDataService delegate;
    private final Cache<CustomerId, CustomerProfile> cache;
    
    public CachedCustomerDataService(@Qualifier("composite") CustomerDataService delegate) {
        this.delegate = delegate;
        this.cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    }
    
    @Override
    public CustomerProfile getCustomerProfile(CustomerId customerId) {
        return cache.get(customerId, key -> delegate.getCustomerProfile(key));
    }
    
    @Override
    public void updateCustomerData(CustomerId customerId, CustomerUpdate update) {
        delegate.updateCustomerData(customerId, update);
        cache.invalidate(customerId); // Cache invalidieren nach Update
    }
}
```

## 10. Diskussionspunkte

### Architektur-Diskussion
1. **Singleton vs. Dependency Injection**
   - Wann ist Singleton Pattern angemessen?
   - Spring Singleton Beans vs. klassische Singletons

2. **Adapter Granularität**
   - Ein Adapter pro Legacy-System oder pro Fachbereich?
   - Wie handled man System-übergreifende Transaktionen?

3. **Data Consistency**
   - Eventual Consistency bei Multi-System Updates
   - Kompensation bei Teil-Fehlern

### Telekom-spezifische Herausforderungen
1. **Legacy System Evolution**
   - Migration-Pfad von Legacy zu Modern
   - Parallel-Betrieb verschiedener Systeme

2. **Performance Requirements**
   - SLA-Anforderungen bei Legacy-Integration
   - Monitoring und Alerting für Adapter

## 11. Praktische Übung (15 Minuten)

### Aufgabe: Billing System Integration

**Szenario:** Integration verschiedener Billing-Systeme:
- **SAP System**: REST API mit JSON
- **Legacy Billing**: File-based Integration (CSV Export/Import)
- **New Billing**: GraphQL API

### Aufgaben
1. **Domain Interface Design** (5 Min)
   - Definieren Sie BillingService Interface
   - Erstellen Sie Domain Value Objects

2. **Adapter Implementation** (7 Min)
   - Implementieren Sie SapBillingAdapter
   - Skizzieren Sie LegacyFileBillingAdapter

3. **Clean Architecture Integration** (3 Min)
   - Wie integrieren Sie die Adapters in Use Cases?
   - Welche Dependency Injection Strategie wählen Sie?

### Lösungsansatz
```java
// Domain Interface
public interface BillingService {
    BillingStatement getBillingStatement(CustomerId customerId, BillingPeriod period);
    List<BillingItem> getBillingItems(CustomerId customerId, BillingPeriod period);
    void createBillingEntry(CustomerId customerId, BillingItem item);
}

// Domain Value Objects
public class BillingStatement {
    private final CustomerId customerId;
    private final BillingPeriod period;
    private final MonetaryAmount totalAmount;
    private final List<BillingItem> items;
    private final BillingStatus status;
    // ...
}

// SAP Adapter
@Component
public class SapBillingAdapter implements BillingService {
    private final SapRestClient sapClient;
    private final SapBillingMapper mapper;
    
    @Override
    public BillingStatement getBillingStatement(CustomerId customerId, BillingPeriod period) {
        SapBillingResponse response = sapClient.getBilling(
            customerId.getValue(), 
            period.getStart(), 
            period.getEnd()
        );
        return mapper.toDomainModel(response);
    }
}
```

## 12. Anti-Pattern Vermeidung

### Singleton Anti-Patterns
```java
// Anti-Pattern: Not Thread-Safe
public class BadSingleton {
    private static BadSingleton instance;
    
    public static BadSingleton getInstance() {
        if (instance == null) {
            instance = new BadSingleton(); // Race condition!
        }
        return instance;
    }
}

// Anti-Pattern: Global State
public class ConfigSingleton {
    private Map<String, Object> globalState = new HashMap<>();
    
    public void setState(String key, Object value) {
        globalState.put(key, value); // Mutable global state!
    }
}
```

### Adapter Anti-Patterns
```java
// Anti-Pattern: God Adapter
public class MonolithicAdapter implements
    CustomerService, BillingService, OrderService, InventoryService {
    // Ein Adapter für alles - verletzt SRP
}

// Anti-Pattern: Leaky Abstraction
public interface CustomerService {
    SoapResponse getCustomerSoapData(String id); // SOAP Details in Interface
    MainframeRecord getMainframeRecord(String id); // Mainframe Details
}
```

## 13. Moderne Alternativen

### Dependency Injection statt Singleton
```java
@Configuration
public class ConfigurationConfig {
    
    @Bean
    @Scope("singleton") // Spring managed Singleton
    public ConfigurationService configurationService() {
        return new ConfigurationService();
    }
}
```

### Reactive Adapters
```java
public interface ReactiveCustomerService {
    Mono<CustomerProfile> getCustomerProfile(CustomerId customerId);
    Flux<CustomerProfile> searchCustomers(CustomerSearchCriteria criteria);
}

@Component
public class ReactiveMainframeAdapter implements ReactiveCustomerService {
    
    @Override
    public Mono<CustomerProfile> getCustomerProfile(CustomerId customerId) {
        return Mono.fromCallable(() -> {
            // Legacy call in separate thread
            return legacyAdapter.getCustomerProfile(customerId);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .timeout(Duration.ofSeconds(5))
        .onErrorMap(TimeoutException.class, ex -> 
            new CustomerDataAccessException("Mainframe timeout", ex));
    }
}
```

## 14. Zusammenfassung

### Schlüsselerkenntnisse
- Singleton Pattern für Shared Resources (Connection Pools, Configuration)
- Adapter Pattern für Legacy-Integration ohne Domain-Verschmutzung
- Clean Architecture durch Dependency Inversion
- Anti-Corruption Layer schützt Domain Model

### Vorteile
- **Singleton**: Resourcen-Sharing, einheitliche Konfiguration
- **Adapter**: Legacy-Integration ohne Domain-Änderung
- **Clean Architecture**: Testbarkeit, Flexibilität, Wartbarkeit

### Nachteile
- **Singleton**: Potentielle Tight Coupling, Testing-Schwierigkeiten
- **Adapter**: Mehr Klassen, Performance-Overhead
- **Clean Architecture**: Anfängliche Komplexität höher

### Best Practices
- Enum-Singleton für einfache Fälle verwenden
- Adapter-Factory für Multi-System Support
- Composite Adapter für Daten-Aggregation
- Caching-Decorator für Performance
- Thread-Safety bei Singleton beachten

### Integration in größere Systeme
- Microservice-Architektur mit Adapter-Services
- Event-Driven Architecture für Daten-Synchronisation
- Circuit Breaker Pattern für Resilience
- Monitoring und Observability für Legacy-Adapter

### Nächste Schritte
- Tag 2: Structural Patterns (Facade, Decorator, Proxy)
- Integration Pattern: Event Sourcing, CQRS
- Resilience Pattern: Circuit Breaker, Bulkhead, Timeout