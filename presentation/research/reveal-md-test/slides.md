# Design Patterns Workshop

## Telekom Architecture Training

<!-- .element: class="fragment" -->

Note: Willkommen zum Design Patterns Workshop. Dieser Workshop ist Teil des Telekom Architecture Trainings und fokussiert sich auf bewährte Entwurfsmuster in der Softwareentwicklung.

---

# Workshop Agenda

Note: In diesem Abschnitt werden wir die Agenda des Workshops durchgehen und einen Überblick über die zu behandelnden Themen geben.

---

# Design Patterns in der Softwareentwicklung

Design Patterns sind bewährte Lösungsschablonen für wiederkehrende Entwurfsprobleme in der Softwareentwicklung. 

<!-- .element: class="fragment" -->

Sie beschreiben die Kommunikation zwischen Objekten und Klassen, die angepasst werden, um ein allgemeines Entwurfsproblem in einem bestimmten Kontext zu lösen.

<!-- .element: class="fragment" -->

Note: Diese Folie führt das Konzept der Design Patterns ein. Wir erklären, was Patterns sind und warum sie wichtig sind.

---

## Patterns Übersicht

<div class="two-columns">
<div class="left-column">

### Erstellungsmuster:
- Singleton <!-- .element: class="fragment" -->
- Factory Method <!-- .element: class="fragment" -->
- Abstract Factory <!-- .element: class="fragment" -->
- Builder <!-- .element: class="fragment" -->
- Prototype <!-- .element: class="fragment" -->

</div>
<div class="right-column">

### Strukturmuster:
- Adapter <!-- .element: class="fragment" -->
- Decorator <!-- .element: class="fragment" -->
- Facade <!-- .element: class="fragment" -->
- Composite <!-- .element: class="fragment" -->
- Proxy <!-- .element: class="fragment" -->

</div>
</div>

<style>
.two-columns { display: flex; gap: 2em; }
.left-column, .right-column { flex: 1; }
</style>

Note: Hier sehen wir eine Übersicht der wichtigsten Design Patterns, die wir in diesem Workshop behandeln werden.

---

## Praxisbeispiele

<div class="split-content">
<div class="text-content">

Patterns in der Praxis:

- MVC Architecture Pattern <!-- .element: class="fragment" -->
- Dependency Injection <!-- .element: class="fragment" -->
- Repository Pattern <!-- .element: class="fragment" -->
- Observer für Event Handling <!-- .element: class="fragment" -->
- Strategy für Algorithmus-Auswahl <!-- .element: class="fragment" -->

</div>
<div class="image-content">

![Code visualization](https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400)

</div>
</div>

<style>
.split-content { display: flex; gap: 2em; align-items: center; }
.text-content, .image-content { flex: 1; }
.image-content img { width: 100%; height: auto; border-radius: 8px; }
</style>

Note: Diese Folie zeigt konkrete Beispiele, wie Design Patterns in realen Softwareprojekten eingesetzt werden.

---

## Singleton Pattern

<div class="code-explanation">
<div class="text-section">

Das Singleton Pattern stellt sicher, dass eine Klasse nur eine Instanz hat.

<!-- .element: class="fragment" -->

- Zentrale Ressourcenverwaltung
- Thread-sichere Implementierung  
- Lazy Loading Unterstützung

<!-- .element: class="fragment" -->

</div>
<div class="code-section">

```javascript
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

<style>
.code-explanation { display: flex; gap: 2em; }
.text-section, .code-section { flex: 1; }
.code-section pre { font-size: 0.7em; }
</style>

Note: Das Singleton Pattern ist eines der am häufigsten verwendeten Design Patterns.