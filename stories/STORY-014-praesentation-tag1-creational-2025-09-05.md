# STORY-014: Präsentationsmaterialien Tag 1 - Creational Patterns

## Beschreibung
Erstellung der PowerPoint-Präsentationen für Tag 1 des Workshops mit Fokus auf Creational Patterns und Grundlagen-Architektur. Die Folien unterstützen die Wissensvermittlung der 5 Pattern-Module sowie der architektonischen Grundkonzepte.

## Umfang

### Folien-Module
1. **Tagesübersicht** (5 Folien)
   - Agenda Tag 1
   - Lernziele
   - Creational Patterns Übersicht
   - Architektur-Kontext
   - Zeitplan

2. **Factory Method Pattern** (12-15 Folien)
   - CustomerService Problem
   - Code-Smell: God Class
   - Pattern-Struktur (UML)
   - Refactoring-Schritte
   - Telekom-Beispiel
   - Vor-/Nachteile

3. **Abstract Factory Pattern** (12-15 Folien)
   - Service-Familien Problem
   - Produktlinien-Verwaltung
   - Pattern-Struktur (UML)
   - DSL/Mobile/Festnetz Beispiel
   - Implementierung
   - Einsatzgebiete

4. **Builder Pattern** (12-15 Folien)
   - Komplexe Objekt-Erstellung
   - Long Parameter List Problem
   - Pattern-Struktur (UML)
   - Fluent Interface
   - Contract Builder Beispiel
   - Director-Variante

5. **Prototype Pattern** (12-15 Folien)
   - Teure Objekt-Erstellung
   - Klonen vs Neu-Erstellung
   - Pattern-Struktur (UML)
   - Deep vs Shallow Copy
   - Configuration Templates
   - Registry-Pattern

6. **Singleton Pattern** (12-15 Folien)
   - Globaler Zugriff Problem
   - Pattern-Struktur (UML)
   - Thread-Safety
   - Anti-Pattern Diskussion
   - Dependency Injection Alternative
   - Testing-Problematik

7. **Zusammenfassung** (5-8 Folien)
   - Pattern-Vergleich
   - Entscheidungsmatrix
   - Clean Architecture Integration
   - Übungsaufgaben
   - Key Takeaways

## Grafische Anforderungen

### UML-Diagramme
- Factory Method Klassendiagramm
- Abstract Factory Struktur
- Builder Sequenzdiagramm
- Prototype Objektbeziehungen
- Singleton Implementierungsvarianten

### Code-Visualisierung
- Vorher-Nachher Code-Beispiele
- Maximal 8-10 Zeilen pro Folie
- Syntax-Highlighting
- Fokus auf Pattern-Anwendung

### Architektur-Grafiken
- Layered Architecture
- Clean Architecture
- Repository Pattern Integration
- Adapter Pattern Platzierung

## Sprachliche Vorgaben
- Technisches Deutsch
- Keine Marketing-Begriffe
- Maximal 5-6 Bulletpoints
- Kurze, prägnante Sätze
- Konsistent mit Script-Material

## Deliverables
```
./presentation/day1/
├── 00-tagesübersicht.pptx
├── 01-factory-method.pptx
├── 02-abstract-factory.pptx
├── 03-builder.pptx
├── 04-prototype.pptx
├── 05-singleton.pptx
└── 06-zusammenfassung.pptx
```

## Akzeptanzkriterien
- [ ] 70-90 Folien gesamt für Tag 1
- [ ] Alle 5 Patterns mit UML-Diagrammen
- [ ] Code-Beispiele visuell aufbereitet
- [ ] Architektur-Integration dargestellt
- [ ] Trainer-Notizen ergänzt
- [ ] Konsistentes Design
- [ ] Review durch Fachexperten