# Software-Architektur Workshop: Design Patterns im Fokus + Architektur-Überblick
## 4-Tage Training - Von einfachen Patterns zu komplexen Architekturen

---

## **Workshop-Struktur: Natürliche Progression**

**Schwerpunkt**: Design Patterns mit ausführlichen Diskussionen, Hands-on, Live-Demos
**Architektur**: Überblick, Schemas, Konzepte - weniger detailliert
**Aufbau**: Einfach → Komplex, Creational → Structural → Behavioral → Event-driven Architecture

---

## **Tag 1: Einfache Patterns und Schicht-Grundlagen**

### **09:00 - 10:30 | Code-Analyse und Factory Method - Erste Refactoring-Erfolge**
**Code-Beispiel**: Telekom Customer Service - 150 Zeilen mit mehreren Anti-Patterns
- **Code-Smell-Identifikation**: Long Method, God Class, Feature Envy erkennen
- **Ausführliche Analyse**: Was ist hier problematisch? Diskussion mit Teilnehmern
- **Refactoring-Prinzipien**: Kleine Schritte, Red-Green-Refactor, Ein Smell nach dem anderen
- **Factory Method**: Objekt-Creation vereinfachen - erste Pattern-Lösung
- **Clean-Code-Prinzipien**: SRP, DRY entstehen durch Factory-Refactoring
- **Anti-Pattern**: Object Orgy, Switch Statement Smell
- **Live-Demo**: Trainer refactored systematisch - Extract Method → Factory Method
- **Diskussion**: Wo habt ihr ähnliche Probleme? Wann macht Factory Sinn?

### **10:45 - 12:00 | Abstract Factory und Layered Architecture - System-Struktur schaffen**
**Pattern-Vertiefung**: Produkt-Familien elegant erstellen
- **Problem**: DSL-Services, Mobile-Services, Festnetz-Services koordinieren
- **Abstract Factory**: Zusammengehörige Objekt-Familien
- **Refactoring-Demo**: Von Conditional Creation Logic zu Abstract Factory
- **SOLID-Prinzipien**: Open/Closed Principle durch Factory-Abstraktion
- **Anti-Pattern**: Concrete Factory, Hard-coded Dependencies
- **Extensive Demo**: Trainer baut Service-Factory-Hierarchie
- **KISS vs. Flexibility**: Wann ist Abstract Factory zu komplex?
- **Architektur-Überblick**: Warum Layered Architecture? Schema und Grundprinzipien
- **Integration**: Wie Factory in Layer-Struktur passt

### **13:00 - 14:30 | Builder Pattern und Repository - Komplexe Objekterstellung elegant lösen**
**Komplexere Creation**: 15-Parameter-Konstruktor-Problem lösen
- **Code Smell**: Long Parameter List, Primitive Obsession
- **Problem-Diskussion**: Warum sind große Konstruktoren schlecht?
- **Refactoring-Schritte**: Introduce Parameter Object → Builder Pattern
- **Builder Pattern**: Schritt-für-Schritt Object-Construction
- **Fluent Interface**: Method-Chaining für bessere Lesbarkeit
- **Clean-Code**: Intention Revealing Names, Consistent Abstractions
- **Anti-Pattern**: Telescoping Constructor, Inconsistent Builder
- **Ausführliche Live-Demo**: Contract-Builder entwickeln
- **YAGNI-Prinzip**: Builder nur bei wirklicher Komplexität
- **Architektur-Pattern**: Repository für Data Access - Schema und Einsatz
- **Integration**: Builder im Repository-Context

### **14:45 - 16:30 | Singleton Gefahren und Adapter Pattern - Legacy-Integration meistern**
**Verschiedene Pattern-Typen**: Creation + Structural
- **Singleton**: Controlled Instance Creation - Nutzen und Gefahren
- **Anti-Pattern-Fokus**: Global State, Testing Problems, Hidden Dependencies
- **Refactoring Singleton**: Dependency Injection als Alternative
- **Adapter Pattern**: Legacy-System-Integration (SAP-Beispiel)
- **Code Smell**: Shotgun Surgery bei Legacy-Integration
- **Anti-Corruption-Refactoring**: Legacy-Chaos isolieren
- **Extensive Demo**: Trainer kapselt Legacy-API in moderne Schnittstelle
- **SOLID**: Interface Segregation, Dependency Inversion praktisch
- **Problem-Lösung-Diskussion**: Wie geht ihr mit Legacy um?
- **Clean Architecture Überblick**: Schema, Dependency-Direction, Grundprinzipien
- **Pattern-Integration**: Adapter in Clean Architecture

---

## **Tag 2: Structural Patterns und Hexagonal Architecture**

### **09:00 - 10:30 | Decorator Pattern - Flexible Erweiterungen ohne Vererbungs-Explosion**
**Verhalten dynamisch erweitern**: Tarif-Optionen ohne Vererbungs-Explosion
- **Problem-Szenario**: Tarife mit beliebigen Kombinationen von Optionen
- **Decorator Pattern**: Wrapper-Objekte für dynamische Features
- **Live-Coding Session**: Trainer baut Tarif-Decorator-System
- **Extensive Discussion**: Decorator vs. Inheritance - Vor- und Nachteile
- **Hands-on**: Teilnehmer erweitern Decorator-System (25 Min)

### **10:45 - 12:00 | Composite und Facade - Hierarchien vereinfachen, Komplexität verstecken**
**Hierarchische Strukturen**: Bäume einheitlich behandeln
- **Composite Pattern**: Teil-Ganzes-Hierarchien (verschachtelte Tarif-Strukturen)
- **Detailed Demo**: Trainer entwickelt Tarif-Composite-Struktur
- **Facade Pattern**: Komplexe Subsysteme vereinfachen
- **Integration-Demo**: Facade für Composite-Hierarchie
- **Hexagonal Architecture**: Ports & Adapters Schema und Konzept
- **Pattern-Mapping**: Structural Patterns als Adapters in Hexagonal Architecture

### **13:00 - 14:30 | Proxy und Flyweight - Performance durch intelligentes Caching**
**Controlled Access**: Caching, Lazy Loading, Access Control
- **Problem**: Teure Customer-Data-Abfragen bei jedem Zugriff
- **Proxy Pattern**: Placeholder mit intelligenter Logik
- **Performance-Demo**: Trainer misst Verbesserungen durch Caching-Proxy
- **Flyweight Pattern**: Memory-Optimierung für große Datenmengen
- **Extensive Discussion**: Performance-Patterns - wann welcher Ansatz?
- **Hands-on**: Teilnehmer implementieren einfachen Caching-Proxy (20 Min)

### **14:45 - 16:30 | Bridge Pattern und Microservices - Abstraktion von Implementation trennen**
**Abstraction-Implementation trennen**: Payment-Processing-Beispiel
- **Bridge Pattern**: Abstraktion von konkreter Implementation entkoppeln
- **Detailed Example**: Payment-Processor für verschiedene Provider
- **Demo**: Trainer zeigt austauschbare Payment-Implementierungen
- **Design-Prinzipien**: Composition over Inheritance praktisch anwenden
- **Microservices Architecture**: Schema, Service-Schneidung, Grundprinzipien
- **Pattern-Services**: Wie Patterns in Service-Architektur einsetzen

---

## **Tag 3: Behavioral Patterns und Event-Driven Basics**

### **09:00 - 10:30 | Strategy Pattern - Austauschbare Algorithmen zur Laufzeit**
**Austauschbare Algorithmen**: Monitoring-Strategien für verschiedene Device-Types
- **Problem**: If/Else-Ketten für verschiedene Berechnungsarten
- **Strategy Pattern**: Algorithmen-Familie zur Laufzeit austauschbar
- **Comprehensive Demo**: Trainer entwickelt Device-Monitoring-Strategies
- **OCP-Prinzip**: Open/Closed Principle praktisch erleben
- **Extensive Discussion**: Strategy vs. Template Method - Abgrenzung
- **Hands-on**: Teilnehmer entwickeln Pricing-Strategy (25 Min)

### **10:45 - 12:00 | Template Method und Observer - Workflows vereinheitlichen, Kopplung reduzieren**
**Gemeinsame Workflows + Loose Coupling**: Device-Processing mit Events
- **Template Method**: Algorithmus-Skelett mit variablen Schritten
- **Live-Demo**: Device-Processing-Workflow für verschiedene Types
- **Observer Pattern**: Event-Notification ohne tight coupling
- **Detailed Implementation**: Device-Status-Changes mit Observer-System
- **Event-Driven Überblick**: Events vs. Messages, Grundprinzipien, Schema
- **Pattern-Events**: Observer als Basis für Event-driven Architecture

### **13:00 - 14:30 | Command Pattern - Operations zu Objekten für Undo und Logging**
**Requests kapseln**: Undo, Logging, Queuing für Network-Operations
- **Command Pattern**: Operations als First-Class Objects
- **Comprehensive Example**: Device-Commands mit Undo-Funktionalität
- **Live-Coding**: Trainer baut Command-System mit MacroCommands
- **Discussion**: Command vs. Strategy - Unterschiede und Einsatzgebiete
- **Hands-on**: Teilnehmer erweitern Command-System um Logging (20 Min)
- **CQRS-Überblick**: Command Query Responsibility Segregation - Schema

### **14:45 - 16:30 | State und Chain of Responsibility - Zustandsmaschinen und Processing-Pipelines**
**Zustandsmaschinen + Processing-Pipelines**: Device-Lifecycle Management
- **State Pattern**: Zustandsabhängiges Verhalten ohne If/Else-Chaos
- **Detailed Demo**: Device-State-Machine (Online, Offline, Maintenance, Error)
- **Chain of Responsibility**: Request-Processing-Pipeline
- **Integration-Example**: State-Changes durch Chain validieren
- **Event Sourcing Überblick**: State-Changes als Events - Schema und Vorteile
- **Extensive Discussion**: Wann State Pattern, wann einfache Enums?

---

## **Tag 4: Komplexe Patterns und Architecture Integration**

### **09:00 - 10:30 | Mediator Pattern - Zentrale Koordination statt Communication-Spaghetti**
**Zentrale Koordination**: Multi-Device Operations orchestrieren  
- **Problem**: Viele Objects kommunizieren direkt - Communication-Spaghetti
- **Mediator Pattern**: Zentrale Koordinations-Logik
- **Complex Demo**: Trainer orchestriert Device-Interaktionen durch Mediator
- **vs. Observer**: Unterschied zwischen Mediator und Observer Pattern
- **API Gateway**: Mediator-Pattern auf Architektur-Ebene - Schema
- **Hands-on**: Teilnehmer skizzieren Mediator für eigenes System (15 Min)

### **10:45 - 12:00 | Iterator und Visitor - Collection-Traversal und flexible Operations**
**Collection-Traversal + Operations**: Flexible Object-Structure-Processing
- **Iterator Pattern**: Einheitlicher Collection-Zugriff ohne interne Struktur
- **Visitor Pattern**: Operations auf Object-Hierarchien ohne deren Änderung
- **Advanced Demo**: Device-Reports durch Visitor-Pattern
- **Double Dispatch**: Visitor-Mechanismus im Detail verstehen
- **Integration Architecture**: Message-Processing-Patterns in verteilten Systemen
- **Complex Discussion**: Wann Visitor, wann normale Methoden?

### **13:00 - 14:30 | Memento und Interpreter - State-Management und Domain-Specific Languages**
**State-Management + Domain Languages**: Komplexe Business-Rule-Systeme
- **Memento Pattern**: State-Snapshots für Undo/Rollback-Funktionalität
- **Interpreter Pattern**: Domain-Specific Language für Business Rules
- **Advanced Example**: Tarif-Rules als interpretierbare Expressions
- **Demo**: Trainer baut Mini-DSL für Telekom-Business-Rules
- **Event Sourcing Deep-Dive**: Events als Memento-Pattern - praktische Umsetzung
- **Discussion**: DSL vs. Configuration - Komplexität vs. Flexibilität

### **14:45 - 16:00 | Pattern-Integration - Alles zusammenfügen in produktiver Architektur**
**Alles zusammenfügen**: Patterns in produktiver Architektur
- **Pattern-Combinations**: Wie Patterns elegant zusammenspielen
- **Real-World System**: Trainer zeigt Pattern-basierte Telekom-Architektur
- **Anti-Patterns**: Over-Engineering, Pattern-Obsession vermeiden
- **Production-Concerns**: Monitoring, Logging, Error-Handling in Pattern-Code
- **Microservices + Patterns**: Patterns in Service-orientierter Architektur
- **Architecture Evolution**: Wie entwickelt sich Pattern-basierte Architektur?

### **16:00 - 16:30 | Praxis-Transfer - Vom Workshop in den Entwicklungsalltag**
**Wissen anwenden**: Konkrete nächste Schritte
- **Pattern-Assessment**: Welche Patterns lösen eure größten Code-Probleme?
- **Architecture-Roadmap**: Von aktueller Situation zu Ziel-Architektur
- **Team-Adoption**: Wie führt man Patterns und bessere Architektur ein?
- **Learning-Path**: Vertiefung in spezifische Patterns/Architecture-Bereiche
- **Code-Review-Guidelines**: Pattern-Qualität in Reviews sicherstellen
- **Community**: Architektur-Wissen im Team etablieren und weiterentwickeln

---

## **Durchgängige Best-Practices und Anti-Pattern Awareness**

### **Clean-Code-Prinzipien (integriert, nicht als Block)**
- **SOLID**: Entstehen natürlich durch Pattern-Anwendung
- **DRY**: Don't Repeat Yourself - Template Method, Abstract Factory
- **KISS**: Keep It Simple - Pattern nur bei echtem Bedarf
- **YAGNI**: You Aren't Gonna Need It - Over-Engineering vermeiden

### **Refactoring-Techniken (durchgängig)**
- **Small Steps**: Ein Refactoring nach dem anderen
- **Red-Green-Refactor**: Tests als Sicherheitsnetz
- **Code Smells**: Long Method, God Class, Feature Envy, Data Clumps
- **Extract Method/Class**: Cohesion verbessern
- **Move Method**: Coupling reduzieren
- **Introduce Parameter Object**: Long Parameter List beseitigen

### **Anti-Patterns (bei jedem Pattern)**
- **Pattern-Obsession**: Patterns um der Patterns willen
- **Golden Hammer**: Ein Pattern für alle Probleme
- **God Object**: Singleton als Global State missbraucht
- **Spaghetti Code**: Observer ohne klare Struktur
- **Lasagna Code**: Zu viele Abstraktions-Layer
- **Copy-Paste Programming**: Template Method vergessen

### **Best-Practice-Integration**
- **When to Pattern**: Problem first, Pattern second  
- **Refactoring to Patterns**: Schrittweise Pattern einführen
- **Pattern Combinations**: Elegante Pattern-Synergien
- **Performance Considerations**: Pattern-Overhead verstehen
- **Testing Patterns**: Wie Pattern-Code testbar bleibt
- **Team Adoption**: Pattern-Wissen nachhaltig etablieren

---

## **Schwerpunkte und Zeiteinteilung**

### **Design Patterns (75% der Zeit)**
- **Ausführliche Diskussionen**: Wann Pattern, wann einfache Lösung?
- **Hands-on Exercises**: 15-25 Min praktische Übungen pro Pattern
- **Live-Demos**: Trainer entwickelt Patterns vor den Teilnehmern
- **Problem-Lösung-Fokus**: Echte Code-Probleme elegant lösen
- **Anti-Pattern Awareness**: Was kann schiefgehen?

### **Software-Architekturen (25% der Zeit)**
- **Schemas und Überblicke**: Visuelle Darstellung der Konzepte
- **Grundprinzipien**: Dependency Direction, Separation of Concerns
- **Integration**: Wie Patterns in Architekturen einsetzen
- **Evolution**: Von einfachen zu komplexen Architektur-Ansätzen
- **Production-Ready**: Was braucht produktive Architektur?

### **Progression und Integration**
- **Einfach → Komplex**: Factory Method → Visitor Pattern
- **Creational → Structural → Behavioral**: Natürlicher Pattern-Aufbau  
- **Layer → Clean → Hexagonal → Event-driven**: Architektur-Evolution
- **Pattern-Architecture-Synergien**: Wie beides zusammenarbeitet