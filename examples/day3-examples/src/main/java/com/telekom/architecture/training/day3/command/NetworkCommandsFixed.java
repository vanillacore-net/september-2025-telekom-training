package com.telekom.architecture.training.day3.command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * FIXED IMPLEMENTATION using Command Pattern
 * 
 * Benefits:
 * - Commands are first-class objects with execute() and undo() methods
 * - Full undo/redo functionality with command history
 * - Easy to implement queuing, logging, and batch operations
 * - Loose coupling between invoker and receiver
 * - Support for macro commands and command composition
 * - Command validation before execution
 * - Easy to implement scheduling and delayed execution
 * 
 * Trainer Notes:
 * - Show how Command pattern encapsulates operations as objects
 * - Demonstrate undo/redo functionality with command stack
 * - Point out macro command composition
 * - Highlight separation of concerns between invoker and receiver
 * - Show how easy it is to add logging, queuing, validation
 */
public class NetworkCommandsFixed {
    
    // Reusing NetworkDevice from initial implementation (receiver)
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
            this.status = "ONLINE";
        }
        
        public String getDeviceId() { return deviceId; }
        public String getStatus() { return status; }
        public List<String> getConfiguration() { return new ArrayList<>(configuration); }
        public boolean isConnected() { return connected; }
        public List<String> getInterfaces() { return new ArrayList<>(interfaces); }
    }
    
    /**
     * Command interface - defines the contract for all commands
     */
    public interface NetworkCommand {
        void execute();
        void undo();
        String getDescription();
        boolean canUndo();
        LocalDateTime getTimestamp();
        
        /**
         * Validate command before execution
         */
        default boolean validate() {
            return true;
        }
    }
    
    /**
     * Abstract base command with common functionality
     */
    public static abstract class AbstractNetworkCommand implements NetworkCommand {
        protected final NetworkDevice device;
        protected final String description;
        protected final LocalDateTime timestamp;
        protected boolean executed = false;
        
        public AbstractNetworkCommand(NetworkDevice device, String description) {
            this.device = device;
            this.description = description;
            this.timestamp = LocalDateTime.now();
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        @Override
        public boolean canUndo() {
            return executed;
        }
    }
    
    /**
     * Connect Device Command
     */
    public static class ConnectDeviceCommand extends AbstractNetworkCommand {
        private boolean wasConnected;
        
        public ConnectDeviceCommand(NetworkDevice device) {
            super(device, "Connect device " + device.getDeviceId());
        }
        
        @Override
        public void execute() {
            wasConnected = device.isConnected();
            device.connect();
            executed = true;
        }
        
        @Override
        public void undo() {
            if (canUndo() && !wasConnected) {
                device.disconnect();
            }
        }
        
        @Override
        public boolean validate() {
            return device != null && !device.isConnected();
        }
    }
    
    /**
     * Disconnect Device Command
     */
    public static class DisconnectDeviceCommand extends AbstractNetworkCommand {
        private boolean wasConnected;
        
        public DisconnectDeviceCommand(NetworkDevice device) {
            super(device, "Disconnect device " + device.getDeviceId());
        }
        
        @Override
        public void execute() {
            wasConnected = device.isConnected();
            device.disconnect();
            executed = true;
        }
        
        @Override
        public void undo() {
            if (canUndo() && wasConnected) {
                device.connect();
            }
        }
        
        @Override
        public boolean validate() {
            return device != null && device.isConnected();
        }
    }
    
    /**
     * Add Configuration Command
     */
    public static class AddConfigurationCommand extends AbstractNetworkCommand {
        private final String config;
        
        public AddConfigurationCommand(NetworkDevice device, String config) {
            super(device, "Add configuration to " + device.getDeviceId() + ": " + config);
            this.config = config;
        }
        
        @Override
        public void execute() {
            device.addConfiguration(config);
            executed = true;
        }
        
        @Override
        public void undo() {
            if (canUndo()) {
                device.removeConfiguration(config);
            }
        }
        
        @Override
        public boolean validate() {
            return device != null && config != null && !config.trim().isEmpty() 
                && !device.getConfiguration().contains(config);
        }
    }
    
    /**
     * Remove Configuration Command
     */
    public static class RemoveConfigurationCommand extends AbstractNetworkCommand {
        private final String config;
        
        public RemoveConfigurationCommand(NetworkDevice device, String config) {
            super(device, "Remove configuration from " + device.getDeviceId() + ": " + config);
            this.config = config;
        }
        
        @Override
        public void execute() {
            device.removeConfiguration(config);
            executed = true;
        }
        
        @Override
        public void undo() {
            if (canUndo()) {
                device.addConfiguration(config);
            }
        }
        
        @Override
        public boolean validate() {
            return device != null && config != null && device.getConfiguration().contains(config);
        }
    }
    
    /**
     * Enable Interface Command
     */
    public static class EnableInterfaceCommand extends AbstractNetworkCommand {
        private final String interfaceName;
        private boolean wasEnabled;
        
        public EnableInterfaceCommand(NetworkDevice device, String interfaceName) {
            super(device, "Enable interface " + interfaceName + " on " + device.getDeviceId());
            this.interfaceName = interfaceName;
        }
        
        @Override
        public void execute() {
            wasEnabled = device.getInterfaces().contains(interfaceName);
            device.enableInterface(interfaceName);
            executed = true;
        }
        
        @Override
        public void undo() {
            if (canUndo() && !wasEnabled) {
                device.disableInterface(interfaceName);
            }
        }
        
        @Override
        public boolean validate() {
            return device != null && interfaceName != null && 
                !device.getInterfaces().contains(interfaceName);
        }
    }
    
    /**
     * Reboot Device Command (non-undoable)
     */
    public static class RebootDeviceCommand extends AbstractNetworkCommand {
        
        public RebootDeviceCommand(NetworkDevice device) {
            super(device, "Reboot device " + device.getDeviceId());
        }
        
        @Override
        public void execute() {
            device.reboot();
            executed = true;
        }
        
        @Override
        public void undo() {
            // Reboot cannot be undone
            System.out.println("Cannot undo reboot operation");
        }
        
        @Override
        public boolean canUndo() {
            return false; // Reboot is not undoable
        }
    }
    
    /**
     * Macro Command - combines multiple commands
     */
    public static class MacroCommand extends AbstractNetworkCommand {
        private final List<NetworkCommand> commands;
        private final List<NetworkCommand> executedCommands;
        
        public MacroCommand(String description, List<NetworkCommand> commands) {
            super(null, description);
            this.commands = new ArrayList<>(commands);
            this.executedCommands = new ArrayList<>();
        }
        
        @Override
        public void execute() {
            for (NetworkCommand command : commands) {
                if (command.validate()) {
                    command.execute();
                    executedCommands.add(command);
                } else {
                    System.out.println("Validation failed for command: " + command.getDescription());
                    // Rollback already executed commands
                    undo();
                    return;
                }
            }
            executed = true;
        }
        
        @Override
        public void undo() {
            // Undo in reverse order
            for (int i = executedCommands.size() - 1; i >= 0; i--) {
                NetworkCommand command = executedCommands.get(i);
                if (command.canUndo()) {
                    command.undo();
                }
            }
            executedCommands.clear();
        }
        
        @Override
        public boolean validate() {
            return commands.stream().allMatch(NetworkCommand::validate);
        }
        
        @Override
        public boolean canUndo() {
            return executed && executedCommands.stream().anyMatch(NetworkCommand::canUndo);
        }
    }
    
    /**
     * Command Invoker with undo/redo functionality
     */
    public static class NetworkCommandInvoker {
        private final Stack<NetworkCommand> executedCommands = new Stack<>();
        private final Stack<NetworkCommand> undoneCommands = new Stack<>();
        
        public boolean executeCommand(NetworkCommand command) {
            if (!command.validate()) {
                System.out.println("Command validation failed: " + command.getDescription());
                return false;
            }
            
            command.execute();
            executedCommands.push(command);
            undoneCommands.clear(); // Clear redo stack when new command executed
            
            return true;
        }
        
        public boolean undo() {
            if (executedCommands.isEmpty()) {
                System.out.println("No commands to undo");
                return false;
            }
            
            NetworkCommand command = executedCommands.pop();
            if (command.canUndo()) {
                command.undo();
                undoneCommands.push(command);
                System.out.println("Undone: " + command.getDescription());
                return true;
            } else {
                System.out.println("Cannot undo: " + command.getDescription());
                executedCommands.push(command); // Put it back
                return false;
            }
        }
        
        public boolean redo() {
            if (undoneCommands.isEmpty()) {
                System.out.println("No commands to redo");
                return false;
            }
            
            NetworkCommand command = undoneCommands.pop();
            command.execute();
            executedCommands.push(command);
            System.out.println("Redone: " + command.getDescription());
            return true;
        }
        
        public List<NetworkCommand> getCommandHistory() {
            return new ArrayList<>(executedCommands);
        }
        
        public boolean canUndo() {
            return !executedCommands.isEmpty();
        }
        
        public boolean canRedo() {
            return !undoneCommands.isEmpty();
        }
        
        public void clearHistory() {
            executedCommands.clear();
            undoneCommands.clear();
        }
    }
    
    /**
     * Network Manager using Command Pattern
     */
    public static class NetworkManager {
        private final List<NetworkDevice> devices;
        private final NetworkCommandInvoker invoker;
        
        public NetworkManager() {
            this.devices = new ArrayList<>();
            this.invoker = new NetworkCommandInvoker();
        }
        
        public void addDevice(NetworkDevice device) {
            devices.add(device);
        }
        
        public boolean connectDevice(String deviceId) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                return invoker.executeCommand(new ConnectDeviceCommand(device));
            }
            return false;
        }
        
        public boolean disconnectDevice(String deviceId) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                return invoker.executeCommand(new DisconnectDeviceCommand(device));
            }
            return false;
        }
        
        public boolean configureDevice(String deviceId, String config) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                return invoker.executeCommand(new AddConfigurationCommand(device, config));
            }
            return false;
        }
        
        public boolean enableInterface(String deviceId, String interfaceName) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                return invoker.executeCommand(new EnableInterfaceCommand(device, interfaceName));
            }
            return false;
        }
        
        public boolean rebootDevice(String deviceId) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                return invoker.executeCommand(new RebootDeviceCommand(device));
            }
            return false;
        }
        
        /**
         * FIXED: Undo/redo now works perfectly
         */
        public boolean undoLastOperation() {
            return invoker.undo();
        }
        
        public boolean redoLastOperation() {
            return invoker.redo();
        }
        
        /**
         * FIXED: Macro commands allow complex operations
         */
        public boolean performMaintenanceSequence(String deviceId) {
            NetworkDevice device = findDevice(deviceId);
            if (device != null) {
                List<NetworkCommand> maintenanceCommands = List.of(
                    new DisconnectDeviceCommand(device),
                    new AddConfigurationCommand(device, "maintenance_mode=true"),
                    new RebootDeviceCommand(device),
                    new ConnectDeviceCommand(device),
                    new RemoveConfigurationCommand(device, "maintenance_mode=true")
                );
                
                MacroCommand maintenanceSequence = new MacroCommand(
                    "Maintenance sequence for " + deviceId, maintenanceCommands);
                
                return invoker.executeCommand(maintenanceSequence);
            }
            return false;
        }
        
        public List<NetworkCommand> getCommandHistory() {
            return invoker.getCommandHistory();
        }
        
        public boolean canUndo() { return invoker.canUndo(); }
        public boolean canRedo() { return invoker.canRedo(); }
        
        private NetworkDevice findDevice(String deviceId) {
            return devices.stream()
                .filter(d -> d.getDeviceId().equals(deviceId))
                .findFirst()
                .orElse(null);
        }
        
        public List<NetworkDevice> getDevices() { return new ArrayList<>(devices); }
    }
    
    /**
     * FIXED: Network Console with command pattern benefits
     */
    public static class NetworkConsole {
        private final NetworkManager networkManager;
        
        public NetworkConsole(NetworkManager manager) {
            this.networkManager = manager;
        }
        
        public void executeUserCommand(String command, String deviceId, String parameter) {
            boolean success = false;
            
            switch (command.toLowerCase()) {
                case "connect":
                    success = networkManager.connectDevice(deviceId);
                    break;
                case "disconnect":
                    success = networkManager.disconnectDevice(deviceId);
                    break;
                case "configure":
                    success = networkManager.configureDevice(deviceId, parameter);
                    break;
                case "enable":
                    success = networkManager.enableInterface(deviceId, parameter);
                    break;
                case "reboot":
                    success = networkManager.rebootDevice(deviceId);
                    break;
                case "maintenance":
                    success = networkManager.performMaintenanceSequence(deviceId);
                    break;
                case "undo":
                    success = networkManager.undoLastOperation();
                    break;
                case "redo":
                    success = networkManager.redoLastOperation();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    return;
            }
            
            System.out.println("Command " + command + " " + (success ? "succeeded" : "failed"));
        }
        
        /**
         * FIXED: Command history is now available
         */
        public void showCommandHistory() {
            List<NetworkCommand> history = networkManager.getCommandHistory();
            System.out.println("Command History:");
            for (int i = 0; i < history.size(); i++) {
                NetworkCommand cmd = history.get(i);
                System.out.printf("%d. [%s] %s%n", i + 1, cmd.getTimestamp(), cmd.getDescription());
            }
        }
        
        public void showUndoRedoStatus() {
            System.out.println("Can undo: " + networkManager.canUndo());
            System.out.println("Can redo: " + networkManager.canRedo());
        }
    }
}