package com.telekom.architecture.training.day3.strategy;

import java.util.Map;
import java.util.HashMap;

/**
 * INITIAL (PROBLEMATIC) IMPLEMENTATION
 * 
 * Problems:
 * - If/Else chains for different device types
 * - Adding new device types requires code modification
 * - Violation of Open/Closed Principle
 * - Hard to test individual monitoring algorithms
 * 
 * Trainer Notes:
 * - Show how conditional logic becomes unmanageable
 * - Demonstrate difficulty in adding new device types
 * - Point out code duplication in similar monitoring patterns
 */
public class DeviceMonitoringInitial {
    
    public enum DeviceType {
        ROUTER, SWITCH, FIREWALL, SERVER, ACCESS_POINT
    }
    
    public static class Device {
        private final String id;
        private final DeviceType type;
        private final Map<String, Object> metrics;
        
        public Device(String id, DeviceType type) {
            this.id = id;
            this.type = type;
            this.metrics = new HashMap<>();
        }
        
        // Getters
        public String getId() { return id; }
        public DeviceType getType() { return type; }
        public Map<String, Object> getMetrics() { return metrics; }
        
        public void updateMetric(String key, Object value) {
            metrics.put(key, value);
        }
    }
    
    public static class MonitoringResult {
        private final String deviceId;
        private final String status;
        private final String message;
        private final int severity; // 1=info, 2=warning, 3=critical
        
        public MonitoringResult(String deviceId, String status, String message, int severity) {
            this.deviceId = deviceId;
            this.status = status;
            this.message = message;
            this.severity = severity;
        }
        
        // Getters
        public String getDeviceId() { return deviceId; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public int getSeverity() { return severity; }
    }
    
    /**
     * PROBLEMATIC: Long if/else chain that grows with each new device type
     */
    public MonitoringResult monitorDevice(Device device) {
        if (device.getType() == DeviceType.ROUTER) {
            // Router specific monitoring logic
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
            
        } else if (device.getType() == DeviceType.SWITCH) {
            // Switch specific monitoring logic
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
            
        } else if (device.getType() == DeviceType.FIREWALL) {
            // Firewall specific monitoring logic
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
            
        } else if (device.getType() == DeviceType.SERVER) {
            // Server specific monitoring logic
            Integer cpuUsage = (Integer) device.getMetrics().get("cpu_usage");
            Integer memoryUsage = (Integer) device.getMetrics().get("memory_usage");
            Integer diskUsage = (Integer) device.getMetrics().get("disk_usage");
            
            if (cpuUsage != null && cpuUsage > 95) {
                return new MonitoringResult(device.getId(), "CRITICAL", 
                    "Server CPU usage critical: " + cpuUsage + "%", 3);
            } else if (memoryUsage != null && memoryUsage > 90) {
                return new MonitoringResult(device.getId(), "CRITICAL", 
                    "Server memory usage critical: " + memoryUsage + "%", 3);
            } else if (diskUsage != null && diskUsage > 95) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "Server disk usage high: " + diskUsage + "%", 2);
            } else {
                return new MonitoringResult(device.getId(), "OK", "Server operating normally", 1);
            }
            
        } else if (device.getType() == DeviceType.ACCESS_POINT) {
            // Access Point specific monitoring logic
            Integer connectedClients = (Integer) device.getMetrics().get("connected_clients");
            Integer signalStrength = (Integer) device.getMetrics().get("signal_strength");
            
            if (signalStrength != null && signalStrength < -70) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "Access Point signal strength low: " + signalStrength + " dBm", 2);
            } else if (connectedClients != null && connectedClients > 50) {
                return new MonitoringResult(device.getId(), "WARNING", 
                    "Access Point client count high: " + connectedClients, 2);
            } else {
                return new MonitoringResult(device.getId(), "OK", "Access Point operating normally", 1);
            }
            
        } else {
            // What happens when we add a new device type?
            return new MonitoringResult(device.getId(), "ERROR", 
                "Unknown device type: " + device.getType(), 3);
        }
    }
}