# Composite Pattern - Trainer Notes

## Lernziele
- Teil-Ganzes-Hierarchien uniform behandeln
- Tree-Strukturen ohne Type-Casting verwalten
- Rekursive Operationen elegant implementieren

## Problem Demonstration (Initial)

### Unterschiedliche Interfaces
```java
// PROBLEM: Verschiedene Behandlung für Singles vs. Bundles
public void processItems(List<Object> items) {
    for (Object item : items) {
        if (item instanceof SingleTariff) {
            SingleTariff single = (SingleTariff) item;
            total += single.getPrice(); // Methode: getPrice()
        } else if (item instanceof TariffBundle) {
            TariffBundle bundle = (TariffBundle) item;
            total += bundle.getTotalPrice(); // Andere Methode: getTotalPrice()!
        }
        // Was wenn neue Typen? Mehr if-else!
    }
}
```

### Code zeigen: `tariffhierarchy/initial/`
- `SingleTariff`: Hat `getPrice()`
- `TariffBundle`: Hat `getTotalPrice()` - inkonsistent!
- `ProblemDemonstrator`: Zeigt hässliche Type-Checks

## Lösung: Composite Pattern (Fixed)

### Struktur
```
TariffComponent (Component)
├── SingleTariff (Leaf)
└── TariffBundle (Composite)
    ├── SingleTariff
    ├── TariffBundle  ← Kann andere Bundles enthalten!
    └── SingleTariff
```

### Uniformes Interface
```java
public abstract class TariffComponent {
    public abstract BigDecimal getPrice(); // Einheitlich!
    public abstract void printStructure(String prefix);
    
    // Default-Implementierung für Composite-Operationen
    public void add(TariffComponent component) {
        throw new UnsupportedOperationException("...");
    }
}
```

## Code-Demonstration

### 1. Einfache Verwendung
```java
// Kein Unterschied zwischen Single und Bundle!
TariffComponent mobile = new SingleTariff("Mobile", price, "Mobilfunk");
TariffComponent bundle = new TariffBundle("Familie", discount);
bundle.add(mobile);

// Einheitliche Behandlung
BigDecimal mobilePrice = mobile.getPrice();
BigDecimal bundlePrice = bundle.getPrice(); // Rekursiv berechnet
```

### 2. Verschachtelte Strukturen
```java
// Bundles in Bundles - kein Problem!
TariffBundle mobileBundle = new TariffBundle("Mobile Duo", discount);
mobileBundle.add(new SingleTariff("Line 1", price1, "Mobile"));
mobileBundle.add(new SingleTariff("Line 2", price2, "Mobile"));

TariffBundle masterBundle = new TariffBundle("Alles Inklusive", discount);
masterBundle.add(mobileBundle); // Bundle zu Bundle hinzufügen
masterBundle.add(new SingleTariff("Internet", price3, "DSL"));
```

### 3. Uniform Processing
```java
// Keine Type-Checks mehr!
public BigDecimal calculateTotal(TariffComponent[] items) {
    BigDecimal total = BigDecimal.ZERO;
    for (TariffComponent item : items) {
        total = total.add(item.getPrice()); // Einheitlich!
    }
    return total;
}
```

## Hands-on Übungen

### 1. Tree-Traversal implementieren
```java
public void printHierarchy(TariffComponent root, int level) {
    String indent = "  ".repeat(level);
    System.out.println(indent + root.getName() + ": " + root.getPrice());
    
    if (root instanceof TariffBundle) {
        TariffBundle bundle = (TariffBundle) root;
        for (TariffComponent child : bundle.getComponents()) {
            printHierarchy(child, level + 1); // Rekursion!
        }
    }
}
```

### 2. Neue Composite-Art hinzufügen
```java
// SpecialOfferBundle mit zeitlicher Begrenzung
public class SpecialOfferBundle extends TariffComponent {
    private LocalDate validUntil;
    private BigDecimal specialDiscount;
    // ... Implementation
}
```

## Pattern-Vorteile demonstrieren

### 1. Einheitliche Behandlung
- Keine instanceof-Checks
- Gleiche Methoden für alle Objekte
- Client-Code unabhängig von Struktur-Komplexität

### 2. Einfache Erweiterung
- Neue Leaf-Typen: Erweitern von Component
- Neue Composite-Typen: Erweitern und add/remove implementieren
- Client-Code bleibt unverändert

### 3. Rekursion natürlich
```java
// Preisberechnung automatisch rekursiv
@Override
public BigDecimal getPrice() {
    BigDecimal total = BigDecimal.ZERO;
    for (TariffComponent child : children) {
        total = total.add(child.getPrice()); // Rekursion!
    }
    return total.subtract(discount);
}
```

## Design Considerations

### Component-Interface Design
```java
// Sicherheits-Trade-off
public abstract class TariffComponent {
    // Option 1: Alle Methoden in Component (weniger typsicher)
    public void add(TariffComponent child) {
        throw new UnsupportedOperationException();
    }
    
    // Option 2: Nur in Composite (mehr Type-Casting)
    // add() nur in TariffBundle -> instanceof Checks nötig
}
```

### Leaf-Implementierung
```java
public class SingleTariff extends TariffComponent {
    // Composite-Methoden werfen Exception
    @Override
    public void add(TariffComponent child) {
        throw new UnsupportedOperationException("Cannot add to single tariff");
    }
    
    @Override
    public List<TariffComponent> getComponents() {
        throw new UnsupportedOperationException("Single tariff has no components");
    }
}
```

## Diskussionspunkte

### Wann Composite verwenden?
✅ **Perfekt für**:
- Baum-ähnliche Strukturen (UI, Dateisysteme, Organisationen)
- Teil-Ganzes-Hierarchien
- Rekursive Operationen
- Einheitliche Behandlung verschiedener Objekttypen

❌ **Nicht geeignet für**:
- Flache Listen ohne Hierarchie
- Verschiedene Interfaces für verschiedene Objekttypen
- Performance-kritisch (extra Objektebene)

### Real-World Beispiele
- **UI-Framework**: Swing Components, HTML DOM
- **Dateisystem**: Files und Directories
- **Organisationsstruktur**: Einzelpersonen und Abteilungen
- **Telekom**: Service-Pakete, Netzwerk-Hierarchien

## Anti-Pattern Warnung

### 1. Zu komplexe Component-Interface
```java
// SCHLECHT - zu viele Methoden für einfache Leafs
interface Component {
    void operation1();
    void operation2();
    void operation3(); // Nur für manche Composites relevant
    void operation4(); // Führt zu vielen "not supported" Exceptions
}
```

### 2. Falsche Hierarchie-Abstraktion
```java
// SCHLECHT - Wenn Objekte fundamental unterschiedlich sind
// Customer und Product sollten nicht beide TariffComponent sein
class Customer extends TariffComponent { } // Macht keinen Sinn!
```

## Code Review Checklist
- [ ] Component definiert einheitliches Interface
- [ ] Leaf implementiert nur relevante Operationen
- [ ] Composite delegiert an Kinder
- [ ] Rekursive Operationen korrekt implementiert
- [ ] Client-Code ohne Type-Casting
- [ ] Tests decken verschachtelte Strukturen ab
- [ ] Exception-Handling für nicht-unterstützte Operationen

## Performance-Hinweise
- Tree-Traversal kann teuer werden bei tiefen Hierarchien
- Caching von berechneten Werten erwägen
- Lazy-Loading für große Composite-Strukturen

## Zeitplanung
- Problem-Demo: 8 min
- Pattern-Erklärung: 12 min
- Code-Walkthrough: 15 min
- Hands-on: 20 min
- Diskussion: 10 min