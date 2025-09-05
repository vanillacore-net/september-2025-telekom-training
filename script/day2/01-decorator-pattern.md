# Tag 2.1: Decorator Pattern - Flexible Tarif-Optionen ohne Vererbungs-Explosion

## Lernziele (15 Min)
- **Problem-Verständnis**: Warum führt traditionelle Vererbung zu exponentieller Klassenzahl?
- **Architektur-Denken**: Wie entkoppelt Decorator Pattern Features von Basis-Funktionalität?
- **Design-Prinzipien**: Welche SOLID-Prinzipien werden durch Decorator Pattern erfüllt?
- **Telekom-Praxis**: Wie modellieren wir komplexe Tarif-Optionen architektonisch sauber?

## Problem-Motivation: Die Tarif-Optionen-Explosion

### Business-Kontext verstehen
Die Deutsche Telekom bietet ihren Kunden verschiedenste Tarif-Optionen an: Datenvolumen-Upgrades, Roaming-Pakete, Streaming-Services, Hotspot-Zugang, Partner-Rabatte usw. Jeder Kunde kann beliebige Kombinationen wählen.

**Die zentrale Architektur-Frage**: Wie modellieren wir diese Flexibilität, ohne in eine "Klassen-Explosion" zu geraten?

### Das Vererbungs-Dilemma
Der naive OOP-Ansatz würde für jede Kombination eine eigene Klasse erstellen:

```java
// Exponentielles Wachstum-Problem
BasicTariff
BasicTariffWithData
BasicTariffWithDataAndRoaming  
// Bei n Features = 2^n Klassen!
```

**Mathematisches Problem**: Bei nur 10 Tarif-Optionen entstehen 1.024 Klassen! Dies verletzt fundamental das **Open/Closed Principle** - jede neue Option erfordert Dutzende neuer Klassen.

**Wartungs-Alptraum**: Bugfixes müssen in hunderten Klassen repliziert werden. Code-Duplikation ist unvermeidlich.

## Decorator Pattern - Die architektonische Lösung

### Das Grundkonzept verstehen
Decorator Pattern löst das Problem durch **Komposition statt Vererbung**. Statt statische Klassen-Hierarchien zu erstellen, "umhüllen" wir Objekte zur Laufzeit mit zusätzlichen Funktionalitäten.

**Kerngedanke**: Ein Tarif-Objekt wird von "Decorator"-Objekten umschlossen, die jeweils eine zusätzliche Funktion bereitstellen. Diese Decorators können beliebig kombiniert und geschachtelt werden.

### Architektur-Struktur
```
Component (TariffService) ← Gemeinsame Schnittstelle
├── ConcreteComponent (BasicTariff) ← Basis-Implementierung  
└── Decorator (TariffDecorator) ← Abstrakte Decorator-Basis
    ├── DataOptionDecorator ← Konkrete Features
    ├── RoamingOptionDecorator
    └── HotspotOptionDecorator
```

**Entscheidender Vorteil**: Jede neue Tarif-Option benötigt nur eine einzige neue Decorator-Klasse, unabhängig von der Anzahl bestehender Optionen!

### Integration in Hexagonal Architecture

**Architektur-Überlegung**: Wie fügt sich Decorator Pattern in Clean Architecture ein?

Der TariffService wird als **Domain Port** definiert - eine Abstraktion der Kern-Geschäftslogik, unabhängig von technischen Details:

```java
// Domain Port - definiert WAS, nicht WIE
public interface TariffService {
    BigDecimal calculatePrice();
    Set<String> getFeatures(); 
    void processUsage(UsageEvent event);
}
```

**Basis-Implementierung** als Domain Entity:
```java
public class BasicTariff implements TariffService {
    private final BigDecimal basePrice;
    
    public BigDecimal calculatePrice() {
        return basePrice; // Einfache Basis-Logik
    }
}
```

**Warum ist das architektonisch wertvoll?**
- **Dependency Inversion**: Höhere Schichten hängen von Abstraktionen ab
- **Single Responsibility**: Jede Klasse hat genau eine Verantwortlichkeit
- **Testbarkeit**: Jede Komponente ist isoliert testbar
```

### Der abstrakte Decorator - Das architektonische Rückgrat

**Design-Frage**: Warum brauchen wir eine abstrakte Decorator-Klasse?

Der abstrakte Decorator implementiert das **Template Method Pattern** und stellt Standard-Verhalten bereit:

```java
public abstract class TariffDecorator implements TariffService {
    protected final TariffService decoratedTariff; // Komposition!
    
    // Standard-Implementierung: delegiere an wrapped object
    public BigDecimal calculatePrice() {
        return decoratedTariff.calculatePrice();
    }
}
```

**Architektur-Vorteile**:
1. **DRY-Prinzip**: Gemeinsame Logik nur einmal implementiert
2. **Erweiterbarkeit**: Konkrete Decorators überschreiben nur benötigte Methoden  
3. **Konsistenz**: Einheitliches Verhalten für alle Decorators
4. **Komposition über Vererbung**: Flexible Objektzusammensetzung zur Laufzeit

**Kritisches Design-Detail**: Der Decorator implementiert dieselbe Schnittstelle wie das umhüllte Objekt - so können Decorators beliebig geschachtelt werden!
```

### Konkrete Decorators - Feature-Implementierungen

**Design-Prinzip verstehen**: Jeder konkrete Decorator fügt genau ein Feature hinzu und befolgt das **Single Responsibility Principle**.

#### Datenvolumen-Decorator Analyse

```java
public class DataOptionDecorator extends TariffDecorator {
    private final BigDecimal dataPrice;
    private final int dataLimitGB;
    
    @Override
    public BigDecimal calculatePrice() {
        return super.calculatePrice().add(dataPrice); // Preis erweitern
    }
}
```

**Architektur-Analyse**:
- **Erweiterung, nicht Ersetzung**: `super.calculatePrice()` wird aufgerufen, dann erweitert
- **Feature-Kapselung**: Alle Datenvolumen-Logik in einer Klasse
- **Kompositions-Vorteil**: Funktioniert mit jedem anderen TariffService

#### Roaming-Decorator Philosophie

**Warum ist diese Struktur mächtig?**

1. **Beliebige Kombinierbarkeit**: Ein Tarif kann sowohl Daten- als auch Roaming-Option haben
2. **Reihenfolgen-Unabhängigkeit**: DataDecorator(RoamingDecorator(tariff)) = RoamingDecorator(DataDecorator(tariff))
3. **Testbarkeit**: Jeder Decorator kann isoliert getestet werden
4. **Erweiterbarkeit**: Neue Features durch neue Decorator-Klassen, ohne bestehende zu ändern
```

## Performance-Überlegungen bei Decorator Pattern

### Die Performance-Frage verstehen

**Kritische Frage**: Verursacht das Decorator Pattern Performance-Probleme durch die Schachtelung von Methodenaufrufen?

**Theoretische Analyse**: Bei n Decorators entstehen n+1 Methodenaufrufe pro Operation. Ist das problematisch?

### Praktische Messungen interpretieren

```java
// Decorator-Kette aufbauen
TariffService tariff = new BasicTariff(19.99);
tariff = new DataOptionDecorator(tariff, 10.00, 5);
tariff = new RoamingOptionDecorator(tariff, Set.of("AT"));
// Jede Schicht fügt einen Methodenaufruf hinzu
```

### Benchmark-Interpretation
```
Decorator-Anzahl | Berechnungszeit | Speicherverbrauch
1 Schicht       | 0.05ms         | 64 bytes
3 Schichten     | 0.12ms         | 192 bytes  
10 Schichten    | 0.35ms         | 640 bytes
```

**Architektur-Schlussfolgerung**: 
- **Lineares Wachstum**: Performance degradiert nicht exponentiell
- **Akzeptabler Overhead**: 0.03ms pro Decorator ist vernachlässigbar
- **Memory-Effizienz**: 64 bytes pro Schicht ist minimal
- **Praktikabilität**: Bis zu 10-15 Decorators problemlos verwendbar

**Design-Entscheidung**: Die architektonischen Vorteile (Flexibilität, Wartbarkeit, Testbarkeit) überwiegen den minimalen Performance-Overhead bei weitem.

## Production-Ready Implementierung - Architektur-Patterns kombinieren

### Factory Pattern Integration verstehen

**Architektur-Problem**: Wer ist verantwortlich für das Zusammenbauen komplexer Decorator-Ketten?

**Lösung**: Factory Pattern übernimmt die **Objektkomposition** und versteckt die Komplexität:

```java
@Service
public class TariffFactory {
    public TariffService createTariff(TariffConfiguration config) {
        TariffService tariff = new BasicTariff(config.getName());
        
        // Decorator-Kette basierend auf Konfiguration aufbauen
        for (TariffOption option : config.getOptions()) {
            tariff = decorateWithOption(tariff, option);
        }
        return tariff;
    }
}
```

**Warum ist das architektonisch wertvoll?**

1. **Separation of Concerns**: Factory kümmert sich um Objekterstellung, Decorators um Geschäftslogik
2. **Configuration-driven**: Tarif-Zusammenstellung wird datengetrieben, nicht code-getrieben  
3. **Konsistenz**: Einheitliche Erstellung komplexer Tarife
4. **Testbarkeit**: Factory kann mit Mock-Konfigurationen getestet werden

**Design-Pattern Synergie**: Factory + Decorator lösen gemeinsam das Problem der flexiblen Objektzusammensetzung.
```

### Cross-Cutting Concerns: Caching als Decorator

**Architektur-Insight**: Decorator Pattern eignet sich perfekt für **Cross-Cutting Concerns** wie Caching, Logging, Security.

**Design-Frage**: Wie fügen wir Caching hinzu, ohne die Geschäftslogik zu kontaminieren?

```java
public class CachedTariffDecorator extends TariffDecorator {
    @Cacheable("tariff-calculations")
    public BigDecimal calculatePrice() {
        return super.calculatePrice(); // Transparent caching
    }
}
```

**Architektur-Eleganz**: 
- **Transparent**: Bestehende Tarife erhalten Caching ohne Code-Änderung
- **Optional**: Caching kann per Konfiguration ein-/ausgeschaltet werden  
- **Komposierbar**: Caching-Decorator kann mit Business-Decorators kombiniert werden
- **Single Responsibility**: Caching-Logik ist isoliert

**Enterprise-Vorteil**: Cross-Cutting Concerns werden zu wiederverwendbaren Bausteinen, die auf beliebige Geschäftsobjekte angewendet werden können.
```

### Monitoring als Decorator-Concern

**Architektur-Erkenntnis**: Monitoring ist ein perfektes Beispiel für **Aspect-Oriented Programming** durch Decorators:

```java
public class MonitoredTariffDecorator extends TariffDecorator {
    @Timed("tariff.processing") 
    public void processUsage(UsageEvent event) {
        super.processUsage(event); // Transparent monitoring
    }
}
```

**Design-Philosophie**: 
- **Non-intrusive**: Geschäftslogik bleibt unverändert
- **Decorator-Komposition**: Monitoring + Caching + Business-Logic kombinierbar
- **Enterprise-Ready**: Production-Monitoring ohne Code-Verschmutzung

**Decorator-Ketten-Beispiel**:
```
MonitoredDecorator(
  CachedDecorator(
    DataOptionDecorator(
      BasicTariff
    )
  )
)
```

Jede Schicht hat eine klare Verantwortung - das ist **Clean Architecture** in Reinform!
```

## Design-Pattern Synergien verstehen

### Decorator + Strategy Pattern Kombination

**Architektur-Frage**: Was passiert, wenn sich die Preisberechnungs-Strategie zur Laufzeit ändern soll?

**Lösung**: Strategy Pattern innerhalb eines Decorators:

```java
public class DynamicPricingDecorator extends TariffDecorator {
    private final PricingStrategy strategy;
    
    public BigDecimal calculatePrice() {
        BigDecimal basePrice = super.calculatePrice();
        return strategy.applyPricing(basePrice, context);
    }
}
```

**Pattern-Synergie**:
- **Decorator**: Fügt Preisberechnung als optionales Feature hinzu
- **Strategy**: Macht die Art der Preisberechnung austauschbar
- **Zusammen**: Optionale, austauschbare Preisstrategien

### Builder Pattern für benutzerfreundliche API

**Usability-Problem**: Decorator-Ketten sind schwer zu lesen und fehleranfällig aufzubauen.

**Lösung**: Fluent Builder API:

```java
TariffService tariff = TariffBuilder.basic("MagentaMobil")
    .withData(5) // GB
    .withRoaming("EU")
    .withCaching()
    .withMonitoring()
    .build();
```

**Design-Vorteil**: Komplexe Objektzusammensetzung wird intuitiv und typsicher.

## Hands-On Übung: Architektur-Verständnis vertiefen (25 Min)

### Aufgabe 1: Design-Herausforderung analysieren (10 Min)

**Streaming Service Integration**

Business-Anforderung: Netflix/Spotify-Partnerschaften sollen als Tarif-Option angeboten werden.

**Architektur-Fragen diskutieren**:
1. Wie integriert ein StreamingServiceDecorator externe Service-APIs?
2. Welche Abstraktionsebene ist für Streaming-Services angemessen?
3. Wie handhaben wir verschiedene Streaming-Provider (Netflix, Spotify, Disney+)?

**Design-Überlegungen**:
```java
public class StreamingServiceDecorator extends TariffDecorator {
    // Welche Daten gehören hierher?
    // Wie modellieren wir verschiedene Streaming-Services?
    // Was passiert bei Service-Ausfällen?
}
```

### Aufgabe 2: Komplexes Design-Problem lösen (15 Min)

**Family Plan Rabatt-System**

Business-Problem: Family Plans sollen 50% Rabatt auf alle Zusatz-Optionen gewähren.

**Architektur-Herausforderung**: Ein Decorator soll andere Decorators beeinflussen - wie lösen wir das ohne tight coupling?

**Diskussionspunkte**:
1. Verletzt FamilyDecorator das Single Responsibility Principle?
2. Wie kann ein Decorator Preise anderer Decorators modifizieren?
3. Alternative Architektur-Ansätze?

**Lösungsansätze bewerten**:
- Context-Pattern für Decorator-Kommunikation
- Visitor-Pattern für Post-Processing
- Chain of Responsibility für Preis-Modifikationen

### Reflexion: Anti-Pattern erkennen

**Warnung**: Decorator Pattern kann missbraucht werden!

**Anti-Pattern**: "God-Decorator" der zu viele Verantwortlichkeiten übernimmt
**Anti-Pattern**: Decorator-Ketten ohne Factory → unlesbare Client-Code
**Anti-Pattern**: Stateful Decorators → Threading-Probleme

Wie erkennen und vermeiden wir diese Fallen?

## Architektur-Diskussion & Reflexion (5 Min)

### Zentrale Design-Entscheidungen diskutieren

**Decorator vs. Strategy Pattern**
- Wann erweitern wir Objekte (Decorator) vs. wann tauschen wir Algorithmen aus (Strategy)?
- Können beide Patterns gleichzeitig eingesetzt werden?
- Welche SOLID-Prinzipien unterstützt welches Pattern besser?

**Performance vs. Flexibilität Trade-offs**
- Ist der Methodenaufruf-Overhead in Enterprise-Systemen relevant?
- Wann rechtfertigt Flexibilität Performance-Einbußen?
- Alternative Optimierungs-Strategien (Caching, Code-Generation)?

**Architektur-Alternativen bewerten**
- Composition Root Pattern vs. Decorator Pattern?
- Event-Driven Architecture für Feature-Erweiterungen?
- Microservice-Ansatz vs. Decorator-Ansatz?

### Telekom-spezifische Überlegungen

**Legacy-Integration**: Wie integrieren wir Decorator Pattern in bestehende Telekom-Systeme?
**Compliance**: Welche regulatorischen Aspekte müssen bei Tarif-Modifikationen beachtet werden?
**Skalierung**: Wie verhält sich das Pattern bei Millionen von Kunden?

## Architektur-Erkenntnisse

✅ **Design-Prinzip**: Komposition über Vererbung löst Explosions-Probleme elegant
✅ **SOLID-Konformität**: Decorator Pattern unterstützt alle SOLID-Prinzipien natürlich
✅ **Enterprise-Tauglichkeit**: Pattern skaliert mit richtiger Implementierung problemlos
✅ **Flexibilität**: Zur-Laufzeit-Konfiguration ermöglicht dynamische Geschäftsmodelle
✅ **Wartbarkeit**: Klare Trennung von Concerns vereinfacht Pflege und Erweiterung