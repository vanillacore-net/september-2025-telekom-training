# Architectural Patterns - Part 4
## Klassische und Fundamentale Architekturen

---

## Übersicht: Fundamentale Architektur-Patterns

```
Evolution der Software-Architekturen:

1960s-1970s          1980s-1990s         2000s-2010s        2020s+
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ Monolithic  │ --> │   Modular   │ --> │ Distributed │ --> │Cloud-Native │
│ Batch       │     │ Client-     │     │    SOA      │     │Microservices│
│ Processing  │     │   Server    │     │    ESB      │     │ Serverless  │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘

Mainframe           Desktop Era         Internet Era        Cloud Era
```

**Focus:** Bewährte Patterns die heute noch relevant sind

---

## 1. Service-Oriented Architecture (SOA)

### Architecture Schema
```
SOA - Service-Orientierte Architektur:

┌─────────────────────────────────────────────────────────────┐
│                   Enterprise Service Bus (ESB)              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Routing, Transformation, Orchestration, Security    │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────┬──────────────┬──────────────┬────────────────┘
              │              │              │
    ┌─────────▼────┐ ┌───────▼──────┐ ┌────▼─────────┐
    │   Service    │ │   Service    │ │   Service    │
    │      A       │ │      B       │ │      C       │
    │              │ │              │ │              │
    │ - Contract  │ │ - Contract  │ │ - Contract  │
    │ - Interface │ │ - Interface │ │ - Interface │
    │ - Reusable  │ │ - Reusable  │ │ - Reusable  │
    └──────────────┘ └──────────────┘ └──────────────┘
              │              │              │
    ┌─────────▼────────────────────────────▼─────────┐
    │             Shared Data Repository              │
    └─────────────────────────────────────────────────┘

Protocols: SOAP, WSDL, UDDI | Standards: WS-* specifications
```

---

## 1. SOA - Telekom Use Case

### Enterprise Integration Platform

**Service Landscape:**
- **Customer Service:** SOAP-basierte Kundendaten-Services
- **Billing Service:** Abrechnungs- und Tarif-Services
- **Network Service:** Netzwerk-Status und Konfiguration
- **Order Service:** Bestell- und Fulfillment-Prozesse

**ESB Implementation:**
- **IBM WebSphere ESB** oder **Oracle Service Bus**
- **Message Transformation:** XML zu JSON, Format-Konvertierung
- **Service Registry:** UDDI für Service Discovery
- **Orchestration:** BPEL für Geschäftsprozesse

---

## 1. SOA - Trade-offs

### Vorteile
- **Wiederverwendbare Services unternehmensweit**
- **Standardisierte Interfaces (WSDL)**
- **Lose Kopplung durch Service Contracts**
- **Zentrale Governance und Security**
- **Legacy-System Integration möglich**

### Nachteile
- **ESB wird zum Bottleneck und Single Point of Failure**
- **Hohe Komplexität der WS-* Standards**
- **Performance-Overhead durch XML/SOAP**
- **Vendor Lock-in bei ESB-Produkten**
- **Schwerfällige Änderungen durch zentrale Governance**

### Production Considerations
- **ESB High Availability Clustering**
- **Service Versioning Strategie**
- **Performance Monitoring des ESB**
- **Service Level Agreements (SLAs)**

---

## 1. SOA - When to Use

### Ideal für:
- **Enterprise-Integration erforderlich**
- **Heterogene System-Landschaft**
- **Legacy-System Einbindung**
- **Starke Governance-Anforderungen**
- **Service-Wiederverwendung unternehmensweit**

### Vermeiden wenn:
- **Kleine, homogene Systeme**
- **Low-Latency kritisch**
- **Begrenzte ESB-Expertise**
- **Microservices bereits etabliert**
- **Einfache Point-to-Point Integration ausreichend**

---

## 2. Component-Based Architecture

### Architecture Schema
```
Component-Based Architecture:

┌──────────────────────────────────────────────────────────┐
│                    Application Container                 │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Component   │  │  Component   │  │  Component   │  │
│  │      A       │  │      B       │  │      C       │  │
│  │              │  │              │  │              │  │
│  │ - Interface │  │ - Interface │  │ - Interface │  │
│  │ - Props     │◄─►│ - Props     │◄─►│ - Props     │  │
│  │ - Events    │  │ - Events    │  │ - Events    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │           Component Communication Bus             │  │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │              Shared Component Library             │  │
│  └──────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘

Technologies: COM/DCOM, CORBA, JavaBeans, .NET Components
```

---

## 2. Component-Based - Telekom Use Case

### Modulares CRM System

**Component Structure:**
- **Customer Component:** Kundendaten-Verwaltung
- **Contract Component:** Vertrags-Management
- **Billing Component:** Abrechnungs-Logik
- **Communication Component:** Email/SMS Integration

**Component Integration:**
- **Interface Definition:** Klare Schnittstellen zwischen Components
- **Event Bus:** Für lose gekoppelte Kommunikation
- **Dependency Injection:** Für Component-Verdrahtung
- **Component Registry:** Für Discovery und Lifecycle

---

## 2. Component-Based - Trade-offs

### Vorteile
- **Hohe Wiederverwendbarkeit von Components**
- **Klare Modularisierung**
- **Unabhängige Component-Entwicklung**
- **Einfacher Component-Austausch**
- **Bessere Testbarkeit einzelner Components**

### Nachteile
- **Komplexe Component-Interaktionen**
- **Versionierungs-Herausforderungen**
- **Performance-Overhead durch Indirection**
- **Schwierige Fehlersuche über Components**
- **Component-Kompatibilität muss gewährleistet werden**

### Production Considerations
- **Component Versioning Strategy**
- **Backward Compatibility Requirements**
- **Component Deployment Coordination**
- **Interface Stability Guarantees**

---

## 2. Component-Based - When to Use

### Ideal für:
- **Modulare Desktop-Anwendungen**
- **Wiederverwendbare UI-Components**
- **Team-übergreifende Entwicklung**
- **Produkt-Familien mit gemeinsamen Components**
- **Schrittweise System-Modernisierung**

### Vermeiden wenn:
- **Einfache, monolithische Anwendung**
- **Performance kritischer als Modularität**
- **Kleine Teams ohne Component-Governance**
- **Microservices-Architektur bevorzugt**
- **Rapid Prototyping Phase**

---

## 3. Plugin Architecture

### Architecture Schema
```
Plugin Architecture:

┌───────────────────────────────────────────────────────────┐
│                     Core Application                      │
│  ┌─────────────────────────────────────────────────────┐ │
│  │              Plugin Manager/Registry                 │ │
│  │                                                     │ │
│  │  - Plugin Discovery                                │ │
│  │  - Plugin Loading                                  │ │
│  │  - Lifecycle Management                            │ │
│  │  - Dependency Resolution                           │ │
│  └──────────────────┬──────────────────────────────────┘ │
│                     │                                     │
│  ┌──────────────────▼──────────────────────────────────┐ │
│  │                 Plugin Interface                     │ │
│  │  - Hooks/Extension Points                           │ │
│  │  - Event Subscriptions                              │ │
│  │  - API Access                                       │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────────────────┬──────────────────────────────────┘
                         │
    ┌────────────────────┼────────────────────┐
    │                    │                    │
┌───▼────┐         ┌────▼────┐         ┌─────▼───┐
│Plugin A│         │Plugin B │         │Plugin C │
│        │         │         │         │         │
│-Feature│         │-Feature │         │-Feature │
│-Config │         │-Config  │         │-Config  │
└─────────┘         └──────────┘         └──────────┘

Examples: Eclipse, WordPress, Jenkins, VS Code
```

---

## 3. Plugin Architecture - Telekom Use Case

### Network Monitoring Platform

**Core System:**
- **Monitoring Engine:** Basis-Monitoring-Funktionalität
- **Plugin Manager:** Plugin-Verwaltung und Lifecycle
- **Core API:** Zugriff auf Monitoring-Daten
- **Event System:** Plugin-Kommunikation

**Plugin Examples:**
- **SNMP Plugin:** SNMP-Device Monitoring
- **HTTP Plugin:** Website/API Monitoring
- **Database Plugin:** DB Performance Monitoring
- **Alert Plugin:** Verschiedene Alert-Kanäle (Email, SMS, Slack)

---

## 3. Plugin Architecture - Trade-offs

### Vorteile
- **Erweiterbarkeit ohne Core-Änderungen**
- **Third-Party Entwicklung möglich**
- **Customization für verschiedene Kunden**
- **Modularer Funktionsumfang**
- **Core bleibt schlank und stabil**

### Nachteile
- **Plugin-Kompatibilität bei Core-Updates**
- **Security-Risiken durch Third-Party Plugins**
- **Performance-Impact durch viele Plugins**
- **Debugging über Plugin-Grenzen schwierig**
- **Plugin-Abhängigkeiten komplex**

### Production Considerations
- **Plugin Sandboxing für Security**
- **Plugin Performance Profiling**
- **Version Compatibility Matrix**
- **Plugin Marketplace Governance**

---

## 3. Plugin Architecture - When to Use

### Ideal für:
- **Erweiterbarkeit durch Dritte erforderlich**
- **Customization für verschiedene Kunden**
- **Ecosystem/Marketplace geplant**
- **Core-Funktionalität stabil**
- **IDE oder Tool-Entwicklung**

### Vermeiden wenn:
- **Geschlossenes System gewünscht**
- **Security kritisch, Third-Party riskant**
- **Einfache, statische Funktionalität**
- **Performance-kritische Anwendungen**
- **Begrenzte Plugin-Management Ressourcen**

---

## 4. Pipes and Filters Architecture

### Architecture Schema
```
Pipes and Filters Pattern:

Data Stream Processing Pipeline:

Input               Filter 1            Filter 2            Filter 3          Output
  │                    │                   │                   │                │
  ▼                    ▼                   ▼                   ▼                ▼
┌──────┐  Pipe 1   ┌────────┐  Pipe 2  ┌────────┐  Pipe 3  ┌────────┐  Pipe 4  ┌──────┐
│      │ ────────> │ Parse  │ ───────> │Transform│ ───────> │Validate│ ───────> │      │
│Source│           │ Filter │          │ Filter  │          │ Filter │          │ Sink │
│      │           └────────┘          └─────────┘          └────────┘          │      │
└──────┘                                                                        └──────┘

Each Filter:
- Single Responsibility
- Stateless Processing
- Input/Output Transformation
- Composable & Reusable

Unix Example: cat file.txt | grep "error" | sort | uniq | wc -l
```

---

## 4. Pipes and Filters - Telekom Use Case

### Log Processing Pipeline

**Pipeline Stages:**
- **Source:** Log Files, Syslog, Application Logs
- **Parser Filter:** Parse verschiedene Log-Formate
- **Enrichment Filter:** Füge Metadata hinzu (GeoIP, User Info)
- **Filter Filter:** Entferne unwichtige Einträge
- **Aggregation Filter:** Gruppiere und zähle Events
- **Alert Filter:** Erkenne Anomalien und Muster
- **Sink:** Elasticsearch, S3, Alert System

**Implementation:**
- **Apache Kafka:** Als Pipe zwischen Filters
- **Apache Flink/Spark:** Für Stream Processing
- **Logstash:** Für Log-spezifische Filter

---

## 4. Pipes and Filters - Trade-offs

### Vorteile
- **Hohe Modularität und Wiederverwendbarkeit**
- **Einfache Pipeline-Erweiterung**
- **Parallele Verarbeitung möglich**
- **Filter unabhängig entwickelbar**
- **Klare Datenfluss-Visualisierung**

### Nachteile
- **Overhead durch Daten-Serialisierung**
- **Schwierige Fehlerbehandlung**
- **State Management zwischen Filters komplex**
- **Latenz durch Pipeline-Länge**
- **Debugging des Datenflusses herausfordernd**

### Production Considerations
- **Backpressure Handling**
- **Pipeline Monitoring und Metriken**
- **Error Recovery Strategien**
- **Performance Tuning pro Filter**

---

## 4. Pipes and Filters - When to Use

### Ideal für:
- **Stream Processing Anwendungen**
- **ETL Pipelines**
- **Log Processing**
- **Unix-Philosophy Ansatz**
- **Datenverarbeitungs-Workflows**

### Vermeiden wenn:
- **Interaktive Anwendungen**
- **Bidirektionale Kommunikation nötig**
- **Komplexe State-Management**
- **Low-Latency Requirements**
- **Tightly-coupled Processing**

---

## 5. Blackboard Architecture

### Architecture Schema
```
Blackboard Pattern - Collaborative Problem Solving:

┌──────────────────────────────────────────────────────────┐
│                       Blackboard                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │           Shared Knowledge/Problem Space           │ │
│  │                                                    │ │
│  │  - Current State                                   │ │
│  │  - Partial Solutions                              │ │
│  │  - Constraints                                    │ │
│  │  - Hypotheses                                     │ │
│  └────────────────────────────────────────────────────┘ │
└──────────────────────┬───────────────────────────────────┘
                       │
           ┌───────────┼───────────┐
           │           │           │
    ┌──────▼────┐ ┌────▼────┐ ┌───▼─────┐
    │Knowledge  │ │Knowledge│ │Knowledge│
    │Source 1   │ │Source 2 │ │Source 3 │
    │           │ │         │ │         │
    │-Expert    │ │-Expert  │ │-Expert  │
    │ System    │ │ System  │ │ System  │
    └───────────┘ └─────────┘ └─────────┘
           ▲           ▲           ▲
           └───────────┼───────────┘
                       │
              ┌────────▼────────┐
              │    Controller   │
              │                 │
              │-Scheduling      │
              │-Coordination    │
              └─────────────────┘

Use Cases: AI Systems, Complex Problem Solving, Speech Recognition
```

---

## 5. Blackboard - Telekom Use Case

### Network Problem Diagnosis System

**Blackboard Components:**
- **Problem Space:** Aktuelle Netzwerk-Probleme und Symptome
- **Knowledge Sources:**
  - **Performance Analyzer:** Analysiert Metriken
  - **Log Analyzer:** Untersucht Log-Patterns
  - **Topology Analyzer:** Prüft Netzwerk-Struktur
  - **History Analyzer:** Vergleicht mit bekannten Problemen

**Controller:**
- Aktiviert relevante Knowledge Sources
- Koordiniert Lösungsfindung
- Bewertet Hypothesen
- Konvergiert zu Diagnose

---

## 5. Blackboard - Trade-offs

### Vorteile
- **Komplexe Probleme kollaborativ lösbar**
- **Verschiedene Expertise-Quellen integrierbar**
- **Inkrementelle Lösungsfindung**
- **Flexibles Hinzufügen von Knowledge Sources**
- **Opportunistische Problemlösung**

### Nachteile
- **Keine Garantie für Lösungsfindung**
- **Schwierige Vorhersagbarkeit**
- **Komplexe Koordination notwendig**
- **Performance schwer optimierbar**
- **Debugging sehr herausfordernd**

### Production Considerations
- **Conflict Resolution zwischen Sources**
- **Performance Monitoring der Konvergenz**
- **Timeout-Strategien**
- **Fallback bei Nicht-Konvergenz**

---

## 5. Blackboard - When to Use

### Ideal für:
- **Komplexe Problemlösung (AI, NLP)**
- **Multiple Expertise-Quellen**
- **Opportunistische Problemlösung**
- **Hypothesen-basierte Analyse**
- **Diagnose-Systeme**

### Vermeiden wenn:
- **Deterministische Lösungen erforderlich**
- **Einfache, lineare Prozesse**
- **Zeitkritische Anwendungen**
- **Begrenzte Expertise-Quellen**
- **Vorhersagbare Performance nötig**

---

## 6. Publish-Subscribe Pattern

### Architecture Schema
```
Publish-Subscribe (Pub-Sub) Pattern:

Publishers                  Message Broker                Subscribers
                                                          
┌──────────┐               ┌─────────────┐              ┌──────────┐
│Publisher │──publish────>│   Topic A    │──deliver────>│Subscriber│
│    A     │               └─────────────┘              │    1     │
└──────────┘                      │                     └──────────┘
                                  │                     
┌──────────┐               ┌─────────────┐              ┌──────────┐
│Publisher │──publish────>│   Topic B    │──deliver────>│Subscriber│
│    B     │               └─────────────┘      ┌──────>│    2     │
└──────────┘                      │             │       └──────────┘
                                  │             │       
┌──────────┐               ┌─────────────┐      │       ┌──────────┐
│Publisher │──publish────>│   Topic C    │──deliver────>│Subscriber│
│    C     │               └─────────────┘              │    3     │
└──────────┘                                            └──────────┘

Features:
- Decoupled Publishers and Subscribers
- Topic-based or Content-based Routing
- One-to-Many Communication
- Asynchronous Delivery
```

---

## 6. Pub-Sub - Telekom Use Case

### Customer Notification System

**Topics:**
- **billing.invoice.created:** Neue Rechnung erstellt
- **network.outage.detected:** Netzwerkausfall erkannt
- **customer.package.upgraded:** Tarif-Upgrade durchgeführt
- **maintenance.scheduled:** Wartung geplant

**Publishers:**
- **Billing System:** Publiziert Rechnungs-Events
- **Network Monitor:** Publiziert Netzwerk-Events
- **CRM System:** Publiziert Kunden-Events

**Subscribers:**
- **Email Service:** Abonniert alle Customer-relevanten Topics
- **SMS Service:** Abonniert kritische Alerts
- **Push Notification:** Abonniert App-relevante Events

---

## 6. Pub-Sub - Trade-offs

### Vorteile
- **Lose Kopplung zwischen Komponenten**
- **Dynamisches Hinzufügen von Subscribers**
- **Skalierbare Message-Verteilung**
- **Publisher kennt Subscriber nicht**
- **Asynchrone Kommunikation**

### Nachteile
- **Message Delivery Garantien komplex**
- **Debugging von Message Flows schwierig**
- **Message Ordering nicht immer garantiert**
- **Potential für Message Storms**
- **Dead Letter Queue Management**

### Production Considerations
- **Message Retention Policies**
- **Subscriber Group Management**
- **Message Deduplication**
- **Monitoring von Queue Depths**

---

## 6. Pub-Sub - When to Use

### Ideal für:
- **Event-getriebene Systeme**
- **Lose gekoppelte Kommunikation**
- **One-to-Many Broadcasting**
- **Asynchrone Benachrichtigungen**
- **Dynamische Subscriber**

### Vermeiden wenn:
- **Synchrone Request-Response nötig**
- **Garantierte Message Ordering kritisch**
- **Einfache Point-to-Point Kommunikation**
- **Transaktionale Garantien erforderlich**
- **Begrenzte Message Broker Expertise**

---

## 7. Peer-to-Peer Architecture

### Architecture Schema
```
Peer-to-Peer (P2P) Architecture:

Decentralized Network:

     Peer A ────────────── Peer B
        │                    │
        │    ┌─────────┐     │
        │    │ Peer C  │     │
        │    │         │     │
        │    └────┬────┘     │
        │         │          │
    Peer D ───────┼───────  Peer E
        │         │          │
        │    ┌────▼────┐     │
        │    │ Peer F  │     │
        └────┤         ├─────┘
             └─────────┘

Each Peer:
- Acts as Client and Server
- Direct Communication
- Resource Sharing
- No Central Authority
- Self-Organizing

Types:
- Structured P2P (DHT-based)
- Unstructured P2P (Random connections)
- Hybrid P2P (Super-peers)
```

---

## 7. P2P - Telekom Use Case

### Distributed Content Delivery Network

**P2P Implementation:**
- **Content Distribution:** Software-Updates, Firmware
- **Edge Caching:** Router als Peers für lokales Caching
- **Bandwidth Optimization:** Lokaler Content-Austausch
- **Resilience:** Kein Single Point of Failure

**Peer Roles:**
- **Seeder Peers:** Initiale Content-Quellen
- **Leecher Peers:** Content-Empfänger
- **Relay Peers:** Weiterleitungs-Knoten
- **Index Peers:** Für Content Discovery

---

## 7. P2P - Trade-offs

### Vorteile
- **Hohe Skalierbarkeit durch verteilte Last**
- **Keine zentrale Infrastruktur notwendig**
- **Resilient gegen Ausfälle**
- **Kosteneffizient für Content Distribution**
- **Selbstorganisierend und selbstheilend**

### Nachteile
- **Schwierige Quality of Service Garantien**
- **Security und Trust Issues**
- **Komplexes NAT Traversal**
- **Unvorhersagbare Performance**
- **Schwierige Governance und Kontrolle**

### Production Considerations
- **Peer Discovery Mechanismen**
- **Reputation Systems für Trust**
- **Bandwidth Management**
- **Content Integrity Verification**

---

## 7. P2P - When to Use

### Ideal für:
- **Content Distribution Networks**
- **Dezentrale Systeme**
- **File Sharing Anwendungen**
- **Blockchain/Distributed Ledger**
- **Resilience gegen Ausfälle**

### Vermeiden wenn:
- **Zentrale Kontrolle erforderlich**
- **Garantierte QoS nötig**
- **Security/Trust kritisch**
- **Einfache Client-Server ausreichend**
- **Regulatorische Compliance schwierig**

---

## 8. Master-Slave Architecture

### Architecture Schema
```
Master-Slave (Primary-Secondary) Pattern:

Write Operations                    Read Operations
       │                                   │
       ▼                                   ▼
┌─────────────┐                    ┌──────┴───────┬───────────┐
│             │                    │              │           │
│   Master    │                    ▼              ▼           ▼
│   (Primary) │              ┌──────────┐  ┌──────────┐  ┌──────────┐
│             │─replication─>│ Slave 1  │  │ Slave 2  │  │ Slave 3  │
│  - Writes   │              │(Secondary)│ │(Secondary)│ │(Secondary)│
│  - Updates  │              │          │  │          │  │          │
│  - Deletes  │              │ - Reads  │  │ - Reads  │  │ - Reads  │
└─────────────┘              └──────────┘  └──────────┘  └──────────┘
       │                           ▲             ▲             ▲
       │                           │             │             │
       └───────Replication─────────┴─────────────┴─────────────┘

Variations:
- Single Master, Multiple Slaves
- Multi-Master Replication
- Cascading Replication
```

---

## 8. Master-Slave - Telekom Use Case

### Customer Database Cluster

**Master-Slave Setup:**
- **Master:** Alle Schreib-Operationen (Customer Updates)
- **Slaves:** Lese-Operationen (Customer Queries)
- **Replication:** Asynchrone MySQL Replication
- **Load Balancing:** HAProxy für Read Distribution

**Use Cases:**
- **Write Operations:** Neue Kunden, Updates, Löschungen
- **Read Operations:** Customer Portal, Reports, Analytics
- **Backup:** Slaves für Backup ohne Master-Impact
- **Failover:** Slave Promotion bei Master-Ausfall

---

## 8. Master-Slave - Trade-offs

### Vorteile
- **Read-Skalierung durch mehrere Slaves**
- **Einfache Backup-Strategie**
- **Klare Schreib-Konsistenz**
- **Load Distribution für Reads**
- **Failover-Möglichkeiten**

### Nachteile
- **Master ist Single Point of Failure**
- **Replication Lag möglich**
- **Schreib-Skalierung limitiert**
- **Komplexes Failover bei Master-Ausfall**
- **Eventual Consistency für Reads**

### Production Considerations
- **Monitoring von Replication Lag**
- **Automated Failover Procedures**
- **Read-after-Write Consistency Strategien**
- **Split-Brain Prevention**

---

## 8. Master-Slave - When to Use

### Ideal für:
- **Read-Heavy Workloads**
- **Geografisch verteilte Reads**
- **Backup ohne Production Impact**
- **Einfache Skalierung von Reads**
- **Klare Write-Konsistenz erforderlich**

### Vermeiden wenn:
- **Write-Heavy Workloads**
- **Starke Read-Consistency nötig**
- **Multi-Master erforderlich**
- **Zero Replication Lag kritisch**
- **Komplexe Failover unerwünscht**

---

## 9. Broker Architecture

### Architecture Schema
```
Broker Pattern - Service Mediation:

┌──────────────────────────────────────────────────────────┐
│                      Service Broker                       │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │            Service Registry & Discovery            │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │               Request Routing                      │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │          Protocol & Message Translation            │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │            Security & Authentication               │ │
│  └────────────────────────────────────────────────────┘ │
└──────────┬─────────────────┬─────────────────┬──────────┘
           │                 │                 │
    ┌──────▼──────┐   ┌──────▼──────┐  ┌──────▼──────┐
    │  Service A  │   │  Service B  │  │  Service C  │
    └─────────────┘   └─────────────┘  └─────────────┘

Examples: CORBA, Message Brokers, API Gateways
```

---

## 9. Broker - Telekom Use Case

### Service Integration Platform

**Broker Functions:**
- **Service Registry:** Alle verfügbaren Services
- **Protocol Translation:** REST zu SOAP, HTTP zu MQ
- **Message Routing:** Request-basiertes Routing
- **Load Balancing:** Verteilung auf Service-Instanzen

**Integrated Services:**
- **Legacy Systems:** Mainframe-Services via MQ
- **Modern APIs:** REST-basierte Microservices
- **External Partners:** B2B Service Integration
- **Cloud Services:** AWS/Azure Service Integration

---

## 9. Broker - Trade-offs

### Vorteile
- **Zentrale Service-Verwaltung**
- **Protocol-Übersetzung möglich**
- **Service Location Transparency**
- **Vereinfachte Client-Implementierung**
- **Zentrale Security und Governance**

### Nachteile
- **Broker als Bottleneck und Single Point of Failure**
- **Zusätzliche Latenz durch Broker**
- **Komplexität der Broker-Implementierung**
- **Schwierige Skalierung des Brokers**
- **Vendor Lock-in bei kommerziellen Brokern**

### Production Considerations
- **Broker High Availability Setup**
- **Performance Monitoring und Tuning**
- **Service Circuit Breakers**
- **Request Rate Limiting**

---

## 9. Broker - When to Use

### Ideal für:
- **Service Integration Platform**
- **Protocol Translation erforderlich**
- **Zentrale Service Registry**
- **Location Transparency gewünscht**
- **Heterogene Service-Landschaft**

### Vermeiden wenn:
- **Direct Service Communication möglich**
- **Low-Latency kritisch**
- **Einfache Service-Landschaft**
- **Microservices mit Service Mesh**
- **Broker wird zum Bottleneck**

---

## 10. Model-View-Controller (MVC)

### Architecture Schema
```
MVC Pattern - Separation of Concerns:

                    User Interaction
                           │
                           ▼
                    ┌─────────────┐
                    │    View     │
                    │             │
                    │ - UI        │◄─── Display
                    │ - Templates │
                    └──────┬──────┘
                           │
                    User Actions
                           │
                           ▼
                    ┌─────────────┐
                    │ Controller  │
                    │             │
                    │ - Routes    │
                    │ - Actions   │
                    │ - Logic     │
                    └──────┬──────┘
                           │
                    Updates/Queries
                           │
                           ▼
                    ┌─────────────┐
                    │    Model    │
                    │             │
                    │ - Data      │
                    │ - Business  │
                    │   Logic     │
                    │ - State     │
                    └─────────────┘
                           │
                    Notify Changes
                           │
                           ▼
                      View Updates

Variations: MVP, MVVM, MVI
```

---

## 10. MVC - Telekom Use Case

### Customer Self-Service Web Portal

**MVC Implementation:**
- **Model:** Customer, Contract, Invoice Entities
- **View:** HTML Templates, React Components
- **Controller:** Request Handler, Business Logic Coordinator

**Framework Examples:**
- **Java:** Spring MVC
- **Python:** Django
- **Ruby:** Rails
- **JavaScript:** Angular, Express.js

**Workflow:**
- User Request → Controller
- Controller → Model Query/Update
- Model → Data Changes
- Controller → View Selection
- View → Response Generation

---

## 10. MVC - Trade-offs

### Vorteile
- **Klare Trennung der Verantwortlichkeiten**
- **Parallele Entwicklung möglich**
- **Wiederverwendbare Views und Models**
- **Einfaches Testing der Komponenten**
- **Etabliertes Pattern mit viel Tooling**

### Nachteile
- **Komplexität für einfache Anwendungen**
- **Enge Kopplung zwischen View und Controller**
- **Fat Controller Anti-Pattern möglich**
- **Multiple Views schwierig zu koordinieren**
- **Learning Curve für Einsteiger**

### Production Considerations
- **View Caching Strategien**
- **Controller Action Filters**
- **Model Validation Framework**
- **Session State Management**

---

## 10. MVC - When to Use

### Ideal für:
- **Web-Anwendungen**
- **GUI-basierte Desktop Apps**
- **CRUD-heavy Applications**
- **Rapid Application Development**
- **Teams mit klarer Rollentrennung**

### Vermeiden wenn:
- **Sehr einfache Anwendungen**
- **Real-time Updates kritisch**
- **Event-Driven Architecture bevorzugt**
- **API-only Backend**
- **Reactive Programming bevorzugt**

---

## Architecture Decision Matrix - Part 4

| Criteria | SOA | Component | Plugin | P&F | Blackboard | Pub-Sub |
|----------|-----|-----------|--------|-----|------------|---------|
| **Team Size** | 5+ | 3-8 | 2-5 | 2-4 | 3-6 | 3-8 |
| **Complexity** | High | Medium | Low | Low | High | Medium |
| **Flexibility** | High | High | Very High | Medium | High | High |
| **Performance** | Low | Medium | Medium | High | Variable | High |
| **Scalability** | Medium | Medium | Low | High | Low | High |
| **Maintenance** | High | Medium | Medium | Low | High | Medium |

---

## Pattern Selection Guide - Part 4

### Wann welches klassische Pattern?

**SOA verwenden wenn:**
- Enterprise-weite Service Integration
- Heterogene System-Landschaft
- Starke Governance Requirements
- Legacy System Integration

**Component-Based wenn:**
- Modulare Desktop-Anwendungen
- Wiederverwendbare Business Components
- Team-übergreifende Entwicklung

**Plugin Architecture wenn:**
- Erweiterbarkeit durch Dritte
- Customization Requirements
- Marketplace/Ecosystem geplant

**Pipes & Filters wenn:**
- Stream Processing
- ETL Pipelines
- Unix-Philosophy Ansatz

---

## Anti-Pattern Warnung - Part 4

### Häufige Fehler bei klassischen Patterns

**SOA Anti-Patterns:**
- **ESB als Monster:** Zu viel Logic im ESB
- **Chatty Services:** Zu granulare Services
- **Distributed Monolith:** Services zu eng gekoppelt

**Component Anti-Patterns:**
- **Component Sprawl:** Zu viele kleine Components
- **Circular Dependencies:** Components referenzieren sich gegenseitig
- **Version Hell:** Inkompatible Component-Versionen

**Plugin Anti-Patterns:**
- **Plugin Chaos:** Keine Quality Control
- **Core Pollution:** Core wird durch Plugin-Needs aufgebläht
- **Security Nightmare:** Unsichere Third-Party Plugins

**Master-Slave Anti-Patterns:**
- **Split Brain:** Mehrere Master nach Netzwerk-Partition
- **Replication Storm:** Kaskadierendes Replication Failure
- **Read-Write Confusion:** Writes auf Slaves

---

## Migration von klassisch zu modern

### Evolution Paths

**Von SOA zu Microservices:**
- ESB eliminieren
- Services autonomer machen
- Event-Driven Communication
- Service Mesh statt ESB

**Von Monolith zu Component-Based:**
- Module identifizieren
- Interfaces definieren
- Schrittweise Extraktion
- Component Registry einführen

**Von Client-Server zu Cloud:**
- Stateless machen
- Horizontal skalierbar
- Container-ready
- Cloud-Native Services nutzen

**Von Master-Slave zu Multi-Master:**
- Conflict Resolution einführen
- Eventually Consistent Design
- CRDT für Konflikt-freie Replikation

---

## Zusammenfassung - Part 4

### Key Takeaways für klassische Architekturen:

**Bewährte Patterns bleiben relevant:**
- MVC für Web-Anwendungen
- Pub-Sub für Event-Systeme
- Pipes & Filters für Datenverarbeitung
- Plugin für Erweiterbarkeit

**Evolution statt Revolution:**
- Klassische Patterns als Basis
- Moderne Patterns als Erweiterung
- Pragmatische Kombination
- Schrittweise Migration

**Telekom-spezifische Empfehlungen:**
- **SOA** für Legacy Integration
- **Pub-Sub** für Event Distribution
- **Master-Slave** für Database Scaling
- **Plugin** für erweiterbare Plattformen

**Die Kunst liegt in der richtigen Kombination klassischer und moderner Patterns für optimale Lösungen.**