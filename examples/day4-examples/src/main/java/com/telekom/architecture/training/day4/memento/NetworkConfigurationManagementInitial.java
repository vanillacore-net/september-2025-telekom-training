package com.telekom.architecture.training.day4.memento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * INITIAL IMPLEMENTATION - Configuration Horror without Rollback
 * 
 * Problems demonstrated:
 * - No state preservation before configuration changes
 * - Impossible to rollback failed configurations
 * - No configuration history or audit trail
 * - Risk of network outages from failed changes
 * - No atomic configuration operations
 * - Lost previous state when changes fail
 * 
 * Business Impact:
 * - Network outages from failed configuration changes
 * - Extended downtime when rollback is impossible
 * - Regulatory compliance issues due to missing audit trail
 * - Operations teams afraid to make changes
 * - Manual recovery processes taking hours
 * 
 * Trainer Notes:
 * - Demonstrate the horror of failed configuration without rollback
 * - Show how one failed change can break entire network segments
 * - Point out compliance and audit issues
 * - Emphasize the operational risk and fear factor
 */
public class NetworkConfigurationManagementInitial {
    
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
            // Initialize with default configuration
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
        
        // PROBLEM: Getters return direct references to internal state
        public Map<String, String> getInterfaces() { return interfaces; }
        public Map<String, String> getRoutingRules() { return routingRules; }
        public Map<String, String> getSecurityPolicies() { return securityPolicies; }
        public Map<String, String> getQosSettings() { return qosSettings; }
        public Map<String, String> getMonitoringConfig() { return monitoringConfig; }
        
        public String getDeviceId() { return deviceId; }
        public String getOperatingMode() { return operatingMode; }
        public void setOperatingMode(String mode) { 
            this.operatingMode = mode;
            this.lastModified = LocalDateTime.now();
        }
        public LocalDateTime getLastModified() { return lastModified; }
        
        public void updateLastModified() {
            this.lastModified = LocalDateTime.now();
        }
        
        @Override
        public String toString() {
            return String.format("Config[%s] - Mode: %s, Modified: %s", 
                deviceId, operatingMode, lastModified.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
    
    // PROBLEMATIC: Network device without state management
    public static class NetworkDevice {
        private String id;
        private String type;
        private String status = "ONLINE";
        private NetworkDeviceConfiguration configuration;
        
        public NetworkDevice(String id, String type) {
            this.id = id;
            this.type = type;
            this.configuration = new NetworkDeviceConfiguration(id);
        }
        
        // HORROR: Direct configuration modification without backup
        public void updateInterfaceConfiguration(String interfaceName, String config) {
            System.out.println("[" + id + "] Updating interface " + interfaceName + " to " + config);
            configuration.getInterfaces().put(interfaceName, config);
            configuration.updateLastModified();
            // No backup of previous state! What if this fails?
        }
        
        public void updateRoutingConfiguration(String routeName, String config) {
            System.out.println("[" + id + "] Updating route " + routeName + " to " + config);
            configuration.getRoutingRules().put(routeName, config);
            configuration.updateLastModified();
            // Previous routing state is lost!
        }
        
        public void updateSecurityConfiguration(String policyName, String config) {
            System.out.println("[" + id + "] Updating security policy " + policyName + " to " + config);
            configuration.getSecurityPolicies().put(policyName, config);
            configuration.updateLastModified();
            // What if security update breaks connectivity?
        }
        
        public void updateQosConfiguration(String setting, String value) {
            System.out.println("[" + id + "] Updating QoS " + setting + " to " + value);
            configuration.getQosSettings().put(setting, value);
            configuration.updateLastModified();
            // QoS changes can affect entire network performance!
        }
        
        public void setOperatingMode(String mode) {
            System.out.println("[" + id + "] Changing operating mode to " + mode);
            configuration.setOperatingMode(mode);
            // Mode changes are critical - no rollback possible!
        }
        
        // CATASTROPHIC: Batch configuration update with no atomicity
        public void applyCompleteConfiguration(Map<String, String> interfaces,
                                             Map<String, String> routing,
                                             Map<String, String> security,
                                             Map<String, String> qos,
                                             String operatingMode) {
            
            System.out.println("[" + id + "] ⚠️  CRITICAL: Applying complete configuration update...");
            
            try {
                // DISASTER WAITING: No transaction, no rollback, all-or-nothing gamble
                
                // Step 1: Clear existing config (POINT OF NO RETURN!)
                configuration.getInterfaces().clear();
                System.out.println("   → Interfaces cleared (network connectivity lost!)");
                
                // Step 2: Update interfaces (What if this fails?)
                configuration.getInterfaces().putAll(interfaces);
                System.out.println("   → Interfaces updated");
                
                // Step 3: Update routing (Critical routing changes)
                configuration.getRoutingRules().clear();
                configuration.getRoutingRules().putAll(routing);
                System.out.println("   → Routing updated");
                
                // Step 4: Update security (Security holes possible)
                configuration.getSecurityPolicies().clear();
                configuration.getSecurityPolicies().putAll(security);
                System.out.println("   → Security policies updated");
                
                // Step 5: Update QoS (Performance impact)
                configuration.getQosSettings().clear();
                configuration.getQosSettings().putAll(qos);
                System.out.println("   → QoS settings updated");
                
                // Step 6: Change operating mode (Final critical change)
                configuration.setOperatingMode(operatingMode);
                System.out.println("   → Operating mode changed to " + operatingMode);
                
                // Simulate potential failure point
                if (Math.random() < 0.3) { // 30% chance of failure
                    throw new RuntimeException("Configuration validation failed!");
                }
                
                System.out.println("[" + id + "] ✅ Configuration applied successfully");
                
            } catch (Exception e) {
                // HORROR: We're in an unknown state with no way back!
                System.err.println("[" + id + "] 🚨 CATASTROPHIC FAILURE: " + e.getMessage());
                System.err.println("   → Device in UNKNOWN configuration state");
                System.err.println("   → Previous configuration LOST");
                System.err.println("   → Manual recovery required");
                System.err.println("   → Network connectivity may be broken");
                
                this.status = "CONFIGURATION_FAILED";
                
                // In reality, this is where the operations team gets called at 3 AM
                System.err.println("   → 📞 EMERGENCY: Operations team notified");
                System.err.println("   → 🕐 Estimated recovery time: 2-4 hours");
                System.err.println("   → 💰 Business impact: €50,000+ per hour");
            }
        }
        
        // Getters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public NetworkDeviceConfiguration getConfiguration() { return configuration; }
    }
    
    // PROBLEMATIC: Network operations without safety nets
    public static class NetworkOperationsCenter {
        private List<NetworkDevice> managedDevices = new ArrayList<>();
        private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        public void addDevice(NetworkDevice device) {
            managedDevices.add(device);
            System.out.println(timeFormatter.format(LocalDateTime.now()) + 
                " [NOC] Device added: " + device.getId());
        }
        
        // DANGEROUS: Mass configuration update without rollback capability
        public void performMaintenanceWindow(String maintenanceType) {
            System.out.println("\n" + timeFormatter.format(LocalDateTime.now()) + 
                " [NOC] 🔧 Starting maintenance window: " + maintenanceType);
            
            switch (maintenanceType) {
                case "SECURITY_UPDATE":
                    performSecurityUpdate();
                    break;
                case "QOS_OPTIMIZATION":
                    performQosOptimization();
                    break;
                case "ROUTING_RECONFIGURATION":
                    performRoutingReconfiguration();
                    break;
                case "COMPLETE_OVERHAUL":
                    performCompleteOverhaul();
                    break;
            }
            
            System.out.println(timeFormatter.format(LocalDateTime.now()) + 
                " [NOC] Maintenance window completed\n");
        }
        
        private void performSecurityUpdate() {
            System.out.println("   Applying security updates to all devices...");
            
            for (NetworkDevice device : managedDevices) {
                // RISK: No backup before critical security changes
                device.updateSecurityConfiguration("firewall", "STRICT_MODE");
                device.updateSecurityConfiguration("intrusion_detection", "AGGRESSIVE");
                device.updateSecurityConfiguration("access_control", "WHITELIST_ONLY");
                
                // Simulate configuration failure
                if (Math.random() < 0.2) {
                    System.err.println("   ❌ Security update failed on " + device.getId());
                    device.setStatus("SECURITY_CONFIG_FAILED");
                    // No way to rollback - security is now in unknown state!
                }
            }
        }
        
        private void performQosOptimization() {
            System.out.println("   Optimizing QoS settings across network...");
            
            for (NetworkDevice device : managedDevices) {
                // DANGER: QoS changes can affect entire network performance
                device.updateQosConfiguration("bandwidth_limit", "10000Mbps");
                device.updateQosConfiguration("priority_queues", "16");
                device.updateQosConfiguration("traffic_shaping", "ENABLED");
                
                if (Math.random() < 0.15) {
                    System.err.println("   ❌ QoS optimization failed on " + device.getId());
                    device.setStatus("QOS_CONFIG_FAILED");
                    // Network performance is now unpredictable!
                }
            }
        }
        
        private void performRoutingReconfiguration() {
            System.out.println("   Reconfiguring routing protocols...");
            
            for (NetworkDevice device : managedDevices) {
                // CRITICAL: Routing changes can isolate network segments
                device.updateRoutingConfiguration("bgp_as", "65001");
                device.updateRoutingConfiguration("ospf_area", "0.0.0.1");
                device.updateRoutingConfiguration("static_routes", "10.0.0.0/8->10.1.1.1");
                
                if (Math.random() < 0.25) {
                    System.err.println("   ❌ Routing reconfiguration failed on " + device.getId());
                    device.setStatus("ROUTING_CONFIG_FAILED");
                    // Network segments may be isolated!
                }
            }
        }
        
        private void performCompleteOverhaul() {
            System.out.println("   🚨 PERFORMING COMPLETE CONFIGURATION OVERHAUL");
            System.out.println("   ⚠️  WARNING: High risk operation - no rollback possible");
            
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
            
            for (NetworkDevice device : managedDevices) {
                System.out.println("   🎲 Rolling dice for " + device.getId() + "...");
                // ULTIMATE HORROR: Complete configuration replacement without backup
                device.applyCompleteConfiguration(newInterfaces, newRouting, newSecurity, newQos, "HIGH_PERFORMANCE");
            }
        }
        
        public void showNetworkStatus() {
            System.out.println("\n=== Network Status ===");
            for (NetworkDevice device : managedDevices) {
                System.out.println(device.getId() + " [" + device.getType() + "] - Status: " + 
                    device.getStatus() + " | " + device.getConfiguration().toString());
            }
            
            long failedDevices = managedDevices.stream()
                .mapToLong(d -> d.getStatus().contains("FAILED") ? 1 : 0)
                .sum();
                
            if (failedDevices > 0) {
                System.err.println("⚠️  " + failedDevices + " devices in FAILED state - manual intervention required!");
                System.err.println("   Operations team response time: 30+ minutes");
                System.err.println("   Expected recovery time: 2-4 hours");
                System.err.println("   Business impact: CRITICAL");
            }
            System.out.println("========================\n");
        }
        
        // USELESS: Attempt to "fix" problems without proper rollback
        public void attemptEmergencyRecovery() {
            System.out.println("🚨 EMERGENCY RECOVERY ATTEMPT");
            System.out.println("   (This usually makes things worse...)");
            
            for (NetworkDevice device : managedDevices) {
                if (device.getStatus().contains("FAILED")) {
                    System.out.println("   Attempting to recover " + device.getId());
                    
                    // Desperate attempt to restore "some" configuration
                    device.updateInterfaceConfiguration("eth0", "192.168.1.1/24");
                    device.updateRoutingConfiguration("default", "192.168.1.254");
                    device.setOperatingMode("SAFE_MODE");
                    device.setStatus("PARTIALLY_RECOVERED");
                    
                    System.out.println("   " + device.getId() + " partially recovered (maybe)");
                }
            }
            
            System.out.println("   Recovery attempt completed");
            System.out.println("   Success rate: ~50% (if we're lucky)");
            System.out.println("   Recommended: Full manual reconfiguration");
        }
    }
}