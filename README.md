# Software-Architektur Workshop: Design Patterns im Fokus

## Grundlagen-Einführung + 4-Tage Training - Von Architektur-Prinzipien zu Design Patterns

### Workshop-Übersicht

**Zielgruppe**: Software-Entwickler und Architekten bei Telekom  
**Dauer**: Einführung (90-120 Min) + 4 Tage (September 2025)  
**Schwerpunkt**: Fachlichkeit vor Technik, Clean Code, Design Patterns mit Diskussionen und Live-Demos  
**Methodik**: Wissensvermittlung mit 70% Erklärung, 30% Code-Beispiele

### Verfügbare Materialien

#### Schulungsunterlagen (Vollständig)

**Einführung: Grundlagen Software-Architektur (90-120 Min)**
- `script/intro/01-was-ist-software-architektur.md` - Interaktive Einführung
- `script/intro/02-clean-code-grundlagen.md` - Qualitätsprinzipien
- `script/intro/03-fachlichkeit-vor-technik.md` - **KERN-MESSAGE: Domain-First!**
- `script/intro/04-design-patterns-motivation.md` - Warum Patterns?
- `script/intro/05-refactoring-philosophie.md` - Kontinuierliche Verbesserung
- `script/intro/workshop-erwartungen.md` - 4-Tage Roadmap

**Tag 1: Creational Patterns und Grundlagen-Architektur**
- `script/day1/01-code-analyse-factory-method.md`
- `script/day1/02-abstract-factory-layered-architecture.md`
- `script/day1/03-builder-repository-pattern.md`
- `script/day1/05-prototype-pattern-configuration-cloning.md`
- `script/day1/06-singleton-adapter-clean-architecture.md`

**Tag 2: Structural Patterns und Hexagonal Architecture**
- `script/day2/01-decorator-pattern.md`
- `script/day2/02-composite-facade.md`
- `script/day2/03-proxy-flyweight.md`
- `script/day2/04-bridge-microservices.md`

**Tag 3: Behavioral Patterns und Event-Driven Grundlagen**
- `script/day3/01-strategy-pattern.md`
- `script/day3/02-template-method-observer.md`
- `script/day3/03-command-pattern.md`
- `script/day3/04-state-chain-of-responsibility.md`

**Tag 4: Advanced Patterns und Architektur-Integration**
- `script/day4/01-mediator-pattern.md`
- `script/day4/02-iterator-visitor.md`
- `script/day4/03-memento-interpreter.md`
- `script/day4/04-pattern-integration.md`

#### Referenz-Dokumentation (Teilweise vollständig)

**Refactoring & Code-Qualität** (Vollständig)
- `script/references/code-smell-katalog.md` - 18 Code-Smells mit Telekom-Beispielen
- `script/references/refactoring-techniken.md` - Schritt-für-Schritt Anleitungen
- `script/references/refactoring-checkliste.md` - Praktische Workshop-Checkliste

**Clean Code & SOLID** (Vollständig)
- `script/references/clean-code-prinzipien.md` - DRY, KISS, YAGNI Prinzipien
- `script/references/solid-principles.md` - Alle 5 SOLID Prinzipien erklärt
- `script/references/code-quality-checkliste.md` - Review-Checkliste

### Projekt-Organisation

#### Verzeichnisstruktur

```
.
├── README.md                   # Diese Datei
├── agenda.md                   # Detaillierte Workshop-Agenda
│
├── script/                     # Schulungsunterlagen
│   ├── intro/                  # Grundlagen-Einführung (6 Module, 90-120 Min)
│   ├── day1/                   # Tag 1: Creational Patterns (5 Module)
│   ├── day2/                   # Tag 2: Structural Patterns (4 Module)
│   ├── day3/                   # Tag 3: Behavioral Patterns (4 Module)
│   ├── day4/                   # Tag 4: Advanced Patterns (4 Module)
│   └── references/             # Referenz-Dokumentation (13 Dokumente)
│
└── examples/                   # Code-Beispiele und Übungen
    ├── day1-examples/          # Creational Patterns Implementierungen
    ├── day2-examples/          # Structural Patterns Implementierungen
    ├── day3-examples/          # Behavioral Patterns Implementierungen
    ├── day4-examples/          # Advanced Patterns Implementierungen
    └── exercises/              # Hands-On Übungsaufgaben
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

**Telekom-Kontext durchgängig**:
- Customer Service Systeme
- Tarif-Verwaltung
- Service-Provisioning
- Device Management
- Network Operations

### Technische Dokumentation

**Setup und Installation**:
- [`examples/README-SETUP.md`](examples/README-SETUP.md) - Projekt-Setup und Maven-Konfiguration
- [`examples/README-DOCKER.md`](examples/README-DOCKER.md) - Docker-Umgebung für Windows/Mac/Linux

**Übungsaufgaben**:
- [`examples/exercises/day1-exercises.md`](examples/exercises/day1-exercises.md) - Tag 1: Creational Patterns
- [`examples/exercises/day2-exercises.md`](examples/exercises/day2-exercises.md) - Tag 2: Structural Patterns
- [`examples/exercises/day3-exercises.md`](examples/exercises/day3-exercises.md) - Tag 3: Behavioral Patterns
- [`examples/exercises/day4-exercises.md`](examples/exercises/day4-exercises.md) - Tag 4: Advanced Patterns

## Lizenz

Die Trainingsmaterialien unterliegen dem Copyright und sind ausschließlich für Schulungsteilnehmer bestimmt.