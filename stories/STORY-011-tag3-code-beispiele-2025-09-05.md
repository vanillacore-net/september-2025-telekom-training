# STORY-011: Tag 3 - Code-Beispiele Behavioral Patterns

## Story
Als Trainer benötige ich Code-Beispiele für Tag 3, um Behavioral Patterns und Event-Konzepte praktisch zu demonstrieren.

## Umfang

### Device Monitoring System
- Initial: If/Else Ketten für Device-Typen
- Smells: Switch Statements, OCP Violation
- Fixed: Strategy Pattern

### Device Processing Workflow
- Initial: Code-Duplikation
- Smells: Missing Template
- Fixed: Template Method

### Device Status Events
- Initial: Tight Coupling
- Smells: High Coupling
- Fixed: Observer Pattern

### Network Operations Commands
- Initial: Keine Undo-Möglichkeit
- Smells: Missing History
- Fixed: Command mit Undo/Redo

### Device Lifecycle Management
- Initial: Verschachtelte If/Else
- Smells: Complex Conditionals
- Fixed: State Pattern

### Request Validation Pipeline
- Initial: Monolithische Validierung
- Smells: God Method
- Fixed: Chain of Responsibility

## Akzeptanzkriterien

### Code-Qualität
- [ ] Strategy austauschbar zur Laufzeit
- [ ] Template Method Workflow klar
- [ ] Observer loose gekoppelt
- [ ] Command Undo funktionsfähig
- [ ] State Machine sauber implementiert
- [ ] Chain flexibel erweiterbar

### Dokumentation
- [ ] Event-Konzepte erklärt
- [ ] Pattern-Unterschiede klar
- [ ] Testing-Strategien dokumentiert
- [ ] Performance-Überlegungen

### Praxisbezug
- [ ] Device-Management realistisch
- [ ] Event-System praxisnah
- [ ] Undo-Requirements authentisch

## Deliverables
```
./examples/day3/
├── device-monitoring/
│   ├── initial/
│   ├── fixed/
│   ├── tests/
│   └── trainer-notes.md
├── device-workflow/
├── device-events/
├── network-commands/
├── device-lifecycle/
└── request-pipeline/
    └── [alle analog]
```

## Abhängigkeiten
- Unterstützt: STORY-003 (Tag 3 Content)
- Baut auf: STORY-010 (Tag 2 Beispiele)

## Priorität
**HOCH** - Behavioral Core

## Geschätzter Aufwand
10 Stunden