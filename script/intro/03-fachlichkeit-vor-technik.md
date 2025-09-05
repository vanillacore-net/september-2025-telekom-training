# Fachlichkeit vor Technik
*Dauer: 25-30 Minuten - Kern-Message des Workshops*

## Lernziele
- Technology-First Anti-Pattern erkennen
- Domain-Driven Design Grundgedanke verstehen
- Richtige Reihenfolge: Fachlichkeit, Architektur, Technologie
- Typische Fehlentscheidungen als Warnung verstehen

## Das häufigste Anti-Pattern: Technology-First (10 Minuten)

### Typische Aussagen in Projekten:
> *"Wir nutzen jetzt Microservices!"* - Aber warum?  
> *"Lass uns auf Kubernetes umsteigen!"* - Aber welches Problem löst das?  
> *"Wir brauchen eine Event-Driven Architecture!"* - Aber passt das zu unserer Fachlichkeit?  
> *"NoSQL ist modern, weg mit der relationalen DB!"* - Aber was sind unsere Datenanforderungen?  

### Frage an Sie:
**"Kennen Sie solche Aussagen? Was war das Ergebnis?""

### Technology-First Symptome:
- **Hype-Driven Development**: "Das ist modern, das machen wir auch"
- **Solution looking for a problem**: "Wir haben Kafka, jetzt brauchen wir Events"
- **Architecture Astronauts**: Komplexe Lösungen für einfache Probleme
- **Over-Engineering**: 20 Services für 5 Use Cases

### Warum passiert Technology-First?
1. **Techniker denken technisch** - das ist normal
2. **Marketing macht Technologie sexy** - Buzzwords verkaufen sich
3. **CV-Driven Development** - "Ich will Kubernetes lernen"
4. **Komplexität wirkt professionell** - Einfachheit wird unterschätzt
5. **Copy-Paste Architecture** - "Netflix macht das so"

## Typische Beispiele für Technology-First (8-10 Minuten)

### Beispiel 1: Der Microservices-Hype
**Situation:** Monolithische Anwendung funktioniert gut, aber "Microservices sind modern"

**Technology-First Entscheidung:** 
- Monolith in 20+ Services aufteilen
- Kubernetes-Cluster für alles
- Event-Sourcing mit Message Queues

**Ergebnis:**
- 3x höhere Komplexität
- Latenz-Probleme durch Netzwerk-Calls
- Entwicklungszeit verdoppelt
- Debugging wird zum Albtraum

**Was war das Problem?** Es gab KEIN fachliches Problem, das Microservices gelöst hätten!

### Beispiel 2: Die NoSQL-Modernisierung
**Situation:** Bewährte relationale Datenbank für Finanzdaten

**Technology-First Entscheidung:**
- "NoSQL ist skalierbar und modern"
- Migration auf Document-Database
- "Schema-less ist flexibler"

**Ergebnis:**
- Datenkonsistenz-Probleme
- Komplexe Joins unmöglich
- Performance schlechter als vorher
- Migration zurück nach 18 Monaten

**Was war das Problem?** Finanzdaten sind RELATIONAL - NoSQL passte nicht zur Fachlichkeit!

### Beispiel 3: Die Event-Driven Everything
**Situation:** Interne Systeme mit synchronen APIs

**Technology-First Entscheidung:**
- "Events sind die Zukunft"
- Alles asynchron machen
- Event-Sourcing für alles

**Ergebnis:**
- Eventual Consistency verwirrt Benutzer
- Debugging unmöglich
- Race Conditions überall
- Rollback zu synchronen APIs nach 2 Jahren

**Was war das Problem?** Die Fachlichkeit brauchte SYNCHRONE Konsistenz!

## Domain-Driven Design: Fachlichkeit First (7-10 Minuten)

### Der DDD-Grundgedanke nach Eric Evans:
> *"The heart of software is its ability to solve domain-related problems for its user. All other concerns, however fascinating, should be subordinated."*

### Die richtige Reihenfolge:

#### 1. Fachlichkeit verstehen
- Was ist das Geschäftsproblem?
- Wie arbeiten die Fachexperten heute?
- Welche Regeln und Prozesse gibt es?
- Was sind die echten Anforderungen?

#### 2. Fachliche Architektur entwerfen
- Welche fachlichen Bereiche (Domains) gibt es?
- Wie schneiden wir die Bereiche (Bounded Contexts)?
- Welche fachlichen Services brauchen wir?
- Wie modellieren wir die Geschäftslogik?

#### 3. Technologie auswählen
- Welche Technologie unterstützt unser fachliches Modell am besten?
- Was löst unsere spezifischen Probleme?
- Was passt zu unserem Team und unserer Infrastruktur?

### Praktisches Beispiel: Kunden-Onboarding
#### Technology-First (falsch):
"Wir bauen eine Event-Driven Microservices-Architektur mit Message Queues und Container-Orchestrierung"

#### Domain-First (richtig):
1. **Fachlichkeit:** Kunde möchte Produkt bestellen → Verfügbarkeit prüfen → Vertrag erstellen → Bereitstellung beauftragen
2. **Fachliche Architektur:** CustomerManagement, ProductCatalog, OrderProcessing, Provisioning
3. **Technologie:** REST APIs reichen, relationale DB für Konsistenz, einfaches Messaging für Entkopplung

## Die richtige Herangehensweise (5 Minuten)

### Fragen in der richtigen Reihenfolge:

#### Phase 1: Fachlichkeit verstehen
1. **Was** soll das System tun?
2. **Warum** ist das wichtig für das Business?
3. **Wer** sind die Stakeholder?
4. **Wie** funktioniert der Prozess heute?

#### Phase 2: Fachliche Lösung entwerfen
5. **Welche** fachlichen Bereiche gibt es?
6. **Wo** sind die Grenzen zwischen den Bereichen?
7. **Wie** kommunizieren die Bereiche?

#### Phase 3: Technologie auswählen (erst jetzt!)
8. **Womit** implementieren wir das am besten?
9. **Was** löst unsere spezifischen Probleme?
10. **Wie** fügt es sich in unsere Landschaft ein?

## Warnsignale für Technology-First

### Aussagen, die Sie alarmieren sollten:
- "Das ist modern/trendy/hip"
- "Das macht Netflix/Google/Amazon auch"
- "Das steht in meinem Lebenslauf gut"
- "Das ist die Zukunft"
- "Das ist skalierbar" (ohne Nachweis des Skalierungsbedarfs)
- "Das ist performant" (ohne Performance-Anforderungen)

### Die richtigen Fragen stattdessen:
- "Welches fachliche Problem löst das?"
- "Was sind unsere spezifischen Anforderungen?"
- "Warum ist die aktuelle Lösung nicht gut genug?"
- "Was sind die Risiken und Kosten?"
- "Passt das zu unserem Team und unserer Infrastruktur?"

## Kernbotschaften
1. **Technologie ist Mittel, nicht Zweck**
2. **Fachlichkeit bestimmt die Architektur**
3. **Einfache Lösungen sind oft die besten**
4. **Nicht jedes Problem braucht die neueste Technologie**
5. **Copy-Paste von anderen Unternehmen funktioniert nicht**

## Diskussionsfragen
1. "Welche Technology-First Entscheidung bereuen Sie in Ihren Projekten?"
2. "Wie überzeuge ich ein Team, das 'coole Technologie' will?"
3. "Wann ist es OK, neue Technologie ohne konkretes Problem auszuprobieren?"

---
*Mit diesem Mindset verstehen wir jetzt, warum Design Patterns entstanden sind: Um bewährte FACHLICHE Lösungen zu dokumentieren!*