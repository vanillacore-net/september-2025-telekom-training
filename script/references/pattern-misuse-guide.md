# Pattern Misuse Guide

## Übersicht

Design Patterns sind bewährte Lösungsansätze für wiederkehrende Probleme. Jedoch kann deren unsachgemäße Anwendung zu Anti-Patterns und verschlechterter Codequalität führen. Dieser Guide dokumentiert häufige Pattern-Missverständnisse und deren korrekte Anwendung in Telekom-Projekten.

## Pattern Misuse Categories

### 1. Over-Engineering (Überanwendung)

#### Singleton Pattern Missbrauch

**Falsche Anwendung**:
```java
// FALSCH: Singleton für alles verwenden
public class TelekomConfigSingleton {
    private static TelekomConfigSingleton instance;
    private Map<String, String> config = new HashMap<>();
    
    private TelekomConfigSingleton() {}
    
    public static synchronized TelekomConfigSingleton getInstance() {
        if (instance == null) {
            instance = new TelekomConfigSingleton();
        }
        return instance;
    }
    
    // Probleme:
    // - Threading-Probleme
    // - Schwer testbar
    // - Versteckte Dependencies
    // - Global State
}

// FALSCH: Singleton für Service-Klassen
public class TelekomCustomerService {
    private static TelekomCustomerService instance;
    
    // Service-Logik sollte nicht Singleton sein!
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Dependency Injection verwenden
@Service
public class TelekomCustomerService {
    private final TelekomConfigProperties config;
    
    public TelekomCustomerService(TelekomConfigProperties config) {
        this.config = config;
    }
    
    // Testbar, konfigurierbar, keine globalen Dependencies
}

// RICHTIG: Singleton nur für echte einmalige Ressourcen
public class TelekomDatabaseConnectionPool {
    private static volatile TelekomDatabaseConnectionPool instance;
    private final HikariDataSource dataSource;
    
    private TelekomDatabaseConnectionPool() {
        // Thread-safe Initialisierung
        this.dataSource = new HikariDataSource(createConfig());
    }
    
    public static TelekomDatabaseConnectionPool getInstance() {
        if (instance == null) {
            synchronized (TelekomDatabaseConnectionPool.class) {
                if (instance == null) {
                    instance = new TelekomDatabaseConnectionPool();
                }
            }
        }
        return instance;
    }
}
```

**Erkennungsmerkmale des Missbrauchs**:
- Viele Singleton-Klassen im Projekt
- Service-Logik als Singleton implementiert
- Tests, die nicht parallel laufen können
- Global State Probleme

#### Factory Pattern Übernutzung

**Falsche Anwendung**:
```java
// FALSCH: Factory für einfache Objekterstellung
public class TelekomStringFactory {
    public static String createString(String value) {
        return new String(value); // Überflüssige Factory
    }
    
    public static String createEmptyString() {
        return ""; // Triviale Erstellung
    }
}

// FALSCH: Factory Pattern ohne Variabilität
public class TelekomCustomerFactory {
    public TelekomCustomer createCustomer() {
        return new TelekomCustomer(); // Immer der gleiche Typ
    }
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Factory für komplexe Objekterstellung mit Variationen
public class TelekomTariffFactory {
    
    public Tariff createTariff(TariffType type, CustomerSegment segment) {
        switch (type) {
            case MOBILE:
                return createMobileTariff(segment);
            case DSL:
                return createDSLTariff(segment);
            case TV:
                return createTVTariff(segment);
            default:
                throw new IllegalArgumentException("Unknown tariff type: " + type);
        }
    }
    
    private MobileTariff createMobileTariff(CustomerSegment segment) {
        MobileTariff tariff = new MobileTariff();
        
        // Komplexe Konfiguration basierend auf Segment
        if (segment == CustomerSegment.BUSINESS) {
            tariff.setDataVolume(50);
            tariff.setInternationalRoaming(true);
            tariff.setPriority(Priority.HIGH);
        } else {
            tariff.setDataVolume(10);
            tariff.setInternationalRoaming(false);
            tariff.setPriority(Priority.NORMAL);
        }
        
        return tariff;
    }
}
```

### 2. Pattern Mismatching (Falscher Kontext)

#### Observer Pattern im falschen Kontext

**Falsche Anwendung**:
```java
// FALSCH: Observer für synchrone, direkte Kommunikation
public class TelekomBillingService {
    private List<Observer> observers = new ArrayList<>();
    
    public void generateBill(String customerId) {
        Bill bill = createBill(customerId);
        
        // PROBLEM: Synchrone Verarbeitung mit Observer
        notifyObservers(bill); // Blockiert hier
        
        saveBill(bill);
    }
    
    private void notifyObservers(Bill bill) {
        for (Observer observer : observers) {
            observer.update(bill); // Kann Exceptions werfen, langsam sein
        }
    }
}

// FALSCH: Observer für einfache Callbacks
public class TelekomValidation {
    private ValidationObserver observer;
    
    public boolean validateCustomer(Customer customer) {
        boolean valid = performValidation(customer);
        if (observer != null) {
            observer.onValidationComplete(valid); // Overkill für einfachen Callback
        }
        return valid;
    }
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Observer für asynchrone, lose gekoppelte Events
@Component
public class TelekomBillingService {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public TelekomBillingService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    public void generateBill(String customerId) {
        Bill bill = createBill(customerId);
        saveBill(bill);
        
        // Asynchrones Event - keine Blockierung
        eventPublisher.publishEvent(new BillGeneratedEvent(bill));
    }
}

@EventListener
@Async
public class TelekomNotificationService {
    
    public void handleBillGenerated(BillGeneratedEvent event) {
        // Asynchrone Verarbeitung
        sendBillNotification(event.getBill());
    }
}

// RICHTIG: Einfacher Callback für direkte Kommunikation
public class TelekomValidation {
    
    public boolean validateCustomer(Customer customer, Consumer<Boolean> callback) {
        boolean valid = performValidation(customer);
        callback.accept(valid); // Direkter, typsicherer Callback
        return valid;
    }
}
```

#### Strategy Pattern Missbrauch

**Falsche Anwendung**:
```java
// FALSCH: Strategy für statische Logik ohne Variationen
public interface TelekomMathStrategy {
    int add(int a, int b);
    int multiply(int a, int b);
}

public class TelekomBasicMathStrategy implements TelekomMathStrategy {
    public int add(int a, int b) {
        return a + b; // Triviale Implementierung
    }
    
    public int multiply(int a, int b) {
        return a * b; // Keine Variationen möglich
    }
}

// FALSCH: Strategy mit nur einer Implementierung
public interface TelekomStringFormatter {
    String format(String input);
}

public class TelekomUpperCaseFormatter implements TelekomStringFormatter {
    public String format(String input) {
        return input.toUpperCase(); // Nur eine sinnvolle Implementierung
    }
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Strategy für echte Variationen in der Geschäftslogik
public interface TelekomPricingStrategy {
    BigDecimal calculatePrice(Customer customer, List<Service> services);
}

@Component
public class TelekomBusinessPricingStrategy implements TelekomPricingStrategy {
    
    @Override
    public BigDecimal calculatePrice(Customer customer, List<Service> services) {
        BigDecimal totalPrice = services.stream()
            .map(Service::getBasePrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        // Business-spezifische Rabatte
        BigDecimal businessDiscount = totalPrice.multiply(new BigDecimal("0.15"));
        BigDecimal finalPrice = totalPrice.subtract(businessDiscount);
        
        // Volume-basierte Zusatzrabatte
        if (services.size() > 5) {
            finalPrice = finalPrice.multiply(new BigDecimal("0.95"));
        }
        
        return finalPrice;
    }
}

@Component
public class TelekomPrivatePricingStrategy implements TelekomPricingStrategy {
    
    @Override
    public BigDecimal calculatePrice(Customer customer, List<Service> services) {
        BigDecimal totalPrice = services.stream()
            .map(Service::getBasePrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        // Privatkunden-spezifische Logik
        if (customer.isNewCustomer()) {
            BigDecimal newCustomerDiscount = totalPrice.multiply(new BigDecimal("0.10"));
            totalPrice = totalPrice.subtract(newCustomerDiscount);
        }
        
        // Loyalitätsrabatte
        if (customer.getCustomerSince().isBefore(LocalDate.now().minusYears(2))) {
            totalPrice = totalPrice.multiply(new BigDecimal("0.95"));
        }
        
        return totalPrice;
    }
}

// Verwendung mit echten Variationen
@Service
public class TelekomPricingService {
    
    private final Map<CustomerType, TelekomPricingStrategy> strategies;
    
    public TelekomPricingService(List<TelekomPricingStrategy> strategies) {
        this.strategies = strategies.stream().collect(
            Collectors.toMap(
                this::getStrategyType,
                Function.identity()
            )
        );
    }
    
    public BigDecimal calculatePrice(Customer customer, List<Service> services) {
        TelekomPricingStrategy strategy = strategies.get(customer.getType());
        if (strategy == null) {
            throw new IllegalArgumentException("No pricing strategy for: " + customer.getType());
        }
        return strategy.calculatePrice(customer, services);
    }
}
```

### 3. Implementation Misunderstanding

#### Builder Pattern Fehlerhafte Implementierung

**Falsche Anwendung**:
```java
// FALSCH: Builder für einfache Objekte
public class TelekomCustomer {
    private String name;
    private String email;
    
    public static class Builder {
        private String name;
        private String email;
        
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }
        
        public TelekomCustomer build() {
            return new TelekomCustomer(name, email); // Overkill für 2 Felder
        }
    }
}

// FALSCH: Builder ohne Validierung
public class TelekomOrder {
    // Viele Felder...
    
    public static class Builder {
        private String customerId;
        private List<Product> products = new ArrayList<>();
        private PaymentMethod paymentMethod;
        
        public TelekomOrder build() {
            return new TelekomOrder(customerId, products, paymentMethod);
            // FEHLT: Validierung der Required Fields
        }
    }
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Builder für komplexe Objekte mit Validierung
public class TelekomContract {
    private final String customerId;
    private final String contractNumber;
    private final ContractType type;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<Service> services;
    private final PaymentMethod paymentMethod;
    private final BillingAddress billingAddress;
    private final boolean autoRenewal;
    private final BigDecimal securityDeposit;
    
    private TelekomContract(Builder builder) {
        this.customerId = builder.customerId;
        this.contractNumber = builder.contractNumber;
        this.type = builder.type;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.services = Collections.unmodifiableList(new ArrayList<>(builder.services));
        this.paymentMethod = builder.paymentMethod;
        this.billingAddress = builder.billingAddress;
        this.autoRenewal = builder.autoRenewal;
        this.securityDeposit = builder.securityDeposit;
    }
    
    public static class Builder {
        // Required fields
        private final String customerId;
        private final ContractType type;
        
        // Optional fields with defaults
        private String contractNumber;
        private LocalDate startDate = LocalDate.now();
        private LocalDate endDate;
        private List<Service> services = new ArrayList<>();
        private PaymentMethod paymentMethod = PaymentMethod.SEPA;
        private BillingAddress billingAddress;
        private boolean autoRenewal = true;
        private BigDecimal securityDeposit = BigDecimal.ZERO;
        
        public Builder(String customerId, ContractType type) {
            this.customerId = Objects.requireNonNull(customerId);
            this.type = Objects.requireNonNull(type);
        }
        
        public Builder contractNumber(String contractNumber) {
            this.contractNumber = contractNumber;
            return this;
        }
        
        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder services(List<Service> services) {
            this.services = new ArrayList<>(services);
            return this;
        }
        
        public Builder addService(Service service) {
            this.services.add(service);
            return this;
        }
        
        public TelekomContract build() {
            validateRequiredFields();
            validateBusinessRules();
            generateDefaults();
            
            return new TelekomContract(this);
        }
        
        private void validateRequiredFields() {
            if (customerId == null || customerId.trim().isEmpty()) {
                throw new IllegalStateException("Customer ID is required");
            }
            if (services.isEmpty()) {
                throw new IllegalStateException("At least one service must be specified");
            }
        }
        
        private void validateBusinessRules() {
            if (endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalStateException("End date cannot be before start date");
            }
            
            if (type == ContractType.PREPAID && securityDeposit.compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalStateException("Prepaid contracts cannot have security deposit");
            }
        }
        
        private void generateDefaults() {
            if (contractNumber == null) {
                contractNumber = generateContractNumber(customerId, type);
            }
            
            if (endDate == null && type == ContractType.FIXED_TERM) {
                endDate = startDate.plusYears(2); // Standard 2-Jahr-Vertrag
            }
        }
    }
}

// Verwendung
TelekomContract contract = new TelekomContract.Builder("CUST-123", ContractType.FIXED_TERM)
    .addService(new MobileService("Mobile Premium"))
    .addService(new DSLService("DSL 100"))
    .paymentMethod(PaymentMethod.CREDIT_CARD)
    .autoRenewal(false)
    .securityDeposit(new BigDecimal("100.00"))
    .build();
```

### 4. Performance Anti-Patterns

#### Repository Pattern Missbrauch

**Falsche Anwendung**:
```java
// FALSCH: N+1 Query Problem durch schlechte Repository-Nutzung
@Service
public class TelekomOrderService {
    
    private final TelekomCustomerRepository customerRepository;
    private final TelekomOrderRepository orderRepository;
    
    public List<OrderSummary> getOrderSummaries() {
        List<Order> orders = orderRepository.findAll(); // 1 Query
        
        return orders.stream()
            .map(order -> {
                // N separate Queries - PROBLEM!
                Customer customer = customerRepository.findById(order.getCustomerId()).orElse(null);
                return new OrderSummary(order, customer);
            })
            .collect(Collectors.toList());
    }
}

// FALSCH: Repository mit zu granularen Methoden
public interface TelekomCustomerRepository extends JpaRepository<Customer, String> {
    
    // Zu spezifische Query-Methoden
    Customer findByFirstNameAndLastName(String firstName, String lastName);
    Customer findByFirstNameAndLastNameAndAge(String firstName, String lastName, int age);
    Customer findByFirstNameAndLastNameAndAgeAndCity(String firstName, String lastName, int age, String city);
    
    // Führt zu vielen ähnlichen Queries
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Optimierte Queries mit JOINs
public interface TelekomOrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.customer " +
           "LEFT JOIN FETCH o.orderItems " +
           "WHERE o.status = :status")
    List<Order> findOrdersWithCustomerAndItems(@Param("status") OrderStatus status);
}

// RICHTIG: Service mit optimierten Queries
@Service
public class TelekomOrderService {
    
    private final TelekomOrderRepository orderRepository;
    
    public List<OrderSummary> getOrderSummaries(OrderStatus status) {
        // Eine Query mit JOINs statt N+1 Queries
        List<Order> orders = orderRepository.findOrdersWithCustomerAndItems(status);
        
        return orders.stream()
            .map(order -> new OrderSummary(order, order.getCustomer()))
            .collect(Collectors.toList());
    }
}

// RICHTIG: Flexible Repository mit Specifications
@Repository
public class TelekomCustomerRepository {
    
    private final JpaSpecificationExecutor<Customer> specificationExecutor;
    
    public List<Customer> findCustomers(CustomerSearchCriteria criteria) {
        Specification<Customer> spec = Specification.where(null);
        
        if (criteria.getFirstName() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.like(root.get("firstName"), "%" + criteria.getFirstName() + "%"));
        }
        
        if (criteria.getLastName() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.like(root.get("lastName"), "%" + criteria.getLastName() + "%"));
        }
        
        if (criteria.getAge() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("age"), criteria.getAge()));
        }
        
        // Eine flexible Query statt vieler spezifischer Methoden
        return specificationExecutor.findAll(spec);
    }
}
```

### 5. Security Pattern Misuse

#### Proxy Pattern für Security

**Falsche Anwendung**:
```java
// FALSCH: Security in Business Logic vermischt
public class TelekomCustomerService {
    
    public Customer getCustomer(String customerId, User currentUser) {
        // Security Logic vermischt mit Business Logic
        if (!currentUser.hasRole("ADMIN") && !currentUser.getId().equals(customerId)) {
            throw new SecurityException("Access denied");
        }
        
        Customer customer = customerRepository.findById(customerId);
        
        // Weitere vermischte Security Checks
        if (customer.isVip() && !currentUser.hasRole("VIP_ACCESS")) {
            customer.setPersonalDetails(null); // Security durch Datenmodifikation
        }
        
        return customer;
    }
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Security durch Annotations und AOP
@Service
public class TelekomCustomerService {
    
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.name")
    public Customer getCustomer(String customerId) {
        return customerRepository.findById(customerId);
    }
    
    @PreAuthorize("hasRole('VIP_ACCESS')")
    public Customer getVipCustomerDetails(String customerId) {
        return customerRepository.findById(customerId);
    }
}

// RICHTIG: Security Proxy Pattern
@Component
public class TelekomSecurityProxy implements TelekomCustomerService {
    
    private final TelekomCustomerService target;
    private final SecurityContext securityContext;
    
    public TelekomSecurityProxy(TelekomCustomerService target, SecurityContext securityContext) {
        this.target = target;
        this.securityContext = securityContext;
    }
    
    @Override
    public Customer getCustomer(String customerId) {
        // Security Check isoliert im Proxy
        validateAccess(customerId);
        
        // Delegation an echte Implementierung
        Customer customer = target.getCustomer(customerId);
        
        // Post-processing Security
        return applyDataMasking(customer);
    }
    
    private void validateAccess(String customerId) {
        User currentUser = securityContext.getCurrentUser();
        
        if (!currentUser.hasRole("ADMIN") && 
            !currentUser.getId().equals(customerId)) {
            throw new AccessDeniedException("Customer access denied");
        }
    }
    
    private Customer applyDataMasking(Customer customer) {
        User currentUser = securityContext.getCurrentUser();
        
        if (customer.isVip() && !currentUser.hasRole("VIP_ACCESS")) {
            return customer.withMaskedPersonalDetails();
        }
        
        return customer;
    }
}
```

## Common Pattern Misuse Scenarios

### 1. MVC Pattern Violations

**Model-View-Controller Boundary Violations**:

```java
// FALSCH: Business Logic in Controller
@RestController
public class TelekomOrderController {
    
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        // FEHLER: Business Logic im Controller
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        
        // Komplexe Geschäftslogik gehört nicht in Controller
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : request.getItems()) {
            BigDecimal itemPrice = item.getPrice();
            
            // Rabattberechnung
            if (item.getQuantity() > 10) {
                itemPrice = itemPrice.multiply(new BigDecimal("0.9"));
            }
            
            totalPrice = totalPrice.add(itemPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        
        // Weitere 50 Zeilen Business Logic...
        
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(savedOrder);
    }
}
```

**Korrekte MVC Implementierung**:

```java
// RICHTIG: Klare Trennung der Verantwortlichkeiten

// Controller - nur für HTTP-spezifische Logik
@RestController
public class TelekomOrderController {
    
    private final TelekomOrderService orderService;
    
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        // Nur Input-Validation und Delegation
        Order order = orderService.createOrder(mapToOrder(request));
        OrderResponse response = mapToResponse(order);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    private Order mapToOrder(CreateOrderRequest request) {
        // Einfache Datenkonvertierung
        return Order.builder()
            .customerId(request.getCustomerId())
            .items(request.getItems())
            .build();
    }
}

// Service - Business Logic
@Service
public class TelekomOrderService {
    
    private final OrderRepository orderRepository;
    private final TelekomPricingService pricingService;
    private final TelekomInventoryService inventoryService;
    
    @Transactional
    public Order createOrder(Order order) {
        // Business Logic Orchestrierung
        validateOrder(order);
        reserveInventory(order);
        
        BigDecimal totalPrice = pricingService.calculateTotalPrice(order);
        order.setTotalPrice(totalPrice);
        
        Order savedOrder = orderRepository.save(order);
        publishOrderCreatedEvent(savedOrder);
        
        return savedOrder;
    }
    
    private void validateOrder(Order order) {
        if (order.getItems().isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }
        // Weitere Validierungslogik
    }
}

// Model - reine Datenstrukturen ohne Business Logic
@Entity
public class Order {
    private String id;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    
    // Nur Getters/Setters, keine Business Logic
}
```

### 2. Dependency Injection Anti-Patterns

#### Constructor Injection vs. Field Injection Missverständnisse

**Falsche Anwendung**:
```java
// FALSCH: Field Injection überall verwenden
@Service
public class TelekomBillingService {
    
    @Autowired
    private CustomerRepository customerRepository; // Nicht testbar
    
    @Autowired
    private PricingService pricingService; // Versteckte Dependencies
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;
    
    @Autowired
    private TaxService taxService; // 7+ Dependencies = Code Smell
    
    // Constructor fehlt - schwer zu testen
    // Dependencies nicht klar sichtbar
}
```

**Korrekte Anwendung**:
```java
// RICHTIG: Constructor Injection mit bewusster Dependency-Kontrolle
@Service
public class TelekomBillingService {
    
    private final CustomerRepository customerRepository;
    private final PricingService pricingService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    
    // Constructor macht Dependencies explizit
    public TelekomBillingService(CustomerRepository customerRepository,
                                PricingService pricingService,
                                NotificationService notificationService,
                                AuditService auditService) {
        this.customerRepository = customerRepository;
        this.pricingService = pricingService;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }
    
    // Wenn zu viele Dependencies: Refactoring nötig
    // Builder oder Facade Pattern anwenden
}

// RICHTIG: Zu viele Dependencies durch Facade lösen
@Service
public class TelekomBillingService {
    
    private final CustomerRepository customerRepository;
    private final BillingOrchestrator billingOrchestrator; // Facade für komplexe Operations
    
    public TelekomBillingService(CustomerRepository customerRepository,
                                BillingOrchestrator billingOrchestrator) {
        this.customerRepository = customerRepository;
        this.billingOrchestrator = billingOrchestrator;
    }
}

@Component
public class BillingOrchestrator {
    // Komplexe Dependencies gekapselt
    private final PricingService pricingService;
    private final NotificationService notificationService;
    private final TaxService taxService;
    
    // Orchestrierung der komplexen Billing-Logik
}
```

## Prevention Strategies

### 1. Code Review Guidelines

#### Pattern Misuse Checklist

```markdown
# Telekom Pattern Review Checklist

## Singleton Review
- [ ] Ist Singleton wirklich nötig? (Thread-safe Resource?)
- [ ] Kann durch Dependency Injection ersetzt werden?
- [ ] Ist die Implementierung Thread-safe?
- [ ] Sind Tests möglich und parallelisierbar?

## Factory Review  
- [ ] Werden verschiedene Objekttypen erstellt?
- [ ] Ist die Erstellung komplex genug für eine Factory?
- [ ] Gibt es echte Konfigurationsvariationen?

## Observer Review
- [ ] Ist lose Kopplung gewünscht?
- [ ] Sind Events asynchron verarbeitet?
- [ ] Gibt es mehrere unabhängige Observer?

## Strategy Review
- [ ] Gibt es mindestens 2 unterschiedliche Implementierungen?
- [ ] Variiert die Logik zur Laufzeit?
- [ ] Sind die Strategien austauschbar?

## Builder Review
- [ ] Hat das Objekt >5 Parameter?
- [ ] Sind Parameter optional?
- [ ] Ist Validierung komplex?
- [ ] Werden Required vs. Optional Fields unterschieden?

## Repository Review
- [ ] Werden N+1 Queries vermieden?
- [ ] Sind JOINs für related Data verwendet?
- [ ] Ist die Granularität angemessen?

## Dependency Injection Review
- [ ] Constructor Injection verwendet?
- [ ] Dependencies <5 pro Klasse?
- [ ] Sind alle Dependencies wirklich nötig?
```

### 2. Automated Detection

#### Static Analysis Rules

```xml
<!-- Custom PMD Rules für Pattern Misuse -->
<rule name="SingletonMisuse" language="java" message="Avoid Singleton for service classes">
    <description>
        Service classes should not be implemented as Singletons.
        Use Dependency Injection instead.
    </description>
    <priority>2</priority>
    <properties>
        <property name="xpath">
            <value>
//ClassOrInterfaceDeclaration[contains(@Image, 'Service')]
    //MethodDeclaration[@Name='getInstance']
            </value>
        </property>
    </properties>
</rule>

<rule name="ExcessiveDependencies" language="java" message="Too many constructor parameters">
    <description>
        Classes with more than 5 constructor parameters may have too many responsibilities.
    </description>
    <priority>3</priority>
    <properties>
        <property name="xpath">
            <value>
//ConstructorDeclaration[count(FormalParameters/FormalParameter) > 5]
            </value>
        </property>
    </properties>
</rule>
```

#### SonarQube Custom Rules

```java
@Rule(key = "telekom-pattern-misuse")
public class TelekomPatternMisuseRule extends JavaFilesScannerTest {
    
    @Override
    public void visitClass(ClassTree tree) {
        // Factory Pattern Misuse Detection
        if (tree.simpleName().name().endsWith("Factory")) {
            checkFactoryComplexity(tree);
        }
        
        // Builder Pattern Misuse Detection
        if (hasBuilderInnerClass(tree)) {
            checkBuilderNecessity(tree);
        }
        
        super.visitClass(tree);
    }
    
    private void checkFactoryComplexity(ClassTree factoryClass) {
        List<MethodTree> factoryMethods = getFactoryMethods(factoryClass);
        
        // Wenn alle Factory-Methoden nur "new Object()" zurückgeben
        boolean allTrivial = factoryMethods.stream()
            .allMatch(this::isTrivialFactory);
            
        if (allTrivial && factoryMethods.size() == 1) {
            context().reportIssue(this, factoryClass, 
                "Factory pattern unnecessary for simple object creation");
        }
    }
    
    private boolean isTrivialFactory(MethodTree method) {
        // Prüfung ob nur "return new SomeClass();" ohne komplexe Logik
        return isSimpleReturnStatement(method.block());
    }
}
```

### 3. Team Training

#### Pattern Workshops

**Workshop 1: Pattern Fundamentals**
- Wann Pattern anwenden vs. einfache Lösungen
- Kosten-Nutzen-Analyse von Patterns
- Telekom-spezifische Anwendungsfälle

**Workshop 2: Common Misuse Scenarios**
- Live-Coding Session: Refactoring von Misused Patterns
- Code Review Practice
- Pattern Alternatives

**Workshop 3: Performance Impact**
- Messen von Pattern Performance
- Profiling von Pattern-heavy Code
- Optimization Strategies

#### Documentation Standards

```markdown
# Telekom Pattern Documentation Template

## Pattern: [Name]

### Intent
Was soll das Pattern lösen?

### When to Use
- Spezifische Szenarien
- Minimum Komplexität
- Team Größe Considerations

### When NOT to Use
- Alternative einfache Lösungen
- Performance Constraints
- Over-engineering Warnsignale

### Telekom Implementation
- Code Beispiele
- Spring Integration
- Testing Strategies

### Common Misuses
- Typische Fehler
- Detection Methods
- Refactoring Approaches

### Performance Considerations
- Benchmarks
- Monitoring Points
- Optimization Tips

### Review Checklist
- [ ] Specific criteria
- [ ] Code quality gates
- [ ] Performance thresholds
```

### 4. Metrics and Monitoring

#### Pattern Usage Metrics

```java
@Component
public class TelekomPatternMetrics {
    
    private final MeterRegistry meterRegistry;
    
    @EventListener
    public void trackPatternUsage(PatternDetectionEvent event) {
        Counter.builder("pattern.usage")
            .tag("pattern", event.getPatternType())
            .tag("project", event.getProjectName())
            .tag("complexity", event.getComplexityLevel())
            .register(meterRegistry)
            .increment();
    }
    
    @EventListener
    public void trackPatternMisuse(PatternMisuseEvent event) {
        Counter.builder("pattern.misuse")
            .tag("pattern", event.getPatternType())
            .tag("misuse_type", event.getMisuseType())
            .tag("severity", event.getSeverity())
            .register(meterRegistry)
            .increment();
            
        // Alert bei kritischen Misuses
        if (event.getSeverity() == Severity.HIGH) {
            alertService.sendAlert("Pattern Misuse Detected", event);
        }
    }
}
```

#### Quality Gates

```yaml
# SonarQube Quality Gate für Pattern Misuse
quality_gate:
  name: "Telekom Pattern Quality Gate"
  conditions:
    - metric: "pattern_misuse_density"
      op: "GT"
      error: "5"
      warning: "3"
    - metric: "singleton_count"
      op: "GT"
      error: "2"
      warning: "1"
    - metric: "factory_complexity_avg"
      op: "LT"
      error: "3"
      warning: "5"
    - metric: "builder_usage_ratio"
      op: "LT"
      error: "0.8"
      warning: "0.9"
```

## Best Practices Summary

### 1. Pattern Selection Criteria

**Verwende Pattern wenn**:
- Echte Komplexität vorhanden ist
- Verschiedene Implementierungen möglich sind
- Team-Größe Pattern-Overhead rechtfertigt
- Performance nicht kritisch beeinträchtigt wird

**Verwende KEINE Pattern wenn**:
- Einfache Lösungen ausreichen
- Nur eine Implementierung existiert
- Over-Engineering entsteht
- Maintenance-Overhead zu hoch wird

### 2. Implementation Guidelines

**Code Quality**:
- Constructor Injection bevorzugen
- Dependencies minimal halten (<5)
- Interfaces für Abstraktion nutzen
- Tests für alle Pattern-Implementierungen

**Performance**:
- Pattern-Overhead messen
- Lazy Initialization wo möglich
- Caching für teure Operationen
- Profiling bei kritischen Pfaden

### 3. Review Process

**Pre-Implementation**:
- Pattern-Notwendigkeit rechtfertigen
- Alternativen evaluieren
- Performance-Impact abschätzen

**Code Review**:
- Pattern-Checklist verwenden
- Misuse-Detection ausführen
- Team-Diskussion bei Zweifeln

**Post-Implementation**:
- Metrics überwachen
- Performance validieren
- Documentation aktualisieren

### 4. Continuous Improvement

**Learning**:
- Misuse-Cases dokumentieren
- Team-Learnings teilen
- Best-Practices aktualisieren

**Tooling**:
- Static Analysis Rules erweitern
- Metrics Dashboard pflegen
- Automated Detection verbessern

**Culture**:
- Quality-first Mindset fördern
- Knowledge Sharing Sessions
- Pattern-Expertise aufbauen