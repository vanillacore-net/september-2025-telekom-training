# Decorator Pattern - Trainer Notes

## Lernziele
- Problem der Vererbungsexplosion verstehen
- Decorator Pattern als Lösung für flexible Objekterweiterung
- Runtime-Komposition vs. Compile-time-Vererbung

## Problem Demonstration (Initial)

### Vererbungsexplosion
```
BasicTariff
├── BasicTariffWithFlatrate
├── BasicTariffWithRoaming  
├── BasicTariffWithFlatrateAndRoaming
├── BasicTariffWithInsurance
├── BasicTariffWithFlatrateAndInsurance
├── BasicTariffWithRoamingAndInsurance
└── BasicTariffWithFlatrateAndRoamingAndInsurance
```

**Problem**: N Optionen = 2^N Klassen!
- 4 Optionen = 16 Klassen
- 10 Optionen = 1024 Klassen
- Nicht wartbar, nicht erweiterbar

### Code zeigen: `tarifoptions/initial/`
```java
// Jede Kombination braucht eigene Klasse
BasicTariffWithFlatrateAndRoaming tariff = 
    new BasicTariffWithFlatrateAndRoaming(...);
```

## Lösung: Decorator Pattern (Fixed)

### Struktur
- **Component**: `Tariff` Interface
- **ConcreteComponent**: `BasicTariff`
- **Decorator**: `TariffDecorator` abstract
- **ConcreteDecorators**: `FlatrateDecorator`, `RoamingDecorator`, etc.

### Code zeigen: `tarifoptions/fixed/`
```java
// Flexible Komposition zur Laufzeit
Tariff tariff = new BasicTariff("Premium", price, ...);
tariff = new FlatrateDecorator(tariff);
tariff = new RoamingDecorator(tariff);
tariff = new InsuranceDecorator(tariff);
```

## Vorteile demonstrieren

### 1. Skalierbarkeit
- **Vererbung**: N Optionen = 2^N Klassen (exponentiell)
- **Decorator**: N Optionen = N+1 Klassen (linear)

### 2. Runtime-Flexibilität
```java
// Zur Laufzeit konfigurierbar
String[] customerOptions = {"flatrate", "roaming"};
Tariff configured = base;
for (String option : customerOptions) {
    configured = decorateWith(configured, option);
}
```

### 3. Erweiterbarkeit
- Neue Option = Eine neue Decorator-Klasse
- Keine Änderung bestehender Klassen
- Open-Closed Principle erfüllt

## Hands-on Übungen

### 1. Neue Option hinzufügen
```java
// Teilnehmer sollen MultiSIMDecorator implementieren
public class MultiSIMDecorator extends TariffDecorator {
    private static final BigDecimal MULTI_SIM_COST = new BigDecimal("9.99");
    
    @Override
    public BigDecimal getPrice() {
        return tariff.getPrice().add(MULTI_SIM_COST);
    }
}
```

### 2. Performance-Vergleich
- Test ausführen und Klassenanzahl vergleichen
- Memory-Footprint diskutieren
- Runtime-Komposition vs. statische Vererbung

## Diskussionspunkte

### Wann Decorator verwenden?
✅ **Gut für**:
- Viele optionale Features
- Runtime-Konfiguration nötig
- Open-Closed Principle wichtig

❌ **Nicht gut für**:
- Wenige, statische Optionen
- Performance-kritische Bereiche (mehr Objektebenen)
- Einfache Vererbungshierarchien

### Design Considerations
- **Interface-Design**: Decorator muss alles abbilden können
- **Reihenfolge wichtig**: `new A(new B(base))` ≠ `new B(new A(base))`
- **Debugging**: Verschachtelte Objekte schwerer zu debuggen

## Code Review Checklist
- [ ] Component Interface vollständig
- [ ] ConcreteComponent implementiert alles
- [ ] BaseDecorator delegiert standardmäßig
- [ ] ConcreteDecorators überschreiben nur was nötig
- [ ] Runtime-Komposition möglich
- [ ] Tests decken verschiedene Kombinationen ab

## Real-World Beispiele
- **Java**: `InputStream` Decorators (`BufferedInputStream`, `GZIPInputStream`)
- **Web**: Middleware Pattern in Express.js
- **UI**: Component Wrappers in React
- **Telekom**: Feature-Flags, Service-Layer Enhancements

## Anti-Pattern Warnung
```java
// NICHT so machen - verletzt Interface Segregation
interface Tariff {
    BigDecimal getPrice();
    boolean hasFlatrate(); // Nur für manche Decorator relevant
    boolean hasRoaming();  // Nur für manche Decorator relevant
    // ... zu spezifisch für Component Interface
}
```

## Zeitplanung
- Problem erklären: 10 min
- Code-Vergleich: 15 min
- Hands-on Übung: 20 min
- Diskussion: 10 min