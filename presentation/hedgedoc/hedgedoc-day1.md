---
title: Design Patterns Workshop - Tag 1
description: Design Patterns Grundlagen - Singleton und Factory Method Patterns
tags: design-patterns, workshop, telekom, architecture, training, day1, singleton, factory
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

/* Critical: Fix content overflow and enforce left alignment */
.reveal .slides {
  font-size: 18px !important; /* Further reduced for better fit */
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
  overflow-y: auto !important; /* Allow scrolling if needed */
  overflow-x: hidden;
  padding: 20px !important; /* Add padding to prevent edge cutoff */
  box-sizing: border-box;
  text-align: left !important; /* Ensure all content is left-aligned */
}

.reveal h1 {
  font-size: 1.8em !important; /* Smaller headers */
  color: #2c2c2c;
  font-weight: 600 !important;
  text-align: left !important;
}

.reveal h2 {
  font-size: 1.4em !important;
  color: #2c2c2c;
  font-weight: 500 !important;
  text-align: left !important;
}

.reveal h3 {
  font-size: 1.2em !important;
  font-weight: 400 !important;
  text-align: left !important;
}

.reveal h4, .reveal h5, .reveal h6 {
  font-weight: 400 !important;
  text-align: left !important;
}

.reveal p, .reveal li {
  font-weight: 300 !important;
  text-align: left !important;
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
  text-align: left;
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
/* This targets paragraphs that contain speaker notes */
.reveal .slides section p {
  /* We cannot use :contains() as it's not standard CSS */
  /* Instead, we'll hide all p elements that might be speaker notes */
}

/* Hide .speaker-notes divs (class-based approach) */
.speaker-notes {
  display: none !important;
}

/* Hide reveal.js notes */
.reveal .notes {
  display: none !important;
}

/* A more direct approach: we'll need to convert **Speaker Notes:** to a class */
</style>

<div class="workshop-header">

# Tag 1: Design Patterns Workshop
## Design Patterns Workshop

</div>

---

# Willkommen & Einführung

## Über diesen Workshop
- Praxisorientierte Design Pattern Schulung <!-- .element: class="fragment" -->
- Fokus auf Enterprise-Architektur <!-- .element: class="fragment" -->
- Praktische Programmierübungen <!-- .element: class="fragment" -->
- Reale Beispiele aus der Praxis <!-- .element: class="fragment" -->
- Interaktive Diskussionen <!-- .element: class="fragment" -->

## Lernziele  
- Design Pattern Grundlagen verstehen <!-- .element: class="fragment" -->
- Singleton und Factory Pattern implementieren <!-- .element: class="fragment" -->
- Pattern in Enterprise-Szenarien anwenden <!-- .element: class="fragment" -->
- Pattern-Erkennungskompetenzen aufbauen <!-- .element: class="fragment" -->

<!-- Speaker Notes: Herzlich willkommen zum Design Patterns Workshop. Dieser Workshop konzentriert sich auf die praktische Anwendung von Design Patterns in Enterprise-Umgebungen. Wir beginnen mit den Grundlagen und gehen zu praktischen Implementierungen über. -->

---

# Workshop Agenda - Tag 1

<div class="two-column">
<div>

## Vormittagssession (9:00 - 12:00)
- Design Pattern Grundlagen <!-- .element: class="fragment" -->
- Pattern Vorteile und Kategorien <!-- .element: class="fragment" -->
- Singleton Pattern Tiefgang <!-- .element: class="fragment" -->
- Praktische Singleton Übung <!-- .element: class="fragment" -->

</div>
<div>

## Nachmittagssession (13:00 - 17:00)
- Factory Method Pattern <!-- .element: class="fragment" -->
- Implementierungsstrategien <!-- .element: class="fragment" -->
- Enterprise Anwendungsfälle <!-- .element: class="fragment" -->
- Praktische Übungen <!-- .element: class="fragment" -->
- Q&A und Diskussion <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Tag 1 behandelt fundamentale Pattern, die die Basis für komplexere Pattern bilden. Wir werden Theorie mit praktischer Implementierung ausbalancieren, um sicherzustellen, dass die Konzepte hängen bleiben. -->

---

# Was sind Design Patterns?

<div class="pattern-definition">

#### Design Patterns Definition
**Zweck**: Wiederverwendbare Lösungen für häufige Probleme im Software-Design.

**Problem**: Wiederkehrende Design-Herausforderungen, die in verschiedenen Anwendungen und Kontexten auftreten.

**Lösung**: Bewährte Ansätze, die Best Practices erfassen und ein gemeinsames Vokabular für Entwickler bereitstellen.

</div>

<div class="two-column">
<div>

## Definition
- Wiederverwendbare Lösungen für häufige Probleme <!-- .element: class="fragment" -->
- Bewährte Ansätze im Software-Design <!-- .element: class="fragment" -->
- Kommunikationswerkzeug zwischen Entwicklern <!-- .element: class="fragment" -->
- Nicht Code, sondern Konzepte und Beziehungen <!-- .element: class="fragment" -->
- Kontextspezifische Implementierungen <!-- .element: class="fragment" -->

</div>
<div>

## Zentrale Vorteile
- Reduzierte Entwicklungszeit <!-- .element: class="fragment" -->
- Verbesserte Code-Qualität <!-- .element: class="fragment" -->
- Bessere Kommunikation <!-- .element: class="fragment" -->
- Erleichterte Wartung <!-- .element: class="fragment" -->
- Förderung von Best Practices <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Design Patterns sind wie architektonische Blaupausen - sie bieten eine Vorlage für die Lösung häufiger Probleme, müssen aber an spezifische Kontexte angepasst werden. Sie sind keine magischen Lösungen, sondern bewährte Ansätze. -->

---

# Pattern Kategorien Überblick

<div class="two-column">
<div>

## Creational Patterns
- **Singleton** - Einzelinstanz-Kontrolle <!-- .element: class="fragment highlight-red" -->
- **Factory Method** - Objekt-Erstellungs-Delegation <!-- .element: class="fragment highlight-red" -->
- Abstract Factory - Produktfamilien <!-- .element: class="fragment" -->
- Builder - Komplexe Objekt-Konstruktion <!-- .element: class="fragment" -->
- Prototype - Objekt-Kloning <!-- .element: class="fragment" -->

</div>
<div>

## Structural Patterns
- Adapter - Interface-Kompatibilität <!-- .element: class="fragment" -->
- Decorator - Dynamische Verhaltenserweiterung <!-- .element: class="fragment" -->
- Facade - Vereinfachte Interfaces <!-- .element: class="fragment" -->
- Composite - Baumstrukturen <!-- .element: class="fragment" -->
- Proxy - Objekt-Repräsentanten <!-- .element: class="fragment" -->

</div>
</div>

<div class="highlight-box accent">
**Heutiger Fokus**: Creational Patterns, speziell Singleton und Factory Method
</div>

<!-- Speaker Notes: Heute fokussieren wir uns auf Creational Patterns, speziell Singleton und Factory Method. Das sind fundamentale Pattern, die in den meisten Enterprise-Anwendungen auftreten. -->

---

# Wann sollte man Patterns verwenden

<div class="two-column">
<div>

## Gute Gründe
- Lösung wiederkehrender Design-Probleme <!-- .element: class="fragment highlight-green" -->
- Verbesserung der Code-Kommunikation <!-- .element: class="fragment highlight-green" -->
- Aufbau flexibler Architekturen <!-- .element: class="fragment highlight-green" -->
- Befolgen von Team-Standards <!-- .element: class="fragment highlight-green" -->
- Lernen von bewährten Lösungen <!-- .element: class="fragment highlight-green" -->

</div>
<div>

## Warnsignale
- Pattern um des Patterns willen <!-- .element: class="fragment highlight-red" -->
- Über-Engineering einfacher Lösungen <!-- .element: class="fragment highlight-red" -->
- Erzwingen von Patterns wo unnötig <!-- .element: class="fragment highlight-red" -->
- Ignorieren von Performance-Auswirkungen <!-- .element: class="fragment highlight-red" -->
- Verpassung des Problem-Kontexts <!-- .element: class="fragment highlight-red" -->

</div>
</div>

<div class="highlight-box warning">
**Grundprinzip**: Patterns sollten echte Probleme lösen, nicht Komplexität schaffen. Fragen Sie immer "Welches Problem löse ich?" bevor Sie ein Pattern anwenden.
</div>

<!-- Speaker Notes: Patterns sollten echte Probleme lösen, nicht Komplexität schaffen. Fragen Sie immer "Welches Problem löse ich?" bevor Sie ein Pattern anwenden. -->

---

# Singleton Pattern Einführung

<div class="pattern-definition">

#### Singleton Pattern
**Zweck**: Sicherstellen, dass eine Klasse nur eine Instanz hat und globalen Zugriff darauf bieten.

**Problem**: Sie benötigen genau eine Instanz einer Klasse, und Clients benötigen globalen Zugriff darauf.

**Lösung**: Die Klasse übernimmt die Verantwortung für die Verwaltung ihrer einzigen Instanz.

</div>

<div class="two-column">
<div>

## Anwendungsfälle
- Datenbankverbindungen <!-- .element: class="fragment" -->
- Logging Services <!-- .element: class="fragment" -->
- Konfigurations-Manager <!-- .element: class="fragment" -->
- Cache-Implementierungen <!-- .element: class="fragment" -->
- Ressourcen-Manager <!-- .element: class="fragment" -->

</div>
<div>

## Schlüssel-Merkmale
- Private Konstruktor <!-- .element: class="fragment" -->
- Statische Instanz-Variable <!-- .element: class="fragment" -->
- Statische getInstance() Methode <!-- .element: class="fragment" -->
- Thread-sichere Implementierung <!-- .element: class="fragment" -->
- Lazy oder Eager Initialization <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Singleton ist eines der bekanntesten Patterns, aber auch umstritten. Wir werden sowohl die richtige Verwendung als auch häufige Fallstricke erforschen. -->

---

# Singleton Implementation - Basic

<div class="code-example">
<h5>Basic Singleton Pattern</h5>

```javascript
// Basic Singleton Pattern
class Logger { // fragment
  static instance = null; // fragment
  
  constructor() { // fragment
    if (Logger.instance) { // fragment
      return Logger.instance; // fragment
    } // fragment
    
    this.logs = []; // fragment
    Logger.instance = this; // fragment
  } // fragment
  
  static getInstance() { // fragment
    if (!this.instance) { // fragment
      this.instance = new Logger(); // fragment
    } // fragment
    return this.instance; // fragment
  } // fragment
  
  log(message) { // fragment
    const timestamp = new Date().toISOString(); // fragment
    this.logs.push(`${timestamp}: ${message}`); // fragment
  } // fragment
  
  getLogs() { // fragment
    return [...this.logs]; // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Diese grundlegende Implementierung zeigt das Kernkonzept. Beachten Sie die statische Instanz, die getInstance Methode und die Konstruktor-Logik, die mehrere Instanzen verhindert. -->

---

# Singleton Implementation - Thread Safe

<div class="code-example">
<h5>Thread-Safe Singleton with Lazy Loading</h5>

```javascript
// Thread-Safe Singleton with Lazy Loading
class DatabaseConnection { // fragment
  static instance = null; // fragment
  static isCreating = false; // fragment
  
  constructor() { // fragment
    if (DatabaseConnection.instance) { // fragment
      return DatabaseConnection.instance; // fragment
    } // fragment
    
    if (DatabaseConnection.isCreating) { // fragment
      throw new Error('Singleton creation in progress'); // fragment
    } // fragment
    
    DatabaseConnection.isCreating = true; // fragment
    this.connectionString = ''; // fragment
    this.isConnected = false; // fragment
    DatabaseConnection.instance = this; // fragment
    DatabaseConnection.isCreating = false; // fragment
  } // fragment
  
  static getInstance() { // fragment
    if (!this.instance && !this.isCreating) { // fragment
      this.instance = new DatabaseConnection(); // fragment
    } // fragment
    return this.instance; // fragment
  } // fragment
  
  connect(connectionString) { // fragment
    if (!this.isConnected) { // fragment
      this.connectionString = connectionString; // fragment
      this.isConnected = true; // fragment
      console.log('Database connected'); // fragment
    } // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: In Enterprise-Anwendungen ist Thread-Sicherheit entscheidend. Diese Implementierung verhindert Race Conditions bei der Instanz-Erstellung. -->

---

# Singleton - Enterprise Example

<div class="code-example">
<h5>Configuration Manager Singleton</h5>

```javascript
// Configuration Manager Singleton
class ConfigurationManager { // fragment
  static instance = null; // fragment
  
  constructor() { // fragment
    if (ConfigurationManager.instance) { // fragment
      return ConfigurationManager.instance; // fragment
    } // fragment
    
    this.config = {}; // fragment
    this.loaded = false; // fragment
    ConfigurationManager.instance = this; // fragment
  } // fragment
  
  static getInstance() { // fragment
    if (!this.instance) { // fragment
      this.instance = new ConfigurationManager(); // fragment
    } // fragment
    return this.instance; // fragment
  } // fragment
  
  async loadConfiguration(source) { // fragment
    if (!this.loaded) { // fragment
      // Simulate loading from external source // fragment
      this.config = await this.fetchConfig(source); // fragment
      this.loaded = true; // fragment
    } // fragment
  } // fragment
  
  getProperty(key, defaultValue = null) { // fragment
    return this.config[key] ?? defaultValue; // fragment
  } // fragment
  
  async fetchConfig(source) { // fragment
    // Simulate async config loading // fragment
    return { // fragment
      apiUrl: 'https://api.telekom.com', // fragment
      timeout: 5000, // fragment
      retryAttempts: 3 // fragment
    }; // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Dieses Beispiel zeigt einen realistischen Enterprise-Anwendungsfall. Die Konfiguration wird einmal geladen und über die gesamte Anwendung geteilt. -->

---

# Factory Method Pattern Einführung

<div class="pattern-definition">

#### Factory Method Pattern
**Zweck**: Objekte erstellen ohne exakte Klassen zu spezifizieren, Delegation der Erstellung an Subklassen.

**Problem**: Objekte müssen erstellt werden, aber die Instanziierung soll an Subklassen übergeben werden.

**Lösung**: Ein Interface für die Objekt-Erstellung definieren, Subklassen entscheiden welche Klasse instanziiert wird.

</div>

<div class="two-column">
<div>

## Anwendungsfälle
- Erstellung von UI-Komponenten <!-- .element: class="fragment" -->
- Datenbank-Treiber-Auswahl <!-- .element: class="fragment" -->
- Logger-Implementierungen <!-- .element: class="fragment" -->
- Plugin-Architekturen <!-- .element: class="fragment" -->
- Service-Instanziierung <!-- .element: class="fragment" -->

</div>
<div>

## Zentrale Vorteile
- Lose Kopplung zwischen Creator und Product <!-- .element: class="fragment" -->
- Einfache Erweiterung mit neuen Products <!-- .element: class="fragment" -->
- Single Responsibility Principle <!-- .element: class="fragment" -->
- Open/Closed Principle Konformität <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Factory Method fördert Flexibilität durch Entfernung direkter Klassenabhängigkeiten. Clients arbeiten mit Abstraktionen anstatt mit konkreten Klassen. -->

---

# Factory Method - Basic Structure

<div class="code-example">
<h5>Product Interface and Concrete Products</h5>

```javascript
// Product Interface
class PaymentProcessor { // fragment
  processPayment(amount) { // fragment
    throw new Error('Must implement processPayment method'); // fragment
  } // fragment
} // fragment

// Concrete Products
class CreditCardProcessor extends PaymentProcessor { // fragment
  processPayment(amount) { // fragment
    return `Processing $${amount} via Credit Card`; // fragment
  } // fragment
} // fragment

class PayPalProcessor extends PaymentProcessor { // fragment
  processPayment(amount) { // fragment
    return `Processing $${amount} via PayPal`; // fragment
  } // fragment
} // fragment

class BankTransferProcessor extends PaymentProcessor { // fragment
  processPayment(amount) { // fragment
    return `Processing $${amount} via Bank Transfer`; // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Beachten Sie die abstrakte Basisklasse, die das Interface definiert. Konkrete Implementierungen bieten spezifisches Verhalten bei Beibehaltung desselben Interfaces. -->

---

# Factory Method - Implementation

<div class="code-example">
<h5>Factory Method Implementation</h5>

```javascript
// Factory Method Implementation
class PaymentProcessorFactory { // fragment
  static createProcessor(type, config = {}) { // fragment
    switch(type) { // fragment
      case 'creditcard': // fragment
        return new CreditCardProcessor(config); // fragment
      case 'paypal': // fragment
        return new PayPalProcessor(config); // fragment
      case 'banktransfer': // fragment
        return new BankTransferProcessor(config); // fragment
      default: // fragment
        throw new Error(`Unknown payment processor: ${type}`); // fragment
    } // fragment
  } // fragment
  
  static getSupportedProcessors() { // fragment
    return ['creditcard', 'paypal', 'banktransfer']; // fragment
  } // fragment
  
  static createFromConfig(configObject) { // fragment
    const { type, settings } = configObject; // fragment
    return this.createProcessor(type, settings); // fragment
  } // fragment
} // fragment

// Usage
const processor = PaymentProcessorFactory.createProcessor('creditcard'); // fragment
const result = processor.processPayment(100.00); // fragment
console.log(result); // "Processing $100 via Credit Card" // fragment
```

</div>

<!-- Speaker Notes: Die Factory kapselt Erstellungslogik und kann leicht erweitert werden. Beachten Sie, wie Clients keine spezifischen Processor-Klassen kennen müssen. -->

---

# Factory Method - Enterprise Example

<div class="code-example">
<h5>Enterprise Service Factory</h5>

```javascript
// Enterprise Service Factory
class ServiceFactory { // fragment
  static services = new Map(); // fragment
  
  static registerService(name, serviceClass) { // fragment
    this.services.set(name, serviceClass); // fragment
  } // fragment
  
  static createService(name, config = {}) { // fragment
    const ServiceClass = this.services.get(name); // fragment
    
    if (!ServiceClass) { // fragment
      throw new Error(`Service '${name}' not registered`); // fragment
    } // fragment
    
    return new ServiceClass(config); // fragment
  } // fragment
  
  static createFromEnvironment(environmentConfig) { // fragment
    const services = {}; // fragment
    
    for (const [name, config] of Object.entries(environmentConfig)) { // fragment
      try { // fragment
        services[name] = this.createService(name, config); // fragment
      } catch (error) { // fragment
        console.warn(`Failed to create service ${name}: ${error.message}`); // fragment
      } // fragment
    } // fragment
    
    return services; // fragment
  } // fragment
} // fragment

// Register services
ServiceFactory.registerService('logger', ConsoleLogger); // fragment
ServiceFactory.registerService('database', PostgreSQLConnection); // fragment
ServiceFactory.registerService('cache', RedisCache); // fragment
```

</div>

<!-- Speaker Notes: Dieses Enterprise-Beispiel zeigt, wie Factories Plugin-Architekturen und konfigurationsgesteuerte Service-Erstellung ermöglichen. -->

---

# Tag 1 Übung 1: Singleton Implementierung

<div class="interactive-question">

## Ihre Aufgabe
#### Implementieren Sie einen thread-sicheren Configuration Manager

</div>

<div class="two-column">
<div>

### Anforderungen
- Einzelinstanz-Garantie <!-- .element: class="fragment" -->
- Thread-sichere Erstellung <!-- .element: class="fragment" -->
- Konfiguration laden von Datei/URL <!-- .element: class="fragment" -->
- Property-Zugriff mit Defaults <!-- .element: class="fragment" -->
- Umgebungsspezifische Konfigurationen <!-- .element: class="fragment" -->
- Reset-Funktionalität für Tests <!-- .element: class="fragment" -->

</div>
<div>

### Bonus Herausforderungen
- Asynchrones Konfigurationsladen <!-- .element: class="fragment" -->
- Konfigurations-Validierung <!-- .element: class="fragment" -->
- Änderungs-Benachrichtigungssystem <!-- .element: class="fragment" -->
- Hot Configuration Reload <!-- .element: class="fragment" -->
- Konfigurations-Verschlüsselung <!-- .element: class="fragment" -->

</div>
</div>

<div class="progress-indicator">
<div class="progress-step current">🛠️ Übungszeit: 30 Minuten</div>
<div class="progress-step pending">👥 Arbeiten Sie zu zweit</div>
<div class="progress-step pending">🎯 Fokus zuerst auf Kernanforderungen</div>
</div>

<!-- Speaker Notes: Arbeiten Sie 30 Minuten lang zu zweit. Konzentrieren Sie sich zuerst auf die Kernanforderungen, dann gehen Sie zu Bonus-Herausforderungen über, falls Zeit bleibt. -->

---

# Tag 1 Übung 2: Factory Method Erweiterung

<div class="interactive-question">

## Ihre Aufgabe
#### Erstellen Sie eine Notification Service Factory

</div>

<div class="two-column">
<div>

### Benötigte Notification-Typen
- E-Mail Benachrichtigungen <!-- .element: class="fragment" -->
- SMS Benachrichtigungen  <!-- .element: class="fragment" -->
- Push Benachrichtigungen <!-- .element: class="fragment" -->
- Slack Benachrichtigungen <!-- .element: class="fragment" -->
- Teams Benachrichtigungen <!-- .element: class="fragment" -->

### Factory Features
- Provider-Registrierungssystem <!-- .element: class="fragment" -->
- Konfigurationsbasierte Erstellung <!-- .element: class="fragment" -->
- Fallback-Mechanismus für fehlgeschlagene Provider <!-- .element: class="fragment" -->
- Batch-Notification-Unterstützung <!-- .element: class="fragment" -->

</div>
<div>

### Bonus Herausforderungen
- Provider Health Checking <!-- .element: class="fragment" -->
- Load Balancing zwischen Providern <!-- .element: class="fragment" -->
- Notification Templates <!-- .element: class="fragment" -->
- Zustellbestätigung <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Diese Übung verstärkt das Factory Pattern während sie reale Komplexität einführt. Ermutigen Sie Studenten, über Fehlerbehandlung und Skalierbarkeit nachzudenken. -->

---

# Übungslösungen Review

<div class="two-column">
<div>

## Zentrale Lernpunkte
- Pattern-Implementierung variiert je nach Kontext <!-- .element: class="fragment highlight-green" -->
- Thread-Safety-Überlegungen in Enterprise-Apps <!-- .element: class="fragment highlight-green" -->
- Best Practices für Konfigurations-Management <!-- .element: class="fragment highlight-green" -->
- Factory Flexibilität vs. Komplexitäts-Trade-offs <!-- .element: class="fragment highlight-green" -->
- Test-Strategien für Singleton Patterns <!-- .element: class="fragment highlight-green" -->

</div>
<div>

## Häufige Implementierungsprobleme
- Fehlende Thread-Safety Maßnahmen <!-- .element: class="fragment highlight-red" -->
- Über-komplizierte Factory-Hierarchien <!-- .element: class="fragment highlight-red" -->
- Enge Kopplung zwischen Factory und Products <!-- .element: class="fragment highlight-red" -->
- Unzureichende Fehlerbehandlung <!-- .element: class="fragment highlight-red" -->
- Fehlende Konfigurations-Validierung <!-- .element: class="fragment highlight-red" -->

</div>
</div>

<!-- Speaker Notes: Reviewen Sie 2-3 Student-Lösungen und heben sowohl gute Praktiken als auch Verbesserungsbereiche hervor. Fokussieren Sie sich auf praktische Enterprise-Überlegungen. -->

---

# Tag 1 Zusammenfassung

<div class="progress-indicator">
<div class="progress-step completed">✅ Design Pattern Grundlagen</div>
<div class="progress-step completed">✅ Singleton Pattern Implementierung</div>
<div class="progress-step completed">✅ Factory Method Pattern Anwendung</div>
<div class="progress-step completed">✅ Praktische Übungen</div>
</div>

<div class="two-column">
<div>

## Was wir behandelt haben
- Design Pattern Grundlagen <!-- .element: class="fragment" -->
- Singleton Pattern Implementierung <!-- .element: class="fragment" -->
- Factory Method Pattern Anwendung <!-- .element: class="fragment" -->
- Enterprise-Architektur-Überlegungen <!-- .element: class="fragment" -->
- Praktische Programmierübungen <!-- .element: class="fragment" -->

</div>
<div>

## Zentrale Erkenntnisse
- Patterns lösen wiederkehrende Probleme <!-- .element: class="fragment" -->
- Implementierung hängt vom Kontext ab <!-- .element: class="fragment" -->
- Thread-Safety ist wichtig in Enterprise-Apps <!-- .element: class="fragment" -->
- Factories fördern lose Kopplung <!-- .element: class="fragment" -->
- Testen erfordert besondere Überlegungen <!-- .element: class="fragment" -->

</div>
</div>

<div class="highlight-box accent">

## Morgen: Tag 2 Vorschau
- Observer Pattern für Event-Handling
- Decorator Pattern für Feature-Erweiterung  
- Reale architektonische Patterns
- Erweiterte Enterprise-Szenarien

</div>

<!-- Speaker Notes: Schließen Sie ab, indem Sie die Schlüsselkonzepte verstärken und Begeisterung für Tag 2 schaffen. Ermutigen Sie Studenten darüber nachzudenken, wie sie diese Patterns in ihren aktuellen Projekten anwenden könnten. -->

---

# Fragen & Diskussion

<div class="two-column">
<div>

## Kontaktinformationen
**E-Mail:** architecture-training@telekom.com  <!-- .element: class="fragment" -->
**Ressourcen:** [Design Patterns Catalog](https://patterns.telekom.com)  <!-- .element: class="fragment" -->
**Code-Beispiele:** Verfügbar im Workshop-Repository <!-- .element: class="fragment" -->

</div>
<div>

## Nächste Schritte
- Üben mit eigenen Beispielen <!-- .element: class="fragment" -->
- Pattern-Gelegenheiten in aktuellen Projekten identifizieren <!-- .element: class="fragment" -->
- Zusätzliche Ressourcen reviewen <!-- .element: class="fragment" -->
- Vorbereitung auf Tag 2 erweiterte Patterns <!-- .element: class="fragment" -->

</div>
</div>

<div class="workshop-header">

**Vielen Dank für Ihre Teilnahme!**

</div>

<!-- Speaker Notes: Öffnen Sie die Diskussion für Fragen. Häufige Fragen beinhalten: Wann Singleton NICHT verwenden, Unterschiede zwischen Factory vs Abstract Factory, Test-Strategien und Performance-Überlegungen. Seien Sie mit praktischen Enterprise-Beispielen vorbereitet. -->