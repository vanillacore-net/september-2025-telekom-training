# Bridge Pattern - Trainer Notes

## Lernziele
- Abstraktion von Implementierung trennen
- Provider-Lock-in durch Bridge Pattern vermeiden
- Kombinatorische Explosion bei Abstraktion × Implementierung verstehen

## Problem Demonstration (Initial)

### Tight Coupling zu Payment Providern
```java
// PROBLEM: Client muss jeden Provider anders behandeln
public String processPayment(String method, String token, BigDecimal amount) {
    if ("PAYPAL".equals(method)) {
        // PayPal spezifische API
        PaypalResult result = paypalProcessor.processPaypalPayment(token, amount, currency);
        if ("COMPLETED".equals(result.getStatus())) {
            return result.getTransactionId();
        }
    } else if ("STRIPE".equals(method)) {
        // Stripe spezifische API - ganz anders!
        int cents = amount.multiply(BigDecimal.valueOf(100)).intValue();
        StripeResponse response = stripeHandler.chargeCard(token, cents, currency);
        if (response.isSuccess()) {
            return response.getId();
        }
    }
    // Für jeden neuen Provider: Mehr if-else Code!
}
```

### Code zeigen: `paymentbridge/initial/`
- `PaypalPaymentProcessor`: `processPaypalPayment()` - gibt `PaypalResult`
- `StripePaymentHandler`: `chargeCard()` - gibt `StripeResponse`  
- `OrderService`: Hässliche if-else Ketten für jeden Provider

### Probleme aufzeigen
1. **Verschiedene APIs**: Jeder Provider hat andere Methodennamen
2. **Verschiedene Parameter**: PayPal nutzt `BigDecimal`, Stripe `int cents`
3. **Verschiedene Results**: Verschiedene Response-Klassen
4. **Nicht erweiterbar**: Neuer Provider = Code in allen Clients ändern

## Lösung: Bridge Pattern (Fixed)

### Pattern-Struktur
```
Abstraction → Implementation
    ↓              ↓
RefinedAbstraction → ConcreteImplementation
```

### Unsere Implementierung
```
PaymentProcessor → PaymentImplementation
    ↓                   ↓
SimplePaymentProcessor → PaypalImplementation
LoggingPaymentProcessor → StripeImplementation
```

### Trennung demonstrieren
- **Abstraction**: Wie verarbeiten wir? (Simple, Logging, Auditing)
- **Implementation**: Womit verarbeiten wir? (PayPal, Stripe, Apple Pay)
- **Kombination**: N Abstractions × M Implementations = N×M Möglichkeiten

## Code-Demonstration

### 1. Einheitliches Interface
```java
// Alle Implementierungen bieten gleiches Interface
PaymentImplementation paypal = new PaypalImplementation();
PaymentImplementation stripe = new StripeImplementation();

// Gleiche Methoden-Signatur für beide!
PaymentResult paypalResult = paypal.processPayment(token, amount, currency);
PaymentResult stripeResult = stripe.processPayment(token, amount, currency);
```

### 2. Abstraktion variieren
```java
// Gleiche Implementation, verschiedene Abstraktion
PaymentProcessor simple = new SimplePaymentProcessor(paypal);
PaymentProcessor logging = new LoggingPaymentProcessor(paypal);

// Verschiedenes Verhalten, gleicher Provider
simple.processPayment(token, amount, currency);   // Nur Processing
logging.processPayment(token, amount, currency);  // + Audit Logs
```

### 3. Implementation zur Laufzeit wechseln
```java
// Runtime Provider-Switching
PaymentProcessor processor = new SimplePaymentProcessor(paypal);
PaymentResult result1 = processor.processPayment(...); // PayPal

// Wechsel zu Stripe - gleiche Abstraktion!
processor = new SimplePaymentProcessor(stripe);
PaymentResult result2 = processor.processPayment(...); // Stripe
```

## Hands-on Übungen

### 1. Neuen Provider hinzufügen
```java
// Apple Pay Implementation hinzufügen
public class ApplePayImplementation implements PaymentImplementation {
    @Override
    public PaymentResult processPayment(String token, BigDecimal amount, String currency) {
        // Apple Pay spezifische Logik
        return new PaymentResult("APPLE_" + System.currentTimeMillis(), 
                                true, "Apple Pay successful", "ApplePay");
    }
    
    // Funktioniert sofort mit allen existierenden Abstraktionen!
}
```

### 2. Neue Abstraktion hinzufügen
```java
// Retry-fähiger Payment Processor
public class RetryPaymentProcessor extends PaymentProcessor {
    private int maxRetries = 3;
    
    @Override
    public PaymentResult processPayment(String token, BigDecimal amount, String currency) {
        for (int i = 0; i < maxRetries; i++) {
            PaymentResult result = implementation.processPayment(token, amount, currency);
            if (result.isSuccess()) return result;
            
            // Retry-Logik
            Thread.sleep(1000 * (i + 1)); // Exponential backoff
        }
        return new PaymentResult(null, false, "Max retries exceeded", 
                               implementation.getProviderName());
    }
}
```

### 3. Kombinatorik demonstrieren
```java
// N Abstractions × M Implementations = N×M Kombinationen
PaymentImplementation[] providers = {paypal, stripe, applePay}; // 3 Provider
PaymentProcessor[] processors = {simple, logging, retry};      // 3 Abstractions

// = 3×3 = 9 Kombinationen ohne 9 verschiedene Klassen!
for (PaymentImplementation impl : providers) {
    for (PaymentProcessor proc : processors) {
        // proc.setImplementation(impl); oder neuer Konstruktor
        proc.processPayment(token, amount, currency);
    }
}
```

## Bridge vs. Strategy Pattern

### Unterschiede hervorheben
| Aspekt | Bridge | Strategy |
|--------|--------|----------|
| **Zweck** | Abstraktion von Implementation trennen | Algorithmus austauschbar machen |
| **Struktur** | Abstraktion hat Implementation | Context hat Strategy |
| **Variation** | Beide Seiten können variieren | Nur Algorithmus variiert |
| **Beispiel** | UI + Rendering Engine | Sortierung + Sort-Algorithmus |

### Bridge-Beispiel
```java
// BRIDGE: Beide Seiten entwickeln sich unabhängig
PaymentProcessor (Abstraktion) ←→ PaymentProvider (Implementation)
- Simple, Logging, Retry        - PayPal, Stripe, Apple Pay
- Entwickeln unabhängig         - Verschiedene APIs
```

### Strategy-Beispiel  
```java
// STRATEGY: Eine Familie von Algorithmen
SortContext ←→ SortStrategy
- Kontext bleibt gleich  - QuickSort, MergeSort, BubbleSort
- Nur Algorithmus wechselt
```

## Design Considerations

### 1. Interface Design
```java
// Implementation Interface sollte primitiv sein
public interface PaymentImplementation {
    PaymentResult processPayment(String token, BigDecimal amount, String currency);
    PaymentResult refundPayment(String transactionId, BigDecimal amount);
    String getProviderName();
    // Keine Provider-spezifischen Methoden!
}
```

### 2. Abstraction Design
```java
// Abstraktion kann reichere Interface haben
public abstract class PaymentProcessor {
    protected PaymentImplementation implementation;
    
    // Template methods
    public PaymentResult processPaymentWithValidation(String token, BigDecimal amount) {
        validateInput(token, amount);
        return implementation.processPayment(token, amount, "EUR");
    }
}
```

### 3. Error Handling
```java
// Unified error handling across providers
public class PaymentResult {
    private boolean success;
    private String errorCode;    // Standardized error codes
    private String errorMessage; // Provider-specific messages
    private String providerName; // For debugging
}
```

## Diskussionspunkte

### Wann Bridge verwenden?
✅ **Perfekt für**:
- Abstraktion UND Implementation variieren
- Vermeidung von Provider Lock-in
- Multiple orthogonale Dimensionen
- Runtime-Switching von Implementations

❌ **Nicht geeignet**:
- Nur eine Dimension variiert (→ Strategy)
- Stabile, wenig variierende Hierarchie
- Performance-kritisch (extra Indirektion)

### Real-World Beispiele

#### Telekom-spezifisch
- **Payment Gateway**: Verschiedene Provider (PayPal, Stripe, SEPA)
- **SMS Gateway**: Verschiedene Provider (Twilio, Amazon SNS)
- **Cloud Provider**: AWS, Azure, Google Cloud APIs
- **Database Layer**: MySQL, PostgreSQL, Oracle

#### Allgemein
- **JDBC**: Database-unabhängige Abstraktion
- **Graphics APIs**: DirectX vs. OpenGL
- **UI Frameworks**: Win32 vs. X11 vs. Cocoa
- **Messaging**: JMS mit verschiedenen Providern

## Anti-Pattern Warnings

### 1. Falsche Abstraktion
```java
// SCHLECHT - zu Provider-spezifisch
public interface PaymentImplementation {
    PaypalResult processPaypalPayment(...); // PayPal-spezifisch!
    StripeResponse chargeStripeCard(...);   // Stripe-spezifisch!
    // Verletzt Abstraktion - Implementation leakt durch
}
```

### 2. Zu viele Parameter
```java
// SCHLECHT - zu komplexes Interface
public PaymentResult processPayment(String token, BigDecimal amount, 
    String currency, String merchantId, String description,
    Map<String, String> metadata, PaymentOptions options, ...);
// Schwer zu implementieren für verschiedene Provider
```

### 3. God-Interface
```java
// SCHLECHT - Interface macht zu viel
public interface PaymentImplementation {
    PaymentResult processPayment(...);
    PaymentResult refundPayment(...);
    PaymentResult voidTransaction(...);
    PaymentResult captureAuthorization(...);
    CustomerProfile createCustomer(...);     // Anderes Concern!
    MerchantAccount setupAccount(...);       // Noch ein anderes Concern!
    // Zu viele Verantwortlichkeiten
}
```

## Code Review Checklist
- [ ] Abstraktion und Implementation klar getrennt
- [ ] Implementation-Interface provider-agnostic
- [ ] Abstraktion kann Implementation zur Laufzeit wechseln
- [ ] Neue Provider/Abstraktionen einfach hinzufügbar
- [ ] Keine Leakage von Implementation-Details
- [ ] Error Handling unified
- [ ] Tests für verschiedene Kombinationen

## Performance Considerations
- **Extra Indirektion**: Minimaler Performance-Overhead
- **Object Creation**: Abstraktion + Implementation = 2 Objekte
- **Memory**: Zusätzliche Referenz in Abstraction
- **Profiling**: Messen wenn Performance kritisch

## Zeitplanung
- Problem mit tight coupling: 10 min
- Bridge Pattern Konzept: 10 min  
- Code-Demonstration: 15 min
- Hands-on neue Provider/Abstraktion: 20 min
- Bridge vs Strategy: 5 min
- Real-World Beispiele: 5 min