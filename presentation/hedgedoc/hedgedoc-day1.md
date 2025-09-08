---
title: Design Patterns Workshop - Day 1
description: Design Patterns Fundamentals - Singleton and Factory Method Patterns
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

<link rel="stylesheet" type="text/css" href="presentation-styles.css">

<div class="workshop-header">

# Day 1: Design Patterns Workshop
## Design Patterns Workshop

</div>

---

# Welcome & Introduction

## About This Workshop
- Practical design patterns training <!-- .element: class="fragment" -->
- Focus on enterprise architecture <!-- .element: class="fragment" -->
- Hands-on coding exercises <!-- .element: class="fragment" -->
- Real-world examples <!-- .element: class="fragment" -->
- Interactive discussions <!-- .element: class="fragment" -->

## Learning Objectives  
- Understand design pattern fundamentals <!-- .element: class="fragment" -->
- Implement Singleton and Factory patterns <!-- .element: class="fragment" -->
- Apply patterns to enterprise scenarios <!-- .element: class="fragment" -->
- Build pattern recognition skills <!-- .element: class="fragment" -->

**Speaker Notes:** Welcome everyone to the Design Patterns Workshop. This workshop focuses on practical application of design patterns in enterprise environments. We'll start with foundations and move to hands-on implementation.

---

# Workshop Agenda - Day 1

<div class="two-column">
<div>

## Morning Session (9:00 - 12:00)
- Design patterns fundamentals <!-- .element: class="fragment" -->
- Pattern benefits and categories <!-- .element: class="fragment" -->
- Singleton pattern deep dive <!-- .element: class="fragment" -->
- Hands-on Singleton exercise <!-- .element: class="fragment" -->

</div>
<div>

## Afternoon Session (13:00 - 17:00)
- Factory Method pattern <!-- .element: class="fragment" -->
- Implementation strategies <!-- .element: class="fragment" -->
- Enterprise use cases <!-- .element: class="fragment" -->
- Practical exercises <!-- .element: class="fragment" -->
- Q&A and discussion <!-- .element: class="fragment" -->

</div>
</div>

**Speaker Notes:** Day 1 covers fundamental patterns that form the basis for more complex patterns. We'll balance theory with practical implementation to ensure concepts stick.

---

# What Are Design Patterns?

<div class="pattern-definition">

#### Design Patterns Definition
**Intent**: Reusable solutions to common problems in software design.

**Problem**: Recurring design challenges that appear across different applications and contexts.

**Solution**: Proven approaches that capture best practices and provide a common vocabulary for developers.

</div>

<div class="two-column">
<div>

## Definition
- Reusable solutions to common problems <!-- .element: class="fragment" -->
- Proven approaches in software design <!-- .element: class="fragment" -->
- Communication tool between developers <!-- .element: class="fragment" -->
- Not code, but concepts and relationships <!-- .element: class="fragment" -->
- Context-specific implementations <!-- .element: class="fragment" -->

</div>
<div>

## Key Benefits
- Reduce development time <!-- .element: class="fragment" -->
- Improve code quality <!-- .element: class="fragment" -->
- Enable better communication <!-- .element: class="fragment" -->
- Facilitate maintenance <!-- .element: class="fragment" -->
- Promote best practices <!-- .element: class="fragment" -->

</div>
</div>

**Speaker Notes:** Design patterns are like architectural blueprints - they provide a template for solving common problems but must be adapted to specific contexts. They're not magic solutions but proven approaches.

---

# Pattern Categories Overview

<div class="two-column">
<div>

## Creational Patterns
- **Singleton** - Single instance control <!-- .element: class="fragment highlight-red" -->
- **Factory Method** - Object creation delegation <!-- .element: class="fragment highlight-red" -->
- Abstract Factory - Product families <!-- .element: class="fragment" -->
- Builder - Complex object construction <!-- .element: class="fragment" -->
- Prototype - Object cloning <!-- .element: class="fragment" -->

</div>
<div>

## Structural Patterns
- Adapter - Interface compatibility <!-- .element: class="fragment" -->
- Decorator - Dynamic behavior extension <!-- .element: class="fragment" -->
- Facade - Simplified interfaces <!-- .element: class="fragment" -->
- Composite - Tree structures <!-- .element: class="fragment" -->
- Proxy - Object representatives <!-- .element: class="fragment" -->

</div>
</div>

<div class="highlight-box accent">
**Today's Focus**: Creational patterns, specifically Singleton and Factory Method
</div>

**Speaker Notes:** Today we focus on Creational patterns, specifically Singleton and Factory Method. These are fundamental patterns that appear in most enterprise applications.

---

# When to Use Patterns

<div class="two-column">
<div>

## Good Reasons
- Solving recurring design problems <!-- .element: class="fragment highlight-green" -->
- Improving code communication <!-- .element: class="fragment highlight-green" -->
- Building flexible architectures <!-- .element: class="fragment highlight-green" -->
- Following team standards <!-- .element: class="fragment highlight-green" -->
- Learning from proven solutions <!-- .element: class="fragment highlight-green" -->

</div>
<div>

## Warning Signs
- Pattern for pattern's sake <!-- .element: class="fragment highlight-red" -->
- Over-engineering simple solutions <!-- .element: class="fragment highlight-red" -->
- Forcing patterns where unnecessary <!-- .element: class="fragment highlight-red" -->
- Ignoring performance implications <!-- .element: class="fragment highlight-red" -->
- Missing the problem context <!-- .element: class="fragment highlight-red" -->

</div>
</div>

<div class="highlight-box warning">
**Key Principle**: Patterns should solve real problems, not create complexity. Always ask "what problem am I solving?" before applying a pattern.
</div>

**Speaker Notes:** Patterns should solve real problems, not create complexity. Always ask "what problem am I solving?" before applying a pattern.

---

# Singleton Pattern Introduction

<div class="pattern-definition">

#### Singleton Pattern
**Intent**: Ensure a class has only one instance and provide global access to it.

**Problem**: You need exactly one instance of a class, and clients need global access to it.

**Solution**: Make the class responsible for keeping track of its sole instance.

</div>

<div class="two-column">
<div>

## Use Cases
- Database connections <!-- .element: class="fragment" -->
- Logging services <!-- .element: class="fragment" -->
- Configuration managers <!-- .element: class="fragment" -->
- Cache implementations <!-- .element: class="fragment" -->
- Resource managers <!-- .element: class="fragment" -->

</div>
<div>

## Key Characteristics
- Private constructor <!-- .element: class="fragment" -->
- Static instance variable <!-- .element: class="fragment" -->
- Static getInstance() method <!-- .element: class="fragment" -->
- Thread-safe implementation <!-- .element: class="fragment" -->
- Lazy or eager initialization <!-- .element: class="fragment" -->

</div>
</div>

**Speaker Notes:** Singleton is one of the most recognized patterns but also controversial. We'll explore both proper usage and common pitfalls.

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

**Speaker Notes:** This basic implementation shows the core concept. Notice the static instance, getInstance method, and constructor logic that prevents multiple instances.

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

**Speaker Notes:** In enterprise applications, thread safety is crucial. This implementation prevents race conditions during instance creation.

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

**Speaker Notes:** This example shows a realistic enterprise use case. Configuration is loaded once and shared across the application.

---

# Factory Method Pattern Introduction

<div class="pattern-definition">

#### Factory Method Pattern
**Intent**: Create objects without specifying exact classes, delegating creation to subclasses.

**Problem**: Need to create objects but want to defer instantiation to subclasses.

**Solution**: Define an interface for creating objects, let subclasses decide which class to instantiate.

</div>

<div class="two-column">
<div>

## Use Cases
- Creating UI components <!-- .element: class="fragment" -->
- Database driver selection <!-- .element: class="fragment" -->
- Logger implementations <!-- .element: class="fragment" -->
- Plugin architectures <!-- .element: class="fragment" -->
- Service instantiation <!-- .element: class="fragment" -->

</div>
<div>

## Key Benefits
- Loose coupling between creator and product <!-- .element: class="fragment" -->
- Easy extension with new products <!-- .element: class="fragment" -->
- Single responsibility principle <!-- .element: class="fragment" -->
- Open/closed principle compliance <!-- .element: class="fragment" -->

</div>
</div>

**Speaker Notes:** Factory Method promotes flexibility by removing direct class dependencies. Clients work with abstractions rather than concrete classes.

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

**Speaker Notes:** Notice the abstract base class defining the interface. Concrete implementations provide specific behavior while maintaining the same interface.

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

**Speaker Notes:** The factory encapsulates creation logic and can easily be extended. Notice how clients don't need to know about specific processor classes.

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

**Speaker Notes:** This enterprise example shows how factories enable plugin architectures and configuration-driven service creation.

---

# Day 1 Exercise 1: Singleton Implementation

<div class="interactive-question">

## Your Task
#### Implement a thread-safe Configuration Manager

</div>

<div class="two-column">
<div>

### Requirements
- Single instance guarantee <!-- .element: class="fragment" -->
- Thread-safe creation <!-- .element: class="fragment" -->
- Configuration loading from file/URL <!-- .element: class="fragment" -->
- Property access with defaults <!-- .element: class="fragment" -->
- Environment-specific configurations <!-- .element: class="fragment" -->
- Reset functionality for testing <!-- .element: class="fragment" -->

</div>
<div>

### Bonus Challenges
- Async configuration loading <!-- .element: class="fragment" -->
- Configuration validation <!-- .element: class="fragment" -->
- Change notification system <!-- .element: class="fragment" -->
- Hot configuration reload <!-- .element: class="fragment" -->
- Configuration encryption <!-- .element: class="fragment" -->

</div>
</div>

<div class="progress-indicator">
<div class="progress-step current">🛠️ Exercise Time: 30 minutes</div>
<div class="progress-step pending">👥 Work in pairs</div>
<div class="progress-step pending">🎯 Focus on core requirements first</div>
</div>

**Speaker Notes:** Work in pairs for 30 minutes. Focus on the core requirements first, then tackle bonus challenges if time permits.

---

# Day 1 Exercise 2: Factory Method Extension

<div class="interactive-question">

## Your Task
#### Create a Notification Service Factory

</div>

<div class="two-column">
<div>

### Required Notification Types
- Email notifications <!-- .element: class="fragment" -->
- SMS notifications  <!-- .element: class="fragment" -->
- Push notifications <!-- .element: class="fragment" -->
- Slack notifications <!-- .element: class="fragment" -->
- Teams notifications <!-- .element: class="fragment" -->

### Factory Features
- Provider registration system <!-- .element: class="fragment" -->
- Configuration-based creation <!-- .element: class="fragment" -->
- Fallback mechanism for failed providers <!-- .element: class="fragment" -->
- Batch notification support <!-- .element: class="fragment" -->

</div>
<div>

### Bonus Challenges
- Provider health checking <!-- .element: class="fragment" -->
- Load balancing between providers <!-- .element: class="fragment" -->
- Notification templates <!-- .element: class="fragment" -->
- Delivery confirmation <!-- .element: class="fragment" -->

</div>
</div>

**Speaker Notes:** This exercise reinforces the Factory pattern while introducing real-world complexity. Encourage students to think about error handling and scalability.

---

# Exercise Solutions Review

<div class="two-column">
<div>

## Key Learning Points
- Pattern implementation varies by context <!-- .element: class="fragment highlight-green" -->
- Thread safety considerations in enterprise apps <!-- .element: class="fragment highlight-green" -->
- Configuration management best practices <!-- .element: class="fragment highlight-green" -->
- Factory flexibility vs. complexity trade-offs <!-- .element: class="fragment highlight-green" -->
- Testing strategies for singleton patterns <!-- .element: class="fragment highlight-green" -->

</div>
<div>

## Common Implementation Issues
- Missing thread safety measures <!-- .element: class="fragment highlight-red" -->
- Over-complicated factory hierarchies <!-- .element: class="fragment highlight-red" -->
- Tight coupling between factory and products <!-- .element: class="fragment highlight-red" -->
- Inadequate error handling <!-- .element: class="fragment highlight-red" -->
- Missing configuration validation <!-- .element: class="fragment highlight-red" -->

</div>
</div>

**Speaker Notes:** Review 2-3 student solutions, highlighting both good practices and areas for improvement. Focus on practical enterprise considerations.

---

# Day 1 Summary

<div class="progress-indicator">
<div class="progress-step completed">✅ Design pattern fundamentals</div>
<div class="progress-step completed">✅ Singleton pattern implementation</div>
<div class="progress-step completed">✅ Factory Method pattern usage</div>
<div class="progress-step completed">✅ Practical exercises</div>
</div>

<div class="two-column">
<div>

## What We Covered
- Design pattern fundamentals <!-- .element: class="fragment" -->
- Singleton pattern implementation <!-- .element: class="fragment" -->
- Factory Method pattern usage <!-- .element: class="fragment" -->
- Enterprise architecture considerations <!-- .element: class="fragment" -->
- Practical coding exercises <!-- .element: class="fragment" -->

</div>
<div>

## Key Takeaways
- Patterns solve recurring problems <!-- .element: class="fragment" -->
- Implementation depends on context <!-- .element: class="fragment" -->
- Thread safety matters in enterprise apps <!-- .element: class="fragment" -->
- Factories promote loose coupling <!-- .element: class="fragment" -->
- Testing requires special consideration <!-- .element: class="fragment" -->

</div>
</div>

<div class="highlight-box accent">

## Tomorrow: Day 2 Preview
- Observer pattern for event handling
- Decorator pattern for feature extension  
- Real-world architectural patterns
- Advanced enterprise scenarios

</div>

**Speaker Notes:** Wrap up by reinforcing the key concepts and creating excitement for Day 2. Encourage students to think about how they might apply these patterns in their current projects.

---

# Questions & Discussion

<div class="two-column">
<div>

## Contact Information
**Email:** architecture-training@telekom.com  <!-- .element: class="fragment" -->
**Resources:** [Design Patterns Catalog](https://patterns.telekom.com)  <!-- .element: class="fragment" -->
**Code Examples:** Available in workshop repository <!-- .element: class="fragment" -->

</div>
<div>

## Next Steps
- Practice with your own examples <!-- .element: class="fragment" -->
- Identify pattern opportunities in current projects <!-- .element: class="fragment" -->
- Review additional resources <!-- .element: class="fragment" -->
- Prepare for Day 2 advanced patterns <!-- .element: class="fragment" -->

</div>
</div>

<div class="workshop-header">

**Thank you for your participation!**

</div>

**Speaker Notes:** Open the floor for questions. Common questions include: When NOT to use Singleton, Factory vs Abstract Factory differences, testing strategies, and performance considerations. Be prepared with practical enterprise examples.