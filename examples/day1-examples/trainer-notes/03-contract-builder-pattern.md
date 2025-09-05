# Contract Builder: Long Constructor → Builder Pattern

## Übersicht

Dieses Beispiel demonstriert die Transformation einer Klasse mit 15+ Constructor-Parametern in ein elegantes Builder Pattern mit Fluent Interface.

## Initial Version Problems

### Long Parameter Constructor Anti-Pattern

```java
// NIGHTMARE: 27 Parameter Constructor!
public TelekomContract(String contractId, String customerId, String contractType,
                      String internetSpeed, String internetTechnology, String phoneNumber,
                      String phoneFeatures, String tvPackage, String tvEquipment,
                      double internetPrice, double phonePrice, double tvPrice,
                      double installationFee, double discountPercentage, int contractDurationMonths,
                      LocalDate startDate, String billingCycle, String installationAddress,
                      String billingAddress, String technicalContact, String salesPerson,
                      boolean autoRenewal, boolean paperlessBilling, String paymentMethod,
                      String bankAccount, String priorityLevel, String promotionCode) {
    // 27 assignments...
    this.validateContract(); // Validation am Ende
}
```

### Telescoping Constructor Anti-Pattern

```java
// Multiple Überladungen - verwirrend und fehleranfällig
public TelekomContract(String contractId, String customerId, String contractType,
                      String internetSpeed, String phoneNumber, double internetPrice,
                      double phonePrice, LocalDate startDate, String installationAddress) {
    this(contractId, customerId, contractType, internetSpeed, "DSL", phoneNumber,
         "BASIC", null, null, internetPrice, phonePrice, 0.0, 0.0, 0.0, 24,
         startDate, "MONTHLY", installationAddress, installationAddress,
         null, null, true, false, "BANK_TRANSFER", null, "STANDARD", null);
}

// Noch mehr Constructor Overloads...
```

### Identifizierte Code-Smells

1. **Long Parameter List**: 27 Parameter - unmöglich zu merken
2. **Parameter Confusion**: Reihenfolge kritisch und fehleranfällig
3. **Telescoping Constructors**: Multiple Überladungen
4. **Poor Readability**: Client Code unleserlich
5. **Hard to Extend**: Neue Parameter = Breaking Changes
6. **Null Parameters**: Für optionale Werte
7. **All-or-Nothing Validation**: Validation erst am Ende
8. **Mutable State**: Object kann nach Erstellung geändert werden

### Konkrete Probleme

```java
// HORROR: Was bedeuten diese Parameter?
TelekomContract contract = new TelekomContract(
    "CTR-001", "CUST-001", "INTERNET_PHONE_TV",    // OK, noch verständlich
    "100", "DSL", "+49301234567", "PREMIUM",       // Wird schon schwieriger
    "BASIC_TV", "STANDARD_BOX", 39.95, 19.95, 24.95, // Was ist was??
    49.99, 10.0, 24, LocalDate.now().plusDays(7),  // Völlig verloren
    "MONTHLY", "Musterstraße 1", "Musterstraße 1", // Duplicate addresses?
    "tech@example.com", "Hans Müller", true, true, // Boolean bedeutung?
    "DIRECT_DEBIT", "DE123456789", "VIP", "SUMMER2024" // Parameter salat!
);

// Parameter-Reihenfolge verwechselt = Subtle Bug!
TelekomContract buggy = new TelekomContract(
    "CTR-002", "CUST-002", "INTERNET_PHONE",
    "250", "FIBER", "+49301234568", "BASIC", null, null,
    19.95, 49.95, 0.0, // <- internetPrice und phonePrice vertauscht!
    // ... Bug ist schwer zu erkennen
);
```

## Fixed Version Solution

### Builder Pattern Implementierung

```java
public class TelekomContract {
    // Final fields - Immutable nach Erstellung
    private final String contractId;
    private final String customerId;
    // ... alle fields final
    
    // Private Constructor - nur Builder kann instanzieren
    private TelekomContract(Builder builder) {
        this.contractId = builder.contractId;
        this.customerId = builder.customerId;
        // ... alle assignments from builder
        this.creationDate = LocalDateTime.now();
    }
    
    // Static Factory Method für Builder
    public static Builder builder() {
        return new Builder();
    }
}
```

### Fluent Builder Interface

```java
public static class Builder {
    // Required Fields - müssen gesetzt werden
    private String contractId;
    private String customerId;
    private String contractType;
    private LocalDate startDate;
    private String installationAddress;
    
    // Optional Fields - mit sinnvollen Defaults  
    private String internetTechnology = "DSL";
    private String phoneFeatures = "BASIC";
    private int contractDurationMonths = 24;
    private boolean autoRenewal = true;
    private String paymentMethod = "BANK_TRANSFER";
    // ...
    
    // Fluent Methods - Self-documenting
    public Builder withContractId(String contractId) {
        this.contractId = contractId;
        return this;
    }
    
    public Builder forCustomer(String customerId) {
        this.customerId = customerId;
        return this;
    }
    
    public Builder withContractType(String contractType) {
        this.contractType = contractType;
        return this;
    }
    
    // Domain-specific methods
    public Builder withInternetSpeed(String speed) {
        this.internetSpeed = speed;
        return this;
    }
    
    public Builder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
    
    // Convenience Methods für häufige Use Cases
    public Builder asInternetOnlyContract(String speed, double price) {
        this.contractType = "INTERNET_ONLY";
        this.internetSpeed = speed;
        this.internetPrice = price;
        return this;
    }
    
    public Builder asPremiumBundle() {
        this.priorityLevel = "VIP";
        this.phoneFeatures = "PREMIUM";
        this.autoRenewal = true;
        this.paperlessBilling = true;
        this.discountPercentage = 15.0;
        return this;
    }
    
    // Build with Validation
    public TelekomContract build() {
        validate(); // Step-by-step validation
        return new TelekomContract(this);
    }
    
    private void validate() {
        // Required field validation
        if (contractId == null || contractId.trim().isEmpty()) {
            throw new IllegalArgumentException("Contract ID is required");
        }
        // ... weitere Validations
        
        // Contract Type specific validation
        validateContractTypeSpecificFields();
    }
}
```

### Self-Documenting Client Code

```java
// BEAUTIFUL: Completely readable and self-documenting
var contract = TelekomContract.builder()
    .withContractId("CTR-READABLE-001")
    .forCustomer("CUST-HAPPY")
    .withContractType("INTERNET_PHONE_TV")
    .startingOn(LocalDate.now().plusWeeks(1))
    .installedAt("Clean Code Street 42, Berlin")
    .withInternetSpeed("Gigabit")
    .withInternetTechnology("FIBER")
    .withInternetPrice(59.95)
    .withPhoneNumber("+49301111111")
    .withPhoneFeatures("PREMIUM")
    .withPhonePrice(29.95)
    .withTvPackage("4K Premium")
    .withTvPrice(39.95)
    .asPremiumBundle()  // Convenience method
    .withSpecialConditions("Migrated from competitor - VIP treatment")
    .build();
    
// What each parameter means is CRYSTAL CLEAR!
```

## Refactoring Schritte

### Schritt 1: Analyse bestehende Constructor Parameter
- Identifiziere Required vs Optional Parameters
- Gruppiere verwandte Parameter
- Finde häufige Parameter-Kombinationen
- Dokumentiere Default-Values

### Schritt 2: Builder Class Design
```java
public static class Builder {
    // Required fields - no defaults
    private String contractId;
    private String customerId;
    
    // Optional fields - with defaults
    private int contractDurationMonths = 24;
    private boolean autoRenewal = true;
    
    private Builder() {} // Private constructor
}
```

### Schritt 3: Fluent Methods implementieren
```java
// Self-documenting method names
public Builder forCustomer(String customerId) { /* ... */ }
public Builder startingOn(LocalDate startDate) { /* ... */ }
public Builder installedAt(String address) { /* ... */ }
public Builder billedAt(String address) { /* ... */ }  
public Builder soldBy(String salesPerson) { /* ... */ }
```

### Schritt 4: Convenience Methods hinzufügen
```java
// Für häufige Use Cases
public Builder asTriplePlayContract(String internetSpeed, String phoneNumber, 
                                   String tvPackage, double internetPrice, 
                                   double phonePrice, double tvPrice) {
    this.contractType = "INTERNET_PHONE_TV";
    this.internetSpeed = internetSpeed;
    this.phoneNumber = phoneNumber;
    this.tvPackage = tvPackage;
    this.internetPrice = internetPrice;
    this.phonePrice = phonePrice;  
    this.tvPrice = tvPrice;
    return this;
}
```

### Schritt 5: Validation Logic verbessern
```java
public TelekomContract build() {
    validate(); // Fail-fast validation
    return new TelekomContract(this);
}

private void validate() {
    // Required fields first
    validateRequiredFields();
    // Business rules
    validateBusinessRules();
    // Contract type specific
    validateContractTypeSpecificFields();
}
```

### Schritt 6: Immutability sicherstellen
```java
public class TelekomContract {
    // All fields final
    private final String contractId;
    private final String customerId;
    // ...
    
    // Private constructor
    private TelekomContract(Builder builder) { /* ... */ }
    
    // Only getters, no setters
    public String getContractId() { return contractId; }
}
```

## Architektur Verbesserungen

### Vorher (Long Constructor)
```
TelekomContract Constructor
├── 27 Parameter in einer Zeile
├── Parameter-Reihenfolge critical
├── Telescoping Constructors (4 Überladungen)
├── Validation am Ende
├── Mutable State möglich
└── Client Code unleserlich

Client Usage:
new TelekomContract("p1", "p2", "p3", ..., "p27") // Horror!
```

### Nachher (Builder Pattern)
```
TelekomContract
├── Private Constructor
├── Final Fields (Immutable)
├── Static builder() Factory Method
└── Rich domain methods

Builder
├── Required Fields (validation)
├── Optional Fields (mit Defaults)  
├── Fluent Interface Methods
├── Convenience Methods (Use Cases)
├── Step-by-step Validation
└── build() Method

Client Usage:
TelekomContract.builder()
  .forCustomer("CUST-001")     // Crystal clear
  .withContractType("TRIPLE")   // Self-documenting
  .startingOn(nextMonth)        // Domain language  
  .installedAt(customerAddress) // Meaningful names
  .asPremiumBundle()           // Business concepts
  .build();                    // Clean and readable
```

## Design Patterns Angewendet

### 1. Builder Pattern
- **Intent**: Komplexe Objekterstellung schrittweise mit flexibler Konfiguration
- **Application**: TelekomContract mit 20+ konfigurierbaren Eigenschaften
- **Benefit**: Lesbarkeit, Flexibilität, Validation, Immutability

### 2. Fluent Interface Pattern
- **Intent**: Method Chaining für bessere Lesbarkeit
- **Application**: Builder methods return `this` für Method Chaining  
- **Benefit**: Domain-specific Language (DSL), Self-documenting Code

### 3. Template Method Pattern (in Builder)
- **Intent**: Algorithm-Struktur definieren, Details variierbar
- **Application**: `build()` method definiert Validierung → Konstruktion
- **Benefit**: Consistent validation approach, extensible

### 4. Static Factory Method
- **Intent**: Named constructors mit klarer Intent
- **Application**: `TelekomContract.builder()` statt `new Builder()`
- **Benefit**: Clear intent, hides implementation details

## Client Code Verbesserungen

### Vorher: Unleserlich und fehleranfällig
```java
// Horror: Was bedeutet was?
TelekomContract contract = new TelekomContract(
    "CTR-001", "CUST-001", "INTERNET_PHONE_TV",
    "100", "DSL", "+49301234567", "PREMIUM", "BASIC_TV",
    "STANDARD_BOX", 39.95, 19.95, 24.95, 49.99, 10.0, 24,
    LocalDate.now().plusDays(7), "MONTHLY", 
    "Musterstraße 1, Berlin", "Musterstraße 1, Berlin",
    "tech@example.com", "Hans Müller", true, true,
    "DIRECT_DEBIT", "DE123456789", "VIP", "SUMMER2024"
);
```

### Nachher: Self-documenting und typo-safe
```java
// Beautiful: Every parameter is crystal clear
var contract = TelekomContract.builder()
    .withContractId("CTR-001")
    .forCustomer("CUST-001")
    .withContractType("INTERNET_PHONE_TV")
    .startingOn(LocalDate.now().plusDays(7))
    .installedAt("Musterstraße 1, Berlin")
    .withInternetSpeed("100")
    .withInternetTechnology("DSL")  
    .withInternetPrice(39.95)
    .withPhoneNumber("+49301234567")
    .withPhoneFeatures("PREMIUM")
    .withPhonePrice(19.95)
    .withTvPackage("BASIC_TV")
    .withTvEquipment("STANDARD_BOX")
    .withTvPrice(24.95)
    .withInstallationFee(49.99)
    .withDiscount(10.0)
    .forDuration(24)
    .withBillingCycle("MONTHLY")
    .withTechnicalContact("tech@example.com")
    .soldBy("Hans Müller")
    .withAutoRenewal(true)
    .withPaperlessBilling(true)
    .withPaymentMethod("DIRECT_DEBIT")
    .withBankAccount("DE123456789")
    .withPriority("VIP")
    .withPromotionCode("SUMMER2024")
    .build();
```

## Testing Verbesserungen

### Vorher: Schwer testbare Constructor Calls
```java
@Test
void testContract() {
    // Muss alle Parameter korrekt setzen - fehleranfällig
    TelekomContract contract = new TelekomContract(
        "CTR-TEST", "CUST-TEST", "INTERNET_ONLY",
        "50", "DSL", null, null, null, null,
        29.95, 0.0, 0.0, 0.0, 0.0, 12,
        LocalDate.now(), "MONTHLY", "Test Address", null,
        null, null, false, false, "BANK_TRANSFER", 
        null, "STANDARD", null
    );
    // 27 Parameter für einfachen Test!
}
```

### Nachher: Fokussierte, lesbare Tests
```java
@Test
void testInternetOnlyContract() {
    // Focus auf das was getestet wird
    var contract = TelekomContract.builder()
        .withContractId("CTR-TEST")
        .forCustomer("CUST-TEST")
        .startingOn(LocalDate.now())
        .installedAt("Test Address")
        .asInternetOnlyContract("50", 29.95) // Convenience method!
        .forDuration(12)
        .build();
    
    assertThat(contract.getContractType()).isEqualTo("INTERNET_ONLY");
    assertThat(contract.getInternetSpeed()).isEqualTo("50");
    assertThat(contract.getInternetPrice()).isEqualTo(29.95);
    assertThat(contract.calculateTotalMonthlyPrice()).isEqualTo(29.95);
}

@Test  
void testBuilderValidation() {
    // Test validation behavior
    assertThatThrownBy(() ->
        TelekomContract.builder()
            .forCustomer("CUST-TEST")
            // Missing required contractId
            .build()
    ).isInstanceOf(IllegalArgumentException.class)
     .hasMessageContaining("Contract ID is required");
}
```

## Key Takeaways

### Builder Pattern Benefits
1. **Readability**: Self-documenting parameter names
2. **Flexibility**: Optional parameters mit Defaults
3. **Validation**: Step-by-step validation mit clear error messages
4. **Immutability**: Final fields, thread-safe objects
5. **Extensibility**: New parameters ohne Breaking Changes
6. **Convenience**: Domain-specific methods für Use Cases
7. **Type Safety**: Compile-time validation
8. **Testing**: Fokussierte Tests, easy setup

### When to use Builder Pattern
- **4+ Constructor Parameters** (besonders wenn optional)
- **Complex Object Setup** mit vielen Konfigurationen
- **Immutable Objects** gewünscht
- **Domain-Specific Language** für Object Creation
- **Multiple Object Variants** mit verschiedenen Konfigurationen

### Implementation Guidelines
1. **Start with Required Fields** - was muss immer gesetzt werden
2. **Sensible Defaults** für Optional Fields
3. **Fluent Method Names** - Domain Language verwenden
4. **Convenience Methods** für häufige Use Cases
5. **Fail-Fast Validation** - so früh wie möglich validieren  
6. **Immutable Results** - Final fields nach Build
7. **Private Constructor** - nur Builder kann instanzieren

## Diskussionspunkte für Training

1. **Ab wie vielen Parametern ist Builder sinnvoll?**
2. **Required vs Optional Parameters - wie unterscheiden?**
3. **Validation Timing - wann und wo validieren?**
4. **Performance Implications von Builder Pattern?**
5. **Builder vs Factory vs Constructor - wann was?**

## Weiterführende Übungen

1. **Validation Enhancement**: Erweiterte Cross-Field Validation implementieren
2. **Builder Inheritance**: Specialized Builder für verschiedene Contract Types
3. **Preset Builders**: Standard-Konfigurationen als Named Builders
4. **Integration Testing**: Builder in Integration Tests verwenden  
5. **Performance Comparison**: Constructor vs Builder Performance messen