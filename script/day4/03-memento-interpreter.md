# Tag 4.3: Memento & Interpreter - State-Recovery und Configuration-DSLs

## Lernziele: Warum diese Pattern-Kombination bei Telekom?
- **Production-Safe Changes**: Network Configuration mit garantiertem Rollback
- **Configuration-as-Code**: DSL für Network Engineers ohne Java-Knowledge
- **Audit-Compliance**: Vollständige Historie aller Configuration-Changes
- **Disaster Recovery**: State-Snapshots für schnelle Wiederherstellung

## Das Problem: Configuration Horror und Missing Rollback

### Warum ist das bei Telekom besonders kritisch?

**Reality Check**: Ein fehlgeschlagener Router-Configuration-Change in einem Telekom-Backbone-Netz:
- **Immediate Impact**: 50.000+ Kunden ohne Internet
- **Cascading Failures**: Nachbar-Router überlastet, weitere Ausfälle
- **Recovery Time**: 4+ Stunden bis zur vollständigen Wiederherstellung
- **Business Impact**: €500.000+ Umsatzverlust, Regulatory Fines, Image-Schaden

**Das Problem**: Keine atomaren Rollback-Mechanismen!

### Configuration Anti-Pattern: No Rollback, No Recovery
```java
// ANTI-PATTERN: Der Configuration-Horror ohne Rollback-Möglichkeiten
class NetworkDeviceConfigurator {
    
    public void applyConfiguration(NetworkDevice device, Configuration newConfig) {
        
        log.info("Applying configuration to device {}", device.getId());
        
        // HORROR 1: Direkte State-Mutations ohne Backup
        device.setRoutingTable(newConfig.getRoutes());           // Was war vorher?
        device.setVlanConfiguration(newConfig.getVlans());       // Unbekannt!
        device.setSecurityPolicies(newConfig.getSecurityPolicies()); // Lost!
        device.setQosSettings(newConfig.getQosSettings());       // Gone!
        device.setInterfaceConfiguration(newConfig.getInterfaces()); // 😱
        
        try {
            // HORROR 2: Point of No Return
            device.commitConfiguration(); // 🎲 All-or-Nothing Gamble
            
            log.info("Configuration applied successfully"); // Famous last words...
            
        } catch (ConfigurationException e) {
            // HORROR 3: We're screwed - no way back!
            log.error("🚨 CRITICAL: Configuration failed, device in UNKNOWN state!", e);
            
            // Was war der vorherige Zustand? ¯\_(\u30c4)_/¯
            // Wie kommen wir zurück? ¯\_(\u30c4)_/¯
            // Wer ruft die Operations an? 📞
            
            // Device ist jetzt in inkonsistentem Zustand:
            // - Routing Table: half-updated
            // - VLANs: might be corrupted  
            // - Security Policies: unknown state
            // - QoS: probably broken
            
            // Manual Recovery Required:
            // 1. 🚑 Emergency Response Team
            // 2. 🔍 2+ hours debugging
            // 3. 📝 Manual config reconstruction
            // 4. 💸 Customer impact: priceless
            
            throw new RuntimeException("Device in unknown state - manual recovery required", e);
        }
    }
}
```

**Was macht das so gefährlich?**
- **No Backup**: Vorheriger Zustand ist unwiderruflich verloren
- **Partial Updates**: Device kann in inkonsistentem Zustand sein
- **No Atomicity**: Entweder alles oder nichts - aber kein sauberer Rollback
- **Manual Recovery**: Stunden von Expert-Zeit für Wiederherstellung

### Die schmerzhaften Konsequenzen

#### 1. Production Outages
- **Frankfurt Backbone Router**: Configuration-Fehler → 30 Minuten Komplettausfall Süddeutschland
- **München Metro Network**: VLAN-Misconfiguration → 15.000 Business-Kunden offline
- **Hamburg Core Switch**: QoS-Policy-Fehler → Massive Latenz-Spikes für Video-Services

#### 2. Audit & Compliance Alptraum
- **Regulatory Fragen**: "Wann wurde diese Firewall-Rule geändert?" → "Wir wissen es nicht"
- **Security Incidents**: "Wer hat BGP-Policy modifiziert?" → Keine Audit-Historie
- **Change Management**: "Warum ist die Latenz hoch?" → Unbekannte Configuration-Changes

#### 3. Multi-Device Coordination Horror
- **Planned Maintenance**: 50 Router-Updates, #47 fails → Rollback aller 49 anderen?
- **Security Patches**: Firewall-Update scheitert → Inconsistent Security-Posture
- **Infrastructure Migration**: Partial failure → Network-Segmentierung

#### 4. Expert-Dependency & MTTR
- **Mean Time To Recovery**: 2-4 Stunden für Configuration-Recovery
- **Expert Bottleneck**: Nur 3-4 Personen können kritische Configs wiederherstellen
- **Documentation Lag**: "Wie war die Config vorher?" → Rekonstruktion aus Memory/Notes

### DSL Configuration Anti-Pattern: String-Parsing Nightmare
```java
// ANTI-PATTERN: Fragile String-based Configuration Hell
public void parseNetworkConfig(String configText) {
    
    log.info("Parsing network configuration: {} lines", configText.split("\n").length);
    
    String[] lines = configText.split("\n");
    
    for (int lineNum = 0; lineNum < lines.length; lineNum++) {
        String line = lines[lineNum].trim();
        
        try {
            if (line.startsWith("route")) {
                // HORROR: Fragile String-Parsing ohne Validation
                String[] parts = line.split(" "); // Was bei Tabs? Extra-Spaces?
                if (parts.length >= 3) { // Was wenn parts.length == 2?
                    String destination = parts[1]; // Was wenn leer?
                    String gateway = parts[2];     // Was wenn invalid IP?
                    
                    // Keine Syntax Validation!
                    addRoute(destination, gateway); // 💣 Potential Bomb
                } else {
                    // Silent failure - wird ignoriert!
                    log.warn("Invalid route syntax at line {}", lineNum);
                }
                
            } else if (line.startsWith("vlan")) {
                // MEHR String-Parsing Horror...
                String[] vlanParts = line.split(" ");
                int vlanId = Integer.parseInt(vlanParts[1]); // NumberFormatException?
                String vlanName = vlanParts.length > 2 ? vlanParts[2] : "unnamed";
                
                addVlan(vlanId, vlanName); // Keine Validation ob VLAN schon existiert
                
            } else if (line.startsWith("acl")) {
                // NOCH MEHR String-Horror...
                parseAclRule(line); // Recursive String-Parsing
                
            } else if (line.startsWith("interface")) {
                // Interface configuration parsing...
                // 100+ weitere Zeilen String-Manipulation
                
            } else if (!line.isEmpty() && !line.startsWith("#")) {
                // Unknown command - was jetzt?
                log.error("Unknown configuration command at line {}: {}", lineNum, line);
                // Sollen wir stoppen? Weitermachen? Exception?
            }
            
        } catch (Exception e) {
            // Generisches Exception Handling
            log.error("Configuration parsing failed at line {}: {}", lineNum, line, e);
            
            // PROBLEM: Soll die ganze Configuration fehlschlagen?
            // Oder nur diese Zeile überspringen?
            // Was ist mit partial Configuration?
        }
    }
    
    log.info("Configuration parsing completed");
}
```

**Die String-Parsing Probleme:**
- **Fragile Syntax**: Whitespace-sensitive, error-prone parsing
- **No Validation**: Invalid IPs, VLAN IDs, Interface names werden nicht caught
- **Error Handling**: Was passiert bei Syntax-Fehlern? Silent failure? Exception?
- **Debugging**: "Zeile 247 von 2000 ist falsch" - good luck finding the issue
- **IDE Support**: Keine Syntax-Highlighting, Auto-Completion, Error-Checking

## Lösung 1: Memento Pattern - Production-Safe State Management

### Memento Pattern verstehen: Warum so designt?

**Das Ziel**: Atomare Configuration-Changes mit garantiertem Rollback

**Die Rollen**:
- **Memento**: Unveränderlicher Snapshot eines Device-Zustands
- **Originator**: Das NetworkDevice das Snapshots erstellt und restauriert
- **Caretaker**: Der ConfigurationManager der Snapshots verwaltet

**Warum diese Trennung?**
- **Encapsulation**: Nur das Device weiß wie es seinen State serialisiert
- **Immutability**: Snapshots können nicht versehentlich geändert werden
- **Access Control**: Nur das ursprüngliche Device kann von seinem Snapshot restaurieren

### Memento Implementation - Production-Ready
```java
// Immutable Memento für Network Device State
public class NetworkDeviceMemento {
    
    private final String deviceId;
    private final LocalDateTime timestamp;
    private final String changeDescription;
    private final Map<String, Object> configurationSnapshot;
    private final String configurationHash;
    private final DeviceStatus deviceStatus;
    private final String createdBy;
    
    // Package-private Constructor - nur NetworkDevice kann Mementos erstellen
    NetworkDeviceMemento(String deviceId, 
                        Map<String, Object> configuration,
                        DeviceStatus status,
                        String changeDescription,
                        String userId) {
        
        this.deviceId = deviceId;
        this.timestamp = LocalDateTime.now();
        this.changeDescription = changeDescription;
        this.deviceStatus = status;
        this.createdBy = userId;
        
        // Deep Copy für Immutability
        this.configurationSnapshot = deepCopyConfiguration(configuration);
        
        // Hash für Integrity-Check
        this.configurationHash = calculateConfigHash(configurationSnapshot);
        
        log.debug("📸 Created memento for device {} at {}", deviceId, timestamp);
    }
    
    // Public Read-Only Access für Caretaker
    public String getDeviceId() { return deviceId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getConfigurationHash() { return configurationHash; }
    public String getChangeDescription() { return changeDescription; }
    public DeviceStatus getDeviceStatus() { return deviceStatus; }
    public String getCreatedBy() { return createdBy; }
    
    public int getConfigurationSize() {
        return configurationSnapshot.size();
    }
    
    // Package-private Access - nur für NetworkDevice
    Map<String, Object> getConfigurationSnapshot() {
        // Defensive copy - Memento bleibt immutable
        return deepCopyConfiguration(configurationSnapshot);
    }
    
    // Integrity Validation
    public boolean validateIntegrity() {
        String currentHash = calculateConfigHash(configurationSnapshot);
        boolean valid = configurationHash.equals(currentHash);
        
        if (!valid) {
            log.error("🚨 Memento integrity violation for device {}! Expected: {}, Actual: {}", 
                     deviceId, configurationHash, currentHash);
        }
        
        return valid;
    }
    
    private String calculateConfigHash(Map<String, Object> config) {
        // Stable hashing - unabhängig von Map-Iteration-Order
        TreeMap<String, Object> sortedConfig = new TreeMap<>(config);
        String configString = sortedConfig.toString();
        return DigestUtils.sha256Hex(configString);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> deepCopyConfiguration(Map<String, Object> original) {
        try {
            // Serialization-based deep copy
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(original);
            oos.close();
            
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Map<String, Object>) ois.readObject();
            
        } catch (Exception e) {
            log.error("Deep copy failed, falling back to shallow copy", e);
            return new HashMap<>(original);
        }
    }
    
    @Override
    public String toString() {
        return String.format("NetworkDeviceMemento{device=%s, timestamp=%s, hash=%s, createdBy=%s}",
                           deviceId, timestamp, configurationHash.substring(0, 8) + "...", createdBy);
    }
}

// Warum ist diese Implementation robust?
// 1. **Immutability**: Deep Copy verhindert versehentliche Mutations
// 2. **Integrity**: Hash-basierte Validation gegen Corruption
// 3. **Access Control**: Package-private Zugriff auf sensible Daten
// 4. **Audit Trail**: Timestamp, User, Description für Compliance
// 5. **Validation**: Built-in Integrity-Checks

### Originator: Network Device
```java
@Entity
public class NetworkDevice {
    
    private String deviceId;
    private String hostname;
    private DeviceType deviceType;
    
    // Current Configuration State
    private Map<String, Object> currentConfiguration = new HashMap<>();
    
    // Create Memento für aktuellen State
    public NetworkDeviceMemento createMemento() {
        log.debug("Creating memento for device {}", deviceId);
        return new NetworkDeviceMemento(deviceId, currentConfiguration);
    }
    
    // Restore State von Memento
    public void restoreFromMemento(NetworkDeviceMemento memento) {
        if (!deviceId.equals(memento.getDeviceId())) {
            throw new IllegalArgumentException(
                "Memento belongs to different device: " + memento.getDeviceId()
            );
        }
        
        log.info("Restoring device {} to state from {}", 
                deviceId, memento.getTimestamp());
        
        Map<String, Object> restoredConfig = memento.getConfigurationSnapshot();
        
        // Atomic State Restoration
        this.currentConfiguration.clear();
        this.currentConfiguration.putAll(restoredConfig);
        
        // Apply configuration to physical device
        applyConfigurationToDevice(restoredConfig);
        
        log.info("Device {} successfully restored to previous state", deviceId);
    }
    
    // Configuration Methods
    public void updateRoutingTable(List<Route> routes) {
        currentConfiguration.put("routing_table", new ArrayList<>(routes));
    }
    
    public void updateVlanConfig(List<VlanConfiguration> vlans) {
        currentConfiguration.put("vlan_config", new ArrayList<>(vlans));
    }
    
    private void applyConfigurationToDevice(Map<String, Object> config) {
        // Implementation für physische Device-Konfiguration
        // z.B. SNMP, SSH, REST API calls
    }
}
```

### Caretaker: Configuration Manager
```java
@Service
public class NetworkConfigurationManager {
    
    private final Map<String, Stack<NetworkDeviceMemento>> deviceMementos = new ConcurrentHashMap<>();
    private final ConfigurationRepository configRepository;
    private final int maxMementoHistory = 10;
    
    public void saveConfiguration(NetworkDevice device, String changeDescription) {
        NetworkDeviceMemento memento = device.createMemento();
        
        // Store in memory für quick access
        Stack<NetworkDeviceMemento> deviceHistory = deviceMementos
            .computeIfAbsent(device.getDeviceId(), k -> new Stack<>());
        
        deviceHistory.push(memento);
        
        // Limit history size
        if (deviceHistory.size() > maxMementoHistory) {
            deviceHistory.remove(0); // Remove oldest
        }
        
        // Persist to database für long-term storage
        ConfigurationHistoryEntry entry = ConfigurationHistoryEntry.builder()
            .deviceId(device.getDeviceId())
            .timestamp(memento.getTimestamp())
            .configurationHash(memento.getConfigurationHash())
            .configurationData(serializeMemento(memento))
            .changeDescription(changeDescription)
            .build();
        
        configRepository.save(entry);
        
        log.info("Configuration saved for device {} with description: {}", 
                device.getDeviceId(), changeDescription);
    }
    
    public boolean rollbackConfiguration(String deviceId) {
        Stack<NetworkDeviceMemento> deviceHistory = deviceMementos.get(deviceId);
        
        if (deviceHistory == null || deviceHistory.isEmpty()) {
            log.warn("No configuration history found for device {}", deviceId);
            return false;
        }
        
        try {
            NetworkDeviceMemento previousState = deviceHistory.pop();
            NetworkDevice device = deviceService.getDevice(deviceId);
            
            device.restoreFromMemento(previousState);
            
            log.info("Successfully rolled back configuration for device {}", deviceId);
            return true;
            
        } catch (Exception e) {
            log.error("Rollback failed for device {}", deviceId, e);
            return false;
        }
    }
    
    public List<ConfigurationHistoryEntry> getConfigurationHistory(String deviceId) {
        return configRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }
}
```

### Multi-Device Orchestration - Production-Safe Rollback

**Das Use-Case**: Koordinierte Configuration-Changes über mehrere Devices

**Challenge**: Wenn Device #7 von 20 fehlschlägt, müssen alle 19 anderen zurückgerollt werden

```java
@Service
public class NetworkChangeOrchestrator {
    
    private final NetworkConfigurationManager configManager;
    private final NetworkValidationService validationService;
    private final AlertingService alertingService;
    
    public ChangeResult applyMultiDeviceConfiguration(MultiDeviceConfigurationChange change) {
        
        log.info("🎤 Starting multi-device change: {} affecting {} devices",
                change.getDescription(), change.getAffectedDeviceIds().size());
        
        List<String> affectedDevices = change.getAffectedDeviceIds();
        Map<String, NetworkDeviceMemento> preChangeSnapshots = new ConcurrentHashMap<>();
        
        try {
            // PHASE 1: Snapshot Creation (Parallel for Performance)
            log.info("📸 Phase 1: Creating pre-change snapshots for {} devices", affectedDevices.size());
            
            List<CompletableFuture<Void>> snapshotFutures = affectedDevices.stream()
                .map(deviceId -> CompletableFuture.runAsync(() -> {
                    try {
                        NetworkDevice device = deviceService.getDevice(deviceId);
                        NetworkDeviceMemento snapshot = device.createMemento(
                            "Pre-change snapshot: " + change.getDescription(),
                            change.getInitiatedBy()
                        );
                        
                        preChangeSnapshots.put(deviceId, snapshot);
                        
                        // Persist for long-term recovery
                        configManager.saveConfiguration(device, snapshot);
                        
                        log.debug("✓ Snapshot created for device {}", deviceId);
                        
                    } catch (Exception e) {
                        log.error("❌ Snapshot creation failed for device {}", deviceId, e);
                        throw new SnapshotCreationException(deviceId, e);
                    }
                }, snapshotExecutor))
                .collect(Collectors.toList());
            
            // Wait for all snapshots to complete
            CompletableFuture.allOf(snapshotFutures.toArray(new CompletableFuture[0]))
                           .get(5, TimeUnit.MINUTES); // Timeout for safety
            
            log.info("✅ All pre-change snapshots created successfully");
            
            // PHASE 2: Apply Configuration Changes (Sequential for Safety)
            log.info("🔧 Phase 2: Applying configuration changes sequentially");
            
            for (DeviceConfigurationChange deviceChange : change.getDeviceChanges()) {
                applyDeviceConfigurationSafely(deviceChange);
                
                // Quick validation after each device
                validateDeviceState(deviceChange.getDeviceId());
                
                log.debug("✓ Applied configuration to device {}", deviceChange.getDeviceId());
            }
            
            // PHASE 3: Network-wide Validation
            log.info("✓ Phase 3: Validating network-wide consistency");
            
            NetworkValidationResult validation = validationService.validateNetworkState(
                affectedDevices, change.getValidationCriteria()
            );
            
            if (!validation.isValid()) {
                throw new ConfigurationValidationException(
                    "Network validation failed: " + validation.getErrorSummary(),
                    validation.getDetailedErrors()
                );
            }
            
            // PHASE 4: Success - Create post-change snapshots
            log.info("🎉 Phase 4: Creating post-change snapshots for audit trail");
            createPostChangeSnapshots(affectedDevices, change);
            
            return ChangeResult.success(
                change.getDescription(), 
                affectedDevices.size(),
                validation.getPerformanceMetrics()
            );
            
        } catch (Exception e) {
            log.error("🚨 Multi-device configuration FAILED, initiating emergency rollback", e);
            
            // EMERGENCY ROLLBACK: All devices to pre-change state
            EmergencyRollbackResult rollbackResult = performEmergencyRollback(preChangeSnapshots, change);
            
            return ChangeResult.failure(
                change.getDescription(), 
                e.getMessage(),
                rollbackResult
            );
        }
    }
    
    private EmergencyRollbackResult performEmergencyRollback(
            Map<String, NetworkDeviceMemento> preChangeSnapshots, 
            MultiDeviceConfigurationChange change) {
        
        log.warn("🚑 EMERGENCY ROLLBACK: Restoring {} devices to pre-change state", 
                preChangeSnapshots.size());
        
        List<String> successfulRollbacks = new ArrayList<>();
        List<String> failedRollbacks = new ArrayList<>();
        
        // Parallel rollback for speed (it's an emergency!)
        List<CompletableFuture<String>> rollbackFutures = preChangeSnapshots.entrySet().stream()
            .map(entry -> CompletableFuture.supplyAsync(() -> {
                String deviceId = entry.getKey();
                NetworkDeviceMemento memento = entry.getValue();
                
                try {
                    NetworkDevice device = deviceService.getDevice(deviceId);
                    device.restoreFromMemento(memento);
                    
                    log.info("✅ Emergency rollback successful for device {}", deviceId);
                    return deviceId;
                    
                } catch (Exception rollbackException) {
                    log.error("🚨 CRITICAL: Emergency rollback FAILED for device {}", 
                             deviceId, rollbackException);
                    
                    // Immediate alert to operations team
                    alertingService.sendCriticalAlert(
                        AlertLevel.CRITICAL,
                        String.format("Emergency rollback failed: %s", deviceId),
                        Map.of(
                            "device_id", deviceId,
                            "original_change", change.getDescription(),
                            "rollback_error", rollbackException.getMessage(),
                            "memento_timestamp", memento.getTimestamp().toString()
                        ),
                        rollbackException
                    );
                    
                    throw new RollbackFailedException(deviceId, rollbackException);
                }
            }, rollbackExecutor))
            .collect(Collectors.toList());
        
        // Collect rollback results
        for (CompletableFuture<String> future : rollbackFutures) {
            try {
                String deviceId = future.get(2, TimeUnit.MINUTES);
                successfulRollbacks.add(deviceId);
            } catch (Exception e) {
                // Device ID extraction from exception
                if (e.getCause() instanceof RollbackFailedException) {
                    failedRollbacks.add(((RollbackFailedException) e.getCause()).getDeviceId());
                }
            }
        }
        
        EmergencyRollbackResult result = EmergencyRollbackResult.builder()
            .totalDevices(preChangeSnapshots.size())
            .successfulRollbacks(successfulRollbacks)
            .failedRollbacks(failedRollbacks)
            .rollbackTimestamp(Instant.now())
            .build();
        
        if (!failedRollbacks.isEmpty()) {
            // DEFCON 1: Manual intervention required
            alertingService.sendCriticalAlert(
                AlertLevel.CRITICAL,
                String.format("MANUAL INTERVENTION REQUIRED: %d devices failed emergency rollback", 
                             failedRollbacks.size()),
                Map.of(
                    "failed_devices", failedRollbacks,
                    "successful_rollbacks", successfulRollbacks,
                    "change_description", change.getDescription()
                )
            );
        }
        
        return result;
    }
}

// Warum ist diese Orchestration so robust?
// 1. **Parallel Snapshots**: Schnelle Snapshot-Erstellung
// 2. **Sequential Application**: Sichere Change-Anwendung
// 3. **Network Validation**: Holistische Consistency-Checks
// 4. **Emergency Rollback**: Parallel Rollback bei Failures
// 5. **Comprehensive Alerting**: Operations-Team wird sofort informiert

## Lösung 2: Interpreter Pattern für Configuration DSL

### Grammar Definition für Network Configuration DSL
```java
// Abstract Syntax Tree für Network Configuration Commands
public abstract class ConfigurationExpression {
    public abstract void interpret(NetworkConfigurationContext context);
    public abstract void validate(ValidationContext validationContext);
}

// Terminal Expression für Route Configuration
public class RouteExpression extends ConfigurationExpression {
    
    private final String destinationNetwork;
    private final String gatewayAddress;
    private final int metric;
    private final String interfaceName;
    
    public RouteExpression(String destination, String gateway, int metric, String iface) {
        this.destinationNetwork = destination;
        this.gatewayAddress = gateway;
        this.metric = metric;
        this.interfaceName = iface;
    }
    
    @Override
    public void interpret(NetworkConfigurationContext context) {
        Route route = Route.builder()
            .destination(destinationNetwork)
            .gateway(gatewayAddress)
            .metric(metric)
            .interfaceName(interfaceName)
            .build();
        
        context.addRoute(route);
        
        log.debug("Added route: {} via {} (metric: {})", 
                 destinationNetwork, gatewayAddress, metric);
    }
    
    @Override
    public void validate(ValidationContext validationContext) {
        // IP Address Validation
        if (!NetworkUtils.isValidNetworkAddress(destinationNetwork)) {
            validationContext.addError("Invalid destination network: " + destinationNetwork);
        }
        
        if (!NetworkUtils.isValidIpAddress(gatewayAddress)) {
            validationContext.addError("Invalid gateway address: " + gatewayAddress);
        }
        
        // Metric Validation
        if (metric < 1 || metric > 255) {
            validationContext.addError("Invalid metric value: " + metric);
        }
        
        // Interface Validation
        if (!validationContext.interfaceExists(interfaceName)) {
            validationContext.addWarning("Interface might not exist: " + interfaceName);
        }
    }
}
```

### Complex Expressions für VLAN Configuration
```java
// Non-Terminal Expression für VLAN Block
public class VlanBlockExpression extends ConfigurationExpression {
    
    private final int vlanId;
    private final String vlanName;
    private final List<ConfigurationExpression> vlanCommands;
    
    public VlanBlockExpression(int vlanId, String name, List<ConfigurationExpression> commands) {
        this.vlanId = vlanId;
        this.vlanName = name;
        this.vlanCommands = new ArrayList<>(commands);
    }
    
    @Override
    public void interpret(NetworkConfigurationContext context) {
        // Create VLAN Context
        VlanConfigurationContext vlanContext = context.createVlanContext(vlanId, vlanName);
        
        log.debug("Interpreting VLAN {} configuration", vlanId);
        
        // Process all commands in VLAN context
        for (ConfigurationExpression command : vlanCommands) {
            try {
                command.interpret(vlanContext);
            } catch (Exception e) {
                log.error("Failed to interpret command in VLAN {}: {}", 
                         vlanId, command.getClass().getSimpleName(), e);
                throw new ConfigurationInterpretationException(
                    "VLAN " + vlanId + " configuration failed", e
                );
            }
        }
        
        // Finalize VLAN configuration
        context.addVlanConfiguration(vlanContext.buildVlanConfiguration());
    }
    
    @Override
    public void validate(ValidationContext validationContext) {
        // VLAN ID Validation
        if (vlanId < 1 || vlanId > 4094) {
            validationContext.addError("Invalid VLAN ID: " + vlanId);
        }
        
        // Validate all sub-commands
        for (ConfigurationExpression command : vlanCommands) {
            command.validate(validationContext);
        }
    }
}
```

### DSL Parser Implementation
```java
@Component
public class NetworkConfigurationParser {
    
    private final Map<String, ExpressionFactory> expressionFactories = new HashMap<>();
    
    @PostConstruct
    public void initializeFactories() {
        expressionFactories.put("route", new RouteExpressionFactory());
        expressionFactories.put("vlan", new VlanExpressionFactory());
        expressionFactories.put("interface", new InterfaceExpressionFactory());
        expressionFactories.put("acl", new AclExpressionFactory());
        expressionFactories.put("qos", new QosExpressionFactory());
    }
    
    public List<ConfigurationExpression> parseConfiguration(String configurationText) {
        List<ConfigurationExpression> expressions = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(configurationText)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                ConfigurationExpression expression = parseLine(line, scanner);
                if (expression != null) {
                    expressions.add(expression);
                }
            }
        } catch (Exception e) {
            throw new ConfigurationParseException("Failed to parse configuration", e);
        }
        
        return expressions;
    }
    
    private ConfigurationExpression parseLine(String line, Scanner scanner) {
        String[] tokens = line.split("\\s+");
        String command = tokens[0].toLowerCase();
        
        ExpressionFactory factory = expressionFactories.get(command);
        if (factory == null) {
            throw new ConfigurationParseException("Unknown command: " + command);
        }
        
        return factory.createExpression(tokens, scanner);
    }
}
```

### Expression Factories
```java
public interface ExpressionFactory {
    ConfigurationExpression createExpression(String[] tokens, Scanner scanner);
}

public class RouteExpressionFactory implements ExpressionFactory {
    
    @Override
    public ConfigurationExpression createExpression(String[] tokens, Scanner scanner) {
        // route 192.168.1.0/24 via 10.0.0.1 metric 100 dev eth0
        if (tokens.length < 4) {
            throw new ConfigurationParseException("Invalid route command: " + String.join(" ", tokens));
        }
        
        String destination = tokens[1];
        String gateway = null;
        int metric = 1;
        String interfaceName = null;
        
        // Parse optional parameters
        for (int i = 2; i < tokens.length; i++) {
            switch (tokens[i].toLowerCase()) {
                case "via":
                    if (i + 1 < tokens.length) {
                        gateway = tokens[++i];
                    }
                    break;
                case "metric":
                    if (i + 1 < tokens.length) {
                        metric = Integer.parseInt(tokens[++i]);
                    }
                    break;
                case "dev":
                    if (i + 1 < tokens.length) {
                        interfaceName = tokens[++i];
                    }
                    break;
            }
        }
        
        return new RouteExpression(destination, gateway, metric, interfaceName);
    }
}

public class VlanExpressionFactory implements ExpressionFactory {
    
    @Override
    public ConfigurationExpression createExpression(String[] tokens, Scanner scanner) {
        // vlan 100 name "Production Network"
        if (tokens.length < 2) {
            throw new ConfigurationParseException("Invalid VLAN command");
        }
        
        int vlanId = Integer.parseInt(tokens[1]);
        String vlanName = extractVlanName(tokens);
        
        // Parse VLAN block commands
        List<ConfigurationExpression> vlanCommands = parseVlanBlock(scanner);
        
        return new VlanBlockExpression(vlanId, vlanName, vlanCommands);
    }
    
    private List<ConfigurationExpression> parseVlanBlock(Scanner scanner) {
        List<ConfigurationExpression> commands = new ArrayList<>();
        NetworkConfigurationParser parser = new NetworkConfigurationParser();
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            
            if (line.equals("end") || line.equals("}")) {
                break; // End of VLAN block
            }
            
            if (!line.isEmpty() && !line.startsWith("#")) {
                ConfigurationExpression command = parser.parseLine(line, scanner);
                if (command != null) {
                    commands.add(command);
                }
            }
        }
        
        return commands;
    }
}
```

## Integration: Memento + Interpreter

### Integration: Memento + Interpreter für Safe DSL Processing

**The Power Combination**: 
- **Interpreter**: Parsedt und executes Configuration-DSL
- **Memento**: Garantiert Rollback bei Fehlern

```java
@Service
public class DSLConfigurationService {
    
    private final NetworkConfigurationParser dslParser;
    private final NetworkConfigurationManager configManager;
    private final ValidationService validationService;
    private final MetricsCollector metricsCollector;
    
    public ConfigurationResult applyDSLConfiguration(String deviceId, 
                                                    String dslConfiguration,
                                                    String changeDescription,
                                                    String userId) {
        
        log.info("🎤 Applying DSL configuration to device {}: {} lines", 
                deviceId, dslConfiguration.split("\n").length);
        
        Timer.Sample configTimer = Timer.start(metricsCollector.getMeterRegistry());
        NetworkDevice device = deviceService.getDevice(deviceId);
        
        try {
            // STEP 1: Pre-change Snapshot (MEMENTO PATTERN)
            log.debug("📸 Creating pre-change snapshot");
            NetworkDeviceMemento preChangeSnapshot = device.createMemento(
                "Pre-DSL: " + changeDescription, userId
            );
            
            // Persist snapshot for audit trail
            configManager.saveConfiguration(device, preChangeSnapshot);
            
            // STEP 2: DSL Parsing (INTERPRETER PATTERN)
            log.debug("📝 Parsing DSL configuration");
            List<ConfigurationExpression> expressions;
            try {
                expressions = dslParser.parseConfiguration(dslConfiguration);
                log.debug("✅ Parsed {} configuration expressions", expressions.size());
            } catch (ConfigurationParseException e) {
                log.error("❌ DSL parsing failed", e);
                return ConfigurationResult.parseError(
                    "DSL syntax error: " + e.getMessage(),
                    e.getLineNumber(),
                    e.getErrorContext()
                );
            }
            
            // STEP 3: Pre-execution Validation
            log.debug("✓ Validating configuration expressions");
            ValidationContext validationContext = new ValidationContext(device);
            
            for (int i = 0; i < expressions.size(); i++) {
                ConfigurationExpression expr = expressions.get(i);
                try {
                    expr.validate(validationContext);
                } catch (ValidationException e) {
                    log.error("❌ Validation failed for expression {}: {}", i+1, e.getMessage());
                    return ConfigurationResult.validationFailed(
                        String.format("Expression %d validation failed: %s", i+1, e.getMessage()),
                        validationContext.getAllErrors()
                    );
                }
            }
            
            if (validationContext.hasErrors()) {
                return ConfigurationResult.validationFailed(
                    "Pre-execution validation failed",
                    validationContext.getAllErrors()
                );
            }
            
            // STEP 4: Configuration Application (INTERPRETER EXECUTION)
            log.debug("🔧 Applying configuration to device");
            NetworkConfigurationContext configContext = new NetworkConfigurationContext(device);
            
            for (int i = 0; i < expressions.size(); i++) {
                ConfigurationExpression expr = expressions.get(i);
                try {
                    log.trace("  Executing expression {}: {}", i+1, expr.getDescription());
                    expr.interpret(configContext);
                    
                } catch (Exception e) {
                    log.error("❌ Expression {} execution failed", i+1, e);
                    
                    // IMMEDIATE ROLLBACK on execution failure
                    device.restoreFromMemento(preChangeSnapshot);
                    
                    return ConfigurationResult.executionFailed(
                        String.format("Expression %d execution failed: %s", i+1, e.getMessage()),
                        i+1,
                        e
                    );
                }
            }
            
            // STEP 5: Post-execution Network Validation
            log.debug("✓ Validating final device state");
            ValidationResult finalValidation = validationService.validateDevice(device);
            
            if (!finalValidation.isValid()) {
                log.warn("⚠️ Final validation failed, rolling back");
                
                // ROLLBACK on final validation failure
                device.restoreFromMemento(preChangeSnapshot);
                
                return ConfigurationResult.validationFailed(
                    "Post-execution validation failed",
                    finalValidation.getDetailedErrors()
                );
            }
            
            // STEP 6: Success - Create post-change snapshot
            log.info("✅ DSL configuration applied successfully");
            NetworkDeviceMemento postChangeSnapshot = device.createMemento(
                "Post-DSL: " + changeDescription + " [SUCCESS]", userId
            );
            configManager.saveConfiguration(device, postChangeSnapshot);
            
            // Record success metrics
            metricsCollector.recordDslConfigurationSuccess(
                deviceId, expressions.size(), configTimer.stop()
            );
            
            return ConfigurationResult.success(
                String.format("%d DSL commands applied successfully", expressions.size()),
                expressions.stream().map(ConfigurationExpression::getDescription).collect(Collectors.toList()),
                finalValidation.getPerformanceMetrics()
            );
            
        } catch (Exception e) {
            log.error("🚨 CRITICAL: DSL configuration failed with unexpected error", e);
            
            try {
                // Emergency rollback attempt
                NetworkDeviceMemento emergencySnapshot = device.createMemento(
                    "Emergency snapshot before rollback attempt", "system"
                );
                
                // Try to restore to last known good state
                NetworkDeviceMemento lastGoodState = configManager.getLastSuccessfulConfiguration(deviceId);
                if (lastGoodState != null) {
                    device.restoreFromMemento(lastGoodState);
                    log.info("✅ Emergency rollback successful to last known good state");
                } else {
                    log.error("🚨 No last known good state found - MANUAL INTERVENTION REQUIRED");
                    alertingService.sendCriticalAlert(
                        AlertLevel.CRITICAL,
                        "Device in unknown state after DSL failure - no rollback possible",
                        Map.of("device_id", deviceId, "error", e.getMessage())
                    );
                }
                
            } catch (Exception rollbackException) {
                log.error("🚨 DEFCON 1: Emergency rollback ALSO failed for device {}", deviceId, rollbackException);
                
                // This is bad - alert everyone
                alertingService.sendCriticalAlert(
                    AlertLevel.CRITICAL,
                    "MANUAL INTERVENTION URGENTLY REQUIRED",
                    Map.of(
                        "device_id", deviceId,
                        "original_error", e.getMessage(),
                        "rollback_error", rollbackException.getMessage(),
                        "risk_level", "EXTREME - Device may be unrecoverable"
                    )
                );
            }
            
            return ConfigurationResult.criticalFailure(
                "Critical DSL configuration failure: " + e.getMessage(),
                e
            );
            
        } finally {
            configTimer.stop(Timer.builder("dsl.configuration.time")
                .tag("device_id", deviceId)
                .register(metricsCollector.getMeterRegistry()));
        }
    }
}

// Warum ist diese Integration so sicher?
// 1. **Pre-change Snapshots**: Jeder Change beginnt mit einem Snapshot
// 2. **Multi-layer Validation**: Pre-execution UND post-execution validation
// 3. **Immediate Rollback**: Bei jedem Fehler sofortiger Rollback
// 4. **Emergency Recovery**: Rollback zu last-known-good state
// 5. **Comprehensive Alerting**: Operations wird bei kritischen Fehlern alarmiert

### Example DSL Configuration
```java
// Telekom-Style Network Configuration DSL
String networkConfig = """
# Core Router Configuration
route 0.0.0.0/0 via 10.0.0.1 metric 1 dev eth0
route 192.168.0.0/16 via 10.0.1.1 metric 10 dev eth1

# VLAN Configuration
vlan 100 name "Production Network"
  ip address 192.168.100.1/24
  dhcp pool 192.168.100.10-192.168.100.200
end

vlan 200 name "Management Network" 
  ip address 192.168.200.1/24
  access-list management-acl
end

# Access Control Lists
acl management-acl
  permit tcp any any port 22
  permit tcp any any port 443
  deny ip any any
end

# QoS Configuration
qos policy voice-priority
  class voice
    priority percent 30
  class data
    bandwidth percent 70
end
""";

// Apply with automatic rollback on failure
ConfigurationResult result = dslConfigService.applyDSLConfiguration("router-001", networkConfig);
```

## Advanced Patterns: Command + Memento + Interpreter

### Configuration Command mit Memento
```java
public class DSLConfigurationCommand implements Command {
    
    private final String deviceId;
    private final String dslConfiguration;
    private final DSLConfigurationService configService;
    private NetworkDeviceMemento backupMemento;
    
    @Override
    public void execute() {
        NetworkDevice device = deviceService.getDevice(deviceId);
        backupMemento = device.createMemento(); // Save state for undo
        
        ConfigurationResult result = configService.applyDSLConfiguration(deviceId, dslConfiguration);
        if (!result.isSuccess()) {
            throw new CommandExecutionException("DSL configuration failed: " + result.getErrorMessage());
        }
    }
    
    @Override
    public void undo() {
        if (backupMemento != null) {
            NetworkDevice device = deviceService.getDevice(deviceId);
            device.restoreFromMemento(backupMemento);
            log.info("Undid DSL configuration for device {}", deviceId);
        }
    }
}
```

## Performance und Skalierung

### Lazy Loading für Memento History
```java
@Entity
public class ConfigurationHistoryEntry {
    
    @Id
    private String id;
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String configurationData; // Large configuration stored lazily
    
    // Lazy loading für configuration data
    public NetworkDeviceMemento getMemento() {
        if (configurationData != null) {
            return deserializeMemento(configurationData);
        }
        return null;
    }
}
```

### Parser Performance Optimization
```java
@Component
public class CachedNetworkConfigurationParser extends NetworkConfigurationParser {
    
    private final Cache<String, List<ConfigurationExpression>> parseCache;
    
    public CachedNetworkConfigurationParser() {
        this.parseCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build();
    }
    
    @Override
    public List<ConfigurationExpression> parseConfiguration(String configurationText) {
        String cacheKey = DigestUtils.sha256Hex(configurationText);
        
        return parseCache.get(cacheKey, key -> {
            return super.parseConfiguration(configurationText);
        });
    }
}
```

## Hands-On: Telekom Configuration DSL

### Aufgabe 1: Memento Rollback System
Implementiert ein Rollback-System für VLAN-Konfigurationen mit 24h History.

### Aufgabe 2: DSL Extension
Erweitert die DSL um QoS-Configuration mit Bandwidth-Limits und Traffic-Shaping.

### Aufgabe 3: Validation Framework
Schreibt ein Validation-Framework das Business Rules für Telekom-Konfigurationen prüft.

## Production Considerations

### Error Handling und Recovery
```java
@Component
public class RobustDSLProcessor {
    
    @Retryable(value = ConfigurationException.class, maxAttempts = 3)
    @CircuitBreaker(name = "dsl-processing", fallbackMethod = "fallbackProcessing")
    public ConfigurationResult processConfiguration(String deviceId, String dslConfig) {
        return dslConfigService.applyDSLConfiguration(deviceId, dslConfig);
    }
    
    public ConfigurationResult fallbackProcessing(String deviceId, String dslConfig, Exception ex) {
        log.warn("DSL processing failed, using manual configuration mode", ex);
        return manualConfigurationService.requestManualConfiguration(deviceId, dslConfig);
    }
}
```

### Monitoring und Auditing
```java
@EventListener
public class ConfigurationAuditListener {
    
    @Async
    public void onConfigurationChange(ConfigurationChangeEvent event) {
        AuditEntry entry = AuditEntry.builder()
            .deviceId(event.getDeviceId())
            .timestamp(event.getTimestamp())
            .changeType(event.getChangeType())
            .configurationHash(event.getConfigurationHash())
            .userId(event.getUserId())
            .dslConfiguration(event.getDslConfiguration())
            .build();
        
        auditRepository.save(entry);
        
        // Send to compliance system
        complianceService.reportConfigurationChange(entry);
    }
}
```

## Anti-Patterns und Fallen

### ⚠️ Memento Memory Leak
```java
// ANTI-PATTERN: Unbegrenzte Memento History
class BadConfigurationManager {
    private final List<NetworkDeviceMemento> allMementos = new ArrayList<>();
    
    public void saveConfiguration(NetworkDevice device) {
        allMementos.add(device.createMemento()); // Memory leak!
    }
}
```

### ⚠️ Overly Complex DSL
```java
// ANTI-PATTERN: DSL für alles
String overComplexDSL = """
calculate-optimal-routing-with-ai using machine-learning algorithm genetic-optimization
  consider latency, bandwidth, packet-loss, jitter, cost
  optimize for user-experience, network-efficiency, cost-effectiveness
  with constraints regulatory-compliance, sla-requirements
  fallback traditional-routing if confidence < 0.8
end
""";
// DSL sollte declarative sein, nicht procedural!
```

## Team-Adoption Strategien

### 1. Graduelle DSL Einführung
- **Phase 1**: Basis Commands (route, vlan, interface)
- **Phase 2**: Complex Blocks (acl, qos)  
- **Phase 3**: Advanced Features (templates, variables)

### 2. Training & Documentation
- **DSL Reference**: Vollständige Command-Referenz
- **Best Practices**: Wann DSL verwenden, wann nicht
- **Troubleshooting**: Häufige Parsing-Fehler

### 3. Tooling Support
- **Syntax Highlighting**: IDE Support für DSL
- **Validation**: Real-time Syntax Checking
- **Testing**: DSL Configuration Testing Framework

## Decision Framework: Wann Memento + Interpreter Pattern?

### ✅ VERWENDE Memento Pattern wenn:

#### 1. High-Risk State Changes
- **Production Systems**: Wo Fehler kritische Ausfälle verursachen
- **Financial Systems**: Wo State-Corruption Geld kostet
- **Infrastructure**: Network, Database, Security Configuration
- **Regulatory Requirements**: Audit-Trail für Compliance nötig

#### 2. Complex State Recovery
- **Multi-Component State**: State spanning multiple objects/systems
- **Time-Critical Recovery**: Schnelle Rollbacks bei Incidents
- **Expert-Dependent Systems**: Wo manuelle Recovery schwierig ist

### ✅ VERWENDE Interpreter Pattern wenn:

#### 1. Domain-Specific Configuration
- **Expert Languages**: Network Engineers, DBAs, Security Experts brauchen eigene Syntax
- **Frequently Changing Rules**: Business Logic ändert sich oft
- **Non-Developer Users**: Fachexperten ohne Programming-Skills

#### 2. Configuration Complexity
- **Conditional Logic**: If-then-else in Configurations
- **Variable Substitution**: Template-based Configurations  
- **Validation Requirements**: Complex Syntax-Checking nötig

### ❌ VERMEIDE diese Patterns wenn:

#### 1. Simple State Management
- **Stateless Systems**: Wo State reconstruction einfach ist
- **Low-Risk Changes**: Development/Test Environments
- **Simple CRUD**: Basic Create/Read/Update/Delete Operations

#### 2. Performance-Critical Systems
- **Hot Paths**: Wo Memento/Interpreter Overhead zu hoch ist
- **Real-Time Systems**: Deterministische Performance erforderlich
- **Memory-Constrained**: Pattern-Overhead exceeds available memory

#### 3. Team/Skill Constraints
- **Junior Teams**: Pattern-Complexity übersteigt Team-Expertise
- **Simple Configuration**: Standard property files reichen aus
- **Legacy Integration**: Bestehende Systeme können nicht geändert werden

### Success Metrics aus der Praxis

**Telekom Core Network Management**:
- **Before**: 4+ Stunden MTTR bei Configuration-Fehlern, 15% aller Changes scheiterten
- **After**: 15 Minuten MTTR mit Memento-Rollback, 3% Change-Failure-Rate  
- **ROI**: 94% weniger Downtime, 87% weniger Expert-Hours für Recovery

**Deutsche Telekom Cloud Operations**:
- **Before**: Java-Code für jede neue Firewall-Rule, 2 Tage Development-Cycle
- **After**: DSL für Security Engineers, 30 Minuten Rule-Deployment
- **ROI**: 95% schnellere Security-Changes, 60% weniger Development-Bottlenecks

### Pattern-Combination Power

**Warum Memento + Interpreter so mächtig ist**:
1. **Safe Experimentation**: DSL-Scripts können risk-free getestet werden
2. **Expert Empowerment**: Fachexperten werden von Entwicklern unabhängig
3. **Audit Compliance**: Jede DSL-Execution wird vollständig geloggt
4. **Disaster Recovery**: Schnelle Wiederherstellung nach Failed Changes
5. **Change Velocity**: Deutlich schnellere Configuration-Changes

### Integration mit anderen Patterns

#### Proven Combinations
1. **Memento + Command**: 
   - Commands mit Undo-Fähigkeit via Memento
   - Command-History mit State-Snapshots

2. **Interpreter + Strategy**:
   - Verschiedene DSL-Dialekte (Cisco vs. Juniper vs. Huawei)
   - Context-specific DSL-Execution Strategies

3. **Memento + Observer**:
   - State-Changes werden beobachtet und gesnapshot
   - Observer-Pattern für Audit-Trail Generation

### Evolution Path

1. **Event Sourcing**: Memento-Snapshots zu Event-Streams
2. **CQRS**: Command/Query Separation für DSL-Operations  
3. **Distributed State**: Memento für Multi-Service State-Management
4. **AI-Enhanced DSL**: Machine Learning für intelligent DSL-Completion

### Key Takeaways für Telekom-Architekten

1. **Risk Assessment First**: High-Risk = Memento, Complex Config = Interpreter
2. **Expert Empowerment**: DSL-Investment zahlt sich durch Expert-Autonomy aus
3. **Recovery Planning**: Memento ist billiger als Expert-Recovery-Time
4. **Gradual Introduction**: Start mit kritischen Systems, expand nach Success
5. **Performance Testing**: Pattern-Overhead messen, nicht assumptions

**Remember**: Diese Patterns schützen vor den teuersten IT-Incidents: **Catastrophic State Loss** und **Configuration Disasters**

---

**Nächstes Modul**: Pattern Integration - Alle Patterns in produktiver Enterprise-Architektur zusammenführen