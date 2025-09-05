package com.telekom.architecture.training.day3.state;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * FIXED IMPLEMENTATION using STATE PATTERN
 * 
 * Benefits:
 * - Clean separation of state-specific logic
 * - Easy to add new states
 * - Consistent state transition validation
 * - Each state handles its own behavior
 * - Improved testability and maintainability
 * 
 * Trainer Notes:
 * - Show how State Pattern eliminates if/else chains
 * - Demonstrate easy extensibility with new states
 * - Point out how each state class has single responsibility
 * - Show improved error handling and validation
 */
public class DeviceStateFixed {
    
    // State Interface
    public interface DeviceState {
        StateTransitionResult activate(DeviceContext context);
        StateTransitionResult deactivate(DeviceContext context);
        StateTransitionResult enterMaintenance(DeviceContext context);
        StateTransitionResult exitMaintenance(DeviceContext context);
        StateTransitionResult handleError(DeviceContext context, String error);
        
        String getStateName();
        Set<String> getAllowedTransitions();
        boolean isOperational();
        boolean allowsTraffic();
    }
    
    // Result of state transition
    public static class StateTransitionResult {
        private final boolean success;
        private final String message;
        private final DeviceState newState;
        private final String errorCode;
        
        public StateTransitionResult(boolean success, String message, DeviceState newState) {
            this(success, message, newState, null);
        }
        
        public StateTransitionResult(boolean success, String message, DeviceState newState, String errorCode) {
            this.success = success;
            this.message = message;
            this.newState = newState;
            this.errorCode = errorCode;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public DeviceState getNewState() { return newState; }
        public String getErrorCode() { return errorCode; }
    }
    
    // Device Context - maintains current state
    public static class DeviceContext {
        private final String id;
        private final String name;
        private DeviceState currentState;
        private String errorMessage;
        private LocalDateTime lastStateChange;
        private List<String> stateHistory;
        
        public DeviceContext(String id, String name) {
            this.id = id;
            this.name = name;
            this.currentState = new InactiveState();
            this.lastStateChange = LocalDateTime.now();
            this.stateHistory = new ArrayList<>();
            this.stateHistory.add("Device created in INACTIVE state");
        }
        
        // Public interface methods
        public StateTransitionResult activate() {
            return executeTransition(() -> currentState.activate(this), "activate");
        }
        
        public StateTransitionResult deactivate() {
            return executeTransition(() -> currentState.deactivate(this), "deactivate");
        }
        
        public StateTransitionResult enterMaintenance() {
            return executeTransition(() -> currentState.enterMaintenance(this), "enterMaintenance");
        }
        
        public StateTransitionResult exitMaintenance() {
            return executeTransition(() -> currentState.exitMaintenance(this), "exitMaintenance");
        }
        
        public StateTransitionResult handleError(String error) {
            return executeTransition(() -> currentState.handleError(this, error), "handleError");
        }
        
        // State management
        private StateTransitionResult executeTransition(
            java.util.function.Supplier<StateTransitionResult> transition, 
            String transitionName) {
            
            String previousState = currentState.getStateName();
            StateTransitionResult result = transition.get();
            
            if (result.isSuccess() && result.getNewState() != null) {
                changeState(result.getNewState());
            }
            
            updateStateChange(String.format("%s: %s", transitionName, result.getMessage()));
            return result;
        }
        
        public void changeState(DeviceState newState) {
            String previousStateName = currentState.getStateName();
            this.currentState = newState;
            this.lastStateChange = LocalDateTime.now();
            
            String message = String.format("State changed from %s to %s", 
                previousStateName, newState.getStateName());
            stateHistory.add(lastStateChange + ": " + message);
        }
        
        private void updateStateChange(String message) {
            lastStateChange = LocalDateTime.now();
            stateHistory.add(lastStateChange + ": " + message);
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public DeviceState getCurrentState() { return currentState; }
        public String getStateName() { return currentState.getStateName(); }
        public String getErrorMessage() { return errorMessage; }
        public LocalDateTime getLastStateChange() { return lastStateChange; }
        public List<String> getStateHistory() { return new ArrayList<>(stateHistory); }
        public boolean isOperational() { return currentState.isOperational(); }
        public boolean allowsTraffic() { return currentState.allowsTraffic(); }
        public Set<String> getAllowedTransitions() { return currentState.getAllowedTransitions(); }
        
        // Package-private setters for states to use
        void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        void clearErrorMessage() { this.errorMessage = null; }
    }
    
    // Concrete State: Inactive
    public static class InactiveState implements DeviceState {
        @Override
        public StateTransitionResult activate(DeviceContext context) {
            // Perform health check before activation
            if (!performHealthCheck()) {
                return new StateTransitionResult(false, "Health check failed", 
                    new ErrorState("Health check failed during activation"), "HEALTH_CHECK_FAILED");
            }
            
            return new StateTransitionResult(true, "Activation started", new InitializingState());
        }
        
        @Override
        public StateTransitionResult deactivate(DeviceContext context) {
            return new StateTransitionResult(true, "Already inactive", null);
        }
        
        @Override
        public StateTransitionResult enterMaintenance(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Cannot enter maintenance from inactive state", null, "INVALID_TRANSITION");
        }
        
        @Override
        public StateTransitionResult exitMaintenance(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Device is not in maintenance mode", null, "NOT_IN_MAINTENANCE");
        }
        
        @Override
        public StateTransitionResult handleError(DeviceContext context, String error) {
            context.setErrorMessage(error);
            return new StateTransitionResult(true, "Error logged for inactive device: " + error, null);
        }
        
        @Override
        public String getStateName() { return "INACTIVE"; }
        
        @Override
        public Set<String> getAllowedTransitions() {
            return Set.of("activate");
        }
        
        @Override
        public boolean isOperational() { return false; }
        
        @Override
        public boolean allowsTraffic() { return false; }
        
        private boolean performHealthCheck() {
            return Math.random() > 0.2; // 80% success rate
        }
    }
    
    // Concrete State: Initializing
    public static class InitializingState implements DeviceState {
        @Override
        public StateTransitionResult activate(DeviceContext context) {
            return new StateTransitionResult(true, "Device is already activating", null);
        }
        
        @Override
        public StateTransitionResult deactivate(DeviceContext context) {
            return new StateTransitionResult(true, "Initialization cancelled", new InactiveState());
        }
        
        @Override
        public StateTransitionResult enterMaintenance(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Cannot enter maintenance during initialization", null, "INVALID_TRANSITION");
        }
        
        @Override
        public StateTransitionResult exitMaintenance(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Device is not in maintenance mode", null, "NOT_IN_MAINTENANCE");
        }
        
        @Override
        public StateTransitionResult handleError(DeviceContext context, String error) {
            context.setErrorMessage(error);
            return new StateTransitionResult(true, "Initialization failed: " + error, 
                new ErrorState("Initialization failed: " + error));
        }
        
        @Override
        public String getStateName() { return "INITIALIZING"; }
        
        @Override
        public Set<String> getAllowedTransitions() {
            return Set.of("deactivate", "handleError");
        }
        
        @Override
        public boolean isOperational() { return false; }
        
        @Override
        public boolean allowsTraffic() { return false; }
    }
    
    // Concrete State: Active
    public static class ActiveState implements DeviceState {
        @Override
        public StateTransitionResult activate(DeviceContext context) {
            return new StateTransitionResult(true, "Device is already active", null);
        }
        
        @Override
        public StateTransitionResult deactivate(DeviceContext context) {
            performGracefulShutdown();
            return new StateTransitionResult(true, "Device deactivated gracefully", new InactiveState());
        }
        
        @Override
        public StateTransitionResult enterMaintenance(DeviceContext context) {
            if (!isMaintenanceAllowed()) {
                return new StateTransitionResult(false, 
                    "Maintenance not allowed during business hours", null, "MAINTENANCE_BLOCKED");
            }
            
            redirectTraffic();
            return new StateTransitionResult(true, "Entered maintenance mode", new MaintenanceState());
        }
        
        @Override
        public StateTransitionResult exitMaintenance(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Device is not in maintenance mode", null, "NOT_IN_MAINTENANCE");
        }
        
        @Override
        public StateTransitionResult handleError(DeviceContext context, String error) {
            context.setErrorMessage(error);
            return new StateTransitionResult(true, "Error occurred: " + error, 
                new ErrorState(error));
        }
        
        @Override
        public String getStateName() { return "ACTIVE"; }
        
        @Override
        public Set<String> getAllowedTransitions() {
            return Set.of("deactivate", "enterMaintenance", "handleError");
        }
        
        @Override
        public boolean isOperational() { return true; }
        
        @Override
        public boolean allowsTraffic() { return true; }
        
        private void performGracefulShutdown() {
            // Simulate shutdown process
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private boolean isMaintenanceAllowed() {
            int hour = LocalDateTime.now().getHour();
            return hour < 9 || hour > 17;
        }
        
        private void redirectTraffic() {
            // Simulate traffic redirection
        }
    }
    
    // Concrete State: Maintenance
    public static class MaintenanceState implements DeviceState {
        @Override
        public StateTransitionResult activate(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Cannot activate while in maintenance. Exit maintenance first.", null, "IN_MAINTENANCE");
        }
        
        @Override
        public StateTransitionResult deactivate(DeviceContext context) {
            return new StateTransitionResult(true, "Deactivated from maintenance mode", new InactiveState());
        }
        
        @Override
        public StateTransitionResult enterMaintenance(DeviceContext context) {
            return new StateTransitionResult(true, "Already in maintenance mode", null);
        }
        
        @Override
        public StateTransitionResult exitMaintenance(DeviceContext context) {
            if (!performMaintenanceValidation()) {
                return new StateTransitionResult(false, 
                    "Device not ready to exit maintenance mode", null, "VALIDATION_FAILED");
            }
            
            restoreTraffic();
            context.clearErrorMessage();
            return new StateTransitionResult(true, "Exited maintenance mode", new ActiveState());
        }
        
        @Override
        public StateTransitionResult handleError(DeviceContext context, String error) {
            context.setErrorMessage(error);
            return new StateTransitionResult(true, "Error logged during maintenance: " + error, null);
        }
        
        @Override
        public String getStateName() { return "MAINTENANCE"; }
        
        @Override
        public Set<String> getAllowedTransitions() {
            return Set.of("deactivate", "exitMaintenance", "handleError");
        }
        
        @Override
        public boolean isOperational() { return false; }
        
        @Override
        public boolean allowsTraffic() { return false; }
        
        private boolean performMaintenanceValidation() {
            return Math.random() > 0.1; // 90% success rate
        }
        
        private void restoreTraffic() {
            // Simulate traffic restoration
        }
    }
    
    // Concrete State: Error
    public static class ErrorState implements DeviceState {
        private final String errorCause;
        
        public ErrorState(String errorCause) {
            this.errorCause = errorCause;
        }
        
        @Override
        public StateTransitionResult activate(DeviceContext context) {
            if (attemptErrorRecovery()) {
                context.clearErrorMessage();
                return new StateTransitionResult(true, "Recovered from error and activated", new ActiveState());
            } else {
                return new StateTransitionResult(false, 
                    "Cannot activate - error recovery failed", null, "RECOVERY_FAILED");
            }
        }
        
        @Override
        public StateTransitionResult deactivate(DeviceContext context) {
            context.clearErrorMessage();
            return new StateTransitionResult(true, "Force deactivated from error state", new InactiveState());
        }
        
        @Override
        public StateTransitionResult enterMaintenance(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Cannot enter maintenance from error state", null, "INVALID_TRANSITION");
        }
        
        @Override
        public StateTransitionResult exitMaintenance(DeviceContext context) {
            return new StateTransitionResult(false, 
                "Device is not in maintenance mode", null, "NOT_IN_MAINTENANCE");
        }
        
        @Override
        public StateTransitionResult handleError(DeviceContext context, String error) {
            context.setErrorMessage(error);
            return new StateTransitionResult(true, "Additional error logged: " + error, null);
        }
        
        @Override
        public String getStateName() { return "ERROR"; }
        
        @Override
        public Set<String> getAllowedTransitions() {
            return Set.of("activate", "deactivate", "handleError");
        }
        
        @Override
        public boolean isOperational() { return false; }
        
        @Override
        public boolean allowsTraffic() { return false; }
        
        private boolean attemptErrorRecovery() {
            return Math.random() > 0.4; // 60% success rate
        }
        
        public String getErrorCause() { return errorCause; }
    }
    
    // Exception for state operations
    public static class DeviceOperationException extends RuntimeException {
        private final String errorCode;
        
        public DeviceOperationException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() { return errorCode; }
    }
}