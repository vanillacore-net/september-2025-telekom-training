# Complete Design Patterns & Architecture Training Content
*Comprehensive extraction from all training materials*

---

# INTRODUCTION BLOCK

## 1. Was ist Software-Architektur?

### Lernziele
- Gemeinsames Verständnis von Software-Architektur
- Verschiedene Definitionen kennenlernen
- Enterprise-Kontext verstehen

### Definition nach IEEE 1471
> *"Architecture is the fundamental organization of a system, embodied in its components, their relationships to each other and to the environment, and the principles governing its design and evolution."*

**Auf Deutsch:** Software-Architektur ist die grundlegende Organisation eines Systems, verkörpert durch seine Komponenten, deren Beziehungen zueinander und zur Umgebung, sowie die Prinzipien für Design und Evolution.

### Definition nach Martin Fowler
> *"Architecture is about the important stuff. Whatever that is."*

### Definition nach Grady Booch
> *"Architecture represents the significant design decisions that shape a system, where significant is measured by cost of change."*

### Arbeitsdefinition für diesen Workshop
> **Software-Architektur ist die Kunst, wichtige Designentscheidungen zu treffen, die die Struktur, das Verhalten und die Evolution eines Systems bestimmen - mit dem Ziel, fachliche Anforderungen optimal zu erfüllen.**

### Enterprise-Besonderheiten
- **Legacy-Systeme**: Jahrzehntealte Systeme, die noch laufen müssen
- **Regulatorische Anforderungen**: DSGVO, Compliance-Standards
- **Hochverfügbarkeit**: 99.9%+ Uptime-Anforderungen
- **Skalierung**: Millionen von Benutzern, große Datenmengen
- **Sicherheit**: Kritische Geschäftsdaten, Cyber-Security
- **Integration**: Dutzende von Systemen müssen zusammenarbeiten

---

## 2. Clean Code Grundlagen

### Lernziele
- Verständnis für sauberen Code
- Unterschied zwischen clever und lesbar
- Wartbarkeit als Ziel
- Technische Schulden vermeiden

### Definition nach Robert C. Martin
> *"Clean code is code that has been taken care of. Someone has taken the time to keep it simple and orderly."*

### Die 5 Grundregeln des Clean Code

#### 1. Lesbarkeit vor Cleverness
```text
❌ SCHLECHT: int d; // elapsed time in days
✅ GUT:     int elapsedTimeInDays;
```

#### 2. Eindeutige Namen
- **Funktionen**: Verben (calculateTax, sendEmail)
- **Variablen**: Substantive (customerName, orderTotal)
- **Boolean**: Fragen (isValid, hasPermission)
- **Konstanten**: Großbuchstaben (MAX_RETRY_COUNT)

#### 3. Kurze Funktionen
- Eine Funktion = Ein Gedanke
- Faustregeln: Max. 20 Zeilen, max. 3 Parameter

#### 4. Keine Kommentare, die Code erklären
```text
❌ SCHLECHT: 
// Check if employee is eligible for bonus
if (employee.getYearsOfService() > 5 && employee.getPerformanceRating() > 7)

✅ GUT:
if (employee.isEligibleForBonus())
```

#### 5. Konsistenz
- Gleiche Namenskonventionen
- Gleiche Formatierung
- Gleiche Patterns

### Technische Schulden nach Martin Fowler
> *"Technical debt is a metaphor referring to the eventual consequences of poor system design, software architecture or software development within a codebase."*

#### Arten technischer Schulden
1. **Bewusste Schulden**: "Wir machen es schnell und dirty, räumen aber nächste Woche auf"
2. **Unbewusste Schulden**: Entstehen durch Unwissen oder mangelnde Skills
3. **Umwelt-Schulden**: Änderung der Anforderungen macht bisherigen Code obsolet

---

## 3. Fachlichkeit vor Technik

### Das Technology-First Anti-Pattern

#### Typische Aussagen
- *"Wir nutzen jetzt Microservices!"* - Aber warum?
- *"Lass uns auf Kubernetes umsteigen!"* - Aber welches Problem löst das?
- *"Wir brauchen eine Event-Driven Architecture!"* - Aber passt das zu unserer Fachlichkeit?

#### Technology-First Symptome
- **Hype-Driven Development**: "Das ist modern, das machen wir auch"
- **Solution looking for a problem**: "Wir haben Kafka, jetzt brauchen wir Events"
- **Architecture Astronauts**: Komplexe Lösungen für einfache Probleme
- **Over-Engineering**: 20 Services für 5 Use Cases

### Domain-Driven Design: Die richtige Reihenfolge

#### 1. Fachlichkeit verstehen
- Was ist das Geschäftsproblem?
- Wie arbeiten die Fachexperten heute?
- Welche Regeln und Prozesse gibt es?
- Was sind die echten Anforderungen?

#### 2. Fachliche Architektur entwerfen
- Welche fachlichen Bereiche (Domains) gibt es?
- Wie schneiden wir die Bereiche (Bounded Contexts)?
- Welche fachlichen Services brauchen wir?
- Wie modellieren wir die Geschäftslogik?

#### 3. Technologie auswählen
- Welche Technologie unterstützt unser fachliches Modell am besten?
- Was löst unsere spezifischen Probleme?
- Was passt zu unserem Team und unserer Infrastruktur?

### Warnsignale für Technology-First
- "Das ist modern/trendy/hip"
- "Das macht Netflix/Google/Amazon auch"
- "Das steht in meinem Lebenslauf gut"
- "Das ist die Zukunft"
- "Das ist skalierbar" (ohne Nachweis des Skalierungsbedarfs)

---

## 4. Design Patterns Motivation

### Geschichte der Design Patterns

#### Christopher Alexander (1977 - Architektur)
> *"Each pattern describes a problem which occurs over and over again in our environment, and then describes the core of the solution to that problem, in such a way that you can use this solution a million times over, without ever doing it the same way twice."*

#### Gang of Four (1994)
**Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides**

##### Warum brauchten wir Patterns?
1. **Wiederkehrende Design-Probleme**: Jeder erfindet das Rad neu
2. **Schlechte Kommunikation**: Lange Erklärungen statt präziser Begriffe
3. **Fehlende Best Practices**: Bewährte Lösungen werden nicht geteilt

### Warum Design Patterns?

#### 1. Bewährte Lösungen nutzen
Anstatt das Rad neu zu erfinden, nutzen wir erprobte Lösungen.

#### 2. Gemeinsame Sprache entwickeln
```text
❌ OHNE PATTERN-SPRACHE:
"Wir brauchen eine Klasse, die andere Klassen erzeugt, 
aber die Entscheidung welche Klasse zur Laufzeit trifft..."

✅ MIT PATTERN-SPRACHE:  
"Wir nutzen Factory Pattern"
```

#### 3. Design-Qualität verbessern
Patterns kodifizieren gute objektorientierten Design.

#### 4. Wartbarkeit erhöhen
Bekannte Patterns sind einfacher zu verstehen und zu ändern.

### Was Patterns NICHT sind
1. **Silberkugeln**: Patterns lösen nicht alle Probleme
2. **Dogmen**: Patterns müssen nicht sklavisch befolgt werden
3. **Komplexität um der Komplexität willen**: Einfache Probleme brauchen einfache Lösungen
4. **Copy-Paste Code**: Patterns sind konzeptuelle Lösungen, nicht Code-Snippets

---

## 5. Refactoring Philosophie

### Definition nach Martin Fowler
> *"Refactoring is the process of changing a software system in such a way that it does not alter the external behavior of the code yet improves its internal structure."*

### Die Boy Scout Rule
> *"Always leave the campground cleaner than you found it."*
> *"Always check a module in cleaner than when you checked it out."*

#### Konkrete Boy Scout Aktionen
- Variable umbenennen (temp → elapsedTimeInDays)
- Magic Number extrahieren (7 → DAYS_PER_WEEK)
- Long Method aufteilen
- Duplicate Code extrahieren
- Unused Code entfernen

### Rule of Three nach Martin Fowler
1. **Das erste Mal** - mache es einfach
2. **Das zweite Mal** - ärgere dich über die Duplikation, aber mache es trotzdem
3. **Das dritte Mal** - refactore!

### Refactoring-Sicherheitsmaßnahmen
#### 1. Tests zuerst
- Verstehe das aktuelle Verhalten
- Schreibe Tests für das Verhalten (falls nicht vorhanden)
- Alle Tests sind grün

#### 2. Kleine Schritte
- Ein Schritt nach dem anderen
- Nach jedem Schritt: Tests laufen lassen
- Immer grün bleiben

#### 3. IDE-Unterstützung nutzen
- Extract Method
- Rename Variable/Method/Class
- Move Method/Field
- Inline Variable/Method

### Patterns entstehen durch Refactoring
**Wichtige Erkenntnis: Patterns werden durch Refactoring eingeführt, nicht von Anfang an geplant!**

---

# CREATIONAL PATTERNS (DAY 1)

## 1. Factory Method Pattern

### Problem-Motivation: Customer Service System
Ein typisches Problem in gewachsenen Telekom-Systemen: Die Kundenbetreuung muss verschiedene Kunden-Typen verwalten - Privatkunden, Geschäftskunden, Premium-Kunden.

### Problematischer Code
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
            // ... weitere Cases mit Duplikation
        }
    }
}
```

### Identifizierte Code-Smells
1. **Long Method**: createCustomer-Methode hat zu viele Zeilen
2. **Switch Statements**: Typ-basierte Verzweigung deutet auf fehlendes Polymorphismus hin
3. **Feature Envy**: Methode manipuliert mehr Daten als sie besitzt
4. **Duplicate Code**: Wiederholte Zuweisungen in jedem Case
5. **Open/Closed Principle Verletzung**: Neue Kunden-Typen erfordern Änderung bestehender Methode

### Factory Method Lösung

#### UML-Struktur
```
Creator (abstract)
  +factoryMethod(): Product
  +businessLogic()
  
ConcreteCreator extends Creator
  +factoryMethod(): ConcreteProduct
  
Product (interface)
  
ConcreteProduct implements Product
```

#### Refactoring-Schritte

**Schritt 1: Kunden-Abstraktion erstellen**
```java
public interface Customer {
    String getName();
    String getContractId();
    List<String> getTariffOptions();
    String getPaymentMethod();
    String getSupportLevel();
    void processContract();
}
```

**Schritt 2: Konkrete Implementierungen**
```java
public class PrivateCustomer implements Customer {
    private String name;
    private String contractId;
    private List<String> tariffOptions;
    
    public PrivateCustomer(String name, String contractId) {
        this.name = name;
        this.contractId = contractId;
        this.tariffOptions = Arrays.asList("Basic", "Comfort");
    }
    
    @Override
    public String getPaymentMethod() {
        return "SEPA";
    }
    
    @Override
    public void processContract() {
        validatePersonalData();
        setupBasicServices();
    }
}
```

**Schritt 3: Factory Method Creator**
```java
public abstract class CustomerFactory {
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

### SOLID-Prinzipien Integration
- **SRP**: Jede Factory-Klasse ist nur für einen Kunden-Typ zuständig
- **OCP**: Neue Kunden-Typen durch neue Factory-Klassen hinzufügbar
- **DIP**: Abhängigkeit zu Customer-Interface, nicht zu konkreten Implementierungen

---

## 2. Abstract Factory & Layered Architecture

### Problem-Motivation: Multi-Channel Service Platform
Telekom betreibt verschiedene Service-Kanäle (Web, Mobile App, Call Center, Partner-Portal). Jeder Kanal benötigt unterschiedliche Service-Implementierungen, aber ähnliche Funktionalitäten.

### Problematischer Code
```java
public class CustomerService {
    public CustomerData getCustomer(String customerId, String channel) {
        // Code-Smell: Kanal-spezifische Logik verstreut
        if ("WEB".equals(channel)) {
            // Web-spezifische Authentifizierung
            WebAuthService webAuth = new WebAuthService();
            if (!webAuth.validateSession(getCurrentSession())) {
                throw new AuthenticationException();
            }
            WebCustomerAPI webAPI = new WebCustomerAPI();
            return webAPI.getCustomerData(customerId);
        } else if ("MOBILE".equals(channel)) {
            // Mobile-spezifische OAuth
            MobileOAuthService mobileAuth = new MobileOAuthService();
            // ... weitere Kanäle
        }
    }
}
```

### Abstract Factory Lösung

#### UML-Struktur
```
AbstractFactory
  +createServiceA(): AbstractServiceA
  +createServiceB(): AbstractServiceB

ConcreteFactory1 extends AbstractFactory
  +createServiceA(): ConcreteServiceA1
  +createServiceB(): ConcreteServiceB1
```

#### Layered Architecture Integration
```
┌─────────────────────────────────────┐
│        Presentation Layer           │ <- Channel-spezifische Controller
├─────────────────────────────────────┤
│         Business Layer              │ <- Channel-agnostische Geschäftslogik
├─────────────────────────────────────┤
│        Integration Layer            │ <- Abstract Factory Pattern
├─────────────────────────────────────┤
│         Data Access Layer           │ <- Channel-spezifische Implementierungen
└─────────────────────────────────────┘
```

#### Service-Familie Definition
```java
// Service-Abstrakte definieren
public interface AuthenticationService {
    boolean authenticate(AuthenticationContext context);
    void logout(String sessionId);
}

public interface CustomerService {
    CustomerData getCustomer(String customerId);
    List<CustomerData> searchCustomers(SearchCriteria criteria);
}

// Abstract Factory
public abstract class ChannelServiceFactory {
    public abstract AuthenticationService createAuthenticationService();
    public abstract CustomerService createCustomerService();
    public abstract BillingService createBillingService();
    
    // Convenience Method für komplette Service-Suite
    public ChannelServiceSuite createServiceSuite() {
        return new ChannelServiceSuite(
            createAuthenticationService(),
            createCustomerService(),
            createBillingService()
        );
    }
}
```

#### Konkrete Implementierungen
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
}

public class MobileChannelFactory extends ChannelServiceFactory {
    @Override
    public AuthenticationService createAuthenticationService() {
        return new OAuthAuthenticationService();
    }
    
    @Override
    public CustomerService createCustomerService() {
        return new MobileCustomerService();
    }
}
```

---

## 3. Builder Pattern & Repository Pattern

### Problem-Motivation: Komplexe Repository-Konfiguration
Telekom-Systeme benötigen flexible Repository-Konfigurationen für verschiedene Datenquellen, Caching-Strategien und Performance-Optimierungen.

### Builder Pattern für Repository-Konfiguration
```java
public class RepositoryBuilder {
    private DataSource dataSource;
    private CachingStrategy cachingStrategy;
    private ConnectionPool connectionPool;
    private QueryOptimizer queryOptimizer;
    
    public static RepositoryBuilder newRepository() {
        return new RepositoryBuilder();
    }
    
    public RepositoryBuilder withDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }
    
    public RepositoryBuilder withCaching(CachingStrategy strategy) {
        this.cachingStrategy = strategy;
        return this;
    }
    
    public RepositoryBuilder withConnectionPool(int maxConnections) {
        this.connectionPool = new ConnectionPool(maxConnections);
        return this;
    }
    
    public <T> Repository<T> build(Class<T> entityType) {
        validate();
        return new ConfiguredRepository<>(
            entityType, dataSource, cachingStrategy, 
            connectionPool, queryOptimizer
        );
    }
}
```

### Repository Pattern Integration
```java
public interface Repository<T> {
    Optional<T> findById(Long id);
    List<T> findAll();
    T save(T entity);
    void delete(Long id);
    List<T> findBySpecification(Specification<T> spec);
}

public class TelekomCustomerRepository implements Repository<Customer> {
    private final RepositoryBuilder.ConfiguredRepository<Customer> delegate;
    
    public TelekomCustomerRepository() {
        this.delegate = RepositoryBuilder.newRepository()
            .withDataSource(createTelekomDataSource())
            .withCaching(new RedisCache())
            .withConnectionPool(50)
            .withQueryOptimization(true)
            .build(Customer.class);
    }
    
    @Override
    public List<Customer> findBySpecification(Specification<Customer> spec) {
        return delegate.findBySpecification(spec);
    }
    
    // Telekom-spezifische Methoden
    public List<Customer> findByTariffType(String tariffType) {
        return findBySpecification(
            CustomerSpecifications.hasTariffType(tariffType)
        );
    }
}
```

---

## 4. Prototype Pattern & Configuration Cloning

### Problem-Motivation: Telekom Netzwerk-Konfigurationen
In Telekom-Netzwerken müssen ähnliche Gerätekonfigurationen schnell erstellt und angepasst werden.

### Prototype Pattern Lösung
```java
public interface NetworkDeviceConfiguration extends Cloneable {
    NetworkDeviceConfiguration clone();
    void customize(Map<String, Object> parameters);
}

public class RouterConfiguration implements NetworkDeviceConfiguration {
    private String deviceType;
    private Map<String, String> settings;
    private List<NetworkInterface> interfaces;
    
    @Override
    public NetworkDeviceConfiguration clone() {
        try {
            RouterConfiguration cloned = (RouterConfiguration) super.clone();
            // Deep copy für komplexe Objekte
            cloned.settings = new HashMap<>(this.settings);
            cloned.interfaces = new ArrayList<>(this.interfaces);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    @Override
    public void customize(Map<String, Object> parameters) {
        parameters.forEach((key, value) -> {
            switch (key) {
                case "ip_address":
                    this.settings.put("ip", value.toString());
                    break;
                case "subnet_mask":
                    this.settings.put("mask", value.toString());
                    break;
            }
        });
    }
}

// Configuration Registry
public class ConfigurationRegistry {
    private Map<String, NetworkDeviceConfiguration> prototypes;
    
    public void registerPrototype(String name, NetworkDeviceConfiguration prototype) {
        prototypes.put(name, prototype);
    }
    
    public NetworkDeviceConfiguration createConfiguration(String prototypeName, 
                                                        Map<String, Object> customization) {
        NetworkDeviceConfiguration prototype = prototypes.get(prototypeName);
        if (prototype == null) {
            throw new IllegalArgumentException("Unknown prototype: " + prototypeName);
        }
        
        NetworkDeviceConfiguration clone = prototype.clone();
        clone.customize(customization);
        return clone;
    }
}
```

---

## 5. Singleton Pattern & Adapter Pattern & Clean Architecture

### Singleton Pattern - Richtige Anwendung
```java
// Thread-safe Singleton für Configuration Manager
public class TelekomConfigurationManager {
    private static volatile TelekomConfigurationManager instance;
    private final Properties configuration;
    
    private TelekomConfigurationManager() {
        configuration = loadConfiguration();
    }
    
    public static TelekomConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (TelekomConfigurationManager.class) {
                if (instance == null) {
                    instance = new TelekomConfigurationManager();
                }
            }
        }
        return instance;
    }
    
    public String getConfigValue(String key) {
        return configuration.getProperty(key);
    }
}
```

### Adapter Pattern Integration
```java
// Legacy System Integration
public interface ModernCustomerService {
    CustomerData getCustomer(String customerId);
    void updateCustomer(CustomerData customer);
}

public class LegacyCustomerSystemAdapter implements ModernCustomerService {
    private final LegacyCustomerSystem legacySystem;
    private final DataTransformer transformer;
    
    @Override
    public CustomerData getCustomer(String customerId) {
        LegacyCustomerRecord record = legacySystem.retrieveCustomer(customerId);
        return transformer.transformToModern(record);
    }
    
    @Override
    public void updateCustomer(CustomerData customer) {
        LegacyCustomerRecord record = transformer.transformToLegacy(customer);
        legacySystem.updateCustomer(record);
    }
}
```

### Clean Architecture Integration
```
┌─────────────────────────────────────┐
│   Frameworks & Drivers              │ <- Web, Database, External APIs
├─────────────────────────────────────┤
│   Interface Adapters               │ <- Controllers, Gateways, Presenters
├─────────────────────────────────────┤
│   Application Business Rules       │ <- Use Cases, Application Services
├─────────────────────────────────────┤
│   Enterprise Business Rules        │ <- Entities, Domain Services
└─────────────────────────────────────┘

Dependency Rule: Inner circles don't depend on outer circles
```

---

# STRUCTURAL PATTERNS (DAY 2)

## 1. Decorator Pattern

### Problem-Motivation: Telekom Service-Erweiterungen
Basis-Telekom-Services sollen dynamisch erweitert werden: Logging, Caching, Monitoring, Rate-Limiting.

### Decorator Pattern Lösung
```java
// Component Interface
public interface TelekomService {
    ServiceResponse processRequest(ServiceRequest request);
}

// Concrete Component
public class BaseBillingService implements TelekomService {
    @Override
    public ServiceResponse processRequest(ServiceRequest request) {
        // Basis-Billing-Logik
        return new ServiceResponse("Billing processed");
    }
}

// Base Decorator
public abstract class ServiceDecorator implements TelekomService {
    protected TelekomService decoratedService;
    
    public ServiceDecorator(TelekomService service) {
        this.decoratedService = service;
    }
    
    @Override
    public ServiceResponse processRequest(ServiceRequest request) {
        return decoratedService.processRequest(request);
    }
}

// Concrete Decorators
public class LoggingDecorator extends ServiceDecorator {
    public LoggingDecorator(TelekomService service) {
        super(service);
    }
    
    @Override
    public ServiceResponse processRequest(ServiceRequest request) {
        logger.info("Processing request: " + request.getId());
        ServiceResponse response = super.processRequest(request);
        logger.info("Response: " + response.getStatus());
        return response;
    }
}

public class CachingDecorator extends ServiceDecorator {
    private final Cache cache;
    
    @Override
    public ServiceResponse processRequest(ServiceRequest request) {
        String cacheKey = request.getCacheKey();
        ServiceResponse cached = cache.get(cacheKey);
        
        if (cached != null) {
            return cached;
        }
        
        ServiceResponse response = super.processRequest(request);
        cache.put(cacheKey, response);
        return response;
    }
}

// Usage
TelekomService service = new LoggingDecorator(
    new CachingDecorator(
        new BaseBillingService()
    )
);
```

---

## 2. Composite Pattern & Facade Pattern

### Composite Pattern: Hierarchische Netzwerk-Strukturen
```java
// Component
public interface NetworkComponent {
    String getName();
    double getCost();
    void displayInfo(int depth);
    void addComponent(NetworkComponent component);
    void removeComponent(NetworkComponent component);
}

// Leaf
public class NetworkDevice implements NetworkComponent {
    private String name;
    private double cost;
    
    @Override
    public void displayInfo(int depth) {
        System.out.println(" ".repeat(depth) + "Device: " + name + " ($" + cost + ")");
    }
    
    @Override
    public void addComponent(NetworkComponent component) {
        throw new UnsupportedOperationException("Cannot add to leaf node");
    }
}

// Composite
public class NetworkSegment implements NetworkComponent {
    private String name;
    private List<NetworkComponent> components = new ArrayList<>();
    
    @Override
    public double getCost() {
        return components.stream()
                .mapToDouble(NetworkComponent::getCost)
                .sum();
    }
    
    @Override
    public void displayInfo(int depth) {
        System.out.println(" ".repeat(depth) + "Segment: " + name + " (Total: $" + getCost() + ")");
        components.forEach(component -> component.displayInfo(depth + 2));
    }
    
    @Override
    public void addComponent(NetworkComponent component) {
        components.add(component);
    }
}
```

### Facade Pattern: Vereinfachte Netzwerk-API
```java
// Complex Subsystems
class NetworkConfigurationSystem { /* komplexe Konfiguration */ }
class NetworkMonitoringSystem { /* komplexe Überwachung */ }
class NetworkSecuritySystem { /* komplexe Sicherheit */ }

// Facade
public class NetworkManagementFacade {
    private NetworkConfigurationSystem configSystem;
    private NetworkMonitoringSystem monitorSystem;
    private NetworkSecuritySystem securitySystem;
    
    public NetworkManagementFacade() {
        this.configSystem = new NetworkConfigurationSystem();
        this.monitorSystem = new NetworkMonitoringSystem();
        this.securitySystem = new NetworkSecuritySystem();
    }
    
    // Vereinfachte High-Level Operations
    public void deployNewRouter(String location, RouterSpec spec) {
        // Orchestriert mehrere Subsysteme
        RouterConfig config = configSystem.createRouterConfig(spec);
        securitySystem.applySecurityPolicies(config);
        configSystem.deployConfiguration(location, config);
        monitorSystem.startMonitoring(location);
    }
    
    public NetworkStatus getNetworkStatus(String region) {
        List<DeviceStatus> devices = monitorSystem.getDeviceStatuses(region);
        List<SecurityAlert> alerts = securitySystem.getActiveAlerts(region);
        ConfigurationSummary config = configSystem.getConfigurationSummary(region);
        
        return new NetworkStatus(devices, alerts, config);
    }
}
```

---

## 3. Proxy Pattern & Flyweight Pattern

### Proxy Pattern: Service Access Control
```java
// Subject Interface
public interface TelekomDataService {
    CustomerData getCustomerData(String customerId);
    void updateCustomerData(String customerId, CustomerData data);
}

// Real Subject
public class RealTelekomDataService implements TelekomDataService {
    @Override
    public CustomerData getCustomerData(String customerId) {
        // Expensive database operation
        return database.loadCustomer(customerId);
    }
}

// Protection Proxy
public class SecureDataServiceProxy implements TelekomDataService {
    private RealTelekomDataService realService;
    private AccessControlService accessControl;
    
    @Override
    public CustomerData getCustomerData(String customerId) {
        if (!accessControl.hasReadPermission(getCurrentUser(), customerId)) {
            throw new AccessDeniedException("Insufficient permissions");
        }
        
        if (realService == null) {
            realService = new RealTelekomDataService();
        }
        
        return realService.getCustomerData(customerId);
    }
}

// Virtual Proxy für Lazy Loading
public class LazyCustomerDataProxy implements TelekomDataService {
    private String customerId;
    private CustomerData customerData;
    private boolean loaded = false;
    
    @Override
    public CustomerData getCustomerData(String customerId) {
        if (!loaded) {
            this.customerData = database.loadCustomer(customerId);
            this.loaded = true;
        }
        return customerData;
    }
}
```

### Flyweight Pattern: Efficient Icon Management
```java
// Flyweight Interface
public interface NetworkIcon {
    void display(int x, int y, String label);
}

// Concrete Flyweight
public class NetworkDeviceIcon implements NetworkIcon {
    private final String iconType; // Intrinsic state
    private final BufferedImage icon;
    
    public NetworkDeviceIcon(String iconType) {
        this.iconType = iconType;
        this.icon = loadIconImage(iconType);
    }
    
    @Override
    public void display(int x, int y, String label) { // Extrinsic state
        // Render icon at position with label
        graphics.drawImage(icon, x, y);
        graphics.drawString(label, x, y + icon.getHeight() + 5);
    }
}

// Flyweight Factory
public class NetworkIconFactory {
    private static final Map<String, NetworkIcon> icons = new HashMap<>();
    
    public static NetworkIcon getIcon(String iconType) {
        NetworkIcon icon = icons.get(iconType);
        if (icon == null) {
            icon = new NetworkDeviceIcon(iconType);
            icons.put(iconType, icon);
        }
        return icon;
    }
    
    public static int getCreatedIconsCount() {
        return icons.size();
    }
}

// Usage
public class NetworkDiagram {
    private List<NetworkDevice> devices;
    
    public void render() {
        for (NetworkDevice device : devices) {
            NetworkIcon icon = NetworkIconFactory.getIcon(device.getType());
            icon.display(device.getX(), device.getY(), device.getName());
        }
    }
}
```

---

## 4. Bridge Pattern & Microservices Architecture

### Bridge Pattern: Platform-Independent Services
```java
// Implementation Interface (Bridge)
public interface MessageSender {
    void sendMessage(String recipient, String message);
}

// Concrete Implementations
public class EmailSender implements MessageSender {
    @Override
    public void sendMessage(String recipient, String message) {
        // Email-spezifische Implementierung
        emailClient.send(new Email(recipient, "Telekom Notification", message));
    }
}

public class SMSSender implements MessageSender {
    @Override
    public void sendMessage(String recipient, String message) {
        // SMS-spezifische Implementierung
        smsGateway.send(recipient, message);
    }
}

// Abstraction
public abstract class NotificationService {
    protected MessageSender messageSender;
    
    protected NotificationService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
    
    public abstract void sendNotification(String recipient, String message);
}

// Refined Abstractions
public class CustomerNotificationService extends NotificationService {
    public CustomerNotificationService(MessageSender sender) {
        super(sender);
    }
    
    @Override
    public void sendNotification(String recipient, String message) {
        String formattedMessage = "Dear Customer, " + message + " - Telekom";
        messageSender.sendMessage(recipient, formattedMessage);
    }
}

public class TechnicalNotificationService extends NotificationService {
    public TechnicalNotificationService(MessageSender sender) {
        super(sender);
    }
    
    @Override
    public void sendNotification(String recipient, String message) {
        String formattedMessage = "[TECHNICAL] " + message + " - Network Operations";
        messageSender.sendMessage(recipient, formattedMessage);
    }
}
```

### Microservices Architecture Integration
```
Microservices mit Bridge Pattern:

┌────────────────┐    ┌────────────────┐    ┌────────────────┐
│ Notification   │    │   Customer     │    │   Billing      │
│   Service      │    │   Service      │    │   Service      │
├────────────────┤    ├────────────────┤    ├────────────────┤
│   Abstraction  │    │   Abstraction  │    │   Abstraction  │
│      │         │    │      │         │    │      │         │
│   Bridge       │    │   Bridge       │    │   Bridge       │
└──────┼─────────┘    └──────┼─────────┘    └──────┼─────────┘
       │                     │                     │
┌──────▼─────────┐    ┌──────▼─────────┐    ┌──────▼─────────┐
│ Implementation │    │ Implementation │    │ Implementation │
│   Platforms    │    │   Platforms    │    │   Platforms    │
│                │    │                │    │                │
│ - Email        │    │ - Database     │    │ - SAP          │
│ - SMS          │    │ - REST API     │    │ - Oracle       │
│ - Push         │    │ - GraphQL      │    │ - Legacy       │
└────────────────┘    └────────────────┘    └────────────────┘

Benefits:
- Service kann Implementation wechseln ohne Clients zu ändern
- Platform-spezifische Details isoliert
- Testbarkeit durch Mock-Implementations
- Skalierung unabhängig von Implementation
```

---

# BEHAVIORAL PATTERNS (DAY 3)

## 1. Strategy Pattern

### Problem-Motivation: Flexible Billing-Strategien
Telekom benötigt verschiedene Billing-Strategien für unterschiedliche Kunden-Segmente und Tarif-Modelle.

### Strategy Pattern Lösung
```java
// Strategy Interface
public interface BillingStrategy {
    BigDecimal calculateBill(Usage usage, CustomerProfile customer);
    String getStrategyName();
}

// Concrete Strategies
public class PrepaidBillingStrategy implements BillingStrategy {
    @Override
    public BigDecimal calculateBill(Usage usage, CustomerProfile customer) {
        BigDecimal baseRate = new BigDecimal("0.09"); // 9 Cent per minute
        return usage.getMinutesUsed().multiply(baseRate);
    }
    
    @Override
    public String getStrategyName() {
        return "Prepaid Billing";
    }
}

public class PostpaidBillingStrategy implements BillingStrategy {
    @Override
    public BigDecimal calculateBill(Usage usage, CustomerProfile customer) {
        BigDecimal monthlyFee = customer.getTariff().getMonthlyFee();
        BigDecimal overage = calculateOverageCharges(usage, customer.getTariff());
        return monthlyFee.add(overage);
    }
    
    private BigDecimal calculateOverageCharges(Usage usage, Tariff tariff) {
        long exceededMinutes = Math.max(0, usage.getMinutesUsed().longValue() - tariff.getIncludedMinutes());
        return new BigDecimal(exceededMinutes).multiply(tariff.getOverageRate());
    }
}

public class BusinessBillingStrategy implements BillingStrategy {
    @Override
    public BigDecimal calculateBill(Usage usage, CustomerProfile customer) {
        BigDecimal baseBill = new PostpaidBillingStrategy().calculateBill(usage, customer);
        // Business discount
        BigDecimal discount = baseBill.multiply(new BigDecimal("0.15")); // 15% discount
        return baseBill.subtract(discount);
    }
}

// Context
public class BillingContext {
    private BillingStrategy strategy;
    
    public void setBillingStrategy(BillingStrategy strategy) {
        this.strategy = strategy;
    }
    
    public BillingResult generateBill(Usage usage, CustomerProfile customer) {
        if (strategy == null) {
            throw new IllegalStateException("Billing strategy not set");
        }
        
        BigDecimal amount = strategy.calculateBill(usage, customer);
        
        return new BillingResult(
            customer.getCustomerId(),
            amount,
            strategy.getStrategyName(),
            usage.getBillingPeriod()
        );
    }
}

// Strategy Factory
public class BillingStrategyFactory {
    public static BillingStrategy createStrategy(CustomerType customerType, TariffType tariffType) {
        switch (customerType) {
            case BUSINESS:
                return new BusinessBillingStrategy();
            case PRIVATE:
                return tariffType == TariffType.PREPAID ? 
                    new PrepaidBillingStrategy() : 
                    new PostpaidBillingStrategy();
            default:
                throw new IllegalArgumentException("Unknown customer type: " + customerType);
        }
    }
}
```

---

## 2. Template Method Pattern & Observer Pattern

### Template Method: Standardisierte Prozesse
```java
public abstract class CustomerOnboardingProcess {
    
    // Template Method - definiert den Algorithmus
    public final CustomerOnboardingResult onboardCustomer(CustomerApplication application) {
        validateApplication(application);
        
        if (requiresAdditionalDocuments()) {
            requestAdditionalDocuments(application);
        }
        
        CustomerProfile customer = createCustomerProfile(application);
        setupInitialServices(customer);
        
        if (requiresManualReview()) {
            scheduleManualReview(customer);
        }
        
        sendWelcomePackage(customer);
        
        return new CustomerOnboardingResult(customer, LocalDateTime.now());
    }
    
    // Concrete methods
    private void validateApplication(CustomerApplication application) {
        // Standard validation logic
        if (application.getEmail() == null || !isValidEmail(application.getEmail())) {
            throw new InvalidApplicationException("Invalid email address");
        }
    }
    
    private void sendWelcomePackage(CustomerProfile customer) {
        // Standard welcome package
        emailService.sendWelcomeEmail(customer.getEmail());
    }
    
    // Abstract methods - subclasses must implement
    protected abstract CustomerProfile createCustomerProfile(CustomerApplication application);
    protected abstract void setupInitialServices(CustomerProfile customer);
    
    // Hook methods - subclasses can override
    protected boolean requiresAdditionalDocuments() {
        return false;
    }
    
    protected boolean requiresManualReview() {
        return false;
    }
    
    protected void requestAdditionalDocuments(CustomerApplication application) {
        // Default implementation does nothing
    }
    
    protected void scheduleManualReview(CustomerProfile customer) {
        // Default implementation does nothing
    }
}

// Concrete implementations
public class PrivateCustomerOnboarding extends CustomerOnboardingProcess {
    @Override
    protected CustomerProfile createCustomerProfile(CustomerApplication application) {
        return new PrivateCustomerProfile(
            application.getFirstName(),
            application.getLastName(),
            application.getEmail(),
            application.getAddress()
        );
    }
    
    @Override
    protected void setupInitialServices(CustomerProfile customer) {
        // Basic services for private customers
        serviceActivator.activateBasicInternet(customer);
        serviceActivator.setupStandardSupport(customer);
    }
}

public class BusinessCustomerOnboarding extends CustomerOnboardingProcess {
    @Override
    protected CustomerProfile createCustomerProfile(CustomerApplication application) {
        return new BusinessCustomerProfile(
            application.getCompanyName(),
            application.getContactPerson(),
            application.getEmail(),
            application.getBusinessAddress(),
            application.getTaxId()
        );
    }
    
    @Override
    protected void setupInitialServices(CustomerProfile customer) {
        serviceActivator.activateBusinessInternet(customer);
        serviceActivator.setupPremiumSupport(customer);
        serviceActivator.assignAccountManager(customer);
    }
    
    @Override
    protected boolean requiresAdditionalDocuments() {
        return true; // Business customers need additional verification
    }
    
    @Override
    protected boolean requiresManualReview() {
        return true; // All business customers need manual review
    }
    
    @Override
    protected void requestAdditionalDocuments(CustomerApplication application) {
        documentService.requestBusinessVerification(application.getEmail());
    }
    
    @Override
    protected void scheduleManualReview(CustomerProfile customer) {
        reviewService.scheduleBusinessReview(customer.getCustomerId());
    }
}
```

### Observer Pattern: Event-Driven Notifications
```java
// Observer Interface
public interface CustomerEventObserver {
    void onCustomerEvent(CustomerEvent event);
}

// Subject Interface
public interface CustomerEventSubject {
    void addObserver(CustomerEventObserver observer);
    void removeObserver(CustomerEventObserver observer);
    void notifyObservers(CustomerEvent event);
}

// Concrete Subject
public class CustomerService implements CustomerEventSubject {
    private List<CustomerEventObserver> observers = new ArrayList<>();
    
    @Override
    public void addObserver(CustomerEventObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(CustomerEventObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(CustomerEvent event) {
        for (CustomerEventObserver observer : observers) {
            try {
                observer.onCustomerEvent(event);
            } catch (Exception e) {
                logger.error("Error notifying observer", e);
            }
        }
    }
    
    public void activateCustomer(String customerId) {
        // Activate customer logic
        Customer customer = customerRepository.findById(customerId);
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
        
        // Notify observers
        notifyObservers(new CustomerActivatedEvent(customerId, LocalDateTime.now()));
    }
    
    public void suspendCustomer(String customerId, String reason) {
        // Suspend customer logic
        Customer customer = customerRepository.findById(customerId);
        customer.setStatus(CustomerStatus.SUSPENDED);
        customerRepository.save(customer);
        
        // Notify observers
        notifyObservers(new CustomerSuspendedEvent(customerId, reason, LocalDateTime.now()));
    }
}

// Concrete Observers
public class BillingSystemObserver implements CustomerEventObserver {
    private BillingService billingService;
    
    @Override
    public void onCustomerEvent(CustomerEvent event) {
        switch (event.getType()) {
            case CUSTOMER_ACTIVATED:
                billingService.setupBillingAccount(event.getCustomerId());
                break;
            case CUSTOMER_SUSPENDED:
                billingService.suspendBilling(event.getCustomerId());
                break;
        }
    }
}

public class NotificationObserver implements CustomerEventObserver {
    private NotificationService notificationService;
    
    @Override
    public void onCustomerEvent(CustomerEvent event) {
        switch (event.getType()) {
            case CUSTOMER_ACTIVATED:
                notificationService.sendActivationNotification(event.getCustomerId());
                break;
            case CUSTOMER_SUSPENDED:
                CustomerSuspendedEvent suspendedEvent = (CustomerSuspendedEvent) event;
                notificationService.sendSuspensionNotification(
                    event.getCustomerId(), 
                    suspendedEvent.getReason()
                );
                break;
        }
    }
}

public class CRMIntegrationObserver implements CustomerEventObserver {
    private CRMService crmService;
    
    @Override
    public void onCustomerEvent(CustomerEvent event) {
        // Update CRM system with customer status changes
        crmService.updateCustomerStatus(event.getCustomerId(), event.getType());
    }
}
```

---

## 3. Command Pattern

### Problem-Motivation: Network Configuration Commands
Telekom Network Operations Center benötigt flexible Command-Execution mit Undo-Funktionalität, Logging und Batch-Processing.

### Command Pattern Lösung
```java
// Command Interface
public interface NetworkCommand {
    void execute();
    void undo();
    String getDescription();
    boolean isUndoable();
}

// Receiver - Network Device
public class NetworkDevice {
    private String deviceId;
    private String configuration;
    private boolean isOnline;
    
    public void applyConfiguration(String config) {
        logger.info("Applying configuration to device " + deviceId);
        this.configuration = config;
    }
    
    public void reboot() {
        logger.info("Rebooting device " + deviceId);
        this.isOnline = false;
        // Simulate reboot process
        this.isOnline = true;
    }
    
    public void shutdown() {
        logger.info("Shutting down device " + deviceId);
        this.isOnline = false;
    }
    
    public void startup() {
        logger.info("Starting up device " + deviceId);
        this.isOnline = true;
    }
}

// Concrete Commands
public class ConfigureDeviceCommand implements NetworkCommand {
    private NetworkDevice device;
    private String newConfiguration;
    private String previousConfiguration;
    
    public ConfigureDeviceCommand(NetworkDevice device, String newConfiguration) {
        this.device = device;
        this.newConfiguration = newConfiguration;
    }
    
    @Override
    public void execute() {
        previousConfiguration = device.getConfiguration();
        device.applyConfiguration(newConfiguration);
    }
    
    @Override
    public void undo() {
        if (previousConfiguration != null) {
            device.applyConfiguration(previousConfiguration);
        }
    }
    
    @Override
    public String getDescription() {
        return "Configure device " + device.getDeviceId() + " with new configuration";
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
}

public class RebootDeviceCommand implements NetworkCommand {
    private NetworkDevice device;
    
    public RebootDeviceCommand(NetworkDevice device) {
        this.device = device;
    }
    
    @Override
    public void execute() {
        device.reboot();
    }
    
    @Override
    public void undo() {
        // Reboot cannot be undone
        throw new UnsupportedOperationException("Reboot command cannot be undone");
    }
    
    @Override
    public String getDescription() {
        return "Reboot device " + device.getDeviceId();
    }
    
    @Override
    public boolean isUndoable() {
        return false;
    }
}

// Macro Command
public class MaintenanceSequenceCommand implements NetworkCommand {
    private List<NetworkCommand> commands;
    private String description;
    
    public MaintenanceSequenceCommand(String description) {
        this.commands = new ArrayList<>();
        this.description = description;
    }
    
    public void addCommand(NetworkCommand command) {
        commands.add(command);
    }
    
    @Override
    public void execute() {
        for (NetworkCommand command : commands) {
            try {
                command.execute();
            } catch (Exception e) {
                logger.error("Command execution failed: " + command.getDescription(), e);
                throw new CommandExecutionException("Maintenance sequence failed", e);
            }
        }
    }
    
    @Override
    public void undo() {
        // Undo in reverse order
        List<NetworkCommand> reversedCommands = new ArrayList<>(commands);
        Collections.reverse(reversedCommands);
        
        for (NetworkCommand command : reversedCommands) {
            if (command.isUndoable()) {
                try {
                    command.undo();
                } catch (Exception e) {
                    logger.error("Command undo failed: " + command.getDescription(), e);
                }
            }
        }
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public boolean isUndoable() {
        return commands.stream().anyMatch(NetworkCommand::isUndoable);
    }
}

// Invoker - Network Operations Center
public class NetworkOperationsCenter {
    private Stack<NetworkCommand> commandHistory = new Stack<>();
    private Queue<NetworkCommand> commandQueue = new LinkedList<>();
    
    public void executeCommand(NetworkCommand command) {
        try {
            command.execute();
            commandHistory.push(command);
            logger.info("Executed: " + command.getDescription());
        } catch (Exception e) {
            logger.error("Command execution failed: " + command.getDescription(), e);
            throw e;
        }
    }
    
    public void undoLastCommand() {
        if (commandHistory.isEmpty()) {
            logger.warn("No commands to undo");
            return;
        }
        
        NetworkCommand lastCommand = commandHistory.pop();
        
        if (lastCommand.isUndoable()) {
            try {
                lastCommand.undo();
                logger.info("Undone: " + lastCommand.getDescription());
            } catch (Exception e) {
                logger.error("Undo failed: " + lastCommand.getDescription(), e);
                // Put command back in history
                commandHistory.push(lastCommand);
                throw e;
            }
        } else {
            logger.warn("Command is not undoable: " + lastCommand.getDescription());
        }
    }
    
    public void scheduleCommand(NetworkCommand command) {
        commandQueue.offer(command);
        logger.info("Scheduled: " + command.getDescription());
    }
    
    public void executeScheduledCommands() {
        while (!commandQueue.isEmpty()) {
            NetworkCommand command = commandQueue.poll();
            executeCommand(command);
        }
    }
    
    public List<String> getCommandHistory() {
        return commandHistory.stream()
                .map(NetworkCommand::getDescription)
                .collect(Collectors.toList());
    }
}
```

### Usage Example
```java
public class NetworkMaintenanceExample {
    public static void main(String[] args) {
        // Setup
        NetworkDevice router = new NetworkDevice("R001", "Basic Config", true);
        NetworkDevice switch1 = new NetworkDevice("S001", "Switch Config", true);
        NetworkOperationsCenter noc = new NetworkOperationsCenter();
        
        // Create individual commands
        NetworkCommand configRouter = new ConfigureDeviceCommand(router, "Updated Router Config");
        NetworkCommand rebootRouter = new RebootDeviceCommand(router);
        NetworkCommand configSwitch = new ConfigureDeviceCommand(switch1, "Updated Switch Config");
        
        // Create maintenance sequence
        MaintenanceSequenceCommand maintenance = new MaintenanceSequenceCommand("Weekly Maintenance");
        maintenance.addCommand(configRouter);
        maintenance.addCommand(configSwitch);
        maintenance.addCommand(rebootRouter);
        
        // Execute commands
        noc.executeCommand(maintenance);
        
        // Show command history
        System.out.println("Command History:");
        noc.getCommandHistory().forEach(System.out::println);
        
        // Undo if needed
        noc.undoLastCommand();
    }
}
```

---

## 4. State Pattern & Chain of Responsibility

### State Pattern: Customer Status Management
```java
// State Interface
public interface CustomerState {
    void activate(CustomerContext context);
    void suspend(CustomerContext context);
    void terminate(CustomerContext context);
    void reactivate(CustomerContext context);
    String getStateName();
    List<String> getAllowedActions();
}

// Context
public class CustomerContext {
    private CustomerState currentState;
    private String customerId;
    private CustomerData customerData;
    
    public CustomerContext(String customerId) {
        this.customerId = customerId;
        this.currentState = new PendingState(); // Initial state
    }
    
    public void setState(CustomerState state) {
        logger.info("Customer {} state changed from {} to {}", 
                   customerId, currentState.getStateName(), state.getStateName());
        this.currentState = state;
    }
    
    // Delegate actions to current state
    public void activate() { currentState.activate(this); }
    public void suspend() { currentState.suspend(this); }
    public void terminate() { currentState.terminate(this); }
    public void reactivate() { currentState.reactivate(this); }
    
    public String getCurrentState() { return currentState.getStateName(); }
    public List<String> getAvailableActions() { return currentState.getAllowedActions(); }
}

// Concrete States
public class PendingState implements CustomerState {
    @Override
    public void activate(CustomerContext context) {
        // Business logic for activation
        activateCustomerServices(context.getCustomerId());
        context.setState(new ActiveState());
    }
    
    @Override
    public void suspend(CustomerContext context) {
        throw new IllegalStateException("Cannot suspend pending customer");
    }
    
    @Override
    public void terminate(CustomerContext context) {
        // Cancel pending activation
        cancelPendingActivation(context.getCustomerId());
        context.setState(new TerminatedState());
    }
    
    @Override
    public void reactivate(CustomerContext context) {
        throw new IllegalStateException("Cannot reactivate pending customer");
    }
    
    @Override
    public String getStateName() { return "PENDING"; }
    
    @Override
    public List<String> getAllowedActions() {
        return Arrays.asList("activate", "terminate");
    }
}

public class ActiveState implements CustomerState {
    @Override
    public void activate(CustomerContext context) {
        throw new IllegalStateException("Customer is already active");
    }
    
    @Override
    public void suspend(CustomerContext context) {
        suspendCustomerServices(context.getCustomerId());
        context.setState(new SuspendedState());
    }
    
    @Override
    public void terminate(CustomerContext context) {
        terminateCustomerServices(context.getCustomerId());
        context.setState(new TerminatedState());
    }
    
    @Override
    public void reactivate(CustomerContext context) {
        throw new IllegalStateException("Customer is already active");
    }
    
    @Override
    public String getStateName() { return "ACTIVE"; }
    
    @Override
    public List<String> getAllowedActions() {
        return Arrays.asList("suspend", "terminate");
    }
}

public class SuspendedState implements CustomerState {
    @Override
    public void activate(CustomerContext context) {
        throw new IllegalStateException("Use reactivate for suspended customers");
    }
    
    @Override
    public void suspend(CustomerContext context) {
        throw new IllegalStateException("Customer is already suspended");
    }
    
    @Override
    public void terminate(CustomerContext context) {
        terminateCustomerServices(context.getCustomerId());
        context.setState(new TerminatedState());
    }
    
    @Override
    public void reactivate(CustomerContext context) {
        reactivateCustomerServices(context.getCustomerId());
        context.setState(new ActiveState());
    }
    
    @Override
    public String getStateName() { return "SUSPENDED"; }
    
    @Override
    public List<String> getAllowedActions() {
        return Arrays.asList("reactivate", "terminate");
    }
}

public class TerminatedState implements CustomerState {
    @Override
    public void activate(CustomerContext context) {
        throw new IllegalStateException("Cannot activate terminated customer");
    }
    
    @Override
    public void suspend(CustomerContext context) {
        throw new IllegalStateException("Cannot suspend terminated customer");
    }
    
    @Override
    public void terminate(CustomerContext context) {
        throw new IllegalStateException("Customer is already terminated");
    }
    
    @Override
    public void reactivate(CustomerContext context) {
        throw new IllegalStateException("Cannot reactivate terminated customer");
    }
    
    @Override
    public String getStateName() { return "TERMINATED"; }
    
    @Override
    public List<String> getAllowedActions() {
        return Collections.emptyList();
    }
}
```

### Chain of Responsibility: Support Ticket Routing
```java
// Handler Interface
public abstract class SupportHandler {
    protected SupportHandler nextHandler;
    
    public void setNextHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    public abstract void handleRequest(SupportTicket ticket);
    
    protected void passToNext(SupportTicket ticket) {
        if (nextHandler != null) {
            nextHandler.handleRequest(ticket);
        } else {
            ticket.setStatus(TicketStatus.ESCALATED);
            ticket.addNote("No handler found for this ticket type");
        }
    }
}

// Concrete Handlers
public class Level1SupportHandler extends SupportHandler {
    @Override
    public void handleRequest(SupportTicket ticket) {
        if (canHandle(ticket)) {
            ticket.setAssignedTo("Level 1 Support");
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticket.addNote("Assigned to Level 1 Support for basic troubleshooting");
            
            // Simulate handling
            resolveBasicIssue(ticket);
        } else {
            passToNext(ticket);
        }
    }
    
    private boolean canHandle(SupportTicket ticket) {
        return ticket.getPriority() == Priority.LOW && 
               (ticket.getCategory() == Category.ACCOUNT_QUESTION ||
                ticket.getCategory() == Category.BASIC_TECHNICAL);
    }
    
    private void resolveBasicIssue(SupportTicket ticket) {
        // Basic resolution logic
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.addNote("Issue resolved by Level 1 Support");
    }
}

public class Level2TechnicalHandler extends SupportHandler {
    @Override
    public void handleRequest(SupportTicket ticket) {
        if (canHandle(ticket)) {
            ticket.setAssignedTo("Level 2 Technical Support");
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticket.addNote("Escalated to Level 2 Technical Support");
            
            resolveTechnicalIssue(ticket);
        } else {
            passToNext(ticket);
        }
    }
    
    private boolean canHandle(SupportTicket ticket) {
        return ticket.getCategory() == Category.TECHNICAL_ISSUE && 
               ticket.getPriority() != Priority.CRITICAL;
    }
    
    private void resolveTechnicalIssue(SupportTicket ticket) {
        // Technical resolution logic
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.addNote("Technical issue resolved by Level 2 Support");
    }
}

public class SeniorEngineerHandler extends SupportHandler {
    @Override
    public void handleRequest(SupportTicket ticket) {
        if (canHandle(ticket)) {
            ticket.setAssignedTo("Senior Network Engineer");
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticket.addNote("Escalated to Senior Network Engineer");
            
            resolveComplexIssue(ticket);
        } else {
            passToNext(ticket);
        }
    }
    
    private boolean canHandle(SupportTicket ticket) {
        return ticket.getCategory() == Category.NETWORK_OUTAGE || 
               ticket.getPriority() == Priority.CRITICAL;
    }
    
    private void resolveComplexIssue(SupportTicket ticket) {
        // Complex resolution logic
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.addNote("Complex issue resolved by Senior Engineer");
    }
}

public class ExecutiveEscalationHandler extends SupportHandler {
    @Override
    public void handleRequest(SupportTicket ticket) {
        // Executive escalation - handles everything that reaches here
        ticket.setAssignedTo("Executive Support Team");
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.addNote("Escalated to Executive Support Team");
        
        // Notify management
        notifyExecutiveTeam(ticket);
    }
    
    private void notifyExecutiveTeam(SupportTicket ticket) {
        // Executive notification logic
        ticket.addNote("Executive team notified of escalation");
    }
}

// Chain Setup and Usage
public class SupportTicketProcessor {
    private SupportHandler handlerChain;
    
    public SupportTicketProcessor() {
        buildHandlerChain();
    }
    
    private void buildHandlerChain() {
        SupportHandler level1 = new Level1SupportHandler();
        SupportHandler level2 = new Level2TechnicalHandler();
        SupportHandler senior = new SeniorEngineerHandler();
        SupportHandler executive = new ExecutiveEscalationHandler();
        
        level1.setNextHandler(level2);
        level2.setNextHandler(senior);
        senior.setNextHandler(executive);
        
        this.handlerChain = level1;
    }
    
    public void processTicket(SupportTicket ticket) {
        ticket.setStatus(TicketStatus.RECEIVED);
        ticket.addNote("Ticket received and entered into support queue");
        
        handlerChain.handleRequest(ticket);
    }
}
```

---

# ADVANCED PATTERNS (DAY 4)

## 1. Mediator Pattern

### Problem-Motivation: Complex System Integration
Telekom hat viele Systeme die miteinander kommunizieren müssen: CRM, Billing, Inventory, Monitoring. Direkte Kommunikation führt zu tight coupling.

### Mediator Pattern Lösung
```java
// Mediator Interface
public interface SystemMediator {
    void notify(SystemComponent sender, String event, Object data);
}

// Abstract Component
public abstract class SystemComponent {
    protected SystemMediator mediator;
    
    public SystemComponent(SystemMediator mediator) {
        this.mediator = mediator;
    }
}

// Concrete Components
public class CRMSystem extends SystemComponent {
    public CRMSystem(SystemMediator mediator) {
        super(mediator);
    }
    
    public void updateCustomerInfo(String customerId, CustomerData data) {
        // Update CRM
        logger.info("CRM: Updated customer " + customerId);
        
        // Notify other systems through mediator
        mediator.notify(this, "CUSTOMER_UPDATED", new CustomerUpdateEvent(customerId, data));
    }
    
    public void handleCustomerStatusChange(String customerId, String newStatus) {
        logger.info("CRM: Customer " + customerId + " status changed to " + newStatus);
        // Update CRM customer status
    }
}

public class BillingSystem extends SystemComponent {
    public BillingSystem(SystemMediator mediator) {
        super(mediator);
    }
    
    public void processPayment(String customerId, BigDecimal amount) {
        // Process payment
        logger.info("Billing: Processed payment of " + amount + " for customer " + customerId);
        
        // Notify other systems
        mediator.notify(this, "PAYMENT_PROCESSED", new PaymentEvent(customerId, amount));
    }
    
    public void handleCustomerUpdate(CustomerUpdateEvent event) {
        logger.info("Billing: Updating billing info for customer " + event.getCustomerId());
        // Update billing information
    }
}

public class InventorySystem extends SystemComponent {
    public InventorySystem(SystemMediator mediator) {
        super(mediator);
    }
    
    public void reserveEquipment(String customerId, String equipmentType) {
        // Reserve equipment
        logger.info("Inventory: Reserved " + equipmentType + " for customer " + customerId);
        
        mediator.notify(this, "EQUIPMENT_RESERVED", 
                       new EquipmentReservationEvent(customerId, equipmentType));
    }
    
    public void handlePaymentProcessed(PaymentEvent event) {
        logger.info("Inventory: Payment confirmed for customer " + event.getCustomerId());
        // Confirm equipment allocation
    }
}

// Concrete Mediator
public class TelekomSystemMediator implements SystemMediator {
    private CRMSystem crmSystem;
    private BillingSystem billingSystem;
    private InventorySystem inventorySystem;
    private NotificationService notificationService;
    
    public void setCRMSystem(CRMSystem crmSystem) {
        this.crmSystem = crmSystem;
    }
    
    public void setBillingSystem(BillingSystem billingSystem) {
        this.billingSystem = billingSystem;
    }
    
    public void setInventorySystem(InventorySystem inventorySystem) {
        this.inventorySystem = inventorySystem;
    }
    
    @Override
    public void notify(SystemComponent sender, String event, Object data) {
        switch (event) {
            case "CUSTOMER_UPDATED":
                handleCustomerUpdated(sender, (CustomerUpdateEvent) data);
                break;
            case "PAYMENT_PROCESSED":
                handlePaymentProcessed(sender, (PaymentEvent) data);
                break;
            case "EQUIPMENT_RESERVED":
                handleEquipmentReserved(sender, (EquipmentReservationEvent) data);
                break;
            default:
                logger.warn("Unknown event: " + event);
        }
    }
    
    private void handleCustomerUpdated(SystemComponent sender, CustomerUpdateEvent event) {
        // Notify all relevant systems except sender
        if (sender != billingSystem) {
            billingSystem.handleCustomerUpdate(event);
        }
        
        // Check if status change affects other systems
        if (event.isStatusChange()) {
            if (sender != crmSystem) {
                crmSystem.handleCustomerStatusChange(event.getCustomerId(), event.getNewStatus());
            }
        }
        
        // Send notification to customer
        notificationService.sendCustomerUpdateNotification(event.getCustomerId());
    }
    
    private void handlePaymentProcessed(SystemComponent sender, PaymentEvent event) {
        // Update inventory system
        if (sender != inventorySystem) {
            inventorySystem.handlePaymentProcessed(event);
        }
        
        // Send payment confirmation
        notificationService.sendPaymentConfirmation(event.getCustomerId());
    }
    
    private void handleEquipmentReserved(SystemComponent sender, EquipmentReservationEvent event) {
        // Update CRM with equipment info
        if (sender != crmSystem) {
            crmSystem.handleEquipmentReservation(event);
        }
        
        // Send confirmation to customer
        notificationService.sendEquipmentReservationConfirmation(event.getCustomerId());
    }
}
```

---

## 2. Iterator Pattern & Visitor Pattern

### Iterator Pattern: Network Topology Traversal
```java
// Iterator Interface
public interface NetworkIterator<T> {
    boolean hasNext();
    T next();
    void remove();
}

// Aggregate Interface
public interface NetworkTopology<T> {
    NetworkIterator<T> createIterator();
    NetworkIterator<T> createDepthFirstIterator();
    NetworkIterator<T> createBreadthFirstIterator();
}

// Concrete Iterator - Depth First
public class DepthFirstNetworkIterator implements NetworkIterator<NetworkNode> {
    private Stack<NetworkNode> stack;
    private Set<NetworkNode> visited;
    
    public DepthFirstNetworkIterator(NetworkNode root) {
        stack = new Stack<>();
        visited = new HashSet<>();
        if (root != null) {
            stack.push(root);
        }
    }
    
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }
    
    @Override
    public NetworkNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        NetworkNode current = stack.pop();
        visited.add(current);
        
        // Add unvisited children to stack (in reverse order for correct traversal)
        List<NetworkNode> children = current.getConnectedNodes();
        for (int i = children.size() - 1; i >= 0; i--) {
            NetworkNode child = children.get(i);
            if (!visited.contains(child)) {
                stack.push(child);
            }
        }
        
        return current;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported in network traversal");
    }
}

// Concrete Aggregate
public class TelekomNetworkTopology implements NetworkTopology<NetworkNode> {
    private NetworkNode rootNode;
    private List<NetworkNode> allNodes;
    
    @Override
    public NetworkIterator<NetworkNode> createIterator() {
        return createDepthFirstIterator(); // Default
    }
    
    @Override
    public NetworkIterator<NetworkNode> createDepthFirstIterator() {
        return new DepthFirstNetworkIterator(rootNode);
    }
    
    @Override
    public NetworkIterator<NetworkNode> createBreadthFirstIterator() {
        return new BreadthFirstNetworkIterator(rootNode);
    }
}

// Usage Example
public class NetworkAnalysisService {
    public void analyzeNetworkHealth(TelekomNetworkTopology topology) {
        NetworkIterator<NetworkNode> iterator = topology.createDepthFirstIterator();
        
        int totalNodes = 0;
        int healthyNodes = 0;
        
        while (iterator.hasNext()) {
            NetworkNode node = iterator.next();
            totalNodes++;
            
            if (node.getHealthStatus() == HealthStatus.HEALTHY) {
                healthyNodes++;
            }
            
            logger.info("Analyzing node: " + node.getId() + " - Status: " + node.getHealthStatus());
        }
        
        double healthPercentage = (double) healthyNodes / totalNodes * 100;
        logger.info("Network health: " + healthPercentage + "% (" + healthyNodes + "/" + totalNodes + ")");
    }
}
```

### Visitor Pattern: Network Component Operations
```java
// Visitor Interface
public interface NetworkVisitor {
    void visitRouter(Router router);
    void visitSwitch(Switch networkSwitch);
    void visitFirewall(Firewall firewall);
    void visitLoadBalancer(LoadBalancer loadBalancer);
}

// Element Interface
public interface NetworkElement {
    void accept(NetworkVisitor visitor);
    String getId();
    String getType();
}

// Concrete Elements
public class Router implements NetworkElement {
    private String id;
    private String firmwareVersion;
    private List<String> routingTable;
    private int portCount;
    
    @Override
    public void accept(NetworkVisitor visitor) {
        visitor.visitRouter(this);
    }
    
    // Router-specific methods
    public List<String> getRoutingTable() { return routingTable; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public int getPortCount() { return portCount; }
}

public class Switch implements NetworkElement {
    private String id;
    private int vlanCount;
    private List<String> macAddressTable;
    
    @Override
    public void accept(NetworkVisitor visitor) {
        visitor.visitSwitch(this);
    }
    
    // Switch-specific methods
    public int getVlanCount() { return vlanCount; }
    public List<String> getMacAddressTable() { return macAddressTable; }
}

public class Firewall implements NetworkElement {
    private String id;
    private List<SecurityRule> securityRules;
    private int activeConnections;
    
    @Override
    public void accept(NetworkVisitor visitor) {
        visitor.visitFirewall(this);
    }
    
    // Firewall-specific methods
    public List<SecurityRule> getSecurityRules() { return securityRules; }
    public int getActiveConnections() { return activeConnections; }
}

// Concrete Visitors
public class SecurityAuditVisitor implements NetworkVisitor {
    private List<SecurityIssue> securityIssues = new ArrayList<>();
    
    @Override
    public void visitRouter(Router router) {
        // Check router security
        if (router.getFirmwareVersion().compareTo("2.5.0") < 0) {
            securityIssues.add(new SecurityIssue(
                router.getId(), 
                "Outdated firmware version: " + router.getFirmwareVersion(),
                Severity.MEDIUM
            ));
        }
        
        // Check for default passwords, open ports, etc.
        checkDefaultConfiguration(router);
    }
    
    @Override
    public void visitSwitch(Switch networkSwitch) {
        // Check switch security
        if (networkSwitch.getVlanCount() == 1) {
            securityIssues.add(new SecurityIssue(
                networkSwitch.getId(),
                "Single VLAN configuration - consider network segmentation",
                Severity.LOW
            ));
        }
    }
    
    @Override
    public void visitFirewall(Firewall firewall) {
        // Check firewall rules
        List<SecurityRule> rules = firewall.getSecurityRules();
        if (rules.stream().anyMatch(rule -> rule.getAction() == Action.ALLOW_ALL)) {
            securityIssues.add(new SecurityIssue(
                firewall.getId(),
                "Firewall has overly permissive rules",
                Severity.HIGH
            ));
        }
    }
    
    @Override
    public void visitLoadBalancer(LoadBalancer loadBalancer) {
        // Check load balancer configuration
        if (!loadBalancer.isSslTerminationEnabled()) {
            securityIssues.add(new SecurityIssue(
                loadBalancer.getId(),
                "SSL termination not enabled",
                Severity.MEDIUM
            ));
        }
    }
    
    public List<SecurityIssue> getSecurityIssues() {
        return securityIssues;
    }
}

public class PerformanceReportVisitor implements NetworkVisitor {
    private StringBuilder report = new StringBuilder();
    private int deviceCount = 0;
    private double totalUtilization = 0;
    
    @Override
    public void visitRouter(Router router) {
        deviceCount++;
        double utilization = calculateRouterUtilization(router);
        totalUtilization += utilization;
        
        report.append("Router ").append(router.getId())
              .append(": ").append(utilization).append("% utilization")
              .append(" (").append(router.getPortCount()).append(" ports)\n");
    }
    
    @Override
    public void visitSwitch(Switch networkSwitch) {
        deviceCount++;
        double utilization = calculateSwitchUtilization(networkSwitch);
        totalUtilization += utilization;
        
        report.append("Switch ").append(networkSwitch.getId())
              .append(": ").append(utilization).append("% utilization")
              .append(" (").append(networkSwitch.getVlanCount()).append(" VLANs)\n");
    }
    
    @Override
    public void visitFirewall(Firewall firewall) {
        deviceCount++;
        double utilization = (double) firewall.getActiveConnections() / 10000 * 100; // Assuming 10k max
        totalUtilization += utilization;
        
        report.append("Firewall ").append(firewall.getId())
              .append(": ").append(utilization).append("% utilization")
              .append(" (").append(firewall.getActiveConnections()).append(" active connections)\n");
    }
    
    @Override
    public void visitLoadBalancer(LoadBalancer loadBalancer) {
        deviceCount++;
        double utilization = calculateLoadBalancerUtilization(loadBalancer);
        totalUtilization += utilization;
        
        report.append("LoadBalancer ").append(loadBalancer.getId())
              .append(": ").append(utilization).append("% utilization\n");
    }
    
    public String generateReport() {
        double averageUtilization = totalUtilization / deviceCount;
        report.append("\n--- Summary ---\n");
        report.append("Total devices: ").append(deviceCount).append("\n");
        report.append("Average utilization: ").append(String.format("%.2f", averageUtilization)).append("%\n");
        
        return report.toString();
    }
}

// Usage
public class NetworkManagementService {
    public SecurityReport performSecurityAudit(List<NetworkElement> networkElements) {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        
        for (NetworkElement element : networkElements) {
            element.accept(visitor);
        }
        
        return new SecurityReport(visitor.getSecurityIssues());
    }
    
    public String generatePerformanceReport(List<NetworkElement> networkElements) {
        PerformanceReportVisitor visitor = new PerformanceReportVisitor();
        
        for (NetworkElement element : networkElements) {
            element.accept(visitor);
        }
        
        return visitor.generateReport();
    }
}
```

---

## 3. Memento Pattern & Interpreter Pattern

### Memento Pattern: Configuration Snapshots
```java
// Memento
public class ConfigurationMemento {
    private final String configuration;
    private final LocalDateTime timestamp;
    private final String description;
    private final Map<String, Object> metadata;
    
    public ConfigurationMemento(String configuration, String description, Map<String, Object> metadata) {
        this.configuration = configuration;
        this.description = description;
        this.metadata = new HashMap<>(metadata);
        this.timestamp = LocalDateTime.now();
    }
    
    // Package-private - only Originator can access
    String getConfiguration() {
        return configuration;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
}

// Originator
public class NetworkDevice {
    private String deviceId;
    private String currentConfiguration;
    private String firmwareVersion;
    private Map<String, Object> settings;
    
    public NetworkDevice(String deviceId) {
        this.deviceId = deviceId;
        this.settings = new HashMap<>();
    }
    
    // Create memento
    public ConfigurationMemento createSnapshot(String description) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("deviceId", deviceId);
        metadata.put("firmwareVersion", firmwareVersion);
        metadata.put("settings", new HashMap<>(settings));
        
        return new ConfigurationMemento(currentConfiguration, description, metadata);
    }
    
    // Restore from memento
    public void restoreFromSnapshot(ConfigurationMemento memento) {
        this.currentConfiguration = memento.getConfiguration();
        
        Map<String, Object> metadata = memento.getMetadata();
        this.firmwareVersion = (String) metadata.get("firmwareVersion");
        this.settings = new HashMap<>((Map<String, Object>) metadata.get("settings"));
        
        logger.info("Device {} restored from snapshot: {}", deviceId, memento.getDescription());
    }
    
    public void updateConfiguration(String newConfiguration) {
        logger.info("Updating configuration for device {}", deviceId);
        this.currentConfiguration = newConfiguration;
    }
    
    // Getters and setters
    public String getDeviceId() { return deviceId; }
    public String getCurrentConfiguration() { return currentConfiguration; }
}

// Caretaker
public class ConfigurationManager {
    private Map<String, List<ConfigurationMemento>> deviceSnapshots;
    private static final int MAX_SNAPSHOTS_PER_DEVICE = 10;
    
    public ConfigurationManager() {
        this.deviceSnapshots = new HashMap<>();
    }
    
    public void saveSnapshot(String deviceId, ConfigurationMemento snapshot) {
        deviceSnapshots.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(snapshot);
        
        // Keep only the last N snapshots
        List<ConfigurationMemento> snapshots = deviceSnapshots.get(deviceId);
        if (snapshots.size() > MAX_SNAPSHOTS_PER_DEVICE) {
            snapshots.remove(0); // Remove oldest
        }
        
        logger.info("Snapshot saved for device {}: {}", deviceId, snapshot.getDescription());
    }
    
    public ConfigurationMemento getSnapshot(String deviceId, int index) {
        List<ConfigurationMemento> snapshots = deviceSnapshots.get(deviceId);
        if (snapshots == null || index < 0 || index >= snapshots.size()) {
            throw new IllegalArgumentException("Invalid snapshot index for device " + deviceId);
        }
        return snapshots.get(index);
    }
    
    public ConfigurationMemento getLatestSnapshot(String deviceId) {
        List<ConfigurationMemento> snapshots = deviceSnapshots.get(deviceId);
        if (snapshots == null || snapshots.isEmpty()) {
            throw new IllegalArgumentException("No snapshots found for device " + deviceId);
        }
        return snapshots.get(snapshots.size() - 1);
    }
    
    public List<ConfigurationMemento> getAllSnapshots(String deviceId) {
        return new ArrayList<>(deviceSnapshots.getOrDefault(deviceId, Collections.emptyList()));
    }
    
    public void performMaintenanceWithRollback(NetworkDevice device, Runnable maintenanceTask) {
        // Create snapshot before maintenance
        ConfigurationMemento preMaintenanceSnapshot = device.createSnapshot("Pre-maintenance backup");
        saveSnapshot(device.getDeviceId(), preMaintenanceSnapshot);
        
        try {
            // Perform maintenance
            maintenanceTask.run();
            
            // Create post-maintenance snapshot
            ConfigurationMemento postMaintenanceSnapshot = device.createSnapshot("Post-maintenance snapshot");
            saveSnapshot(device.getDeviceId(), postMaintenanceSnapshot);
            
        } catch (Exception e) {
            logger.error("Maintenance failed for device {}. Rolling back...", device.getDeviceId(), e);
            
            // Rollback to pre-maintenance state
            device.restoreFromSnapshot(preMaintenanceSnapshot);
            throw new MaintenanceException("Maintenance failed and was rolled back", e);
        }
    }
}
```

### Interpreter Pattern: Network Query Language
```java
// Abstract Expression
public interface NetworkExpression {
    boolean interpret(NetworkContext context);
}

// Terminal Expressions
public class DeviceTypeExpression implements NetworkExpression {
    private String deviceType;
    
    public DeviceTypeExpression(String deviceType) {
        this.deviceType = deviceType;
    }
    
    @Override
    public boolean interpret(NetworkContext context) {
        return deviceType.equalsIgnoreCase(context.getCurrentDevice().getType());
    }
}

public class LocationExpression implements NetworkExpression {
    private String location;
    
    public LocationExpression(String location) {
        this.location = location;
    }
    
    @Override
    public boolean interpret(NetworkContext context) {
        return location.equalsIgnoreCase(context.getCurrentDevice().getLocation());
    }
}

public class StatusExpression implements NetworkExpression {
    private String status;
    
    public StatusExpression(String status) {
        this.status = status;
    }
    
    @Override
    public boolean interpret(NetworkContext context) {
        return status.equalsIgnoreCase(context.getCurrentDevice().getStatus());
    }
}

// Non-Terminal Expressions
public class AndExpression implements NetworkExpression {
    private NetworkExpression expr1;
    private NetworkExpression expr2;
    
    public AndExpression(NetworkExpression expr1, NetworkExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(NetworkContext context) {
        return expr1.interpret(context) && expr2.interpret(context);
    }
}

public class OrExpression implements NetworkExpression {
    private NetworkExpression expr1;
    private NetworkExpression expr2;
    
    public OrExpression(NetworkExpression expr1, NetworkExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(NetworkContext context) {
        return expr1.interpret(context) || expr2.interpret(context);
    }
}

public class NotExpression implements NetworkExpression {
    private NetworkExpression expression;
    
    public NotExpression(NetworkExpression expression) {
        this.expression = expression;
    }
    
    @Override
    public boolean interpret(NetworkContext context) {
        return !expression.interpret(context);
    }
}

// Context
public class NetworkContext {
    private NetworkDevice currentDevice;
    
    public NetworkContext(NetworkDevice currentDevice) {
        this.currentDevice = currentDevice;
    }
    
    public NetworkDevice getCurrentDevice() {
        return currentDevice;
    }
    
    public void setCurrentDevice(NetworkDevice currentDevice) {
        this.currentDevice = currentDevice;
    }
}

// Query Parser
public class NetworkQueryParser {
    
    public NetworkExpression parse(String query) {
        // Simple parser for demonstration
        // In practice, you'd use a proper parser generator
        
        String[] tokens = query.split(" ");
        return parseTokens(tokens, 0).expression;
    }
    
    private ParseResult parseTokens(String[] tokens, int start) {
        if (start >= tokens.length) {
            throw new IllegalArgumentException("Unexpected end of query");
        }
        
        NetworkExpression left = parseTerm(tokens, start);
        int nextIndex = start + getTermLength(tokens, start);
        
        if (nextIndex < tokens.length) {
            String operator = tokens[nextIndex];
            
            switch (operator.toUpperCase()) {
                case "AND":
                    ParseResult rightAnd = parseTokens(tokens, nextIndex + 1);
                    return new ParseResult(
                        new AndExpression(left, rightAnd.expression), 
                        rightAnd.nextIndex
                    );
                case "OR":
                    ParseResult rightOr = parseTokens(tokens, nextIndex + 1);
                    return new ParseResult(
                        new OrExpression(left, rightOr.expression), 
                        rightOr.nextIndex
                    );
            }
        }
        
        return new ParseResult(left, nextIndex);
    }
    
    private NetworkExpression parseTerm(String[] tokens, int index) {
        String term = tokens[index];
        String value = tokens[index + 1];
        
        switch (term.toUpperCase()) {
            case "TYPE":
                return new DeviceTypeExpression(value);
            case "LOCATION":
                return new LocationExpression(value);
            case "STATUS":
                return new StatusExpression(value);
            case "NOT":
                NetworkExpression negatedExpr = parseTerm(tokens, index + 1);
                return new NotExpression(negatedExpr);
            default:
                throw new IllegalArgumentException("Unknown term: " + term);
        }
    }
    
    private int getTermLength(String[] tokens, int start) {
        if (tokens[start].equalsIgnoreCase("NOT")) {
            return 3; // NOT + term + value
        }
        return 2; // term + value
    }
    
    private static class ParseResult {
        final NetworkExpression expression;
        final int nextIndex;
        
        ParseResult(NetworkExpression expression, int nextIndex) {
            this.expression = expression;
            this.nextIndex = nextIndex;
        }
    }
}

// Query Engine
public class NetworkQueryEngine {
    private NetworkQueryParser parser;
    
    public NetworkQueryEngine() {
        this.parser = new NetworkQueryParser();
    }
    
    public List<NetworkDevice> executeQuery(String query, List<NetworkDevice> devices) {
        NetworkExpression expression = parser.parse(query);
        List<NetworkDevice> results = new ArrayList<>();
        
        for (NetworkDevice device : devices) {
            NetworkContext context = new NetworkContext(device);
            if (expression.interpret(context)) {
                results.add(device);
            }
        }
        
        return results;
    }
}

// Usage Examples
public class NetworkQueryExamples {
    public void demonstrateQueries() {
        NetworkQueryEngine engine = new NetworkQueryEngine();
        List<NetworkDevice> allDevices = loadAllNetworkDevices();
        
        // Find all routers in Munich
        List<NetworkDevice> municRouters = engine.executeQuery(
            "TYPE Router AND LOCATION Munich", allDevices);
        
        // Find all offline devices
        List<NetworkDevice> offlineDevices = engine.executeQuery(
            "STATUS Offline", allDevices);
        
        // Find routers or switches that are online
        List<NetworkDevice> activeRoutersOrSwitches = engine.executeQuery(
            "TYPE Router OR TYPE Switch AND STATUS Online", allDevices);
        
        // Find devices that are NOT firewalls
        List<NetworkDevice> nonFirewalls = engine.executeQuery(
            "NOT TYPE Firewall", allDevices);
    }
}
```

---

## 4. Pattern Integration & Best Practices

### Complex Pattern Combinations
```java
// Combining multiple patterns for robust network management system
public class IntegratedNetworkManagementSystem {
    
    // Singleton for system-wide configuration
    private static volatile IntegratedNetworkManagementSystem instance;
    
    // Mediator for system communication
    private SystemMediator mediator;
    
    // Factory for creating different network components
    private AbstractFactory<NetworkDevice> deviceFactory;
    
    // Strategy for different management approaches
    private ManagementStrategy currentStrategy;
    
    // Observer pattern for event handling
    private List<NetworkEventObserver> observers;
    
    // Command pattern for operations
    private CommandQueue commandQueue;
    
    // Memento for configuration backup
    private ConfigurationManager configManager;
    
    private IntegratedNetworkManagementSystem() {
        initialize();
    }
    
    public static IntegratedNetworkManagementSystem getInstance() {
        if (instance == null) {
            synchronized (IntegratedNetworkManagementSystem.class) {
                if (instance == null) {
                    instance = new IntegratedNetworkManagementSystem();
                }
            }
        }
        return instance;
    }
    
    private void initialize() {
        // Initialize all components
        this.mediator = new TelekomSystemMediator();
        this.observers = new ArrayList<>();
        this.commandQueue = new CommandQueue();
        this.configManager = new ConfigurationManager();
        
        // Setup default strategy
        this.currentStrategy = new StandardManagementStrategy();
        
        // Setup device factory based on environment
        this.deviceFactory = createDeviceFactory();
    }
    
    // Template method for network operation execution
    public final OperationResult executeNetworkOperation(NetworkOperationRequest request) {
        // Pre-operation validation
        validateRequest(request);
        
        // Create snapshot for rollback capability
        ConfigurationMemento preOpSnapshot = createPreOperationSnapshot(request);
        
        try {
            // Execute operation using current strategy
            OperationResult result = currentStrategy.execute(request);
            
            // Notify observers of successful operation
            notifyObservers(new NetworkOperationEvent(request, result, true));
            
            return result;
            
        } catch (Exception e) {
            // Rollback on failure
            rollbackOperation(preOpSnapshot);
            
            // Notify observers of failed operation
            notifyObservers(new NetworkOperationEvent(request, null, false));
            
            throw new NetworkOperationException("Operation failed", e);
        }
    }
    
    // Builder pattern for complex configuration
    public static class ConfigurationBuilder {
        private ManagementStrategy strategy;
        private List<NetworkEventObserver> observers = new ArrayList<>();
        private AbstractFactory<NetworkDevice> deviceFactory;
        
        public ConfigurationBuilder withStrategy(ManagementStrategy strategy) {
            this.strategy = strategy;
            return this;
        }
        
        public ConfigurationBuilder addObserver(NetworkEventObserver observer) {
            this.observers.add(observer);
            return this;
        }
        
        public ConfigurationBuilder withDeviceFactory(AbstractFactory<NetworkDevice> factory) {
            this.deviceFactory = factory;
            return this;
        }
        
        public void applyTo(IntegratedNetworkManagementSystem system) {
            if (strategy != null) {
                system.setManagementStrategy(strategy);
            }
            system.observers.addAll(this.observers);
            if (deviceFactory != null) {
                system.deviceFactory = deviceFactory;
            }
        }
    }
}

// Facade for simplified access to complex system
public class NetworkManagementFacade {
    private IntegratedNetworkManagementSystem managementSystem;
    private NetworkQueryEngine queryEngine;
    private SecurityAuditService securityAudit;
    private PerformanceMonitor performanceMonitor;
    
    public NetworkManagementFacade() {
        this.managementSystem = IntegratedNetworkManagementSystem.getInstance();
        this.queryEngine = new NetworkQueryEngine();
        this.securityAudit = new SecurityAuditService();
        this.performanceMonitor = new PerformanceMonitor();
    }
    
    // High-level operations combining multiple patterns
    public void performComprehensiveHealthCheck(String networkSegment) {
        // Use Command pattern for batch operations
        CompositeCommand healthCheck = new CompositeCommand("Comprehensive Health Check");
        
        // Query devices using Interpreter pattern
        List<NetworkDevice> devices = queryEngine.executeQuery(
            "LOCATION " + networkSegment, getAllNetworkDevices());
        
        // Create commands for each device using Visitor pattern
        HealthCheckVisitor visitor = new HealthCheckVisitor();
        for (NetworkDevice device : devices) {
            device.accept(visitor);
            healthCheck.addCommand(new HealthCheckCommand(device));
        }
        
        // Execute with rollback capability (Memento pattern)
        managementSystem.executeNetworkOperation(
            new BatchOperationRequest(healthCheck));
        
        // Generate report using Template Method
        HealthCheckReport report = generateHealthReport(devices, visitor.getResults());
        
        // Notify observers of completion
        notifyHealthCheckCompletion(report);
    }
    
    public void configureBulkDevices(List<DeviceConfiguration> configurations) {
        // Use Abstract Factory to create appropriate configuration commands
        ConfigurationCommandFactory factory = new ConfigurationCommandFactory();
        
        // Use Chain of Responsibility for validation
        ConfigurationValidationChain validator = new ConfigurationValidationChain();
        
        // Use Command pattern with Memento for rollback
        CompositeCommand bulkConfiguration = new CompositeCommand("Bulk Device Configuration");
        
        for (DeviceConfiguration config : configurations) {
            // Validate configuration
            ValidationResult validation = validator.validate(config);
            if (!validation.isValid()) {
                throw new ConfigurationException("Invalid configuration: " + validation.getErrors());
            }
            
            // Create appropriate command
            Command configCommand = factory.createConfigurationCommand(config);
            bulkConfiguration.addCommand(configCommand);
        }
        
        // Execute with full integration system support
        managementSystem.executeNetworkOperation(
            new BatchOperationRequest(bulkConfiguration));
    }
}
```

### Anti-Patterns to Avoid

#### 1. Pattern Overuse (Golden Hammer)
```java
// DON'T: Over-engineering simple operations
public class SimpleCalculatorFactoryBuilderObserverProxy {
    // 200 lines of code for adding two numbers!
}

// DO: Keep simple operations simple
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

#### 2. Inappropriate Pattern Selection
```java
// DON'T: Singleton for stateful objects
public class CustomerDataSingleton {
    private static CustomerDataSingleton instance;
    private String currentCustomerId; // State = bad for Singleton!
}

// DO: Use Singleton only for stateless services
public class ConfigurationServiceSingleton {
    private static volatile ConfigurationServiceSingleton instance;
    
    public String getConfigValue(String key) {
        return properties.getProperty(key);
    }
}
```

#### 3. Pattern Mixing Without Design
```java
// DON'T: Random pattern combination
public class MixedPatternMess implements Observer, Command, Strategy {
    // Implements multiple patterns without clear design intent
}

// DO: Thoughtful pattern integration with clear responsibilities
public class NetworkManagementService {
    private ObserverManager observers;      // Event handling
    private CommandQueue commands;          // Operation queuing
    private ManagementStrategy strategy;    // Algorithm selection
    
    // Each pattern has clear purpose and boundaries
}
```

---

# REFERENCE MATERIALS

## Clean Code Principles Summary

### DRY (Don't Repeat Yourself)
```java
// BAD - Validation logic duplicated
public void activateContract(String customerId) {
    if (customerId == null || customerId.trim().isEmpty() || !customerId.matches("\\d{10}")) {
        throw new IllegalArgumentException("Invalid customer ID");
    }
    // ... activation logic
}

// GOOD - Extracted validation
public class CustomerValidator {
    public void validateCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty() || !customerId.matches("\\d{10}")) {
            throw new IllegalArgumentException("Invalid customer ID");
        }
    }
}
```

### SOLID Principles in Practice

#### Single Responsibility Principle (SRP)
```java
// BAD - Multiple responsibilities
public class CustomerManager {
    public void saveCustomer(Customer customer) { /* database logic */ }
    public void sendEmail(Customer customer) { /* email logic */ }
    public void validateCustomer(Customer customer) { /* validation logic */ }
}

// GOOD - Separated responsibilities
public class CustomerService {
    private CustomerRepository repository;
    private EmailService emailService;
    private CustomerValidator validator;
}
```

#### Open/Closed Principle (OCP)
```java
// BAD - Modification required for new payment types
public class PaymentProcessor {
    public void processPayment(String type, double amount) {
        if ("CREDIT".equals(type)) {
            // credit card logic
        } else if ("DEBIT".equals(type)) {
            // debit card logic
        }
        // Adding new type requires modifying this method
    }
}

// GOOD - Open for extension, closed for modification
public interface PaymentMethod {
    void process(double amount);
}

public class PaymentProcessor {
    public void processPayment(PaymentMethod method, double amount) {
        method.process(amount);  // No modification needed for new types
    }
}
```

## Architecture Patterns Quick Reference

### Layered Architecture
```
┌─────────────────┐
│  Presentation   │ ← User Interface
├─────────────────┤
│    Business     │ ← Domain Logic  
├─────────────────┤
│   Persistence   │ ← Data Access
├─────────────────┤
│    Database     │ ← Data Storage
└─────────────────┘

✓ Simple and well-understood
✓ Good separation of concerns
✗ Monolithic deployment
✗ Performance bottlenecks
```

### Microservices Architecture
```
┌─────────┐  ┌─────────┐  ┌─────────┐
│Service A│  │Service B│  │Service C│
├─────────┤  ├─────────┤  ├─────────┤  
│  DB A   │  │  DB B   │  │  DB C   │
└─────────┘  └─────────┘  └─────────┘

✓ Independent scalability
✓ Technology diversity
✓ Fault isolation
✗ Distributed complexity
✗ Network latency
✗ Data consistency challenges
```

### Event-Driven Architecture
```
Producers → Event Bus → Consumers
    ↑           ↑           ↑
 Services   Message     Services
            Queue

✓ Loose coupling
✓ Asynchronous processing
✓ Event replay capability
✗ Eventually consistent
✗ Complex error handling
✗ Event schema evolution
```

## Pattern Categories & When to Use

### Creational Patterns - Object Creation
| Pattern | Use When | Example |
|---------|----------|---------|
| **Factory Method** | Creating objects without specifying exact class | Customer types (Private, Business) |
| **Abstract Factory** | Families of related objects | Service suites per channel (Web, Mobile) |
| **Builder** | Complex object construction | Multi-step configuration building |
| **Prototype** | Cloning expensive-to-create objects | Network device configurations |
| **Singleton** | Single instance required globally | Configuration manager |

### Structural Patterns - Object Composition
| Pattern | Use When | Example |
|---------|----------|---------|
| **Decorator** | Adding behavior dynamically | Service enhancements (logging, caching) |
| **Adapter** | Integrating incompatible interfaces | Legacy system integration |
| **Facade** | Simplifying complex subsystem | Unified network management API |
| **Composite** | Tree-like structures | Network topology hierarchy |
| **Proxy** | Controlling access to objects | Security, lazy loading, caching |
| **Bridge** | Separating abstraction from implementation | Platform-independent services |
| **Flyweight** | Managing large numbers of fine-grained objects | UI icons, cached data |

### Behavioral Patterns - Object Interaction
| Pattern | Use When | Example |
|---------|----------|---------|
| **Strategy** | Algorithm variations at runtime | Billing strategies (Prepaid, Postpaid) |
| **Observer** | One-to-many notifications | Event-driven system updates |
| **Command** | Queuing, logging, undo operations | Network configuration commands |
| **State** | Object behavior changes with state | Customer lifecycle management |
| **Template Method** | Algorithm skeleton with variations | Standardized processes |
| **Chain of Responsibility** | Processing chain with multiple handlers | Support ticket routing |
| **Mediator** | Complex communication between objects | System integration hub |
| **Iterator** | Sequential access to collection | Network topology traversal |
| **Visitor** | Operations on object structures | Network device analysis |
| **Memento** | Capturing and restoring object state | Configuration snapshots |
| **Interpreter** | Language or expression evaluation | Network query language |

## Anti-Patterns to Avoid

### Common Anti-Patterns
1. **Golden Hammer**: Using the same pattern for every problem
2. **Pattern Overload**: Too many patterns in simple scenarios
3. **Premature Patterning**: Applying patterns before understanding the problem
4. **God Object**: One class doing everything
5. **Copy-Paste Programming**: Duplicating code instead of extracting patterns
6. **Magic Numbers/Strings**: Hardcoded values without explanation
7. **Long Parameter List**: Too many method parameters
8. **Feature Envy**: Methods using more data from other classes than their own

### Pattern-Specific Anti-Patterns
- **Singleton Overuse**: Using Singleton for stateful objects
- **Factory Overkill**: Factory pattern for simple object creation
- **Observer Memory Leaks**: Not removing observers properly
- **Command Queue Overflow**: Unbounded command queues
- **Strategy Explosion**: Too many strategy classes for simple variations

---

# CONCLUSION & NEXT STEPS

## Key Takeaways

### Design Patterns are Solutions, Not Goals
- Patterns solve recurring design problems
- Apply patterns when the problem actually exists
- Simple problems need simple solutions
- Patterns improve communication between developers

### Fachlichkeit vor Technik
- Understand the domain before choosing technology
- Business requirements drive architectural decisions
- Technology should support, not dictate, business logic

### Clean Code Enables Patterns
- Clean code makes patterns recognizable
- Refactoring reveals pattern opportunities
- Boy Scout Rule: Leave code better than you found it
- Patterns emerge through iterative improvement

### Architecture Serves Business Needs
- Start simple and evolve as needed
- Consider team size and domain complexity
- Balance technical excellence with practical constraints
- Measure architecture success by business value delivered

## Implementation Strategy

### 1. Assessment Phase
- Audit current codebase for anti-patterns
- Identify recurring design problems
- Assess team pattern knowledge
- Establish baseline quality metrics

### 2. Education Phase
- Team training on core patterns
- Code review guidelines incorporating patterns
- Pattern library and examples
- Mentoring and knowledge sharing

### 3. Gradual Application
- Start with most pressing problems
- Apply Boy Scout Rule consistently
- Refactor toward patterns, don't force them
- Document pattern decisions and rationale

### 4. Continuous Improvement
- Regular architecture reviews
- Pattern effectiveness measurement
- Team feedback and adaptation
- Evolve patterns based on experience

## Recommended Reading & Resources

### Essential Books
- **"Design Patterns: Elements of Reusable Object-Oriented Software"** - Gang of Four
- **"Clean Code: A Handbook of Agile Software Craftsmanship"** - Robert C. Martin
- **"Clean Architecture: A Craftsman's Guide to Software Structure and Design"** - Robert C. Martin
- **"Domain-Driven Design: Tackling Complexity in the Heart of Software"** - Eric Evans
- **"Patterns of Enterprise Application Architecture"** - Martin Fowler
- **"Building Microservices: Designing Fine-Grained Systems"** - Sam Newman

### Online Resources
- **Refactoring.guru** - Interactive pattern explanations
- **Martin Fowler's Blog** - Architecture and design insights
- **Clean Code Blog** - Uncle Bob's ongoing wisdom
- **ThoughtWorks Technology Radar** - Industry trend analysis

### Tools & Practices
- **Static Analysis**: SonarQube, SpotBugs, PMD
- **Architecture Documentation**: C4 Model, Architecture Decision Records
- **Refactoring Tools**: IDE support, automated refactoring
- **Continuous Learning**: Code reviews, pair programming, architecture reviews

---

*This comprehensive training content combines theoretical foundations with practical Telekom-specific examples, providing a complete resource for Design Patterns and Architecture training.*