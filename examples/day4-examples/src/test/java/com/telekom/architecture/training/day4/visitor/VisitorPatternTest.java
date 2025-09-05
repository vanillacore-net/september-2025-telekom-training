package com.telekom.architecture.training.day4.visitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.architecture.training.day4.visitor.NetworkReportGenerationFixed.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Test suite for Visitor Pattern implementation
 * 
 * Tests demonstrate:
 * - Type-safe operations without instanceof casting
 * - Easy addition of new operations without modifying device classes
 * - Clean separation between data structure and operations
 * - Multiple visitor types for different purposes
 */
class VisitorPatternTest {
    
    private List<NetworkDevice> testDevices;
    private Router router;
    private Switch switchDevice;
    private FirewallDevice firewall;
    private LoadBalancer loadBalancer;
    private NetworkReportGenerator reportGenerator;
    
    @BeforeEach
    void setUp() {
        router = new Router("R001");
        router.setCpuUtilization(75.0); // High CPU for testing
        
        switchDevice = new Switch("SW001");
        switchDevice.setMacTableSize(800);
        
        firewall = new FirewallDevice("FW001");
        firewall.setThreatsBlocked(50); // High threats for testing
        
        loadBalancer = new LoadBalancer("LB001");
        loadBalancer.setResponseTime(250.0); // High response time for testing
        
        testDevices = Arrays.asList(router, switchDevice, firewall, loadBalancer);
        reportGenerator = new NetworkReportGenerator();
    }
    
    @Test
    @DisplayName("XML visitor should generate valid XML for all device types")
    void shouldGenerateXmlForAllDeviceTypes() {
        XmlReportVisitor xmlVisitor = new XmlReportVisitor();
        
        String routerXml = router.accept(xmlVisitor);
        assertThat(routerXml).contains("<router id='R001'>")
                            .contains("<cpu_utilization>75.0</cpu_utilization>")
                            .contains("</router>");
        
        String switchXml = switchDevice.accept(xmlVisitor);
        assertThat(switchXml).contains("<switch id='SW001'>")
                            .contains("<mac_table_size>800</mac_table_size>")
                            .contains("</switch>");
        
        String firewallXml = firewall.accept(xmlVisitor);
        assertThat(firewallXml).contains("<firewall id='FW001'>")
                              .contains("<threats_blocked>50</threats_blocked>")
                              .contains("</firewall>");
        
        String loadBalancerXml = loadBalancer.accept(xmlVisitor);
        assertThat(loadBalancerXml).contains("<load_balancer id='LB001'>")
                                  .contains("<response_time>250.0</response_time>")
                                  .contains("</load_balancer>");
    }
    
    @Test
    @DisplayName("JSON visitor should generate valid JSON for all device types")
    void shouldGenerateJsonForAllDeviceTypes() {
        JsonReportVisitor jsonVisitor = new JsonReportVisitor();
        
        String routerJson = router.accept(jsonVisitor);
        assertThat(routerJson).contains("\"type\": \"router\"")
                             .contains("\"id\": \"R001\"")
                             .contains("\"cpu_utilization\": 75.0");
        
        String switchJson = switchDevice.accept(jsonVisitor);
        assertThat(switchJson).contains("\"type\": \"switch\"")
                             .contains("\"id\": \"SW001\"")
                             .contains("\"mac_table_size\": 800");
        
        String firewallJson = firewall.accept(jsonVisitor);
        assertThat(firewallJson).contains("\"type\": \"firewall\"")
                               .contains("\"id\": \"FW001\"")
                               .contains("\"threats_blocked\": 50");
        
        String loadBalancerJson = loadBalancer.accept(jsonVisitor);
        assertThat(loadBalancerJson).contains("\"type\": \"load_balancer\"")
                                   .contains("\"id\": \"LB001\"")
                                   .contains("\"response_time\": 250.0");
    }
    
    @Test
    @DisplayName("CSV visitor should generate correct CSV format")
    void shouldGenerateCsvForAllDeviceTypes() {
        CsvReportVisitor csvVisitor = new CsvReportVisitor();
        
        String routerCsv = router.accept(csvVisitor);
        assertThat(routerCsv).isEqualTo("R001,ROUTER,ACTIVE,CPU:75.0% Routes:1000");
        
        String switchCsv = switchDevice.accept(csvVisitor);
        assertThat(switchCsv).contains("SW001,SWITCH,ACTIVE")
                            .contains("Ports:24")
                            .contains("VLANs:4");
        
        String firewallCsv = firewall.accept(csvVisitor);
        assertThat(firewallCsv).contains("FW001,FIREWALL,ACTIVE")
                              .contains("Threats:50")
                              .contains("Rules:");
        
        String loadBalancerCsv = loadBalancer.accept(csvVisitor);
        assertThat(loadBalancerCsv).contains("LB001,LOAD_BALANCER,ACTIVE")
                                  .contains("RPS:1500.0")
                                  .contains("RT:250.0ms");
    }
    
    @Test
    @DisplayName("Performance analysis visitor should calculate metrics correctly")
    void shouldCalculatePerformanceMetricsCorrectly() {
        PerformanceAnalysisVisitor performanceVisitor = new PerformanceAnalysisVisitor();
        
        // Test router performance analysis
        Map<String, Object> routerMetrics = router.accept(performanceVisitor);
        assertThat(routerMetrics).containsKey("cpu_utilization")
                                .containsKey("routing_table_size")
                                .containsKey("performance_score")
                                .containsKey("route_count")
                                .containsKey("bgp_peer_count");
        
        assertThat(routerMetrics.get("cpu_utilization")).isEqualTo(75.0);
        assertThat(routerMetrics.get("performance_score")).isEqualTo(25.0); // 100 - 75
        
        // Test switch performance analysis
        Map<String, Object> switchMetrics = switchDevice.accept(performanceVisitor);
        assertThat(switchMetrics).containsKey("mac_table_size")
                                .containsKey("active_ports")
                                .containsKey("port_utilization")
                                .containsKey("performance_score");
        
        // Test firewall performance analysis
        Map<String, Object> firewallMetrics = firewall.accept(performanceVisitor);
        assertThat(firewallMetrics).containsKey("packets_processed")
                                  .containsKey("threats_blocked")
                                  .containsKey("threat_ratio")
                                  .containsKey("performance_score");
        
        // Test load balancer performance analysis
        Map<String, Object> lbMetrics = loadBalancer.accept(performanceVisitor);
        assertThat(lbMetrics).containsKey("requests_per_second")
                            .containsKey("response_time")
                            .containsKey("server_health_ratio")
                            .containsKey("performance_score");
    }
    
    @Test
    @DisplayName("Health check visitor should identify device health correctly")
    void shouldIdentifyDeviceHealthCorrectly() {
        HealthCheckVisitor healthVisitor = new HealthCheckVisitor();
        
        // Router with high CPU should be in warning/critical state
        String routerHealth = router.accept(healthVisitor);
        assertThat(routerHealth).contains("R001")
                               .contains("CPU: 75.0%");
        
        // Check if it identifies as warning or critical
        assertThat(routerHealth).containsAnyOf("WARNING", "CRITICAL");
        
        // Switch health check
        String switchHealth = switchDevice.accept(healthVisitor);
        assertThat(switchHealth).contains("SW001")
                               .contains("Port utilization:");
        
        // Firewall health check - high threats should be concerning
        String firewallHealth = firewall.accept(healthVisitor);
        assertThat(firewallHealth).contains("FW001")
                                 .contains("Threat ratio:");
        
        // Load balancer health check - high response time should be warning/critical
        String lbHealth = loadBalancer.accept(healthVisitor);
        assertThat(lbHealth).contains("LB001")
                           .contains("servers healthy");
    }
    
    @Test
    @DisplayName("Composite visitor should combine multiple analyses")
    void shouldCombineMultipleAnalyses() {
        CompositeAnalysisVisitor compositeVisitor = new CompositeAnalysisVisitor();
        
        Map<String, Object> routerComposite = router.accept(compositeVisitor);
        
        // Should contain performance metrics
        assertThat(routerComposite).containsKey("cpu_utilization")
                                  .containsKey("performance_score");
        
        // Should contain health status
        assertThat(routerComposite).containsKey("health_status");
        String healthStatus = (String) routerComposite.get("health_status");
        assertThat(healthStatus).contains("R001");
        
        // Should contain metadata
        assertThat(routerComposite).containsKey("device_type")
                                  .containsKey("timestamp");
        
        assertThat(routerComposite.get("device_type")).isEqualTo("ROUTER");
    }
    
    @Test
    @DisplayName("Report generator should create complete reports using visitors")
    void shouldGenerateCompleteReports() {
        // Test XML report generation
        String xmlReport = reportGenerator.generateXmlReport(testDevices);
        assertThat(xmlReport).contains("<?xml version='1.0'")
                            .contains("<network_report>")
                            .contains("<router id='R001'>")
                            .contains("<switch id='SW001'>")
                            .contains("<firewall id='FW001'>")
                            .contains("<load_balancer id='LB001'>")
                            .contains("</network_report>");
        
        // Test JSON report generation
        String jsonReport = reportGenerator.generateJsonReport(testDevices);
        assertThat(jsonReport).contains("\"timestamp\":")
                             .contains("\"devices\":")
                             .contains("\"type\": \"router\"")
                             .contains("\"type\": \"switch\"")
                             .contains("\"type\": \"firewall\"")
                             .contains("\"type\": \"load_balancer\"");
        
        // Test CSV report generation
        String csvReport = reportGenerator.generateCsvReport(testDevices);
        String[] csvLines = csvReport.split("\\n");
        assertThat(csvLines).hasSize(5); // Header + 4 devices
        assertThat(csvLines[0]).isEqualTo("Device ID,Type,Status,Specific Data");
        assertThat(csvLines[1]).startsWith("R001,ROUTER");
        assertThat(csvLines[2]).startsWith("SW001,SWITCH");
        assertThat(csvLines[3]).startsWith("FW001,FIREWALL");
        assertThat(csvLines[4]).startsWith("LB001,LOAD_BALANCER");
    }
    
    @Test
    @DisplayName("Performance analysis should handle all device types")
    void shouldGeneratePerformanceAnalysisForAllDevices() {
        Map<String, Map<String, Object>> analysis = reportGenerator.generatePerformanceAnalysis(testDevices);
        
        assertThat(analysis).hasSize(4)
                           .containsKeys("R001", "SW001", "FW001", "LB001");
        
        // Each device should have performance metrics
        analysis.values().forEach(metrics -> {
            assertThat(metrics).containsKey("performance_score");
        });
        
        // Router-specific metrics
        assertThat(analysis.get("R001")).containsKey("cpu_utilization")
                                       .containsKey("routing_table_size");
        
        // Switch-specific metrics
        assertThat(analysis.get("SW001")).containsKey("mac_table_size")
                                        .containsKey("port_utilization");
        
        // Firewall-specific metrics
        assertThat(analysis.get("FW001")).containsKey("packets_processed")
                                        .containsKey("threats_blocked");
        
        // Load balancer-specific metrics
        assertThat(analysis.get("LB001")).containsKey("requests_per_second")
                                        .containsKey("server_health_ratio");
    }
    
    @Test
    @DisplayName("Health report should identify critical devices")
    void shouldIdentifyCriticalDevices() {
        List<String> healthResults = reportGenerator.generateHealthReport(testDevices);
        
        assertThat(healthResults).hasSize(4);
        
        // Check that each device is represented
        assertThat(healthResults).anyMatch(result -> result.contains("R001"))
                                .anyMatch(result -> result.contains("SW001"))
                                .anyMatch(result -> result.contains("FW001"))
                                .anyMatch(result -> result.contains("LB001"));
        
        // High CPU router should be flagged
        assertThat(healthResults).anyMatch(result -> 
            result.contains("R001") && (result.contains("WARNING") || result.contains("CRITICAL")));
    }
    
    @Test
    @DisplayName("Composite analysis should provide comprehensive device insights")
    void shouldProvideComprehensiveDeviceInsights() {
        Map<String, Map<String, Object>> compositeAnalysis = reportGenerator.generateCompositeAnalysis(testDevices);
        
        assertThat(compositeAnalysis).hasSize(4);
        
        // Each device should have comprehensive analysis
        compositeAnalysis.values().forEach(analysis -> {
            assertThat(analysis).containsKey("health_status")
                               .containsKey("performance_score")
                               .containsKey("device_type")
                               .containsKey("timestamp");
        });
        
        // Verify device types are correct
        assertThat(compositeAnalysis.get("R001").get("device_type")).isEqualTo("ROUTER");
        assertThat(compositeAnalysis.get("SW001").get("device_type")).isEqualTo("SWITCH");
        assertThat(compositeAnalysis.get("FW001").get("device_type")).isEqualTo("FIREWALL");
        assertThat(compositeAnalysis.get("LB001").get("device_type")).isEqualTo("LOAD_BALANCER");
    }
    
    @Test
    @DisplayName("Adding new visitor should not require device class changes")
    void shouldSupportNewVisitorWithoutDeviceChanges() {
        // Create a custom visitor for security audit
        NetworkDeviceVisitor<String> securityAuditVisitor = new NetworkDeviceVisitor<String>() {
            @Override
            public String visitRouter(Router router) {
                return "Router " + router.getId() + ": BGP peers = " + router.getBgpPeers().size();
            }
            
            @Override
            public String visitSwitch(Switch switchDevice) {
                return "Switch " + switchDevice.getId() + ": VLANs = " + switchDevice.getVlans().size();
            }
            
            @Override
            public String visitFirewallDevice(FirewallDevice firewall) {
                return "Firewall " + firewall.getId() + ": Security rules = " + firewall.getSecurityRules().size();
            }
            
            @Override
            public String visitLoadBalancer(LoadBalancer lb) {
                return "LoadBalancer " + lb.getId() + ": Backend servers = " + lb.getBackendServers().size();
            }
        };
        
        // Test the new visitor works with all devices
        String routerAudit = router.accept(securityAuditVisitor);
        assertThat(routerAudit).contains("Router R001: BGP peers = 3");
        
        String switchAudit = switchDevice.accept(securityAuditVisitor);
        assertThat(switchAudit).contains("Switch SW001: VLANs = 4");
        
        String firewallAudit = firewall.accept(securityAuditVisitor);
        assertThat(firewallAudit).contains("Firewall FW001: Security rules = 3");
        
        String lbAudit = loadBalancer.accept(securityAuditVisitor);
        assertThat(lbAudit).contains("LoadBalancer LB001: Backend servers = 3");
    }
}