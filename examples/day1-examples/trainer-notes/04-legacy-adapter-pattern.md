# Legacy Integration: Direct Coupling → Adapter Pattern

## Übersicht

Dieses Beispiel demonstriert die Transformation von direkter Legacy System Integration in ein sauberes Adapter Pattern als Anti-Corruption Layer.

## Initial Version Problems

### Direct Legacy Integration Anti-Pattern

```java
public class ModernCustomerService {
    private LegacyBillingSystem legacyBilling; // TIGHT COUPLING!
    
    public String createCustomerInvoice(String customerId, double amount, LocalDate dueDate,
                                       String invoiceType, String paymentMethod) {
        try {
            // TYPE CONVERSION überall!
            Date legacyCreationDate = Date.from(LocalDateTime.now()
                .atZone(ZoneId.systemDefault()).toInstant());
            Date legacyDueDate = Date.from(dueDate.atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant());
            
            // Legacy Parameter Mapping - fehleranfällig
            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            int dueDays = (int) daysBetween;
            
            // DIRECT LEGACY CALL - Tight Coupling
            OldInvoiceRecord legacyInvoice = legacyBilling.createBillForCustomer(
                customerId, amount, legacyCreationDate,
                mapToLegacyInvoiceType(invoiceType),  // More mapping logic
                mapToLegacyPaymentMethod(paymentMethod),
                dueDays
            );
            
            return legacyInvoice.invoiceId; // Return Legacy Data!
            
        } catch (LegacyBillingException e) {
            // LEGACY EXCEPTION HANDLING - unschön
            throw new RuntimeException("Failed: " + e.getMessage() + " [" + e.getErrorCode() + "]");
        }
    }
}
```

### Identifizierte Code-Smells

1. **Tight Coupling**: Direkt an Legacy System gebunden
2. **Mixed APIs**: Moderne und Legacy Types vermischt
3. **Type Conversions**: LocalDate ↔ Date Conversions überall
4. **Legacy Exception Handling**: In Business Logic verstreut
5. **Duplicate Mapping Logic**: Nicht zentral organisiert
6. **Boolean Return Values**: Keine aussagekräftigen Errors
7. **String-based Reports**: Schwer zu verarbeiten
8. **Hard to Test**: Immer Legacy System benötigt
9. **Legacy Concerns Leak**: Durchsickern ins moderne System
10. **Hard to Replace**: Legacy System fest verdrahtet

### Konkrete Probleme

```java
// PROBLEM: Type Conversions überall verstreut
Date legacyFromDate = fromDate != null ? 
    Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
Date legacyToDate = toDate != null ? 
    Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

// PROBLEM: Boolean Return ohne Kontext
boolean success = legacyBilling.processPaymentForInvoice(invoiceId, amount, paymentDate, ref);
// Was bedeutet false? Not found? Partial payment? Unknown!

// PROBLEM: String-based Error Reporting  
String legacyReport = legacyBilling.getInvoiceStatusReport(invoiceId);
if (legacyReport.startsWith("ERROR:")) {
    throw new RuntimeException(legacyReport); // String parsing!
}
```

## Fixed Version Solution

### Modern Target Interface Definition

```java
// Clean Modern Interface - No Legacy Concerns
public interface BillingService {
    Invoice createInvoice(CreateInvoiceRequest request);
    List<Invoice> findInvoices(InvoiceSearchRequest request);
    PaymentResult processPayment(PaymentRequest request);
    InvoiceDetails getInvoiceDetails(String invoiceId);
}

// Modern Request/Response Objects
public class CreateInvoiceRequest {
    private final String customerId;
    private final double amount;
    private final LocalDate dueDate;          // Modern Types!
    private final InvoiceType invoiceType;    // Enums!
    private final PaymentMethod paymentMethod;
    // Constructor, getters...
}

public class PaymentResult {
    private final boolean success;
    private final PaymentStatus paymentStatus; // Rich Status Info!
    private final String message;              // Descriptive Message
    private final double remainingAmount;      // Structured Data
    // Constructor, getters...
}
```

### Adapter Pattern Implementation

```java
// ADAPTER PATTERN: Legacy System → Modern Interface
public class LegacyBillingAdapter implements BillingService {
    
    private final LegacyBillingSystem legacySystem; // Composition, not inheritance
    
    public LegacyBillingAdapter(LegacyBillingSystem legacySystem) {
        this.legacySystem = legacySystem;
    }
    
    @Override
    public Invoice createInvoice(CreateInvoiceRequest request) {
        try {
            // CENTRALIZED Type Conversions
            Date creationDate = convertToLegacyDate(LocalDate.now());
            int dueDays = calculateDueDays(request.getDueDate());
            
            // CENTRALIZED Parameter Mapping  
            OldInvoiceRecord legacyInvoice = legacySystem.createBillForCustomer(
                request.getCustomerId(),
                request.getAmount(),
                creationDate,
                mapInvoiceTypeToLegacy(request.getInvoiceType()),
                mapPaymentMethodToLegacy(request.getPaymentMethod()),
                dueDays
            );
            
            // CENTRALIZED Response Conversion
            return convertLegacyToModernInvoice(legacyInvoice);
            
        } catch (LegacyBillingException e) {
            // CENTRALIZED Exception Translation
            throw new BillingServiceException("Failed to create invoice", e);
        }
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        Date paymentDate = convertToLegacyDate(LocalDate.now());
        
        // Legacy System Call
        boolean success = legacySystem.processPaymentForInvoice(
            request.getInvoiceId(),
            request.getAmount(), 
            paymentDate,
            request.getPaymentReference()
        );
        
        // INTELLIGENT Result Interpretation
        return interpretLegacyPaymentResult(request, success);
    }
}
```

### Anti-Corruption Layer Features

```java
// CENTRALIZED MAPPING LOGIC - No Code Duplication
private String mapInvoiceTypeToLegacy(InvoiceType modernType) {
    switch (modernType) {
        case MONTHLY_BILL: return "MONTHLY";
        case INSTALLATION: return "INSTALL";
        case ADJUSTMENT: return "ADJUST";
        case TERMINATION: return "TERM";
        case OTHER:
        default: return "OTHER";
    }
}

private InvoiceType mapInvoiceTypeFromLegacy(String legacyType) {
    switch (legacyType) {
        case "MONTHLY": return InvoiceType.MONTHLY_BILL;
        case "INSTALL": return InvoiceType.INSTALLATION;
        case "ADJUST": return InvoiceType.ADJUSTMENT;
        case "TERM": return InvoiceType.TERMINATION;
        default: return InvoiceType.OTHER;
    }
}

// CENTRALIZED TYPE CONVERSION
private LocalDate convertLegacyDateToModern(Date legacyDate) {
    return legacyDate != null ?
        legacyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
}

private Date convertToLegacyDate(LocalDate modernDate) {
    return modernDate != null ?
        Date.from(modernDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
}

// INTELLIGENT LEGACY RESULT INTERPRETATION
private PaymentResult interpretLegacyPaymentResult(PaymentRequest request, boolean legacySuccess) {
    if (!legacySuccess) {
        return new PaymentResult(false, PaymentStatus.INVOICE_NOT_FOUND,
            "Payment failed - invoice may not exist or payment was partial", 0.0);
    }
    
    return new PaymentResult(true, PaymentStatus.FULL_PAYMENT,
        "Payment processed successfully", 0.0);
}
```

### Modern Client Code - Clean API Usage

```java
// REFACTORED: Modern Customer Service without Legacy Concerns
public class ModernCustomerService {
    
    private final BillingService billingService; // INTERFACE - Loose Coupling!
    
    public ModernCustomerService(BillingService billingService) {
        this.billingService = billingService;
    }
    
    // CLEAN: No Legacy Concerns anymore
    public String createCustomerInvoice(String customerId, double amount, LocalDate dueDate,
                                       InvoiceType invoiceType, PaymentMethod paymentMethod) {
        
        CreateInvoiceRequest request = new CreateInvoiceRequest(
            customerId, amount, dueDate, invoiceType, paymentMethod
        );
        
        Invoice invoice = billingService.createInvoice(request); // Clean Interface!
        return invoice.getInvoiceId();
    }
    
    // RICH: PaymentResult instead of boolean
    public PaymentResult processPayment(String invoiceId, double amount, String paymentReference) {
        
        PaymentRequest request = new PaymentRequest(invoiceId, amount, paymentReference);
        PaymentResult result = billingService.processPayment(request);
        
        // Rich Information for Error Handling
        if (!result.isSuccess()) {
            System.err.println("Payment failed: " + result.getMessage() + 
                             " (Status: " + result.getPaymentStatus() + ")");
        }
        
        return result;
    }
    
    // BUSINESS LOGIC: Without Legacy Constraints
    public double calculateOutstandingAmount(String customerId) {
        List<Invoice> openInvoices = getOpenInvoicesForCustomer(customerId);
        return openInvoices.stream().mapToDouble(Invoice::getAmount).sum();
    }
}
```

## Refactoring Schritte

### Schritt 1: Modern Target Interface Design
```java
// Define clean modern interface - What SHOULD the API look like?
public interface BillingService {
    // Use modern types (LocalDate, Enums)
    // Rich request/response objects
    // Clear method names
    // Comprehensive error handling
}
```

### Schritt 2: Request/Response Objects erstellen
```java
// Modern data structures
public class CreateInvoiceRequest { /* LocalDate, Enums */ }
public class PaymentResult { /* Rich status information */ }
public enum InvoiceType { MONTHLY_BILL, INSTALLATION, ADJUSTMENT }
public enum PaymentStatus { FULL_PAYMENT, PARTIAL_PAYMENT, OVERPAYMENT }
```

### Schritt 3: Adapter Class implementieren
```java
public class LegacyBillingAdapter implements BillingService {
    private final LegacyBillingSystem legacySystem; // Composition
    
    // Implement modern interface by delegating to legacy system
    @Override
    public Invoice createInvoice(CreateInvoiceRequest request) {
        // Convert modern → legacy → call → convert legacy → modern
    }
}
```

### Schritt 4: Centralized Mapping Logic
```java
// All type conversions in one place
private LocalDate convertLegacyDateToModern(Date legacyDate) { /* ... */ }
private String mapInvoiceTypeToLegacy(InvoiceType modernType) { /* ... */ }
private InvoiceType mapInvoiceTypeFromLegacy(String legacyType) { /* ... */ }
```

### Schritt 5: Exception Translation
```java
// Modern exceptions for legacy problems
try {
    // Legacy call
} catch (LegacyBillingException e) {
    throw new BillingServiceException("Modern message", e);
}
```

### Schritt 6: Client Code Refactoring
```java
// Replace direct legacy dependency with interface dependency
// OLD: ModernCustomerService(LegacyBillingSystem legacy)
// NEW: ModernCustomerService(BillingService billingService)
```

## Architektur Verbesserungen

### Vorher (Direct Integration)
```
ModernCustomerService
├── LegacyBillingSystem (tight coupling)
├── createCustomerInvoice()
│   ├── LocalDate → Date conversion
│   ├── Enum → String mapping
│   ├── Legacy API call
│   ├── Legacy exception handling
│   └── Legacy data → Modern data
├── processPayment()
│   ├── Type conversions (scattered)
│   ├── Boolean interpretation (unclear)
│   └── String parsing (fragile)
└── Mixed Legacy/Modern Types throughout
```

### Nachher (Adapter Pattern)
```
ModernCustomerService
├── BillingService (interface - loose coupling)
├── createCustomerInvoice() [clean modern API]
├── processPayment() [rich return objects]
└── Business Logic [no legacy concerns]

LegacyBillingAdapter [Anti-Corruption Layer]
├── implements BillingService
├── LegacyBillingSystem (encapsulated)
├── Centralized Type Conversions
├── Centralized Mapping Logic
├── Exception Translation
├── Result Interpretation
└── Legacy Concerns Isolation

LegacyBillingSystem
└── [Legacy API unchanged]
```

## Design Patterns Angewendet

### 1. Adapter Pattern
- **Intent**: Interface-Kompatibilität zwischen inkompatiblen Klassen
- **Application**: Legacy System API → Modern BillingService Interface
- **Benefit**: Loose coupling, legacy isolation, interface consistency

### 2. Anti-Corruption Layer (DDD Pattern)
- **Intent**: Domäne vor schlechten fremden Modellen schützen
- **Application**: Modern Domain vor Legacy System Corruption schützen  
- **Benefit**: Clean domain model, legacy concerns isolation

### 3. Interface Segregation (SOLID)
- **Intent**: Clients nicht von Interfaces abhängig machen die sie nicht nutzen
- **Application**: BillingService Interface nur mit relevanten Methoden
- **Benefit**: Clean contracts, focused interfaces

### 4. Dependency Inversion (SOLID)
- **Intent**: High-level modules nicht von low-level modules abhängig
- **Application**: ModernCustomerService → BillingService Interface
- **Benefit**: Testability, flexibility, loose coupling

## Legacy System Challenges Addressed

### Type System Mismatch
```java
// BEFORE: Type conversions everywhere
Date legacyDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

// AFTER: Centralized in adapter
private Date convertToLegacyDate(LocalDate modernDate) {
    return modernDate != null ?
        Date.from(modernDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
}
```

### Exception Handling Mess
```java
// BEFORE: Legacy exceptions leak through
catch (LegacyBillingException e) {
    throw new RuntimeException("Failed: " + e.getMessage() + " [" + e.getErrorCode() + "]");
}

// AFTER: Clean exception translation
catch (LegacyBillingException e) {
    throw new BillingServiceException("Failed to create invoice", e);
}
```

### Unclear Return Values
```java
// BEFORE: Boolean with no context
boolean success = legacySystem.processPayment(...);
// What does false mean? Not found? Partial payment? Error?

// AFTER: Rich result objects
PaymentResult result = new PaymentResult(success, PaymentStatus.FULL_PAYMENT, 
    "Payment processed successfully", remainingAmount);
```

## Testing Improvements

### Vorher: Hard to Test
```java
@Test
void testWithLegacySystem() {
    LegacyBillingSystem legacySystem = new LegacyBillingSystem(); // Always needed!
    ModernCustomerService service = new ModernCustomerService(legacySystem);
    
    // Test coupled to legacy system behavior
    // Hard to control legacy system state
    // Integration test, not unit test
}
```

### Nachher: Testable with Mocks
```java
@Test
void testWithMockedBilling() {
    BillingService mockBilling = mock(BillingService.class);
    ModernCustomerService service = new ModernCustomerService(mockBilling);
    
    // Arrange
    Invoice expectedInvoice = new Invoice(/* ... */);
    when(mockBilling.createInvoice(any())).thenReturn(expectedInvoice);
    
    // Act  
    String result = service.createCustomerInvoice("CUST-001", 100.0, 
        LocalDate.now().plusDays(30), InvoiceType.MONTHLY_BILL, PaymentMethod.DIRECT_DEBIT);
    
    // Assert
    assertThat(result).isEqualTo(expectedInvoice.getInvoiceId());
    
    // Pure unit test - no legacy system needed!
}

@Test
void testAdapterIntegration() {
    LegacyBillingSystem legacySystem = new LegacyBillingSystem();
    BillingService adapter = new LegacyBillingAdapter(legacySystem);
    
    // Test adapter translation logic
    CreateInvoiceRequest request = new CreateInvoiceRequest(/*...*/);
    Invoice invoice = adapter.createInvoice(request);
    
    // Verify modern types returned
    assertThat(invoice.getCreationDate()).isInstanceOf(LocalDate.class);
    assertThat(invoice.getInvoiceType()).isInstanceOf(InvoiceType.class);
    assertThat(invoice.getStatus()).isInstanceOf(InvoiceStatus.class);
}
```

## Migration Strategy

### Gradual Replacement
```java
// Phase 1: Introduce Adapter
BillingService billingService = new LegacyBillingAdapter(legacySystem);
ModernCustomerService service = new ModernCustomerService(billingService);

// Phase 2: Parallel Implementation
BillingService newBillingService = new NewBillingServiceImpl();
// Run both systems in parallel, compare results

// Phase 3: Switch Implementation  
ModernCustomerService service = new ModernCustomerService(newBillingService);
// Same interface - no client changes needed!

// Phase 4: Remove Legacy System
// Legacy system no longer needed
```

## Key Takeaways

### Anti-Corruption Layer Benefits
1. **Legacy Isolation**: Legacy concerns stay in adapter
2. **Clean Domain**: Modern code uses only modern types
3. **Testability**: Interface allows mocking
4. **Migration Path**: Easy to replace legacy system
5. **Type Safety**: Modern enums and types
6. **Error Handling**: Rich error information
7. **Maintainability**: Centralized mapping logic
8. **Flexibility**: Multiple implementations possible

### When to use Adapter Pattern
- **Legacy System Integration** mit inkompatiblem Interface
- **Third-Party Libraries** mit schlechten APIs
- **Gradual Migration** von alten zu neuen Systemen
- **API Modernization** ohne Breaking Changes
- **Anti-Corruption Layer** für Domain Protection

### Implementation Guidelines
1. **Define Target Interface First** - What should the modern API look like?
2. **Create Rich Data Objects** - Request/Response mit modernen Types
3. **Centralize Conversions** - All mapping logic in adapter
4. **Translate Exceptions** - Modern exceptions for modern clients
5. **Interpret Results** - Boolean → Rich objects
6. **Keep Adapter Thin** - Business logic in service, not adapter
7. **Test Both Layers** - Unit tests with mocks, integration tests with adapter

## Diskussionspunkte für Training

1. **Wann ist ein Legacy System 'legacy'?**
2. **Adapter vs. Facade vs. Wrapper - was sind die Unterschiede?**
3. **Wie geht man mit Legacy System Performance um?**
4. **Migration vs. Replacement Strategies?**
5. **Testing Legacy Integration - Integration vs. Unit Tests?**

## Weiterführende Übungen

1. **Multiple Legacy Systems**: Adapter für mehrere Legacy Systems
2. **Caching Layer**: Adapter mit Caching für Performance
3. **Circuit Breaker**: Adapter mit Resilience Patterns
4. **Event Sourcing**: Legacy Events → Modern Event Stream
5. **Configuration**: Adapter-Konfiguration für verschiedene Legacy Versions