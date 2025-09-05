package com.telekom.architecture.training.day4.visitor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FIXED IMPLEMENTATION using Visitor Pattern
 * 
 * Benefits:
 * - No instanceof chains - type-safe operations
 * - Easy to add new operations without modifying device classes
 * - Clean separation between data structure and operations
 * - Follows Open/Closed Principle - open for extension
 * - Single Responsibility Principle - each visitor has one job
 * - Performance improvement - no runtime type checking
 * 
 * Enterprise Features:
 * - Extensible visitor hierarchy for different report types
 * - Composite visitors for complex operations
 * - Thread-safe visitor implementations
 * - Error handling and validation in visitors
 * 
 * Trainer Notes:
 * - Show how adding new operations is trivial
 * - Demonstrate type safety benefits
 * - Point out performance improvements
 * - Show clean separation of concerns
 */
public class NetworkReportGenerationFixed {
    
    // Visitor interface - defines operations that can be performed on network devices
    public interface NetworkDeviceVisitor<T> {
        T visitRouter(Router router);
        T visitSwitch(Switch switchDevice);  
        T visitFirewallDevice(FirewallDevice firewall);
        T visitLoadBalancer(LoadBalancer loadBalancer);
    }
    
    // Visitable interface - allows devices to accept visitors
    public interface NetworkDeviceVisitable {
        <T> T accept(NetworkDeviceVisitor<T> visitor);
    }
    
    // Base network device class
    public static abstract class NetworkDevice implements NetworkDeviceVisitable {
        protected final String id;
        protected final String type;
        protected String status = "ACTIVE";
        protected LocalDateTime lastUpdate = LocalDateTime.now();
        
        public NetworkDevice(String id, String type) {
            this.id = id;
            this.type = type;
        }
        
        public String getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { 
            this.status = status;
            this.lastUpdate = LocalDateTime.now();
        }
        public LocalDateTime getLastUpdate() { return lastUpdate; }
    }
    
    // Specific device implementations
    public static class Router extends NetworkDevice {
        private final List<String> routes = new ArrayList<>();
        private final List<String> bgpPeers = new ArrayList<>();
        private int routingTableSize = 1000;
        private double cpuUtilization = 25.0;
        
        public Router(String id) {
            super(id, "ROUTER");
            routes.addAll(Arrays.asList("192.168.1.0/24", "10.0.0.0/8", "172.16.0.0/12"));
            bgpPeers.addAll(Arrays.asList("AS65001", "AS65002", "AS65003"));
        }
        
        @Override
        public <T> T accept(NetworkDeviceVisitor<T> visitor) {
            return visitor.visitRouter(this);
        }
        
        public List<String> getRoutes() { return new ArrayList<>(routes); }
        public List<String> getBgpPeers() { return new ArrayList<>(bgpPeers); }
        public int getRoutingTableSize() { return routingTableSize; }
        public double getCpuUtilization() { return cpuUtilization; }
        public void setCpuUtilization(double cpu) { this.cpuUtilization = cpu; }
    }
    
    public static class Switch extends NetworkDevice {
        private final List<SwitchPort> ports = new ArrayList<>();
        private final List<String> vlans = new ArrayList<>();
        private int macTableSize = 500;
        
        public Switch(String id) {
            super(id, "SWITCH");
            for (int i = 1; i <= 24; i++) {
                ports.add(new SwitchPort(i, i <= 20 ? "ACTIVE" : "INACTIVE"));
            }
            vlans.addAll(Arrays.asList("VLAN_10", "VLAN_20", "VLAN_30", "VLAN_100"));
        }
        
        @Override
        public <T> T accept(NetworkDeviceVisitor<T> visitor) {
            return visitor.visitSwitch(this);
        }
        
        public List<SwitchPort> getPorts() { return new ArrayList<>(ports); }
        public List<String> getVlans() { return new ArrayList<>(vlans); }
        public int getMacTableSize() { return macTableSize; }
        public void setMacTableSize(int size) { this.macTableSize = size; }
    }
    
    public static class SwitchPort {
        private final int number;
        private final String status;
        
        public SwitchPort(int number, String status) {
            this.number = number;
            this.status = status;
        }
        
        public int getNumber() { return number; }
        public String getStatus() { return status; }
    }
    
    public static class FirewallDevice extends NetworkDevice {
        private final List<SecurityRule> securityRules = new ArrayList<>();
        private final List<String> blockedIps = new ArrayList<>();
        private long packetsProcessed = 150000;
        private int threatsBlocked = 25;
        
        public FirewallDevice(String id) {
            super(id, "FIREWALL");
            securityRules.add(new SecurityRule("ALLOW", "HTTP", "80"));
            securityRules.add(new SecurityRule("ALLOW", "HTTPS", "443"));
            securityRules.add(new SecurityRule("BLOCK", "TELNET", "23"));
            blockedIps.addAll(Arrays.asList("192.168.1.100", "10.0.0.50"));
        }
        
        @Override
        public <T> T accept(NetworkDeviceVisitor<T> visitor) {
            return visitor.visitFirewallDevice(this);
        }
        
        public List<SecurityRule> getSecurityRules() { return new ArrayList<>(securityRules); }
        public List<String> getBlockedIps() { return new ArrayList<>(blockedIps); }
        public long getPacketsProcessed() { return packetsProcessed; }
        public int getThreatsBlocked() { return threatsBlocked; }
        public void setThreatsBlocked(int threats) { this.threatsBlocked = threats; }
    }
    
    public static class SecurityRule {
        private final String action;
        private final String protocol;
        private final String port;
        
        public SecurityRule(String action, String protocol, String port) {
            this.action = action;
            this.protocol = protocol;
            this.port = port;
        }
        
        public String getAction() { return action; }
        public String getProtocol() { return protocol; }
        public String getPort() { return port; }
    }
    
    public static class LoadBalancer extends NetworkDevice {
        private final List<BackendServer> backendServers = new ArrayList<>();
        private double totalRequestsPerSecond = 1500.0;
        private double responseTime = 150.0;
        
        public LoadBalancer(String id) {
            super(id, "LOAD_BALANCER");
            backendServers.add(new BackendServer("server1", "HEALTHY", 45.0));
            backendServers.add(new BackendServer("server2", "HEALTHY", 52.0));
            backendServers.add(new BackendServer("server3", "UNHEALTHY", 0.0));
        }
        
        @Override
        public <T> T accept(NetworkDeviceVisitor<T> visitor) {
            return visitor.visitLoadBalancer(this);
        }
        
        public List<BackendServer> getBackendServers() { return new ArrayList<>(backendServers); }
        public double getTotalRequestsPerSecond() { return totalRequestsPerSecond; }
        public double getResponseTime() { return responseTime; }
        public void setResponseTime(double time) { this.responseTime = time; }
    }
    
    public static class BackendServer {
        private final String name;
        private final String status;
        private final double loadPercentage;
        
        public BackendServer(String name, String status, double loadPercentage) {
            this.name = name;
            this.status = status;
            this.loadPercentage = loadPercentage;
        }
        
        public String getName() { return name; }
        public String getStatus() { return status; }
        public double getLoadPercentage() { return loadPercentage; }
    }
    
    // Abstract base visitor for common functionality
    public static abstract class BaseNetworkDeviceVisitor<T> implements NetworkDeviceVisitor<T> {
        protected final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        protected String formatTimestamp() {
            return dateFormatter.format(LocalDateTime.now());
        }
    }
    
    // Concrete Visitor Implementations
    
    // XML Report Visitor
    public static class XmlReportVisitor extends BaseNetworkDeviceVisitor<String> {
        
        @Override
        public String visitRouter(Router router) {
            StringBuilder xml = new StringBuilder();
            xml.append("    <router id='").append(router.getId()).append("'>\n");
            xml.append("      <status>").append(router.getStatus()).append("</status>\n");
            xml.append("      <cpu_utilization>").append(router.getCpuUtilization()).append("</cpu_utilization>\n");
            xml.append("      <routing_table_size>").append(router.getRoutingTableSize()).append("</routing_table_size>\n");
            xml.append("      <routes>\n");
            for (String route : router.getRoutes()) {
                xml.append("        <route>").append(route).append("</route>\n");
            }
            xml.append("      </routes>\n");
            xml.append("      <bgp_peers>\n");
            for (String peer : router.getBgpPeers()) {
                xml.append("        <peer>").append(peer).append("</peer>\n");
            }
            xml.append("      </bgp_peers>\n");
            xml.append("    </router>\n");
            return xml.toString();
        }
        
        @Override
        public String visitSwitch(Switch switchDevice) {
            StringBuilder xml = new StringBuilder();
            xml.append("    <switch id='").append(switchDevice.getId()).append("'>\n");
            xml.append("      <status>").append(switchDevice.getStatus()).append("</status>\n");
            xml.append("      <mac_table_size>").append(switchDevice.getMacTableSize()).append("</mac_table_size>\n");
            xml.append("      <ports>\n");
            for (SwitchPort port : switchDevice.getPorts()) {
                xml.append("        <port number='").append(port.getNumber()).append("' status='").append(port.getStatus()).append("'/>\n");
            }
            xml.append("      </ports>\n");
            xml.append("      <vlans>\n");
            for (String vlan : switchDevice.getVlans()) {
                xml.append("        <vlan>").append(vlan).append("</vlan>\n");
            }
            xml.append("      </vlans>\n");
            xml.append("    </switch>\n");
            return xml.toString();
        }
        
        @Override
        public String visitFirewallDevice(FirewallDevice firewall) {
            StringBuilder xml = new StringBuilder();
            xml.append("    <firewall id='").append(firewall.getId()).append("'>\n");
            xml.append("      <status>").append(firewall.getStatus()).append("</status>\n");
            xml.append("      <packets_processed>").append(firewall.getPacketsProcessed()).append("</packets_processed>\n");
            xml.append("      <threats_blocked>").append(firewall.getThreatsBlocked()).append("</threats_blocked>\n");
            xml.append("      <security_rules>\n");
            for (SecurityRule rule : firewall.getSecurityRules()) {
                xml.append("        <rule action='").append(rule.getAction()).append("' protocol='").append(rule.getProtocol()).append("' port='").append(rule.getPort()).append("'/>\n");
            }
            xml.append("      </security_rules>\n");
            xml.append("    </firewall>\n");
            return xml.toString();
        }
        
        @Override
        public String visitLoadBalancer(LoadBalancer lb) {
            StringBuilder xml = new StringBuilder();
            xml.append("    <load_balancer id='").append(lb.getId()).append("'>\n");
            xml.append("      <status>").append(lb.getStatus()).append("</status>\n");
            xml.append("      <requests_per_second>").append(lb.getTotalRequestsPerSecond()).append("</requests_per_second>\n");
            xml.append("      <response_time>").append(lb.getResponseTime()).append("</response_time>\n");
            xml.append("      <backend_servers>\n");
            for (BackendServer server : lb.getBackendServers()) {
                xml.append("        <server name='").append(server.getName()).append("' status='").append(server.getStatus()).append("' load='").append(server.getLoadPercentage()).append("'/>\n");
            }
            xml.append("      </backend_servers>\n");
            xml.append("    </load_balancer>\n");
            return xml.toString();
        }
    }
    
    // JSON Report Visitor
    public static class JsonReportVisitor extends BaseNetworkDeviceVisitor<String> {
        
        @Override
        public String visitRouter(Router router) {
            StringBuilder json = new StringBuilder();
            json.append("    {\n");
            json.append("      \"type\": \"router\",\n");
            json.append("      \"id\": \"").append(router.getId()).append("\",\n");
            json.append("      \"status\": \"").append(router.getStatus()).append("\",\n");
            json.append("      \"cpu_utilization\": ").append(router.getCpuUtilization()).append(",\n");
            json.append("      \"routing_table_size\": ").append(router.getRoutingTableSize()).append(",\n");
            json.append("      \"routes\": [");
            List<String> routes = router.getRoutes();
            for (int j = 0; j < routes.size(); j++) {
                json.append("\"").append(routes.get(j)).append("\"");
                if (j < routes.size() - 1) json.append(", ");
            }
            json.append("]\n");
            json.append("    }");
            return json.toString();
        }
        
        @Override
        public String visitSwitch(Switch switchDevice) {
            return String.format(
                "    {\n" +
                "      \"type\": \"switch\",\n" +
                "      \"id\": \"%s\",\n" +
                "      \"status\": \"%s\",\n" +
                "      \"mac_table_size\": %d,\n" +
                "      \"port_count\": %d\n" +
                "    }",
                switchDevice.getId(),
                switchDevice.getStatus(),
                switchDevice.getMacTableSize(),
                switchDevice.getPorts().size()
            );
        }
        
        @Override
        public String visitFirewallDevice(FirewallDevice firewall) {
            return String.format(
                "    {\n" +
                "      \"type\": \"firewall\",\n" +
                "      \"id\": \"%s\",\n" +
                "      \"status\": \"%s\",\n" +
                "      \"packets_processed\": %d,\n" +
                "      \"threats_blocked\": %d\n" +
                "    }",
                firewall.getId(),
                firewall.getStatus(),
                firewall.getPacketsProcessed(),
                firewall.getThreatsBlocked()
            );
        }
        
        @Override
        public String visitLoadBalancer(LoadBalancer lb) {
            return String.format(
                "    {\n" +
                "      \"type\": \"load_balancer\",\n" +
                "      \"id\": \"%s\",\n" +
                "      \"status\": \"%s\",\n" +
                "      \"requests_per_second\": %.1f,\n" +
                "      \"response_time\": %.1f\n" +
                "    }",
                lb.getId(),
                lb.getStatus(),
                lb.getTotalRequestsPerSecond(),
                lb.getResponseTime()
            );
        }
    }
    
    // CSV Report Visitor
    public static class CsvReportVisitor extends BaseNetworkDeviceVisitor<String> {
        
        @Override
        public String visitRouter(Router router) {
            return String.format("%s,ROUTER,%s,CPU:%.1f%% Routes:%d",
                router.getId(),
                router.getStatus(),
                router.getCpuUtilization(),
                router.getRoutingTableSize()
            );
        }
        
        @Override
        public String visitSwitch(Switch switchDevice) {
            return String.format("%s,SWITCH,%s,Ports:%d VLANs:%d",
                switchDevice.getId(),
                switchDevice.getStatus(),
                switchDevice.getPorts().size(),
                switchDevice.getVlans().size()
            );
        }
        
        @Override
        public String visitFirewallDevice(FirewallDevice firewall) {
            return String.format("%s,FIREWALL,%s,Threats:%d Rules:%d",
                firewall.getId(),
                firewall.getStatus(),
                firewall.getThreatsBlocked(),
                firewall.getSecurityRules().size()
            );
        }
        
        @Override
        public String visitLoadBalancer(LoadBalancer lb) {
            return String.format("%s,LOAD_BALANCER,%s,RPS:%.1f RT:%.1fms",
                lb.getId(),
                lb.getStatus(),
                lb.getTotalRequestsPerSecond(),
                lb.getResponseTime()
            );
        }
    }
    
    // Performance Analysis Visitor
    public static class PerformanceAnalysisVisitor extends BaseNetworkDeviceVisitor<Map<String, Object>> {
        
        @Override
        public Map<String, Object> visitRouter(Router router) {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("cpu_utilization", router.getCpuUtilization());
            metrics.put("routing_table_size", router.getRoutingTableSize());
            metrics.put("performance_score", calculateRouterPerformance(router));
            metrics.put("route_count", router.getRoutes().size());
            metrics.put("bgp_peer_count", router.getBgpPeers().size());
            return metrics;
        }
        
        @Override
        public Map<String, Object> visitSwitch(Switch switchDevice) {
            Map<String, Object> metrics = new HashMap<>();
            long activePorts = switchDevice.getPorts().stream()
                .mapToLong(p -> "ACTIVE".equals(p.getStatus()) ? 1 : 0)
                .sum();
            
            metrics.put("mac_table_size", switchDevice.getMacTableSize());
            metrics.put("active_ports", activePorts);
            metrics.put("total_ports", switchDevice.getPorts().size());
            metrics.put("vlan_count", switchDevice.getVlans().size());
            metrics.put("performance_score", calculateSwitchPerformance(switchDevice));
            metrics.put("port_utilization", (double) activePorts / switchDevice.getPorts().size() * 100);
            return metrics;
        }
        
        @Override
        public Map<String, Object> visitFirewallDevice(FirewallDevice firewall) {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("packets_processed", firewall.getPacketsProcessed());
            metrics.put("threats_blocked", firewall.getThreatsBlocked());
            metrics.put("security_rule_count", firewall.getSecurityRules().size());
            metrics.put("blocked_ip_count", firewall.getBlockedIps().size());
            metrics.put("performance_score", calculateFirewallPerformance(firewall));
            metrics.put("threat_ratio", (double) firewall.getThreatsBlocked() / firewall.getPacketsProcessed() * 100000);
            return metrics;
        }
        
        @Override
        public Map<String, Object> visitLoadBalancer(LoadBalancer lb) {
            Map<String, Object> metrics = new HashMap<>();
            long healthyServers = lb.getBackendServers().stream()
                .mapToLong(s -> "HEALTHY".equals(s.getStatus()) ? 1 : 0)
                .sum();
            
            metrics.put("requests_per_second", lb.getTotalRequestsPerSecond());
            metrics.put("response_time", lb.getResponseTime());
            metrics.put("backend_server_count", lb.getBackendServers().size());
            metrics.put("healthy_servers", healthyServers);
            metrics.put("performance_score", calculateLoadBalancerPerformance(lb));
            metrics.put("server_health_ratio", (double) healthyServers / lb.getBackendServers().size() * 100);
            return metrics;
        }
        
        private double calculateRouterPerformance(Router router) {
            return Math.max(0, 100.0 - router.getCpuUtilization());
        }
        
        private double calculateSwitchPerformance(Switch switchDevice) {
            long activePorts = switchDevice.getPorts().stream()
                .mapToLong(p -> "ACTIVE".equals(p.getStatus()) ? 1 : 0)
                .sum();
            return (double) activePorts / switchDevice.getPorts().size() * 100;
        }
        
        private double calculateFirewallPerformance(FirewallDevice firewall) {
            return Math.max(0, 100.0 - (firewall.getThreatsBlocked() * 2.0));
        }
        
        private double calculateLoadBalancerPerformance(LoadBalancer lb) {
            return Math.max(0, 100.0 - (lb.getResponseTime() / 10.0));
        }
    }
    
    // Health Check Visitor - example of easy extensibility
    public static class HealthCheckVisitor extends BaseNetworkDeviceVisitor<String> {
        
        @Override
        public String visitRouter(Router router) {
            String health = router.getCpuUtilization() > 80 ? "CRITICAL" : 
                           router.getCpuUtilization() > 60 ? "WARNING" : "HEALTHY";
            return String.format("Router %s: %s (CPU: %.1f%%)", router.getId(), health, router.getCpuUtilization());
        }
        
        @Override
        public String visitSwitch(Switch switchDevice) {
            long activePorts = switchDevice.getPorts().stream()
                .mapToLong(p -> "ACTIVE".equals(p.getStatus()) ? 1 : 0)
                .sum();
            double utilization = (double) activePorts / switchDevice.getPorts().size() * 100;
            String health = utilization > 90 ? "CRITICAL" : utilization > 70 ? "WARNING" : "HEALTHY";
            return String.format("Switch %s: %s (Port utilization: %.1f%%)", switchDevice.getId(), health, utilization);
        }
        
        @Override
        public String visitFirewallDevice(FirewallDevice firewall) {
            double threatRatio = (double) firewall.getThreatsBlocked() / firewall.getPacketsProcessed() * 100000;
            String health = threatRatio > 100 ? "CRITICAL" : threatRatio > 50 ? "WARNING" : "HEALTHY";
            return String.format("Firewall %s: %s (Threat ratio: %.2f per 100k packets)", firewall.getId(), health, threatRatio);
        }
        
        @Override
        public String visitLoadBalancer(LoadBalancer lb) {
            long healthyServers = lb.getBackendServers().stream()
                .mapToLong(s -> "HEALTHY".equals(s.getStatus()) ? 1 : 0)
                .sum();
            String health = healthyServers == 0 ? "CRITICAL" : 
                           healthyServers == 1 ? "WARNING" : "HEALTHY";
            return String.format("LoadBalancer %s: %s (%d/%d servers healthy)", 
                lb.getId(), health, healthyServers, lb.getBackendServers().size());
        }
    }
    
    // Composite Visitor for combining multiple operations
    public static class CompositeAnalysisVisitor extends BaseNetworkDeviceVisitor<Map<String, Object>> {
        
        private final PerformanceAnalysisVisitor performanceVisitor = new PerformanceAnalysisVisitor();
        private final HealthCheckVisitor healthCheckVisitor = new HealthCheckVisitor();
        
        @Override
        public Map<String, Object> visitRouter(Router router) {
            Map<String, Object> result = performanceVisitor.visitRouter(router);
            result.put("health_status", healthCheckVisitor.visitRouter(router));
            result.put("device_type", "ROUTER");
            result.put("timestamp", formatTimestamp());
            return result;
        }
        
        @Override
        public Map<String, Object> visitSwitch(Switch switchDevice) {
            Map<String, Object> result = performanceVisitor.visitSwitch(switchDevice);
            result.put("health_status", healthCheckVisitor.visitSwitch(switchDevice));
            result.put("device_type", "SWITCH");
            result.put("timestamp", formatTimestamp());
            return result;
        }
        
        @Override
        public Map<String, Object> visitFirewallDevice(FirewallDevice firewall) {
            Map<String, Object> result = performanceVisitor.visitFirewallDevice(firewall);
            result.put("health_status", healthCheckVisitor.visitFirewallDevice(firewall));
            result.put("device_type", "FIREWALL");
            result.put("timestamp", formatTimestamp());
            return result;
        }
        
        @Override
        public Map<String, Object> visitLoadBalancer(LoadBalancer lb) {
            Map<String, Object> result = performanceVisitor.visitLoadBalancer(lb);
            result.put("health_status", healthCheckVisitor.visitLoadBalancer(lb));
            result.put("device_type", "LOAD_BALANCER");
            result.put("timestamp", formatTimestamp());
            return result;
        }
    }
    
    // Report Generator using Visitor Pattern
    public static class NetworkReportGenerator {
        
        public String generateXmlReport(List<NetworkDevice> devices) {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version='1.0' encoding='UTF-8'?>\n");
            xml.append("<network_report>\n");
            xml.append("  <timestamp>").append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())).append("</timestamp>\n");
            xml.append("  <devices>\n");
            
            XmlReportVisitor visitor = new XmlReportVisitor();
            for (NetworkDevice device : devices) {
                xml.append(device.accept(visitor)); // Clean visitor dispatch!
            }
            
            xml.append("  </devices>\n");
            xml.append("</network_report>\n");
            return xml.toString();
        }
        
        public String generateJsonReport(List<NetworkDevice> devices) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": \"").append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())).append("\",\n");
            json.append("  \"devices\": [\n");
            
            JsonReportVisitor visitor = new JsonReportVisitor();
            for (int i = 0; i < devices.size(); i++) {
                json.append(devices.get(i).accept(visitor)); // Type-safe visitor dispatch!
                if (i < devices.size() - 1) {
                    json.append(",\n");
                } else {
                    json.append("\n");
                }
            }
            
            json.append("  ]\n");
            json.append("}\n");
            return json.toString();
        }
        
        public String generateCsvReport(List<NetworkDevice> devices) {
            StringBuilder csv = new StringBuilder();
            csv.append("Device ID,Type,Status,Specific Data\n");
            
            CsvReportVisitor visitor = new CsvReportVisitor();
            for (NetworkDevice device : devices) {
                csv.append(device.accept(visitor)).append("\n"); // Clean and simple!
            }
            
            return csv.toString();
        }
        
        public Map<String, Map<String, Object>> generatePerformanceAnalysis(List<NetworkDevice> devices) {
            Map<String, Map<String, Object>> analysis = new HashMap<>();
            
            PerformanceAnalysisVisitor visitor = new PerformanceAnalysisVisitor();
            for (NetworkDevice device : devices) {
                analysis.put(device.getId(), device.accept(visitor)); // No instanceof checks!
            }
            
            return analysis;
        }
        
        public List<String> generateHealthReport(List<NetworkDevice> devices) {
            List<String> healthResults = new ArrayList<>();
            
            HealthCheckVisitor visitor = new HealthCheckVisitor();
            for (NetworkDevice device : devices) {
                healthResults.add(device.accept(visitor)); // Easy to add new health checks!
            }
            
            return healthResults;
        }
        
        public Map<String, Map<String, Object>> generateCompositeAnalysis(List<NetworkDevice> devices) {
            Map<String, Map<String, Object>> analysis = new HashMap<>();
            
            CompositeAnalysisVisitor visitor = new CompositeAnalysisVisitor();
            for (NetworkDevice device : devices) {
                analysis.put(device.getId(), device.accept(visitor)); // Combines multiple analyses!
            }
            
            return analysis;
        }
    }
}