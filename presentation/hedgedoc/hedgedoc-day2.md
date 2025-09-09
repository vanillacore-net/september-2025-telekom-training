---
title: Software-Architektur - Tag 2
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
  text-align: center !important;
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

/* Code blocks sizing - Full Width Optimized */
.reveal pre {
  font-size: 1.2em !important; /* Increased by 20% for better readability */
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

.reveal pre code {
  font-size: 1.2em !important; /* Increased by 20% for better readability */
  line-height: 1.3 !important;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace !important;
  color: #e2e8f0 !important;
  background: transparent !important;
  padding: 0 !important;
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

<script>
// Hide speaker notes that start with "Note:"
document.addEventListener('DOMContentLoaded', function() {
  // Function to hide speaker notes
  function hideSpeakerNotes() {
    const slides = document.querySelectorAll('.reveal .slides section');
    
    slides.forEach(slide => {
      const paragraphs = slide.querySelectorAll('p');
      
      paragraphs.forEach((p, index) => {
        // Check if paragraph starts with "Note:"
        if (p.textContent.trim().startsWith('Note:')) {
          // Hide this paragraph
          p.style.display = 'none';
          
          // Also hide the following list if it exists
          const nextElement = p.nextElementSibling;
          if (nextElement && (nextElement.tagName === 'UL' || nextElement.tagName === 'OL')) {
            nextElement.style.display = 'none';
          }
        }
      });
      
      // Also hide any elements with "notes" class
      const notesElements = slide.querySelectorAll('[class*="notes"], .element.notes');
      notesElements.forEach(el => {
        el.style.display = 'none';
      });
    });
  }
  
  // Hide notes immediately
  hideSpeakerNotes();
  
  // Also hide notes after reveal.js initializes
  setTimeout(hideSpeakerNotes, 1000);
  
  // Re-hide notes when slide changes
  if (typeof Reveal !== 'undefined') {
    Reveal.on('slidechanged', hideSpeakerNotes);
  }
});
</script>

<div class="workshop-header title-slide">

<div class="vanilla-logo">
  <img src="/images/VanillaCore_Vertical.png" alt="VanillaCore Logo">
</div>

# Software-Architektur
## Tag 2: Strukturmuster in der Praxis
### Bring your own brain and use it!

Note:
- Begrüßung am zweiten Tag - kurze Auffrischung vom Vortag
- Fragen Sie nach Erkenntnissen und offenen Fragen von Tag 1
- Betonen Sie den Fokus auf Strukturmuster für System-Integration
<!-- .element: class="notes" -->

</div>

---

# Tag 2 Agenda

<div class="progress-indicator">
<div class="progress-step current">📍 Strukturmuster Übersicht</div>
<div class="progress-step pending">⏳ Erweiterte Patterns</div>
<div class="progress-step pending">⏳ Enterprise Examples</div>
<div class="progress-step pending">⏳ Hands-on Übungen</div>
</div>

<div class="fragment">- **Strukturmuster Übersicht** - Adapter, Decorator, Facade</div>
<div class="fragment">- **Erweiterte Strukturmuster** - Composite, Proxy</div>
<div class="fragment">- **Enterprise Patterns** - Reale Anwendungsbeispiele</div>
<div class="fragment">- **Hands-on Übungen** - Praktische Implementierung</div>
<div class="fragment">- **Best Practices** - Enterprise spezifische Anwendungen</div> 

Note:
- Verwenden Sie Exercise 1 "Service Enhancement Pipeline" aus day2-exercises.md als Motivation
- Erklären Sie den Zusammenhang: Strukturmuster = System-Integration
- Betonen Sie die praktische Relevanz für Telekom-Umgebungen
- Heben Sie hervor: "Heute lösen wir echte Integrationsprobleme"
- Verbinden Sie zu Tag 1: Creational → Structural → Behavioral (Tag 3/4)
<!-- .element: class="notes" -->

---

# Wiederholung: Strukturmuster

<div class="pattern-definition">

#### Strukturmuster
**Intent**: Beschäftigen sich mit der Komposition von Klassen und Objekten für flexible und wartbare Architekturen.

**Problem**: Komplexe Systeme benötigen elegante Wege zur Objektkomposition und Interface-Harmonisierung.

**Solution**: Bewährte Patterns für Objektzusammensetzung, Interface-Anpassung und Komplexitätsreduktion.

</div>

<div class="fragment">- **Objekte kombinieren** - Funktionalität durch Zusammensetzung</div>
<div class="fragment">- **Interface Harmonisierung** - Inkompatible Schnittstellen verbinden</div>
<div class="fragment">- **Komplexität reduzieren** - Einfache Schnittstellen für komplexe Subsysteme</div>
<div class="fragment">- **Flexibilität erhöhen** - Laufzeit-Komposition statt Vererbung</div>
<div class="fragment">- **Performance optimieren** - Lazy Loading und Proxy-Mechanismen</div> 

<!-- Speaker Notes: Strukturmuster sind das Herzstück moderner Enterprise-Architekturen. Sie ermöglichen es uns, komplexe Systeme modular und wartbar zu gestalten. Bei Enterprise nutzen wir diese Patterns täglich für API-Integration und Microservices. -->

---

# Adapter Pattern

## Was ist hier falsch?

<div class="problem-highlight">

#### Adapter Pattern Problem
**Situation**: Eine typische Enterprise-Herausforderung: Verschiedene Systeme mit inkompatiblen Interfaces müssen zusammenarbeiten.

**Was sehen Sie hier Problematisches?**

</div>

<div class="code-example">

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

</div>

---

## Identifizierte Code-Smells

- **Interface Mismatch**: Verschiedene APIs für gleiche Funktionalität 
- **Duplicate Conversion**: Manuelle Datenkonvertierung überall 
- **Tight Coupling**: Direkte Abhängigkeit zu Legacy-Systemen 
- **No Abstraction**: Keine einheitliche Schnittstelle 
- **Duplicate Code**: Ähnliche Konvertierungs-Logik in jeder Methode 
- **Type Conversion Hell**: int/float/String-Konvertierungen überall 

---

## Adapter Pattern - Die Lösung

<div class="pattern-definition">

#### Adapter Pattern
**Intent**: Ermöglicht die Zusammenarbeit zwischen inkompatiblen Interfaces.

**Problem**: Verschiedene APIs oder Legacy-Systeme haben unterschiedliche Schnittstellen.

**Solution**: Ein Adapter übersetzt zwischen den verschiedenen Interfaces und macht sie kompatibel.

</div>

<div class="two-column">
<div>

## Use Cases
<div class="fragment">- **Legacy System Integration** - Alte APIs in moderne Architekturen</div>
<div class="fragment">- **Third-Party Integration** - Externe Services standardisiert nutzen</div>
<div class="fragment">- **Interface Vereinheitlichung** - Verschiedene APIs unter einem Interface</div>
<div class="fragment">- **Backward Compatibility** - Alte Schnittstellen weiter unterstützen</div>
<div class="fragment">- **Protocol Translation** - Verschiedene Protokolle harmonisieren</div> 

</div>
<div>

## Benefits
<div class="fragment">- **Separation of Concerns** - Business logic von Integration getrennt</div>
<div class="fragment">- **Reusability** - Legacy-Code weiter nutzen</div>
<div class="fragment">- **Maintainability** - Änderungen isoliert im Adapter</div>
<div class="fragment">- **Testability** - Adapter separat testbar</div> 

</div>
</div>

<!-- Speaker Notes: Das Adapter Pattern ist bei Enterprise besonders wichtig für die Integration von Legacy-Systemen. Wir haben viele bewährte Systeme, die weiterhin funktionieren, aber moderne APIs benötigen. -->

---

## Lösung

<div class="code-example">
<h5>Enterprise Billing System Adapter</h5>

```typescript
// Modern Interface
interface ModernBillingInterface { // fragment
  createInvoice(customer: Customer, amount: number): Invoice; // fragment
  getInvoiceStatus(invoiceId: string): BillingStatus; // fragment
} // fragment

class LegacyBillingSystem { // fragment
  // Legacy API mit anderem Interface // fragment
  generateBill(custId: number, sum: number): LegacyBill { // fragment
    return new LegacyBill(custId, sum, new Date()); // fragment
  } // fragment
  
  checkBillStatus(billNumber: string): number { // fragment
    // Returns numeric status codes // fragment
    return 1; // 1=pending, 2=paid, 3=overdue // fragment
  } // fragment
} // fragment

class BillingAdapter implements ModernBillingInterface { // fragment
  constructor(private legacySystem: LegacyBillingSystem) {} // fragment
  
  createInvoice(customer: Customer, amount: number): Invoice { // fragment
    const legacyBill = this.legacySystem.generateBill(customer.id, amount); // fragment
    
    return new Invoice({ // fragment
      id: legacyBill.billNumber, // fragment
      customerId: customer.id, // fragment
      amount: amount, // fragment
      date: legacyBill.createdAt, // fragment
      status: 'pending' // fragment
    }); // fragment
  } // fragment
  
  getInvoiceStatus(invoiceId: string): BillingStatus { // fragment
    const statusCode = this.legacySystem.checkBillStatus(invoiceId); // fragment
    
    // Convert numeric codes to modern enum // fragment
    switch(statusCode) { // fragment
      case 1: return BillingStatus.PENDING; // fragment
      case 2: return BillingStatus.PAID; // fragment
      case 3: return BillingStatus.OVERDUE; // fragment
      default: return BillingStatus.UNKNOWN; // fragment
    } // fragment
  } // fragment
} // fragment

// Verwendung
const legacySystem = new LegacyBillingSystem(); // fragment
const modernBilling: ModernBillingInterface = new BillingAdapter(legacySystem); // fragment

const invoice = modernBilling.createInvoice(customer, 99.99); // fragment
const status = modernBilling.getInvoiceStatus(invoice.id); // fragment
```

</div>

<!-- Speaker Notes: Der Adapter fungiert als Übersetzer zwischen alter und neuer Welt. Notice how the adapter translates between different parameter types and return formats while maintaining the modern interface. -->

---

## Übung

<div class="interactive-question">

### Praktische Übung: Adapter Pattern
#### Implementieren Sie einen PaymentSystemAdapter

</div>

**Aufgabe**: Erweitern Sie das System um verschiedene Payment-Provider:
- PayPalAdapter für PayPal API Integration
- StripeAdapter für Stripe API Integration  
- LegacyBankAdapter für proprietäre Banking-Systeme
- Einheitliches PaymentService Interface

**Implementierungsschritte**:
1. PaymentService Interface definieren
2. Adapter für jeden Provider implementieren
3. Datenkonvertierung zwischen APIs
4. Fehlerbehandlung und Status-Mapping

---

# Decorator Pattern

## Was ist hier falsch?

<div class="problem-highlight">

#### Decorator Pattern Problem
**Situation**: Cross-Cutting Concerns wie Logging, Security und Performance Monitoring führen zu einem Durcheinander wenn sie direkt in die Business-Logik eingebaut werden.

**Was sehen Sie hier Problematisches?**

</div>

<div class="code-example">

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

</div>

---

## Identifizierte Code-Smells

- **Mixed Concerns**: Business-Logik vermischt mit Cross-Cutting Concerns 
- **Duplicate Code**: Logging/Security/Performance Code in jeder Methode 
- **Hard to Maintain**: Änderungen am Logging betreffen alle Methoden 
- **Inflexible**: Concerns können nicht optional oder kombiniert werden 
- **Single Responsibility Violation**: Service macht viel mehr als Business-Logik 
- **Testing Nightmare**: Cross-Cutting Concerns erschweren Unit-Tests 

---

## Decorator Pattern - Die Lösung

<div class="pattern-definition">

#### Decorator Pattern
**Intent**: Erweitert Objektfunktionalität dynamisch zur Laufzeit ohne die ursprüngliche Klasse zu ändern.

**Problem**: Objekte benötigen zusätzliche Funktionalität, aber Vererbung ist zu starr oder nicht praktikabel.

**Solution**: Wrapper-Objekte, die das ursprüngliche Interface implementieren und Funktionalität hinzufügen.

</div>

<div class="two-column">
<div>

## Use Cases
<div class="fragment">- **Behavior Extension** - Funktionalität ohne Vererbung hinzufügen</div>
<div class="fragment">- **Responsibility Chaining** - Multiple Decorator verschachteln</div>
<div class="fragment">- **Runtime Composition** - Flexibel zur Laufzeit konfigurieren</div>
<div class="fragment">- **Open-Closed Principle** - Erweiterung ohne Modifikation</div>
<div class="fragment">- **Cross-Cutting Concerns** - Logging, Security, Caching</div> 

</div>
<div>

## Benefits
<div class="fragment">- **Flexibility** - Laufzeit-Konfiguration</div>
<div class="fragment">- **Single Responsibility** - Jeder Decorator eine Aufgabe</div>
<div class="fragment">- **Composability** - Verschiedene Kombinationen möglich</div>
<div class="fragment">- **Maintainability** - Concerns getrennt</div> 

</div>
</div>

<!-- Speaker Notes: Das Decorator Pattern ist essentiell für Enterprise-Services. Bei Enterprise verwenden wir es für Logging, Security, Performance Monitoring und Caching. Es ermöglicht uns, Cross-Cutting Concerns sauber zu trennen und flexibel zu kombinieren. -->

---

## Lösung

<div class="code-example">
<h5>Enterprise Service Decoration für Cross-Cutting Concerns</h5>

```typescript
interface EnterpriseService { // fragment
  processRequest(request: ServiceRequest): ServiceResponse; // fragment
} // fragment

class BasicCustomerService implements EnterpriseService { // fragment
  processRequest(request: ServiceRequest): ServiceResponse { // fragment
    // Core business logic // fragment
    return new ServiceResponse({ // fragment
      success: true, // fragment
      data: `Processed customer request: ${request.type}`, // fragment
      timestamp: new Date() // fragment
    }); // fragment
  } // fragment
} // fragment

// Decorator Base Class
abstract class ServiceDecorator implements EnterpriseService { // fragment
  constructor(protected service: EnterpriseService) {} // fragment
  
  processRequest(request: ServiceRequest): ServiceResponse { // fragment
    return this.service.processRequest(request); // fragment
  } // fragment
} // fragment

// Logging Decorator
class LoggingDecorator extends ServiceDecorator { // fragment
  processRequest(request: ServiceRequest): ServiceResponse { // fragment
    console.log(`[LOG] Processing request: ${request.type} at ${new Date()}`); // fragment
    
    const response = super.processRequest(request); // fragment
    
    console.log(`[LOG] Request completed: ${response.success ? 'SUCCESS' : 'FAILED'}`); // fragment
    return response; // fragment
  } // fragment
} // fragment

// Security Decorator
class SecurityDecorator extends ServiceDecorator { // fragment
  processRequest(request: ServiceRequest): ServiceResponse { // fragment
    // Validate authentication and authorization // fragment
    if (!request.token || !this.validateToken(request.token)) { // fragment
      return new ServiceResponse({ // fragment
        success: false, // fragment
        error: 'Unauthorized access', // fragment
        timestamp: new Date() // fragment
      }); // fragment
    } // fragment
    
    return super.processRequest(request); // fragment
  } // fragment
  
  private validateToken(token: string): boolean { // fragment
    // Enterprise token validation logic // fragment
    return token.startsWith('enterprise_') && token.length > 10; // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Notice how each decorator adds a single responsibility. We can combine them in any order and create different service configurations for different environments. -->

---

# Decorator Pattern - Flexible Composition

<div class="code-example">
<h5>Flexible Service Composition</h5>

```typescript
// Performance Monitoring Decorator
class PerformanceDecorator extends ServiceDecorator { // fragment
  processRequest(request: ServiceRequest): ServiceResponse { // fragment
    const startTime = performance.now(); // fragment
    
    const response = super.processRequest(request); // fragment
    
    const executionTime = performance.now() - startTime; // fragment
    console.log(`[PERF] Request executed in ${executionTime.toFixed(2)}ms`); // fragment
    
    // Add performance metrics to response // fragment
    response.metadata = { executionTime }; // fragment
    return response; // fragment
  } // fragment
} // fragment

// Usage - Flexible composition
let customerService: EnterpriseService = new BasicCustomerService(); // fragment

// Add logging
customerService = new LoggingDecorator(customerService); // fragment

// Add security
customerService = new SecurityDecorator(customerService); // fragment

// Add performance monitoring
customerService = new PerformanceDecorator(customerService); // fragment

// Process request with all decorators
const response = customerService.processRequest({ // fragment
  type: 'UPDATE_CUSTOMER_DATA', // fragment
  token: 'enterprise_abc123456789', // fragment
  customerId: '12345' // fragment
}); // fragment
```

</div>

<div class="highlight-box success">
**Execution Flow**: Basic Service → Performance → Security → Logging → Core Business Logic
</div>

<!-- Speaker Notes: This demonstrates the power of the decorator pattern. We can create different service configurations by mixing and matching decorators. Each decorator wraps the previous one, creating a chain of responsibility. -->

---

## Übung

<div class="interactive-question">

### Praktische Übung: Decorator Pattern
#### Implementieren Sie einen CachingDecorator

</div>

**Aufgabe**: Erweitern Sie das Service-System um Caching-Funktionalität:
- CachingDecorator für automatisches Caching
- TTL-basierte Cache-Expiration
- Cache-Statistics für Monitoring
- Kombinierbar mit anderen Decorators

**Implementierungsschritte**:
1. CachingDecorator-Klasse implementieren
2. Cache-Key Generation Strategy
3. TTL-Management für Expiration
4. Kombination mit Security/Logging-Decorators testen

---

# Facade Pattern

## Was ist hier falsch?

<div class="problem-highlight">

#### Facade Pattern Problem
**Situation**: Clients müssen mit vielen verschiedenen Services und komplexen APIs interagieren, was zu unübersichtlichem und fehleranfälligem Code führt.

**Was sehen Sie hier Problematisches?**

</div>

<div class="code-example">

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

</div>

---

## Identifizierte Code-Smells

- **Complex Orchestration**: Client muss alle Services koordinieren 
- **Tight Coupling**: Client kennt alle Subsystem-Details 
- **Error Handling Chaos**: Rollback und Partial Failures komplex 
- **Duplicate Logic**: Orchestration Code in jedem Client wiederholt 
- **Transaction Management**: Kein koordiniertes Transaction-Handling 
- **Single Point of Failure**: Wenn ein Service failiert, bricht alles zusammen 

---

## Facade Pattern - Die Lösung

<div class="pattern-definition">

#### Facade Pattern
**Intent**: Bietet eine vereinfachte Schnittstelle zu komplexen Subsystemen.

**Problem**: Clients müssen mit vielen verschiedenen Klassen und komplexen APIs interagieren.

**Solution**: Eine Facade-Klasse kapselt die Komplexität und bietet einfache, hochlevelige Operationen.

</div>

<div class="two-column">
<div>

## Use Cases
- **Complexity Hiding** - Einfache API für komplexe Operationen 
- **Subsystem Coordination** - Multiple Services orchestrieren 
- **Client Decoupling** - Clients vom Subsystem entkoppeln 
- **API Standardization** - Einheitliche Schnittstellen schaffen 
- **Integration Layer** - Microservices zusammenfassen 

</div>
<div>

## Benefits
- **Simplicity** - Einfache Client-Schnittstelle 
- **Decoupling** - Clients unabhängig von Implementierung 
- **Flexibility** - Subsystem-Änderungen transparent 
- **Reusability** - Hochlevelige Operationen wiederverwendbar 

</div>
</div>

<!-- Speaker Notes: Das Facade Pattern ist bei Enterprise unverzichtbar für die Vereinfachung komplexer Geschäftsprozesse. Customer Onboarding involviert normalerweise 5-6 verschiedene Services. Die Facade versteckt diese Komplexität und bietet eine einfache, fehlerresistente API. -->

---

## Lösung

<div class="code-example">
<h5>Enterprise Customer Management Facade</h5>

```typescript
class EnterpriseCustomerFacade { // fragment
  constructor( // fragment
    private customerService: CustomerService, // fragment
    private billingService: BillingService, // fragment
    private contractService: ContractService, // fragment
    private notificationService: NotificationService, // fragment
    private auditService: AuditService // fragment
  ) {} // fragment
  
  // Vereinfachte API für komplexes Customer Onboarding
  async onboardNewCustomer(customerData: CustomerData): Promise<OnboardingResult> { // fragment
    try { // fragment
      // 1. Create customer record // fragment
      const customer = await this.customerService.create({ // fragment
        name: customerData.name, // fragment
        email: customerData.email, // fragment
        phone: customerData.phone, // fragment
        address: customerData.address // fragment
      }); // fragment
      
      // 2. Set up billing account // fragment
      const billingAccount = await this.billingService.createAccount({ // fragment
        customerId: customer.id, // fragment
        paymentMethod: customerData.paymentMethod, // fragment
        billingAddress: customerData.billingAddress // fragment
      }); // fragment
      
      // 3. Create initial contract // fragment
      const contract = await this.contractService.create({ // fragment
        customerId: customer.id, // fragment
        planType: customerData.selectedPlan, // fragment
        startDate: new Date(), // fragment
        billingAccountId: billingAccount.id // fragment
      }); // fragment
      
      // 4. Send welcome notification // fragment
      await this.notificationService.sendWelcomeEmail({ // fragment
        customerEmail: customer.email, // fragment
        customerName: customer.name, // fragment
        contractDetails: contract // fragment
      }); // fragment
      
      // 5. Log audit trail // fragment
      await this.auditService.logCustomerAction({ // fragment
        action: 'CUSTOMER_ONBOARDED', // fragment
        customerId: customer.id, // fragment
        timestamp: new Date(), // fragment
        details: { contractId: contract.id, plan: customerData.selectedPlan } // fragment
      }); // fragment
      
      return new OnboardingResult({ // fragment
        success: true, // fragment
        customerId: customer.id, // fragment
        contractId: contract.id, // fragment
        message: 'Customer successfully onboarded' // fragment
      }); // fragment
      
    } catch (error) { // fragment
      // Comprehensive error handling // fragment
      await this.auditService.logError({ // fragment
        action: 'CUSTOMER_ONBOARDING_FAILED', // fragment
        error: error.message, // fragment
        customerData: customerData.email // Safe logging // fragment
      }); // fragment
      
      return new OnboardingResult({ // fragment
        success: false, // fragment
        error: 'Onboarding process failed', // fragment
        details: error.message // fragment
      }); // fragment
    } // fragment
  } // fragment
} // fragment

// Simple usage for complex operations
const customerFacade = new EnterpriseCustomerFacade( // fragment
  customerService, billingService, contractService,  // fragment
  notificationService, auditService // fragment
); // fragment

// Complex onboarding with single method call
const result = await customerFacade.onboardNewCustomer({ // fragment
  name: 'Max Mustermann', // fragment
  email: 'max@example.com', // fragment
  phone: '+49 123 456789', // fragment
  selectedPlan: 'BUSINESS_PRO', // fragment
  paymentMethod: 'SEPA' // fragment
}); // fragment
```

</div>

<!-- Speaker Notes: Notice how the facade coordinates multiple services and handles all error scenarios. What would be 20+ lines of client code is now a single method call. -->

---

# Composite Pattern

<div class="pattern-definition">

#### Composite Pattern
**Intent**: Behandelt einzelne Objekte und Objektgruppen einheitlich in Baumstrukturen.

**Problem**: Hierarchische Strukturen sollen einheitlich behandelt werden, unabhängig davon ob es Einzelobjekte oder Gruppen sind.

**Solution**: Gemeinsames Interface für Leaf- und Composite-Objekte mit rekursiven Operationen.

</div>

<div class="two-column">
<div>

## Use Cases
- **Hierarchical Structures** - Baumstrukturen elegant handhaben 
- **Uniform Interface** - Gleiche Operationen für Blätter und Äste 
- **Recursive Operations** - Operationen propagieren automatisch 
- **Dynamic Composition** - Strukturen zur Laufzeit ändern 
- **Organizational Modeling** - Unternehmen, Teams, Projekte 

</div>
<div>

## Benefits
- **Simplicity** - Einheitliche Behandlung 
- **Flexibility** - Dynamische Strukturänderungen 
- **Scalability** - Beliebig tiefe Hierarchien 
- **Reusability** - Wiederverwendbare Tree-Operationen 

</div>
</div>

<!-- Speaker Notes: Das Composite Pattern ist perfekt für enterprise organizational structures. Wir können einheitlich auf Mitarbeiter, Teams und ganze Divisionen zugreifen. Egal ob wir das Budget eines einzelnen Mitarbeiters oder der gesamten Technologie-Sparte berechnen - das Interface bleibt gleich. -->

---

# Composite Pattern - Organization Structure

<div class="code-example">
<h5>Enterprise Organizational Structure mit Composite Pattern</h5>

```typescript
interface OrganizationalUnit { // fragment
  getName(): string; // fragment
  getEmployeeCount(): number; // fragment
  getBudget(): number; // fragment
  addUnit(unit: OrganizationalUnit): void; // fragment
  removeUnit(unit: OrganizationalUnit): void; // fragment
  generateReport(): OrganizationalReport; // fragment
} // fragment

class Employee implements OrganizationalUnit { // fragment
  constructor( // fragment
    private name: string, // fragment
    private salary: number, // fragment
    private position: string // fragment
  ) {} // fragment
  
  getName(): string { // fragment
    return this.name; // fragment
  } // fragment
  
  getEmployeeCount(): number { // fragment
    return 1; // An employee counts as 1 // fragment
  } // fragment
  
  getBudget(): number { // fragment
    return this.salary; // fragment
  } // fragment
  
  addUnit(unit: OrganizationalUnit): void { // fragment
    throw new Error('Cannot add units to an employee'); // fragment
  } // fragment
  
  removeUnit(unit: OrganizationalUnit): void { // fragment
    throw new Error('Cannot remove units from an employee'); // fragment
  } // fragment
  
  generateReport(): OrganizationalReport { // fragment
    return { // fragment
      name: this.name, // fragment
      type: 'EMPLOYEE', // fragment
      employeeCount: 1, // fragment
      budget: this.salary, // fragment
      position: this.position, // fragment
      children: [] // fragment
    }; // fragment
  } // fragment
} // fragment

class Department implements OrganizationalUnit { // fragment
  private units: OrganizationalUnit[] = []; // fragment
  
  constructor( // fragment
    private name: string, // fragment
    private operationalBudget: number = 0 // fragment
  ) {} // fragment
  
  getName(): string { // fragment
    return this.name; // fragment
  } // fragment
  
  getEmployeeCount(): number { // fragment
    return this.units.reduce((total, unit) => { // fragment
      return total + unit.getEmployeeCount(); // fragment
    }, 0); // fragment
  } // fragment
  
  getBudget(): number { // fragment
    const personnelBudget = this.units.reduce((total, unit) => { // fragment
      return total + unit.getBudget(); // fragment
    }, 0); // fragment
    
    return personnelBudget + this.operationalBudget; // fragment
  } // fragment
  
  addUnit(unit: OrganizationalUnit): void { // fragment
    this.units.push(unit); // fragment
  } // fragment
  
  removeUnit(unit: OrganizationalUnit): void { // fragment
    const index = this.units.indexOf(unit); // fragment
    if (index > -1) { // fragment
      this.units.splice(index, 1); // fragment
    } // fragment
  } // fragment
  
  generateReport(): OrganizationalReport { // fragment
    const childReports = this.units.map(unit => unit.generateReport()); // fragment
    
    return { // fragment
      name: this.name, // fragment
      type: 'DEPARTMENT', // fragment
      employeeCount: this.getEmployeeCount(), // fragment
      budget: this.getBudget(), // fragment
      operationalBudget: this.operationalBudget, // fragment
      children: childReports // fragment
    }; // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Notice how both Employee and Department implement the same interface. This allows us to treat individual employees and entire departments uniformly. -->

---

# Composite Pattern - Usage Example

<div class="code-example">
<h5>Building Enterprise Organization Hierarchy</h5>

```typescript
// Enterprise organizational structure example
const = new Department('Deutsche Enterprise AG', 1000000); // fragment

// Technology Division
const technologyDiv = new Department('Technology', 500000); // fragment
const cloudTeam = new Department('Cloud Services', 50000); // fragment
const securityTeam = new Department('IT Security', 75000); // fragment

// Add employees to teams
cloudTeam.addUnit(new Employee('Anna Schmidt', 85000, 'Cloud Architect')); // fragment
cloudTeam.addUnit(new Employee('Peter Wagner', 75000, 'DevOps Engineer')); // fragment
cloudTeam.addUnit(new Employee('Lisa Chen', 80000, 'Cloud Developer')); // fragment

securityTeam.addUnit(new Employee('Michael Bauer', 90000, 'Security Architect')); // fragment
securityTeam.addUnit(new Employee('Sarah Müller', 82000, 'Security Engineer')); // fragment

// Build hierarchy
technologyDiv.addUnit(cloudTeam); // fragment
technologyDiv.addUnit(securityTeam); // fragment
telekom.addUnit(technologyDiv); // fragment

// Uniform operations on the entire structure
console.log(`Total employees: ${telekom.getEmployeeCount()}`); // fragment
console.log(`Total budget: €${telekom.getBudget().toLocaleString()}`); // fragment

// Generate comprehensive report
const report = telekom.generateReport(); // fragment
console.log(JSON.stringify(report, null, 2)); // fragment

// Dynamic restructuring
const newInnovationLab = new Department('Innovation Lab', 25000); // fragment
newInnovationLab.addUnit(new Employee('Dr. Frank Weber', 95000, 'Innovation Lead')); // fragment

technologyDiv.addUnit(newInnovationLab); // fragment
```

</div>

<div class="highlight-box accent">
**Key Benefit**: Same operations work on individuals, teams, divisions, or the entire company structure!
</div>

<!-- Speaker Notes: This demonstrates the power of uniform interfaces. We can calculate budgets, employee counts, and generate reports at any level of the organization using the same methods. -->

---

# Proxy Pattern

<div class="pattern-definition">

#### Proxy Pattern
**Intent**: Fungiert als Stellvertreter und kontrolliert Zugriff auf andere Objekte.

**Problem**: Direkter Zugriff auf Objekte ist teuer, unsicher oder unpraktisch.

**Solution**: Ein Proxy-Objekt kontrolliert und optimiert den Zugriff auf das eigentliche Objekt.

</div>

<div class="two-column">
<div>

## Use Cases
- **Access Control** - Sicherheitsproxy für geschützte Ressourcen 
- **Lazy Loading** - Objekte erst bei Bedarf laden 
- **Caching** - Wiederholte Anfragen zwischenspeichern 
- **Remote Access** - Entfernte Objekte lokal repräsentieren 
- **Resource Management** - Teure Operationen optimieren 

</div>
<div>

## Benefits
- **Performance** - Caching und Lazy Loading 
- **Security** - Zugriffskontrolle 
- **Transparency** - Client merkt Proxy nicht 
- **Flexibility** - Verschiedene Proxy-Typen 

</div>
</div>

<!-- Speaker Notes: Das Proxy Pattern ist bei Enterprise kritisch für Performance und Security. Wir nutzen es für Datenschutz (Customer Data Protection), Caching teurer Analytics-Queries und für Remote Service Access. Der Proxy fungiert als intelligenter Gateway zwischen Client und Service. -->

---

# Proxy Pattern - Data Access with Security

<div class="code-example">
<h5>Enterprise Data Access Proxy mit Caching und Security</h5>

```typescript
interface EnterpriseDataService { // fragment
  getCustomerData(customerId: string): Promise<CustomerData>; // fragment
  updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean>; // fragment
  getUsageStatistics(customerId: string, period: DateRange): Promise<UsageStats>; // fragment
} // fragment

class RealEnterpriseDataService implements EnterpriseDataService { // fragment
  async getCustomerData(customerId: string): Promise<CustomerData> { // fragment
    // Simulate expensive database call // fragment
    console.log(`[DB] Fetching customer data for ${customerId}`); // fragment
    await this.delay(500); // Simulate network delay // fragment
    
    return { // fragment
      id: customerId, // fragment
      name: 'Max Mustermann', // fragment
      email: 'max@telekom.de', // fragment
      contract: 'BUSINESS_PRO', // fragment
      lastLogin: new Date(), // fragment
      dataUsage: 15.5 // fragment
    }; // fragment
  } // fragment
  
  async updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean> { // fragment
    console.log(`[DB] Updating customer data for ${customerId}`); // fragment
    await this.delay(300); // fragment
    return true; // fragment
  } // fragment
  
  async getUsageStatistics(customerId: string, period: DateRange): Promise<UsageStats> { // fragment
    console.log(`[DB] Fetching usage statistics for ${customerId}`); // fragment
    await this.delay(800); // Heavy analytics query // fragment
    
    return { // fragment
      customerId, // fragment
      period, // fragment
      dataUsed: 45.2, // fragment
      callMinutes: 890, // fragment
      smsCount: 156, // fragment
      costs: 89.95 // fragment
    }; // fragment
  } // fragment
  
  private delay(ms: number): Promise<void> { // fragment
    return new Promise(resolve => setTimeout(resolve, ms)); // fragment
  } // fragment
} // fragment
```

</div>

<!-- Speaker Notes: This is the real service that does the actual work. Notice the simulated delays that represent real-world database and network latency. -->

---

# Proxy Pattern - Implementation

<div class="code-example">
<h5>Security and Caching Proxy Implementation</h5>

```typescript
class EnterpriseDataServiceProxy implements EnterpriseDataService { // fragment
  private realService: RealEnterpriseDataService; // fragment
  private cache = new Map<string, { data: any; timestamp: number }>(); // fragment
  private readonly CACHE_TTL = 5 * 60 * 1000; // 5 minutes // fragment
  
  constructor(private authService: AuthenticationService) { // fragment
    this.realService = new RealEnterpriseDataService(); // fragment
  } // fragment
  
  async getCustomerData(customerId: string): Promise<CustomerData> { // fragment
    // Security check // fragment
    if (!await this.checkAccess(customerId)) { // fragment
      throw new Error('Unauthorized access to customer data'); // fragment
    } // fragment
    
    // Check cache first // fragment
    const cacheKey = `customer_${customerId}`; // fragment
    const cached = this.getCachedData(cacheKey); // fragment
    if (cached) { // fragment
      console.log(`[CACHE] Returning cached customer data for ${customerId}`); // fragment
      return cached; // fragment
    } // fragment
    
    // Fetch from real service // fragment
    const data = await this.realService.getCustomerData(customerId); // fragment
    this.setCachedData(cacheKey, data); // fragment
    
    return data; // fragment
  } // fragment
  
  async updateCustomerData(customerId: string, data: Partial<CustomerData>): Promise<boolean> { // fragment
    // Security check // fragment
    if (!await this.checkAccess(customerId)) { // fragment
      throw new Error('Unauthorized access to customer data'); // fragment
    } // fragment
    
    // Update via real service // fragment
    const result = await this.realService.updateCustomerData(customerId, data); // fragment
    
    // Invalidate cache // fragment
    this.invalidateCustomerCache(customerId); // fragment
    
    return result; // fragment
  } // fragment
  
  private async checkAccess(customerId: string): Promise<boolean> { // fragment
    // Simulate authentication and authorization check // fragment
    const currentUser = await this.authService.getCurrentUser(); // fragment
    
    // Check if user has access to this customer data // fragment
    if (currentUser.role === 'ADMIN') { // fragment
      return true; // fragment
    } // fragment
    
    if (currentUser.role === 'SUPPORT' && currentUser.customerId === customerId) { // fragment
      return true; // fragment
    } // fragment
    
    if (currentUser.customerId === customerId) { // fragment
      return true; // fragment
    } // fragment
    
    console.log(`[SECURITY] Access denied for user ${currentUser.id} to customer ${customerId}`); // fragment
    return false; // fragment
  } // fragment
  
  private getCachedData(key: string): any { // fragment
    const cached = this.cache.get(key); // fragment
    if (cached && (Date.now() - cached.timestamp < this.CACHE_TTL)) { // fragment
      return cached.data; // fragment
    } // fragment
    return null; // fragment
  } // fragment
  
  private setCachedData(key: string, data: any): void { // fragment
    this.cache.set(key, { // fragment
      data: data, // fragment
      timestamp: Date.now() // fragment
    }); // fragment
  } // fragment
  
  private invalidateCustomerCache(customerId: string): void { // fragment
    const keysToDelete: string[] = []; // fragment
    for (const key of this.cache.keys()) { // fragment
      if (key.includes(customerId)) { // fragment
        keysToDelete.push(key); // fragment
      } // fragment
    } // fragment
    keysToDelete.forEach(key => this.cache.delete(key)); // fragment
  } // fragment
} // fragment

// Usage
const authService = new AuthenticationService(); // fragment
const dataService: EnterpriseDataService = new EnterpriseDataServiceProxy(authService); // fragment

// First call - hits database
const customer1 = await dataService.getCustomerData('123456'); // fragment

// Second call - returns from cache
const customer2 = await dataService.getCustomerData('123456'); // fragment

// Unauthorized call - throws security exception
try { // fragment
  await dataService.getCustomerData('unauthorized_customer'); // fragment
} catch (error) { // fragment
  console.log('Security proxy blocked unauthorized access'); // fragment
} // fragment
```

</div>

<!-- Speaker Notes: Notice how the proxy adds security and caching without the client knowing. The client uses the same interface but gets enhanced functionality. -->

---

# Day 2 Hands-on Übung

<div class="interactive-question">

## Aufgabe: Enterprise Service Integration Platform
#### Implementieren Sie eine Service Integration Platform mit allen gelernten Structural Patterns

</div>

<div class="two-column">
<div>

### Anforderungen:
- **Adapter** - Legacy billing system integration 
- **Decorator** - Add logging, security, monitoring 
- **Facade** - Unified customer service API 
- **Composite** - Service hierarchy management 
- **Proxy** - Caching and access control 

</div>
<div>

### Bonus Challenges:
- Configuration-driven decorator selection 
- Dynamic service discovery 
- Health monitoring integration 
- Performance metrics collection 
- Error recovery mechanisms 

</div>
</div>

<div class="progress-indicator">
<div class="progress-step current">🛠️ Exercise Time</div>
<div class="progress-step pending">👥 Work in teams of 3-4</div>
<div class="progress-step pending">🎯 Focus on pattern integration</div>
</div>

<div class="code-example">
<h5>Basis Code Structure:</h5>

```typescript
// Your task: Implement missing patterns
interface ServicePlatform {
  // Define your unified interface
}

class LegacyBillingSystem {
  generateBill(custId: number, amount: number): LegacyBill { /* ... */ }
}

class CustomerServiceFacade {
  // Implement facade for complex operations
}

// Implement all patterns step by step
```

</div>

Note:
- GROSSE PRAXIS-ÜBUNG: Intensive Teamarbeit mit day2-exercises.md
- Teams von 3-4 Personen, unterschiedliche Erfahrungslevels mischen
- WICHTIG: Verwenden Sie alle 4 Exercises aus day2-exercises.md:
  * Exercise 1: Service Enhancement Pipeline (Decorator Pattern)
  * Exercise 2: Legacy System Integration (Adapter Pattern)  
  * Exercise 3: Complex Configuration (Composite Pattern)
  * Exercise 4: Service Caching Solution (Proxy Pattern)
- Jedes Team wählt 2 Exercises basierend auf Interesse/Erfahrung
- Zirkulieren Sie regelmäßig zwischen Teams
- Hilfestellung bei Design-Entscheidungen, nicht bei Syntax
- Zwischenstand und Feedback-Runde
- Jedes Team präsentiert eine Lösung
- Bereiten Sie Referenz-Lösungen aus exercises vor
- Präsentation und Diskussion der Lösungsansätze
- Musterlösung bereithalten für Teams die schnell fertig sind
<!-- .element: class="notes" -->

---

# Structural Patterns Best Practices

<div class="two-column">
<div>

## Enterprise Specific Guidelines:

- **Security First** - Alle Patterns mit Security Decorators erweitern 
- **Performance Aware** - Proxy Pattern für teure Operations 
- **Legacy Integration** - Adapter Pattern für Bestandssysteme 
- **API Standardization** - Facade Pattern für einheitliche APIs 
- **Monitoring Integration** - Decorator für comprehensive logging 

</div>
<div>

## Häufige Fallstricke:

- **Over-Engineering** - Nicht jede Klasse braucht einen Proxy 
- **Cache Invalidation** - Proxy Caches müssen intelligent invalidiert werden 
- **Deep Nesting** - Decorator chains nicht zu tief verschachteln 
- **Interface Bloat** - Facades nicht mit zu vielen Methoden überladen 

</div>
</div>

<div class="highlight-box warning">
**Golden Rule**: Patterns sollen Komplexität reduzieren, nicht erhöhen. Wenn ein Pattern mehr Probleme schafft als löst, überdenken Sie den Ansatz.
</div>

<!-- Speaker Notes: Diese Best Practices basieren auf realen Enterprise Projekten. Teilen Sie konkrete Beispiele wo möglich und warnen Sie vor den typischen Fehlern, die wir selbst gemacht haben. -->

---

# Tag 2 Zusammenfassung

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

- **Adapter Pattern** - Legacy Integration mit modernen APIs 
- **Decorator Pattern** - Flexible Cross-Cutting Concerns 
- **Facade Pattern** - Vereinfachung komplexer Subsysteme 
- **Composite Pattern** - Hierarchische Strukturen elegant handhaben 
- **Proxy Pattern** - Intelligente Zugriffskontrolle und Caching 

</div>
<div>

## Morgen - Tag 3:

- **Behavioral Patterns** - Observer, Strategy, Command 
- **Enterprise Integration** - Message Queues und Event-Driven Architecture 
- **Microservices Patterns** - Service Mesh und API Gateway 
- **Advanced Topics** - CQRS, Event Sourcing 

</div>
</div>

<!-- Speaker Notes: Fassen Sie die wichtigsten Erkenntnisse des Tages zusammen. Betonen Sie, wie diese Patterns bei Enterprise täglich verwendet werden. Machen Sie die Teilnehmer neugierig auf Tag 3 - Behavioral Patterns sind noch spannender und näher an der Geschäftslogik. -->

---

# Q&A und Diskussion

<div class="interactive-question">

## Fragen zu den Structural Patterns?

</div>

<div class="two-column">
<div>

### Diskussionspunkte:
- Welche Patterns verwenden Sie bereits? 
- Wo sehen Sie Anwendungsmöglichkeiten in Ihren Projekten? 
- Welche Herausforderungen haben Sie bei der Implementation? 
- Wie kombinieren Sie verschiedene Patterns? 

</div>
<div>

### Kontakt für weitere Fragen:
- **Email:** architecture-training@company.com 
- **Teams:** Enterprise Architecture Community 
- **Wiki:** Internal Pattern Documentation 
- **GitHub:** Code Examples Repository 

</div>
</div>

<div class="workshop-header">

**Vielen Dank für Ihre aktive Teilnahme!**
### Bis morgen zu Tag 3 - Behavioral Patterns!

</div>

<!-- Speaker Notes: Ermutigen Sie zur offenen Diskussion. Viele Teilnehmer haben bereits unbewusst diese Patterns verwendet. Helfen Sie dabei, die Verbindung zwischen Theorie und ihrer täglichen Arbeit herzustellen. Sammeln Sie Feedback für Verbesserungen des Trainings. -->