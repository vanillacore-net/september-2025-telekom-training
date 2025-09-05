# Tag 1 - Creational Patterns: Code-Beispiele

Diese Tag 1 Code-Beispiele demonstrieren die Transformation von problematischen Code-Patterns in saubere Creational Design Patterns mit realitätsnahen Telekom-Szenarien.

## Übersicht der Beispiele

### 1. Customer Service: God Class → Factory Method Pattern

**Problem:** 150+ Zeilen God Class mit Giant Switch Statements und vermischten Verantwortlichkeiten.

**Lösung:** Factory Method Pattern mit spezialisierten Request Handlers.

**Verbesserungen:**
- Single Responsibility Principle
- Open/Closed Principle
- Template Method Pattern
- Testbare Komponenten

**Dateien:**
- `src/main/java/.../customerservice/initial/CustomerService.java` - God Class Anti-Pattern
- `src/main/java/.../customerservice/fixed/` - Factory Method Implementation
- `src/test/java/.../customerservice/CustomerServiceTest.java` - Vergleichende Tests

### 2. Service Families: Hard-coded Creation → Abstract Factory Pattern

**Problem:** Hard-coded Service Creation mit Giant Switch Statements und fehlender Service-Kohärenz.

**Lösung:** Abstract Factory Pattern für kohärente Service-Familien (DSL/Mobile/Fiber).

**Verbesserungen:**
- Service Family Cohesion
- Keine Code Duplication
- Erweiterbar ohne Breaking Changes
- Template Method für Bundle Creation

**Dateien:**
- `src/main/java/.../servicefamilies/initial/TelekomServiceProvider.java` - Hard-coded Creation
- `src/main/java/.../servicefamilies/fixed/` - Abstract Factory Implementation
- `src/test/java/.../servicefamilies/ServiceFamiliesTest.java` - Family Cohesion Tests

### 3. Contract Builder: Long Constructor → Builder Pattern

**Problem:** Constructor mit 27 Parametern, Telescoping Constructor Pattern, unlesbarer Client Code.

**Lösung:** Builder Pattern mit Fluent Interface und step-by-step Validation.

**Verbesserungen:**
- Self-documenting Code
- Flexible Parameter-Reihenfolge
- Immutable Objects
- Domain-specific Language

**Dateien:**
- `src/main/java/.../contractbuilder/initial/TelekomContract.java` - Long Constructor Anti-Pattern
- `src/main/java/.../contractbuilder/fixed/TelekomContract.java` - Builder Pattern Implementation
- `src/test/java/.../contractbuilder/ContractBuilderTest.java` - Builder vs Constructor Tests

### 4. Legacy Integration: Direct Coupling → Adapter Pattern

**Problem:** Direkte Legacy System Integration mit Type Conversions, mixed APIs und durchsickernden Legacy Concerns.

**Lösung:** Adapter Pattern als Anti-Corruption Layer mit modernem Interface.

**Verbesserungen:**
- Anti-Corruption Layer
- Centralized Type Conversions
- Testability durch Interface
- Legacy System Isolation

**Dateien:**
- `src/main/java/.../legacyadapter/initial/` - Direct Legacy Integration Anti-Pattern
- `src/main/java/.../legacyadapter/fixed/` - Adapter Pattern Implementation
- `src/test/java/.../legacyadapter/LegacyAdapterSimpleTest.java` - Architectural Tests

## Tests ausführen

```bash
# Alle Tests ausführen
./docker-build.sh test

# Nur kompilieren
./docker-build.sh compile

# Clean build
./docker-build.sh clean
```

## Test-Ergebnisse

```
Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
✅ All tests passed!
```

## Trainer-Hinweise

Jedes Beispiel enthält ausführliche Trainer-Notes in `trainer-notes/`:

1. **`01-customer-service-refactoring.md`** - God Class → Factory Method Transformation
2. **`02-service-families-abstract-factory.md`** - Service Cohesion mit Abstract Factory
3. **`03-contract-builder-pattern.md`** - Complex Object Construction mit Builder
4. **`04-legacy-adapter-pattern.md`** - Legacy Integration mit Anti-Corruption Layer

## Code-Smells vs. Patterns

### Initial Versions (Anti-Patterns)
- **God Class**: Alles in einer 150+ Zeilen Klasse
- **Long Constructor**: 27 Parameter Constructor
- **Giant Switch Statements**: Verletzt Open/Closed Principle
- **Hard-coded Logic**: Keine Flexibilität oder Konfigurierbarkeit
- **Tight Coupling**: Direkte Dependencies zu Legacy Systemen
- **Mixed Concerns**: Business Logic vermischt mit Technical Concerns

### Fixed Versions (Design Patterns)
- **Factory Method**: Objekterzeugung an Subklassen delegiert
- **Abstract Factory**: Kohärente Produkt-Familien
- **Builder Pattern**: Flexible, lesbare Objektkonstruktion
- **Adapter Pattern**: Legacy System Isolation mit Anti-Corruption Layer

## Architektur-Prinzipien

Alle Fixed Versions implementieren SOLID Principles:

- **S**ingle Responsibility - jede Klasse hat eine klare Aufgabe
- **O**pen/Closed - erweiterbar ohne Änderung bestehender Klassen
- **L**iskov Substitution - austauschbare Implementierungen
- **I**nterface Segregation - fokussierte Interfaces
- **D**ependency Inversion - Abhängigkeiten zu Abstraktionen

## Didaktische Ziele

### Pattern Recognition
- Erkennen von Code-Smells in Legacy Code
- Verstehen wann welches Pattern angemessen ist
- Refactoring-Strategien für schrittweise Verbesserung

### Practical Application
- Realitätsnahe Telekom Business-Szenarien
- Nachvollziehbare Transformationen
- Testbare und erweiterbare Lösungen

### Architecture Understanding  
- Separation of Concerns
- Loose Coupling vs. Tight Coupling
- Anti-Corruption Layer Konzepte
- Clean Architecture Prinzipien

## Verwendung im Training

1. **Code Review**: Initial Versions analysieren und Code-Smells identifizieren
2. **Pattern Discussion**: Passende Patterns auswählen und begründen  
3. **Implementation**: Schrittweise Transformation durchführen
4. **Testing**: Vergleichende Tests schreiben und ausführen
5. **Architecture Review**: Architektur-Verbesserungen diskutieren

Jedes Beispiel ist vollständig lauffähig und demonstriert sowohl die Probleme als auch die Lösungen in realitätsnahen Telekom-Szenarien.