package com.telekom.architecture.training.day3.command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * INITIAL (PROBLEMATIC) IMPLEMENTATION
 * 
 * Problems:
 * - Direct execution without command history
 * - No undo/redo functionality
 * - Difficult to implement queuing, logging, or batch operations
 * - Tight coupling between invoker and receiver
 * - Cannot implement macro commands or command composition
 * - No way to validate commands before execution
 * 
 * Trainer Notes:
 * - Show how direct method calls limit flexibility
 * - Demonstrate missing command history and undo capabilities
 * - Point out difficulty in implementing queuing or scheduling
 * - Highlight tight coupling between UI/controller and network operations
 */
public class NetworkCommandsInitial {
    
    public static class NetworkDevice {
        private final String deviceId;
        private String status;
        private final List<String> configuration;
        private boolean connected;
        private final List<String> interfaces;
        
        public NetworkDevice(String deviceId) {
            this.deviceId = deviceId;
            this.status = "ONLINE";
            this.configuration = new ArrayList<>();
            this.connected = true;
            this.interfaces = new ArrayList<>();
        }
        
        // Direct operations - no command pattern
        public void connect() {
            this.connected = true;
            this.status = "ONLINE";
            System.out.println("Device " + deviceId + " connected");
        }
        
        public void disconnect() {
            this.connected = false;
            this.status = "OFFLINE";
            System.out.println("Device " + deviceId + " disconnected");
        }
        
        public void addConfiguration(String config) {
            configuration.add(config);
            System.out.println("Configuration added to " + deviceId + ": " + config);
        }
        
        public void removeConfiguration(String config) {
            configuration.remove(config);
            System.out.println("Configuration removed from " + deviceId + ": " + config);
        }
        
        public void enableInterface(String interfaceName) {
            if (!interfaces.contains(interfaceName)) {
                interfaces.add(interfaceName);
                System.out.println("Interface " + interfaceName + " enabled on " + deviceId);
            }
        }
        
        public void disableInterface(String interfaceName) {
            interfaces.remove(interfaceName);
            System.out.println("Interface " + interfaceName + " disabled on " + deviceId);
        }
        
        public void reboot() {
            this.status = "REBOOTING";
            System.out.println("Device " + deviceId + " rebooting...");
            // Simulate reboot
            this.status = "ONLINE";
        }
        
        // Getters
        public String getDeviceId() { return deviceId; }
        public String getStatus() { return status; }
        public List<String> getConfiguration() { return new ArrayList<>(configuration); }
        public boolean isConnected() { return connected; }
        public List<String> getInterfaces() { return new ArrayList<>(interfaces); }
    }
    
    /**
     * PROBLEMATIC: Network manager with direct method calls
     */
    public static class NetworkManager {
        private final List<NetworkDevice> devices;
        private final List<String> operationLog; // Simple logging, but no command objects
        
        public NetworkManager() {
            this.devices = new ArrayList<>();
            this.operationLog = new ArrayList<>();
        }
        
        public void addDevice(NetworkDevice device) {
            devices.add(device);
        }
        
        /**
         * PROBLEM: Direct method calls - no undo capability
         */
        public void connectDevice(String deviceId) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                device.connect();
                operationLog.add(LocalDateTime.now() + ": Connected " + deviceId);
            }
            // No way to undo this operation!
        }
        
        public void disconnectDevice(String deviceId) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                device.disconnect();
                operationLog.add(LocalDateTime.now() + ": Disconnected " + deviceId);
            }
            // No way to undo this operation!
        }
        
        public void configureDevice(String deviceId, String config) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                device.addConfiguration(config);
                operationLog.add(LocalDateTime.now() + ": Configured " + deviceId + " with " + config);
            }
            // No way to undo this operation!
        }
        
        public void enableInterface(String deviceId, String interfaceName) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                device.enableInterface(interfaceName);
                operationLog.add(LocalDateTime.now() + ": Enabled interface " + interfaceName + " on " + deviceId);
            }
            // No way to undo this operation!
        }
        
        /**
         * PROBLEM: No command history means no undo functionality
         * If we wanted undo, we'd need to manually track state changes
         */
        public void undoLastOperation() {
            // This is impossible to implement properly without command objects!
            System.out.println("Cannot undo - no command history available!");
            
            // We'd need to manually track what the last operation was
            // and how to reverse it - very complex and error-prone
        }
        
        /**
         * PROBLEM: Cannot implement redo without command objects
         */
        public void redoLastOperation() {
            // This is also impossible without command objects
            System.out.println("Cannot redo - no command history available!");
        }
        
        /**
         * PROBLEM: Batch operations require manually calling each method
         */
        public void performMaintenanceSequence(String deviceId) {
            // Manual sequence - difficult to modify or extend
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                System.out.println("Starting maintenance on " + deviceId);
                
                // Hard-coded sequence
                device.disconnect();
                device.addConfiguration("maintenance_mode=true");
                device.reboot();
                device.connect();
                device.removeConfiguration("maintenance_mode=true");
                
                operationLog.add(LocalDateTime.now() + ": Completed maintenance on " + deviceId);
            }
            
            // What if we want to undo this entire sequence?
            // What if one step fails and we need to rollback?
            // Very difficult without command objects!
        }
        
        /**
         * PROBLEM: Cannot queue commands for later execution
         */
        public void scheduleOperation(String deviceId, String operation, LocalDateTime when) {
            // We can't easily implement scheduling without command objects
            System.out.println("Scheduling not supported - operations execute immediately");
            
            // We'd have to create complex scheduling logic for each operation type
            // This violates DRY and makes the code very hard to maintain
        }
        
        private NetworkDevice findDevice(String deviceId) {
            return devices.stream()
                .filter(d -> d.getDeviceId().equals(deviceId))
                .findFirst()
                .orElse(null);
        }
        
        public List<NetworkDevice> getDevices() { return new ArrayList<>(devices); }
        public List<String> getOperationLog() { return new ArrayList<>(operationLog); }
    }
    
    /**
     * PROBLEM: User interface tightly coupled to network operations
     */
    public static class NetworkConsole {
        private final NetworkManager networkManager;
        
        public NetworkConsole(NetworkManager manager) {
            this.networkManager = manager;
        }
        
        /**
         * PROBLEM: Console directly calls manager methods
         * No abstraction, no command queuing, no undo
         */
        public void executeUserCommand(String command, String deviceId, String parameter) {
            switch (command.toLowerCase()) {
                case "connect":
                    networkManager.connectDevice(deviceId);
                    break;
                case "disconnect":
                    networkManager.disconnectDevice(deviceId);
                    break;
                case "configure":
                    networkManager.configureDevice(deviceId, parameter);
                    break;
                case "enable":
                    networkManager.enableInterface(deviceId, parameter);
                    break;
                case "undo":
                    networkManager.undoLastOperation(); // Won't work!
                    break;
                case "redo":
                    networkManager.redoLastOperation(); // Won't work!
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        }
        
        /**
         * PROBLEM: Cannot implement command history or replay
         */
        public void showCommandHistory() {
            System.out.println("Command history not available - only operation log");
            // We only have a simple log, not executable command objects
        }
        
        /**
         * PROBLEM: Cannot validate commands before execution
         */
        public boolean validateCommand(String command, String deviceId, String parameter) {
            // Without command objects, validation logic must be duplicated
            // in multiple places
            return true; // Simplified - real validation would be complex
        }
    }
}