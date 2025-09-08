---
theme: default
highlighter: shiki
lineNumbers: false
transition: slide-left
title: Design Patterns Workshop
fonts:
  sans: 'Open Sans'
---

# Design Patterns Workshop

## Telekom Architecture Training

<div class="abs-br m-6 flex gap-2">
  <a href="https://vanilla-core.com" target="_blank" alt="CORE Logo" class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    CORE
  </a>
</div>

<!--
Willkommen zum Design Patterns Workshop. Dieser Workshop ist Teil des Telekom Architecture Trainings und fokussiert sich auf bewährte Entwurfsmuster in der Softwareentwicklung.
-->

---

# Workshop Agenda

<div class="abs-br m-6 flex gap-2">
  <button class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    CORE
  </button>
</div>

<!--
In diesem Abschnitt werden wir die Agenda des Workshops durchgehen und einen Überblick über die zu behandelnden Themen geben.
-->

---

# Design Patterns in der Softwareentwicklung

Design Patterns sind bewährte Lösungsschablonen für wiederkehrende Entwurfsprobleme in der Softwareentwicklung. Sie beschreiben die Kommunikation zwischen Objekten und Klassen, die angepasst werden, um ein allgemeines Entwurfsproblem in einem bestimmten Kontext zu lösen.

Jedes Pattern beschreibt ein Problem, das immer wieder in unserer Umgebung auftritt, und dann den Kern der Lösung zu diesem Problem, auf eine Weise, dass Sie diese Lösung millionenfach anwenden können, ohne sie jemals auf die gleiche Weise zu implementieren.

<div class="abs-br m-6 opacity-30">
  CORE
</div>

<!--
Diese Folie führt das Konzept der Design Patterns ein. Wir erklären, was Patterns sind und warum sie wichtig sind.
-->

---
layout: two-cols
---

# Patterns Übersicht

<template v-slot:default>

### Erstellungsmuster:
- Singleton
- Factory Method
- Abstract Factory  
- Builder
- Prototype

</template>
<template v-slot:right>

### Strukturmuster:
- Adapter
- Decorator
- Facade
- Composite
- Proxy

</template>

<div class="abs-br m-6 opacity-30">
  CORE
</div>

<!--
Hier sehen wir eine Übersicht der wichtigsten Design Patterns, die wir in diesem Workshop behandeln werden.
-->

---
layout: image-right
image: https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400
---

# Praxisbeispiele

Patterns in der Praxis:

- MVC Architecture Pattern
- Dependency Injection
- Repository Pattern
- Observer für Event Handling
- Strategy für Algorithmus-Auswahl
- Command für Undo-Funktionalität
- Template Method für Workflows

<div class="abs-br m-6 opacity-30">
  CORE
</div>

<!--
Diese Folie zeigt konkrete Beispiele, wie Design Patterns in realen Softwareprojekten eingesetzt werden.
-->

---

# Singleton Pattern

<div class="grid grid-cols-2 gap-4">

<div>

Das Singleton Pattern stellt sicher, dass eine Klasse nur eine Instanz hat und bietet einen globalen Zugriffspunkt darauf.

- Zentrale Ressourcenverwaltung
- Thread-sichere Implementierung
- Lazy Loading Unterstützung
- Einfache `getInstance()` Methode

</div>

<div>

```javascript {1|3-6|8-12|14-16}
// Singleton Pattern in JavaScript
class DatabaseConnection {
  static instance = null;
  
  constructor() {
    if (DatabaseConnection.instance) {
      return DatabaseConnection.instance;
    }
    
    this.connection = null;
    this.isConnected = false;
    DatabaseConnection.instance = this;
  }
  
  static getInstance() {
    if (!this.instance) {
      this.instance = new DatabaseConnection();
    }
    return this.instance;
  }
}

// Verwendung
const db1 = DatabaseConnection.getInstance();
const db2 = DatabaseConnection.getInstance();
console.log(db1 === db2); // true
```

</div>

</div>

<div class="abs-br m-6 opacity-30">
  CORE
</div>

<!--
Das Singleton Pattern ist eines der am häufigsten verwendeten Design Patterns.
-->