---
marp: true
theme: telekom-theme
paginate: false
backgroundColor: white
---

<!-- _class: title-slide -->

![CORE Logo](./assets/img/VanillaCore_Vertical.png)

# Design Patterns Workshop

## Telekom Architecture Training

<!--
Willkommen zum Design Patterns Workshop. Dieser Workshop ist Teil des Telekom Architecture Trainings und fokussiert sich auf bewährte Entwurfsmuster in der Softwareentwicklung.
-->

---

<!-- _class: section-slide -->

![CORE Logo](./assets/img/VanillaCore_Vertical.png)

# Workshop Agenda

<!--
In diesem Abschnitt werden wir die Agenda des Workshops durchgehen und einen Überblick über die zu behandelnden Themen geben.
-->

---

<!-- _class: content-slide single-column -->

![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Design Patterns in der Softwareentwicklung

Design Patterns sind bewährte Lösungsschablonen für wiederkehrende Entwurfsprobleme in der Softwareentwicklung. Sie beschreiben die Kommunikation zwischen Objekten und Klassen, die angepasst werden, um ein allgemeines Entwurfsproblem in einem bestimmten Kontext zu lösen.

Jedes Pattern beschreibt ein Problem, das immer wieder in unserer Umgebung auftritt, und dann den Kern der Lösung zu diesem Problem, auf eine Weise, dass Sie diese Lösung millionenfach anwenden können, ohne sie jemals auf die gleiche Weise zu implementieren.

Patterns fördern die Wiederverwendung von bewährtem Design und Architekturen. Sie schaffen eine gemeinsame Sprache für Entwickler und machen komplexe Designentscheidungen für alle Teammitglieder nachvollziehbar.

<!--
Diese Folie führt das Konzept der Design Patterns ein. Wir erklären, was Patterns sind und warum sie wichtig sind. Die Definition stammt aus dem berühmten "Gang of Four" Buch, dem Grundlagenwerk für Design Patterns.
-->

---

<!-- _class: content-slide single-column -->

![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Workshop Ziele

- Design Patterns verstehen und anwenden
- Bewährte Lösungsmuster für häufige Probleme erkennen
- Code-Qualität und Wartbarkeit verbessern
- Kommunikation zwischen Entwicklern fördern
- Praktische Anwendung in realen Projekten

<!--
Diese Folie zeigt die Hauptziele unseres Design Patterns Workshops. Wir fokussieren uns darauf, wie Patterns dabei helfen, besseren und wartbareren Code zu schreiben.
-->

---

<!-- _class: content-slide two-columns -->

![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Patterns Übersicht

<div class="columns-container">
<div class="column">

### Erstellungsmuster:
- Singleton
- Factory Method
- Abstract Factory
- Builder
- Prototype

</div>
<div class="column">

### Strukturmuster:
- Adapter
- Decorator
- Facade
- Composite
- Proxy

</div>
</div>

<!--
Hier sehen wir eine Übersicht der wichtigsten Design Patterns, die wir in diesem Workshop behandeln werden. Zunächst erscheinen nur die Überschriften und ersten Einträge, dann werden die Patterns nach und nach sichtbar. Links die Creational Patterns für Objekterzeugung, rechts die Structural Patterns für Objektkomposition.
-->

---

<!-- _class: content-slide half-picture -->

![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

<div class="text-content">

## Praxisbeispiele

Patterns in der Praxis:

- MVC Architecture Pattern
- Dependency Injection
- Repository Pattern
- Observer für Event Handling
- Strategy für Algorithmus-Auswahl
- Command für Undo-Funktionalität
- Template Method für Workflows

</div>

<div class="image-content">

![Code architecture visualization](./assets/img/VanillaCore_Vertical.png)

</div>

<!--
Diese Folie zeigt konkrete Beispiele, wie Design Patterns in realen Softwareprojekten eingesetzt werden. Jedes Pattern löst spezifische Probleme in der Architektur und im Design.
-->

---

<!-- _class: content-slide code-embedded -->

![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Singleton Pattern

<div class="code-embedded">
<div class="text-section">

Das Singleton Pattern stellt sicher, dass eine Klasse nur eine Instanz hat und bietet einen globalen Zugriffspunkt darauf.

- Zentrale Ressourcenverwaltung
- Thread-sichere Implementierung
- Lazy Loading Unterstützung
- Einfache `getInstance()` Methode

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
  
  connect() {
    if (!this.isConnected) {
      this.connection = createConnection();
      this.isConnected = true;
    }
    return this.connection;
  }
}

// Verwendung
const db1 = DatabaseConnection.getInstance();
const db2 = DatabaseConnection.getInstance();
console.log(db1 === db2); // true
```

</div>
</div>

<!--
Das Singleton Pattern ist eines der am häufigsten verwendeten Design Patterns. Es eignet sich besonders für Datenbankverbindungen, Logger oder Konfigurationsobjekte, wo nur eine Instanz existieren soll.
-->

---

<!-- _class: content-slide code-standalone -->

![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Factory Method Pattern

```java
// Factory Method Pattern in Java
public abstract class NotificationFactory {
    
    // Factory Method
    public abstract Notification createNotification();
    
    // Template Method
    public void processNotification(String message) {
        Notification notification = createNotification();
        notification.setMessage(message);
        notification.send();
        notification.log();
    }
}

public class EmailNotificationFactory extends NotificationFactory {
    @Override
    public Notification createNotification() {
        return new EmailNotification();
    }
}

public class SMSNotificationFactory extends NotificationFactory {
    @Override
    public Notification createNotification() {
        return new SMSNotification();
    }
}

// Interface für alle Notification-Typen
public interface Notification {
    void setMessage(String message);
    void send();
    void log();
}

// Verwendung
NotificationFactory emailFactory = new EmailNotificationFactory();
emailFactory.processNotification("Willkommen zum Training!");

NotificationFactory smsFactory = new SMSNotificationFactory();
smsFactory.processNotification("Training beginnt in 5 Minuten");
```

*Factory Method entkoppelt die Objekterzeugung vom Client-Code*

<!--
Das Factory Method Pattern ist ein Creational Pattern, das eine Schnittstelle für die Erzeugung von Objekten definiert, aber den Subklassen die Entscheidung überlässt, welche Klasse instanziiert werden soll. Sehr nützlich für Plugin-Architekturen.
-->

---

<!-- _class: content-slide code-embedded -->

![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Observer Pattern

<div class="code-embedded">
<div class="text-section">

Das Observer Pattern definiert eine Abhängigkeit zwischen Objekten, sodass bei Änderung eines Objekts alle abhängigen Objekte automatisch benachrichtigt werden.

- Lose Kopplung zwischen `Subject` und `Observer`
- Event-driven Architektur
- Unterstützung für `subscribe()` und `unsubscribe()`
- Automatische Benachrichtigungen via `notify()`

*Ideal für GUI-Events, Model-View-Architekturen und Messaging-Systeme.*

</div>
<div class="code-section">

```javascript
// Observer Pattern Implementation
class Subject {
  constructor() {
    this.observers = [];
  }
  
  subscribe(observer) {
    this.observers.push(observer);
  }
  
  unsubscribe(observer) {
    const index = this.observers.indexOf(observer);
    if (index > -1) {
      this.observers.splice(index, 1);
    }
  }
  
  notify(data) {
    this.observers.forEach(observer => {
      observer.update(data);
    });
  }
}

class Observer {
  constructor(name) {
    this.name = name;
  }
  
  update(data) {
    console.log(`${this.name} received:`, data);
  }
}

// Verwendung
const newsPublisher = new Subject();
const subscriber1 = new Observer("Email Service");
const subscriber2 = new Observer("Push Notification");

newsPublisher.subscribe(subscriber1);
newsPublisher.subscribe(subscriber2);
newsPublisher.notify("Breaking: New Design Pattern!");
```

</div>
</div>

<!--
Das Observer Pattern ist fundamental für event-driven Programming. Es findet sich in fast allen modernen Frameworks wieder - von DOM Events in JavaScript bis zu Reactive Programming mit RxJS oder Spring's ApplicationEvent System.
-->

---

<!-- _class: full-picture -->

<div class="placeholder-image warehouse" aria-label="Software architecture blueprint"></div>

<div class="text-overlay">

## Bessere Software
Durch bewährte Patterns

</div>

<!--
Design Patterns helfen uns dabei, bessere Software zu entwickeln. Sie bieten bewährte Lösungen für wiederkehrende Probleme und verbessern die Kommunikation zwischen Entwicklern.
-->