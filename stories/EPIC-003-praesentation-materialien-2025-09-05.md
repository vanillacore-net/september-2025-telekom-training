# EPIC-003: Präsentationsmaterialien erstellen

## Ziel
Erstellung professioneller PowerPoint-Präsentationsmaterialien für das 4-tägige Software-Architektur Training. Die Folien dienen als visuelle Unterstützung während des Workshops und folgen der Struktur der Schulungsunterlagen mit technischem, neutralem Sprachstil.

## Umfang

### Präsentations-Struktur
- Einführungspräsentation (90-120 Minuten)
- Tag 1: Creational Patterns (8 Stunden Material)
- Tag 2: Structural Patterns (8 Stunden Material)
- Tag 3: Behavioral Patterns (8 Stunden Material)
- Tag 4: Advanced Patterns (8 Stunden Material)

### Folien-Komponenten

#### Einführung (25-30 Folien)
- Software-Architektur Definition
- Clean Code Grundlagen
- Fachlichkeit vor Technik
- Design Patterns Motivation
- Refactoring Philosophie
- Workshop-Ablauf

#### Pro Pattern-Modul (12-15 Folien)
- Problem-Beschreibung
- Code-Smell Beispiel
- Pattern-Struktur (UML-Diagramm)
- Lösung und Vorteile
- Implementierungs-Details
- Praxis-Beispiel
- Diskussionspunkte

#### Pro Tag zusätzlich
- Tagesübersicht
- Architektur-Kontext
- Pattern-Zusammenhänge
- Zusammenfassung
- Übungsaufgaben

## Deliverables

### Verzeichnis-Struktur
```
./presentation/
├── intro/
│   ├── 01-software-architektur.pptx
│   ├── 02-clean-code.pptx
│   ├── 03-fachlichkeit-vor-technik.pptx
│   ├── 04-patterns-motivation.pptx
│   ├── 05-refactoring.pptx
│   └── 06-workshop-ablauf.pptx
├── day1/
│   ├── 00-tagesübersicht.pptx
│   ├── 01-factory-method.pptx
│   ├── 02-abstract-factory.pptx
│   ├── 03-builder.pptx
│   ├── 04-prototype.pptx
│   ├── 05-singleton.pptx
│   └── 06-zusammenfassung.pptx
├── day2/
│   ├── 00-tagesübersicht.pptx
│   ├── 01-decorator.pptx
│   ├── 02-composite.pptx
│   ├── 03-facade.pptx
│   ├── 04-proxy.pptx
│   ├── 05-flyweight.pptx
│   ├── 06-bridge.pptx
│   ├── 07-adapter.pptx
│   └── 08-zusammenfassung.pptx
├── day3/
│   ├── 00-tagesübersicht.pptx
│   ├── 01-strategy.pptx
│   ├── 02-template-method.pptx
│   ├── 03-observer.pptx
│   ├── 04-command.pptx
│   ├── 05-state.pptx
│   ├── 06-chain-of-responsibility.pptx
│   └── 07-zusammenfassung.pptx
├── day4/
│   ├── 00-tagesübersicht.pptx
│   ├── 01-mediator.pptx
│   ├── 02-iterator.pptx
│   ├── 03-visitor.pptx
│   ├── 04-memento.pptx
│   ├── 05-interpreter.pptx
│   ├── 06-pattern-integration.pptx
│   └── 07-workshop-abschluss.pptx
└── templates/
    ├── folienlayout.pptx
    └── grafik-vorlagen/
```

## Design-Anforderungen

### Folien-Gestaltung
- **Schrift**: Klar lesbar, mindestens 20pt für Bulletpoints
- **Farben**: Telekom Corporate Design
- **Layout**: Einheitlich über alle Präsentationen
- **Grafiken**: UML-Diagramme für Pattern-Strukturen
- **Code**: Syntax-Highlighting, maximal 10 Zeilen pro Folie

### Inhaltliche Vorgaben
- **Bulletpoints**: Maximal 5-6 pro Folie
- **Text**: Kurze, prägnante Sätze
- **Sprache**: Technisches Deutsch ohne Füllwörter
- **Stil**: Neutral, sachlich, nicht KI-generiert
- **Struktur**: Identisch zum Script-Material

### Qualitätskriterien
- Keine überladenen Folien
- Klare visuelle Hierarchie
- Konsistente Terminologie
- Fokus auf Kernaussagen
- Unterstützung des Vortrags, nicht Ersatz

## Grafische Elemente

### UML-Diagramme
- Klassendiagramme für Pattern-Struktur
- Sequenzdiagramme für Ablauf
- Komponentendiagramme für Architektur
- Vereinfachte Darstellung

### Code-Beispiele
- Vorher-Nachher Vergleiche
- Farbliche Hervorhebung von Änderungen
- Maximal 10 Zeilen pro Beispiel
- Fokus auf Kernkonzept

### Architektur-Grafiken
- Schichtenmodelle
- Hexagonal Architecture
- Microservices-Übersichten
- Pattern-Beziehungen

## Technische Spezifikationen

### Format
- PowerPoint (.pptx)
- 16:9 Bildformat
- Druckoptimiert
- Notizen für Trainer

### Kompatibilität
- PowerPoint 2016+
- Keine externen Plugins
- Eingebettete Schriften
- Offline-fähig

## Abhängigkeiten
- Script-Materialien aus EPIC-001
- Code-Beispiele aus EPIC-002
- Telekom Corporate Design Guidelines

## Akzeptanzkriterien
- [ ] Alle Pattern-Module mit Präsentationen abgedeckt
- [ ] Maximal 5-6 Bulletpoints pro Folie eingehalten
- [ ] Sprache entspricht Script-Material
- [ ] UML-Diagramme für alle Patterns erstellt
- [ ] Code-Beispiele visuell aufbereitet
- [ ] Einheitliches Design durchgängig
- [ ] Trainer-Notizen hinzugefügt
- [ ] Review durch Trainings-Team positiv

## Ausschluss
Das `presentation/` Verzeichnis wird in `.gitignore` aufgenommen, da PowerPoint-Dateien nicht versionskontrolliert werden sollen.