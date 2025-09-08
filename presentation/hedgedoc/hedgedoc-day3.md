---
title: Design Patterns Workshop - Tag 3
description: Verhaltensmuster - Observer, Strategy, Command, Template Method, Iterator, Chain of Responsibility
tags: design-patterns, workshop,  architecture, training, day3, behavioral, observer, strategy, command, template-method, iterator, chain-of-responsibility
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
  width: 1024
  height: 768
  margin: 0.1
  minScale: 0.5
  maxScale: 2.0
---

<style>
/* HedgeDoc Presentation Styles */
.reveal {
  font-family: 'Open Sans', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-weight: 300;
}

/* Critical: Fix content overflow */
.reveal .slides {
  font-size: 18px !important; /* Further reduced for better fit */
  line-height: 1.3 !important;
}

.reveal .slides section {
  height: 100%;
  width: 100%;
  overflow-y: auto !important; /* Allow scrolling if needed */
  overflow-x: hidden;
  padding: 20px !important; /* Add padding to prevent edge cutoff */
  box-sizing: border-box;
}

.reveal h1 {
  font-size: 1.8em !important; /* Smaller headers */
  color: #2c2c2c;
  font-weight: 600 !important;
}

.reveal h2 {
  font-size: 1.4em !important;
  color: #2c2c2c;
  font-weight: 500 !important;
}

.reveal h3 {
  font-size: 1.2em !important;
  font-weight: 400 !important;
}

.reveal h4, .reveal h5, .reveal h6 {
  font-weight: 400 !important;
}

.reveal p, .reveal li {
  font-weight: 300 !important;
}

/* Prevent text from being too large */
.reveal .slides {
  max-width: 100%;
  max-height: 100%;
}

/* Lists should not overflow */
.reveal ul, .reveal ol {
  max-width: 90%;
  margin-left: 0 !important;
  padding-left: 1.5em !important;
  list-style-type: none;
}

.reveal ul li::before {
  content: "▸";
  color: #666666;
  font-weight: 400;
  display: inline-block;
  width: 1em;
  margin-left: -1em;
}

/* Code blocks sizing */
.reveal pre {
  font-size: 0.6em !important; /* Smaller code blocks */
  max-height: 500px;
  overflow: auto !important;
}

.reveal .two-column {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
}

.reveal .two-column > div {
  flex: 1;
}

.workshop-header {
  text-align: center;
  background: linear-gradient(135deg, #2c2c2c 0%, #4a4a4a 100%);
  color: white;
  padding: 40px;
  margin: -20px;
  border-radius: 8px;
}

.workshop-header h1,
.workshop-header h2 {
  color: white;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.pattern-definition {
  background-color: #F5F5F5;
  border-left: 4px solid #666666;
  padding: 20px;
  margin: 20px 0;
  border-radius: 4px;
}

.highlight-box {
  background-color: #F5F5F5;
  border-left: 4px solid #666666;
  padding: 20px;
  margin: 20px 0;
  border-radius: 4px;
}

.highlight-box.warning {
  border-left-color: #ff9800;
  background-color: #fff3e0;
}

.highlight-box.accent {
  border-left-color: #4a4a4a;
  background-color: #F5F5F5;
}

.code-example {
  background: #2d3748;
  color: #e2e8f0;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
}

.code-example h5 {
  color: #666666;
  margin-top: 0;
}

.interactive-question {
  background-color: #F5F5F5;
  border: 2px solid #666666;
  padding: 20px;
  margin: 20px 0;
  border-radius: 8px;
  text-align: center;
}

.progress-indicator {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
}

.progress-step {
  padding: 10px 15px;
  border-radius: 20px;
  font-weight: 400;
}

.progress-step.completed {
  background-color: #2c2c2c;
  color: white;
}

.progress-step.current {
  background-color: #ff9800;
  color: white;
}

.progress-step.pending {
  background-color: #F5F5F5;
  color: #666666;
}

.reveal .fragment.highlight-green {
  color: #2c2c2c;
  font-weight: 600;
}

.reveal .fragment.highlight-red {
  color: #666666;
  font-style: italic;
}

@media screen and (max-width: 768px) {
  .reveal .two-column {
    flex-direction: column;
    gap: 20px;
  }
}
/* Hide Speaker Notes in Presentation Mode */
.speaker-notes {
  display: none !important;
}

/* Hide reveal.js notes */
.reveal .notes {
  display: none !important;
}
</style>

<div class="workshop-header">

# Design Patterns Workshop - Tag 3
## Verhaltensmuster (Behavioral Patterns)

</div>

<!-- Speaker Notes: Willkommen zum dritten Tag unserer Design Patterns Workshop-Reihe. Heute fokussieren wir uns auf Verhaltensmuster - die komplexesten und mächtigsten Design Patterns. Diese Muster definieren, wie Objekte miteinander interagieren und kommunizieren. -->

---

# Agenda Tag 3

<div class="progress-indicator">
<div class="progress-step current">📍 Observer Pattern - Event-getriebene Architektur</div>
<div class="progress-step pending">⏳ Strategy Pattern - Algorithmus-Austauschbarkeit</div>
<div class="progress-step pending">⏳ Command Pattern - Aktion-Kapselung und Undo</div>
<div class="progress-step pending">⏳ Template Method, Iterator, Chain of Responsibility</div>
<div class="progress-step pending">⏳ Praktische Übungen</div>
</div>

- **Observer Pattern** - Event-getriebene Architektur <!-- .element: class="fragment" -->
- **Strategy Pattern** - Algorithmus-Austauschbarkeit <!-- .element: class="fragment" -->
- **Command Pattern** - Aktion-Kapselung und Undo <!-- .element: class="fragment" -->
- **Template Method Pattern** - Algorithmus-Skelett <!-- .element: class="fragment" -->
- **Iterator Pattern** - Sequenzielle Zugriffe <!-- .element: class="fragment" -->
- **Chain of Responsibility** - Anfragen-Weiterleitung <!-- .element: class="fragment" -->
- **Praktische Übungen** - Hands-on Implementation <!-- .element: class="fragment" -->

<!-- Speaker Notes: Verhaltensmuster sind besonders wichtig für Event-getriebene Systeme, Microservices und moderne Architekturen. Sie ermöglichen lose Kopplung und flexible Kommunikation zwischen Komponenten. -->

---

# Verhaltensmuster Übersicht

<div class="pattern-definition">

#### Was sind Verhaltensmuster?
**Intent**: Fokussieren sich auf Kommunikation zwischen Objekten und die Verteilung von Verantwortlichkeiten.

**Problem**: Objekte müssen flexibel interagieren ohne fest gekoppelt zu sein.

**Solution**: Bewährte Patterns für Objektkommunikation, Algorithmuskapselung und lose Kopplung.

</div>

- **Fokus auf Kommunikation** - Objektinteraktion optimieren <!-- .element: class="fragment" -->
- **Verantwortlichkeits-Verteilung** - Wer macht was, wann? <!-- .element: class="fragment" -->
- **Algorithmus-Kapselung** - Verhalten abstrahieren <!-- .element: class="fragment" -->
- **Lose Kopplung** - Minimale Abhängigkeiten <!-- .element: class="fragment" -->
- **Flexibilität zur Laufzeit** - Dynamische Verhaltensänderung <!-- .element: class="fragment" -->

<!-- Speaker Notes: Im Gegensatz zu Erzeugungsmustern (Objekterstellung) und Strukturmustern (Objektkomposition) befassen sich Verhaltensmuster mit der Art und Weise, wie Objekte miteinander interagieren und Verantwortlichkeiten verteilen. -->

---

# Observer Pattern

<div class="pattern-definition">

#### Observer Pattern - Das Event-System Fundament
**Intent**: Definiert eine eins-zu-viele Abhängigkeit zwischen Objekten, so dass bei Änderung eines Objekts alle abhängigen automatisch benachrichtigt werden.

**Problem**: Objekte sollen über Änderungen informiert werden, ohne fest gekoppelt zu sein.

**Solution**: Subject verwaltet Observer-Liste und benachrichtigt alle bei Zustandsänderungen.

</div>

<div class="two-column">
<div>

## Key Concepts
- **One-to-Many Abhängigkeit** - Ein Subject, viele Observer <!-- .element: class="fragment" -->
- **Automatische Benachrichtigung** - Push-basierte Updates <!-- .element: class="fragment" -->
- **Lose Kopplung** - Subject kennt Observer nicht konkret <!-- .element: class="fragment" -->
- **Event-getriebene Architektur** - Basis für reactive Systems <!-- .element: class="fragment" -->
- **Pub/Sub Implementierung** - Publisher-Subscriber Pattern <!-- .element: class="fragment" -->

</div>
<div>

## Use Cases
- Event-Handling-Systeme <!-- .element: class="fragment" -->
- Model-View Architekturen (MVC, MVVM) <!-- .element: class="fragment" -->
- Notification Services <!-- .element: class="fragment" -->
- Real-time Data Updates <!-- .element: class="fragment" -->
- Reactive Programming <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Observer Pattern ist das Fundament moderner Event-Systeme. In JavaScript ist es die Basis für DOM Events, Node.js EventEmitter und reaktive Programmierung mit RxJS. -->

---

# Observer Pattern - Implementation

<div class="code-example">
<h5>Observer Pattern Implementation</h5>

```javascript
// Observer Pattern Implementation
class Subject { // fragment
  constructor() { // fragment
    this.observers = []; // fragment
  } // fragment
  
  subscribe(observer) { // fragment
    this.observers.push(observer); // fragment
  } // fragment
  
  unsubscribe(observer) { // fragment
    this.observers = this.observers.filter(obs => obs !== observer); // fragment
  } // fragment
  
  notify(data) { // fragment
    this.observers.forEach(observer => observer.update(data)); // fragment
  } // fragment
} // fragment

// News System Example
class NewsAgency extends Subject { // fragment
  constructor() { // fragment
    super(); // fragment
    this.news = ""; // fragment
  } // fragment
  
  setNews(news) { // fragment
    this.news = news; // fragment
    this.notify(news); // fragment
  } // fragment
  
  getNews() { // fragment
    return this.news; // fragment
  } // fragment
} // fragment

class NewsChannel { // fragment
  constructor(name) { // fragment
    this.name = name; // fragment
  } // fragment
  
  update(news) { // fragment
    console.log(`${this.name} received: ${news}`); // fragment
    this.broadcast(news); // fragment
  } // fragment
  
  broadcast(news) { // fragment
    console.log(`${this.name} broadcasting: ${news}`); // fragment
  } // fragment
} // fragment

// Verwendung
const agency = new NewsAgency(); // fragment
const cnn = new NewsChannel("CNN"); // fragment
const bbc = new NewsChannel("BBC"); // fragment

agency.subscribe(cnn); // fragment
agency.subscribe(bbc); // fragment
agency.setNews("Breaking: Design Patterns Workshop starts!"); // fragment
```

</div>

<!-- Speaker Notes: Dieses Beispiel zeigt die Macht des Observer Patterns: Eine Nachrichtenagentur kann beliebig viele Kanäle benachrichtigen, ohne jeden Kanal explizit zu kennen. Neue Kanäle können zur Laufzeit hinzugefügt werden. -->

---

# Strategy Pattern

<div class="pattern-definition">

#### Strategy Pattern - Algorithmus-Austauschbarkeit zur Laufzeit
**Intent**: Definiert eine Familie von Algorithmen, kapselt jeden einzeln und macht sie austauschbar.

**Problem**: Verschiedene Algorithmen sollen zur Laufzeit ausgewählt werden können.

**Solution**: Context delegiert an Strategy-Objekte, die Algorithmen implementieren.

</div>

<div class="two-column">
<div>

## Key Concepts
- **Familie von Algorithmen** - Verschiedene Implementierungen <!-- .element: class="fragment" -->
- **Kapselung von Verhalten** - Strategy-Objekte isoliert <!-- .element: class="fragment" -->
- **Laufzeit-Austausch** - Dynamische Strategie-Wahl <!-- .element: class="fragment" -->
- **Open/Closed Prinzip** - Neue Strategien ohne Änderungen <!-- .element: class="fragment" -->
- **Context-Delegation** - Context nutzt Strategy <!-- .element: class="fragment" -->

</div>
<div>

## Use Cases
- Payment Processing Systems <!-- .element: class="fragment" -->
- Sorting Algorithms <!-- .element: class="fragment" -->
- Data Compression Methods <!-- .element: class="fragment" -->
- Authentication Strategies <!-- .element: class="fragment" -->
- Tax Calculation Systems <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Strategy Pattern eliminiert komplexe if-else Strukturen und macht Code erweiterbar. In E-Commerce Systemen können neue Zahlungsmethoden hinzugefügt werden, ohne bestehenden Code zu ändern. -->

---

# Strategy Pattern - Payment System

<div class="code-example">
<h5>E-Commerce Payment System</h5>

```javascript
// Strategy Pattern Base
class PaymentStrategy { // fragment
  pay(amount) { // fragment
    throw new Error("pay method must be implemented"); // fragment
  } // fragment
} // fragment

class CreditCardStrategy extends PaymentStrategy { // fragment
  constructor(cardNumber) { // fragment
    super(); // fragment
    this.cardNumber = cardNumber; // fragment
  } // fragment
  
  pay(amount) { // fragment
    console.log(`Paid ${amount} using Credit Card ${this.cardNumber}`); // fragment
    return { success: true, method: 'credit_card', amount }; // fragment
  } // fragment
} // fragment

class PayPalStrategy extends PaymentStrategy { // fragment
  constructor(email) { // fragment
    super(); // fragment
    this.email = email; // fragment
  } // fragment
  
  pay(amount) { // fragment
    console.log(`Paid ${amount} using PayPal ${this.email}`); // fragment
    return { success: true, method: 'paypal', amount }; // fragment
  } // fragment
} // fragment

class ShoppingCart { // fragment
  constructor() { // fragment
    this.items = []; // fragment
    this.paymentStrategy = null; // fragment
  } // fragment
  
  addItem(item) { // fragment
    this.items.push(item); // fragment
  } // fragment
  
  setPaymentStrategy(strategy) { // fragment
    this.paymentStrategy = strategy; // fragment
  } // fragment
  
  calculateTotal() { // fragment
    return this.items.reduce((sum, item) => sum + item.price, 0); // fragment
  } // fragment
  
  checkout() { // fragment
    if (!this.paymentStrategy) { // fragment
      throw new Error("Payment strategy not set"); // fragment
    } // fragment
    
    const total = this.calculateTotal(); // fragment
    return this.paymentStrategy.pay(total); // fragment
  } // fragment
} // fragment

// Verwendung
const cart = new ShoppingCart(); // fragment
cart.addItem({ name: "Laptop", price: 999 }); // fragment
cart.addItem({ name: "Mouse", price: 29 }); // fragment

// Zur Laufzeit Strategy wechseln
cart.setPaymentStrategy(new CreditCardStrategy("1234-5678-9012")); // fragment
cart.checkout(); // fragment

cart.setPaymentStrategy(new PayPalStrategy("user@example.com")); // fragment
cart.checkout(); // fragment
```

</div>

<!-- Speaker Notes: Der Context (ShoppingCart) delegiert die Zahlungsabwicklung an die gewählte Strategy. Benutzer können zur Laufzeit die Zahlungsmethode wechseln, ohne dass sich am Context etwas ändert. -->

---

# Command Pattern

<div class="pattern-definition">

#### Command Pattern - Aktionen als Objekte kapseln
**Intent**: Kapselt Anfragen als Objekte und ermöglicht Parametrisierung, Queuing und Logging von Anfragen.

**Problem**: Aktionen sollen als First-Class Citizens behandelt und rückgängig gemacht werden können.

**Solution**: Commands kapseln Aktionen mit execute() und undo() Methoden.

</div>

<div class="two-column">
<div>

## Key Concepts
- **Aktion-Objekte** - Requests als First-Class Citizens <!-- .element: class="fragment" -->
- **Undo/Redo Funktionalität** - Kommando-Rückgängigmachung <!-- .element: class="fragment" -->
- **Macro-Commands** - Kombinierte Operationen <!-- .element: class="fragment" -->
- **Queuing und Logging** - Kommandos verzögern/protokollieren <!-- .element: class="fragment" -->
- **Invoker-Receiver Entkopplung** - Lose gekoppelte Ausführung <!-- .element: class="fragment" -->

</div>
<div>

## Use Cases
- GUI Button Actions <!-- .element: class="fragment" -->
- Undo/Redo Systems <!-- .element: class="fragment" -->
- Macro Recording <!-- .element: class="fragment" -->
- Transactional Operations <!-- .element: class="fragment" -->
- Remote Procedure Calls <!-- .element: class="fragment" -->
- Job Queues and Schedulers <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Command Pattern ist essentiell für Undo/Redo-Funktionalität in Editoren, GUI-Anwendungen und Transaktionssystemen. Jede Aktion wird als Objekt gekapselt und kann rückgängig gemacht werden. -->

---

# Command Pattern - Smart Home System

<div class="code-example">
<h5>Smart Home Control System</h5>

```javascript
// Command Pattern Base
class Command { // fragment
  execute() { // fragment
    throw new Error("execute method must be implemented"); // fragment
  } // fragment
  
  undo() { // fragment
    throw new Error("undo method must be implemented"); // fragment
  } // fragment
} // fragment

class Light { // fragment
  constructor(location) { // fragment
    this.location = location; // fragment
    this.isOn = false; // fragment
  } // fragment
  
  turnOn() { // fragment
    this.isOn = true; // fragment
    console.log(`${this.location} light is ON`); // fragment
  } // fragment
  
  turnOff() { // fragment
    this.isOn = false; // fragment
    console.log(`${this.location} light is OFF`); // fragment
  } // fragment
} // fragment

class LightOnCommand extends Command { // fragment
  constructor(light) { // fragment
    super(); // fragment
    this.light = light; // fragment
  } // fragment
  
  execute() { // fragment
    this.light.turnOn(); // fragment
  } // fragment
  
  undo() { // fragment
    this.light.turnOff(); // fragment
  } // fragment
} // fragment

class RemoteControl { // fragment
  constructor() { // fragment
    this.commands = []; // fragment
    this.history = []; // fragment
  } // fragment
  
  setCommand(slot, command) { // fragment
    this.commands[slot] = command; // fragment
  } // fragment
  
  pressButton(slot) { // fragment
    if (this.commands[slot]) { // fragment
      this.commands[slot].execute(); // fragment
      this.history.push(this.commands[slot]); // fragment
    } // fragment
  } // fragment
  
  pressUndo() { // fragment
    if (this.history.length > 0) { // fragment
      const lastCommand = this.history.pop(); // fragment
      lastCommand.undo(); // fragment
    } // fragment
  } // fragment
} // fragment

// Macro Command für komplexe Operationen
class MacroCommand extends Command { // fragment
  constructor(commands) { // fragment
    super(); // fragment
    this.commands = commands; // fragment
  } // fragment
  
  execute() { // fragment
    this.commands.forEach(command => command.execute()); // fragment
  } // fragment
  
  undo() { // fragment
    // Rückwärts durchlaufen für korrekte Undo-Reihenfolge // fragment
    for (let i = this.commands.length - 1; i >= 0; i--) { // fragment
      this.commands[i].undo(); // fragment
    } // fragment
  } // fragment
} // fragment

// Verwendung
const remote = new RemoteControl(); // fragment
const livingRoomLight = new Light("Living Room"); // fragment
const kitchenLight = new Light("Kitchen"); // fragment

remote.setCommand(0, new LightOnCommand(livingRoomLight)); // fragment
remote.setCommand(1, new MacroCommand([ // fragment
  new LightOnCommand(livingRoomLight), // fragment
  new LightOnCommand(kitchenLight) // fragment
])); // fragment

remote.pressButton(1); // Alle Lichter an // fragment
remote.pressUndo();    // Alle Lichter aus // fragment
```

</div>

<!-- Speaker Notes: Macro Commands zeigen die Flexibilität des Patterns - komplexe Operationen werden durch Kombination einfacher Commands erstellt. Smart Home Systemen nutzen dies für Szenarien wie "Gute Nacht" oder "Verlassen". -->

---

# Template Method Pattern

<div class="pattern-definition">

#### Template Method Pattern - Algorithmus-Skelett mit variablen Schritten
**Intent**: Definiert das Skelett eines Algorithmus in einer Basisklasse und lässt Subklassen bestimmte Schritte neu definieren.

**Problem**: Algorithmus-Struktur soll gleich bleiben, aber einzelne Schritte variieren.

**Solution**: Basisklasse definiert Template Method, Subklassen implementieren abstrakte Schritte.

</div>

<div class="code-example">
<h5>Data Import Pipeline</h5>

```javascript
// Template Method Pattern
class DataProcessor { // fragment
  // Template Method - definiert Algorithmus-Struktur // fragment
  process() { // fragment
    this.loadData(); // fragment
    this.validateData(); // fragment
    this.transformData(); // fragment
    this.saveData(); // fragment
    
    // Hook method - optional override // fragment
    if (this.shouldNotify()) { // fragment
      this.sendNotification(); // fragment
    } // fragment
  } // fragment
  
  // Abstract methods - müssen implementiert werden // fragment
  loadData() { // fragment
    throw new Error("loadData must be implemented"); // fragment
  } // fragment
  
  validateData() { // fragment
    throw new Error("validateData must be implemented"); // fragment
  } // fragment
  
  transformData() { // fragment
    throw new Error("transformData must be implemented"); // fragment
  } // fragment
  
  saveData() { // fragment
    throw new Error("saveData must be implemented"); // fragment
  } // fragment
  
  // Hook method - optionale Implementierung // fragment
  shouldNotify() { // fragment
    return true; // fragment
  } // fragment
  
  sendNotification() { // fragment
    console.log("Data processing completed"); // fragment
  } // fragment
} // fragment

class CSVImporter extends DataProcessor { // fragment
  constructor(filename) { // fragment
    super(); // fragment
    this.filename = filename; // fragment
    this.data = null; // fragment
  } // fragment
  
  loadData() { // fragment
    console.log(`Loading CSV data from ${this.filename}`); // fragment
    // Simulierte CSV-Daten // fragment
    this.data = [ // fragment
      { name: "John", age: "30", city: "NYC" }, // fragment
      { name: "Jane", age: "25", city: "SF" } // fragment
    ]; // fragment
  } // fragment
  
  validateData() { // fragment
    console.log("Validating CSV data structure"); // fragment
    this.data = this.data.filter(row => row.name && row.age && row.city); // fragment
  } // fragment
  
  transformData() { // fragment
    console.log("Transforming CSV data"); // fragment
    this.data = this.data.map(row => ({ // fragment
      ...row, // fragment
      age: parseInt(row.age), // fragment
      timestamp: new Date().toISOString() // fragment
    })); // fragment
  } // fragment
  
  saveData() { // fragment
    console.log("Saving transformed data to database"); // fragment
    console.log("Data:", this.data); // fragment
  } // fragment
  
  shouldNotify() { // fragment
    return this.data.length > 0; // fragment
  } // fragment
} // fragment

// Verwendung
const csvImporter = new CSVImporter("users.csv"); // fragment
csvImporter.process(); // fragment
```

</div>

<!-- Speaker Notes: Template Method Pattern ist perfekt für ETL-Pipelines, Build-Prozesse und Datenverarbeitungs-Workflows. Die Struktur bleibt gleich, aber jeder Schritt kann unterschiedlich implementiert werden. -->

---

# Iterator Pattern

<div class="pattern-definition">

#### Iterator Pattern - Sequenzieller Zugriff ohne Struktur-Kenntnis
**Intent**: Bietet einen Weg, um auf Elemente einer Sammlung sequenziell zuzugreifen, ohne die zugrunde liegende Struktur preiszugeben.

**Problem**: Verschiedene Collections sollen einheitlich durchlaufen werden können.

**Solution**: Iterator-Interface abstrahiert den Zugriff auf Collection-Elemente.

</div>

<div class="code-example">
<h5>Playlist Iterator mit verschiedenen Modi</h5>

```javascript
class Playlist { // fragment
  constructor(name) { // fragment
    this.name = name; // fragment
    this.songs = []; // fragment
  } // fragment
  
  addSong(song) { // fragment
    this.songs.push(song); // fragment
  } // fragment
  
  createSequentialIterator() { // fragment
    return new SequentialIterator(this.songs); // fragment
  } // fragment
  
  createShuffleIterator() { // fragment
    return new ShuffleIterator(this.songs); // fragment
  } // fragment
  
  createFilterIterator(filterFn) { // fragment
    return new FilterIterator(this.songs, filterFn); // fragment
  } // fragment
} // fragment

class SequentialIterator { // fragment
  constructor(songs) { // fragment
    this.songs = songs; // fragment
    this.currentIndex = 0; // fragment
  } // fragment
  
  hasNext() { // fragment
    return this.currentIndex < this.songs.length; // fragment
  } // fragment
  
  next() { // fragment
    return this.hasNext() ? this.songs[this.currentIndex++] : null; // fragment
  } // fragment
} // fragment

class ShuffleIterator { // fragment
  constructor(songs) { // fragment
    this.songs = [...songs]; // Copy to avoid mutation // fragment
    this.shuffle(); // fragment
    this.currentIndex = 0; // fragment
  } // fragment
  
  shuffle() { // fragment
    for (let i = this.songs.length - 1; i > 0; i--) { // fragment
      const j = Math.floor(Math.random() * (i + 1)); // fragment
      [this.songs[i], this.songs[j]] = [this.songs[j], this.songs[i]]; // fragment
    } // fragment
  } // fragment
  
  hasNext() { // fragment
    return this.currentIndex < this.songs.length; // fragment
  } // fragment
  
  next() { // fragment
    return this.hasNext() ? this.songs[this.currentIndex++] : null; // fragment
  } // fragment
} // fragment

// Verwendung
const playlist = new Playlist("My Favorites"); // fragment
playlist.addSong({ title: "Song 1", artist: "Artist A", genre: "Rock" }); // fragment
playlist.addSong({ title: "Song 2", artist: "Artist B", genre: "Pop" }); // fragment
playlist.addSong({ title: "Song 3", artist: "Artist A", genre: "Rock" }); // fragment

// Sequential playback
const sequential = playlist.createSequentialIterator(); // fragment
while (sequential.hasNext()) { // fragment
  console.log("Now playing:", sequential.next().title); // fragment
} // fragment

// Shuffle playback
const shuffle = playlist.createShuffleIterator(); // fragment
while (shuffle.hasNext()) { // fragment
  console.log("Shuffle:", shuffle.next().title); // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Verschiedene Iterator-Implementierungen ermöglichen unterschiedliche Durchlauf-Strategien für die gleiche Collection. Streaming-Dienste nutzen ähnliche Patterns für Playlisten und Empfehlungs-Algorithmen. -->

---

# Chain of Responsibility

<div class="pattern-definition">

#### Chain of Responsibility - Anfragen durch Handler-Kette weiterleiten
**Intent**: Gibt mehreren Objekten die Chance, eine Anfrage zu behandeln, indem diese Objekte in einer Kette verknüpft werden.

**Problem**: Verschiedene Objekte sollen eine Anfrage verarbeiten können, ohne dass der Sender den konkreten Empfänger kennt.

**Solution**: Handler-Kette, wo jeder Handler entscheidet, ob er die Anfrage verarbeitet oder weiterleitet.

</div>

<div class="code-example">
<h5>HTTP Request Processing Pipeline</h5>

```javascript
// Chain of Responsibility Pattern
class Handler { // fragment
  constructor() { // fragment
    this.nextHandler = null; // fragment
  } // fragment
  
  setNext(handler) { // fragment
    this.nextHandler = handler; // fragment
    return handler; // Ermöglicht Method Chaining // fragment
  } // fragment
  
  handle(request) { // fragment
    if (this.canHandle(request)) { // fragment
      return this.process(request); // fragment
    } else if (this.nextHandler) { // fragment
      return this.nextHandler.handle(request); // fragment
    } else { // fragment
      console.log(`No handler found for request: ${request.type}`); // fragment
      return null; // fragment
    } // fragment
  } // fragment
  
  canHandle(request) { // fragment
    throw new Error("canHandle method must be implemented"); // fragment
  } // fragment
  
  process(request) { // fragment
    throw new Error("process method must be implemented"); // fragment
  } // fragment
} // fragment

class AuthenticationHandler extends Handler { // fragment
  canHandle(request) { // fragment
    return request.type === 'auth' && !request.authenticated; // fragment
  } // fragment
  
  process(request) { // fragment
    console.log("Processing authentication..."); // fragment
    request.authenticated = true; // fragment
    request.userId = "user123"; // fragment
    
    // Continue chain after processing // fragment
    if (this.nextHandler) { // fragment
      return this.nextHandler.handle(request); // fragment
    } // fragment
    return request; // fragment
  } // fragment
} // fragment

class AuthorizationHandler extends Handler { // fragment
  canHandle(request) { // fragment
    return request.authenticated && !request.authorized; // fragment
  } // fragment
  
  process(request) { // fragment
    console.log("Checking authorization..."); // fragment
    if (request.userId && request.resource) { // fragment
      request.authorized = this.checkPermission(request.userId, request.resource); // fragment
    } // fragment
    
    if (request.authorized && this.nextHandler) { // fragment
      return this.nextHandler.handle(request); // fragment
    } else if (!request.authorized) { // fragment
      throw new Error("Access denied"); // fragment
    } // fragment
    return request; // fragment
  } // fragment
  
  checkPermission(userId, resource) { // fragment
    // Simplified permission check // fragment
    const permissions = { // fragment
      "user123": ["read", "write"], // fragment
      "user456": ["read"] // fragment
    }; // fragment
    
    return permissions[userId]?.includes(resource.action) || false; // fragment
  } // fragment
} // fragment

class BusinessLogicHandler extends Handler { // fragment
  canHandle(request) { // fragment
    return request.authorized; // fragment
  } // fragment
  
  process(request) { // fragment
    console.log("Processing business logic..."); // fragment
    request.result = { // fragment
      status: "success", // fragment
      message: "Request processed successfully", // fragment
      timestamp: new Date().toISOString() // fragment
    }; // fragment
    
    return request; // fragment
  } // fragment
} // fragment

// Handler-Kette aufbauen
const authHandler = new AuthenticationHandler(); // fragment
const authzHandler = new AuthorizationHandler(); // fragment
const businessHandler = new BusinessLogicHandler(); // fragment

// Kette verknüpfen
authHandler // fragment
  .setNext(authzHandler) // fragment
  .setNext(businessHandler); // fragment

// Request verarbeiten
const request = { // fragment
  type: 'auth', // fragment
  resource: { action: 'write' }, // fragment
  data: { content: "Hello World" }, // fragment
  authenticated: false, // fragment
  authorized: false // fragment
}; // fragment

try { // fragment
  const result = authHandler.handle(request); // fragment
  console.log("Final result:", result); // fragment
} catch (error) { // fragment
  console.error("Request failed:", error.message); // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Diese Pipeline zeigt typische Web-Request-Verarbeitung: Authentication → Authorization → Business Logic. Jeder Handler ist unabhängig testbar und die Reihenfolge kann dynamisch angepasst werden. -->

---

# Behavioral Patterns Vergleich

<div class="interactive-question">

## Wann welches Pattern verwenden?

</div>

| Pattern | Verwendung | Vorteil |
|---------|------------|---------|
| **Observer** | Event-Systeme, MVC | Lose Kopplung bei 1:n <!-- .element: class="fragment" --> |
| **Strategy** | Algorithmus-Varianten | Runtime-Algorithmus-Wechsel <!-- .element: class="fragment" --> |
| **Command** | Undo/Redo, Queuing | Aktionen als Objekte <!-- .element: class="fragment" --> |
| **Template Method** | Algorithmus-Skelett | Code-Wiederverwendung <!-- .element: class="fragment" --> |
| **Iterator** | Collection-Durchlauf | Einheitlicher Zugriff <!-- .element: class="fragment" --> |
| **Chain of Responsibility** | Middleware, Pipelines | Flexible Handler-Ketten <!-- .element: class="fragment" --> |

<div class="highlight-box success">
**Entscheidungshilfe**: Jedes Pattern löst spezifische Kommunikations- und Interaktions-Probleme. Die Wahl hängt davon ab, ob Sie Events handhaben, Algorithmen austauschen oder Anfragen verarbeiten müssen.
</div>

<!-- Speaker Notes: Jedes Pattern löst spezifische Kommunikations- und Interaktions-Probleme. Die Wahl hängt davon ab, ob Sie Events handhaben, Algorithmen austauschen oder Anfragen verarbeiten müssen. -->

---

# Moderne JavaScript Implementierungen

<div class="code-example">
<h5>ES6+ Features für Patterns</h5>

```javascript
// Observer mit ES6 Proxy für automatische Updates
class ReactiveObject { // fragment
  constructor(target) { // fragment
    this.observers = []; // fragment
    
    return new Proxy(target, { // fragment
      set: (obj, prop, value) => { // fragment
        const oldValue = obj[prop]; // fragment
        obj[prop] = value; // fragment
        this.notify({ prop, oldValue, newValue: value }); // fragment
        return true; // fragment
      } // fragment
    }); // fragment
  } // fragment
  
  subscribe(observer) { // fragment
    this.observers.push(observer); // fragment
  } // fragment
  
  notify(change) { // fragment
    this.observers.forEach(observer => observer(change)); // fragment
  } // fragment
} // fragment

// Iterator mit Generators
class LazyRange { // fragment
  constructor(start, end) { // fragment
    this.start = start; // fragment
    this.end = end; // fragment
  } // fragment
  
  *[Symbol.iterator]() { // fragment
    for (let i = this.start; i <= this.end; i++) { // fragment
      yield i; // fragment
    } // fragment
  } // fragment
  
  *filter(predicate) { // fragment
    for (const value of this) { // fragment
      if (predicate(value)) { // fragment
        yield value; // fragment
      } // fragment
    } // fragment
  } // fragment
} // fragment

// Async Iterator für Streams
class AsyncDataStream { // fragment
  async *fetchData() { // fragment
    let page = 1; // fragment
    while (true) { // fragment
      const response = await fetch(`/api/data?page=${page}`); // fragment
      const data = await response.json(); // fragment
      
      if (data.length === 0) break; // fragment
      
      for (const item of data) { // fragment
        yield item; // fragment
      } // fragment
      page++; // fragment
    } // fragment
  } // fragment
} // fragment

// Verwendung
const user = new ReactiveObject({ name: 'John', age: 30 }); // fragment
user.subscribe(change => console.log('Property changed:', change)); // fragment

const range = new LazyRange(1, 10); // fragment
for (const num of range.filter(x => x % 2 === 0)) { // fragment
  console.log('Even number:', num); // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Moderne JavaScript-Features wie Proxies, Generators und Async Iterators ermöglichen elegante Pattern-Implementierungen. Reactive Frameworks wie Vue.js nutzen Proxies für automatische Observer-Pattern-Implementierung. -->

---

# Hands-on Übungen

<div class="interactive-question">

## Pattern-Kombination in der Praxis

</div>

<div class="progress-indicator">
<div class="progress-step current">🛠️ Übung 1: Event-System (20 min)</div>
<div class="progress-step pending">⏳ Übung 2: Payment Gateway (25 min)</div>
<div class="progress-step pending">⏳ Übung 3: Data Pipeline (30 min)</div>
</div>

### Übung 1: Chat Event-System
**Observer Pattern implementieren**
- User können sich für Messages subscriben <!-- .element: class="fragment" -->
- Beim Senden einer Message werden alle User benachrichtigt <!-- .element: class="fragment" -->
- Channel-specific Subscriptions <!-- .element: class="fragment" -->
- Typing indicators implementieren <!-- .element: class="fragment" -->

### Übung 2: Payment Gateway mit Undo
**Strategy + Command Pattern**
- Verschiedene Payment Methods (PayPal, Credit Card, Bank Transfer) <!-- .element: class="fragment" -->
- Transaction history mit Undo capability <!-- .element: class="fragment" -->
- Failed payment retry logic <!-- .element: class="fragment" -->
- Compound transactions (multiple payments) <!-- .element: class="fragment" -->

### Übung 3: Data Processing Pipeline
**Template Method + Chain of Responsibility**
- ETL-Pipeline mit flexiblen Processing-Steps <!-- .element: class="fragment" -->
- Data cleaning, validation, format conversion handlers <!-- .element: class="fragment" -->
- Error recovery handlers <!-- .element: class="fragment" -->
- Parallel processing und batch operations <!-- .element: class="fragment" -->

<!-- Speaker Notes: Diese Übungen kombinieren alle heute gelernten Patterns in realistischen Szenarien. Die Teilnehmer sollen die Synergien zwischen verschiedenen Patterns erleben. -->

---

# Best Practices

<div class="two-column">
<div>

## Behavioral Patterns richtig einsetzen

- **Event-First Design** - Observer für lose Kopplung nutzen <!-- .element: class="fragment highlight-green" -->
- **Strategy über If-Else** - Algorithmen austauschbar machen <!-- .element: class="fragment highlight-green" -->
- **Commands für Undo** - Aktionen reversibel gestalten <!-- .element: class="fragment highlight-green" -->
- **Templates für Workflows** - Gemeinsame Struktur teilen <!-- .element: class="fragment highlight-green" -->
- **Iterators für Collections** - Einheitliche Zugriffe schaffen <!-- .element: class="fragment highlight-green" -->
- **Chains für Pipelines** - Flexible Verarbeitung ermöglichen <!-- .element: class="fragment highlight-green" -->

</div>
<div>

## Anti-Patterns vermeiden

- **Over-Engineering** - Pattern nur bei echtem Nutzen <!-- .element: class="fragment highlight-red" -->
- **Observer-Leaks** - Subscription cleanup nicht vergessen <!-- .element: class="fragment highlight-red" -->
- **Command-Explosion** - Nicht jeden Setter als Command <!-- .element: class="fragment highlight-red" -->
- **Strategy-Overkill** - Einfache if-else manchmal besser <!-- .element: class="fragment highlight-red" -->
- **Chain-Overuse** - Nicht jede Sequenz braucht eine Kette <!-- .element: class="fragment highlight-red" -->

</div>
</div>

<div class="highlight-box warning">
**Goldene Regel**: Behavioral Patterns können schnell zu Over-Engineering führen. Verwenden Sie sie nur, wenn die Flexibilität wirklich benötigt wird. Observer-Pattern kann zu Memory Leaks führen, wenn Subscriptions nicht ordnungsgemäß aufgeräumt werden.
</div>

<!-- Speaker Notes: Behavioral Patterns können schnell zu Over-Engineering führen. Verwenden Sie sie nur, wenn die Flexibilität wirklich benötigt wird. Observer-Pattern kann zu Memory Leaks führen, wenn Subscriptions nicht ordnungsgemäß aufgeräumt werden. -->

---

# Zusammenfassung Tag 3

<div class="progress-indicator">
<div class="progress-step completed">✅ Observer Pattern - Event-getriebene Architekturen</div>
<div class="progress-step completed">✅ Strategy Pattern - Algorithmus-Flexibilität zur Laufzeit</div>
<div class="progress-step completed">✅ Command Pattern - Undo/Redo und Action-Queuing</div>
<div class="progress-step completed">✅ Template Method - Workflow-Strukturen definieren</div>
<div class="progress-step completed">✅ Iterator Pattern - Einheitliche Collection-Zugriffe</div>
<div class="progress-step completed">✅ Chain of Responsibility - Flexible Request-Verarbeitung</div>
</div>

<div class="two-column">
<div>

## Behavioral Patterns Mastery

- **Observer Pattern** - Event-getriebene Architekturen <!-- .element: class="fragment" -->
- **Strategy Pattern** - Algorithmus-Flexibilität zur Laufzeit <!-- .element: class="fragment" -->
- **Command Pattern** - Undo/Redo und Action-Queuing <!-- .element: class="fragment" -->
- **Template Method** - Workflow-Strukturen definieren <!-- .element: class="fragment" -->
- **Iterator Pattern** - Einheitliche Collection-Zugriffe <!-- .element: class="fragment" -->
- **Chain of Responsibility** - Flexible Request-Verarbeitung <!-- .element: class="fragment" -->

</div>
<div>

## Nächste Schritte

- **Microservices** - Patterns in verteilten Systemen <!-- .element: class="fragment" -->
- **Reactive Programming** - Observer Pattern erweitert <!-- .element: class="fragment" -->
- **Event Sourcing** - Command Pattern in der Praxis <!-- .element: class="fragment" -->
- **Advanced Patterns** - CQRS, Saga, Event-driven Architecture <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Behavioral Patterns sind das Fundament moderner Softwarearchitekturen. Sie ermöglichen Event-getriebene Systeme, Microservices-Kommunikation und Reactive Programming. Die heute gelernten Patterns werden in praktisch jeder modernen Anwendung verwendet. -->

---

# Fragen & Diskussion

<div class="interactive-question">

## Ihre Erfahrungen mit Behavioral Patterns?

</div>

<div class="two-column">
<div>

### Diskussionspunkte:
- Wo haben Sie bereits diese Patterns gesehen? <!-- .element: class="fragment" -->
- Welche Herausforderungen bei der Implementierung? <!-- .element: class="fragment" -->
- Fragen zu spezifischen Use Cases? <!-- .element: class="fragment" -->
- Pattern-Kombinationen in Ihren Projekten? <!-- .element: class="fragment" -->

</div>
<div>

### Kontakt für Follow-up:
- **Workshop-Repository** mit allen Code-Beispielen <!-- .element: class="fragment" -->
- **Best-Practice Checklisten** für jedes Pattern <!-- .element: class="fragment" -->
- **Weiterführende Resources** und advanced topics <!-- .element: class="fragment" -->
- **Community** für weitere Diskussionen <!-- .element: class="fragment" -->

</div>
</div>

<div class="workshop-header">

**Vielen Dank für Ihre aktive Teilnahme!**
### Morgen: Tag 4 - Advanced Patterns und Integration

</div>

<!-- Speaker Notes: Ermutigen Sie Diskussion über real-world Erfahrungen. Oft haben Teilnehmer bereits diese Patterns verwendet, ohne sie als solche zu erkennen. Sammeln Sie Feedback für zukünftige Workshop-Verbesserungen. -->