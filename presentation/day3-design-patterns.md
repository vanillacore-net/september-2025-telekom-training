# Software-Architektur - Tag 3
## Bring your own brain and use it!
### Verhaltensmuster (Behavioral Patterns)

*Speaker Notes: Willkommen zum dritten Tag unserer Design Patterns Workshop-Reihe. Heute fokussieren wir uns auf Verhaltensmuster - die komplexesten und mächtigsten Design Patterns. Diese Muster definieren, wie Objekte miteinander interagieren und kommunizieren.*

---

# Agenda Tag 3

- **Observer Pattern** - Event-getriebene Architektur
- **Strategy Pattern** - Algorithmus-Austauschbarkeit
- **Command Pattern** - Aktion-Kapselung und Undo
- **Template Method Pattern** - Algorithmus-Skelett
- **Iterator Pattern** - Sequenzielle Zugriffe
- **Chain of Responsibility** - Anfragen-Weiterleitung
- **Praktische Übungen** - Hands-on Implementation

*Speaker Notes: Verhaltensmuster sind besonders wichtig für Event-getriebene Systeme, Microservices und moderne Architekturen. Sie ermöglichen lose Kopplung und flexible Kommunikation zwischen Komponenten.*

---

# Verhaltensmuster Übersicht

## Was sind Verhaltensmuster?

- **Fokus auf Kommunikation** - Objektinteraktion optimieren
- **Verantwortlichkeits-Verteilung** - Wer macht was, wann?
- **Algorithmus-Kapselung** - Verhalten abstrahieren
- **Lose Kopplung** - Minimale Abhängigkeiten
- **Flexibilität zur Laufzeit** - Dynamische Verhaltensänderung

*Speaker Notes: Im Gegensatz zu Erzeugungsmustern (Objekterstellung) und Strukturmustern (Objektkomposition) befassen sich Verhaltensmuster mit der Art und Weise, wie Objekte miteinander interagieren und Verantwortlichkeiten verteilen.*

---

# Observer Pattern

## Das Event-System Fundament

- **One-to-Many Abhängigkeit** - Ein Subject, viele Observer
- **Automatische Benachrichtigung** - Push-basierte Updates
- **Lose Kopplung** - Subject kennt Observer nicht konkret
- **Event-getriebene Architektur** - Basis für reactive Systems
- **Pub/Sub Implementierung** - Publisher-Subscriber Pattern

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
    this.observers = this.observers.filter(obs => obs !== observer);
  }
  
  notify(data) {
    this.observers.forEach(observer => observer.update(data));
  }
}
```

*Speaker Notes: Observer Pattern ist das Fundament moderner Event-Systeme. In JavaScript ist es die Basis für DOM Events, Node.js EventEmitter und reaktive Programmierung mit RxJS.*

---

# Observer Pattern - Praxis

## News-System Beispiel

```javascript
class NewsAgency extends Subject {
  constructor() {
    super();
    this.news = "";
  }
  
  setNews(news) {
    this.news = news;
    this.notify(news);
  }
  
  getNews() {
    return this.news;
  }
}

class NewsChannel {
  constructor(name) {
    this.name = name;
  }
  
  update(news) {
    console.log(`${this.name} received: ${news}`);
    this.broadcast(news);
  }
  
  broadcast(news) {
    console.log(`${this.name} broadcasting: ${news}`);
  }
}

// Verwendung
const agency = new NewsAgency();
const cnn = new NewsChannel("CNN");
const bbc = new NewsChannel("BBC");

agency.subscribe(cnn);
agency.subscribe(bbc);
agency.setNews("Breaking: Design Patterns Workshop starts!");
```

*Speaker Notes: Dieses Beispiel zeigt die Macht des Observer Patterns: Eine Nachrichtenagentur kann beliebig viele Kanäle benachrichtigen, ohne jeden Kanal explizit zu kennen. Neue Kanäle können zur Laufzeit hinzugefügt werden.*

---

# Strategy Pattern

## Algorithmus-Austauschbarkeit zur Laufzeit

- **Familie von Algorithmen** - Verschiedene Implementierungen
- **Kapselung von Verhalten** - Strategy-Objekte isoliert
- **Laufzeit-Austausch** - Dynamische Strategie-Wahl
- **Open/Closed Prinzip** - Neue Strategien ohne Änderungen
- **Context-Delegation** - Context nutzt Strategy

```javascript
// Strategy Pattern Base
class PaymentStrategy {
  pay(amount) {
    throw new Error("pay method must be implemented");
  }
}

class CreditCardStrategy extends PaymentStrategy {
  constructor(cardNumber) {
    super();
    this.cardNumber = cardNumber;
  }
  
  pay(amount) {
    console.log(`Paid ${amount} using Credit Card ${this.cardNumber}`);
    return { success: true, method: 'credit_card', amount };
  }
}

class PayPalStrategy extends PaymentStrategy {
  constructor(email) {
    super();
    this.email = email;
  }
  
  pay(amount) {
    console.log(`Paid ${amount} using PayPal ${this.email}`);
    return { success: true, method: 'paypal', amount };
  }
}
```

*Speaker Notes: Strategy Pattern eliminiert komplexe if-else Strukturen und macht Code erweiterbar. In E-Commerce Systemen können neue Zahlungsmethoden hinzugefügt werden, ohne bestehenden Code zu ändern.*

---

# Strategy Pattern - Praxis

## E-Commerce Payment System

```javascript
class ShoppingCart {
  constructor() {
    this.items = [];
    this.paymentStrategy = null;
  }
  
  addItem(item) {
    this.items.push(item);
  }
  
  setPaymentStrategy(strategy) {
    this.paymentStrategy = strategy;
  }
  
  calculateTotal() {
    return this.items.reduce((sum, item) => sum + item.price, 0);
  }
  
  checkout() {
    if (!this.paymentStrategy) {
      throw new Error("Payment strategy not set");
    }
    
    const total = this.calculateTotal();
    return this.paymentStrategy.pay(total);
  }
}

// Verwendung
const cart = new ShoppingCart();
cart.addItem({ name: "Laptop", price: 999 });
cart.addItem({ name: "Mouse", price: 29 });

// Zur Laufzeit Strategy wechseln
cart.setPaymentStrategy(new CreditCardStrategy("1234-5678-9012"));
cart.checkout();

cart.setPaymentStrategy(new PayPalStrategy("user@example.com"));
cart.checkout();
```

*Speaker Notes: Der Context (ShoppingCart) delegiert die Zahlungsabwicklung an die gewählte Strategy. Benutzer können zur Laufzeit die Zahlungsmethode wechseln, ohne dass sich am Context etwas ändert.*

---

# Command Pattern

## Aktionen als Objekte kapseln

- **Aktion-Objekte** - Requests als First-Class Citizens
- **Undo/Redo Funktionalität** - Kommando-Rückgängigmachung
- **Macro-Commands** - Kombinierte Operationen
- **Queuing und Logging** - Kommandos verzögern/protokollieren
- **Invoker-Receiver Entkopplung** - Lose gekoppelte Ausführung

```javascript
// Command Pattern Base
class Command {
  execute() {
    throw new Error("execute method must be implemented");
  }
  
  undo() {
    throw new Error("undo method must be implemented");
  }
}

class Light {
  constructor(location) {
    this.location = location;
    this.isOn = false;
  }
  
  turnOn() {
    this.isOn = true;
    console.log(`${this.location} light is ON`);
  }
  
  turnOff() {
    this.isOn = false;
    console.log(`${this.location} light is OFF`);
  }
}

class LightOnCommand extends Command {
  constructor(light) {
    super();
    this.light = light;
  }
  
  execute() {
    this.light.turnOn();
  }
  
  undo() {
    this.light.turnOff();
  }
}
```

*Speaker Notes: Command Pattern ist essentiell für Undo/Redo-Funktionalität in Editoren, GUI-Anwendungen und Transaktionssystemen. Jede Aktion wird als Objekt gekapselt und kann rückgängig gemacht werden.*

---

# Command Pattern - Praxis

## Smart Home Control System

```javascript
class RemoteControl {
  constructor() {
    this.commands = [];
    this.history = [];
  }
  
  setCommand(slot, command) {
    this.commands[slot] = command;
  }
  
  pressButton(slot) {
    if (this.commands[slot]) {
      this.commands[slot].execute();
      this.history.push(this.commands[slot]);
    }
  }
  
  pressUndo() {
    if (this.history.length > 0) {
      const lastCommand = this.history.pop();
      lastCommand.undo();
    }
  }
}

// Macro Command für komplexe Operationen
class MacroCommand extends Command {
  constructor(commands) {
    super();
    this.commands = commands;
  }
  
  execute() {
    this.commands.forEach(command => command.execute());
  }
  
  undo() {
    // Rückwärts durchlaufen für korrekte Undo-Reihenfolge
    for (let i = this.commands.length - 1; i >= 0; i--) {
      this.commands[i].undo();
    }
  }
}

// Verwendung
const remote = new RemoteControl();
const livingRoomLight = new Light("Living Room");
const kitchenLight = new Light("Kitchen");

remote.setCommand(0, new LightOnCommand(livingRoomLight));
remote.setCommand(1, new MacroCommand([
  new LightOnCommand(livingRoomLight),
  new LightOnCommand(kitchenLight)
]));

remote.pressButton(1); // Alle Lichter an
remote.pressUndo();    // Alle Lichter aus
```

*Speaker Notes: Macro Commands zeigen die Flexibilität des Patterns - komplexe Operationen werden durch Kombination einfacher Commands erstellt. Smart Home Systemen nutzen dies für Szenarien wie "Gute Nacht" oder "Verlassen".*

---

# Template Method Pattern

## Algorithmus-Skelett mit variablen Schritten

- **Algorithmus-Struktur** - Unveränderliches Skelett
- **Variable Schritte** - Subklassen implementieren Details
- **Hook-Methoden** - Optionale Erweiterungspunkte
- **Code-Wiederverwendung** - Gemeinsame Struktur teilen
- **Hollywood-Prinzip** - "Don't call us, we'll call you"

```javascript
// Template Method Pattern
class DataProcessor {
  // Template Method - definiert Algorithmus-Struktur
  process() {
    this.loadData();
    this.validateData();
    this.transformData();
    this.saveData();
    
    // Hook method - optional override
    if (this.shouldNotify()) {
      this.sendNotification();
    }
  }
  
  // Abstract methods - müssen implementiert werden
  loadData() {
    throw new Error("loadData must be implemented");
  }
  
  validateData() {
    throw new Error("validateData must be implemented");
  }
  
  transformData() {
    throw new Error("transformData must be implemented");
  }
  
  saveData() {
    throw new Error("saveData must be implemented");
  }
  
  // Hook method - optionale Implementierung
  shouldNotify() {
    return true;
  }
  
  sendNotification() {
    console.log("Data processing completed");
  }
}
```

*Speaker Notes: Template Method Pattern ist perfekt für ETL-Pipelines, Build-Prozesse und Datenverarbeitungs-Workflows. Die Struktur bleibt gleich, aber jeder Schritt kann unterschiedlich implementiert werden.*

---

# Template Method - Praxis

## Data Import Pipeline

```javascript
class CSVImporter extends DataProcessor {
  constructor(filename) {
    super();
    this.filename = filename;
    this.data = null;
  }
  
  loadData() {
    console.log(`Loading CSV data from ${this.filename}`);
    // Simulierte CSV-Daten
    this.data = [
      { name: "John", age: "30", city: "NYC" },
      { name: "Jane", age: "25", city: "SF" }
    ];
  }
  
  validateData() {
    console.log("Validating CSV data structure");
    this.data = this.data.filter(row => row.name && row.age && row.city);
  }
  
  transformData() {
    console.log("Transforming CSV data");
    this.data = this.data.map(row => ({
      ...row,
      age: parseInt(row.age),
      timestamp: new Date().toISOString()
    }));
  }
  
  saveData() {
    console.log("Saving transformed data to database");
    console.log("Data:", this.data);
  }
  
  shouldNotify() {
    return this.data.length > 0;
  }
}

class XMLImporter extends DataProcessor {
  constructor(xmlString) {
    super();
    this.xmlString = xmlString;
    this.data = null;
  }
  
  loadData() {
    console.log("Loading XML data");
    // Vereinfachte XML-Verarbeitung
    this.data = [{ source: "xml", content: this.xmlString }];
  }
  
  validateData() {
    console.log("Validating XML structure");
    // XML-Validierung hier
  }
  
  transformData() {
    console.log("Transforming XML to standard format");
    // XML-Transformation hier
  }
  
  saveData() {
    console.log("Saving XML-derived data");
  }
  
  sendNotification() {
    console.log("XML import notification sent via email");
  }
}

// Verwendung
const csvImporter = new CSVImporter("users.csv");
csvImporter.process();

const xmlImporter = new XMLImporter("<users><user>...</user></users>");
xmlImporter.process();
```

*Speaker Notes: Beide Importers folgen dem gleichen 4-Schritt-Prozess, implementieren aber jeden Schritt unterschiedlich. Der Hook "shouldNotify" ermöglicht optionale Benachrichtigungen.*

---

# Iterator Pattern

## Sequenzieller Zugriff ohne Struktur-Kenntnis

- **Sammlung-Durchlauf** - Ohne interne Struktur zu kennen
- **Einheitliche Schnittstelle** - Gleicher Zugriff für alle Collections
- **Mehrere Iteratoren** - Parallel verschiedene Durchläufe
- **Lazy Evaluation** - Elemente on-demand laden
- **Bidirektional möglich** - Vorwärts und rückwärts

```javascript
// Iterator Pattern Implementation
class Iterator {
  constructor(collection) {
    this.collection = collection;
    this.index = 0;
  }
  
  hasNext() {
    return this.index < this.collection.length;
  }
  
  next() {
    if (!this.hasNext()) {
      throw new Error("No more elements");
    }
    return this.collection[this.index++];
  }
  
  current() {
    return this.collection[this.index];
  }
  
  reset() {
    this.index = 0;
  }
}

class CustomCollection {
  constructor() {
    this.items = [];
  }
  
  addItem(item) {
    this.items.push(item);
  }
  
  createIterator() {
    return new Iterator(this.items);
  }
  
  // ES6 Iterator Protocol Support
  [Symbol.iterator]() {
    let index = 0;
    const items = this.items;
    
    return {
      next() {
        if (index < items.length) {
          return { value: items[index++], done: false };
        }
        return { done: true };
      }
    };
  }
}
```

*Speaker Notes: JavaScript hat bereits mächtige Iterator-Support eingebaut. for...of Loops, Array-Methoden und Generator-Funktionen nutzen das Iterator Protocol. Das Pattern ist besonders nützlich für custom Collections.*

---

# Iterator Pattern - Praxis

## Playlist Iterator mit Shuffle

```javascript
class Playlist {
  constructor(name) {
    this.name = name;
    this.songs = [];
  }
  
  addSong(song) {
    this.songs.push(song);
  }
  
  createSequentialIterator() {
    return new SequentialIterator(this.songs);
  }
  
  createShuffleIterator() {
    return new ShuffleIterator(this.songs);
  }
  
  createFilterIterator(filterFn) {
    return new FilterIterator(this.songs, filterFn);
  }
}

class SequentialIterator {
  constructor(songs) {
    this.songs = songs;
    this.currentIndex = 0;
  }
  
  hasNext() {
    return this.currentIndex < this.songs.length;
  }
  
  next() {
    return this.hasNext() ? this.songs[this.currentIndex++] : null;
  }
}

class ShuffleIterator {
  constructor(songs) {
    this.songs = [...songs]; // Copy to avoid mutation
    this.shuffle();
    this.currentIndex = 0;
  }
  
  shuffle() {
    for (let i = this.songs.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [this.songs[i], this.songs[j]] = [this.songs[j], this.songs[i]];
    }
  }
  
  hasNext() {
    return this.currentIndex < this.songs.length;
  }
  
  next() {
    return this.hasNext() ? this.songs[this.currentIndex++] : null;
  }
}

class FilterIterator {
  constructor(songs, filterFn) {
    this.songs = songs.filter(filterFn);
    this.currentIndex = 0;
  }
  
  hasNext() {
    return this.currentIndex < this.songs.length;
  }
  
  next() {
    return this.hasNext() ? this.songs[this.currentIndex++] : null;
  }
}

// Verwendung
const playlist = new Playlist("My Favorites");
playlist.addSong({ title: "Song 1", artist: "Artist A", genre: "Rock" });
playlist.addSong({ title: "Song 2", artist: "Artist B", genre: "Pop" });
playlist.addSong({ title: "Song 3", artist: "Artist A", genre: "Rock" });

// Sequential playback
const sequential = playlist.createSequentialIterator();
while (sequential.hasNext()) {
  console.log("Now playing:", sequential.next().title);
}

// Shuffle playback
const shuffle = playlist.createShuffleIterator();
while (shuffle.hasNext()) {
  console.log("Shuffle:", shuffle.next().title);
}

// Rock songs only
const rockSongs = playlist.createFilterIterator(song => song.genre === "Rock");
while (rockSongs.hasNext()) {
  console.log("Rock song:", rockSongs.next().title);
}
```

*Speaker Notes: Verschiedene Iterator-Implementierungen ermöglichen unterschiedliche Durchlauf-Strategien für die gleiche Collection. Streaming-Dienste nutzen ähnliche Patterns für Playlisten und Empfehlungs-Algorithmen.*

---

# Chain of Responsibility

## Anfragen durch Handler-Kette weiterleiten

- **Handler-Kette** - Verkettete Request-Verarbeitung
- **Lose Kopplung** - Sender kennt Handler nicht
- **Dynamische Ketten** - Zur Laufzeit konfigurierbar
- **Fallback-Mechanismus** - Mehrere Verarbeitungsmöglichkeiten
- **Single Responsibility** - Jeder Handler für einen Typ

```javascript
// Chain of Responsibility Pattern
class Handler {
  constructor() {
    this.nextHandler = null;
  }
  
  setNext(handler) {
    this.nextHandler = handler;
    return handler; // Ermöglicht Method Chaining
  }
  
  handle(request) {
    if (this.canHandle(request)) {
      return this.process(request);
    } else if (this.nextHandler) {
      return this.nextHandler.handle(request);
    } else {
      console.log(`No handler found for request: ${request.type}`);
      return null;
    }
  }
  
  canHandle(request) {
    throw new Error("canHandle method must be implemented");
  }
  
  process(request) {
    throw new Error("process method must be implemented");
  }
}

class AuthenticationHandler extends Handler {
  canHandle(request) {
    return request.type === 'auth' && !request.authenticated;
  }
  
  process(request) {
    console.log("Processing authentication...");
    request.authenticated = true;
    request.userId = "user123";
    
    // Continue chain after processing
    if (this.nextHandler) {
      return this.nextHandler.handle(request);
    }
    return request;
  }
}
```

*Speaker Notes: Chain of Responsibility ist fundamental für HTTP-Middleware, Event-Bubbling und Validation-Pipelines. Jeder Handler entscheidet, ob er die Anfrage verarbeiten kann oder an den nächsten weiterleitet.*

---

# Chain of Responsibility - Praxis

## HTTP Request Processing Pipeline

```javascript
class AuthorizationHandler extends Handler {
  canHandle(request) {
    return request.authenticated && !request.authorized;
  }
  
  process(request) {
    console.log("Checking authorization...");
    if (request.userId && request.resource) {
      request.authorized = this.checkPermission(request.userId, request.resource);
    }
    
    if (request.authorized && this.nextHandler) {
      return this.nextHandler.handle(request);
    } else if (!request.authorized) {
      throw new Error("Access denied");
    }
    return request;
  }
  
  checkPermission(userId, resource) {
    // Simplified permission check
    const permissions = {
      "user123": ["read", "write"],
      "user456": ["read"]
    };
    
    return permissions[userId]?.includes(resource.action) || false;
  }
}

class ValidationHandler extends Handler {
  canHandle(request) {
    return request.authorized && !request.validated;
  }
  
  process(request) {
    console.log("Validating request data...");
    if (request.data) {
      request.validated = this.validateData(request.data);
      if (!request.validated) {
        throw new Error("Invalid request data");
      }
    }
    
    if (this.nextHandler) {
      return this.nextHandler.handle(request);
    }
    return request;
  }
  
  validateData(data) {
    // Simple validation
    return data && typeof data === 'object' && Object.keys(data).length > 0;
  }
}

class BusinessLogicHandler extends Handler {
  canHandle(request) {
    return request.validated;
  }
  
  process(request) {
    console.log("Processing business logic...");
    request.result = {
      status: "success",
      message: "Request processed successfully",
      timestamp: new Date().toISOString()
    };
    
    return request;
  }
}

// Handler-Kette aufbauen
const authHandler = new AuthenticationHandler();
const authzHandler = new AuthorizationHandler();
const validationHandler = new ValidationHandler();
const businessHandler = new BusinessLogicHandler();

// Kette verknüpfen
authHandler
  .setNext(authzHandler)
  .setNext(validationHandler)
  .setNext(businessHandler);

// Request verarbeiten
const request = {
  type: 'auth',
  resource: { action: 'write' },
  data: { content: "Hello World" },
  authenticated: false,
  authorized: false,
  validated: false
};

try {
  const result = authHandler.handle(request);
  console.log("Final result:", result);
} catch (error) {
  console.error("Request failed:", error.message);
}
```

*Speaker Notes: Diese Pipeline zeigt typische Web-Request-Verarbeitung: Authentication → Authorization → Validation → Business Logic. Jeder Handler ist unabhängig testbar und die Reihenfolge kann dynamisch angepasst werden.*

---

# Behavioral Patterns Vergleich

## Wann welches Pattern verwenden?

| Pattern | Verwendung | Vorteil |
|---------|------------|---------|
| **Observer** | Event-Systeme, MVC | Lose Kopplung bei 1:n |
| **Strategy** | Algorithmus-Varianten | Runtime-Algorithmus-Wechsel |
| **Command** | Undo/Redo, Queuing | Aktionen als Objekte |
| **Template Method** | Algorithmus-Skelett | Code-Wiederverwendung |
| **Iterator** | Collection-Durchlauf | Einheitlicher Zugriff |
| **Chain of Responsibility** | Middleware, Pipelines | Flexible Handler-Ketten |

*Speaker Notes: Jedes Pattern löst spezifische Kommunikations- und Interaktions-Probleme. Die Wahl hängt davon ab, ob Sie Events handhaben, Algorithmen austauschen oder Anfragen verarbeiten müssen.*

---

# Pattern Kombinationen

## Behavioral Patterns in Kombination

```javascript
// Observer + Command für Undo-fähige GUI
class UndoableCommand extends Command {
  constructor(originalCommand) {
    super();
    this.originalCommand = originalCommand;
    this.observers = [];
  }
  
  subscribe(observer) {
    this.observers.push(observer);
  }
  
  execute() {
    const result = this.originalCommand.execute();
    this.notify({ type: 'executed', command: this });
    return result;
  }
  
  undo() {
    const result = this.originalCommand.undo();
    this.notify({ type: 'undone', command: this });
    return result;
  }
  
  notify(event) {
    this.observers.forEach(observer => observer.update(event));
  }
}

// Strategy + Chain of Responsibility für Pricing
class PricingChain extends Handler {
  constructor(strategy) {
    super();
    this.pricingStrategy = strategy;
  }
  
  canHandle(request) {
    return request.type === this.pricingStrategy.getType();
  }
  
  process(request) {
    return this.pricingStrategy.calculatePrice(request);
  }
}
```

*Speaker Notes: Pattern-Kombinationen sind in der Praxis sehr häufig. Observer + Command ermöglicht Event-basierte Undo-Systeme, während Strategy + Chain flexible Verarbeitungs-Pipelines schafft.*

---

# Moderne JavaScript Implementierungen

## ES6+ Features für Patterns

```javascript
// Observer mit ES6 Proxy für automatische Updates
class ReactiveObject {
  constructor(target) {
    this.observers = [];
    
    return new Proxy(target, {
      set: (obj, prop, value) => {
        const oldValue = obj[prop];
        obj[prop] = value;
        this.notify({ prop, oldValue, newValue: value });
        return true;
      }
    });
  }
  
  subscribe(observer) {
    this.observers.push(observer);
  }
  
  notify(change) {
    this.observers.forEach(observer => observer(change));
  }
}

// Iterator mit Generators
class LazyRange {
  constructor(start, end) {
    this.start = start;
    this.end = end;
  }
  
  *[Symbol.iterator]() {
    for (let i = this.start; i <= this.end; i++) {
      yield i;
    }
  }
  
  *filter(predicate) {
    for (const value of this) {
      if (predicate(value)) {
        yield value;
      }
    }
  }
}

// Async Iterator für Streams
class AsyncDataStream {
  async *fetchData() {
    let page = 1;
    while (true) {
      const response = await fetch(`/api/data?page=${page}`);
      const data = await response.json();
      
      if (data.length === 0) break;
      
      for (const item of data) {
        yield item;
      }
      page++;
    }
  }
}
```

*Speaker Notes: Moderne JavaScript-Features wie Proxies, Generators und Async Iterators ermöglichen elegante Pattern-Implementierungen. Reactive Frameworks wie Vue.js nutzen Proxies für automatische Observer-Pattern-Implementierung.*

---

# Übung 1: Event-System

## Observer Pattern implementieren

**Aufgabe:** Erstellen Sie ein Event-System für eine Chat-Anwendung

```javascript
class ChatRoom {
  // TODO: Implement Observer Pattern
  // - User können sich für Messages subscriben
  // - Beim Senden einer Message werden alle User benachrichtigt
  // - User können specific Channels subscriben
  // - Implement typing indicators
}

class User {
  // TODO: Observer implementation
  // - Receive messages from subscribed channels
  // - Handle typing notifications
  // - Update UI accordingly
}

// Bonus: Implement message filtering und private messages
```

**Ziel:** Observer Pattern mit Channel-Support

*Speaker Notes: Diese Übung kombiniert Observer Pattern mit real-world Anforderungen. Teilnehmer sollen die Flexibilität des Patterns durch Channel-Subscriptions und Filtering erleben.*

---

# Übung 2: Payment Gateway

## Strategy + Command Pattern

**Aufgabe:** Erweitern Sie das Payment-System mit Undo-Funktionalität

```javascript
class PaymentGateway {
  // TODO: Strategy Pattern für verschiedene Payment Methods
  // TODO: Command Pattern für Undo/Refund Funktionalität
  // - PayPal, Credit Card, Bank Transfer strategies
  // - Transaction history mit Undo capability
  // - Failed payment retry logic
}

class TransactionManager {
  // TODO: Command Pattern implementation
  // - Execute payments as commands
  // - Maintain transaction history
  // - Implement refund as undo operation
}

// Bonus: Implement compound transactions (multiple payments)
```

**Ziel:** Strategy und Command Pattern kombinieren

*Speaker Notes: Diese Übung zeigt Pattern-Kombination in der Praxis. Refund als Undo-Operation ist ein elegantes Beispiel für Command Pattern in Fintech-Anwendungen.*

---

# Übung 3: Data Processing Pipeline

## Template Method + Chain of Responsibility

**Aufgabe:** ETL-Pipeline mit flexiblen Processing-Steps

```javascript
class DataPipeline {
  // TODO: Template Method für ETL process
  // - Extract, Transform, Load steps
  // - Validation and error handling
  // - Progress reporting
}

class ProcessingChain {
  // TODO: Chain of Responsibility für Data Transformations
  // - Data cleaning handlers
  // - Validation handlers  
  // - Format conversion handlers
  // - Error recovery handlers
}

// Bonus: Add parallel processing und batch operations
```

**Ziel:** Complex pipeline mit Pattern-Kombination

*Speaker Notes: ETL-Pipelines sind perfekte Beispiele für Template Method + Chain of Responsibility. Die Struktur ist fest, aber die Verarbeitungsschritte sind flexibel und erweiterbar.*

---

# Best Practices

## Behavioral Patterns richtig einsetzen

- **Event-First Design** - Observer für lose Kopplung nutzen
- **Strategy über If-Else** - Algorithmen austauschbar machen
- **Commands für Undo** - Aktionen reversibel gestalten
- **Templates für Workflows** - Gemeinsame Struktur teilen
- **Iterators für Collections** - Einheitliche Zugriffe schaffen
- **Chains für Pipelines** - Flexible Verarbeitung ermöglichen

## Anti-Patterns vermeiden

- **Over-Engineering** - Pattern nur bei echtem Nutzen
- **Observer-Leaks** - Subscription cleanup nicht vergessen
- **Command-Explosion** - Nicht jeden Setter als Command

*Speaker Notes: Behavioral Patterns können schnell zu Over-Engineering führen. Verwenden Sie sie nur, wenn die Flexibilität wirklich benötigt wird. Observer-Pattern kann zu Memory Leaks führen, wenn Subscriptions nicht ordnungsgemäß aufgeräumt werden.*

---

# Zusammenfassung Tag 3

## Behavioral Patterns Mastery

- **Observer Pattern** - Event-getriebene Architekturen
- **Strategy Pattern** - Algorithmus-Flexibilität zur Laufzeit
- **Command Pattern** - Undo/Redo und Action-Queuing
- **Template Method** - Workflow-Strukturen definieren
- **Iterator Pattern** - Einheitliche Collection-Zugriffe
- **Chain of Responsibility** - Flexible Request-Verarbeitung

## Nächste Schritte

- **Microservices** - Patterns in verteilten Systemen
- **Reactive Programming** - Observer Pattern erweitert
- **Event Sourcing** - Command Pattern in der Praxis

*Speaker Notes: Behavioral Patterns sind das Fundament moderner Softwarearchitekturen. Sie ermöglichen Event-getriebene Systeme, Microservices-Kommunikation und Reactive Programming. Die heute gelernten Patterns werden in praktisch jeder modernen Anwendung verwendet.*

---

# Fragen & Diskussion

## Ihre Erfahrungen mit Behavioral Patterns?

- Wo haben Sie bereits diese Patterns gesehen?
- Welche Herausforderungen bei der Implementierung?
- Fragen zu spezifischen Use Cases?

**Kontakt für Follow-up:**
- Workshop-Repository mit allen Code-Beispielen
- Best-Practice Checklisten
- Weiterführende Resources

*Speaker Notes: Ermutigen Sie Diskussion über real-world Erfahrungen. Oft haben Teilnehmer bereits diese Patterns verwendet, ohne sie als solche zu erkennen. Sammeln Sie Feedback für zukünftige Workshop-Verbesserungen.*