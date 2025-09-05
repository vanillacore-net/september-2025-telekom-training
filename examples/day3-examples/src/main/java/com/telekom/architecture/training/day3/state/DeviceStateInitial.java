package com.telekom.architecture.training.day3.state;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * INITIAL (PROBLEMATIC) IMPLEMENTATION
 * 
 * Problems:
 * - Complex if/else chains for state transitions
 * - State logic scattered throughout the class
 * - Difficult to add new states or modify transitions
 * - Inconsistent state validation
 * - Hard to test individual state behaviors
 * 
 * Trainer Notes:
 * - Show how state logic becomes unmanageable
 * - Demonstrate difficulty in extending state machine
 * - Point out violation of Open/Closed Principle
 */
public class DeviceStateInitial {
    
    public enum DeviceStatus {
        INACTIVE, INITIALIZING, ACTIVE, MAINTENANCE, ERROR, DECOMMISSIONED
    }
    
    public static class Device {
        private final String id;
        private final String name;
        private DeviceStatus status;
        private String errorMessage;
        private LocalDateTime lastStateChange;
        private List<String> stateHistory;
        
        public Device(String id, String name) {
            this.id = id;
            this.name = name;
            this.status = DeviceStatus.INACTIVE;
            this.lastStateChange = LocalDateTime.now();
            this.stateHistory = new ArrayList<>();
            this.stateHistory.add("Device created in INACTIVE state");
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public DeviceStatus getStatus() { return status; }
        public String getErrorMessage() { return errorMessage; }
        public LocalDateTime getLastStateChange() { return lastStateChange; }
        public List<String> getStateHistory() { return new ArrayList<>(stateHistory); }
        
        /**
         * PROBLEMATIC: Complex activate method with nested conditions
         */
        public boolean activate() throws DeviceOperationException {
            if (status == DeviceStatus.INACTIVE) {
                // Initialize device
                status = DeviceStatus.INITIALIZING;
                updateStateChange("Activating from INACTIVE");
                
                // Simulate initialization
                if (!performHealthCheck()) {
                    status = DeviceStatus.ERROR;
                    errorMessage = "Health check failed during activation";
                    updateStateChange("Activation failed - moved to ERROR");
                    return false;
                }
                
                // Complete activation
                status = DeviceStatus.ACTIVE;
                updateStateChange("Successfully activated");
                return true;
                
            } else if (status == DeviceStatus.MAINTENANCE) {
                // Exit maintenance and activate
                status = DeviceStatus.ACTIVE;
                updateStateChange("Activated from MAINTENANCE");
                return true;
                
            } else if (status == DeviceStatus.ERROR) {
                // Try to recover from error
                if (attemptErrorRecovery()) {
                    status = DeviceStatus.ACTIVE;
                    errorMessage = null;
                    updateStateChange("Recovered from ERROR and activated");
                    return true;
                } else {
                    updateStateChange("Failed to recover from ERROR state");
                    return false;
                }
                
            } else if (status == DeviceStatus.ACTIVE) {
                updateStateChange("Already ACTIVE - no action needed");
                return true;
                
            } else {
                throw new DeviceOperationException(
                    "Cannot activate device from state: " + status);
            }
        }
        
        /**
         * PROBLEMATIC: Another complex method with similar if/else structure
         */
        public boolean deactivate() throws DeviceOperationException {
            if (status == DeviceStatus.ACTIVE) {
                // Graceful shutdown
                performGracefulShutdown();
                status = DeviceStatus.INACTIVE;
                updateStateChange("Deactivated from ACTIVE");
                return true;
                
            } else if (status == DeviceStatus.MAINTENANCE) {
                // Exit maintenance and deactivate
                status = DeviceStatus.INACTIVE;
                updateStateChange("Deactivated from MAINTENANCE");
                return true;
                
            } else if (status == DeviceStatus.ERROR) {
                // Force deactivation from error state
                status = DeviceStatus.INACTIVE;
                errorMessage = null;
                updateStateChange("Force deactivated from ERROR");
                return true;
                
            } else if (status == DeviceStatus.INACTIVE) {
                updateStateChange("Already INACTIVE - no action needed");
                return true;
                
            } else {
                throw new DeviceOperationException(
                    "Cannot deactivate device from state: " + status);
            }
        }
        
        /**
         * PROBLEMATIC: Maintenance logic scattered and inconsistent
         */
        public boolean enterMaintenance() throws DeviceOperationException {
            if (status == DeviceStatus.ACTIVE) {
                // Check business rules for maintenance
                if (!isMaintenanceAllowed()) {
                    throw new DeviceOperationException(
                        "Maintenance not allowed during business hours");
                }
                
                // Redirect traffic and enter maintenance
                redirectTraffic();
                status = DeviceStatus.MAINTENANCE;
                updateStateChange("Entered MAINTENANCE from ACTIVE");
                return true;
                
            } else if (status == DeviceStatus.MAINTENANCE) {
                updateStateChange("Already in MAINTENANCE - no action needed");
                return true;
                
            } else {
                throw new DeviceOperationException(
                    "Cannot enter maintenance from state: " + status);
            }
        }
        
        /**
         * PROBLEMATIC: Exit maintenance with complex validation
         */
        public boolean exitMaintenance() throws DeviceOperationException {
            if (status == DeviceStatus.MAINTENANCE) {
                // Validate device is ready
                if (!performMaintenanceValidation()) {
                    throw new DeviceOperationException(
                        "Device not ready to exit maintenance mode");
                }
                
                // Restore traffic and activate
                restoreTraffic();
                status = DeviceStatus.ACTIVE;
                updateStateChange("Exited MAINTENANCE to ACTIVE");
                return true;
                
            } else {
                throw new DeviceOperationException(
                    "Device is not in maintenance mode");
            }
        }
        
        /**
         * PROBLEMATIC: Error handling inconsistent across states
         */
        public void handleError(String error) {
            errorMessage = error;
            
            if (status == DeviceStatus.ACTIVE || status == DeviceStatus.MAINTENANCE) {
                status = DeviceStatus.ERROR;
                updateStateChange("Moved to ERROR due to: " + error);
                
            } else if (status == DeviceStatus.INITIALIZING) {
                status = DeviceStatus.ERROR;
                updateStateChange("Initialization failed: " + error);
                
            } else {
                // Already in error or inactive - just update message
                updateStateChange("Error occurred in state " + status + ": " + error);
            }
        }
        
        // Private helper methods
        private void updateStateChange(String message) {
            lastStateChange = LocalDateTime.now();
            stateHistory.add(lastStateChange + ": " + message);
        }
        
        private boolean performHealthCheck() {
            // Simulate health check - 80% success rate
            return Math.random() > 0.2;
        }
        
        private boolean attemptErrorRecovery() {
            // Simulate error recovery - 60% success rate
            return Math.random() > 0.4;
        }
        
        private void performGracefulShutdown() {
            // Simulate shutdown process
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private boolean isMaintenanceAllowed() {
            // Business rule: No maintenance during business hours (9 AM - 5 PM)
            int hour = LocalDateTime.now().getHour();
            return hour < 9 || hour > 17;
        }
        
        private void redirectTraffic() {
            // Simulate traffic redirection
            updateStateChange("Traffic redirected for maintenance");
        }
        
        private void restoreTraffic() {
            // Simulate traffic restoration
            updateStateChange("Traffic restored after maintenance");
        }
        
        private boolean performMaintenanceValidation() {
            // Simulate validation - 90% success rate
            return Math.random() > 0.1;
        }
    }
    
    public static class DeviceOperationException extends Exception {
        public DeviceOperationException(String message) {
            super(message);
        }
    }
}