# Day 2: Structural Patterns - Hands-On Exercises

## Learning Objectives
- Master system composition and integration techniques
- Apply Decorator, Composite, Proxy, Facade, Bridge, Flyweight, and Adapter patterns
- Design flexible architectures that support changing requirements
- Practice separating interface from implementation

---

## Exercise 1: Service Enhancement Pipeline 🔧
**Pattern Focus:** Decorator | **Duration:** 20 minutes | **Type:** Implementation

### Scenario
Telekom's service activation system needs to support various enhancements that can be applied to base services (logging, caching, monitoring, security, SLA management). The challenge is that different customers need different combinations of these enhancements.

### Current Problem
```java
public class EnhancedDSLService {
    private DSLService baseService;
    private boolean loggingEnabled;
    private boolean cachingEnabled;  
    private boolean monitoringEnabled;
    private boolean securityEnabled;
    private boolean slaEnabled;
    
    public void activate(String customerId) {
        if (securityEnabled) {
            // Security check logic
            validateCustomerSecurity(customerId);
        }
        
        if (loggingEnabled) {
            // Logging logic
            log("Activating DSL for customer: " + customerId);
        }
        
        String result;
        if (cachingEnabled) {
            result = getFromCache(customerId);
            if (result == null) {
                result = baseService.activate(customerId);
                putInCache(customerId, result);
            }
        } else {
            result = baseService.activate(customerId);
        }
        
        if (monitoringEnabled) {
            // Monitoring logic
            recordMetrics("dsl.activation", customerId);
        }
        
        if (slaEnabled) {
            // SLA tracking logic
            trackSLA(customerId, "DSL_ACTIVATION", result);
        }
        
        if (loggingEnabled) {
            log("DSL activation completed for customer: " + customerId);
        }
    }
}
```

### Problems Identified
- Combinatorial explosion of if-statements
- Difficult to add new enhancements
- Hard to configure different enhancement combinations
- Violates Single Responsibility Principle
- Code duplication across different service types

### Tasks

#### Part A: Decorator Pattern Design (8 minutes)
Design a Decorator pattern that allows dynamic composition of service enhancements.

**Required Components:**
```java
// Base component interface
public interface TelecomService {
    ServiceResult activate(String customerId);
    ServiceInfo getServiceInfo();
}

// Base implementation
public class DSLService implements TelecomService {
    // TODO: Implement base DSL functionality
}

// Abstract decorator
public abstract class ServiceDecorator implements TelecomService {
    // TODO: Implement delegation pattern
}

// Concrete decorators
public class LoggingDecorator extends ServiceDecorator {
    // TODO: Add logging behavior
}

public class CachingDecorator extends ServiceDecorator {
    // TODO: Add caching behavior
}

public class SecurityDecorator extends ServiceDecorator {
    // TODO: Add security validation
}
```

#### Part B: Implementation (10 minutes)
Implement at least two concrete decorators of your choice. Focus on:
- Proper delegation to wrapped component
- Adding behavior before and/or after delegation
- Maintaining the component interface contract

#### Part C: Usage Demonstration (2 minutes)
Show how your solution supports flexible composition:

```java
// Example: Create enhanced service with logging, security, and monitoring
TelecomService enhancedService = new MonitoringDecorator(
    new SecurityDecorator(
        new LoggingDecorator(
            new DSLService()
        )
    )
);

// Different enhancement combination
TelecomService cachedService = new CachingDecorator(
    new LoggingDecorator(
        new DSLService()
    )
);
```

### Discussion Points
- How does Decorator pattern compare to inheritance for adding behavior?
- What are the trade-offs in terms of complexity vs. flexibility?
- How would you handle decorator ordering dependencies?
- When might you choose Strategy pattern instead of Decorator?

---

## Exercise 2: Network Infrastructure Hierarchy 🏗️
**Pattern Focus:** Composite + Facade | **Duration:** 25 minutes | **Type:** Architecture

### Scenario
Telekom manages complex network infrastructures where individual components (routers, switches, servers) can be grouped into logical units (data centers, network segments, availability zones). Operations need to be performed at any level of the hierarchy.

### Part A: Composite Pattern for Network Hierarchy (15 minutes)

Design and implement a Composite pattern for network infrastructure management.

**Requirements:**
- Support individual network components (leaf nodes)
- Support composite components that contain other components  
- Enable uniform operations across all hierarchy levels
- Provide traversal and search capabilities

**Components to Model:**
- **NetworkComponent** (interface)
- **Router, Switch, Server** (leaf components)
- **DataCenter, NetworkSegment, AvailabilityZone** (composite components)

**Operations to Support:**
- Start/Stop all components in a hierarchy
- Get status of all components
- Calculate total power consumption
- Find components by type or name
- Generate configuration reports

**Skeleton:**
```java
public interface NetworkComponent {
    void start();
    void stop(); 
    ComponentStatus getStatus();
    double getPowerConsumption();
    List<NetworkComponent> findByType(String type);
    void generateConfigReport(StringBuilder report, int indentLevel);
}

public class Router implements NetworkComponent {
    // TODO: Implement leaf component
}

public class DataCenter implements NetworkComponent {
    private List<NetworkComponent> components = new ArrayList<>();
    
    public void addComponent(NetworkComponent component) {
        components.add(component);
    }
    
    public void removeComponent(NetworkComponent component) {
        components.remove(component);
    }
    
    // TODO: Implement composite operations
}
```

#### Part B: Facade Pattern for Simplified Management (10 minutes)

The Composite structure is powerful but complex for everyday operations. Create a Facade that provides simplified interfaces for common management tasks.

**Requirements:**
- Provide simplified methods for common operations
- Hide the complexity of traversing the composite structure
- Support batch operations across multiple data centers
- Provide summary reporting capabilities

```java
public class NetworkManagementFacade {
    private List<DataCenter> dataCenters;
    
    // TODO: Implement simplified interface methods
    public void startAllDataCenters()
    public void stopDataCenter(String dataCenterName)  
    public NetworkHealthReport getHealthSummary()
    public double getTotalPowerConsumption()
    public List<ComponentAlert> getActiveAlerts()
    public void scheduleMaintenanceWindow(String dataCenterName, LocalDateTime start, Duration duration)
}
```

### Test Scenarios
Your implementation should support these usage patterns:

```java
// Building the hierarchy
DataCenter primaryDC = new DataCenter("PRIMARY-DC");
NetworkSegment coreNetwork = new NetworkSegment("CORE-NETWORK");
coreNetwork.addComponent(new Router("CORE-RTR-01"));
coreNetwork.addComponent(new Router("CORE-RTR-02"));
coreNetwork.addComponent(new Switch("CORE-SW-01"));

DataCenter backupDC = new DataCenter("BACKUP-DC");
// ... build backup data center

// Operations on composite
primaryDC.start(); // Starts all components in primary DC
double totalPower = primaryDC.getPowerConsumption(); // Sum of all components

// Facade usage
NetworkManagementFacade facade = new NetworkManagementFacade();
facade.addDataCenter(primaryDC);
facade.addDataCenter(backupDC);

NetworkHealthReport report = facade.getHealthSummary();
facade.scheduleMaintenanceWindow("PRIMARY-DC", tomorrow, Duration.ofHours(4));
```

---

## Exercise 3: Legacy System Access Control 🔐
**Pattern Focus:** Proxy | **Duration:** 20 minutes | **Type:** Security & Performance

### Scenario
Telekom needs to control access to expensive legacy billing systems. Requirements include authentication, authorization, caching, logging, and connection pooling. Direct access to legacy systems should be restricted.

### Current Problem
```java
// Direct access to expensive legacy system
public class DirectBillingAccess {
    public void generateBill(String customerId) {
        // Expensive operation - connects directly to mainframe
        LegacyBillingSystem legacy = new LegacyBillingSystem();
        legacy.connect("mainframe:3270");
        BillingResult result = legacy.generateMonthlyBill(customerId);
        legacy.disconnect();
    }
    
    public CustomerData getCustomerData(String customerId) {
        // Another expensive operation
        LegacyBillingSystem legacy = new LegacyBillingSystem();
        legacy.connect("mainframe:3270");
        CustomerData data = legacy.getCustomerRecord(customerId);
        legacy.disconnect();
    }
}
```

### Tasks

#### Part A: Protection Proxy Design (8 minutes)
Design a Proxy that adds security and access control.

**Requirements:**
- Authenticate users before allowing access
- Authorize operations based on user roles
- Log all access attempts (successful and failed)
- Provide audit trail for compliance

```java
public interface BillingSystem {
    BillingResult generateBill(String customerId, UserContext user);
    CustomerData getCustomerData(String customerId, UserContext user);
}

public class LegacyBillingSystemProxy implements BillingSystem {
    private LegacyBillingSystem realSubject;
    private AuthenticationService authService;
    private AuthorizationService authzService;
    private AuditLogger auditLogger;
    
    // TODO: Implement protection proxy logic
}
```

#### Part B: Caching Proxy Implementation (8 minutes)
Implement a caching proxy to improve performance for frequently accessed data.

**Requirements:**
- Cache customer data for configurable time periods
- Implement cache invalidation strategies
- Handle cache misses gracefully
- Provide cache statistics for monitoring

```java
public class CachingBillingProxy implements BillingSystem {
    private BillingSystem delegate;  // Could be security proxy
    private Cache<String, CustomerData> customerCache;
    private CacheStatistics stats;
    
    // TODO: Implement caching proxy logic
}
```

#### Part C: Proxy Chain Integration (4 minutes)
Show how multiple proxies can be chained together:

```java
// Build proxy chain: Security -> Caching -> Legacy System
BillingSystem billingSystem = new SecurityBillingProxy(
    new CachingBillingProxy(
        new LegacyBillingSystemAdapter()
    )
);

// Usage
UserContext user = getCurrentUser();
CustomerData data = billingSystem.getCustomerData("CUST-12345", user);
```

### Test Your Implementation
```java
// Test security enforcement
try {
    billingSystem.generateBill("CUST-001", unauthorizedUser);
    fail("Should have thrown SecurityException");
} catch (SecurityException expected) {
    // Expected behavior
}

// Test caching behavior
CustomerData data1 = billingSystem.getCustomerData("CUST-002", authorizedUser);
CustomerData data2 = billingSystem.getCustomerData("CUST-002", authorizedUser); // Should hit cache

assertEquals(data1, data2);
assertTrue(cachingProxy.getCacheHitRate() > 0);
```

---

## Exercise 4: Multi-Platform Service Bridge 🌉
**Pattern Focus:** Bridge + Flyweight | **Duration:** 25 minutes | **Type:** Advanced Architecture

### Scenario
Telekom operates across multiple countries with different technical platforms, regulatory requirements, and service offerings. The system needs to support the same service abstractions across different implementation platforms.

### Part A: Bridge Pattern for Platform Abstraction (15 minutes)

**Challenge:** Design a Bridge pattern that separates service abstractions from platform-specific implementations.

**Service Types:**
- **MobileService, DSLService, FiberService** (Abstraction hierarchy)

**Platform Implementations:**  
- **GermanPlatform, AustrianPlatform, PolishPlatform** (Implementation hierarchy)

Each platform has different:
- Regulatory compliance requirements
- Technical infrastructure  
- Billing systems
- Customer communication preferences

```java
// Abstraction side
public abstract class TelecomService {
    protected ServicePlatform platform;
    
    public TelecomService(ServicePlatform platform) {
        this.platform = platform;
    }
    
    public abstract ServiceResult activate(String customerId, ServiceConfig config);
    public abstract ServiceResult deactivate(String customerId);
    
    // Template methods using platform implementation
    protected final void logActivation(String customerId) {
        platform.logServiceEvent("ACTIVATION", customerId);
    }
}

// Implementation side
public interface ServicePlatform {
    void validateCustomer(String customerId);
    void configureInfrastructure(ServiceConfig config);
    void updateBilling(String customerId, ServiceConfig config);
    void sendNotification(String customerId, String message);
    void logServiceEvent(String event, String customerId);
    ComplianceResult checkCompliance(ServiceConfig config);
}

public class GermanServicePlatform implements ServicePlatform {
    // TODO: Implement German-specific behavior
    // - GDPR compliance
    // - German billing system integration
    // - German language notifications
}

public class MobileService extends TelecomService {
    // TODO: Implement mobile service abstraction
    // Works with any platform implementation
}
```

**Requirements:**
- Services should work with any platform without changes
- Platforms should support any service type
- Easy to add new countries/platforms
- Easy to add new service types
- Maintain platform-specific behavior

#### Part B: Flyweight Pattern for Configuration Management (10 minutes)

**Challenge:** The system needs to manage thousands of service configurations, but many configurations are shared across customers (same data plans, same SLA levels, etc.).

**Problem:**
```java
// Memory-intensive approach
public class Customer {
    private String customerId;
    private ServiceConfiguration mobileConfig;    // Lots of duplication
    private ServiceConfiguration dslConfig;       // Same config objects
    private ServiceConfiguration fiberConfig;    // across many customers
}

public class ServiceConfiguration {
    private String serviceType;
    private String dataPlan;        // "5GB", "10GB", "UNLIMITED" - repeated across customers
    private String slaLevel;       // "BRONZE", "SILVER", "GOLD" - repeated
    private String billingCycle;   // "MONTHLY", "QUARTERLY" - repeated
    private List<String> features; // Same feature combinations repeated
    private PricingInfo pricing;   // Same pricing for same plans
    // ... many more fields that are often identical
}
```

**Implement Flyweight Solution:**
```java
// Flyweight interface
public interface ServiceConfigurationFlyweight {
    void applyConfiguration(String customerId, ServiceContext context);
}

// Flyweight factory
public class ServiceConfigurationFactory {
    private static Map<String, ServiceConfigurationFlyweight> configurations = new HashMap<>();
    
    public static ServiceConfigurationFlyweight getConfiguration(
        String serviceType, String dataPlan, String slaLevel) {
        
        String key = serviceType + ":" + dataPlan + ":" + slaLevel;
        // TODO: Implement flyweight creation and reuse
    }
}

// Context object (customer-specific data)
public class ServiceContext {
    private String customerId;
    private LocalDateTime activationDate;
    private String customerAddress;  // External state
    private PaymentMethod paymentMethod; // Customer-specific
}
```

### Integration Challenge
Combine Bridge and Flyweight patterns to create an efficient, flexible service management system:

```java
// Usage example
ServicePlatform germanPlatform = new GermanServicePlatform();
TelecomService mobileService = new MobileService(germanPlatform);

// Reuse flyweight configurations  
ServiceConfigurationFlyweight config = ServiceConfigurationFactory.getConfiguration(
    "MOBILE", "10GB", "GOLD");

ServiceContext customerContext = new ServiceContext("CUST-123", now(), address, payment);
config.applyConfiguration("CUST-123", customerContext);
```

### Discussion Points
- How do Bridge and Flyweight patterns complement each other?
- What are the memory vs. complexity trade-offs with Flyweight?
- How would you handle configuration changes in a flyweight system?
- When might Bridge pattern become over-engineered?

---

## Summary Workshop: Pattern Integration Challenge 🎯
**Duration:** 15 minutes | **Type:** Review & Integration

### Scenario Integration
You've now worked with multiple structural patterns. Consider how they might work together in a real Telekom system:

1. **Decorator** for service enhancements
2. **Composite** for network infrastructure 
3. **Proxy** for legacy system access
4. **Bridge** for multi-platform support
5. **Flyweight** for efficient configuration management

### Integration Questions

#### Architecture Analysis (5 minutes)
1. How would you combine Decorator and Proxy patterns in the service activation system?
2. Could Bridge and Adapter patterns be used together for legacy integration?
3. How would Flyweight pattern impact the Composite pattern's memory usage?

#### Design Decisions (5 minutes)
1. Which patterns address **performance** concerns?
2. Which patterns address **flexibility** concerns?  
3. Which patterns address **integration** concerns?
4. How do you avoid pattern overuse in system design?

#### Real-World Application (5 minutes)
**Discussion:** Identify a system in your current work environment that could benefit from structural patterns:
- What integration challenges do you face?
- Which structural patterns might apply?
- What would be your implementation strategy?
- How would you measure the success of pattern introduction?

---

## Key Takeaways from Day 2

### Pattern Relationships
- **Structural patterns** often work together to solve complex system composition problems
- **Bridge + Flyweight** combination addresses both flexibility and performance
- **Decorator + Proxy** chains provide layered enhancements and controls
- **Composite + Facade** offers both fine-grained and simplified interfaces

### Architectural Insights
- Structural patterns primarily address **system composition** and **interface management**
- They enable **loose coupling** between different system components
- Many structural patterns support the **Open/Closed Principle** through composition
- **Memory efficiency** vs. **design flexibility** trade-offs are common themes

### Design Guidelines
- Use structural patterns to **simplify complex subsystems**
- **Compose behavior** rather than inheriting it when possible
- Consider **performance implications** of pattern layering
- **Interface design** is crucial for effective structural pattern application

### Tomorrow's Preview: Behavioral Patterns
Day 3 will focus on **behavioral patterns** that manage **algorithms** and **responsibilities**:
- How objects collaborate and communicate
- Dynamic behavior assignment and modification
- Workflow and process management patterns
- Event handling and notification systems

---

**Instructor Notes:**
- Emphasize composition over inheritance themes throughout exercises
- Use timing to keep exercises moving but allow for good architectural discussions  
- Connect patterns back to real-world enterprise integration challenges
- Prepare to help with complex Bridge pattern implementation - it's often the most challenging