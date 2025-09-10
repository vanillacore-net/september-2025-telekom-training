# Block 1 Complete PowerPoint Specification

## CRITICAL: This presentation is currently INCOMPLETE
The existing block1-presentation.pptx only has 10 slides but should contain ALL 27 slides listed below for complete coverage of all 5 creational patterns.

## Template: VanillaCore.pptx
Source: templates/VanillaCore.pptx
Target: presentations/powerpoint/block1-presentation-complete.pptx

---

## SLIDE 1: Block 1 - Creational Patterns (Title Slide)
**Content:**
- Factory Method Pattern
- Abstract Factory Pattern  
- Builder Pattern
- Prototype Pattern
- Singleton Pattern

**Note:** Block 1 fokussiert auf Objekterzeugungsmuster für saubere, erweiterbare Architekturen in Enterprise-Systemen.

---

## FACTORY METHOD PATTERN (Slides 2-6)

### SLIDE 2: Was ist hier schlecht?
**Code Example:**
```java
public class CustomerManager {
    public Customer createCustomer(String type, String name, String contractId) {
        switch (type) {
            case "PRIVATE":
                Customer privateCustomer = new Customer();
                privateCustomer.setName(name);
                privateCustomer.setTariffOptions(Arrays.asList("Basic", "Comfort"));
                privateCustomer.setPaymentMethod("SEPA");
                return privateCustomer;
            case "BUSINESS":
                Customer businessCustomer = new Customer();
                businessCustomer.setName(name);
                businessCustomer.setTariffOptions(Arrays.asList("Professional", "Enterprise"));
                businessCustomer.setPaymentMethod("Invoice");
                return businessCustomer;
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }
}
```

### SLIDE 3: Code Smells identifiziert
- Long Method: createCustomer-Methode hat zu viele Zeilen
- Switch Statements: Typ-basierte Verzweigung deutet auf fehlendes Polymorphismus hin
- Feature Envy: Methode manipuliert mehr Daten als sie besitzt
- Duplicate Code: Wiederholte Zuweisungen in jedem Case
- Open/Closed Principle Verletzung: Neue Kunden-Typen erfordern Änderung bestehender Methode

### SLIDE 4: Lösung: Factory Method Pattern
- Definiert eine Schnittstelle zum Erstellen von Objekten
- Unterklassen entscheiden, welche Klasse instantiiert wird
- Polymorphismus statt Konditionals
- Erweiterbarkeit ohne Modifikation bestehenden Codes
- Template Method verwendet Factory Method
- Gemeinsame Geschäftslogik in Creator-Klasse

### SLIDE 5: Implementierung - Factory Method Creator
**Code Example:**
```java
public abstract class CustomerFactory {
    // Factory Method - zu implementieren von Subklassen
    protected abstract Customer createCustomer(String name, String contractId);
    
    // Template Method - verwendet Factory Method
    public Customer processNewCustomer(String name, String contractId) {
        Customer customer = createCustomer(name, contractId);
        validateContract(customer);
        persistCustomer(customer);
        sendWelcomeMessage(customer);
        return customer;
    }
}
```

### SLIDE 6: Implementierung - Konkrete Factories
**Code Example:**
```java
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

**Note:** Factory Method löst Code-Smells durch Polymorphismus und erfüllt SOLID-Prinzipien natürlich.

---

## ABSTRACT FACTORY PATTERN (Slides 7-11)

### SLIDE 7: Was ist hier schlecht?
**Code Example:**
```java
public class CustomerService {
    public CustomerData getCustomer(String customerId, String channel) {
        if ("WEB".equals(channel)) {
            WebAuthService webAuth = new WebAuthService();
            if (!webAuth.validateSession(getCurrentSession())) {
                throw new AuthenticationException();
            }
            WebCustomerAPI webAPI = new WebCustomerAPI();
            return webAPI.getCustomerData(customerId);
        } else if ("MOBILE".equals(channel)) {
            MobileOAuthService mobileAuth = new MobileOAuthService();
            if (!mobileAuth.validateToken(getCurrentToken())) {
                throw new AuthenticationException();
            }
            MobileCustomerAPI mobileAPI = new MobileCustomerAPI();
            return transformMobileResponse(mobileAPI.getCustomer(customerId));
        }
        throw new IllegalArgumentException("Unsupported channel: " + channel);
    }
}
```

### SLIDE 8: Code Smells identifiziert
- God Object: Eine Klasse kennt alle Implementierungen
- Switch Statement: Kanal-basierte Verzweigung
- Tight Coupling: Direkte Abhängigkeiten zu konkreten Implementierungen
- Interface Segregation Verletzung: Ein Service für alle Kanäle
- Open/Closed Principle Verletzung: Neue Kanäle erfordern Änderungen

### SLIDE 9: Lösung: Abstract Factory Pattern
- Schnittstelle für Familien verwandter Objekte
- Keine Spezifikation konkreter Klassen
- Konsistente Service-APIs über alle Kanäle
- Einfache Erweiterung um neue Kanäle
- Service-Familie wird zusammen erstellt
- Klare Trennung zwischen Kanal-Logik und Geschäftslogik

### SLIDE 10: Implementierung - Abstract Factory
**Code Example:**
```java
public abstract class ChannelServiceFactory {
    public abstract AuthenticationService createAuthenticationService();
    public abstract CustomerService createCustomerService();
    public abstract BillingService createBillingService();
    public abstract NotificationService createNotificationService();
    
    public ChannelServiceSuite createServiceSuite() {
        return new ChannelServiceSuite(
            createAuthenticationService(),
            createCustomerService(),
            createBillingService(),
            createNotificationService()
        );
    }
}
```

### SLIDE 11: Implementierung - Konkrete Factory
**Code Example:**
```java
public class WebChannelFactory extends ChannelServiceFactory {
    @Override
    public AuthenticationService createAuthenticationService() {
        return new WebSessionAuthService();
    }
    
    @Override
    public CustomerService createCustomerService() {
        return new WebCustomerService();
    }
    
    @Override
    public BillingService createBillingService() {
        return new WebBillingService();
    }
}
```

**Note:** Abstract Factory strukturiert Service-Familien sauber und ermöglicht bessere Testbarkeit durch Service-Isolation.

---

## BUILDER PATTERN (Slides 12-16)

### SLIDE 12: Was ist hier schlecht?
**Code Example:**
```java
public class CustomerRepository {
    // Telescoping Constructor Anti-Pattern
    public List<Customer> findCustomers(String name, String contractType, 
                                       String tariff, Date contractStart, 
                                       Date contractEnd, String city, 
                                       String postalCode, Boolean isActive,
                                       String paymentMethod, Integer minRevenue,
                                       String sortBy, String sortOrder,
                                       Integer limit, Integer offset) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM customers c ");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;
        
        if (name != null && !name.isEmpty()) {
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("c.name LIKE ?");
            params.add("%" + name + "%");
            hasWhere = true;
        }
        // ... weitere 20 Zeilen if-Statements
    }
}
```

### SLIDE 13: Code Smells identifiziert
- Telescoping Constructor: Zu viele Parameter in Methode
- Complex Method: Eine Methode macht zu viel
- Primitive Obsession: Viele String/Integer Parameter statt Value Objects
- String Concatenation: Unsichere SQL-Erstellung
- Boolean Parameters: Schwer verständliche boolean-Flags
- Open/Closed Violation: Neue Filter erfordern Methodenänderung

### SLIDE 14: Lösung: Builder Pattern
- Trennt Konstruktion komplexer Objekte von Repräsentation
- Fluent Interface für bessere Lesbarkeit
- Type-sichere Objekterstellung
- Validation im Builder möglich
- Immutable Value Objects
- Flexible Objektkonfiguration ohne Telescoping Constructor

### SLIDE 15: Implementierung - Search Criteria Builder
**Code Example:**
```java
public class CustomerSearchCriteria {
    private final String name;
    private final String contractType;
    private final DateRange contractPeriod;
    private final SortCriteria sortCriteria;
    
    private CustomerSearchCriteria(Builder builder) {
        this.name = builder.name;
        this.contractType = builder.contractType;
        this.contractPeriod = builder.contractPeriod;
        this.sortCriteria = builder.sortCriteria;
    }
    
    public static class Builder {
        private String name;
        private String contractType;
        private DateRange contractPeriod;
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public CustomerSearchCriteria build() {
            validate();
            return new CustomerSearchCriteria(this);
        }
    }
}
```

### SLIDE 16: Implementierung - Fluent Interface Usage
**Code Example:**
```java
// Factory Methods für häufige Anwendungsfälle
CustomerSearchCriteria businessCustomers = CustomerSearchCriteria
    .forBusinessCustomers()
    .withName("Schmidt")
    .inCity("Berlin")
    .withMinRevenue(1000, Currency.EUR)
    .sortByRevenueDescending()
    .page(1, 20)
    .build();
    
// Repository Integration
List<Customer> results = customerRepository.findByComplexCriteria(businessCustomers);
```

**Note:** Builder Pattern löst Telescoping Constructor Problem und verbessert Code-Readability durch klare Fluent Interfaces.

---

## PROTOTYPE PATTERN (Slides 17-21)

### SLIDE 17: Was ist hier schlecht?
**Code Example:**
```java
@Service
public class ServiceConfigurationManager {
    public ServiceConfiguration createDevConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // Expensive Database Lookups für Defaults (200ms+)
        config.setDatabaseSettings(loadDatabaseDefaults());
        config.setSecuritySettings(loadSecurityDefaults());
        config.setCachingSettings(loadCachingDefaults());
        
        // Expensive SSL Certificate Validation (500ms+)
        config.setSslCertificate(validateAndLoadSslCert("dev-cert.pem"));
        
        // Expensive Service Discovery (300ms+)
        config.setServiceEndpoints(discoverAvailableServices());
        
        // Environment-specific Anpassungen
        config.setEnvironment("DEV");
        config.setLogLevel("DEBUG");
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        // DUPLICATE: Gleiche expensive Operations! (1400ms+)
    }
}
```

### SLIDE 18: Code Smells identifiziert
- Expensive Object Creation: 1400ms+ für identische Operationen
- Duplicate Code: Gleiche Initialisierung für jede Konfiguration
- Performance Problem: Wiederholte Database-Lookups und SSL-Validation
- Template-basierte Konfiguration: 90% identisch, 10% variabel
- Resource Verschwendung: Unnötige Service Discovery bei jeder Erstellung

### SLIDE 19: Lösung: Prototype Pattern
- Erstellt neue Objekte durch Klonen statt Instanziierung
- Template Prototypes werden EINMAL erstellt und gecached
- 99%+ Performance-Verbesserung (1400ms → 2ms)
- Memory-Effizienz durch intelligent Sharing
- Selective Deep Copy für mutable Objects
- Shared References für immutable, expensive Objects

### SLIDE 20: Implementierung - Cloneable Configuration
**Code Example:**
```java
public abstract class ServiceConfiguration implements Cloneable {
    private String environment;
    private DatabaseSettings databaseSettings;
    private SSLCertificate sslCertificate;
    private ServiceEndpoints serviceEndpoints;
    
    @Override
    public ServiceConfiguration clone() {
        try {
            ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
            
            // Deep Copy für mutable, komplexe Objekte
            cloned.databaseSettings = this.databaseSettings.deepCopy();
            
            // Shared Reference für immutable, expensive Objekte
            cloned.sslCertificate = this.sslCertificate;
            cloned.serviceEndpoints = this.serviceEndpoints;
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
}
```

### SLIDE 21: Implementierung - Prototype Registry
**Code Example:**
```java
@Service
public class ConfigurationPrototypeRegistry {
    private final Map<String, ServiceConfiguration> prototypes = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initializePrototypes() {
        // Template wird EINMAL erstellt (1400ms)
        ServiceConfiguration baseTemplate = TelekomServiceConfiguration.createBaseTemplate();
        prototypes.put("TELEKOM_BASE", baseTemplate);
    }
    
    // Fast Cloning - 1400ms → 2ms!
    public ServiceConfiguration createConfiguration(String templateName, 
                                                   ConfigurationCustomizer customizer) {
        ServiceConfiguration prototype = prototypes.get(templateName);
        ServiceConfiguration cloned = prototype.clone();
        customizer.customize(cloned);
        return cloned;
    }
}
```

**Note:** Prototype Pattern bietet 99%+ Performance-Verbesserung bei expensive Object Creation und ermöglicht template-driven Configuration.

---

## SINGLETON PATTERN (Slides 22-26)

### SLIDE 22: Was ist hier schlecht?
**Code Example:**
```java
@Service
public class CustomerManagementService {
    // Tight Coupling zu Legacy-Systemen
    private LegacyMainframeClient mainframeClient;
    private OldCrmSoapClient crmSoapClient;
    
    public CustomerData getCompleteCustomerView(String customerId) {
        try {
            MainframeCustomerRecord record = mainframeClient.getCustomer(customerId);
            result.setName(record.getName());
            result.setAddress(parseMainframeAddress(record.getAddressBlock()));
        } catch (MainframeConnectionException e) {
            try {
                CrmCustomerSoap soapResponse = crmSoapClient.getCustomerData(customerId);
                result.setName(soapResponse.getFullName());
                result.setAddress(convertSoapAddress(soapResponse.getAddressData()));
            } catch (SOAPException e2) {
                throw new CustomerNotFoundException("Could not retrieve customer: " + customerId);
            }
        }
        return result;
    }
}
```

### SLIDE 23: Code Smells identifiziert
- Tight Coupling: Service kennt alle Legacy-Formate
- Multiple Responsibilities: Business Logic + Format-Konvertierung
- Hard to Test: Abhängigkeiten zu externen Systemen
- Inflexible: Neue Systeme erfordern Service-Änderung
- Clean Architecture Violation: Domain Layer kennt Infrastructure Details

### SLIDE 24: Lösung: Singleton + Adapter Pattern
- Singleton für Shared Resources (Connection Pools, Configuration)
- Adapter Pattern für Legacy-Integration ohne Domain-Verschmutzung
- Clean Architecture durch Dependency Inversion
- Anti-Corruption Layer schützt Domain Model
- Thread-Safe Singleton Implementierung
- Enum Singleton als moderne, sichere Variante

### SLIDE 25: Implementierung - Enum Singleton
**Code Example:**
```java
public enum ConfigurationManager {
    INSTANCE;
    
    private final Map<String, String> config;
    
    ConfigurationManager() {
        this.config = loadConfiguration();
    }
    
    public String getProperty(String key) {
        return config.get(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }
    
    private Map<String, String> loadConfiguration() {
        Map<String, String> props = new HashMap<>();
        loadEnvironmentVariables(props);
        loadPropertyFiles(props);
        return Collections.unmodifiableMap(props);
    }
}
```

### SLIDE 26: Implementierung - Adapter für Legacy Integration
**Code Example:**
```java
@Component
public class MainframeCustomerAdapter implements CustomerDataService {
    private final MainframeClient mainframeClient;
    private final MainframeDataMapper mapper;
    
    @Override
    public CustomerProfile getCustomerProfile(CustomerId customerId) {
        try {
            MainframeCustomerRecord record = mainframeClient.retrieveCustomer(customerId.getValue());
            return mapper.toDomainModel(record);
        } catch (MainframeConnectionException e) {
            throw new CustomerDataAccessException("Mainframe connection failed", e);
        }
    }
}

// Domain Interface - keine externe Abhängigkeiten
public interface CustomerDataService {
    CustomerProfile getCustomerProfile(CustomerId customerId);
}
```

**Note:** Singleton Pattern für Configuration Management kombiniert mit Adapter Pattern ermöglicht saubere Legacy-Integration ohne Domain-Verschmutzung.

---

## SLIDE 27: Block 1 Zusammenfassung (Summary Slide)
**Creational Patterns - Schlüsselerkenntnisse:**
- Factory Method: Polymorphismus statt Switch-Statements
- Abstract Factory: Service-Familien sauber strukturieren
- Builder: Komplexe Objektkonfiguration mit Fluent Interface
- Prototype: Performance-Optimierung durch intelligentes Klonen
- Singleton: Shared Resources + Adapter für Legacy-Integration
- SOLID-Prinzipien werden natürlich erfüllt
- Bessere Testbarkeit durch lose Kopplung

**Note:** Block 1 Creational Patterns schaffen die Grundlage für saubere, erweiterbare Enterprise-Architekturen durch systematische Objekterzeugung und klare Verantwortlichkeiten.

---

## FORMATTING GUIDELINES

### Code Slides
- Use monospace font for all code examples
- Syntax highlighting for Java code
- Appropriate font size for readability (not too small)
- Code blocks should fit within slide boundaries

### Bullet Points
- Use consistent bullet point formatting
- Clear hierarchy with indentation
- Professional color scheme from VanillaCore template

### Notes
- Include speaker notes for each slide
- Additional context for instructors
- Key points to emphasize during presentation

---

## VALIDATION CHECKLIST

Before finalizing the presentation, ensure:
- [ ] ALL 27 slides are included
- [ ] Each pattern has complete 4-6 slide coverage
- [ ] All code examples are formatted correctly
- [ ] VanillaCore template styling is applied
- [ ] German text is correctly spelled
- [ ] Code examples use consistent Java formatting
- [ ] Each pattern section is complete with problem → solution → implementation
- [ ] Summary slide captures all key learning points

**CRITICAL:** The current block1-presentation.pptx is INCOMPLETE with only 10 slides. This specification covers ALL 27 required slides for complete pattern coverage.