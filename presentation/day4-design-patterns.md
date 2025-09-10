---
title: Day 4 Design Patterns - Advanced Architecture
description: Advanced patterns, microservices, performance optimization, and pattern synthesis
tags: design-patterns, workshop, telekom, architecture, training, day4, advanced
type: slide
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

.reveal .slides {
  font-size: 1.4em !important; /* Main text readable size */
  line-height: 1.3 !important;
}

.reveal .slides section {
  text-align: left !important;
  padding: 20px !important;
}

.reveal h1, .reveal h2, .reveal h3, .reveal h4 {
  font-weight: 600;
  text-align: left !important;
  margin: 0 0 15px 0 !important;
  color: #333 !important;
}

.reveal h1 { font-size: 2.2em !important; }
.reveal h2 { font-size: 1.8em !important; }
.reveal h3 { font-size: 1.4em !important; }
.reveal h4 { font-size: 1.2em !important; }

.reveal pre {
  width: 98% !important; /* Full slide width for code */
  font-size: 1.0em !important; /* Moderate code reduction */
  text-align: left !important;
  margin: 15px 0 !important;
}

.reveal p, .reveal li {
  text-align: left !important;
  margin: 0 0 8px 0 !important;
}
</style>

# Software-Architektur - Tag 4  
## Bring your own brain and use it!  
### Advanced Architecture Patterns

**Speaker Notes:** Welcome to Day 4 of our Design Patterns Workshop. Today we'll explore advanced architectural patterns, microservice patterns, and performance optimizations. This is our synthesis day where we bring together everything learned over the past four days into practical, enterprise-ready solutions.

---

# Day 4 Agenda

## Advanced Topics Overview

- **Architectural Patterns** - MVC, MVP, MVVM deep dive
- **Microservice Patterns** - Service Discovery, Circuit Breaker, Saga
- **Performance Patterns** - Caching strategies, Object Pooling, Lazy Loading
- **Testing Patterns** - Mock, Stub, Test Double implementations
- **Pattern Combinations** - Real-world architectural solutions
- **Anti-Patterns** - Common mistakes and how to avoid them
- **Workshop Summary** - Four days of learning synthesis

**Speaker Notes:** Today's agenda builds on fundamental patterns from Days 1-3. We'll see how basic creational, structural, and behavioral patterns combine to create robust enterprise architectures. Each section includes hands-on examples and real Enterprise use cases.

---

# Architectural Patterns Deep Dive

## Model-View-Controller (MVC)

MVC separates application logic into three interconnected components:

- **Model** - Business logic and data management
- **View** - User interface and presentation layer  
- **Controller** - Input handling and flow control
- **Benefits** - Clear separation of concerns, testability
- **Use Cases** - Web applications, desktop software
- **Enterprise Example** - Customer portal architecture

```javascript
// MVC Implementation Example
class UserModel {
  constructor() {
    this.users = [];
    this.observers = [];
  }
  
  addUser(user) {
    this.users.push(user);
    this.notifyObservers();
  }
  
  getUsers() {
    return this.users;
  }
  
  notifyObservers() {
    this.observers.forEach(observer => observer.update(this.users));
  }
}

class UserView {
  update(users) {
    console.log('Updated user list:', users);
    // Update DOM representation
    this.renderUserList(users);
  }
  
  renderUserList(users) {
    const userList = document.getElementById('users');
    userList.innerHTML = users.map(user => 
      `<div class="user">${user.name} - ${user.email}</div>`
    ).join('');
  }
}

class UserController {
  constructor(model, view) {
    this.model = model;
    this.view = view;
    this.model.observers.push(view);
  }
  
  addUser(userData) {
    // Validate input
    if (this.validateUser(userData)) {
      this.model.addUser(userData);
    }
  }
  
  validateUser(userData) {
    return userData.name && userData.email;
  }
}

// Usage
const model = new UserModel();
const view = new UserView();
const controller = new UserController(model, view);

controller.addUser({ name: 'Max Mueller', email: 'max@telekom.de' });
```

**Speaker Notes:** MVC is foundational to modern web frameworks. Notice how the Observer pattern from Day 2 enables the Model-View communication. This separation allows teams to work independently on different layers.

---

# Model-View-Presenter (MVP)

MVP refines MVC by making the View more passive and the Presenter handle all UI logic:

- **Model** - Pure business logic, no UI knowledge
- **View** - Passive interface, delegates to Presenter
- **Presenter** - All UI logic and user interaction handling
- **Benefits** - Highly testable, clear responsibilities
- **Difference from MVC** - View doesn't directly observe Model

```javascript
// MVP Implementation
class UserModelMVP {
  constructor() {
    this.users = [];
  }
  
  async saveUser(user) {
    // Simulate API call
    return new Promise(resolve => {
      setTimeout(() => {
        this.users.push({ ...user, id: Date.now() });
        resolve(this.users);
      }, 100);
    });
  }
  
  getUsers() {
    return this.users;
  }
}

class UserViewMVP {
  constructor(presenter) {
    this.presenter = presenter;
    this.setupEventListeners();
  }
  
  setupEventListeners() {
    document.getElementById('addUserBtn')
      .addEventListener('click', () => {
        const userData = this.getUserFormData();
        this.presenter.handleAddUser(userData);
      });
  }
  
  getUserFormData() {
    return {
      name: document.getElementById('userName').value,
      email: document.getElementById('userEmail').value
    };
  }
  
  displayUsers(users) {
    const container = document.getElementById('usersList');
    container.innerHTML = users.map(user => 
      `<div>ID: ${user.id} - ${user.name} (${user.email})</div>`
    ).join('');
  }
  
  showLoading() {
    document.getElementById('loading').style.display = 'block';
  }
  
  hideLoading() {
    document.getElementById('loading').style.display = 'none';
  }
}

class UserPresenter {
  constructor(model, view) {
    this.model = model;
    this.view = view;
  }
  
  async handleAddUser(userData) {
    this.view.showLoading();
    
    try {
      const users = await this.model.saveUser(userData);
      this.view.displayUsers(users);
    } catch (error) {
      console.error('Failed to save user:', error);
    } finally {
      this.view.hideLoading();
    }
  }
  
  loadUsers() {
    const users = this.model.getUsers();
    this.view.displayUsers(users);
  }
}

// Usage
const modelMVP = new UserModelMVP();
const presenterMVP = new UserPresenter(modelMVP, null);
const viewMVP = new UserViewMVP(presenterMVP);
presenterMVP.view = viewMVP;
```

**Speaker Notes:** MVP makes testing easier because the Presenter can be unit tested without a real View. This pattern is excellent for complex UI logic where you need granular control over user interactions.

---

# Model-View-ViewModel (MVVM)

MVVM uses data binding to automatically synchronize View and ViewModel:

- **Model** - Business logic and data
- **View** - UI elements with declarative binding
- **ViewModel** - View abstraction with bindable properties
- **Benefits** - Automatic UI updates, minimal boilerplate
- **Key Feature** - Two-way data binding

```javascript
// MVVM Implementation with Proxy for data binding
class UserViewModel {
  constructor(model) {
    this.model = model;
    this.users = [];
    this.newUser = { name: '', email: '' };
    this.isLoading = false;
    
    // Create observable properties
    return this.createObservableViewModel();
  }
  
  createObservableViewModel() {
    const self = this;
    
    return new Proxy(this, {
      set(target, property, value) {
        target[property] = value;
        self.notifyViewUpdate(property, value);
        return true;
      }
    });
  }
  
  notifyViewUpdate(property, value) {
    // Emit change event for view binding
    document.dispatchEvent(new CustomEvent('viewModelChanged', {
      detail: { property, value, viewModel: this }
    }));
  }
  
  async addUser() {
    this.isLoading = true;
    
    try {
      const users = await this.model.saveUser(this.newUser);
      this.users = users;
      this.newUser = { name: '', email: '' }; // Reset form
    } catch (error) {
      console.error('Add user failed:', error);
    } finally {
      this.isLoading = false;
    }
  }
  
  loadUsers() {
    this.users = this.model.getUsers();
  }
}

// View with automatic binding
class MVVMView {
  constructor(viewModel) {
    this.viewModel = viewModel;
    this.setupBindings();
  }
  
  setupBindings() {
    // Listen for ViewModel changes
    document.addEventListener('viewModelChanged', (event) => {
      this.handleViewModelChange(event.detail);
    });
    
    // Setup form bindings
    const nameInput = document.getElementById('userName');
    const emailInput = document.getElementById('userEmail');
    
    nameInput.addEventListener('input', (e) => {
      this.viewModel.newUser.name = e.target.value;
    });
    
    emailInput.addEventListener('input', (e) => {
      this.viewModel.newUser.email = e.target.value;
    });
    
    document.getElementById('addUserBtn')
      .addEventListener('click', () => this.viewModel.addUser());
  }
  
  handleViewModelChange({ property, value }) {
    switch(property) {
      case 'users':
        this.renderUsers(value);
        break;
      case 'isLoading':
        this.toggleLoading(value);
        break;
      case 'newUser':
        this.updateFormFields(value);
        break;
    }
  }
  
  renderUsers(users) {
    const container = document.getElementById('usersList');
    container.innerHTML = users.map(user => 
      `<div>${user.name} - ${user.email}</div>`
    ).join('');
  }
  
  toggleLoading(isLoading) {
    document.getElementById('loading').style.display = 
      isLoading ? 'block' : 'none';
  }
  
  updateFormFields(newUser) {
    document.getElementById('userName').value = newUser.name || '';
    document.getElementById('userEmail').value = newUser.email || '';
  }
}

// Usage
const modelMVVM = new UserModelMVP();
const viewModelMVVM = new UserViewModel(modelMVVM);
const viewMVVM = new MVVMView(viewModelMVVM);
```

**Speaker Notes:** MVVM shines in scenarios with complex UI state management. Modern frameworks like Angular, Vue, and React implement variations of MVVM. The key insight is automatic synchronization between data and UI.

---

# Microservice Patterns

## Service Discovery Pattern

Service Discovery enables microservices to find and communicate with each other dynamically:

- **Service Registry** - Central directory of available services
- **Health Checks** - Monitor service availability
- **Load Balancing** - Distribute requests across instances
- **Failure Handling** - Graceful degradation when services fail
- **Dynamic Scaling** - Services can be added/removed at runtime

```javascript
// Service Discovery Implementation
class ServiceRegistry {
  constructor() {
    this.services = new Map();
    this.healthCheckInterval = 30000; // 30 seconds
    this.startHealthChecks();
  }
  
  registerService(serviceName, instance) {
    if (!this.services.has(serviceName)) {
      this.services.set(serviceName, []);
    }
    
    const serviceInstance = {
      ...instance,
      registeredAt: Date.now(),
      lastHealthCheck: Date.now(),
      healthy: true
    };
    
    this.services.get(serviceName).push(serviceInstance);
    console.log(`Service registered: ${serviceName} at ${instance.host}:${instance.port}`);
  }
  
  discoverService(serviceName) {
    const instances = this.services.get(serviceName);
    if (!instances || instances.length === 0) {
      throw new Error(`Service ${serviceName} not found`);
    }
    
    // Return healthy instances only
    const healthyInstances = instances.filter(instance => instance.healthy);
    if (healthyInstances.length === 0) {
      throw new Error(`No healthy instances of ${serviceName} available`);
    }
    
    // Simple round-robin load balancing
    return this.selectInstance(healthyInstances);
  }
  
  selectInstance(instances) {
    const randomIndex = Math.floor(Math.random() * instances.length);
    return instances[randomIndex];
  }
  
  async performHealthCheck(instance) {
    try {
      const response = await fetch(`http://${instance.host}:${instance.port}/health`, {
        timeout: 5000
      });
      instance.healthy = response.ok;
      instance.lastHealthCheck = Date.now();
    } catch (error) {
      instance.healthy = false;
      console.warn(`Health check failed for ${instance.host}:${instance.port}`);
    }
  }
  
  startHealthChecks() {
    setInterval(() => {
      this.services.forEach((instances, serviceName) => {
        instances.forEach(instance => {
          this.performHealthCheck(instance);
        });
      });
    }, this.healthCheckInterval);
  }
}

// Usage Example
class MicroserviceClient {
  constructor(serviceRegistry) {
    this.registry = serviceRegistry;
  }
  
  async callService(serviceName, endpoint, data) {
    try {
      const instance = this.registry.discoverService(serviceName);
      const url = `http://${instance.host}:${instance.port}${endpoint}`;
      
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      
      return await response.json();
    } catch (error) {
      console.error(`Service call failed: ${serviceName}${endpoint}`, error);
      throw error;
    }
  }
}

// Service Registration
const registry = new ServiceRegistry();

// Register multiple service instances
registry.registerService('user-service', {
  host: '192.168.1.10',
  port: 3001,
  version: '1.2.0'
});

registry.registerService('user-service', {
  host: '192.168.1.11', 
  port: 3001,
  version: '1.2.0'
});

registry.registerService('order-service', {
  host: '192.168.1.20',
  port: 3002,
  version: '2.1.0'
});

// Client usage
const client = new MicroserviceClient(registry);
client.callService('user-service', '/users', { id: 123 });
```

**Speaker Notes:** Service Discovery is critical for microservice architectures. In production, you'd use tools like Consul, Eureka, or Kubernetes service discovery. The pattern here shows the core concepts that all these tools implement.

---

# Circuit Breaker Pattern

Circuit Breaker prevents cascading failures by monitoring service calls and failing fast when a service is unhealthy:

- **Closed State** - Normal operation, calls pass through
- **Open State** - Fast-fail mode, calls blocked
- **Half-Open State** - Testing if service recovered
- **Failure Threshold** - Configurable failure rate trigger
- **Timeout Recovery** - Automatic state transitions

```javascript
// Circuit Breaker Implementation
class CircuitBreaker {
  constructor(options = {}) {
    this.failureThreshold = options.failureThreshold || 5;
    this.recoveryTimeout = options.recoveryTimeout || 60000; // 1 minute
    this.monitoringPeriod = options.monitoringPeriod || 10000; // 10 seconds
    
    this.state = 'CLOSED'; // CLOSED, OPEN, HALF_OPEN
    this.failureCount = 0;
    this.lastFailureTime = null;
    this.successCount = 0;
    
    // Monitoring
    this.callHistory = [];
  }
  
  async call(serviceCall) {
    if (this.state === 'OPEN') {
      if (this.shouldAttemptReset()) {
        this.state = 'HALF_OPEN';
        console.log('Circuit breaker: Transitioning to HALF_OPEN');
      } else {
        throw new Error('Circuit breaker is OPEN - service unavailable');
      }
    }
    
    try {
      const result = await this.executeCall(serviceCall);
      this.onSuccess();
      return result;
    } catch (error) {
      this.onFailure();
      throw error;
    }
  }
  
  async executeCall(serviceCall) {
    const startTime = Date.now();
    
    try {
      const result = await serviceCall();
      const duration = Date.now() - startTime;
      
      this.recordCall(true, duration);
      return result;
    } catch (error) {
      const duration = Date.now() - startTime;
      this.recordCall(false, duration, error.message);
      throw error;
    }
  }
  
  recordCall(success, duration, error = null) {
    this.callHistory.push({
      timestamp: Date.now(),
      success,
      duration,
      error
    });
    
    // Keep only recent history
    const cutoff = Date.now() - this.monitoringPeriod;
    this.callHistory = this.callHistory.filter(call => call.timestamp > cutoff);
  }
  
  onSuccess() {
    this.failureCount = 0;
    
    if (this.state === 'HALF_OPEN') {
      this.successCount++;
      if (this.successCount >= 3) { // Require 3 successes to close
        this.state = 'CLOSED';
        this.successCount = 0;
        console.log('Circuit breaker: Transitioning to CLOSED');
      }
    }
  }
  
  onFailure() {
    this.failureCount++;
    this.lastFailureTime = Date.now();
    
    if (this.state === 'HALF_OPEN') {
      this.state = 'OPEN';
      console.log('Circuit breaker: Transitioning back to OPEN');
    } else if (this.failureCount >= this.failureThreshold) {
      this.state = 'OPEN';
      console.log(`Circuit breaker: OPENING after ${this.failureCount} failures`);
    }
  }
  
  shouldAttemptReset() {
    return Date.now() - this.lastFailureTime > this.recoveryTimeout;
  }
  
  getStats() {
    const recentCalls = this.callHistory;
    const totalCalls = recentCalls.length;
    const successfulCalls = recentCalls.filter(call => call.success).length;
    const failureRate = totalCalls > 0 ? (totalCalls - successfulCalls) / totalCalls : 0;
    
    return {
      state: this.state,
      totalCalls,
      successfulCalls,
      failureRate: Math.round(failureRate * 100),
      failureCount: this.failureCount,
      averageResponseTime: this.calculateAverageResponseTime(recentCalls)
    };
  }
  
  calculateAverageResponseTime(calls) {
    if (calls.length === 0) return 0;
    const totalTime = calls.reduce((sum, call) => sum + call.duration, 0);
    return Math.round(totalTime / calls.length);
  }
}

// Usage with Service Client
class ResilientServiceClient {
  constructor() {
    this.circuitBreakers = new Map();
  }
  
  getCircuitBreaker(serviceName) {
    if (!this.circuitBreakers.has(serviceName)) {
      this.circuitBreakers.set(serviceName, new CircuitBreaker({
        failureThreshold: 5,
        recoveryTimeout: 30000,
        monitoringPeriod: 60000
      }));
    }
    return this.circuitBreakers.get(serviceName);
  }
  
  async callService(serviceName, endpoint, data) {
    const circuitBreaker = this.getCircuitBreaker(serviceName);
    
    return circuitBreaker.call(async () => {
      const response = await fetch(`http://api.${serviceName}.com${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
        timeout: 5000
      });
      
      if (!response.ok) {
        throw new Error(`Service call failed: ${response.status}`);
      }
      
      return response.json();
    });
  }
  
  getServiceHealth() {
    const health = {};
    this.circuitBreakers.forEach((breaker, serviceName) => {
      health[serviceName] = breaker.getStats();
    });
    return health;
  }
}

// Usage Example
const client = new ResilientServiceClient();

// This will automatically use circuit breaker protection
client.callService('payment-service', '/process-payment', {
  amount: 100,
  currency: 'EUR'
}).then(result => {
  console.log('Payment processed:', result);
}).catch(error => {
  console.error('Payment failed:', error.message);
});

// Monitor service health
setInterval(() => {
  console.log('Service Health:', client.getServiceHealth());
}, 10000);
```

**Speaker Notes:** Circuit Breaker is essential for microservice resilience. Netflix's Hystrix popularized this pattern. The key insight is failing fast to prevent cascading failures across your service mesh.

---

# Saga Pattern

Saga Pattern manages distributed transactions across microservices without locking resources:

- **Orchestration** - Central coordinator manages the saga
- **Choreography** - Services coordinate through events
- **Compensation** - Reverse operations if saga fails
- **Eventual Consistency** - Accept temporary inconsistency
- **Failure Recovery** - Handle partial failures gracefully

```javascript
// Saga Pattern - Orchestration Implementation
class SagaOrchestrator {
  constructor() {
    this.sagas = new Map();
    this.stepDefinitions = new Map();
  }
  
  defineSaga(sagaName, steps) {
    this.stepDefinitions.set(sagaName, steps);
  }
  
  async executeSaga(sagaName, sagaId, initialData) {
    const steps = this.stepDefinitions.get(sagaName);
    if (!steps) {
      throw new Error(`Saga ${sagaName} not defined`);
    }
    
    const saga = {
      id: sagaId,
      name: sagaName,
      status: 'RUNNING',
      currentStep: 0,
      completedSteps: [],
      data: { ...initialData },
      startTime: Date.now()
    };
    
    this.sagas.set(sagaId, saga);
    
    try {
      await this.executeSteps(saga, steps);
      saga.status = 'COMPLETED';
      console.log(`Saga ${sagaName} (${sagaId}) completed successfully`);
      return saga.data;
    } catch (error) {
      console.error(`Saga ${sagaName} (${sagaId}) failed:`, error.message);
      await this.compensate(saga, steps);
      saga.status = 'FAILED';
      throw error;
    }
  }
  
  async executeSteps(saga, steps) {
    for (let i = saga.currentStep; i < steps.length; i++) {
      const step = steps[i];
      saga.currentStep = i;
      
      try {
        console.log(`Executing step ${i + 1}/${steps.length}: ${step.name}`);
        const result = await step.execute(saga.data);
        
        // Merge result into saga data
        saga.data = { ...saga.data, ...result };
        saga.completedSteps.push({
          stepIndex: i,
          name: step.name,
          result,
          completedAt: Date.now()
        });
        
      } catch (error) {
        error.failedStep = step.name;
        error.stepIndex = i;
        throw error;
      }
    }
  }
  
  async compensate(saga, steps) {
    console.log(`Starting compensation for saga ${saga.id}`);
    
    // Execute compensation in reverse order
    for (let i = saga.completedSteps.length - 1; i >= 0; i--) {
      const completedStep = saga.completedSteps[i];
      const stepDefinition = steps[completedStep.stepIndex];
      
      if (stepDefinition.compensate) {
        try {
          console.log(`Compensating step: ${stepDefinition.name}`);
          await stepDefinition.compensate(saga.data, completedStep.result);
        } catch (compensationError) {
          console.error(`Compensation failed for step ${stepDefinition.name}:`, 
                       compensationError.message);
          // Continue with other compensations
        }
      }
    }
  }
  
  getSagaStatus(sagaId) {
    return this.sagas.get(sagaId);
  }
}

// Order Processing Saga Example
class OrderProcessingSaga {
  constructor(orchestrator, services) {
    this.orchestrator = orchestrator;
    this.services = services;
    this.defineSagaSteps();
  }
  
  defineSagaSteps() {
    this.orchestrator.defineSaga('order-processing', [
      {
        name: 'validate-order',
        execute: async (data) => {
          const validation = await this.services.orderService.validateOrder(data.order);
          return { validation };
        },
        compensate: async (data, result) => {
          // No compensation needed for validation
        }
      },
      {
        name: 'reserve-inventory',
        execute: async (data) => {
          const reservation = await this.services.inventoryService.reserveItems(data.order.items);
          return { reservation };
        },
        compensate: async (data, result) => {
          await this.services.inventoryService.releaseReservation(result.reservation.id);
        }
      },
      {
        name: 'process-payment',
        execute: async (data) => {
          const payment = await this.services.paymentService.chargeCustomer({
            customerId: data.order.customerId,
            amount: data.order.total,
            reservationId: data.reservation.id
          });
          return { payment };
        },
        compensate: async (data, result) => {
          await this.services.paymentService.refundPayment(result.payment.id);
        }
      },
      {
        name: 'create-shipment',
        execute: async (data) => {
          const shipment = await this.services.shippingService.createShipment({
            orderId: data.order.id,
            items: data.order.items,
            address: data.order.shippingAddress,
            paymentId: data.payment.id
          });
          return { shipment };
        },
        compensate: async (data, result) => {
          await this.services.shippingService.cancelShipment(result.shipment.id);
        }
      },
      {
        name: 'send-confirmation',
        execute: async (data) => {
          const notification = await this.services.notificationService.sendOrderConfirmation({
            customerId: data.order.customerId,
            orderId: data.order.id,
            shipmentId: data.shipment.id
          });
          return { notification };
        },
        compensate: async (data, result) => {
          // Send cancellation notification
          await this.services.notificationService.sendOrderCancellation({
            customerId: data.order.customerId,
            orderId: data.order.id
          });
        }
      }
    ]);
  }
  
  async processOrder(order) {
    const sagaId = `order-${order.id}-${Date.now()}`;
    
    try {
      const result = await this.orchestrator.executeSaga('order-processing', sagaId, { order });
      return {
        success: true,
        orderId: order.id,
        sagaId,
        result
      };
    } catch (error) {
      return {
        success: false,
        orderId: order.id,
        sagaId,
        error: error.message
      };
    }
  }
}

// Mock Services for Demo
const mockServices = {
  orderService: {
    async validateOrder(order) {
      // Simulate validation
      if (!order.items || order.items.length === 0) {
        throw new Error('Order has no items');
      }
      return { valid: true, orderId: order.id };
    }
  },
  inventoryService: {
    async reserveItems(items) {
      // Simulate inventory reservation
      return { 
        id: `reservation-${Date.now()}`,
        items: items.map(item => ({ ...item, reserved: true }))
      };
    },
    async releaseReservation(reservationId) {
      console.log(`Released inventory reservation: ${reservationId}`);
    }
  },
  paymentService: {
    async chargeCustomer(paymentData) {
      // Simulate payment processing
      return {
        id: `payment-${Date.now()}`,
        amount: paymentData.amount,
        status: 'CHARGED'
      };
    },
    async refundPayment(paymentId) {
      console.log(`Refunded payment: ${paymentId}`);
    }
  },
  shippingService: {
    async createShipment(shipmentData) {
      return {
        id: `shipment-${Date.now()}`,
        trackingNumber: `TRK${Date.now()}`,
        estimatedDelivery: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
      };
    },
    async cancelShipment(shipmentId) {
      console.log(`Cancelled shipment: ${shipmentId}`);
    }
  },
  notificationService: {
    async sendOrderConfirmation(data) {
      console.log(`Sent order confirmation to customer ${data.customerId}`);
      return { sent: true, messageId: `msg-${Date.now()}` };
    },
    async sendOrderCancellation(data) {
      console.log(`Sent order cancellation to customer ${data.customerId}`);
    }
  }
};

// Usage Example
const orchestrator = new SagaOrchestrator();
const orderSaga = new OrderProcessingSaga(orchestrator, mockServices);

// Process an order
const sampleOrder = {
  id: 'ORD-001',
  customerId: 'CUST-123',
  items: [
    { productId: 'PROD-A', quantity: 2, price: 29.99 },
    { productId: 'PROD-B', quantity: 1, price: 49.99 }
  ],
  total: 109.97,
  shippingAddress: {
    street: 'Musterstrasse 123',
    city: 'Berlin',
    zipCode: '10115',
    country: 'Germany'
  }
};

orderSaga.processOrder(sampleOrder)
  .then(result => console.log('Order processing result:', result))
  .catch(error => console.error('Order processing failed:', error));
```

**Speaker Notes:** Saga Pattern is crucial for microservice transactions. Unlike database ACID transactions, sagas provide eventual consistency through compensation actions. This is how companies like Amazon and Netflix handle complex distributed workflows.

---

# Performance Patterns

## Caching Strategies

Effective caching can dramatically improve application performance:

- **Cache-Aside** - Application manages cache explicitly
- **Write-Through** - Write to cache and database simultaneously
- **Write-Behind** - Write to cache first, database asynchronously
- **Cache Invalidation** - Strategies for maintaining data freshness
- **Multi-Level Caching** - Browser, CDN, application, database caches

```javascript
// Multi-Level Cache Implementation
class CacheManager {
  constructor() {
    this.caches = new Map();
    this.defaultTTL = 300000; // 5 minutes
  }
  
  createCache(name, options = {}) {
    const cache = new LRUCache({
      maxSize: options.maxSize || 1000,
      ttl: options.ttl || this.defaultTTL,
      onEviction: options.onEviction
    });
    
    this.caches.set(name, cache);
    return cache;
  }
  
  getCache(name) {
    return this.caches.get(name);
  }
}

class LRUCache {
  constructor(options = {}) {
    this.maxSize = options.maxSize || 100;
    this.ttl = options.ttl || 300000; // 5 minutes default
    this.cache = new Map();
    this.accessOrder = [];
    this.onEviction = options.onEviction || (() => {});
  }
  
  get(key) {
    const item = this.cache.get(key);
    
    if (!item) {
      return null;
    }
    
    // Check if expired
    if (Date.now() > item.expiry) {
      this.delete(key);
      return null;
    }
    
    // Update access order
    this.updateAccessOrder(key);
    
    return item.value;
  }
  
  set(key, value, customTTL = null) {
    const ttl = customTTL || this.ttl;
    const expiry = Date.now() + ttl;
    
    // If key exists, update it
    if (this.cache.has(key)) {
      this.cache.set(key, { value, expiry, createdAt: Date.now() });
      this.updateAccessOrder(key);
      return;
    }
    
    // Check capacity
    if (this.cache.size >= this.maxSize) {
      this.evictLeastRecentlyUsed();
    }
    
    // Add new item
    this.cache.set(key, { value, expiry, createdAt: Date.now() });
    this.accessOrder.push(key);
  }
  
  delete(key) {
    const item = this.cache.get(key);
    if (item) {
      this.cache.delete(key);
      this.removeFromAccessOrder(key);
      this.onEviction(key, item.value);
      return true;
    }
    return false;
  }
  
  updateAccessOrder(key) {
    this.removeFromAccessOrder(key);
    this.accessOrder.push(key);
  }
  
  removeFromAccessOrder(key) {
    const index = this.accessOrder.indexOf(key);
    if (index > -1) {
      this.accessOrder.splice(index, 1);
    }
  }
  
  evictLeastRecentlyUsed() {
    if (this.accessOrder.length > 0) {
      const lruKey = this.accessOrder[0];
      this.delete(lruKey);
    }
  }
  
  clear() {
    this.cache.clear();
    this.accessOrder = [];
  }
  
  size() {
    return this.cache.size;
  }
  
  getStats() {
    return {
      size: this.cache.size,
      maxSize: this.maxSize,
      hitRate: this.calculateHitRate(),
      oldestEntry: this.getOldestEntry(),
      memoryUsage: this.estimateMemoryUsage()
    };
  }
  
  calculateHitRate() {
    // This would require tracking hits/misses in a real implementation
    return 0.85; // Placeholder
  }
  
  getOldestEntry() {
    let oldest = null;
    this.cache.forEach((item, key) => {
      if (!oldest || item.createdAt < oldest.createdAt) {
        oldest = { key, createdAt: item.createdAt };
      }
    });
    return oldest;
  }
  
  estimateMemoryUsage() {
    // Rough estimation - would need more sophisticated calculation
    return this.cache.size * 1024; // KB
  }
}

// Cache-Aside Pattern Implementation
class UserService {
  constructor(database, cacheManager) {
    this.database = database;
    this.cache = cacheManager.createCache('users', {
      maxSize: 1000,
      ttl: 600000, // 10 minutes
      onEviction: (key, value) => {
        console.log(`Evicted user from cache: ${key}`);
      }
    });
  }
  
  async getUser(userId) {
    // Try cache first
    const cacheKey = `user:${userId}`;
    let user = this.cache.get(cacheKey);
    
    if (user) {
      console.log(`Cache hit for user: ${userId}`);
      return user;
    }
    
    // Cache miss - fetch from database
    console.log(`Cache miss for user: ${userId}`);
    user = await this.database.findUser(userId);
    
    if (user) {
      // Store in cache
      this.cache.set(cacheKey, user);
    }
    
    return user;
  }
  
  async updateUser(userId, userData) {
    // Update database
    const updatedUser = await this.database.updateUser(userId, userData);
    
    // Invalidate cache
    const cacheKey = `user:${userId}`;
    this.cache.delete(cacheKey);
    
    return updatedUser;
  }
  
  async searchUsers(query) {
    const cacheKey = `search:${JSON.stringify(query)}`;
    let results = this.cache.get(cacheKey);
    
    if (results) {
      console.log(`Cache hit for search: ${query.term}`);
      return results;
    }
    
    results = await this.database.searchUsers(query);
    
    // Cache search results for shorter time (search results change more frequently)
    this.cache.set(cacheKey, results, 120000); // 2 minutes
    
    return results;
  }
}

// Write-Through Cache Implementation
class ProductService {
  constructor(database, cacheManager) {
    this.database = database;
    this.cache = cacheManager.createCache('products', {
      maxSize: 5000,
      ttl: 1800000 // 30 minutes
    });
  }
  
  async getProduct(productId) {
    const cacheKey = `product:${productId}`;
    let product = this.cache.get(cacheKey);
    
    if (!product) {
      product = await this.database.findProduct(productId);
      if (product) {
        this.cache.set(cacheKey, product);
      }
    }
    
    return product;
  }
  
  async updateProduct(productId, productData) {
    // Write-through: update both cache and database
    const updatedProduct = await this.database.updateProduct(productId, productData);
    
    const cacheKey = `product:${productId}`;
    this.cache.set(cacheKey, updatedProduct);
    
    return updatedProduct;
  }
}

// Usage Example
const cacheManager = new CacheManager();

// Mock database
const mockDatabase = {
  async findUser(userId) {
    // Simulate database delay
    await new Promise(resolve => setTimeout(resolve, 100));
    return { id: userId, name: `User ${userId}`, email: `user${userId}@telekom.de` };
  },
  
  async updateUser(userId, userData) {
    await new Promise(resolve => setTimeout(resolve, 150));
    return { id: userId, ...userData, updatedAt: new Date() };
  },
  
  async searchUsers(query) {
    await new Promise(resolve => setTimeout(resolve, 200));
    return [
      { id: 1, name: 'John Doe', email: 'john@telekom.de' },
      { id: 2, name: 'Jane Smith', email: 'jane@telekom.de' }
    ];
  }
};

const userService = new UserService(mockDatabase, cacheManager);

// Test caching
async function testCaching() {
  console.log('Testing cache performance...');
  
  // First call - cache miss
  console.time('First call');
  await userService.getUser(123);
  console.timeEnd('First call');
  
  // Second call - cache hit
  console.time('Second call');
  await userService.getUser(123);
  console.timeEnd('Second call');
  
  // Check cache stats
  console.log('Cache stats:', userService.cache.getStats());
}

testCaching();
```

**Speaker Notes:** Caching is often the highest-impact performance optimization. The key is choosing the right caching strategy for your use case. Cache-aside is most common, write-through ensures consistency, and LRU eviction prevents memory bloat.

---

# Object Pooling Pattern

Object Pooling manages expensive-to-create objects by reusing them:

- **Pool Management** - Create, acquire, release objects
- **Resource Conservation** - Reduce memory allocation/deallocation
- **Performance Optimization** - Avoid object creation overhead
- **Connection Pooling** - Database connections, HTTP clients
- **Thread Pooling** - Worker threads for concurrent tasks

```javascript
// Generic Object Pool Implementation
class ObjectPool {
  constructor(factory, resetFunction, maxSize = 10) {
    this.factory = factory;
    this.resetFunction = resetFunction;
    this.maxSize = maxSize;
    this.available = [];
    this.inUse = new Set();
    this.created = 0;
    
    // Performance metrics
    this.stats = {
      acquisitions: 0,
      releases: 0,
      creations: 0,
      poolHits: 0,
      poolMisses: 0
    };
  }
  
  acquire() {
    this.stats.acquisitions++;
    
    // Try to get from available pool
    if (this.available.length > 0) {
      const obj = this.available.pop();
      this.inUse.add(obj);
      this.stats.poolHits++;
      console.log(`Pool hit - available: ${this.available.length}, in use: ${this.inUse.size}`);
      return obj;
    }
    
    // Create new object if under limit
    if (this.created < this.maxSize) {
      const obj = this.factory();
      this.inUse.add(obj);
      this.created++;
      this.stats.creations++;
      this.stats.poolMisses++;
      console.log(`Created new object - total created: ${this.created}`);
      return obj;
    }
    
    // Pool exhausted
    this.stats.poolMisses++;
    throw new Error(`Object pool exhausted - max size: ${this.maxSize}`);
  }
  
  release(obj) {
    if (!this.inUse.has(obj)) {
      throw new Error('Object not acquired from this pool');
    }
    
    this.inUse.delete(obj);
    
    try {
      // Reset object state
      this.resetFunction(obj);
      this.available.push(obj);
      this.stats.releases++;
      console.log(`Object released - available: ${this.available.length}, in use: ${this.inUse.size}`);
    } catch (error) {
      // If reset fails, discard the object
      this.created--;
      console.warn('Object reset failed, discarded:', error.message);
    }
  }
  
  size() {
    return {
      available: this.available.length,
      inUse: this.inUse.size,
      total: this.created
    };
  }
  
  getStats() {
    const hitRate = this.stats.acquisitions > 0 
      ? (this.stats.poolHits / this.stats.acquisitions * 100).toFixed(1)
      : 0;
    
    return {
      ...this.stats,
      hitRate: `${hitRate}%`,
      size: this.size()
    };
  }
  
  drain() {
    this.available = [];
    this.inUse.clear();
    this.created = 0;
    console.log('Pool drained');
  }
}

// Database Connection Pool Example
class DatabaseConnection {
  constructor(config) {
    this.config = config;
    this.connected = false;
    this.lastUsed = Date.now();
    this.queries = 0;
    this.connectionId = Math.random().toString(36).substr(2, 9);
  }
  
  async connect() {
    if (!this.connected) {
      // Simulate connection establishment
      await new Promise(resolve => setTimeout(resolve, 100));
      this.connected = true;
      console.log(`Database connection ${this.connectionId} established`);
    }
  }
  
  async query(sql, params = []) {
    if (!this.connected) {
      throw new Error('Connection not established');
    }
    
    this.lastUsed = Date.now();
    this.queries++;
    
    // Simulate query execution
    await new Promise(resolve => setTimeout(resolve, 50));
    
    return {
      sql,
      params,
      rows: [{ id: 1, name: 'Sample Data' }],
      executedAt: new Date(),
      connectionId: this.connectionId
    };
  }
  
  async close() {
    if (this.connected) {
      // Simulate connection cleanup
      await new Promise(resolve => setTimeout(resolve, 50));
      this.connected = false;
      console.log(`Database connection ${this.connectionId} closed`);
    }
  }
  
  isHealthy() {
    const maxIdleTime = 300000; // 5 minutes
    return this.connected && (Date.now() - this.lastUsed) < maxIdleTime;
  }
}

class DatabaseConnectionPool {
  constructor(config, maxConnections = 10) {
    this.config = config;
    
    // Create object pool
    this.pool = new ObjectPool(
      () => new DatabaseConnection(config),
      (connection) => {
        // Reset connection state
        connection.lastUsed = Date.now();
        if (!connection.isHealthy()) {
          throw new Error('Connection unhealthy');
        }
      },
      maxConnections
    );
    
    // Periodic health check
    this.healthCheckInterval = setInterval(() => {
      this.performHealthCheck();
    }, 60000); // Every minute
  }
  
  async getConnection() {
    const connection = this.pool.acquire();
    await connection.connect();
    return connection;
  }
  
  releaseConnection(connection) {
    this.pool.release(connection);
  }
  
  async withConnection(callback) {
    const connection = await this.getConnection();
    
    try {
      return await callback(connection);
    } finally {
      this.releaseConnection(connection);
    }
  }
  
  async performHealthCheck() {
    const { available, inUse } = this.pool.size();
    console.log(`Health check - Available: ${available}, In use: ${inUse}`);
    
    // Check available connections
    const unhealthyConnections = this.pool.available.filter(conn => !conn.isHealthy());
    
    if (unhealthyConnections.length > 0) {
      console.log(`Removing ${unhealthyConnections.length} unhealthy connections`);
      
      unhealthyConnections.forEach(conn => {
        const index = this.pool.available.indexOf(conn);
        if (index > -1) {
          this.pool.available.splice(index, 1);
          this.pool.created--;
          conn.close();
        }
      });
    }
  }
  
  async close() {
    clearInterval(this.healthCheckInterval);
    
    // Close all connections
    const allConnections = [...this.pool.available, ...this.pool.inUse];
    await Promise.all(allConnections.map(conn => conn.close()));
    
    this.pool.drain();
  }
  
  getStats() {
    return this.pool.getStats();
  }
}

// HTTP Client Pool Example
class HttpClientPool {
  constructor(maxClients = 5) {
    this.pool = new ObjectPool(
      () => ({
        id: Math.random().toString(36).substr(2, 9),
        requests: 0,
        createdAt: Date.now()
      }),
      (client) => {
        // Reset client state if needed
        client.lastUsed = Date.now();
      },
      maxClients
    );
  }
  
  async makeRequest(url, options = {}) {
    const client = this.pool.acquire();
    
    try {
      client.requests++;
      
      // Simulate HTTP request
      await new Promise(resolve => setTimeout(resolve, 100));
      
      const response = {
        url,
        status: 200,
        data: { message: 'Success', clientId: client.id },
        clientRequests: client.requests
      };
      
      return response;
    } finally {
      this.pool.release(client);
    }
  }
  
  getStats() {
    return this.pool.getStats();
  }
}

// Usage Examples
async function demonstratePooling() {
  console.log('\n=== Database Connection Pool Demo ===');
  
  const dbPool = new DatabaseConnectionPool({
    host: 'localhost',
    database: 'telekom_db'
  }, 3);
  
  // Multiple concurrent database operations
  const operations = Array.from({ length: 5 }, async (_, i) => {
    return dbPool.withConnection(async (connection) => {
      const result = await connection.query(`SELECT * FROM users WHERE id = ?`, [i + 1]);
      console.log(`Query ${i + 1} executed on connection ${result.connectionId}`);
      return result;
    });
  });
  
  try {
    await Promise.all(operations);
    console.log('Database pool stats:', dbPool.getStats());
  } catch (error) {
    console.error('Database operations failed:', error.message);
  }
  
  console.log('\n=== HTTP Client Pool Demo ===');
  
  const httpPool = new HttpClientPool(2);
  
  // Multiple concurrent HTTP requests
  const requests = Array.from({ length: 4 }, async (_, i) => {
    return httpPool.makeRequest(`https://api.example.com/data/${i + 1}`);
  });
  
  try {
    const responses = await Promise.all(requests);
    responses.forEach((response, i) => {
      console.log(`Request ${i + 1}: Status ${response.status}, Client ${response.data.clientId}`);
    });
    
    console.log('HTTP pool stats:', httpPool.getStats());
  } catch (error) {
    console.error('HTTP requests failed:', error.message);
  }
  
  // Cleanup
  await dbPool.close();
}

demonstratePooling();
```

**Speaker Notes:** Object pooling is crucial for performance in enterprise applications. Database connection pools are the most common example, but the pattern applies to any expensive resources. The key insight is balancing pool size with memory usage and resource constraints.

---

# Lazy Loading Pattern

Lazy Loading defers object initialization until actually needed:

- **On-Demand Loading** - Load resources when first accessed
- **Memory Efficiency** - Reduce initial memory footprint
- **Startup Performance** - Faster application initialization
- **Bandwidth Conservation** - Load content progressively
- **Virtual Proxies** - Placeholder objects until real loading

```javascript
// Lazy Loading Implementation
class LazyLoader {
  constructor(loadFunction) {
    this.loadFunction = loadFunction;
    this.loaded = false;
    this.loading = false;
    this.value = null;
    this.error = null;
    this.loadPromise = null;
  }
  
  async get() {
    if (this.loaded) {
      return this.value;
    }
    
    if (this.error) {
      throw this.error;
    }
    
    if (this.loading) {
      return this.loadPromise;
    }
    
    this.loading = true;
    this.loadPromise = this.performLoad();
    
    try {
      this.value = await this.loadPromise;
      this.loaded = true;
      return this.value;
    } catch (error) {
      this.error = error;
      throw error;
    } finally {
      this.loading = false;
    }
  }
  
  async performLoad() {
    try {
      return await this.loadFunction();
    } catch (error) {
      console.error('Lazy loading failed:', error);
      throw error;
    }
  }
  
  isLoaded() {
    return this.loaded;
  }
  
  reset() {
    this.loaded = false;
    this.loading = false;
    this.value = null;
    this.error = null;
    this.loadPromise = null;
  }
}

// Configuration Lazy Loading
class ConfigurationManager {
  constructor() {
    this.configs = new Map();
  }
  
  registerConfig(name, loadFunction) {
    this.configs.set(name, new LazyLoader(loadFunction));
  }
  
  async getConfig(name) {
    const loader = this.configs.get(name);
    if (!loader) {
      throw new Error(`Configuration '${name}' not registered`);
    }
    
    return loader.get();
  }
  
  isConfigLoaded(name) {
    const loader = this.configs.get(name);
    return loader ? loader.isLoaded() : false;
  }
}

// Module Lazy Loading
class ModuleLoader {
  constructor() {
    this.modules = new Map();
  }
  
  registerModule(name, importFunction) {
    this.modules.set(name, new LazyLoader(importFunction));
  }
  
  async loadModule(name) {
    const loader = this.modules.get(name);
    if (!loader) {
      throw new Error(`Module '${name}' not registered`);
    }
    
    const module = await loader.get();
    console.log(`Module '${name}' loaded successfully`);
    return module;
  }
  
  getModuleStatus() {
    const status = {};
    this.modules.forEach((loader, name) => {
      status[name] = {
        loaded: loader.isLoaded(),
        loading: loader.loading,
        hasError: loader.error !== null
      };
    });
    return status;
  }
}

// Image Lazy Loading
class LazyImage {
  constructor(src, placeholder = null) {
    this.src = src;
    this.placeholder = placeholder;
    this.loaded = false;
    this.loading = false;
    this.image = null;
    this.observers = [];
  }
  
  async load() {
    if (this.loaded || this.loading) {
      return this.image;
    }
    
    this.loading = true;
    this.notifyObservers('loading', true);
    
    try {
      this.image = await this.loadImage(this.src);
      this.loaded = true;
      this.notifyObservers('loaded', this.image);
      return this.image;
    } catch (error) {
      this.notifyObservers('error', error);
      throw error;
    } finally {
      this.loading = false;
      this.notifyObservers('loading', false);
    }
  }
  
  loadImage(src) {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.onload = () => resolve(img);
      img.onerror = () => reject(new Error(`Failed to load image: ${src}`));
      img.src = src;
    });
  }
  
  subscribe(observer) {
    this.observers.push(observer);
  }
  
  unsubscribe(observer) {
    this.observers = this.observers.filter(obs => obs !== observer);
  }
  
  notifyObservers(event, data) {
    this.observers.forEach(observer => {
      if (typeof observer[event] === 'function') {
        observer[event](data);
      }
    });
  }
}

// Data Lazy Loading with Caching
class LazyDataService {
  constructor(apiClient) {
    this.apiClient = apiClient;
    this.cache = new Map();
    this.loaders = new Map();
  }
  
  getUserData(userId) {
    const cacheKey = `user:${userId}`;
    
    if (!this.loaders.has(cacheKey)) {
      this.loaders.set(cacheKey, new LazyLoader(async () => {
        console.log(`Loading user data for ID: ${userId}`);
        
        // Check cache first
        if (this.cache.has(cacheKey)) {
          return this.cache.get(cacheKey);
        }
        
        // Fetch from API
        const userData = await this.apiClient.get(`/users/${userId}`);
        
        // Cache the result
        this.cache.set(cacheKey, userData);
        
        return userData;
      }));
    }
    
    return this.loaders.get(cacheKey).get();
  }
  
  getOrderHistory(userId) {
    const cacheKey = `orders:${userId}`;
    
    if (!this.loaders.has(cacheKey)) {
      this.loaders.set(cacheKey, new LazyLoader(async () => {
        console.log(`Loading order history for user: ${userId}`);
        
        const orders = await this.apiClient.get(`/users/${userId}/orders`);
        this.cache.set(cacheKey, orders);
        
        return orders;
      }));
    }
    
    return this.loaders.get(cacheKey).get();
  }
  
  async invalidateUserCache(userId) {
    const userKey = `user:${userId}`;
    const ordersKey = `orders:${userId}`;
    
    this.cache.delete(userKey);
    this.cache.delete(ordersKey);
    
    // Reset loaders to force reload
    if (this.loaders.has(userKey)) {
      this.loaders.get(userKey).reset();
    }
    if (this.loaders.has(ordersKey)) {
      this.loaders.get(ordersKey).reset();
    }
  }
  
  getCacheStats() {
    return {
      cacheSize: this.cache.size,
      loadersCount: this.loaders.size,
      loadedItems: Array.from(this.loaders.entries())
        .filter(([key, loader]) => loader.isLoaded())
        .map(([key]) => key)
    };
  }
}

// Usage Examples
async function demonstrateLazyLoading() {
  console.log('\n=== Configuration Lazy Loading ===');
  
  const configManager = new ConfigurationManager();
  
  // Register configurations
  configManager.registerConfig('database', async () => {
    console.log('Loading database configuration...');
    await new Promise(resolve => setTimeout(resolve, 100)); // Simulate loading delay
    return {
      host: 'localhost',
      port: 5432,
      database: 'telekom_db',
      ssl: true
    };
  });
  
  configManager.registerConfig('api', async () => {
    console.log('Loading API configuration...');
    await new Promise(resolve => setTimeout(resolve, 150));
    return {
      baseUrl: 'https://api.telekom.de',
      timeout: 5000,
      retries: 3
    };
  });
  
  // Configurations are not loaded yet
  console.log('Database config loaded:', configManager.isConfigLoaded('database')); // false
  
  // Load configuration when needed
  const dbConfig = await configManager.getConfig('database');
  console.log('Database config:', dbConfig);
  console.log('Database config loaded:', configManager.isConfigLoaded('database')); // true
  
  console.log('\n=== Module Lazy Loading ===');
  
  const moduleLoader = new ModuleLoader();
  
  // Register modules (these would be real dynamic imports in practice)
  moduleLoader.registerModule('analytics', async () => {
    console.log('Loading analytics module...');
    await new Promise(resolve => setTimeout(resolve, 200));
    return {
      trackEvent: (event) => console.log(`Tracked: ${event}`),
      getMetrics: () => ({ sessions: 1234, pageViews: 5678 })
    };
  });
  
  moduleLoader.registerModule('reporting', async () => {
    console.log('Loading reporting module...');
    await new Promise(resolve => setTimeout(resolve, 300));
    return {
      generateReport: (type) => `Generated ${type} report`,
      exportToPDF: () => 'PDF export completed'
    };
  });
  
  console.log('Module status before loading:', moduleLoader.getModuleStatus());
  
  // Load modules on demand
  const analytics = await moduleLoader.loadModule('analytics');
  analytics.trackEvent('page_view');
  
  const reporting = await moduleLoader.loadModule('reporting');
  console.log(reporting.generateReport('monthly'));
  
  console.log('Module status after loading:', moduleLoader.getModuleStatus());
  
  console.log('\n=== Data Service Lazy Loading ===');
  
  // Mock API client
  const mockApiClient = {
    async get(endpoint) {
      await new Promise(resolve => setTimeout(resolve, 100));
      
      if (endpoint.includes('/users/')) {
        const userId = endpoint.split('/')[2];
        return {
          id: userId,
          name: `User ${userId}`,
          email: `user${userId}@telekom.de`,
          createdAt: new Date()
        };
      }
      
      if (endpoint.includes('/orders')) {
        const userId = endpoint.split('/')[2];
        return [
          { id: 1, userId, product: 'Internet Plan', amount: 49.99 },
          { id: 2, userId, product: 'Mobile Plan', amount: 29.99 }
        ];
      }
      
      throw new Error(`Unknown endpoint: ${endpoint}`);
    }
  };
  
  const dataService = new LazyDataService(mockApiClient);
  
  // First call loads and caches
  console.time('First user data call');
  const user1 = await dataService.getUserData('123');
  console.timeEnd('First user data call');
  console.log('User data:', user1);
  
  // Second call uses cached loader
  console.time('Second user data call');
  const user2 = await dataService.getUserData('123');
  console.timeEnd('Second user data call');
  console.log('Same user?', user1 === user2);
  
  // Load order history
  const orders = await dataService.getOrderHistory('123');
  console.log('Order history:', orders);
  
  console.log('Cache stats:', dataService.getCacheStats());
}

demonstrateLazyLoading();
```

**Speaker Notes:** Lazy loading is essential for modern applications, especially with large datasets or expensive resources. The pattern shines in micro-frontend architectures where you load modules on demand, reducing initial bundle size and improving performance.

---

# Testing Patterns

## Mock, Stub, and Test Double Patterns

Effective testing requires isolating units under test from their dependencies:

- **Mock Objects** - Verify interactions and behavior
- **Stub Objects** - Provide predetermined responses  
- **Fake Objects** - Working implementations with shortcuts
- **Test Doubles** - Generic term for all test substitutes
- **Dependency Injection** - Enable test double substitution

```javascript
// Test Double Framework
class TestDoubleFactory {
  static createMock(interfaceMethods = []) {
    const mock = {
      _calls: {},
      _expectations: {},
      _responses: {}
    };
    
    // Add tracking for each method
    interfaceMethods.forEach(method => {
      mock._calls[method] = [];
      mock._expectations[method] = [];
      mock._responses[method] = null;
      
      mock[method] = function(...args) {
        // Record the call
        const call = {
          arguments: args,
          timestamp: Date.now(),
          returnValue: null,
          error: null
        };
        
        mock._calls[method].push(call);
        
        // Check if we have a preset response
        if (mock._responses[method]) {
          const response = typeof mock._responses[method] === 'function'
            ? mock._responses[method](...args)
            : mock._responses[method];
            
          call.returnValue = response;
          
          if (response instanceof Error) {
            call.error = response;
            throw response;
          }
          
          return response;
        }
        
        // Default behavior
        return undefined;
      };
    });
    
    // Add verification methods
    mock.verify = function(method, times = null) {
      const calls = mock._calls[method] || [];
      
      if (times !== null && calls.length !== times) {
        throw new Error(`Expected ${method} to be called ${times} times, but was called ${calls.length} times`);
      }
      
      return calls.length > 0;
    };
    
    mock.verifyWith = function(method, ...expectedArgs) {
      const calls = mock._calls[method] || [];
      const matchingCalls = calls.filter(call => 
        JSON.stringify(call.arguments) === JSON.stringify(expectedArgs)
      );
      
      if (matchingCalls.length === 0) {
        throw new Error(`Expected ${method} to be called with ${JSON.stringify(expectedArgs)}, but it wasn't`);
      }
      
      return true;
    };
    
    mock.returns = function(method, value) {
      mock._responses[method] = value;
      return mock;
    };
    
    mock.throws = function(method, error) {
      mock._responses[method] = error;
      return mock;
    };
    
    mock.getCallCount = function(method) {
      return (mock._calls[method] || []).length;
    };
    
    mock.getCalls = function(method) {
      return mock._calls[method] || [];
    };
    
    return mock;
  }
  
  static createStub(responses = {}) {
    const stub = {};
    
    Object.keys(responses).forEach(method => {
      stub[method] = function(...args) {
        const response = responses[method];
        
        if (typeof response === 'function') {
          return response(...args);
        }
        
        if (response instanceof Error) {
          throw response;
        }
        
        return response;
      };
    });
    
    return stub;
  }
  
  static createSpy(originalObject, methodName) {
    const originalMethod = originalObject[methodName];
    const spy = {
      calls: [],
      callCount: 0,
      restore: () => {
        originalObject[methodName] = originalMethod;
      }
    };
    
    originalObject[methodName] = function(...args) {
      const call = {
        arguments: args,
        timestamp: Date.now(),
        returnValue: null,
        error: null
      };
      
      try {
        const result = originalMethod.apply(this, args);
        call.returnValue = result;
        spy.calls.push(call);
        spy.callCount++;
        return result;
      } catch (error) {
        call.error = error;
        spy.calls.push(call);
        spy.callCount++;
        throw error;
      }
    };
    
    return spy;
  }
}

// Example System Under Test
class OrderService {
  constructor(paymentGateway, inventoryService, emailService) {
    this.paymentGateway = paymentGateway;
    this.inventoryService = inventoryService;
    this.emailService = emailService;
  }
  
  async processOrder(order) {
    try {
      // Check inventory
      const inventoryResult = await this.inventoryService.checkAvailability(order.items);
      if (!inventoryResult.available) {
        throw new Error('Insufficient inventory');
      }
      
      // Reserve inventory
      const reservation = await this.inventoryService.reserveItems(order.items);
      
      try {
        // Process payment
        const payment = await this.paymentGateway.chargeCustomer({
          customerId: order.customerId,
          amount: order.total,
          currency: order.currency
        });
        
        // Send confirmation email
        await this.emailService.sendOrderConfirmation({
          email: order.customerEmail,
          orderId: order.id,
          items: order.items,
          total: order.total
        });
        
        return {
          success: true,
          orderId: order.id,
          paymentId: payment.id,
          reservationId: reservation.id
        };
        
      } catch (paymentError) {
        // Release inventory if payment fails
        await this.inventoryService.releaseReservation(reservation.id);
        throw paymentError;
      }
      
    } catch (error) {
      console.error('Order processing failed:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }
}

// Test Suite
class OrderServiceTests {
  constructor() {
    this.tests = [];
  }
  
  async runTests() {
    console.log('\n=== Order Service Tests ===');
    
    for (const test of this.tests) {
      try {
        console.log(`\nRunning: ${test.name}`);
        await test.run();
        console.log('✅ PASSED');
      } catch (error) {
        console.log('❌ FAILED:', error.message);
      }
    }
  }
  
  addTest(name, testFunction) {
    this.tests.push({ name, run: testFunction });
  }
}

// Test Implementation
const testSuite = new OrderServiceTests();

testSuite.addTest('Should process order successfully with mocks', async () => {
  // Arrange - Create mocks
  const paymentMock = TestDoubleFactory.createMock(['chargeCustomer']);
  const inventoryMock = TestDoubleFactory.createMock(['checkAvailability', 'reserveItems']);
  const emailMock = TestDoubleFactory.createMock(['sendOrderConfirmation']);
  
  // Setup mock responses
  paymentMock.returns('chargeCustomer', { id: 'payment-123', status: 'charged' });
  inventoryMock.returns('checkAvailability', { available: true });
  inventoryMock.returns('reserveItems', { id: 'reservation-456' });
  emailMock.returns('sendOrderConfirmation', { sent: true });
  
  const orderService = new OrderService(paymentMock, inventoryMock, emailMock);
  
  const testOrder = {
    id: 'order-789',
    customerId: 'customer-123',
    customerEmail: 'customer@telekom.de',
    items: [{ productId: 'product-1', quantity: 2 }],
    total: 99.98,
    currency: 'EUR'
  };
  
  // Act
  const result = await orderService.processOrder(testOrder);
  
  // Assert
  if (!result.success) {
    throw new Error('Expected successful order processing');
  }
  
  // Verify interactions
  inventoryMock.verify('checkAvailability', 1);
  inventoryMock.verify('reserveItems', 1);
  paymentMock.verify('chargeCustomer', 1);
  emailMock.verify('sendOrderConfirmation', 1);
  
  // Verify specific arguments
  paymentMock.verifyWith('chargeCustomer', {
    customerId: 'customer-123',
    amount: 99.98,
    currency: 'EUR'
  });
});

testSuite.addTest('Should handle payment failure and release inventory', async () => {
  // Arrange - Create mocks with payment failure
  const paymentMock = TestDoubleFactory.createMock(['chargeCustomer']);
  const inventoryMock = TestDoubleFactory.createMock(['checkAvailability', 'reserveItems', 'releaseReservation']);
  const emailMock = TestDoubleFactory.createMock(['sendOrderConfirmation']);
  
  // Setup responses
  inventoryMock.returns('checkAvailability', { available: true });
  inventoryMock.returns('reserveItems', { id: 'reservation-456' });
  paymentMock.throws('chargeCustomer', new Error('Payment declined'));
  
  const orderService = new OrderService(paymentMock, inventoryMock, emailMock);
  
  const testOrder = {
    id: 'order-789',
    customerId: 'customer-123',
    customerEmail: 'customer@telekom.de',
    items: [{ productId: 'product-1', quantity: 2 }],
    total: 99.98,
    currency: 'EUR'
  };
  
  // Act
  const result = await orderService.processOrder(testOrder);
  
  // Assert
  if (result.success) {
    throw new Error('Expected order processing to fail');
  }
  
  // Verify inventory was released after payment failure
  inventoryMock.verify('releaseReservation', 1);
  inventoryMock.verifyWith('releaseReservation', 'reservation-456');
  
  // Verify email was not sent
  if (emailMock.getCallCount('sendOrderConfirmation') > 0) {
    throw new Error('Email should not be sent when payment fails');
  }
});

testSuite.addTest('Should handle insufficient inventory', async () => {
  // Arrange - Create mocks with inventory shortage
  const paymentMock = TestDoubleFactory.createMock(['chargeCustomer']);
  const inventoryMock = TestDoubleFactory.createMock(['checkAvailability', 'reserveItems']);
  const emailMock = TestDoubleFactory.createMock(['sendOrderConfirmation']);
  
  // Setup inventory unavailability
  inventoryMock.returns('checkAvailability', { available: false });
  
  const orderService = new OrderService(paymentMock, inventoryMock, emailMock);
  
  const testOrder = {
    id: 'order-789',
    customerId: 'customer-123',
    customerEmail: 'customer@telekom.de',
    items: [{ productId: 'product-1', quantity: 100 }], // Large quantity
    total: 999.99,
    currency: 'EUR'
  };
  
  // Act
  const result = await orderService.processOrder(testOrder);
  
  // Assert
  if (result.success) {
    throw new Error('Expected order processing to fail due to insufficient inventory');
  }
  
  if (result.error !== 'Insufficient inventory') {
    throw new Error(`Expected 'Insufficient inventory' error, got: ${result.error}`);
  }
  
  // Verify no payment or email actions were attempted
  if (paymentMock.getCallCount('chargeCustomer') > 0) {
    throw new Error('Payment should not be attempted when inventory is insufficient');
  }
  
  if (emailMock.getCallCount('sendOrderConfirmation') > 0) {
    throw new Error('Email should not be sent when inventory is insufficient');
  }
});

testSuite.addTest('Should use stubs for simple responses', async () => {
  // Arrange - Create stubs for simple, predictable responses
  const paymentStub = TestDoubleFactory.createStub({
    chargeCustomer: { id: 'payment-stub-123', status: 'charged' }
  });
  
  const inventoryStub = TestDoubleFactory.createStub({
    checkAvailability: { available: true },
    reserveItems: { id: 'reservation-stub-456' }
  });
  
  const emailStub = TestDoubleFactory.createStub({
    sendOrderConfirmation: { sent: true, messageId: 'msg-stub-789' }
  });
  
  const orderService = new OrderService(paymentStub, inventoryStub, emailStub);
  
  const testOrder = {
    id: 'order-stub-test',
    customerId: 'customer-123',
    customerEmail: 'customer@telekom.de',
    items: [{ productId: 'product-1', quantity: 1 }],
    total: 49.99,
    currency: 'EUR'
  };
  
  // Act
  const result = await orderService.processOrder(testOrder);
  
  // Assert
  if (!result.success) {
    throw new Error('Expected successful order processing with stubs');
  }
  
  if (result.paymentId !== 'payment-stub-123') {
    throw new Error(`Expected payment ID 'payment-stub-123', got: ${result.paymentId}`);
  }
});

// Run the tests
testSuite.runTests();
```

**Speaker Notes:** Testing patterns are crucial for maintainable code. Mocks verify behavior (how many times was a method called), stubs provide data, and spies monitor real objects. The key insight is isolating your unit under test from its dependencies to create fast, reliable tests.

---

# Pattern Combinations & Anti-Patterns

## Real-World Pattern Combinations

Enterprise applications rarely use patterns in isolation - they combine patterns for robust solutions:

- **Repository + Unit of Work** - Data access with transaction management
- **Command + Observer** - Undo/Redo with event notifications
- **Factory + Strategy + Dependency Injection** - Configurable object creation
- **MVC + Observer + Command** - Interactive applications with event handling
- **Circuit Breaker + Retry + Bulkhead** - Resilience patterns combination

```javascript
// Pattern Combination Example: E-Commerce Order System
// Combines: Factory, Strategy, Observer, Command, Repository patterns

// Strategy Pattern for Payment Processing
class PaymentStrategy {
  async processPayment(amount, details) {
    throw new Error('Must implement processPayment method');
  }
}

class CreditCardPayment extends PaymentStrategy {
  async processPayment(amount, details) {
    console.log(`Processing credit card payment: €${amount}`);
    // Simulate credit card processing
    await new Promise(resolve => setTimeout(resolve, 100));
    
    return {
      id: `cc_${Date.now()}`,
      method: 'credit_card',
      amount,
      status: 'completed',
      transactionFee: amount * 0.029 // 2.9%
    };
  }
}

class PayPalPayment extends PaymentStrategy {
  async processPayment(amount, details) {
    console.log(`Processing PayPal payment: €${amount}`);
    await new Promise(resolve => setTimeout(resolve, 150));
    
    return {
      id: `pp_${Date.now()}`,
      method: 'paypal',
      amount,
      status: 'completed',
      transactionFee: amount * 0.035 // 3.5%
    };
  }
}

class BankTransferPayment extends PaymentStrategy {
  async processPayment(amount, details) {
    console.log(`Processing bank transfer: €${amount}`);
    await new Promise(resolve => setTimeout(resolve, 200));
    
    return {
      id: `bt_${Date.now()}`,
      method: 'bank_transfer',
      amount,
      status: 'pending', // Bank transfers take time
      transactionFee: 2.50 // Fixed fee
    };
  }
}

// Factory Pattern for Payment Strategy Creation
class PaymentStrategyFactory {
  static createStrategy(paymentMethod) {
    switch (paymentMethod) {
      case 'credit_card':
        return new CreditCardPayment();
      case 'paypal':
        return new PayPalPayment();
      case 'bank_transfer':
        return new BankTransferPayment();
      default:
        throw new Error(`Unsupported payment method: ${paymentMethod}`);
    }
  }
  
  static getSupportedMethods() {
    return ['credit_card', 'paypal', 'bank_transfer'];
  }
}

// Observer Pattern for Order Events
class OrderEventEmitter {
  constructor() {
    this.observers = new Map();
  }
  
  subscribe(event, observer) {
    if (!this.observers.has(event)) {
      this.observers.set(event, []);
    }
    this.observers.get(event).push(observer);
  }
  
  unsubscribe(event, observer) {
    if (this.observers.has(event)) {
      const observers = this.observers.get(event);
      const index = observers.indexOf(observer);
      if (index > -1) {
        observers.splice(index, 1);
      }
    }
  }
  
  emit(event, data) {
    if (this.observers.has(event)) {
      this.observers.get(event).forEach(observer => {
        try {
          observer.handle(event, data);
        } catch (error) {
          console.error(`Observer error for event ${event}:`, error);
        }
      });
    }
  }
}

// Command Pattern for Order Operations
class OrderCommand {
  constructor(orderService, orderData) {
    this.orderService = orderService;
    this.orderData = orderData;
    this.executed = false;
    this.result = null;
  }
  
  async execute() {
    if (this.executed) {
      throw new Error('Command already executed');
    }
    
    this.result = await this.orderService.processOrder(this.orderData);
    this.executed = true;
    return this.result;
  }
  
  canUndo() {
    return this.executed && this.result && this.result.success;
  }
  
  async undo() {
    if (!this.canUndo()) {
      throw new Error('Cannot undo command');
    }
    
    await this.orderService.cancelOrder(this.result.orderId);
    console.log(`Undone order command for order: ${this.result.orderId}`);
  }
}

// Repository Pattern for Order Persistence
class OrderRepository {
  constructor() {
    this.orders = new Map();
    this.nextId = 1;
  }
  
  async save(order) {
    if (!order.id) {
      order.id = `ORDER-${this.nextId++}`;
    }
    
    order.updatedAt = new Date();
    this.orders.set(order.id, { ...order });
    
    return order;
  }
  
  async findById(orderId) {
    return this.orders.get(orderId) || null;
  }
  
  async findByCustomerId(customerId) {
    return Array.from(this.orders.values())
      .filter(order => order.customerId === customerId);
  }
  
  async updateStatus(orderId, status) {
    const order = this.orders.get(orderId);
    if (order) {
      order.status = status;
      order.updatedAt = new Date();
      return order;
    }
    return null;
  }
  
  async delete(orderId) {
    return this.orders.delete(orderId);
  }
}

// Combined Order Service
class EnhancedOrderService {
  constructor(orderRepository, eventEmitter) {
    this.orderRepository = orderRepository;
    this.eventEmitter = eventEmitter;
    this.commandHistory = [];
  }
  
  async processOrder(orderData) {
    const order = {
      ...orderData,
      status: 'processing',
      createdAt: new Date()
    };
    
    try {
      // Emit order started event
      this.eventEmitter.emit('order.started', { order });
      
      // Save initial order
      const savedOrder = await this.orderRepository.save(order);
      
      // Process payment using Strategy pattern
      const paymentStrategy = PaymentStrategyFactory.createStrategy(order.paymentMethod);
      const paymentResult = await paymentStrategy.processPayment(order.total, order.paymentDetails);
      
      // Update order with payment info
      savedOrder.paymentResult = paymentResult;
      savedOrder.status = paymentResult.status === 'completed' ? 'paid' : 'payment_pending';
      
      await this.orderRepository.save(savedOrder);
      
      // Emit payment processed event
      this.eventEmitter.emit('order.payment_processed', { 
        order: savedOrder, 
        payment: paymentResult 
      });
      
      if (paymentResult.status === 'completed') {
        // Complete the order
        savedOrder.status = 'completed';
        await this.orderRepository.save(savedOrder);
        
        this.eventEmitter.emit('order.completed', { order: savedOrder });
      }
      
      return {
        success: true,
        orderId: savedOrder.id,
        status: savedOrder.status,
        payment: paymentResult
      };
      
    } catch (error) {
      // Handle failure
      order.status = 'failed';
      order.error = error.message;
      await this.orderRepository.save(order);
      
      this.eventEmitter.emit('order.failed', { order, error });
      
      return {
        success: false,
        error: error.message
      };
    }
  }
  
  async executeOrderCommand(orderData) {
    const command = new OrderCommand(this, orderData);
    this.commandHistory.push(command);
    
    return command.execute();
  }
  
  async undoLastOrder() {
    const lastCommand = this.commandHistory[this.commandHistory.length - 1];
    if (lastCommand && lastCommand.canUndo()) {
      await lastCommand.undo();
      return true;
    }
    return false;
  }
  
  async cancelOrder(orderId) {
    const order = await this.orderRepository.findById(orderId);
    if (order) {
      order.status = 'cancelled';
      await this.orderRepository.save(order);
      
      this.eventEmitter.emit('order.cancelled', { order });
      return order;
    }
    throw new Error(`Order ${orderId} not found`);
  }
}

// Observer implementations
class EmailNotificationObserver {
  handle(event, data) {
    switch (event) {
      case 'order.completed':
        this.sendOrderConfirmation(data.order);
        break;
      case 'order.failed':
        this.sendOrderFailureNotification(data.order, data.error);
        break;
      case 'order.cancelled':
        this.sendCancellationNotification(data.order);
        break;
    }
  }
  
  sendOrderConfirmation(order) {
    console.log(`📧 Sending order confirmation email for order ${order.id}`);
  }
  
  sendOrderFailureNotification(order, error) {
    console.log(`📧 Sending order failure notification for order ${order.id}: ${error.message}`);
  }
  
  sendCancellationNotification(order) {
    console.log(`📧 Sending cancellation notification for order ${order.id}`);
  }
}

class AnalyticsObserver {
  handle(event, data) {
    switch (event) {
      case 'order.started':
        this.trackOrderStarted(data.order);
        break;
      case 'order.completed':
        this.trackOrderCompleted(data.order);
        break;
      case 'order.failed':
        this.trackOrderFailed(data.order, data.error);
        break;
    }
  }
  
  trackOrderStarted(order) {
    console.log(`📊 Analytics: Order started - ${order.id} (€${order.total})`);
  }
  
  trackOrderCompleted(order) {
    console.log(`📊 Analytics: Order completed - ${order.id} (€${order.total})`);
  }
  
  trackOrderFailed(order, error) {
    console.log(`📊 Analytics: Order failed - ${order.id}: ${error.message}`);
  }
}

class InventoryObserver {
  handle(event, data) {
    if (event === 'order.completed') {
      this.updateInventory(data.order);
    }
  }
  
  updateInventory(order) {
    console.log(`📦 Inventory: Reducing stock for order ${order.id}`);
    // Update inventory levels
  }
}

// Usage Example - Demonstrates Pattern Combination
async function demonstratePatternCombination() {
  console.log('\n=== Pattern Combination Demo: E-Commerce Order System ===');
  
  // Setup system components
  const orderRepository = new OrderRepository();
  const eventEmitter = new OrderEventEmitter();
  const orderService = new EnhancedOrderService(orderRepository, eventEmitter);
  
  // Setup observers
  const emailObserver = new EmailNotificationObserver();
  const analyticsObserver = new AnalyticsObserver();
  const inventoryObserver = new InventoryObserver();
  
  // Subscribe observers to events
  eventEmitter.subscribe('order.started', analyticsObserver);
  eventEmitter.subscribe('order.completed', emailObserver);
  eventEmitter.subscribe('order.completed', analyticsObserver);
  eventEmitter.subscribe('order.completed', inventoryObserver);
  eventEmitter.subscribe('order.failed', emailObserver);
  eventEmitter.subscribe('order.failed', analyticsObserver);
  eventEmitter.subscribe('order.cancelled', emailObserver);
  
  // Test different payment methods
  const orders = [
    {
      customerId: 'CUST-001',
      items: [
        { productId: 'PROD-A', quantity: 2, price: 49.99 },
        { productId: 'PROD-B', quantity: 1, price: 79.99 }
      ],
      total: 179.97,
      paymentMethod: 'credit_card',
      paymentDetails: { cardNumber: '****-****-****-1234' }
    },
    {
      customerId: 'CUST-002',
      items: [
        { productId: 'PROD-C', quantity: 1, price: 299.99 }
      ],
      total: 299.99,
      paymentMethod: 'paypal',
      paymentDetails: { email: 'customer@example.com' }
    },
    {
      customerId: 'CUST-003',
      items: [
        { productId: 'PROD-D', quantity: 3, price: 29.99 }
      ],
      total: 89.97,
      paymentMethod: 'bank_transfer',
      paymentDetails: { iban: 'DE89370400440532013000' }
    }
  ];
  
  // Process orders using Command pattern
  for (const orderData of orders) {
    console.log(`\nProcessing order with ${orderData.paymentMethod}...`);
    
    try {
      const result = await orderService.executeOrderCommand(orderData);
      console.log('Order result:', result);
    } catch (error) {
      console.error('Order processing failed:', error.message);
    }
  }
  
  // Demonstrate undo functionality
  console.log('\n--- Demonstrating Undo Functionality ---');
  const undoResult = await orderService.undoLastOrder();
  console.log('Undo successful:', undoResult);
  
  // Show final order repository state
  console.log('\n--- Final Order Repository State ---');
  const allOrders = Array.from(orderRepository.orders.values());
  allOrders.forEach(order => {
    console.log(`Order ${order.id}: Status = ${order.status}, Total = €${order.total}`);
  });
}

demonstratePatternCombination();
```

**Speaker Notes:** This example shows how patterns naturally combine in enterprise systems. Strategy provides flexible payment processing, Observer enables loose coupling for notifications, Command enables undo/redo, Factory creates objects cleanly, and Repository manages persistence. The key insight is that patterns work better together than in isolation.

---

# Common Anti-Patterns

Anti-patterns are common solutions that appear beneficial but create more problems than they solve:

- **God Object** - Classes that do too many things
- **Spaghetti Code** - Tangled, unstructured code flow
- **Golden Hammer** - Using one pattern for everything
- **Premature Optimization** - Optimizing before profiling
- **Copy-Paste Programming** - Duplicating code instead of abstracting

```javascript
// Anti-Pattern Examples and Their Solutions

// 1. GOD OBJECT ANTI-PATTERN
// Bad: God Object that does everything
class OrderManagerAntiPattern {
  constructor() {
    this.orders = [];
    this.customers = [];
    this.products = [];
    this.inventory = {};
    this.payments = [];
    this.emails = [];
    this.analytics = {};
  }
  
  // Order management
  createOrder(orderData) { /* ... */ }
  updateOrder(orderId, data) { /* ... */ }
  cancelOrder(orderId) { /* ... */ }
  
  // Customer management
  createCustomer(customerData) { /* ... */ }
  updateCustomer(customerId, data) { /* ... */ }
  deleteCustomer(customerId) { /* ... */ }
  
  // Product management
  addProduct(productData) { /* ... */ }
  updateProduct(productId, data) { /* ... */ }
  removeProduct(productId) { /* ... */ }
  
  // Inventory management
  checkStock(productId) { /* ... */ }
  updateStock(productId, quantity) { /* ... */ }
  reserveStock(productId, quantity) { /* ... */ }
  
  // Payment processing
  processPayment(paymentData) { /* ... */ }
  refundPayment(paymentId) { /* ... */ }
  
  // Email notifications
  sendOrderConfirmation(orderId) { /* ... */ }
  sendShippingNotification(orderId) { /* ... */ }
  
  // Analytics
  trackOrderEvent(event, data) { /* ... */ }
  generateReport(reportType) { /* ... */ }
  
  // This class violates Single Responsibility Principle!
  // It has too many reasons to change and is hard to test and maintain.
}

// SOLUTION: Separate Responsibilities
class OrderService {
  constructor(customerService, productService, inventoryService, paymentService, emailService, analyticsService) {
    this.customerService = customerService;
    this.productService = productService;
    this.inventoryService = inventoryService;
    this.paymentService = paymentService;
    this.emailService = emailService;
    this.analyticsService = analyticsService;
    this.orders = [];
  }
  
  async createOrder(orderData) {
    // Validate customer
    const customer = await this.customerService.getCustomer(orderData.customerId);
    if (!customer) {
      throw new Error('Customer not found');
    }
    
    // Validate products and inventory
    for (const item of orderData.items) {
      const product = await this.productService.getProduct(item.productId);
      if (!product) {
        throw new Error(`Product ${item.productId} not found`);
      }
      
      const available = await this.inventoryService.checkAvailability(item.productId, item.quantity);
      if (!available) {
        throw new Error(`Insufficient inventory for product ${item.productId}`);
      }
    }
    
    // Create order
    const order = {
      id: `ORDER-${Date.now()}`,
      ...orderData,
      status: 'created',
      createdAt: new Date()
    };
    
    this.orders.push(order);
    
    // Track analytics
    this.analyticsService.trackEvent('order_created', { orderId: order.id });
    
    return order;
  }
}

// Individual services following Single Responsibility
class CustomerService {
  constructor() {
    this.customers = new Map();
  }
  
  async getCustomer(customerId) {
    return this.customers.get(customerId);
  }
  
  async createCustomer(customerData) {
    const customer = { id: `CUST-${Date.now()}`, ...customerData };
    this.customers.set(customer.id, customer);
    return customer;
  }
}

// 2. GOLDEN HAMMER ANTI-PATTERN
// Bad: Using Singleton for everything
class ConfigSingleton {
  static instance = null;
  
  static getInstance() {
    if (!this.instance) {
      this.instance = new ConfigSingleton();
    }
    return this.instance;
  }
}

class DatabaseSingleton {
  static instance = null;
  
  static getInstance() {
    if (!this.instance) {
      this.instance = new DatabaseSingleton();
    }
    return this.instance;
  }
}

class LoggerSingleton {
  static instance = null;
  
  static getInstance() {
    if (!this.instance) {
      this.instance = new LoggerSingleton();
    }
    return this.instance;
  }
}

// Problems with overusing Singleton:
// - Hard to test (global state)
// - Tight coupling
// - Difficult to extend
// - Thread safety issues

// SOLUTION: Use Dependency Injection and appropriate patterns
class DependencyContainer {
  constructor() {
    this.services = new Map();
    this.singletons = new Map();
  }
  
  registerSingleton(name, factory) {
    this.services.set(name, { type: 'singleton', factory });
  }
  
  registerTransient(name, factory) {
    this.services.set(name, { type: 'transient', factory });
  }
  
  resolve(name) {
    const service = this.services.get(name);
    if (!service) {
      throw new Error(`Service ${name} not registered`);
    }
    
    if (service.type === 'singleton') {
      if (!this.singletons.has(name)) {
        this.singletons.set(name, service.factory());
      }
      return this.singletons.get(name);
    }
    
    return service.factory();
  }
}

// Usage with proper dependency injection
const container = new DependencyContainer();

container.registerSingleton('config', () => new Configuration());
container.registerTransient('database', () => new DatabaseConnection(container.resolve('config')));
container.registerTransient('logger', () => new Logger(container.resolve('config')));

// 3. SPAGHETTI CODE ANTI-PATTERN
// Bad: Tangled control flow
function processOrderAntiPattern(order) {
  if (order.items && order.items.length > 0) {
    let total = 0;
    for (let i = 0; i < order.items.length; i++) {
      if (order.items[i].price > 0) {
        total += order.items[i].price * order.items[i].quantity;
        if (order.items[i].category === 'electronics') {
          if (order.customer.membership === 'premium') {
            total *= 0.9; // 10% discount
          } else if (order.customer.membership === 'gold') {
            total *= 0.85; // 15% discount
          }
        }
      }
    }
    
    if (total > 100) {
      if (order.shipping === 'express') {
        total += 15;
      } else if (order.shipping === 'standard') {
        total += 5;
      }
    }
    
    if (order.paymentMethod === 'credit_card') {
      if (total > 1000) {
        // Additional verification needed
        return { status: 'pending_verification', total };
      }
    }
    
    return { status: 'processed', total };
  }
  
  return { status: 'invalid', total: 0 };
}

// SOLUTION: Structured, single-responsibility functions
class OrderProcessor {
  constructor(discountCalculator, shippingCalculator, paymentValidator) {
    this.discountCalculator = discountCalculator;
    this.shippingCalculator = shippingCalculator;
    this.paymentValidator = paymentValidator;
  }
  
  processOrder(order) {
    if (!this.validateOrder(order)) {
      return { status: 'invalid', total: 0 };
    }
    
    let total = this.calculateItemsTotal(order.items);
    total = this.discountCalculator.applyDiscounts(total, order.items, order.customer);
    total = this.shippingCalculator.addShippingCost(total, order.shipping);
    
    const validationResult = this.paymentValidator.validate(order.paymentMethod, total);
    
    return {
      status: validationResult.needsVerification ? 'pending_verification' : 'processed',
      total
    };
  }
  
  validateOrder(order) {
    return order && 
           order.items && 
           order.items.length > 0 &&
           order.customer &&
           order.paymentMethod;
  }
  
  calculateItemsTotal(items) {
    return items.reduce((total, item) => {
      if (item.price > 0 && item.quantity > 0) {
        return total + (item.price * item.quantity);
      }
      return total;
    }, 0);
  }
}

class DiscountCalculator {
  applyDiscounts(total, items, customer) {
    const electronicsItems = items.filter(item => item.category === 'electronics');
    if (electronicsItems.length === 0) {
      return total;
    }
    
    const discountMultiplier = this.getDiscountMultiplier(customer.membership);
    const electronicsTotal = electronicsItems.reduce((sum, item) => 
      sum + (item.price * item.quantity), 0);
    const otherItemsTotal = total - electronicsTotal;
    
    return otherItemsTotal + (electronicsTotal * discountMultiplier);
  }
  
  getDiscountMultiplier(membership) {
    const discounts = {
      'premium': 0.9,  // 10% discount
      'gold': 0.85,    // 15% discount
      'standard': 1.0  // No discount
    };
    
    return discounts[membership] || 1.0;
  }
}

// 4. PREMATURE OPTIMIZATION ANTI-PATTERN
// Bad: Optimizing without measuring
class PrematureOptimizationExample {
  constructor() {
    // Prematurely complex caching without knowing if it's needed
    this.cache = new Map();
    this.cacheHits = 0;
    this.cacheMisses = 0;
    
    // Complex object pooling for simple objects
    this.objectPool = [];
    this.poolSize = 1000;
    this.initializePool();
  }
  
  initializePool() {
    // Creating 1000 objects upfront "for performance"
    for (let i = 0; i < this.poolSize; i++) {
      this.objectPool.push(this.createComplexObject());
    }
  }
  
  createComplexObject() {
    // Simple object made complex for "optimization"
    return {
      id: Math.random(),
      data: new ArrayBuffer(1024), // Unnecessary memory allocation
      timestamp: Date.now(),
      metadata: new Map()
    };
  }
  
  getData(key) {
    // Complex caching logic for data that might not benefit from caching
    if (this.cache.has(key)) {
      this.cacheHits++;
      return this.cache.get(key);
    }
    
    this.cacheMisses++;
    const data = this.fetchData(key); // Simple operation made complex
    this.cache.set(key, data);
    return data;
  }
  
  fetchData(key) {
    // Simple operation that doesn't need optimization
    return { key, value: `data_${key}`, timestamp: Date.now() };
  }
}

// SOLUTION: Simple first, optimize when needed
class SimpleSolution {
  getData(key) {
    // Start simple
    return this.fetchData(key);
  }
  
  fetchData(key) {
    return { key, value: `data_${key}`, timestamp: Date.now() };
  }
  
  // Add caching only when profiling shows it's needed
  // Add pooling only when object creation is proven expensive
  // Measure first, optimize second
}

// 5. COPY-PASTE PROGRAMMING ANTI-PATTERN
// Bad: Duplicated code
function validateUser(user) {
  if (!user.name || user.name.trim() === '') {
    throw new Error('Name is required');
  }
  if (!user.email || user.email.trim() === '') {
    throw new Error('Email is required');
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(user.email)) {
    throw new Error('Invalid email format');
  }
}

function validateCustomer(customer) {
  if (!customer.name || customer.name.trim() === '') {
    throw new Error('Name is required');
  }
  if (!customer.email || customer.email.trim() === '') {
    throw new Error('Email is required');
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(customer.email)) {
    throw new Error('Invalid email format');
  }
}

function validateAdmin(admin) {
  if (!admin.name || admin.name.trim() === '') {
    throw new Error('Name is required');
  }
  if (!admin.email || admin.email.trim() === '') {
    throw new Error('Email is required');
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(admin.email)) {
    throw new Error('Invalid email format');
  }
}

// SOLUTION: Extract common functionality
class PersonValidator {
  static validateName(name, fieldName = 'Name') {
    if (!name || name.trim() === '') {
      throw new Error(`${fieldName} is required`);
    }
  }
  
  static validateEmail(email) {
    if (!email || email.trim() === '') {
      throw new Error('Email is required');
    }
    
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      throw new Error('Invalid email format');
    }
  }
  
  static validateBasicPerson(person, personType = 'Person') {
    this.validateName(person.name, `${personType} name`);
    this.validateEmail(person.email);
  }
}

// Specific validators that extend the base
class UserValidator extends PersonValidator {
  static validate(user) {
    super.validateBasicPerson(user, 'User');
    // Add user-specific validations here
  }
}

class CustomerValidator extends PersonValidator {
  static validate(customer) {
    super.validateBasicPerson(customer, 'Customer');
    // Add customer-specific validations here
  }
}

class AdminValidator extends PersonValidator {
  static validate(admin) {
    super.validateBasicPerson(admin, 'Admin');
    // Add admin-specific validations here
    if (!admin.permissions || admin.permissions.length === 0) {
      throw new Error('Admin must have at least one permission');
    }
  }
}

console.log('\n=== Anti-Pattern Solutions Demo ===');

// Demonstrate proper solutions
const container = new DependencyContainer();
container.registerSingleton('discountCalculator', () => new DiscountCalculator());
container.registerSingleton('shippingCalculator', () => ({
  addShippingCost: (total, method) => {
    if (total > 100) return total;
    return total + (method === 'express' ? 15 : 5);
  }
}));
container.registerSingleton('paymentValidator', () => ({
  validate: (method, total) => ({
    needsVerification: method === 'credit_card' && total > 1000
  })
}));

const orderProcessor = new OrderProcessor(
  container.resolve('discountCalculator'),
  container.resolve('shippingCalculator'),
  container.resolve('paymentValidator')
);

const testOrder = {
  items: [
    { price: 100, quantity: 2, category: 'electronics' },
    { price: 50, quantity: 1, category: 'books' }
  ],
  customer: { membership: 'premium' },
  shipping: 'express',
  paymentMethod: 'credit_card'
};

const result = orderProcessor.processOrder(testOrder);
console.log('Order processing result:', result);

// Test validators
try {
  UserValidator.validate({ name: 'John Doe', email: 'john@telekom.de' });
  console.log('✅ User validation passed');
} catch (error) {
  console.log('❌ User validation failed:', error.message);
}
```

**Speaker Notes:** Anti-patterns are as important to learn as patterns themselves. The God Object anti-pattern is extremely common in enterprise codebases. Copy-paste programming creates maintenance nightmares. The key insight is that good design is about knowing when NOT to apply a pattern as much as when to apply it.

---

# Workshop Summary & Synthesis

## Four Days of Design Patterns Mastery

**Complete Learning Journey:**

- **Day 1** - Creational patterns: Singleton, Factory, Builder, Prototype
- **Day 2** - Structural patterns: Adapter, Decorator, Facade, Composite, Proxy
- **Day 3** - Behavioral patterns: Observer, Strategy, Command, Template Method, State
- **Day 4** - Architecture patterns: MVC/MVP/MVVM, Microservices, Performance, Testing

**Speaker Notes:** We've covered the essential patterns every enterprise architect needs. From basic object creation to complex distributed systems, these patterns form the foundation of maintainable software architecture. The progression from fundamental patterns to advanced combinations mirrors real-world development complexity.

---

# Key Insights & Best Practices

## Pattern Selection Guidelines

**When to use patterns:**
- Recurring design problems with proven solutions
- Code that needs flexibility and extensibility  
- Complex systems requiring loose coupling
- Team collaboration requiring shared vocabulary
- Enterprise systems with long-term maintenance needs

**When NOT to use patterns:**
- Simple problems with straightforward solutions
- One-off implementations without reuse requirements
- Performance-critical code where simplicity trumps flexibility
- Small teams or projects where overhead exceeds benefits
- When patterns add complexity without clear value

## Implementation Success Factors

- **Start Simple** - Apply patterns when complexity justifies them
- **Know Your Context** - Choose patterns appropriate for your domain
- **Test Thoroughly** - Patterns should improve testability, not hinder it
- **Document Decisions** - Explain pattern choices for future maintainers
- **Refactor Gradually** - Introduce patterns through iterative improvement

**Speaker Notes:** The most important lesson is knowing when patterns help vs. when they hurt. Patterns are tools, not goals. The best architects apply patterns judiciously, not automatically. Focus on solving problems, not collecting pattern implementations.

---

# Enterprise Integration Strategies

## Enterprise-Specific Applications

**Customer Portal Architecture:**
- **MVC Pattern** - Separates customer interface from business logic
- **Observer Pattern** - Real-time notifications for service updates
- **Strategy Pattern** - Multiple authentication methods (SSO, OAuth, traditional)
- **Circuit Breaker** - Resilient integration with backend systems

**Billing System Patterns:**
- **Command Pattern** - Auditable billing operations with undo capability
- **Saga Pattern** - Distributed transactions across multiple services
- **Repository Pattern** - Consistent data access across billing components
- **Factory Pattern** - Different calculation strategies for various service types

**Network Management Integration:**
- **Adapter Pattern** - Interface with legacy network equipment APIs
- **Facade Pattern** - Simplified interface for complex network operations
- **Proxy Pattern** - Caching and access control for expensive network queries
- **Template Method** - Standardized procedures for network configuration changes

## Scalability Considerations

**Horizontal Scaling Patterns:**
- Service mesh with microservice patterns
- Event-driven architecture with Observer pattern
- Stateless services with proper session management
- Database sharding with Repository pattern abstraction

**Vertical Scaling Optimizations:**
- Object pooling for expensive resource management
- Caching strategies with appropriate invalidation
- Lazy loading for large data structures
- Asynchronous processing with Command pattern

**Speaker Notes:** These examples show how patterns scale from individual classes to enterprise architecture. The key insight is that patterns provide language and structure for communicating complex design decisions across large teams and long timescales.

---

# Advanced Topics for Further Learning

## Next Steps in Architecture Mastery

**Distributed Systems Patterns:**
- **Event Sourcing** - Audit trails and temporal queries
- **CQRS** - Command-Query Responsibility Segregation
- **Bulkhead** - Fault isolation in microservices
- **Strangler Fig** - Legacy system migration patterns

**Cloud-Native Patterns:**
- **Sidecar** - Cross-cutting concerns in container environments
- **Ambassador** - External service communication patterns
- **Scatter-Gather** - Parallel processing and aggregation
- **Backends for Frontends** - API composition for different client types

**AI/ML Integration Patterns:**
- **Model-View-Controller** adapted for ML pipelines
- **Strategy Pattern** for algorithm selection and A/B testing
- **Observer Pattern** for model performance monitoring
- **Command Pattern** for ML experiment tracking

## Recommended Reading & Resources

**Essential Books:**
- "Design Patterns" by Gang of Four (GoF)
- "Patterns of Enterprise Application Architecture" by Martin Fowler
- "Building Microservices" by Sam Newman
- "Clean Architecture" by Robert C. Martin

**Online Resources:**
- Microservice patterns: microservices.io
- Enterprise patterns: martinfowler.com/eaaCatalog
- Cloud patterns: docs.microsoft.com/en-us/azure/architecture/patterns
- Enterprise Architecture Guidelines: [Internal Architecture Portal]

**Speaker Notes:** The learning journey doesn't end here. These advanced patterns build on the foundation we've established. The key is to continue practicing pattern recognition in your daily work and gradually applying more sophisticated patterns as your systems grow in complexity.

---

# Day 4 Practical Exercises

## Exercise 1: Pattern Combination Challenge

**Scenario:** Design a resilient order processing system that combines multiple patterns.

**Requirements:**
- Support multiple payment methods (Strategy)
- Enable order operations tracking and undo (Command)
- Notify multiple systems when orders change (Observer)
- Handle service failures gracefully (Circuit Breaker)
- Cache frequently accessed data (Caching patterns)

**Deliverables:**
- System architecture diagram
- Key pattern implementations
- Integration test scenarios
- Performance considerations


## Exercise 2: Anti-Pattern Refactoring

**Task:** Identify and refactor anti-patterns in provided code samples.

**Code Samples:**
- God Object managing entire e-commerce system
- Spaghetti code with complex nested conditionals
- Copy-paste validation logic across multiple classes
- Premature optimization with unnecessary complexity

**Process:**
1. Identify the anti-patterns
2. Explain the problems they cause
3. Propose pattern-based solutions
4. Implement the refactoring
5. Compare before/after maintainability


## Exercise 3: Architecture Decision Records

**Challenge:** Document pattern decisions for a real Enterprise system.

**System:** Customer Self-Service Portal

**Tasks:**
- Analyze current architecture patterns
- Identify pattern gaps or anti-patterns
- Propose pattern improvements
- Document decision rationale
- Consider migration strategy

**Format:** Architecture Decision Record (ADR) template


**Speaker Notes:** These exercises synthesize the entire workshop experience. Exercise 1 tests pattern combination skills, Exercise 2 reinforces anti-pattern recognition, and Exercise 3 applies learning to real-world scenarios. The goal is practical application, not theoretical perfection.

---

# Workshop Conclusion & Next Steps

## Key Takeaways Summary

**Technical Mastery:**
- **25+ Design Patterns** learned across all categories
- **Real-world examples** with enterprise-ready implementations
- **Pattern combinations** for complex system architecture
- **Anti-pattern recognition** for better design decisions
- **Testing strategies** for pattern-based code

**Business Value:**
- **Reduced Development Time** through proven solutions
- **Improved Code Quality** via established best practices
- **Enhanced Team Communication** with shared pattern vocabulary
- **Increased Maintainability** through structured design approaches
- **Risk Mitigation** by avoiding common design pitfalls

## Immediate Action Items

**For Individual Developers:**
1. **Pattern Portfolio** - Create personal reference of learned patterns
2. **Code Review Focus** - Look for pattern opportunities in current projects
3. **Refactoring Targets** - Identify anti-patterns in existing codebase
4. **Learning Continuation** - Choose 2-3 advanced patterns to explore further

**For Development Teams:**
1. **Pattern Guidelines** - Establish team standards for pattern usage
2. **Architecture Reviews** - Include pattern analysis in design reviews
3. **Knowledge Sharing** - Organize pattern-focused technical talks
4. **Code Standards** - Update coding standards to include pattern guidance

**For Enterprise Architecture:**
1. **Pattern Library** - Build organization-wide pattern catalog
2. **Training Program** - Expand pattern education across development teams
3. **Architecture Governance** - Include pattern compliance in architecture reviews
4. **Tool Integration** - Select development tools that support pattern-based development

## Long-term Development Path

**Month 1-2:** Apply basic patterns in current projects
**Month 3-6:** Explore microservice and distributed system patterns  
**Month 6-12:** Master advanced architectural patterns and enterprise integration
**Year 2+:** Contribute to organizational pattern library and mentor others

**Speaker Notes:** The workshop ends, but the learning continues. Patterns become natural over time through consistent application. Focus on solving real problems with patterns, not applying patterns for their own sake. The goal is better software, not pattern collection.

---

# Thank You & Resources

## Contact Information

**Workshop Team:**
- **Lead Instructor:** [Instructor Name]  
- **Email:** architecture-training@company.com
- **Internal Wiki:** [Enterprise Architecture Portal]
- **Slack Channel:** #design-patterns-workshop

## Additional Resources

**Internal Resources:**
- **Architecture Guidelines:** [Internal Architecture Portal]
- **Code Examples Repository:** [Git Repository URL]
- **Pattern Library:** [Internal Pattern Catalog]
- **Best Practices Documentation:** [Internal Best Practices Wiki]

**External Learning:**
- **Design Patterns Catalog:** https://refactoring.guru/design-patterns
- **Enterprise Architecture Patterns:** https://martinfowler.com/eaaCatalog
- **Microservice Patterns:** https://microservices.io/patterns
- **Cloud Architecture Patterns:** [Azure/AWS/GCP Pattern Libraries]

## Follow-up Sessions

**Advanced Workshops Available:**
- **Enterprise Integration Patterns** (2-day workshop)
- **Microservice Architecture Mastery** (3-day intensive)
- **Cloud-Native Design Patterns** (2-day workshop)
- **Domain-Driven Design** (3-day workshop)

**Monthly Pattern Study Groups:**
- **Pattern Implementation Reviews** - First Friday of each month
- **Architecture Case Studies** - Third Wednesday of each month
- **New Pattern Exploration** - Last Tuesday of each month

**Certification Path:**
- **Enterprise Certified Solution Architect** - Includes pattern mastery requirements
- **Enterprise Architecture Certification** - Advanced pattern application

**Speaker Notes:** Learning doesn't end with this workshop. We've built a foundation for continuous improvement. The resources provided ensure ongoing learning and community support. Remember: great architects are made through consistent practice and continuous learning, not single training events.

---

# Questions & Discussion

## Open Forum

**Discussion Topics:**
- **Real-world challenges** with pattern implementation
- **Enterprise-specific use cases** and architectural decisions  
- **Tool recommendations** for pattern-based development
- **Team adoption strategies** for pattern-driven development
- **Integration with existing codebases** and legacy systems

**Next Workshop Feedback:**
- What patterns would you like to explore deeper?
- Which real-world scenarios need more coverage?
- How can we better support pattern adoption in your teams?

**Networking Opportunity:**
Connect with fellow architects and share experiences implementing patterns in your projects.

---

**🎯 Workshop Complete: From Pattern Novice to Architecture Expert**  
*Four days of intensive learning, practical application, and enterprise-ready solutions*

**🚀 Continue Your Architecture Journey**  
*Apply, practice, and master the art of software design patterns*

**📧 Stay Connected: architecture-training@company.com**