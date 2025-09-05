package com.telekom.architecture.training.day3.strategy;

import java.util.Map;
import java.util.HashMap;

/**
 * FIXED IMPLEMENTATION using Strategy Pattern
 * 
 * Benefits:
 * - Each monitoring algorithm is encapsulated in its own strategy
 * - Easy to add new device types without modifying existing code
 * - Follows Open/Closed Principle
 * - Individual strategies are easily testable
 * - Runtime algorithm selection possible
 * 
 * Trainer Notes:
 * - Show how Strategy pattern eliminates conditional logic
 * - Demonstrate easy extension with new device types
 * - Point out improved testability and separation of concerns
 * - Discuss when to use Strategy vs State pattern
 */
public class DeviceMonitoringFixed {
    
    // Reusing Device and MonitoringResult from Initial implementation
    public static class Device {
        private final String id;
        private final String type;
        private final Map<String, Object> metrics;
        
        public Device(String id, String type) {
            this.id = id;
            this.type = type;
            this.metrics = new HashMap<>();
        }
        
        public String getId() { return id; }
        public String getType() { return type; }
        public Map<String, Object> getMetrics() { return metrics; }
        
        public void updateMetric(String key, Object value) {
            metrics.put(key, value);
        }
    }
    
    public static class MonitoringResult {
        private final String deviceId;
        private final String status;
        private final String message;
        private final int severity;
        
        public MonitoringResult(String deviceId, String status, String message, int severity) {
            this.deviceId = deviceId;
            this.status = status;
            this.message = message;
            this.severity = severity;
        }
        
        public String getDeviceId() { return deviceId; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public int getSeverity() { return severity; }
    }
    
    /**
     * Strategy interface - defines the monitoring algorithm contract
     */
    public interface MonitoringStrategy {
        MonitoringResult monitor(Device device);
        boolean canHandle(String deviceType);
    }
    
    /**
     * Router monitoring strategy
     */
    public static class RouterMonitoringStrategy implements MonitoringStrategy {
        @Override
        public MonitoringResult monitor(Device device) {
            Integer cpuUsage = (Integer) device.getMetrics().get("cpu_usage");
            Integer memoryUsage = (Integer) device.getMetrics().get("memory_usage");
            Integer packetLoss = (Integer) device.getMetrics().get("packet_loss");
            
            if (cpuUsage != null && cpuUsage > 90) {
                return new MonitoringResult(device.getId(), "CRITICAL", 
                    "Router CPU usage critical: " + cpuUsage + "%", 3);
            } else if (memoryUsage != null && memoryUsage > 85) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "Router memory usage high: " + memoryUsage + "%", 2);
            } else if (packetLoss != null && packetLoss > 5) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "Router packet loss detected: " + packetLoss + "%", 2);
            } else {
                return new MonitoringResult(device.getId(), "OK", "Router operating normally", 1);
            }
        }
        
        @Override
        public boolean canHandle(String deviceType) {
            return "ROUTER".equals(deviceType);
        }
    }
    
    /**
     * Switch monitoring strategy
     */
    public static class SwitchMonitoringStrategy implements MonitoringStrategy {
        @Override
        public MonitoringResult monitor(Device device) {
            Integer portUtilization = (Integer) device.getMetrics().get("port_utilization");
            Integer temperature = (Integer) device.getMetrics().get("temperature");
            
            if (temperature != null && temperature > 80) {
                return new MonitoringResult(device.getId(), "CRITICAL", 
                    "Switch temperature critical: " + temperature + "°C", 3);
            } else if (portUtilization != null && portUtilization > 95) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "Switch port utilization high: " + portUtilization + "%", 2);
            } else {
                return new MonitoringResult(device.getId(), "OK", "Switch operating normally", 1);
            }
        }
        
        @Override
        public boolean canHandle(String deviceType) {
            return "SWITCH".equals(deviceType);
        }
    }
    
    /**
     * Firewall monitoring strategy
     */
    public static class FirewallMonitoringStrategy implements MonitoringStrategy {
        @Override
        public MonitoringResult monitor(Device device) {
            Integer blockedConnections = (Integer) device.getMetrics().get("blocked_connections");
            Integer cpuUsage = (Integer) device.getMetrics().get("cpu_usage");
            
            if (cpuUsage != null && cpuUsage > 95) {
                return new MonitoringResult(device.getId(), "CRITICAL", 
                    "Firewall CPU usage critical: " + cpuUsage + "%", 3);
            } else if (blockedConnections != null && blockedConnections > 10000) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "High number of blocked connections: " + blockedConnections, 2);
            } else {
                return new MonitoringResult(device.getId(), "OK", "Firewall operating normally", 1);
            }
        }
        
        @Override
        public boolean canHandle(String deviceType) {
            return "FIREWALL".equals(deviceType);
        }
    }
    
    /**
     * Context class that uses the strategies
     */
    public static class DeviceMonitor {
        private final Map<String, MonitoringStrategy> strategies;
        
        public DeviceMonitor() {
            this.strategies = new HashMap<>();
            // Register default strategies
            registerStrategy(new RouterMonitoringStrategy());
            registerStrategy(new SwitchMonitoringStrategy());
            registerStrategy(new FirewallMonitoringStrategy());
        }
        
        /**
         * Register a new monitoring strategy - allows runtime extension
         */
        public void registerStrategy(MonitoringStrategy strategy) {
            // Find all device types this strategy can handle
            String[] deviceTypes = {"ROUTER", "SWITCH", "FIREWALL", "SERVER", "ACCESS_POINT", "LOAD_BALANCER"};
            for (String deviceType : deviceTypes) {
                if (strategy.canHandle(deviceType)) {
                    strategies.put(deviceType, strategy);
                }
            }
        }
        
        /**
         * Monitor device using appropriate strategy
         */
        public MonitoringResult monitorDevice(Device device) {
            MonitoringStrategy strategy = strategies.get(device.getType());
            
            if (strategy != null) {
                return strategy.monitor(device);
            } else {
                return new MonitoringResult(device.getId(), "ERROR", 
                    "No monitoring strategy found for device type: " + device.getType(), 3);
            }
        }
        
        /**
         * Get all registered device types
         */
        public String[] getSupportedDeviceTypes() {
            return strategies.keySet().toArray(new String[0]);
        }
    }
    
    /**
     * Example of how easy it is to add a new device type
     */
    public static class LoadBalancerMonitoringStrategy implements MonitoringStrategy {
        @Override
        public MonitoringResult monitor(Device device) {
            Integer activeConnections = (Integer) device.getMetrics().get("active_connections");
            Integer responseTime = (Integer) device.getMetrics().get("response_time");
            
            if (responseTime != null && responseTime > 5000) {
                return new MonitoringResult(device.getId(), "CRITICAL", 
                    "Load Balancer response time critical: " + responseTime + "ms", 3);
            } else if (activeConnections != null && activeConnections > 50000) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "Load Balancer high connection count: " + activeConnections, 2);
            } else {
                return new MonitoringResult(device.getId(), "OK", "Load Balancer operating normally", 1);
            }
        }
        
        @Override
        public boolean canHandle(String deviceType) {
            return "LOAD_BALANCER".equals(deviceType);
        }
    }
}