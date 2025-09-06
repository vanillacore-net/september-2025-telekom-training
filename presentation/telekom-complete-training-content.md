# Telekom Architecture Training - Complete 4-Day Workshop

## Presentation Structure (100-120 slides total)

### INTRODUCTION SECTION (10-12 slides)

#### Slide 1: Title Slide
**Title:** Software-Architektur Training
**Subtitle:** 4-tägiger Intensiv-Workshop für Enterprise-Entwicklung
**Date:** September 2025
**Trainer Notes:** Willkommen, Vorstellung, Workshop-Überblick. Betonung auf praktische Anwendung in Enterprise-Umgebung.

#### Slide 2: Workshop-Agenda - Überblick
- **Tag 1:** Grundlagen & Creational Patterns
- **Tag 2:** Structural Patterns & Code-Architektur
- **Tag 3:** Behavioral Patterns & Kommunikation
- **Tag 4:** Advanced Patterns & Integration
**Trainer Notes:** 4 Tage intensive Arbeit, Theorie immer mit Praxis verbunden, Telekom-spezifische Beispiele.

#### Slide 3: Lernziele und Workshop-Ziele
- Software-Architektur als strategisches Werkzeug verstehen
- Design Patterns sicher anwenden können
- Legacy-Code strukturiert refactoren
- Clean Architecture Prinzipien umsetzen
- Enterprise-spezifische Herausforderungen meistern
**Trainer Notes:** Ziele sind messbar und praktisch orientiert. Nach dem Workshop sollen alle Patterns selbständig anwendbar sein.

#### Slide 4: Was ist Software-Architektur?
**Definition (Martin Fowler):** "Architecture is about the important stuff. Whatever that is."
**Praktisch bedeutet das:**
- Strukturelle Entscheidungen mit hohen Änderungskosten
- Kommunikation zwischen Entwicklungsteams
- Balance zwischen Business-Anforderungen und technischer Umsetzung
**Trainer Notes:** Verschiedene Definitionen diskutieren (IEEE 1471, Grady Booch). Fokus auf "cost of change" als Kriterium.

#### Slide 5: Enterprise-Architektur Besonderheiten
**Herausforderungen in großen Unternehmen:**
- Legacy-Systeme (jahrzehntealt, geschäftskritisch)
- Regulatorische Anforderungen (DSGVO, Compliance)
- Hochverfügbarkeit (99.9%+ Uptime)
- Massive Skalierung (Millionen Nutzer)
- Complex Integration (dutzende Systeme)
**Trainer Notes:** Telekom-spezifische Beispiele verwenden. Regulatorische Anforderungen als Treiber für Architektur-Entscheidungen.

#### Slide 6: Design Patterns - Warum und Wozu?
**Patterns lösen wiederkehrende Probleme:**
- Bewährte Lösungsansätze
- Gemeinsame Sprache für Entwickler-Teams
- Reduzierte Komplexität durch Standardisierung
- Verbesserte Code-Qualität und Wartbarkeit
**Trainer Notes:** Gang of Four Historie kurz erwähnen. Betonung auf "gemeinsame Sprache" - wichtig für Teamkommunikation.

#### Slide 7: Clean Architecture Überblick
**Grundprinzipien:**
- Dependency Inversion (abhängig von Abstraktionen)
- Separation of Concerns (klare Verantwortlichkeiten)
- Testbarkeit durch lose Kopplung
- Business Logic im Zentrum
**Trainer Notes:** Zwiebelmodell zeigen. Business Logic ist unabhängig von Framework, Database, UI.

#### Slide 8: SOLID Principles - Einführung
- **S**ingle Responsibility Principle
- **O**pen/Closed Principle
- **L**iskov Substitution Principle
- **I**nterface Segregation Principle
- **D**ependency Inversion Principle
**Trainer Notes:** Nur Überblick, wird in Patterns vertieft. SOLID als Fundament für gute Architektur.

#### Slide 9: Code Smells und Refactoring
**Häufige Code-Smells:**
- Long Method / Large Class
- Switch Statements (fehlender Polymorphismus)
- Duplicate Code
- Feature Envy
**Trainer Notes:** Refactoring als kontinuierlicher Prozess. Code-Smells als Warnsignale für Architektur-Probleme.

#### Slide 10: Workshop-Methodik
**Unser Vorgehen:**
- Problem-Motivation → Pattern-Lösung → Hands-on Implementation
- Telekom-spezifische Beispiele und Use Cases
- Code-Reviews und Diskussion in der Gruppe
- Praxisnahe Übungen mit sofortiger Anwendung
**Trainer Notes:** Interaktive Methode betonen. Teilnehmer sollen aktiv mitarbeiten und eigene Erfahrungen einbringen.

#### Slide 11: Erwartungsabgleich und Fragen
**Was bringen Sie mit?**
- Welche Architektur-Herausforderungen haben Sie aktuell?
- Wo sehen Sie die größten Pain Points?
- Welche Patterns kennen Sie bereits?
**Trainer Notes:** Kurze Vorstellungsrunde. Teilnehmer-Erwartungen sammeln und mit Agenda abgleichen.

### CREATIONAL PATTERNS SECTION (25-30 slides)

#### Slide 12: Creational Patterns - Einführung
**Zweck:** Objekterzeugung flexibel und wiederverwendbar gestalten
**Die 5 Creational Patterns:**
- Factory Method
- Abstract Factory  
- Builder
- Prototype
- Singleton
**Trainer Notes:** Objekterzeugung als kritischer Punkt in OOP. Flexibilität vs. Kontrolle.

#### Slide 13: Factory Method - Problem
**Problematischer Code:**
```java
switch (customerType) {
    case "PRIVATE": return new PrivateCustomer();
    case "BUSINESS": return new BusinessCustomer();
    // ...
}
```
**Code-Smells:** Switch Statement, Open/Closed Principle Verletzung
**Trainer Notes:** Live-Coding mit Telekom Customer Management Beispiel. Code-Smells identifizieren lassen.

#### Slide 14: Factory Method - Lösung
**UML-Struktur zeigen**
```java
abstract class CustomerFactory {
    abstract Customer createCustomer();
    
    public void processCustomer() {
        Customer customer = createCustomer();
        customer.processContract();
    }
}
```
**Trainer Notes:** Template Method Aspekt erklären. Factory Method wird oft in Kombination mit anderen Patterns verwendet.

#### Slide 15: Factory Method - Implementation
**Konkrete Implementierung:**
```java
class PrivateCustomerFactory extends CustomerFactory {
    Customer createCustomer() {
        return new PrivateCustomer();
    }
}
```
**Trainer Notes:** Code live entwickeln. Polymorphismus als Schlüsselkonzept betonen.

#### Slide 16: Factory Method - Telekom Use Cases
**Praktische Anwendung:**
- Service-Provider Erzeugung (SMS, Email, Push)
- Tarif-Konfiguration je Kundentyp
- Billing-System Komponenten
- Report-Generator für verschiedene Formate
**Trainer Notes:** Konkrete Telekom-Beispiele aus der Praxis. Teilnehmer nach eigenen Use Cases fragen.

#### Slide 17: Abstract Factory - Problem
**Produktfamilien koordiniert erstellen:**
```java
// Problematisch: UI-Komponenten mischen Styles
Button button = new WindowsButton();
Scrollbar scrollbar = new MacScrollbar(); // Style-Bruch!
```
**Trainer Notes:** Family of related objects als Kernproblem. Konsistenz zwischen Produkten wichtig.

#### Slide 18: Abstract Factory - UML-Struktur
**Abstract Factory Pattern UML:**
- AbstractFactory (interface)
- ConcreteFactory (implementations)
- AbstractProduct families
- Client code independent of concrete classes
**Trainer Notes:** UML-Diagramm zeichnen oder vorbereitet zeigen. Komplexere Struktur als Factory Method.

#### Slide 19: Abstract Factory - Telekom Beispiel
**Service Provider Families:**
```java
interface NotificationFactory {
    EmailService createEmailService();
    SMSService createSMSService();
    PushService createPushService();
}
```
**Trainer Notes:** B2C vs. B2B Service Families als konkretes Beispiel. Verschiedene SLA-Level.

#### Slide 20: Builder Pattern - Problem
**Komplexe Objektkonstruktion:**
```java
// Viele Parameter, unübersichtlich
Customer customer = new Customer(name, address, phone, 
    email, contractType, paymentMethod, options, ...);
```
**Trainer Notes:** Long Parameter List als Code-Smell. Konstruktor wird unhandlich.

#### Slide 21: Builder Pattern - Fluent Interface
**Builder Implementation:**
```java
Customer customer = Customer.builder()
    .name("Max Mustermann")
    .address(address)
    .contractType("Premium")
    .paymentMethod("SEPA")
    .build();
```
**Trainer Notes:** Fluent Interface Vorteile: Lesbarkeit, optionale Parameter, Validierung.

#### Slide 22: Builder Pattern - Step Builder
**Guided Construction:**
```java
Customer customer = CustomerBuilder
    .requireName("Max Mustermann")
    .requireAddress(address)
    .optionalPayment("SEPA")
    .build();
```
**Trainer Notes:** Step Builder für komplexe Konstruktionslogik. Compiler-time Validierung.

#### Slide 23: Prototype Pattern - Use Cases
**Wann Prototype verwenden:**
- Objekt-Erstellung ist teuer (DB-Abfragen)
- Ähnliche Objekte mit kleinen Unterschieden
- Dynamic Class Loading zur Laufzeit
**Trainer Notes:** Prototype in Java weniger gebräuchlich als in anderen Sprachen. Clone() vs. Copy Constructor.

#### Slide 24: Prototype Pattern - Implementation
**Registry-basierte Implementierung:**
```java
class CustomerPrototypeRegistry {
    Map<String, Customer> prototypes = new HashMap<>();
    
    Customer getCustomer(String type) {
        return prototypes.get(type).clone();
    }
}
```
**Trainer Notes:** Deep vs. Shallow Copy Problem besprechen. Immutable Objects als Alternative.

#### Slide 25: Singleton Pattern - Problem & Solution
**Global Access Point:**
```java
class ConfigurationManager {
    private static ConfigurationManager instance;
    
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
}
```
**Trainer Notes:** Singleton als Anti-Pattern diskutieren. Testing-Probleme, Hidden Dependencies.

#### Slide 26: Singleton Pattern - Modern Alternatives
**Dependency Injection statt Singleton:**
```java
@Service
class ConfigurationManager {
    // Spring/DI container manages lifecycle
}
```
**Trainer Notes:** DI-Container als bessere Lösung für Lifecycle-Management. Testbarkeit verbessert.

#### Slide 27: Singleton Pattern - Thread-Safe Varianten
**Enum Singleton (empfohlen):**
```java
enum ConfigurationManager {
    INSTANCE;
    
    public void loadConfiguration() { ... }
}
```
**Trainer Notes:** Enum als elegante Thread-safe Lösung. Joshua Bloch Empfehlung.

#### Slide 28: Creational Patterns - Vergleich
**Decision Matrix:**
| Pattern | Flexibilität | Komplexität | Use Case |
|---------|-------------|-------------|----------|
| Factory Method | Mittel | Niedrig | Polymorphe Erzeugung |
| Abstract Factory | Hoch | Hoch | Produktfamilien |
| Builder | Hoch | Mittel | Komplexe Konstruktion |
| Prototype | Mittel | Mittel | Teure Objekterstellung |
**Trainer Notes:** Patterns nicht isoliert betrachten. Kombinationen sind häufig.

#### Slide 29: Creational Patterns - Anti-Patterns
**Häufige Fehler:**
- Factory für jeden Objekt-Typ
- Builder für einfache Objekte
- Singleton Overuse
- Missing Abstraction (direkte Klassen-Abhängigkeiten)
**Trainer Notes:** Over-engineering vermeiden. YAGNI-Prinzip (You Ain't Gonna Need It).

#### Slide 30: Hands-On Exercise - Creational Patterns
**Übung: Telekom Service Factory**
- Implementierung einer Service-Factory für verschiedene Telekom-Services
- Integration von Builder Pattern für Service-Konfiguration
- Code-Review und Diskussion
**Trainer Notes:** 30-45 Minuten praktische Arbeit. Gruppen à 2-3 Personen.

### STRUCTURAL PATTERNS SECTION (25-30 slides)

#### Slide 31: Structural Patterns - Einführung
**Zweck:** Komposition von Objekten und Klassen strukturieren
**Die 7 Structural Patterns:**
- Adapter, Bridge, Composite, Decorator
- Facade, Flyweight, Proxy
**Trainer Notes:** Struktur vs. Verhalten unterscheiden. Composition over Inheritance Prinzip.

#### Slide 32: Adapter Pattern - Problem
**Interface-Inkompatibilität:**
```java
// Legacy System
class OldPaymentService {
    void makePayment(String account, double amount) { ... }
}

// New System erwarte
interface PaymentProcessor {
    void processPayment(PaymentRequest request);
}
```
**Trainer Notes:** Legacy-Integration als häufiges Enterprise-Problem. Wrapper vs. Adapter.

#### Slide 33: Adapter Pattern - Class vs Object Adapter
**Object Adapter (bevorzugt):**
```java
class PaymentAdapter implements PaymentProcessor {
    private OldPaymentService legacyService;
    
    public void processPayment(PaymentRequest request) {
        legacyService.makePayment(
            request.getAccount(), 
            request.getAmount()
        );
    }
}
```
**Trainer Notes:** Composition vs. Inheritance für Adapter. Object Adapter flexibler.

#### Slide 34: Adapter Pattern - Telekom Use Cases
**Praktische Anwendung:**
- CRM-System Integration (alte vs. neue APIs)
- Billing-System Adaption für verschiedene Tarif-Systeme
- Third-Party Service Integration (Payment, Messaging)
**Trainer Notes:** API-Versionierung als Adapter-Trigger. Backward Compatibility.

#### Slide 35: Bridge Pattern - Problem
**Abstraktion von Implementation trennen:**
```java
// Problematisch: Kartesisches Produkt
class WindowsButton extends Button { ... }
class MacButton extends Button { ... }
class WindowsCheckbox extends Checkbox { ... }
class MacCheckbox extends Checkbox { ... }
// n*m Kombinationen!
```
**Trainer Notes:** Explosion der Subklassen vermeiden. Zwei Dimensionen der Variation.

#### Slide 36: Bridge Pattern - Lösung
**Bridge Struktur:**
```java
abstract class UIControl {
    protected UIImplementation impl;
    
    protected UIControl(UIImplementation impl) {
        this.impl = impl;
    }
}

interface UIImplementation {
    void render();
}
```
**Trainer Notes:** "Prefer composition over inheritance" praktisch angewendet.

#### Slide 37: Bridge Pattern - Telekom Microservices
**Service Abstraction:**
```java
abstract class NotificationService {
    protected NotificationChannel channel;
    
    abstract void sendNotification(Message msg);
}
```
**Different Channels:** SMS, Email, Push, In-App
**Trainer Notes:** Microservices als Bridge Implementation. Service Discovery Pattern Verbindung.

#### Slide 38: Composite Pattern - Tree Structures
**Problem:** Einzelobjekte und Kompositionen einheitlich behandeln
```java
interface MenuComponent {
    void display();
    void add(MenuComponent component);
    void remove(MenuComponent component);
}
```
**Trainer Notes:** GUI-Komponenten, File-Systeme als klassische Beispiele.

#### Slide 39: Composite Pattern - Telekom Org Structure
**Organizational Hierarchy:**
```java
interface OrganizationUnit {
    double getBudget();
    int getEmployeeCount();
    void addUnit(OrganizationUnit unit);
}
```
**Trainer Notes:** Telekom-Struktur: Divisions → Departments → Teams als Composite.

#### Slide 40: Decorator Pattern - Problem
**Verhalten zur Laufzeit erweitern ohne Vererbung:**
```java
// Problematisch: Subklassen-Explosion
class EncryptedCompressedLoggedFileProcessor extends 
    CompressedLoggedFileProcessor { ... }
```
**Trainer Notes:** Flexible Kombination verschiedener Behaviors. Chain of Decorators.

#### Slide 41: Decorator Pattern - Implementation
**Decorator Structure:**
```java
interface DataProcessor {
    String process(String data);
}

class LoggingDecorator implements DataProcessor {
    private DataProcessor wrapped;
    
    public String process(String data) {
        log("Processing: " + data);
        return wrapped.process(data);
    }
}
```
**Trainer Notes:** Java I/O Streams als Decorator Beispiel. Transparent wrapping.

#### Slide 42: Decorator Pattern - Telekom Services
**Service Enhancement:**
```java
ServiceProcessor processor = new BaseServiceProcessor();
processor = new LoggingDecorator(processor);
processor = new MetricsDecorator(processor);
processor = new SecurityDecorator(processor);
```
**Trainer Notes:** Cross-cutting Concerns als Decorators. AOP-Alternative.

#### Slide 43: Facade Pattern - Subsystem Simplification
**Komplexe Subsysteme vereinfachen:**
```java
class TelekomServiceFacade {
    private CustomerService customerService;
    private BillingService billingService;
    private ContractService contractService;
    
    public void processNewCustomer(CustomerData data) {
        // Koordiniert alle Services
    }
}
```
**Trainer Notes:** API Gateway als moderne Facade. Service Orchestration.

#### Slide 44: Facade Pattern - Enterprise Integration
**System Integration vereinfachen:**
- Legacy-System Access über einheitliche Facade
- Microservices Orchestration
- API Versioning und Backward Compatibility
**Trainer Notes:** Facade als Integration Layer. Anti-Corruption Layer Pattern (DDD).

#### Slide 45: Proxy Pattern - Varianten
**Die 4 Proxy-Typen:**
- **Virtual Proxy:** Lazy Loading teurer Objekte
- **Protection Proxy:** Zugriffskontrolle
- **Remote Proxy:** Entfernte Objekte lokal repräsentieren
- **Smart Proxy:** Zusätzliche Funktionalität (Caching, Logging)
**Trainer Notes:** JPA/Hibernate als Virtual Proxy Beispiel.

#### Slide 46: Proxy Pattern - Security Proxy
**Access Control Implementation:**
```java
class SecurityProxy implements ServiceInterface {
    private RealService realService;
    private SecurityManager security;
    
    public void performOperation(User user) {
        if (security.hasPermission(user, "OPERATION")) {
            realService.performOperation(user);
        } else {
            throw new UnauthorizedException();
        }
    }
}
```
**Trainer Notes:** Spring Security als Proxy-basiertes Framework.

#### Slide 47: Flyweight Pattern - Memory Optimization
**Intrinsic vs. Extrinsic State:**
```java
class CharacterFlyweight {
    private char character;  // intrinsic
    private Font font;       // intrinsic
    
    void render(int x, int y, Color color) {  // extrinsic
        // Render character at position with color
    }
}
```
**Trainer Notes:** Memory-intensive Anwendungen. String Pool in Java als Flyweight.

#### Slide 48: Structural Patterns - Kombinationen
**Patterns zusammen verwenden:**
- Facade + Adapter für Legacy-Integration
- Decorator + Composite für UI-Frameworks
- Proxy + Bridge für Remote Services
**Trainer Notes:** Real-world applications verwenden Pattern-Kombinationen.

#### Slide 49: Structural Patterns - Decision Guide
**Wann welches Pattern:**
- **Adapter:** Bestehende Interfaces anpassen
- **Bridge:** Abstraktion/Implementation entkoppeln
- **Composite:** Tree-Strukturen einheitlich behandeln
- **Decorator:** Verhalten flexibel erweitern
- **Facade:** Komplexe Systeme vereinfachen
- **Proxy:** Zugriff kontrollieren oder optimieren
**Trainer Notes:** Context-driven Pattern Selection. Problemstellung analysieren.

#### Slide 50: Hands-On Exercise - Structural Patterns
**Übung: Telekom Service Layer**
- Facade für Service-Integration implementieren
- Decorator für Cross-cutting Concerns hinzufügen
- Adapter für Legacy-System Integration
**Trainer Notes:** 45-60 Minuten praktische Arbeit. Real-world Szenario.

### BEHAVIORAL PATTERNS SECTION (30-35 slides)

#### Slide 51: Behavioral Patterns - Einführung
**Zweck:** Kommunikation und Verantwortlichkeiten zwischen Objekten
**Die wichtigsten Behavioral Patterns:**
- Strategy, Template Method, Observer
- Command, Chain of Responsibility, State
- Visitor, Iterator, Mediator, Memento
**Trainer Notes:** Verhalten vs. Struktur. Algorithmen und Workflow-Management.

#### Slide 52: Strategy Pattern - Problem
**Algorithmus-Varianten zur Laufzeit wählen:**
```java
// Problematisch: Switch in Business Logic
class PricingCalculator {
    double calculatePrice(Order order, String customerType) {
        switch(customerType) {
            case "PREMIUM": return order.getAmount() * 0.9;
            case "BUSINESS": return order.getAmount() * 0.95;
            default: return order.getAmount();
        }
    }
}
```
**Trainer Notes:** Open/Closed Principle Verletzung. Neue Strategien erfordern Code-Änderung.

#### Slide 53: Strategy Pattern - Lösung
**Strategy Interface:**
```java
interface PricingStrategy {
    double calculatePrice(Order order);
}

class PremiumPricingStrategy implements PricingStrategy {
    public double calculatePrice(Order order) {
        return order.getAmount() * 0.9;
    }
}
```
**Trainer Notes:** Delegation statt Vererbung. Runtime-Austauschbarkeit.

#### Slide 54: Strategy Pattern - Context
**Strategy Context:**
```java
class PricingCalculator {
    private PricingStrategy strategy;
    
    void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    
    double calculatePrice(Order order) {
        return strategy.calculatePrice(order);
    }
}
```
**Trainer Notes:** Context kennt konkrete Strategies nicht. Lose Kopplung.

#### Slide 55: Strategy Pattern - Telekom Billing
**Real-world Application:**
```java
// Tarif-Berechnungsstrategien
interface TariffCalculationStrategy {
    Amount calculateCost(Usage usage);
}

class FlatRateStrategy implements TariffCalculationStrategy { ... }
class PayPerUseStrategy implements TariffCalculationStrategy { ... }
class HybridStrategy implements TariffCalculationStrategy { ... }
```
**Trainer Notes:** Business Rules als Strategies. Regulatory Changes leichter umsetzbar.

#### Slide 56: Template Method Pattern - Problem
**Algorithmus-Skelett mit variablen Schritten:**
```java
// Duplizierter Code in ähnlichen Workflows
class PrivateCustomerOnboarding {
    void processCustomer() {
        validateData();
        createPrivateAccount();
        setupPrivateServices();
        sendWelcomeMail();
    }
}
```
**Trainer Notes:** Similar Steps, Different Implementation. Hollywood Principle.

#### Slide 57: Template Method Pattern - Solution
**Abstract Template:**
```java
abstract class CustomerOnboarding {
    final void processCustomer() {  // Template Method
        validateData();
        createAccount();        // Abstract
        setupServices();        // Abstract  
        if (requiresWelcomeMail()) {  // Hook
            sendWelcomeMail();
        }
    }
    
    abstract void createAccount();
    protected boolean requiresWelcomeMail() { return true; }
}
```
**Trainer Notes:** Final Template Method prevents overriding. Hooks für optionale Steps.

#### Slide 58: Observer Pattern - Problem
**Loose Coupling für Notifications:**
```java
// Problematisch: Tight Coupling
class OrderProcessor {
    void processOrder(Order order) {
        // Process order
        emailService.sendConfirmation(order);  // Tight coupling
        billingService.createInvoice(order);   // Tight coupling
        inventoryService.updateStock(order);   // Tight coupling
    }
}
```
**Trainer Notes:** Subject weiß zu viel über Observers. SRP Violation.

#### Slide 59: Observer Pattern - Solution
**Publisher-Subscriber:**
```java
interface OrderObserver {
    void orderProcessed(Order order);
}

class OrderProcessor {
    private List<OrderObserver> observers = new ArrayList<>();
    
    void addObserver(OrderObserver observer) {
        observers.add(observer);
    }
    
    void processOrder(Order order) {
        // Process order
        notifyObservers(order);
    }
}
```
**Trainer Notes:** Subject unaware of concrete Observers. Event-driven Architecture.

#### Slide 60: Observer Pattern - Java Implementation
**Using Java's Observer API:**
```java
class OrderProcessor extends Observable {
    void processOrder(Order order) {
        // Process order
        setChanged();
        notifyObservers(order);
    }
}
```
**Trainer Notes:** java.util.Observer deprecated since Java 9. Use PropertyChangeListener or custom.

#### Slide 61: Observer Pattern - Modern Event Systems
**Spring Application Events:**
```java
@Component
class OrderProcessor {
    @Autowired
    ApplicationEventPublisher publisher;
    
    void processOrder(Order order) {
        // Process order
        publisher.publishEvent(new OrderProcessedEvent(order));
    }
}
```
**Trainer Notes:** Modern frameworks provide Observer infrastructure. Async processing möglich.

#### Slide 62: Command Pattern - Problem
**Requests als Objekte behandeln:**
```java
// GUI Button directly calls service
class SaveButton {
    void onClick() {
        documentService.save();  // Direct coupling
    }
}
```
**Trainer Notes:** Undo/Redo schwer implementierbar. Request queueing nicht möglich.

#### Slide 63: Command Pattern - Solution
**Command Interface:**
```java
interface Command {
    void execute();
    void undo();
}

class SaveDocumentCommand implements Command {
    private DocumentService service;
    private Document document;
    private Document previousState;
    
    void execute() {
        previousState = document.clone();
        service.save(document);
    }
    
    void undo() {
        document.restore(previousState);
    }
}
```
**Trainer Notes:** Request als First-Class Object. Undo/Redo, Logging, Queueing möglich.

#### Slide 64: Command Pattern - Macro Commands
**Composite Commands:**
```java
class MacroCommand implements Command {
    private List<Command> commands;
    
    void execute() {
        commands.forEach(Command::execute);
    }
    
    void undo() {
        // Reverse order for undo
        Collections.reverse(commands);
        commands.forEach(Command::undo);
        Collections.reverse(commands);
    }
}
```
**Trainer Notes:** Composite Pattern Kombination. Transactional behavior.

#### Slide 65: Chain of Responsibility - Problem
**Request durch Handler-Chain leiten:**
```java
// Problematisch: Handler-Reihenfolge hartcodiert
class AuthenticationHandler {
    void handleRequest(Request request) {
        if (authenticate(request)) {
            authorizationHandler.handle(request);
        }
    }
}
```
**Trainer Notes:** Handler-Chain zur Laufzeit konfigurierbar machen.

#### Slide 66: Chain of Responsibility - Solution
**Handler Chain:**
```java
abstract class RequestHandler {
    private RequestHandler next;
    
    void setNext(RequestHandler next) {
        this.next = next;
    }
    
    final void handleRequest(Request request) {
        if (canHandle(request)) {
            doHandle(request);
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
    
    abstract boolean canHandle(Request request);
    abstract void doHandle(Request request);
}
```
**Trainer Notes:** Flexible Handler-Konfiguration. Pipeline Pattern ähnlich.

#### Slide 67: Chain of Responsibility - Web Filters
**Servlet Filter Chain:**
```java
class AuthenticationFilter implements Filter {
    void doFilter(ServletRequest request, ServletResponse response, 
                  FilterChain chain) {
        if (authenticate(request)) {
            chain.doFilter(request, response);  // Continue chain
        } else {
            response.sendError(401);
        }
    }
}
```
**Trainer Notes:** Web Filter als praktisches Beispiel. Spring Security Filter Chain.

#### Slide 68: State Pattern - Problem
**State-dependent Behavior:**
```java
// Problematisch: State in Conditionals
class Document {
    enum State { DRAFT, REVIEW, PUBLISHED }
    private State state;
    
    void publish() {
        if (state == DRAFT) {
            // Send to review
        } else if (state == REVIEW) {
            // Publish document
        } else {
            throw new IllegalStateException();
        }
    }
}
```
**Trainer Notes:** State Machine Logic verteilt. Schwer erweiterbar.

#### Slide 69: State Pattern - Solution
**State as Objects:**
```java
interface DocumentState {
    void publish(Document doc);
    void reject(Document doc);
}

class DraftState implements DocumentState {
    void publish(Document doc) {
        doc.setState(new ReviewState());
    }
}
```
**Trainer Notes:** State Transitions in State Objects. Finite State Machine.

#### Slide 70: Visitor Pattern - Problem
**Operations auf Object Structure:**
```java
// Neue Operation erfordert Änderung aller Klassen
interface Element {
    void accept(Visitor visitor);
    void print();      // Original operation
    void validate();   // New operation - changes all classes!
}
```
**Trainer Notes:** Open/Closed Principle für Operations. AST Processing als Beispiel.

#### Slide 71: Visitor Pattern - Solution
**Double Dispatch:**
```java
interface Visitor {
    void visitCustomer(Customer customer);
    void visitProduct(Product product);
}

interface Element {
    void accept(Visitor visitor);
}

class Customer implements Element {
    void accept(Visitor visitor) {
        visitor.visitCustomer(this);
    }
}
```
**Trainer Notes:** New Operations ohne Element-Changes. Double Dispatch Mechanism.

#### Slide 72: Iterator Pattern - Collection Traversal
**Einheitlicher Zugriff auf Collections:**
```java
interface Iterator<T> {
    boolean hasNext();
    T next();
}

class CustomerCollection implements Iterable<Customer> {
    public Iterator<Customer> iterator() {
        return new CustomerIterator();
    }
}
```
**Trainer Notes:** Java Collections Framework basiert auf Iterator. Enhanced for-loop.

#### Slide 73: Mediator Pattern - Object Communication
**Centralized Communication:**
```java
interface ChatMediator {
    void sendMessage(String msg, User user);
}

class ChatRoom implements ChatMediator {
    private List<User> users;
    
    void sendMessage(String msg, User sender) {
        users.stream()
             .filter(u -> u != sender)
             .forEach(u -> u.receive(msg));
    }
}
```
**Trainer Notes:** Loose Coupling zwischen Colleagues. GUI Component Communication.

#### Slide 74: Memento Pattern - State Snapshots
**Undo/Redo Functionality:**
```java
class DocumentMemento {
    private final String content;
    private final Date timestamp;
    
    DocumentMemento(String content) {
        this.content = content;
        this.timestamp = new Date();
    }
}

class Document {
    DocumentMemento createMemento() {
        return new DocumentMemento(content);
    }
    
    void restore(DocumentMemento memento) {
        this.content = memento.getContent();
    }
}
```
**Trainer Notes:** Encapsulation nicht verletzen. Originator-Memento-Caretaker.

#### Slide 75: Behavioral Patterns - Pattern Interactions
**Häufige Kombinationen:**
- **Observer + Command:** Event-driven Commands
- **Strategy + Template Method:** Pluggable Algorithms in Template
- **Chain + Command:** Command Pipeline
- **State + Strategy:** State-specific Strategies
**Trainer Notes:** Real applications combine multiple patterns. Architecture emerges from pattern combinations.

#### Slide 76: Behavioral Patterns - Enterprise Usage
**Telekom-spezifische Anwendungen:**
- **Workflow Engines:** State + Command + Observer
- **Business Rules:** Strategy + Chain of Responsibility  
- **Event Processing:** Observer + Mediator
- **Configuration Management:** Strategy + Template Method
**Trainer Notes:** Enterprise Patterns als Behavioral Pattern Anwendungen.

#### Slide 77: Hands-On Exercise - Behavioral Patterns
**Übung: Order Processing Workflow**
- State Pattern für Order States implementieren
- Observer Pattern für Event Notifications
- Strategy Pattern für verschiedene Pricing-Modelle
**Trainer Notes:** 60-90 Minuten. Komplexere Übung mit Pattern-Kombination.

### ADVANCED PATTERNS SECTION (20-25 slides)

#### Slide 78: Advanced Patterns - Einführung
**Enterprise-spezifische Patterns:**
- **Data Access:** Repository, Unit of Work, Data Mapper
- **Domain Logic:** Domain Model, Service Layer
- **Distribution:** Remote Facade, Data Transfer Object
- **Architecture:** CQRS, Event Sourcing, Microservices
**Trainer Notes:** Patterns für große, verteilte Systeme. Martin Fowler's "Patterns of Enterprise Application Architecture".

#### Slide 79: Repository Pattern - Problem
**Data Access Abstraction:**
```java
// Problematisch: Domain Logic gekoppelt mit Data Access
class CustomerService {
    void processCustomer(Long id) {
        // SQL scattered throughout business logic
        String sql = "SELECT * FROM customers WHERE id = ?";
        Customer customer = jdbcTemplate.queryForObject(sql, Customer.class, id);
        // Business logic
    }
}
```
**Trainer Notes:** Domain Logic unabhängig von Persistence. Testability durch Mocking.

#### Slide 80: Repository Pattern - Solution
**Repository Interface:**
```java
interface CustomerRepository {
    Customer findById(Long id);
    List<Customer> findByType(CustomerType type);
    void save(Customer customer);
    void delete(Customer customer);
}

class JpaCustomerRepository implements CustomerRepository {
    @PersistenceContext
    EntityManager em;
    
    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }
}
```
**Trainer Notes:** Collection-like Interface für Domain Objects. Implementation austauschbar.

#### Slide 81: Repository Pattern - Specification
**Flexible Queries:**
```java
interface Specification<T> {
    boolean isSatisfiedBy(T candidate);
    Specification<T> and(Specification<T> other);
    Specification<T> or(Specification<T> other);
}

class ActiveCustomerSpecification implements Specification<Customer> {
    public boolean isSatisfiedBy(Customer customer) {
        return customer.isActive();
    }
}
```
**Trainer Notes:** Query Object Pattern Kombination. Spring Data Specifications.

#### Slide 82: Unit of Work Pattern - Problem
**Transaction Management:**
```java
// Problematisch: Transaction Logic verteilt
void transferCustomer(Long fromId, Long toId, double amount) {
    beginTransaction();
    try {
        Customer from = repository.findById(fromId);
        Customer to = repository.findById(toId);
        from.debit(amount);
        to.credit(amount);
        repository.save(from);
        repository.save(to);
        commitTransaction();
    } catch (Exception e) {
        rollbackTransaction();
    }
}
```
**Trainer Notes:** Transaction boundaries not clear. Change tracking manual.

#### Slide 83: Unit of Work Pattern - Solution
**Automatic Change Tracking:**
```java
class UnitOfWork {
    private Set<Entity> newEntities = new HashSet<>();
    private Set<Entity> dirtyEntities = new HashSet<>();
    private Set<Entity> removedEntities = new HashSet<>();
    
    void registerNew(Entity entity) { newEntities.add(entity); }
    void registerDirty(Entity entity) { dirtyEntities.add(entity); }
    void registerRemoved(Entity entity) { removedEntities.add(entity); }
    
    void commit() {
        // Insert, update, delete in correct order
    }
}
```
**Trainer Notes:** JPA EntityManager als Unit of Work. Hibernate Session.

#### Slide 84: CQRS - Command Query Responsibility Segregation
**Separate Read and Write Models:**
```java
// Command Side (Write Model)
interface CustomerCommandService {
    void createCustomer(CreateCustomerCommand cmd);
    void updateCustomer(UpdateCustomerCommand cmd);
}

// Query Side (Read Model)
interface CustomerQueryService {
    CustomerView findById(Long id);
    List<CustomerSummary> searchCustomers(SearchCriteria criteria);
}
```
**Trainer Notes:** Different optimization for reads vs. writes. Event sourcing compatibility.

#### Slide 85: CQRS - Implementation Patterns
**CQRS Variations:**
- **Simple CQRS:** Same database, different models
- **CQRS with separate databases:** Read/Write database separation
- **Event Sourced CQRS:** Commands generate events, projections for queries
**Trainer Notes:** Start simple, evolve based on scalability needs.

#### Slide 86: Event Sourcing - Problem
**Audit Trail and Reproducibility:**
```java
// Problematisch: State changes not auditable
class Account {
    private double balance;
    
    void debit(double amount) {
        this.balance -= amount;  // Previous state lost
    }
}
```
**Trainer Notes:** Debugging production issues. Compliance requirements. Temporal queries.

#### Slide 87: Event Sourcing - Solution
**Events as First-Class Citizens:**
```java
abstract class AccountEvent {
    private final LocalDateTime timestamp;
    private final Long accountId;
}

class AccountDebitedEvent extends AccountEvent {
    private final double amount;
    private final String reason;
}

class Account {
    private double balance;
    
    void apply(AccountDebitedEvent event) {
        this.balance -= event.getAmount();
    }
}
```
**Trainer Notes:** Events are immutable facts. Aggregate reconstruction by replaying events.

#### Slide 88: Event Sourcing - Event Store
**Event Storage:**
```java
interface EventStore {
    void saveEvents(String streamId, List<Event> events, 
                   long expectedVersion);
    List<Event> getEventsForStream(String streamId);
    List<Event> getEventsForStream(String streamId, 
                                 long fromVersion);
}
```
**Trainer Notes:** Event Store als specialized database. Snapshots for performance.

#### Slide 89: Microservices Patterns - Overview
**Common Microservice Patterns:**
- **Service Discovery:** Registry for service locations
- **API Gateway:** Single entry point for clients
- **Circuit Breaker:** Failure handling between services
- **Saga Pattern:** Distributed transactions
**Trainer Notes:** Microservices architecture patterns. Distributed system complexity.

#### Slide 90: API Gateway Pattern
**Single Entry Point:**
```java
@RestController
class ApiGateway {
    @Autowired
    CustomerService customerService;
    
    @Autowired
    OrderService orderService;
    
    @GetMapping("/customers/{id}/orders")
    CustomerOrdersView getCustomerOrders(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        List<Order> orders = orderService.findByCustomerId(id);
        return new CustomerOrdersView(customer, orders);
    }
}
```
**Trainer Notes:** Backend for Frontend (BFF) pattern. Cross-cutting concerns centralization.

#### Slide 91: Circuit Breaker Pattern
**Resilience Against Service Failures:**
```java
@Component
class CustomerServiceClient {
    private CircuitBreaker circuitBreaker;
    
    @CircuitBreaker(name = "customer-service")
    @TimeLimiter(name = "customer-service")
    @Retry(name = "customer-service")
    Customer getCustomer(Long id) {
        return restTemplate.getForObject(
            "/customers/{id}", Customer.class, id);
    }
    
    Customer fallbackCustomer(Long id, Exception ex) {
        return Customer.unknown(id);
    }
}
```
**Trainer Notes:** Netflix Hystrix, Resilience4j. Fail-fast principle.

#### Slide 92: Saga Pattern - Distributed Transactions
**Long-Running Business Processes:**
```java
class OrderSaga {
    @SagaOrchestrationStart
    void handle(OrderCreatedEvent event) {
        commandGateway.send(new ReserveProductsCommand(event.getOrderId()));
    }
    
    @SagaOrchestrationHandler
    void handle(ProductsReservedEvent event) {
        commandGateway.send(new ProcessPaymentCommand(event.getOrderId()));
    }
    
    @SagaOrchestrationHandler
    void handle(PaymentFailedEvent event) {
        commandGateway.send(new ReleaseProductsCommand(event.getOrderId()));
    }
}
```
**Trainer Notes:** Orchestration vs. Choreography. Compensating actions for rollback.

#### Slide 93: Domain-Driven Design Patterns
**Strategic Patterns:**
- **Bounded Context:** Explicit boundaries for models
- **Context Map:** Relationships between contexts
- **Anti-Corruption Layer:** Protect domain from external systems
**Tactical Patterns:**
- **Aggregate:** Consistency boundary
- **Entity, Value Object:** Domain object types
- **Domain Service:** Domain logic without natural entity home
**Trainer Notes:** DDD als Meta-Pattern für Enterprise Architecture.

#### Slide 94: Integration Patterns
**Enterprise Integration:**
- **Message Channel:** Asynchronous communication
- **Message Router:** Content-based routing
- **Message Translator:** Data format transformation
- **Message Filter:** Selective message processing
**Trainer Notes:** Enterprise Integration Patterns (Hohpe & Woolf). Apache Camel examples.

#### Slide 95: Performance Patterns
**Scalability and Performance:**
- **Lazy Loading:** Load data on demand
- **Eager Loading:** Load related data proactively
- **Caching Patterns:** Different caching strategies
- **Connection Pooling:** Resource management
- **Bulk Operations:** Batch processing for efficiency
**Trainer Notes:** Performance patterns often conflict with clean design. Trade-offs.

#### Slide 96: Security Patterns
**Common Security Patterns:**
- **Authentication Gateway:** Centralized authentication
- **Authorization Filter:** Access control
- **Secure Session Management:** Session handling
- **Input Validation:** Data sanitization
**Trainer Notes:** Security as cross-cutting concern. OWASP Top 10 considerations.

#### Slide 97: Advanced Patterns - Anti-Patterns
**Common Mistakes:**
- **God Object:** Single object knows/does too much
- **Anemic Domain Model:** Domain objects without behavior
- **Chatty Interface:** Too many fine-grained service calls
- **Distributed Monolith:** Microservices with tight coupling
**Trainer Notes:** Anti-patterns as learning tool. Refactoring strategies.

#### Slide 98: Pattern Selection Guide
**Decision Criteria:**
- **Complexity:** Start simple, evolve
- **Team Experience:** Use patterns team understands
- **Performance Requirements:** Some patterns have overhead
- **Maintainability:** Long-term maintenance considerations
**Trainer Notes:** Architecture decisions are trade-offs. Document rationale.

### SUMMARY AND CONCLUSION SECTION (5-8 slides)

#### Slide 99: Pattern Categories Recap
**Creational Patterns:** Object creation flexibility
**Structural Patterns:** Object composition and relationships  
**Behavioral Patterns:** Communication and responsibilities
**Enterprise Patterns:** Large-scale system architecture
**Trainer Notes:** Patterns work together. Real systems combine multiple patterns.

#### Slide 100: Pattern Selection Decision Tree
**When to use which pattern:**
1. **Problem Analysis:** What are you trying to solve?
2. **Context Assessment:** System size, team size, constraints
3. **Pattern Research:** Which patterns address similar problems?
4. **Trade-off Analysis:** Benefits vs. complexity
5. **Team Consensus:** Shared understanding crucial
**Trainer Notes:** Decision process more important than memorizing all patterns.

#### Slide 101: Common Pitfalls and How to Avoid Them
**Pattern Overuse:**
- Don't force patterns where simple solutions work
- YAGNI (You Aren't Gonna Need It) principle
**Pattern Underuse:**
- Recognize recurring problems
- Invest time in pattern learning

**Implementation Mistakes:**
- Understand pattern intent, not just structure
- Adapt patterns to your specific context
**Trainer Notes:** Balance between over-engineering and under-engineering.

#### Slide 102: Integration with Clean Architecture
**How Patterns Support Clean Architecture:**
- **Dependency Inversion:** Repository, Strategy patterns
- **Single Responsibility:** Command, Observer patterns  
- **Open/Closed Principle:** Strategy, Template Method
- **Interface Segregation:** Adapter, Facade patterns
**Trainer Notes:** Patterns as tools for implementing SOLID principles.

#### Slide 103: Modern Framework Integration
**Patterns in Spring Framework:**
- **Dependency Injection:** Service Locator pattern evolution
- **AOP:** Decorator, Proxy patterns
- **Spring Data:** Repository pattern
- **Spring Security:** Chain of Responsibility, Proxy

**Patterns in React/Frontend:**
- **Observer:** State management (Redux)
- **Strategy:** Different rendering strategies
- **Command:** Action dispatching
**Trainer Notes:** Frameworks implement patterns. Understanding helps with framework usage.

#### Slide 104: Next Steps and Continuous Learning
**Recommended Resources:**
- **Books:** Gang of Four, Martin Fowler (PoEAA), Uncle Bob (Clean Architecture)
- **Online:** Refactoring.guru, sourcemaking.com
- **Practice:** Refactor existing code using patterns
- **Community:** Architecture discussion groups, conferences

**Action Items:**
- Identify one pattern to apply in current project
- Set up regular architecture reviews
- Create team coding standards incorporating patterns
**Trainer Notes:** Learning continues after workshop. Practical application crucial.

#### Slide 105: Q&A and Discussion
**Discussion Topics:**
- Which patterns are most relevant for your current projects?
- What are your biggest architecture challenges?
- How will you introduce patterns to your team?

**Workshop Feedback:**
- What was most valuable?
- What would you change?
- Additional topics needed?
**Trainer Notes:** Collect feedback for workshop improvement. Encourage ongoing questions.

#### Slide 106: Thank You and Contact
**Workshop Summary:**
- 4 days intensive pattern training
- 40+ design patterns covered
- Practical examples and hands-on exercises
- Telekom-specific use cases

**Stay Connected:**
- Follow-up questions welcome
- Architecture consulting available
- Advanced workshops planned
**Trainer Notes:** Maintain relationship with participants. Offer ongoing support.

---

## Trainer Notes Summary

This presentation covers 106 slides with comprehensive content for a 4-day architecture training. Each slide includes detailed trainer notes with:

- **Teaching Points:** Key concepts to emphasize
- **Examples:** Practical, Telekom-specific examples
- **Discussion Topics:** Interactive elements for engagement
- **Code Examples:** Live coding opportunities
- **Connections:** How patterns relate to each other and to enterprise concerns

The presentation is designed to be highly interactive with participants, using real-world scenarios and hands-on exercises throughout the four days.