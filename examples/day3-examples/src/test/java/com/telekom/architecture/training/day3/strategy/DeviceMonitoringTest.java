package com.telekom.architecture.training.day3.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests demonstrating the benefits of Strategy pattern over if/else chains
 * 
 * Trainer Notes:
 * - Compare testing complexity between initial and fixed implementations
 * - Show how Strategy pattern enables isolated testing of algorithms
 * - Demonstrate runtime strategy registration and switching
 */
class DeviceMonitoringTest {
    
    private DeviceMonitoringFixed.DeviceMonitor monitor;
    
    @BeforeEach
    void setUp() {
        monitor = new DeviceMonitoringFixed.DeviceMonitor();
    }
    
    @Test
    @DisplayName("Initial implementation handles router monitoring")
    void testInitialRouterMonitoring() {
        // Given
        DeviceMonitoringInitial initialMonitor = new DeviceMonitoringInitial();
        DeviceMonitoringInitial.Device router = new DeviceMonitoringInitial.Device("R001", 
            DeviceMonitoringInitial.DeviceType.ROUTER);
        router.updateMetric("cpu_usage", 95);
        
        // When
        DeviceMonitoringInitial.MonitoringResult result = initialMonitor.monitorDevice(router);
        
        // Then
        assertThat(result.getStatus()).isEqualTo("CRITICAL");
        assertThat(result.getMessage()).contains("CPU usage critical");
        assertThat(result.getSeverity()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("Strategy pattern handles router monitoring with same logic but better structure")
    void testStrategyRouterMonitoring() {
        // Given
        DeviceMonitoringFixed.Device router = new DeviceMonitoringFixed.Device("R001", "ROUTER");
        router.updateMetric("cpu_usage", 95);
        
        // When
        DeviceMonitoringFixed.MonitoringResult result = monitor.monitorDevice(router);
        
        // Then
        assertThat(result.getStatus()).isEqualTo("CRITICAL");
        assertThat(result.getMessage()).contains("CPU usage critical");
        assertThat(result.getSeverity()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("Strategy pattern allows testing individual strategies in isolation")
    void testIndividualStrategyTesting() {
        // Given
        DeviceMonitoringFixed.RouterMonitoringStrategy strategy = 
            new DeviceMonitoringFixed.RouterMonitoringStrategy();
        DeviceMonitoringFixed.Device router = new DeviceMonitoringFixed.Device("R001", "ROUTER");
        router.updateMetric("packet_loss", 10);
        
        // When
        DeviceMonitoringFixed.MonitoringResult result = strategy.monitor(router);
        
        // Then
        assertThat(result.getStatus()).isEqualTo("WARNING");
        assertThat(result.getMessage()).contains("packet loss detected");
    }
    
    @Test
    @DisplayName("Strategy pattern handles switch monitoring")
    void testSwitchMonitoring() {
        // Given
        DeviceMonitoringFixed.Device networkSwitch = new DeviceMonitoringFixed.Device("S001", "SWITCH");
        networkSwitch.updateMetric("temperature", 85);
        
        // When
        DeviceMonitoringFixed.MonitoringResult result = monitor.monitorDevice(networkSwitch);
        
        // Then
        assertThat(result.getStatus()).isEqualTo("CRITICAL");
        assertThat(result.getMessage()).contains("temperature critical");
    }
    
    @Test
    @DisplayName("Strategy pattern allows runtime registration of new strategies")
    void testRuntimeStrategyRegistration() {
        // Given - Load Balancer not initially supported
        DeviceMonitoringFixed.Device loadBalancer = 
            new DeviceMonitoringFixed.Device("LB001", "LOAD_BALANCER");
        loadBalancer.updateMetric("response_time", 6000);
        
        // When - Check initial behavior
        DeviceMonitoringFixed.MonitoringResult initialResult = monitor.monitorDevice(loadBalancer);
        assertThat(initialResult.getStatus()).isEqualTo("ERROR");
        assertThat(initialResult.getMessage()).contains("No monitoring strategy found");
        
        // When - Register new strategy
        monitor.registerStrategy(new DeviceMonitoringFixed.LoadBalancerMonitoringStrategy());
        DeviceMonitoringFixed.MonitoringResult result = monitor.monitorDevice(loadBalancer);
        
        // Then
        assertThat(result.getStatus()).isEqualTo("CRITICAL");
        assertThat(result.getMessage()).contains("response time critical");
    }
    
    @Test
    @DisplayName("Strategy pattern provides list of supported device types")
    void testSupportedDeviceTypes() {
        // When
        String[] supportedTypes = monitor.getSupportedDeviceTypes();
        
        // Then
        assertThat(supportedTypes).contains("ROUTER", "SWITCH", "FIREWALL");
        assertThat(supportedTypes).doesNotContain("LOAD_BALANCER"); // Not registered yet
        
        // When - Register new strategy
        monitor.registerStrategy(new DeviceMonitoringFixed.LoadBalancerMonitoringStrategy());
        supportedTypes = monitor.getSupportedDeviceTypes();
        
        // Then
        assertThat(supportedTypes).contains("LOAD_BALANCER");
    }
    
    @Test
    @DisplayName("Multiple devices can be monitored with different strategies")
    void testMultipleDeviceMonitoring() {
        // Given
        DeviceMonitoringFixed.Device router = new DeviceMonitoringFixed.Device("R001", "ROUTER");
        router.updateMetric("cpu_usage", 50);
        
        DeviceMonitoringFixed.Device firewall = new DeviceMonitoringFixed.Device("FW001", "FIREWALL");
        firewall.updateMetric("blocked_connections", 15000);
        
        // When
        DeviceMonitoringFixed.MonitoringResult routerResult = monitor.monitorDevice(router);
        DeviceMonitoringFixed.MonitoringResult firewallResult = monitor.monitorDevice(firewall);
        
        // Then
        assertThat(routerResult.getStatus()).isEqualTo("OK");
        assertThat(firewallResult.getStatus()).isEqualTo("WARNING");
        assertThat(firewallResult.getMessage()).contains("blocked connections");
    }
    
    @Test
    @DisplayName("Strategy pattern handles edge cases gracefully")
    void testEdgeCases() {
        // Given - Device with no metrics
        DeviceMonitoringFixed.Device router = new DeviceMonitoringFixed.Device("R001", "ROUTER");
        
        // When
        DeviceMonitoringFixed.MonitoringResult result = monitor.monitorDevice(router);
        
        // Then - Should handle null metrics gracefully
        assertThat(result.getStatus()).isEqualTo("OK");
        assertThat(result.getMessage()).contains("operating normally");
    }
}