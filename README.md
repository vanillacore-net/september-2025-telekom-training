# Software-Architektur Workshop: Design Patterns im Fokus

## 4-Tage Training - Von einfachen Patterns zu komplexen Architekturen

### Workshop-Übersicht

**Zielgruppe**: Software-Entwickler und Architekten bei Telekom  
**Dauer**: 4 Tage (September 2025)  
**Schwerpunkt**: Design Patterns mit ausführlichen Diskussionen, Hands-on, Live-Demos  
**Methodik**: Wissensvermittlung mit 70% Erklärung, 30% Code-Beispiele

### Verfügbare Materialien

#### Schulungsunterlagen (Vollständig)

**Tag 1: Creational Patterns und Grundlagen-Architektur**
- `script/day1/01-code-analyse-factory-method.md`
- `script/day1/02-abstract-factory-layered-architecture.md`
- `script/day1/03-builder-repository-pattern.md`
- `script/day1/04-singleton-adapter-clean-architecture.md`

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
├── CLAUDE.md                   # Projekt-Konfiguration
│
├── script/                     # Schulungsunterlagen
│   ├── day1/                   # Tag 1: Creational Patterns (4 Module)
│   ├── day2/                   # Tag 2: Structural Patterns (4 Module)
│   ├── day3/                   # Tag 3: Behavioral Patterns (4 Module)
│   ├── day4/                   # Tag 4: Advanced Patterns (4 Module)
│   └── references/             # Referenz-Dokumentation (6 Dokumente)
│
├── stories/                    # Projekt-Management
│   ├── EPIC-001-*.md           # Training-Inhalte EPIC
│   ├── EPIC-002-*.md           # Code-Beispiele EPIC
│   └── STORY-*.md              # Einzelne User Stories
│
└── prbs/                       # Projekt-Requirement-Beschreibungen
    ├── ready/                  # Zu bearbeitende PRBs
    └── completed/              # Abgeschlossene PRBs
```

### Didaktisches Konzept

**Struktur pro Modul**:
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

### Status der Unterlagen

**Fertiggestellt**:
- Tag 1-4 Schulungsunterlagen (16 Module)
- Refactoring-Referenz (3 Dokumente)
- Clean Code & SOLID Referenz (3 Dokumente)

**Noch zu erstellen**:
- Architektur-Patterns Referenz (STORY-007)
- Anti-Patterns Katalog (STORY-008)
- Code-Beispiele für alle Tage (STORY-009 bis STORY-012)
- Hands-On Übungen (STORY-013)
- Java-Projekt Setup (STORY-014)

## Lizenz

Die Trainingsmaterialien unterliegen dem Copyright und sind ausschließlich für Schulungsteilnehmer bestimmt.