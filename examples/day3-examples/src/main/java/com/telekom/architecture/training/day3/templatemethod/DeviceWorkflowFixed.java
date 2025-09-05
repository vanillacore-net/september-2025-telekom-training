package com.telekom.architecture.training.day3.templatemethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FIXED IMPLEMENTATION using Template Method Pattern
 * 
 * Benefits:
 * - Common workflow steps are implemented once in the base class
 * - Device-specific behavior is implemented in subclasses
 * - Consistent workflow structure across all device types
 * - Easy to modify common behavior without affecting specific implementations
 * - Follows DRY principle
 * 
 * Trainer Notes:
 * - Show how Template Method eliminates code duplication
 * - Demonstrate the inversion of control - framework calls subclass methods
 * - Point out how easy it is to add new device types
 * - Discuss final methods vs hook methods
 */
public class DeviceWorkflowFixed {
    
    // Reusing WorkflowResult from Initial implementation
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
     * Abstract base class defining the template method and common steps
     */
    public static abstract class DeviceProvisioningWorkflow {
        protected final List<String> executedSteps = new ArrayList<>();
        
        /**
         * Template method - defines the workflow algorithm structure
         * This is final - subclasses cannot override the overall workflow
         */
        public final WorkflowResult provisionDevice(String deviceId, Object... parameters) {
            try {
                executedSteps.add("Starting " + getDeviceType() + " provisioning workflow");
                
                // Step 1: Input validation (common logic)
                if (!validateInput(deviceId, parameters)) {
                    return new WorkflowResult(false, getLastValidationError(), executedSteps);
                }
                executedSteps.add("Input validation completed");
                
                // Step 2: Pre-checks (common logic)
                executedSteps.add("Performing pre-checks");
                if (!performPreChecks()) {
                    return new WorkflowResult(false, "Pre-checks failed", executedSteps);
                }
                executedSteps.add("Pre-checks completed");
                
                // Step 3: Device-specific preparation (hook method)
                executedSteps.add("Preparing device-specific configuration");
                prepareDeviceConfiguration(deviceId, parameters);
                executedSteps.add("Device configuration prepared");
                
                // Step 4: Device-specific configuration (abstract method - must be implemented)
                executedSteps.add("Configuring " + getDeviceType().toLowerCase() + "-specific settings");
                configureDevice(deviceId, parameters);
                executedSteps.add(getDeviceType() + " configuration completed");
                
                // Step 5: Apply configuration (common logic)
                executedSteps.add("Applying configuration");
                if (!applyConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration apply failed", executedSteps);
                }
                executedSteps.add("Configuration applied successfully");
                
                // Step 6: Verification (common logic with device-specific validation)
                executedSteps.add("Verifying configuration");
                if (!verifyConfiguration(deviceId) || !validateDeviceSpecificConfiguration(deviceId)) {
                    return new WorkflowResult(false, "Configuration verification failed", executedSteps);
                }
                executedSteps.add("Configuration verified");
                
                // Step 7: Post-setup tasks (common logic)
                executedSteps.add("Performing post-setup tasks");
                performPostSetupTasks(deviceId);
                executedSteps.add("Post-setup tasks completed");
                
                return new WorkflowResult(true, getDeviceType() + " provisioned successfully", executedSteps);
                
            } catch (Exception e) {
                executedSteps.add("Error occurred: " + e.getMessage());
                handleError(e);
                return new WorkflowResult(false, "Provisioning failed: " + e.getMessage(), executedSteps);
            }
        }
        
        // Abstract methods - must be implemented by subclasses
        protected abstract String getDeviceType();
        protected abstract void configureDevice(String deviceId, Object... parameters);
        protected abstract boolean validateDeviceSpecificInput(String deviceId, Object... parameters);
        protected abstract boolean validateDeviceSpecificConfiguration(String deviceId);
        
        // Hook methods - can be overridden by subclasses if needed
        protected void prepareDeviceConfiguration(String deviceId, Object... parameters) {
            // Default implementation - do nothing
            // Subclasses can override if they need specific preparation
        }
        
        protected void handleError(Exception e) {
            // Default error handling - log error
            executedSteps.add("Default error handling performed");
        }
        
        // Common methods - implemented once, used by all subclasses
        private boolean validateInput(String deviceId, Object... parameters) {
            if (deviceId == null || deviceId.trim().isEmpty()) {
                lastValidationError = "Invalid device ID";
                return false;
            }
            
            return validateDeviceSpecificInput(deviceId, parameters);
        }
        
        private String lastValidationError = "";
        private String getLastValidationError() { return lastValidationError; }
        
        protected void setValidationError(String error) {
            this.lastValidationError = error;
        }
        
        private boolean performPreChecks() {
            return checkNetworkConnectivity() && checkSystemResources();
        }
        
        private boolean checkNetworkConnectivity() {
            // Common network connectivity check
            return true; // Simplified
        }
        
        private boolean checkSystemResources() {
            // Common system resource check
            return true; // Simplified
        }
        
        private boolean applyConfiguration(String deviceId) {
            // Common configuration application logic
            return true; // Simplified
        }
        
        private boolean verifyConfiguration(String deviceId) {
            // Common configuration verification logic
            return true; // Simplified
        }
        
        private void performPostSetupTasks(String deviceId) {
            updateInventory(deviceId, getDeviceType());
            logSuccessfulProvisioning(deviceId);
            sendNotification(deviceId);
        }
        
        private void updateInventory(String deviceId, String type) {
            // Common inventory update logic
        }
        
        private void logSuccessfulProvisioning(String deviceId) {
            // Common logging logic
        }
        
        private void sendNotification(String deviceId) {
            // Common notification logic
        }
    }
    
    /**
     * Router-specific implementation
     */
    public static class RouterProvisioningWorkflow extends DeviceProvisioningWorkflow {
        
        @Override
        protected String getDeviceType() {
            return "ROUTER";
        }
        
        @Override
        protected boolean validateDeviceSpecificInput(String deviceId, Object... parameters) {
            if (parameters.length == 0 || !(parameters[0] instanceof String)) {
                setValidationError("IP address parameter required");
                return false;
            }
            
            String ipAddress = (String) parameters[0];
            if (!isValidIP(ipAddress)) {
                setValidationError("Invalid IP address format");
                return false;
            }
            
            return true;
        }
        
        @Override
        protected void configureDevice(String deviceId, Object... parameters) {
            String ipAddress = (String) parameters[0];
            
            configureRoutingTables(deviceId, ipAddress);
            configureBGP(deviceId);
            configureOSPF(deviceId);
        }
        
        @Override
        protected boolean validateDeviceSpecificConfiguration(String deviceId) {
            // Router-specific validation
            return validateRoutingTables(deviceId) && validateBGPConfiguration(deviceId);
        }
        
        @Override
        protected void handleError(Exception e) {
            // Router-specific error handling
            executedSteps.add("Router-specific error handling: rolling back routing changes");
            rollbackRoutingChanges();
        }
        
        // Router-specific private methods
        private void configureRoutingTables(String deviceId, String ipAddress) {
            // Router-specific routing configuration
        }
        
        private void configureBGP(String deviceId) {
            // BGP configuration
        }
        
        private void configureOSPF(String deviceId) {
            // OSPF configuration
        }
        
        private boolean isValidIP(String ip) {
            // IP validation logic
            return ip != null && ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"); // Simplified
        }
        
        private boolean validateRoutingTables(String deviceId) {
            return true; // Simplified
        }
        
        private boolean validateBGPConfiguration(String deviceId) {
            return true; // Simplified
        }
        
        private void rollbackRoutingChanges() {
            // Rollback logic
        }
    }
    
    /**
     * Switch-specific implementation
     */
    public static class SwitchProvisioningWorkflow extends DeviceProvisioningWorkflow {
        
        @Override
        protected String getDeviceType() {
            return "SWITCH";
        }
        
        @Override
        protected boolean validateDeviceSpecificInput(String deviceId, Object... parameters) {
            if (parameters.length == 0 || !(parameters[0] instanceof Integer)) {
                setValidationError("VLAN ID parameter required");
                return false;
            }
            
            Integer vlanId = (Integer) parameters[0];
            if (vlanId <= 0 || vlanId > 4094) {
                setValidationError("Invalid VLAN ID: must be between 1 and 4094");
                return false;
            }
            
            return true;
        }
        
        @Override
        protected void configureDevice(String deviceId, Object... parameters) {
            Integer vlanId = (Integer) parameters[0];
            
            configureVLANs(deviceId, vlanId);
            configureSpanningTree(deviceId);
            configurePortSecurity(deviceId);
        }
        
        @Override
        protected boolean validateDeviceSpecificConfiguration(String deviceId) {
            // Switch-specific validation
            return validateVLANConfiguration(deviceId) && validateSpanningTreeConfiguration(deviceId);
        }
        
        @Override
        protected void prepareDeviceConfiguration(String deviceId, Object... parameters) {
            // Switch-specific preparation - clear existing VLANs
            executedSteps.add("Clearing existing VLAN configuration");
            clearExistingVLANs(deviceId);
        }
        
        // Switch-specific private methods
        private void configureVLANs(String deviceId, int vlanId) {
            // VLAN configuration
        }
        
        private void configureSpanningTree(String deviceId) {
            // Spanning Tree Protocol configuration
        }
        
        private void configurePortSecurity(String deviceId) {
            // Port security configuration
        }
        
        private boolean validateVLANConfiguration(String deviceId) {
            return true; // Simplified
        }
        
        private boolean validateSpanningTreeConfiguration(String deviceId) {
            return true; // Simplified
        }
        
        private void clearExistingVLANs(String deviceId) {
            // Clear existing VLAN configuration
        }
    }
    
    /**
     * Firewall-specific implementation
     */
    public static class FirewallProvisioningWorkflow extends DeviceProvisioningWorkflow {
        
        @Override
        protected String getDeviceType() {
            return "FIREWALL";
        }
        
        @Override
        protected boolean validateDeviceSpecificInput(String deviceId, Object... parameters) {
            if (parameters.length == 0 || !(parameters[0] instanceof String)) {
                setValidationError("Security policy parameter required");
                return false;
            }
            
            String securityPolicy = (String) parameters[0];
            if (securityPolicy == null || securityPolicy.trim().isEmpty()) {
                setValidationError("Security policy cannot be empty");
                return false;
            }
            
            return true;
        }
        
        @Override
        protected void configureDevice(String deviceId, Object... parameters) {
            String securityPolicy = (String) parameters[0];
            
            configureSecurityRules(deviceId, securityPolicy);
            configureNAT(deviceId);
            configureVPN(deviceId);
        }
        
        @Override
        protected boolean validateDeviceSpecificConfiguration(String deviceId) {
            // Firewall-specific validation
            return validateSecurityRules(deviceId) && validateNATConfiguration(deviceId);
        }
        
        @Override
        protected void handleError(Exception e) {
            // Firewall-specific error handling - security is critical
            executedSteps.add("Firewall-specific error handling: applying safe default rules");
            applyFailsafeRules();
        }
        
        // Firewall-specific private methods
        private void configureSecurityRules(String deviceId, String policy) {
            // Security rules configuration
        }
        
        private void configureNAT(String deviceId) {
            // NAT configuration
        }
        
        private void configureVPN(String deviceId) {
            // VPN configuration
        }
        
        private boolean validateSecurityRules(String deviceId) {
            return true; // Simplified
        }
        
        private boolean validateNATConfiguration(String deviceId) {
            return true; // Simplified
        }
        
        private void applyFailsafeRules() {
            // Apply safe default firewall rules
        }
    }
    
    /**
     * Example of how easy it is to add a new device type
     */
    public static class LoadBalancerProvisioningWorkflow extends DeviceProvisioningWorkflow {
        
        @Override
        protected String getDeviceType() {
            return "LOAD_BALANCER";
        }
        
        @Override
        protected boolean validateDeviceSpecificInput(String deviceId, Object... parameters) {
            // Load balancer validation logic
            return parameters.length > 0 && parameters[0] instanceof String[];
        }
        
        @Override
        protected void configureDevice(String deviceId, Object... parameters) {
            String[] backendServers = (String[]) parameters[0];
            configureBackendPool(deviceId, backendServers);
            configureHealthChecks(deviceId);
            configureLoadBalancingAlgorithm(deviceId);
        }
        
        @Override
        protected boolean validateDeviceSpecificConfiguration(String deviceId) {
            return validateBackendPool(deviceId) && validateHealthChecks(deviceId);
        }
        
        private void configureBackendPool(String deviceId, String[] servers) {
            // Backend pool configuration
        }
        
        private void configureHealthChecks(String deviceId) {
            // Health check configuration
        }
        
        private void configureLoadBalancingAlgorithm(String deviceId) {
            // Load balancing algorithm configuration
        }
        
        private boolean validateBackendPool(String deviceId) {
            return true; // Simplified
        }
        
        private boolean validateHealthChecks(String deviceId) {
            return true; // Simplified
        }
    }
}