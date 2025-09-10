# Block 2: Structural Patterns

## Pattern Overview

Block 2 behandelt Structural Patterns - Entwurfsmuster, die sich mit der Komposition von Objekten und Klassen beschäftigen. Diese Patterns lösen Probleme der Objekt-Zusammensetzung und System-Architektur.

---

# Decorator Pattern

## Slide 1: Was ist hier schlecht?

```java
// Exponentielles Wachstum-Problem
BasicTariff
BasicTariffWithData
BasicTariffWithDataAndRoaming  
// Bei n Features = 2^n Klassen!

// Vererbungs-Explosion
public class BasicTariffWithDataAndRoamingAndHotspot 
    extends BasicTariffWithDataAndRoaming {
    // Bei nur 10 Tarif-Optionen entstehen 1.024 Klassen!
}
```

**Note:** Die Deutsche Telekom bietet verschiedenste Tarif-Optionen: Datenvolumen-Upgrades, Roaming-Pakete, Streaming-Services, Hotspot-Zugang. Der naive OOP-Ansatz führt zu exponentieller Klassenzahl.

---

## Slide 2: Code Smells identifiziert

• **Klassen-Explosion**: Bei n Features entstehen 2^n Klassen
• **Code-Duplikation**: Bugfixes müssen in hunderten Klassen repliziert werden  
• **Open/Closed Verletzung**: Jede neue Option erfordert Dutzende neuer Klassen
• **Wartungs-Alptraum**: Änderungen propagieren durch die gesamte Hierarchie
• **Kombinatorisches Problem**: Unmöglich alle Feature-Kombinationen zu modellieren
• **Tight Coupling**: Features sind fest mit Basis-Implementierung gekoppelt
• **Inflexibilität**: Keine zur-Laufzeit-Konfiguration von Features möglich

**Note:** Das mathematische Problem: Bei nur 10 Tarif-Optionen entstehen 1.024 Klassen! Dies verletzt fundamental das Open/Closed Principle.

---

## Slide 3: Lösung: Decorator Pattern

• **Komposition über Vererbung**: Objekte zur Laufzeit "umhüllen"
• **Gemeinsame Schnittstelle**: Component-Interface für einheitliche Behandlung  
• **Transparente Erweiterung**: Decorators sind für Clients unsichtbar
• **Beliebige Kombinierbarkeit**: Features können flexibel kombiniert werden
• **Einzelne Verantwortung**: Jeder Decorator fügt genau ein Feature hinzu
• **Zur-Laufzeit-Konfiguration**: Dynamische Objekt-Zusammensetzung
• **Template Method Integration**: Abstraktes Decorator-Rückgrat für Standards

**Note:** Decorator Pattern löst das Problem durch Komposition statt Vererbung. Ein Tarif-Objekt wird von "Decorator"-Objekten umschlossen, die jeweils eine zusätzliche Funktion bereitstellen.

---

## Slide 4: Implementierung

```java
// Gemeinsame Schnittstelle
public interface TariffService {
    BigDecimal calculatePrice();
    Set<String> getFeatures(); 
}

// Abstrakter Decorator
public abstract class TariffDecorator implements TariffService {
    protected final TariffService decoratedTariff;
    
    public BigDecimal calculatePrice() {
        return decoratedTariff.calculatePrice(); // Delegation
    }
}

// Konkreter Decorator  
public class DataOptionDecorator extends TariffDecorator {
    private final BigDecimal dataPrice;
    
    @Override
    public BigDecimal calculatePrice() {
        return super.calculatePrice().add(dataPrice); // Erweiterung
    }
}
```

**Note:** Jeder Decorator implementiert dieselbe Schnittstelle wie das umhüllte Objekt - so können Decorators beliebig geschachtelt werden. Factory Pattern übernimmt die Objektkomposition und versteckt die Komplexität.

---

# Composite Pattern  

## Slide 5: Was ist hier schlecht?

```java
// Separate Behandlung für Einzeltarife vs. Gruppen
public class TariffService {
    public void processOrder(Object tariff) {
        if (tariff instanceof IndividualTariff) {
            IndividualTariff individual = (IndividualTariff) tariff;
            // Individuelle Verarbeitung
        } else if (tariff instanceof FamilyTariff) {
            FamilyTariff family = (FamilyTariff) tariff;
            // Familiengruppen-spezifische Logik
            for (IndividualTariff member : family.getMembers()) {
                // Rekursive Verarbeitung...
            }
        }
        // Komplexe If-Verschachtelung für alle Tarif-Typen
    }
}
```

**Note:** Telekom verkauft komplexe Familien-Strukturen mit verschachtelten Abhängigkeiten. Separate Klassen für jede Konstellation führen zu kombinatorischer Explosion.

---

## Slide 6: Code Smells identifiziert

• **Type-Checking Nightmare**: Instanceof-Checks überall im Code
• **Rekursive Logik-Duplikation**: Gleiche Traversierung in jeder Methode
• **Kombinatorische Explosion**: Neue Tarif-Typen = neue If-Zweige überall
• **Client-Komplexität**: Clients müssen alle Tarif-Typen kennen
• **Tight Coupling**: Client-Code gekoppelt an konkrete Tarif-Typen
• **Inkonsistente Behandlung**: Verschiedene Logik für ähnliche Operationen  
• **Schwer erweiterbar**: Neue Hierarchie-Ebenen = massive Code-Änderungen

**Note:** Das Komplexitäts-Dilemma: Wie behandeln wir Einzeltarife und Gruppen-Tarife einheitlich? Naive Ansätze scheitern an kombinatorischer Explosion.

---

## Slide 7: Lösung: Composite Pattern

• **Einheitliche Behandlung**: Einzelobjekte und Sammlungen identisch behandeln
• **Rekursive Struktur**: Composites können andere Composites enthalten
• **Transparenz für Client**: Client arbeitet nur mit Component-Interface  
• **Natürliche Hierarchie-Abbildung**: Teil-Ganzes-Beziehungen elegant modelliert
• **Liskov Substitution**: Blätter und Äste vollständig austauschbar
• **Funktionale Aggregation**: Stream-API macht Traversierung elegant
• **Strategy Pattern Integration**: Gruppen-Rabatte bleiben austauschbar

**Note:** Composite Pattern behandelt Einzelobjekte und Objektsammlungen einheitlich. Ein Familien-Tarif wird genauso behandelt wie ein Einzel-Tarif.

---

## Slide 8: Implementierung

```java
// Component-Interface
public interface TariffComponent {
    BigDecimal calculateTotalPrice();
    List<String> getAllFeatures();
    int getContractCount();
}

// Composite-Implementierung
public class TariffGroup implements TariffComponent {
    private final List<TariffComponent> children = new ArrayList<>();
    
    public BigDecimal calculateTotalPrice() {
        BigDecimal total = children.stream()
            .map(TariffComponent::calculateTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return discountStrategy.applyGroupDiscount(total);
    }
    
    public int getContractCount() {
        return children.stream()
            .mapToInt(TariffComponent::getContractCount)
            .sum(); // Rekursive Aggregation
    }
}
```

**Note:** Transparente Rekursion: TariffGroup "weiß" nicht, ob Kinder Leafs oder Composites sind. Visitor Pattern kann für Performance-Optimierung bei tiefen Hierarchien eingesetzt werden.

---

# Facade Pattern

## Slide 9: Was ist hier schlecht?

```java
// Client-Albtraum: 15+ Service-Abhängigkeiten
public class TariffOrderClient {
    private TariffValidationService validation;
    private CustomerCreditCheckService creditCheck;
    private PricingService pricing;
    private InventoryService inventory;
    private NotificationService notification;
    // ... 10 weitere Services
    
    public void createOrder(TariffRequest request) {
        // 50+ Zeilen Orchestrierung
        if (validation.check() && creditCheck.approve() && 
            pricing.calculate() && inventory.reserve()) {
            // Komplexe If-Verschachtelung
        }
    }
}
```

**Note:** Eine einfache Tarif-Bestellung erfordert Koordination von 15+ Microservices. Client-Code wird unlesesbar und untestbar.

---

## Slide 10: Code Smells identifiziert

• **Abhängigkeits-Explosion**: Client kennt 15+ Services intimement
• **Orchestrierungs-Komplexität**: 50+ Zeilen if/else-Logik pro Use Case
• **Fehlerbehandlungs-Chaos**: Exponentiell wachsende Fehlerpfade
• **Testing-Alptraum**: Mocking von 15+ Services pro Unit Test
• **Deployment-Fragilität**: Client bricht bei jeder Service-Änderung
• **Code-Duplikation**: Gleiche Orchestrierung in mehreren Clients
• **Service-Interface-Leakage**: Interne Service-Details überall sichtbar

**Note:** Das Komplexitäts-Dilemma: Jede Service-Änderung bricht den Client. Deployment-Abhängigkeiten werden unbeherrschbar.

---

## Slide 11: Lösung: Facade Pattern

• **Einheitliche Schnittstelle**: Eine einfache API vor komplexem Subsystem
• **Komplexitäts-Kapselung**: Interne Orchestrierung für Clients unsichtbar  
• **Use-Case-spezifische APIs**: APIs sprechen die Sprache der Business-User
• **Zentrale Fehlerbehandlung**: Standardisierte Error-Responses
• **Versionierungs-Puffer**: API-Änderungen ohne Client-Brüche möglich
• **Performance-Optimierung**: Parallelisierung und Caching zentral möglich
• **Service-Abstraktion**: Services können ausgetauscht werden ohne Client-Impact

**Note:** Facade Pattern erstellt eine "einfache" Schnittstelle vor einem "komplexen" Subsystem. Client-Sicht: 1 Methode statt 15 Service-Calls.

---

## Slide 12: Implementierung

```java
@Service
public class TariffOrderFacade {
    // Alle komplexen Dependencies sind privat gekapselt
    private final TariffValidationService validation;
    private final CreditCheckService creditCheck;
    // ... weitere Services (privat!)
    
    public TariffOrderResult createTariffOrder(TariffOrderRequest request) {
        try {
            if (!validateOrder(request).isValid()) {
                return TariffOrderResult.failed("Validation failed");
            }
            return executeOrderProcess(request); // Details versteckt
        } catch (Exception e) {
            return TariffOrderResult.failed(e.getMessage());
        }
    }
    
    // Parallele Validierungen für Performance
    private CompletableFuture<ValidationResult> validateOrderAsync(request) {
        // Unabhängige Service-Calls parallel ausführen
    }
}
```

**Note:** Facade vs. Microservice Gateway: Façade ist ein architektonisches Pattern, Gateway ist infrastrukturell. Circuit Breaker, Timeout Management und Parallelisierung können integriert werden.

---

# Proxy Pattern

## Slide 13: Was ist hier schlecht?

```java
// Synchrone, ungecachte DB-Zugriffe
public class CustomerService {
    public CustomerData getCustomer(String id) {
        // Legacy-DB: 200ms+ pro Query!
        return legacyDatabase.findById(id);
    }
}

// Bei 1000 Requests/sec:
// 1000 × 200ms = 200 Sekunden DB-Zeit pro Sekunde
// System bricht zusammen!

// Memory-Verschwendung: Jeder Customer 2KB
// 50 Million Kunden × 2KB = 100GB RAM nur für Basis-Kundendaten
```

**Note:** Legacy-Datenbanken sind oft der Flaschenhals moderner Systeme. Hochfrequente Zugriffe führen zu Cascading Failures.

---

## Slide 14: Code Smells identifiziert

• **Latenz-Explosion**: 200ms+ Response-Zeit pro DB-Query
• **Memory-Verschwendung**: Redundante Daten millionenfach geladen
• **Cascade-Failure-Risk**: Langsame Services blockieren nachgelagerte Systeme
• **No Caching Strategy**: Identische Queries werden wiederholt ausgeführt
• **Synchronous Blocking**: Alle Threads warten auf DB-Response
• **Resource Exhaustion**: Connection Pools werden erschöpft  
• **Poor User Experience**: Web-Interfaces werden unbenutzbar

**Note:** Die Latenz-Spirale: 1000+ Customer-Abfragen pro Sekunde multipliziert mit 200ms = System-Kollaps.

---

## Slide 15: Lösung: Proxy Pattern

• **Intelligente Zugriffssteuerung**: Proxy kontrolliert Zugriff auf teure Objekte
• **Transparente Optimierung**: Client merkt nichts von Performance-Verbesserungen
• **Lazy Loading**: Objekte nur bei tatsächlichem Bedarf laden
• **Caching-Integration**: Intelligente Zwischenspeicherung mit konfigurierbaren Policies
• **Security-Layer**: Transparent implementierte Sicherheitsprüfungen
• **Remote Proxy**: Netzwerk-Komplexität verstecken mit Resilience Patterns
• **Monitoring-Integration**: Zentrale Metriken für alle Service-Calls

**Note:** Proxy Pattern stellt einen "Stellvertreter" vor ein teures Objekt. Der Proxy kann Caching, Lazy Loading, Security etc. transparent hinzufügen.

---

## Slide 16: Implementierung

```java
// Virtual Proxy für Lazy Loading
public class LazyCustomerServiceProxy implements CustomerService {
    private final CustomerService realService;
    private final Map<String, CustomerData> loadedCustomers = new HashMap<>();
    
    public CustomerData getCustomer(String customerId) {
        return loadedCustomers.computeIfAbsent(customerId, 
            id -> realService.getCustomer(id)); // Lazy Loading
    }
}

// Caching Proxy für Performance
@Component
public class CachedCustomerServiceProxy implements CustomerService {
    private final Cache<String, CustomerData> customerCache;
    
    public CustomerData getCustomer(String customerId) {
        return customerCache.get(customerId, id -> {
            return realService.getCustomer(id); // Cache Miss
        });
    }
}
```

**Note:** Cache-Hit-Rate von 99.5%+ ist realistisch = 2000x Performance-Verbesserung. Security Proxy kann Authorization und Audit-Logging transparent implementieren.

---

# Flyweight Pattern

## Slide 17: Was ist hier schlecht?

```java
// REDUNDANT: Gleiche Tarif-Daten millionenfach dupliziert
public class CustomerData {
    private String customerId;          // INDIVIDUELL
    private String name;                // INDIVIDUELL
    
    // Nur ~50 verschiedene Werte für Millionen Kunden!
    private String tariffPlanName;      // "MagentaMobil L" - 1M× dupliziert!
    private String tariffDescription;   // Beschreibung - 1M× dupliziert!
    private BigDecimal tariffPrice;     // 49.99 - 1M× dupliziert!
    private Set<String> tariffFeatures; // Feature-Liste - 1M× dupliziert!
}

// Memory-Verschwendung: 500MB für identische Strings!
```

**Note:** Mit 50+ Millionen Kunden entstehen massive Datenmengen. Redundante Tarif-Beschreibungen werden millionenfach dupliziert = Gigabytes verschwendeter Speicher.

---

## Slide 18: Code Smells identifiziert

• **Memory-Explosion**: 50 verschiedene Tarife × 1M Kunden = 50M redundante Kopien
• **Gigabyte-Verschwendung**: Identische Strings millionenfach im Memory
• **Skalierungs-Problem**: Memory-Verbrauch explodiert mit Kundenzahl
• **Cloud-Kosten-Explosion**: Ineffiziente Memory-Nutzung = hohe Infrastruktur-Kosten
• **Cache-Ineffizienz**: Redundante Daten verdrängen wichtige Daten aus Caches
• **GC-Pressure**: Garbage Collector überlastet durch redundante Objekte
• **Network-Overhead**: Redundante Daten werden über Netzwerk übertragen

**Note:** Mathematisches Dilemma: 50 Millionen Kunden × 2KB = 100GB RAM nur für Customer-Objekte. Redundante Daten vervielfachen den Speicher-Bedarf exponentiell.

---

## Slide 19: Lösung: Flyweight Pattern

• **Intrinsic vs. Extrinsic State**: Geteilte von individuellen Daten trennen  
• **Memory-Sharing**: Identische Objekte nur einmal im Memory speichern
• **Factory-Management**: Zentrale Verwaltung garantiert Singleton-Eigenschaften
• **Immutable Flyweights**: Thread-Safety durch Unveränderlichkeit
• **Context Pattern**: Individuelle Daten als Parameter-Objekte
• **70%+ Memory-Ersparnis**: Dramatische Reduktion des Speicherverbrauchs
• **Lineare Skalierung**: Memory-Verbrauch wächst linear mit Kundenzahl

**Note:** Flyweight Pattern trennt "geteilte" von "individuellen" Daten. Objekte mit identischen Properties werden nur einmal gespeichert.

---

## Slide 20: Implementierung

```java
// Flyweight: Nur intrinsic (geteilte) Properties
public class TariffPlanFlyweight {
    private final String planName;          // Intrinsic - geteilt
    private final String description;       // Intrinsic - geteilt  
    private final BigDecimal basePrice;     // Intrinsic - geteilt
    private final Set<String> features;     // Intrinsic - geteilt
    
    // Business-Operation mit extrinsic context
    public CustomerBill calculateBill(CustomerContext context) {
        BigDecimal finalPrice = basePrice;
        if (context.hasLoyaltyDiscount()) {
            finalPrice = finalPrice.multiply(BigDecimal.valueOf(0.9));
        }
        return new CustomerBill(context.getCustomerId(), planName, finalPrice);
    }
}

// Factory für Flyweight-Management
@Component
public class TariffPlanFlyweightFactory {
    private final Map<String, TariffPlanFlyweight> flyweights = new ConcurrentHashMap<>();
    
    public TariffPlanFlyweight getTariffPlan(String planName) {
        return flyweights.computeIfAbsent(planName, this::createTariffPlan);
    }
}
```

**Note:** Messbare Ergebnisse: 77% Memory-Ersparnis! Von 103GB auf 24GB RAM-Verbrauch. Nur 50 Flyweight-Objekte statt 50 Millionen redundante Tarif-Kopien.

---

# Pattern-Integration & Enterprise-Readiness

## Slide 21: Pattern-Synergien

• **Decorator + Strategy**: Optionale, austauschbare Algorithmen kombinieren
• **Composite + Visitor**: Performance-optimierte Hierarchie-Traversierung  
• **Facade + Circuit Breaker**: Resiliente Service-Orchestrierung
• **Proxy + Flyweight**: Memory-effiziente Performance-Optimierung
• **Bridge + Factory**: Dynamische Provider-Auswahl mit Abstraktion
• **Template Method Integration**: Konsistente Flows über alle Patterns
• **Event-Driven Enhancement**: Eventual Consistency für komplexe Systeme

**Note:** Die Patterns ergänzen sich perfekt: Komplexe Business-Domänen werden durch Pattern-Kombination beherrschbar. Factory + Decorator lösen gemeinsam das Problem der flexiblen Objektzusammensetzung.

---

## Slide 22: Production-Ready Considerations

• **Distributed Caching**: Redis für Multi-Instance Cache-Konsistenz
• **Circuit Breaker**: Resilience bei Provider-Integration
• **Health Monitoring**: Proaktive Performance- und Verfügbarkeits-Überwachung
• **Cache Warming**: Predictive Loading für kritische Daten
• **Graceful Degradation**: Fallback-Strategien für Provider-Ausfälle
• **Memory Monitoring**: JVM-Metriken und Alerting für Memory-Patterns
• **Event Sourcing**: Audit-Trail und System-Konsistenz

**Note:** Enterprise-Tauglichkeit erfordert mehr als Design Patterns: Monitoring, Resilience, Distributed Systems Patterns und observability sind kritisch für Production-Einsatz.

---

## Zusammenfassung: Block 2 Erkenntnisse

✅ **Strukturelle Probleme elegant lösen**: Patterns adressieren fundamentale Kompositions-Herausforderungen

✅ **Performance durch Design**: Memory-Optimierung und Latenz-Reduktion als architektonische Entscheidung

✅ **Enterprise-Skalierung**: Patterns skalieren mit bewusster Implementierung bis zu 50M+ Kunden  

✅ **Microservice-Integration**: Loose Coupling und Provider-Abstraktion für resiliente Systeme

✅ **Pattern-Kombination**: Synergien zwischen Patterns multiplizieren architektonischen Wert

✅ **Production-Readiness**: Mit Monitoring, Caching und Resilience enterprise-tauglich

**Note:** Block 2 zeigt: Structural Patterns sind nicht nur akademische Konzepte, sondern lösen reale Enterprise-Performance und -Wartungsprobleme. Die Kombination mehrerer Patterns ist oft mächtiger als einzelne Pattern-Anwendung.