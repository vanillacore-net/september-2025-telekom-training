# Tag 4.3: Memento & Interpreter Pattern - State-Management und Domain-Specific Languages

## Lernziele
- Memento Pattern für robustes State-Management
- Interpreter Pattern für Domain-Specific Languages (DSLs)
- State-Versioning und Rollback-Mechanismen
- Configuration-DSLs für Telekom-Infrastruktur

## Problem: Configuration Chaos und fehlende Rollback-Fähigkeiten

### Das Telekom Network Configuration Disaster
```java
// ANTI-PATTERN: Keine Rollback-Möglichkeiten
class NetworkDeviceConfigurator {
    
    public void applyConfiguration(NetworkDevice device, Configuration newConfig) {
        // HORROR: Direkte State-Änderungen ohne Backup
        device.setRoutingTable(newConfig.getRoutes());
        device.setVlanConfiguration(newConfig.getVlans());
        device.setSecurityPolicies(newConfig.getSecurityPolicies());
        device.setQosSettings(newConfig.getQosSettings());
        
        try {
            device.commitConfiguration();
        } catch (ConfigurationException e) {
            // PROBLEM: Wie zurück zum vorherigen Zustand?
            log.error("Configuration failed, device in unknown state!", e);
            // Device ist jetzt in inkonsistentem Zustand!
            // Keine Möglichkeit zum Rollback!
        }
    }
}
```

### Real-World Probleme
- **Production Outages**: Configuration-Fehler ohne Rollback-Möglichkeit
- **Audit-Probleme**: Keine Historie von Configuration-Änderungen
- **Complex Rollouts**: Multi-Device Changes ohne koordiniertes Rollback
- **Manual Recovery**: Stunden von manueller Rekonfiguration nach Fehlern

### DSL Configuration Horror
```java
// ANTI-PATTERN: String-based Configuration Parsing
public void parseNetworkConfig(String configText) {
    String[] lines = configText.split("\n");
    
    for (String line : lines) {
        if (line.startsWith("route")) {
            // Fragile String-Parsing
            String[] parts = line.split(" ");
            if (parts.length >= 3) {
                String destination = parts[1];
                String gateway = parts[2];
                // Was passiert bei Syntax-Fehlern?
                // Wie debugged man komplexe Konfigurationen?
                addRoute(destination, gateway);
            }
        } else if (line.startsWith("vlan")) {
            // Weitere 50 Zeilen String-Parsing Horror...
        }
        // Hunderte von Zeilen fragile Parsing Logic
    }
}
```

## Lösung 1: Memento Pattern für State Management

### Memento Implementation
```java
// Memento für Network Device State
public class NetworkDeviceMemento {
    
    private final String deviceId;
    private final LocalDateTime timestamp;
    private final Map<String, Object> configurationSnapshot;
    private final String configurationHash;
    
    // Package-private Constructor (nur von Originator aufrufbar)
    NetworkDeviceMemento(String deviceId, Map<String, Object> configuration) {
        this.deviceId = deviceId;
        this.timestamp = LocalDateTime.now();
        this.configurationSnapshot = new HashMap<>(configuration);
        this.configurationHash = calculateConfigHash(configuration);
    }
    
    // Read-Only Access für externe Validation
    public String getDeviceId() { return deviceId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getConfigurationHash() { return configurationHash; }
    
    // Nur für Originator sichtbar
    Map<String, Object> getConfigurationSnapshot() {
        return new HashMap<>(configurationSnapshot);
    }
    
    private String calculateConfigHash(Map<String, Object> config) {
        return DigestUtils.sha256Hex(config.toString());
    }
}
```

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

### Orchestrated Multi-Device Rollback
```java
@Service
public class NetworkChangeOrchestrator {
    
    private final NetworkConfigurationManager configManager;
    private final TransactionTemplate transactionTemplate;
    
    public ChangeResult applyMultiDeviceConfiguration(MultiDeviceConfigurationChange change) {
        
        List<String> affectedDevices = change.getAffectedDeviceIds();
        Map<String, NetworkDeviceMemento> preChangeStates = new HashMap<>();
        
        try {
            // 1. Create Snapshots für alle betroffenen Devices
            for (String deviceId : affectedDevices) {
                NetworkDevice device = deviceService.getDevice(deviceId);
                NetworkDeviceMemento snapshot = device.createMemento();
                preChangeStates.put(deviceId, snapshot);
                
                configManager.saveConfiguration(device, 
                    "Pre-change snapshot for: " + change.getDescription());
            }
            
            // 2. Apply Configuration Changes
            for (DeviceConfigurationChange deviceChange : change.getDeviceChanges()) {
                applyDeviceConfiguration(deviceChange);
            }
            
            // 3. Validation Phase
            ValidationResult validation = validateNetworkState(affectedDevices);
            if (!validation.isValid()) {
                throw new ConfigurationValidationException(validation.getErrors());
            }
            
            return ChangeResult.success(change.getDescription(), affectedDevices.size());
            
        } catch (Exception e) {
            log.error("Multi-device configuration failed, initiating rollback", e);
            
            // Rollback ALL devices to pre-change state
            rollbackAllDevices(preChangeStates);
            
            return ChangeResult.failure(change.getDescription(), e.getMessage());
        }
    }
    
    private void rollbackAllDevices(Map<String, NetworkDeviceMemento> preChangeStates) {
        for (Map.Entry<String, NetworkDeviceMemento> entry : preChangeStates.entrySet()) {
            try {
                String deviceId = entry.getKey();
                NetworkDeviceMemento memento = entry.getValue();
                
                NetworkDevice device = deviceService.getDevice(deviceId);
                device.restoreFromMemento(memento);
                
                log.info("Rolled back device {} to pre-change state", deviceId);
            } catch (Exception rollbackException) {
                log.error("CRITICAL: Rollback failed for device {}", 
                         entry.getKey(), rollbackException);
                // Alert operations team für manual intervention
                alertingService.sendCriticalAlert(
                    "Device rollback failed: " + entry.getKey(), 
                    rollbackException
                );
            }
        }
    }
}
```

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

### Configuration mit Rollback-Fähigkeit
```java
@Service
public class DSLConfigurationService {
    
    private final NetworkConfigurationParser parser;
    private final NetworkConfigurationManager configManager;
    private final ValidationService validationService;
    
    public ConfigurationResult applyDSLConfiguration(String deviceId, String dslConfiguration) {
        
        NetworkDevice device = deviceService.getDevice(deviceId);
        
        // 1. Create Memento BEFORE changes
        NetworkDeviceMemento preChangeSnapshot = device.createMemento();
        configManager.saveConfiguration(device, "Pre-DSL configuration snapshot");
        
        try {
            // 2. Parse DSL Configuration
            List<ConfigurationExpression> expressions = parser.parseConfiguration(dslConfiguration);
            
            // 3. Validate Configuration
            ValidationContext validationContext = new ValidationContext(device);
            for (ConfigurationExpression expr : expressions) {
                expr.validate(validationContext);
            }
            
            if (validationContext.hasErrors()) {
                return ConfigurationResult.validationFailed(validationContext.getErrors());
            }
            
            // 4. Apply Configuration
            NetworkConfigurationContext configContext = new NetworkConfigurationContext(device);
            for (ConfigurationExpression expr : expressions) {
                expr.interpret(configContext);
            }
            
            // 5. Final Validation
            ValidationResult finalValidation = validationService.validateDevice(device);
            if (!finalValidation.isValid()) {
                // Rollback on validation failure
                device.restoreFromMemento(preChangeSnapshot);
                return ConfigurationResult.validationFailed(finalValidation.getErrors());
            }
            
            // 6. Success - Save new configuration
            configManager.saveConfiguration(device, "DSL configuration applied successfully");
            
            return ConfigurationResult.success(expressions.size() + " commands applied");
            
        } catch (Exception e) {
            log.error("DSL configuration failed, rolling back", e);
            
            // Rollback to pre-change state
            try {
                device.restoreFromMemento(preChangeSnapshot);
                log.info("Successfully rolled back device {} after DSL failure", deviceId);
            } catch (Exception rollbackException) {
                log.error("CRITICAL: Rollback also failed for device {}", deviceId, rollbackException);
            }
            
            return ConfigurationResult.failure("Configuration failed: " + e.getMessage());
        }
    }
}
```

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

## Fazit: State Management für Production Systems

### Wann Memento Pattern?
- **✅ Undo/Redo** Funktionalität erforderlich
- **✅ Snapshots** für Rollback-Szenarien
- **✅ Audit Trail** für Compliance
- **✅ Complex State** der schwer zu rekonstruieren ist

### Wann Interpreter Pattern?
- **✅ Domain-Specific Language** für Experten
- **✅ Configuration** die häufig ändert
- **✅ Complex Rules** die nicht in Code gehören
- **✅ Non-Developer** User brauchen Scripting

### Wann NICHT verwenden?
- **❌ Simple State** mit wenig History-Bedarf
- **❌ Performance-Critical** Code mit vielen State-Changes
- **❌ Rare Configuration** Changes
- **❌ Small Team** ohne DSL-Expertise

**Nächstes Modul**: Pattern Integration in produktiver Architektur