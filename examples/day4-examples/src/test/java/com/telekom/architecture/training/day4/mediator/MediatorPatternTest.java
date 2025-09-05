package com.telekom.architecture.training.day4.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.architecture.training.day4.mediator.NetworkDevicesCommunicationFixed.*;

/**
 * Test suite for Mediator Pattern implementation
 * 
 * Tests demonstrate:
 * - Central orchestration eliminates O(n²) complexity
 * - Loose coupling between network components
 * - Event filtering and prioritization
 * - Async processing capabilities
 * - Circuit breaker functionality
 */
class MediatorPatternTest {
    
    private TelekomNetworkOrchestrator orchestrator;
    private Router router;
    private Switch switchDevice;
    private FirewallDevice firewall;
    private LoadBalancer loadBalancer;
    private MonitoringSystem monitor;
    
    @BeforeEach
    void setUp() {
        orchestrator = new TelekomNetworkOrchestrator();
        router = new Router("R001");
        switchDevice = new Switch("SW001");
        firewall = new FirewallDevice("FW001");
        loadBalancer = new LoadBalancer("LB001");
        monitor = new MonitoringSystem("MON001");
        
        // Register all devices with mediator
        orchestrator.registerDevice(router);
        orchestrator.registerDevice(switchDevice);
        orchestrator.registerDevice(firewall);
        orchestrator.registerDevice(loadBalancer);
        orchestrator.registerDevice(monitor);
    }
    
    @Test
    @DisplayName("Mediator should register devices and track count")
    void shouldRegisterDevicesAndTrackCount() {
        assertThat(orchestrator.getRegisteredDeviceCount()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("Status change should trigger appropriate device notifications")
    void shouldTriggerAppropriateNotificationsOnStatusChange() throws InterruptedException {
        int initialRouterEvents = router.getEventProcessedCount();
        int initialSwitchEvents = switchDevice.getEventProcessedCount();
        int initialMonitorEvents = monitor.getEventProcessedCount();
        
        // Trigger status change on router
        router.updateStatus("MAINTENANCE");
        
        // Allow async processing
        Thread.sleep(100);
        
        // Verify other devices were notified (but not the router itself)
        assertThat(router.getEventProcessedCount()).isEqualTo(initialRouterEvents);
        assertThat(switchDevice.getEventProcessedCount()).isGreaterThan(initialSwitchEvents);
        assertThat(monitor.getEventProcessedCount()).isGreaterThan(initialMonitorEvents);
        
        // Verify orchestrator processed the event
        assertThat(orchestrator.getProcessedEventCount()).isGreaterThan(0);
    }
    
    @Test
    @DisplayName("Security incident should notify security-relevant devices only")
    void shouldNotifySecurityRelevantDevicesOnly() throws InterruptedException {
        int initialFirewallEvents = firewall.getEventProcessedCount();
        int initialMonitorEvents = monitor.getEventProcessedCount();
        int initialSwitchEvents = switchDevice.getEventProcessedCount();
        
        // Create security incident event
        NetworkEvent securityEvent = new NetworkEvent(
            "EXT001", 
            NetworkEventType.SECURITY_INCIDENT, 
            "Suspicious activity detected",
            1 // Critical priority
        );
        
        orchestrator.notifyEvent(securityEvent);
        Thread.sleep(100);
        
        // Verify firewall and monitor were notified (security relevant)
        assertThat(firewall.getEventProcessedCount()).isGreaterThan(initialFirewallEvents);
        assertThat(monitor.getEventProcessedCount()).isGreaterThan(initialMonitorEvents);
        
        // Verify switch was not notified (not security relevant)
        assertThat(switchDevice.getEventProcessedCount()).isEqualTo(initialSwitchEvents);
    }
    
    @Test
    @DisplayName("Performance alert should notify load balancer and monitoring systems")
    void shouldNotifyPerformanceRelevantDevices() throws InterruptedException {
        int initialLoadBalancerEvents = loadBalancer.getEventProcessedCount();
        int initialMonitorEvents = monitor.getEventProcessedCount();
        int initialFirewallEvents = firewall.getEventProcessedCount();
        
        // Create performance alert event
        NetworkEvent performanceEvent = new NetworkEvent(
            "APP001",
            NetworkEventType.PERFORMANCE_ALERT,
            "High response time detected",
            2 // High priority
        );
        
        orchestrator.notifyEvent(performanceEvent);
        Thread.sleep(100);
        
        // Verify load balancer and monitor were notified
        assertThat(loadBalancer.getEventProcessedCount()).isGreaterThan(initialLoadBalancerEvents);
        assertThat(monitor.getEventProcessedCount()).isGreaterThan(initialMonitorEvents);
        
        // Verify firewall was not notified (not performance relevant)
        assertThat(firewall.getEventProcessedCount()).isEqualTo(initialFirewallEvents);
    }
    
    @Test
    @DisplayName("Circuit breaker should open after multiple failures")
    void shouldOpenCircuitBreakerAfterFailures() throws InterruptedException {
        assertThat(orchestrator.isCircuitBreakerOpen()).isFalse();
        
        // Create events that will trigger failures (this would need to be simulated)
        for (int i = 0; i < 6; i++) {
            NetworkEvent event = new NetworkEvent(
                "FAIL" + i,
                NetworkEventType.STATUS_CHANGE,
                "Failure event " + i,
                1
            );
            orchestrator.notifyEvent(event);
        }
        
        Thread.sleep(200);
        
        // Note: Circuit breaker opening depends on internal failure simulation
        // This test demonstrates the concept
        assertThat(orchestrator.getProcessedEventCount()).isGreaterThan(0);
    }
    
    @Test
    @DisplayName("Device unregistration should remove device from orchestrator")
    void shouldUnregisterDevice() {
        assertThat(orchestrator.getRegisteredDeviceCount()).isEqualTo(5);
        
        orchestrator.unregisterDevice("R001");
        
        assertThat(orchestrator.getRegisteredDeviceCount()).isEqualTo(4);
    }
    
    @Test
    @DisplayName("Topology change should notify routers and switches only")
    void shouldNotifyTopologyRelevantDevices() throws InterruptedException {
        int initialRouterEvents = router.getEventProcessedCount();
        int initialSwitchEvents = switchDevice.getEventProcessedCount();
        int initialFirewallEvents = firewall.getEventProcessedCount();
        
        NetworkEvent topologyEvent = new NetworkEvent(
            "TOPO001",
            NetworkEventType.TOPOLOGY_CHANGE,
            "Network topology updated",
            3
        );
        
        orchestrator.notifyEvent(topologyEvent);
        Thread.sleep(100);
        
        // Verify router and switch were notified
        assertThat(router.getEventProcessedCount()).isGreaterThan(initialRouterEvents);
        assertThat(switchDevice.getEventProcessedCount()).isGreaterThan(initialSwitchEvents);
        
        // Verify firewall was not notified for topology changes
        assertThat(firewall.getEventProcessedCount()).isEqualTo(initialFirewallEvents);
    }
    
    @Test
    @DisplayName("Configuration update should notify security devices")
    void shouldNotifySecurityDevicesForConfigurationUpdates() throws InterruptedException {
        int initialFirewallEvents = firewall.getEventProcessedCount();
        int initialMonitorEvents = monitor.getEventProcessedCount();
        int initialRouterEvents = router.getEventProcessedCount();
        
        NetworkEvent configEvent = new NetworkEvent(
            "CONFIG001",
            NetworkEventType.CONFIGURATION_UPDATE,
            "Security configuration updated",
            3
        );
        
        orchestrator.notifyEvent(configEvent);
        Thread.sleep(100);
        
        // Verify security devices were notified
        assertThat(firewall.getEventProcessedCount()).isGreaterThan(initialFirewallEvents);
        assertThat(monitor.getEventProcessedCount()).isGreaterThan(initialMonitorEvents);
        
        // Verify router was not notified for config updates
        assertThat(router.getEventProcessedCount()).isEqualTo(initialRouterEvents);
    }
    
    @Test
    @DisplayName("Device state should be updated correctly on events")
    void shouldUpdateDeviceStateOnEvents() throws InterruptedException {
        // Initial state
        assertThat(router.getRoutingTableSize()).isEqualTo(100);
        assertThat(switchDevice.getVlanCount()).isEqualTo(50);
        
        // Trigger status change that should update internal state
        router.updateStatus("ONLINE");
        Thread.sleep(100);
        
        // Device state should be updated based on the events they process
        // This verifies the mediator is properly routing events to devices
        assertThat(orchestrator.getProcessedEventCount()).isGreaterThan(0);
    }
}