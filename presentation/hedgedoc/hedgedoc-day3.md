---
<!-- Version: 1.3.0-headlines-fix -->
type: slide
title: Software-Architektur - Verhaltensmuster
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

<style>
/* Minimal HedgeDoc Presentation Styles */
.reveal .slides {
  font-size: 1.4rem;
  text-align: left;
}

.reveal h1 {
  font-size: 2.5em;
  margin-bottom: 0.5em;
  border-bottom: 2px solid #666;
}

.reveal h2 {
  font-size: 1.8em;
  margin: 0.5em 0;
}

.reveal h3 {
  font-size: 1.3em;
  margin: 0.5em 0;
}

.reveal pre {
  width: 100%;
  min-height: 400px;
  max-height: 70vh;
  font-size: 0.9em;
}

.reveal pre code {
  max-height: 70vh;
  min-height: 400px;
  padding: 1em;
}

.reveal ul, .reveal ol {
  margin-left: 1em;
}

.reveal li {
  margin: 0.3em 0;
}

.reveal .slides section {
  height: 100%;
  padding: 1rem;
  text-align: left !important;
}
</style>


<div class="workshop-header title-slide">

<div class="vanilla-logo">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo">
</div>

# Software-Architektur
## Bring your own brain and use it!
Note:
* Willkommen zum dritten Tag - Verhaltensmuster sind die komplexesten Patterns
* Kurze Wiederholung: Creational → Structural → Behavioral
* Betonen Sie: Verhaltensmuster = Objekt-Kommunikation und Interaktion
* Diese Patterns sind fundamental für moderne Enterprise-Architekturen
* Verwenden Sie konkrete Pattern-Szenarien für praktische Anwendung
<!-- .element: class="notes" -->

---

# Verhaltensmuster Agenda

<div class="progress-indicator">
<div class="progress-step current">📍 Observer Pattern - Event-getriebene Architektur</div>
<div class="progress-step pending">⏳ Strategy Pattern - Algorithmus-Austauschbarkeit</div>
<div class="progress-step pending">⏳ Command Pattern - Aktion-Kapselung und Undo</div>
<div class="progress-step pending">⏳ Template Method, Iterator, Chain of Responsibility</div>
<div class="progress-step pending">⏳ Pattern Integration</div>
</div>

* **Observer Pattern** - Event-getriebene Architektur  <!-- .element: class="fragment" data-fragment-index="1" -->
* **Strategy Pattern** - Algorithmus-Austauschbarkeit  <!-- .element: class="fragment" data-fragment-index="2" -->
* **Command Pattern** - Aktion-Kapselung und Undo  <!-- .element: class="fragment" data-fragment-index="3" -->
* **Template Method Pattern** - Algorithmus-Skelett  <!-- .element: class="fragment" data-fragment-index="4" -->
* **Iterator Pattern** - Sequenzielle Zugriffe  <!-- .element: class="fragment" data-fragment-index="5" -->
* **Chain of Responsibility** - Anfragen-Weiterleitung  <!-- .element: class="fragment" data-fragment-index="6" -->
* **Pattern Integration** - Kombination verschiedener Verhaltensmuster  <!-- .element: class="fragment" data-fragment-index="7" -->

<!-- Speaker Notes: Verhaltensmuster sind besonders wichtig für Event-getriebene Systeme, Microservices und moderne Architekturen. Sie ermöglichen lose Kopplung und flexible Kommunikation zwischen Komponenten. -->

---

# Verhaltensmuster Übersicht

#### Was sind Verhaltensmuster?
**Intent**: Fokussieren sich auf Kommunikation zwischen Objekten und die Verteilung von Verantwortlichkeiten.

**Problem**: Objekte müssen flexibel interagieren ohne fest gekoppelt zu sein.

**Solution**: Bewährte Patterns für Objektkommunikation, Algorithmuskapselung und lose Kopplung.
* **Fokus auf Kommunikation** - Objektinteraktion optimieren  <!-- .element: class="fragment" data-fragment-index="1" -->
* **Verantwortlichkeits-Verteilung** - Wer macht was, wann?  <!-- .element: class="fragment" data-fragment-index="2" -->
* **Algorithmus-Kapselung** - Verhalten abstrahieren  <!-- .element: class="fragment" data-fragment-index="3" -->
* **Lose Kopplung** - Minimale Abhängigkeiten  <!-- .element: class="fragment" data-fragment-index="4" -->
* **Flexibilität zur Laufzeit** - Dynamische Verhaltensänderung  <!-- .element: class="fragment" data-fragment-index="5" -->

<!-- Speaker Notes: Im Gegensatz zu Erzeugungsmustern (Objekterstellung) und Strukturmustern (Objektkomposition) befassen sich Verhaltensmuster mit der Art und Weise, wie Objekte miteinander interagieren und Verantwortlichkeiten verteilen. -->

---

# Observer Pattern

## Was ist hier falsch?

#### Observer Pattern Problem
**Situation**: In Event-getriebenen Systemen müssen mehrere Komponenten über Änderungen benachrichtigt werden. Die direkte Kopplung führt zu unübersichtlichem Code.

**Was sehen Sie hier Problematisches?**
```java
public class OrderManager {
    
    public void processOrder(Order order) {
        // Code-Smell: Tight Coupling zu allen Clients
        order.setStatus(OrderStatus.PROCESSING);
        
        // Problem: Direkter Aufruf aller Services!
        EmailService emailService = new EmailService();
        SMSService smsService = new SMSService();
        AuditService auditService = new AuditService();
        InventoryService inventoryService = new InventoryService();
        BillingService billingService = new BillingService();
        
        // ALLE Services müssen explizit benachrichtigt werden
        emailService.sendOrderConfirmation(order.getCustomer().getEmail(), order);
        smsService.sendOrderNotification(order.getCustomer().getPhone(), order.getId());
        auditService.logOrderEvent("ORDER_PROCESSED", order.getId());
        inventoryService.reserveItems(order.getItems());
        billingService.createInvoice(order);
        
        // Was passiert bei neuen Services?
        // Jede neue Benachrichtigung = Code-Änderung hier!
        
        order.setStatus(OrderStatus.PROCESSED);
    }
    
    // DUPLICATE CODE: Ähnliche Logik bei anderen Events
    public void cancelOrder(String orderId) {
        Order order = findOrder(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        
        // Wieder die gleichen direkten Service-Aufrufe
        EmailService emailService = new EmailService();
        SMSService smsService = new SMSService();
        // ... noch mehr Code-Duplikation
    }
}
```

### Identifizierte Code-Smells

* **Tight Coupling**: OrderManager kennt alle Benachrichtigungs-Services direkt <!-- .element: class="fragment" data-fragment-index="1" -->
* **Open/Closed Principle Violation**: Neue Services erfordern Code-Änderungen <!-- .element: class="fragment" data-fragment-index="2" -->
* **Duplicate Code**: Service-Benachrichtigung Code in jeder Methode wiederholt <!-- .element: class="fragment" data-fragment-index="3" -->
* **Hard to Test**: Alle Services müssen gemockt werden <!-- .element: class="fragment" data-fragment-index="4" -->
* **No Event Abstraction**: Keine einheitliche Event-Schnittstelle <!-- .element: class="fragment" data-fragment-index="5" -->
* **Single Responsibility Violation**: OrderManager macht zu viel <!-- .element: class="fragment" data-fragment-index="6" -->

---

## Observer Pattern - Die Lösung

#### Observer Pattern - Das Event-System Fundament
**Intent**: Definiert eine eins-zu-viele Abhängigkeit zwischen Objekten, so dass bei Änderung eines Objekts alle abhängigen automatisch benachrichtigt werden.

**Problem**: Objekte sollen über Änderungen informiert werden, ohne fest gekoppelt zu sein.

**Solution**: Subject verwaltet Observer-Liste und benachrichtigt alle bei Zustandsänderungen.
<div class="two-column">
<div>

## Key Concepts
* **One-to-Many Abhängigkeit** - Ein Subject, viele Observer  <!-- .element: class="fragment" data-fragment-index="1" -->
* **Automatische Benachrichtigung** - Push-basierte Updates  <!-- .element: class="fragment" data-fragment-index="2" -->
* **Lose Kopplung** - Subject kennt Observer nicht konkret  <!-- .element: class="fragment" data-fragment-index="3" -->
* **Event-getriebene Architektur** - Basis für reactive Systems  <!-- .element: class="fragment" data-fragment-index="4" -->
* **Pub/Sub Implementierung** - Publisher-Subscriber Pattern  <!-- .element: class="fragment" data-fragment-index="5" --><div>

## Use Cases
* Event-Handling-Systeme  <!-- .element: class="fragment" data-fragment-index="6" -->
* Model-View Architekturen (MVC, MVVM)  <!-- .element: class="fragment" data-fragment-index="7" -->
* Notification Services  <!-- .element: class="fragment" data-fragment-index="8" -->
* Real-time Data Updates  <!-- .element: class="fragment" data-fragment-index="9" -->
* Reactive Programming  <!-- .element: class="fragment" data-fragment-index="10" --></div>

<!-- Speaker Notes: Observer Pattern ist das Fundament moderner Event-Systeme. In JavaScript ist es die Basis für DOM Events, Node.js EventEmitter und reaktive Programmierung mit RxJS. -->

---

# Observer Pattern - Implementation

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

<!-- Speaker Notes: Dieses Beispiel zeigt die Macht des Observer Patterns: Eine Nachrichtenagentur kann beliebig viele Kanäle benachrichtigen, ohne jeden Kanal explizit zu kennen. Neue Kanäle können zur Laufzeit hinzugefügt werden. -->

---

# Strategy Pattern

## Was ist hier falsch?

#### Strategy Pattern Problem
**Situation**: Verschiedene Algorithmen oder Geschäftsregeln müssen je nach Kontext ausgewählt werden. Switch-Statements und if-else-Kaskaden führen zu unübersichtlichem Code.

**Was sehen Sie hier Problematisches?**
```java
public class PriceCalculator {
    
    public double calculatePrice(Product product, String customerType, String season) {
        double basePrice = product.getBasePrice();
        
        // Code-Smell: Complex Conditional Logic
        if (customerType.equals("PREMIUM")) {
            if (season.equals("WINTER")) {
                // Premium winter pricing
                basePrice = basePrice * 0.8; // 20% discount
            } else if (season.equals("SUMMER")) {
                basePrice = basePrice * 0.85; // 15% discount
            } else {
                basePrice = basePrice * 0.9; // 10% discount
            }
        } else if (customerType.equals("BUSINESS")) {
            if (season.equals("WINTER")) {
                basePrice = basePrice * 0.85;
            } else if (season.equals("SUMMER")) {
                basePrice = basePrice * 0.9;
            } else {
                basePrice = basePrice * 0.95;
            }
        } else if (customerType.equals("REGULAR")) {
            if (season.equals("WINTER") && product.getCategory().equals("ELECTRONICS")) {
                basePrice = basePrice * 0.95;
            }
            // Regular customers: no discount in summer
        } else if (customerType.equals("STUDENT")) {
            // Student pricing logic
            basePrice = basePrice * 0.7; // Always 30% discount
        }
        
        // Weitere Business Rules...
        if (product.isNew() && !customerType.equals("PREMIUM")) {
            basePrice = basePrice * 1.1; // New product surcharge
        }
        
        return basePrice;
    }
    
    // DUPLICATE CODE: Ähnliche if-else Logik in anderen Methoden
    public boolean isEligibleForFreeShipping(String customerType, double orderValue) {
        if (customerType.equals("PREMIUM")) {
            return orderValue > 0; // Always free shipping
        } else if (customerType.equals("BUSINESS")) {
            return orderValue > 100;
        } else if (customerType.equals("REGULAR")) {
            return orderValue > 200;
        }
        return false;
    }
}
```

### Identifizierte Code-Smells

* **Complex Conditional Logic**: Verschachtelte if-else Statements schwer lesbar <!-- .element: class="fragment" data-fragment-index="1" -->
* **Open/Closed Principle Violation**: Neue Kunden-Typen erfordern Code-Änderungen <!-- .element: class="fragment" data-fragment-index="2" -->
* **Duplicate Logic**: Ähnliche if-else Strukturen in verschiedenen Methoden <!-- .element: class="fragment" data-fragment-index="3" -->
* **Hard to Test**: Viele Code-Pfade, exponentiell wachsende Test-Komplexität <!-- .element: class="fragment" data-fragment-index="4" -->
* **No Strategy Abstraction**: Keine einheitliche Algorithmus-Schnittstelle <!-- .element: class="fragment" data-fragment-index="5" -->
* **Long Method**: Methode wird bei neuen Regeln immer länger <!-- .element: class="fragment" data-fragment-index="6" -->

---

## Strategy Pattern - Die Lösung

#### Strategy Pattern - Algorithmus-Austauschbarkeit zur Laufzeit
**Intent**: Definiert eine Familie von Algorithmen, kapselt jeden einzeln und macht sie austauschbar.

**Problem**: Verschiedene Algorithmen sollen zur Laufzeit ausgewählt werden können.

**Solution**: Context delegiert an Strategy-Objekte, die Algorithmen implementieren.
<div class="two-column">
<div>

## Key Concepts
* **Familie von Algorithmen** - Verschiedene Implementierungen  <!-- .element: class="fragment" data-fragment-index="1" -->
* **Kapselung von Verhalten** - Strategy-Objekte isoliert  <!-- .element: class="fragment" data-fragment-index="2" -->
* **Laufzeit-Austausch** - Dynamische Strategie-Wahl  <!-- .element: class="fragment" data-fragment-index="3" -->
* **Open/Closed Prinzip** - Neue Strategien ohne Änderungen  <!-- .element: class="fragment" data-fragment-index="4" -->
* **Context-Delegation** - Context nutzt Strategy  <!-- .element: class="fragment" data-fragment-index="5" --><div>

## Use Cases
* Payment Processing Systems  <!-- .element: class="fragment" data-fragment-index="6" -->
* Sorting Algorithms  <!-- .element: class="fragment" data-fragment-index="7" -->
* Data Compression Methods  <!-- .element: class="fragment" data-fragment-index="8" -->
* Authentication Strategies  <!-- .element: class="fragment" data-fragment-index="9" -->
* Tax Calculation Systems  <!-- .element: class="fragment" data-fragment-index="10" --></div>

<!-- Speaker Notes: Strategy Pattern eliminiert komplexe if-else Strukturen und macht Code erweiterbar. In E-Commerce Systemen können neue Zahlungsmethoden hinzugefügt werden, ohne bestehenden Code zu ändern. -->

---

# Strategy Pattern - Payment System

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

<!-- Speaker Notes: Der Context (ShoppingCart) delegiert die Zahlungsabwicklung an die gewählte Strategy. Benutzer können zur Laufzeit die Zahlungsmethode wechseln, ohne dass sich am Context etwas ändert. -->

---

# Command Pattern

#### Command Pattern - Aktionen als Objekte kapseln
**Intent**: Kapselt Anfragen als Objekte und ermöglicht Parametrisierung, Queuing und Logging von Anfragen.

**Problem**: Aktionen sollen als First-Class Citizens behandelt und rückgängig gemacht werden können.

**Solution**: Commands kapseln Aktionen mit execute() und undo() Methoden.
<div class="two-column">
<div>

## Key Concepts
* **Aktion-Objekte** - Requests als First-Class Citizens  <!-- .element: class="fragment" data-fragment-index="1" -->
* **Undo/Redo Funktionalität** - Kommando-Rückgängigmachung  <!-- .element: class="fragment" data-fragment-index="2" -->
* **Macro-Commands** - Kombinierte Operationen  <!-- .element: class="fragment" data-fragment-index="3" -->

Note:
* Command Pattern ist fundamental für Undo/Redo-Funktionalität
* Sehr praktisch für GUI-Anwendungen, aber auch für API-Queuing
* Unterschied zu Strategy: Command kapselt komplette Operationen, nicht nur Algorithmen
* Zeigen Sie die Trennung von Invoker, Command und Receiver
* Frage: "Wo in Ihren Anwendungen wünschen Sie sich Undo-Funktionalität?"
<!-- .element: class="notes" -->
* **Queuing und Logging** - Kommandos verzögern/protokollieren  <!-- .element: class="fragment" data-fragment-index="9" -->
* **Invoker-Receiver Entkopplung** - Lose gekoppelte Ausführung  <!-- .element: class="fragment" data-fragment-index="10" --><div>

## Use Cases
* GUI Button Actions  <!-- .element: class="fragment" data-fragment-index="11" -->
* Undo/Redo Systems  <!-- .element: class="fragment" data-fragment-index="12" -->
* Macro Recording  <!-- .element: class="fragment" data-fragment-index="13" -->
* Transactional Operations  <!-- .element: class="fragment" data-fragment-index="14" -->
* Remote Procedure Calls  <!-- .element: class="fragment" data-fragment-index="15" -->
* Job Queues and Schedulers  <!-- .element: class="fragment" data-fragment-index="16" --></div>

<!-- Speaker Notes: Command Pattern ist essentiell für Undo/Redo-Funktionalität in Editoren, GUI-Anwendungen und Transaktionssystemen. Jede Aktion wird als Objekt gekapselt und kann rückgängig gemacht werden. -->

---

# Command Pattern - Smart Home System

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

<!-- Speaker Notes: Macro Commands zeigen die Flexibilität des Patterns - komplexe Operationen werden durch Kombination einfacher Commands erstellt. Smart Home Systemen nutzen dies für Szenarien wie "Gute Nacht" oder "Verlassen". -->

---

# Template Method Pattern

## Was ist hier falsch?

#### Template Method Pattern Problem
**Situation**: Ähnliche Algorithmen mit gleicher Struktur aber unterschiedlichen Implementierungsdetails führen zu massiver Code-Duplikation.

**Was sehen Sie hier Problematisches?**
```java
public class DataProcessor {
    
    public void processCsvData(String filePath) {
        // Code-Smell: Duplicate Algorithm Structure
        
        // Step 1: Validate file
        File file = new File(filePath);
        if (!file.exists() || !filePath.endsWith(".csv")) {
            throw new IllegalArgumentException("Invalid CSV file");
        }
        
        // Step 2: Open and read
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String[]> data = new ArrayList<>();
            
            // Step 3: Parse CSV format
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                data.add(fields);
            }
            
            // Step 4: Transform data (CSV-specific)
            for (String[] row : data) {
                // CSV-specific transformation logic
                processRow(row);
            }
            
            // Step 5: Save results
            saveToDatabase(data);
            
        } catch (IOException e) {
            throw new RuntimeException("Error processing CSV", e);
        }
    }
    
    // DUPLICATE CODE: Fast identische Struktur!
    public void processJsonData(String filePath) {
        // Step 1: Validate file (DUPLICATE)
        File file = new File(filePath);
        if (!file.exists() || !filePath.endsWith(".json")) {
            throw new IllegalArgumentException("Invalid JSON file");
        }
        
        // Step 2: Open and read (DUPLICATE)
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String content = reader.lines().collect(Collectors.joining());
            
            // Step 3: Parse JSON format (different parsing, same step)
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> data = mapper.readValue(content, List.class);
            
            // Step 4: Transform data (JSON-specific)
            for (Map<String, Object> row : data) {
                // JSON-specific transformation logic
                processJsonRow(row);
            }
            
            // Step 5: Save results (DUPLICATE)
            saveToDatabase(data);
            
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }
    
    // Noch mehr Duplikation für XML, Excel, etc...
    public void processXmlData(String filePath) {
        // Wieder die gleichen Steps 1, 2, 5 - nur Step 3 und 4 unterschiedlich
    }
}
```

### Identifizierte Code-Smells

* **Massive Code Duplication**: Gleiche Algorithmus-Struktur in jeder Methode <!-- .element: class="fragment" data-fragment-index="1" -->
* **Template Method Missing**: Keine Abstraktion für gemeinsame Schritte <!-- .element: class="fragment" data-fragment-index="2" -->
* **Violation of DRY**: Don't Repeat Yourself - aber hier wird alles wiederholt <!-- .element: class="fragment" data-fragment-index="3" -->
* **Hard to Maintain**: Änderungen am Algorithmus betreffen mehrere Methoden <!-- .element: class="fragment" data-fragment-index="4" -->
* **No Polymorphism**: Keine einheitliche Schnittstelle für verschiedene Formate <!-- .element: class="fragment" data-fragment-index="5" -->
* **Long Methods**: Jede Methode enthält den kompletten Algorithmus <!-- .element: class="fragment" data-fragment-index="6" -->

---

## Template Method Pattern - Die Lösung

#### Template Method Pattern - Algorithmus-Skelett mit variablen Schritten
**Intent**: Definiert das Skelett eines Algorithmus in einer Basisklasse und lässt Subklassen bestimmte Schritte neu definieren.

**Problem**: Algorithmus-Struktur soll gleich bleiben, aber einzelne Schritte variieren.

**Solution**: Basisklasse definiert Template Method, Subklassen implementieren abstrakte Schritte.
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

<!-- Speaker Notes: Template Method Pattern ist perfekt für ETL-Pipelines, Build-Prozesse und Datenverarbeitungs-Workflows. Die Struktur bleibt gleich, aber jeder Schritt kann unterschiedlich implementiert werden. -->

---

# Iterator Pattern

#### Iterator Pattern - Sequenzieller Zugriff ohne Struktur-Kenntnis
**Intent**: Bietet einen Weg, um auf Elemente einer Sammlung sequenziell zuzugreifen, ohne die zugrunde liegende Struktur preiszugeben.

**Problem**: Verschiedene Collections sollen einheitlich durchlaufen werden können.

**Solution**: Iterator-Interface abstrahiert den Zugriff auf Collection-Elemente.
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

<!-- Speaker Notes: Verschiedene Iterator-Implementierungen ermöglichen unterschiedliche Durchlauf-Strategien für die gleiche Collection. Streaming-Dienste nutzen ähnliche Patterns für Playlisten und Empfehlungs-Algorithmen. -->

---

# Chain of Responsibility

#### Chain of Responsibility - Anfragen durch Handler-Kette weiterleiten
**Intent**: Gibt mehreren Objekten die Chance, eine Anfrage zu behandeln, indem diese Objekte in einer Kette verknüpft werden.

**Problem**: Verschiedene Objekte sollen eine Anfrage verarbeiten können, ohne dass der Sender den konkreten Empfänger kennt.

**Solution**: Handler-Kette, wo jeder Handler entscheidet, ob er die Anfrage verarbeitet oder weiterleitet.
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

<!-- Speaker Notes: Diese Pipeline zeigt typische Web-Request-Verarbeitung: Authentication → Authorization → Business Logic. Jeder Handler ist unabhängig testbar und die Reihenfolge kann dynamisch angepasst werden. -->

---

# Behavioral Patterns Vergleich

<div class="interactive-question">

## Wann welches Pattern verwenden?
| Pattern | Verwendung | Vorteil |
|---------|------------|---------|
| **Observer** | Event-Systeme, MVC | Lose Kopplung bei 1:n  |
| **Strategy** | Algorithmus-Varianten | Runtime-Algorithmus-Wechsel  |
| **Command** | Undo/Redo, Queuing | Aktionen als Objekte  |
| **Template Method** | Algorithmus-Skelett | Code-Wiederverwendung  |
| **Iterator** | Collection-Durchlauf | Einheitlicher Zugriff  |
| **Chain of Responsibility** | Middleware, Pipelines | Flexible Handler-Ketten  |

<div class="highlight-box success">
**Entscheidungshilfe**: Jedes Pattern löst spezifische Kommunikations- und Interaktions-Probleme. Die Wahl hängt davon ab, ob Sie Events handhaben, Algorithmen austauschen oder Anfragen verarbeiten müssen.
</div>

<!-- Speaker Notes: Jedes Pattern löst spezifische Kommunikations- und Interaktions-Probleme. Die Wahl hängt davon ab, ob Sie Events handhaben, Algorithmen austauschen oder Anfragen verarbeiten müssen. -->

---

# Moderne JavaScript Implementierungen

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

<!-- Speaker Notes: Moderne JavaScript-Features wie Proxies, Generators und Async Iterators ermöglichen elegante Pattern-Implementierungen. Reactive Frameworks wie Vue.js nutzen Proxies für automatische Observer-Pattern-Implementierung. -->

---

---

# Best Practices

<div class="two-column">
<div>

## Behavioral Patterns richtig einsetzen

* **Event-First Design** - Observer für lose Kopplung nutzen  <!-- .element: class="fragment" data-fragment-index="1" -->
* **Strategy über If-Else** - Algorithmen austauschbar machen  <!-- .element: class="fragment" data-fragment-index="2" -->
* **Commands für Undo** - Aktionen reversibel gestalten  <!-- .element: class="fragment" data-fragment-index="3" -->
* **Templates für Workflows** - Gemeinsame Struktur teilen  <!-- .element: class="fragment" data-fragment-index="4" -->
* **Iterators für Collections** - Einheitliche Zugriffe schaffen  <!-- .element: class="fragment" data-fragment-index="5" -->
* **Chains für Pipelines** - Flexible Verarbeitung ermöglichen  <!-- .element: class="fragment" data-fragment-index="6" --><div>

## Anti-Patterns vermeiden

* **Over-Engineering** - Pattern nur bei echtem Nutzen  <!-- .element: class="fragment" data-fragment-index="7" -->
* **Observer-Leaks** - Subscription cleanup nicht vergessen  <!-- .element: class="fragment" data-fragment-index="8" -->
* **Command-Explosion** - Nicht jeden Setter als Command  <!-- .element: class="fragment" data-fragment-index="9" -->
* **Strategy-Overkill** - Einfache if-else manchmal besser  <!-- .element: class="fragment" data-fragment-index="10" -->
* **Chain-Overuse** - Nicht jede Sequenz braucht eine Kette  <!-- .element: class="fragment" data-fragment-index="11" --></div>

<div class="highlight-box warning">
**Goldene Regel**: Behavioral Patterns können schnell zu Over-Engineering führen. Verwenden Sie sie nur, wenn die Flexibilität wirklich benötigt wird. Observer-Pattern kann zu Memory Leaks führen, wenn Subscriptions nicht ordnungsgemäß aufgeräumt werden.
</div>

<!-- Speaker Notes: Behavioral Patterns können schnell zu Over-Engineering führen. Verwenden Sie sie nur, wenn die Flexibilität wirklich benötigt wird. Observer-Pattern kann zu Memory Leaks führen, wenn Subscriptions nicht ordnungsgemäß aufgeräumt werden. -->

---

# Verhaltensmuster Zusammenfassung

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

* **Observer Pattern** - Event-getriebene Architekturen  <!-- .element: class="fragment" data-fragment-index="1" -->
* **Strategy Pattern** - Algorithmus-Flexibilität zur Laufzeit  <!-- .element: class="fragment" data-fragment-index="2" -->
* **Command Pattern** - Undo/Redo und Action-Queuing  <!-- .element: class="fragment" data-fragment-index="3" -->
* **Template Method** - Workflow-Strukturen definieren  <!-- .element: class="fragment" data-fragment-index="4" -->
* **Iterator Pattern** - Einheitliche Collection-Zugriffe  <!-- .element: class="fragment" data-fragment-index="5" -->
* **Chain of Responsibility** - Flexible Request-Verarbeitung  <!-- .element: class="fragment" data-fragment-index="6" --><div>

## Nächste Schritte

* **Microservices** - Patterns in verteilten Systemen  <!-- .element: class="fragment" data-fragment-index="7" -->
* **Reactive Programming** - Observer Pattern erweitert  <!-- .element: class="fragment" data-fragment-index="8" -->
* **Event Sourcing** - Command Pattern in der Praxis  <!-- .element: class="fragment" data-fragment-index="9" -->
* **Advanced Patterns** - CQRS, Saga, Event-driven Architecture  <!-- .element: class="fragment" data-fragment-index="10" --></div>

<!-- Speaker Notes: Behavioral Patterns sind das Fundament moderner Softwarearchitekturen. Sie ermöglichen Event-getriebene Systeme, Microservices-Kommunikation und Reactive Programming. Die heute gelernten Patterns werden in praktisch jeder modernen Anwendung verwendet. -->

---

# Fragen & Diskussion

<div class="interactive-question">

## Ihre Erfahrungen mit Behavioral Patterns?
<div class="two-column">
<div>

### Diskussionspunkte:
* Wo haben Sie bereits diese Patterns gesehen?  <!-- .element: class="fragment" data-fragment-index="1" -->
* Welche Herausforderungen bei der Implementierung?  <!-- .element: class="fragment" data-fragment-index="2" -->
* Fragen zu spezifischen Use Cases?  <!-- .element: class="fragment" data-fragment-index="3" -->
* Pattern-Kombinationen in Ihren Projekten?  <!-- .element: class="fragment" data-fragment-index="4" --><div>

### Kontakt für Follow-up:
* **Workshop-Repository** mit allen Code-Beispielen  <!-- .element: class="fragment" data-fragment-index="5" -->
* **Best-Practice Checklisten** für jedes Pattern  <!-- .element: class="fragment" data-fragment-index="6" -->
* **Weiterführende Resources** und advanced topics  <!-- .element: class="fragment" data-fragment-index="7" -->
* **Community** für weitere Diskussionen  <!-- .element: class="fragment" data-fragment-index="8" --></div>

<div class="workshop-header">

**Vielen Dank für Ihre aktive Teilnahme!**
### Nächste Schritte: Erweiterte Patterns und Integration
<!-- Speaker Notes: Ermutigen Sie Diskussion über real-world Erfahrungen. Oft haben Teilnehmer bereits diese Patterns verwendet, ohne sie als solche zu erkennen. Sammeln Sie Feedback für zukünftige Workshop-Verbesserungen. -->