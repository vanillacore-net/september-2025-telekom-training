# Code Smell Katalog - Telekom Architektur Workshop

## **Zweck**
Systematischer Katalog zur Erkennung und Bewertung von Code Smells in Telekom-Systemen. Als Quick-Reference während des Workshops und für tägliche Entwicklungsarbeit.

## **Prioritätsstufen**
- **KRITISCH**: Sofort beheben - Betriebsrisiko
- **HOCH**: In aktueller Iteration beheben
- **MITTEL**: Nächste Iteration planen
- **NIEDRIG**: Bei Gelegenheit verbessern

---

## 1. **Long Method - KRITISCH**
**Erkennungsmetriken:**
- Methoden >30 Zeilen (Telekom Standard)
- Cyclomatic Complexity >10
- Mehr als 5 Parameter

**Telekom-Beispiel:**
```java
// SCHLECHT: 85 Zeilen Monster-Methode
public void processCustomerOrder(String customerId, String orderId, 
    String productType, boolean isBusinessCustomer, double discount) {
    // Validierung (15 Zeilen)
    // Preisberechnung (25 Zeilen)
    // Lagerprüfung (20 Zeilen)
    // Bestellungslogik (25 Zeilen)
}
```

**Auswirkung:** Wartungsaufwand +300%, Fehlerrate +200%

---

## 2. **God Object - KRITISCH**
**Erkennungsmetriken:**
- Klassen >500 Zeilen
- >20 öffentliche Methoden
- >15 Instanzvariablen

**Telekom-Beispiel:**
```java
// SCHLECHT: Alles-könner Klasse
public class CustomerManager {
    // 847 Zeilen Code
    // Kundenverwaltung + Billing + Provisioning + Reporting
    public void createCustomer() { ... }
    public void calculateBill() { ... }
    public void provisionService() { ... }
    public void generateReport() { ... }
    // ... 50 weitere Methoden
}
```

**Auswirkung:** Team-Konflikte, Merge-Probleme, Testbarkeit↓

---

## 3. **Duplicate Code - HOCH**
**Erkennungsmetriken:**
- Identische Codeblöcke >6 Zeilen
- Copy-Paste Faktor >10%
- Ähnliche Algorithmen in verschiedenen Klassen

**Telekom-Beispiel:**
```java
// SCHLECHT: 3x derselbe Validierungscode
// In CustomerService, OrderService, BillingService
if (customerId == null || customerId.length() != 10 || 
    !customerId.matches("\\d{10}")) {
    throw new ValidationException("Invalid customer ID");
}
```

**Auswirkung:** Bugfixes müssen 3x gemacht werden

---

## 4. **Feature Envy - MITTEL**
**Erkennungsmetriken:**
- Methode nutzt mehr fremde als eigene Felder
- >5 Methodenaufrufe auf anderen Objekten
- Enge Kopplung zwischen Klassen

**Telekom-Beispiel:**
```java
// SCHLECHT: CustomerService ist neidisch auf Customer
public class CustomerService {
    public void processCustomer(Customer customer) {
        customer.getAddress().getStreet();
        customer.getAddress().getCity();
        customer.getAddress().getZipCode();
        customer.getAddress().validate();
        // Gehört eigentlich in Customer-Klasse
    }
}
```

---

## 5. **Shotgun Surgery - HOCH**
**Erkennungsmetriken:**
- Eine Änderung betrifft >5 Klassen
- Änderungen verstreut über Module
- Hohe Kopplung zwischen Komponenten

**Telekom-Beispiel:**
```java
// SCHLECHT: Neues Feld "contractType" erfordert
// Änderungen in: Customer.java, CustomerDAO.java, 
// CustomerService.java, CustomerController.java,
// CustomerValidator.java, CustomerMapper.java
```

**Auswirkung:** Hohe Änderungskosten, Regression-Risiko

---

## 6. **Data Class - MITTEL**
**Erkennungsmetriken:**
- Nur Getter/Setter ohne Logik
- Keine Validierung oder Verhalten
- Wird von anderen Klassen "missbraucht"

**Telekom-Beispiel:**
```java
// SCHLECHT: Passive Datenklasse
public class Customer {
    private String id;
    private String name;
    // Nur Getter/Setter, keine Geschäftslogik
    // Validierung passiert in CustomerService
}
```

---

## 7. **Large Class - HOCH**
**Erkennungsmetriken:**
- >400 Zeilen Code
- >20 Methoden
- Behandelt mehrere Verantwortlichkeiten

**Telekom-Beispiel:**
```java
// SCHLECHT: TelekomAPIController mit 623 Zeilen
// Behandelt: Authentication, Billing, Provisioning, Support
```

**Refactoring:** Extract Class, Single Responsibility

---

## 8. **Switch Statements - MITTEL**
**Erkennungsmetriken:**
- Switch mit >5 Cases
- Mehrere Switch auf gleichen Typ
- Neue Cases erfordern Codeänderungen

**Telekom-Beispiel:**
```java
// SCHLECHT: Überall switch(customerType)
switch(customer.getType()) {
    case PRIVATE: /* 20 Zeilen */ break;
    case BUSINESS: /* 25 Zeilen */ break;
    case PREMIUM: /* 30 Zeilen */ break;
    // Neuer Kundentyp = überall Code ändern
}
```

**Lösung:** Strategy Pattern verwenden

---

## 9. **Speculative Generality - MITTEL**
**Erkennungsmetriken:**
- Abstrakte Klassen mit nur einem Erben
- Framework-Code der nie genutzt wird
- "Vielleicht-brauchen-wir-das" Features

**Telekom-Beispiel:**
```java
// SCHLECHT: Über-engineerte Abstraktion
abstract class BaseCustomerProcessor {
    // Komplex, aber nur eine Implementierung
}
class TelekomCustomerProcessor extends BaseCustomerProcessor {
    // Einziger Erbe seit 2 Jahren
}
```

---

## 10. **Comments - MITTEL**
**Erkennungsmetriken:**
- Kommentare erklären WAS statt WARUM
- Code und Kommentar widersprechen sich
- Auskommentierter Code im Repository

**Telekom-Beispiel:**
```java
// SCHLECHT: Redundante/veraltete Kommentare
// Addiert 1 zum Counter (offensichtlich!)
counter++; 

// Berechnet Preis für Geschäftskunden (Kommentar von 2019)
// Aktueller Code: Berechnet für alle Kunden
```

---

## 11. **Dead Code - NIEDRIG**
**Erkennungsmetriken:**
- Nie aufgerufene Methoden
- Leere catch-Blöcke
- Auskommentierte Code-Bereiche

**Telekom-Beispiel:**
```java
// SCHLECHT: Legacy-Methoden seit Jahren unbenutzt
@Deprecated
public void oldBillingCalculation() {
    // 50 Zeilen Code, der nie ausgeführt wird
}
```

**Tool-Tipp:** SonarQube findet automatisch Dead Code

---

## 12. **Primitive Obsession - HOCH**
**Erkennungsmetriken:**
- String/int statt Wertobjekte
- Validierungslogik verstreut
- Typunsicherheit bei Parametern

**Telekom-Beispiel:**
```java
// SCHLECHT: String für strukturierte Daten
public void createCustomer(String customerId, String phoneNumber) {
    // customerId Format? phoneNumber international?
}

// BESSER: Value Objects
public void createCustomer(CustomerId id, PhoneNumber phone) {
    // Typsicherheit und Validierung eingebaut
}
```

---

## 13. **Refused Bequest - MITTEL**
**Erkennungsmetriken:**
- Subklasse überschreibt Eltern-Methoden mit "do nothing"
- Liskov Substitution Principle verletzt
- Vererbungshierarchie macht keinen Sinn

**Telekom-Beispiel:**
```java
// SCHLECHT: BusinessCustomer weigert sich zu "zahlen"
class Customer {
    public void payBill() { /* Standard-Zahlung */ }
}

class BusinessCustomer extends Customer {
    @Override
    public void payBill() {
        // Geschäftskunden zahlen anders - macht Vererbung kaputt
        throw new UnsupportedOperationException();
    }
}
```

---

## 14. **Message Chains - MITTEL**
**Erkennungsmetriken:**
- Mehrere verkettete Methodenaufrufe
- customer.getAccount().getBilling().getInvoice().getAmount()
- Hohe Kopplung durch Navigation

**Telekom-Beispiel:**
```java
// SCHLECHT: Tiefe Objektnavigation
String city = customer.getAddress().getLocation()
    .getGeoData().getCity().getName();

// BESSER: Tell Don't Ask
String city = customer.getCityName();
```

---

## 15. **Middle Man - MITTEL**
**Erkennungsmetriken:**
- Klasse delegiert nur weiter
- >80% der Methoden sind Wrapper
- Keine eigene Geschäftslogik

**Telekom-Beispiel:**
```java
// SCHLECHT: Unnötige Zwischenschicht
public class CustomerServiceWrapper {
    private CustomerService service;
    
    public Customer getCustomer(String id) {
        return service.getCustomer(id); // Nur Weiterleitung
    }
}
```

---

## 16. **Inappropriate Intimacy - HOCH**
**Erkennungsmetriken:**
- Klassen greifen auf private Felder zu
- Friend-Klassen oder Package-private Zugriffe
- Enge Kopplung zwischen Modulen

**Telekom-Beispiel:**
```java
// SCHLECHT: CustomerService kennt interne Billing-Details
public class CustomerService {
    public void updateCustomer(Customer customer) {
        // Direkter Zugriff auf Billing-Internals
        customer.billing.internalRecalculate();
    }
}
```

---

## 17. **Lazy Class - NIEDRIG**
**Erkennungsmetriken:**
- Klassen <50 Zeilen ohne Rechtfertigung
- Nur eine oder zwei Methoden
- Könnte als Methode implementiert werden

**Telekom-Beispiel:**
```java
// SCHLECHT: Klasse mit nur einer Hilfsmethode
public class PhoneNumberFormatter {
    public String format(String phone) {
        return phone.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
    }
}
// Könnte Utility-Methode sein
```

---

## 18. **Alternative Classes with Different Interfaces - MITTEL**
**Erkennungsmetriken:**
- Klassen tun dasselbe mit anderen Namen
- Unterschiedliche APIs für gleiche Funktion
- Code-Duplikation durch Interface-Unterschiede

**Telekom-Beispiel:**
```java
// SCHLECHT: Zwei APIs für Kundenverwaltung
class CustomerManagerV1 {
    public Customer getCustomerById(String id) { ... }
}

class CustomerManagerV2 {
    public Customer findCustomer(String identifier) { ... }
    // Gleiche Funktion, anderes Interface
}
```

---

## **Tool-Integration**

### **SonarQube Regeln für Telekom**
```properties
# Telekom-spezifische Limits
sonar.java.method.complexity.maximum=10
sonar.java.class.complexity.maximum=50  
sonar.java.method.lines.maximum=30
sonar.java.class.lines.maximum=400
```

### **IntelliJ IDEA Inspections**
- Code → Inspect Code
- Aktiviere "Code Smells" Kategorie
- Custom Scope: "Telekom Production Code"

### **PMD Integration**
```xml
<ruleset>
    <rule ref="category/java/design.xml/GodClass">
        <properties>
            <property name="tla" value="15"/>
        </properties>
    </rule>
</ruleset>
```

---

## **Erfolgsmessung**

| Metrik | Zielwert | Kritischer Wert |
|--------|----------|------------------|
| Cyclomatic Complexity | <10 | >15 |
| Methodenlänge | <30 LOC | >50 LOC |
| Klassenlänge | <400 LOC | >800 LOC |
| Code Coverage | >80% | <60% |
| Technical Debt Ratio | <5% | >10% |

---

## **Workshop-Einsatz**

### **Als Checkliste verwenden**
1. Code-Review mit diesem Katalog
2. Team diskutiert erkannte Smells  
3. Priorisierung nach Telekom-Kriterien
4. Refactoring-Planung mit Techniken-Guide

### **Interaktive Übung**
- Teilnehmer identifizieren Smells in Beispielcode
- Gemeinsame Bewertung und Priorisierung
- Refactoring-Strategien entwickeln