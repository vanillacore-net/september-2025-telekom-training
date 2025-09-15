# Software-Architektur Workshop: Design Patterns im Fokus

## Grundlagen-Einführung + 4-Tage Training - Von Architektur-Prinzipien zu Design Patterns

### Workshop-Übersicht

**Zielgruppe**: Software-Entwickler und Architekten
**Dauer**: Einführung (90-120 Min) + 4 Tage (September 2025)
**Schwerpunkt**: Fachlichkeit vor Technik, Clean Code, Design Patterns mit Diskussionen und Live-Demos
**Methodik**: Wissensvermittlung mit 70% Erklärung, 30% Code-Beispiele

### Verfügbare Materialien

#### Schulungsunterlagen (Vollständig)

**Einführung: Grundlagen Software-Architektur (90-120 Min)**
- [`script/intro/01-was-ist-software-architektur.md`](script/intro/01-was-ist-software-architektur.md) - Interaktive Einführung
- [`script/intro/02-clean-code-grundlagen.md`](script/intro/02-clean-code-grundlagen.md) - Qualitätsprinzipien
- [`script/intro/03-fachlichkeit-vor-technik.md`](script/intro/03-fachlichkeit-vor-technik.md) - **KERN-MESSAGE: Domain-First!**
- [`script/intro/04-design-patterns-motivation.md`](script/intro/04-design-patterns-motivation.md) - Warum Patterns?
- [`script/intro/05-refactoring-philosophie.md`](script/intro/05-refactoring-philosophie.md) - Kontinuierliche Verbesserung
- [`script/intro/workshop-erwartungen.md`](script/intro/workshop-erwartungen.md) - 4-Tage Roadmap

**Tag 1: Creational Patterns und Grundlagen-Architektur**
- [`script/day1/01-code-analyse-factory-method.md`](script/day1/01-code-analyse-factory-method.md)
- [`script/day1/02-abstract-factory-layered-architecture.md`](script/day1/02-abstract-factory-layered-architecture.md)
- [`script/day1/03-builder-repository-pattern.md`](script/day1/03-builder-repository-pattern.md)
- [`script/day1/05-prototype-pattern-configuration-cloning.md`](script/day1/05-prototype-pattern-configuration-cloning.md)
- [`script/day1/06-singleton-adapter-clean-architecture.md`](script/day1/06-singleton-adapter-clean-architecture.md)

**Tag 2: Structural Patterns und Hexagonal Architecture**
- [`script/day2/01-decorator-pattern.md`](script/day2/01-decorator-pattern.md)
- [`script/day2/02-composite-facade.md`](script/day2/02-composite-facade.md)
- [`script/day2/03-proxy-flyweight.md`](script/day2/03-proxy-flyweight.md)
- [`script/day2/04-bridge-microservices.md`](script/day2/04-bridge-microservices.md)

**Tag 3: Behavioral Patterns und Event-Driven Grundlagen**
- [`script/day3/01-strategy-pattern.md`](script/day3/01-strategy-pattern.md)
- [`script/day3/02-template-method-observer.md`](script/day3/02-template-method-observer.md)
- [`script/day3/03-command-pattern.md`](script/day3/03-command-pattern.md)
- [`script/day3/04-state-chain-of-responsibility.md`](script/day3/04-state-chain-of-responsibility.md)

**Tag 4: Advanced Patterns und Architektur-Integration**
- [`script/day4/01-mediator-pattern.md`](script/day4/01-mediator-pattern.md)
- [`script/day4/02-iterator-visitor.md`](script/day4/02-iterator-visitor.md)
- [`script/day4/03-memento-interpreter.md`](script/day4/03-memento-interpreter.md)
- [`script/day4/04-pattern-integration.md`](script/day4/04-pattern-integration.md)

#### Referenz-Dokumentation (Vollständig)

**Refactoring & Code-Qualität** (Vollständig)
- [`script/references/code-smell-katalog.md`](script/references/code-smell-katalog.md) - 18 Code-Smells mit Praxisbeispielen
- [`script/references/refactoring-techniken.md`](script/references/refactoring-techniken.md) - Schritt-für-Schritt Anleitungen
- [`script/references/refactoring-checkliste.md`](script/references/refactoring-checkliste.md) - Praktische Workshop-Checkliste

**Clean Code & SOLID** (Vollständig)
- [`script/references/clean-code-prinzipien.md`](script/references/clean-code-prinzipien.md) - DRY, KISS, YAGNI Prinzipien
- [`script/references/solid-principles.md`](script/references/solid-principles.md) - Alle 5 SOLID Prinzipien erklärt
- [`script/references/code-quality-checkliste.md`](script/references/code-quality-checkliste.md) - Review-Checkliste

**Architektur & Anti-Patterns** (Vollständig)
- [`script/references/architektur-patterns-overview.md`](script/references/architektur-patterns-overview.md) - Architekturmuster-Übersicht
- [`script/references/architektur-evolution.md`](script/references/architektur-evolution.md) - Evolution von Architekturen
- [`script/references/pattern-architektur-mapping.md`](script/references/pattern-architektur-mapping.md) - Pattern zu Architektur Zuordnung
- [`script/references/architektur-decision-matrix.md`](script/references/architektur-decision-matrix.md) - Entscheidungsmatrix
- [`script/references/anti-patterns-katalog.md`](script/references/anti-patterns-katalog.md) - Häufige Anti-Patterns
- [`script/references/anti-pattern-erkennung.md`](script/references/anti-pattern-erkennung.md) - Erkennungsstrategien
- [`script/references/pattern-misuse-guide.md`](script/references/pattern-misuse-guide.md) - Pattern-Missbrauch vermeiden

### Projekt-Organisation

#### Verzeichnisstruktur

```
.
├── README.md                       # Diese Datei - Projektübersicht
├── VERSION                         # Versions-Tracking
│
├── script/                         # Vollständige Schulungsunterlagen
│   ├── intro/                      # Grundlagen-Einführung (6 Module, 90-120 Min)
│   │   ├── 01-was-ist-software-architektur.md
│   │   ├── 02-clean-code-grundlagen.md
│   │   ├── 03-fachlichkeit-vor-technik.md
│   │   ├── 04-design-patterns-motivation.md
│   │   ├── 05-refactoring-philosophie.md
│   │   └── workshop-erwartungen.md
│   ├── day1/                       # Tag 1: Creational Patterns (6 Module)
│   │   ├── 01-code-analyse-factory-method.md
│   │   ├── 02-abstract-factory-layered-architecture.md
│   │   ├── 03-builder-repository-pattern.md
│   │   ├── 05-prototype-pattern-configuration-cloning.md
│   │   └── 06-singleton-adapter-clean-architecture.md
│   ├── day2/                       # Tag 2: Structural Patterns (4 Module)
│   │   ├── 01-decorator-pattern.md
│   │   ├── 02-composite-facade.md
│   │   ├── 03-proxy-flyweight.md
│   │   └── 04-bridge-microservices.md
│   ├── day3/                       # Tag 3: Behavioral Patterns (4 Module)
│   │   ├── 01-strategy-pattern.md
│   │   ├── 02-template-method-observer.md
│   │   ├── 03-command-pattern.md
│   │   └── 04-state-chain-of-responsibility.md
│   ├── day4/                       # Tag 4: Advanced Patterns (4 Module)
│   │   ├── 01-mediator-pattern.md
│   │   ├── 02-iterator-visitor.md
│   │   ├── 03-memento-interpreter.md
│   │   └── 04-pattern-integration.md
│   └── references/                 # Umfangreiche Referenz-Dokumentation
│       ├── architektur-decision-matrix.md
│       ├── architektur-evolution.md
│       ├── architektur-patterns-overview.md
│       ├── anti-pattern-erkennung.md
│       ├── anti-patterns-katalog.md
│       ├── clean-code-prinzipien.md
│       ├── code-quality-checkliste.md
│       ├── code-smell-katalog.md
│       ├── pattern-architektur-mapping.md
│       ├── pattern-misuse-guide.md
│       ├── refactoring-checkliste.md
│       ├── refactoring-techniken.md
│       └── solid-principles.md
│
├── examples/                       # Vollständige Java-Implementierungen
│   ├── day1-examples/              # Creational Patterns
│   │   ├── src/main/java/          # Factory, Abstract Factory, Builder, Prototype, Adapter
│   │   ├── src/test/java/          # Unit-Tests für alle Patterns
│   │   └── trainer-notes/          # Detaillierte Trainer-Anleitungen
│   ├── day2-examples/              # Structural Patterns
│   │   ├── src/main/java/          # Decorator, Composite, Proxy, Bridge Implementierungen
│   │   ├── src/test/java/          # Umfangreiche Test-Suites
│   │   └── trainer-notes/          # Schritt-für-Schritt Anleitungen
│   ├── day3-examples/              # Behavioral Patterns
│   │   ├── src/main/java/          # Strategy, Observer, Command, State, Chain of Responsibility
│   │   └── src/test/java/          # Behavior-Tests und Szenarien
│   ├── day4-examples/              # Advanced & Multi-Language Examples
│   │   ├── CPP/                    # C++ Implementierungen
│   │   ├── GO/                     # Go Implementierungen
│   │   ├── PY/                     # Python Implementierungen
│   │   ├── Rust/                   # Rust Implementierungen
│   │   └── TS/                     # TypeScript Implementierungen
│   ├── anti-patterns/              # Anti-Pattern Beispiele und Refactorings
│   └── exercises/                  # Praktische Übungsaufgaben
│       ├── day1-exercises.md
│       ├── day2-exercises.md
│       ├── day3-exercises.md
│       └── day4-exercises.md
│
├── presentations/                  # Präsentationen und Visualisierungen
│   ├── architectural-patterns-part1.md
│   ├── architectural-patterns-part2.md
│   ├── architectural-patterns-part3.md
│   ├── architectural-patterns-part4.md
│   ├── ai-patterns.md
│   └── anti-patterns-catalog.md
│
├── docs/                          # Technische Dokumentation
│   ├── HEDGEDOC_ACCESS.md
│   ├── HEDGEDOC_CSS_BEST_PRACTICES.md
│   ├── hedgedoc-README.md
│   └── presentation-visual-specifications.md
│
└── best-practices/                # Best Practices und Guidelines
    └── operations/
        └── hedgedoc-import-procedure.md
```

### Didaktisches Konzept

**Grundlagen-Einführung** (90-120 Min):
1. **"Was ist Software-Architektur?"** - Interaktive Teilnehmer-Diskussion
2. **Clean Code Prinzipien** - Qualität als Basis
3. **FACHLICHKEIT VOR TECHNIK** - Domain-First statt Technology-First  
4. **Design Patterns Motivation** - Warum und wann Patterns?
5. **Refactoring Philosophie** - Kontinuierliche Verbesserung
6. **Workshop-Erwartungen** - 4-Tage Roadmap

**Struktur pro Pattern-Modul**:
1. Problem-Beschreibung und Motivation
2. Code-Smell Identifikation
3. Pattern-Struktur und Erklärung
4. Refactoring-Schritte
5. SOLID-Prinzipien Integration
6. Anti-Pattern Vermeidung
7. Diskussionspunkte
8. Praktische Übung (15-25 Min)

**Praxisnaher Kontext durchgängig**:
- Customer Service Systeme
- Tarif-Verwaltung
- Service-Provisioning
- Device Management
- Network Operations
- Payment Processing
- Configuration Management

### Technische Dokumentation

**Setup und Installation**:
- [`examples/README-setuo.md`](examples/README-setuo.md) - Projekt-Setup und Maven-Konfiguration
- [`examples/README-docker.md`](examples/README-docker.md) - Docker-Umgebung für Windows/Mac/Linux

**Übungsaufgaben**:
- [`examples/exercises/day1-exercises.md`](examples/exercises/day1-exercises.md) - Tag 1: Creational Patterns
- [`examples/exercises/day2-exercises.md`](examples/exercises/day2-exercises.md) - Tag 2: Structural Patterns
- [`examples/exercises/day3-exercises.md`](examples/exercises/day3-exercises.md) - Tag 3: Behavioral Patterns
- [`examples/exercises/day4-exercises.md`](examples/exercises/day4-exercises.md) - Tag 4: Advanced Patterns

### Besondere Features

#### Multi-Language Support (Tag 4)
Das Projekt enthält Pattern-Implementierungen in verschiedenen Programmiersprachen:
- **C++**: Moderne C++17/20 Implementierungen
- **Go**: Idiomatische Go-Patterns
- **Python**: Pythonic Design Patterns
- **Rust**: Memory-safe Pattern-Implementierungen
- **TypeScript**: Type-safe JavaScript Patterns

#### Anti-Pattern Katalog
Umfangreiche Sammlung von Anti-Patterns mit:
- Erkennungsmerkmalen
- Refactoring-Strategien
- Praxisbeispielen aus realen Projekten

#### Interaktive Präsentationen
- HedgeDoc-basierte Präsentationen für kollaboratives Lernen
- Architektur-Pattern Visualisierungen
- AI-Pattern Integration für moderne Anwendungen

## Lizenz

Die Trainingsmaterialien unterliegen dem Copyright und sind ausschließlich für Schulungsteilnehmer bestimmt.