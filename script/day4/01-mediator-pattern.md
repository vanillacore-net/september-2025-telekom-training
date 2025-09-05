# Tag 4.1: Mediator Pattern - Multi-Device Orchestrierung ohne Communication Spaghetti

## Lernziele
- Mediator Pattern für komplexe System-Integration verstehen
- Communication Spaghetti in Telekom-Umgebungen vermeiden
- Device Orchestration patterns implementieren
- Pattern-Kombinationen mit Observer und Command

## Problem: Communication Explosion

### Der Telekom-Alptraum
```java
// ANTI-PATTERN: Jeder redet mit jedem
class NetworkDevice {
    private List<Router> routers;
    private List<Switch> switches;
    private List<FirewallDevice> firewalls;
    private List<MonitoringSystem> monitors;
    
    public void statusChanged() {
        // O(n²) Communication Horror
        for (Router r : routers) {
            r.updateTopology(this);
            for (Switch s : switches) {
                s.recalculateRoutes(r, this);
                for (FirewallDevice f : firewalls) {
                    f.updateRules(r, s, this);
                    // ... und so weiter bis zur Unendlichkeit
                }
            }
        }
    }
}
```

### Die Realität in großen Telekom-Netzen
- **100+ Device Types** × **1000+ Instanzen** = Communication Chaos
- **Abhängigkeiten**: Jeder Routing-Change triggerst 50+ andere Systeme
- **Debugging**: "Warum ist das Netz langsam?" → 72h Investigation
- **Änderungen**: Ein neuer Device-Type = 200 Code-Stellen anfassen

## Lösung: Mediator Pattern

### Konzept-Übersicht
```java
// Zentrale Koordination statt Peer-to-Peer Chaos
interface NetworkMediator {
    void deviceStatusChanged(NetworkDevice device, DeviceStatus status);
    void routingTableUpdated(Router router, RoutingUpdate update);
    void securityPolicyChanged(SecurityPolicy policy);
}
```

### Production-Ready Implementation

```java
@Component
public class TelekomNetworkOrchestrator implements NetworkMediator {
    
    private final Map<DeviceType, List<DeviceHandler>> handlers = new ConcurrentHashMap<>();
    private final EventBus eventBus;
    private final MetricsCollector metrics;
    private final ConfigurationManager config;
    
    @Override
    public void deviceStatusChanged(NetworkDevice device, DeviceStatus status) {
        // 1. Logging & Metrics
        log.info("Device {} changed to {}", device.getId(), status);
        metrics.recordDeviceEvent(device.getType(), status);
        
        // 2. Koordinierte Reaktion
        DeviceChangeEvent event = new DeviceChangeEvent(device, status);
        
        // 3. Handlers nach Priorität ausführen
        getOrderedHandlers(device.getType())
            .parallelStream()
            .forEach(handler -> {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    log.error("Handler {} failed", handler.getClass(), e);
                    metrics.recordHandlerFailure(handler.getClass());
                }
            });
    }
    
    private List<DeviceHandler> getOrderedHandlers(DeviceType type) {
        return handlers.getOrDefault(type, Collections.emptyList())
            .stream()
            .sorted(Comparator.comparingInt(DeviceHandler::getPriority))
            .collect(Collectors.toList());
    }
}
```

### Handler-Architektur
```java
// Spezifische Handler für verschiedene Concerns
@Component
public class RoutingUpdateHandler implements DeviceHandler {
    
    @Override
    public void handle(DeviceChangeEvent event) {
        if (event.getDevice() instanceof Router && event.isStatusCritical()) {
            // Routing-Tables der betroffenen Segmente updaten
            routingService.recalculateAffectedRoutes(
                ((Router) event.getDevice()).getSegments()
            );
        }
    }
    
    @Override
    public int getPriority() {
        return PRIORITY_HIGH; // Routing hat hohe Priorität
    }
}

@Component  
public class SecurityPolicyHandler implements DeviceHandler {
    
    @Override
    public void handle(DeviceChangeEvent event) {
        if (affectsSecurityPerimeter(event)) {
            firewallService.updateRulesForDeviceChange(event);
            intrusionDetection.reconfigureForTopologyChange(event);
        }
    }
    
    @Override
    public int getPriority() {
        return PRIORITY_MEDIUM; // Nach Routing, vor Monitoring
    }
}
```

## Pattern-Kombination: Mediator + Command

### Command-basierte Orchestrierung
```java
// Commands für komplexe Orchestrierung
public class NetworkReconfigurationMediator extends TelekomNetworkOrchestrator {
    
    private final CommandInvoker commandInvoker;
    
    @Override
    public void deviceStatusChanged(NetworkDevice device, DeviceStatus status) {
        // Komplexe Änderungen als Command-Chain
        if (status == DeviceStatus.FAILED && device.isCritical()) {
            List<Command> recoveryCommands = buildRecoveryPlan(device);
            
            CompoundCommand recovery = new CompoundCommand(
                "Network Recovery for " + device.getId(),
                recoveryCommands
            );
            
            commandInvoker.executeWithRollback(recovery);
        } else {
            super.deviceStatusChanged(device, status);
        }
    }
    
    private List<Command> buildRecoveryPlan(NetworkDevice failedDevice) {
        return Arrays.asList(
            new IsolateDeviceCommand(failedDevice),
            new RerouteTrafficCommand(failedDevice.getTrafficRoutes()),
            new NotifyOperatorsCommand(failedDevice, PRIORITY_HIGH),
            new InitiateFailoverCommand(failedDevice.getBackupDevice())
        );
    }
}
```

## Skalierung und Performance

### Event-driven Mediator
```java
@Component
public class AsyncNetworkMediator implements NetworkMediator {
    
    @Async("networkEventExecutor")
    @Override
    public void deviceStatusChanged(NetworkDevice device, DeviceStatus status) {
        // Non-blocking Event Processing
        CompletableFuture.runAsync(() -> {
            processDeviceChange(device, status);
        }, networkExecutor)
        .exceptionally(throwable -> {
            log.error("Async processing failed", throwable);
            // Fallback zu synchroner Verarbeitung für kritische Events
            if (status.isCritical()) {
                processDeviceChange(device, status);
            }
            return null;
        });
    }
    
    @EventListener
    public void handleBulkDeviceUpdates(BulkDeviceUpdateEvent event) {
        // Batch-Processing für Performance
        event.getDeviceUpdates()
            .stream()
            .collect(groupingBy(DeviceUpdate::getType))
            .forEach((type, updates) -> {
                processBatchForDeviceType(type, updates);
            });
    }
}
```

### Monitoring und Observability
```java
@Component
public class ObservableNetworkMediator implements NetworkMediator {
    
    private final MeterRegistry meterRegistry;
    private final Timer deviceChangeTimer;
    private final Counter failedOperationsCounter;
    
    @Override
    public void deviceStatusChanged(NetworkDevice device, DeviceStatus status) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            processDeviceChange(device, status);
            
            // Success Metrics
            meterRegistry.counter("network.device.changes",
                "device_type", device.getType().name(),
                "status", status.name(),
                "result", "success"
            ).increment();
            
        } catch (Exception e) {
            failedOperationsCounter.increment(
                Tags.of("device_type", device.getType().name())
            );
            throw e;
        } finally {
            sample.stop(deviceChangeTimer);
        }
    }
}
```

## Anti-Patterns und Fallen

### ⚠️ God Mediator Anti-Pattern
```java
// ANTI-PATTERN: Mediator wird zum God Object
class NetworkGodMediator {
    // 5000 Zeilen Code für ALLES
    public void handleEverything(Object anything) {
        if (anything instanceof Router) {
            // 200 Zeilen Routing Logic
        } else if (anything instanceof Switch) {
            // 300 Zeilen Switching Logic  
        } else if (anything instanceof Firewall) {
            // 400 Zeilen Security Logic
        }
        // ... weitere 4100 Zeilen Horror
    }
}
```

**Lösung**: Mediator delegiert an spezialisierte Handler!

### ⚠️ Synchroner Deadlock
```java
// ANTI-PATTERN: Synchrone Circular Dependencies
class BadMediator implements NetworkMediator {
    public void deviceStatusChanged(NetworkDevice device, DeviceStatus status) {
        // Router A fragt Router B, der fragt Router C, der fragt Router A...
        for (NetworkDevice other : getAllDevices()) {
            other.syncStateWith(device); // DEADLOCK!
        }
    }
}
```

**Lösung**: Async Processing + Event Sourcing!

## Hands-On: Telekom Device Orchestration

### Aufgabe 1: Basic Mediator
Implementiert einen `TelekomDeviceMediator` für:
- Router Status Changes
- Switch Port Events  
- Firewall Rule Updates

### Aufgabe 2: Performance Optimization
Erweitert den Mediator um:
- Async Event Processing
- Bulk Operations
- Circuit Breaker für externe Services

### Aufgabe 3: Integration Testing
Schreibt Integration Tests für:
- Device Failure Scenarios
- Network Partition Events
- Recovery Procedures

## Team-Adoption Strategien

### 1. Schrittweise Einführung
```java
// Legacy Code schrittweise umstellen
@Component
public class HybridNetworkMediator {
    
    @Value("${mediator.enabled:false}")
    private boolean mediatorEnabled;
    
    public void deviceStatusChanged(NetworkDevice device, DeviceStatus status) {
        if (mediatorEnabled) {
            newMediatorApproach(device, status);
        } else {
            legacyDirectCommunication(device, status);
        }
    }
}
```

### 2. Metrics für Buy-In
- **Vorher**: Durchschnittlich 3.2 Minuten für Device-Recovery
- **Nachher**: Durchschnittlich 45 Sekunden für Device-Recovery  
- **Code Complexity**: 60% weniger Coupling zwischen Device-Klassen

### 3. Training Workshop
- **Tag 1**: Mediator Theorie + Live Coding
- **Tag 2**: Legacy Code Migration  
- **Tag 3**: Monitoring und Troubleshooting

## Production Readiness Checklist

- [ ] **Error Handling**: Alle Handler haben Exception-Behandlung
- [ ] **Monitoring**: Metrics für alle wichtigen Events
- [ ] **Performance**: Async Processing für nicht-kritische Events
- [ ] **Resilience**: Circuit Breaker für externe Dependencies
- [ ] **Testing**: Integration Tests für alle Szenarien
- [ ] **Documentation**: Handler-Registrierung dokumentiert
- [ ] **Rollback**: Feature Flags für schrittweise Einführung

## Fazit: Mediator für Enterprise-Architektur

### Wann Mediator verwenden?
- **✅ Viele Objekte** kommunizieren miteinander
- **✅ Kommunikation** ist komplex und schlecht definiert  
- **✅ Wiederverwendung** wird durch tight Coupling verhindert
- **✅ Verhalten** zwischen Objekten soll zentral konfiguriert werden

### Wann NICHT verwenden?
- **❌ Simple 1:1** Kommunikation reicht
- **❌ Performance** ist kritischer als Maintainability
- **❌ Team** ist nicht bereit für Event-driven Architecture

### Next Level: Event Sourcing
Der nächste Schritt ist die Kombination von Mediator Pattern mit Event Sourcing für auditierbare Network-Changes und Replay-Fähigkeiten.

**Nächstes Modul**: Iterator & Visitor Patterns für komplexe Datenstrukturen