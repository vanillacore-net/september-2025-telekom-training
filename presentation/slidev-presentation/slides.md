---
theme: default
highlighter: shiki
lineNumbers: true
transition: slide-left
title: Design Patterns Workshop
fonts:
  sans: 'Open Sans'
css: unocss
background: '#ffffff'
class: text-center
download: true
exportFilename: design-patterns-workshop
hideInToc: true
---

<style>
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;700&display=swap');

/* Telekom Brand Colors */
:root {
  --telekom-magenta: #E20074;
  --telekom-dark-gray: #333333;
  --telekom-light-gray: #666666;
  --telekom-bg-gray: #F5F5F5;
}

/* Global slide styling */
.slidev-layout {
  background: #ffffff;
  color: var(--telekom-dark-gray);
  font-family: 'Open Sans', sans-serif !important;
}

/* Default content slides */
.slidev-layout.default {
  padding: 60px 80px;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
}

/* Title slide layout - matching RevealJS exactly */
.slidev-layout.cover {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 2rem;
  height: 100vh;
}

.cover h1 {
  color: var(--telekom-dark-gray);
  font-size: 4.5vw !important; /* Responsive like RevealJS */
  font-weight: 400 !important;
  margin: 0;
  margin-bottom: 1rem;
  font-family: 'Open Sans', sans-serif !important;
  line-height: 1.2;
  transform: translateY(1rem); /* Match RevealJS optical balance */
}

.cover h2 {
  color: #666666; /* Match RevealJS gray */
  font-size: 2em !important; /* Match RevealJS subline */
  font-weight: 300 !important;
  margin-top: 20px;
  font-family: 'Open Sans', sans-serif !important;
  transform: translateY(1rem); /* Match RevealJS optical balance */
}

/* Section slide layout */
.slidev-layout.section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.section h1 {
  color: var(--telekom-dark-gray);
  font-size: 4.5vw !important;
  font-weight: 400 !important;
  font-family: 'Open Sans', sans-serif !important;
}

/* Content slides - corner logo */
.corner-logo {
  position: fixed !important;
  top: 20px !important;
  right: 30px !important;
  width: 60px !important; /* Smaller, more reasonable size */
  height: 60px !important;
  object-fit: contain !important;
  z-index: 100;
  opacity: 0.9;
}

/* Main logo for title/section slides - matching RevealJS */
.main-logo {
  max-width: 12vh !important; /* Match RevealJS responsive sizing */
  height: auto;
  margin-bottom: 2rem;
}

/* Two-column layout improvements */
.slidev-layout.two-cols .grid {
  gap: 2rem;
  height: 100%;
  align-items: start;
  padding: 0 1rem;
}

.slidev-layout.two-cols h1 {
  color: var(--telekom-dark-gray);
  font-size: 2rem !important; /* Fixed size for consistency */
  margin-bottom: 1.5rem;
  text-align: left;
  grid-column: span 2;
  font-family: 'Open Sans', sans-serif !important;
  font-weight: 400 !important;
}

/* Image-right layout */
.slidev-layout.image-right h1 {
  color: var(--telekom-dark-gray);
  font-size: 2rem !important; /* Fixed size for consistency */
  margin-bottom: 1.5rem;
  font-family: 'Open Sans', sans-serif !important;
  font-weight: 400 !important;
}

/* Code styling */
.slidev-code {
  background: var(--telekom-light-gray) !important;
  border-radius: 8px;
  font-size: 0.9rem;
}

/* Lists styling */
ul {
  font-size: inherit !important;
  line-height: 1.6;
  font-family: 'Open Sans', sans-serif !important;
  list-style: none;
  padding-left: 0;
}

li {
  margin-bottom: 0.75rem;
  font-size: 1rem !important; /* Normal readable size */
  font-family: 'Open Sans', sans-serif !important;
  font-weight: 300 !important;
  padding-left: 1.5rem;
  position: relative;
  color: var(--telekom-dark-gray);
}

li:before {
  content: "•";
  color: var(--telekom-dark-gray);
  font-weight: bold;
  position: absolute;
  left: 0;
}

/* Headings - h1 for slide titles */
h1 {
  color: var(--telekom-dark-gray);
  font-size: 2rem !important; /* Fixed size instead of vw */
  font-weight: 400 !important;
  margin-bottom: 1.5rem;
  text-align: left;
  font-family: 'Open Sans', sans-serif !important;
}

h2 {
  color: var(--telekom-dark-gray);
  font-size: 1.5rem !important;
  font-weight: 400 !important;
  margin-bottom: 1rem;
  font-family: 'Open Sans', sans-serif !important;
}

h3 {
  color: var(--telekom-dark-gray);
  font-size: 1.25rem !important;
  font-weight: 400 !important;
  margin-bottom: 0.75rem;
  font-family: 'Open Sans', sans-serif !important;
}

/* Paragraph and text styling */
p {
  color: var(--telekom-dark-gray);
  font-size: 1rem !important; /* Normal readable size */
  line-height: 1.6;
  font-family: 'Open Sans', sans-serif !important;
  font-weight: 300 !important;
  max-width: 95%;
  word-wrap: break-word;
}

/* Specific styles for content paragraphs */
.slidev-layout.default p {
  font-size: 1.25rem;
  line-height: 1.6;
  max-width: 85%;
  margin-bottom: 1rem;
}

/* Fragment animations */
.slidev-vclick-target {
  transition: opacity 0.3s ease;
}

.slidev-vclick-hidden {
  opacity: 0;
}

/* Center logo on title slides */
.title-logo-container {
  display: flex;
  justify-content: center;
  margin: 2rem 0;
}
</style>

![VanillaCore Logo](/images/VanillaCore_Vertical.png)

# Design Patterns Workshop

## Telekom Architecture Training

---
layout: section
hideInToc: true
---

![VanillaCore Logo](/images/VanillaCore_Vertical.png)

# Workshop Agenda

---

# Design Patterns in der Softwareentwicklung

Design Patterns sind bewährte Lösungsschablonen für wiederkehrende Entwurfsprobleme in der Softwareentwicklung. Sie beschreiben die Kommunikation zwischen Objekten und Klassen, die angepasst werden, um ein allgemeines Entwurfsproblem in einem bestimmten Kontext zu lösen.

<v-click>

Jedes Pattern beschreibt ein Problem, das immer wieder in unserer Umgebung auftritt, und dann den Kern der Lösung zu diesem Problem, auf eine Weise, dass Sie diese Lösung millionenfach anwenden können, ohne sie jemals auf die gleiche Weise zu implementieren.

</v-click>

![VanillaCore](/images/VanillaCore_Square.png)

---

---
layout: two-cols
---

# Patterns Übersicht

::left::

### Erstellungsmuster

<v-clicks>

- **Singleton** - Eine einzige Instanz
- **Factory Method** - Objekterstellung delegieren  
- **Abstract Factory** - Produktfamilien erstellen
- **Builder** - Komplexe Objekte schrittweise
- **Prototype** - Objekte durch Klonen erstellen

</v-clicks>

::right::

### Strukturmuster

<v-clicks>

- **Adapter** - Inkompatible Interfaces verbinden
- **Decorator** - Verhalten dynamisch erweitern
- **Facade** - Vereinfachte Schnittstelle
- **Composite** - Hierarchische Strukturen
- **Proxy** - Stellvertreter für andere Objekte

</v-clicks>

![VanillaCore](/images/VanillaCore_Square.png)

---
layout: image-right
image: /images/VanillaCore_Vertical.png
---

# Praxisbeispiele

Patterns in der Praxis:

<v-clicks>

- **MVC Architecture Pattern** - Model-View-Controller Trennung
- **Dependency Injection** - Lose Kopplung von Komponenten  
- **Repository Pattern** - Datenzugriff abstrahieren
- **Observer** - Event Handling implementieren
- **Strategy** - Algorithmus-Auswahl zur Laufzeit
- **Command** - Undo-Funktionalität ermöglichen
- **Template Method** - Workflow-Strukturen definieren

</v-clicks>

<img src="/images/VanillaCore_Square.png" alt="VanillaCore" class="corner-logo" />

---

# Singleton Pattern

<div class="grid grid-cols-2 gap-8">

<div>

Das Singleton Pattern stellt sicher, dass eine Klasse nur eine Instanz hat und bietet einen globalen Zugriffspunkt darauf.

<v-clicks>

- **Zentrale Ressourcenverwaltung** - Database connections, logging
- **Thread-sichere Implementierung** - Concurrent access protection
- **Lazy Loading Unterstützung** - Instanziierung bei Bedarf
- **Einfache `getInstance()` Methode** - Konsistenter Zugriff

</v-clicks>

</div>

<div>

```javascript {1|3-6|8-12|14-16|18-21}
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
      this.connection = "DB Connection Established";
      this.isConnected = true;
    }
    return this.connection;
  }
}

// Verwendung
const db1 = DatabaseConnection.getInstance();
const db2 = DatabaseConnection.getInstance();
console.log(db1 === db2); // true - Same instance
```

</div>

</div>

<img src="/images/VanillaCore_Square.png" alt="VanillaCore" class="corner-logo" />

---

# Factory Method Pattern

<div class="grid grid-cols-2 gap-8">

<div>

Das Factory Method Pattern definiert eine Schnittstelle für die Erstellung von Objekten, lässt aber Unterklassen entscheiden, welche Klasse instanziiert werden soll.

<v-clicks>

- **Flexible Objekterstellung** - Runtime-Entscheidungen
- **Lose Kopplung** - Client kennt konkrete Klassen nicht
- **Erweiterbarkeit** - Neue Produkttypen hinzufügen
- **Single Responsibility** - Erstellung von Verwendung trennen

</v-clicks>

</div>

<div>

```javascript {1-4|6-10|12-16|18-22|24-28|30-35}
// Abstract Product
class Logger {
  log(message) {
    throw new Error("Must implement log method");
  }
}

// Concrete Products
class ConsoleLogger extends Logger {
  log(message) {
    console.log(`Console: ${message}`);
  }
}

class FileLogger extends Logger {
  log(message) {
    console.log(`File: ${message}`);
  }
}

class DatabaseLogger extends Logger {
  log(message) {
    console.log(`Database: ${message}`);
  }
}

// Factory Method
class LoggerFactory {
  static createLogger(type) {
    switch(type) {
      case 'console': return new ConsoleLogger();
      case 'file': return new FileLogger();
      case 'database': return new DatabaseLogger();
      default: throw new Error(`Unknown logger type: ${type}`);
    }
  }
}

// Usage
const logger = LoggerFactory.createLogger('console');
logger.log('Application started');
```

</div>

</div>

<img src="/images/VanillaCore_Square.png" alt="VanillaCore" class="corner-logo" />

---

# Observer Pattern

<div class="grid grid-cols-2 gap-8">

<div>

Das Observer Pattern definiert eine one-to-many Abhängigkeit zwischen Objekten, sodass alle Abhängigen automatisch benachrichtigt werden, wenn sich der Zustand des Subjekts ändert.

<v-clicks>

- **Event-Driven Architecture** - Lose Kopplung zwischen Komponenten
- **Dynamic Relationships** - Observer zur Laufzeit hinzufügen/entfernen  
- **Broadcast Communication** - Ein Subjekt, viele Observer
- **Separation of Concerns** - Business Logic von Präsentation trennen

</v-clicks>

</div>

<div>

```javascript {1-8|10-18|20-28|30-40|42-48}
// Subject (Observable)
class NewsAgency {
  constructor() {
    this.observers = [];
    this.news = '';
  }
  
  subscribe(observer) {
    this.observers.push(observer);
  }
  
  unsubscribe(observer) {
    this.observers = this.observers.filter(obs => obs !== observer);
  }
  
  notifyAll() {
    this.observers.forEach(observer => observer.update(this.news));
  }
  
  setNews(news) {
    this.news = news;
    this.notifyAll();
  }
}

// Observer
class NewsChannel {
  constructor(name) {
    this.name = name;
  }
  
  update(news) {
    console.log(`${this.name} broadcasting: ${news}`);
  }
}

// Usage
const agency = new NewsAgency();
const cnn = new NewsChannel('CNN');
const bbc = new NewsChannel('BBC');

agency.subscribe(cnn);
agency.subscribe(bbc);

agency.setNews('Breaking: New software pattern discovered!');
// Output:
// CNN broadcasting: Breaking: New software pattern discovered!
// BBC broadcasting: Breaking: New software pattern discovered!
```

</div>

</div>

<img src="/images/VanillaCore_Square.png" alt="VanillaCore" class="corner-logo" />

---
layout: section
---

# Praktische Übungen

<div class="title-logo-container">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo" class="main-logo" />
</div>

---

# Übung 1: Singleton Implementation

Implementieren Sie ein Thread-sicheres Singleton Pattern für eine Konfigurationsklasse:

<div class="grid grid-cols-2 gap-8">

<div>

## Anforderungen

<v-clicks>

- **Thread Safety** - Mehrfacher Zugriff sicher handhaben
- **Lazy Loading** - Instanziierung nur bei Bedarf  
- **Configuration Loading** - Einstellungen aus Datei laden
- **Immutable Settings** - Konfiguration nicht veränderbar
- **Testing Support** - Reset-Funktionalität für Tests

</v-clicks>

</div>

<div>

```javascript
// Starter Code
class Configuration {
  constructor() {
    // TODO: Implement singleton logic
  }
  
  static getInstance() {
    // TODO: Thread-safe singleton creation
  }
  
  loadSettings(configFile) {
    // TODO: Load configuration from file
  }
  
  getSetting(key) {
    // TODO: Retrieve configuration value
  }
  
  // Test helper - only for testing!
  static resetInstance() {
    // TODO: Reset singleton for tests
  }
}

// Test your implementation
const config1 = Configuration.getInstance();
const config2 = Configuration.getInstance();
console.assert(config1 === config2);
```

</div>

</div>

<img src="/images/VanillaCore_Square.png" alt="VanillaCore" class="corner-logo" />

---

# Übung 2: Factory Method Extension

Erweitern Sie die Logger Factory um neue Logger-Typen:

<div class="grid grid-cols-2 gap-8">

<div>

## Neue Logger-Typen

<v-clicks>

- **EmailLogger** - Send logs via email
- **SlackLogger** - Post to Slack channel
- **MultiLogger** - Combine multiple loggers
- **FilteredLogger** - Log only specific levels
- **AsyncLogger** - Non-blocking logging

</v-clicks>

## Bonus Challenges

<v-clicks>

- **Configuration-based Factory** - Logger aus Config erstellen
- **Plugin Architecture** - Dynamisches Laden neuer Logger
- **Performance Monitoring** - Logging-Performance messen

</v-clicks>

</div>

<div>

```javascript
// Extend this factory
class LoggerFactory {
  static createLogger(config) {
    const { type, options = {} } = config;
    
    switch(type) {
      case 'console': 
        return new ConsoleLogger(options);
      case 'file': 
        return new FileLogger(options);
        
      // TODO: Add new logger types
      case 'email':
        // TODO: Implement EmailLogger
      case 'slack':
        // TODO: Implement SlackLogger
      case 'multi':
        // TODO: Implement MultiLogger
      case 'filtered':
        // TODO: Implement FilteredLogger
      case 'async':
        // TODO: Implement AsyncLogger
        
      default: 
        throw new Error(`Unknown logger: ${type}`);
    }
  }
  
  // TODO: Add configuration-based creation
  static createFromConfig(configFile) {
    // Load and parse config, create appropriate loggers
  }
}
```

</div>

</div>

<img src="/images/VanillaCore_Square.png" alt="VanillaCore" class="corner-logo" />

---
layout: section
---

# Zusammenfassung & Nächste Schritte

<div class="title-logo-container">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo" class="main-logo" />
</div>

---

# Key Takeaways

<div class="grid grid-cols-2 gap-8">

<div>

## Patterns Benefits

<v-clicks>

- **Wiederverwendbare Lösungen** - Bewährte Ansätze für häufige Probleme
- **Kommunikation verbessern** - Gemeinsame Sprache für Entwickler
- **Code-Qualität steigern** - Strukturierte und wartbare Implementierungen  
- **Best Practices fördern** - Etablierte Standards und Konventionen
- **Flexibilität erhöhen** - Anpassbare und erweiterbare Architekturen

</v-clicks>

</div>

<div>

## Implementation Guidelines

<v-clicks>

- **Don't Overengineer** - Patterns nur bei echtem Bedarf einsetzen
- **Context Matters** - Pattern an spezifische Anforderungen anpassen
- **Test Thoroughly** - Pattern-Implementierungen umfassend testen
- **Document Decisions** - Begründung für Pattern-Wahl dokumentieren
- **Team Alignment** - Gemeinsames Verständnis im Team schaffen

</v-clicks>

</div>

</div>

## Nächste Schritte

<v-clicks>

- **Advanced Patterns Workshop** - Composite, Chain of Responsibility, Visitor
- **Architectural Patterns** - MVC, MVP, MVVM, Clean Architecture
- **Microservice Patterns** - Service Discovery, Circuit Breaker, Saga
- **Performance Patterns** - Caching, Pooling, Lazy Loading
- **Testing Patterns** - Mock, Stub, Test Double, Page Object

</v-clicks>

<img src="/images/VanillaCore_Square.png" alt="VanillaCore" class="corner-logo" />

---
layout: end
---

# Vielen Dank!

## Fragen & Diskussion

<div class="title-logo-container">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo" class="main-logo" />
</div>

**Kontakt:** architecture-training@telekom.de  
**Weitere Ressourcen:** [Design Patterns Catalog](https://patterns.telekom.com)

*Professional software architecture with proven design patterns*