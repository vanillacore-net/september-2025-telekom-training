# Block 1: Creational Patterns

---

## Factory Method Pattern

---

### Was ist hier schlecht? - Customer Creation Problems

```java
public class CustomerManager {
    
    public Customer createCustomer(String type, String name, String contractId) {
        // Code-Smell: Switch Statement / Long Parameter List
        switch (type) {
            case "PRIVATE":
                Customer privateCustomer = new Customer();
                privateCustomer.setName(name);
                privateCustomer.setContractId(contractId);
                privateCustomer.setTariffOptions(Arrays.asList("Basic", "Comfort"));
                privateCustomer.setPaymentMethod("SEPA");
                privateCustomer.setSupportLevel("Standard");
                return privateCustomer;
                
            case "BUSINESS":
                Customer businessCustomer = new Customer();
                businessCustomer.setName(name);
                businessCustomer.setContractId(contractId);
                businessCustomer.setTariffOptions(Arrays.asList("Professional", "Enterprise"));
                businessCustomer.setPaymentMethod("Invoice");
                businessCustomer.setSupportLevel("Business");
                businessCustomer.setVatNumber(generateVatNumber());
                businessCustomer.setAccountManager(assignAccountManager());
                return businessCustomer;
                
            case "PREMIUM":
                Customer premiumCustomer = new Customer();
                premiumCustomer.setName(name);
                premiumCustomer.setContractId(contractId);
                premiumCustomer.setTariffOptions(Arrays.asList("Premium", "Unlimited"));
                premiumCustomer.setPaymentMethod("DirectDebit");
                premiumCustomer.setSupportLevel("VIP");
                premiumCustomer.setPrioritySupport(true);
                premiumCustomer.setDedicatedLine(true);
                return premiumCustomer;
                
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }
}
```

Note: This code demonstrates typical problems in legacy Telekom systems where customer creation logic is centralized with extensive switch statements.

---

### Code Smells identifiziert

• **Long Method**: createCustomer method has too many lines and responsibilities
• **Switch Statements**: Type-based branching indicates missing polymorphism  
• **Feature Envy**: Method manipulates more data than it owns
• **Duplicate Code**: Repeated assignments in each case block
• **Open/Closed Principle Violation**: New customer types require modifying existing method
• **Single Responsibility Violation**: One method handles multiple customer types
• **Hard Dependencies**: Direct coupling to concrete customer configurations

Note: These code smells lead to maintenance nightmares and violation of SOLID principles, making the system inflexible for new customer types.

---

### Lösung: Factory Method Pattern

• **Polymorphism over Conditionals**: Replace switch statements with polymorphic factory methods
• **Separate Creation Logic**: Each factory handles one customer type only
• **Template Method Integration**: Common business logic in abstract base class
• **SOLID Principles Alignment**: Single Responsibility and Open/Closed compliance
• **Extensibility**: New customer types through new factory classes without modification
• **Dependency Inversion**: Depend on Customer interface, not concrete implementations
• **Encapsulation**: Customer-specific logic encapsulated in respective classes

Note: Factory Method pattern defines an interface for creating objects but lets subclasses decide which class to instantiate.

---

### Implementierung - Factory Method Solution

```java
// Abstract Factory with Template Method
public abstract class CustomerFactory {
    
    // Factory Method - implemented by subclasses
    protected abstract Customer createCustomer(String name, String contractId);
    
    // Template Method - uses Factory Method
    public Customer processNewCustomer(String name, String contractId) {
        Customer customer = createCustomer(name, contractId);
        
        // Common business logic
        validateContract(customer);
        persistCustomer(customer);
        sendWelcomeMessage(customer);
        
        return customer;
    }
}

// Concrete Factory Implementation
public class PrivateCustomerFactory extends CustomerFactory {
    @Override
    protected Customer createCustomer(String name, String contractId) {
        return new PrivateCustomer(name, contractId);
    }
}

public class BusinessCustomerFactory extends CustomerFactory {
    @Override
    protected Customer createCustomer(String name, String contractId) {
        return new BusinessCustomer(name, contractId);
    }
}
```

Note: The solution separates object creation from business logic, making the system extensible and maintainable.

---

## Abstract Factory Pattern

---

### Was ist hier schlecht? - Multi-Channel Service Chaos

```java
public class CustomerService {
    
    public CustomerData getCustomer(String customerId, String channel) {
        if ("WEB".equals(channel)) {
            // Web-specific authentication
            WebAuthService webAuth = new WebAuthService();
            if (!webAuth.validateSession(getCurrentSession())) {
                throw new AuthenticationException();
            }
            
            // Web-API call
            WebCustomerAPI webAPI = new WebCustomerAPI();
            return webAPI.getCustomerData(customerId);
            
        } else if ("MOBILE".equals(channel)) {
            // Mobile-specific OAuth
            MobileOAuthService mobileAuth = new MobileOAuthService();
            if (!mobileAuth.validateToken(getCurrentToken())) {
                throw new AuthenticationException();
            }
            
            // Mobile-API with different data formats
            MobileCustomerAPI mobileAPI = new MobileCustomerAPI();
            return transformMobileResponse(mobileAPI.getCustomer(customerId));
            
        } else if ("CALLCENTER".equals(channel)) {
            // Call Center employee authentication
            EmployeeAuthService empAuth = new EmployeeAuthService();
            if (!empAuth.validateEmployee(getCurrentEmployee())) {
                throw new AuthenticationException();
            }
            
            // Legacy Backend System
            LegacyCustomerSystem legacy = new LegacyCustomerSystem();
            return transformLegacyData(legacy.retrieveCustomer(customerId));
        }
    }
}
```

Note: Each channel requires different authentication, APIs, and data transformation - leading to a maintenance nightmare.

---

### Code Smells identifiziert

• **God Object**: One class knows all channel implementations and formats
• **Switch Statement**: Channel-based branching violates Open/Closed principle
• **Tight Coupling**: Direct dependencies to concrete channel implementations  
• **Interface Segregation Violation**: One service trying to handle all channels
• **Multiple Responsibilities**: Authentication + Data retrieval + Format conversion
• **Hard to Test**: Dependencies to external systems make unit testing difficult
• **Inflexible**: New channels require modification of existing service code

Note: This approach leads to exponential complexity growth as new channels are added to the Telekom ecosystem.

---

### Lösung: Abstract Factory Pattern

• **Service Families**: Group related services (Auth, Customer, Billing) by channel
• **Consistent Interfaces**: Same service interface across all channels
• **Channel Abstraction**: Abstract factory creates complete service suite per channel
• **Loose Coupling**: Business logic depends only on service interfaces
• **Extensibility**: New channels through new factory implementations
• **Interface Segregation**: Separate service interfaces for different concerns
• **Dependency Inversion**: High-level modules depend on abstractions

Note: Abstract Factory provides interface for creating families of related objects without specifying their concrete classes.

---

### Implementierung - Service Family Solution

```java
// Abstract Factory for Channel Services
public abstract class ChannelServiceFactory {
    
    // Factory Methods for Service Family
    public abstract AuthenticationService createAuthenticationService();
    public abstract CustomerService createCustomerService();
    public abstract BillingService createBillingService();
    public abstract NotificationService createNotificationService();
    
    // Convenience Method for complete Service Suite
    public ChannelServiceSuite createServiceSuite() {
        return new ChannelServiceSuite(
            createAuthenticationService(),
            createCustomerService(),
            createBillingService(),
            createNotificationService()
        );
    }
}

// Concrete Factory Implementation
public class WebChannelFactory extends ChannelServiceFactory {
    
    @Override
    public AuthenticationService createAuthenticationService() {
        return new WebSessionAuthService();
    }
    
    @Override
    public CustomerService createCustomerService() {
        return new WebCustomerService();
    }
    
    // ... other service creations
}
```

Note: Each concrete factory creates a complete family of related services that work together consistently.

---

## Builder Pattern

---

### Was ist hier schlecht? - Complex Query Construction

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
        
        // Code-Smell: Complex Method with StringBuilder chaos
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
        
        // ... 20+ more if-statements for other parameters
        
        return jdbcTemplate.query(sql.toString(), params.toArray(), customerRowMapper);
    }
}
```

Note: Complex database queries with many optional parameters create unmanageable constructor and method signatures.

---

### Code Smells identifiziert

• **Telescoping Constructor**: Too many parameters make method calls error-prone
• **Complex Method**: Single method doing too much (SQL building + parameter handling)
• **Primitive Obsession**: Many String/Integer parameters instead of Value Objects
• **String Concatenation**: Unsafe and error-prone SQL construction
• **Boolean Parameters**: Hard to understand boolean flags in method calls
• **Open/Closed Violation**: New filters require modifying existing method
• **Poor Readability**: Method calls are difficult to understand and maintain

Note: These issues become exponentially worse as query complexity grows in enterprise Telekom systems.

---

### Lösung: Builder Pattern with Fluent Interface

• **Fluent API Design**: Method chaining creates readable, self-documenting code
• **Step-by-step Construction**: Complex objects built incrementally with validation
• **Immutable Value Objects**: Builder creates immutable search criteria objects
• **Type Safety**: Compile-time validation of required vs optional parameters
• **Flexible Construction**: Same builder can create different query variations
• **Readable Client Code**: Queries read like natural language
• **Validation Integration**: Builder validates constraints before object creation

Note: Builder pattern separates construction of complex objects from their representation, enabling same construction process to create different representations.

---

### Implementierung - Query Builder Solution

```java
// Value Object with Builder
public class CustomerSearchCriteria {
    private final String name;
    private final String contractType;
    private final String tariff;
    private final DateRange contractPeriod;
    private final Address address;
    private final SortCriteria sortCriteria;
    private final Pagination pagination;
    
    // Private Constructor - only creatable via Builder
    private CustomerSearchCriteria(Builder builder) {
        this.name = builder.name;
        this.contractType = builder.contractType;
        // ... assign other fields
    }
    
    // Builder with Fluent Interface
    public static class Builder {
        private String name;
        private String contractType;
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withContractType(String contractType) {
            this.contractType = contractType;
            return this;
        }
        
        public Builder inCity(String city) {
            if (this.address == null) {
                this.address = new Address.Builder().build();
            }
            this.address = this.address.withCity(city);
            return this;
        }
        
        public CustomerSearchCriteria build() {
            validate();
            return new CustomerSearchCriteria(this);
        }
    }
    
    // Factory Methods for common patterns
    public static Builder forBusinessCustomers() {
        return new Builder().withContractType("BUSINESS").onlyActive();
    }
}

// Usage Example
CustomerSearchCriteria criteria = CustomerSearchCriteria.forBusinessCustomers()
    .withName("Schmidt")
    .inCity("Berlin")
    .sortByRevenueDescending()
    .page(1, 20)
    .build();
```

Note: The fluent interface makes complex query construction readable and maintainable while ensuring type safety.

---

## Prototype Pattern

---

### Was ist hier schlecht? - Expensive Configuration Recreation

```java
@Service
public class ServiceConfigurationManager {
    
    // Anti-Pattern: Expensive Recreation for similar configurations
    public ServiceConfiguration createDevConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // Expensive Database Lookups for defaults (200ms+)
        config.setDatabaseSettings(loadDatabaseDefaults());
        config.setSecuritySettings(loadSecurityDefaults());
        config.setCachingSettings(loadCachingDefaults());
        
        // Expensive SSL Certificate Validation (500ms+)
        config.setSslCertificate(validateAndLoadSslCert("dev-cert.pem"));
        
        // Expensive Service Discovery (300ms+)
        config.setServiceEndpoints(discoverAvailableServices());
        
        // Only these values are different
        config.setEnvironment("DEV");
        config.setLogLevel("DEBUG");
        config.setDatabaseUrl("dev-database-url");
        
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // DUPLICATE: Same expensive operations! (1400ms total)
        config.setDatabaseSettings(loadDatabaseDefaults());    // 200ms
        config.setSecuritySettings(loadSecurityDefaults());    // 200ms
        config.setCachingSettings(loadCachingDefaults());      // 200ms
        config.setSslCertificate(validateAndLoadSslCert("test-cert.pem")); // 500ms
        config.setServiceEndpoints(discoverAvailableServices()); // 300ms
        
        // Only environment-specific values differ
        config.setEnvironment("TEST");
        config.setLogLevel("INFO");
        config.setDatabaseUrl("test-database-url");
        
        return config;
    }
}
```

Note: Each configuration creation takes 1400ms+ for identical expensive operations that could be shared.

---

### Code Smells identifiziert

• **Expensive Object Creation**: SSL validation, database discovery, complex initialization
• **Duplicate Expensive Operations**: Same costly operations repeated for each configuration
• **Template Pattern Missing**: 90% identical configurations recreated from scratch
• **Performance Bottleneck**: 1400ms+ latency for configuration creation
• **Resource Waste**: Unnecessary CPU and I/O for identical operations
• **Poor Scalability**: Linear performance degradation with more configurations
• **Memory Inefficiency**: Multiple copies of identical immutable data

Note: In enterprise systems, configuration creation can become a significant performance bottleneck during startup and runtime.

---

### Lösung: Prototype Pattern with Intelligent Cloning

• **Template-based Creation**: Create base templates once, clone for variations
• **Selective Deep Copy**: Clone only mutable parts, share immutable expensive objects
• **Prototype Registry**: Central repository of pre-configured templates
• **Performance Optimization**: 1400ms → 2ms (99.86% improvement)
• **Memory Efficiency**: Share immutable objects between configurations
• **Lazy Initialization**: Templates created only when first needed
• **Customization Strategy**: Functional interfaces for configuration customization

Note: Prototype pattern creates new objects by cloning prototypical instances rather than expensive construction from scratch.

---

### Implementierung - Configuration Cloning Solution

```java
// Cloneable Configuration with Smart Copy Strategy
public abstract class ServiceConfiguration implements Cloneable {
    private String environment;
    private String logLevel;
    private String databaseUrl;
    private DatabaseSettings databaseSettings;
    private SecuritySettings securitySettings;
    private SSLCertificate sslCertificate;
    private ServiceEndpoints serviceEndpoints;
    
    @Override
    public ServiceConfiguration clone() {
        try {
            ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
            
            // Deep Copy for mutable, complex objects
            cloned.databaseSettings = this.databaseSettings.deepCopy();
            cloned.securitySettings = this.securitySettings.deepCopy();
            
            // Shared Reference for immutable, expensive objects
            cloned.sslCertificate = this.sslCertificate; // Immutable
            cloned.serviceEndpoints = this.serviceEndpoints; // Immutable
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
}

// Prototype Registry for Template Management
@Service
public class ConfigurationPrototypeRegistry {
    private final Map<String, ServiceConfiguration> prototypes = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initializePrototypes() {
        // Template created ONCE with all expensive operations
        ServiceConfiguration baseTemplate = TelekomServiceConfiguration.createBaseTemplate();
        prototypes.put("TELEKOM_BASE", baseTemplate);
    }
    
    // Fast Cloning - 1400ms → 2ms!
    public ServiceConfiguration createConfiguration(String templateName, 
                                                   ConfigurationCustomizer customizer) {
        ServiceConfiguration prototype = prototypes.get(templateName);
        ServiceConfiguration cloned = prototype.clone(); // Very fast
        customizer.customize(cloned); // Apply customizations
        return cloned;
    }
}
```

Note: Intelligent cloning strategy shares expensive immutable objects while properly copying mutable state.

---

## Singleton Pattern

---

### Was ist hier schlecht? - Thread Safety and Global State Issues

```java
// Anti-Pattern: Not Thread-Safe Singleton
public class ConfigurationManager {
    private static ConfigurationManager instance;
    private Map<String, String> config = new HashMap<>();
    
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager(); // Race condition!
        }
        return instance;
    }
    
    public void setProperty(String key, String value) {
        config.put(key, value); // Mutable global state!
    }
    
    public String getProperty(String key) {
        return config.get(key);
    }
    
    private ConfigurationManager() {
        // Expensive initialization
        loadConfigurationFromMultipleSources();
    }
}
```

Note: Classic Singleton implementation has thread safety issues and promotes global mutable state.

---

### Code Smells identifiziert  

• **Thread Safety Violation**: Race condition in getInstance() method
• **Global Mutable State**: Configuration can be modified from anywhere  
• **Hard to Test**: Global state makes unit tests interdependent
• **Tight Coupling**: Classes directly depend on Singleton instance
• **Difficult Mocking**: Hard to replace with test doubles in tests
• **Hidden Dependencies**: Dependencies to Singleton not visible in constructor
• **Resource Management**: No clear lifecycle management for expensive resources

Note: These issues become critical in multi-threaded enterprise environments like Telekom systems.

---

### Lösung: Modern Singleton Patterns

• **Thread-Safe Implementation**: Use Enum or Initialization-on-Demand patterns
• **Immutable State**: Configuration becomes read-only after initialization
• **Resource Management**: Proper lifecycle management for connections and pools
• **Dependency Injection**: Combine with DI containers for better testability
• **Configuration Layering**: Support multiple configuration sources with precedence
• **Lazy Initialization**: Expensive resources created only when needed
• **Exception Safety**: Robust error handling during initialization

Note: Modern Singleton implementations focus on thread safety, immutability, and integration with dependency injection frameworks.

---

### Implementierung - Thread-Safe Singleton Solution

```java
// Enum Singleton - Recommended Approach
public enum ConfigurationManager {
    INSTANCE;
    
    private final Map<String, String> config;
    private final long lastRefresh;
    
    ConfigurationManager() {
        this.config = Collections.unmodifiableMap(loadConfiguration());
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
            // Reload configuration from sources
            Map<String, String> newConfig = loadConfiguration();
            config.clear();
            config.putAll(newConfig);
        }
    }
    
    private Map<String, String> loadConfiguration() {
        Map<String, String> props = new HashMap<>();
        
        // Load from multiple sources with precedence
        loadEnvironmentVariables(props);
        loadPropertyFiles(props);
        loadExternalConfigService(props);
        
        return props;
    }
}

// Initialization-on-Demand Pattern for Connection Pooling
public class ConnectionPoolManager {
    
    private ConnectionPoolManager() {}
    
    private static class Holder {
        private static final ConnectionPoolManager INSTANCE = new ConnectionPoolManager();
        
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
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        this.dataSource = new HikariDataSource(config);
    }
}
```

Note: Enum-based Singleton provides thread safety, serialization safety, and reflection safety out of the box.

---

## Block 1 Summary

### Design Pattern Benefits Applied

• **Factory Method**: Polymorphism over conditionals, extensible object creation
• **Abstract Factory**: Consistent service families, channel-agnostic business logic  
• **Builder**: Readable complex object construction, fluent interfaces
• **Prototype**: Performance optimization through intelligent cloning
• **Singleton**: Thread-safe resource sharing, centralized configuration

### SOLID Principles Integration

• **Single Responsibility**: Each pattern class has one clear purpose
• **Open/Closed**: Extensions through new implementations, not modifications
• **Liskov Substitution**: Implementations properly substitutable via interfaces
• **Interface Segregation**: Focused interfaces for specific concerns
• **Dependency Inversion**: Depend on abstractions, not concrete implementations

### Telekom Enterprise Benefits

• **Legacy Integration**: Patterns facilitate integration with existing systems
• **Scalability**: Template-based approaches support growing system complexity
• **Performance**: Optimizations like Prototype pattern improve response times
• **Maintainability**: Clear separation of concerns and extensible architecture
• **Testability**: Dependency injection and interface-based design enable testing

Note: These creational patterns form the foundation for building maintainable, scalable enterprise systems in the Telekom architecture.