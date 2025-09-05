package com.telekom.architecture.training.day3.observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FIXED IMPLEMENTATION using Observer Pattern
 * 
 * Benefits:
 * - Loose coupling between device and notification systems
 * - Device doesn't need to know about specific observer implementations
 * - Easy to add new observers without modifying device code
 * - Follows Open/Closed Principle
 * - Individual components can be tested in isolation
 * - Observers can be added/removed at runtime
 * 
 * Trainer Notes:
 * - Show how Observer pattern eliminates tight coupling
 * - Demonstrate runtime observer registration/removal
 * - Point out how device focuses on its core responsibility
 * - Discuss push vs pull observer models
 * - Show how easy it is to add new observers
 */
public class DeviceEventsFixed {
    
    // Reusing EventType and DeviceEvent from initial implementation
    public enum EventType {
        STATUS_CHANGE, THRESHOLD_EXCEEDED, CONNECTION_LOST, CONFIGURATION_CHANGED
    }
    
    public static class DeviceEvent {
        private final String deviceId;
        private final EventType type;
        private final String message;
        private final Object data;
        private final LocalDateTime timestamp;
        
        public DeviceEvent(String deviceId, EventType type, String message, Object data) {
            this.deviceId = deviceId;
            this.type = type;
            this.message = message;
            this.data = data;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getDeviceId() { return deviceId; }
        public EventType getType() { return type; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    /**
     * Observer interface - defines the contract for event listeners
     */
    public interface DeviceEventObserver {
        void onDeviceEvent(DeviceEvent event);
        
        /**
         * Optional method to filter events the observer is interested in
         * Default implementation accepts all events
         */
        default boolean isInterestedIn(EventType eventType) {
            return true;
        }
        
        /**
         * Observer name for identification
         */
        default String getObserverName() {
            return this.getClass().getSimpleName();
        }
    }
    
    /**
     * Subject interface - defines the contract for observable objects
     */
    public interface DeviceEventSubject {
        void addObserver(DeviceEventObserver observer);
        void removeObserver(DeviceEventObserver observer);
        void notifyObservers(DeviceEvent event);
    }
    
    /**
     * Monitoring System Observer - now decoupled from device
     */
    public static class MonitoringSystemObserver implements DeviceEventObserver {
        private final List<DeviceEvent> events = new ArrayList<>();
        
        @Override
        public void onDeviceEvent(DeviceEvent event) {
            events.add(event);
            System.out.println("[MONITORING] Device " + event.getDeviceId() + ": " + event.getMessage());
            
            // Monitoring-specific logic
            if (event.getType() == EventType.THRESHOLD_EXCEEDED) {
                updateMetricsDashboard(event);
            }
        }
        
        private void updateMetricsDashboard(DeviceEvent event) {
            // Update monitoring dashboard
        }
        
        public List<DeviceEvent> getEvents() { return new ArrayList<>(events); }
    }
    
    /**
     * Logging System Observer - focuses only on logging
     */
    public static class LoggingSystemObserver implements DeviceEventObserver {
        private final List<String> logs = new ArrayList<>();
        
        @Override
        public void onDeviceEvent(DeviceEvent event) {
            String logEntry = String.format("[%s] %s - %s: %s", 
                event.getTimestamp(), event.getDeviceId(), event.getType(), event.getMessage());
            logs.add(logEntry);
            System.out.println("[LOG] " + logEntry);
        }
        
        public List<String> getLogs() { return new ArrayList<>(logs); }
    }
    
    /**
     * Alerting System Observer - only interested in critical events
     */
    public static class AlertingSystemObserver implements DeviceEventObserver {
        private final List<DeviceEvent> alerts = new ArrayList<>();
        
        @Override
        public boolean isInterestedIn(EventType eventType) {
            // Only handle critical events
            return eventType == EventType.CONNECTION_LOST || 
                   eventType == EventType.THRESHOLD_EXCEEDED;
        }
        
        @Override
        public void onDeviceEvent(DeviceEvent event) {
            alerts.add(event);
            System.out.println("[ALERT] CRITICAL: Device " + event.getDeviceId() + " - " + event.getMessage());
            sendNotification(event);
        }
        
        private void sendNotification(DeviceEvent event) {
            // Send alert notification
        }
        
        public List<DeviceEvent> getAlerts() { return new ArrayList<>(alerts); }
    }
    
    /**
     * Inventory System Observer - can be added without modifying device!
     */
    public static class InventorySystemObserver implements DeviceEventObserver {
        private final List<DeviceEvent> inventoryEvents = new ArrayList<>();
        
        @Override
        public boolean isInterestedIn(EventType eventType) {
            // Only track certain events for inventory
            return eventType == EventType.STATUS_CHANGE || 
                   eventType == EventType.CONFIGURATION_CHANGED;
        }
        
        @Override
        public void onDeviceEvent(DeviceEvent event) {
            inventoryEvents.add(event);
            System.out.println("[INVENTORY] Updated: " + event.getDeviceId());
            updateInventoryDatabase(event);
        }
        
        private void updateInventoryDatabase(DeviceEvent event) {
            // Update inventory database
        }
        
        public List<DeviceEvent> getInventoryEvents() { return new ArrayList<>(inventoryEvents); }
    }
    
    /**
     * FIXED: Device class now focuses on device logic only
     */
    public static class NetworkDevice implements DeviceEventSubject {
        private final String deviceId;
        private String status;
        private int cpuUsage;
        private boolean connected;
        
        // Thread-safe list for observers
        private final List<DeviceEventObserver> observers = new CopyOnWriteArrayList<>();
        
        public NetworkDevice(String deviceId) {
            this.deviceId = deviceId;
            this.status = "ONLINE";
            this.cpuUsage = 0;
            this.connected = true;
        }
        
        @Override
        public void addObserver(DeviceEventObserver observer) {
            observers.add(observer);
        }
        
        @Override
        public void removeObserver(DeviceEventObserver observer) {
            observers.remove(observer);
        }
        
        @Override
        public void notifyObservers(DeviceEvent event) {
            // Only notify observers that are interested in this event type
            for (DeviceEventObserver observer : observers) {
                if (observer.isInterestedIn(event.getType())) {
                    observer.onDeviceEvent(event);
                }
            }
        }
        
        public void updateStatus(String newStatus) {
            String oldStatus = this.status;
            this.status = newStatus;
            
            DeviceEvent event = new DeviceEvent(deviceId, EventType.STATUS_CHANGE, 
                "Status changed from " + oldStatus + " to " + newStatus, newStatus);
            
            // Simply notify observers - no need to know who they are
            notifyObservers(event);
        }
        
        public void updateCpuUsage(int usage) {
            this.cpuUsage = usage;
            
            if (usage > 80) { // Threshold exceeded
                DeviceEvent event = new DeviceEvent(deviceId, EventType.THRESHOLD_EXCEEDED, 
                    "CPU usage exceeded threshold: " + usage + "%", usage);
                
                // Same simple notification pattern
                notifyObservers(event);
            }
        }
        
        public void setConnectionStatus(boolean connected) {
            boolean oldConnected = this.connected;
            this.connected = connected;
            
            if (!connected && oldConnected) {
                DeviceEvent event = new DeviceEvent(deviceId, EventType.CONNECTION_LOST, 
                    "Connection lost", connected);
                
                notifyObservers(event);
            }
        }
        
        public void updateConfiguration(String configKey, String configValue) {
            DeviceEvent event = new DeviceEvent(deviceId, EventType.CONFIGURATION_CHANGED, 
                "Configuration changed: " + configKey + " = " + configValue, 
                configKey + "=" + configValue);
            
            notifyObservers(event);
        }
        
        public List<DeviceEventObserver> getObservers() {
            return new ArrayList<>(observers);
        }
        
        // Getters
        public String getDeviceId() { return deviceId; }
        public String getStatus() { return status; }
        public int getCpuUsage() { return cpuUsage; }
        public boolean isConnected() { return connected; }
    }
    
    /**
     * Example of how easy it is to add new observers
     */
    public static class SecurityAuditObserver implements DeviceEventObserver {
        private final List<DeviceEvent> securityEvents = new ArrayList<>();
        
        @Override
        public boolean isInterestedIn(EventType eventType) {
            // Security audit is interested in all events
            return true;
        }
        
        @Override
        public void onDeviceEvent(DeviceEvent event) {
            securityEvents.add(event);
            System.out.println("[SECURITY] Audit log: " + event.getDeviceId() + 
                " - " + event.getType() + " at " + event.getTimestamp());
            
            // Perform security analysis
            if (event.getType() == EventType.CONFIGURATION_CHANGED) {
                analyzeConfigurationChange(event);
            }
        }
        
        private void analyzeConfigurationChange(DeviceEvent event) {
            // Analyze configuration changes for security implications
        }
        
        public List<DeviceEvent> getSecurityEvents() { return new ArrayList<>(securityEvents); }
    }
    
    /**
     * Performance Metrics Observer - another example of easy extension
     */
    public static class PerformanceMetricsObserver implements DeviceEventObserver {
        private final List<DeviceEvent> performanceEvents = new ArrayList<>();
        private int eventCount = 0;
        
        @Override
        public boolean isInterestedIn(EventType eventType) {
            // Performance metrics interested in status and threshold events
            return eventType == EventType.STATUS_CHANGE || 
                   eventType == EventType.THRESHOLD_EXCEEDED;
        }
        
        @Override
        public void onDeviceEvent(DeviceEvent event) {
            performanceEvents.add(event);
            eventCount++;
            
            System.out.println("[PERFORMANCE] Event #" + eventCount + " from " + event.getDeviceId());
            
            // Calculate performance metrics
            calculateMetrics();
        }
        
        private void calculateMetrics() {
            // Calculate performance metrics based on events
        }
        
        public List<DeviceEvent> getPerformanceEvents() { return new ArrayList<>(performanceEvents); }
        public int getEventCount() { return eventCount; }
    }
    
    /**
     * Device Manager - shows how to work with multiple devices and observers
     */
    public static class DeviceManager {
        private final List<NetworkDevice> devices = new ArrayList<>();
        private final List<DeviceEventObserver> globalObservers = new ArrayList<>();
        
        public void addDevice(NetworkDevice device) {
            devices.add(device);
            
            // Add all global observers to the new device
            for (DeviceEventObserver observer : globalObservers) {
                device.addObserver(observer);
            }
        }
        
        public void addGlobalObserver(DeviceEventObserver observer) {
            globalObservers.add(observer);
            
            // Add observer to all existing devices
            for (NetworkDevice device : devices) {
                device.addObserver(observer);
            }
        }
        
        public void removeGlobalObserver(DeviceEventObserver observer) {
            globalObservers.remove(observer);
            
            // Remove observer from all devices
            for (NetworkDevice device : devices) {
                device.removeObserver(observer);
            }
        }
        
        public List<NetworkDevice> getDevices() { return new ArrayList<>(devices); }
        public List<DeviceEventObserver> getGlobalObservers() { return new ArrayList<>(globalObservers); }
    }
}