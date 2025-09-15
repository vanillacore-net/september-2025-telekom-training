# Architectural Patterns - Part 3
## Moderne und Cloud-Native Architekturen

---

## Übersicht: Moderne Architektur-Evolution

```
Von Monolithen zu Cloud-Native:

Traditional          Cloud-First         Serverless          Edge-Native
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Monolith   │ --> │ Microservices│ --> │  Functions  │ --> │ Edge Compute│
│  On-Premise │     │  Containers │     │  Event-Driven│     │ Distributed │
│  Stateful   │     │  Stateless  │     │  Ephemeral  │     │  Geo-Located│
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘

Infrastructure      Platform           Serverless         Edge Computing
as Code            Engineering        Computing          & IoT
```

**Focus:** Cloud-Native, Serverless und moderne Datenarchitekturen

---

## 1. Client-Server Architecture

### Architecture Schema
```
Client-Server Grundarchitektur:

┌─────────────────┐         Request          ┌─────────────────┐
│                 │ ─────────────────────────>│                 │
│     Client      │                           │     Server      │
│                 │<───────────────────────── │                 │
│  - Thin/Thick   │         Response         │  - Business     │
│  - Stateless    │                           │    Logic        │
│  - UI Logic     │                           │  - Data Access  │
└─────────────────┘                           │  - Resources    │
                                              └─────────────────┘

Variations:
- 2-Tier: Client ←→ Database Server
- 3-Tier: Client ←→ Application Server ←→ Database
- N-Tier: Multiple intermediate layers
```

---

## 1. Client-Server - Telekom Use Case

### Network Management Terminal System

**Implementation:**
- **Thin Clients:** Web-basierte Network Management Konsolen
- **Thick Clients:** Desktop-Anwendungen für komplexe Konfigurationen
- **Server:** Zentrale Management-Server für Netzwerkgeräte
- **Protokolle:** HTTPS, WebSocket für Real-time Updates

**Anwendungsbereiche:**
- Network Operations Center (NOC) Terminals
- Customer Service Portale
- Techniker-Arbeitsplätze
- Management Dashboards

---

## 1. Client-Server - Trade-offs

### Vorteile
- **Zentrale Datenhaltung und Kontrolle**
- **Einfache Wartung der Business Logic**
- **Skalierung durch leistungsfähigere Server**
- **Thin Clients reduzieren Client-Anforderungen**
- **Etablierte Sicherheitsmodelle**

### Nachteile
- **Single Point of Failure beim Server**
- **Netzwerkabhängigkeit für alle Operationen**
- **Skalierungsgrenzen bei vielen Clients**
- **Latenz bei geografisch verteilten Clients**
- **Server-Ressourcen müssen für Peak-Load dimensioniert werden**

### Production Considerations
- **Load Balancing für mehrere Server-Instanzen**
- **Session Management bei zustandsbehafteten Verbindungen**
- **Caching-Strategien zur Latenzreduktion**
- **Failover-Mechanismen für High Availability**

---

## 1. Client-Server - When to Use

### Ideal für:
- **Zentrale Datenverwaltung erforderlich**
- **Kontrollierte Zugriffe auf Ressourcen**
- **Einheitliche Business Logic für alle Clients**
- **Etablierte Enterprise-Umgebungen**
- **Klassische Desktop-Anwendungen**

### Vermeiden wenn:
- **Hochgradig verteilte Teams**
- **Offline-Funktionalität kritisch**
- **Extreme Skalierungsanforderungen**
- **Microservices-Architektur gewünscht**
- **Edge Computing erforderlich**

---

## 2. Serverless Architecture (FaaS)

### Architecture Schema
```
Serverless / Function-as-a-Service:

Events                    Functions                  Services
┌──────────┐            ┌──────────────┐           ┌──────────┐
│ HTTP     │───────────>│ API Handler  │──────────>│ Database │
│ Request  │            │ (Lambda)     │           │ (DynamoDB)│
└──────────┘            └──────────────┘           └──────────┘

┌──────────┐            ┌──────────────┐           ┌──────────┐
│ S3       │───────────>│ Image        │──────────>│ S3       │
│ Upload   │            │ Processor    │           │ Processed│
└──────────┘            └──────────────┘           └──────────┘

┌──────────┐            ┌──────────────┐           ┌──────────┐
│ Schedule │───────────>│ Report       │──────────>│ Email    │
│ (Cron)   │            │ Generator    │           │ Service  │
└──────────┘            └──────────────┘           └──────────┘

Key: No server management, automatic scaling, pay-per-use
```

---

## 2. Serverless - Telekom Use Case

### SMS/Notification Processing System

**Function Implementation:**
- **SMS Handler:** Verarbeitung eingehender SMS
- **Notification Router:** Verteilung an verschiedene Kanäle
- **Billing Calculator:** Usage-basierte Abrechnung
- **Report Generator:** Scheduled Reports

**Technology Stack:**
- **Functions:** AWS Lambda / Azure Functions
- **API Gateway:** REST/GraphQL Endpoints
- **Storage:** S3, DynamoDB, Cosmos DB
- **Events:** SNS, SQS, EventBridge

---

## 2. Serverless - Trade-offs

### Vorteile
- **Keine Server-Verwaltung notwendig**
- **Automatische Skalierung von 0 bis unendlich**
- **Pay-per-Use Kostenmodell**
- **Schnelle Entwicklung und Deployment**
- **Eingebaute High Availability**

### Nachteile
- **Vendor Lock-in bei Cloud-Provider**
- **Cold Start Latenz bei ersten Aufrufen**
- **Limitierte Execution Time (meist 15 Minuten)**
- **Debugging und Testing komplexer**
- **Stateless-Zwang kann Architektur verkomplizieren**

### Production Considerations
- **Function Warming für Cold Start Vermeidung**
- **Distributed Tracing für End-to-End Monitoring**
- **Error Handling und Dead Letter Queues**
- **Cost Monitoring bei hohem Traffic**

---

## 2. Serverless - When to Use

### Ideal für:
- **Event-getriebene Workloads**
- **Sporadische oder unvorhersehbare Last**
- **Rapid Prototyping und MVPs**
- **Microservices ohne Server-Management**
- **Cost-Optimization bei variablem Traffic**

### Vermeiden wenn:
- **Lang laufende Prozesse (>15 Minuten)**
- **Stateful Applications**
- **Niedrige Latenz kritisch (Cold Starts)**
- **Komplexe Debugging-Anforderungen**
- **Vendor Lock-in inakzeptabel**

---

## 3. Micro-Frontend Architecture

### Architecture Schema
```
Micro-Frontend Composition:

┌─────────────────────────────────────────────────────────────┐
│                    App Shell / Container                     │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  Shared Components                    │  │
│  │              (Header, Navigation, Footer)             │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   Product    │  │   Shopping   │  │   Account    │    │
│  │   Catalog    │  │     Cart     │  │  Management  │    │
│  │              │  │              │  │              │    │
│  │  Team A      │  │   Team B     │  │   Team C     │    │
│  │  React       │  │   Vue        │  │   Angular    │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
└─────────────────────────────────────────────────────────────┘

Integration: Module Federation / Single-SPA / Web Components
```

---

## 3. Micro-Frontend - Telekom Use Case

### Customer Self-Service Portal

**Frontend Decomposition:**
- **Account Team:** Profilverwaltung (React)
- **Billing Team:** Rechnungen und Zahlungen (Vue)
- **Products Team:** Tarife und Upgrades (Angular)
- **Support Team:** Tickets und Chat (Svelte)

**Integration Strategy:**
- **Module Federation:** Webpack 5 für Runtime Integration
- **Shared State:** Redux/MobX für Cross-Team State
- **Design System:** Gemeinsame Component Library
- **Routing:** Single-SPA für Frontend Routing

---

## 3. Micro-Frontend - Trade-offs

### Vorteile
- **Team-Autonomie bei Technology Stack**
- **Unabhängige Deployments pro Team**
- **Isolierte Fehler pro Frontend-Modul**
- **Parallele Entwicklung verschiedener Features**
- **Inkrementelle Modernisierung möglich**

### Nachteile
- **Initial Load Performance durch mehrere Bundles**
- **Komplexe Integration und Orchestrierung**
- **Duplizierte Dependencies erhöhen Bundle-Größe**
- **Cross-Team Communication notwendig**
- **Testing der Integration aufwendig**

### Production Considerations
- **Shared Design System für Konsistenz**
- **Performance Budgets pro Micro-Frontend**
- **Fallback-Strategien bei Modul-Ausfall**
- **Versionierung von Shared Dependencies**

---

## 3. Micro-Frontend - When to Use

### Ideal für:
- **Große Frontend-Teams (5+ Teams)**
- **Unterschiedliche Release-Zyklen**
- **Technology Diversity gewünscht**
- **Unabhängige Team-Autonomie**
- **Schrittweise Migration von Legacy**

### Vermeiden wenn:
- **Kleines Frontend-Team (<3 Entwickler)**
- **Einfache, kohärente Anwendung**
- **Performance kritisch**
- **Konsistente UX oberste Priorität**
- **Begrenzte DevOps-Ressourcen**

---

## 4. Backend for Frontend (BFF) Pattern

### Architecture Schema
```
BFF Pattern - Optimierte APIs pro Client:

┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Mobile    │     │     Web     │     │   Desktop   │
│     App     │     │     SPA     │     │     App     │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Mobile BFF │     │   Web BFF   │     │ Desktop BFF │
│             │     │             │     │             │
│ - Optimized │     │ - GraphQL   │     │ - Full Data │
│ - Compressed│     │ - Caching   │     │ - Bulk Ops  │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    ┌───▼───┐        ┌───▼───┐        ┌───▼───┐
    │Service│        │Service│        │Service│
    │   A   │        │   B   │        │   C   │
    └───────┘        └───────┘        └───────┘
```

---

## 4. BFF - Telekom Use Case

### Multi-Channel Customer Experience

**BFF Implementations:**
- **Mobile BFF:** Optimierte Payloads, Offline-Support
- **Web BFF:** GraphQL, Real-time Subscriptions
- **TV/STB BFF:** Minimale Payloads, Navigation-optimiert
- **Voice BFF:** Alexa/Google Home Integration

**Optimization per Channel:**
- **Mobile:** Datenkompression, Pagination
- **Web:** Prefetching, Caching-Strategien
- **TV:** Simplified Navigation Trees
- **Voice:** Natural Language Response

---

## 4. BFF - Trade-offs

### Vorteile
- **Optimierte APIs für jeden Client-Typ**
- **Reduzierte Netzwerk-Roundtrips**
- **Client-spezifische Aggregation**
- **Vereinfachte Client-Logik**
- **Bessere Performance pro Kanal**

### Nachteile
- **Code-Duplikation zwischen BFFs**
- **Erhöhte Wartungskomplexität**
- **Mehrere Deployment-Pipelines**
- **Konsistenz zwischen BFFs herausfordernd**
- **Zusätzliche Infrastruktur-Komponenten**

### Production Considerations
- **Shared Libraries für gemeinsame Logik**
- **API Versioning pro BFF**
- **Monitoring pro Client-Channel**
- **Rate Limiting differenziert nach Client**

---

## 4. BFF - When to Use

### Ideal für:
- **Multiple Client-Typen (Mobile, Web, TV)**
- **Stark unterschiedliche Client-Anforderungen**
- **API Aggregation erforderlich**
- **Client-spezifische Performance-Optimierung**
- **Reduzierung von Netzwerk-Roundtrips**

### Vermeiden wenn:
- **Nur ein Client-Typ**
- **Ähnliche Client-Anforderungen**
- **Einfache CRUD-Operationen**
- **Begrenzte Backend-Ressourcen**
- **GraphQL bereits implementiert**

---

## 5. Data Lake Architecture

### Architecture Schema
```
Data Lake - Zentralisierter Rohdatenspeicher:

Data Sources           Data Lake                Processing           Analytics
┌──────────┐         ┌────────────────────────────────────┐      ┌──────────┐
│Structured│────────>│         Landing Zone               │      │   BI     │
│   Data   │         │  ┌────────────────────────────┐   │      │  Tools   │
└──────────┘         │  │     Raw Data Storage      │   │      └──────────┘
                     │  └────────────────────────────┘   │
┌──────────┐         │                                   │      ┌──────────┐
│  Semi-   │────────>│        Curated Zone              │─────>│   ML     │
│Structured│         │  ┌────────────────────────────┐   │      │ Platform │
└──────────┘         │  │  Processed & Cataloged    │   │      └──────────┘
                     │  └────────────────────────────┘   │
┌──────────┐         │                                   │      ┌──────────┐
│Unstructur│────────>│      Consumption Zone            │─────>│Analytics │
│   ed     │         │  ┌────────────────────────────┐   │      │ Engines  │
└──────────┘         │  │    Optimized Datasets     │   │      └──────────┘
                     └────────────────────────────────────┘

Storage: S3, ADLS, GCS | Processing: Spark, Flink | Query: Athena, Presto
```

---

## 5. Data Lake - Telekom Use Case

### Network Analytics Platform

**Data Sources:**
- **Network Logs:** Router, Switch, Firewall Logs
- **Customer Data:** CRM, Billing, Support Tickets
- **IoT Sensors:** Network Performance Metrics
- **External Data:** Weather, Events, Social Media

**Data Zones:**
- **Bronze/Landing:** Rohdaten ohne Transformation
- **Silver/Curated:** Bereinigt und strukturiert
- **Gold/Consumption:** Business-ready Datasets

**Analytics Use Cases:**
- Network Performance Prediction
- Customer Churn Analysis
- Capacity Planning
- Fraud Detection

---

## 5. Data Lake - Trade-offs

### Vorteile
- **Schema-on-Read Flexibilität**
- **Kosteneffiziente Speicherung großer Datenmengen**
- **Alle Datentypen in einem Repository**
- **Nachträgliche Use-Case Entwicklung möglich**
- **Skalierung in Petabyte-Bereich**

### Nachteile
- **Data Swamp Gefahr ohne Governance**
- **Komplexe Zugriffskontrolle**
- **Performance-Probleme bei direkten Queries**
- **Fehlende Datenqualität ohne Prozesse**
- **Hohe Expertise für Datenverarbeitung nötig**

### Production Considerations
- **Data Catalog für Discoverability**
- **Data Lineage Tracking**
- **Partitionierung für Query-Performance**
- **Lifecycle Policies für Kostenoptimierung**

---

## 5. Data Lake - When to Use

### Ideal für:
- **Big Data Analytics erforderlich**
- **Heterogene Datenquellen**
- **Explorative Datenanalyse**
- **ML/AI Training Data Storage**
- **Langzeit-Datenarchivierung**

### Vermeiden wenn:
- **Real-time Analytics kritisch**
- **Strukturierte Daten ausreichend**
- **Begrenzte Data Engineering Expertise**
- **Strikte Compliance-Anforderungen**
- **Kleine Datenmengen (<TB)**

---

## 6. Data Mesh Architecture

### Architecture Schema
```
Data Mesh - Dezentralisierte Domain-orientierte Daten:

┌─────────────────────────────────────────────────────────────────┐
│                    Self-Serve Data Platform                     │
│  (Infrastructure, Pipelines, Storage, Compute, Access Control)  │
└─────────────────────────────────────────────────────────────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
┌───────▼────────┐    ┌────────▼────────┐    ┌────────▼────────┐
│  Customer      │    │    Network      │    │    Billing      │
│   Domain       │    │    Domain       │    │    Domain       │
│                │    │                 │    │                 │
│ ┌────────────┐ │    │ ┌────────────┐  │    │ ┌────────────┐  │
│ │Data Product│ │◄──►│ │Data Product│  │◄──►│ │Data Product│  │
│ └────────────┘ │    │ └────────────┘  │    │ └────────────┘  │
│                │    │                 │    │                 │
│ Domain Team    │    │ Domain Team     │    │ Domain Team     │
└────────────────┘    └─────────────────┘    └─────────────────┘

Principles: Domain Ownership, Data as Product, Self-Service, Federated Governance
```

---

## 6. Data Mesh - Telekom Use Case

### Dezentralisierte Telekom-Datenprodukte

**Domain Teams & Their Data Products:**
- **Customer Domain:** Customer 360°, Segmentation Data
- **Network Domain:** Performance Metrics, Topology Data
- **Billing Domain:** Usage Data, Invoice Analytics
- **Marketing Domain:** Campaign Performance, Attribution

**Self-Service Platform:**
- **Data Infrastructure:** Kubernetes, Spark, Kafka
- **Data Contracts:** Schema Registry, API Specs
- **Discovery:** Data Catalog, Lineage
- **Governance:** Access Control, Compliance

---

## 6. Data Mesh - Trade-offs

### Vorteile
- **Domain-Expertise direkt bei den Daten**
- **Skalierung durch Dezentralisierung**
- **Schnellere Time-to-Market für Datenprodukte**
- **Klare Ownership und Verantwortlichkeiten**
- **Reduktion von Datensilos**

### Nachteile
- **Hohe organisatorische Komplexität**
- **Duplizierte Infrastruktur-Efforts**
- **Konsistenz zwischen Domains schwierig**
- **Kultureller Wandel notwendig**
- **Initiale Investition in Platform sehr hoch**

### Production Considerations
- **Federated Governance Framework**
- **Standardisierte Data Product Templates**
- **Cross-Domain Data Contracts**
- **Monitoring der Data Product Quality**

---

## 6. Data Mesh - When to Use

### Ideal für:
- **Große, verteilte Organisationen**
- **Multiple Domain Teams mit Daten-Expertise**
- **Dezentrale Daten-Ownership gewünscht**
- **Skalierungsprobleme mit zentralem Data Team**
- **Domain-spezifische Datenprodukte**

### Vermeiden wenn:
- **Kleine Organisation (<50 Entwickler)**
- **Wenige Datenquellen**
- **Zentrale Data Governance erforderlich**
- **Begrenzte Domain-Expertise**
- **Einfache Reporting-Anforderungen**

---

## 7. Cell-Based Architecture

### Architecture Schema
```
Cell-Based Architecture - Isolierte Deployment Units:

┌──────────────────────────────────────────────────────────┐
│                    Cell Router                           │
│              (Routing based on tenant/region)            │
└────────┬──────────────────┬──────────────────┬──────────┘
         │                  │                  │
    ┌────▼─────┐      ┌─────▼────┐      ┌─────▼────┐
    │  Cell 1  │      │  Cell 2  │      │  Cell 3  │
    │          │      │          │      │          │
    │ ┌──────┐ │      │ ┌──────┐ │      │ ┌──────┐ │
    │ │ API  │ │      │ │ API  │ │      │ │ API  │ │
    │ └──────┘ │      │ └──────┘ │      │ └──────┘ │
    │ ┌──────┐ │      │ ┌──────┐ │      │ ┌──────┐ │
    │ │ Logic│ │      │ │ Logic│ │      │ │ Logic│ │
    │ └──────┘ │      │ └──────┘ │      │ └──────┘ │
    │ ┌──────┐ │      │ ┌──────┐ │      │ ┌──────┐ │
    │ │  DB  │ │      │ │  DB  │ │      │ │  DB  │ │
    │ └──────┘ │      │ └──────┘ │      │ └──────┘ │
    │          │      │          │      │          │
    │ Region:  │      │ Region:  │      │ Region:  │
    │   EU     │      │   US     │      │   APAC   │
    └──────────┘      └──────────┘      └──────────┘

Each cell is independent, reducing blast radius
```

---

## 7. Cell-Based - Telekom Use Case

### Multi-Region Customer Platform

**Cell Distribution:**
- **Geographic Cells:** EU, US, APAC, MENA
- **Customer Tier Cells:** Enterprise, Business, Consumer
- **Service Type Cells:** Mobile, Broadband, TV

**Cell Components:**
- **Complete Stack:** API, Business Logic, Database
- **Independent Scaling:** Per Cell Requirements
- **Isolated Failures:** Cell-Ausfall betrifft nur Teil der User
- **Data Residency:** GDPR-Compliance per Region

---

## 7. Cell-Based - Trade-offs

### Vorteile
- **Blast Radius Reduktion bei Ausfällen**
- **Unabhängige Skalierung pro Cell**
- **Data Residency und Compliance**
- **Einfachere Disaster Recovery**
- **Performance durch Lokalität**

### Nachteile
- **Höhere Infrastruktur-Kosten**
- **Komplexes Cell Routing**
- **Cross-Cell Operationen schwierig**
- **Datenreplikation zwischen Cells**
- **Erhöhter Operational Overhead**

### Production Considerations
- **Cell Router High Availability**
- **Cross-Cell Data Synchronization**
- **Cell Health Monitoring**
- **Automated Cell Failover**

---

## 7. Cell-Based - When to Use

### Ideal für:
- **Multi-Region Deployments**
- **Regulatorische Data Residency**
- **Blast Radius Minimierung kritisch**
- **Tenant Isolation erforderlich**
- **Unterschiedliche SLAs pro Region/Kunde**

### Vermeiden wenn:
- **Single Region ausreichend**
- **Starke Cross-Cell Kommunikation**
- **Begrenzte Operations-Expertise**
- **Kosten-Optimierung prioritär**
- **Einfache Failover-Anforderungen**

---

## 8. Clean Architecture

### Architecture Schema
```
Clean Architecture (Uncle Bob):

┌──────────────────────────────────────────────────────────┐
│                    Frameworks & Drivers                  │
│  (Web, UI, DB, Devices, External Interfaces)            │
├──────────────────────────────────────────────────────────┤
│                  Interface Adapters                      │
│  (Controllers, Gateways, Presenters)                    │
├──────────────────────────────────────────────────────────┤
│                  Application Business Rules              │
│  (Use Cases, Application Specific Business Rules)       │
├──────────────────────────────────────────────────────────┤
│                  Enterprise Business Rules               │
│  (Entities, Core Business Logic)                        │
└──────────────────────────────────────────────────────────┘

Dependencies: Outer layers depend on inner layers only
              Inner layers know nothing about outer layers
```

---

## 8. Clean Architecture - Telekom Use Case

### Billing System Redesign

**Layer Implementation:**
- **Entities:** Customer, Invoice, Tariff, Usage
- **Use Cases:** CreateInvoice, ApplyDiscount, ProcessPayment
- **Interface Adapters:** REST Controller, Database Gateway
- **Frameworks:** Spring Boot, PostgreSQL, RabbitMQ

**Dependency Rule:**
- Entities kennen keine Use Cases
- Use Cases kennen keine Controller
- Controller kennen keine Frameworks
- Testbarkeit ohne externe Dependencies

---

## 8. Clean Architecture - Trade-offs

### Vorteile
- **Unabhängigkeit von Frameworks**
- **Testbarkeit der Business Logic**
- **UI-Unabhängigkeit**
- **Datenbank-Unabhängigkeit**
- **Klare Separation of Concerns**

### Nachteile
- **Initiale Komplexität hoch**
- **Mehr Code durch Abstractions**
- **Steile Lernkurve für Teams**
- **Overengineering für simple CRUD**
- **Performance-Overhead durch Indirection**

### Production Considerations
- **Dependency Injection Framework**
- **Mapping zwischen Layer-Objekten**
- **Konsistente Naming Conventions**
- **Automated Architecture Tests**

---

## 8. Clean Architecture - When to Use

### Ideal für:
- **Langlebige Business Applications**
- **Häufige Framework-Wechsel erwartet**
- **Hohe Testbarkeit erforderlich**
- **Komplexe Geschäftslogik**
- **Domain-Driven Design Ansatz**

### Vermeiden wenn:
- **Einfache CRUD-Anwendungen**
- **Rapid Prototyping**
- **Kleines Team ohne DDD-Erfahrung**
- **Time-to-Market kritisch**
- **Minimale Business Logic**

---

## 9. Onion Architecture

### Architecture Schema
```
Onion Architecture - Konzentrische Kreise:

            ┌─────────────────────────────┐
           │     Infrastructure          │
          │   (UI, DB, Web, Files)      │
         │ ┌───────────────────────────┐ │
        │ │    Application Services    │ │
       │ │  (Application Interfaces)  │ │
      │ │ ┌─────────────────────────┐ │ │
     │ │ │   Domain Services        │ │ │
    │ │ │  (Business Operations)   │ │ │
   │ │ │ ┌───────────────────────┐ │ │ │
  │ │ │ │    Domain Model        │ │ │ │
 │ │ │ │   (Entities, VOs)      │ │ │ │
  │ │ │ └───────────────────────┘ │ │ │
   │ │ └─────────────────────────┘ │ │
    │ └───────────────────────────┘ │
     └─────────────────────────────┘

Core is independent, outer layers depend on inner
```

---

## 9. Onion Architecture - Telekom Use Case

### Customer Relationship Management

**Layer Structure:**
- **Domain Model:** Customer, Contract, ServiceLevel
- **Domain Services:** ContractValidation, CreditCheck
- **Application Services:** CustomerRegistration, ContractRenewal
- **Infrastructure:** SQL Repository, REST API, Email Service

**Inversion of Control:**
- Repository Interfaces in Domain
- Implementations in Infrastructure
- Dependency Injection verbindet Layers
- Domain bleibt Framework-agnostisch

---

## 9. Onion Architecture - Trade-offs

### Vorteile
- **Domain-Fokussierung im Zentrum**
- **Kopplung Richtung Zentrum**
- **Flexible äußere Schichten**
- **Hohe Testbarkeit des Kerns**
- **Evolutionäre Architektur möglich**

### Nachteile
- **Abstraktion kann verwirrend sein**
- **Mehr Interfaces und Indirection**
- **Mapping zwischen Schichten aufwendig**
- **Kann zu Over-Engineering führen**
- **Performance-Overhead möglich**

### Production Considerations
- **Domain Model Persistence Ignorance**
- **DTO Mapping Strategies**
- **Dependency Injection Container**
- **Integration Testing über Schichten**

---

## 9. Onion Architecture - When to Use

### Ideal für:
- **Domain-zentrische Anwendungen**
- **Evolutionäre Architektur gewünscht**
- **Testbarkeit des Kerns kritisch**
- **Häufige Infrastructure-Änderungen**
- **Enterprise Applications**

### Vermeiden wenn:
- **Einfache Anwendungen**
- **Framework-zentrische Entwicklung**
- **Begrenzte Abstraktions-Erfahrung**
- **Performance kritischer als Flexibilität**
- **Schnelle Prototypen**

---

## 10. Screaming Architecture

### Architecture Schema
```
Screaming Architecture - Domain-Centric Structure:

Traditional Structure:          Screaming Architecture:
/src                           /src
  /controllers                   /CustomerManagement
  /models                          /RegisterCustomer
  /views                           /UpdateProfile
  /services                        /CloseAccount
  /repositories                  /BillingSystem
  /utils                           /GenerateInvoice
                                  /ProcessPayment
                                  /ApplyDiscount
                                /NetworkManagement
                                  /ConfigureDevice
                                  /MonitorPerformance
                                  /HandleAlerts

Architecture "screams" its business purpose!
```

---

## 10. Screaming Architecture - Telekom Use Case

### Telekom Business Platform

**Domain-Centric Organization:**
```
/TelekomPlatform
  /CustomerOnboarding
    - VerifyIdentity
    - CreateAccount
    - AssignServices
  /NetworkProvisioning
    - AllocateBandwidth
    - ConfigureRouting
    - ActivateService
  /BillingOperations
    - CalculateCharges
    - GenerateInvoice
    - ProcessPayment
  /SupportTicketing
    - CreateTicket
    - EscalateIssue
    - ResolveTicket
```

**Prinzip:** Geschäftsdomäne sofort erkennbar

---

## 10. Screaming Architecture - Trade-offs

### Vorteile
- **Business Intent sofort erkennbar**
- **Neue Entwickler verstehen Domäne schnell**
- **Use-Case-getriebene Organisation**
- **Reduzierte kognitive Belastung**
- **Natürliche Modularisierung**

### Nachteile
- **Technische Aspekte versteckt**
- **Potenzielle Code-Duplikation**
- **Framework-Integration schwieriger**
- **Cross-Cutting Concerns verstreut**
- **Refactoring bei Domänen-Änderungen**

### Production Considerations
- **Klare Bounded Contexts definieren**
- **Shared Kernel für Common Code**
- **Build-System Anpassungen**
- **IDE Navigation Optimierung**

---

## 10. Screaming Architecture - When to Use

### Ideal für:
- **Business-fokussierte Teams**
- **Neue Entwickler Onboarding**
- **Use-Case-getriebene Entwicklung**
- **Klare Domänen-Grenzen**
- **Selbstdokumentierende Struktur gewünscht**

### Vermeiden wenn:
- **Technische Organisation bevorzugt**
- **Framework-Standards wichtig**
- **Kleine, technische Anwendungen**
- **Häufige Domänen-Umstrukturierungen**
- **Cross-Cutting Concerns dominieren**

---

## 11. GitOps Architecture

### Architecture Schema
```
GitOps - Git as Single Source of Truth:

Developer              Git Repository           Kubernetes Cluster
┌──────────┐          ┌──────────────┐         ┌──────────────────┐
│          │          │              │         │                  │
│  Code    │ Push     │  Config      │ Pull    │   GitOps        │
│  Change  ├─────────>│  Files       │<────────┤   Operator      │
│          │          │              │         │   (ArgoCD)      │
└──────────┘          │  - k8s       │         │                  │
                      │  - Helm      │         │   Sync &         │
┌──────────┐          │  - Kustomize │         │   Deploy        │
│          │          │              │         │      │           │
│  Merge   │ Approve  │  Main        │         │      ▼           │
│  Request ├─────────>│  Branch      │         │  Applications    │
│          │          │              │         │  & Services      │
└──────────┘          └──────────────┘         └──────────────────┘

Principles: Declarative, Versioned, Immutable, Continuously Reconciled
```

---

## 11. GitOps - Telekom Use Case

### Infrastructure Automation Platform

**GitOps Implementation:**
- **Git Repository:** Alle Kubernetes Manifests, Helm Charts
- **GitOps Operator:** ArgoCD für Continuous Deployment
- **Environments:** Dev, Staging, Production als Git Branches
- **Rollback:** Git Revert für sofortiges Rollback

**Workflow:**
- Developer erstellt Merge Request
- Review und Approval durch Team
- Merge triggert automatisches Deployment
- ArgoCD synchronisiert Cluster-State
- Monitoring und Alerting bei Drift

---

## 11. GitOps - Trade-offs

### Vorteile
- **Vollständige Audit Trail durch Git History**
- **Deklarative Infrastructure as Code**
- **Einfaches Rollback via Git**
- **Selbstheilende Infrastruktur**
- **Verbesserte Security durch Pull-Model**

### Nachteile
- **Git wird Single Point of Failure**
- **Secrets Management komplex**
- **Große Repositories bei vielen Services**
- **Learning Curve für Git-basierte Ops**
- **Debugging von Sync-Fehlern schwierig**

### Production Considerations
- **Sealed Secrets oder External Secrets Operator**
- **Branch Protection Rules**
- **Automated Testing von Manifests**
- **Disaster Recovery für Git Repository**

---

## 11. GitOps - When to Use

### Ideal für:
- **Kubernetes-basierte Infrastruktur**
- **Infrastructure as Code Ansatz**
- **Audit Trail Requirements**
- **Multi-Environment Deployments**
- **Self-Healing Infrastructure gewünscht**

### Vermeiden wenn:
- **Keine Container-Infrastruktur**
- **Legacy Deployment-Prozesse**
- **Manuelle Interventionen erforderlich**
- **Secrets Management unklar**
- **Git-Expertise begrenzt**

---

## Architecture Decision Matrix - Part 3

| Criteria | Serverless | Micro-Frontend | BFF | Data Lake | Data Mesh | Cell-Based |
|----------|------------|----------------|-----|-----------|-----------|------------|
| **Team Size** | 1-3 | 5+ | 2-4 | 3-5 | 10+ | 5+ |
| **Scalability** | Infinite | High | Medium | Very High | Very High | Very High |
| **Complexity** | Low | High | Medium | High | Very High | High |
| **Cost Model** | Pay-per-use | Standard | Standard | Storage-heavy | High | High |
| **Time-to-Market** | Very Fast | Medium | Fast | Slow | Very Slow | Slow |
| **Maintenance** | Minimal | High | Medium | High | Very High | High |

---

## Pattern Combinations - Part 3

### Moderne Architekturen kombinieren

**Serverless + BFF:**
- Lambda Functions als BFF Implementation
- Cost-optimized API Layer
- Auto-scaling für variable Last

**Micro-Frontend + BFF:**
- Optimierte APIs pro Frontend-Team
- End-to-End Team Ownership
- Full-Stack Autonomie

**Data Lake + Data Mesh:**
- Data Lake als Storage Layer
- Data Mesh als Organizational Layer
- Best of Both Worlds

**Cell-Based + GitOps:**
- GitOps für Cell Deployments
- Automated Cell Management
- Infrastructure as Code per Cell

---

## Telekom-Specific Recommendations - Part 3

### Für Cloud-Native Transformation:
- **Serverless** für Event-Processing und APIs
- **BFF** für Multi-Channel Experiences
- **GitOps** für Kubernetes Deployments

### Für Daten-Strategie:
- **Data Lake** für zentrale Datenhaltung
- **Data Mesh** für Skalierung und Ownership
- Migration Path von Lake zu Mesh

### Für Frontend-Modernisierung:
- **Micro-Frontends** für große Teams
- **BFF** für optimierte APIs
- **Clean Architecture** für Langlebigkeit

### Für Resilience:
- **Cell-Based** für geografische Verteilung
- **Serverless** für automatische Skalierung
- **GitOps** für Self-Healing Infrastructure

---

## Summary - Part 3

### Key Takeaways für moderne Architekturen:

**Cloud-Native First:**
- Serverless für variable Workloads
- Container für konsistente Deployments
- GitOps für Infrastructure Automation

**Data als Strategic Asset:**
- Data Lake für Sammlung
- Data Mesh für Skalierung
- Clear Data Governance

**Frontend Evolution:**
- Micro-Frontends für Team-Skalierung
- BFF für Client-Optimierung
- Progressive Web Apps für Mobile

**Architecture als Code:**
- Clean/Onion für Langlebigkeit
- GitOps für Operations
- Everything as Code

**Next:** Part 4 behandelt klassische Patterns und Grundlagen