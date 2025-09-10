# Design Patterns Workshop - Einführungsblock

---

# Workshop-Erwartungen

## Was werden wir in den nächsten 4 Blöcken lernen?

- Block 1: Creational Patterns - Flexible Objekterzeugung in Enterprise-Systemen
- Block 2: Structural Patterns - Objektkomposition und Systemintegration  
- Block 3: Behavioral Patterns - Verhalten und Kommunikation zwischen Objekten
- Block 4: Advanced Patterns - Komplexe Szenarien und Pattern-Kombinationen

**Note:** Die Workshop-Struktur folgt der bewährten GoF-Kategorisierung und fokussiert auf praktische Enterprise-Anwendungsfälle. Jeder Block kombiniert Theorie mit intensiven Praxisübungen.

---

# Block 1: Creational Patterns

## Schwerpunkt: Objekterzeugung

- Factory Method: Objekterzeugung ohne konkrete Klassen
- Abstract Factory: Familien verwandter Objekte
- Builder: Komplexe Objekte Schritt für Schritt aufbauen
- Prototype: Objektklonierung für kostspielige Initialisierung
- Singleton: Eine Instanz für das ganze System

**Note:** Praktische Anwendungsfälle umfassen Service-Instantiierung, Multi-Provider APIs, komplexe Request-Objekte, kostspielige Objektklonierung und Shared Resources. Lernziele: Dependency Injection verstehen, flexible Objekterzeugung implementieren, Tight Coupling vermeiden.

---

# Block 2: Structural Patterns

## Schwerpunkt: Strukturelle Komposition

- Adapter: Inkompatible Schnittstellen verbinden
- Decorator: Verhalten dynamisch erweitern
- Facade: Komplexe Subsysteme vereinfachen
- Composite: Hierarchische Strukturen behandeln

**Note:** Praktische Anwendungsfälle: Legacy-System Integration, Service-Erweiterungen, API-Vereinfachung und hierarchische Strukturen. Lernziele: Legacy-Integration meistern, flexible Erweiterungen implementieren, Komplexität kapseln, Hierarchien elegant modellieren.

---

# Block 3: Behavioral Patterns

## Schwerpunkt: Verhalten und Kommunikation

- Observer: Ereignisse propagieren
- Strategy: Algorithmen austauschbar machen
- Command: Operationen als Objekte
- State: Zustandsabhängiges Verhalten

**Note:** Praktische Anwendungsfälle: Event-Systeme, Algorithmus-Varianten, API-Operations und Workflow-States. Lernziele: Lose Kopplung durch Events, Algorithmen flexibel gestalten, Undo/Redo implementieren, State Machines beherrschen.

---

# Block 4: Advanced Patterns

## Schwerpunkt: Komplexe Szenarien

- Template Method: Algorithmus-Skelett mit variablen Teilen
- Visitor: Operationen von Datenstruktur trennen
- Chain of Responsibility: Requests durch Handler-Kette
- Mediator: Komplexe Objekt-Interaktionen koordinieren
- Enterprise Patterns: Repository, Unit of Work, MVC/MVP/MVVM

**Note:** Lernziele: Enterprise-Architektur implementieren, Datenzugriff professionell abstrahieren, UI-Logik sauber trennen, komplexe Workflows koordinieren. Diese Patterns sind essentiell für skalierbare Enterprise-Anwendungen.

---

# Was werden wir NICHT behandeln?

## Bewusst ausgeklammerte Themen

- Framework-spezifische Patterns (Spring, Hibernate, etc.)
- Architektur-Patterns (Microservices, CQRS, Event Sourcing)
- Concurrency-Patterns (würde eigenen Workshop füllen)
- Specific UI-Frameworks (Angular, React, Vue)
- Cloud-native Patterns (separate Workshop-Serie)

**Note:** Grund für diese Beschränkung: GoF Patterns sind zeitlos und fundamental, Prinzipien gelten für alle Technologien, lieber wenige Patterns richtig verstehen, jeden Tag praktische Übungen. Fokus behalten und Übertragbarkeit sicherstellen.

---

# Lernziele und Kompetenzen

## Zentrale Problemstellungen

- Legacy-System Integration und Adapter-Patterns
- Flexible Objekterzeugung und Dependency Management
- Algorithmus-Variationen und Strategy-Patterns
- Event-basierte Kommunikation und Observer-Patterns
- Komplexe Objektstrukturen und Composite-Patterns

**Note:** Nach dem Workshop können Sie GoF-Patterns in konkreten Problemkontexten anwenden, pattern-basierte Refactoring-Strategien entwickeln, Architektur-Entscheidungen mit Patterns begründen, Clean Code Prinzipien mit Pattern-Design verbinden.

---

# Praktische Organisation

## Was Sie mitbringen sollten

- Laptop mit Java 11+ und IDE (IntelliJ/Eclipse)
- Git für Code-Austausch
- Offenheit für neue Perspektiven
- Eigene Code-Beispiele (wenn möglich)

**Note:** Was wir bereitstellen: Vollständige Code-Beispiele für alle Patterns, Enterprise-typische Use Cases, Refactoring-Challenges, Pattern-Spickzettel als Nachschlagewerk.

---

# Workshop-Regeln

## Zusammenarbeit

- Fragen jederzeit - Unterbrechungen sind erwünscht
- Handy stumm - aber für Code-Recherche gerne nutzen
- Kamera an bei Remote-Teilnahme
- Code teilen - Github Repository für alle
- Feedback geben - täglich kurze Retro

**Note:** Diese Regeln fördern aktive Teilnahme und effektiven Wissensaustausch. Der Workshop lebt von Interaktion und praktischen Diskussionen.

---

# Was ist Software-Architektur?

## Verschiedene Definitionen

- IEEE 1471: "Grundlegende Organisation eines Systems durch Komponenten und deren Beziehungen"
- Martin Fowler: "Architecture is about the important stuff. Whatever that is."
- Grady Booch: "Signifikante Design-Entscheidungen, gemessen an Änderungskosten"
- Simon Brown: "Struktur und Vision für gemeinsames Verständnis"

**Note:** Diese Definitionen zeigen verschiedene Perspektiven auf Software-Architektur. Gemeinsame Erkenntnisse: Struktur (Organisation der Software), Entscheidungen (wichtige Design-Entscheidungen), Beziehungen (Zusammenhänge), Kosten (spätere Änderbarkeit), Kommunikation (Vermittlung des Designs).

---

# Arbeitsdefinition für diesen Workshop

## Unser gemeinsames Verständnis

> **Software-Architektur ist die Kunst, wichtige Designentscheidungen zu treffen, die die Struktur, das Verhalten und die Evolution eines Systems bestimmen - mit dem Ziel, fachliche Anforderungen optimal zu erfüllen.**

**Note:** Diese Definition betont sowohl technische Aspekte (Struktur, Verhalten) als auch fachliche Ziele. Architektur ist nicht nur Technik - sie muss Geschäftsziele verstehen und unterstützen.

---

# Enterprise-Kontext

## Besonderheiten in großen Unternehmen

- Legacy-Systeme: Jahrzehntealte Systeme, die noch laufen müssen
- Regulatorische Anforderungen: DSGVO, Compliance-Standards
- Hochverfügbarkeit: 99.9%+ Uptime-Anforderungen
- Skalierung: Millionen von Benutzern, große Datenmengen
- Sicherheit: Kritische Geschäftsdaten, Cyber-Security

**Note:** Enterprise-Herausforderungen: Modernisierung bei laufendem Betrieb, Compliance und Governance, Performance bei hoher Last, Kostenoptimierung bei gleichzeitig hoher Qualität. Integration von Dutzenden von Systemen.

---

# Clean Code Grundlagen

## Was bedeutet "sauber"?

- Lesbarkeit vor Cleverness
- Eindeutige Namen für Funktionen, Variablen, Boolean
- Kurze Funktionen (Eine Funktion = Ein Gedanke)
- Keine Kommentare, die Code erklären
- Konsistenz in Namenskonventionen und Formatierung

**Note:** Clean Code Definition nach Robert C. Martin: "Code that has been taken care of." 80% der Zeit wird Code gelesen, nur 20% geschrieben. Wartbarkeit ist wichtiger als Cleverness.

---

# Software-Lebenszyklus in der Praxis

## Kostenverteilung

- Entwicklung: 20% der Gesamtkosten
- Wartung: 80% der Gesamtkosten

## Wartbarkeits-Faktoren

- Verständlichkeit: Kann ich verstehen, was der Code macht?
- Änderbarkeit: Kann ich sicher Änderungen vornehmen?
- Testbarkeit: Kann ich das Verhalten überprüfen?

**Note:** Legacy-Systeme mit Millionen Zeilen undokumentiertem Code verursachen hohe Wartungskosten, weil jede kleine Änderung Wochen dauert und risikoreich ist. Clean Code ist eine Investition in die Zukunft.

---

# Technische Schulden

## Definition nach Martin Fowler

> *"Technical debt is a metaphor referring to the eventual consequences of poor system design, software architecture or software development within a codebase."*

## Arten technischer Schulden

- Bewusste Schulden: "Quick and dirty, aber nächste Woche aufräumen"
- Unbewusste Schulden: Entstehen durch Unwissen (gefährlichste Art)
- Umwelt-Schulden: Änderung der Anforderungen macht Code obsolet

**Note:** Praktische Auswirkungen: Zinsen (jede Änderung dauert länger), Hauptsumme (Aufwand für Refactoring), Insolvenz (System nicht mehr wartbar). Schulden-Management: Sichtbar machen, priorisieren, kontinuierlich abbauen.

---

# Fachlichkeit vor Technik

## Das häufigste Anti-Pattern: Technology-First

- "Wir nutzen jetzt Microservices!" - Aber warum?
- "Lass uns auf Kubernetes umsteigen!" - Aber welches Problem löst das?
- "NoSQL ist modern, weg mit der relationalen DB!" - Aber was sind unsere Datenanforderungen?

**Note:** Technology-First Symptome: Hype-Driven Development, Solution looking for a problem, Architecture Astronauts, Over-Engineering. Warum passiert das? Techniker denken technisch, Marketing macht Technologie sexy, CV-Driven Development, Komplexität wirkt professionell.

---

# Technology-First Beispiele

## Der Microservices-Hype

- Problem: Monolith funktioniert gut, aber "Microservices sind modern"
- Entscheidung: Monolith in 20+ Services aufteilen
- Ergebnis: 3x höhere Komplexität, Latenz-Probleme, Debugging-Albtraum

## Die NoSQL-Modernisierung

- Problem: Bewährte relationale DB für Finanzdaten
- Entscheidung: Migration auf Document-Database
- Ergebnis: Datenkonsistenz-Probleme, Migration zurück nach 18 Monaten

**Note:** Das Grundproblem: Es gab KEIN fachliches Problem, das diese Technologien gelöst hätten. Finanzdaten sind RELATIONAL - NoSQL passte nicht zur Fachlichkeit.

---

# Domain-Driven Design: Fachlichkeit First

## Die richtige Reihenfolge

1. **Fachlichkeit verstehen**: Geschäftsproblem, Arbeitsprozesse, Regeln, echte Anforderungen
2. **Fachliche Architektur entwerfen**: Domains, Bounded Contexts, fachliche Services, Geschäftslogik-Modellierung
3. **Technologie auswählen**: Was unterstützt unser fachliches Modell am besten?

**Note:** DDD-Grundgedanke nach Eric Evans: "The heart of software is its ability to solve domain-related problems for its user. All other concerns should be subordinated." Technology-First führt zu Over-Engineering und unpassenden Lösungen.

---

# Die richtige Herangehensweise

## Fragen in der richtigen Reihenfolge

**Phase 1: Fachlichkeit verstehen**
- Was soll das System tun? Warum ist das wichtig? Wer sind die Stakeholder?

**Phase 2: Fachliche Lösung entwerfen**
- Welche fachlichen Bereiche gibt es? Wo sind die Grenzen? Wie kommunizieren sie?

**Phase 3: Technologie auswählen**
- Womit implementieren wir das am besten? Was löst unsere spezifischen Probleme?

**Note:** Warnsignale für Technology-First: "Das ist modern/trendy", "Das macht Netflix auch", "Das steht in meinem Lebenslauf gut". Die richtigen Fragen: "Welches fachliche Problem löst das?", "Was sind unsere spezifischen Anforderungen?"

---

# Design Patterns Motivation

## Geschichte: Warum entstanden Design Patterns?

- Christopher Alexander (1977): "A Pattern Language" - Gebäude-Architektur!
- Gang of Four (1994): Übertragung auf Software
- Problem 1: Wiederkehrende Design-Probleme
- Problem 2: Schlechte Kommunikation zwischen Entwicklern
- Problem 3: Fehlende Best Practices

**Note:** Alexanders Erkenntnis: "Each pattern describes a problem which occurs over and over again, and then describes the core of the solution." GoF erkannten: Erfahrene Entwickler nutzen bewährte Lösungen, wiederkehrende Probleme haben wiederkehrende Lösungen.

---

# Warum Design Patterns?

## Vier Hauptvorteile

- **Bewährte Lösungen nutzen**: Anstatt Rad neu erfinden
- **Gemeinsame Sprache entwickeln**: Vokabular für Teams
- **Design-Qualität verbessern**: SOLID-Prinzipien kodifiziert
- **Wartbarkeit erhöhen**: Bekannte Patterns sind verständlicher

**Note:** Kommunikationsverbesserung: Vorher: "Wir brauchen eine Klasse, die andere Klassen erzeugt..." Nachher: "Wir nutzen Factory Pattern". Team-Kommunikation wird effizienter, neue Teammitglieder verstehen Design schneller.

---

# Patterns sind NICHT...

## Pattern-Missbrauch vermeiden

- Silberkugeln: Patterns lösen nicht alle Probleme
- Dogmen: Patterns müssen nicht sklavisch befolgt werden
- Komplexität um der Komplexität willen
- Copy-Paste Code: Patterns sind konzeptuelle Lösungen

## Anti-Beispiel aus der Praxis
- Problem: Einfache Konfigurationswerte lesen
- Overengineered: AbstractConfigurationFactoryBuilderStrategyProxy
- Einfach: Properties.load()

**Note:** Golden Hammer vermeiden: "Ich habe einen Hammer, alles sieht aus wie ein Nagel". Pattern Overload: 20 Patterns für 5 Klassen. Wichtige Fragen: Haben wir wirklich das Problem? Ist es komplex genug für ein Pattern?

---

# Refactoring Philosophie

## Was ist Refactoring?

**Definition nach Martin Fowler:**
> *"Refactoring is the process of changing a software system in such a way that it does not alter the external behavior of the code yet improves its internal structure."*

## Schlüsselelemente

- Verhalten bleibt gleich - Funktionalität ändert sich nicht
- Struktur wird besser - Code wird wartbarer
- Kleine Schritte - Viele kleine, sichere Änderungen

**Note:** Was Refactoring NICHT ist: Bugfixes, neue Features, Performance-Optimierung, Rewrite. Refactoring ist kontinuierlich, nicht ein einmaliges Event.

---

# Die Boy Scout Rule

## Ursprung und Übertragung

**Boy Scouts of America:**
> *"Try and leave this world a little better than you found it."*

**Uncle Bob für Software:**
> *"Always leave the campground cleaner than you found it."*

## Praktische Anwendung

- Bei jedem Code-Touch: Verstehe → Verbessere → Prüfe → Committe
- Konkrete Aktionen: Variable umbenennen, Magic Numbers extrahieren, Long Methods aufteilen

**Note:** Boy Scout Aktionen: temp → elapsedTimeInDays, 7 → DAYS_PER_WEEK, Duplicate Code extrahieren, Unused Code entfernen, Kommentare durch self-documenting code ersetzen.

---

# Wann refactoren?

## Die "Rule of Three"

1. **Das erste Mal** - mache es einfach
2. **Das zweite Mal** - ärgere dich über Duplikation, aber mache es trotzdem
3. **Das dritte Mal** - refactore!

## Refactoring-Trigger

- Wenn du Code verstehen musst (vor Bug-Fix, vor Feature)
- Wenn du Duplikation siehst (Copy-Paste Code)
- Wenn Code "riecht" (Long Method, Large Class, Long Parameter List)

**Note:** Gute Refactoring-Zeitpunkte: Sprint Planning, Bug-Fixing mit Boy Scout Rule, Code Reviews, Technische Stories. "Make the change easy, then make the easy change."

---

# Refactoring-Sicherheitsmaßnahmen

## Tests zuerst

**Vor Refactoring:** Verstehe Verhalten, schreibe Tests, alle Tests grün
**Während Refactoring:** Nach jedem Schritt Tests laufen lassen
**Nach Refactoring:** Alle Tests noch grün

## Kleine Schritte und IDE-Unterstützung

- Ein Schritt nach dem anderen
- Extract Method, Rename Variable, Move Method
- IDE kann viel automatisch und sicher machen

**Note:** Häufige Refactoring-Patterns: Extract Method (Long Method aufteilen), Replace Magic Number with Named Constant. Enterprise-Kontext: Legacy Systems extra vorsichtig, Live Systems graduelle Änderungen.

---

# Refactoring als Weg zu Patterns

## Patterns entstehen durch Refactoring

**Typischer Ablauf:**
1. Duplikation entsteht natürlich
2. Refactoring macht Gemeinsamkeiten sichtbar
3. Pattern Recognition - "Das ist ein bekanntes Problem"
4. Pattern Application - Bekannte Lösung anwenden

## Wichtige Erkenntnis

**Patterns werden durch Refactoring eingeführt, nicht von Anfang an geplant!**

**Note:** Beispiel Strategy Pattern: Ein if-else → zweites if-else → Duplikation reduzieren → drittes ähnliches Pattern → "Das ist Strategy Pattern!" → Refactoring zu Strategy Pattern. Tests sind essenziell für sicheres Refactoring.

---

# Workshop-Bereitschaft

## Ihr persönliches Lernziel

**Frage an die Teilnehmer:**
> *"Was ist Ihr persönliches Lernziel für diese 4 Blöcke?"*

## Zielsetzung

**Systematische Anwendung von Design Patterns zur Verbesserung von Code-Qualität, Wartbarkeit und Systemarchitektur in Enterprise-Umgebungen.**

**Sind Sie bereit? Dann starten wir mit Block 1: Creational Patterns!**

**Note:** Lassen Sie jeden Teilnehmer sein Ziel formulieren - wir kommen am Ende darauf zurück! Mit diesem Refactoring-Mindset sind wir bereit - wir wissen jetzt, dass wir Patterns durch evolutionäre Verbesserung entdecken.