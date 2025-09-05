package com.telekom.architecture.training.day3.observer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests demonstrating the benefits of Observer pattern over tight coupling
 * 
 * Trainer Notes:
 * - Compare the coupling in initial vs fixed implementations
 * - Show how Observer pattern enables runtime observer management
 * - Demonstrate how easy it is to add new observers
 * - Point out improved testability of individual components
 */
class DeviceEventsTest {
    
    private DeviceEventsFixed.NetworkDevice device;
    private DeviceEventsFixed.MonitoringSystemObserver monitoringObserver;
    private DeviceEventsFixed.LoggingSystemObserver loggingObserver;
    private DeviceEventsFixed.AlertingSystemObserver alertingObserver;
    
    @BeforeEach
    void setUp() {
        device = new DeviceEventsFixed.NetworkDevice("TEST001");
        monitoringObserver = new DeviceEventsFixed.MonitoringSystemObserver();
        loggingObserver = new DeviceEventsFixed.LoggingSystemObserver();
        alertingObserver = new DeviceEventsFixed.AlertingSystemObserver();
        
        // Add observers
        device.addObserver(monitoringObserver);
        device.addObserver(loggingObserver);
        device.addObserver(alertingObserver);
    }
    
    @Test
    @DisplayName("Initial implementation requires tight coupling")
    void testInitialTightCoupling() {
        // Given - Initial implementation requires all systems in constructor
        DeviceEventsInitial.MonitoringSystem monitoring = new DeviceEventsInitial.MonitoringSystem();
        DeviceEventsInitial.LoggingSystem logging = new DeviceEventsInitial.LoggingSystem();
        DeviceEventsInitial.AlertingSystem alerting = new DeviceEventsInitial.AlertingSystem();
        
        // Device must know about all systems at creation time
        DeviceEventsInitial.NetworkDevice initialDevice = 
            new DeviceEventsInitial.NetworkDevice("INITIAL001", monitoring, logging, alerting);
        
        // When
        initialDevice.updateStatus("OFFLINE");
        
        // Then - All systems receive notifications, but coupling is tight
        assertThat(monitoring.getEvents()).hasSize(1);
        assertThat(logging.getLogs()).hasSize(1);
        // Note: Cannot easily add new systems without modifying device constructor
    }
    
    @Test
    @DisplayName("Observer pattern enables loose coupling")
    void testObserverLooseCoupling() {
        // Given - Device doesn't need to know about specific observers
        // Observers can be added after device creation
        
        // When
        device.updateStatus("OFFLINE");
        
        // Then - All observers receive notifications
        assertThat(monitoringObserver.getEvents()).hasSize(1);
        assertThat(loggingObserver.getLogs()).hasSize(1);
        
        // Device only knows about the observer interface, not specific implementations
        assertThat(device.getObservers()).hasSize(3);
    }
    
    @Test
    @DisplayName("Observers can be added and removed at runtime")
    void testRuntimeObserverManagement() {
        // Given - Start with initial observers
        assertThat(device.getObservers()).hasSize(3);
        
        // When - Remove an observer
        device.removeObserver(alertingObserver);
        assertThat(device.getObservers()).hasSize(2);
        
        // When - Trigger event
        device.updateCpuUsage(90);
        
        // Then - Only remaining observers are notified
        assertThat(monitoringObserver.getEvents()).hasSize(1);
        assertThat(loggingObserver.getLogs()).hasSize(1);
        assertThat(alertingObserver.getAlerts()).hasSize(0); // Removed, so no alerts
        
        // When - Add new observer at runtime
        DeviceEventsFixed.InventorySystemObserver inventoryObserver = 
            new DeviceEventsFixed.InventorySystemObserver();
        device.addObserver(inventoryObserver);
        
        device.updateConfiguration("hostname", "new-hostname");
        
        // Then - New observer receives events
        assertThat(inventoryObserver.getInventoryEvents()).hasSize(1);
    }
    
    @Test
    @DisplayName("Observers can filter events they're interested in")
    void testEventFiltering() {
        // Given - Alerting observer only interested in critical events
        assertThat(alertingObserver.isInterestedIn(DeviceEventsFixed.EventType.CONNECTION_LOST)).isTrue();
        assertThat(alertingObserver.isInterestedIn(DeviceEventsFixed.EventType.THRESHOLD_EXCEEDED)).isTrue();
        assertThat(alertingObserver.isInterestedIn(DeviceEventsFixed.EventType.STATUS_CHANGE)).isFalse();
        assertThat(alertingObserver.isInterestedIn(DeviceEventsFixed.EventType.CONFIGURATION_CHANGED)).isFalse();
        
        // When - Trigger various events
        device.updateStatus("MAINTENANCE"); // Non-critical
        device.updateCpuUsage(95); // Critical
        device.updateConfiguration("timeout", "30"); // Non-critical
        device.setConnectionStatus(false); // Critical
        
        // Then - Alerting observer only receives critical events
        assertThat(monitoringObserver.getEvents()).hasSize(4); // All events
        assertThat(loggingObserver.getLogs()).hasSize(4); // All events
        assertThat(alertingObserver.getAlerts()).hasSize(2); // Only critical events
    }
    
    @Test
    @DisplayName("Easy to add new observer types")
    void testAddNewObserverTypes() {
        // Given - Add new observer types without modifying device
        DeviceEventsFixed.SecurityAuditObserver securityObserver = 
            new DeviceEventsFixed.SecurityAuditObserver();
        DeviceEventsFixed.PerformanceMetricsObserver performanceObserver = 
            new DeviceEventsFixed.PerformanceMetricsObserver();
        
        device.addObserver(securityObserver);
        device.addObserver(performanceObserver);
        
        // When - Trigger events
        device.updateConfiguration("security_policy", "strict");
        device.updateStatus("ONLINE");
        device.updateCpuUsage(85);
        
        // Then - New observers receive appropriate events
        assertThat(securityObserver.getSecurityEvents()).hasSize(3); // All events for audit
        assertThat(performanceObserver.getPerformanceEvents()).hasSize(2); // Status and threshold events
        assertThat(performanceObserver.getEventCount()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Individual observers can be tested in isolation")
    void testObserverIsolation() {
        // Given - Test observer in isolation without device
        DeviceEventsFixed.MonitoringSystemObserver isolatedObserver = 
            new DeviceEventsFixed.MonitoringSystemObserver();
        
        DeviceEventsFixed.DeviceEvent testEvent = new DeviceEventsFixed.DeviceEvent(
            "TEST001", 
            DeviceEventsFixed.EventType.THRESHOLD_EXCEEDED, 
            "Test threshold event", 
            85
        );
        
        // When - Directly call observer
        isolatedObserver.onDeviceEvent(testEvent);
        
        // Then - Observer handles event correctly
        assertThat(isolatedObserver.getEvents()).hasSize(1);
        assertThat(isolatedObserver.getEvents().get(0).getMessage()).isEqualTo("Test threshold event");
    }
    
    @Test
    @DisplayName("Device manager can handle multiple devices and global observers")
    void testDeviceManager() {
        // Given
        DeviceEventsFixed.DeviceManager manager = new DeviceEventsFixed.DeviceManager();
        DeviceEventsFixed.NetworkDevice device1 = new DeviceEventsFixed.NetworkDevice("DEV001");
        DeviceEventsFixed.NetworkDevice device2 = new DeviceEventsFixed.NetworkDevice("DEV002");
        
        DeviceEventsFixed.LoggingSystemObserver globalLogger = 
            new DeviceEventsFixed.LoggingSystemObserver();
        
        // When - Add global observer first
        manager.addGlobalObserver(globalLogger);
        
        // When - Add devices
        manager.addDevice(device1);
        manager.addDevice(device2);
        
        // Then - Global observer is added to all devices
        assertThat(device1.getObservers()).contains(globalLogger);
        assertThat(device2.getObservers()).contains(globalLogger);
        
        // When - Trigger events from different devices
        device1.updateStatus("OFFLINE");
        device2.updateCpuUsage(90);
        
        // Then - Global observer receives events from all devices
        assertThat(globalLogger.getLogs()).hasSize(2);
    }
    
    @Test
    @DisplayName("Observer pattern handles concurrent notifications safely")
    void testConcurrentObserverNotification() {
        // Given - Device uses thread-safe observer list
        DeviceEventsFixed.NetworkDevice concurrentDevice = 
            new DeviceEventsFixed.NetworkDevice("CONCURRENT001");
        
        // Add multiple observers
        for (int i = 0; i < 10; i++) {
            concurrentDevice.addObserver(new DeviceEventsFixed.LoggingSystemObserver());
        }
        
        // When - Multiple threads trigger events
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    concurrentDevice.updateCpuUsage(50 + threadId);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for completion
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Then - No exceptions should occur and all observers should be intact
        assertThat(concurrentDevice.getObservers()).hasSize(10);
    }
}