---
<!-- Version: 1.3.0-headlines-fix -->
type: slide
title: Software-Architektur - Strukturmuster
description: Strukturmuster in der Praxis - Adapter, Decorator, Facade, Composite, Proxy
tags: design-patterns, workshop,  architecture, training, day2, structural, adapter, decorator, facade, composite, proxy
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
## Strukturmuster in der Praxis
### Bring your own brain and use it!

Note:
* Begrüßung am zweiten Tag - kurze Auffrischung vom Vortag
* Fragen Sie nach Erkenntnissen und offenen Fragen vom vorherigen Workshop
* Betonen Sie den Fokus auf Strukturmuster für System-Integration
<!-- .element: class="notes" -->

</div>

---

# Strukturmuster Agenda

<div class="progress-indicator">
<div class="progress-step current">📍 Strukturmuster Übersicht</div>
<div class="progress-step pending">⏳ Erweiterte Patterns</div>
<div class="progress-step pending">⏳ Enterprise Examples</div>
<div class="progress-step pending">⏳ Praktische Anwendung</div>
</div>

* **Strukturmuster Übersicht** - Adapter, Decorator, Facade

* **Erweiterte Strukturmuster** - Composite, Proxy <!-- .element: class="fragment" data-fragment-index="1" -->
* **Enterprise Patterns** - Reale Anwendungsbeispiele <!-- .element: class="fragment" data-fragment-index="2" -->
* **Praktische Anwendung** - Code-Analyse und Refactoring <!-- .element: class="fragment" data-fragment-index="3" -->
* **Best Practices** - Enterprise spezifische Anwendungen <!-- .element: class="fragment" data-fragment-index="4" --> 

Note:
* Verwenden Sie Service Enhancement Pipeline als Motivation für Decorator Pattern
* Erklären Sie den Zusammenhang: Strukturmuster = System-Integration
* Betonen Sie die praktische Relevanz für Enterprise-Umgebungen
* Heben Sie hervor: "Heute lösen wir echte Integrationsprobleme"
* Verbinden Sie zu vorherigen Mustern: Creational → Structural → Behavioral
<!-- .element: class="notes" -->

---

# Wiederholung: Strukturmuster

#### Strukturmuster
**Intent**: Beschäftigen sich mit der Komposition von Klassen und Objekten für flexible und wartbare Architekturen.

**Problem**: Komplexe Systeme benötigen elegante Wege zur Objektkomposition und Interface-Harmonisierung.

**Solution**: Bewährte Patterns für Objektzusammensetzung, Interface-Anpassung und Komplexitätsreduktion.

* **Objekte kombinieren** - Funktionalität durch Zusammensetzung

<!-- .element: class="fragment" data-fragment-index="1" -->
* **Interface Harmonisierung** - Inkompatible Schnittstellen verbinden

<!-- .element: class="fragment" data-fragment-index="2" -->
* **Komplexität reduzieren** - Einfache Schnittstellen für komplexe Subsysteme

<!-- .element: class="fragment" data-fragment-index="3" -->
* **Flexibilität erhöhen** - Laufzeit-Komposition statt Vererbung

<!-- .element: class="fragment" data-fragment-index="4" -->
* **Performance optimieren** - Lazy Loading und Proxy-Mechanismen

<!-- .element: class="fragment" data-fragment-index="5" --> 

<!-- Speaker Notes: Strukturmuster sind das Herzstück moderner Enterprise-Architekturen. Sie ermöglichen es uns, komplexe Systeme modular und wartbar zu gestalten. Bei Enterprise nutzen wir diese Patterns täglich für API-Integration und Microservices. -->

---

# Adapter Pattern

## Was ist hier falsch?

#### Adapter Pattern Problem
**Situation**: Eine typische Enterprise-Herausforderung: Verschiedene Systeme mit inkompatiblen Interfaces müssen zusammenarbeiten.

**Was sehen Sie hier Problematisches?**

```java
public class BillingManager {
    
    public void processBilling(Customer customer, double amount) {
        // Code-Smell: Hardcodierte System-Auswahl
        if (customer.getRegion().equals("LEGACY")) {
            // Legacy System - völlig andere API
            LegacyBillingSystem legacy = new LegacyBillingSystem();
            
            // Problem: Manuelle Datenkonvertierung überall!
            int custId = Integer.parseInt(customer.getId());
            float sum = (float) amount;
            String billRef = legacy.generateBill(custId, sum, "EUR");
            
            // Weitere Konvertierung für Status-Check
            int status = legacy.checkBillStatus(billRef);
            customer.setBillingStatus(status == 1 ? "PENDING" : "PAID");
            
        } else {
            // Moderne API
            ModernBillingService modern = new ModernBillingService();
            Invoice invoice = modern.createInvoice(customer, amount);
            customer.setBillingStatus(invoice.getStatus().toString());
        }
    }
    
    // DUPLICATE CODE: Ähnliche Logik für andere Operations
    public void refundBilling(Customer customer, double amount) {
        if (customer.getRegion().equals("LEGACY")) {
            // Wieder die gleiche Konvertierungs-Logik...
        }
    }
}
```

---

## Identifizierte Code-Smells

* **Interface Mismatch**: Verschiedene APIs für gleiche Funktionalität <!-- .element: class="fragment" data-fragment-index="1" -->
* **Duplicate Conversion**: Manuelle Datenkonvertierung überall <!-- .element: class="fragment" data-fragment-index="2" -->
* **Tight Coupling**: Direkte Abhängigkeit zu Legacy-Systemen <!-- .element: class="fragment" data-fragment-index="3" -->
* **No Abstraction**: Keine einheitliche Schnittstelle <!-- .element: class="fragment" data-fragment-index="4" -->
* **Duplicate Code**: Ähnliche Konvertierungs-Logik in jeder Methode <!-- .element: class="fragment" data-fragment-index="5" -->
* **Type Conversion Hell**: int/float/String-Konvertierungen überall <!-- .element: class="fragment" data-fragment-index="6" --> 

---

## Adapter Pattern - Die Lösung

#### Adapter Pattern
**Intent**: Ermöglicht die Zusammenarbeit zwischen inkompatiblen Interfaces.

**Problem**: Verschiedene APIs oder Legacy-Systeme haben unterschiedliche Schnittstellen.

**Solution**: Ein Adapter übersetzt zwischen den verschiedenen Interfaces und macht sie kompatibel.

<div class="two-column">
<div>

## Use Cases
* **Legacy System Integration** - Alte APIs in moderne Architekturen <!-- .element: class="fragment" data-fragment-index="1" -->
* **Third-Party Integration** - Externe Services standardisiert nutzen <!-- .element: class="fragment" data-fragment-index="2" -->
* **Interface Vereinheitlichung** - Verschiedene APIs unter einem Interface <!-- .element: class="fragment" data-fragment-index="3" -->
* **Backward Compatibility** - Alte Schnittstellen weiter unterstützen <!-- .element: class="fragment" data-fragment-index="4" -->
* **Protocol Translation** - Verschiedene Protokolle harmonisieren <!-- .element: class="fragment" data-fragment-index="5" --> 

</div>
<div>

## Benefits
* **Separation of Concerns** - Business logic von Integration getrennt <!-- .element: class="fragment" data-fragment-index="6" -->
* **Reusability** - Legacy-Code weiter nutzen <!-- .element: class="fragment" data-fragment-index="7" -->
* **Maintainability** - Änderungen isoliert im Adapter <!-- .element: class="fragment" data-fragment-index="8" -->
* **Testability** - Adapter separat testbar <!-- .element: class="fragment" data-fragment-index="9" --> 

</div>
</div>

<!-- Speaker Notes: Das Adapter Pattern ist bei Enterprise besonders wichtig für die Integration von Legacy-Systemen. Wir haben viele bewährte Systeme, die weiterhin funktionieren, aber moderne APIs benötigen. -->

---

## Lösung

<h5>Enterprise Billing System Adapter</h5>

```typescript
// Modern Interface
interface ModernBillingInterface {
  createInvoice(customer: Customer, amount: number): Invoice;
  getInvoiceStatus(invoiceId: string): BillingStatus;
}

class LegacyBillingSystem {
  // Legacy API mit anderem Interface
  generateBill(custId: number, sum: number): LegacyBill {
    return new LegacyBill(custId, sum, new Date());
  }
  
  checkBillStatus(billNumber: string): number {
    // Returns numeric status codes
    return 1; // 1=pending, 2=paid, 3=overdue
  }
}

class BillingAdapter implements ModernBillingInterface {
  constructor(private legacySystem: LegacyBillingSystem) {}
  
  createInvoice(customer: Customer, amount: number): Invoice {
    const legacyBill = this.legacySystem.generateBill(customer.id, amount);
    
    return new Invoice({
      id: legacyBill.billNumber,
      customerId: customer.id,
      amount: amount,
      date: legacyBill.createdAt,
      status: 'pending'
    });
  }
  
  getInvoiceStatus(invoiceId: string): BillingStatus {
    const statusCode = this.legacySystem.checkBillStatus(invoiceId);
    
    // Convert numeric codes to modern enum
    switch(statusCode) {
      case 1: return BillingStatus.PENDING;
      case 2: return BillingStatus.PAID;
      case 3: return BillingStatus.OVERDUE;
      default: return BillingStatus.UNKNOWN;
    }
  }
}

// Verwendung
const legacySystem = new LegacyBillingSystem();
const modernBilling: ModernBillingInterface = new BillingAdapter(legacySystem);

const invoice = modernBilling.createInvoice(customer, 99.99);
const status = modernBilling.getInvoiceStatus(invoice.id);
```

<!-- .element: class="fragment" data-fragment-index="1" -->

</div>

<!-- Speaker Notes: Der Adapter fungiert als Übersetzer zwischen alter und neuer Welt. Notice how the adapter translates between different parameter types and return formats while maintaining the modern interface. -->

---

---

# Decorator Pattern

## Was ist hier falsch?

#### Decorator Pattern Problem
**Situation**: Cross-Cutting Concerns wie Logging, Security und Performance Monitoring führen zu einem Durcheinander wenn sie direkt in die Business-Logik eingebaut werden.

**Was sehen Sie hier Problematisches?**

```java
public class CustomerService {
    
    public Customer getCustomer(String customerId) {
        // Code-Smell: Mixed Concerns
        long startTime = System.currentTimeMillis();
        
        // Security Check - direkt in Business-Logik!
        if (!SecurityManager.hasPermission("READ_CUSTOMER")) {
            throw new SecurityException("No permission");
        }
        
        // Logging - überall die gleiche Logik
        System.out.println("Getting customer: " + customerId);
        
        try {
            // Eigentliche Business-Logik fast versteckt
            Customer customer = database.findCustomer(customerId);
            
            // Performance Logging - weitere Vermischung
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Customer lookup took: " + duration + "ms");
            
            // Caching Logic - noch mehr Code
            cache.put(customerId, customer);
            
            return customer;
            
        } catch (Exception e) {
            // Error Logging - duplicate code
            System.err.println("Error getting customer: " + e.getMessage());
            throw e;
        }
    }
    
    // DUPLICATE: Alle anderen Methoden haben die gleichen Concerns
    public void updateCustomer(Customer customer) {
        long startTime = System.currentTimeMillis();
        if (!SecurityManager.hasPermission("UPDATE_CUSTOMER")) {
            throw new SecurityException("No permission");
        }
        System.out.println("Updating customer: " + customer.getId());
        // ... weitere 20 Zeilen ähnlicher Code
    }
}
```

---

## Identifizierte Code-Smells

* **Mixed Concerns**: Business-Logik vermischt mit Cross-Cutting Concerns <!-- .element: class="fragment" data-fragment-index="1" -->
* **Duplicate Code**: Logging/Security/Performance Code in jeder Methode <!-- .element: class="fragment" data-fragment-index="2" -->
* **Hard to Maintain**: Änderungen am Logging betreffen alle Methoden <!-- .element: class="fragment" data-fragment-index="3" -->
* **Inflexible**: Concerns können nicht optional oder kombiniert werden <!-- .element: class="fragment" data-fragment-index="4" -->
* **Single Responsibility Violation**: Service macht viel mehr als Business-Logik <!-- .element: class="fragment" data-fragment-index="5" -->
* **Testing Nightmare**: Cross-Cutting Concerns erschweren Unit-Tests <!-- .element: class="fragment" data-fragment-index="6" --> 

---

## Decorator Pattern - Die Lösung

#### Decorator Pattern
**Intent**: Erweitert Objektfunktionalität dynamisch zur Laufzeit ohne die ursprüngliche Klasse zu ändern.

**Problem**: Objekte benötigen zusätzliche Funktionalität, aber Vererbung ist zu starr oder nicht praktikabel.

**Solution**: Wrapper-Objekte, die das ursprüngliche Interface implementieren und Funktionalität hinzufügen.

<div class="two-column">
<div>

## Use Cases
* **Behavior Extension** - Funktionalität ohne Vererbung hinzufügen <!-- .element: class="fragment" data-fragment-index="1" -->
* **Responsibility Chaining** - Multiple Decorator verschachteln <!-- .element: class="fragment" data-fragment-index="2" -->
* **Runtime Composition** - Flexibel zur Laufzeit konfigurieren <!-- .element: class="fragment" data-fragment-index="3" -->
* **Open-Closed Principle** - Erweiterung ohne Modifikation <!-- .element: class="fragment" data-fragment-index="4" -->
* **Cross-Cutting Concerns** - Logging, Security, Caching <!-- .element: class="fragment" data-fragment-index="5" --> 

</div>
<div>

## Benefits
* **Flexibility** - Laufzeit-Konfiguration <!-- .element: class="fragment" data-fragment-index="6" -->
* **Single Responsibility** - Jeder Decorator eine Aufgabe <!-- .element: class="fragment" data-fragment-index="7" -->
* **Composability** - Verschiedene Kombinationen möglich <!-- .element: class="fragment" data-fragment-index="8" -->
* **Maintainability** - Concerns getrennt <!-- .element: class="fragment" data-fragment-index="9" --> 

</div>
</div>

<!-- Speaker Notes: Das Decorator Pattern ist essentiell für Enterprise-Services. Bei Enterprise verwenden wir es für Logging, Security, Performance Monitoring und Caching. Es ermöglicht uns, Cross-Cutting Concerns sauber zu trennen und flexibel zu kombinieren. -->

---

## Lösung

<h5>Enterprise Service Decoration für Cross-Cutting Concerns</h5>

```typescript
interface EnterpriseService {  processRequest(request: ServiceRequest): ServiceResponse;}
class BasicCustomerService implements EnterpriseService {  processRequest(request: ServiceRequest): ServiceResponse {    // Core business logic    return new ServiceResponse({      success: true,      data: `Processed customer request: ${request.type}`,      timestamp: new Date()    });  }}
// Decorator Base Class
abstract class ServiceDecorator implements EnterpriseService {  constructor(protected service: EnterpriseService) {}  
  processRequest(request: ServiceRequest): ServiceResponse {    return this.service.processRequest(request);  }}
// Logging Decorator
class LoggingDecorator extends ServiceDecorator {  processRequest(request: ServiceRequest): ServiceResponse {    console.log(`[LOG] Processing request: ${request.type} at ${new Date()}`);    
    const response = super.processRequest(request);    
    console.log(`[LOG] Request completed: ${response.success ? 'SUCCESS' : 'FAILED'}`);    return response;  }}
// Security Decorator
class SecurityDecorator extends ServiceDecorator {  processRequest(request: ServiceRequest): ServiceResponse {    // Validate authentication and authorization    if (!request.token || !this.validateToken(request.token)) {      return new ServiceResponse({        success: false,        error: 'Unauthorized access',        timestamp: new Date()      });    }    
    return super.processRequest(request);  }  
  private validateToken(token: string): boolean {    // Enterprise token validation logic    return token.startsWith('enterprise_') && token.length > 10;  }}```

<!-- Speaker Notes: Notice how each decorator adds a single responsibility. We can combine them in any order and create different service configurations for different environments. -->

---

# Decorator Pattern - Flexible Composition

<h5>Flexible Service Composition</h5>

```typescript
// Performance Monitoring Decorator
class PerformanceDecorator extends ServiceDecorator {  processRequest(request: ServiceRequest): ServiceResponse {    const startTime = performance.now();    
    const response = super.processRequest(request);    
    const executionTime = performance.now() - startTime;    console.log(`[PERF] Request executed in ${executionTime.toFixed(2)}ms`);    
    // Add performance metrics to response    response.metadata = { executionTime };    return response;  }}
// Usage - Flexible composition
let customerService: EnterpriseService = new BasicCustomerService();
// Add logging
customerService = new LoggingDecorator(customerService);
// Add security
customerService = new SecurityDecorator(customerService);
// Add performance monitoring
customerService = new PerformanceDecorator(customerService);
// Process request with all decorators
const response = customerService.processRequest({  type: 'UPDATE_CUSTOMER_DATA',  token: 'enterprise_abc123456789',  customerId: '12345'});```

<div class="highlight-box success">
**Execution Flow**: Basic Service → Performance → Security → Logging → Core Business Logic
</div>

<!-- Speaker Notes: This demonstrates the power of the decorator pattern. We can create different service configurations by mixing and matching decorators. Each decorator wraps the previous one, creating a chain of responsibility. -->

---

---

# Facade Pattern

## Was ist hier falsch?

#### Facade Pattern Problem
**Situation**: Clients müssen mit vielen verschiedenen Services und komplexen APIs interagieren, was zu unübersichtlichem und fehleranfälligem Code führt.

**Was sehen Sie hier Problematisches?**

```java
public class CustomerController {
    
    public ResponseEntity<String> onboardCustomer(CustomerRequest request) {
        // Code-Smell: Complex Client-Side Orchestration
        
        try {
            // 1. Customer Service - komplexe Parameter
            CustomerService customerService = new CustomerService();
            Customer customer = customerService.validateAndCreate(
                request.getName(), 
                request.getEmail(), 
                request.getAddress(), 
                request.getPhone(),
                "ACTIVE", 
                new Date()
            );
            
            // 2. Billing Service - andere API-Signatur
            BillingService billingService = new BillingService();
            Account account = billingService.createAccount(
                customer.getId(), 
                request.getPaymentMethod(),
                request.getBillingAddress() != null ? request.getBillingAddress() : request.getAddress()
            );
            
            // 3. Notification Service - wieder andere Parameter
            NotificationService notificationService = new NotificationService();
            notificationService.sendWelcomeEmail(
                customer.getEmail(),
                customer.getName(),
                account.getAccountNumber()
            );
            
            // 4. Audit Service - komplexe Event-Erstellung
            AuditService auditService = new AuditService();
            auditService.logEvent(
                "CUSTOMER_ONBOARDED",
                customer.getId(),
                "New customer onboarded: " + customer.getName(),
                new AuditMetadata(getCurrentUser(), new Date(), "ONBOARDING")
            );
            
            return ResponseEntity.ok("Customer onboarded successfully");
            
        } catch (CustomerValidationException e) {
            // Fehlerbehandlung für jeden Service separat
            return ResponseEntity.badRequest().body("Customer validation failed: " + e.getMessage());
        } catch (BillingException e) {
            // Rollback Customer? Partial failure handling?
            return ResponseEntity.status(500).body("Billing setup failed: " + e.getMessage());
        } catch (NotificationException e) {
            // Customer und Billing OK, aber Notification failed - was nun?
            return ResponseEntity.status(207).body("Customer created but notification failed");
        }
        // ... weitere 20 Zeilen Exception Handling
    }
}
```

---

## Identifizierte Code-Smells

* **Complex Orchestration**: Client muss alle Services koordinieren <!-- .element: class="fragment" data-fragment-index="1" -->
* **Tight Coupling**: Client kennt alle Subsystem-Details <!-- .element: class="fragment" data-fragment-index="2" -->
* **Error Handling Chaos**: Rollback und Partial Failures komplex <!-- .element: class="fragment" data-fragment-index="3" -->
* **Duplicate Logic**: Orchestration Code in jedem Client wiederholt <!-- .element: class="fragment" data-fragment-index="4" -->
* **Transaction Management**: Kein koordiniertes Transaction-Handling <!-- .element: class="fragment" data-fragment-index="5" -->
* **Single Point of Failure**: Wenn ein Service failiert, bricht alles zusammen <!-- .element: class="fragment" data-fragment-index="6" --> 

---

## Facade Pattern - Die Lösung

#### Facade Pattern
**Intent**: Bietet eine vereinfachte Schnittstelle zu komplexen Subsystemen.

**Problem**: Clients müssen mit vielen verschiedenen Klassen und komplexen APIs interagieren.

**Solution**: Eine Facade-Klasse kapselt die Komplexität und bietet einfache, hochlevelige Operationen.

<div class="two-column">
<div>

## Use Cases
* **Complexity Hiding** - Einfache API für komplexe Operationen <!-- .element: class="fragment" data-fragment-index="1" -->
* **Subsystem Coordination** - Multiple Services orchestrieren <!-- .element: class="fragment" data-fragment-index="2" -->
* **Client Decoupling** - Clients vom Subsystem entkoppeln <!-- .element: class="fragment" data-fragment-index="3" -->
* **API Standardization** - Einheitliche Schnittstellen schaffen <!-- .element: class="fragment" data-fragment-index="4" -->
* **Integration Layer** - Microservices zusammenfassen <!-- .element: class="fragment" data-fragment-index="5" --> 

</div>
<div>

## Benefits
* **Simplicity** - Einfache Client-Schnittstelle <!-- .element: class="fragment" data-fragment-index="6" -->
* **Decoupling** - Clients unabhängig von Implementierung <!-- .element: class="fragment" data-fragment-index="7" -->
* **Flexibility** - Subsystem-Änderungen transparent <!-- .element: class="fragment" data-fragment-index="8" -->
* **Reusability** - Hochlevelige Operationen wiederverwendbar <!-- .element: class="fragment" data-fragment-index="9" --> 

</div>
</div>

<!-- Speaker Notes: Das Facade Pattern ist bei Enterprise unverzichtbar für die Vereinfachung komplexer Geschäftsprozesse. Customer Onboarding involviert normalerweise 5-6 verschiedene Services. Die Facade versteckt diese Komplexität und bietet eine einfache, fehlerresistente API. -->

---

## Lösung

<h5>Enterprise Customer Management Facade</h5>

```typescript
class EnterpriseCustomerFacade {  constructor(    private customerService: CustomerService,    private billingService: BillingService,    private contractService: ContractService,    private notificationService: NotificationService,    private auditService: AuditService  ) {}  
  // Vereinfachte API für komplexes Customer Onboarding
  async onboardNewCustomer(customerData: CustomerData): Promise<OnboardingResult> {    try {      // 1. Create customer record      const customer = await this.customerService.create({        name: customerData.name,        email: customerData.email,        phone: customerData.phone,        address: customerData.address      });      
      // 2. Set up billing account      const billingAccount = await this.billingService.createAccount({        customerId: customer.id,        paymentMethod: customerData.paymentMethod,        billingAddress: customerData.billingAddress      });      
      // 3. Create initial contract      const contract = await this.contractService.create({        customerId: customer.id,        planType: customerData.selectedPlan,        startDate: new Date(),        billingAccountId: billingAccount.id      });      
      // 4. Send welcome notification      await this.notificationService.sendWelcomeEmail({        customerEmail: customer.email,        customerName: customer.name,        contractDetails: contract      });      
      // 5. Log audit trail      await this.auditService.logCustomerAction({        action: 'CUSTOMER_ONBOARDED',        customerId: customer.id,        timestamp: new Date(),        details: { contractId: contract.id, plan: customerData.selectedPlan }      });      
      return new OnboardingResult({        success: true,        customerId: customer.id,        contractId: contract.id,        message: 'Customer successfully onboarded'      });      
    } catch (error) {      // Comprehensive error handling      await this.auditService.logError({        action: 'CUSTOMER_ONBOARDING_FAILED',        error: error.message,        customerData: customerData.email // Safe logging      });      
      return new OnboardingResult({        success: false,        error: 'Onboarding process failed',        details: error.message      });    }  }}
// Simple usage for complex operations
const customerFacade = new EnterpriseCustomerFacade(  customerService, billingService, contractService,   notificationService, auditService);
// Complex onboarding with single method call
const result = await customerFacade.onboardNewCustomer({  name: 'Max Mustermann',  email: 'max@example.com',  phone: '+49 123 456789',  selectedPlan: 'BUSINESS_PRO',  paymentMethod: 'SEPA'});```

<!-- Speaker Notes: Notice how the facade coordinates multiple services and handles all error scenarios. What would be 20+ lines of client code is now a single method call. -->

---

# Composite Pattern

#### Composite Pattern
**Intent**: Behandelt einzelne Objekte und Objektgruppen einheitlich in Baumstrukturen.

**Problem**: Hierarchische Strukturen sollen einheitlich behandelt werden, unabhängig davon ob es Einzelobjekte oder Gruppen sind.

**Solution**: Gemeinsames Interface für Leaf- und Composite-Objekte mit rekursiven Operationen.

<div class="two-column">
<div>

## Use Cases
* **Hierarchical Structures** - Baumstrukturen elegant handhaben <!-- .element: class="fragment" data-fragment-index="1" -->
* **Uniform Interface** - Gleiche Operationen für Blätter und Äste <!-- .element: class="fragment" data-fragment-index="2" -->
* **Recursive Operations** - Operationen propagieren automatisch <!-- .element: class="fragment" data-fragment-index="3" -->
* **Dynamic Composition** - Strukturen zur Laufzeit ändern <!-- .element: class="fragment" data-fragment-index="4" -->
* **Organizational Modeling** - Unternehmen, Teams, Projekte <!-- .element: class="fragment" data-fragment-index="5" --> 

</div>
<div>

## Benefits
* **Simplicity** - Einheitliche Behandlung <!-- .element: class="fragment" data-fragment-index="6" -->
* **Flexibility** - Dynamische Strukturänderungen <!-- .element: class="fragment" data-fragment-index="7" -->
* **Scalability** - Beliebig tiefe Hierarchien <!-- .element: class="fragment" data-fragment-index="8" -->
* **Reusability** - Wiederverwendbare Tree-Operationen <!-- .element: class="fragment" data-fragment-index="9" --> 

</div>
</div>

<!-- Speaker Notes: Das Composite Pattern ist perfekt für enterprise organizational structures. Wir können einheitlich auf Mitarbeiter, Teams und ganze Divisionen zugreifen. Egal ob wir das Budget eines einzelnen Mitarbeiters oder der gesamten Technologie-Sparte berechnen - das Interface bleibt gleich. -->

---

# Composite Pattern - Organization Structure

<h5>Enterprise Organizational Structure mit Composite Pattern</h5>

```typescript
interface OrganizationalUnit {  getName(): string;  getEmployeeCount(): number;  getBudget(): number;  addUnit(unit: OrganizationalUnit): void;  removeUnit(unit: OrganizationalUnit): void;  generateReport(): OrganizationalReport;}
class Employee implements OrganizationalUnit {  constructor(    private name: string,    private salary: number,    private position: string  ) {}  
  getName(): string {    return this.name;  }  
  getEmployeeCount(): number {    return 1; // An employee counts as 1  }  
  getBudget(): number {    return this.salary;  }  
  addUnit(unit: OrganizationalUnit): void {    throw new Error('Cannot add units to an employee');  }  
  removeUnit(unit: OrganizationalUnit): void {    throw new Error('Cannot remove units from an employee');  }  
  generateReport(): OrganizationalReport {    return {      name: this.name,      type: 'EMPLOYEE',      employeeCount: 1,      budget: this.salary,      position: this.position,      children: []    };  }}
class Department implements OrganizationalUnit {  private units: OrganizationalUnit[] = [];  
  constructor(    private name: string,    private operationalBudget: number = 0  ) {}  
  getName(): string {    return this.name;  }  
  getEmployeeCount(): number {    return this.units.reduce((total, unit) => {      return total + unit.getEmployeeCount();    }, 0);  }  
  getBudget(): number {    const personnelBudget = this.units.reduce((total, unit) => {      return total + unit.getBudget();    }, 0);    
    return personnelBudget + this.operationalBudget;  }  
  addUnit(unit: OrganizationalUnit): void {    this.units.push(unit);  }  
  removeUnit(unit: OrganizationalUnit): void {    const index = this.units.indexOf(unit);    if (index > -1) {      this.units.splice(index, 1);    }  }  
  generateReport(): OrganizationalReport {    const childReports = this.units.map(unit => unit.generateReport());    
    return {      name: this.name,      type: 'DEPARTMENT',      employeeCount: this.getEmployeeCount(),      budget: this.getBudget(),      operationalBudget: this.operationalBudget,      children: childReports    };  }}```

<!-- Speaker Notes: Notice how both Employee and Department implement the same interface. This allows us to treat individual employees and entire departments uniformly. -->

---

# Composite Pattern - Usage Example

<h5>Building Enterprise Organization Hierarchy</h5>

```typescript
// Enterprise organizational structure example
const = new Department('Deutsche Enterprise AG', 1000000);
// Technology Division
const technologyDiv = new Department('Technology', 500000);const cloudTeam = new Department('Cloud Services', 50000);const securityTeam = new Department('IT Security', 75000);
// Add employees to teams
cloudTeam.addUnit(new Employee('Anna Schmidt', 85000, 'Cloud Architect'));cloudTeam.addUnit(new Employee('Peter Wagner', 75000, 'DevOps Engineer'));cloudTeam.addUnit(new Employee('Lisa Chen', 80000, 'Cloud Developer'));
securityTeam.addUnit(new Employee('Michael Bauer', 90000, 'Security Architect'));securityTeam.addUnit(new Employee('Sarah Müller', 82000, 'Security Engineer'));
// Build hierarchy
technologyDiv.addUnit(cloudTeam);technologyDiv.addUnit(securityTeam);company.addUnit(technologyDiv);
// Uniform operations on the entire structure
console.log(`Total employees: ${company.getEmployeeCount()}`);console.log(`Total budget: €${company.getBudget().toLocaleString()}`);
// Generate comprehensive report
const report = company.generateReport();console.log(JSON.stringify(report, null, 2));
// Dynamic restructuring
const newInnovationLab = new Department('Innovation Lab', 25000);newInnovationLab.addUnit(new Employee('Dr. Frank Weber', 95000, 'Innovation Lead'));
technologyDiv.addUnit(newInnovationLab);```

<div class="highlight-box accent">
**Key Benefit**: Same operations work on individuals, teams, divisions, or the entire company structure!
</div>

<!-- Speaker Notes: This demonstrates the power of uniform interfaces. We can calculate budgets, employee counts, and generate reports at any level of the organization using the same methods. -->

---

# Proxy Pattern

#### Proxy Pattern
**Intent**: Fungiert als Stellvertreter und kontrolliert Zugriff auf andere Objekte.

**Problem**: Direkter Zugriff auf Objekte ist teuer, unsicher oder unpraktisch.

**Solution**: Ein Proxy-Objekt kontrolliert und optimiert den Zugriff auf das eigentliche Objekt.

<div class="two-column">
<div>

## Use Cases
* **Access Control** - Sicherheitsproxy für geschützte Ressourcen <!-- .element: class="fragment" data-fragment-index="1" -->
* **Lazy Loading** - Objekte erst bei Bedarf laden <!-- .element: class="fragment" data-fragment-index="2" -->
* **Caching** - Wiederholte Anfragen zwischenspeichern <!-- .element: class="fragment" data-fragment-index="3" -->
* **Remote Access** - Entfernte Objekte lokal repräsentieren <!-- .element: class="fragment" data-fragment-index="4" -->
* **Resource Management** - Teure Operationen optimieren <!-- .element: class="fragment" data-fragment-index="5" --> 

</div>
<div>

## Benefits
* **Performance** - Caching und Lazy Loading <!-- .element: class="fragment" data-fragment-index="6" -->
* **Security** - Zugriffskontrolle <!-- .element: class="fragment" data-fragment-index="7" -->
* **Transparency** - Client merkt Proxy nicht <!-- .element: class="fragment" data-fragment-index="8" -->
* **Flexibility** - Verschiedene Proxy-Typen <!-- .element: class="fragment" data-fragment-index="9" --> 

</div>
</div>

<!-- Speaker Notes: Das Proxy Pattern ist bei Enterprise kritisch für Performance und Security. Wir nutzen es für Datenschutz (Customer Data Protection), Caching teurer Analytics-Queries und für Remote Service Access. Der Proxy fungiert als intelligenter Gateway zwischen Client und Service. -->

---

# Proxy Pattern - Data Access with Security

<h5>Enterprise Data Access Proxy mit Caching und Security</h5>

```typescript
interface EnterpriseDataService {  getCustomerData(customerId: string): Promise<CustomerData>;  updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean>;  getUsageStatistics(customerId: string, period: DateRange): Promise<UsageStats>;}
class RealEnterpriseDataService implements EnterpriseDataService {  async getCustomerData(customerId: string): Promise<CustomerData> {    // Simulate expensive database call    console.log(`[DB] Fetching customer data for ${customerId}`);    await this.delay(500); // Simulate network delay    
    return {      id: customerId,      name: 'Max Mustermann',      email: 'max@company.com',      contract: 'BUSINESS_PRO',      lastLogin: new Date(),      dataUsage: 15.5    };  }  
  async updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean> {    console.log(`[DB] Updating customer data for ${customerId}`);    await this.delay(300);    return true;  }  
  async getUsageStatistics(customerId: string, period: DateRange): Promise<UsageStats> {    console.log(`[DB] Fetching usage statistics for ${customerId}`);    await this.delay(800); // Heavy analytics query    
    return {      customerId,      period,      dataUsed: 45.2,      callMinutes: 890,      smsCount: 156,      costs: 89.95    };  }  
  private delay(ms: number): Promise<void> {    return new Promise(resolve => setTimeout(resolve, ms));  }}```

<!-- Speaker Notes: This is the real service that does the actual work. Notice the simulated delays that represent real-world database and network latency. -->

---

# Proxy Pattern - Implementation

<h5>Security and Caching Proxy Implementation</h5>

```typescript
class EnterpriseDataServiceProxy implements EnterpriseDataService {  private realService: RealEnterpriseDataService;  private cache = new Map<string, { data: any; timestamp: number }>();  private readonly CACHE_TTL = 5 * 60 * 1000; // 5 minutes  
  constructor(private authService: AuthenticationService) {    this.realService = new RealEnterpriseDataService();  }  
  async getCustomerData(customerId: string): Promise<CustomerData> {    // Security check    if (!await this.checkAccess(customerId)) {      throw new Error('Unauthorized access to customer data');    }    
    // Check cache first    const cacheKey = `customer_${customerId}`;    const cached = this.getCachedData(cacheKey);    if (cached) {      console.log(`[CACHE] Returning cached customer data for ${customerId}`);      return cached;    }    
    // Fetch from real service    const data = await this.realService.getCustomerData(customerId);    this.setCachedData(cacheKey, data);    
    return data;  }  
  async updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean> {    // Security check    if (!await this.checkAccess(customerId)) {      throw new Error('Unauthorized access to customer data');    }    
    // Update via real service    const result = await this.realService.updateCustomerData(customerId, data);    
    // Invalidate cache    this.invalidateCustomerCache(customerId);    
    return result;  }  
  private async checkAccess(customerId: string): Promise<boolean> {    // Simulate authentication and authorization check    const currentUser = await this.authService.getCurrentUser();    
    // Check if user has access to this customer data    if (currentUser.role === 'ADMIN') {      return true;    }    
    if (currentUser.role === 'SUPPORT' && currentUser.customerId === customerId) {      return true;    }    
    if (currentUser.customerId === customerId) {      return true;    }    
    console.log(`[SECURITY] Access denied for user ${currentUser.id} to customer ${customerId}`);    return false;  }  
  private getCachedData(key: string): any {    const cached = this.cache.get(key);    if (cached && (Date.now() - cached.timestamp < this.CACHE_TTL)) {      return cached.data;    }    return null;  }  
  private setCachedData(key: string, data: any): void {    this.cache.set(key, {      data: data,      timestamp: Date.now()    });  }  
  private invalidateCustomerCache(customerId: string): void {    const keysToDelete: string[] = [];    for (const key of this.cache.keys()) {      if (key.includes(customerId)) {        keysToDelete.push(key);      }    }    keysToDelete.forEach(key => this.cache.delete(key));  }}
// Usage
const authService = new AuthenticationService();const dataService: EnterpriseDataService = new EnterpriseDataServiceProxy(authService);
// First call - hits database
const customer1 = await dataService.getCustomerData('123456');
// Second call - returns from cache
const customer2 = await dataService.getCustomerData('123456');
// Unauthorized call - throws security exception
try {  await dataService.getCustomerData('unauthorized_customer');} catch (error) {  console.log('Security proxy blocked unauthorized access');}```

<!-- Speaker Notes: Notice how the proxy adds security and caching without the client knowing. The client uses the same interface but gets enhanced functionality. -->

---

---

# Structural Patterns Best Practices

<div class="two-column">
<div>

## Enterprise Specific Guidelines:

* **Security First** - Alle Patterns mit Security Decorators erweitern <!-- .element: class="fragment" data-fragment-index="1" -->
* **Performance Aware** - Proxy Pattern für teure Operations <!-- .element: class="fragment" data-fragment-index="2" -->
* **Legacy Integration** - Adapter Pattern für Bestandssysteme <!-- .element: class="fragment" data-fragment-index="3" -->
* **API Standardization** - Facade Pattern für einheitliche APIs <!-- .element: class="fragment" data-fragment-index="4" -->
* **Monitoring Integration** - Decorator für comprehensive logging <!-- .element: class="fragment" data-fragment-index="5" --> 

</div>
<div>

## Häufige Fallstricke:

* **Over-Engineering** - Nicht jede Klasse braucht einen Proxy <!-- .element: class="fragment" data-fragment-index="6" -->
* **Cache Invalidation** - Proxy Caches müssen intelligent invalidiert werden <!-- .element: class="fragment" data-fragment-index="7" -->
* **Deep Nesting** - Decorator chains nicht zu tief verschachteln <!-- .element: class="fragment" data-fragment-index="8" -->
* **Interface Bloat** - Facades nicht mit zu vielen Methoden überladen <!-- .element: class="fragment" data-fragment-index="9" --> 

</div>
</div>

<div class="highlight-box warning">
**Golden Rule**: Patterns sollen Komplexität reduzieren, nicht erhöhen. Wenn ein Pattern mehr Probleme schafft als löst, überdenken Sie den Ansatz.
</div>

<!-- Speaker Notes: Diese Best Practices basieren auf realen Enterprise Projekten. Teilen Sie konkrete Beispiele wo möglich und warnen Sie vor den typischen Fehlern, die wir selbst gemacht haben. -->

---

# Strukturmuster Zusammenfassung

<div class="progress-indicator">
<div class="progress-step completed">✅ Adapter Pattern - Legacy Integration</div>
<div class="progress-step completed">✅ Decorator Pattern - Flexible Cross-Cutting Concerns</div>
<div class="progress-step completed">✅ Facade Pattern - Komplexitätsreduktion</div>
<div class="progress-step completed">✅ Composite Pattern - Hierarchische Strukturen</div>
<div class="progress-step completed">✅ Proxy Pattern - Intelligente Zugriffskontrolle</div>
</div>

<div class="two-column">
<div>

## Was haben wir gelernt:

* **Adapter Pattern** - Legacy Integration mit modernen APIs <!-- .element: class="fragment" data-fragment-index="1" -->
* **Decorator Pattern** - Flexible Cross-Cutting Concerns <!-- .element: class="fragment" data-fragment-index="2" -->
* **Facade Pattern** - Vereinfachung komplexer Subsysteme <!-- .element: class="fragment" data-fragment-index="3" -->
* **Composite Pattern** - Hierarchische Strukturen elegant handhaben <!-- .element: class="fragment" data-fragment-index="4" -->
* **Proxy Pattern** - Intelligente Zugriffskontrolle und Caching <!-- .element: class="fragment" data-fragment-index="5" --> 

</div>
<div>

## Nächste Schritte - Verhaltensmuster:

* **Behavioral Patterns** - Observer, Strategy, Command <!-- .element: class="fragment" data-fragment-index="6" -->
* **Enterprise Integration** - Message Queues und Event-Driven Architecture <!-- .element: class="fragment" data-fragment-index="7" -->
* **Microservices Patterns** - Service Mesh und API Gateway <!-- .element: class="fragment" data-fragment-index="8" -->
* **Advanced Topics** - CQRS, Event Sourcing <!-- .element: class="fragment" data-fragment-index="9" --> 

</div>
</div>

<!-- Speaker Notes: Fassen Sie die wichtigsten Erkenntnisse zusammen. Betonen Sie, wie diese Patterns bei Enterprise täglich verwendet werden. Machen Sie die Teilnehmer neugierig auf Behavioral Patterns - diese sind noch spannender und näher an der Geschäftslogik. -->

---

# Q&A und Diskussion

<div class="interactive-question">

## Fragen zu den Structural Patterns?

</div>

<div class="two-column">
<div>

### Diskussionspunkte:
* Welche Patterns verwenden Sie bereits? <!-- .element: class="fragment" data-fragment-index="1" -->
* Wo sehen Sie Anwendungsmöglichkeiten in Ihren Projekten? <!-- .element: class="fragment" data-fragment-index="2" -->
* Welche Herausforderungen haben Sie bei der Implementation? <!-- .element: class="fragment" data-fragment-index="3" -->
* Wie kombinieren Sie verschiedene Patterns? <!-- .element: class="fragment" data-fragment-index="4" --> 

</div>
<div>

### Kontakt für weitere Fragen:
* **Email:** architecture-training@company.com <!-- .element: class="fragment" data-fragment-index="5" -->
* **Teams:** Enterprise Architecture Community <!-- .element: class="fragment" data-fragment-index="6" -->
* **Wiki:** Internal Pattern Documentation <!-- .element: class="fragment" data-fragment-index="7" -->
* **GitHub:** Code Examples Repository <!-- .element: class="fragment" data-fragment-index="8" --> 

</div>
</div>

<div class="workshop-header">

**Vielen Dank für Ihre aktive Teilnahme!**
### Fortsetzung mit Verhaltensmuster!

</div>

<!-- Speaker Notes: Ermutigen Sie zur offenen Diskussion. Viele Teilnehmer haben bereits unbewusst diese Patterns verwendet. Helfen Sie dabei, die Verbindung zwischen Theorie und ihrer täglichen Arbeit herzustellen. Sammeln Sie Feedback für Verbesserungen des Trainings. -->