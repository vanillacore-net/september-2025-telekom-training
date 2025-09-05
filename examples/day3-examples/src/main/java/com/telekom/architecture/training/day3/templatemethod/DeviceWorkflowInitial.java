package com.telekom.architecture.training.day3.templatemethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * INITIAL (PROBLEMATIC) IMPLEMENTATION
 * 
 * Problems:
 * - Code duplication in similar workflows
 * - Common steps repeated across different device provisioning processes
 * - Difficult to maintain consistency across workflows
 * - Hard to modify common behavior without touching all implementations
 * 
 * Trainer Notes:
 * - Point out duplicated validation, logging, and cleanup code
 * - Show how changes to common steps require modifications everywhere
 * - Demonstrate violation of DRY principle
 * - Highlight testing complexity due to duplication
 */
public class DeviceWorkflowInitial {
    
    public static class WorkflowResult {
        private final boolean success;
        private final String message;
        private final List<String> steps;
        private final LocalDateTime timestamp;
        
        public WorkflowResult(boolean success, String message, List<String> steps) {
            this.success = success;
            this.message = message;
            this.steps = new ArrayList<>(steps);
            this.timestamp = LocalDateTime.now();
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<String> getSteps() { return steps; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    /**
     * Router provisioning workflow - lots of duplicated logic
     */
    public static class RouterProvisioningWorkflow {
        private final List<String> executedSteps = new ArrayList<>();
        
        public WorkflowResult provisionRouter(String deviceId, String ipAddress) {
            try {
                // Step 1: Input validation (DUPLICATED)
                executedSteps.add("Starting router provisioning workflow");
                if (deviceId == null || deviceId.trim().isEmpty()) {
                    return new WorkflowResult(false, "Invalid device ID", executedSteps);
                }
                if (ipAddress == null || !isValidIP(ipAddress)) {
                    return new WorkflowResult(false, "Invalid IP address", executedSteps);
                }
                executedSteps.add("Input validation completed");
                
                // Step 2: Pre-checks (DUPLICATED)
                executedSteps.add("Performing pre-checks");
                if (!checkNetworkConnectivity()) {
                    return new WorkflowResult(false, "Network connectivity check failed", executedSteps);
                }
                executedSteps.add("Pre-checks completed");
                
                // Step 3: Device-specific configuration (UNIQUE)
                executedSteps.add("Configuring router-specific settings");
                configureRoutingTables(deviceId);
                configureBGP(deviceId);
                configureOSPF(deviceId);
                executedSteps.add("Router configuration completed");
                
                // Step 4: Apply configuration (DUPLICATED)
                executedSteps.add("Applying configuration");
                if (!applyConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration apply failed", executedSteps);
                }
                executedSteps.add("Configuration applied successfully");
                
                // Step 5: Verification (DUPLICATED)
                executedSteps.add("Verifying configuration");
                if (!verifyConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration verification failed", executedSteps);
                }
                executedSteps.add("Configuration verified");
                
                // Step 6: Post-setup tasks (DUPLICATED)
                executedSteps.add("Performing post-setup tasks");
                updateInventory(deviceId, "ROUTER");
                logSuccessfulProvisioning(deviceId);
                executedSteps.add("Post-setup tasks completed");
                
                return new WorkflowResult(true, "Router provisioned successfully", executedSteps);
                
            } catch (Exception e) {
                executedSteps.add("Error occurred: " + e.getMessage());
                return new WorkflowResult(false, "Provisioning failed: " + e.getMessage(), executedSteps);
            }
        }
        
        // Router-specific methods
        private void configureRoutingTables(String deviceId) {
            // Router-specific routing configuration
        }
        
        private void configureBGP(String deviceId) {
            // BGP configuration
        }
        
        private void configureOSPF(String deviceId) {
            // OSPF configuration
        }
        
        // Common methods (DUPLICATED across classes)
        private boolean isValidIP(String ip) { return true; } // Simplified
        private boolean checkNetworkConnectivity() { return true; }
        private boolean applyConfiguration(String deviceId) { return true; }
        private boolean verifyConfiguration(String deviceId) { return true; }
        private void updateInventory(String deviceId, String type) {}
        private void logSuccessfulProvisioning(String deviceId) {}
    }
    
    /**
     * Switch provisioning workflow - notice the duplication!
     */
    public static class SwitchProvisioningWorkflow {
        private final List<String> executedSteps = new ArrayList<>();
        
        public WorkflowResult provisionSwitch(String deviceId, int vlanId) {
            try {
                // Step 1: Input validation (DUPLICATED from RouterProvisioningWorkflow)
                executedSteps.add("Starting switch provisioning workflow");
                if (deviceId == null || deviceId.trim().isEmpty()) {
                    return new WorkflowResult(false, "Invalid device ID", executedSteps);
                }
                if (vlanId <= 0 || vlanId > 4094) {
                    return new WorkflowResult(false, "Invalid VLAN ID", executedSteps);
                }
                executedSteps.add("Input validation completed");
                
                // Step 2: Pre-checks (DUPLICATED)
                executedSteps.add("Performing pre-checks");
                if (!checkNetworkConnectivity()) {
                    return new WorkflowResult(false, "Network connectivity check failed", executedSteps);
                }
                executedSteps.add("Pre-checks completed");
                
                // Step 3: Device-specific configuration (UNIQUE)
                executedSteps.add("Configuring switch-specific settings");
                configureVLANs(deviceId, vlanId);
                configureSpanningTree(deviceId);
                configurePortSecurity(deviceId);
                executedSteps.add("Switch configuration completed");
                
                // Step 4: Apply configuration (DUPLICATED)
                executedSteps.add("Applying configuration");
                if (!applyConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration apply failed", executedSteps);
                }
                executedSteps.add("Configuration applied successfully");
                
                // Step 5: Verification (DUPLICATED)
                executedSteps.add("Verifying configuration");
                if (!verifyConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration verification failed", executedSteps);
                }
                executedSteps.add("Configuration verified");
                
                // Step 6: Post-setup tasks (DUPLICATED)
                executedSteps.add("Performing post-setup tasks");
                updateInventory(deviceId, "SWITCH");
                logSuccessfulProvisioning(deviceId);
                executedSteps.add("Post-setup tasks completed");
                
                return new WorkflowResult(true, "Switch provisioned successfully", executedSteps);
                
            } catch (Exception e) {
                executedSteps.add("Error occurred: " + e.getMessage());
                return new WorkflowResult(false, "Provisioning failed: " + e.getMessage(), executedSteps);
            }
        }
        
        // Switch-specific methods
        private void configureVLANs(String deviceId, int vlanId) {
            // VLAN configuration
        }
        
        private void configureSpanningTree(String deviceId) {
            // Spanning Tree Protocol configuration
        }
        
        private void configurePortSecurity(String deviceId) {
            // Port security configuration
        }
        
        // Common methods (DUPLICATED - same as in RouterProvisioningWorkflow!)
        private boolean checkNetworkConnectivity() { return true; }
        private boolean applyConfiguration(String deviceId) { return true; }
        private boolean verifyConfiguration(String deviceId) { return true; }
        private void updateInventory(String deviceId, String type) {}
        private void logSuccessfulProvisioning(String deviceId) {}
    }
    
    /**
     * Firewall provisioning workflow - even more duplication!
     */
    public static class FirewallProvisioningWorkflow {
        private final List<String> executedSteps = new ArrayList<>();
        
        public WorkflowResult provisionFirewall(String deviceId, String securityPolicy) {
            try {
                // Step 1: Input validation (DUPLICATED again!)
                executedSteps.add("Starting firewall provisioning workflow");
                if (deviceId == null || deviceId.trim().isEmpty()) {
                    return new WorkflowResult(false, "Invalid device ID", executedSteps);
                }
                if (securityPolicy == null || securityPolicy.trim().isEmpty()) {
                    return new WorkflowResult(false, "Invalid security policy", executedSteps);
                }
                executedSteps.add("Input validation completed");
                
                // Step 2: Pre-checks (DUPLICATED again!)
                executedSteps.add("Performing pre-checks");
                if (!checkNetworkConnectivity()) {
                    return new WorkflowResult(false, "Network connectivity check failed", executedSteps);
                }
                executedSteps.add("Pre-checks completed");
                
                // Step 3: Device-specific configuration (UNIQUE)
                executedSteps.add("Configuring firewall-specific settings");
                configureSecurityRules(deviceId, securityPolicy);
                configureNAT(deviceId);
                configureVPN(deviceId);
                executedSteps.add("Firewall configuration completed");
                
                // Step 4: Apply configuration (DUPLICATED again!)
                executedSteps.add("Applying configuration");
                if (!applyConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration apply failed", executedSteps);
                }
                executedSteps.add("Configuration applied successfully");
                
                // Step 5: Verification (DUPLICATED again!)
                executedSteps.add("Verifying configuration");
                if (!verifyConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration verification failed", executedSteps);
                }
                executedSteps.add("Configuration verified");
                
                // Step 6: Post-setup tasks (DUPLICATED again!)
                executedSteps.add("Performing post-setup tasks");
                updateInventory(deviceId, "FIREWALL");
                logSuccessfulProvisioning(deviceId);
                executedSteps.add("Post-setup tasks completed");
                
                return new WorkflowResult(true, "Firewall provisioned successfully", executedSteps);
                
            } catch (Exception e) {
                executedSteps.add("Error occurred: " + e.getMessage());
                return new WorkflowResult(false, "Provisioning failed: " + e.getMessage(), executedSteps);
            }
        }
        
        // Firewall-specific methods
        private void configureSecurityRules(String deviceId, String policy) {
            // Security rules configuration
        }
        
        private void configureNAT(String deviceId) {
            // NAT configuration
        }
        
        private void configureVPN(String deviceId) {
            // VPN configuration
        }
        
        // Common methods (DUPLICATED yet again!)
        private boolean checkNetworkConnectivity() { return true; }
        private boolean applyConfiguration(String deviceId) { return true; }
        private boolean verifyConfiguration(String deviceId) { return true; }
        private void updateInventory(String deviceId, String type) {}
        private void logSuccessfulProvisioning(String deviceId) {}
    }
}