# Refactoring-Techniken - Telekom Workshop Guide

## 🎯 Zweck
Schritt-für-Schritt Anleitungen für systematisches Refactoring in Telekom-Systemen. Praxisorientiert mit messbaren Verbesserungen.

## 📋 Sicherheitsprinzipien
1. **Tests zuerst** - Keine Refactorings ohne Absicherung
2. **Baby Steps** - Kleine, nachvollziehbare Schritte  
3. **Continuous Integration** - Nach jedem Schritt ausführen
4. **Messbarkeit** - Vorher/Nachher Metriken dokumentieren

---

## 1. Extract Method - Methodenextraktion

### 🎯 Anwendung
- Long Methods aufteilen
- Duplizierten Code eliminieren
- Lesbarkeit verbessern

### 📋 Voraussetzungen
- ✅ Unit Tests vorhanden
- ✅ Code-Coverage >70%
- ✅ Keine kritischen Bugs

### 🔧 Schritt-für-Schritt

#### Schritt 1: Code-Segment identifizieren
```java
// ❌ VORHER: 45-Zeilen Methode
public void processCustomerOrder(Order order) {
    // Validierung (Zeile 1-15)
    if (order == null) throw new IllegalArgumentException();
    if (order.getCustomerId() == null) throw new ValidationException();
    if (!isValidCustomerId(order.getCustomerId())) throw new ValidationException();
    
    // Preisberechnung (Zeile 16-30)
    double basePrice = order.getItems().stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
    double discount = calculateDiscount(order.getCustomer());
    double finalPrice = basePrice * (1 - discount);
    
    // Persistierung (Zeile 31-45)
    OrderEntity entity = new OrderEntity();
    entity.setOrderId(order.getId());
    entity.setPrice(finalPrice);
    repository.save(entity);
}
```

#### Schritt 2: Extrahierte Methode definieren
```java
// ✅ SCHRITT 1: Validierung extrahieren
private void validateOrder(Order order) {
    if (order == null) {
        throw new IllegalArgumentException("Order cannot be null");
    }
    if (order.getCustomerId() == null) {
        throw new ValidationException("Customer ID is required");
    }
    if (!isValidCustomerId(order.getCustomerId())) {
        throw new ValidationException("Invalid customer ID format");
    }
}
```

#### Schritt 3: Tests anpassen
```java
@Test
public void shouldValidateOrderCorrectly() {
    // Test für neue validateOrder() Methode
    assertThrows(IllegalArgumentException.class, 
        () -> orderService.validateOrder(null));
}
```

#### Schritt 4: Original-Methode anpassen
```java
public void processCustomerOrder(Order order) {
    validateOrder(order); // ✅ Extrahierte Methode nutzen
    
    double finalPrice = calculateOrderPrice(order); // ✅ Weitere Extraktion
    
    persistOrder(order, finalPrice); // ✅ Weitere Extraktion
}
```

### 📊 Metriken Vorher/Nachher
| Metrik | Vorher | Nachher | Verbesserung |
|--------|--------|---------|--------------|
| Methodenlänge | 45 LOC | 3 LOC | -93% |
| Complexity | 12 | 2 | -83% |
| Testabdeckung | 60% | 95% | +35% |

---

## 2. Extract Class - Klassenextraktion

### 🎯 Anwendung
- God Objects aufteilen
- Single Responsibility Principle
- Cohesion verbessern

### 🔧 Schritt-für-Schritt

#### Schritt 1: Verantwortlichkeiten identifizieren
```java
// ❌ VORHER: God Object mit 847 Zeilen
public class CustomerManager {
    // Gruppe 1: Kundenverwaltung (200 Zeilen)
    public void createCustomer() { ... }
    public void updateCustomer() { ... }
    
    // Gruppe 2: Billing (300 Zeilen)  
    public void calculateBill() { ... }
    public void generateInvoice() { ... }
    
    // Gruppe 3: Provisioning (347 Zeilen)
    public void activateService() { ... }
    public void deactivateService() { ... }
}
```

#### Schritt 2: Erste Klasse extrahieren
```java
// ✅ SCHRITT 1: Billing extrahieren
public class CustomerBilling {
    private final CustomerRepository customerRepo;
    private final PricingService pricingService;
    
    public Bill calculateBill(CustomerId customerId, Period period) {
        Customer customer = customerRepo.findById(customerId);
        return pricingService.calculateFor(customer, period);
    }
    
    public Invoice generateInvoice(Bill bill) {
        return new Invoice(bill, generateInvoiceNumber());
    }
}
```

#### Schritt 3: Dependencies refactoren
```java
// ✅ SCHRITT 2: CustomerManager entschlacken
public class CustomerManager {
    private final CustomerBilling billing; // ✅ Dependency Injection
    
    public void processCustomerBilling(CustomerId id) {
        Bill bill = billing.calculateBill(id, currentPeriod());
        Invoice invoice = billing.generateInvoice(bill);
        sendInvoice(invoice);
    }
}
```

### 📊 Metriken nach Extraktion
| Klasse | LOC | Methoden | Responsibility |
|--------|-----|----------|----------------|
| CustomerManager | 247 | 8 | Kunde verwalten |
| CustomerBilling | 156 | 6 | Abrechnung |
| CustomerProvisioning | 203 | 9 | Service-Aktivierung |

---

## 3. Replace Conditional with Polymorphism

### 🎯 Anwendung  
- Switch Statements eliminieren
- Extensibilität verbessern
- Open/Closed Principle

### 🔧 Schritt-für-Schritt

#### Schritt 1: Switch Statement identifizieren
```java
// ❌ VORHER: Switch über Kundentyp
public double calculateDiscount(Customer customer) {
    switch(customer.getType()) {
        case PRIVATE:
            return customer.getOrderValue() > 100 ? 0.05 : 0.0;
        case BUSINESS:
            return 0.1 + (customer.getYearsWithUs() * 0.01);
        case PREMIUM:
            return 0.15 + calculateLoyaltyBonus(customer);
        default:
            return 0.0;
    }
}
```

#### Schritt 2: Interface definieren
```java
// ✅ SCHRITT 1: Strategy Interface
public interface DiscountStrategy {
    double calculateDiscount(Customer customer);
    CustomerType getApplicableType();
}
```

#### Schritt 3: Konkrete Strategien implementieren
```java
// ✅ SCHRITT 2: Private Customer Strategy
@Component
public class PrivateCustomerDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(Customer customer) {
        return customer.getOrderValue() > 100 ? 0.05 : 0.0;
    }
    
    @Override
    public CustomerType getApplicableType() {
        return CustomerType.PRIVATE;
    }
}

@Component  
public class BusinessCustomerDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(Customer customer) {
        return 0.1 + (customer.getYearsWithUs() * 0.01);
    }
    
    @Override
    public CustomerType getApplicableType() {
        return CustomerType.BUSINESS;
    }
}
```

#### Schritt 4: Factory/Registry implementieren
```java
// ✅ SCHRITT 3: Strategy Registry
@Service
public class DiscountCalculator {
    private final Map<CustomerType, DiscountStrategy> strategies;
    
    public DiscountCalculator(List<DiscountStrategy> strategies) {
        this.strategies = strategies.stream()
            .collect(toMap(DiscountStrategy::getApplicableType, identity()));
    }
    
    public double calculateDiscount(Customer customer) {
        return strategies.get(customer.getType())
            .calculateDiscount(customer);
    }
}
```

### 📊 Erweiterbarkeit Vorher/Nachher
| Änderung | Vorher | Nachher |
|----------|--------|---------|
| Neuer Kundentyp | 5 Stellen ändern | 1 neue Klasse |
| Discount-Logik ändern | Switch modifizieren | Spezifische Klasse |
| Testing | Monolithisch | Isoliert testbar |

---

## 4. Move Method - Methodenverschiebung

### 🎯 Anwendung
- Feature Envy beheben
- Cohesion verbessern  
- Coupling reduzieren

### 🔧 Schritt-für-Schritt

#### Schritt 1: Feature Envy erkennen
```java
// ❌ VORHER: CustomerService ist "neidisch" auf Address
public class CustomerService {
    public boolean isCustomerInServiceArea(Customer customer) {
        Address address = customer.getAddress();
        String zipCode = address.getZipCode();
        String state = address.getState();
        String city = address.getCity();
        
        return serviceAreaConfig.contains(zipCode) ||
               serviceAreaConfig.containsState(state) ||
               serviceAreaConfig.containsCity(city);
    }
}
```

#### Schritt 2: Zielklasse vorbereiten
```java
// ✅ SCHRITT 1: Methode zu Address verschieben
public class Address {
    private String zipCode;
    private String state;
    private String city;
    
    public boolean isInServiceArea(ServiceAreaConfig config) {
        return config.contains(this.zipCode) ||
               config.containsState(this.state) ||
               config.containsCity(this.city);
    }
}
```

#### Schritt 3: Aufrufe anpassen
```java
// ✅ SCHRITT 2: CustomerService vereinfachen
public class CustomerService {
    public boolean isCustomerInServiceArea(Customer customer) {
        return customer.getAddress().isInServiceArea(serviceAreaConfig);
    }
}
```

### 📊 Coupling-Analyse
| Metrik | Vorher | Nachher |
|--------|--------|---------|
| CustomerService → Address | 4 Aufrufe | 1 Aufruf |
| Method Lines | 8 | 3 |
| Address Cohesion | 60% | 85% |

---

## 5. Replace Magic Numbers with Constants

### 🎯 Anwendung
- Code-Verständlichkeit
- Wartbarkeit
- Konfigurierbarkeit

### 🔧 Schritt-für-Schritt

#### Schritt 1: Magic Numbers identifizieren
```java
// ❌ VORHER: Magic Numbers überall
public class TelekomBillingService {
    public double calculateMonthlyFee(Customer customer) {
        if (customer.getType() == 1) { // Was ist 1?
            return 29.99 * 1.19; // Was ist 1.19?
        } else if (customer.getType() == 2) { // Was ist 2?  
            return 49.99 * 1.19 * 0.9; // Was ist 0.9?
        }
        return 19.99 * 1.19; // Default?
    }
}
```

#### Schritt 2: Constants definieren
```java
// ✅ SCHRITT 1: Constants Class erstellen
public final class TelekomConstants {
    // Customer Types
    public static final int PRIVATE_CUSTOMER = 1;
    public static final int BUSINESS_CUSTOMER = 2;
    public static final int PREMIUM_CUSTOMER = 3;
    
    // Pricing
    public static final double PRIVATE_BASE_PRICE = 29.99;
    public static final double BUSINESS_BASE_PRICE = 49.99;
    public static final double PREMIUM_BASE_PRICE = 19.99;
    
    // Tax and Discounts
    public static final double VAT_RATE = 1.19; // 19% MwSt
    public static final double BUSINESS_DISCOUNT = 0.9; // 10% Rabatt
    
    private TelekomConstants() {} // Utility class
}
```

#### Schritt 3: Code refactoren
```java
// ✅ SCHRITT 2: Readable Code
public class TelekomBillingService {
    public double calculateMonthlyFee(Customer customer) {
        double basePrice = getBasePriceForCustomerType(customer.getType());
        double discount = getDiscountForCustomerType(customer.getType());
        
        return basePrice * VAT_RATE * discount;
    }
    
    private double getBasePriceForCustomerType(int customerType) {
        switch (customerType) {
            case PRIVATE_CUSTOMER: return PRIVATE_BASE_PRICE;
            case BUSINESS_CUSTOMER: return BUSINESS_BASE_PRICE;
            case PREMIUM_CUSTOMER: return PREMIUM_BASE_PRICE;
            default: throw new IllegalArgumentException("Unknown customer type");
        }
    }
}
```

#### Schritt 4: Configuration externalisieren
```java
// ✅ SCHRITT 3: Configuration Properties
@ConfigurationProperties("telekom.billing")
public class BillingConfig {
    private double privateBasePrice = 29.99;
    private double businessBasePrice = 49.99;
    private double vatRate = 1.19;
    
    // Getters/Setters
}
```

---

## 6. Introduce Parameter Object

### 🎯 Anwendung
- Long Parameter Lists reduzieren
- Zusammengehörige Parameter gruppieren
- Data Clumps eliminieren

### 🔧 Schritt-für-Schritt

#### Schritt 1: Parameter Groups identifizieren
```java
// ❌ VORHER: 8 Parameter - zu viel!
public void createCustomerAccount(
    String firstName, String lastName, String email,
    String street, String city, String zipCode, String country,
    String phoneNumber) {
    // Implementation
}
```

#### Schritt 2: Parameter Object erstellen
```java
// ✅ SCHRITT 1: Address Value Object
public class Address {
    private final String street;
    private final String city;
    private final String zipCode;
    private final String country;
    
    public Address(String street, String city, String zipCode, String country) {
        this.street = requireNonNull(street, "Street is required");
        this.city = requireNonNull(city, "City is required");
        this.zipCode = validateZipCode(zipCode);
        this.country = requireNonNull(country, "Country is required");
    }
    
    // Validation logic hier
    // Getters
}

// ✅ SCHRITT 2: Customer Info Object
public class CustomerInfo {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final Address address;
    
    // Constructor mit Validation
    // Getters
}
```

#### Schritt 3: Methodensignatur vereinfachen
```java
// ✅ SCHRITT 3: Cleaner Method Signature  
public void createCustomerAccount(CustomerInfo customerInfo) {
    validateCustomerInfo(customerInfo);
    
    Customer customer = Customer.builder()
        .personalInfo(customerInfo.getPersonalInfo())
        .address(customerInfo.getAddress())
        .contact(customerInfo.getContactInfo())
        .build();
        
    customerRepository.save(customer);
}
```

### 📊 Parameterkomplexität
| Metrik | Vorher | Nachher |
|--------|--------|---------|
| Parameter Count | 8 | 1 |
| Method Complexity | 5 | 2 |
| Validation Logic | Verstreut | Zentralisiert |
| Reusability | 0 | Hoch |

---

## 7. Replace Inheritance with Delegation

### 🎯 Anwendung
- Refused Bequest beheben
- Composition over Inheritance
- Flexibilität erhöhen

### 🔧 Schritt-für-Schritt

#### Schritt 1: Problematische Vererbung identifizieren
```java
// ❌ VORHER: Problematische Vererbung
class Customer {
    public void payBill(Bill bill) {
        // Standard Zahlungslogik
        creditCard.charge(bill.getAmount());
    }
    
    public void sendReminder() {
        emailService.sendReminder(this.email);
    }
}

class BusinessCustomer extends Customer {
    @Override
    public void payBill(Bill bill) {
        // Business Kunden zahlen anders - bricht LSP
        throw new UnsupportedOperationException("Business customers use invoice payment");
    }
    
    @Override  
    public void sendReminder() {
        // Auch andere Reminder-Logik
        throw new UnsupportedOperationException("Business customers get phone calls");
    }
}
```

#### Schritt 2: Strategy Interfaces definieren
```java
// ✅ SCHRITT 1: Payment Strategy
public interface PaymentStrategy {
    void pay(Bill bill, PaymentDetails details);
    boolean canProcess(PaymentMethod method);
}

public interface CommunicationStrategy {
    void sendReminder(Customer customer, Bill bill);
    void sendNotification(Customer customer, String message);
}
```

#### Schritt 3: Konkrete Strategien implementieren
```java
// ✅ SCHRITT 2: Concrete Strategies
@Component
public class CreditCardPayment implements PaymentStrategy {
    public void pay(Bill bill, PaymentDetails details) {
        creditCardProcessor.charge(details.getCreditCard(), bill.getAmount());
    }
}

@Component  
public class InvoicePayment implements PaymentStrategy {
    public void pay(Bill bill, PaymentDetails details) {
        invoiceService.createInvoice(bill, details.getBillingAddress());
    }
}

@Component
public class EmailCommunication implements CommunicationStrategy {
    public void sendReminder(Customer customer, Bill bill) {
        emailService.sendBillReminder(customer.getEmail(), bill);
    }
}

@Component
public class PhoneCommunication implements CommunicationStrategy {
    public void sendReminder(Customer customer, Bill bill) {
        phoneService.callCustomer(customer.getPhoneNumber(), bill);
    }
}
```

#### Schritt 4: Composition-basierte Klasse
```java
// ✅ SCHRITT 3: Composition statt Inheritance
public class Customer {
    private final CustomerInfo info;
    private final PaymentStrategy paymentStrategy;
    private final CommunicationStrategy communicationStrategy;
    
    public Customer(CustomerInfo info, 
                   PaymentStrategy paymentStrategy,
                   CommunicationStrategy communicationStrategy) {
        this.info = info;
        this.paymentStrategy = paymentStrategy;
        this.communicationStrategy = communicationStrategy;
    }
    
    public void payBill(Bill bill) {
        paymentStrategy.pay(bill, info.getPaymentDetails());
    }
    
    public void sendReminder(Bill bill) {
        communicationStrategy.sendReminder(this, bill);
    }
}
```

#### Schritt 5: Factory für Kunden-Typen
```java
// ✅ SCHRITT 4: Customer Factory
@Service
public class CustomerFactory {
    
    public Customer createPrivateCustomer(CustomerInfo info) {
        return new Customer(info, 
            new CreditCardPayment(),
            new EmailCommunication());
    }
    
    public Customer createBusinessCustomer(CustomerInfo info) {
        return new Customer(info,
            new InvoicePayment(),
            new PhoneCommunication());
    }
}
```

---

## 8. Introduce Null Object

### 🎯 Anwendung
- Null-Pointer-Exceptions vermeiden
- Defensive Programming reduzieren
- Cleaner Code ohne Null-Checks

### 🔧 Schritt-für-Schritt

#### Schritt 1: Null-Check Proliferation identifizieren
```java
// ❌ VORHER: Null Checks überall
public class CustomerService {
    public void processCustomer(Customer customer) {
        if (customer != null) {
            Address address = customer.getAddress();
            if (address != null) {
                String city = address.getCity();
                if (city != null && !city.isEmpty()) {
                    processCity(city);
                }
            }
        }
    }
    
    public double calculateShipping(Customer customer) {
        if (customer == null) return 0.0;
        
        Address address = customer.getAddress(); 
        if (address == null) return STANDARD_SHIPPING;
        
        return shippingCalculator.calculate(address);
    }
}
```

#### Schritt 2: Null Object Pattern implementieren  
```java
// ✅ SCHRITT 1: Address Interface
public interface Address {
    String getStreet();
    String getCity();
    String getZipCode();
    String getCountry();
    boolean isValid();
    double getShippingCost();
}

// ✅ SCHRITT 2: Real Address Implementation
public class RealAddress implements Address {
    private final String street;
    private final String city;
    private final String zipCode;
    private final String country;
    
    // Constructor, getters
    
    @Override
    public boolean isValid() {
        return stream.of(street, city, zipCode, country)
            .allMatch(s -> s != null && !s.trim().isEmpty());
    }
    
    @Override
    public double getShippingCost() {
        return shippingCalculator.calculateFor(this);
    }
}

// ✅ SCHRITT 3: Null Object Implementation
public class NullAddress implements Address {
    @Override
    public String getStreet() { return ""; }
    
    @Override  
    public String getCity() { return ""; }
    
    @Override
    public String getZipCode() { return ""; }
    
    @Override
    public String getCountry() { return ""; }
    
    @Override
    public boolean isValid() { return false; }
    
    @Override
    public double getShippingCost() { 
        return CustomerService.STANDARD_SHIPPING; 
    }
}
```

#### Schritt 3: Customer anpassen
```java  
// ✅ SCHRITT 4: Customer ohne Null
public class Customer {
    private final Address address; // Nie null!
    
    public Customer(CustomerInfo info) {
        this.address = info.getAddress() != null 
            ? new RealAddress(info.getAddress())
            : new NullAddress();
    }
    
    public Address getAddress() {
        return address; // Garantiert nicht null
    }
}
```

#### Schritt 4: Clean Service Code
```java
// ✅ SCHRITT 5: Null-Check-freier Code
public class CustomerService {
    public void processCustomer(Customer customer) {
        Address address = customer.getAddress(); // Nie null
        String city = address.getCity(); // Nie null
        
        if (address.isValid()) { // Explizite Validierung statt Null-Check
            processCity(city);
        }
    }
    
    public double calculateShipping(Customer customer) {
        return customer.getAddress().getShippingCost(); // Clean & Simple
    }
}
```

---

## 🧪 Test-Driven Refactoring Workflow

### Golden Rule: Tests First!
```java
// ✅ SCHRITT 0: Immer zuerst Tests schreiben
@Test
public void shouldCalculateCorrectDiscountForBusinessCustomer() {
    // Given
    Customer businessCustomer = TestData.createBusinessCustomer();
    
    // When  
    double discount = discountCalculator.calculate(businessCustomer);
    
    // Then
    assertThat(discount).isEqualTo(0.15);
}
```

### Refactoring Cycle
1. 🔴 **RED**: Existing tests pass
2. 🟡 **REFACTOR**: Apply technique 
3. 🟢 **GREEN**: All tests still pass
4. 📊 **MEASURE**: Verify improvement
5. 🔄 **REPEAT**: Next refactoring

---

## 📊 Metriken für Refactoring-Erfolg

### Code-Qualität
| Metrik | Tool | Zielwert |
|--------|------|----------|
| Cyclomatic Complexity | SonarQube | <10 |
| Method Length | PMD | <30 LOC |
| Class Length | Checkstyle | <400 LOC |
| Duplication | SonarQube | <3% |
| Coverage | JaCoCo | >80% |

### Maintainability Index
```bash
# Beispiel: SonarQube Metrics API
curl -u token: \
  'https://sonar.telekom.com/api/measures/component?
   component=customer-service&
   metricKeys=sqale_index,reliability_rating'
```

### Team-Produktivität
- 📉 **Bug Rate**: -40% nach Refactoring
- ⚡ **Feature Velocity**: +25% nach 3 Monaten  
- 🎯 **Code Reviews**: -50% Zeit pro Review
- 🧪 **Test Execution**: +60% schneller

---

## 🎯 Workshop-Integration

### Hands-On Übung (45 Min)
1. **Teams à 3-4 Personen** (15 Min Setup)
2. **Code-Smell identifizieren** aus Beispiel-Repository
3. **Refactoring-Technik auswählen** gemeinsam
4. **Pair Programming** Refactoring durchführen
5. **Metriken messen** Vorher/Nachher
6. **Ergebnisse präsentieren** (5 Min pro Team)

### Live-Demo Setup
```bash
# Repository klonen
git clone https://github.com/telekom/refactoring-workshop
cd refactoring-workshop

# Metrics baseline  
mvn sonar:sonar
mvn jacoco:report

# Refactoring durchführen...

# Metrics nach Refactoring
mvn sonar:sonar  
mvn jacoco:report
```

### Evaluation Criteria
- **Readability**: Ist Code verständlicher?
- **Testability**: Sind Tests einfacher zu schreiben?
- **Maintainability**: Sind Änderungen einfacher?
- **Performance**: Wurde Performance beeinflusst?