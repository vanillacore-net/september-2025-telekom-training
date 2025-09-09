---
type: slide
title: Software-Architektur - Grundlagen
description: Code-Analyse, Factory Method, Abstract Factory, Builder & Prototype Patterns
tags: design-patterns, workshop, architecture, training, day1, factory, builder, prototype
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

Note:
* Begrüßen Sie die Teilnehmer herzlich
<!-- .element: class="fragment" -->
* Stellen Sie sich kurz vor (Name, Hintergrund)
<!-- .element: class="fragment" -->
* Überprüfen Sie die technische Ausstattung
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

</div>

---

# Willkommen zur Software-Architektur - Grundlagen
## Bring your own brain and use it!

### Über diesen Workshop
<!-- .slide: data-auto-animate -->
* Code-Analyse & Factory Method Pattern
<!-- .element: class="fragment" data-fragment-index="1" -->

* Abstract Factory & Layered Architecture
<!-- .element: class="fragment" data-fragment-index="2" -->

* Builder Pattern & Repository Integration
<!-- .element: class="fragment" data-fragment-index="3" -->

* Prototype Pattern & Configuration Cloning
<!-- .element: class="fragment" data-fragment-index="4" -->

* Clean Architecture: Fundamentale Prinzipien
<!-- .element: class="fragment" data-fragment-index="5" -->

* Singleton & Adapter mit Clean Architecture
<!-- .element: class="fragment" data-fragment-index="6" -->

## Lernziele

* Code-Smells in Legacy-Code identifizieren
<!-- .element: class="fragment" data-fragment-index="1" -->

* Factory Method Pattern verstehen und anwenden  
<!-- .element: class="fragment" data-fragment-index="2" -->

* Abstract Factory in geschichteten Architekturen
<!-- .element: class="fragment" data-fragment-index="3" -->

* Builder Pattern für komplexe Objekterstellung
<!-- .element: class="fragment" data-fragment-index="4" -->

* SOLID-Prinzipien praktisch umsetzen
<!-- .element: class="fragment" data-fragment-index="5" -->

Note:
* Betonen Sie die praktische Relevanz der Lernziele
<!-- .element: class="fragment" -->
* Fragen Sie nach Vorerfahrungen mit Design Patterns
<!-- .element: class="fragment" -->
* Erwähnen Sie, dass alle Beispiele auf realistischen Enterprise-Szenarien basieren
<!-- .element: class="fragment" -->
* Heben Sie hervor: "Wir analysieren echten Legacy-Code"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

<!-- Speaker Notes: Herzlich willkommen zur Software-Architektur Schulung. Wir konzentrieren uns auf Creational Patterns in Enterprise-Umgebungen. Wir werden Legacy-Code analysieren und Refactoring mit Pattern-basierten Lösungen durchführen. -->

---

# Workshop Agenda

## Workshop Module

* **Modul 1:** Code-Analyse & Factory Method
<!-- .element: class="fragment" data-fragment-index="1" -->
* **Modul 2:** Abstract Factory & Layered Architecture
<!-- .element: class="fragment" data-fragment-index="2" -->
* **Modul 3:** Builder Pattern & Repository Integration
<!-- .element: class="fragment" data-fragment-index="3" -->
* **Modul 4:** Prototype Pattern & Configuration Cloning
<!-- .element: class="fragment" data-fragment-index="4" -->
* **Clean Architecture:** Fundamentale Prinzipien
<!-- .element: class="fragment" data-fragment-index="5" -->
* **Modul 5:** Singleton & Adapter mit Clean Architecture
<!-- .element: class="fragment" data-fragment-index="6" -->
* Code-Refactoring & Best Practices
<!-- .element: class="fragment" data-fragment-index="7" -->
* Q&A und Diskussion
<!-- .element: class="fragment" data-fragment-index="8" -->

Note:
* Halten Sie sich an die Module - jedes Modul ist sorgfältig strukturiert
<!-- .element: class="fragment" -->
* Ermutigen Sie zur aktiven Teilnahme in den Hands-on-Phasen
<!-- .element: class="fragment" -->
* Builder Pattern ist oft das komplexeste - planen Sie mehr Diskussion dafür
<!-- .element: class="fragment" -->
* Praxisnahe Refactoring-Beispiele sind essentiell - lassen Sie ausreichend Raum für Diskussion
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Modul 1: Code-Analyse & Factory Method Pattern

## Lernziele
* Code-Smells in Legacy-Code identifizieren
<!-- .element: class="fragment" -->
* Factory Method Pattern verstehen und anwenden
<!-- .element: class="fragment" -->
* Refactoring-Strategien entwickeln
<!-- .element: class="fragment" -->
* Single Responsibility Principle praktisch umsetzen
<!-- .element: class="fragment" --> 

Note:
* Betonen Sie die praktische Relevanz für gewachsene Systemlandschaften
<!-- .element: class="fragment" -->
* Erklären Sie, dass wir mit Code-Smell Analyse beginnen, bevor wir Patterns einführen
<!-- .element: class="fragment" -->
* Interaktive Frage: "Welche Code-Probleme kennen Sie aus Ihren Projekten?"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Factory Method Pattern


## Was passt hier nicht?


Ein typisches Problem in gewachsenen Enterprise-Systemen: Die Kundenbetreuung muss verschiedene Kunden-Typen verwalten - Privatkunden, Geschäftskunden, Premium-Kunden.

---

## Was ist hier falsch?

<div class="problem-highlight">

#### Factory Method Pattern Problem
**Situation**: Die Kundenbetreuung muss verschiedene Kunden-Typen verwalten - Privatkunden, Geschäftskunden, Premium-Kunden.

**Was sehen Sie hier Problematisches?**

</div>

<div class="code-example">

```java
public class CustomerManager {
    
    public Customer createCustomer(String type, String name, String contractId) {
        // Code-Smell: Switch Statement / Long Parameter List
        switch (type) {
            case "PRIVATE":
                Customer privateCustomer = new Customer();
                privateCustomer.setName(name);
                privateCustomer.setContractId(contractId);
                privateCustomer.setTariffOptions(Arrays.asList("Basic", "Comfort"));
                privateCustomer.setPaymentMethod("SEPA");
                privateCustomer.setSupportLevel("Standard");
                return privateCustomer;
                
            case "BUSINESS":
                Customer businessCustomer = new Customer();
                businessCustomer.setName(name);
                businessCustomer.setContractId(contractId);
                businessCustomer.setTariffOptions(Arrays.asList("Professional", "Enterprise"));
                businessCustomer.setPaymentMethod("Invoice");
                businessCustomer.setSupportLevel("Priority");
                return businessCustomer;
                
            case "PREMIUM":
                Customer premiumCustomer = new Customer();
                // ... noch mehr Code-Duplikation
                return premiumCustomer;
                
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }
}
```

</div>

### Identifizierte Code-Smells

* **Long Method**: Jeder switch-case Block ist zu komplex
<!-- .element: class="fragment" -->
* **Switch Statement**: Typ-basierte Verzweigung verletzt Open/Closed Principle  
<!-- .element: class="fragment" -->
* **Duplicate Code**: Ähnliche Initialisierungsmuster in jedem Branch
<!-- .element: class="fragment" -->
* **Feature Envy**: Manipuliert mehr Customer-Daten als sie besitzt
<!-- .element: class="fragment" -->
* **Long Parameter List**: Viele Parameter machen Methode schwer verwendbar

---

## Factory Method Pattern - Die Lösung

<div class="pattern-definition">

#### Factory Method Pattern
**Zweck**: Das Factory Method Pattern definiert eine Schnittstelle zum Erstellen von Objekten, aber lässt Unterklassen entscheiden, welche Klasse instantiiert wird.

**Problem**: Objekte müssen erstellt werden, aber die Instanziierung soll an Subklassen übergeben werden.

**Lösung**: Ein Interface für die Objekt-Erstellung definieren, Subklassen entscheiden welche Klasse instanziiert wird.

</div>

### UML-Struktur


```
Creator (abstract)
  +factoryMethod(): Product
  +businessLogic()
  
ConcreteCreator extends Creator
  +factoryMethod(): ConcreteProduct
  
Product (interface)
  
ConcreteProduct implements Product
```


Note:
* Erklären Sie das Pattern-Konzept anhand des UML-Diagramms
<!-- .element: class="fragment" -->
* Betonen Sie den Unterschied zwischen Creator und Product-Hierarchie
<!-- .element: class="fragment" -->
* Wichtig: Factory Method ist NICHT Simple Factory - erklären Sie den Unterschied
<!-- .element: class="fragment" -->
* Verbindung zu unserem Customer-Problem herstellen
<!-- .element: class="fragment" -->
* Frage: "Wo könnten Sie dieses Pattern in Ihren Projekten einsetzen?"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

## Lösung

<div class="code-example">

```java
// Basis-Interface definieren
public interface Customer {
    String getName();
    String getContractId();
    List<String> getTariffOptions();
    String getPaymentMethod();
    String getSupportLevel();
    void processContract();
}
```


</div>

Note:
* Erklären Sie die Einführung des Customer-Interfaces als ersten Refactoring-Schritt
<!-- .element: class="fragment" -->
* Betonen Sie die gemeinsamen Methoden für alle Kunden-Typen
<!-- .element: class="fragment" -->
* Wichtig: processContract() ermöglicht kunden-spezifische Geschäftslogik
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

### Konkrete Implementierung - PrivateCustomer


<div class="code-example">

```java
public class PrivateCustomer implements Customer {
    private String name;
    private String contractId;
    private List<String> tariffOptions;
    
    public PrivateCustomer(String name, String contractId) {
        this.name = name;
        this.contractId = contractId;
        this.tariffOptions = Arrays.asList("Basic", "Comfort");
    }
    
    @Override
    public String getPaymentMethod() {
        return "SEPA";
    }
    
    @Override
    public String getSupportLevel() {
        return "Standard";
    }
    
    @Override
    public void processContract() {
        validatePersonalData();
        setupBasicServices();
    }
}
```


</div>

Note:
* Zeigen Sie, wie die kunden-spezifische Logik in der Implementierung gekapselt wird
<!-- .element: class="fragment" -->
* Erklären Sie die private Methoden validatePersonalData() und setupBasicServices()
<!-- .element: class="fragment" -->
* Betonen Sie die Kapselung der Geschäftsregeln pro Kunden-Typ
<!-- .element: class="fragment" -->
* Frage: "Welche Geschäftsregeln würden Sie für BusinessCustomer erwarten?"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Factory Method Creator - Template Method Integration

<div class="code-example">

```java
public abstract class CustomerFactory {
    
    // Factory Method - zu implementieren von Subklassen
    protected abstract Customer createCustomer(String name, String contractId);
    
    // Template Method - verwendet Factory Method
    public Customer processNewCustomer(String name, String contractId) {
        Customer customer = createCustomer(name, contractId);
        
        // Gemeinsame Geschäftslogik
        validateContract(customer);
        persistCustomer(customer);
        sendWelcomeMessage(customer);
        
        return customer;
    }
    
    private void validateContract(Customer customer) {
        // Allgemeine Vertragsprüfung
    }
    
    private void persistCustomer(Customer customer) {
        // Kunde in Datenbank speichern
    }
    
    private void sendWelcomeMessage(Customer customer) {
        // Willkommensnachricht senden
    }
}
```


</div>

Note:
* Erklären Sie die Kombination von Factory Method und Template Method Pattern
<!-- .element: class="fragment" -->
* Betonen Sie die gemeinsame Geschäftslogik im Template Method
<!-- .element: class="fragment" -->
* Wichtig: Factory Method ist protected, nicht public
<!-- .element: class="fragment" -->
* Zeigen Sie den Aufruf-Flow: processNewCustomer() -> createCustomer() -> validate -> persist
<!-- .element: class="fragment" -->
* Frage: "Welche Geschäftslogik würden Sie als gemeinsam identifizieren?"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Konkrete Factory-Implementierungen

<div class="code-example">

```java
public class PrivateCustomerFactory extends CustomerFactory {
    @Override
    protected Customer createCustomer(String name, String contractId) {
        return new PrivateCustomer(name, contractId);
    }
}

public class BusinessCustomerFactory extends CustomerFactory {
    @Override
    protected Customer createCustomer(String name, String contractId) {
        return new BusinessCustomer(name, contractId);
    }
}

public class PremiumCustomerFactory extends CustomerFactory {
    @Override
    protected Customer createCustomer(String name, String contractId) {
        return new PremiumCustomer(name, contractId);
    }
}
```


</div>

Note:
* Betonen Sie die Einfachheit der konkreten Factory-Implementierungen
<!-- .element: class="fragment" -->
* Erklären Sie: Nur die Factory Method muss implementiert werden
<!-- .element: class="fragment" -->
* Neue Kunden-Typen = neue Factory-Klasse, kein bestehender Code geändert
<!-- .element: class="fragment" -->
* Verbindung zu Open/Closed Principle
<!-- .element: class="fragment" -->
* Praktische Übung vorbereiten: "Implementieren Sie StudentCustomerFactory"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

## Übung

<div class="interactive-question">

### Praktische Übung: Factory Method


#### Implementieren Sie einen StudentCustomerFactory


</div>

**Aufgabe**: Erweitern Sie das Customer-System um einen StudentCustomer mit:
* Spezielle Tarif-Optionen ("Student Basic", "Student Plus")
<!-- .element: class="fragment" -->
* Studentenausweis-Validierung
<!-- .element: class="fragment" -->
* Ermäßigte Konditionen

**Implementierungsschritte**:
1. StudentCustomer-Klasse erstellen
2. StudentCustomerFactory implementieren
3. Integration in bestehende Architektur

---

# SOLID-Prinzipien Integration

<div class="two-column">
<div>

## Single Responsibility Principle (SRP)
* Jede Factory-Klasse ist nur für einen Kunden-Typ zuständig 
<!-- .element: class="fragment" -->
* Customer-Klassen kapseln ihre spezifische Geschäftslogik 

## Open/Closed Principle (OCP)
* Neue Kunden-Typen durch neue Factory-Klassen hinzufügbar 
<!-- .element: class="fragment" -->
* Bestehender Code muss nicht geändert werden 

</div>
<div>

## Dependency Inversion Principle (DIP)
* Abhängigkeit zu Customer-Interface, nicht zu konkreten Implementierungen 

## Moderne Alternative - Java 8+


```java
public enum CustomerType {
    PRIVATE(PrivateCustomer::new),
    BUSINESS(BusinessCustomer::new),
    PREMIUM(PremiumCustomer::new);
    
    private final BiFunction<String, String, Customer> factory;
    
    public Customer create(String name, String contractId) {
        return factory.apply(name, contractId);
    }
}
```


</div>
</div>

Note:
* Verbinden Sie Factory Method Pattern explizit zu SOLID-Prinzipien
<!-- .element: class="fragment" -->
* SRP: Jede Klasse hat genau eine Verantwortung
<!-- .element: class="fragment" -->
* OCP: Erweiterung ohne Modifikation durch neue Factory-Klassen
<!-- .element: class="fragment" -->
* DIP: Abhängigkeit zu Abstraktion, nicht zu konkreten Klassen
<!-- .element: class="fragment" -->
* Moderne Alternative mit Java 8+: Zeigen Sie funktionale Ansätze
<!-- .element: class="fragment" -->
* Diskussion: "Welche SOLID-Prinzipien verletzt unser ursprünglicher Code?"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Modul 2: Abstract Factory & Layered Architecture

## Lernziele
* Abstract Factory Pattern in geschichteten Architekturen anwenden 
<!-- .element: class="fragment" -->
* Service-Familien sauber strukturieren 
<!-- .element: class="fragment" -->
* Dependency Injection Prinzipien verstehen 
<!-- .element: class="fragment" -->
* Interface Segregation in der Praxis umsetzen 

Note:
* Übergang von Factory Method zu Abstract Factory erklären
<!-- .element: class="fragment" -->
* Betonen Sie den Unterschied: Familien verwandter Objekte
<!-- .element: class="fragment" -->
* Verbindung zu Enterprise-Architekturen: Service-Layers
<!-- .element: class="fragment" -->
* Motivation schaffen: "Warum reicht Factory Method nicht für komplexe Systeme?"
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Abstract Factory Pattern


## Was passt hier nicht?


Das Unternehmen betreibt verschiedene Service-Kanäle (Web, Mobile App, Call Center, Partner-Portal). Die aktuelle Implementierung hat komplexe Herausforderungen:

---

# Problematischer Code - Service Manager

<div class="code-example">

```java
public class ServiceManager {
    
    public void setupWebChannel() {
        // Code-Smell: Hardcodierte Konfiguration
        AuthenticationService auth = new WebSessionAuthService();
        CustomerService customer = new WebCustomerService();
        BillingService billing = new WebBillingService();
        NotificationService notification = new WebNotificationService();
        
        // Problem: Services passen nicht zusammen!
        customer.setAuthService(auth); // WebCustomer braucht WebAuth
        billing.setNotificationService(notification);
    }
    
    public void setupMobileChannel() {
        // DUPLICATE CODE: Ähnliche Struktur, andere Services
        AuthenticationService auth = new OAuthAuthenticationService();
        CustomerService customer = new MobileCustomerService();
        BillingService billing = new MobileBillingService();
        // FEHLER: Falsche Kombination!
        NotificationService notification = new WebNotificationService(); // <- Falsch!
    }
}
```

</div>

---

## Identifizierte Code-Smells

* **Service-Familie Inkonsistenz**: Gemischte Service-Implementierungen 
<!-- .element: class="fragment" -->
* **Duplicate Code**: Ähnliche Setup-Logik für jeden Kanal 
<!-- .element: class="fragment" -->
* **Tight Coupling**: Direkte Abhängigkeiten zu konkreten Klassen 
<!-- .element: class="fragment" -->
* **Fehlende Konsistenz-Garantie**: Keine Gewähr für kompatible Services 
<!-- .element: class="fragment" -->
* **Mixed Concerns**: Service-Erstellung vermischt mit Business-Logik 

---

# Layered Architecture Integration

### Schichtenmodell


```
┌─────────────────────────────────────┐
│        Presentation Layer           │ <- Channel-spezifische Controller
├─────────────────────────────────────┤
│         Business Layer              │ <- Channel-agnostische Geschäftslogik
├─────────────────────────────────────┤
│        Integration Layer            │ <- Abstract Factory Pattern
├─────────────────────────────────────┤
│         Data Access Layer           │ <- Channel-spezifische Implementierungen
└─────────────────────────────────────┘
```


---

## Was ist hier falsch?

<div class="problem-highlight">

#### Abstract Factory Pattern Problem
**Situation**: Das Unternehmen betreibt verschiedene Service-Kanäle (Web, Mobile App, Call Center, Partner-Portal). Die aktuelle Implementierung weist strukturelle Herausforderungen auf.

**Was sehen Sie hier Problematisches?**

</div>

<div class="code-example">

```java
public class ServiceManager {
    
    public void setupWebChannel() {
        // Code-Smell: Hardcodierte Konfiguration
        AuthenticationService auth = new WebSessionAuthService();
        CustomerService customer = new WebCustomerService();
        BillingService billing = new WebBillingService();
        NotificationService notification = new WebNotificationService();
        
        // Problem: Services passen nicht zusammen!
        customer.setAuthService(auth); // WebCustomer braucht WebAuth
        billing.setNotificationService(notification);
    }
    
    public void setupMobileChannel() {
        // DUPLICATE CODE: Ähnliche Struktur, andere Services
        AuthenticationService auth = new OAuthAuthenticationService();
        CustomerService customer = new MobileCustomerService();
        BillingService billing = new MobileBillingService();
        // FEHLER: Falsche Kombination!
        NotificationService notification = new WebNotificationService(); // <- Falsch!
    }
}
```

</div>

### Identifizierte Code-Smells

* **Service-Familie Inkonsistenz**: Gemischte Service-Implementierungen
<!-- .element: class="fragment" -->
* **Duplicate Code**: Ähnliche Setup-Logik für jeden Kanal
<!-- .element: class="fragment" -->
* **Tight Coupling**: Direkte Abhängigkeiten zu konkreten Klassen
<!-- .element: class="fragment" -->
* **Fehlende Konsistenz-Garantie**: Keine Gewähr für kompatible Services
<!-- .element: class="fragment" -->
* **Mixed Concerns**: Service-Erstellung vermischt mit Business-Logik

---

## Abstract Factory Pattern - Die Lösung

<div class="pattern-definition">

#### Abstract Factory Pattern
**Zweck**: Abstract Factory stellt eine Schnittstelle bereit, um Familien verwandter Objekte zu erstellen, ohne deren konkrete Klassen zu spezifizieren.

**Problem**: Service-Familien müssen konsistent erstellt werden, verschiedene Implementierungen für unterschiedliche Kanäle.

**Lösung**: Factory-Interface für Service-Familie, konkrete Factories für jeden Kanal.

</div>

### UML-Struktur


```
AbstractFactory
  +createServiceA(): AbstractServiceA
  +createServiceB(): AbstractServiceB

ConcreteFactory1 extends AbstractFactory
  +createServiceA(): ConcreteServiceA1
  +createServiceB(): ConcreteServiceB1

ConcreteFactory2 extends AbstractFactory  
  +createServiceA(): ConcreteServiceA2
  +createServiceB(): ConcreteServiceB2
```


---

# Service-Abstrakte definieren

<div class="code-example">

```java
// Authentication Service Familie
public interface AuthenticationService {
    boolean authenticate(AuthenticationContext context);
    void logout(String sessionId);
}

// Customer Service Familie  
public interface CustomerService {
    CustomerData getCustomer(String customerId);
    List<CustomerData> searchCustomers(SearchCriteria criteria);
    void updateCustomer(CustomerData customer);
}

// Billing Service Familie
public interface BillingService {
    BillingInfo getBillingInfo(String customerId);
    List<Invoice> getInvoices(String customerId, DateRange range);
}

// Notification Service Familie
public interface NotificationService {
    void sendNotification(String customerId, String message);
    boolean isChannelAvailable(String customerId);
}
```


</div>

---

# Abstract Factory Definition

<div class="code-example">

```java
public abstract class ChannelServiceFactory {
    
    // Factory Methods für Service-Familie
    public abstract AuthenticationService createAuthenticationService();
    public abstract CustomerService createCustomerService();
    public abstract BillingService createBillingService();
    public abstract NotificationService createNotificationService();
    
    // Convenience Method für komplette Service-Suite
    public ChannelServiceSuite createServiceSuite() {
        return new ChannelServiceSuite(
            createAuthenticationService(),
            createCustomerService(),
            createBillingService(),
            createNotificationService()
        );
    }
}
```


</div>

---

# Konkrete Factory - Web Channel

<div class="code-example">

```java
public class WebChannelFactory extends ChannelServiceFactory {
    
    @Override
    public AuthenticationService createAuthenticationService() {
        return new WebSessionAuthService();
    }
    
    @Override
    public CustomerService createCustomerService() {
        return new WebCustomerService();
    }
    
    @Override
    public BillingService createBillingService() {
        return new WebBillingService();
    }
    
    @Override
    public NotificationService createNotificationService() {
        return new WebNotificationService();
    }
}

public class MobileChannelFactory extends ChannelServiceFactory {
    
    @Override
    public AuthenticationService createAuthenticationService() {
        return new OAuthAuthenticationService();
    }
    
    @Override
    public CustomerService createCustomerService() {
        return new MobileCustomerService();
    }
    
    // ... weitere Service-Implementierungen
}
```

</div>

---

## Lösung

<div class="code-example">

```java
public class ChannelServiceSuite {
    private final AuthenticationService authService;
    private final CustomerService customerService;
    private final BillingService billingService;
    private final NotificationService notificationService;
    
    public ChannelServiceSuite(AuthenticationService authService,
                              CustomerService customerService,
                              BillingService billingService,
                              NotificationService notificationService) {
        this.authService = authService;
        this.customerService = customerService;
        this.billingService = billingService;
        this.notificationService = notificationService;
    }
    
    // Geschäftslogik-Methoden die Service-Familie nutzen
    public CustomerProfile getCompleteCustomerProfile(String customerId, 
                                                     AuthenticationContext auth) {
        // 1. Authentifizierung
        if (!authService.authenticate(auth)) {
            throw new AuthenticationException("Authentication failed");
        }
        
        // 2. Kundendaten laden
        CustomerData customer = customerService.getCustomer(customerId);
        
        // 3. Billing-Info hinzufügen
        BillingInfo billing = billingService.getBillingInfo(customerId);
        
        // 4. Benachrichtigungsstatus prüfen
        boolean notificationsEnabled = notificationService.isChannelAvailable(customerId);
        
        return new CustomerProfile(customer, billing, notificationsEnabled);
    }
}
```

</div>

---

## Übung

<div class="interactive-question">

### Praktische Übung: Abstract Factory
#### Implementieren Sie eine CallCenterChannelFactory

</div>

**Aufgabe**: Erweitern Sie das Multi-Channel-System um Call Center Support:
* CallCenterAuthenticationService (PIN-basiert)
<!-- .element: class="fragment" -->
* CallCenterCustomerService (vereinfachte Daten)
<!-- .element: class="fragment" -->
* CallCenterBillingService (nur Grundinformationen)
<!-- .element: class="fragment" -->
* CallCenterNotificationService (Telefon + SMS)

**Implementierungsschritte**:
1. Konkrete Service-Implementierungen erstellen
2. CallCenterChannelFactory implementieren
3. Integration in ChannelFactoryProvider
4. Konsistenz-Tests durchführen

---

# Dependency Injection Integration

<div class="code-example">

```java
@Component
public class ChannelFactoryProvider {
    
    private final Map<String, ChannelServiceFactory> factories;
    
    public ChannelFactoryProvider() {
        factories = new HashMap<>();
        factories.put("WEB", new WebChannelFactory());
        factories.put("MOBILE", new MobileChannelFactory());
        factories.put("CALLCENTER", new CallCenterChannelFactory());
        factories.put("PARTNER", new PartnerChannelFactory());
    }
    
    public ChannelServiceFactory getFactory(String channel) {
        ChannelServiceFactory factory = factories.get(channel);
        if (factory == null) {
            throw new UnsupportedChannelException("Channel not supported: " + channel);
        }
        return factory;
    }
    
    public Set<String> getSupportedChannels() {
        return factories.keySet();
    }
}
```

</div>

---

# Modul 3: Builder Pattern & Repository Integration

## Lernziele
* Builder Pattern für komplexe Objekterstellung meistern 
<!-- .element: class="fragment" -->
* Repository Pattern mit Builder kombinieren 
<!-- .element: class="fragment" -->
* Fluent Interfaces designen und umsetzen 
<!-- .element: class="fragment" -->
* Liskov Substitution Principle praktisch anwenden 

Note:
* Builder Pattern löst das "Telescoping Constructor" Problem
<!-- .element: class="fragment" -->
* Besonders wertvoll für Query-Builder und komplexe Konfigurationsobjekte
<!-- .element: class="fragment" -->
* Zeigen Sie den Unterschied zu Factory Method: Builder für EINEN komplexen Typ
<!-- .element: class="fragment" -->
* Frage: "Kennen Sie Konstruktoren mit vielen Parametern in Ihren Projekten?"
<!-- .element: class="fragment" -->
* Verbindung zu Repository Pattern: Saubere Query-Erstellung
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Builder Pattern


## Was passt hier nicht?


Enterprise-Systeme arbeiten mit komplexen Datenbankabfragen - Kunden können nach vielen Kriterien gefiltert werden. Die aktuelle Implementierung weist Wartungsherausforderungen auf:

---

# Problematischer Code - Customer Repository

<div class="code-example">

```java
public class CustomerRepository {
    
    // Code-Smell: Telescoping Constructor
    public List<Customer> findCustomers(String name, String contractType, 
                                       String tariff, Date contractStart, 
                                       Date contractEnd, String city, 
                                       String postalCode, Boolean isActive,
                                       String paymentMethod, Integer minRevenue,
                                       String sortBy, String sortOrder,
                                       Integer limit, Integer offset) {
        
        // Code-Smell: Complex Method mit StringBuilder-Chaos
        StringBuilder sql = new StringBuilder("SELECT * FROM customers c ");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;
        
        if (name != null && !name.isEmpty()) {
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("c.name LIKE ?");
            params.add("%" + name + "%");
            hasWhere = true;
        }
        
        if (contractType != null && !contractType.isEmpty()) {
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("c.contract_type = ?");
            params.add(contractType);
            hasWhere = true;
        }
        
        // ... weitere 20 Zeilen if-Statements
        
        return jdbcTemplate.query(sql.toString(), params.toArray(), customerRowMapper);
    }
}
```

</div>

---

## Identifizierte Code-Smells

* **Telescoping Constructor**: 14 Parameter machen die Methode unverwendbar 
<!-- .element: class="fragment" -->
* **Long Method**: 30+ Zeilen nur für SQL-String-Erstellung 
<!-- .element: class="fragment" -->
* **Duplicate Code**: hasWhere-Logik wird überall wiederholt 
<!-- .element: class="fragment" -->
* **Complex Conditional**: Verschachtelte if-Statements schwer lesbar 
<!-- .element: class="fragment" -->
* **String Concatenation**: SQL-Injection Risiko und schwer zu testen 

### Was passiert bei neuen Suchkriterien?

* **Signature-Breaking**: Alle Aufrufe müssen angepasst werden
<!-- .element: class="fragment" -->
* **Maintenance**: Mehr if-Blöcke, noch komplexere Logik
<!-- .element: class="fragment" -->
* **Testing**: Exponentiell wachsende Kombinationen

---

## Was ist hier falsch?

<div class="problem-highlight">

#### Builder Pattern Problem
**Situation**: Enterprise-Systeme arbeiten mit komplexen Datenbankabfragen - Kunden können nach vielen Kriterien gefiltert werden. Die aktuelle Implementierung weist strukturelle Herausforderungen auf.

**Was sehen Sie hier Problematisches?**

</div>

<div class="code-example">

```java
public class CustomerRepository {
    
    // Code-Smell: Telescoping Constructor
    public List<Customer> findCustomers(String name, String contractType, 
                                       String tariff, Date contractStart, 
                                       Date contractEnd, String city, 
                                       String postalCode, Boolean isActive,
                                       String paymentMethod, Integer minRevenue,
                                       String sortBy, String sortOrder,
                                       Integer limit, Integer offset) {
        
        // Code-Smell: Complex Method mit StringBuilder-Chaos
        StringBuilder sql = new StringBuilder("SELECT * FROM customers c ");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;
        
        if (name != null && !name.isEmpty()) {
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("c.name LIKE ?");
            params.add("%" + name + "%");
            hasWhere = true;
        }
        
        if (contractType != null && !contractType.isEmpty()) {
            sql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("c.contract_type = ?");
            params.add(contractType);
            hasWhere = true;
        }
        
        // ... weitere 20 Zeilen if-Statements
        
        return jdbcTemplate.query(sql.toString(), params.toArray(), customerRowMapper);
    }
}
```

</div>

### Identifizierte Code-Smells

* **Telescoping Constructor**: 14 Parameter machen die Methode unverwendbar
<!-- .element: class="fragment" -->
* **Long Method**: 30+ Zeilen nur für SQL-String-Erstellung
<!-- .element: class="fragment" -->
* **Duplicate Code**: hasWhere-Logik wird überall wiederholt
<!-- .element: class="fragment" -->
* **Complex Conditional**: Verschachtelte if-Statements schwer lesbar
<!-- .element: class="fragment" -->
* **String Concatenation**: SQL-Injection Risiko und schwer zu testen

---

## Builder Pattern - Die Lösung

<div class="pattern-definition">

#### Builder Pattern
**Zweck**: Builder Pattern trennt die Konstruktion komplexer Objekte von deren Repräsentation, so dass der gleiche Konstruktionsprozess verschiedene Darstellungen erstellen kann.

**Problem**: Komplexe Objekte mit vielen optionalen Parametern müssen erstellt werden.

**Lösung**: Step-by-step Konstruktion mit Fluent Interface.

</div>

### UML-Struktur


```
Director
  +construct(): Product
  
Builder (interface)
  +buildPartA()
  +buildPartB()
  +getResult(): Product
  
ConcreteBuilder implements Builder
  +buildPartA()
  +buildPartB()
  +getResult(): ConcreteProduct
```


---

## Lösung

<div class="code-example">

```java
public class CustomerQueryBuilder {
    private final StringBuilder query = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();
    private boolean hasWhere = false;
    private boolean hasJoins = false;
    
    public CustomerQueryBuilder() {
        query.append("SELECT c.* FROM customers c");
    }
    
    public CustomerQueryBuilder withName(String name) {
        if (name != null && !name.isEmpty()) {
            addWhereClause("c.name LIKE ?");
            parameters.add("%" + name + "%");
        }
        return this;
    }
    
    public CustomerQueryBuilder withContractType(String contractType) {
        if (contractType != null && !contractType.isEmpty()) {
            addWhereClause("c.contract_type = ?");
            parameters.add(contractType);
        }
        return this;
    }
    
    public CustomerQueryBuilder withTariff(String tariff) {
        if (tariff != null && !tariff.isEmpty()) {
            ensureTariffJoin();
            addWhereClause("t.name = ?");
            parameters.add(tariff);
        }
        return this;
    }
    
    public CustomerQueryBuilder withActiveStatus(Boolean isActive) {
        if (isActive != null) {
            addWhereClause("c.is_active = ?");
            parameters.add(isActive);
        }
        return this;
    }
    
    public CustomerQueryBuilder sortBy(String field, SortOrder order) {
        if (field != null) {
            query.append(" ORDER BY ").append(field);
            if (order != null) {
                query.append(" ").append(order.name());
            }
        }
        return this;
    }
    
    public CustomerQueryBuilder limit(int limit, int offset) {
        query.append(" LIMIT ").append(limit);
        if (offset > 0) {
            query.append(" OFFSET ").append(offset);
        }
        return this;
    }
    
    private void addWhereClause(String condition) {
        query.append(hasWhere ? " AND " : " WHERE ");
        query.append(condition);
        hasWhere = true;
    }
    
    private void ensureTariffJoin() {
        if (!hasJoins) {
            query.append(" JOIN tariffs t ON c.tariff_id = t.id");
            hasJoins = true;
        }
    }
    
    public Query build() {
        return new Query(query.toString(), parameters);
    }
}
```


</div>

---

# Repository mit Builder Integration

<div class="code-example">

```java
@Repository
public class CustomerRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Customer> customerRowMapper;
    
    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = new CustomerRowMapper();
    }
    
    public List<Customer> findCustomers(CustomerSearchCriteria criteria) {
        Query query = createQueryBuilder()
            .withName(criteria.getName())
            .withContractType(criteria.getContractType())
            .withTariff(criteria.getTariff())
            .withActiveStatus(criteria.getIsActive())
            .withDateRange(criteria.getContractStart(), criteria.getContractEnd())
            .withLocation(criteria.getCity(), criteria.getPostalCode())
            .withRevenue(criteria.getMinRevenue())
            .sortBy(criteria.getSortBy(), criteria.getSortOrder())
            .limit(criteria.getLimit(), criteria.getOffset())
            .build();
        
        return jdbcTemplate.query(query.getSql(), 
                                 query.getParameters().toArray(), 
                                 customerRowMapper);
    }
    
    public CustomerQueryBuilder createQueryBuilder() {
        return new CustomerQueryBuilder();
    }
    
    // Fluent Interface für direkte Nutzung
    public List<Customer> findActivePrivateCustomers() {
        return findCustomers(createQueryBuilder()
            .withContractType("PRIVATE")
            .withActiveStatus(true)
            .sortBy("name", SortOrder.ASC)
            .limit(100, 0)
            .build());
    }
}
```

</div>

---

# Fluent Interface - Advanced Features

<div class="code-example">

```java
// Type-Safe Builder mit Method Chaining
public class TypeSafeCustomerQueryBuilder {
    
    public NameStage byName(String name) {
        return new CustomerQueryBuilderImpl().withName(name);
    }
    
    public interface NameStage {
        ContractStage withContractType(String contractType);
        QueryStage build();
    }
    
    public interface ContractStage {
        TariffStage withTariff(String tariff);
        QueryStage build();
    }
    
    public interface TariffStage {
        SortStage sortBy(String field, SortOrder order);
        QueryStage build();
    }
    
    public interface SortStage {
        QueryStage limit(int limit, int offset);
        QueryStage build();
    }
    
    public interface QueryStage {
        Query build();
    }
}

// Usage - verhindert ungültige Query-Kombinationen
Query query = new TypeSafeCustomerQueryBuilder()
    .byName("Müller")
    .withContractType("BUSINESS")
    .withTariff("Professional")
    .sortBy("contract_date", SortOrder.DESC)
    .limit(50, 0)
    .build();
```

</div>

---

## Übung

<div class="interactive-question">

### Praktische Übung: Builder Pattern
#### Implementieren Sie einen TariffQueryBuilder

</div>

**Aufgabe**: Erweitern Sie das Query-System um Tariff-spezifische Abfragen:
* TariffQueryBuilder mit Fluent Interface
<!-- .element: class="fragment" -->
* Unterstützung für: Tariff-Name, Preis-Range, Features, Verfügbarkeit
<!-- .element: class="fragment" -->
* Integration in TariffRepository
<!-- .element: class="fragment" -->
* Type-Safe Builder Implementation

**Implementierungsschritte**:
1. TariffQueryBuilder-Klasse erstellen
2. Fluent Methods implementieren (withName, withPriceRange, withFeatures)
3. TariffRepository-Integration
4. Type-Safe Builder-Interfaces definieren

---

# Modul 4: Prototype Pattern & Configuration Cloning

## Lernziele
* Prototype Pattern für kostspielige Objekterzeugung verstehen 
<!-- .element: class="fragment" -->
* Deep vs. Shallow Copy Problematik meistern 
<!-- .element: class="fragment" -->
* Configuration-Cloning für komplexe Enterprise-Settings 
<!-- .element: class="fragment" -->
* Performance-Optimierung durch intelligente Objektkopierung 

Note:
* Prototype Pattern ist performance-kritisch bei teuren Objekten
<!-- .element: class="fragment" -->
* Typische Anwendung: Konfigurationsobjekte, DatabaseConnections, Parser-Zustände
<!-- .element: class="fragment" -->
* WICHTIG: Deep vs Shallow Copy - zeigen Sie die Fallstricke auf
<!-- .element: class="fragment" -->
* Frage: "Wo in Ihren Systemen ist Objekterstellung besonders teuer?"
<!-- .element: class="fragment" -->
* Performance-Aspekt betonen: Wann ist clonen() schneller als new?
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

---

# Prototype Pattern


## Was passt hier nicht?


In Enterprise-Umgebungen müssen häufig ähnliche, aber leicht unterschiedliche Service-Konfigurationen erstellt werden. Die aktuelle Implementierung verschwendet massive Ressourcen:

---

# Problematischer Code - Configuration Manager

<div class="code-example">

```java
@Service
public class ServiceConfigurationManager {
    
    // Anti-Pattern: Expensive Recreation für ähnliche Konfigurationen
    public ServiceConfiguration createDevConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // Expensive Database Lookups für Defaults (200ms+)
        config.setDatabaseSettings(loadDatabaseDefaults());
        config.setSecuritySettings(loadSecurityDefaults());
        config.setCachingSettings(loadCachingDefaults());
        
        // Expensive SSL Certificate Validation (500ms+)
        config.setSslCertificate(validateAndLoadSslCert("dev-cert.pem"));
        
        // Expensive Service Discovery (300ms+)
        config.setServiceEndpoints(discoverAvailableServices());
        
        // Environment-specific anpassungen
        config.setEnvironment("DEV");
        config.setLogLevel("DEBUG");
        config.setDatabaseUrl("dev-database-url");
        
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // DUPLICATE: Gleiche expensive Operations!
        config.setDatabaseSettings(loadDatabaseDefaults());    // 200ms
        config.setSecuritySettings(loadSecurityDefaults());    // 200ms
        config.setCachingSettings(loadCachingDefaults());      // 200ms
        config.setSslCertificate(validateAndLoadSslCert("test-cert.pem")); // 500ms
        config.setServiceEndpoints(discoverAvailableServices()); // 300ms
        
        // Nur diese Werte sind unterschiedlich
        config.setEnvironment("TEST");
        config.setLogLevel("INFO");
        config.setDatabaseUrl("test-database-url");
        
        return config;
    }
}
```

</div>

**Performance-Problem**: Jede Konfigurationserstellung dauert 1.4+ Sekunden für identische Operationen!

---

## Identifizierte Code-Smells

* **Expensive Recreation**: Identische teure Operationen werden wiederholt 
<!-- .element: class="fragment" -->
* **Duplicate Code**: 90% der Konfigurationserstellung ist identisch 
<!-- .element: class="fragment" -->
* **Resource Waste**: 1.4s für jede neue Konfiguration (nur für 3 Unterschiede!) 
<!-- .element: class="fragment" -->
* **Method Duplication**: createDevConfiguration und createTestConfiguration nahezu identisch 
<!-- .element: class="fragment" -->
* **Missing Abstraction**: Keine Wiederverwendung der teuren Initialisierungslogik 

### Performance-Impact Analysis

* **Database Lookups**: 3x 200ms = 600ms
<!-- .element: class="fragment" -->
* **SSL Validation**: 500ms
<!-- .element: class="fragment" -->
* **Service Discovery**: 300ms
<!-- .element: class="fragment" -->
* **Total per Config**: 1400ms für 99% identische Arbeit

---

## Was ist hier falsch?

<div class="problem-highlight">

#### Prototype Pattern Problem
**Situation**: In Enterprise-Umgebungen müssen häufig ähnliche, aber leicht unterschiedliche Service-Konfigurationen erstellt werden. Die aktuelle Implementierung verschwendet massive Ressourcen.

**Was sehen Sie hier Problematisches?**

</div>

<div class="code-example">

```java
@Service
public class ServiceConfigurationManager {
    
    // Anti-Pattern: Expensive Recreation für ähnliche Konfigurationen
    public ServiceConfiguration createDevConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // Expensive Database Lookups für Defaults (200ms+)
        config.setDatabaseSettings(loadDatabaseDefaults());
        config.setSecuritySettings(loadSecurityDefaults());
        config.setCachingSettings(loadCachingDefaults());
        
        // Expensive SSL Certificate Validation (500ms+)
        config.setSslCertificate(validateAndLoadSslCert("dev-cert.pem"));
        
        // Expensive Service Discovery (300ms+)
        config.setServiceEndpoints(discoverAvailableServices());
        
        // Environment-specific Anpassungen
        config.setEnvironment("DEV");
        config.setLogLevel("DEBUG");
        config.setDatabaseUrl("dev-database-url");
        
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // DUPLICATE: Gleiche expensive Operations!
        config.setDatabaseSettings(loadDatabaseDefaults());    // 200ms
        config.setSecuritySettings(loadSecurityDefaults());    // 200ms
        config.setCachingSettings(loadCachingDefaults());      // 200ms
        config.setSslCertificate(validateAndLoadSslCert("test-cert.pem")); // 500ms
        config.setServiceEndpoints(discoverAvailableServices()); // 300ms
        
        // Nur diese Werte sind unterschiedlich
        config.setEnvironment("TEST");
        config.setLogLevel("INFO");
        config.setDatabaseUrl("test-database-url");
        
        return config;
    }
}
```

</div>

### Identifizierte Code-Smells

* **Expensive Recreation**: Identische teure Operationen werden wiederholt
<!-- .element: class="fragment" -->
* **Duplicate Code**: 90% der Konfigurationserstellung ist identisch
<!-- .element: class="fragment" -->
* **Resource Waste**: 1.4s für jede neue Konfiguration (nur für 3 Unterschiede!)
<!-- .element: class="fragment" -->
* **Method Duplication**: createDevConfiguration und createTestConfiguration nahezu identisch
<!-- .element: class="fragment" -->
* **Missing Abstraction**: Keine Wiederverwendung der teuren Initialisierungslogik

**Performance-Problem**: Jede Konfigurationserstellung dauert 1.4+ Sekunden für identische Operationen!

---

## Prototype Pattern - Die Lösung

<div class="pattern-definition">

#### Prototype Pattern
**Zweck**: Das Prototype Pattern erstellt neue Objekte durch Klonen eines prototypischen Objekts, statt durch normale Instanziierung.

**Problem**: Kostspielige Objekterzeugung soll vermieden werden, ähnliche Objekte benötigt.

**Lösung**: Clone-Interface implementieren, Prototyp-Registry für Template-Objekte.

</div>

### UML-Struktur


```
Prototype (interface)
  +clone(): Prototype
  
ConcretePrototype implements Prototype
  +clone(): ConcretePrototype
  
PrototypeRegistry
  +getPrototype(String key): Prototype
  +setPrototype(String key, Prototype prototype)
```


---

## Lösung

<div class="code-example">

```java
public class ServiceConfiguration implements Cloneable {
    
    // Expensive-to-create Objects
    private DatabaseSettings databaseSettings;
    private SecuritySettings securitySettings;
    private CachingSettings cachingSettings;
    private SSLCertificate sslCertificate;
    private List<ServiceEndpoint> serviceEndpoints;
    
    // Environment-specific Properties (cheap to change)
    private String environment;
    private String logLevel;
    private String databaseUrl;
    private Map<String, String> customProperties;
    
    // Deep Clone Implementation
    @Override
    public ServiceConfiguration clone() {
        try {
            ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
            
            // Deep copy expensive objects (immutable ones can be shared)
            cloned.databaseSettings = this.databaseSettings.clone();
            cloned.securitySettings = this.securitySettings.clone();
            cloned.cachingSettings = this.cachingSettings.clone();
            
            // SSL Certificate kann geshared werden (immutable)
            cloned.sslCertificate = this.sslCertificate; // Shallow copy OK
            
            // Service Endpoints deep copy (können sich ändern)
            cloned.serviceEndpoints = new ArrayList<>();
            for (ServiceEndpoint endpoint : this.serviceEndpoints) {
                cloned.serviceEndpoints.add(endpoint.clone());
            }
            
            // Environment properties - neue Map erstellen
            cloned.customProperties = new HashMap<>(this.customProperties);
            
            return cloned;
            
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Fluent Interface für Environment-specific Changes
    public ServiceConfiguration withEnvironment(String environment) {
        this.environment = environment;
        return this;
    }
    
    public ServiceConfiguration withLogLevel(String logLevel) {
        this.logLevel = logLevel;
        return this;
    }
    
    public ServiceConfiguration withDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        return this;
    }
    
    public ServiceConfiguration withCustomProperty(String key, String value) {
        this.customProperties.put(key, value);
        return this;
    }
}
```

</div>

---

# Configuration Prototype Registry

<div class="code-example">

```java
@Component
public class ConfigurationPrototypeRegistry {
    
    private final Map<String, ServiceConfiguration> prototypes = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;
    
    @PostConstruct
    public void initializePrototypes() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    createBasePrototype();
                    initialized = true;
                }
            }
        }
    }
    
    private void createBasePrototype() {
        ServiceConfiguration baseConfig = new ServiceConfiguration();
        
        // Expensive one-time initialization
        baseConfig.setDatabaseSettings(loadDatabaseDefaults());      // 200ms
        baseConfig.setSecuritySettings(loadSecurityDefaults());      // 200ms
        baseConfig.setCachingSettings(loadCachingDefaults());        // 200ms
        baseConfig.setSslCertificate(validateAndLoadSslCert());      // 500ms
        baseConfig.setServiceEndpoints(discoverAvailableServices()); // 300ms
        
        prototypes.put("BASE", baseConfig);
    }
    
    public ServiceConfiguration getPrototype(String key) {
        ServiceConfiguration prototype = prototypes.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found: " + key);
        }
        return prototype.clone();
    }
    
    public void registerPrototype(String key, ServiceConfiguration prototype) {
        prototypes.put(key, prototype);
    }
    
    public Set<String> getAvailablePrototypes() {
        return prototypes.keySet();
    }
}
```

</div>

---

# Configuration Manager mit Prototype

<div class="code-example">

```java
@Service
public class PrototypeBasedConfigurationManager {
    
    private final ConfigurationPrototypeRegistry registry;
    
    public PrototypeBasedConfigurationManager(ConfigurationPrototypeRegistry registry) {
        this.registry = registry;
    }
    
    // Fast configuration creation - nur ~10ms statt 1400ms!
    public ServiceConfiguration createDevConfiguration() {
        return registry.getPrototype("BASE")
            .withEnvironment("DEV")
            .withLogLevel("DEBUG")
            .withDatabaseUrl("dev-database-url")
            .withCustomProperty("debug.level", "TRACE")
            .withCustomProperty("cache.ttl", "300");
    }
    
    public ServiceConfiguration createTestConfiguration() {
        return registry.getPrototype("BASE")
            .withEnvironment("TEST")
            .withLogLevel("INFO")
            .withDatabaseUrl("test-database-url")
            .withCustomProperty("test.parallel", "true");
    }
    
    public ServiceConfiguration createProdConfiguration() {
        return registry.getPrototype("BASE")
            .withEnvironment("PROD")
            .withLogLevel("WARN")
            .withDatabaseUrl("prod-database-url")
            .withCustomProperty("monitoring.enabled", "true")
            .withCustomProperty("cache.ttl", "3600");
    }
    
    // Advanced: Environment-specific prototypes
    public ServiceConfiguration createOptimizedProdConfiguration() {
        // Falls bereits optimierter Prod-Prototype existiert
        try {
            return registry.getPrototype("PROD_OPTIMIZED")
                .withCustomProperty("instance.id", generateInstanceId());
        } catch (IllegalArgumentException e) {
            // Fallback auf Base + Optimierungen
            return createProdConfiguration()
                .withCustomProperty("performance.mode", "HIGH");
        }
    }
}
```

</div>

---

## Übung

<div class="interactive-question">

### Praktische Übung: Prototype Pattern
#### Implementieren Sie einen DatabaseConnectionPrototype

</div>

**Aufgabe**: Erweitern Sie das Konfigurationssystem um Database Connection Prototypes:
* DatabaseConnectionPrototype mit Clone-Implementation
<!-- .element: class="fragment" -->
* Verschiedene DB-Typen (Oracle, PostgreSQL, MongoDB)
<!-- .element: class="fragment" -->
* Connection Pool Settings
<!-- .element: class="fragment" -->
* Performance-optimierte Klonierung

**Implementierungsschritte**:
1. DatabaseConnectionPrototype-Klasse erstellen
2. Deep/Shallow Copy Strategie implementieren
3. PrototypeRegistry für DB-Connections
4. Performance-Vergleich durchführen

---

# Deep vs Shallow Copy Strategy

<div class="two-column">
<div>

## Shallow Copy (Performance)
```java
// Immutable objects - safe to share
cloned.sslCertificate = this.sslCertificate;
cloned.staticConfiguration = this.staticConfiguration;

// Thread-safe objects - safe to share
cloned.connectionPool = this.connectionPool;
```

## Deep Copy (Safety)
```java
// Mutable objects - must copy
cloned.customProperties = new HashMap<>(this.customProperties);

// Complex objects with state
cloned.serviceEndpoints = cloneServiceEndpoints();
```

</div>
<div>

## Lazy Cloning Strategy
```java
public class LazyCloneConfiguration {
    private DatabaseSettings databaseSettings;
    private boolean databaseSettingsCloned = false;
    
    public DatabaseSettings getDatabaseSettings() {
        if (!databaseSettingsCloned && databaseSettings != null) {
            databaseSettings = databaseSettings.clone();
            databaseSettingsCloned = true;
        }
        return databaseSettings;
    }
}
```

## Copy-on-Write Pattern
```java
public class CowConfiguration {
    private volatile List<String> properties;
    private volatile boolean isClone = false;
    
    public void addProperty(String prop) {
        if (!isClone) {
            properties = new ArrayList<>(properties);
            isClone = true;
        }
        properties.add(prop);
    }
}
```

</div>
</div>

---

# Clean Architecture: Fundamentale Prinzipien

## Was ist Clean Architecture?


**Clean Architecture** ist ein Architekturmuster von Robert C. Martin (Uncle Bob), das die **Dependency Rule** als zentrales Prinzip etabliert:

**📍 Dependency Rule: Dependencies zeigen nur nach innen!**
<!-- .element: class="fragment" data-fragment-index="2" -->

---

# Clean Architecture Schichten

<div class="two-column">
<div class="left">

## Schichten von außen nach innen:
**4. Frameworks & Drivers**<br/>UI, Database, Web, External APIs
<!-- .element: class="fragment" data-fragment-index="1" -->

**3. Interface Adapters**<br/>Controllers, Gateways, Presenters
<!-- .element: class="fragment" data-fragment-index="2" -->

**2. Application Business Rules**<br/>Use Cases, Interactors
<!-- .element: class="fragment" data-fragment-index="3" -->

**1. Enterprise Business Rules**<br/>Entities, Domain Objects
<!-- .element: class="fragment" data-fragment-index="4" -->

</div>
<div class="right">

## Visualisierung
```
    ┌─────────────────────────────┐
    │  Frameworks & Drivers (4)   │  ← External
    │  ┌─────────────────────────┐ │
    │  │ Interface Adapters (3)  │ │
    │  │  ┌─────────────────────┐│ │
    │  │  │ Use Cases (2)       ││ │
    │  │  │  ┌─────────────────┐││ │
    │  │  │  │  Entities (1)   │││ │  ← Core
    │  │  │  │                 │││ │
    │  │  │  └─────────────────┘││ │
    │  │  └─────────────────────┘│ │
    │  └─────────────────────────┘ │
    └─────────────────────────────┘
``` 
<!-- .element: class="fragment" data-fragment-index="5" -->
</div>
</div>

---

# Dependency Rule in Detail

## 🎯 Kernprinzip: Dependencies zeigen nach innen


**✅ Erlaubt:**
<!-- .element: class="fragment" data-fragment-index="2" -->
* Use Cases nutzen Entities
<!-- .element: class="fragment" -->
* Controllers nutzen Use Cases
<!-- .element: class="fragment" -->
* Gateways implementieren Repository-Interfaces

**❌ Verboten:**
<!-- .element: class="fragment" data-fragment-index="3" -->
* Entities kennen Use Cases
<!-- .element: class="fragment" -->
* Use Cases kennen Controllers
<!-- .element: class="fragment" -->
* Domain kennt Database-Details

---

# Clean Architecture & SOLID Prinzipien

## Wie Clean Architecture SOLID umsetzt:


**🔹 Dependency Inversion Principle:**<br/>
Use Cases definieren Interfaces, Adapter implementieren sie
<!-- .element: class="fragment" data-fragment-index="2" -->

**🔹 Single Responsibility:**<br/>
Jede Schicht hat eine klare Verantwortlichkeit
<!-- .element: class="fragment" data-fragment-index="3" -->

**🔹 Open/Closed:**<br/>
Neue Features durch neue Use Cases, ohne Core zu ändern
<!-- .element: class="fragment" data-fragment-index="4" -->

---

# Warum Clean Architecture für Enterprise-Systeme?

<div class="two-column">
<div class="left">

## 💪 Vorteile:
* **Testbarkeit**: Core-Logik isoliert testbar
<!-- .element: class="fragment" data-fragment-index="1" -->
* **Framework-Unabhängigkeit**: Core überlebt Framework-Wechsel
<!-- .element: class="fragment" data-fragment-index="2" -->
* **Database-Agnostik**: Core kennt keine DB-Details
<!-- .element: class="fragment" data-fragment-index="3" -->
* **UI-Flexibilität**: Verschiedene Interfaces möglich
<!-- .element: class="fragment" data-fragment-index="4" -->

</div>
<div class="right">

## 🎯 Enterprise-Realität:
* **Legacy-Integration**: Adapter für alte Systeme
<!-- .element: class="fragment" data-fragment-index="5" -->
* **Compliance**: Business-Rules zentral verwaltbar
<!-- .element: class="fragment" data-fragment-index="6" -->
* **Skalierung**: Schichten unabhängig skalierbar
<!-- .element: class="fragment" data-fragment-index="7" -->
* **Wartbarkeit**: Änderungen in abgegrenzten Bereichen
<!-- .element: class="fragment" data-fragment-index="8" -->

</div>
</div>

---

# Design Patterns in Clean Architecture

## Wie Patterns die Architektur unterstützen:


**🏗️ Adapter Pattern:** Interface zwischen Schichten schaffen
<!-- .element: class="fragment" data-fragment-index="2" -->

**📊 Repository Pattern:** Data Access abstrahieren
<!-- .element: class="fragment" data-fragment-index="3" -->

**🏭 Factory Pattern:** Abhängigkeiten auflösen
<!-- .element: class="fragment" data-fragment-index="4" -->

**🎯 Strategy Pattern:** Business-Rules austauschbar machen
<!-- .element: class="fragment" data-fragment-index="5" -->

---

# Clean Architecture Beispiel: Customer Service

<div class="code-example">

```java
// 1. Entity (Core Domain)
public class Customer {
    private final String id;
    private final String name;
    private final CustomerStatus status;
    
    // Business logic hier!
    public boolean canUpgrade() {
        return status == CustomerStatus.ACTIVE && 
               hasMinimumHistory();
    }
}

// 2. Use Case (Application Business Rules)
public class GetCustomerUseCase {
    private final CustomerRepository repository; // Interface!
    
    public Customer execute(String customerId) {
        return repository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
```

</div>

---

# Clean Architecture: Adapter Integration

<div class="code-example">

```java
// 3. Interface Adapter (Gateway Implementation)
public class DatabaseCustomerRepository implements CustomerRepository {
    private final CustomerJpaRepository jpaRepository;
    
    @Override
    public Optional<Customer> findById(String id) {
        return jpaRepository.findById(id)
            .map(this::toDomainEntity);  // Adapter-Funktion!
    }
}

// 4. Framework Layer (Controller)
@RestController
public class CustomerController {
    private final GetCustomerUseCase getCustomerUseCase;
    
    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable String id) {
        Customer customer = getCustomerUseCase.execute(id);
        return ResponseEntity.ok(toDto(customer)); // Presenter-Logik
    }
}
```

</div>

---

# Modul 5: Singleton & Adapter mit Clean Architecture

## Lernziele
* Singleton Pattern korrekt in Enterprise-Umgebungen anwenden 
<!-- .element: class="fragment" -->
* Adapter Pattern für Legacy-System Integration 
<!-- .element: class="fragment" -->
* Clean Architecture Prinzipien mit Pattern-Integration 
<!-- .element: class="fragment" -->
* Thread-Safety und Performance-Optimierung 

---

# Singleton & Adapter Pattern


## Was passt hier nicht?


Viele Unternehmen haben gewachsene Systemlandschaften mit unterschiedlichen APIs. Die aktuelle Integration weist komplexe Integrationsherausforderungen auf:

---

# Problematischer Code - Customer Service Manager

<div class="code-example">

```java
public class CustomerServiceManager {
    
    public Customer getCustomer(String customerId) {
        // Code-Smell: Hardcodierte System-Auswahl
        if (customerId.startsWith("OLD_")) {
            // Legacy SOAP System - direkter Aufruf
            LegacySOAPService soapService = new LegacySOAPService();
            String xml = soapService.getCustomerXML(customerId);
            
            // Problem: XML-Parsing in Business-Logik!
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder().parse(xml);
            
            Customer customer = new Customer();
            customer.setName(doc.getElementsByTagName("name").item(0).getTextContent());
            return customer;
        } else {
            // Modernes REST System
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject("/customers/" + customerId, Customer.class);
        }
    }
    
    // DUPLICATE CODE: Ähnliche Logik für andere Operationen
    public void updateCustomer(Customer customer) {
        if (customer.getId().startsWith("OLD_")) {
            // Legacy update... (weitere 20 Zeilen XML-Chaos)
        } else {
            // REST update...
        }
    }
}
```

</div>

---

## Identifizierte Code-Smells

* **Mixed Concerns**: Business-Logik vermischt mit Integration-Details 
<!-- .element: class="fragment" -->
* **Duplicate Code**: Ähnliche Integration-Logik in jeder Methode 
<!-- .element: class="fragment" -->
* **Tight Coupling**: Direkte Abhängigkeiten zu Legacy-APIs 
<!-- .element: class="fragment" -->
* **No Abstraction**: Keine einheitliche Service-Schnittstelle 
<!-- .element: class="fragment" -->
* **String-based Switching**: ID-Präfix bestimmt System-Auswahl 
<!-- .element: class="fragment" -->
* **Error-Prone**: Falsche Service-Kombinationen möglich 

### Was macht diese Lösung problematisch?

* **Neue Legacy-Systeme**: Änderung in JEDER Service-Methode
<!-- .element: class="fragment" -->
* **XML-Parsing**: Überall verstreute Parsing-Logik
<!-- .element: class="fragment" -->
* **Testing**: Schwierig zu mocken und isoliert zu testen

---

## Singleton - Konzept

### Thread-safe Singleton für Resource Management

<div class="code-example">

```java
// Thread-safe Singleton mit Enum (Bloch's Approach)
public enum ConfigurationManager {
    INSTANCE;
    
    private final Map<String, String> properties = new ConcurrentHashMap<>();
    private volatile boolean loaded = false;
    
    public void loadConfiguration(String configPath) {
        if (!loaded) {
            synchronized (this) {
                if (!loaded) {
                    // Expensive configuration loading
                    loadPropertiesFromFile(configPath);
                    loadEnvironmentVariables();
                    loadSystemProperties();
                    loaded = true;
                }
            }
        }
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    // Lazy initialization helper
    private void ensureLoaded() {
        if (!loaded) {
            loadConfiguration("application.properties");
        }
    }
    
    private void loadPropertiesFromFile(String configPath) {
        // Implementation...
    }
}

// Usage
String dbUrl = ConfigurationManager.INSTANCE.getProperty("db.url", "localhost");
```

</div>

---

## Lösung

<div class="code-example">

```java
// Legacy SOAP Customer Service (nicht veränderbar)
@WebService
public class LegacyCustomerSOAPService {
    
    public CustomerSOAPResponse getCustomerDetails(String customerId) {
        // Legacy SOAP implementation
        CustomerSOAPResponse response = new CustomerSOAPResponse();
        response.setCustomerXML(buildCustomerXML(customerId));
        return response;
    }
    
    public boolean updateCustomerSOAP(String customerId, String customerXML) {
        // Legacy update implementation
        return processCustomerXML(customerXML);
    }
}

// Modern Customer Service Interface (Domain Layer)
public interface CustomerService {
    Customer getCustomer(String customerId);
    void updateCustomer(Customer customer);
    List<Customer> searchCustomers(SearchCriteria criteria);
}

// Adapter für Legacy Integration
@Component
public class LegacyCustomerServiceAdapter implements CustomerService {
    
    private final LegacyCustomerSOAPService legacyService;
    private final CustomerXMLMapper xmlMapper;
    
    public LegacyCustomerServiceAdapter(LegacyCustomerSOAPService legacyService,
                                       CustomerXMLMapper xmlMapper) {
        this.legacyService = legacyService;
        this.xmlMapper = xmlMapper;
    }
    
    @Override
    public Customer getCustomer(String customerId) {
        try {
            CustomerSOAPResponse response = legacyService.getCustomerDetails(customerId);
            return xmlMapper.fromXML(response.getCustomerXML());
        } catch (Exception e) {
            throw new CustomerServiceException("Failed to get customer from legacy system", e);
        }
    }
    
    @Override
    public void updateCustomer(Customer customer) {
        try {
            String customerXML = xmlMapper.toXML(customer);
            boolean success = legacyService.updateCustomerSOAP(customer.getId(), customerXML);
            if (!success) {
                throw new CustomerServiceException("Legacy update failed");
            }
        } catch (Exception e) {
            throw new CustomerServiceException("Failed to update customer in legacy system", e);
        }
    }
    
    @Override
    public List<Customer> searchCustomers(SearchCriteria criteria) {
        // Legacy system doesn't support complex search
        throw new UnsupportedOperationException("Search not supported by legacy system");
    }
}
```

</div>

---

# Clean Architecture Integration

<div class="code-example">

```java
// Domain Layer - Customer Entity
public class Customer {
    private final String id;
    private final String name;
    private final String email;
    private final CustomerStatus status;
    
    // Constructor, getters, business methods
    public Customer(String id, String name, String email, CustomerStatus status) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.email = requireNonNull(email);
        this.status = requireNonNull(status);
    }
    
    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }
    
    public Customer activate() {
        return new Customer(id, name, email, CustomerStatus.ACTIVE);
    }
}

// Use Case Layer - Application Service
@Service
public class CustomerApplicationService {
    
    private final CustomerService customerService; // Interface from Domain
    private final NotificationService notificationService;
    
    public CustomerApplicationService(CustomerService customerService,
                                    NotificationService notificationService) {
        this.customerService = customerService;
        this.notificationService = notificationService;
    }
    
    @Transactional
    public void activateCustomer(String customerId) {
        Customer customer = customerService.getCustomer(customerId);
        
        if (customer.isActive()) {
            throw new BusinessException("Customer already active");
        }
        
        Customer activatedCustomer = customer.activate();
        customerService.updateCustomer(activatedCustomer);
        
        notificationService.sendActivationNotification(customerId);
    }
}
```

</div>

---

# Adapter Registry Pattern

<div class="code-example">

```java
// Interface für verschiedene Backend-Systeme
public interface BackendSystemAdapter {
    String getSystemName();
    boolean isAvailable();
    Customer getCustomer(String customerId);
    void updateCustomer(Customer customer);
}

// Registry für Backend-System Management
@Component
public class BackendSystemRegistry {
    
    private final Map<String, BackendSystemAdapter> adapters = new ConcurrentHashMap<>();
    private final Map<String, BackendSystemAdapter> customerRouting = new ConcurrentHashMap<>();
    
    public void registerAdapter(String systemName, BackendSystemAdapter adapter) {
        adapters.put(systemName, adapter);
    }
    
    public BackendSystemAdapter getAdapter(String systemName) {
        BackendSystemAdapter adapter = adapters.get(systemName);
        if (adapter == null) {
            throw new IllegalArgumentException("Backend system not found: " + systemName);
        }
        return adapter;
    }
    
    public BackendSystemAdapter getAdapterForCustomer(String customerId) {
        // Routing-Logik: Kunde zu Backend-System mapping
        BackendSystemAdapter adapter = customerRouting.get(customerId);
        if (adapter == null) {
            // Fallback: Legacy system für unbekannte Kunden
            adapter = getAdapter("LEGACY_SOAP");
        }
        
        if (!adapter.isAvailable()) {
            // Failover zu verfügbarem System
            adapter = findAvailableAdapter();
        }
        
        return adapter;
    }
    
    private BackendSystemAdapter findAvailableAdapter() {
        return adapters.values().stream()
            .filter(BackendSystemAdapter::isAvailable)
            .findFirst()
            .orElseThrow(() -> new SystemException("No backend system available"));
    }
    
    public List<String> getAvailableSystems() {
        return adapters.values().stream()
            .filter(BackendSystemAdapter::isAvailable)
            .map(BackendSystemAdapter::getSystemName)
            .collect(Collectors.toList());
    }
}
```

</div>

---

# Composite Service mit Adaptern

<div class="code-example">

```java
@Service
public class CompositeCustomerService implements CustomerService {
    
    private final BackendSystemRegistry systemRegistry;
    private final CustomerMergeStrategy mergeStrategy;
    
    public CompositeCustomerService(BackendSystemRegistry systemRegistry,
                                   CustomerMergeStrategy mergeStrategy) {
        this.systemRegistry = systemRegistry;
        this.mergeStrategy = mergeStrategy;
    }
    
    @Override
    public Customer getCustomer(String customerId) {
        BackendSystemAdapter primaryAdapter = systemRegistry.getAdapterForCustomer(customerId);
        
        try {
            return primaryAdapter.getCustomer(customerId);
        } catch (Exception e) {
            // Fallback strategy
            return getCustomerWithFallback(customerId, primaryAdapter);
        }
    }
    
    private Customer getCustomerWithFallback(String customerId, 
                                           BackendSystemAdapter excludeAdapter) {
        List<String> availableSystems = systemRegistry.getAvailableSystems();
        
        for (String systemName : availableSystems) {
            if (systemName.equals(excludeAdapter.getSystemName())) {
                continue; // Skip failed system
            }
            
            try {
                BackendSystemAdapter adapter = systemRegistry.getAdapter(systemName);
                return adapter.getCustomer(customerId);
            } catch (Exception e) {
                // Log and try next system
                log.warn("Failed to get customer from {}: {}", systemName, e.getMessage());
            }
        }
        
        throw new CustomerNotFoundException("Customer not found in any system: " + customerId);
    }
    
    @Override
    public void updateCustomer(Customer customer) {
        BackendSystemAdapter adapter = systemRegistry.getAdapterForCustomer(customer.getId());
        adapter.updateCustomer(customer);
        
        // Optional: Sync to other systems
        syncToOtherSystems(customer, adapter);
    }
}
```

</div>

---

## Übung

<div class="interactive-question">

### Praktische Übung: Singleton & Adapter
#### Implementieren Sie ein Legacy-Integration System

</div>

**Aufgabe**: Erweitern Sie das System um weitere Legacy-Integrationen:
* FileSystemAdapter für CSV-basierte Legacy-Daten
<!-- .element: class="fragment" -->
* DatabaseAdapter für direkte DB-Zugriffe
<!-- .element: class="fragment" -->
* Thread-safe ConnectionPoolSingleton
<!-- .element: class="fragment" -->
* Adapter Registry mit Failover-Mechanismus

**Implementierungsschritte**:
1. Neue Adapter-Implementierungen erstellen
2. ConnectionPoolSingleton implementieren
3. AdapterRegistry erweitern
4. Fallback-Strategien testen

---

# Praktische Übung - Integration Challenge

<div class="interactive-question">

## Ihre Aufgabe: Enterprise Service Integration
#### Implementieren Sie eine vollständige Service-Integration mit allen Patterns

</div>

<div class="two-column">
<div>

### Szenario: Multi-Backend Customer Management
* **Legacy SOAP Service** (nicht änderbar) 
<!-- .element: class="fragment" -->
* **Moderne REST API** (neu entwickelt) 
<!-- .element: class="fragment" -->
* **NoSQL Document Store** (MongoDB) 
<!-- .element: class="fragment" -->
* **Configuration Management** (Singleton) 
<!-- .element: class="fragment" -->
* **Query Builder** für komplexe Suchen 

### Anforderungen
* Factory Method für Service-Erstellung 
<!-- .element: class="fragment" -->
* Abstract Factory für Backend-Familien 
<!-- .element: class="fragment" -->
* Builder für Query-Erstellung 
<!-- .element: class="fragment" -->
* Prototype für Configuration-Templates 
<!-- .element: class="fragment" -->
* Adapter für Legacy-Integration 

</div>
<div>

### Implementation Tasks
1. **Service Factory Design** 
   - CustomerServiceFactory mit Backend-spezifischen Implementierungen
   - Abstract Factory für Service-Familien

2. **Builder Implementation** 
   - CustomerQueryBuilder mit Fluent Interface
   - Type-safe Query-Erstellung

3. **Adapter & Integration** 
   - Legacy SOAP Adapter
   - Configuration Prototype Registry
   - Service Registry mit Failover

Note:
* WICHTIGE HANDS-ON ÜBUNG: Intensive Praxisarbeit
<!-- .element: class="fragment" -->
* Teams von 2-3 Personen bilden lassen nach Erfahrungslevel
<!-- .element: class="fragment" -->
* Jedes Team wählt bevorzugtes Backend (SOAP/REST/NoSQL)
<!-- .element: class="fragment" -->
* Betonen Sie: Integration ALLER Patterns (Factory Method, Abstract Factory, Builder, Prototype, Singleton)
<!-- .element: class="fragment" -->
* Präsentieren Sie konkrete Code-Beispiele und Refactoring-Szenarien
<!-- .element: class="fragment" -->
* Zirkulieren Sie zwischen Teams, bieten Sie gezielte Diskussionsimpulse an
<!-- .element: class="fragment" -->
* Nach 30 Min: Zwischenstand abfragen, eventuell gemeinsame Patterns klären
<!-- .element: class="fragment" -->
* Letzten 15 Min für Diskussion der Lösungsansätze nutzen
<!-- .element: class="fragment" -->
* Bereiten Sie Refactoring-Beispiele vor als Diskussionsgrundlage
<!-- .element: class="fragment" -->
* WICHTIG: Code-Analyse und Pattern-Erkennung als Aufwärmung verwenden
<!-- .element: class="fragment" -->
* Bei Zeitnot: konzentrieren Sie sich auf Factory Method + Builder Pattern
<!-- .element: class="fragment" -->
<!-- .element: class="notes" -->

### Bonus Challenges
* **Performance Optimization** mit Caching 
<!-- .element: class="fragment" -->
* **Circuit Breaker** Pattern für Legacy-Services 
<!-- .element: class="fragment" -->
* **Monitoring & Observability** Integration 

</div>
</div>

---

# Zusammenfassung

<div class="progress-indicator">
<div class="progress-step completed">✅ Code-Analyse & Factory Method</div>
<div class="progress-step completed">✅ Abstract Factory & Layered Architecture</div>
<div class="progress-step completed">✅ Builder Pattern & Repository Integration</div>
<div class="progress-step completed">✅ Prototype Pattern & Configuration Cloning</div>
<div class="progress-step completed">✅ Clean Architecture: Fundamentale Prinzipien</div>
<div class="progress-step completed">✅ Singleton & Adapter mit Clean Architecture</div>
</div>

<div class="two-column">
<div>

## Was wir behandelt haben
* **Creational Patterns** in Enterprise-Kontexten 
<!-- .element: class="fragment" -->
* **Legacy-Code Refactoring** mit Pattern-basierten Lösungen 
<!-- .element: class="fragment" -->
* **Performance-Optimierung** durch intelligente Objekterstellung 
<!-- .element: class="fragment" -->
* **Clean Architecture** Integration mit Design Patterns 
<!-- .element: class="fragment" -->
* **SOLID-Prinzipien** in der Praxis 

</div>
<div>

## Zentrale Erkenntnisse
* **Factory Method** löst Code-Smells durch Polymorphismus 
<!-- .element: class="fragment" -->
* **Abstract Factory** strukturiert Service-Familien elegant 
<!-- .element: class="fragment" -->
* **Builder Pattern** macht komplexe Objekterstellung verständlich 
<!-- .element: class="fragment" -->
* **Prototype Pattern** optimiert Performance bei ähnlichen Objekten 
<!-- .element: class="fragment" -->
* **Adapter Pattern** ermöglicht nahtlose Legacy-Integration 

</div>
</div>

<div class="highlight-box accent">

## Morgen: Tag 2 Vorschau
* **Structural Patterns**: Decorator, Composite, Proxy
<!-- .element: class="fragment" -->
* **Advanced Enterprise Patterns**: Facade, Bridge, Flyweight
<!-- .element: class="fragment" -->
* **Microservice Architecture** Integration
<!-- .element: class="fragment" -->
* **Event-Driven Patterns** für reactive Systems

</div>

---

# Fragen & Diskussion

<div class="two-column">
<div>

## Kontaktinformationen
**E-Mail:** architecture-training@vanillacore.com  
**Repository:** [Design Patterns Examples](https://github.com/vanillacore/design-patterns)  
**Code-Beispiele:** Verfügbar im Workshop-Repository 

</div>
<div>

## Nächste Schritte
* **Pattern-Integration** in eigenen Projekten testen 
<!-- .element: class="fragment" -->
* **Code-Smell Detection** Tools einsetzen 
<!-- .element: class="fragment" -->
* **Legacy-Refactoring** Strategien entwickeln 
<!-- .element: class="fragment" -->
* **Vorbereitung auf Tag 2** Structural Patterns 

</div>
</div>

<div class="workshop-header">

**Vielen Dank für Ihre Teilnahme!**

</div>

<!-- Speaker Notes: Schließen Sie ab, indem Sie die Schlüsselkonzepte verstärken. Hervorhebung der praktischen Anwendbarkeit in Enterprise-Umgebungen und Begeisterung für weitere Patterns schaffen. Ermutigen Sie zur Anwendung der Pattern in aktuellen Projekten. -->