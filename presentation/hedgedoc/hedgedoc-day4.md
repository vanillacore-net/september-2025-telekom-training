---
title: Software-Architektur - Tag 4
description: Erweiterte Architektur-Patterns - MVC/MVP/MVVM, Microservices, Performance, Testing und Pattern-Synthese
tags: design-patterns, workshop,  architecture, training, day4, advanced, mvc, mvvm, microservices, performance, testing, synthesis
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
  font-size: 3.6em !important; /* Increased by 20% for better readability */
  color: #2c2c2c;
  font-weight: 700 !important; /* Bolder weight for more prominence */
  text-align: left !important;
  margin-top: 0.8em !important;
  margin-bottom: 0.5em !important; /* More spacing below headline */
  border-bottom: 3px solid #666666 !important; /* Visual separator */
  padding-bottom: 0.2em !important; /* Padding above border */
}

/* First heading on slide should not have top margin */
.reveal .slides section > h1:first-child {
  margin-top: 0 !important;
}

.reveal h2 {
  font-size: 2.2em !important;
  color: #2c2c2c !important;
  font-weight: normal !important;
  text-align: left !important;
  margin-top: 0.8em !important;
  margin-bottom: 0.4em !important;
  width: 100% !important;
}

/* First heading on slide should not have top margin */
.reveal .slides section > h2:first-child {
  margin-top: 0 !important;
}

.reveal h3 {
  font-size: 1.6em !important; /* Increased by 20% for better readability */
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
  font-size: 1.2em !important;
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

/* Code blocks sizing - Full Width Optimized with Overflow Prevention */
.reveal pre {
  font-size: 1.2em !important; /* Increased by 20% for better readability */
  max-height: calc(100vh - 200px); /* Use full available screen height */
  max-width: 100% !important; /* Prevent horizontal overflow */
  overflow-x: auto !important; /* Allow horizontal scrolling if needed */
  overflow-y: auto !important; /* Allow vertical scrolling if needed */
  white-space: pre-wrap !important; /* Wrap long lines */
  word-wrap: break-word !important; /* Break long words */
  background: #2d3748 !important; /* Dark background for better contrast */
  color: #e2e8f0 !important; /* Light text for contrast */
  border: 1px solid #4a5568; /* Subtle darker border */
  width: 88% !important; /* Use most of screen width */
  margin-left: auto !important;
  margin-right: auto !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2) !important;
  box-sizing: border-box !important;
}

.reveal pre code {
  font-size: 1.2em !important; /* Increased by 20% for better readability */
  line-height: 1.3 !important;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace !important;
  color: #e2e8f0 !important;
  background: transparent !important;
  padding: 0 !important;
  max-width: 100% !important;
  overflow-x: auto !important;
  white-space: pre-wrap !important;
  word-wrap: break-word !important;
}

.reveal code {
  font-size: 1.2em !important; /* Increased by 20% for better readability */
  background: #f0f0f0 !important;
  color: #d73a49 !important;
  padding: 0.1em 0.3em !important;
  border-radius: 3px !important;
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

/* Hide reveal.js built-in notes */
.reveal .notes,
.reveal aside.notes,
.reveal .speaker-notes {
  display: none !important;
}

/* Hide any element with "notes" class */
.notes, 
.speaker-notes,
.presentation-notes {
  display: none !important;
}

/* More aggressive hiding of Note: content */
.reveal .slides section p:first-child {
  /* Check if this paragraph starts with Note: and hide it */
}

/* Hide elements marked with class="notes" */
.element.notes {
  display: none !important;
}

/* CRITICAL: Hide speaker notes patterns - enhanced selector */
/* Hide any paragraph starting with "Note:" and following content until next section */
.reveal .slides section p:first-line:contains("Note:") {
  display: none !important;
}

/* Hide content that comes after "Note:" on slides */
.reveal .slides section p + ul,
.reveal .slides section p + ol {
  /* Only hide lists that follow paragraphs starting with Note: */
}

/* More aggressive approach: hide all speaker note blocks */
.reveal .slides section *:has(+ *[class*="notes"]),
.reveal .slides section *[class*="notes"] + *,
.reveal .slides section *[class*="notes"] {
  display: none !important;
}

/* Target specific speaker notes pattern: Note: followed by list */
.reveal .slides section {
  /* JS will need to handle the Note: + list pattern */
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

.reveal .fragment {
  visibility: hidden;
  opacity: 0;
  transition: opacity 0.3s ease, visibility 0s linear 0.3s;
}

.reveal .fragment.visible {
  visibility: visible;
  opacity: 1;
  transition: opacity 0.3s ease;
}
</style>


<div class="workshop-header title-slide">

<div class="vanilla-logo">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo">
</div>

# Software-Architektur
## Bring your own brain and use it!
### Tag 4: Erweiterte Architektur-Patterns

</div>

Note:
- Willkommen zum finalen Tag - Integration und Synthese aller Patterns
- Heute: Von einzelnen Patterns zu kompletten Architekturen
- Kurze Reflexion: Was haben wir von Tag 1-3 gelernt?
- Fokus auf reale Enterprise-Anwendungen und Microservice-Architekturen
- Verwenden Sie finale Pattern-Synthese für komplexe Architektur-Diskussion
- Betonen Sie: Pattern-Integration und Anti-Pattern-Vermeidung
<!-- .element: class="notes" -->

---

# Tag 4 Agenda

<div class="progress-indicator">
<div class="progress-step current">📍 Architektur-Patterns - MVC, MVP, MVVM
<!-- .element: class="fragment" -->
<div class="progress-step pending">⏳ Microservice Patterns - Service Discovery, Circuit Breaker
<!-- .element: class="fragment" -->
<div class="progress-step pending">⏳ Performance Patterns - Caching, Pooling, Lazy Loading
<!-- .element: class="fragment" -->
<div class="progress-step pending">⏳ Testing Patterns - Mock, Stub, Test Doubles
<!-- .element: class="fragment" -->
<div class="progress-step pending">⏳ Pattern Synthese & Anti-Patterns
<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

## Erweiterte Themen Überblick

- **Architektur-Patterns** - MVC, MVP, MVVM Tiefgang
<!-- .element: class="fragment" -->
- **Microservice Patterns** - Service Discovery, Circuit Breaker, Saga
<!-- .element: class="fragment" -->
- **Performance Patterns** - Caching-Strategien, Object Pooling, Lazy Loading
<!-- .element: class="fragment" -->
- **Testing Patterns** - Mock, Stub, Test Double Implementierungen
<!-- .element: class="fragment" -->
- **Pattern-Kombinationen** - Reale architektonische Lösungen
<!-- .element: class="fragment" -->
- **Anti-Patterns** - Häufige Fehler und wie man sie vermeidet
<!-- .element: class="fragment" -->
- **Workshop Zusammenfassung** - Vier Tage Lernsynthese
<!-- .element: class="fragment" -->

<!-- Speaker Notes: Die heutige Agenda baut auf den fundamentalen Patterns aus Tag 1-3 auf. Wir werden sehen, wie grundlegende Creational-, Structural- und Behavioral-Patterns sich kombinieren, um robuste Enterprise-Architekturen zu schaffen. Jeder Abschnitt enthält praktische Beispiele und reale Enterprise-Anwendungsfälle. -->

---

# Architektur-Patterns Tiefgang

<div class="pattern-definition">

#### Model-View-Controller (MVC)
**Zweck**: Trennt Anwendungslogik in drei miteinander verbundene Komponenten für bessere Organisation und Testbarkeit.

**Problem**: Monolithische Anwendungen sind schwer zu testen und zu warten.

**Lösung**: Klare Trennung zwischen Datenlogik (Model), Präsentation (View) und Eingabesteuerung (Controller).


<!-- .element: class="fragment" -->

<div class="two-column">
<div>

## MVC Komponenten
- **Model** - Geschäftslogik und Datenverwaltung
<!-- .element: class="fragment" -->
- **View** - Benutzeroberfläche und Präsentationsschicht
<!-- .element: class="fragment" -->
- **Controller** - Eingabebehandlung und Ablaufsteuerung
<!-- .element: class="fragment" -->
- **Vorteile** - Klare Trennung der Belange, Testbarkeit
<!-- .element: class="fragment" -->
- **Anwendungsfälle** - Webanwendungen, Desktop-Software
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->
<div>

## Enterprise-Anwendungen
- **Kundenportal** - Benutzerkonto-Verwaltung
<!-- .element: class="fragment" -->
- **Abrechnungssystem** - Rechnungsbearbeitung
<!-- .element: class="fragment" -->
- **Service-Management** - Service-Konfiguration
<!-- .element: class="fragment" -->
- **Admin-Dashboards** - System-Monitoring
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<!-- Speaker Notes: MVC ist grundlegend für moderne Web-Frameworks. Beachten Sie, wie das Observer Pattern von Tag 3 die Model-View-Kommunikation ermöglicht. Diese Trennung erlaubt es Teams, unabhängig an verschiedenen Schichten zu arbeiten. -->

---

# MVC Implementation Example

<div class="code-example">
<h5>MVC with Observer Pattern Integration</h5>

```javascript
// MVC Implementation Example
class UserModel {
  constructor() {    this.users = [];    this.observers = [];  }  
  addUser(user) {    this.users.push(user);    this.notifyObservers();  }  
  getUsers() {    return this.users;  }  
  notifyObservers() {    this.observers.forEach(observer => observer.update(this.users));  }}
class UserView {  update(users) {    console.log('Updated user list:', users);    // Update DOM representation    this.renderUserList(users);  }  
  renderUserList(users) {    const userList = document.getElementById('users');    userList.innerHTML = users.map(user =>       `<div class="user">${user.name} - ${user.email}</div>`    ).join('');  }}
class UserController {  constructor(model, view) {    this.model = model;    this.view = view;    this.model.observers.push(view);  }  
  addUser(userData) {    // Validate input    if (this.validateUser(userData)) {      this.model.addUser(userData);    }  }  
  validateUser(userData) {    return userData.name && userData.email;  }}
// Usage
const model = new UserModel();const view = new UserView();const controller = new UserController(model, view);
controller.addUser({ name: 'Max Mueller', email: 'max@company.com' });```


<!-- .element: class="fragment" -->

<!-- Speaker Notes: Notice how the Observer pattern enables automatic View updates when the Model changes. This loose coupling is essential for maintaining large applications. -->

---

# Model-View-Presenter (MVP)

<div class="pattern-definition">

#### Model-View-Presenter (MVP)
**Intent**: Verfeinert MVC durch eine passivere View und einen Presenter, der die gesamte UI-Logik behandelt.

**Problem**: MVC Views können zu komplex werden und sind schwer zu testen.

**Solution**: Presenter übernimmt alle UI-Logik, View wird zur passiven Schnittstelle.


<!-- .element: class="fragment" -->

<div class="two-column">
<div>

## MVP Components
- **Model** - Pure business logic, no UI knowledge
<!-- .element: class="fragment" -->
- **View** - Passive interface, delegates to Presenter
<!-- .element: class="fragment" -->
- **Presenter** - All UI logic and user interaction handling
<!-- .element: class="fragment" -->
- **Benefits** - Highly testable, clear responsibilities
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->
<div>

## Key Differences from MVC
- View doesn't directly observe Model
<!-- .element: class="fragment" -->
- Presenter mediates all Model-View communication
<!-- .element: class="fragment" -->
- Better testability (Presenter can be unit tested)
<!-- .element: class="fragment" -->
- More explicit control flow
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<!-- Speaker Notes: MVP makes testing easier because the Presenter can be unit tested without a real View. This pattern is excellent for complex UI logic where you need granular control over user interactions. -->

---

# Model-View-ViewModel (MVVM)

<div class="pattern-definition">

#### Model-View-ViewModel (MVVM)
**Intent**: Nutzt Data Binding um View und ViewModel automatisch zu synchronisieren.

**Problem**: Manuelle UI-Updates sind fehleranfällig und führen zu Boilerplate-Code.

**Solution**: Zwei-Wege Data Binding zwischen View und ViewModel eliminiert manuelle Updates.


<!-- .element: class="fragment" -->

<div class="code-example">
<h5>MVVM with Proxy-based Data Binding</h5>

```javascript
// MVVM Implementation with Proxy for data binding
class UserViewModel {  constructor(model) {    this.model = model;    this.users = [];    this.newUser = { name: '', email: '' };    this.isLoading = false;    
    // Create observable properties    return this.createObservableViewModel();  }  
  createObservableViewModel() {    const self = this;    
    return new Proxy(this, {      set(target, property, value) {        target[property] = value;        self.notifyViewUpdate(property, value);        return true;      }    });  }  
  notifyViewUpdate(property, value) {    // Emit change event for view binding    document.dispatchEvent(new CustomEvent('viewModelChanged', {      detail: { property, value, viewModel: this }    }));  }  
  async addUser() {    this.isLoading = true;    
    try {      const users = await this.model.saveUser(this.newUser);      this.users = users;      this.newUser = { name: '', email: '' }; // Reset form    } catch (error) {      console.error('Add user failed:', error);    } finally {      this.isLoading = false;    }  }}
// View with automatic binding
class MVVMView {  constructor(viewModel) {    this.viewModel = viewModel;    this.setupBindings();  }  
  setupBindings() {    // Listen for ViewModel changes    document.addEventListener('viewModelChanged', (event) => {      this.handleViewModelChange(event.detail);    });    
    // Setup form bindings    const nameInput = document.getElementById('userName');    const emailInput = document.getElementById('userEmail');    
    nameInput.addEventListener('input', (e) => {      this.viewModel.newUser.name = e.target.value;    });    
    emailInput.addEventListener('input', (e) => {      this.viewModel.newUser.email = e.target.value;    });  }  
  handleViewModelChange({ property, value }) {    switch(property) {      case 'users':        this.renderUsers(value);        break;      case 'isLoading':        this.toggleLoading(value);        break;      case 'newUser':        this.updateFormFields(value);        break;    }  }}```


<!-- .element: class="fragment" -->

<!-- Speaker Notes: MVVM shines in scenarios with complex UI state management. Modern frameworks like Angular, Vue, and React implement variations of MVVM. The key insight is automatic synchronization between data and UI. -->

---

# Microservice Patterns

<div class="pattern-definition">

#### Service Discovery Pattern
**Intent**: Ermöglicht Microservices, sich dynamisch zu finden und miteinander zu kommunizieren.

**Problem**: In einer Microservice-Architektur müssen Services andere Services finden, ohne fest gekoppelt zu sein.

**Solution**: Zentrales Service Registry mit Health Checks und Load Balancing.


<!-- .element: class="fragment" -->

<div class="two-column">
<div>

## Key Components
- **Service Registry** - Central directory of available services
<!-- .element: class="fragment" -->
- **Health Checks** - Monitor service availability
<!-- .element: class="fragment" -->
- **Load Balancing** - Distribute requests across instances
<!-- .element: class="fragment" -->
- **Failure Handling** - Graceful degradation when services fail
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->
<div>

## Benefits
- **Dynamic Scaling** - Services can be added/removed at runtime
<!-- .element: class="fragment" -->
- **High Availability** - Automatic failover to healthy instances
<!-- .element: class="fragment" -->
- **Location Transparency** - Services don't need to know physical locations
<!-- .element: class="fragment" -->
- **Configuration Management** - Centralized service configuration
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<!-- Speaker Notes: Service Discovery is fundamental to scalable microservice architectures. In enterprise environments, we use this pattern for our API gateway and internal service communication. -->

---

# Circuit Breaker Pattern

<div class="pattern-definition">

#### Circuit Breaker Pattern
**Intent**: Verhindert kaskadierende Ausfälle durch Überwachung und Unterbrechung von Serviceaufrufen bei Fehlern.

**Problem**: Ein fehlerhafter Service kann das gesamte System zum Absturz bringen.

**Solution**: Circuit Breaker überwacht Service-Calls und öffnet bei Fehlern, um weitere Calls zu verhindern.


<!-- .element: class="fragment" -->

<div class="code-example">
<h5>Circuit Breaker Implementation</h5>

```javascript
// Circuit Breaker Pattern Implementation
class CircuitBreaker {  constructor(service, options = {}) {    this.service = service;    this.failureThreshold = options.failureThreshold || 5;    this.resetTimeout = options.resetTimeout || 30000; // 30 seconds    this.state = 'CLOSED'; // CLOSED, OPEN, HALF_OPEN    this.failureCount = 0;    this.lastFailureTime = null;  }  
  async call(method, ...args) {    if (this.state === 'OPEN') {      if (this.shouldAttemptReset()) {        this.state = 'HALF_OPEN';      } else {        throw new Error('Circuit breaker is OPEN');      }    }    
    try {      const result = await this.service[method](...args);      this.onSuccess();      return result;    } catch (error) {      this.onFailure(error);      throw error;    }  }  
  shouldAttemptReset() {    return Date.now() - this.lastFailureTime >= this.resetTimeout;  }  
  onSuccess() {    this.failureCount = 0;    this.state = 'CLOSED';  }  
  onFailure(error) {    this.failureCount++;    this.lastFailureTime = Date.now();    
    if (this.failureCount >= this.failureThreshold) {      this.state = 'OPEN';      console.log(`Circuit breaker opened due to ${this.failureCount} failures`);    }  }  
  getState() {    return {      state: this.state,      failureCount: this.failureCount,      lastFailureTime: this.lastFailureTime    };  }}
// Usage with external service
class PaymentService {  async processPayment(amount) {    // Simulate occasional failures    if (Math.random() < 0.3) {      throw new Error('Payment service unavailable');    }    
    return { success: true, amount, transactionId: Date.now() };  }}
const paymentService = new PaymentService();const protectedPaymentService = new CircuitBreaker(paymentService, {  failureThreshold: 3,  resetTimeout: 10000});
// Protected service calls
try {  const result = await protectedPaymentService.call('processPayment', 100);  console.log('Payment processed:', result);} catch (error) {  console.log('Payment failed:', error.message);  console.log('Circuit breaker state:', protectedPaymentService.getState());}```


<!-- .element: class="fragment" -->

<!-- Speaker Notes: Circuit Breaker prevents cascading failures in distributed systems. It's essential for resilient microservice architectures and is implemented in many service mesh solutions. -->

---

# Performance Patterns

<div class="two-column">
<div>

## Caching Strategies

### Cache-Aside Pattern
```javascript
class CacheAsideRepository {  constructor(cache, database) {    this.cache = cache;    this.database = database;  }  
  async get(key) {    // Try cache first    let data = await this.cache.get(key);    
    if (!data) {      // Cache miss - get from database      data = await this.database.get(key);      if (data) {        await this.cache.set(key, data);      }    }    
    return data;  }  
  async update(key, data) {    // Update database first    await this.database.update(key, data);    // Invalidate cache    await this.cache.delete(key);  }}```


<!-- .element: class="fragment" -->
<div>

## Object Pool Pattern

```javascript
class ConnectionPool {  constructor(createFn, maxSize = 10) {    this.createConnection = createFn;    this.maxSize = maxSize;    this.available = [];    this.inUse = new Set();  }  
  async acquire() {    if (this.available.length > 0) {      const conn = this.available.pop();      this.inUse.add(conn);      return conn;    }    
    if (this.inUse.size < this.maxSize) {      const conn = await this.createConnection();      this.inUse.add(conn);      return conn;    }    
    throw new Error('Pool exhausted');  }  
  release(connection) {    if (this.inUse.has(connection)) {      this.inUse.delete(connection);      this.available.push(connection);    }  }}```


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<!-- Speaker Notes: Performance patterns are crucial for enterprise applications. Caching strategies can dramatically improve response times, while object pools manage expensive resources like database connections. -->

---

# Testing Patterns

<div class="pattern-definition">

#### Mock, Stub, and Test Double Patterns
**Intent**: Isoliert Units Under Test durch Ersatz von Abhängigkeiten mit kontrollierten Test-Implementierungen.

**Problem**: Unit Tests sollen isoliert und vorhersagbar sein, aber reale Abhängigkeiten sind komplex.

**Solution**: Test Doubles ersetzen reale Objekte mit testbaren Alternativen.


<!-- .element: class="fragment" -->

<div class="code-example">
<h5>Test Doubles Implementation</h5>

```javascript
// Mock Pattern - Verifies interactions
class MockEmailService {  constructor() {    this.calls = [];  }  
  sendEmail(to, subject, body) {    this.calls.push({ to, subject, body, timestamp: Date.now() });    return Promise.resolve({ success: true, messageId: 'mock-id' });  }  
  verify(expectedCalls) {    return this.calls.length === expectedCalls.length &&           expectedCalls.every((expected, index) =>             this.calls[index] &&             this.calls[index].to === expected.to &&             this.calls[index].subject === expected.subject           );  }  
  getCalls() {    return this.calls;  }}
// Stub Pattern - Provides controlled responses
class StubUserRepository {  constructor(testData = {}) {    this.users = testData;  }  
  async findById(id) {    const user = this.users[id];    if (!user) {      throw new Error('User not found');    }    return Promise.resolve({ ...user });  }  
  async save(user) {    this.users[user.id] = { ...user };    return Promise.resolve(user);  }}
// Test Double Factory
class TestDoubleFactory {  static createUserService(options = {}) {    const mockEmailService = new MockEmailService();    const stubUserRepository = new StubUserRepository(options.userData || {});    
    return {      userService: new UserService(stubUserRepository, mockEmailService),      mocks: { emailService: mockEmailService },      stubs: { userRepository: stubUserRepository }    };  }}
// Usage in tests
class UserService {  constructor(userRepository, emailService) {    this.userRepository = userRepository;    this.emailService = emailService;  }  
  async createUser(userData) {    const user = await this.userRepository.save(userData);    await this.emailService.sendEmail(user.email, 'Welcome', 'Welcome to our platform');    return user;  }}
// Test example
async function testUserCreation() {  const { userService, mocks } = TestDoubleFactory.createUserService();  
  const newUser = await userService.createUser({    id: '123',    name: 'Test User',    email: 'test@company.com'  });  
  // Verify mock interactions  const emailCalls = mocks.emailService.getCalls();  console.assert(emailCalls.length === 1, 'Should send one email');  console.assert(emailCalls[0].to === 'test@company.com', 'Should send to correct address');  
  console.log('Test passed: User creation with email notification');}
testUserCreation();```


<!-- .element: class="fragment" -->

<!-- Speaker Notes: Test doubles are essential for proper unit testing. Mocks verify behavior, stubs provide data, and both enable fast, isolated tests that don't depend on external systems. -->

---

# Pattern Combinations in Practice

<div class="interactive-question">

## Real-World Pattern Integration
#### How patterns work together in enterprise architectures


<!-- .element: class="fragment" -->

<div class="two-column">
<div>

### E-Commerce Platform Stack
- **MVC/MVVM** - Frontend architecture
<!-- .element: class="fragment" -->
- **Factory + Strategy** - Payment processing
<!-- .element: class="fragment" -->
- **Observer + Command** - Order management
<!-- .element: class="fragment" -->
- **Circuit Breaker** - External service calls
<!-- .element: class="fragment" -->
- **Cache-Aside** - Product catalog
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->
<div>

### Enterprise Customer Platform
- **Service Discovery** - Microservice communication
<!-- .element: class="fragment" -->
- **Facade** - API Gateway pattern
<!-- .element: class="fragment" -->
- **Decorator** - Cross-cutting concerns (logging, security)
<!-- .element: class="fragment" -->
- **Template Method** - Data processing pipelines
<!-- .element: class="fragment" -->
- **Proxy** - Caching and access control
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<div class="highlight-box accent">
**Key Insight**: Enterprise applications rarely use single patterns. The art is in combining patterns effectively to create maintainable, scalable systems.

<!-- .element: class="fragment" -->

<!-- Speaker Notes: Real applications combine multiple patterns. The key is understanding when and how to combine them effectively. Each pattern solves specific problems, and their combination creates robust architectures. -->

---

# Anti-Patterns to Avoid

<div class="two-column">
<div>

## Common Design Anti-Patterns

- **God Object** - One class does everything
<!-- .element: class="fragment" -->
- **Singleton Overuse** - Everything becomes a singleton
<!-- .element: class="fragment" -->
- **Pattern for Pattern's Sake** - Using patterns unnecessarily
<!-- .element: class="fragment" -->
- **Over-Engineering** - Complex solutions for simple problems
<!-- .element: class="fragment" -->
- **Tight Coupling** - Classes too dependent on each other
<!-- .element: class="fragment" -->


<!-- .element: class="fragment" -->
<div>

## Microservice Anti-Patterns

- **Distributed Monolith** - Microservices too tightly coupled
<!-- .element: class="fragment" -->
- **Chatty Interfaces** - Too many fine-grained service calls
<!-- .element: class="fragment" -->
- **Shared Database** - Multiple services sharing one database
<!-- .element: class="fragment" -->
- **No Circuit Breaker** - Cascading failures not prevented
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<div class="highlight-box warning">
**Golden Rule**: Patterns should solve problems, not create them. Always ask "What problem am I solving?" before applying a pattern.

<!-- .element: class="fragment" -->

<!-- Speaker Notes: Anti-patterns are as important to learn as patterns themselves. They represent common mistakes that lead to maintenance nightmares. Recognizing these early can save significant refactoring effort. -->

---

# Four-Day Workshop Synthesis

<div class="progress-indicator">
<div class="progress-step completed">✅ Day 1: Creational Patterns (Singleton, Factory)
<!-- .element: class="fragment" -->
<div class="progress-step completed">✅ Day 2: Structural Patterns (Adapter, Decorator, Facade, Composite, Proxy)
<!-- .element: class="fragment" -->
<div class="progress-step completed">✅ Day 3: Behavioral Patterns (Observer, Strategy, Command, Template Method, Iterator, Chain)
<!-- .element: class="fragment" -->
<div class="progress-step completed">✅ Day 4: Advanced Patterns (MVC/MVP/MVVM, Microservices, Performance, Testing)
<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<div class="two-column">
<div>

## Pattern Categories Mastered

### **Creational Patterns**
- Object creation abstraction
<!-- .element: class="fragment" -->
- Singleton for unique instances
<!-- .element: class="fragment" -->
- Factory for flexible object creation
<!-- .element: class="fragment" --> 

### **Structural Patterns** 
- Object composition and relationships
<!-- .element: class="fragment" -->
- Adapter for interface compatibility
<!-- .element: class="fragment" -->
- Decorator for flexible enhancement
<!-- .element: class="fragment" -->
- Facade for complexity hiding
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->
<div>

### **Behavioral Patterns**
- Object interaction and communication
<!-- .element: class="fragment" -->
- Observer for event-driven architectures
<!-- .element: class="fragment" -->
- Strategy for algorithm flexibility
<!-- .element: class="fragment" -->
- Command for action encapsulation
<!-- .element: class="fragment" --> 

### **Advanced Patterns**
- Architectural pattern foundations
<!-- .element: class="fragment" -->
- Microservice communication patterns
<!-- .element: class="fragment" -->
- Performance optimization strategies
<!-- .element: class="fragment" -->
- Testing and quality assurance patterns
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<!-- Speaker Notes: This synthesis shows how patterns build upon each other. Creational patterns solve object creation problems, structural patterns organize objects, behavioral patterns enable communication, and advanced patterns create entire architectural solutions. -->

---

# Key Takeaways and Next Steps

<div class="interactive-question">

## Your Design Patterns Journey
#### From Learning to Mastery


<!-- .element: class="fragment" -->

<div class="two-column">
<div>

### **Key Learning Outcomes**
- **23 Core Patterns** - Comprehensive pattern library
<!-- .element: class="fragment" -->
- **Real-World Examples** - Enterprise use cases and implementations
<!-- .element: class="fragment" -->
- **Pattern Combinations** - How patterns work together
<!-- .element: class="fragment" -->
- **Best Practices** - When to use (and not use) patterns
<!-- .element: class="fragment" -->
- **Anti-Pattern Awareness** - Common mistakes to avoid
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->
<div>

### **Next Steps for Mastery**
- **Practice Integration** - Apply patterns in current projects
<!-- .element: class="fragment" -->
- **Study Frameworks** - See patterns in action (React, Angular, Spring)
<!-- .element: class="fragment" -->
- **Architect Solutions** - Design systems using pattern combinations
<!-- .element: class="fragment" -->
- **Mentorship** - Teach patterns to spread knowledge
<!-- .element: class="fragment" -->
- **Stay Updated** - Modern pattern adaptations and new patterns
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<div class="highlight-box success">
**Remember**: Patterns are tools, not rules. Use them to solve real problems and create maintainable, scalable software architectures.

<!-- .element: class="fragment" -->

<!-- Speaker Notes: Mastery comes through practice and thoughtful application. Start identifying patterns in existing code, then gradually apply them in new projects. The goal is writing better software, not using every pattern. -->

---

# Workshop Resources and Community

<div class="two-column">
<div>

## **Workshop Materials**
- **Complete Code Repository** - All pattern implementations and examples
<!-- .element: class="fragment" -->
- **Pattern Reference Guide** - Quick lookup for each pattern
<!-- .element: class="fragment" -->
- **Best Practice Checklists** - When to use each pattern
<!-- .element: class="fragment" -->
- **Architecture Templates** - Starter templates for common scenarios
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->
<div>

## **Continuing Education**
- **Enterprise Architecture Community** - Internal forums and discussions
<!-- .element: class="fragment" -->
- **Advanced Workshops** - Domain-specific deep dives
<!-- .element: class="fragment" -->
- **Monthly Pattern Reviews** - Real project pattern analysis
<!-- .element: class="fragment" -->
- **Conference Talks** - Share your pattern implementations
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

### **Contact Information**
- **Email**: architecture-training@company.com
- **Teams**: Enterprise Architecture Community
- **GitHub**: Workshop materials and examples
- **Wiki**: Internal pattern documentation and case studies

<!-- Speaker Notes: Learning doesn't end with this workshop. The provided resources and community connections will support your continued growth as a software architect. -->

---

# Final Q&A and Reflection

<div class="interactive-question">

## Four Days of Design Patterns
#### Your Questions, Insights, and Future Applications


<!-- .element: class="fragment" -->

<div class="two-column">
<div>

### **Discussion Topics**
- Which patterns will you apply first?
<!-- .element: class="fragment" -->
- What surprised you most about pattern combinations?
<!-- .element: class="fragment" -->
- How will you introduce patterns to your team?
<!-- .element: class="fragment" -->
- What challenges do you anticipate?
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->
<div>

### **Action Planning**
- **Identify one pattern** to implement this month
<!-- .element: class="fragment" -->
- **Review existing code** for pattern opportunities
<!-- .element: class="fragment" -->
- **Share knowledge** with your development team
<!-- .element: class="fragment" -->
- **Plan architectural improvements** using pattern combinations
<!-- .element: class="fragment" --> 


<!-- .element: class="fragment" -->

<!-- .element: class="fragment" -->

<div class="workshop-header">

# Thank You!
## **Congratulations on completing the Software-Architektur Workshop**
### **You're now equipped with powerful tools for creating better software architectures**

**Your journey to pattern mastery starts now!**


<!-- .element: class="fragment" -->

<!-- Speaker Notes: End with celebration and forward momentum. Participants have gained significant knowledge over four intense days. Encourage them to start small, apply patterns gradually, and share their experiences with the community. -->