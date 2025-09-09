---
title: Software-Architektur - Tag 3
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
  width: 1920
  height: 1080
  margin: 0.05
  minScale: 0.5
  maxScale: 2.0
---

<!-- <style>
/* HedgeDoc Presentation Styles */
.reveal {
  font-family: 'Open Sans', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-weight: 300;
}

/* Critical: Fix content overflow and enforce left alignment */
.reveal .slides {
  font-size: 26px !important; /* Increased by 20% for better readability (22px * 1.20) */
  line-height: 1.3 !important;
}

/* Override reveal.js center alignment - force left alignment for all content */
.reveal .slides section,
.reveal .slides section > *,
.reveal .center {
  text-align: left !important;
}

.reveal .slides section {
  height: 100%;
  width: 100%;
  max-width: 100vw;
  overflow-y: auto !important; /* Allow scrolling if needed */
  overflow-x: hidden;
  padding: 20px 25px 25px 25px !important; /* Reduce top padding to move content up */
  box-sizing: border-box;
  text-align: left !important; /* Ensure all content is left-aligned */
  display: flex !important;
  flex-direction: column !important;
  justify-content: flex-start !important; /* Move content to top */
}

.reveal h1 {
  font-size: 2.16em !important; /* Increased by 20% (1.8em * 1.20) */
  color: #2c2c2c;
  font-weight: 600 !important;
  text-align: left !important;
  margin-top: 0.8em !important;
  margin-bottom: 0.3em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h1:first-child {
  margin-top: 0 !important;
}

.reveal h2 {
  font-size: 1.68em !important; /* Increased by 20% (1.4em * 1.20) */
  color: #2c2c2c;
  font-weight: 500 !important;
  text-align: left !important;
  margin-top: 0.7em !important;
  margin-bottom: 0.4em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h2:first-child {
  margin-top: 0 !important;
}

.reveal h3 {
  font-size: 1.44em !important; /* Increased by 20% (1.2em * 1.20) */
  font-weight: 400 !important;
  text-align: left !important;
  margin-top: 0.6em !important;
  margin-bottom: 0.3em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h3:first-child {
  margin-top: 0 !important;
}

.reveal h4, .reveal h5, .reveal h6 {
  font-weight: 400 !important;
  text-align: left !important;
  margin-top: 0.5em !important;
  margin-bottom: 0.3em !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h4:first-child,
.reveal .slides section > h5:first-child,
.reveal .slides section > h6:first-child {
  margin-top: 0 !important;
}

.reveal p, .reveal li {
  font-weight: 300 !important;
  text-align: left !important;
}

/* Add proper paragraph spacing */
.reveal p {
  margin-bottom: 0.3em !important;
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
  margin-bottom: 0.3em !important;
}

/* Add spacing between list items for better readability */
.reveal ul li, .reveal ol li {
  margin-bottom: 0.3em !important;
}

.reveal ul li:last-child, .reveal ol li:last-child {
  margin-bottom: 0 !important;
}

.reveal ul li::before {
  content: "▸";
  color: #666666;
  font-weight: 400;
  display: inline-block;
  width: 1em;
  margin-left: -1em;
}

/* Code blocks sizing - Full Width Optimized */
.reveal pre {
  font-size: 1.1em !important; /* Significantly increased for better readability */
  max-height: calc(100vh - 200px); /* Use full available screen height */
  overflow-y: auto !important;
  background: #2d3748 !important; /* Dark background for better contrast */
  color: #e2e8f0 !important; /* Light text for contrast */
  border: 1px solid #4a5568; /* Subtle darker border */
  width: 88% !important; /* Use most of screen width */
  max-width: 88% !important;
  margin-left: auto !important;
  margin-right: auto !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2) !important;
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
  background: #ffffff;
  color: #333;
  padding: 75px; /* Scaled for FHD (40px * 1.875) */
  margin: -38px; /* Scaled for FHD (-20px * 1.875) */
  border-radius: 15px; /* Scaled for FHD (8px * 1.875) */
}

.workshop-header h1,
.workshop-header h2 {
  color: #333;
  text-shadow: none;
}

.pattern-definition {
  background-color: #F5F5F5;
  border-left: 8px solid #666666; /* Scaled for FHD (4px * 2) */
  padding: 25px; /* Scaled for FHD (20px * 1.875) */
  margin: 38px 0; /* Scaled for FHD (20px * 1.875) */
  border-radius: 8px; /* Scaled for FHD (4px * 2) */
}

.highlight-box {
  background-color: #F5F5F5;
  border-left: 8px solid #666666; /* Scaled for FHD (4px * 2) */
  padding: 25px; /* Scaled for FHD (20px * 1.875) */
  margin: 38px 0; /* Scaled for FHD (20px * 1.875) */
  border-radius: 8px; /* Scaled for FHD (4px * 2) */
}

.highlight-box.warning {
  border-left-color: #8B0000;
  background-color: #F5F5DC;
}

.highlight-box.accent {
  border-left-color: #4a4a4a;
  background-color: #F5F5F5;
}

.code-example {
  background: #f8f9fa;
  color: #333;
  padding: 30px 36px; /* Enhanced for FHD presentation */
  border-radius: 10px;
  margin: 20px auto;
  font-size: 1.2em; /* Significantly increased font size for better readability */
  width: 86% !important; /* Use most of screen width */
  max-width: 86% !important;
  box-sizing: border-box;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.code-example h5 {
  color: #333;
  margin-top: 0;
  font-weight: 600;
  font-size: 1.3em;
  margin-bottom: 0.8em;
}

.code-example pre {
  width: 100% !important;
  max-width: 100% !important;
  margin: 0 !important;
}

.code-example pre code {
  font-size: 1.05em !important;
}

.interactive-question {
  background-color: #F5F5F5;
  border: 4px solid #666666; /* Scaled for FHD (2px * 2) */
  padding: 25px; /* Scaled for FHD (20px * 1.875) */
  margin: 38px 0; /* Scaled for FHD (20px * 1.875) */
  border-radius: 15px; /* Scaled for FHD (8px * 1.875) */
  text-align: center;
}

.progress-indicator {
  display: flex;
  justify-content: space-around;
  margin: 38px 0; /* Scaled for FHD (20px * 1.875) */
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
  background-color: #006400;
  color: white;
}

.progress-step.pending {
  background-color: #F5F5F5;
  color: #666666;
}

/* Fragment Visibility Control - Critical Fix */
.reveal .fragment {
  visibility: hidden;
  opacity: 0;
  transition: opacity 0.3s ease, visibility 0s linear 0.3s;
}

.reveal .fragment.visible {
  visibility: visible;
  opacity: 1;
  transition: opacity 0.3s ease, visibility 0s linear 0s;
}

/* Ensure fragments are invisible until explicitly revealed */
.reveal .fragment:not(.visible) {
  visibility: hidden !important;
  opacity: 0 !important;
}

.reveal .fragment.highlight-green {
  color: #2c2c2c;
  font-weight: 600;
}

.reveal .fragment.highlight-red {
  color: #8B0000;
  font-weight: 500;
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

/* VanillaCore Logo Styling - Option 1: Small logo in top-right corner */
.vanilla-logo {
  position: absolute;
  top: 25px;
  right: 25px;
  max-width: 80px;
  max-height: calc(100vh - 200px);
  z-index: 1000;
  pointer-events: none;
}

/* SELECTED: Option 1 - Small logo in top-right corner for content slides */
.reveal .slides section:not(.title-slide)::after {
  content: '';
  position: absolute;
  top: 25px;
  right: 25px;
  width: 80px;
  height: 80px;
  background-image: url('/images/VanillaCore_Vertical.png');
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  z-index: 1000;
  pointer-events: none;
  opacity: 0.8;
}

.vanilla-logo img {
  width: 100%;
  height: auto;
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

/* Logo for title slides - centered in middle of slide */
.title-slide .vanilla-logo {
  max-width: 300px;
  max-height: calc(100vh - 250px);
  position: static;
  display: block;
  margin: 0 auto 60px auto;
  text-align: center;
}

/* Remove corner logo from title slides */
.title-slide::after {
  display: none !important;
}

/* Center title slide content with logo above title */
.title-slide {
  text-align: center !important;
  display: flex !important;
  flex-direction: column !important;
  justify-content: center !important;
  align-items: center !important;
  padding: 60px 40px !important;
  min-height: 100vh;
}

/* Style title slide headings to be centered below logo */
.title-slide h1,
.title-slide h2 {
  text-align: center !important;
  margin-left: auto !important;
  margin-right: auto !important;
  width: 100% !important;
}

.title-slide h1 {
  margin-top: 0 !important;
  margin-bottom: 20px !important;
  font-size: 2.88em !important; /* Increased by 20% (2.4em * 1.20) */
  font-weight: 600 !important;
}

.title-slide h2 {
  margin-top: 0 !important;
  margin-bottom: 40px !important;
  font-size: 2.16em !important; /* Increased by 20% (1.8em * 1.20) */
  color: #555555 !important;
  font-weight: 400 !important;
}

/* Subtle watermark for content slides */
.content-slide::after {
  content: "";
  background-image: url('/images/VanillaCore_Vertical.png');
  background-size: 80px;
  background-repeat: no-repeat;
  background-position: bottom 10px right 10px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.1;
  pointer-events: none;
  z-index: -1;
}

/* Hide reveal.js progress bar and controls to remove orange box artifact */
.reveal .progress {
  display: none !important;
}

.reveal .controls {
  display: none !important;
}
</style> -->

<div class="workshop-header title-slide">

<div class="vanilla-logo">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo">
</div>

# Software-Architektur
## Bring your own brain and use it!

</div>

Note:
- Willkommen zum dritten Tag - Verhaltensmuster sind die komplexesten Patterns
- Kurze Wiederholung: Creational (Tag 1) → Structural (Tag 2) → Behavioral (Tag 3)
- Betonen Sie: Verhaltensmuster = Objekt-Kommunikation und Interaktion
- Diese Patterns sind fundamental für moderne Enterprise-Architekturen
- Verwenden Sie examples/exercises/day3-exercises.md für praktische Übungen
<!-- .element: class="notes" -->

---

# Agenda Tag 3

<div class="progress-indicator">
<div class="progress-step current">📍 Observer Pattern - Event-getriebene Architektur</div>
<div class="progress-step pending">⏳ Strategy Pattern - Algorithmus-Austauschbarkeit</div>
<div class="progress-step pending">⏳ Command Pattern - Aktion-Kapselung und Undo</div>
<div class="progress-step pending">⏳ Template Method, Iterator, Chain of Responsibility</div>
<div class="progress-step pending">⏳ Praktische Übungen</div>
</div>

- **Observer Pattern** - Event-getriebene Architektur <!-- .element: class="fragment" data-fragment-index="1" -->
- **Strategy Pattern** - Algorithmus-Austauschbarkeit <!-- .element: class="fragment" data-fragment-index="2" -->
- **Command Pattern** - Aktion-Kapselung und Undo <!-- .element: class="fragment" data-fragment-index="3" -->
- **Template Method Pattern** - Algorithmus-Skelett <!-- .element: class="fragment" data-fragment-index="4" -->
- **Iterator Pattern** - Sequenzielle Zugriffe <!-- .element: class="fragment" data-fragment-index="5" -->
- **Chain of Responsibility** - Anfragen-Weiterleitung <!-- .element: class="fragment" data-fragment-index="6" -->
- **Praktische Übungen** - Hands-on Implementation <!-- .element: class="fragment" data-fragment-index="7" -->

<!-- Speaker Notes: Verhaltensmuster sind besonders wichtig für Event-getriebene Systeme, Microservices und moderne Architekturen. Sie ermöglichen lose Kopplung und flexible Kommunikation zwischen Komponenten. -->

---

# Verhaltensmuster Übersicht

<div class="pattern-definition">

#### Was sind Verhaltensmuster?
**Intent**: Fokussieren sich auf Kommunikation zwischen Objekten und die Verteilung von Verantwortlichkeiten.

**Problem**: Objekte müssen flexibel interagieren ohne fest gekoppelt zu sein.

**Solution**: Bewährte Patterns für Objektkommunikation, Algorithmuskapselung und lose Kopplung.

</div>

- **Fokus auf Kommunikation** - Objektinteraktion optimieren <!-- .element: class="fragment" data-fragment-index="1" -->
- **Verantwortlichkeits-Verteilung** - Wer macht was, wann? <!-- .element: class="fragment" data-fragment-index="2" -->
- **Algorithmus-Kapselung** - Verhalten abstrahieren <!-- .element: class="fragment" data-fragment-index="3" -->
- **Lose Kopplung** - Minimale Abhängigkeiten <!-- .element: class="fragment" data-fragment-index="4" -->
- **Flexibilität zur Laufzeit** - Dynamische Verhaltensänderung <!-- .element: class="fragment" data-fragment-index="5" -->

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
- **One-to-Many Abhängigkeit** - Ein Subject, viele Observer <!-- .element: class="fragment" data-fragment-index="1" -->
- **Automatische Benachrichtigung** - Push-basierte Updates <!-- .element: class="fragment" data-fragment-index="2" -->
- **Lose Kopplung** - Subject kennt Observer nicht konkret <!-- .element: class="fragment" data-fragment-index="3" -->
- **Event-getriebene Architektur** - Basis für reactive Systems <!-- .element: class="fragment" data-fragment-index="4" -->
- **Pub/Sub Implementierung** - Publisher-Subscriber Pattern <!-- .element: class="fragment" data-fragment-index="5" -->

</div>
<div>

## Use Cases
- Event-Handling-Systeme <!-- .element: class="fragment" data-fragment-index="6" -->
- Model-View Architekturen (MVC, MVVM) <!-- .element: class="fragment" data-fragment-index="7" -->
- Notification Services <!-- .element: class="fragment" data-fragment-index="8" -->
- Real-time Data Updates <!-- .element: class="fragment" data-fragment-index="9" -->
- Reactive Programming <!-- .element: class="fragment" data-fragment-index="10" -->

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
agency.setNews("Breaking: Software-Architektur Workshop starts!"); // fragment
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
- **Familie von Algorithmen** - Verschiedene Implementierungen <!-- .element: class="fragment" data-fragment-index="1" -->
- **Kapselung von Verhalten** - Strategy-Objekte isoliert <!-- .element: class="fragment" data-fragment-index="2" -->
- **Laufzeit-Austausch** - Dynamische Strategie-Wahl <!-- .element: class="fragment" data-fragment-index="3" -->
- **Open/Closed Prinzip** - Neue Strategien ohne Änderungen <!-- .element: class="fragment" data-fragment-index="4" -->
- **Context-Delegation** - Context nutzt Strategy <!-- .element: class="fragment" data-fragment-index="5" -->

</div>
<div>

## Use Cases
- Payment Processing Systems <!-- .element: class="fragment" data-fragment-index="6" -->
- Sorting Algorithms <!-- .element: class="fragment" data-fragment-index="7" -->
- Data Compression Methods <!-- .element: class="fragment" data-fragment-index="8" -->
- Authentication Strategies <!-- .element: class="fragment" data-fragment-index="9" -->
- Tax Calculation Systems <!-- .element: class="fragment" data-fragment-index="10" -->

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
- **Aktion-Objekte** - Requests als First-Class Citizens <!-- .element: class="fragment" data-fragment-index="1" -->
- **Undo/Redo Funktionalität** - Kommando-Rückgängigmachung <!-- .element: class="fragment" data-fragment-index="2" -->
- **Macro-Commands** - Kombinierte Operationen <!-- .element: class="fragment" data-fragment-index="3" -->

Note:
- Command Pattern ist fundamental für Undo/Redo-Funktionalität
- Sehr praktisch für GUI-Anwendungen, aber auch für API-Queuing
- Unterschied zu Strategy: Command kapselt komplette Operationen, nicht nur Algorithmen
- Zeigen Sie die Trennung von Invoker, Command und Receiver
- Frage: "Wo in Ihren Anwendungen wünschen Sie sich Undo-Funktionalität?"
<!-- .element: class="notes" -->
- **Queuing und Logging** - Kommandos verzögern/protokollieren <!-- .element: class="fragment" data-fragment-index="4" -->
- **Invoker-Receiver Entkopplung** - Lose gekoppelte Ausführung <!-- .element: class="fragment" data-fragment-index="5" -->

</div>
<div>

## Use Cases
- GUI Button Actions <!-- .element: class="fragment" data-fragment-index="6" -->
- Undo/Redo Systems <!-- .element: class="fragment" data-fragment-index="7" -->
- Macro Recording <!-- .element: class="fragment" data-fragment-index="8" -->
- Transactional Operations <!-- .element: class="fragment" data-fragment-index="9" -->
- Remote Procedure Calls <!-- .element: class="fragment" data-fragment-index="10" -->
- Job Queues and Schedulers <!-- .element: class="fragment" data-fragment-index="11" -->

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
| **Observer** | Event-Systeme, MVC | Lose Kopplung bei 1:n <!-- .element: class="fragment" data-fragment-index="1" --> |
| **Strategy** | Algorithmus-Varianten | Runtime-Algorithmus-Wechsel <!-- .element: class="fragment" data-fragment-index="2" --> |
| **Command** | Undo/Redo, Queuing | Aktionen als Objekte <!-- .element: class="fragment" data-fragment-index="3" --> |
| **Template Method** | Algorithmus-Skelett | Code-Wiederverwendung <!-- .element: class="fragment" data-fragment-index="4" --> |
| **Iterator** | Collection-Durchlauf | Einheitlicher Zugriff <!-- .element: class="fragment" data-fragment-index="5" --> |
| **Chain of Responsibility** | Middleware, Pipelines | Flexible Handler-Ketten <!-- .element: class="fragment" data-fragment-index="6" --> |

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
<div class="progress-step current">🛠️ Übung 1: Event-System</div>
<div class="progress-step pending">⏳ Übung 2: Payment Gateway</div>
<div class="progress-step pending">⏳ Übung 3: Data Pipeline</div>
</div>

### Übung 1: Chat Event-System
**Observer Pattern implementieren**
- User können sich für Messages subscriben <!-- .element: class="fragment" data-fragment-index="1" -->
- Beim Senden einer Message werden alle User benachrichtigt <!-- .element: class="fragment" data-fragment-index="2" -->
- Channel-specific Subscriptions <!-- .element: class="fragment" data-fragment-index="3" -->
- Typing indicators implementieren <!-- .element: class="fragment" data-fragment-index="4" -->

### Übung 2: Payment Gateway mit Undo
**Strategy + Command Pattern**
- Verschiedene Payment Methods (PayPal, Credit Card, Bank Transfer) <!-- .element: class="fragment" data-fragment-index="5" -->
- Transaction history mit Undo capability <!-- .element: class="fragment" data-fragment-index="6" -->
- Failed payment retry logic <!-- .element: class="fragment" data-fragment-index="7" -->
- Compound transactions (multiple payments) <!-- .element: class="fragment" data-fragment-index="8" -->

### Übung 3: Data Processing Pipeline
**Template Method + Chain of Responsibility**
- ETL-Pipeline mit flexiblen Processing-Steps <!-- .element: class="fragment" data-fragment-index="9" -->
- Data cleaning, validation, format conversion handlers <!-- .element: class="fragment" data-fragment-index="10" -->
- Error recovery handlers <!-- .element: class="fragment" data-fragment-index="11" -->
- Parallel processing und batch operations <!-- .element: class="fragment" data-fragment-index="12" -->

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

- **Observer Pattern** - Event-getriebene Architekturen <!-- .element: class="fragment" data-fragment-index="1" -->
- **Strategy Pattern** - Algorithmus-Flexibilität zur Laufzeit <!-- .element: class="fragment" data-fragment-index="2" -->
- **Command Pattern** - Undo/Redo und Action-Queuing <!-- .element: class="fragment" data-fragment-index="3" -->
- **Template Method** - Workflow-Strukturen definieren <!-- .element: class="fragment" data-fragment-index="4" -->
- **Iterator Pattern** - Einheitliche Collection-Zugriffe <!-- .element: class="fragment" data-fragment-index="5" -->
- **Chain of Responsibility** - Flexible Request-Verarbeitung <!-- .element: class="fragment" data-fragment-index="6" -->

</div>
<div>

## Nächste Schritte

- **Microservices** - Patterns in verteilten Systemen <!-- .element: class="fragment" data-fragment-index="7" -->
- **Reactive Programming** - Observer Pattern erweitert <!-- .element: class="fragment" data-fragment-index="8" -->
- **Event Sourcing** - Command Pattern in der Praxis <!-- .element: class="fragment" data-fragment-index="9" -->
- **Advanced Patterns** - CQRS, Saga, Event-driven Architecture <!-- .element: class="fragment" data-fragment-index="10" -->

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
- Wo haben Sie bereits diese Patterns gesehen? <!-- .element: class="fragment" data-fragment-index="1" -->
- Welche Herausforderungen bei der Implementierung? <!-- .element: class="fragment" data-fragment-index="2" -->
- Fragen zu spezifischen Use Cases? <!-- .element: class="fragment" data-fragment-index="3" -->
- Pattern-Kombinationen in Ihren Projekten? <!-- .element: class="fragment" data-fragment-index="4" -->

</div>
<div>

### Kontakt für Follow-up:
- **Workshop-Repository** mit allen Code-Beispielen <!-- .element: class="fragment" data-fragment-index="5" -->
- **Best-Practice Checklisten** für jedes Pattern <!-- .element: class="fragment" data-fragment-index="6" -->
- **Weiterführende Resources** und advanced topics <!-- .element: class="fragment" data-fragment-index="7" -->
- **Community** für weitere Diskussionen <!-- .element: class="fragment" data-fragment-index="8" -->

</div>
</div>

<div class="workshop-header">

**Vielen Dank für Ihre aktive Teilnahme!**
### Morgen: Tag 4 - Advanced Patterns und Integration

</div>

<!-- Speaker Notes: Ermutigen Sie Diskussion über real-world Erfahrungen. Oft haben Teilnehmer bereits diese Patterns verwendet, ohne sie als solche zu erkennen. Sammeln Sie Feedback für zukünftige Workshop-Verbesserungen. -->