---
<!-- Version: 1.3.0-fragment-colon-fix -->
type: slide
title: Software-Architektur - Einführung
description: Grundlagen zu Software-Architektur, Clean Code, Fachlichkeit vor Technik und Design Patterns Motivation
tags: design-patterns, workshop, architecture, training, introduction, clean-code, domain-first
slideOptions:
  theme: white
  transition: slide
  backgroundTransition: fade
  center: false
  progress: true
  controls: true
  mouseWheel: false
  history: true
  keyboard: true
  overview: true
  touch: true
  fragments: true
  width: 1920
  height: 1080
  margin: 0.05
  minScale: 0.5
  maxScale: 2.0
---



<div class="workshop-header title-slide">

<div class="vanilla-logo">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo">
</div>

# Software-Architektur
## Einführung und Grundlagen

</div>

---

## Agenda Einführung

### Themenblöcke:
* **Was ist Software-Architektur?** <!-- .element: class="fragment" data-fragment-index="1" -->
* **Clean Code Grundlagen** <!-- .element: class="fragment" data-fragment-index="2" -->
* **Fachlichkeit vor Technik** <!-- .element: class="fragment" data-fragment-index="3" -->
* **Design Patterns Motivation** <!-- .element: class="fragment" data-fragment-index="4" -->
* **Refactoring Philosophie** <!-- .element: class="fragment" data-fragment-index="5" -->

### Lernziele:
* Gemeinsames Verständnis von Software-Architektur entwickeln <!-- .element: class="fragment" data-fragment-index="6" -->
* Clean Code Prinzipien verstehen und anwenden <!-- .element: class="fragment" data-fragment-index="7" -->
* Domain-First statt Technology-First Denken etablieren <!-- .element: class="fragment" data-fragment-index="8" -->
* Motivation für Design Patterns verstehen <!-- .element: class="fragment" data-fragment-index="9" -->
* Refactoring als kontinuierlichen Prozess begreifen <!-- .element: class="fragment" data-fragment-index="10" -->

---

# Teil 1: Was ist Software-Architektur?

## Lernziele
* Gemeinsames Verständnis von Software-Architektur <!-- .element: class="fragment" data-fragment-index="1" -->
* Verschiedene Definitionen kennenlernen <!-- .element: class="fragment" data-fragment-index="2" -->
* Enterprise-Kontext verstehen <!-- .element: class="fragment" data-fragment-index="3" -->

---

## Einstiegsfrage

<div class="interactive-question">
<strong>Was ist für Sie Software-Architektur?<br>
3 Begriffe oder Sätze!</strong>

### Typische Antworten:
<!-- .element: class="fragment" data-fragment-index="1" -->

* "Das große Ganze" <!-- .element: class="fragment" data-fragment-index="2" -->
* "Struktur der Software" <!-- .element: class="fragment" data-fragment-index="3" -->
* "Komponenten und deren Beziehungen" <!-- .element: class="fragment" data-fragment-index="4" -->
* "Wie alles zusammenhängt" <!-- .element: class="fragment" data-fragment-index="5" -->
* "Framework-Auswahl" <!-- .element: class="fragment" data-fragment-index="6" -->
* "Datenbanken und Services" <!-- .element: class="fragment" data-fragment-index="7" -->
* "Microservices vs. Monolith" <!-- .element: class="fragment" data-fragment-index="8" -->

Note: Sammeln auf Flipchart oder Chat, nicht bewerten. Diese Antworten sind alle richtig und zeigen die verschiedenen Perspektiven auf Software-Architektur. Lassen Sie jeden Teilnehmer zu Wort kommen. Dauer: 5 Minuten.

---

## Definition 1: IEEE 1471

<strong>Architecture is the fundamental organization of a system, embodied in its components, their relationships to each other and to the environment, and the principles governing its design and evolution.</strong>

**Auf Deutsch:** Software-Architektur ist die grundlegende Organisation eines Systems, verkörpert durch seine Komponenten, deren Beziehungen zueinander und zur Umgebung, sowie die Prinzipien für Design und Evolution.

---

## Definition 2: Martin Fowler

<strong>Architecture is about the important stuff. Whatever that is.</strong>

**Bedeutung:** Architektur befasst sich mit den wichtigen Entscheidungen - aber was wichtig ist, hängt vom Kontext ab.

---

## Definition 3: Grady Booch

<strong>Architecture represents the significant design decisions that shape a system, where significant is measured by cost of change.</strong>

**Bedeutung:** Architektur umfasst die wichtigen Design-Entscheidungen - wichtig sind die, die später schwer zu ändern sind.

---

## Definition 4: Simon Brown

<strong>Software architecture is about structure and vision, creating a shared understanding of the software being built.</strong>

**Bedeutung:** Software-Architektur schafft Struktur und Vision für ein gemeinsames Verständnis.

---

## Gemeinsame Erkenntnisse

### Architektur umfasst:
1. **Struktur** - Wie ist die Software organisiert? <!-- .element: class="fragment" data-fragment-index="1" -->
2. **Entscheidungen** - Welche wichtigen Designentscheidungen wurden getroffen? <!-- .element: class="fragment" data-fragment-index="2" -->
3. **Beziehungen** - Wie hängen die Teile zusammen? <!-- .element: class="fragment" data-fragment-index="3" -->
4. **Kosten** - Was ist später schwer zu ändern? <!-- .element: class="fragment" data-fragment-index="4" -->
5. **Kommunikation** - Wie vermitteln wir unser Design anderen? <!-- .element: class="fragment" data-fragment-index="5" -->

Note: Diese fünf Punkte ergeben sich aus den verschiedenen Definitionen. Betonen Sie, dass alle Definitionen diese Aspekte teilen. Die Kosten-Perspektive von Booch ist besonders wichtig - wichtig sind die Entscheidungen, die schwer zu ändern sind.

---

## Arbeitsdefinition für diesen Workshop

<div class="highlight-box accent">
<strong>Software-Architektur ist die Kunst, wichtige Designentscheidungen zu treffen, die die Struktur, das Verhalten und die Evolution eines Systems bestimmen - mit dem Ziel, fachliche Anforderungen optimal zu erfüllen.</strong>

---

## Enterprise-Kontext

### Besonderheiten in großen Unternehmen:
* **Legacy-Systeme** - Jahrzehntealte Systeme, die noch laufen müssen <!-- .element: class="fragment" data-fragment-index="1" -->
* **Regulatorische Anforderungen** - DSGVO, Compliance-Standards <!-- .element: class="fragment" data-fragment-index="2" -->
* **Hochverfügbarkeit** - 99.9%+ Uptime-Anforderungen <!-- .element: class="fragment" data-fragment-index="3" -->
* **Skalierung** - Millionen von Benutzern, große Datenmengen <!-- .element: class="fragment" data-fragment-index="4" -->
* **Sicherheit** - Kritische Geschäftsdaten, Cyber-Security <!-- .element: class="fragment" data-fragment-index="5" -->
* **Integration** - Dutzende von Systemen müssen zusammenarbeiten <!-- .element: class="fragment" data-fragment-index="6" -->

---

## Architektur-Herausforderungen

### In Enterprises:
1. **Modernisierung bei laufendem Betrieb** <!-- .element: class="fragment" data-fragment-index="1" -->
2. **Compliance und Governance** <!-- .element: class="fragment" data-fragment-index="2" -->
3. **Performance bei hoher Last** <!-- .element: class="fragment" data-fragment-index="3" -->
4. **Kostenoptimierung bei gleichzeitig hoher Qualität** <!-- .element: class="fragment" data-fragment-index="4" -->

<div class="highlight-box accent">
<strong>Kernbotschaft:</strong> Architektur ist nicht nur Technik. Sie muss Geschäftsziele verstehen und unterstützen.
</div>

---

## Diskussion

<div class="interactive-question">
<strong>Reflexionsfragen:</strong><br>
• Was war Ihre wichtigste Architektur-Entscheidung?<br>
• Wann haben Sie gemerkt, dass eine Entscheidung falsch war?<br>
• Was unterscheidet gute von schlechter Software-Architektur?
</div>

Note: Lassen Sie 2-3 Teilnehmer antworten. Diese Fragen leiten über zu Clean Code Grundlagen, wo wir die Qualitätsaspekte vertiefen. Dauer: 5-7 Minuten für diesen Diskussionsblock.

---

# Teil 2: Clean Code Grundlagen

## Lernziele
* Verständnis für sauberen Code <!-- .element: class="fragment" data-fragment-index="1" -->
* Unterschied zwischen clever und lesbar <!-- .element: class="fragment" data-fragment-index="2" -->
* Wartbarkeit als Ziel <!-- .element: class="fragment" data-fragment-index="3" -->
* Technische Schulden vermeiden <!-- .element: class="fragment" data-fragment-index="4" -->

---

## Was bedeutet "sauber"?

<div class="interactive-question">
<strong>Analogie: Beschreiben Sie ein sauberes Büro.<br>
Was macht es sauber?</strong>

### Typische Antworten:
<!-- .element: class="fragment" data-fragment-index="1" -->

* **Aufgeräumt und organisiert** <!-- .element: class="fragment" data-fragment-index="2" -->
* **Alles hat seinen Platz** <!-- .element: class="fragment" data-fragment-index="3" -->
* **Man findet schnell, was man sucht** <!-- .element: class="fragment" data-fragment-index="4" -->
* **Nichts Überflüssiges liegt herum** <!-- .element: class="fragment" data-fragment-index="5" -->
* **Funktional und effizient** <!-- .element: class="fragment" data-fragment-index="6" -->

Note: Diese Analogie hilft beim Verstehen von Clean Code. Sammeln Sie die Antworten und übertragen Sie sie dann auf Code: Organisiert = gut strukturiert, alles hat seinen Platz = richtige Abstraktionen, man findet schnell = lesbare Namen, nichts Überflüssiges = kein Dead Code. Dauer: 5 Minuten.

---

## Clean Code nach Robert C. Martin

<strong>"Clean code is code that has been taken care of. Someone has taken the time to keep it simple and orderly."</strong>

Note: Betonen Sie "taken care of" - jemand hat sich Mühe gegeben. Clean Code ist nicht automatisch entstanden, sondern das Ergebnis bewusster Arbeit. Uncle Bob ist Robert C. Martin, einer der einflussreichsten Softwareentwickler.

---

## Die 5 Grundregeln des Clean Code

### 1. Lesbarkeit vor Cleverness

<h5>Beispiel: Variable Namen</h5>
<pre><code>❌ SCHLECHT: int d; // elapsed time in days
✅ GUT:     int elapsedTimeInDays;

❌ SCHLECHT: if(u.getAge()>18 && u.getAge()<65 && u.isActive())
✅ GUT:     if(user.isEligibleForService())</code></pre>
</div>

---

## 2. Eindeutige Namen

* **Funktionen** - Verben (calculateTax, sendEmail) <!-- .element: class="fragment" data-fragment-index="1" -->
* **Variablen** - Substantive (customerName, orderTotal) <!-- .element: class="fragment" data-fragment-index="2" -->
* **Boolean** - Fragen (isValid, hasPermission) <!-- .element: class="fragment" data-fragment-index="3" -->
* **Konstanten** - Großbuchstaben (MAX_RETRY_COUNT) <!-- .element: class="fragment" data-fragment-index="4" -->

---

## 3. Kurze Funktionen

* Eine Funktion = Ein Gedanke <!-- .element: class="fragment" data-fragment-index="1" -->
* Faustregeln: Max. 20 Zeilen, max. 3 Parameter <!-- .element: class="fragment" data-fragment-index="2" -->
* "Extract Till You Drop" - so lange extrahieren, bis es nicht mehr geht <!-- .element: class="fragment" data-fragment-index="3" -->

---

## 4. Selbstdokumentierender Code

<h5>Kommentare vermeiden</h5>
<pre><code>❌ SCHLECHT: 
// Check if employee is eligible for bonus
if (employee.getYearsOfService() > 5 && employee.getPerformanceRating() > 7)

✅ GUT:
if (employee.isEligibleForBonus())</code></pre>
</div>

---

## 5. Konsistenz

* Gleiche Namenskonventionen <!-- .element: class="fragment" data-fragment-index="1" -->
* Gleiche Formatierung <!-- .element: class="fragment" data-fragment-index="2" -->
* Gleiche Patterns <!-- .element: class="fragment" data-fragment-index="3" -->

---

## Lesbarkeit vs. Cleverness

### Das "Clever Code" Anti-Pattern

#### Clever vs. Lesbar

```java
// Clever, aber unleserlich:
return condition ? value1 : condition2 ? value2 : condition3 ? value3 : defaultValue;

// Lesbar und verständlich:
if (isHighPriorityCustomer()) {
    return PREMIUM_SERVICE_LEVEL;
}
if (isRegularCustomer()) {
    return STANDARD_SERVICE_LEVEL; 
}
if (isTrialCustomer()) {
    return BASIC_SERVICE_LEVEL;
}
return DEFAULT_SERVICE_LEVEL;
```

---

## Warum Lesbarkeit gewinnt

* **Code wird öfter gelesen als geschrieben** <!-- .element: class="fragment" data-fragment-index="1" -->
* Code wird häufiger debuggt als geschrieben <!-- .element: class="fragment" data-fragment-index="2" -->
* Neue Teammitglieder müssen Code verstehen <!-- .element: class="fragment" data-fragment-index="3" -->
* Wartung und Erweiterung brauchen Verständnis <!-- .element: class="fragment" data-fragment-index="4" -->

---

## Wartbarkeit als Ziel

### Software-Lebenszyklus in der Praxis:
* **Entwicklung** - 20% der Gesamtkosten <!-- .element: class="fragment" data-fragment-index="1" -->
* **Wartung** - 80% der Gesamtkosten <!-- .element: class="fragment" data-fragment-index="2" -->

### Wartbarkeits-Faktoren:
1. **Verständlichkeit** - Kann ich verstehen, was der Code macht? <!-- .element: class="fragment" data-fragment-index="3" -->
2. **Änderbarkeit** - Kann ich sicher Änderungen vornehmen? <!-- .element: class="fragment" data-fragment-index="4" -->
3. **Testbarkeit** - Kann ich das Verhalten überprüfen? <!-- .element: class="fragment" data-fragment-index="5" -->
4. **Wiederverwendbarkeit** - Kann ich Teile in anderen Kontexten nutzen? <!-- .element: class="fragment" data-fragment-index="6" -->

### Enterprise-Beispiel:
Legacy-Systeme mit Millionen Zeilen undokumentiertem Code verursachen hohe Wartungskosten, weil jede kleine Änderung Wochen dauert und risikoreich ist.

---

## Technische Schulden

### Definition nach Martin Fowler:
> *"Technical debt is a metaphor referring to the eventual consequences of poor system design, software architecture or software development within a codebase."*

---

### Arten technischer Schulden:

#### 1. **Bewusste Schulden**
* "Wir machen es schnell und dirty, räumen aber nächste Woche auf" <!-- .element: class="fragment" data-fragment-index="1" -->
* Kann strategisch sinnvoll sein (Time-to-Market)

#### 2. **Unbewusste Schulden** 
* Entstehen durch Unwissen oder mangelnde Skills <!-- .element: class="fragment" data-fragment-index="2" -->
* Die gefährlichste Art von Schulden

#### 3. **Umwelt-Schulden**
* Änderung der Anforderungen macht bisherigen Code obsolet <!-- .element: class="fragment" data-fragment-index="3" -->
* Nicht vermeidbar, aber managbar

---

### Praktische Auswirkungen:
* **Zinsen** - Jede Änderung dauert länger <!-- .element: class="fragment" data-fragment-index="1" -->
* **Hauptsumme** - Aufwand für Refactoring <!-- .element: class="fragment" data-fragment-index="2" -->
* **Insolvenz** - System nicht mehr wartbar <!-- .element: class="fragment" data-fragment-index="3" -->

### Schulden-Management:
1. **Sichtbar machen** - Technische Schulden dokumentieren <!-- .element: class="fragment" data-fragment-index="4" -->
2. **Priorisieren** - Welche Schulden kosten am meisten? <!-- .element: class="fragment" data-fragment-index="5" -->
3. **Kontinuierlich abbauen** - Boy Scout Rule <!-- .element: class="fragment" data-fragment-index="6" -->
4. **Neue Schulden vermeiden** - Code Reviews, Standards <!-- .element: class="fragment" data-fragment-index="7" -->

---

## Kernbotschaft
Clean Code ist nicht Perfektionismus. Es ist eine Investition in die Zukunft.

## Diskussionsfragen
1. "Wann haben Sie zuletzt Code gelesen und gedacht: 'Das verstehe ich nicht'?"
2. "Was ist schwieriger: Neuen Code schreiben oder alten Code verstehen?"
3. "Wie erklären Sie Management, warum Clean Code wichtig ist?"

*Diese Grundlagen führen uns zur wichtigsten Erkenntnis: Fachlichkeit muss vor Technik kommen!*

---

# Teil 3: Fachlichkeit vor Technik
**Kern-Message des Workshops**

## Lernziele
* Technology-First Anti-Pattern erkennen <!-- .element: class="fragment" data-fragment-index="1" -->
* Domain-Driven Design Grundgedanke verstehen <!-- .element: class="fragment" data-fragment-index="2" -->
* Richtige Reihenfolge: Fachlichkeit, Architektur, Technologie <!-- .element: class="fragment" data-fragment-index="3" -->
* Typische Fehlentscheidungen als Warnung verstehen

---

## Das häufigste Anti-Pattern: Technology-First

### Typische Aussagen in Projekten:

* "Wir nutzen jetzt Microservices!" - Aber warum? <!-- .element: class="fragment" data-fragment-index="1" -->

* "Lass uns auf Kubernetes umsteigen!" - Aber welches Problem löst das? <!-- .element: class="fragment" data-fragment-index="2" -->

* "Wir brauchen eine Event-Driven Architecture!" - Aber passt das zu unserer Fachlichkeit? <!-- .element: class="fragment" data-fragment-index="3" -->

* "NoSQL ist modern, weg mit der relationalen DB!" - Aber was sind unsere Datenanforderungen? <!-- .element: class="fragment" data-fragment-index="4" -->

**Frage an Sie:**
<!-- .element: class="fragment" data-fragment-index="5" -->

Welche Technologie-Entscheidungen haben Sie erlebt, die ohne klare fachliche Anforderungen getroffen wurden?
<!-- .element: class="fragment" data-fragment-index="6" -->

Wie hätten Sie diese Entscheidungen anders angegangen?
<!-- .element: class="fragment" data-fragment-index="7" -->
<div class="interactive-question">
<strong>Kennen Sie solche Aussagen?<br>
Was war das Ergebnis?</strong>

Note: Lassen Sie 2-3 Teilnehmer ihre Erfahrungen teilen. Oft kommen hier Geschichten von gescheiterten Microservice-Einführungen oder überkomplexen Architekturen heraus. Diese Erfahrungen sind wertvoll für die weiteren Beispiele.

---

### Technology-First Symptome:
* **Hype-Driven Development** - "Das ist modern, das machen wir auch" <!-- .element: class="fragment" data-fragment-index="1" -->
* **Solution looking for a problem** - "Wir haben Kafka, jetzt brauchen wir Events" <!-- .element: class="fragment" data-fragment-index="2" -->
* **Architecture Astronauts** - Komplexe Lösungen für einfache Probleme <!-- .element: class="fragment" data-fragment-index="3" -->
* **Over-Engineering** - 20 Services für 5 Use Cases <!-- .element: class="fragment" data-fragment-index="4" -->

Note: Diese Symptome sind sehr häufig. "Architecture Astronauts" ist ein Begriff von Joel Spolsky für Entwickler, die nur in abstrakten Konzepten denken. Fragen Sie nach konkreten Beispielen aus den Projekten der Teilnehmer.

---

### Warum passiert Technology-First?
1. **Techniker denken technisch** - das ist normal <!-- .element: class="fragment" data-fragment-index="1" -->
2. **Marketing macht Technologie sexy** - Buzzwords verkaufen sich <!-- .element: class="fragment" data-fragment-index="2" -->
3. **CV-Driven Development** - "Ich will Kubernetes lernen" <!-- .element: class="fragment" data-fragment-index="3" -->
4. **Komplexität wirkt professionell** - Einfachheit wird unterschätzt <!-- .element: class="fragment" data-fragment-index="4" -->
5. **Copy-Paste Architecture** - "Netflix macht das so" <!-- .element: class="fragment" data-fragment-index="5" -->

Note: Diese Gründe sind menschlich verständlich, aber gefährlich für Projekte. CV-Driven Development ist besonders in IT-Teams verbreitet. Betonen Sie: Was für Netflix funktioniert, funktioniert nicht automatisch für andere Unternehmen mit anderen Problemen.

---

## Typische Beispiele für Technology-First

### Beispiel 1: Der Microservices-Hype
**Situation:** Monolithische Anwendung funktioniert gut, aber "Microservices sind modern"

**Technology-First Entscheidung:** 
* Monolith in 20+ Services aufteilen <!-- .element: class="fragment" data-fragment-index="1" -->
* Kubernetes-Cluster für alles <!-- .element: class="fragment" data-fragment-index="2" -->
* Event-Sourcing mit Message Queues

**Ergebnis:**
* 3x höhere Komplexität <!-- .element: class="fragment" data-fragment-index="3" -->
* Latenz-Probleme durch Netzwerk-Calls <!-- .element: class="fragment" data-fragment-index="4" -->
* Entwicklungszeit verdoppelt <!-- .element: class="fragment" data-fragment-index="5" -->
* Debugging wird zum Albtraum

**Was war das Problem?** Es gab KEIN fachliches Problem, das Microservices gelöst hätten!

---

### Beispiel 2: Die NoSQL-Modernisierung
**Situation:** Bewährte relationale Datenbank für Finanzdaten

**Technology-First Entscheidung:**
* "NoSQL ist skalierbar und modern" <!-- .element: class="fragment" data-fragment-index="1" -->
* Migration auf Document-Database <!-- .element: class="fragment" data-fragment-index="2" -->
* "Schema-less ist flexibler"

**Ergebnis:**
* Datenkonsistenz-Probleme <!-- .element: class="fragment" data-fragment-index="3" -->
* Komplexe Joins unmöglich <!-- .element: class="fragment" data-fragment-index="4" -->
* Performance schlechter als vorher <!-- .element: class="fragment" data-fragment-index="5" -->
* Migration zurück nach 18 Monaten

**Was war das Problem?** Finanzdaten sind RELATIONAL - NoSQL passte nicht zur Fachlichkeit!

---

### Beispiel 3: Die Event-Driven Everything
**Situation:** Interne Systeme mit synchronen APIs

**Technology-First Entscheidung:**
* "Events sind die Zukunft" <!-- .element: class="fragment" data-fragment-index="1" -->
* Alles asynchron machen <!-- .element: class="fragment" data-fragment-index="2" -->
* Event-Sourcing für alles

**Ergebnis:**
* Eventual Consistency verwirrt Benutzer <!-- .element: class="fragment" data-fragment-index="3" -->
* Debugging unmöglich <!-- .element: class="fragment" data-fragment-index="4" -->
* Race Conditions überall <!-- .element: class="fragment" data-fragment-index="5" -->
* Rollback zu synchronen APIs nach 2 Jahren

**Was war das Problem?** Die Fachlichkeit brauchte SYNCHRONE Konsistenz!

---

## Domain-Driven Design: Fachlichkeit First

### Der DDD-Grundgedanke nach Eric Evans:
> *"The heart of software is its ability to solve domain-related problems for its user. All other concerns, however fascinating, should be subordinated."*

---

### Die richtige Reihenfolge:

#### 1. Fachlichkeit verstehen
* Was ist das Geschäftsproblem? <!-- .element: class="fragment" data-fragment-index="1" -->
* Wie arbeiten die Fachexperten heute? <!-- .element: class="fragment" data-fragment-index="2" -->
* Welche Regeln und Prozesse gibt es? <!-- .element: class="fragment" data-fragment-index="3" -->
* Was sind die echten Anforderungen?

#### 2. Fachliche Architektur entwerfen
* Welche fachlichen Bereiche (Domains) gibt es? <!-- .element: class="fragment" data-fragment-index="4" -->
* Wie schneiden wir die Bereiche (Bounded Contexts)? <!-- .element: class="fragment" data-fragment-index="5" -->
* Welche fachlichen Services brauchen wir? <!-- .element: class="fragment" data-fragment-index="6" -->
* Wie modellieren wir die Geschäftslogik?

#### 3. Technologie auswählen
* Welche Technologie unterstützt unser fachliches Modell am besten? <!-- .element: class="fragment" data-fragment-index="7" -->
* Was löst unsere spezifischen Probleme? <!-- .element: class="fragment" data-fragment-index="8" -->
* Was passt zu unserem Team und unserer Infrastruktur?

---

### Praktisches Beispiel: Kunden-Onboarding
#### Technology-First (falsch):
"Wir bauen eine Event-Driven Microservices-Architektur mit Message Queues und Container-Orchestrierung"

#### Domain-First (richtig):
1. **Fachlichkeit:** Kunde möchte Produkt bestellen → Verfügbarkeit prüfen → Vertrag erstellen → Bereitstellung beauftragen
2. **Fachliche Architektur:** CustomerManagement, ProductCatalog, OrderProcessing, Provisioning
3. **Technologie:** REST APIs reichen, relationale DB für Konsistenz, einfaches Messaging für Entkopplung

---

## Die richtige Herangehensweise

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

---

## Warnsignale für Technology-First

### Aussagen, die Sie alarmieren sollten:
* "Das ist modern/trendy/hip" <!-- .element: class="fragment" data-fragment-index="1" -->
* "Das macht Netflix/Google/Amazon auch" <!-- .element: class="fragment" data-fragment-index="2" -->
* "Das steht in meinem Lebenslauf gut" <!-- .element: class="fragment" data-fragment-index="3" -->
* "Das ist die Zukunft" <!-- .element: class="fragment" data-fragment-index="4" -->
* "Das ist skalierbar" (ohne Nachweis des Skalierungsbedarfs) <!-- .element: class="fragment" data-fragment-index="5" -->
* "Das ist performant" (ohne Performance-Anforderungen)

---

### Die richtigen Fragen stattdessen:
* "Welches fachliche Problem löst das?" <!-- .element: class="fragment" data-fragment-index="1" -->
* "Was sind unsere spezifischen Anforderungen?" <!-- .element: class="fragment" data-fragment-index="2" -->
* "Warum ist die aktuelle Lösung nicht gut genug?" <!-- .element: class="fragment" data-fragment-index="3" -->
* "Was sind die Risiken und Kosten?" <!-- .element: class="fragment" data-fragment-index="4" -->
* "Passt das zu unserem Team und unserer Infrastruktur?"

---

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

*Mit diesem Mindset verstehen wir jetzt, warum Design Patterns entstanden sind: Um bewährte FACHLICHE Lösungen zu dokumentieren!*

---

# Teil 4: Design Patterns Motivation

## Lernziele
* Geschichte der Design Patterns verstehen <!-- .element: class="fragment" data-fragment-index="1" -->
* Gang of Four Kontext kennenlernen <!-- .element: class="fragment" data-fragment-index="2" -->
* Wert einer gemeinsamen Sprache erkennen <!-- .element: class="fragment" data-fragment-index="3" -->
* Patterns als bewährte Lösungen verstehen

---

## Geschichte: Warum entstanden Design Patterns?

### Die Ursprünge: Christopher Alexander (Architektur)
**1977 - "A Pattern Language"** - Nicht Software, sondern Gebäude-Architektur!

#### Alexanders Erkenntnis:
<strong>"Each pattern describes a problem which occurs over and over again in our environment, and then describes the core of the solution to that problem, in such a way that you can use this solution a million times over, without ever doing it the same way twice."</strong>

Note: Das ist die Ursprungsdefinition von Patterns! Alexander war Gebäude-Architekt, kein Software-Architekt. Seine Patterns beschreiben z.B. "Wie gestalte ich einen öffentlichen Platz, damit Menschen sich gerne dort aufhalten." Die Übertragung auf Software war genial.

---

### Übertragung auf Software: Gang of Four (1994)
**Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides**

#### Warum brauchten wir Patterns in der Software?

##### Problem 1: Wiederkehrende Design-Probleme
```text
Scenario: "Ich brauche eine Klasse, aber nur eine Instanz davon"
Lösung: Jeder Entwickler erfindet das Rad neu
Ergebnis: 100 verschiedene "Singleton"-Implementierungen
```

##### Problem 2: Schlechte Kommunikation zwischen Entwicklern
```text
Entwickler A: "Wir brauchen eine abstrakte Schnittstelle, die verschiedene 
Implementierungen kapselt, aber zur Laufzeit austauschbar ist..."

Entwickler B: "Ah, du meinst Strategy Pattern!"
```

---

##### Problem 3: Fehlende Best Practices
* Jeder Entwickler lernt aus eigenen Fehlern <!-- .element: class="fragment" data-fragment-index="1" -->
* Bewährte Lösungen werden nicht geteilt <!-- .element: class="fragment" data-fragment-index="2" -->
* Qualität schwankt stark zwischen Entwicklern

### Gang of Four Erkenntnisse:
1. **Erfahrene Entwickler** nutzen bewährte Lösungen
2. **Wiederkehrende Probleme** haben wiederkehrende Lösungen  
3. **Kommunikation** verbessert sich durch gemeinsame Begriffe
4. **Qualität** steigt durch bewährte Patterns

---

## Warum Design Patterns?

### 1. **Bewährte Lösungen nutzen**
Anstatt das Rad neu zu erfinden, nutzen wir erprobte Lösungen.

#### Praktisches Beispiel: Observer Pattern
**Problem:** Statusänderungen müssen an verschiedene Systeme (Billing, CRM, Analytics) kommuniziert werden.

**Ohne Pattern:** Jeder Service implementiert eigene Notification-Logik
**Mit Pattern:** Observer Pattern - einmal richtig implementiert, überall nutzbar

---

### 2. **Gemeinsame Sprache entwickeln**
Patterns schaffen ein Vokabular für Entwicklungs-Teams.

#### Vorher vs. Nachher:
```text
❌ OHNE PATTERN-SPRACHE:
"Wir brauchen eine Klasse, die andere Klassen erzeugt, 
aber die Entscheidung welche Klasse zur Laufzeit trifft..."

✅ MIT PATTERN-SPRACHE:  
"Wir nutzen Factory Pattern"
```

#### Team-Kommunikation:
* **Architektur-Reviews** werden effizienter <!-- .element: class="fragment" data-fragment-index="1" -->
* **Code-Reviews** fokussieren auf Pattern-Anwendung <!-- .element: class="fragment" data-fragment-index="2" -->
* **Neue Teammitglieder** verstehen Design schneller <!-- .element: class="fragment" data-fragment-index="3" -->
* **Dokumentation** wird kompakter und präziser

---

### 3. **Design-Qualität verbessern**
Patterns kodifizieren gutes objektorientierten Design.

#### Die SOLID-Prinzipien in Patterns:
* **Single Responsibility** - Command Pattern <!-- .element: class="fragment" data-fragment-index="1" -->
* **Open/Closed** - Strategy Pattern   <!-- .element: class="fragment" data-fragment-index="2" -->
* **Liskov Substitution** - Template Method <!-- .element: class="fragment" data-fragment-index="3" -->
* **Interface Segregation** - Adapter Pattern <!-- .element: class="fragment" data-fragment-index="4" -->
* **Dependency Inversion** - Abstract Factory <!-- .element: class="fragment" data-fragment-index="5" -->

---

### 4. **Wartbarkeit erhöhen**
Bekannte Patterns sind einfacher zu verstehen und zu ändern.

#### Vorteile für die Wartung:
* **Vorhersagbare Struktur** - Entwickler wissen, wo sie suchen müssen <!-- .element: class="fragment" data-fragment-index="1" -->
* **Dokumentierte Intentionen** - Pattern-Name erklärt die Absicht <!-- .element: class="fragment" data-fragment-index="2" -->
* **Erprobte Erweiterungspunkte** - Patterns zeigen, wo Änderungen sicher sind <!-- .element: class="fragment" data-fragment-index="3" -->

---

## Patterns sind NICHT...

### Was Patterns NICHT sind:
1. **Silberkugeln** - Patterns lösen nicht alle Probleme <!-- .element: class="fragment" data-fragment-index="1" -->
2. **Dogmen** - Patterns müssen nicht sklavisch befolgt werden <!-- .element: class="fragment" data-fragment-index="2" -->
3. **Komplexität um der Komplexität willen** - Einfache Probleme brauchen einfache Lösungen <!-- .element: class="fragment" data-fragment-index="3" -->
4. **Copy-Paste Code** - Patterns sind konzeptuelle Lösungen, nicht Code-Snippets <!-- .element: class="fragment" data-fragment-index="4" -->

### Pattern-Missbrauch vermeiden:
* **Golden Hammer** - "Ich habe einen Hammer, alles sieht aus wie ein Nagel" <!-- .element: class="fragment" data-fragment-index="5" -->
* **Pattern Overload** - 20 Patterns für 5 Klassen <!-- .element: class="fragment" data-fragment-index="6" -->
* **Premature Patterning** - Patterns einsetzen bevor das Problem klar ist <!-- .element: class="fragment" data-fragment-index="7" -->

---

### Anti-Beispiel aus der Praxis:
```text
Problem: Einfache Konfigurationswerte lesen
Overengineered: AbstractConfigurationFactoryBuilderStrategyProxy
Einfach: Properties.load() oder ähnliche Standard-Lösung
```

---

## Patterns als gemeinsame Sprache

### Kommunikationsvorteile:

#### In Design-Meetings:
```text
SCHLECHT: "Wir nutzen eine Klasse, die sich wie verschiedene andere 
Klassen verhalten kann, je nachdem was übergeben wird..."

GUT: "Wir nutzen Strategy Pattern für die verschiedenen Zahlungsarten"
```

#### In Code-Reviews:
```text
SCHLECHT: "Die Implementierung hier ist komisch verschachtelt..."

GUT: "Das sieht nach einem Decorator Pattern aus, aber ist es richtig angewendet?"
```

---

#### Bei der Einarbeitung:
```text
SCHLECHT: Neue Entwickler: "Wie funktioniert das komplexe System?"

GUT: Mit Patterns: "Das ist MVC mit Observer für Events und Factory für Services"
```

### Enterprise-Vorteile:
* **Internationale Teams** - Patterns sind sprachunabhängig <!-- .element: class="fragment" data-fragment-index="1" -->
* **Verschiedene Abteilungen** - Einheitliches Verständnis <!-- .element: class="fragment" data-fragment-index="2" -->
* **Externe Dienstleister** - Schnelle Einarbeitung <!-- .element: class="fragment" data-fragment-index="3" -->
* **Dokumentation** - Kompakte, präzise Beschreibungen <!-- .element: class="fragment" data-fragment-index="4" -->

---

## Kernbotschaften
1. **Patterns dokumentieren bewährte Lösungen** für wiederkehrende Probleme
2. **Gemeinsame Sprache** verbessert Kommunikation dramatisch
3. **Qualität steigt**, weil wir von Experten-Erfahrung profitieren
4. **Patterns sind Werkzeuge**, nicht Ziele - maßvoll einsetzen

## Wichtige Fragen vor Pattern-Einsatz:
1. **Haben wir wirklich das Problem**, das dieses Pattern löst?
2. **Ist unser Problem komplex genug** für ein Pattern?
3. **Verstehen alle Beteiligten** das Pattern?
4. **Macht es den Code wirklich besser** oder nur komplexer?

---

## Diskussionsfragen
1. "Welche Patterns kennen Sie bereits aus Ihren Projekten (auch ohne den Namen zu wissen)?"
2. "Wo haben Sie schon mal ein Pattern übertrieben eingesetzt?"
3. "Wie erklären Sie einem Junior-Entwickler, wann man Patterns nutzen sollte?"

*Diese solide Basis hilft uns zu verstehen, dass Refactoring der natürliche Weg ist, Patterns einzuführen - nicht anders herum!*

---

# Teil 5: Refactoring Philosophie

## Lernziele
* Refactoring als kontinuierlichen Prozess verstehen <!-- .element: class="fragment" data-fragment-index="1" -->
* Boy Scout Rule kennenlernen und anwenden <!-- .element: class="fragment" data-fragment-index="2" -->
* Wissen, wann und wie refactoriert wird <!-- .element: class="fragment" data-fragment-index="3" -->
* Refactoring als Weg zu Patterns verstehen

---

## Was ist Refactoring?

### Definition nach Martin Fowler:
> *"Refactoring is the process of changing a software system in such a way that it does not alter the external behavior of the code yet improves its internal structure."*

### Schlüsselelemente:
1. **Verhalten bleibt gleich** - Funktionalität ändert sich nicht
2. **Struktur wird besser** - Code wird sauberer, verständlicher, wartbarer
3. **Kleine Schritte** - Viele kleine, sichere Änderungen
4. **Tests als Sicherheitsnetz** - Verhalten wird automatisch überprüft

---

### Was Refactoring NICHT ist:
* Bugfixes (das ändert Verhalten) <!-- .element: class="fragment" data-fragment-index="1" -->
* Neue Features (das erweitert Verhalten)   <!-- .element: class="fragment" data-fragment-index="2" -->
* Performance-Optimierung (das kann Verhalten beeinflussen) <!-- .element: class="fragment" data-fragment-index="3" -->
* Rewrite (zu riskant und zu groß)

---

## Die Boy Scout Rule

### Ursprung: Boy Scouts of America
> *"Try and leave this world a little better than you found it."*

### Übertragung auf Software nach Uncle Bob:
<strong>"Always leave the campground cleaner than you found it."</strong><br><br>
<strong>"Always check a module in cleaner than when you checked it out."</strong>

Note: Die Boy Scout Rule ist ein Kernprinzip für kontinuierliche Qualitätsverbesserung. Uncle Bob (Robert C. Martin) hat diese Regel aus dem Pfadfindertum auf Software übertragen. Es geht nicht um perfekten Code, sondern um kontinuierliche kleine Verbesserungen.

---

### Praktische Anwendung:

#### Bei jedem Code-Touch:
1. **Verstehe** was der Code macht
2. **Verbessere** eine Kleinigkeit  
3. **Prüfe** dass alles noch funktioniert
4. **Committe** die Verbesserung

#### Konkrete Boy Scout Aktionen:
```text
Variable umbenennen (temp → elapsedTimeInDays)
Magic Number extrahieren (7 → DAYS_PER_WEEK)
Long Method aufteilen
Duplicate Code extrahieren
Unused Code entfernen
Kommentare durch self-documenting code ersetzen
```

---

### Praktisches Beispiel:

<h5>Situation: Bug-Fix in alter Service-Klasse</h5>

**Vor Boy Scout Rule:**
* Fix den Bug <!-- .element: class="fragment" data-fragment-index="1" -->
* Code bleibt messy <!-- .element: class="fragment" data-fragment-index="2" -->
* Nächster Entwickler hat gleichen Kampf

**Mit Boy Scout Rule:**
* Fix den Bug   <!-- .element: class="fragment" data-fragment-index="3" -->
* Benenne 2-3 kryptische Variablen um <!-- .element: class="fragment" data-fragment-index="4" -->
* Extrahiere eine lange Methode <!-- .element: class="fragment" data-fragment-index="5" -->
* Nächster Entwickler findet sich besser zurecht <!-- .element: class="fragment" data-fragment-index="6" -->
</div>

Note: Das ist ein sehr realistisches Beispiel. Betonen Sie, dass es nur 5-10 Minuten extra kostet, aber für das Team enormen Mehrwert schafft. Der nächste Entwickler (der Sie selbst sein könnten!) hat es leichter.

---

## Wann refactoren?

### Die "Rule of Three" nach Martin Fowler:
1. **Das erste Mal** - mache es einfach
2. **Das zweite Mal** - ärgere dich über die Duplikation, aber mache es trotzdem
3. **Das dritte Mal** - refactore!

---

### Refactoring-Trigger:

#### 1. **Wenn du Code verstehen musst**
* Bevor du einen Bug fixst <!-- .element: class="fragment" data-fragment-index="1" -->
* Bevor du ein Feature hinzufügst <!-- .element: class="fragment" data-fragment-index="2" -->
* Beim Code Review

#### 2. **Wenn du Duplikation siehst**
* Copy-Paste Code <!-- .element: class="fragment" data-fragment-index="3" -->
* Ähnliche Methoden in verschiedenen Klassen <!-- .element: class="fragment" data-fragment-index="4" -->
* Wiederkehrende Patterns

#### 3. **Wenn Code "riecht" (Code Smells)**
* Long Method (>20 Zeilen) <!-- .element: class="fragment" data-fragment-index="5" -->
* Large Class (>200 Zeilen) <!-- .element: class="fragment" data-fragment-index="6" -->
* Long Parameter List (>3 Parameter) <!-- .element: class="fragment" data-fragment-index="7" -->
* Duplicate Code <!-- .element: class="fragment" data-fragment-index="8" -->
* Comments explaining complex code

---

#### 4. **Beim Hinzufügen von Features**
* Mache den Code erst änderungsfreundlich <!-- .element: class="fragment" data-fragment-index="1" -->
* Füge dann das Feature hinzu <!-- .element: class="fragment" data-fragment-index="2" -->
* "Make the change easy, then make the easy change"

### Gute Refactoring-Gelegenheiten:
* **Sprint Planning** - "Diese Story braucht Refactoring" <!-- .element: class="fragment" data-fragment-index="4" -->
* **Bug-Fixing** - Immer mit Boy Scout Rule kombinieren <!-- .element: class="fragment" data-fragment-index="5" -->
* **Code Reviews** - Verbesserungen vorschlagen und umsetzen <!-- .element: class="fragment" data-fragment-index="6" -->
* **Technische Stories** - Dedicated Refactoring <!-- .element: class="fragment" data-fragment-index="7" -->

---

## Wie refactoren?

### Die Refactoring-Sicherheitsmaßnahmen:

#### 1. **Tests zuerst**
```text
Vor Refactoring:
☐ Verstehe das aktuelle Verhalten
☐ Schreibe Tests für das Verhalten (falls nicht vorhanden)
☐ Alle Tests sind grün

Während Refactoring:
☐ Nach jedem Schritt: Tests laufen lassen
☐ Immer grün bleiben

Nach Refactoring:
☐ Alle Tests noch grün
☐ Neues Verhalten getestet (falls hinzugefügt)
```

---

#### 2. **Kleine Schritte**
```text
SCHLECHT: Alles auf einmal ändern
GUT: Ein Schritt nach dem anderen

Beispiel - Long Method Refactoring:
1. Extrahiere erste Methode → Test
2. Extrahiere zweite Methode → Test  
3. Benenne Parameter um → Test
4. Extrahiere dritte Methode → Test
```

#### 3. **IDE-Unterstützung nutzen**
Moderne IDEs können viel automatisch und sicher machen:
* Extract Method <!-- .element: class="fragment" data-fragment-index="1" -->
* Rename Variable/Method/Class <!-- .element: class="fragment" data-fragment-index="2" -->
* Move Method/Field <!-- .element: class="fragment" data-fragment-index="3" -->
* Inline Variable/Method

---

### Refactoring-Patterns (häufige Operationen):

#### Extract Method
```text
// Vorher: Long Method
public void processOrder(Order order) {
    // validate order (10 lines)
    // calculate shipping (15 lines) 
    // update inventory (12 lines)
    // send confirmation (8 lines)
}

// Nachher: Mehrere fokussierte Methoden
public void processOrder(Order order) {
    validateOrder(order);
    calculateShipping(order);
    updateInventory(order);
    sendConfirmation(order);
}
```

---

#### Replace Magic Number with Named Constant
```text
// Vorher
if (customer.getOrderCount() > 10) { ... }

// Nachher  
private static final int PREMIUM_CUSTOMER_THRESHOLD = 10;
if (customer.getOrderCount() > PREMIUM_CUSTOMER_THRESHOLD) { ... }
```

---

## Refactoring als Weg zu Patterns

### Patterns entstehen durch Refactoring:

#### Typischer Ablauf:
1. **Duplikation** entsteht natürlich
2. **Refactoring** macht Gemeinsamkeiten sichtbar
3. **Pattern Recognition** - "Das ist ein bekanntes Problem"
4. **Pattern Application** - Bekannte Lösung anwenden

---

### Beispiel: Strategy Pattern durch Refactoring

#### Evolution:
```text
1. Ein if-else für Zahlungsarten
2. Zweites if-else an anderer Stelle
3. Duplikation durch Extract Method reduzieren
4. Ähnliches Pattern an dritter Stelle
5. Erkennung: "Das ist Strategy Pattern!"
6. Refactoring zu Strategy Pattern
```

### Wichtige Erkenntnis:
<div class="highlight-box warning">
<strong>Patterns werden durch Refactoring eingeführt, nicht von Anfang an geplant!</strong>

Note: Das ist eine der wichtigsten Botschaften des gesamten Workshops! Viele Entwickler versuchen, Patterns von Anfang an zu planen, aber das führt oft zu Over-Engineering. Patterns entstehen natürlich durch evolutionäre Verbesserung des Codes.

---

## Refactoring-Fallen vermeiden

### Häufige Fehler:
1. **Big Bang Refactoring** - Alles auf einmal ändern <!-- .element: class="fragment" data-fragment-index="1" -->
2. **Refactoring ohne Tests** - Kein Sicherheitsnetz <!-- .element: class="fragment" data-fragment-index="2" -->
3. **Perfectionism** - Endlos optimieren <!-- .element: class="fragment" data-fragment-index="3" -->
4. **Wrong Context** - Refactoring unter Druck <!-- .element: class="fragment" data-fragment-index="4" -->
5. **Changing Behavior** - Versehentlich Verhalten ändern <!-- .element: class="fragment" data-fragment-index="5" -->

### Enterprise-Kontext:
* **Legacy Systems** - Extra vorsichtig, mehr Tests <!-- .element: class="fragment" data-fragment-index="6" -->
* **Live Systems** - Graduelle Änderungen <!-- .element: class="fragment" data-fragment-index="7" -->
* **Compliance** - Dokumentation der Änderungen <!-- .element: class="fragment" data-fragment-index="8" -->
* **Team Size** - Koordination bei großen Teams <!-- .element: class="fragment" data-fragment-index="9" -->

---

## Kernbotschaften
1. **Refactoring ist kontinuierlich**, nicht ein einmaliges Event
2. **Boy Scout Rule** macht Refactoring zur Gewohnheit
3. **Kleine, sichere Schritte** sind besser als große Sprünge
4. **Patterns entstehen durch Refactoring**, nicht durch Planung
5. **Tests sind essenziell** für sicheres Refactoring

## Diskussionsfragen
1. "Wann haben Sie zuletzt Boy Scout Rule angewendet?"
2. "Was hindert Sie daran, kontinuierlich zu refactoren?"
3. "Wie überzeuge ich Management, Refactoring zu unterstützen?"
4. "Welche Tools nutzen Sie für sicheres Refactoring?"

*Mit diesem Refactoring-Mindset sind wir bereit für den eigentlichen Workshop - wir wissen jetzt, dass wir Patterns durch evolutionäre Verbesserung entdecken!*

