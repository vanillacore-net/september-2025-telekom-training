---
title: Design Patterns Workshop - Tag 1
description: Code-Analyse, Factory Method, Abstract Factory, Builder & Prototype Patterns
tags: design-patterns, workshop, telekom, architecture, training, day1, factory, builder, prototype
slideOptions:
  theme: white
  transition: slide
  backgroundTransition: fade
  center: false
  progress: true
  controls: true
  mouseWheel: false
  history: true
  keyboard: true
  overview: true
  touch: true
  fragments: true
  width: 1920
  height: 1080
  margin: 0.05
  minScale: 0.5
  maxScale: 2.0
---

<style>
/* HedgeDoc Presentation Styles */
.reveal {
  font-family: 'Open Sans', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-weight: 300;
}

/* Critical: Fix content overflow and enforce left alignment */
.reveal .slides {
  font-size: 26px !important; /* Increased by 20% for better readability (22px * 1.20) */
  line-height: 1.3 !important;
}

/* Override reveal.js center alignment - force left alignment for all content */
.reveal .slides section,
.reveal .slides section > *,
.reveal .center {
  text-align: left !important;
}

.reveal .slides section {
  height: 100%;
  width: 100%;
  max-width: 100vw;
  overflow-y: auto !important; /* Allow scrolling if needed */
  overflow-x: hidden;
  padding: 20px 25px 25px 25px !important; /* Reduce top padding to move content up */
  box-sizing: border-box;
  text-align: left !important; /* Ensure all content is left-aligned */
  display: flex !important;
  flex-direction: column !important;
  justify-content: flex-start !important; /* Move content to top */
}

.reveal h1 {
  font-size: 2.16em !important; /* Increased by 20% (1.8em * 1.20) */
  color: #2c2c2c;
  font-weight: 600 !important;
  text-align: left !important;
  margin-top: 0.8em !important;
  margin-bottom: 0.3em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h1:first-child {
  margin-top: 0 !important;
}

.reveal h2 {
  font-size: 1.68em !important; /* Increased by 20% (1.4em * 1.20) */
  color: #2c2c2c;
  font-weight: 500 !important;
  text-align: left !important;
  margin-top: 0.7em !important;
  margin-bottom: 0.4em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h2:first-child {
  margin-top: 0 !important;
}

.reveal h3 {
  font-size: 1.44em !important; /* Increased by 20% (1.2em * 1.20) */
  font-weight: 400 !important;
  text-align: left !important;
  margin-top: 0.6em !important;
  margin-bottom: 0.3em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h3:first-child {
  margin-top: 0 !important;
}

.reveal h4, .reveal h5, .reveal h6 {
  font-weight: 400 !important;
  text-align: left !important;
  margin-top: 0.5em !important;
  margin-bottom: 0.3em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h4:first-child,
.reveal .slides section > h5:first-child,
.reveal .slides section > h6:first-child {
  margin-top: 0 !important;
}

.reveal p, .reveal li {
  font-weight: 300 !important;
  text-align: left !important;
}

/* Add proper paragraph spacing */
.reveal p {
  margin-bottom: 0.3em !important;
}

/* Prevent text from being too large */
.reveal .slides {
  max-width: 100%;
  max-height: 100%;
}

/* Lists should not overflow */
.reveal ul, .reveal ol {
  max-width: 90%;
  margin-left: 0 !important;
  padding-left: 1.5em !important;
  list-style-type: none;
  margin-bottom: 0.3em !important;
}

/* Add spacing between list items for better readability */
.reveal ul li, .reveal ol li {
  margin-bottom: 0.3em !important;
}

.reveal ul li:last-child, .reveal ol li:last-child {
  margin-bottom: 0 !important;
}

.reveal ul li::before {
  content: "▸";
  color: #666666;
  font-weight: 400;
  display: inline-block;
  width: 1em;
  margin-left: -1em;
}

/* Code blocks sizing */
.reveal pre {
  font-size: 0.85em !important; /* Further increased for better readability */
  max-height: 940px; /* Scaled for FHD (500px * 1.875) */
  overflow: auto !important;
  background: #f8f8f8 !important; /* Light gray background */
  color: #333 !important; /* Dark text for contrast */
  border: 1px solid #e0e0e0; /* Subtle border */
}

.reveal .two-column {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
}

.reveal .two-column > div {
  flex: 1;
}

.workshop-header {
  text-align: center;
  background: #ffffff;
  color: #333;
  padding: 75px; /* Scaled for FHD (40px * 1.875) */
  margin: -38px; /* Scaled for FHD (-20px * 1.875) */
  border-radius: 15px; /* Scaled for FHD (8px * 1.875) */
}

.workshop-header h1,
.workshop-header h2 {
  color: #333;
  text-shadow: none;
}

.pattern-definition {
  background-color: #F5F5F5;
  border-left: 8px solid #666666; /* Scaled for FHD (4px * 2) */
  padding: 25px; /* Scaled for FHD (20px * 1.875) */
  margin: 38px 0; /* Scaled for FHD (20px * 1.875) */
  border-radius: 8px; /* Scaled for FHD (4px * 2) */
}

.highlight-box {
  background-color: #F5F5F5;
  border-left: 4px solid #666666;
  padding: 20px;
  margin: 20px 0;
  border-radius: 4px;
}

.highlight-box.warning {
  border-left-color: #8B0000;
  background-color: #F5F5DC;
}

.highlight-box.accent {
  border-left-color: #4a4a4a;
  background-color: #F5F5F5;
}

.code-example {
  background: #f5f5f5;
  color: #333;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
  font-size: 1.1em; /* Increased font size for better readability */
}

.code-example h5 {
  color: #333;
  margin-top: 0;
  font-weight: 600;
}

.interactive-question {
  background-color: #F5F5F5;
  border: 2px solid #666666;
  padding: 20px;
  margin: 20px 0;
  border-radius: 8px;
  text-align: center;
}

.progress-indicator {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
}

.progress-step {
  padding: 10px 15px;
  border-radius: 20px;
  font-weight: 400;
}

.progress-step.completed {
  background-color: #2c2c2c;
  color: white;
}

.progress-step.current {
  background-color: #006400;
  color: white;
}

.progress-step.pending {
  background-color: #F5F5F5;
  color: #666666;
}

.reveal .fragment.highlight-green {
  color: #2c2c2c;
  font-weight: 600;
}

.reveal .fragment.highlight-red {
  color: #8B0000;
  font-weight: 500;
}

@media screen and (max-width: 768px) {
  .reveal .two-column {
    flex-direction: column;
    gap: 20px;
  }
}
/* Hide Speaker Notes in Presentation Mode */
/* This targets paragraphs that contain speaker notes */
.reveal .slides section p {
  /* We cannot use :contains() as it's not standard CSS */
  /* Instead, we'll hide all p elements that might be speaker notes */
}

/* Hide .speaker-notes divs (class-based approach) */
.speaker-notes {
  display: none !important;
}

/* Hide reveal.js notes */
.reveal .notes {
  display: none !important;
}

/* A more direct approach: we'll need to convert **Speaker Notes:** to a class */

/* VanillaCore Logo Styling - Option 1: Small logo in top-right corner */
.vanilla-logo {
  position: absolute;
  top: 25px;
  right: 25px;
  max-width: 80px;
  max-height: 80px;
  z-index: 1000;
  pointer-events: none;
}

/* SELECTED: Option 1 - Small logo in top-right corner for content slides */
.reveal .slides section:not(.title-slide)::after {
  content: '';
  position: absolute;
  top: 25px;
  right: 25px;
  width: 80px;
  height: 80px;
  background-image: url('/images/VanillaCore_Vertical.png');
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  z-index: 1000;
  pointer-events: none;
  opacity: 0.8;
}

.vanilla-logo img {
  width: 100%;
  height: auto;
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

/* Logo for title slides - centered in middle of slide */
.title-slide .vanilla-logo {
  max-width: 300px;
  max-height: 300px;
  position: static;
  display: block;
  margin: 0 auto 60px auto;
  text-align: center;
}

/* Remove corner logo from title slides */
.title-slide::after {
  display: none !important;
}

/* Center title slide content with logo above title */
.title-slide {
  text-align: center !important;
  display: flex !important;
  flex-direction: column !important;
  justify-content: center !important;
  align-items: center !important;
  padding: 60px 40px !important;
  min-height: 100vh;
}

/* Style title slide headings to be centered below logo */
.title-slide h1,
.title-slide h2 {
  text-align: center !important;
  margin-left: auto !important;
  margin-right: auto !important;
  width: 100% !important;
}

.title-slide h1 {
  margin-top: 0 !important;
  margin-bottom: 20px !important;
  font-size: 2.88em !important; /* Increased by 20% (2.4em * 1.20) */
  font-weight: 600 !important;
}

.title-slide h2 {
  margin-top: 0 !important;
  margin-bottom: 40px !important;
  font-size: 2.16em !important; /* Increased by 20% (1.8em * 1.20) */
  color: #555555 !important;
  font-weight: 400 !important;
}

/* Subtle watermark for content slides */
.content-slide::after {
  content: "";
  background-image: url('/images/VanillaCore_Vertical.png');
  background-size: 80px;
  background-repeat: no-repeat;
  background-position: bottom 10px right 10px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.1;
  pointer-events: none;
  z-index: -1;
}

/* Hide reveal.js progress bar and controls to remove orange box artifact */
.reveal .progress {
  display: none !important;
}

.reveal .controls {
  display: none !important;
}
</style>

<div class="workshop-header title-slide">

<div class="vanilla-logo">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo">
</div>

# Tag 1: Design Patterns Workshop
## Creational Patterns in Enterprise-Architekturen

</div>

---

# Willkommen zum Design Patterns Workshop - Tag 1

## Über diesen Workshop
- Code-Analyse & Factory Method Pattern <!-- .element: class="fragment" -->
- Abstract Factory & Layered Architecture <!-- .element: class="fragment" -->
- Builder Pattern & Repository Integration <!-- .element: class="fragment" -->
- Prototype Pattern & Configuration Cloning <!-- .element: class="fragment" -->
- Singleton & Adapter mit Clean Architecture <!-- .element: class="fragment" -->

## Lernziele Tag 1
- Code-Smells in Legacy-Code identifizieren <!-- .element: class="fragment" -->
- Factory Method Pattern verstehen und anwenden <!-- .element: class="fragment" -->
- Abstract Factory in geschichteten Architekturen <!-- .element: class="fragment" -->
- Builder Pattern für komplexe Objekterstellung <!-- .element: class="fragment" -->
- SOLID-Prinzipien praktisch umsetzen <!-- .element: class="fragment" -->

<!-- Speaker Notes: Herzlich willkommen zum Design Patterns Workshop. Tag 1 konzentriert sich auf Creational Patterns in Enterprise-Telekom-Umgebungen. Wir werden Legacy-Code analysieren und refactoring mit Pattern-basierten Lösungen durchführen. -->

---

# Workshop Agenda - Tag 1

<div class="two-column">
<div>

## Vormittagssession (9:00 - 12:00)
- **Modul 1:** Code-Analyse & Factory Method <!-- .element: class="fragment" -->
- **Modul 2:** Abstract Factory & Layered Architecture <!-- .element: class="fragment" -->
- **Modul 3:** Builder Pattern & Repository Integration <!-- .element: class="fragment" -->

</div>
<div>

## Nachmittagssession (13:00 - 17:00)
- **Modul 4:** Prototype Pattern & Configuration Cloning <!-- .element: class="fragment" -->
- **Modul 5:** Singleton & Adapter mit Clean Architecture <!-- .element: class="fragment" -->
- Praktische Übungen & Code-Refactoring <!-- .element: class="fragment" -->
- Telekom-spezifische Implementierungen <!-- .element: class="fragment" -->
- Q&A und Diskussion <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Tag 1 behandelt Creational Patterns mit Fokus auf Telekom Legacy-Systeme. Jedes Modul baut aufeinander auf - von einfacher Code-Analyse bis zu komplexen Factory-Hierarchien in geschichteten Architekturen. -->

---

# Modul 1: Code-Analyse & Factory Method Pattern

## Lernziele
- Code-Smells in Legacy-Code identifizieren <!-- .element: class="fragment" -->
- Factory Method Pattern verstehen und anwenden <!-- .element: class="fragment" -->
- Refactoring-Strategien entwickeln <!-- .element: class="fragment" -->
- Single Responsibility Principle praktisch umsetzen <!-- .element: class="fragment" -->

---

# Problem-Motivation: Customer Service System

### Ausgangssituation
Ein typisches Problem in gewachsenen Telekom-Systemen: Die Kundenbetreuung muss verschiedene Kunden-Typen verwalten - Privatkunden, Geschäftskunden, Premium-Kunden. Der vorhandene Code enthält mehrere Code-Smells.

### Identifizierte Code-Smells
- **Long Method**: createCustomer-Methode hat zu viele Zeilen <!-- .element: class="fragment" -->
- **Switch Statements**: Typ-basierte Verzweigung deutet auf fehlendes Polymorphismus hin <!-- .element: class="fragment" -->
- **Feature Envy**: Methode manipuliert mehr Daten als sie besitzt <!-- .element: class="fragment" -->
- **Duplicate Code**: Wiederholte Zuweisungen in jedem Case <!-- .element: class="fragment" -->
- **Open/Closed Principle Verletzung**: Neue Kunden-Typen erfordern Änderung bestehender Methode <!-- .element: class="fragment" -->

---

# Problematischer Code - Customer Manager

<div class="code-example">

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
                
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }
}
```

</div>

---

# Factory Method Pattern - Konzept

<div class="pattern-definition">

#### Factory Method Pattern
**Zweck**: Das Factory Method Pattern definiert eine Schnittstelle zum Erstellen von Objekten, aber lässt Unterklassen entscheiden, welche Klasse instantiiert wird.

**Problem**: Objekte müssen erstellt werden, aber die Instanziierung soll an Subklassen übergeben werden.

**Lösung**: Ein Interface für die Objekt-Erstellung definieren, Subklassen entscheiden welche Klasse instanziiert wird.

</div>

### UML-Struktur
```
Creator (abstract)
  +factoryMethod(): Product
  +businessLogic()
  
ConcreteCreator extends Creator
  +factoryMethod(): ConcreteProduct
  
Product (interface)
  
ConcreteProduct implements Product
```

---

# Refactoring Schritt 1: Kunden-Abstraktion

<div class="code-example">

```java
// Basis-Interface definieren
public interface Customer {
    String getName();
    String getContractId();
    List<String> getTariffOptions();
    String getPaymentMethod();
    String getSupportLevel();
    void processContract();
}
```

</div>

### Konkrete Implementierung - PrivateCustomer

<div class="code-example">

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
    public String getSupportLevel() {
        return "Standard";
    }
    
    @Override
    public void processContract() {
        validatePersonalData();
        setupBasicServices();
    }
}
```

</div>

---

# Factory Method Creator - Template Method Integration

<div class="code-example">

```java
public abstract class CustomerFactory {
    
    // Factory Method - zu implementieren von Subklassen
    protected abstract Customer createCustomer(String name, String contractId);
    
    // Template Method - verwendet Factory Method
    public Customer processNewCustomer(String name, String contractId) {
        Customer customer = createCustomer(name, contractId);
        
        // Gemeinsame Geschäftslogik
        validateContract(customer);
        persistCustomer(customer);
        sendWelcomeMessage(customer);
        
        return customer;
    }
    
    private void validateContract(Customer customer) {
        // Allgemeine Vertragsprüfung
    }
    
    private void persistCustomer(Customer customer) {
        // Kunde in Datenbank speichern
    }
    
    private void sendWelcomeMessage(Customer customer) {
        // Willkommensnachricht senden
    }
}
```

</div>

---

# Konkrete Factory-Implementierungen

<div class="code-example">

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

public class PremiumCustomerFactory extends CustomerFactory {
    @Override
    protected Customer createCustomer(String name, String contractId) {
        return new PremiumCustomer(name, contractId);
    }
}
```

</div>

---

# SOLID-Prinzipien Integration

<div class="two-column">
<div>

## Single Responsibility Principle (SRP)
- Jede Factory-Klasse ist nur für einen Kunden-Typ zuständig <!-- .element: class="fragment" -->
- Customer-Klassen kapseln ihre spezifische Geschäftslogik <!-- .element: class="fragment" -->

## Open/Closed Principle (OCP)
- Neue Kunden-Typen durch neue Factory-Klassen hinzufügbar <!-- .element: class="fragment" -->
- Bestehender Code muss nicht geändert werden <!-- .element: class="fragment" -->

</div>
<div>

## Dependency Inversion Principle (DIP)
- Abhängigkeit zu Customer-Interface, nicht zu konkreten Implementierungen <!-- .element: class="fragment" -->

## Moderne Alternative - Java 8+
```java
public enum CustomerType {
    PRIVATE(PrivateCustomer::new),
    BUSINESS(BusinessCustomer::new),
    PREMIUM(PremiumCustomer::new);
    
    private final BiFunction<String, String, Customer> factory;
    
    public Customer create(String name, String contractId) {
        return factory.apply(name, contractId);
    }
}
```

</div>
</div>

---

# Modul 2: Abstract Factory & Layered Architecture

## Lernziele
- Abstract Factory Pattern in geschichteten Architekturen anwenden <!-- .element: class="fragment" -->
- Service-Familien sauber strukturieren <!-- .element: class="fragment" -->
- Dependency Injection Prinzipien verstehen <!-- .element: class="fragment" -->
- Interface Segregation in der Praxis umsetzen <!-- .element: class="fragment" -->

---

# Problem-Motivation: Multi-Channel Service Platform

### Ausgangssituation
Telekom betreibt verschiedene Service-Kanäle (Web, Mobile App, Call Center, Partner-Portal). Jeder Kanal benötigt unterschiedliche Service-Implementierungen, aber ähnliche Funktionalitäten.

### Herausforderungen
- Verschiedene Authentifizierung pro Kanal <!-- .element: class="fragment" -->
- Unterschiedliche Datenformate und APIs <!-- .element: class="fragment" -->
- Kanal-spezifische Geschäftsregeln <!-- .element: class="fragment" -->
- Einheitliche Service-Schnittstelle gewünscht <!-- .element: class="fragment" -->

### Telekom Service-Familien
```
Channel Factory
├── Authentication Service
├── Customer Service  
├── Billing Service
└── Notification Service
```

---

# Layered Architecture Integration

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

---

# Abstract Factory Pattern - Definition

<div class="pattern-definition">

#### Abstract Factory Pattern
**Zweck**: Abstract Factory stellt eine Schnittstelle bereit, um Familien verwandter Objekte zu erstellen, ohne deren konkrete Klassen zu spezifizieren.

**Problem**: Service-Familien müssen konsistent erstellt werden, verschiedene Implementierungen für unterschiedliche Kanäle.

**Lösung**: Factory-Interface für Service-Familie, konkrete Factories für jeden Kanal.

</div>

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

---

# Service-Abstrakte definieren

<div class="code-example">

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

</div>

---

# Abstract Factory Definition

<div class="code-example">

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

</div>

---

# Konkrete Factory - Web Channel

<div class="code-example">

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
    
    // ... weitere Service-Implementierungen
}
```

</div>

---

# Service-Suite für einheitliche Nutzung

<div class="code-example">

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

</div>

---

# Dependency Injection Integration

<div class="code-example">

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

</div>

---

# Modul 3: Builder Pattern & Repository Integration

## Lernziele
- Builder Pattern für komplexe Objekterstellung meistern <!-- .element: class="fragment" -->
- Repository Pattern mit Builder kombinieren <!-- .element: class="fragment" -->
- Fluent Interfaces designen und umsetzen <!-- .element: class="fragment" -->
- Liskov Substitution Principle praktisch anwenden <!-- .element: class="fragment" -->

---

# Problem-Motivation: Komplexe Datenbank-Queries

### Ausgangssituation
Telekom-Systeme arbeiten mit komplexen Datenbankabfragen - Kunden können nach vielen Kriterien gefiltert werden, verschiedene Joins sind nötig, Performance-Optimierungen müssen berücksichtigt werden.

### Herausforderungen
- Komplexe Query-Erstellung mit vielen optionalen Parametern <!-- .element: class="fragment" -->
- Verschiedene Datenbanken (Oracle, PostgreSQL, MongoDB) <!-- .element: class="fragment" -->
- Performance-kritische Abfragen mit Indizierung <!-- .element: class="fragment" -->
- Type-sichere Query-Erstellung <!-- .element: class="fragment" -->

---

# Problematischer Code - Repository

<div class="code-example">

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
        
        // ... weitere 20 Zeilen if-Statements
        
        return jdbcTemplate.query(sql.toString(), params.toArray(), customerRowMapper);
    }
}
```

</div>

---

# Builder Pattern - Konzept

<div class="pattern-definition">

#### Builder Pattern
**Zweck**: Builder Pattern trennt die Konstruktion komplexer Objekte von deren Repräsentation, so dass der gleiche Konstruktionsprozess verschiedene Darstellungen erstellen kann.

**Problem**: Komplexe Objekte mit vielen optionalen Parametern müssen erstellt werden.

**Lösung**: Step-by-step Konstruktion mit Fluent Interface.

</div>

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

---

# Query Builder - Implementation

<div class="code-example">

```java
public class CustomerQueryBuilder {
    private final StringBuilder query = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();
    private boolean hasWhere = false;
    private boolean hasJoins = false;
    
    public CustomerQueryBuilder() {
        query.append("SELECT c.* FROM customers c");
    }
    
    public CustomerQueryBuilder withName(String name) {
        if (name != null && !name.isEmpty()) {
            addWhereClause("c.name LIKE ?");
            parameters.add("%" + name + "%");
        }
        return this;
    }
    
    public CustomerQueryBuilder withContractType(String contractType) {
        if (contractType != null && !contractType.isEmpty()) {
            addWhereClause("c.contract_type = ?");
            parameters.add(contractType);
        }
        return this;
    }
    
    public CustomerQueryBuilder withTariff(String tariff) {
        if (tariff != null && !tariff.isEmpty()) {
            ensureTariffJoin();
            addWhereClause("t.name = ?");
            parameters.add(tariff);
        }
        return this;
    }
    
    public CustomerQueryBuilder withActiveStatus(Boolean isActive) {
        if (isActive != null) {
            addWhereClause("c.is_active = ?");
            parameters.add(isActive);
        }
        return this;
    }
    
    public CustomerQueryBuilder sortBy(String field, SortOrder order) {
        if (field != null) {
            query.append(" ORDER BY ").append(field);
            if (order != null) {
                query.append(" ").append(order.name());
            }
        }
        return this;
    }
    
    public CustomerQueryBuilder limit(int limit, int offset) {
        query.append(" LIMIT ").append(limit);
        if (offset > 0) {
            query.append(" OFFSET ").append(offset);
        }
        return this;
    }
    
    private void addWhereClause(String condition) {
        query.append(hasWhere ? " AND " : " WHERE ");
        query.append(condition);
        hasWhere = true;
    }
    
    private void ensureTariffJoin() {
        if (!hasJoins) {
            query.append(" JOIN tariffs t ON c.tariff_id = t.id");
            hasJoins = true;
        }
    }
    
    public Query build() {
        return new Query(query.toString(), parameters);
    }
}
```

</div>

---

# Repository mit Builder Integration

<div class="code-example">

```java
@Repository
public class CustomerRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Customer> customerRowMapper;
    
    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = new CustomerRowMapper();
    }
    
    public List<Customer> findCustomers(CustomerSearchCriteria criteria) {
        Query query = createQueryBuilder()
            .withName(criteria.getName())
            .withContractType(criteria.getContractType())
            .withTariff(criteria.getTariff())
            .withActiveStatus(criteria.getIsActive())
            .withDateRange(criteria.getContractStart(), criteria.getContractEnd())
            .withLocation(criteria.getCity(), criteria.getPostalCode())
            .withRevenue(criteria.getMinRevenue())
            .sortBy(criteria.getSortBy(), criteria.getSortOrder())
            .limit(criteria.getLimit(), criteria.getOffset())
            .build();
        
        return jdbcTemplate.query(query.getSql(), 
                                 query.getParameters().toArray(), 
                                 customerRowMapper);
    }
    
    public CustomerQueryBuilder createQueryBuilder() {
        return new CustomerQueryBuilder();
    }
    
    // Fluent Interface für direkte Nutzung
    public List<Customer> findActivePrivateCustomers() {
        return findCustomers(createQueryBuilder()
            .withContractType("PRIVATE")
            .withActiveStatus(true)
            .sortBy("name", SortOrder.ASC)
            .limit(100, 0)
            .build());
    }
}
```

</div>

---

# Fluent Interface - Advanced Features

<div class="code-example">

```java
// Type-Safe Builder mit Method Chaining
public class TypeSafeCustomerQueryBuilder {
    
    public NameStage byName(String name) {
        return new CustomerQueryBuilderImpl().withName(name);
    }
    
    public interface NameStage {
        ContractStage withContractType(String contractType);
        QueryStage build();
    }
    
    public interface ContractStage {
        TariffStage withTariff(String tariff);
        QueryStage build();
    }
    
    public interface TariffStage {
        SortStage sortBy(String field, SortOrder order);
        QueryStage build();
    }
    
    public interface SortStage {
        QueryStage limit(int limit, int offset);
        QueryStage build();
    }
    
    public interface QueryStage {
        Query build();
    }
}

// Usage - verhindert ungültige Query-Kombinationen
Query query = new TypeSafeCustomerQueryBuilder()
    .byName("Müller")
    .withContractType("BUSINESS")
    .withTariff("Professional")
    .sortBy("contract_date", SortOrder.DESC)
    .limit(50, 0)
    .build();
```

</div>

---

# Modul 4: Prototype Pattern & Configuration Cloning

## Lernziele
- Prototype Pattern für kostspielige Objekterzeugung verstehen <!-- .element: class="fragment" -->
- Deep vs. Shallow Copy Problematik meistern <!-- .element: class="fragment" -->
- Configuration-Cloning für komplexe Enterprise-Settings <!-- .element: class="fragment" -->
- Performance-Optimierung durch intelligente Objektkopierung <!-- .element: class="fragment" -->

---

# Problem-Motivation: Komplexe Service-Konfiguration

### Ausgangssituation
In Enterprise-Umgebungen müssen häufig ähnliche, aber leicht unterschiedliche Service-Konfigurationen erstellt werden. Die Initialisierung dieser Konfigurationsobjekte ist aufwändig und zeitkritisch.

### Herausforderungen
- **Kostspielige Objekterzeugung**: Database-Connections, SSL-Certificates, Complex Validation <!-- .element: class="fragment" -->
- **Template-basierte Konfiguration**: 90% identisch, 10% variabel <!-- .element: class="fragment" -->
- **Runtime-Configuration**: Dynamische Erstellung zur Laufzeit erforderlich <!-- .element: class="fragment" -->
- **Memory-Effizienz**: Sharing von unveränderlichen Teilen <!-- .element: class="fragment" -->

---

# Problematischer Code - Expensive Object Creation

<div class="code-example">

```java
@Service
public class ServiceConfigurationManager {
    
    // Anti-Pattern: Expensive Recreation für ähnliche Konfigurationen
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
        
        // Environment-specific anpassungen
        config.setEnvironment("DEV");
        config.setLogLevel("DEBUG");
        config.setDatabaseUrl("dev-database-url");
        
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // DUPLICATE: Gleiche expensive Operations!
        config.setDatabaseSettings(loadDatabaseDefaults());    // 200ms
        config.setSecuritySettings(loadSecurityDefaults());    // 200ms
        config.setCachingSettings(loadCachingDefaults());      // 200ms
        config.setSslCertificate(validateAndLoadSslCert("test-cert.pem")); // 500ms
        config.setServiceEndpoints(discoverAvailableServices()); // 300ms
        
        // Nur diese Werte sind unterschiedlich
        config.setEnvironment("TEST");
        config.setLogLevel("INFO");
        config.setDatabaseUrl("test-database-url");
        
        return config;
    }
}
```

</div>

**Performance-Problem**: Jede Konfigurationserstellung dauert 1.4+ Sekunden für identische Operationen!

---

# Prototype Pattern - Struktur

<div class="pattern-definition">

#### Prototype Pattern
**Zweck**: Das Prototype Pattern erstellt neue Objekte durch Klonen eines prototypischen Objekts, statt durch normale Instanziierung.

**Problem**: Kostspielige Objekterzeugung soll vermieden werden, ähnliche Objekte benötigt.

**Lösung**: Clone-Interface implementieren, Prototyp-Registry für Template-Objekte.

</div>

### UML-Struktur
```
Prototype (interface)
  +clone(): Prototype
  
ConcretePrototype implements Prototype
  +clone(): ConcretePrototype
  
PrototypeRegistry
  +getPrototype(String key): Prototype
  +setPrototype(String key, Prototype prototype)
```

---

# Configuration Prototype Implementation

<div class="code-example">

```java
public class ServiceConfiguration implements Cloneable {
    
    // Expensive-to-create Objects
    private DatabaseSettings databaseSettings;
    private SecuritySettings securitySettings;
    private CachingSettings cachingSettings;
    private SSLCertificate sslCertificate;
    private List<ServiceEndpoint> serviceEndpoints;
    
    // Environment-specific Properties (cheap to change)
    private String environment;
    private String logLevel;
    private String databaseUrl;
    private Map<String, String> customProperties;
    
    // Deep Clone Implementation
    @Override
    public ServiceConfiguration clone() {
        try {
            ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
            
            // Deep copy expensive objects (immutable ones can be shared)
            cloned.databaseSettings = this.databaseSettings.clone();
            cloned.securitySettings = this.securitySettings.clone();
            cloned.cachingSettings = this.cachingSettings.clone();
            
            // SSL Certificate kann geshared werden (immutable)
            cloned.sslCertificate = this.sslCertificate; // Shallow copy OK
            
            // Service Endpoints deep copy (können sich ändern)
            cloned.serviceEndpoints = new ArrayList<>();
            for (ServiceEndpoint endpoint : this.serviceEndpoints) {
                cloned.serviceEndpoints.add(endpoint.clone());
            }
            
            // Environment properties - neue Map erstellen
            cloned.customProperties = new HashMap<>(this.customProperties);
            
            return cloned;
            
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Fluent Interface für Environment-specific Changes
    public ServiceConfiguration withEnvironment(String environment) {
        this.environment = environment;
        return this;
    }
    
    public ServiceConfiguration withLogLevel(String logLevel) {
        this.logLevel = logLevel;
        return this;
    }
    
    public ServiceConfiguration withDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        return this;
    }
    
    public ServiceConfiguration withCustomProperty(String key, String value) {
        this.customProperties.put(key, value);
        return this;
    }
}
```

</div>

---

# Configuration Prototype Registry

<div class="code-example">

```java
@Component
public class ConfigurationPrototypeRegistry {
    
    private final Map<String, ServiceConfiguration> prototypes = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;
    
    @PostConstruct
    public void initializePrototypes() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    createBasePrototype();
                    initialized = true;
                }
            }
        }
    }
    
    private void createBasePrototype() {
        ServiceConfiguration baseConfig = new ServiceConfiguration();
        
        // Expensive one-time initialization
        baseConfig.setDatabaseSettings(loadDatabaseDefaults());      // 200ms
        baseConfig.setSecuritySettings(loadSecurityDefaults());      // 200ms
        baseConfig.setCachingSettings(loadCachingDefaults());        // 200ms
        baseConfig.setSslCertificate(validateAndLoadSslCert());      // 500ms
        baseConfig.setServiceEndpoints(discoverAvailableServices()); // 300ms
        
        prototypes.put("BASE", baseConfig);
    }
    
    public ServiceConfiguration getPrototype(String key) {
        ServiceConfiguration prototype = prototypes.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found: " + key);
        }
        return prototype.clone();
    }
    
    public void registerPrototype(String key, ServiceConfiguration prototype) {
        prototypes.put(key, prototype);
    }
    
    public Set<String> getAvailablePrototypes() {
        return prototypes.keySet();
    }
}
```

</div>

---

# Configuration Manager mit Prototype

<div class="code-example">

```java
@Service
public class PrototypeBasedConfigurationManager {
    
    private final ConfigurationPrototypeRegistry registry;
    
    public PrototypeBasedConfigurationManager(ConfigurationPrototypeRegistry registry) {
        this.registry = registry;
    }
    
    // Fast configuration creation - nur ~10ms statt 1400ms!
    public ServiceConfiguration createDevConfiguration() {
        return registry.getPrototype("BASE")
            .withEnvironment("DEV")
            .withLogLevel("DEBUG")
            .withDatabaseUrl("dev-database-url")
            .withCustomProperty("debug.level", "TRACE")
            .withCustomProperty("cache.ttl", "300");
    }
    
    public ServiceConfiguration createTestConfiguration() {
        return registry.getPrototype("BASE")
            .withEnvironment("TEST")
            .withLogLevel("INFO")
            .withDatabaseUrl("test-database-url")
            .withCustomProperty("test.parallel", "true");
    }
    
    public ServiceConfiguration createProdConfiguration() {
        return registry.getPrototype("BASE")
            .withEnvironment("PROD")
            .withLogLevel("WARN")
            .withDatabaseUrl("prod-database-url")
            .withCustomProperty("monitoring.enabled", "true")
            .withCustomProperty("cache.ttl", "3600");
    }
    
    // Advanced: Environment-specific prototypes
    public ServiceConfiguration createOptimizedProdConfiguration() {
        // Falls bereits optimierter Prod-Prototype existiert
        try {
            return registry.getPrototype("PROD_OPTIMIZED")
                .withCustomProperty("instance.id", generateInstanceId());
        } catch (IllegalArgumentException e) {
            // Fallback auf Base + Optimierungen
            return createProdConfiguration()
                .withCustomProperty("performance.mode", "HIGH");
        }
    }
}
```

</div>

---

# Deep vs Shallow Copy Strategy

<div class="two-column">
<div>

## Shallow Copy (Performance)
```java
// Immutable objects - safe to share
cloned.sslCertificate = this.sslCertificate;
cloned.staticConfiguration = this.staticConfiguration;

// Thread-safe objects - safe to share
cloned.connectionPool = this.connectionPool;
```

## Deep Copy (Safety)
```java
// Mutable objects - must copy
cloned.customProperties = new HashMap<>(this.customProperties);

// Complex objects with state
cloned.serviceEndpoints = cloneServiceEndpoints();
```

</div>
<div>

## Lazy Cloning Strategy
```java
public class LazyCloneConfiguration {
    private DatabaseSettings databaseSettings;
    private boolean databaseSettingsCloned = false;
    
    public DatabaseSettings getDatabaseSettings() {
        if (!databaseSettingsCloned && databaseSettings != null) {
            databaseSettings = databaseSettings.clone();
            databaseSettingsCloned = true;
        }
        return databaseSettings;
    }
}
```

## Copy-on-Write Pattern
```java
public class CowConfiguration {
    private volatile List<String> properties;
    private volatile boolean isClone = false;
    
    public void addProperty(String prop) {
        if (!isClone) {
            properties = new ArrayList<>(properties);
            isClone = true;
        }
        properties.add(prop);
    }
}
```

</div>
</div>

---

# Modul 5: Singleton & Adapter mit Clean Architecture

## Lernziele
- Singleton Pattern korrekt in Enterprise-Umgebungen anwenden <!-- .element: class="fragment" -->
- Adapter Pattern für Legacy-System Integration <!-- .element: class="fragment" -->
- Clean Architecture Prinzipien mit Pattern-Integration <!-- .element: class="fragment" -->
- Thread-Safety und Performance-Optimierung <!-- .element: class="fragment" -->

---

# Problem-Motivation: Legacy Integration

### Ausgangssituation
Telekom hat viele Legacy-Systeme mit unterschiedlichen APIs. Neue Services müssen mit alten Systemen kommunizieren, aber die Interfaces passen nicht zusammen.

### Herausforderungen
- **Legacy APIs**: SOAP, XML-RPC, proprietäre Protokolle <!-- .element: class="fragment" -->
- **Moderne Services**: REST, JSON, reactive programming <!-- .element: class="fragment" -->
- **Resource Management**: Connection Pools, Thread-sichere Singletons <!-- .element: class="fragment" -->
- **Clean Architecture**: Abhängigkeiten von außen nach innen <!-- .element: class="fragment" -->

---

# Singleton Pattern - Enterprise Implementation

<div class="code-example">

```java
// Thread-safe Singleton mit Enum (Bloch's Approach)
public enum ConfigurationManager {
    INSTANCE;
    
    private final Map<String, String> properties = new ConcurrentHashMap<>();
    private volatile boolean loaded = false;
    
    public void loadConfiguration(String configPath) {
        if (!loaded) {
            synchronized (this) {
                if (!loaded) {
                    // Expensive configuration loading
                    loadPropertiesFromFile(configPath);
                    loadEnvironmentVariables();
                    loadSystemProperties();
                    loaded = true;
                }
            }
        }
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    // Lazy initialization helper
    private void ensureLoaded() {
        if (!loaded) {
            loadConfiguration("application.properties");
        }
    }
    
    private void loadPropertiesFromFile(String configPath) {
        // Implementation...
    }
}

// Usage
String dbUrl = ConfigurationManager.INSTANCE.getProperty("db.url", "localhost");
```

</div>

---

# Legacy System Adapter Pattern

<div class="code-example">

```java
// Legacy SOAP Customer Service (nicht veränderbar)
@WebService
public class LegacyCustomerSOAPService {
    
    public CustomerSOAPResponse getCustomerDetails(String customerId) {
        // Legacy SOAP implementation
        CustomerSOAPResponse response = new CustomerSOAPResponse();
        response.setCustomerXML(buildCustomerXML(customerId));
        return response;
    }
    
    public boolean updateCustomerSOAP(String customerId, String customerXML) {
        // Legacy update implementation
        return processCustomerXML(customerXML);
    }
}

// Modern Customer Service Interface (Domain Layer)
public interface CustomerService {
    Customer getCustomer(String customerId);
    void updateCustomer(Customer customer);
    List<Customer> searchCustomers(SearchCriteria criteria);
}

// Adapter für Legacy Integration
@Component
public class LegacyCustomerServiceAdapter implements CustomerService {
    
    private final LegacyCustomerSOAPService legacyService;
    private final CustomerXMLMapper xmlMapper;
    
    public LegacyCustomerServiceAdapter(LegacyCustomerSOAPService legacyService,
                                       CustomerXMLMapper xmlMapper) {
        this.legacyService = legacyService;
        this.xmlMapper = xmlMapper;
    }
    
    @Override
    public Customer getCustomer(String customerId) {
        try {
            CustomerSOAPResponse response = legacyService.getCustomerDetails(customerId);
            return xmlMapper.fromXML(response.getCustomerXML());
        } catch (Exception e) {
            throw new CustomerServiceException("Failed to get customer from legacy system", e);
        }
    }
    
    @Override
    public void updateCustomer(Customer customer) {
        try {
            String customerXML = xmlMapper.toXML(customer);
            boolean success = legacyService.updateCustomerSOAP(customer.getId(), customerXML);
            if (!success) {
                throw new CustomerServiceException("Legacy update failed");
            }
        } catch (Exception e) {
            throw new CustomerServiceException("Failed to update customer in legacy system", e);
        }
    }
    
    @Override
    public List<Customer> searchCustomers(SearchCriteria criteria) {
        // Legacy system doesn't support complex search
        throw new UnsupportedOperationException("Search not supported by legacy system");
    }
}
```

</div>

---

# Clean Architecture Integration

<div class="code-example">

```java
// Domain Layer - Customer Entity
public class Customer {
    private final String id;
    private final String name;
    private final String email;
    private final CustomerStatus status;
    
    // Constructor, getters, business methods
    public Customer(String id, String name, String email, CustomerStatus status) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.email = requireNonNull(email);
        this.status = requireNonNull(status);
    }
    
    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }
    
    public Customer activate() {
        return new Customer(id, name, email, CustomerStatus.ACTIVE);
    }
}

// Use Case Layer - Application Service
@Service
public class CustomerApplicationService {
    
    private final CustomerService customerService; // Interface from Domain
    private final NotificationService notificationService;
    
    public CustomerApplicationService(CustomerService customerService,
                                    NotificationService notificationService) {
        this.customerService = customerService;
        this.notificationService = notificationService;
    }
    
    @Transactional
    public void activateCustomer(String customerId) {
        Customer customer = customerService.getCustomer(customerId);
        
        if (customer.isActive()) {
            throw new BusinessException("Customer already active");
        }
        
        Customer activatedCustomer = customer.activate();
        customerService.updateCustomer(activatedCustomer);
        
        notificationService.sendActivationNotification(customerId);
    }
}
```

</div>

---

# Adapter Registry Pattern

<div class="code-example">

```java
// Interface für verschiedene Backend-Systeme
public interface BackendSystemAdapter {
    String getSystemName();
    boolean isAvailable();
    Customer getCustomer(String customerId);
    void updateCustomer(Customer customer);
}

// Registry für Backend-System Management
@Component
public class BackendSystemRegistry {
    
    private final Map<String, BackendSystemAdapter> adapters = new ConcurrentHashMap<>();
    private final Map<String, BackendSystemAdapter> customerRouting = new ConcurrentHashMap<>();
    
    public void registerAdapter(String systemName, BackendSystemAdapter adapter) {
        adapters.put(systemName, adapter);
    }
    
    public BackendSystemAdapter getAdapter(String systemName) {
        BackendSystemAdapter adapter = adapters.get(systemName);
        if (adapter == null) {
            throw new IllegalArgumentException("Backend system not found: " + systemName);
        }
        return adapter;
    }
    
    public BackendSystemAdapter getAdapterForCustomer(String customerId) {
        // Routing-Logik: Kunde zu Backend-System mapping
        BackendSystemAdapter adapter = customerRouting.get(customerId);
        if (adapter == null) {
            // Fallback: Legacy system für unbekannte Kunden
            adapter = getAdapter("LEGACY_SOAP");
        }
        
        if (!adapter.isAvailable()) {
            // Failover zu verfügbarem System
            adapter = findAvailableAdapter();
        }
        
        return adapter;
    }
    
    private BackendSystemAdapter findAvailableAdapter() {
        return adapters.values().stream()
            .filter(BackendSystemAdapter::isAvailable)
            .findFirst()
            .orElseThrow(() -> new SystemException("No backend system available"));
    }
    
    public List<String> getAvailableSystems() {
        return adapters.values().stream()
            .filter(BackendSystemAdapter::isAvailable)
            .map(BackendSystemAdapter::getSystemName)
            .collect(Collectors.toList());
    }
}
```

</div>

---

# Composite Service mit Adaptern

<div class="code-example">

```java
@Service
public class CompositeCustomerService implements CustomerService {
    
    private final BackendSystemRegistry systemRegistry;
    private final CustomerMergeStrategy mergeStrategy;
    
    public CompositeCustomerService(BackendSystemRegistry systemRegistry,
                                   CustomerMergeStrategy mergeStrategy) {
        this.systemRegistry = systemRegistry;
        this.mergeStrategy = mergeStrategy;
    }
    
    @Override
    public Customer getCustomer(String customerId) {
        BackendSystemAdapter primaryAdapter = systemRegistry.getAdapterForCustomer(customerId);
        
        try {
            return primaryAdapter.getCustomer(customerId);
        } catch (Exception e) {
            // Fallback strategy
            return getCustomerWithFallback(customerId, primaryAdapter);
        }
    }
    
    private Customer getCustomerWithFallback(String customerId, 
                                           BackendSystemAdapter excludeAdapter) {
        List<String> availableSystems = systemRegistry.getAvailableSystems();
        
        for (String systemName : availableSystems) {
            if (systemName.equals(excludeAdapter.getSystemName())) {
                continue; // Skip failed system
            }
            
            try {
                BackendSystemAdapter adapter = systemRegistry.getAdapter(systemName);
                return adapter.getCustomer(customerId);
            } catch (Exception e) {
                // Log and try next system
                log.warn("Failed to get customer from {}: {}", systemName, e.getMessage());
            }
        }
        
        throw new CustomerNotFoundException("Customer not found in any system: " + customerId);
    }
    
    @Override
    public void updateCustomer(Customer customer) {
        BackendSystemAdapter adapter = systemRegistry.getAdapterForCustomer(customer.getId());
        adapter.updateCustomer(customer);
        
        // Optional: Sync to other systems
        syncToOtherSystems(customer, adapter);
    }
}
```

</div>

---

# Tag 1 Praktische Übung - Integration Challenge

<div class="interactive-question">

## Ihre Aufgabe: Telekom Service Integration
#### Implementieren Sie eine vollständige Service-Integration mit allen Pattern aus Tag 1

</div>

<div class="two-column">
<div>

### Szenario: Multi-Backend Customer Management
- **Legacy SOAP Service** (nicht änderbar) <!-- .element: class="fragment" -->
- **Moderne REST API** (neu entwickelt) <!-- .element: class="fragment" -->
- **NoSQL Document Store** (MongoDB) <!-- .element: class="fragment" -->
- **Configuration Management** (Singleton) <!-- .element: class="fragment" -->
- **Query Builder** für komplexe Suchen <!-- .element: class="fragment" -->

### Anforderungen
- Factory Method für Service-Erstellung <!-- .element: class="fragment" -->
- Abstract Factory für Backend-Familien <!-- .element: class="fragment" -->
- Builder für Query-Erstellung <!-- .element: class="fragment" -->
- Prototype für Configuration-Templates <!-- .element: class="fragment" -->
- Adapter für Legacy-Integration <!-- .element: class="fragment" -->

</div>
<div>

### Implementation Tasks (45 Min)
1. **Service Factory Design** (15 Min) <!-- .element: class="fragment" -->
   - CustomerServiceFactory mit Backend-spezifischen Implementierungen
   - Abstract Factory für Service-Familien

2. **Builder Implementation** (15 Min) <!-- .element: class="fragment" -->
   - CustomerQueryBuilder mit Fluent Interface
   - Type-safe Query-Erstellung

3. **Adapter & Integration** (15 Min) <!-- .element: class="fragment" -->
   - Legacy SOAP Adapter
   - Configuration Prototype Registry
   - Service Registry mit Failover

### Bonus Challenges
- **Performance Optimization** mit Caching <!-- .element: class="fragment" -->
- **Circuit Breaker** Pattern für Legacy-Services <!-- .element: class="fragment" -->
- **Monitoring & Observability** Integration <!-- .element: class="fragment" -->

</div>
</div>

---

# Tag 1 Zusammenfassung

<div class="progress-indicator">
<div class="progress-step completed">✅ Code-Analyse & Factory Method</div>
<div class="progress-step completed">✅ Abstract Factory & Layered Architecture</div>
<div class="progress-step completed">✅ Builder Pattern & Repository Integration</div>
<div class="progress-step completed">✅ Prototype Pattern & Configuration Cloning</div>
<div class="progress-step completed">✅ Singleton & Adapter mit Clean Architecture</div>
</div>

<div class="two-column">
<div>

## Was wir behandelt haben
- **Creational Patterns** in Enterprise-Kontexten <!-- .element: class="fragment" -->
- **Legacy-Code Refactoring** mit Pattern-basierten Lösungen <!-- .element: class="fragment" -->
- **Performance-Optimierung** durch intelligente Objekterstellung <!-- .element: class="fragment" -->
- **Clean Architecture** Integration mit Design Patterns <!-- .element: class="fragment" -->
- **SOLID-Prinzipien** in der Praxis <!-- .element: class="fragment" -->

</div>
<div>

## Zentrale Erkenntnisse
- **Factory Method** löst Code-Smells durch Polymorphismus <!-- .element: class="fragment" -->
- **Abstract Factory** strukturiert Service-Familien elegant <!-- .element: class="fragment" -->
- **Builder Pattern** macht komplexe Objekterstellung verständlich <!-- .element: class="fragment" -->
- **Prototype Pattern** optimiert Performance bei ähnlichen Objekten <!-- .element: class="fragment" -->
- **Adapter Pattern** ermöglicht nahtlose Legacy-Integration <!-- .element: class="fragment" -->

</div>
</div>

<div class="highlight-box accent">

## Morgen: Tag 2 Vorschau
- **Structural Patterns**: Decorator, Composite, Proxy
- **Advanced Enterprise Patterns**: Facade, Bridge, Flyweight
- **Microservice Architecture** Integration
- **Event-Driven Patterns** für reactive Systems

</div>

---

# Fragen & Diskussion

<div class="two-column">
<div>

## Kontaktinformationen
**E-Mail:** architecture-training@vanillacore.com  <!-- .element: class="fragment" -->
**Repository:** [Design Patterns Examples](https://github.com/vanillacore/design-patterns)  <!-- .element: class="fragment" -->
**Code-Beispiele:** Verfügbar im Workshop-Repository <!-- .element: class="fragment" -->

</div>
<div>

## Nächste Schritte
- **Pattern-Integration** in eigenen Projekten testen <!-- .element: class="fragment" -->
- **Code-Smell Detection** Tools einsetzen <!-- .element: class="fragment" -->
- **Legacy-Refactoring** Strategien entwickeln <!-- .element: class="fragment" -->
- **Vorbereitung auf Tag 2** Structural Patterns <!-- .element: class="fragment" -->

</div>
</div>

<div class="workshop-header">

**Vielen Dank für Ihre Teilnahme!**

</div>

<!-- Speaker Notes: Schließen Sie ab, indem Sie die Schlüsselkonzepte von Tag 1 verstärken. Hervorhebung der praktischen Anwendbarkeit in Enterprise-Umgebungen und Begeisterung für Tag 2 schaffen. Ermutigen Sie zur Anwendung der Pattern in aktuellen Projekten. -->