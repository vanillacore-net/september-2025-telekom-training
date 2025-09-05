# Modul 2: Abstract Factory & Layered Architecture

## Lernziele
- Abstract Factory Pattern in geschichteten Architekturen anwenden
- Service-Familien sauber strukturieren
- Dependency Injection Prinzipien verstehen
- Interface Segregation in der Praxis umsetzen

## 1. Problem-Motivation

### Ausgangssituation: Multi-Channel Service Platform
Telekom betreibt verschiedene Service-Kanäle (Web, Mobile App, Call Center, Partner-Portal). Jeder Kanal benötigt unterschiedliche Service-Implementierungen, aber ähnliche Funktionalitäten.

### Herausforderungen
- Verschiedene Authentifizierung pro Kanal
- Unterschiedliche Datenformate und APIs
- Kanal-spezifische Geschäftsregeln
- Einheitliche Service-Schnittstelle gewünscht

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
            
            // Web-API Aufruf
            WebCustomerAPI webAPI = new WebCustomerAPI();
            return webAPI.getCustomerData(customerId);
            
        } else if ("MOBILE".equals(channel)) {
            // Mobile-spezifische OAuth
            MobileOAuthService mobileAuth = new MobileOAuthService();
            if (!mobileAuth.validateToken(getCurrentToken())) {
                throw new AuthenticationException();
            }
            
            // Mobile-API mit anderen Datenformaten
            MobileCustomerAPI mobileAPI = new MobileCustomerAPI();
            return transformMobileResponse(mobileAPI.getCustomer(customerId));
            
        } else if ("CALLCENTER".equals(channel)) {
            // Call Center Mitarbeiter-Authentifizierung
            EmployeeAuthService empAuth = new EmployeeAuthService();
            if (!empAuth.validateEmployee(getCurrentEmployee())) {
                throw new AuthenticationException();
            }
            
            // Legacy Backend-System
            LegacyCustomerSystem legacy = new LegacyCustomerSystem();
            return transformLegacyData(legacy.retrieveCustomer(customerId));
        }
        
        throw new IllegalArgumentException("Unsupported channel: " + channel);
    }
}
```

### Code-Smell Analyse
1. **God Object**: Eine Klasse kennt alle Implementierungen
2. **Switch Statement**: Kanal-basierte Verzweigung
3. **Tight Coupling**: Direkte Abhängigkeiten zu konkreten Implementierungen
4. **Interface Segregation Verletzung**: Ein Service für alle Kanäle
5. **Open/Closed Principle Verletzung**: Neue Kanäle erfordern Änderungen

## 2. Abstract Factory Pattern - Struktur

### Konzept
Abstract Factory stellt eine Schnittstelle bereit, um Familien verwandter Objekte zu erstellen, ohne deren konkrete Klassen zu spezifizieren.

### UML-Struktur
```
AbstractFactory
  +createServiceA(): AbstractServiceA
  +createServiceB(): AbstractServiceB

ConcreteFactory1 extends AbstractFactory
  +createServiceA(): ConcreteServiceA1
  +createServiceB(): ConcreteServiceB1

ConcreteFactory2 extends AbstractFactory  
  +createServiceA(): ConcreteServiceA2
  +createServiceB(): ConcreteServiceB2
```

### Telekom Service-Familien
```
Channel Factory
├── Authentication Service
├── Customer Service  
├── Billing Service
└── Notification Service
```

## 3. Layered Architecture Integration

### Schichtenmodell
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

## 4. Refactoring-Lösung

### Schritt 1: Service-Abstrakte definieren

```java
// Authentication Service Familie
public interface AuthenticationService {
    boolean authenticate(AuthenticationContext context);
    void logout(String sessionId);
}

// Customer Service Familie  
public interface CustomerService {
    CustomerData getCustomer(String customerId);
    List<CustomerData> searchCustomers(SearchCriteria criteria);
    void updateCustomer(CustomerData customer);
}

// Billing Service Familie
public interface BillingService {
    BillingInfo getBillingInfo(String customerId);
    List<Invoice> getInvoices(String customerId, DateRange range);
}

// Notification Service Familie
public interface NotificationService {
    void sendNotification(String customerId, String message);
    boolean isChannelAvailable(String customerId);
}
```

### Schritt 2: Abstract Factory definieren

```java
public abstract class ChannelServiceFactory {
    
    // Factory Methods für Service-Familie
    public abstract AuthenticationService createAuthenticationService();
    public abstract CustomerService createCustomerService();
    public abstract BillingService createBillingService();
    public abstract NotificationService createNotificationService();
    
    // Convenience Method für komplette Service-Suite
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

### Schritt 3: Konkrete Factories für jeden Kanal

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
    
    @Override
    public NotificationService createNotificationService() {
        return new WebNotificationService();
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
    
    @Override
    public BillingService createBillingService() {
        return new MobileBillingService();
    }
    
    @Override
    public NotificationService createNotificationService() {
        return new PushNotificationService();
    }
}
```

### Schritt 4: Service-Suite für einheitliche Nutzung

```java
public class ChannelServiceSuite {
    private final AuthenticationService authService;
    private final CustomerService customerService;
    private final BillingService billingService;
    private final NotificationService notificationService;
    
    public ChannelServiceSuite(AuthenticationService authService,
                              CustomerService customerService,
                              BillingService billingService,
                              NotificationService notificationService) {
        this.authService = authService;
        this.customerService = customerService;
        this.billingService = billingService;
        this.notificationService = notificationService;
    }
    
    // Geschäftslogik-Methoden die Service-Familie nutzen
    public CustomerProfile getCompleteCustomerProfile(String customerId, 
                                                     AuthenticationContext auth) {
        // 1. Authentifizierung
        if (!authService.authenticate(auth)) {
            throw new AuthenticationException("Authentication failed");
        }
        
        // 2. Kundendaten laden
        CustomerData customer = customerService.getCustomer(customerId);
        
        // 3. Billing-Info hinzufügen
        BillingInfo billing = billingService.getBillingInfo(customerId);
        
        // 4. Benachrichtigungsstatus prüfen
        boolean notificationsEnabled = notificationService.isChannelAvailable(customerId);
        
        return new CustomerProfile(customer, billing, notificationsEnabled);
    }
}
```

## 5. Konfiguration und Dependency Injection

### Factory Provider Pattern
```java
@Component
public class ChannelFactoryProvider {
    
    private final Map<String, ChannelServiceFactory> factories;
    
    public ChannelFactoryProvider() {
        factories = new HashMap<>();
        factories.put("WEB", new WebChannelFactory());
        factories.put("MOBILE", new MobileChannelFactory());
        factories.put("CALLCENTER", new CallCenterChannelFactory());
        factories.put("PARTNER", new PartnerChannelFactory());
    }
    
    public ChannelServiceFactory getFactory(String channel) {
        ChannelServiceFactory factory = factories.get(channel);
        if (factory == null) {
            throw new UnsupportedChannelException("Channel not supported: " + channel);
        }
        return factory;
    }
    
    public Set<String> getSupportedChannels() {
        return factories.keySet();
    }
}
```

### Spring Integration Beispiel
```java
@Configuration
public class ServiceFactoryConfiguration {
    
    @Bean
    @Qualifier("webFactory")
    public ChannelServiceFactory webChannelFactory() {
        return new WebChannelFactory();
    }
    
    @Bean
    @Qualifier("mobileFactory")
    public ChannelServiceFactory mobileChannelFactory() {
        return new MobileChannelFactory();
    }
    
    @Bean
    public ChannelFactoryProvider factoryProvider(
            @Qualifier("webFactory") ChannelServiceFactory webFactory,
            @Qualifier("mobileFactory") ChannelServiceFactory mobileFactory) {
        
        return new ChannelFactoryProvider(Map.of(
            "WEB", webFactory,
            "MOBILE", mobileFactory
        ));
    }
}
```

## 6. SOLID-Prinzipien Integration

### Single Responsibility Principle (SRP)
- Jede konkrete Factory ist nur für einen Kanal zuständig
- Services kapseln kanal-spezifische Implementierung

### Open/Closed Principle (OCP)
- Neue Kanäle durch neue Factory-Implementierungen
- Bestehende Geschäftslogik bleibt unverändert

### Interface Segregation Principle (ISP)
- Getrennte Service-Interfaces statt einer großen Service-Klasse
- Clients abhängig nur von benötigten Interfaces

### Dependency Inversion Principle (DIP)
- Abhängigkeit zu Abstract Factory, nicht zu konkreten Implementierungen
- Service-Implementierungen austauschbar

## 7. Anti-Pattern Vermeidung

### Simple Factory vs Abstract Factory
**Anti-Pattern: Zentrale Factory mit Switch**
```java
// Vermeiden
public class ServiceFactory {
    public Object createService(String channel, String serviceType) {
        switch (channel + "_" + serviceType) {
            case "WEB_AUTH": return new WebAuthService();
            case "WEB_CUSTOMER": return new WebCustomerService();
            // ... 20+ weitere Cases
        }
    }
}
```

**Korrekt: Abstract Factory mit Service-Familien**
- Zusammengehörige Services in einer Factory
- Typ-sichere Service-Erstellung

### Über-Abstraktion vermeiden
**Anti-Pattern: Factory für jede Klasse**
```java
// Übertrieben - nicht jede Klasse braucht eigene Factory
public interface StringBuilderFactory {
    StringBuilder createStringBuilder();
}
```

**Regel**: Abstract Factory nur bei echten Service-Familien verwenden

## 8. Performance und Skalierung

### Factory Caching
```java
public class CachedChannelFactory extends ChannelServiceFactory {
    private final Map<Class<?>, Object> serviceCache = new ConcurrentHashMap<>();
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createService(Class<T> serviceType) {
        return (T) serviceCache.computeIfAbsent(serviceType, this::instantiateService);
    }
    
    private Object instantiateService(Class<?> serviceType) {
        // Service-Instanz erstellen (teuer)
        return ServiceInstantiator.create(serviceType);
    }
}
```

### Lazy Initialization
```java
public class LazyWebChannelFactory extends ChannelServiceFactory {
    private volatile CustomerService customerService;
    
    @Override
    public CustomerService createCustomerService() {
        if (customerService == null) {
            synchronized (this) {
                if (customerService == null) {
                    customerService = new WebCustomerService();
                }
            }
        }
        return customerService;
    }
}
```

## 9. Diskussionspunkte

### Architektur-Diskussion
1. **Granularität der Service-Familien**
   - Wie viele Services gehören in eine Familie?
   - Wie organisiert man große Service-Landschaften?

2. **Factory Lifecycle Management**
   - Singleton vs. Prototype Scope für Factories?
   - Wie handled man Factory-Konfigurationsänderungen zur Laufzeit?

3. **Testbarkeit**
   - Mock-Factories für Unit Tests
   - Integration Tests mit verschiedenen Factory-Implementierungen

### Telekom-spezifische Herausforderungen
1. **Legacy-System Integration**
   - Wie wrapper man alte APIs in moderne Service-Interfaces?
   - Migration-Strategien für bestehende Kanäle

2. **Monitoring und Observability**
   - Factory-spezifische Metriken
   - Kanal-übergreifende Service-Monitoring

## 10. Praktische Übung (25 Minuten)

### Aufgabe: Tarif-Management Service-Familie

**Szenario:** Verschiedene Kanäle benötigen unterschiedliche Tarif-Services:
- **Web**: RESTful API mit JSON
- **Call Center**: SOAP API mit XML  
- **Mobile**: GraphQL API
- **Partner**: B2B API mit eigenen Datenformaten

### Gegeben: Problematischer Service
```java
public class TariffService {
    public List<Tariff> getTariffs(String channel, String customerId) {
        if ("WEB".equals(channel)) {
            // REST API Aufruf
            RestTemplate restTemplate = new RestTemplate();
            // ... 15 Zeilen Code
        } else if ("CALLCENTER".equals(channel)) {
            // SOAP API Aufruf  
            SOAPConnector soapConnector = new SOAPConnector();
            // ... 20 Zeilen Code
        }
        // ... weitere Kanäle
    }
}
```

### Aufgaben
1. **Service-Familie Design** (10 Min)
   - Definieren Sie die Abstract Factory für Tariff-Services
   - Identifizieren Sie die Service-Interfaces
   - Skizzieren Sie 2 konkrete Factory-Implementierungen

2. **Implementation** (10 Min)
   - Implementieren Sie WebTariffFactory vollständig
   - Implementieren Sie TariffService Interface

3. **Integration** (5 Min)
   - Wie würden Sie die Factory in bestehende Controller integrieren?
   - Welche Dependency Injection Strategie wählen Sie?

### Lösungsansatz
```java
public interface TariffService {
    List<Tariff> getAvailableTariffs(String customerId);
    TariffDetails getTariffDetails(String tariffId);
    boolean isEligibleForTariff(String customerId, String tariffId);
}

public interface TariffComparisonService {
    TariffComparison compareTariffs(List<String> tariffIds);
    List<String> recommendTariffs(String customerId);
}

public abstract class TariffServiceFactory {
    public abstract TariffService createTariffService();
    public abstract TariffComparisonService createTariffComparisonService();
    
    // Template method für kompletten Service-Setup
    public TariffServiceSuite createTariffSuite() {
        return new TariffServiceSuite(
            createTariffService(),
            createTariffComparisonService()
        );
    }
}
```

## 11. Testing Patterns

### Factory Testing
```java
@Test
public void shouldCreateConsistentServiceFamily() {
    // Arrange
    ChannelServiceFactory factory = new WebChannelFactory();
    
    // Act
    ChannelServiceSuite suite = factory.createServiceSuite();
    
    // Assert
    assertThat(suite.getAuthenticationService())
        .isInstanceOf(WebSessionAuthService.class);
    assertThat(suite.getCustomerService())
        .isInstanceOf(WebCustomerService.class);
    
    // Service-Familie sollte zusammen funktionieren
    AuthenticationContext context = createValidWebContext();
    assertTrue(suite.authenticateAndGetCustomer("12345", context));
}
```

### Mock Factory für Unit Tests
```java
public class MockChannelFactory extends ChannelServiceFactory {
    @Override
    public CustomerService createCustomerService() {
        return Mockito.mock(CustomerService.class);
    }
    
    // ... weitere Mock Services
}
```

## 12. Zusammenfassung

### Schlüsselerkenntnisse
- Abstract Factory strukturiert Service-Familien sauber
- Layered Architecture profitiert von konsistenten Service-Interfaces
- Interface Segregation verbessert Testbarkeit und Wartbarkeit
- Dependency Injection macht Factory-Pattern flexibel

### Vorteile
- Konsistente Service-APIs über alle Kanäle
- Einfache Erweiterung um neue Kanäle
- Bessere Testbarkeit durch Service-Isolation
- Klare Trennung zwischen Kanal-Logik und Geschäftslogik

### Nachteile
- Mehr Klassen und Interfaces
- Anfängliche Komplexität höher
- Über-Engineering bei einfachen Szenarien möglich

### Nächste Schritte
- Im nächsten Modul: Builder Pattern für komplexe Repository-Konfigurationen
- Kombination von Abstract Factory mit Dependency Injection Containern
- Integration in Microservice-Architekturen