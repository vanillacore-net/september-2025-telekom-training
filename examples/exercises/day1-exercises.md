# Day 1: Creational Patterns - Hands-On Exercises

## Learning Objectives
- Master identification of creation-related code smells
- Apply Factory Method, Abstract Factory, Builder, Prototype, and Singleton patterns
- Practice SOLID principles through pattern implementation
- Develop refactoring skills for legacy code improvement

---

## Exercise 1: Code Smell Detective 🔍
**Pattern Focus:** Factory Method | **Duration:** 15 minutes | **Type:** Analysis

### Scenario
The Telekom Service Activation system has grown organically and now contains several creation-related code smells. Your task is to identify these issues and propose pattern-based solutions.

### Given Code
```java
public class ServiceActivationManager {
    
    public void activateService(String customerId, String serviceType, Map<String, String> config) {
        if ("DSL".equals(serviceType)) {
            // 25 lines of DSL setup logic
            DSLService dsl = new DSLService();
            dsl.setCustomerId(customerId);
            dsl.setSpeed(config.get("speed"));
            dsl.setTechnology(config.get("technology"));
            validateDSLAvailability(config.get("address"));
            provisionDSLRouter(config.get("routerType"));
            scheduleDSLInstallation(customerId);
            updateBillingSystem(customerId, calculateDSLPrice(config));
            sendDSLWelcomeEmail(customerId);
            // ... more DSL specific code
            
        } else if ("MOBILE".equals(serviceType)) {
            // 20 lines of Mobile setup logic  
            MobileService mobile = new MobileService();
            mobile.setCustomerId(customerId);
            mobile.setDataPlan(config.get("dataPlan"));
            mobile.setPhoneNumber(generatePhoneNumber());
            provisionSIMCard(config.get("simType"));
            activateMobileNetwork(mobile);
            updateBillingSystem(customerId, calculateMobilePrice(config));
            sendMobileWelcomeEmail(customerId);
            // ... more Mobile specific code
            
        } else if ("FIBER".equals(serviceType)) {
            // 30 lines of Fiber setup logic
            FiberService fiber = new FiberService();
            fiber.setCustomerId(customerId);
            fiber.setBandwidth(config.get("bandwidth"));
            fiber.setServiceLevel(config.get("sla"));
            checkFiberAvailability(config.get("address"));
            scheduleFiberInstallation(customerId);
            provisionOpticalUnit(config.get("unitType"));
            updateBillingSystem(customerId, calculateFiberPrice(config));
            sendFiberWelcomeEmail(customerId);
            // ... more Fiber specific code
        } else {
            throw new IllegalArgumentException("Unsupported service: " + serviceType);
        }
    }
}
```

### Tasks

#### Part A: Code Smell Identification (5 minutes)
1. **Identify at least 5 code smells** in the given code
2. **Categorize each smell** (creation, behavior, structure)
3. **Assess SOLID violations** - which principles are broken?

**Expected Findings:**
- [ ] Long Method (each if-block is too long)
- [ ] Feature Envy (manipulating data from multiple objects)
- [ ] Switch Statement (type-based branching)
- [ ] Duplicate Code (similar patterns in each branch)
- [ ] Open/Closed Principle violation (new services require method changes)
- [ ] Single Responsibility Principle violation (handles all service types)

#### Part B: Pattern Selection (5 minutes)
**Question:** Which creational pattern(s) would best solve these issues?
- [ ] Factory Method - Why?
- [ ] Abstract Factory - Why?
- [ ] Builder - Why?
- [ ] Singleton - Why?
- [ ] Prototype - Why?

#### Part C: Solution Design (5 minutes)
Sketch a class diagram showing how you would restructure this code using your chosen pattern(s).

### Discussion Points
- How does pattern choice affect testability?
- What are the trade-offs between different creational patterns?
- How would you handle adding a new service type (5G) with minimal code changes?

### Reference Solution
<details>
<summary>Click to reveal reference solution</summary>

**Pattern Choice: Factory Method + Template Method**

```java
public abstract class ServiceActivationFactory {
    // Template Method that uses Factory Method
    public final void activateService(String customerId, Map<String, String> config) {
        TelecomService service = createService(customerId, config);
        
        // Common activation steps
        validateServiceAvailability(service, config);
        provisionService(service);
        updateBillingSystem(customerId, service.getPrice());
        sendWelcomeEmail(customerId, service);
    }
    
    // Factory Method - subclasses decide which service to create
    protected abstract TelecomService createService(String customerId, Map<String, String> config);
    
    // Common methods used by template
    private void validateServiceAvailability(TelecomService service, Map<String, String> config) { /* */ }
    private void provisionService(TelecomService service) { /* */ }
    private void updateBillingSystem(String customerId, double price) { /* */ }
    private void sendWelcomeEmail(String customerId, TelecomService service) { /* */ }
}

public class DSLActivationFactory extends ServiceActivationFactory {
    @Override
    protected TelecomService createService(String customerId, Map<String, String> config) {
        DSLService dsl = new DSLService(customerId);
        dsl.setSpeed(config.get("speed"));
        dsl.setTechnology(config.get("technology"));
        // DSL-specific configuration
        return dsl;
    }
}
```

**Benefits:**
- Single Responsibility: Each factory handles one service type
- Open/Closed: New services added without modifying existing code
- Template Method eliminates duplicate activation workflow
- Factory Method delegates object creation to specialized factories
</details>

---

## Exercise 2: Contract Builder Challenge 🛠️
**Pattern Focus:** Builder | **Duration:** 25 minutes | **Type:** Implementation

### Scenario
Telekom contracts have become increasingly complex with optional services, various billing cycles, promotional offers, and regulatory requirements. The current constructor-based approach is becoming unwieldy.

### Current Problem
```java
// This is becoming impossible to use and maintain
TelekomContract contract = new TelekomContract(
    customerId,           // required
    contractType,         // required
    null,                // optional: promotionalDiscount
    null,                // optional: loyaltyDiscount  
    true,                // optional: autoRenewal
    "MONTHLY",           // optional: billingCycle
    null,                // optional: minimumTermMonths
    null,                // optional: cancellationNoticeMonths
    false,               // optional: includeInternationalCalls
    true,                // optional: includeVoicemail
    null,                // optional: dataRollover
    null,                // optional: familyPlanMembers
    null,                // optional: businessAccountManager
    null,                // optional: corporateDiscount
    null,                // optional: specialNeeds
    true,                // optional: paperlessBilling
    null                 // optional: billingAddress
);
```

### Tasks

#### Part A: Builder Pattern Design (10 minutes)
Design a Builder pattern that makes contract creation readable and flexible.

**Requirements:**
- Support fluent interface
- Validate required fields before building
- Provide sensible defaults for optional fields
- Support both personal and business contract variations
- Include validation for business rules (e.g., family plan max 5 members)

**Skeleton to Complete:**
```java
public class TelekomContract {
    // Contract fields...
    
    public static class Builder {
        // TODO: Implement builder fields and methods
    }
}
```

#### Part B: Implementation (10 minutes)
Implement your Builder design with the following test scenarios:

**Test Scenario 1: Basic Personal Contract**
```java
TelekomContract contract = TelekomContract.builder()
    .customerId("CUST-12345")
    .contractType("MOBILE_PERSONAL")
    .billingCycle("MONTHLY")
    .includeVoicemail(true)
    .build();
```

**Test Scenario 2: Complex Business Contract**
```java
TelekomContract businessContract = TelekomContract.builder()
    .customerId("BUSINESS-789")
    .contractType("FIBER_BUSINESS")
    .businessAccountManager("John Smith")
    .corporateDiscount(0.15)
    .minimumTermMonths(24)
    .billingCycle("QUARTERLY")
    .includeInternationalCalls(true)
    .build();
```

#### Part C: Validation Implementation (5 minutes)
Add validation logic that throws meaningful exceptions:
- Required fields must be present
- Business contracts require account manager
- Family plan members cannot exceed 5
- Discount values must be between 0.0 and 1.0

### Extension Activities
- **For Fast Learners:** Implement a Director class that creates common contract templates
- **Advanced Challenge:** Add support for contract modifications (change billing cycle, add services)

### Discussion Points
- When is Builder pattern overkill vs. beneficial?
- How does Builder pattern improve testing?
- What are alternatives to Builder for complex object creation?

### Reference Solution
<details>
<summary>Click to reveal reference solution</summary>

```java
public class TelekomContract {
    private final String customerId;
    private final String contractType;
    private final double promotionalDiscount;
    private final double loyaltyDiscount;
    private final boolean autoRenewal;
    private final String billingCycle;
    private final Integer minimumTermMonths;
    private final Integer cancellationNoticeMonths;
    private final boolean includeInternationalCalls;
    private final boolean includeVoicemail;
    private final boolean dataRollover;
    private final List<String> familyPlanMembers;
    private final String businessAccountManager;
    private final double corporateDiscount;
    private final String specialNeeds;
    private final boolean paperlessBilling;
    private final String billingAddress;
    
    private TelekomContract(Builder builder) {
        this.customerId = builder.customerId;
        this.contractType = builder.contractType;
        this.promotionalDiscount = builder.promotionalDiscount;
        this.loyaltyDiscount = builder.loyaltyDiscount;
        this.autoRenewal = builder.autoRenewal;
        this.billingCycle = builder.billingCycle;
        this.minimumTermMonths = builder.minimumTermMonths;
        this.cancellationNoticeMonths = builder.cancellationNoticeMonths;
        this.includeInternationalCalls = builder.includeInternationalCalls;
        this.includeVoicemail = builder.includeVoicemail;
        this.dataRollover = builder.dataRollover;
        this.familyPlanMembers = builder.familyPlanMembers != null 
            ? new ArrayList<>(builder.familyPlanMembers) : new ArrayList<>();
        this.businessAccountManager = builder.businessAccountManager;
        this.corporateDiscount = builder.corporateDiscount;
        this.specialNeeds = builder.specialNeeds;
        this.paperlessBilling = builder.paperlessBilling;
        this.billingAddress = builder.billingAddress;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String customerId;
        private String contractType;
        private double promotionalDiscount = 0.0;
        private double loyaltyDiscount = 0.0;
        private boolean autoRenewal = true;
        private String billingCycle = "MONTHLY";
        private Integer minimumTermMonths;
        private Integer cancellationNoticeMonths = 1;
        private boolean includeInternationalCalls = false;
        private boolean includeVoicemail = false;
        private boolean dataRollover = false;
        private List<String> familyPlanMembers;
        private String businessAccountManager;
        private double corporateDiscount = 0.0;
        private String specialNeeds;
        private boolean paperlessBilling = true;
        private String billingAddress;
        
        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }
        
        public Builder contractType(String contractType) {
            this.contractType = contractType;
            return this;
        }
        
        public Builder promotionalDiscount(double discount) {
            validateDiscount(discount);
            this.promotionalDiscount = discount;
            return this;
        }
        
        public Builder businessAccountManager(String accountManager) {
            this.businessAccountManager = accountManager;
            return this;
        }
        
        public Builder corporateDiscount(double discount) {
            validateDiscount(discount);
            this.corporateDiscount = discount;
            return this;
        }
        
        public Builder familyPlanMembers(List<String> members) {
            if (members != null && members.size() > 5) {
                throw new IllegalArgumentException("Family plan cannot exceed 5 members");
            }
            this.familyPlanMembers = members;
            return this;
        }
        
        public Builder billingCycle(String cycle) {
            this.billingCycle = cycle;
            return this;
        }
        
        public Builder minimumTermMonths(int months) {
            this.minimumTermMonths = months;
            return this;
        }
        
        public Builder includeVoicemail(boolean include) {
            this.includeVoicemail = include;
            return this;
        }
        
        public Builder includeInternationalCalls(boolean include) {
            this.includeInternationalCalls = include;
            return this;
        }
        
        public TelekomContract build() {
            validateRequired();
            validateBusinessRules();
            return new TelekomContract(this);
        }
        
        private void validateRequired() {
            if (customerId == null || customerId.isEmpty()) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (contractType == null || contractType.isEmpty()) {
                throw new IllegalArgumentException("Contract type is required");
            }
        }
        
        private void validateBusinessRules() {
            if (contractType.contains("BUSINESS") && businessAccountManager == null) {
                throw new IllegalArgumentException("Business contracts require an account manager");
            }
        }
        
        private void validateDiscount(double discount) {
            if (discount < 0.0 || discount > 1.0) {
                throw new IllegalArgumentException("Discount must be between 0.0 and 1.0");
            }
        }
    }
    
    // Getters...
}
```
</details>

---

## Exercise 3: Legacy System Integration Puzzle 🔄
**Pattern Focus:** Adapter | **Duration:** 20 minutes | **Type:** Refactoring

### Scenario
Telekom needs to integrate with multiple legacy billing systems that have incompatible interfaces. Currently, the integration code is scattered throughout the application, making it brittle and hard to test.

### Current Problem Code
```java
public class BillingIntegrationService {
    
    public void processCustomerBilling(String customerId, double amount, String currency) {
        String legacySystem = determineLegacySystem(customerId);
        
        if ("SAP_R3".equals(legacySystem)) {
            // Direct SAP integration - tightly coupled
            SapBillingClient sapClient = new SapBillingClient();
            sapClient.connect("sap-server:3300", "BILLING_USER", "secret123");
            
            // SAP expects amounts in cents, different date format
            int amountInCents = (int) (amount * 100);
            String sapDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            SapBillingRecord sapRecord = new SapBillingRecord();
            sapRecord.setKundenNummer(customerId);
            sapRecord.setBetragInCent(amountInCents);
            sapRecord.setWaehrung(convertCurrency(currency));
            sapRecord.setBuchungsdatum(sapDate);
            
            sapClient.buche(sapRecord);
            sapClient.disconnect();
            
        } else if ("ORACLE_FINANCIALS".equals(legacySystem)) {
            // Direct Oracle integration - tightly coupled
            OracleFinancialsClient oracleClient = new OracleFinancialsClient();
            oracleClient.initialize("oracle-db:1521:PROD", "FINANCE", "oraclepwd");
            
            // Oracle expects XML format
            String xmlBilling = String.format(
                "<billing><customer_id>%s</customer_id><amount>%.2f</amount><currency>%s</currency><timestamp>%s</timestamp></billing>",
                customerId, amount, currency, Instant.now().toString()
            );
            
            OracleFinancialsResult result = oracleClient.submitXmlBilling(xmlBilling);
            if (!result.isSuccess()) {
                throw new BillingException("Oracle billing failed: " + result.getErrorMessage());
            }
            
        } else if ("LEGACY_COBOL".equals(legacySystem)) {
            // Direct COBOL mainframe integration - tightly coupled  
            CobolBillingClient cobolClient = new CobolBillingClient();
            cobolClient.connectMainframe("mainframe.internal", 23);
            
            // COBOL expects fixed-width records
            String cobolRecord = String.format("%-10s%08d%-3s%-14s",
                customerId, 
                (int)(amount * 100),  // cents
                currency,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            );
            
            cobolClient.sendRecord("BILLING", cobolRecord);
            cobolClient.disconnect();
        }
    }
    
    private String convertCurrency(String currency) {
        // Currency conversion logic scattered here too
        switch(currency) {
            case "EUR": return "EUR";
            case "USD": return "USD";
            case "GBP": return "GBP";
            default: return "EUR";
        }
    }
}
```

### Tasks

#### Part A: Problem Analysis (5 minutes)
Identify the integration issues:
- [ ] Tight coupling to legacy systems
- [ ] Mixed responsibilities (connection, data conversion, business logic)
- [ ] Hard to test (requires actual legacy systems)
- [ ] Code duplication across integrations
- [ ] Error handling inconsistencies

#### Part B: Adapter Pattern Design (8 minutes)
Design an Adapter pattern solution that:
1. **Defines a common interface** for all billing systems
2. **Isolates legacy system complexities** in dedicated adapters
3. **Provides consistent error handling** across all systems
4. **Enables easy testing** through mocking

**Create these interfaces/classes:**
```java
// Common interface for billing operations
public interface BillingService {
    // TODO: Define common billing operations
}

// Adapter for SAP R3 system  
public class SapBillingAdapter implements BillingService {
    // TODO: Wrap SAP-specific logic
}

// Adapter for Oracle Financials
public class OracleFinancialsAdapter implements BillingService {
    // TODO: Wrap Oracle-specific logic  
}

// Adapter for Legacy COBOL
public class CobolBillingAdapter implements BillingService {
    // TODO: Wrap COBOL-specific logic
}
```

#### Part C: Implementation (7 minutes)
Implement one complete adapter of your choice, focusing on:
- Clean separation of concerns
- Proper error handling and conversion
- Data transformation between domain model and legacy format

### Test Your Solution
Your refactored code should support this clean usage:
```java
public class BillingIntegrationService {
    private Map<String, BillingService> billingAdapters;
    
    public void processCustomerBilling(String customerId, BillingRequest request) {
        String legacySystem = determineLegacySystem(customerId);
        BillingService adapter = billingAdapters.get(legacySystem);
        
        if (adapter == null) {
            throw new UnsupportedBillingSystemException("No adapter for: " + legacySystem);
        }
        
        adapter.processBilling(request);
    }
}
```

### Discussion Points
- How does the Adapter pattern improve testability?
- What are the trade-offs between object adapter vs class adapter?
- How would you handle configuration and connection management?
- When might you choose Facade pattern instead of Adapter?

### Reference Solution
<details>
<summary>Click to reveal reference solution</summary>

```java
// Common domain model
public class BillingRequest {
    private final String customerId;
    private final double amount;
    private final String currency;
    private final LocalDateTime timestamp;
    
    // Constructor, getters, builder...
}

public class BillingResult {
    private final boolean success;
    private final String transactionId;
    private final String errorMessage;
    
    // Constructor, getters...
}

// Common interface
public interface BillingService {
    BillingResult processBilling(BillingRequest request) throws BillingException;
    boolean isHealthy();
}

// SAP Adapter Example
public class SapBillingAdapter implements BillingService {
    private final SapBillingClient sapClient;
    private final CurrencyConverter currencyConverter;
    
    public SapBillingAdapter(SapBillingClient sapClient, CurrencyConverter currencyConverter) {
        this.sapClient = sapClient;
        this.currencyConverter = currencyConverter;
    }
    
    @Override
    public BillingResult processBilling(BillingRequest request) throws BillingException {
        try {
            sapClient.connect("sap-server:3300", "BILLING_USER", "secret123");
            
            // Transform domain object to SAP format
            SapBillingRecord sapRecord = transformToSapFormat(request);
            
            sapClient.buche(sapRecord);
            
            return BillingResult.success("SAP-" + System.currentTimeMillis());
            
        } catch (SapException e) {
            throw new BillingException("SAP billing failed", e);
        } finally {
            sapClient.disconnect();
        }
    }
    
    private SapBillingRecord transformToSapFormat(BillingRequest request) {
        SapBillingRecord record = new SapBillingRecord();
        record.setKundenNummer(request.getCustomerId());
        record.setBetragInCent((int) (request.getAmount() * 100));
        record.setWaehrung(currencyConverter.convertToSapFormat(request.getCurrency()));
        record.setBuchungsdatum(request.getTimestamp()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        return record;
    }
    
    @Override
    public boolean isHealthy() {
        try {
            sapClient.connect("sap-server:3300", "BILLING_USER", "secret123");
            boolean healthy = sapClient.ping();
            sapClient.disconnect();
            return healthy;
        } catch (Exception e) {
            return false;
        }
    }
}

// Usage in main service
public class BillingIntegrationService {
    private final Map<String, BillingService> billingAdapters;
    private final SystemLookupService systemLookup;
    
    public BillingIntegrationService(Map<String, BillingService> adapters, SystemLookupService lookup) {
        this.billingAdapters = adapters;
        this.systemLookup = lookup;
    }
    
    public BillingResult processCustomerBilling(String customerId, double amount, String currency) {
        String legacySystem = systemLookup.determineLegacySystem(customerId);
        BillingService adapter = billingAdapters.get(legacySystem);
        
        if (adapter == null) {
            throw new UnsupportedBillingSystemException("No adapter for: " + legacySystem);
        }
        
        if (!adapter.isHealthy()) {
            throw new BillingSystemUnavailableException("Billing system unavailable: " + legacySystem);
        }
        
        BillingRequest request = BillingRequest.builder()
            .customerId(customerId)
            .amount(amount)
            .currency(currency)
            .timestamp(LocalDateTime.now())
            .build();
            
        return adapter.processBilling(request);
    }
}
```

**Benefits Achieved:**
- Single Responsibility: Each adapter handles one legacy system
- Open/Closed: New systems added by creating new adapters
- Testability: Adapters can be mocked for unit tests
- Consistency: Common interface and error handling
- Maintainability: Legacy system changes isolated to specific adapters
</details>

---

## Exercise 4: Configuration Management Chaos 🏗️ 
**Pattern Focus:** Singleton + Prototype | **Duration:** 25 minutes | **Type:** Architecture

### Scenario
The Telekom network management system needs a robust configuration management solution. Currently, configuration is scattered, inconsistent, and difficult to clone for testing environments.

### Current Problems
1. **Configuration Scattered Everywhere**
2. **No Central Configuration Access**  
3. **Difficult to Clone Configurations for Testing**
4. **Thread-Safety Issues in Multi-threaded Environment**

### Part A: Singleton Configuration Manager (10 minutes)

Design and implement a thread-safe Singleton that manages system-wide configuration.

**Requirements:**
- Thread-safe lazy initialization
- Support for different configuration sources (properties, environment, database)
- Provide typed access to configuration values
- Support configuration change notifications

**Skeleton:**
```java
public class ConfigurationManager {
    // TODO: Implement thread-safe Singleton
    // TODO: Add configuration loading mechanism
    // TODO: Add typed getters (getString, getInt, getBoolean, etc.)
    // TODO: Add configuration change notification
}
```

**Test Scenario:**
```java
// Should work from multiple threads
ConfigurationManager config = ConfigurationManager.getInstance();
String dbUrl = config.getString("database.url");
int maxConnections = config.getInt("database.maxConnections", 10);
boolean debugMode = config.getBoolean("system.debug", false);
```

### Part B: Prototype Pattern for Configuration Cloning (10 minutes)

Implement a Prototype pattern that allows deep cloning of complex network configurations for testing and development environments.

**Given Complex Configuration:**
```java
public class NetworkConfiguration {
    private String networkName;
    private List<RouterConfig> routers;
    private List<SwitchConfig> switches;  
    private SecurityPolicy securityPolicy;
    private QualityOfServiceConfig qosConfig;
    private MonitoringConfig monitoringConfig;
    
    // This configuration is complex and expensive to recreate
    // Need to support deep cloning for test environments
}

public class RouterConfig {
    private String routerId;
    private String ipAddress;
    private List<InterfaceConfig> interfaces;
    private RoutingTable routingTable;
    // ... complex nested structure
}
```

**Requirements:**
- Implement deep cloning for all configuration objects
- Support configuration variations (production -> test -> development)
- Ensure cloned configurations are independent (changes don't affect original)
- Support selective cloning (clone only specific components)

### Part C: Integration Challenge (5 minutes)

Combine both patterns to create a system where:
1. **Singleton manages global configuration templates**
2. **Prototype creates environment-specific copies**
3. **Configuration changes are centrally managed**

**Target Usage:**
```java
ConfigurationManager manager = ConfigurationManager.getInstance();

// Get production template
NetworkConfiguration prodTemplate = manager.getNetworkConfigTemplate("PRODUCTION");

// Clone for test environment with modifications
NetworkConfiguration testConfig = prodTemplate.clone();
testConfig.setNetworkName("TEST-NETWORK");
testConfig.getSecurityPolicy().setSecurityLevel("RELAXED");

// Clone for development with further modifications  
NetworkConfiguration devConfig = testConfig.clone();
devConfig.getMonitoringConfig().setLoggingLevel("DEBUG");
```

### Discussion Points
- When is Singleton appropriate vs. problematic?
- How does Prototype pattern impact memory usage?
- What are alternatives to Singleton for global state management?
- How would you handle configuration versioning and rollback?

### Extension Activities
- **Advanced:** Implement configuration change listeners using Observer pattern
- **Challenge:** Add configuration validation and schema enforcement
- **Architecture:** Design configuration inheritance hierarchies

### Reference Solution
<details>
<summary>Click to reveal reference solution</summary>

```java
// Thread-safe Singleton with lazy initialization
public class ConfigurationManager {
    private static volatile ConfigurationManager instance;
    private final Map<String, Object> configurations = new ConcurrentHashMap<>();
    private final List<ConfigurationChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    private ConfigurationManager() {
        loadDefaultConfigurations();
    }
    
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }
    
    public String getString(String key) {
        return getString(key, null);
    }
    
    public String getString(String key, String defaultValue) {
        return (String) configurations.getOrDefault(key, defaultValue);
    }
    
    public int getInt(String key, int defaultValue) {
        Object value = configurations.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = configurations.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
    
    public NetworkConfiguration getNetworkConfigTemplate(String environment) {
        return (NetworkConfiguration) configurations.get("network.template." + environment);
    }
    
    public void setConfiguration(String key, Object value) {
        Object oldValue = configurations.put(key, value);
        notifyListeners(key, oldValue, value);
    }
    
    public void addConfigurationChangeListener(ConfigurationChangeListener listener) {
        listeners.add(listener);
    }
    
    private void notifyListeners(String key, Object oldValue, Object newValue) {
        for (ConfigurationChangeListener listener : listeners) {
            try {
                listener.configurationChanged(key, oldValue, newValue);
            } catch (Exception e) {
                // Log error but continue notifying other listeners
                System.err.println("Error notifying configuration listener: " + e.getMessage());
            }
        }
    }
    
    private void loadDefaultConfigurations() {
        // Load from multiple sources
        loadFromProperties();
        loadFromEnvironment();
        loadNetworkTemplates();
    }
    
    private void loadNetworkTemplates() {
        // Create default production template
        NetworkConfiguration prodTemplate = createProductionTemplate();
        configurations.put("network.template.PRODUCTION", prodTemplate);
    }
    
    private NetworkConfiguration createProductionTemplate() {
        return NetworkConfiguration.builder()
            .networkName("PROD-NETWORK")
            .addRouter(RouterConfig.builder()
                .routerId("PROD-RTR-001")
                .ipAddress("10.0.1.1")
                .build())
            .securityPolicy(SecurityPolicy.builder()
                .securityLevel("HIGH")
                .encryptionEnabled(true)
                .build())
            .build();
    }
}

// Prototype pattern implementation
public class NetworkConfiguration implements Cloneable {
    private String networkName;
    private List<RouterConfig> routers;
    private List<SwitchConfig> switches;
    private SecurityPolicy securityPolicy;
    private QualityOfServiceConfig qosConfig;
    private MonitoringConfig monitoringConfig;
    
    private NetworkConfiguration(Builder builder) {
        this.networkName = builder.networkName;
        this.routers = new ArrayList<>(builder.routers);
        this.switches = new ArrayList<>(builder.switches);
        this.securityPolicy = builder.securityPolicy;
        this.qosConfig = builder.qosConfig;
        this.monitoringConfig = builder.monitoringConfig;
    }
    
    @Override
    public NetworkConfiguration clone() {
        try {
            NetworkConfiguration cloned = (NetworkConfiguration) super.clone();
            
            // Deep clone complex objects
            cloned.routers = routers.stream()
                .map(RouterConfig::clone)
                .collect(Collectors.toList());
            
            cloned.switches = switches.stream()
                .map(SwitchConfig::clone)
                .collect(Collectors.toList());
                
            cloned.securityPolicy = securityPolicy.clone();
            cloned.qosConfig = qosConfig.clone();
            cloned.monitoringConfig = monitoringConfig.clone();
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String networkName;
        private List<RouterConfig> routers = new ArrayList<>();
        private List<SwitchConfig> switches = new ArrayList<>();
        private SecurityPolicy securityPolicy;
        private QualityOfServiceConfig qosConfig;
        private MonitoringConfig monitoringConfig;
        
        public Builder networkName(String name) {
            this.networkName = name;
            return this;
        }
        
        public Builder addRouter(RouterConfig router) {
            this.routers.add(router);
            return this;
        }
        
        public Builder securityPolicy(SecurityPolicy policy) {
            this.securityPolicy = policy;
            return this;
        }
        
        public NetworkConfiguration build() {
            return new NetworkConfiguration(this);
        }
    }
    
    // Getters and setters...
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }
    
    public SecurityPolicy getSecurityPolicy() {
        return securityPolicy;
    }
    
    public MonitoringConfig getMonitoringConfig() {
        return monitoringConfig;
    }
}

// Supporting classes that also implement Cloneable
public class RouterConfig implements Cloneable {
    private String routerId;
    private String ipAddress;
    private List<InterfaceConfig> interfaces;
    private RoutingTable routingTable;
    
    @Override
    public RouterConfig clone() {
        try {
            RouterConfig cloned = (RouterConfig) super.clone();
            cloned.interfaces = interfaces.stream()
                .map(InterfaceConfig::clone)
                .collect(Collectors.toList());
            cloned.routingTable = routingTable.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Builder and other methods...
}

// Configuration change listener interface
public interface ConfigurationChangeListener {
    void configurationChanged(String key, Object oldValue, Object newValue);
}
```

**Benefits Achieved:**
- Centralized configuration management through Singleton
- Thread-safe access to configuration values
- Deep cloning capabilities for complex configurations
- Environment-specific configuration creation
- Change notification system for configuration updates
</details>

---

## Summary and Reflection

### Key Takeaways from Day 1 Exercises

1. **Pattern Recognition Skills**
   - Identifying code smells that indicate need for creational patterns
   - Understanding when patterns solve real problems vs. over-engineering

2. **SOLID Principles Application**
   - How creational patterns naturally support SOLID principles
   - Balancing flexibility with complexity

3. **Real-World Implementation**
   - Telekom-specific scenarios demonstrate practical pattern application
   - Integration challenges mirror actual enterprise development

### Preparation for Day 2
Tomorrow's focus on Structural Patterns will build upon these foundations:
- **Composition over Inheritance** concepts from today's exercises
- **Interface design** skills developed through adapters
- **System integration** thinking from legacy adapter exercise

### Action Items
- [ ] Review your solutions with reference implementations
- [ ] Identify which patterns felt most/least natural to implement
- [ ] Consider how these patterns apply to your current projects
- [ ] Prepare questions about complex scenarios for tomorrow's discussion

### Extended Learning
- Explore advanced creational pattern variations (e.g., Registry of Singletons)
- Research pattern combinations (Builder + Factory Method)
- Study dependency injection as modern alternative to some creational patterns

---

**Instructor Notes:**
- Encourage pair programming for complex exercises
- Use timer to maintain pace but be flexible for good discussions
- Emphasize that patterns are tools, not goals in themselves
- Connect each exercise back to SOLID principles and clean code practices