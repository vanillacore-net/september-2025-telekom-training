---
title: Design Patterns Workshop - Tag 4
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

/* Code blocks sizing */
.reveal pre {
  font-size: 0.85em !important; /* Further increased for better readability */
  max-height: 500px;
  overflow: auto !important;
  background: #f8f8f8 !important; /* Light gray background */
  color: #333 !important; /* Dark text for contrast */
  border: 1px solid #e0e0e0; /* Subtle border */
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
  background: #f5f5f5;
  color: #333;
  padding: 25px; /* Scaled for FHD (20px * 1.875) */
  border-radius: 15px; /* Scaled for FHD (8px * 1.875) */
  margin: 38px 0; /* Scaled for FHD (20px * 1.875) */
  font-size: 1.1em; /* Increased font size for better readability */
}

.code-example h5 {
  color: #333;
  margin-top: 0;
  font-weight: 600;
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
  max-height: 80px;
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
  max-height: 300px;
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
</style>

<div class="workshop-header title-slide">

<div class="vanilla-logo">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo">
</div>

# Design Patterns Workshop - Tag 4
## Erweiterte Architektur-Patterns
### Enterprise Architecture Training

</div>

<!-- Speaker Notes: Willkommen zu Tag 4 unseres Design Patterns Workshops. Heute erforschen wir erweiterte architektonische Patterns, Microservice-Patterns und Performance-Optimierungen. Das ist unser Synthese-Tag, an dem wir alles zusammenbringen, was wir über die vergangenen vier Tage gelernt haben, zu praktischen, enterprise-reifen Lösungen. -->

---

# Tag 4 Agenda

<div class="progress-indicator">
<div class="progress-step current">📍 Architektur-Patterns - MVC, MVP, MVVM</div>
<div class="progress-step pending">⏳ Microservice Patterns - Service Discovery, Circuit Breaker</div>
<div class="progress-step pending">⏳ Performance Patterns - Caching, Pooling, Lazy Loading</div>
<div class="progress-step pending">⏳ Testing Patterns - Mock, Stub, Test Doubles</div>
<div class="progress-step pending">⏳ Pattern Synthese & Anti-Patterns</div>
</div>

## Erweiterte Themen Überblick

- **Architektur-Patterns** - MVC, MVP, MVVM Tiefgang <!-- .element: class="fragment" -->
- **Microservice Patterns** - Service Discovery, Circuit Breaker, Saga <!-- .element: class="fragment" -->
- **Performance Patterns** - Caching-Strategien, Object Pooling, Lazy Loading <!-- .element: class="fragment" -->
- **Testing Patterns** - Mock, Stub, Test Double Implementierungen <!-- .element: class="fragment" -->
- **Pattern-Kombinationen** - Reale architektonische Lösungen <!-- .element: class="fragment" -->
- **Anti-Patterns** - Häufige Fehler und wie man sie vermeidet <!-- .element: class="fragment" -->
- **Workshop Zusammenfassung** - Vier Tage Lernsynthese <!-- .element: class="fragment" -->

<!-- Speaker Notes: Die heutige Agenda baut auf den fundamentalen Patterns aus Tag 1-3 auf. Wir werden sehen, wie grundlegende Creational-, Structural- und Behavioral-Patterns sich kombinieren, um robuste Enterprise-Architekturen zu schaffen. Jeder Abschnitt enthält praktische Beispiele und reale Enterprise-Anwendungsfälle. -->

---

# Architektur-Patterns Tiefgang

<div class="pattern-definition">

#### Model-View-Controller (MVC)
**Zweck**: Trennt Anwendungslogik in drei miteinander verbundene Komponenten für bessere Organisation und Testbarkeit.

**Problem**: Monolithische Anwendungen sind schwer zu testen und zu warten.

**Lösung**: Klare Trennung zwischen Datenlogik (Model), Präsentation (View) und Eingabesteuerung (Controller).

</div>

<div class="two-column">
<div>

## MVC Komponenten
- **Model** - Geschäftslogik und Datenverwaltung <!-- .element: class="fragment" -->
- **View** - Benutzeroberfläche und Präsentationsschicht <!-- .element: class="fragment" -->
- **Controller** - Eingabebehandlung und Ablaufsteuerung <!-- .element: class="fragment" -->
- **Vorteile** - Klare Trennung der Belange, Testbarkeit <!-- .element: class="fragment" -->
- **Anwendungsfälle** - Webanwendungen, Desktop-Software <!-- .element: class="fragment" -->

</div>
<div>

## Enterprise-Anwendungen
- **Kundenportal** - Benutzerkonto-Verwaltung <!-- .element: class="fragment" -->
- **Abrechnungssystem** - Rechnungsbearbeitung <!-- .element: class="fragment" -->
- **Service-Management** - Service-Konfiguration <!-- .element: class="fragment" -->
- **Admin-Dashboards** - System-Monitoring <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: MVC ist grundlegend für moderne Web-Frameworks. Beachten Sie, wie das Observer Pattern von Tag 3 die Model-View-Kommunikation ermöglicht. Diese Trennung erlaubt es Teams, unabhängig an verschiedenen Schichten zu arbeiten. -->

---

# MVC Implementation Example

<div class="code-example">
<h5>MVC with Observer Pattern Integration</h5>

```javascript
// MVC Implementation Example
class UserModel { // fragment
  constructor() { // fragment
    this.users = []; // fragment
    this.observers = []; // fragment
  } // fragment
  
  addUser(user) { // fragment
    this.users.push(user); // fragment
    this.notifyObservers(); // fragment
  } // fragment
  
  getUsers() { // fragment
    return this.users; // fragment
  } // fragment
  
  notifyObservers() { // fragment
    this.observers.forEach(observer => observer.update(this.users)); // fragment
  } // fragment
} // fragment

class UserView { // fragment
  update(users) { // fragment
    console.log('Updated user list:', users); // fragment
    // Update DOM representation // fragment
    this.renderUserList(users); // fragment
  } // fragment
  
  renderUserList(users) { // fragment
    const userList = document.getElementById('users'); // fragment
    userList.innerHTML = users.map(user =>  // fragment
      `<div class="user">${user.name} - ${user.email}</div>` // fragment
    ).join(''); // fragment
  } // fragment
} // fragment

class UserController { // fragment
  constructor(model, view) { // fragment
    this.model = model; // fragment
    this.view = view; // fragment
    this.model.observers.push(view); // fragment
  } // fragment
  
  addUser(userData) { // fragment
    // Validate input // fragment
    if (this.validateUser(userData)) { // fragment
      this.model.addUser(userData); // fragment
    } // fragment
  } // fragment
  
  validateUser(userData) { // fragment
    return userData.name && userData.email; // fragment
  } // fragment
} // fragment

// Usage
const model = new UserModel(); // fragment
const view = new UserView(); // fragment
const controller = new UserController(model, view); // fragment

controller.addUser({ name: 'Max Mueller', email: 'max@telekom.de' }); // fragment
```

</div>

<!-- Speaker Notes: Notice how the Observer pattern enables automatic View updates when the Model changes. This loose coupling is essential for maintaining large applications. -->

---

# Model-View-Presenter (MVP)

<div class="pattern-definition">

#### Model-View-Presenter (MVP)
**Intent**: Verfeinert MVC durch eine passivere View und einen Presenter, der die gesamte UI-Logik behandelt.

**Problem**: MVC Views können zu komplex werden und sind schwer zu testen.

**Solution**: Presenter übernimmt alle UI-Logik, View wird zur passiven Schnittstelle.

</div>

<div class="two-column">
<div>

## MVP Components
- **Model** - Pure business logic, no UI knowledge <!-- .element: class="fragment" -->
- **View** - Passive interface, delegates to Presenter <!-- .element: class="fragment" -->
- **Presenter** - All UI logic and user interaction handling <!-- .element: class="fragment" -->
- **Benefits** - Highly testable, clear responsibilities <!-- .element: class="fragment" -->

</div>
<div>

## Key Differences from MVC
- View doesn't directly observe Model <!-- .element: class="fragment" -->
- Presenter mediates all Model-View communication <!-- .element: class="fragment" -->
- Better testability (Presenter can be unit tested) <!-- .element: class="fragment" -->
- More explicit control flow <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: MVP makes testing easier because the Presenter can be unit tested without a real View. This pattern is excellent for complex UI logic where you need granular control over user interactions. -->

---

# Model-View-ViewModel (MVVM)

<div class="pattern-definition">

#### Model-View-ViewModel (MVVM)
**Intent**: Nutzt Data Binding um View und ViewModel automatisch zu synchronisieren.

**Problem**: Manuelle UI-Updates sind fehleranfällig und führen zu Boilerplate-Code.

**Solution**: Zwei-Wege Data Binding zwischen View und ViewModel eliminiert manuelle Updates.

</div>

<div class="code-example">
<h5>MVVM with Proxy-based Data Binding</h5>

```javascript
// MVVM Implementation with Proxy for data binding
class UserViewModel { // fragment
  constructor(model) { // fragment
    this.model = model; // fragment
    this.users = []; // fragment
    this.newUser = { name: '', email: '' }; // fragment
    this.isLoading = false; // fragment
    
    // Create observable properties // fragment
    return this.createObservableViewModel(); // fragment
  } // fragment
  
  createObservableViewModel() { // fragment
    const self = this; // fragment
    
    return new Proxy(this, { // fragment
      set(target, property, value) { // fragment
        target[property] = value; // fragment
        self.notifyViewUpdate(property, value); // fragment
        return true; // fragment
      } // fragment
    }); // fragment
  } // fragment
  
  notifyViewUpdate(property, value) { // fragment
    // Emit change event for view binding // fragment
    document.dispatchEvent(new CustomEvent('viewModelChanged', { // fragment
      detail: { property, value, viewModel: this } // fragment
    })); // fragment
  } // fragment
  
  async addUser() { // fragment
    this.isLoading = true; // fragment
    
    try { // fragment
      const users = await this.model.saveUser(this.newUser); // fragment
      this.users = users; // fragment
      this.newUser = { name: '', email: '' }; // Reset form // fragment
    } catch (error) { // fragment
      console.error('Add user failed:', error); // fragment
    } finally { // fragment
      this.isLoading = false; // fragment
    } // fragment
  } // fragment
} // fragment

// View with automatic binding
class MVVMView { // fragment
  constructor(viewModel) { // fragment
    this.viewModel = viewModel; // fragment
    this.setupBindings(); // fragment
  } // fragment
  
  setupBindings() { // fragment
    // Listen for ViewModel changes // fragment
    document.addEventListener('viewModelChanged', (event) => { // fragment
      this.handleViewModelChange(event.detail); // fragment
    }); // fragment
    
    // Setup form bindings // fragment
    const nameInput = document.getElementById('userName'); // fragment
    const emailInput = document.getElementById('userEmail'); // fragment
    
    nameInput.addEventListener('input', (e) => { // fragment
      this.viewModel.newUser.name = e.target.value; // fragment
    }); // fragment
    
    emailInput.addEventListener('input', (e) => { // fragment
      this.viewModel.newUser.email = e.target.value; // fragment
    }); // fragment
  } // fragment
  
  handleViewModelChange({ property, value }) { // fragment
    switch(property) { // fragment
      case 'users': // fragment
        this.renderUsers(value); // fragment
        break; // fragment
      case 'isLoading': // fragment
        this.toggleLoading(value); // fragment
        break; // fragment
      case 'newUser': // fragment
        this.updateFormFields(value); // fragment
        break; // fragment
    } // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: MVVM shines in scenarios with complex UI state management. Modern frameworks like Angular, Vue, and React implement variations of MVVM. The key insight is automatic synchronization between data and UI. -->

---

# Microservice Patterns

<div class="pattern-definition">

#### Service Discovery Pattern
**Intent**: Ermöglicht Microservices, sich dynamisch zu finden und miteinander zu kommunizieren.

**Problem**: In einer Microservice-Architektur müssen Services andere Services finden, ohne fest gekoppelt zu sein.

**Solution**: Zentrales Service Registry mit Health Checks und Load Balancing.

</div>

<div class="two-column">
<div>

## Key Components
- **Service Registry** - Central directory of available services <!-- .element: class="fragment" -->
- **Health Checks** - Monitor service availability <!-- .element: class="fragment" -->
- **Load Balancing** - Distribute requests across instances <!-- .element: class="fragment" -->
- **Failure Handling** - Graceful degradation when services fail <!-- .element: class="fragment" -->

</div>
<div>

## Benefits
- **Dynamic Scaling** - Services can be added/removed at runtime <!-- .element: class="fragment" -->
- **High Availability** - Automatic failover to healthy instances <!-- .element: class="fragment" -->
- **Location Transparency** - Services don't need to know physical locations <!-- .element: class="fragment" -->
- **Configuration Management** - Centralized service configuration <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: Service Discovery is fundamental to scalable microservice architectures. In enterprise environments, we use this pattern for our API gateway and internal service communication. -->

---

# Circuit Breaker Pattern

<div class="pattern-definition">

#### Circuit Breaker Pattern
**Intent**: Verhindert kaskadierende Ausfälle durch Überwachung und Unterbrechung von Serviceaufrufen bei Fehlern.

**Problem**: Ein fehlerhafter Service kann das gesamte System zum Absturz bringen.

**Solution**: Circuit Breaker überwacht Service-Calls und öffnet bei Fehlern, um weitere Calls zu verhindern.

</div>

<div class="code-example">
<h5>Circuit Breaker Implementation</h5>

```javascript
// Circuit Breaker Pattern Implementation
class CircuitBreaker { // fragment
  constructor(service, options = {}) { // fragment
    this.service = service; // fragment
    this.failureThreshold = options.failureThreshold || 5; // fragment
    this.resetTimeout = options.resetTimeout || 30000; // 30 seconds // fragment
    this.state = 'CLOSED'; // CLOSED, OPEN, HALF_OPEN // fragment
    this.failureCount = 0; // fragment
    this.lastFailureTime = null; // fragment
  } // fragment
  
  async call(method, ...args) { // fragment
    if (this.state === 'OPEN') { // fragment
      if (this.shouldAttemptReset()) { // fragment
        this.state = 'HALF_OPEN'; // fragment
      } else { // fragment
        throw new Error('Circuit breaker is OPEN'); // fragment
      } // fragment
    } // fragment
    
    try { // fragment
      const result = await this.service[method](...args); // fragment
      this.onSuccess(); // fragment
      return result; // fragment
    } catch (error) { // fragment
      this.onFailure(error); // fragment
      throw error; // fragment
    } // fragment
  } // fragment
  
  shouldAttemptReset() { // fragment
    return Date.now() - this.lastFailureTime >= this.resetTimeout; // fragment
  } // fragment
  
  onSuccess() { // fragment
    this.failureCount = 0; // fragment
    this.state = 'CLOSED'; // fragment
  } // fragment
  
  onFailure(error) { // fragment
    this.failureCount++; // fragment
    this.lastFailureTime = Date.now(); // fragment
    
    if (this.failureCount >= this.failureThreshold) { // fragment
      this.state = 'OPEN'; // fragment
      console.log(`Circuit breaker opened due to ${this.failureCount} failures`); // fragment
    } // fragment
  } // fragment
  
  getState() { // fragment
    return { // fragment
      state: this.state, // fragment
      failureCount: this.failureCount, // fragment
      lastFailureTime: this.lastFailureTime // fragment
    }; // fragment
  } // fragment
} // fragment

// Usage with external service
class PaymentService { // fragment
  async processPayment(amount) { // fragment
    // Simulate occasional failures // fragment
    if (Math.random() < 0.3) { // fragment
      throw new Error('Payment service unavailable'); // fragment
    } // fragment
    
    return { success: true, amount, transactionId: Date.now() }; // fragment
  } // fragment
} // fragment

const paymentService = new PaymentService(); // fragment
const protectedPaymentService = new CircuitBreaker(paymentService, { // fragment
  failureThreshold: 3, // fragment
  resetTimeout: 10000 // fragment
}); // fragment

// Protected service calls
try { // fragment
  const result = await protectedPaymentService.call('processPayment', 100); // fragment
  console.log('Payment processed:', result); // fragment
} catch (error) { // fragment
  console.log('Payment failed:', error.message); // fragment
  console.log('Circuit breaker state:', protectedPaymentService.getState()); // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Circuit Breaker prevents cascading failures in distributed systems. It's essential for resilient microservice architectures and is implemented in many service mesh solutions. -->

---

# Performance Patterns

<div class="two-column">
<div>

## Caching Strategies

### Cache-Aside Pattern
```javascript
class CacheAsideRepository { // fragment
  constructor(cache, database) { // fragment
    this.cache = cache; // fragment
    this.database = database; // fragment
  } // fragment
  
  async get(key) { // fragment
    // Try cache first // fragment
    let data = await this.cache.get(key); // fragment
    
    if (!data) { // fragment
      // Cache miss - get from database // fragment
      data = await this.database.get(key); // fragment
      if (data) { // fragment
        await this.cache.set(key, data); // fragment
      } // fragment
    } // fragment
    
    return data; // fragment
  } // fragment
  
  async update(key, data) { // fragment
    // Update database first // fragment
    await this.database.update(key, data); // fragment
    // Invalidate cache // fragment
    await this.cache.delete(key); // fragment
  } // fragment
} // fragment
```

</div>
<div>

## Object Pool Pattern

```javascript
class ConnectionPool { // fragment
  constructor(createFn, maxSize = 10) { // fragment
    this.createConnection = createFn; // fragment
    this.maxSize = maxSize; // fragment
    this.available = []; // fragment
    this.inUse = new Set(); // fragment
  } // fragment
  
  async acquire() { // fragment
    if (this.available.length > 0) { // fragment
      const conn = this.available.pop(); // fragment
      this.inUse.add(conn); // fragment
      return conn; // fragment
    } // fragment
    
    if (this.inUse.size < this.maxSize) { // fragment
      const conn = await this.createConnection(); // fragment
      this.inUse.add(conn); // fragment
      return conn; // fragment
    } // fragment
    
    throw new Error('Pool exhausted'); // fragment
  } // fragment
  
  release(connection) { // fragment
    if (this.inUse.has(connection)) { // fragment
      this.inUse.delete(connection); // fragment
      this.available.push(connection); // fragment
    } // fragment
  } // fragment
} // fragment
```

</div>
</div>

<!-- Speaker Notes: Performance patterns are crucial for enterprise applications. Caching strategies can dramatically improve response times, while object pools manage expensive resources like database connections. -->

---

# Testing Patterns

<div class="pattern-definition">

#### Mock, Stub, and Test Double Patterns
**Intent**: Isoliert Units Under Test durch Ersatz von Abhängigkeiten mit kontrollierten Test-Implementierungen.

**Problem**: Unit Tests sollen isoliert und vorhersagbar sein, aber reale Abhängigkeiten sind komplex.

**Solution**: Test Doubles ersetzen reale Objekte mit testbaren Alternativen.

</div>

<div class="code-example">
<h5>Test Doubles Implementation</h5>

```javascript
// Mock Pattern - Verifies interactions
class MockEmailService { // fragment
  constructor() { // fragment
    this.calls = []; // fragment
  } // fragment
  
  sendEmail(to, subject, body) { // fragment
    this.calls.push({ to, subject, body, timestamp: Date.now() }); // fragment
    return Promise.resolve({ success: true, messageId: 'mock-id' }); // fragment
  } // fragment
  
  verify(expectedCalls) { // fragment
    return this.calls.length === expectedCalls.length && // fragment
           expectedCalls.every((expected, index) => // fragment
             this.calls[index] && // fragment
             this.calls[index].to === expected.to && // fragment
             this.calls[index].subject === expected.subject // fragment
           ); // fragment
  } // fragment
  
  getCalls() { // fragment
    return this.calls; // fragment
  } // fragment
} // fragment

// Stub Pattern - Provides controlled responses
class StubUserRepository { // fragment
  constructor(testData = {}) { // fragment
    this.users = testData; // fragment
  } // fragment
  
  async findById(id) { // fragment
    const user = this.users[id]; // fragment
    if (!user) { // fragment
      throw new Error('User not found'); // fragment
    } // fragment
    return Promise.resolve({ ...user }); // fragment
  } // fragment
  
  async save(user) { // fragment
    this.users[user.id] = { ...user }; // fragment
    return Promise.resolve(user); // fragment
  } // fragment
} // fragment

// Test Double Factory
class TestDoubleFactory { // fragment
  static createUserService(options = {}) { // fragment
    const mockEmailService = new MockEmailService(); // fragment
    const stubUserRepository = new StubUserRepository(options.userData || {}); // fragment
    
    return { // fragment
      userService: new UserService(stubUserRepository, mockEmailService), // fragment
      mocks: { emailService: mockEmailService }, // fragment
      stubs: { userRepository: stubUserRepository } // fragment
    }; // fragment
  } // fragment
} // fragment

// Usage in tests
class UserService { // fragment
  constructor(userRepository, emailService) { // fragment
    this.userRepository = userRepository; // fragment
    this.emailService = emailService; // fragment
  } // fragment
  
  async createUser(userData) { // fragment
    const user = await this.userRepository.save(userData); // fragment
    await this.emailService.sendEmail(user.email, 'Welcome', 'Welcome to our platform'); // fragment
    return user; // fragment
  } // fragment
} // fragment

// Test example
async function testUserCreation() { // fragment
  const { userService, mocks } = TestDoubleFactory.createUserService(); // fragment
  
  const newUser = await userService.createUser({ // fragment
    id: '123', // fragment
    name: 'Test User', // fragment
    email: 'test@telekom.de' // fragment
  }); // fragment
  
  // Verify mock interactions // fragment
  const emailCalls = mocks.emailService.getCalls(); // fragment
  console.assert(emailCalls.length === 1, 'Should send one email'); // fragment
  console.assert(emailCalls[0].to === 'test@telekom.de', 'Should send to correct address'); // fragment
  
  console.log('Test passed: User creation with email notification'); // fragment
} // fragment

testUserCreation(); // fragment
```

</div>

<!-- Speaker Notes: Test doubles are essential for proper unit testing. Mocks verify behavior, stubs provide data, and both enable fast, isolated tests that don't depend on external systems. -->

---

# Pattern Combinations in Practice

<div class="interactive-question">

## Real-World Pattern Integration
#### How patterns work together in enterprise architectures

</div>

<div class="two-column">
<div>

### E-Commerce Platform Stack
- **MVC/MVVM** - Frontend architecture <!-- .element: class="fragment" -->
- **Factory + Strategy** - Payment processing <!-- .element: class="fragment" -->
- **Observer + Command** - Order management <!-- .element: class="fragment" -->
- **Circuit Breaker** - External service calls <!-- .element: class="fragment" -->
- **Cache-Aside** - Product catalog <!-- .element: class="fragment" -->

</div>
<div>

### Enterprise Customer Platform
- **Service Discovery** - Microservice communication <!-- .element: class="fragment" -->
- **Facade** - API Gateway pattern <!-- .element: class="fragment" -->
- **Decorator** - Cross-cutting concerns (logging, security) <!-- .element: class="fragment" -->
- **Template Method** - Data processing pipelines <!-- .element: class="fragment" -->
- **Proxy** - Caching and access control <!-- .element: class="fragment" -->

</div>
</div>

<div class="highlight-box accent">
**Key Insight**: Enterprise applications rarely use single patterns. The art is in combining patterns effectively to create maintainable, scalable systems.
</div>

<!-- Speaker Notes: Real applications combine multiple patterns. The key is understanding when and how to combine them effectively. Each pattern solves specific problems, and their combination creates robust architectures. -->

---

# Anti-Patterns to Avoid

<div class="two-column">
<div>

## Common Design Anti-Patterns

- **God Object** - One class does everything <!-- .element: class="fragment highlight-red" -->
- **Singleton Overuse** - Everything becomes a singleton <!-- .element: class="fragment highlight-red" -->
- **Pattern for Pattern's Sake** - Using patterns unnecessarily <!-- .element: class="fragment highlight-red" -->
- **Over-Engineering** - Complex solutions for simple problems <!-- .element: class="fragment highlight-red" -->
- **Tight Coupling** - Classes too dependent on each other <!-- .element: class="fragment highlight-red" -->

</div>
<div>

## Microservice Anti-Patterns

- **Distributed Monolith** - Microservices too tightly coupled <!-- .element: class="fragment highlight-red" -->
- **Chatty Interfaces** - Too many fine-grained service calls <!-- .element: class="fragment highlight-red" -->
- **Shared Database** - Multiple services sharing one database <!-- .element: class="fragment highlight-red" -->
- **No Circuit Breaker** - Cascading failures not prevented <!-- .element: class="fragment highlight-red" -->

</div>
</div>

<div class="highlight-box warning">
**Golden Rule**: Patterns should solve problems, not create them. Always ask "What problem am I solving?" before applying a pattern.
</div>

<!-- Speaker Notes: Anti-patterns are as important to learn as patterns themselves. They represent common mistakes that lead to maintenance nightmares. Recognizing these early can save significant refactoring effort. -->

---

# Four-Day Workshop Synthesis

<div class="progress-indicator">
<div class="progress-step completed">✅ Day 1: Creational Patterns (Singleton, Factory)</div>
<div class="progress-step completed">✅ Day 2: Structural Patterns (Adapter, Decorator, Facade, Composite, Proxy)</div>
<div class="progress-step completed">✅ Day 3: Behavioral Patterns (Observer, Strategy, Command, Template Method, Iterator, Chain)</div>
<div class="progress-step completed">✅ Day 4: Advanced Patterns (MVC/MVP/MVVM, Microservices, Performance, Testing)</div>
</div>

<div class="two-column">
<div>

## Pattern Categories Mastered

### **Creational Patterns**
- Object creation abstraction <!-- .element: class="fragment" -->
- Singleton for unique instances <!-- .element: class="fragment" -->
- Factory for flexible object creation <!-- .element: class="fragment" -->

### **Structural Patterns** 
- Object composition and relationships <!-- .element: class="fragment" -->
- Adapter for interface compatibility <!-- .element: class="fragment" -->
- Decorator for flexible enhancement <!-- .element: class="fragment" -->
- Facade for complexity hiding <!-- .element: class="fragment" -->

</div>
<div>

### **Behavioral Patterns**
- Object interaction and communication <!-- .element: class="fragment" -->
- Observer for event-driven architectures <!-- .element: class="fragment" -->
- Strategy for algorithm flexibility <!-- .element: class="fragment" -->
- Command for action encapsulation <!-- .element: class="fragment" -->

### **Advanced Patterns**
- Architectural pattern foundations <!-- .element: class="fragment" -->
- Microservice communication patterns <!-- .element: class="fragment" -->
- Performance optimization strategies <!-- .element: class="fragment" -->
- Testing and quality assurance patterns <!-- .element: class="fragment" -->

</div>
</div>

<!-- Speaker Notes: This synthesis shows how patterns build upon each other. Creational patterns solve object creation problems, structural patterns organize objects, behavioral patterns enable communication, and advanced patterns create entire architectural solutions. -->

---

# Key Takeaways and Next Steps

<div class="interactive-question">

## Your Design Patterns Journey
#### From Learning to Mastery

</div>

<div class="two-column">
<div>

### **Key Learning Outcomes**
- **23 Core Patterns** - Comprehensive pattern library <!-- .element: class="fragment" -->
- **Real-World Examples** - Enterprise use cases and implementations <!-- .element: class="fragment" -->
- **Pattern Combinations** - How patterns work together <!-- .element: class="fragment" -->
- **Best Practices** - When to use (and not use) patterns <!-- .element: class="fragment" -->
- **Anti-Pattern Awareness** - Common mistakes to avoid <!-- .element: class="fragment" -->

</div>
<div>

### **Next Steps for Mastery**
- **Practice Integration** - Apply patterns in current projects <!-- .element: class="fragment" -->
- **Study Frameworks** - See patterns in action (React, Angular, Spring) <!-- .element: class="fragment" -->
- **Architect Solutions** - Design systems using pattern combinations <!-- .element: class="fragment" -->
- **Mentorship** - Teach patterns to spread knowledge <!-- .element: class="fragment" -->
- **Stay Updated** - Modern pattern adaptations and new patterns <!-- .element: class="fragment" -->

</div>
</div>

<div class="highlight-box success">
**Remember**: Patterns are tools, not rules. Use them to solve real problems and create maintainable, scalable software architectures.
</div>

<!-- Speaker Notes: Mastery comes through practice and thoughtful application. Start identifying patterns in existing code, then gradually apply them in new projects. The goal is writing better software, not using every pattern. -->

---

# Workshop Resources and Community

<div class="two-column">
<div>

## **Workshop Materials**
- **Complete Code Repository** - All examples and exercises <!-- .element: class="fragment" -->
- **Pattern Reference Guide** - Quick lookup for each pattern <!-- .element: class="fragment" -->
- **Best Practice Checklists** - When to use each pattern <!-- .element: class="fragment" -->
- **Architecture Templates** - Starter templates for common scenarios <!-- .element: class="fragment" -->

</div>
<div>

## **Continuing Education**
- **Enterprise Architecture Community** - Internal forums and discussions <!-- .element: class="fragment" -->
- **Advanced Workshops** - Domain-specific deep dives <!-- .element: class="fragment" -->
- **Monthly Pattern Reviews** - Real project pattern analysis <!-- .element: class="fragment" -->
- **Conference Talks** - Share your pattern implementations <!-- .element: class="fragment" -->

</div>
</div>

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

</div>

<div class="two-column">
<div>

### **Discussion Topics**
- Which patterns will you apply first? <!-- .element: class="fragment" -->
- What surprised you most about pattern combinations? <!-- .element: class="fragment" -->
- How will you introduce patterns to your team? <!-- .element: class="fragment" -->
- What challenges do you anticipate? <!-- .element: class="fragment" -->

</div>
<div>

### **Action Planning**
- **Identify one pattern** to implement this month <!-- .element: class="fragment" -->
- **Review existing code** for pattern opportunities <!-- .element: class="fragment" -->
- **Share knowledge** with your development team <!-- .element: class="fragment" -->
- **Plan architectural improvements** using pattern combinations <!-- .element: class="fragment" -->

</div>
</div>

<div class="workshop-header">

# Thank You!
## **Congratulations on completing the Design Patterns Workshop**
### **You're now equipped with powerful tools for creating better software architectures**

**Your journey to pattern mastery starts now!**

</div>

<!-- Speaker Notes: End with celebration and forward momentum. Participants have gained significant knowledge over four intense days. Encourage them to start small, apply patterns gradually, and share their experiences with the community. -->