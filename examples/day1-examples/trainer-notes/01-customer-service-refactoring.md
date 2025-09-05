# Customer Service Refactoring: God Class → Factory Method Pattern

## Übersicht

Dieses Beispiel demonstriert die Transformation einer God Class in ein sauberes Factory Method Pattern Design.

## Initial Version Problems

### God Class Anti-Pattern

```java
// 150+ Zeilen Monster-Klasse
public class CustomerService {
    // ALLES in einer Klasse:
    // - Customer Management
    // - Contract Creation  
    // - DSL Logic
    // - Mobile Logic
    // - Festnetz Logic
    // - Billing Logic
    // - Support Tickets
    // - etc.
}
```

### Identifizierte Code-Smells

1. **God Class**: Über 150 Zeilen, zu viele Verantwortlichkeiten
2. **Long Method**: `handleCustomerRequest()` mit 50+ Zeilen
3. **Giant Switch Statement**: Verletzt Open/Closed Principle
4. **Hardcoded Business Logic**: Keine Konfigurierbarkeit
5. **Mixed Concerns**: UI, Business Logic, Data Access vermischt
6. **Schwer testbar**: Alles private und gekoppelt

### Konkrete Probleme

```java
// PROBLEM: Giant Switch Statement
switch (requestType.toUpperCase()) {
    case "DSL_ACTIVATION":
        // 15+ Zeilen hardcoded DSL Logic
        break;
    case "MOBILE_ACTIVATION":  
        // 15+ Zeilen hardcoded Mobile Logic
        break;
    // ... viele weitere Cases
}
```

## Fixed Version Solution

### Factory Method Pattern Anwendung

```java
// Abstract Creator
public abstract class RequestHandler {
    // Template Method
    public final String handleRequest(Customer customer, Map<String, Object> params) {
        if (!isValidRequest(customer, params)) {
            return "ERROR: Invalid request parameters";
        }
        return createResponse(customer, params); // Factory Method
    }
    
    // Factory Method - von Subklassen implementiert
    protected abstract String createResponse(Customer customer, Map<String, Object> params);
}
```

### Concrete Handlers

```java
// Concrete Creator für DSL
public class DslActivationHandler extends RequestHandler {
    @Override
    protected String createResponse(Customer customer, Map<String, Object> params) {
        // Nur DSL-spezifische Logik
        // Clean, focused, testable
    }
}
```

### Factory für Handler Selection

```java
public class RequestHandlerFactory {
    private static final Map<String, RequestHandler> handlers = new HashMap<>();
    
    static {
        handlers.put("DSL_ACTIVATION", new DslActivationHandler());
        handlers.put("MOBILE_ACTIVATION", new MobileActivationHandler());
        // Einfach erweiterbar
    }
    
    public static RequestHandler createHandler(String requestType) {
        return handlers.get(requestType.toUpperCase());
    }
}
```

## Refactoring Schritte

### Schritt 1: Identify Variations
- Analysiere Switch Statement Cases
- Identifiziere verschiedene Request Types
- Dokumentiere gemeinsame Patterns

### Schritt 2: Extract Abstract Base
- Erstelle `RequestHandler` Abstract Class
- Template Method für gemeinsamen Ablauf
- Factory Method für Variationen

### Schritt 3: Create Concrete Handlers
- Ein Handler pro Request Type
- Extrahiere spezifische Logic aus Original Switch
- Single Responsibility per Handler

### Schritt 4: Implement Factory
- `RequestHandlerFactory` für Handler Selection
- Static Map für Handler Registration
- Factory Method Pattern completion

### Schritt 5: Refactor Client Code
- Replace Switch Statement mit Factory Call
- Simplify main service class
- Delegate to appropriate handlers

## Architektur Verbesserungen

### Vorher (God Class)
```
CustomerService
├── handleCustomerRequest() [50+ Zeilen]
│   ├── DSL Logic [hardcoded]
│   ├── Mobile Logic [hardcoded]  
│   ├── Festnetz Logic [hardcoded]
│   └── Giant Switch Statement
├── calculateDslFee() [hardcoded]
├── calculateMobileFee() [hardcoded]
└── [20+ weitere Methods]
```

### Nachher (Factory Method)
```
RequestHandlerFactory
├── createHandler() [Factory Method]
└── handlers Map

RequestHandler [Abstract]
├── handleRequest() [Template Method]
└── createResponse() [Factory Method]

DslActivationHandler
├── createResponse() [DSL-specific]
└── DSL business logic

MobileActivationHandler  
├── createResponse() [Mobile-specific]
└── Mobile business logic

CustomerService [Simplified]
├── handleCustomerRequest() [Clean delegation]
└── Basic customer operations
```

## Design Patterns Angewendet

### 1. Factory Method Pattern
- **Intent**: Objekterzeugung an Subklassen delegieren
- **Application**: Handler creation basierend auf Request Type
- **Benefit**: Open/Closed Principle, erweiterbar ohne Änderung

### 2. Template Method Pattern
- **Intent**: Algorithmus-Struktur definieren, Details an Subklassen
- **Application**: `handleRequest()` definiert Ablauf, `createResponse()` ist variabel
- **Benefit**: Code Reuse, konsistenter Ablauf

### 3. Strategy Pattern Elements
- **Intent**: Algorithmen austauschbar machen
- **Application**: Verschiedene Handler für verschiedene Request Types
- **Benefit**: Runtime selection, testable components

## Testing Verbesserungen

### Vorher: Schwer testbar
```java
@Test
void testGodClass() {
    CustomerService service = new CustomerService();
    // Muss ALLES testen in einem großen Test
    // Schwer zu isolieren
    // Viele Setup-Requirements
}
```

### Nachher: Granulare Tests
```java
@Test
void testDslHandlerIsolated() {
    DslActivationHandler handler = new DslActivationHandler();
    // Nur DSL Logic testen
    // Clean, focused, fast
}

@Test
void testFactorySelection() {
    RequestHandler handler = RequestHandlerFactory.createHandler("DSL_ACTIVATION");
    assertThat(handler).isInstanceOf(DslActivationHandler.class);
}
```

## Erweiterbarkeit Demo

### Neue Handler hinzufügen
```java
// 1. Create new handler
public class FiberActivationHandler extends RequestHandler {
    @Override
    protected String createResponse(Customer customer, Map<String, Object> params) {
        // Fiber-specific logic
    }
}

// 2. Register in factory
handlers.put("FIBER_ACTIVATION", new FiberActivationHandler());

// 3. Ready to use - no existing code changes!
```

## Key Takeaways

### SOLID Principles Applied
- **S**: Single Responsibility - each handler has one job
- **O**: Open/Closed - extend with new handlers, no modification  
- **L**: Liskov Substitution - all handlers work through same interface
- **I**: Interface Segregation - focused interfaces
- **D**: Dependency Inversion - depend on abstractions

### Benefits Achieved
1. **Maintainability**: Easier to understand and modify
2. **Testability**: Each component testable in isolation
3. **Extensibility**: New handlers without touching existing code
4. **Readability**: Clear separation of concerns
5. **Reusability**: Template method reused across handlers

### Learning Objectives Met
- God Class identification and refactoring
- Factory Method Pattern implementation
- Template Method Pattern application
- SOLID Principles in practice
- Code smell detection and resolution

## Diskussionspunkte für Training

1. **Was macht eine Klasse zur "God Class"?**
2. **Wann sollte Factory Method verwendet werden?**  
3. **Wie erkennt man Switch Statement Smells?**
4. **Welche anderen Patterns könnten hier verwendet werden?**
5. **Wie testet man komplexe Legacy Code?**

## Weiterführende Übungen

1. **Refactoring Challenge**: Implementiere `BillingInquiryHandler` und `TechnicalSupportHandler`
2. **Pattern Variation**: Implementiere Abstract Factory für Handler-Familien
3. **Configuration**: Mache Handler-Logic konfigurierbar statt hardcoded
4. **Testing**: Schreibe umfassende Tests für alle Handler
5. **Performance**: Analysiere Performance-Unterschiede zwischen Patterns