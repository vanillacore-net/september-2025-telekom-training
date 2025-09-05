# Tag 2 - Structural Patterns Code-Beispiele

## Überblick

Vollständige Code-Beispiele für alle 4 Structural Design Patterns mit jeweils:
- ❌ **Initial Version**: Zeigt das Problem auf
- ✅ **Fixed Version**: Löst das Problem mit dem Pattern
- 🧪 **Tests**: Demonstrieren Performance & Flexibilität
- 📚 **Trainer-Notes**: Detaillierte Trainingsanleitungen

## Pattern-Übersicht

### 1. Decorator Pattern - Tarif-Optionen
**Problem**: Vererbungsexplosion bei Tarif-Kombinationen
- N Optionen = 2^N Klassen (4 Optionen = 16 Klassen!)
- Nicht wartbar, nicht erweiterbar

**Lösung**: Flexible Runtime-Komposition
```java
Tariff tariff = new BasicTariff("Premium", price, ...);
tariff = new FlatrateDecorator(tariff);
tariff = new RoamingDecorator(tariff);
tariff = new InsuranceDecorator(tariff);
```

**Dateien**:
- `tarifoptions/initial/` - Vererbungsexplosion
- `tarifoptions/fixed/` - Decorator Pattern
- Tests: Performance-Vergleich und Flexibilität

### 2. Composite Pattern - Tarif-Hierarchien
**Problem**: Uneinheitliche Behandlung von Einzel-Tarifen vs. Bundles
- Verschiedene APIs (`getPrice()` vs `getTotalPrice()`)
- Hässliche Type-Checks und Casting

**Lösung**: Einheitliches Interface für Teil-Ganzes-Hierarchien
```java
// Beide gleich behandelbar!
TariffComponent single = new SingleTariff(...);
TariffComponent bundle = new TariffBundle(...);
bundle.add(single);
BigDecimal price = bundle.getPrice(); // Rekursiv!
```

**Dateien**:
- `tariffhierarchy/initial/` - Inkonsistente Behandlung
- `tariffhierarchy/fixed/` - Composite Pattern
- Tests: Verschachtelte Strukturen und einheitliche Behandlung

### 3. Proxy Pattern - Customer Caching
**Problem**: Jeder DB-Zugriff dauert 100ms - keine Wiederverwendung
- 3 gleiche Abfragen = 300ms Latenz
- Keine Intelligenz bei wiederholten Zugriffen

**Lösung**: Intelligentes Caching mit TTL und Invalidierung
```java
CachedCustomerServiceProxy proxy = new CachedCustomerServiceProxy(realService, 5000);
Customer c1 = proxy.getCustomer("CUST001"); // 100ms - Cache Miss
Customer c2 = proxy.getCustomer("CUST001"); // ~0ms - Cache Hit!
```

**Performance**: 50-75% Verbesserung bei wiederholten Zugriffen

**Dateien**:
- `customercaching/initial/` - Teure DB-Zugriffe
- `customercaching/fixed/` - Proxy mit Caching
- Tests: Performance-Messungen und Cache-Verhalten

### 4. Bridge Pattern - Payment Gateway
**Problem**: Provider-Lock-in mit hässlichen if-else Ketten
- Jeder Provider andere API (PayPal, Stripe, etc.)
- Client-Code muss alle Provider kennen

**Lösung**: Abstraktion von Implementation trennen
```java
// Gleiche Abstraktion, verschiedene Provider
PaymentProcessor processor = new LoggingPaymentProcessor(paypal);
PaymentResult result1 = processor.processPayment(...); // PayPal

processor = new LoggingPaymentProcessor(stripe);  
PaymentResult result2 = processor.processPayment(...); // Stripe
```

**Vorteil**: N Abstractions × M Implementations ohne N×M Klassen

**Dateien**:
- `paymentbridge/initial/` - Provider-Coupling
- `paymentbridge/fixed/` - Bridge Pattern
- Tests: Runtime-Switching und Extensibility

## Tests ausführen

```bash
# Alle Tests
./docker-build.sh test

# Nur Tag 2 Tests (ohne Checkstyle)
docker run --rm -v "$(pwd)":/app -w /app examples-maven-ci mvn test -pl day2-examples -Dcheckstyle.skip=true
```

## Trainer-Materialien

### 1. `trainer-notes/01-decorator-pattern.md`
- Vererbungsexplosion vs. Runtime-Komposition
- Performance-Vergleich: 2^N vs. N+1 Klassen
- Hands-on: Neue Decorator hinzufügen

### 2. `trainer-notes/02-composite-pattern.md`  
- Teil-Ganzes-Hierarchien ohne Type-Casting
- Rekursive Operationen
- Tree-Traversal Implementierung

### 3. `trainer-notes/03-proxy-pattern.md`
- Performance-Analyse mit Live-Messungen
- Cache-Strategien (TTL, LRU, Invalidierung)
- Verschiedene Proxy-Arten (Security, Lazy Loading)

### 4. `trainer-notes/04-bridge-pattern.md`
- Bridge vs. Strategy Pattern
- Provider-Lock-in vermeiden
- Kombinatorische Explosion verhindern

## Code-Qualität

✅ **Alle Tests grün**: 21 Tests, 0 Failures  
✅ **Java 11 kompatibel**: Läuft im Docker-Container  
✅ **Performance-Messungen**: Live-Demonstrationen möglich  
✅ **Trainer-Ready**: Detaillierte Anleitungen mit Zeitplanung

## Pattern-Benefits Live zeigen

### Decorator: 
```bash
# Zeigt Klassenexplosion vs. lineare Skalierung
# 4 Optionen: 16 Klassen vs. 5 Klassen
```

### Composite:
```bash
# Zeigt einheitliche Behandlung ohne Type-Checks
# Rekursive Strukturen ohne komplexe Logik
```

### Proxy:
```bash
# Live Performance-Messung:
# Ohne Cache: 300ms+ | Mit Cache: <150ms
# Hit-Ratio: 66.7% bei 3 Aufrufen
```

### Bridge:
```bash
# Zeigt Provider-Switching zur Laufzeit
# N×M Kombinationen ohne N×M Klassen
```

## Extensibility demonstrieren

Alle Patterns sind erweiterbar - live im Training neue Komponenten hinzufügen:
- **Decorator**: Neue `MultiSIMDecorator`
- **Composite**: Neue `SpecialOfferBundle`  
- **Proxy**: Security-Proxy implementieren
- **Bridge**: Neuen Payment-Provider hinzufügen

## Zeitplanung pro Pattern
- Problem-Demo: 8-10 min
- Pattern-Erklärung: 10-12 min  
- Code-Walkthrough: 15 min
- Hands-on: 15-20 min
- Diskussion: 5-10 min

**Total pro Pattern**: ~60 min  
**Tag 2 Gesamt**: ~4 Stunden + Pausen