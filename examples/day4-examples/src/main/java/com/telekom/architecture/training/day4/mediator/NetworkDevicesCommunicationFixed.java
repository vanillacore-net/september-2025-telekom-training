package com.telekom.architecture.training.day4.mediator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FIXED IMPLEMENTATION using Mediator Pattern
 * 
 * Benefits:
 * - Central coordination eliminates O(n²) complexity
 * - Loose coupling between network components
 * - Easy to add new device types without changing existing code
 * - Centralized business logic for network orchestration
 * - Async processing capabilities for better performance
 * - Consistent error handling and logging
 * 
 * Enterprise Features:
 * - Event prioritization and filtering
 * - Async processing with thread pools
 * - Circuit breaker pattern for resilience
 * - Centralized metrics and monitoring
 * 
 * Trainer Notes:
 * - Show how complexity drops from O(n²) to O(n)
 * - Demonstrate adding new device types easily
 * - Point out centralized business logic benefits
 * - Discuss async processing advantages
 */
public class NetworkDevicesCommunicationFixed {
    
    // Event types for network communication
    public enum NetworkEventType {
        STATUS_CHANGE, TOPOLOGY_CHANGE, CONFIGURATION_UPDATE, PERFORMANCE_ALERT, SECURITY_INCIDENT
    }
    
    public static class NetworkEvent {
        private final String sourceDeviceId;
        private final NetworkEventType eventType;
        private final String details;
        private final LocalDateTime timestamp;
        private final int priority; // 1=Critical, 5=Info
        
        public NetworkEvent(String sourceDeviceId, NetworkEventType eventType, String details, int priority) {
            this.sourceDeviceId = sourceDeviceId;
            this.eventType = eventType;
            this.details = details;
            this.priority = priority;
            this.timestamp = LocalDateTime.now();
        }
        
        // Getters
        public String getSourceDeviceId() { return sourceDeviceId; }
        public NetworkEventType getEventType() { return eventType; }
        public String getDetails() { return details; }
        public int getPriority() { return priority; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    // Mediator interface
    public interface NetworkOrchestrationMediator {
        void registerDevice(NetworkDevice device);
        void unregisterDevice(String deviceId);
        void notifyEvent(NetworkEvent event);
        void shutdown();
    }
    
    // Concrete Mediator - The heart of network orchestration
    public static class TelekomNetworkOrchestrator implements NetworkOrchestrationMediator {
        
        private final List<NetworkDevice> devices = new CopyOnWriteArrayList<>();
        private final ExecutorService eventProcessor = Executors.newFixedThreadPool(10);
        private final AtomicInteger processedEvents = new AtomicInteger(0);
        private volatile boolean circuitBreakerOpen = false;
        private AtomicInteger failureCount = new AtomicInteger(0);
        
        @Override
        public void registerDevice(NetworkDevice device) {
            devices.add(device);
            device.setMediator(this);
            System.out.println(LocalDateTime.now() + " [ORCHESTRATOR] Device registered: " + device.getId() + " (" + device.getType() + ")");
        }
        
        @Override
        public void unregisterDevice(String deviceId) {
            devices.removeIf(device -> device.getId().equals(deviceId));
            System.out.println(LocalDateTime.now() + " [ORCHESTRATOR] Device unregistered: " + deviceId);
        }
        
        @Override
        public void notifyEvent(NetworkEvent event) {
            if (circuitBreakerOpen) {
                System.out.println("⚠️ Circuit breaker open - dropping event from " + event.getSourceDeviceId());
                return;
            }
            
            // Async processing for better performance
            CompletableFuture.runAsync(() -> processEvent(event), eventProcessor)
                .exceptionally(throwable -> {
                    handleEventProcessingFailure(event, throwable);
                    return null;
                });
        }
        
        private void processEvent(NetworkEvent event) {
            processedEvents.incrementAndGet();
            System.out.println(LocalDateTime.now() + " [ORCHESTRATOR] Processing event: " + event.getEventType() + " from " + event.getSourceDeviceId());
            
            // Central business logic for network orchestration
            switch (event.getEventType()) {
                case STATUS_CHANGE:
                    handleStatusChange(event);
                    break;
                case TOPOLOGY_CHANGE:
                    handleTopologyChange(event);
                    break;
                case CONFIGURATION_UPDATE:
                    handleConfigurationUpdate(event);
                    break;
                case PERFORMANCE_ALERT:
                    handlePerformanceAlert(event);
                    break;
                case SECURITY_INCIDENT:
                    handleSecurityIncident(event);
                    break;
            }
            
            // Reset failure count on successful processing
            failureCount.set(0);
            circuitBreakerOpen = false;
        }
        
        private void handleStatusChange(NetworkEvent event) {
            // Notify only relevant devices based on business rules
            devices.parallelStream()
                .filter(device -> shouldNotifyForStatusChange(device, event))
                .forEach(device -> device.onNetworkEvent(event));
        }
        
        private void handleTopologyChange(NetworkEvent event) {
            // Routers and switches need to know about topology changes
            devices.parallelStream()
                .filter(device -> device.getType().equals("ROUTER") || device.getType().equals("SWITCH"))
                .forEach(device -> device.onNetworkEvent(event));
        }
        
        private void handleConfigurationUpdate(NetworkEvent event) {
            // Security devices need configuration updates
            devices.parallelStream()
                .filter(device -> device.getType().equals("FIREWALL") || device.getType().equals("MONITOR"))
                .forEach(device -> device.onNetworkEvent(event));
        }
        
        private void handlePerformanceAlert(NetworkEvent event) {
            // Load balancers and monitoring systems handle performance
            devices.parallelStream()
                .filter(device -> device.getType().equals("LOAD_BALANCER") || device.getType().equals("MONITOR"))
                .forEach(device -> device.onNetworkEvent(event));
        }
        
        private void handleSecurityIncident(NetworkEvent event) {
            // High priority - notify all security-relevant devices
            devices.parallelStream()
                .filter(device -> device.getType().equals("FIREWALL") || 
                               device.getType().equals("MONITOR") || 
                               device.getType().equals("ROUTER"))
                .forEach(device -> device.onNetworkEvent(event));
        }
        
        private boolean shouldNotifyForStatusChange(NetworkDevice device, NetworkEvent event) {
            // Business logic: don't notify device about its own status changes
            return !device.getId().equals(event.getSourceDeviceId());
        }
        
        private void handleEventProcessingFailure(NetworkEvent event, Throwable throwable) {
            int failures = failureCount.incrementAndGet();
            System.err.println("❌ Event processing failed: " + throwable.getMessage());
            
            // Circuit breaker pattern
            if (failures >= 5) {
                circuitBreakerOpen = true;
                System.err.println("🔴 Circuit breaker opened - too many failures");
            }
        }
        
        @Override
        public void shutdown() {
            eventProcessor.shutdown();
        }
        
        public int getProcessedEventCount() { return processedEvents.get(); }
        public int getRegisteredDeviceCount() { return devices.size(); }
        public boolean isCircuitBreakerOpen() { return circuitBreakerOpen; }
    }
    
    // Base NetworkDevice class
    public abstract static class NetworkDevice {
        protected final String id;
        protected final String type;
        protected String status = "ONLINE";
        protected NetworkOrchestrationMediator mediator;
        
        public NetworkDevice(String id, String type) {
            this.id = id;
            this.type = type;
        }
        
        public void setMediator(NetworkOrchestrationMediator mediator) {
            this.mediator = mediator;
        }
        
        public void updateStatus(String newStatus) {
            this.status = newStatus;
            
            // Notify mediator instead of directly notifying other devices
            if (mediator != null) {
                NetworkEvent event = new NetworkEvent(
                    id, 
                    NetworkEventType.STATUS_CHANGE, 
                    "Status changed to: " + newStatus,
                    newStatus.equals("OFFLINE") ? 1 : 3 // Critical if offline
                );
                mediator.notifyEvent(event);
            }
        }
        
        // Template method for handling network events
        public abstract void onNetworkEvent(NetworkEvent event);
        
        public String getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
    }
    
    // Specific device implementations
    public static class Router extends NetworkDevice {
        private int routingTableSize = 100;
        private AtomicInteger eventProcessedCount = new AtomicInteger(0);
        
        public Router(String id) {
            super(id, "ROUTER");
        }
        
        @Override
        public void onNetworkEvent(NetworkEvent event) {
            eventProcessedCount.incrementAndGet();
            
            switch (event.getEventType()) {
                case STATUS_CHANGE:
                    System.out.println("  → Router [" + id + "] updating routing table for " + event.getSourceDeviceId());
                    routingTableSize += event.getDetails().contains("ONLINE") ? 5 : -3;
                    break;
                case TOPOLOGY_CHANGE:
                    System.out.println("  → Router [" + id + "] recalculating routes due to topology change");
                    routingTableSize += 10;
                    break;
            }
        }
        
        public int getEventProcessedCount() { return eventProcessedCount.get(); }
        public int getRoutingTableSize() { return routingTableSize; }
    }
    
    public static class Switch extends NetworkDevice {
        private int vlanCount = 50;
        private AtomicInteger eventProcessedCount = new AtomicInteger(0);
        
        public Switch(String id) {
            super(id, "SWITCH");
        }
        
        @Override
        public void onNetworkEvent(NetworkEvent event) {
            eventProcessedCount.incrementAndGet();
            
            switch (event.getEventType()) {
                case STATUS_CHANGE:
                    System.out.println("  → Switch [" + id + "] updating VLAN config for " + event.getSourceDeviceId());
                    vlanCount += event.getDetails().contains("ONLINE") ? 2 : -1;
                    break;
                case TOPOLOGY_CHANGE:
                    System.out.println("  → Switch [" + id + "] reconfiguring ports for topology change");
                    vlanCount += 5;
                    break;
            }
        }
        
        public int getEventProcessedCount() { return eventProcessedCount.get(); }
        public int getVlanCount() { return vlanCount; }
    }
    
    public static class FirewallDevice extends NetworkDevice {
        private int ruleCount = 200;
        private AtomicInteger eventProcessedCount = new AtomicInteger(0);
        
        public FirewallDevice(String id) {
            super(id, "FIREWALL");
        }
        
        @Override
        public void onNetworkEvent(NetworkEvent event) {
            eventProcessedCount.incrementAndGet();
            
            switch (event.getEventType()) {
                case CONFIGURATION_UPDATE:
                    System.out.println("  → Firewall [" + id + "] updating security policies");
                    ruleCount += 10;
                    break;
                case SECURITY_INCIDENT:
                    System.out.println("  → Firewall [" + id + "] CRITICAL: Processing security incident");
                    ruleCount += 20;
                    break;
            }
        }
        
        public int getEventProcessedCount() { return eventProcessedCount.get(); }
        public int getRuleCount() { return ruleCount; }
    }
    
    public static class LoadBalancer extends NetworkDevice {
        private double cpuUtilization = 35.0;
        private AtomicInteger eventProcessedCount = new AtomicInteger(0);
        
        public LoadBalancer(String id) {
            super(id, "LOAD_BALANCER");
        }
        
        @Override
        public void onNetworkEvent(NetworkEvent event) {
            eventProcessedCount.incrementAndGet();
            
            switch (event.getEventType()) {
                case PERFORMANCE_ALERT:
                    System.out.println("  → LoadBalancer [" + id + "] adjusting traffic distribution");
                    cpuUtilization += event.getPriority() == 1 ? 10.0 : 2.0;
                    break;
                case STATUS_CHANGE:
                    if (event.getDetails().contains("OFFLINE")) {
                        System.out.println("  → LoadBalancer [" + id + "] removing " + event.getSourceDeviceId() + " from pool");
                        cpuUtilization += 5.0;
                    }
                    break;
            }
        }
        
        public int getEventProcessedCount() { return eventProcessedCount.get(); }
        public double getCpuUtilization() { return cpuUtilization; }
    }
    
    public static class MonitoringSystem extends NetworkDevice {
        private int alertsGenerated = 0;
        private AtomicInteger eventProcessedCount = new AtomicInteger(0);
        
        public MonitoringSystem(String id) {
            super(id, "MONITOR");
        }
        
        @Override
        public void onNetworkEvent(NetworkEvent event) {
            eventProcessedCount.incrementAndGet();
            
            // Monitoring system processes all events
            System.out.println("  → Monitor [" + id + "] logging event: " + event.getEventType() + " from " + event.getSourceDeviceId());
            
            // Generate alerts for critical events
            if (event.getPriority() <= 2) {
                alertsGenerated++;
                System.out.println("    📊 ALERT generated for critical event");
            }
        }
        
        public int getEventProcessedCount() { return eventProcessedCount.get(); }
        public int getAlertsGenerated() { return alertsGenerated; }
    }
}