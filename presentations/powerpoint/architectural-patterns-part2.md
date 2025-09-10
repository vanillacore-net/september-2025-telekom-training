# Architectural Patterns - PART 2
*Enterprise Architecture Patterns für Skalierbarkeit und Integration*

---

## CQRS (Command Query Responsibility Segregation)
*Separate Read and Write Models für Performance-Optimierung*

### Problem
- Read- und Write-Operationen haben unterschiedliche Performance-Anforderungen
- Complex Queries bei hohem Write Throughput problematisch
- Ein Model für alle Use Cases führt zu Kompromissen

### CQRS Pattern
```
Command Side              Event Store           Query Side
┌──────────────────────┐  ┌─────────────────┐  ┌──────────────────────┐
│ Write Model          │  │   Events       │  │ Read Model         │
│                      │  │                 │  │                    │
│ - Normalized         │─>│ Event Bus      │─>│ - Denormalized     │
│ - PostgreSQL         │  │ Command/Query  │  │ - Elasticsearch    │
│ - ACID Transactions  │  │ Sync           │  │ - Read Optimized   │
└──────────────────────┘  └─────────────────┘  └──────────────────────┘
```

---

## CQRS - Telekom Use Case

### Network Device Management System
- **Commands**: Add device, update configuration, assign to network
- **Queries**: Device list, configuration history, network topology
- **Write Model**: Normalized device tables, configuration audit
- **Read Model**: Denormalized views für Dashboard, reports

### Implementation
```java
// Command Side: Command + Factory Pattern
@Component
public class ConfigurationCommandHandler {
    public CommandResult handle(ApplyConfigurationCommand command) {
        DeviceAggregate aggregate = aggregateFactory.load(command.getDeviceId());
        aggregate.applyConfiguration(command.getConfiguration());
        
        List<DomainEvent> events = aggregate.getUncommittedEvents();
        eventStore.save(events);
        return CommandResult.success();
    }
}
```

---

## CQRS - Query Side

### Optimized Read Models
```java
// Query Side: Builder + Strategy Pattern  
@Component
public class DeviceQueryService {
    public DeviceView getDeviceOverview(String deviceId) {
        return DeviceView.builder()
            .basicInfo(deviceRepo.findBasicInfo(deviceId))
            .currentConfig(configRepo.findCurrent(deviceId))
            .metrics(metricsRepo.findLatest(deviceId))
            .alerts(alertRepo.findActive(deviceId))
            .build();
    }
    
    public List<DeviceView> search(DeviceQuery query) {
        QueryStrategy strategy = queryStrategies.get(query.getType());
        return strategy.execute(query);
    }
}
```

---

## CQRS Trade-offs

### Vorteile
✅ **Performance**: Optimized Data Models für Read/Write Operations  
✅ **Skalierung**: Read/Write Operations unabhängig skalierbar  
✅ **Complex Queries**: Performance durch denormalized Read Models  
✅ **Event Sourcing Integration**: Natürliche Kombination möglich  

### Nachteile
❌ **Eventual Consistency**: zwischen Read/Write Models  
❌ **Maintenance**: Doppelte Data Models erhöhen Aufwand  
❌ **Synchronization**: Event Sync zwischen Models komplex  
❌ **Over-Engineering**: für simple CRUD Applications  

### Production Considerations
- Event Store für reliable Command/Query Sync
- Read Model Rebuild Strategy bei Schema Changes
- Monitoring von Read/Write Model Sync Lag

---

## Event Sourcing
*Immutable Event Log als Single Source of Truth*

### Problem
- Traditionelle CRUD: Aktueller State, Historie verloren
- Audit Trail für Compliance Requirements
- Debugging: "Wie kam das System in diesen Zustand?"
- Rollback und Time-Travel schwierig

### Event Sourcing Konzept
```
Commands            Events               Current State
┌───────────┐    ┌─────────────┐      ┌─────────────────┐
│ Create    │───>│ DeviceCreated│───> │                 │
│ Device    │    │   Event     │     │     Current     │
└───────────┘    └─────────────┘     │      State      │
                                     │                 │
┌───────────┐    ┌─────────────┐     │   (Projection   │
│ Configure │───>│ConfigChanged│───> │   from Events)  │
│ Device    │    │   Event     │     │                 │
└───────────┘    └─────────────┘     └─────────────────┘
```

---

## Event Sourcing - Implementation

### Event Store Pattern
```
Event Sourcing Pattern:

  Commands            Events               Projections
┌───────────┐      ┌─────────────────┐      ┌──────────────────┐
│ Commands  │────> │ Event Store    │────> │ Current State   │
│           │      │                 │      │ Projection      │
│           │      │ Immutable       │      │                 │
│           │      │ Append-Only     │      │                 │
└───────────┘      │ Event Log       │      └──────────────────┘
                   └─────────────────┘              ▲
                                                    │
                                       ┌──────────────────┐
                                       │ History         │
                                       │ Projection      │
                                       │                 │
                                       │ Time Travel:    │
                                       │ State at T =    │
                                       │ f(Events until T)│
                                       └──────────────────┘
```

---

## Event Sourcing - Telekom Use Case

### Network Configuration Audit System
- **Events**: Configuration changes, device assignments, policy updates
- **Event Store**: Immutable log of all network changes
- **Projections**: Current network state, compliance reports, change history
- **Benefits**: Full audit trail, time-travel debugging, rollback capability

### Implementation
```java
@Repository
public class EventStore {
    public List<DomainEvent> loadEvents(String aggregateId, long fromVersion) {
        return eventStream.iterator(aggregateId)
            .skip(fromVersion)
            .collect(toList());
    }
    
    public void saveSnapshot(String aggregateId, AggregateSnapshot snapshot) {
        // Memento pattern for aggregate snapshots
        snapshotRepository.save(aggregateId, snapshot.getMemento());
    }
}
```

---

## Event Sourcing Trade-offs

### Vorteile
✅ **Complete Audit Trail**: für Compliance  
✅ **Time Travel**: für Debugging und Analysis  
✅ **Event Replay**: für verschiedene Projections  
✅ **Natural fit**: für Event-Driven Architecture  

### Nachteile  
❌ **Storage Requirements**: wachsen monoton  
❌ **Event Schema Evolution**: challenging  
❌ **Query Performance**: für Complex Projections  
❌ **Learning Curve**: für Traditional SQL Developers  

### Production Considerations
- Event Store Snapshots für Performance
- Event Schema Versioning Strategy
- Projection Rebuild für Schema Changes
- Event Store Backup und Recovery

---

## Circuit Breaker Pattern
*Fault Tolerance für External Service Dependencies*

### Problem
- External Services können ausfallen
- Cascading Failures vermeiden
- Graceful Degradation statt Totalausfall
- System Resilience verbessern

### Circuit Breaker States
```
Circuit Breaker State Machine:

    CLOSED                    OPEN                    HALF-OPEN
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│ Normal          │      │ Service Down    │      │ Testing         │
│ Operation       │─────>│ Fast Fail       │─────>│ Recovery        │
│                 │      │                 │      │                 │
│ Success Rate    │      │ Timeout Period  │      │ Limited Calls   │
│ > Threshold     │      │ Elapsed         │      │                 │
└─────────────────┘      └─────────────────┘      └─────────────────┘
         ▲                                                  │
         └──────────────────────────────────────────────────┘
                         Success Rate > Threshold
```

---

## Circuit Breaker - Implementation

### Resilient Service Integration
```java
@Component
public class ResilientPaymentProvider implements PaymentProvider {
    private final PaymentProvider delegate;
    private final CircuitBreaker circuitBreaker;
    
    public ResilientPaymentProvider(PaymentProvider delegate) {
        this.delegate = delegate;
        this.circuitBreaker = CircuitBreaker.ofDefaults("payment");
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        return circuitBreaker.executeSupplier(() -> {
            try {
                return delegate.processPayment(request);
            } catch (Exception e) {
                throw new PaymentProviderException("Provider temporarily unavailable", e);
            }
        });
    }
}
```

---

## Saga Pattern
*Distributed Transaction Management*

### Problem
- Transaktionen über Service-Grenzen hinweg
- ACID properties in Microservices schwierig
- Rollback-Strategien für verteilte Operationen
- Data Consistency ohne 2-Phase-Commit

### Saga Pattern Types
```
Choreography Saga:           Orchestration Saga:
                            
Service A ──> Service B       ┌─────────────────┐
    │             │          │ Saga Manager   │
    ▼             ▼          │                 │
Service C ──> Service D      │ 1. Service A    │
                             │ 2. Service B    │
Event-driven,                │ 3. Service C    │
Distributed control          │ 4. Rollback?    │
                             └─────────────────┘
                             
                             Centralized control,
                             Explicit workflow
```

---

## Saga Pattern - Telekom Use Case

### Network Configuration Deployment
```
Configuration Deployment Saga:

1. ┌────────────────────────┐
   │ Config Service:        │
   │ CREATE config          │ ──Success──┐
   │ Status: CREATED        │             │
   └────────────────────────┘             ▼
                                 2. ┌────────────────────────┐
                                    │ Device Service:        │
                                    │ RESERVE device         │ ──Success──┐
                                    │ Status: RESERVED       │             │
                                    └────────────────────────┘             ▼
                                                                  3. ┌────────────────────────┐
                                                                     │ Deployment Service:    │
                                                                     │ APPLY config           │
                                                                     │ Status: DEPLOYED/FAIL  │
                                                                     └────────────────────────┘
                                                                              │
                                                               If SUCCESS     │ If FAIL
                                                                              ▼ (Compensation)
4. ┌────────────────────────┐  ┌────────────────────────┐
   │ All Services:          │  │ ROLLBACK:              │
   │ CONFIRM completion     │  │ 1. UNDEPLOY config     │
   │ Status: COMPLETED      │  │ 2. RELEASE device      │
   └────────────────────────┘  │ 3. DELETE config       │
                               └────────────────────────┘
```

---

## API Gateway Pattern
*Unified Entry Point für Microservices*

### Problem
- Clients müssen viele Services direkt ansprechen
- Cross-cutting Concerns (Auth, Logging, Rate Limiting) verteilt
- Service Discovery Complexity für Clients
- API Versioning und Evolution

### API Gateway Architecture
```
API Gateway Pattern:

          Client Requests
               │
               ▼
    ┌─────────────────────┐
    │   API Gateway       │
    │                     │
    │ - Authentication    │
    │ - Authorization     │
    │ - Rate Limiting     │
    │ - Load Balancing    │
    │ - Request Routing   │
    │ - Response Caching  │
    │ - Monitoring        │
    └─────────────────────┘
               │
               ▼
    ┌─────────┬─────────┬─────────┐
    │Service A│Service B│Service C│
    └─────────┴─────────┴─────────┘
```

---

## API Gateway - Implementation

### Gateway Features
```java
@RestController
public class TelekomAPIGateway {
    
    @GetMapping("/api/v1/devices/**")
    @RateLimiter(name = "device-api", fallbackMethod = "deviceFallback")
    @CircuitBreaker(name = "device-service")
    public ResponseEntity<?> routeDeviceRequest(HttpServletRequest request) {
        
        // 1. Authentication & Authorization
        if (!authService.isAuthorized(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // 2. Service Discovery & Load Balancing
        ServiceInstance service = discoveryService.getInstance("device-service");
        
        // 3. Request Routing
        return restTemplate.exchange(
            service.getUri() + extractPath(request),
            HttpMethod.GET,
            new HttpEntity<>(createHeaders(request)),
            Object.class
        );
    }
}
```

---

## Service Mesh
*Infrastructure Layer für Service-to-Service Communication*

### Problem
- Service-to-Service Communication Complexity
- Distributed System Concerns (Circuit Breaker, Retry, etc.)
- Observability über Service-Grenzen hinweg
- Security Policies zwischen Services

### Service Mesh Architecture
```
Service Mesh (Istio):

┌──────────────────────────────────────────────────────┐
│                Control Plane                         │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │
│ │   Pilot     │ │   Citadel   │ │   Mixer     │      │
│ │ (Discovery) │ │ (Security)  │ │ (Telemetry) │      │
│ └─────────────┘ └─────────────┘ └─────────────┘      │
└──────────────────────────────────────────────────────┘
                        │
                        ▼ (Configuration)
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ Service A   │     │ Service B   │     │ Service C   │
│             │     │             │     │             │
│ ┌─────────┐ │◄────┤ ┌─────────┐ │◄────┤ ┌─────────┐ │
│ │ Proxy   │ │     │ │ Proxy   │ │     │ │ Proxy   │ │
│ │(Envoy)  │ │     │ │(Envoy)  │ │     │ │(Envoy)  │ │
│ └─────────┘ │     │ └─────────┘ │     │ └─────────┘ │
└─────────────┘     └─────────────┘     └─────────────┘

Data Plane: Envoy Proxies handle all traffic
Control Plane: Configures and monitors proxies
```

---

## Service Mesh - Benefits

### Infrastructure Concerns
- **Traffic Management**: Load balancing, routing, circuit breaking
- **Security**: mTLS, RBAC, service-to-service authentication
- **Observability**: Distributed tracing, metrics, logging
- **Policy Enforcement**: Rate limiting, access control

### Service Discovery with Service Mesh
```
Service Discovery with Istio:

Client Request -> Envoy Proxy -> Service Instance
┌──────────────┐     ┌───────────────┐     ┌───────────────┐
│ Service A    │◄────│  Envoy Proxy   │◄────│  Service B   │
└──────────────┘     │               │     └───────────────┘
                     │ - Load Balance │
┌──────────────┐     │ - Health Check │
│ Service A    │◄────│ - Circuit Break│
│ Instance 2   │     │ - Retry       │
└──────────────┘     └───────────────┘

Service Registry: Pilot maintains service registry
Health Checks: Automatic health checking
Load Balancing: Round-robin, least-connection, etc.
```

---

## Bulkhead Pattern
*Isolation of Critical Resources*

### Problem
- Ein überlasteter Service kann andere Services beeinträchtigen
- Resource Starvation durch einen problematischen Client
- System Resilience durch Resource Isolation

### Bulkhead Implementation
```
Bulkhead Pattern - Resource Isolation:

Thread Pools per Client Type:
┌─────────────────────────────────────────────────┐
│                Service                          │
│                                                 │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│ │Premium      │ │Standard     │ │Basic        │ │
│ │Clients      │ │Clients      │ │Clients      │ │
│ │             │ │             │ │             │ │
│ │Thread Pool  │ │Thread Pool  │ │Thread Pool  │ │
│ │Size: 50     │ │Size: 30     │ │Size: 20     │ │
│ └─────────────┘ └─────────────┘ └─────────────┘ │
└─────────────────────────────────────────────────┘

Benefit: Basic clients cannot starve Premium clients
```

---

## Domain-Driven Design (DDD) Patterns
*Business-oriented Architecture Design*

### Core DDD Concepts
- **Ubiquitous Language**: Gemeinsame Sprache zwischen Business und Tech
- **Bounded Context**: Klar abgegrenzte Domänen-Bereiche
- **Aggregates**: Transaktional konsistente Einheiten
- **Domain Services**: Domain Logic ohne natürliches Entity Home

### Bounded Context Mapping
```
DDD Bounded Contexts - Telekom:

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Customer        │    │ Billing         │    │ Network         │
│ Management      │    │ Context         │    │ Operations      │
│                 │    │                 │    │                 │
│ - Customer      │◄──►│ - Invoice       │◄──►│ - Device        │
│ - Contract      │    │ - Payment       │    │ - Configuration │
│ - Profile       │    │ - Tariff        │    │ - Monitoring    │
└─────────────────┘    └─────────────────┘    └─────────────────┘

Relationships:
◄──► Anti-Corruption Layer (ACL)
═══► Shared Kernel
───► Customer-Supplier
```

---

## Architecture Decision Framework
*Wann welches Pattern verwenden?*

### Decision Matrix
```
Architecture Complexity vs. Pattern Choice:

   High │                                  ┌─ Event Sourcing
Compl- │                        ┌─ CQRS ──┤
exity  │              ┌─ Microservices ───┘ └─ Service Mesh
       │    ┌─ Event-Driven + API Gateway      
   Med │    │         └─ Saga Pattern
       │    │   
       │ Circuit Breaker + Bulkhead   
   Low │ └─ Layered ──┴─ 3-Tier
       └─────────────────────────────────────────────>
         Simple    Medium    High      Very High
                      Team Size / Domain Complexity
```

### Pattern Selection Criteria
| Criteria | Microservices | Event-Driven | CQRS | Event Sourcing |
|----------|---------------|--------------|------|----------------|
| **Team Size** | 5+ | 3-8 | 4-10 | 6-12 |
| **Scalability** | High | High | Very High | High |
| **Complexity** | Very High | Medium | High | Very High |
| **Performance** | Variable | High | Very High | Medium |
| **Audit Requirements** | Medium | Medium | High | Very High |

---

## Migration Strategies
*Evolution Path für bestehende Systeme*

### Strangler Fig Pattern
```
Legacy System Migration:

Phase 1: Identify Boundaries
┌─────────────────────────────────┐
│         Legacy System           │
│  ┌─────┬─────┬─────┬─────┐     │
│  │ M1  │ M2  │ M3  │ M4  │     │
│  └─────┴─────┴─────┴─────┘     │
└─────────────────────────────────┘

Phase 2: Incremental Replacement
┌─────────────────────────────────┐
│ New │     Legacy System        │
│ Svc │  ┌─────┬─────┬─────┐     │
│ M1  │  │ M2  │ M3  │ M4  │     │
└─────┴──┴─────┴─────┴─────┘     │

Phase 3: Complete Migration
┌─────┬─────┬─────┬─────┐
│New  │New  │New  │New  │
│Svc  │Svc  │Svc  │Svc  │
│M1   │M2   │M3   │M4   │
└─────┴─────┴─────┴─────┘
```

---

## Architecture Evolution Path
*Telekom-spezifische Empfehlungen*

### Evolution Journey
```
Telekom Architecture Evolution:

 Current           Phase 1          Phase 2          Phase 3
┌───────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│ Legacy    │   │  Modular    │   │Microservices│   │ Event Driven│
│ Monolith  │──>│  Monolith   │──>│  + API      │──>│ + CQRS      │
└───────────┘   └─────────────┘   │  Gateway    │   └─────────────┘
                                  └─────────────┘

Timeline:         6 Monate       12 Monate      18-24 Monate
Risk:             Low            Medium         High  
Business Value:   Low            Medium         High
Team Impact:      Minimal        Moderate       Significant
```

### Use Case Mapping
**Für Network Operations (NOC):**
- Event-Driven für Real-time Monitoring
- CQRS für High-Performance Dashboards  
- Circuit Breaker für External Dependencies

**Für Customer Management:**
- Domain-Driven Design für Complex Business Logic
- Event Sourcing für Audit Requirements
- Saga Pattern für Multi-Service Transactions

---

## Anti-Patterns vermeiden
*Häufige Architektur-Fehler*

### Microservices Anti-Patterns
- **Distributed Monolith**: Services zu gekoppelt
- **Chatty Services**: Zu viele Service-to-Service Calls
- **Shared Database**: Verletzt Service-Autonomie
- **Synchronous Coupling**: Services blockieren sich gegenseitig

### Event Sourcing Anti-Patterns
- **Event as State**: Events enthalten kompletten State statt Änderung
- **Big Events**: Events sind zu groß und enthalten zu viel Information
- **Schema Evolution Ignorance**: Keine Versioning-Strategie für Events

### CQRS Anti-Patterns
- **CQRS Everywhere**: CQRS für einfache CRUD-Operations
- **Complex Queries on Write Side**: Query-Logic im Write Model
- **Synchronous Read Model Updates**: Blocking Updates der Read Models

---

## Zusammenfassung
*Key Takeaways für Enterprise Architecture*

### Pattern-Kombinationen nutzen
- **CQRS + Event Sourcing**: Für Audit und Performance
- **Microservices + Service Mesh**: Für Infrastructure Concerns
- **Circuit Breaker + Bulkhead**: Für System Resilience
- **API Gateway + DDD**: Für Business-oriented APIs

### Evolution über Revolution
- Start Simple: Layered Architecture für Prototypen
- Scale Systematically: Microservices für große Teams
- Optimize Specifically: CQRS/Event Sourcing für Performance
- Design for Change: Patterns als Evolution Path nutzen

### Telekom-spezifische Guidance
✅ **Network Operations**: Event-Driven + CQRS für Real-time Performance  
✅ **Customer Management**: DDD + Event Sourcing für Compliance  
✅ **Billing Systems**: Saga Pattern für Distributed Transactions  
✅ **Legacy Integration**: Strangler Fig für graduelle Migration  

**Die richtige Architektur löst aktuelle Probleme und ermöglicht zukünftige Entwicklung.**