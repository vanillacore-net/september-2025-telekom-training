package com.telekom.architecture.training.day3.observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * INITIAL (PROBLEMATIC) IMPLEMENTATION
 * 
 * Problems:
 * - Tight coupling between device and all systems that need notifications
 * - Device class knows about all dependent systems (monitoring, logging, alerting, etc.)
 * - Difficult to add new systems without modifying device code
 * - Violation of Single Responsibility Principle
 * - Hard to test individual components in isolation
 * 
 * Trainer Notes:
 * - Point out how Device class has too many responsibilities
 * - Show how adding new notification systems requires changing Device class
 * - Demonstrate tight coupling makes testing difficult
 * - Highlight violation of Open/Closed Principle
 */
public class DeviceEventsInitial {
    
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
        
        // Getters
        public String getDeviceId() { return deviceId; }
        public EventType getType() { return type; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    /**
     * Monitoring System - Device needs to know about this directly
     */
    public static class MonitoringSystem {
        private final List<DeviceEvent> events = new ArrayList<>();
        
        public void handleDeviceEvent(DeviceEvent event) {
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
     * Logging System - Device needs to know about this directly
     */
    public static class LoggingSystem {
        private final List<String> logs = new ArrayList<>();
        
        public void handleDeviceEvent(DeviceEvent event) {
            String logEntry = String.format("[%s] %s - %s: %s", 
                event.getTimestamp(), event.getDeviceId(), event.getType(), event.getMessage());
            logs.add(logEntry);
            System.out.println("[LOG] " + logEntry);
        }
        
        public List<String> getLogs() { return new ArrayList<>(logs); }
    }
    
    /**
     * Alerting System - Device needs to know about this directly
     */
    public static class AlertingSystem {
        private final List<DeviceEvent> alerts = new ArrayList<>();
        
        public void handleDeviceEvent(DeviceEvent event) {
            // Only handle critical events
            if (event.getType() == EventType.CONNECTION_LOST || 
                event.getType() == EventType.THRESHOLD_EXCEEDED) {
                
                alerts.add(event);
                System.out.println("[ALERT] CRITICAL: Device " + event.getDeviceId() + " - " + event.getMessage());
                
                // Send notifications (email, SMS, etc.)
                sendNotification(event);
            }
        }
        
        private void sendNotification(DeviceEvent event) {
            // Send alert notification
        }
        
        public List<DeviceEvent> getAlerts() { return new ArrayList<>(alerts); }
    }
    
    /**
     * PROBLEMATIC: Device class is tightly coupled to all systems
     */
    public static class NetworkDevice {
        private final String deviceId;
        private String status;
        private int cpuUsage;
        private boolean connected;
        
        // TIGHT COUPLING: Device knows about all these systems directly
        private final MonitoringSystem monitoringSystem;
        private final LoggingSystem loggingSystem;
        private final AlertingSystem alertingSystem;
        
        public NetworkDevice(String deviceId, MonitoringSystem monitoring, 
                           LoggingSystem logging, AlertingSystem alerting) {
            this.deviceId = deviceId;
            this.status = "ONLINE";
            this.cpuUsage = 0;
            this.connected = true;
            
            // Device must know about all systems it needs to notify
            this.monitoringSystem = monitoring;
            this.loggingSystem = logging;
            this.alertingSystem = alerting;
        }
        
        public void updateStatus(String newStatus) {
            String oldStatus = this.status;
            this.status = newStatus;
            
            DeviceEvent event = new DeviceEvent(deviceId, EventType.STATUS_CHANGE, 
                "Status changed from " + oldStatus + " to " + newStatus, newStatus);
            
            // PROBLEM: Device must notify all systems directly
            monitoringSystem.handleDeviceEvent(event);
            loggingSystem.handleDeviceEvent(event);
            alertingSystem.handleDeviceEvent(event);
        }
        
        public void updateCpuUsage(int usage) {
            this.cpuUsage = usage;
            
            if (usage > 80) { // Threshold exceeded
                DeviceEvent event = new DeviceEvent(deviceId, EventType.THRESHOLD_EXCEEDED, 
                    "CPU usage exceeded threshold: " + usage + "%", usage);
                
                // PROBLEM: Again, device must notify all systems
                monitoringSystem.handleDeviceEvent(event);
                loggingSystem.handleDeviceEvent(event);
                alertingSystem.handleDeviceEvent(event);
            }
        }
        
        public void setConnectionStatus(boolean connected) {
            boolean oldConnected = this.connected;
            this.connected = connected;
            
            if (!connected && oldConnected) {
                DeviceEvent event = new DeviceEvent(deviceId, EventType.CONNECTION_LOST, 
                    "Connection lost", connected);
                
                // PROBLEM: Same notification pattern repeated
                monitoringSystem.handleDeviceEvent(event);
                loggingSystem.handleDeviceEvent(event);
                alertingSystem.handleDeviceEvent(event);
            }
        }
        
        public void updateConfiguration(String configKey, String configValue) {
            DeviceEvent event = new DeviceEvent(deviceId, EventType.CONFIGURATION_CHANGED, 
                "Configuration changed: " + configKey + " = " + configValue, 
                configKey + "=" + configValue);
            
            // PROBLEM: What if we want to add a new system that needs to know about config changes?
            // We have to modify this method!
            monitoringSystem.handleDeviceEvent(event);
            loggingSystem.handleDeviceEvent(event);
            // Note: Alerting system might not care about all config changes
        }
        
        // Getters
        public String getDeviceId() { return deviceId; }
        public String getStatus() { return status; }
        public int getCpuUsage() { return cpuUsage; }
        public boolean isConnected() { return connected; }
    }
    
    /**
     * What if we want to add a new system? We need to modify NetworkDevice!
     */
    public static class InventorySystem {
        private final List<DeviceEvent> inventoryEvents = new ArrayList<>();
        
        public void handleDeviceEvent(DeviceEvent event) {
            // Only track certain events for inventory
            if (event.getType() == EventType.STATUS_CHANGE || 
                event.getType() == EventType.CONFIGURATION_CHANGED) {
                
                inventoryEvents.add(event);
                System.out.println("[INVENTORY] Updated: " + event.getDeviceId());
                updateInventoryDatabase(event);
            }
        }
        
        private void updateInventoryDatabase(DeviceEvent event) {
            // Update inventory database
        }
        
        public List<DeviceEvent> getInventoryEvents() { return new ArrayList<>(inventoryEvents); }
    }
    
    /**
     * To add InventorySystem, we would need to:
     * 1. Add InventorySystem parameter to NetworkDevice constructor
     * 2. Add instance variable to NetworkDevice
     * 3. Modify every event notification method to include inventory
     * 
     * This violates Open/Closed Principle!
     */
}