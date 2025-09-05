# Pattern-Architektur Mapping - Telekom Network Management Platform

## Executive Summary
Diese Dokumentation zeigt, welche Design Patterns in welchen Architektur-Patterns am besten platziert werden und wie sie synergetisch zusammenwirken.

## Pattern-zu-Architektur Mapping Matrix

### Fundamental Mapping
```
             Layered  Hexagonal  Microsvcs  Event-Driven  CQRS  Event-Src
             ┌───────┐ ┌─────────┐ ┌─────────┐ ┌─────────────┐ ┌─────┐ ┌──────────┐
Singleton    │   ✓   │ │    ✓    │ │    X    │ │     X      │ │  X  │ │    X     │
Factory      │   ✓   │ │   ✓✓    │ │   ✓✓   │ │    ✓     │ │ ✓✓ │ │   ✓✓    │
Builder      │   ✓   │ │   ✓✓    │ │   ✓    │ │    ✓     │ │ ✓  │ │   ✓     │
Adapter      │   ✓   │ │   ✓✓✓   │ │   ✓✓   │ │    ✓     │ │ ✓  │ │   ✓     │
Decorator    │   ✓   │ │   ✓✓    │ │   ✓✓   │ │    ✓     │ │ ✓  │ │   ✓     │
Facade       │  ✓✓   │ │    ✓    │ │   ✓✓✓  │ │    ✓     │ │ ✓  │ │   ✓     │
Observer     │   ✓   │ │    ✓    │ │   ✓✓   │ │   ✓✓✓    │ │ ✓✓ │ │   ✓✓✓   │
Strategy     │   ✓   │ │   ✓✓    │ │   ✓✓   │ │    ✓     │ │ ✓✓ │ │   ✓     │
Command      │   ✓   │ │   ✓✓    │ │   ✓    │ │   ✓✓     │ │ ✓✓ │ │   ✓✓✓   │
Mediator     │   ✓   │ │    ✓    │ │   ✓✓✓  │ │   ✓✓✓    │ │ ✓  │ │   ✓     │
Memento      │   ✓   │ │    ✓    │ │   ✓    │ │    ✓     │ │ ✓  │ │   ✓✓✓   │
Visitor      │   ✓   │ │   ✓✓    │ │   ✓    │ │    ✓     │ │ ✓✓ │ │   ✓     │
Iterator     │   ✓   │ │   ✓✓    │ │   ✓    │ │    ✓     │ │ ✓✓ │ │   ✓     │
Interpreter  │   ✓   │ │   ✓✓    │ │   ✓    │ │    ✓     │ │ ✓  │ │   ✓     │
             └───────┘ └─────────┘ └─────────┘ └─────────────┘ └─────┘ └──────────┘

Legende:
✓   = Gut geeignet
✓✓  = Sehr gut geeignet  
✓✓✓ = Perfektes Match
X   = Nicht empfohlen
```

## Architektur-spezifische Pattern-Platzierung

### 1. Layered Architecture - Pattern Placement

#### Pattern Distribution
```
Layered Architecture Pattern Mapping:

┌─────────────────────────────────────────────────────┐
│              Presentation Layer              │
│                                             │
│ Patterns:                                   │
│ - Facade (API Simplified Interface)        │
│ - Decorator (Cross-cutting Concerns)       │
│ - Observer (UI Updates)                    │
├─────────────────────────────────────────────────────┤
│               Business Layer               │
│                                             │
│ Patterns:                                   │
│ - Strategy (Business Rules)                │
│ - Command (Business Operations)            │
│ - Factory (Domain Object Creation)         │
│ - Visitor (Domain Processing)              │
│ - Singleton (Business Configuration)       │
├─────────────────────────────────────────────────────┤
│             Persistence Layer             │
│                                             │
│ Patterns:                                   │
│ - Builder (Complex Queries)                │
│ - Adapter (Different Data Sources)         │
│ - Iterator (Data Traversal)                │
│ - Memento (State Snapshots)                │
└─────────────────────────────────────────────────────┘
```

**Telekom Beispiel - Device Management Service:**

```java
// Presentation Layer: Facade Pattern
@RestController
public class DeviceManagementFacade {
    // Simplified interface for complex subsystems
    public DeviceStatus getDeviceOverview(String deviceId) {
        return DeviceStatus.builder()
            .basicInfo(deviceService.getDevice(deviceId))
            .configuration(configService.getCurrentConfig(deviceId))
            .metrics(monitoringService.getLatestMetrics(deviceId))
            .alerts(alertService.getActiveAlerts(deviceId))
            .build();
    }
}

// Business Layer: Strategy Pattern
public class ConfigurationManager {
    private final Map<DeviceType, ConfigurationStrategy> strategies;
    
    public void applyConfiguration(Device device, Configuration config) {
        ConfigurationStrategy strategy = strategies.get(device.getType());
        strategy.apply(device, config);
    }
}

// Persistence Layer: Builder Pattern
public class DeviceQueryBuilder {
    public Query buildComplexQuery() {
        return Query.builder()
            .select("device_id", "status", "config")
            .from("devices")
            .where("status", "ACTIVE")
            .join("configurations", "device_id")
            .orderBy("last_updated")
            .build();
    }
}
```

### 2. Hexagonal Architecture - Pattern Placement

#### Hexagonal Pattern Distribution
```
Hexagonal Architecture Pattern Mapping:

    Driving Adapters          Core Domain         Driven Adapters
┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
│ REST API (Adapter)   │  │  Domain Core        │  │ Database (Adapter)  │
│ Web UI (Adapter)     │  │                     │  │ External API        │
│ CLI (Adapter)        │  │ Patterns:           │  │ (Adapter)           │
│                      │  │ - Factory (Domain)  │  │ Message Queue       │
│ Patterns:            │  │ - Strategy (Rules)  │  │ (Adapter)           │
│ - Adapter (Protocol) │  │ - Command (UseCase) │  │                     │
│ - Facade (Interface) │  │ - Visitor (Domain)  │  │ Patterns:           │
│ - Decorator (Aspect) │  │ - Builder (Domain)  │  │ - Adapter (Tech)    │
│                      │  │                     │  │ - Builder (Queries) │
└─────────────────────┘  └─────────────────────┘  │ - Memento (State)   │
         │                         │                         └─────────────────────┘
         │      Ports (Interfaces)      │
         └─────────────────────────────────┘
```

**Telekom Beispiel - Network Configuration Service:**

```java
// Core Domain: Factory + Strategy Pattern
public class NetworkConfigurationService {
    private final ConfigurationFactory configFactory;     // Factory for domain objects
    private final Map<DeviceType, ValidationStrategy> validators; // Strategy for rules
    
    public ConfigurationResult applyConfiguration(Device device, ConfigRequest request) {
        // Domain logic - pure, testable
        Configuration config = configFactory.create(request);
        ValidationStrategy validator = validators.get(device.getType());
        
        ValidationResult validation = validator.validate(device, config);
        if (!validation.isValid()) {
            return ConfigurationResult.failed(validation.getErrors());
        }
        
        return ConfigurationResult.success(config);
    }
}

// Driving Adapter: REST API with Facade
@RestController
public class ConfigurationApiAdapter {
    private final NetworkConfigurationService service; // Port interface
    
    @PostMapping("/devices/{id}/config")
    public ResponseEntity<ConfigResult> configure(@PathVariable String id, @RequestBody ConfigDto dto) {
        // Adapter converts HTTP to domain
        Device device = deviceRepository.findById(id);
        ConfigRequest request = ConfigRequest.fromDto(dto);
        
        ConfigurationResult result = service.applyConfiguration(device, request);
        return ResponseEntity.ok(ConfigResult.fromDomain(result));
    }
}

// Driven Adapter: Database with Builder
@Repository
public class PostgreSQLConfigurationRepository implements ConfigurationRepository {
    
    public List<Configuration> findByDevice(String deviceId) {
        // Builder for complex queries
        Query query = QueryBuilder.create()
            .select("config_data", "created_at", "applied_at")
            .from("device_configurations")
            .where("device_id", deviceId)
            .where("status", "ACTIVE")
            .orderBy("created_at", DESC)
            .build();
        
        return jdbcTemplate.query(query.getSql(), this::mapToConfiguration);
    }
}
```

### 3. Microservices Architecture - Pattern Placement

#### Service-Level Pattern Distribution
```
Microservices Pattern Mapping:

   Service A           Service B           Service C
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
   │ Device Mgmt     │   │ Config Mgmt     │   │ Monitoring      │
   │                 │   │                 │   │                 │
   │ Patterns:       │   │ Patterns:       │   │ Patterns:       │
   │ - Factory       │   │ - Strategy      │   │ - Observer      │
   │ - Adapter       │   │ - Builder       │   │ - Visitor       │
   │ - Facade        │   │ - Memento       │   │ - Iterator      │
   └───────────────┘   └───────────────┘   └───────────────┘
          │                     │                     │
          └─────────────────────┼─────────────────────┘
                                  │
                        ┌─────────────────────┐
                        │   API Gateway       │
                        │                     │
                        │ Patterns:           │
                        │ - Facade (Unified)  │
                        │ - Decorator (Auth)  │
                        │ - Mediator (Route)  │
                        └─────────────────────┘
```

**Pattern-Specialization pro Service:**

**Device Management Service:**
- **Factory Pattern**: Device-Type-spezifische Creation
- **Adapter Pattern**: verschiedene Device Protocols (SNMP, REST, SSH)
- **Facade Pattern**: Vereinfachte Device Operations API

```java
// Factory Pattern in Device Service
@Component
public class DeviceFactory {
    private final Map<DeviceType, DeviceBuilder> builders;
    
    public NetworkDevice createDevice(DeviceType type, DeviceSpec spec) {
        DeviceBuilder builder = builders.get(type);
        return builder.withSpecification(spec)
                     .withMonitoring(true)
                     .withConfiguration(defaultConfig)
                     .build();
    }
}
```

**Configuration Management Service:**
- **Strategy Pattern**: Configuration Validation per Device Type
- **Builder Pattern**: Complex Configuration Assembly
- **Memento Pattern**: Configuration Rollback

```java
// Strategy Pattern in Config Service
public class ConfigurationValidator {
    private final Map<DeviceType, ValidationStrategy> strategies;
    
    public ValidationResult validate(Device device, Configuration config) {
        ValidationStrategy strategy = strategies.get(device.getType());
        return strategy.validate(device, config);
    }
}
```

**Monitoring Service:**
- **Observer Pattern**: Metric Subscribers
- **Visitor Pattern**: Metric Processing
- **Iterator Pattern**: Time-Series Data Traversal

### 4. Event-Driven Architecture - Pattern Placement

#### Event-Centric Pattern Distribution
```
Event-Driven Pattern Mapping:

    Event Producers        Event Bus         Event Consumers
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│ Device Sensors     │   │   Apache Kafka    │   │ Alert Service   │
│ User Actions       │   │                 │   │ Dashboard       │
│ System Events      │   │ Patterns:       │   │ Analytics       │
│                    │   │ - Mediator      │   │ Audit Log       │
│ Patterns:          │   │ - Observer      │   │                 │
│ - Factory (Events) │   │ - Strategy      │   │ Patterns:       │
│ - Builder (Events) │   │   (Routing)       │   │ - Observer      │
│ - Strategy (Pub)   │   └─────────────────┘   │ - Command       │
└─────────────────┘        │                 │ - Visitor       │
         │              │                 │ - Iterator      │
         └──────────────┴─────────────────┘
                        │
               Event Processing Chain
```

**Telekom Beispiel - Network Event Processing:**

```java
// Event Producer: Factory + Builder Pattern
@Component
public class NetworkEventPublisher {
    private final EventFactory eventFactory;
    
    public void publishDeviceAlert(String deviceId, AlertType type, String details) {
        NetworkEvent event = eventFactory.createEvent(EventType.DEVICE_ALERT)
            .deviceId(deviceId)
            .alertType(type)
            .details(details)
            .timestamp(Instant.now())
            .build();
        
        eventBus.publish(event);
    }
}

// Event Bus: Mediator + Observer Pattern
@Component
public class NetworkEventMediator {
    private final List<EventConsumer> consumers = new CopyOnWriteArrayList<>();
    
    public void publish(NetworkEvent event) {
        // Mediator coordinates event distribution
        consumers.parallelStream()
            .filter(consumer -> consumer.canHandle(event.getType()))
            .forEach(consumer -> consumer.handle(event));
    }
}

// Event Consumer: Command + Visitor Pattern
@EventConsumer
public class AlertingEventConsumer {
    
    public void handle(NetworkEvent event) {
        // Command pattern for alert processing
        AlertCommand command = AlertCommandFactory.createFor(event);
        command.execute();
        
        // Visitor pattern for event-specific processing
        event.accept(new AlertProcessingVisitor());
    }
}
```

### 5. CQRS Architecture - Pattern Placement

#### Command/Query Pattern Distribution
```
CQRS Pattern Mapping:

    Command Side              Event Store           Query Side
┌──────────────────────┐    ┌─────────────────┐    ┌──────────────────────┐
│ Write Model          │    │   Events       │    │ Read Model         │
│                      │    │                 │    │                    │
│ Patterns:            │    │ Patterns:       │    │ Patterns:          │
│ - Command (CQRS)     │    │ - Memento       │    │ - Builder (Query)  │
│ - Factory (Aggr)     │    │ - Observer      │    │ - Visitor (Proj)   │
│ - Strategy (Valid)   │    │ - Iterator      │    │ - Strategy (View)  │
│ - Builder (Complex)  │───>│ - Factory       │───>│ - Observer (Sub)   │
└──────────────────────┘    └─────────────────┘    └──────────────────────┘
         │                              │                         │
         └──────── Eventual Consistency ────────┘
```

**Telekom Beispiel - Network Configuration CQRS:**

```java
// Command Side: Command + Factory Pattern
@Component
public class ConfigurationCommandHandler {
    private final DeviceAggregateFactory aggregateFactory;
    
    public CommandResult handle(ApplyConfigurationCommand command) {
        // Command pattern for write operations
        DeviceAggregate aggregate = aggregateFactory.load(command.getDeviceId());
        
        aggregate.applyConfiguration(command.getConfiguration());
        
        List<DomainEvent> events = aggregate.getUncommittedEvents();
        eventStore.save(events);
        
        return CommandResult.success();
    }
}

// Query Side: Builder + Strategy Pattern
@Component
public class DeviceQueryService {
    private final Map<QueryType, QueryStrategy> queryStrategies;
    
    public DeviceView getDeviceOverview(String deviceId) {
        // Builder for complex read model assembly
        return DeviceView.builder()
            .basicInfo(deviceRepo.findBasicInfo(deviceId))
            .currentConfig(configRepo.findCurrent(deviceId))
            .metrics(metricsRepo.findLatest(deviceId))
            .alerts(alertRepo.findActive(deviceId))
            .build();
    }
    
    public List<DeviceView> search(DeviceQuery query) {
        // Strategy pattern for different query types
        QueryStrategy strategy = queryStrategies.get(query.getType());
        return strategy.execute(query);
    }
}

// Event Store: Memento + Iterator Pattern
@Repository
public class EventStore {
    
    public List<DomainEvent> loadEvents(String aggregateId, long fromVersion) {
        // Iterator pattern for event stream traversal
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

## Pattern Synergy Matrix

### Pattern-Kombinationen für optimale Wirkung

```
Pattern Synergy Combinations:

             Factory  Builder  Adapter  Observer  Strategy  Command  Mediator
             ┌───────┐ ┌───────┐ ┌───────┐ ┌────────┐ ┌────────┐ ┌───────┐ ┌────────┐
Factory      │   -   │ │  ✓✓✓  │ │   ✓   │ │   ✓✓   │ │  ✓✓✓   │ │   ✓   │ │   ✓    │
Builder      │  ✓✓✓  │ │   -   │ │   ✓   │ │   ✓    │ │   ✓    │ │  ✓✓  │ │   ✓    │
Adapter      │   ✓   │ │   ✓   │ │   -   │ │   ✓    │ │  ✓✓    │ │   ✓   │ │  ✓✓    │
Observer     │   ✓✓  │ │   ✓   │ │   ✓   │ │    -    │ │   ✓    │ │  ✓✓  │ │  ✓✓✓   │
Strategy     │  ✓✓✓  │ │   ✓   │ │  ✓✓  │ │   ✓    │ │    -    │ │  ✓✓  │ │   ✓    │
Command      │   ✓   │ │  ✓✓  │ │   ✓   │ │  ✓✓    │ │  ✓✓    │ │   -   │ │  ✓✓✓   │
Mediator     │   ✓   │ │   ✓   │ │  ✓✓  │ │  ✓✓✓    │ │   ✓    │ │  ✓✓✓ │ │    -    │
Memento      │   ✓   │ │  ✓✓  │ │   ✓   │ │   ✓    │ │   ✓    │ │  ✓✓✓ │ │   ✓    │
Visitor      │   ✓   │ │   ✓   │ │   ✓   │ │   ✓    │ │  ✓✓    │ │   ✓   │ │   ✓    │
Iterator     │   ✓   │ │  ✓✓  │ │   ✓   │ │  ✓✓    │ │   ✓    │ │   ✓   │ │   ✓    │
             └───────┘ └───────┘ └───────┘ └────────┘ └────────┘ └───────┘ └────────┘

Legende:
✓✓✓ = Perfect Synergy (Patterns verstärken sich gegenseitig)
✓✓  = Good Synergy (Patterns ergänzen sich gut)
✓   = Compatible (Patterns arbeiten zusammen)
-   = Same Pattern
```

### Top Pattern-Kombinationen für Telekom Use Cases

#### 1. Factory + Builder + Strategy (Perfect Synergy)
**Use Case:** Complex Device Configuration

```java
// Synergistische Pattern-Kombination
public class NetworkDeviceOrchestrator {
    
    // Factory creates appropriate builders
    private final DeviceBuilderFactory builderFactory;
    // Strategy selects configuration approach
    private final Map<DeviceType, ConfigurationStrategy> configStrategies;
    
    public NetworkDevice createAndConfigureDevice(DeviceSpec spec, NetworkRequirements requirements) {
        // 1. Factory Pattern - Creates right builder for device type
        DeviceBuilder builder = builderFactory.createBuilder(spec.getDeviceType());
        
        // 2. Builder Pattern - Assembles complex device configuration
        NetworkDevice device = builder
            .withSpecification(spec)
            .withNetworkRequirements(requirements)
            .withMonitoring(true)
            .withSecurityProfile(requirements.getSecurityLevel())
            .build();
        
        // 3. Strategy Pattern - Applies device-type-specific configuration
        ConfigurationStrategy strategy = configStrategies.get(spec.getDeviceType());
        Configuration config = strategy.generateConfiguration(device, requirements);
        
        device.applyConfiguration(config);
        return device;
    }
}
```

**Synergy Benefits:**
- Factory eliminiert if-else chains für Builder-Selection
- Builder ermöglicht flexible Object Creation für Factory
- Strategy ermöglicht austauschbare Algorithms für Builder

#### 2. Observer + Mediator + Command (Event Processing)
**Use Case:** Network Event Processing System

```java
// Multi-Pattern Event Processing
public class NetworkEventProcessingSystem {
    
    // Mediator coordinates between components
    private final NetworkEventMediator eventMediator;
    
    // Observer pattern for event notifications
    private final List<EventObserver> eventObservers;
    
    // Command pattern for event processing actions
    private final Map<EventType, EventProcessor> eventProcessors;
    
    public void processNetworkEvent(NetworkEvent event) {
        // 1. Mediator Pattern - Central coordination point
        eventMediator.handleEvent(event);
    }
}

@Component
public class NetworkEventMediator {
    
    public void handleEvent(NetworkEvent event) {
        // 2. Observer Pattern - Notify all interested observers
        notifyObservers(event);
        
        // 3. Command Pattern - Execute appropriate processing command
        EventProcessingCommand command = commandFactory.createCommand(event);
        command.execute();
    }
    
    private void notifyObservers(NetworkEvent event) {
        eventObservers.parallelStream()
            .filter(observer -> observer.interestedIn(event.getType()))
            .forEach(observer -> observer.onEvent(event));
    }
}
```

**Synergy Benefits:**
- Mediator reduziert Observer coupling complexity
- Observer ermöglicht flexible Event distribution für Mediator
- Command ermöglicht undo/redo und async processing

#### 3. Adapter + Decorator + Facade (Integration Layer)
**Use Case:** Legacy System Integration

```java
// Integration Pattern Combination
public class LegacySystemIntegrationFacade {
    
    // Facade provides simplified interface
    public DeviceData getDeviceInformation(String deviceId) {
        
        // 1. Adapter Pattern - Convert legacy protocols
        LegacyDevice legacyDevice = snmpAdapter.getDevice(deviceId);
        
        // 2. Decorator Pattern - Add monitoring and caching
        MonitoredDevice monitoredDevice = new MonitoringDecorator(
            new CachingDecorator(legacyDevice)
        );
        
        // 3. Facade Pattern - Provide unified interface
        return DeviceData.builder()
            .basicInfo(monitoredDevice.getBasicInfo())
            .status(monitoredDevice.getStatus())
            .metrics(monitoredDevice.getMetrics())
            .build();
    }
}

// Adapter for SNMP legacy devices
public class SnmpDeviceAdapter implements NetworkDevice {
    private final SnmpClient snmpClient;
    
    public DeviceStatus getStatus() {
        // Convert SNMP response to domain model
        SnmpResponse response = snmpClient.get(deviceIp, STATUS_OID);
        return DeviceStatus.fromSnmpResponse(response);
    }
}

// Decorator for monitoring
public class MonitoringDecorator extends NetworkDeviceDecorator {
    
    public DeviceStatus getStatus() {
        long startTime = System.currentTimeMillis();
        try {
            DeviceStatus status = super.getStatus();
            recordMetric("device.status.success", System.currentTimeMillis() - startTime);
            return status;
        } catch (Exception e) {
            recordMetric("device.status.error", System.currentTimeMillis() - startTime);
            throw e;
        }
    }
}
```

**Synergy Benefits:**
- Facade vereinfacht Adapter+Decorator complexity für Client
- Decorator ermöglicht cross-cutting concerns für Adapter
- Adapter isoliert legacy system complexity von Decorator und Facade

## Anti-Pattern Kombinationen

### Problematische Pattern-Kombinationen vermeiden

```
Anti-Pattern Combinations (Avoid!):

1. Singleton + Factory in Microservices
   Problem: Global state across service boundaries
   Impact:  Service coupling, scaling issues
   
2. Observer + Singleton in Event-Driven
   Problem: Shared mutable state in async environment
   Impact:  Race conditions, unpredictable behavior
   
3. Mediator + Mediator (Nested)
   Problem: Mediator calling another mediator
   Impact:  Complex debugging, circular dependencies
   
4. Command + Observer without clear boundaries
   Problem: Commands triggering observers triggering commands
   Impact:  Infinite loops, performance degradation
```

**Telekom Anti-Pattern Beispiel:**

```java
// ANTI-PATTERN: Singleton Factory in Microservices
public class GlobalDeviceFactory {
    private static GlobalDeviceFactory INSTANCE; // BAD in microservices!
    
    // Problem: Shared state across services
    private final Map<String, Device> deviceCache = new ConcurrentHashMap<>();
    
    public static synchronized GlobalDeviceFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GlobalDeviceFactory();
        }
        return INSTANCE;
    }
    
    // This breaks microservice isolation!
    public Device createDevice(DeviceSpec spec) {
        // Global state shared across services = Bad!
        return deviceCache.computeIfAbsent(spec.getId(), 
            id -> new NetworkDevice(spec));
    }
}

// BETTER: Service-specific Factory
@Component // Spring manages lifecycle
public class DeviceServiceFactory {
    // No global state - service-scoped
    public Device createDevice(DeviceSpec spec) {
        return NetworkDevice.builder()
            .specification(spec)
            .monitoring(true)
            .build();
    }
}
```

## Pattern Placement Decision Matrix

### Decision Framework für Pattern-Architektur Zuordnung

| Kriterium | Layered | Hexagonal | Microservices | Event-Driven | CQRS |
|-----------|---------|-----------|---------------|--------------|------|
| **Object Creation** | Factory in Business Layer | Factory in Domain Core | Factory per Service | Factory for Events | Factory for Commands/Queries |
| **External Integration** | Adapter in Persistence | Adapter as Port Implementation | Adapter per Service | Adapter for Event Conversion | Adapter for Read/Write Models |
| **Business Rules** | Strategy in Business Layer | Strategy in Domain Core | Strategy within Service Boundary | Strategy for Event Processing | Strategy for Command Validation |
| **Communication** | Observer within Layer | Observer through Ports | Observer via Service Mesh | Observer as Event Consumer | Observer for Read Model Updates |
| **Complex Operations** | Command in Business Layer | Command in Use Cases | Command within Service | Command for Event Processing | Command for Write Operations |
| **System Coordination** | Mediator in Business Layer | Mediator in Application Layer | API Gateway as Mediator | Event Bus as Mediator | Event Store as Mediator |

### Telekom-spezifische Placement Guidelines

#### Network Operations Center (NOC)
**Recommended Architecture:** Event-Driven + CQRS
**Key Patterns:**
- **Observer Pattern:** Real-time dashboard updates
- **Command Pattern:** Network configuration changes
- **Mediator Pattern:** Event bus coordination
- **Strategy Pattern:** Alert processing rules

```java
// NOC Pattern Placement Example
@Component
public class NetworkOperationsCenterOrchestrator {
    
    // Event-driven: Observer for real-time updates
    @EventListener
    public void onNetworkEvent(NetworkEvent event) {
        // CQRS: Command for write operations
        if (event.requiresAction()) {
            NetworkCommand command = commandFactory.createFor(event);
            commandBus.send(command);
        }
        
        // Event-driven: Update read models
        updateDashboard(event);
    }
    
    // CQRS: Separate query handling
    public DashboardView getDashboard(String region) {
        return dashboardQueryService.getView(region);
    }
}
```

#### Customer Management System
**Recommended Architecture:** Hexagonal + Event Sourcing
**Key Patterns:**
- **Adapter Pattern:** Legacy system integration
- **Factory Pattern:** Customer aggregate creation
- **Memento Pattern:** Customer state snapshots
- **Command Pattern:** Customer actions

```java
// Customer Management Pattern Placement
public class CustomerManagementService {
    
    // Hexagonal: Domain core with clean interfaces
    private final CustomerRepository customerRepo; // Port
    private final NotificationService notifier;    // Port
    
    // Factory pattern in domain core
    private final CustomerFactory customerFactory;
    
    public void updateCustomerProfile(UpdateCustomerCommand command) {
        // Event Sourcing: Load from events
        Customer customer = customerRepo.load(command.getCustomerId());
        
        // Command pattern: Execute business operation
        customer.updateProfile(command.getProfileData());
        
        // Event Sourcing: Store events
        List<DomainEvent> events = customer.getUncommittedEvents();
        eventStore.save(events);
        
        // Hexagonal: Use port for notifications
        notifier.notifyProfileUpdated(customer.getId());
    }
}
```

## Zusammenfassung

Pattern-Architektur Mapping folgt klaren Prinzipien:

**Layer-Based Architectures:** Patterns folgen Layer-Grenzen
- Presentation → UI Patterns (Facade, Decorator)
- Business → Domain Patterns (Strategy, Command, Factory)
- Persistence → Data Patterns (Builder, Adapter, Iterator)

**Service-Based Architectures:** Patterns fördern Service-Autonomie
- Microservices → Service-lokale Patterns, externe Integration via Adapter
- Event-Driven → Event-centric Patterns (Observer, Mediator, Command)

**Data-Centric Architectures:** Patterns optimieren Data Operations
- CQRS → Read/Write optimized Patterns
- Event Sourcing → Event-focused Patterns (Memento, Observer, Factory)

**Pattern Synergies nutzen:** Kombiniere Patterns für maximale Effektivität
**Anti-Patterns vermeiden:** Vermeide problematische Kombinationen
**Context-driven:** Wähle Patterns basierend auf Architektur-Kontext

Die richtige Pattern-Platzierung verstärkt die gewählte Architektur und macht das System maintainable, testable und skalierbar.