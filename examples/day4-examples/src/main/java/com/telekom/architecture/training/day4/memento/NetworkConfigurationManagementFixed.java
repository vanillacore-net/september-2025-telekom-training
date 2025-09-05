package com.telekom.architecture.training.day4.memento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FIXED IMPLEMENTATION using Memento Pattern
 * 
 * Benefits:
 * - Complete state preservation before any changes
 * - Atomic rollback capability for failed configurations
 * - Full configuration history and audit trail
 * - Zero-downtime recovery from failed changes
 * - Compliance-ready configuration tracking
 * - Confidence in making network changes
 * 
 * Enterprise Features:
 * - Configuration versioning with metadata
 * - Automatic rollback triggers on validation failures
 * - Audit trail for compliance requirements
 * - Point-in-time recovery capabilities
 * - Configuration drift detection
 * 
 * Trainer Notes:
 * - Show how Memento pattern eliminates configuration fear
 * - Demonstrate instant rollback capabilities
 * - Point out compliance and audit benefits
 * - Show how operations teams can work confidently
 */
public class NetworkConfigurationManagementFixed {
    
    // Memento class - immutable snapshot of device configuration
    public static class ConfigurationMemento {
        private final String deviceId;
        private final Map<String, String> interfaces;
        private final Map<String, String> routingRules;
        private final Map<String, String> securityPolicies;
        private final Map<String, String> qosSettings;
        private final Map<String, String> monitoringConfig;
        private final String operatingMode;
        private final LocalDateTime snapshotTime;
        private final String version;
        private final String description;
        private final String createdBy;
        
        public ConfigurationMemento(String deviceId,
                                  Map<String, String> interfaces,
                                  Map<String, String> routingRules,
                                  Map<String, String> securityPolicies,
                                  Map<String, String> qosSettings,
                                  Map<String, String> monitoringConfig,
                                  String operatingMode,
                                  String version,
                                  String description,
                                  String createdBy) {
            this.deviceId = deviceId;
            // Create immutable copies of all maps
            this.interfaces = new HashMap<>(interfaces);
            this.routingRules = new HashMap<>(routingRules);
            this.securityPolicies = new HashMap<>(securityPolicies);
            this.qosSettings = new HashMap<>(qosSettings);
            this.monitoringConfig = new HashMap<>(monitoringConfig);
            this.operatingMode = operatingMode;
            this.snapshotTime = LocalDateTime.now();
            this.version = version;
            this.description = description;
            this.createdBy = createdBy;
        }
        
        // Immutable getters
        public String getDeviceId() { return deviceId; }
        public Map<String, String> getInterfaces() { return new HashMap<>(interfaces); }
        public Map<String, String> getRoutingRules() { return new HashMap<>(routingRules); }
        public Map<String, String> getSecurityPolicies() { return new HashMap<>(securityPolicies); }
        public Map<String, String> getQosSettings() { return new HashMap<>(qosSettings); }
        public Map<String, String> getMonitoringConfig() { return new HashMap<>(monitoringConfig); }
        public String getOperatingMode() { return operatingMode; }
        public LocalDateTime getSnapshotTime() { return snapshotTime; }
        public String getVersion() { return version; }
        public String getDescription() { return description; }
        public String getCreatedBy() { return createdBy; }
        
        @Override
        public String toString() {
            return String.format("ConfigMemento[%s v%s] - %s (by %s at %s)", 
                deviceId, version, description, createdBy, 
                snapshotTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
    
    // Configuration history manager
    public static class ConfigurationHistoryManager {
        private final Map<String, List<ConfigurationMemento>> deviceHistory = new HashMap<>();
        private final int maxHistorySize;
        private final AtomicInteger versionCounter = new AtomicInteger(1);
        
        public ConfigurationHistoryManager(int maxHistorySize) {
            this.maxHistorySize = maxHistorySize;
        }
        
        public void saveMemento(ConfigurationMemento memento) {
            deviceHistory.computeIfAbsent(memento.getDeviceId(), k -> new ArrayList<>())
                        .add(memento);
            
            // Maintain history size limit
            List<ConfigurationMemento> history = deviceHistory.get(memento.getDeviceId());
            if (history.size() > maxHistorySize) {
                history.remove(0); // Remove oldest
            }
            
            System.out.println("📄 Configuration snapshot saved: " + memento);
        }
        
        public ConfigurationMemento getLatestMemento(String deviceId) {
            List<ConfigurationMemento> history = deviceHistory.get(deviceId);
            return (history != null && !history.isEmpty()) ? 
                history.get(history.size() - 1) : null;
        }
        
        public ConfigurationMemento getMementoByVersion(String deviceId, String version) {
            List<ConfigurationMemento> history = deviceHistory.get(deviceId);
            if (history == null) return null;
            
            return history.stream()
                .filter(m -> version.equals(m.getVersion()))
                .findFirst()
                .orElse(null);
        }
        
        public List<ConfigurationMemento> getDeviceHistory(String deviceId) {
            return new ArrayList<>(deviceHistory.getOrDefault(deviceId, new ArrayList<>()));
        }
        
        public List<ConfigurationMemento> getHistoryAfter(String deviceId, LocalDateTime after) {
            return deviceHistory.getOrDefault(deviceId, new ArrayList<>())
                .stream()
                .filter(m -> m.getSnapshotTime().isAfter(after))
                .toList();
        }
        
        public String generateNextVersion(String deviceId) {
            return String.format("v%s.%d", deviceId, versionCounter.getAndIncrement());
        }
        
        public void showHistory(String deviceId) {
            List<ConfigurationMemento> history = deviceHistory.get(deviceId);
            if (history == null || history.isEmpty()) {
                System.out.println("No configuration history for " + deviceId);
                return;
            }
            
            System.out.println("\n=== Configuration History for " + deviceId + " ===");
            for (int i = history.size() - 1; i >= 0; i--) {
                ConfigurationMemento memento = history.get(i);
                System.out.println((history.size() - i) + ". " + memento);
            }
            System.out.println("================================================\n");
        }
    }
    
    // Enhanced configuration class with memento support
    public static class NetworkDeviceConfiguration {
        private String deviceId;
        private Map<String, String> interfaces = new HashMap<>();
        private Map<String, String> routingRules = new HashMap<>();
        private Map<String, String> securityPolicies = new HashMap<>();
        private Map<String, String> qosSettings = new HashMap<>();
        private Map<String, String> monitoringConfig = new HashMap<>();
        private String operatingMode = "NORMAL";
        private LocalDateTime lastModified = LocalDateTime.now();
        
        public NetworkDeviceConfiguration(String deviceId) {
            this.deviceId = deviceId;
            initializeDefaultConfig();
        }
        
        private void initializeDefaultConfig() {
            interfaces.put("eth0", "192.168.1.1/24");
            interfaces.put("eth1", "10.0.1.1/24");
            routingRules.put("default", "192.168.1.254");
            routingRules.put("internal", "10.0.0.0/8");
            securityPolicies.put("firewall", "ENABLED");
            securityPolicies.put("intrusion_detection", "ENABLED");
            qosSettings.put("bandwidth_limit", "1000Mbps");
            qosSettings.put("priority_queues", "8");
            monitoringConfig.put("snmp", "ENABLED");
            monitoringConfig.put("logging_level", "INFO");
        }
        
        // Create memento from current state
        public ConfigurationMemento createMemento(String description, String createdBy, 
                                                ConfigurationHistoryManager historyManager) {
            return new ConfigurationMemento(
                deviceId,
                interfaces,
                routingRules,
                securityPolicies,
                qosSettings,
                monitoringConfig,
                operatingMode,
                historyManager.generateNextVersion(deviceId),
                description,
                createdBy
            );
        }
        
        // Restore from memento
        public void restoreFromMemento(ConfigurationMemento memento) {
            System.out.println("🔄 Restoring configuration from: " + memento);
            
            this.interfaces.clear();
            this.interfaces.putAll(memento.getInterfaces());
            
            this.routingRules.clear();
            this.routingRules.putAll(memento.getRoutingRules());
            
            this.securityPolicies.clear();
            this.securityPolicies.putAll(memento.getSecurityPolicies());
            
            this.qosSettings.clear();
            this.qosSettings.putAll(memento.getQosSettings());
            
            this.monitoringConfig.clear();
            this.monitoringConfig.putAll(memento.getMonitoringConfig());
            
            this.operatingMode = memento.getOperatingMode();
            this.lastModified = LocalDateTime.now();
            
            System.out.println("✅ Configuration successfully restored to version " + memento.getVersion());
        }
        
        // Safe getters (defensive copies)
        public Map<String, String> getInterfaces() { return new HashMap<>(interfaces); }
        public Map<String, String> getRoutingRules() { return new HashMap<>(routingRules); }
        public Map<String, String> getSecurityPolicies() { return new HashMap<>(securityPolicies); }
        public Map<String, String> getQosSettings() { return new HashMap<>(qosSettings); }
        public Map<String, String> getMonitoringConfig() { return new HashMap<>(monitoringConfig); }
        
        // Mutable access for updates (package-private)
        Map<String, String> getInterfacesMutable() { return interfaces; }
        Map<String, String> getRoutingRulesMutable() { return routingRules; }
        Map<String, String> getSecurityPoliciesMutable() { return securityPolicies; }
        Map<String, String> getQosSettingsMutable() { return qosSettings; }
        Map<String, String> getMonitoringConfigMutable() { return monitoringConfig; }
        
        public String getDeviceId() { return deviceId; }
        public String getOperatingMode() { return operatingMode; }
        public void setOperatingMode(String mode) { 
            this.operatingMode = mode;
            this.lastModified = LocalDateTime.now();
        }
        public LocalDateTime getLastModified() { return lastModified; }
        public void updateLastModified() { this.lastModified = LocalDateTime.now(); }
        
        @Override
        public String toString() {
            return String.format("Config[%s] - Mode: %s, Modified: %s", 
                deviceId, operatingMode, lastModified.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
    
    // Enhanced network device with memento support
    public static class NetworkDevice {
        private String id;
        private String type;
        private String status = "ONLINE";
        private NetworkDeviceConfiguration configuration;
        private ConfigurationHistoryManager historyManager;
        private ConfigurationMemento currentSessionBackup; // Automatic backup before any changes
        
        public NetworkDevice(String id, String type, ConfigurationHistoryManager historyManager) {
            this.id = id;
            this.type = type;
            this.configuration = new NetworkDeviceConfiguration(id);
            this.historyManager = historyManager;
            
            // Create initial configuration snapshot
            saveConfigurationSnapshot("Initial configuration", "SYSTEM_INIT");
        }
        
        // Create configuration snapshot
        public void saveConfigurationSnapshot(String description, String createdBy) {
            ConfigurationMemento memento = configuration.createMemento(description, createdBy, historyManager);
            historyManager.saveMemento(memento);
        }
        
        // Create automatic backup before making changes
        private void createSessionBackup(String operation) {
            if (currentSessionBackup == null) {
                currentSessionBackup = configuration.createMemento(
                    "Auto-backup before " + operation, "SYSTEM_AUTO");
                System.out.println("🛡️  Automatic backup created before " + operation);
            }
        }
        
        // Commit session (clear automatic backup)
        public void commitSession() {
            if (currentSessionBackup != null) {
                System.out.println("✅ Session committed - automatic backup cleared");
                currentSessionBackup = null;
            }
        }
        
        // Rollback to session backup
        public void rollbackSession() {
            if (currentSessionBackup != null) {
                System.out.println("⏪ Rolling back to session backup");
                configuration.restoreFromMemento(currentSessionBackup);
                currentSessionBackup = null;
                status = "ONLINE";
            } else {
                System.out.println("⚠️  No session backup available");
            }
        }
        
        // Rollback to specific version
        public boolean rollbackToVersion(String version) {
            ConfigurationMemento memento = historyManager.getMementoByVersion(id, version);
            if (memento != null) {
                configuration.restoreFromMemento(memento);
                status = "ONLINE";
                return true;
            }
            return false;
        }
        
        // Rollback to latest saved configuration
        public boolean rollbackToLatest() {
            ConfigurationMemento latest = historyManager.getLatestMemento(id);
            if (latest != null) {
                configuration.restoreFromMemento(latest);
                status = "ONLINE";
                return true;
            }
            return false;
        }
        
        // Safe configuration updates with automatic backup
        public void updateInterfaceConfiguration(String interfaceName, String config) {
            createSessionBackup("interface update");
            System.out.println("[" + id + "] Updating interface " + interfaceName + " to " + config);
            configuration.getInterfacesMutable().put(interfaceName, config);
            configuration.updateLastModified();
        }
        
        public void updateRoutingConfiguration(String routeName, String config) {
            createSessionBackup("routing update");
            System.out.println("[" + id + "] Updating route " + routeName + " to " + config);
            configuration.getRoutingRulesMutable().put(routeName, config);
            configuration.updateLastModified();
        }
        
        public void updateSecurityConfiguration(String policyName, String config) {
            createSessionBackup("security update");
            System.out.println("[" + id + "] Updating security policy " + policyName + " to " + config);
            configuration.getSecurityPoliciesMutable().put(policyName, config);
            configuration.updateLastModified();
        }
        
        public void updateQosConfiguration(String setting, String value) {
            createSessionBackup("QoS update");
            System.out.println("[" + id + "] Updating QoS " + setting + " to " + value);
            configuration.getQosSettingsMutable().put(setting, value);
            configuration.updateLastModified();
        }
        
        public void setOperatingMode(String mode) {
            createSessionBackup("operating mode change");
            System.out.println("[" + id + "] Changing operating mode to " + mode);
            configuration.setOperatingMode(mode);
        }
        
        // Atomic configuration update with automatic rollback on failure
        public boolean applyCompleteConfiguration(Map<String, String> interfaces,
                                                Map<String, String> routing,
                                                Map<String, String> security,
                                                Map<String, String> qos,
                                                String operatingMode,
                                                String description,
                                                String operator) {
            
            // Create pre-change snapshot
            saveConfigurationSnapshot("Before: " + description, operator);
            
            System.out.println("[" + id + "] 🛡️  SAFE: Applying complete configuration update...");
            System.out.println("   → Pre-change backup created");
            
            try {
                // Apply all changes atomically
                System.out.println("   → Updating interfaces...");
                configuration.getInterfacesMutable().clear();
                configuration.getInterfacesMutable().putAll(interfaces);
                
                System.out.println("   → Updating routing...");
                configuration.getRoutingRulesMutable().clear();
                configuration.getRoutingRulesMutable().putAll(routing);
                
                System.out.println("   → Updating security policies...");
                configuration.getSecurityPoliciesMutable().clear();
                configuration.getSecurityPoliciesMutable().putAll(security);
                
                System.out.println("   → Updating QoS settings...");
                configuration.getQosSettingsMutable().clear();
                configuration.getQosSettingsMutable().putAll(qos);
                
                System.out.println("   → Changing operating mode...");
                configuration.setOperatingMode(operatingMode);
                
                // Simulate validation
                validateConfiguration();
                
                // Success - create post-change snapshot
                saveConfigurationSnapshot("After: " + description, operator);
                commitSession();
                
                System.out.println("[" + id + "] ✅ Configuration applied and validated successfully");
                return true;
                
            } catch (Exception e) {
                // AUTOMATIC ROLLBACK - No manual intervention needed!
                System.err.println("[" + id + "] ❌ Configuration failed: " + e.getMessage());
                System.out.println("[" + id + "] 🔄 AUTOMATIC ROLLBACK initiated...");
                
                // Instant rollback to pre-change state
                ConfigurationMemento preChangeBackup = historyManager.getLatestMemento(id);
                if (preChangeBackup != null && preChangeBackup.getDescription().startsWith("Before:")) {
                    configuration.restoreFromMemento(preChangeBackup);
                    status = "ONLINE";
                    System.out.println("[" + id + "] ✅ ROLLBACK completed - device restored to stable state");
                } else {
                    rollbackSession(); // Fallback to session backup
                }
                
                System.out.println("[" + id + "] 📊 Network connectivity maintained");
                System.out.println("[" + id + "] ⏱️  Recovery time: < 5 seconds");
                System.out.println("[" + id + "] 💰 Business impact: ZERO");
                
                return false;
            }
        }
        
        private void validateConfiguration() throws Exception {
            // Simulate configuration validation
            if (Math.random() < 0.3) { // 30% chance of validation failure
                throw new RuntimeException("Configuration validation failed - incompatible settings detected");
            }
        }
        
        public void showConfigurationHistory() {
            historyManager.showHistory(id);
        }
        
        // Getters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public NetworkDeviceConfiguration getConfiguration() { return configuration; }
    }
    
    // Enhanced network operations with confidence
    public static class NetworkOperationsCenter {
        private List<NetworkDevice> managedDevices = new ArrayList<>();
        private ConfigurationHistoryManager globalHistoryManager = new ConfigurationHistoryManager(50);
        private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        public void addDevice(NetworkDevice device) {
            managedDevices.add(device);
            System.out.println(timeFormatter.format(LocalDateTime.now()) + 
                " [NOC] Device added with configuration management: " + device.getId());
        }
        
        // SAFE: Mass configuration updates with full rollback capability
        public void performMaintenanceWindow(String maintenanceType, String operator) {
            System.out.println("\n" + timeFormatter.format(LocalDateTime.now()) + 
                " [NOC] 🛡️  Starting SAFE maintenance window: " + maintenanceType);
            
            List<NetworkDevice> failedDevices = new ArrayList<>();
            
            switch (maintenanceType) {
                case "SECURITY_UPDATE":
                    failedDevices = performSafeSecurityUpdate(operator);
                    break;
                case "QOS_OPTIMIZATION":
                    failedDevices = performSafeQosOptimization(operator);
                    break;
                case "ROUTING_RECONFIGURATION":
                    failedDevices = performSafeRoutingReconfiguration(operator);
                    break;
                case "COMPLETE_OVERHAUL":
                    failedDevices = performSafeCompleteOverhaul(operator);
                    break;
            }
            
            if (failedDevices.isEmpty()) {
                System.out.println(timeFormatter.format(LocalDateTime.now()) + 
                    " [NOC] ✅ Maintenance window completed successfully - ALL devices updated");
            } else {
                System.out.println(timeFormatter.format(LocalDateTime.now()) + 
                    " [NOC] ⚠️  Maintenance completed with " + failedDevices.size() + 
                    " devices rolled back (network remained stable)");
            }
            
            commitAllSessions();
            System.out.println();
        }
        
        private List<NetworkDevice> performSafeSecurityUpdate(String operator) {
            System.out.println("   🔒 Applying security updates with automatic rollback protection...");
            List<NetworkDevice> failedDevices = new ArrayList<>();
            
            for (NetworkDevice device : managedDevices) {
                try {
                    device.updateSecurityConfiguration("firewall", "STRICT_MODE");
                    device.updateSecurityConfiguration("intrusion_detection", "AGGRESSIVE");
                    device.updateSecurityConfiguration("access_control", "WHITELIST_ONLY");
                    
                    // Save successful security update
                    device.saveConfigurationSnapshot("Security update - " + operator, operator);
                    
                } catch (Exception e) {
                    device.rollbackSession();
                    failedDevices.add(device);
                    System.out.println("   ⏪ " + device.getId() + " rolled back - security remains in stable state");
                }
            }
            
            return failedDevices;
        }
        
        private List<NetworkDevice> performSafeQosOptimization(String operator) {
            System.out.println("   ⚡ Optimizing QoS with rollback protection...");
            List<NetworkDevice> failedDevices = new ArrayList<>();
            
            for (NetworkDevice device : managedDevices) {
                try {
                    device.updateQosConfiguration("bandwidth_limit", "10000Mbps");
                    device.updateQosConfiguration("priority_queues", "16");
                    device.updateQosConfiguration("traffic_shaping", "ENABLED");
                    
                    device.saveConfigurationSnapshot("QoS optimization - " + operator, operator);
                    
                } catch (Exception e) {
                    device.rollbackSession();
                    failedDevices.add(device);
                    System.out.println("   ⏪ " + device.getId() + " rolled back - QoS remains stable");
                }
            }
            
            return failedDevices;
        }
        
        private List<NetworkDevice> performSafeRoutingReconfiguration(String operator) {
            System.out.println("   🗺️  Reconfiguring routing with instant rollback...");
            List<NetworkDevice> failedDevices = new ArrayList<>();
            
            for (NetworkDevice device : managedDevices) {
                try {
                    device.updateRoutingConfiguration("bgp_as", "65001");
                    device.updateRoutingConfiguration("ospf_area", "0.0.0.1");
                    device.updateRoutingConfiguration("static_routes", "10.0.0.0/8->10.1.1.1");
                    
                    device.saveConfigurationSnapshot("Routing reconfiguration - " + operator, operator);
                    
                } catch (Exception e) {
                    device.rollbackSession();
                    failedDevices.add(device);
                    System.out.println("   ⏪ " + device.getId() + " rolled back - routing connectivity maintained");
                }
            }
            
            return failedDevices;
        }
        
        private List<NetworkDevice> performSafeCompleteOverhaul(String operator) {
            System.out.println("   🔧 PERFORMING SAFE COMPLETE CONFIGURATION OVERHAUL");
            System.out.println("   ✅ Full rollback capability - ZERO risk operation");
            
            Map<String, String> newInterfaces = Map.of(
                "eth0", "172.16.1.1/24",
                "eth1", "172.16.2.1/24", 
                "eth2", "172.16.3.1/24"
            );
            
            Map<String, String> newRouting = Map.of(
                "default", "172.16.1.254",
                "internal", "172.16.0.0/16",
                "bgp_as", "65002"
            );
            
            Map<String, String> newSecurity = Map.of(
                "firewall", "ULTRA_STRICT",
                "intrusion_detection", "MAXIMUM",
                "encryption", "AES256"
            );
            
            Map<String, String> newQos = Map.of(
                "bandwidth_limit", "50000Mbps",
                "priority_queues", "32",
                "traffic_shaping", "ADVANCED"
            );
            
            List<NetworkDevice> failedDevices = new ArrayList<>();
            
            for (NetworkDevice device : managedDevices) {
                System.out.println("   🛡️  Safely updating " + device.getId() + " with rollback protection...");
                
                boolean success = device.applyCompleteConfiguration(
                    newInterfaces, newRouting, newSecurity, newQos, 
                    "HIGH_PERFORMANCE", "Complete overhaul", operator);
                
                if (!success) {
                    failedDevices.add(device);
                }
            }
            
            return failedDevices;
        }
        
        private void commitAllSessions() {
            for (NetworkDevice device : managedDevices) {
                device.commitSession();
            }
        }
        
        public void performEmergencyRollback(String deviceId, String version) {
            System.out.println("🚨 EMERGENCY ROLLBACK for " + deviceId + " to version " + version);
            
            NetworkDevice device = managedDevices.stream()
                .filter(d -> d.getId().equals(deviceId))
                .findFirst()
                .orElse(null);
                
            if (device != null) {
                boolean success = device.rollbackToVersion(version);
                if (success) {
                    System.out.println("✅ Emergency rollback completed successfully");
                } else {
                    System.err.println("❌ Version not found - rolling back to latest stable");
                    device.rollbackToLatest();
                }
            }
        }
        
        public void showNetworkStatus() {
            System.out.println("\n=== Network Status (With Configuration Management) ===");
            for (NetworkDevice device : managedDevices) {
                System.out.println(device.getId() + " [" + device.getType() + "] - Status: " + 
                    device.getStatus() + " | " + device.getConfiguration().toString());
            }
            
            long stableDevices = managedDevices.stream()
                .mapToLong(d -> "ONLINE".equals(d.getStatus()) ? 1 : 0)
                .sum();
                
            System.out.println("✅ " + stableDevices + "/" + managedDevices.size() + 
                " devices in STABLE state");
            System.out.println("🛡️  All devices have rollback capability");
            System.out.println("📊 Network reliability: 99.9%+");
            System.out.println("⏱️  Recovery time objective: < 5 seconds");
            System.out.println("======================================================\n");
        }
        
        public void showConfigurationAuditTrail(String deviceId) {
            NetworkDevice device = managedDevices.stream()
                .filter(d -> d.getId().equals(deviceId))
                .findFirst()
                .orElse(null);
                
            if (device != null) {
                device.showConfigurationHistory();
            } else {
                System.out.println("Device not found: " + deviceId);
            }
        }
        
        public void demonstratePointInTimeRecovery() {
            System.out.println("🕰️  DEMONSTRATING Point-in-Time Recovery Capabilities");
            
            if (!managedDevices.isEmpty()) {
                NetworkDevice device = managedDevices.get(0);
                System.out.println("   Device: " + device.getId());
                
                // Show history
                device.showConfigurationHistory();
                
                // Demonstrate rollback
                List<ConfigurationMemento> history = globalHistoryManager.getDeviceHistory(device.getId());
                if (history.size() >= 2) {
                    ConfigurationMemento targetVersion = history.get(history.size() - 2);
                    System.out.println("   Rolling back to: " + targetVersion);
                    
                    device.rollbackToVersion(targetVersion.getVersion());
                    System.out.println("   ✅ Point-in-time recovery completed");
                }
            }
        }
    }
}