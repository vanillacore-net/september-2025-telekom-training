# Modul 1: Code-Analyse & Factory Method Pattern

## Lernziele
- Code-Smells in Legacy-Code identifizieren
- Factory Method Pattern verstehen und anwenden
- Refactoring-Strategien entwickeln
- Single Responsibility Principle praktisch umsetzen

## 1. Problem-Motivation

### Ausgangssituation: Customer Service System
Ein typisches Problem in gewachsenen Telekom-Systemen: Die Kundenbetreuung muss verschiedene Kunden-Typen verwalten - Privatkunden, Geschäftskunden, Premium-Kunden. Der vorhandene Code enthält mehrere Code-Smells.

### Problematischer Code (Code-Smell Analyse)

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
                
            case "PREMIUM":
                Customer premiumCustomer = new Customer();
                premiumCustomer.setName(name);
                premiumCustomer.setContractId(contractId);
                premiumCustomer.setTariffOptions(Arrays.asList("Premium", "Unlimited"));
                premiumCustomer.setPaymentMethod("DirectDebit");
                premiumCustomer.setSupportLevel("VIP");
                premiumCustomer.setPrioritySupport(true);
                premiumCustomer.setDedicatedLine(true);
                return premiumCustomer;
                
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
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

## 2. Factory Method Pattern - Struktur

### Konzept
Das Factory Method Pattern definiert eine Schnittstelle zum Erstellen von Objekten, aber lässt Unterklassen entscheiden, welche Klasse instantiiert wird.

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

## 3. Refactoring-Schritte

### Schritt 1: Kunden-Abstraktion erstellen

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

### Schritt 2: Konkrete Kunden-Implementierungen

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
        // Privatkunden-spezifische Vertragsabwicklung
        validatePersonalData();
        setupBasicServices();
    }
    
    private void validatePersonalData() {
        // Personalausweis-Prüfung
    }
    
    private void setupBasicServices() {
        // Standard-Services aktivieren
    }
}
```

### Schritt 3: Factory Method Creator

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

### Schritt 4: Konkrete Factory-Implementierungen

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

## 4. SOLID-Prinzipien Integration

### Single Responsibility Principle (SRP)
- Jede Factory-Klasse ist nur für einen Kunden-Typ zuständig
- Customer-Klassen kapseln ihre spezifische Geschäftslogik

### Open/Closed Principle (OCP)
- Neue Kunden-Typen durch neue Factory-Klassen hinzufügbar
- Bestehender Code muss nicht geändert werden

### Dependency Inversion Principle (DIP)
- Abhängigkeit zu Customer-Interface, nicht zu konkreten Implementierungen

## 5. Anti-Pattern Vermeidung

### Simple Factory vs Factory Method
**Anti-Pattern: Simple Factory mit Switch**
```java
// Vermeiden: Zentralisierte if/switch-Logik
public class CustomerSimpleFactory {
    public Customer create(String type) {
        if ("PRIVATE".equals(type)) {
            return new PrivateCustomer();
        } // ... weitere if-Blöcke
    }
}
```

**Korrekt: Factory Method Pattern**
- Polymorphismus statt Konditionals
- Erweiterbarkeit ohne Modifikation bestehenden Codes

### Über-Engineering vermeiden
- Factory Method nur bei tatsächlicher Variabilität einsetzen
- Nicht für einfache Objekterstellung verwenden

## 6. Moderne Alternativen

### Java 8+ Ansatz mit Function Interface
```java
public enum CustomerType {
    PRIVATE(PrivateCustomer::new),
    BUSINESS(BusinessCustomer::new),
    PREMIUM(PremiumCustomer::new);
    
    private final BiFunction<String, String, Customer> factory;
    
    CustomerType(BiFunction<String, String, Customer> factory) {
        this.factory = factory;
    }
    
    public Customer create(String name, String contractId) {
        return factory.apply(name, contractId);
    }
}
```

## 7. Diskussionspunkte

### Technische Diskussion
1. **Wann ist Factory Method sinnvoll?**
   - Bei komplexer Objekterstellung
   - Wenn Erstellung von Geschäftslogik getrennt werden soll
   - Bei häufigen neuen Varianten

2. **Performance-Überlegungen**
   - Speicher-Overhead durch zusätzliche Klassen
   - Laufzeit-Impact bei vielen Factory-Aufrufen

3. **Wartbarkeit**
   - Klarere Trennung von Zuständigkeiten
   - Testbarkeit durch bessere Kapselung

### Architektur-Diskussion
1. **Integration in bestehende Systeme**
   - Schrittweise Migration möglich?
   - Auswirkungen auf abhängige Module?

2. **Telekom-spezifische Herausforderungen**
   - Umgang mit Legacy-Datenstrukturen
   - Integration verschiedener Backend-Systeme

## 8. Praktische Übung (20 Minuten)

### Aufgabe: Service-Provisioning Refactoring

**Gegeben:** Service-Provisioning Code mit Code-Smells
```java
public class ServiceProvisioning {
    public void activateService(String serviceType, String customerId) {
        if ("DSL".equals(serviceType)) {
            // 15 Zeilen DSL-Aktivierung
        } else if ("FIBER".equals(serviceType)) {
            // 18 Zeilen Fiber-Aktivierung  
        } else if ("MOBILE".equals(serviceType)) {
            // 12 Zeilen Mobile-Aktivierung
        }
    }
}
```

### Aufgaben
1. **Code-Smell Identifikation** (5 Min)
   - Welche Code-Smells erkennen Sie?
   - Wie verletzt der Code SOLID-Prinzipien?

2. **Factory Method Design** (10 Min)
   - Entwerfen Sie eine Lösung mit Factory Method Pattern
   - Definieren Sie Service-Interface und konkrete Implementierungen

3. **Implementation** (5 Min)
   - Implementieren Sie eine der Service-Klassen vollständig

### Lösungsansatz
```java
public interface TelecomService {
    void activate(String customerId);
    boolean isAvailable(String location);
}

public abstract class ServiceProvisioningFactory {
    protected abstract TelecomService createService();
    
    public final void provisionService(String customerId, String location) {
        TelecomService service = createService();
        if (service.isAvailable(location)) {
            service.activate(customerId);
        }
    }
}
```

## 9. Zusammenfassung

### Schlüsselerkenntnisse
- Factory Method löst Code-Smells durch Polymorphismus
- SOLID-Prinzipien werden natürlich erfüllt
- Erweiterbarkeit ohne Modifikation bestehenden Codes
- Klare Trennung von Objekterstellung und Geschäftslogik

### Nächste Schritte
- Im nächsten Modul: Abstract Factory für Service-Familien
- Kombination verschiedener Creational Patterns
- Integration in Layered Architecture

### Anti-Pattern Checkliste
- ✗ Zentrale Switch/If-Statements für Objekterstellung
- ✗ Konstruktor-Aufrufe verstreut im Code
- ✗ Harte Abhängigkeiten zu konkreten Klassen
- ✓ Polymorphismus statt Konditionals
- ✓ Geschäftslogik von Objekterstellung getrennt