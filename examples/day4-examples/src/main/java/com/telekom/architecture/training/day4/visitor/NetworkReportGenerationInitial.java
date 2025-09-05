package com.telekom.architecture.training.day4.visitor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * INITIAL IMPLEMENTATION - Type-Casting Horror Anti-Pattern
 * 
 * Problems demonstrated:
 * - Massive instanceof chains for different device types
 * - Report generation logic scattered across multiple methods
 * - Adding new device types requires changes in every report format
 * - No type safety - easy to miss device types
 * - Complex nested conditionals for device type × report format combinations
 * - Violation of Open/Closed Principle - not open for extension
 * 
 * Business Impact:
 * - New device types require extensive code changes across all reporting
 * - Report formats are brittle and error-prone
 * - Performance suffers from repeated type checking
 * - Maintenance nightmare grows exponentially with device types
 * 
 * Trainer Notes:
 * - Show exponential complexity growth with device types × report formats
 * - Demonstrate how easy it is to miss cases
 * - Point out performance impact of repeated instanceof checks
 */
public class NetworkReportGenerationInitial {
    
    // Base network device class
    public static abstract class NetworkDevice {
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
    
    // Specific device types
    public static class Router extends NetworkDevice {
        private final List<String> routes = new ArrayList<>();
        private final List<String> bgpPeers = new ArrayList<>();
        private int routingTableSize = 1000;
        private double cpuUtilization = 25.0;
        
        public Router(String id) {
            super(id, "ROUTER");
            // Initialize with sample data
            routes.addAll(Arrays.asList("192.168.1.0/24", "10.0.0.0/8", "172.16.0.0/12"));
            bgpPeers.addAll(Arrays.asList("AS65001", "AS65002", "AS65003"));
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
            // Initialize with sample data
            for (int i = 1; i <= 24; i++) {
                ports.add(new SwitchPort(i, i <= 20 ? "ACTIVE" : "INACTIVE"));
            }
            vlans.addAll(Arrays.asList("VLAN_10", "VLAN_20", "VLAN_30", "VLAN_100"));
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
            // Initialize with sample data
            securityRules.add(new SecurityRule("ALLOW", "HTTP", "80"));
            securityRules.add(new SecurityRule("ALLOW", "HTTPS", "443"));
            securityRules.add(new SecurityRule("BLOCK", "TELNET", "23"));
            blockedIps.addAll(Arrays.asList("192.168.1.100", "10.0.0.50"));
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
        private double responseTime = 150.0; // milliseconds
        
        public LoadBalancer(String id) {
            super(id, "LOAD_BALANCER");
            // Initialize with sample data
            backendServers.add(new BackendServer("server1", "HEALTHY", 45.0));
            backendServers.add(new BackendServer("server2", "HEALTHY", 52.0));
            backendServers.add(new BackendServer("server3", "UNHEALTHY", 0.0));
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
    
    // HORRIFIC: Report generation with massive instanceof chains
    public static class NetworkReportGenerator {
        
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // NIGHTMARE: XML Report Generation
        public String generateXmlReport(List<NetworkDevice> devices) {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version='1.0' encoding='UTF-8'?>\n");
            xml.append("<network_report>\n");
            xml.append("  <timestamp>").append(dateFormatter.format(LocalDateTime.now())).append("</timestamp>\n");
            xml.append("  <devices>\n");
            
            for (NetworkDevice device : devices) {
                // HORROR: Massive instanceof chain for each device type
                if (device instanceof Router) {
                    Router router = (Router) device; // Unsafe cast!
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
                    
                } else if (device instanceof Switch) {
                    Switch switchDevice = (Switch) device; // Another unsafe cast!
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
                    
                } else if (device instanceof FirewallDevice) {
                    FirewallDevice firewall = (FirewallDevice) device; // Yet another cast!
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
                    
                } else if (device instanceof LoadBalancer) {
                    LoadBalancer lb = (LoadBalancer) device; // More casting horror!
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
                    
                } else {
                    // DISASTER: What if we add a new device type and forget this?
                    xml.append("    <!-- UNKNOWN DEVICE TYPE: ").append(device.getClass().getSimpleName()).append(" -->\n");
                }
            }
            
            xml.append("  </devices>\n");
            xml.append("</network_report>\n");
            return xml.toString();
        }
        
        // NIGHTMARE CONTINUES: JSON Report Generation - DUPLICATE LOGIC!
        public String generateJsonReport(List<NetworkDevice> devices) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": \"").append(dateFormatter.format(LocalDateTime.now())).append("\",\n");
            json.append("  \"devices\": [\n");
            
            for (int i = 0; i < devices.size(); i++) {
                NetworkDevice device = devices.get(i);
                
                // HORROR REPEATS: Same instanceof nightmare, different format!
                if (device instanceof Router) {
                    Router router = (Router) device;
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
                    
                } else if (device instanceof Switch) {
                    Switch switchDevice = (Switch) device;
                    json.append("    {\n");
                    json.append("      \"type\": \"switch\",\n");
                    json.append("      \"id\": \"").append(switchDevice.getId()).append("\",\n");
                    json.append("      \"status\": \"").append(switchDevice.getStatus()).append("\",\n");
                    json.append("      \"mac_table_size\": ").append(switchDevice.getMacTableSize()).append(",\n");
                    json.append("      \"port_count\": ").append(switchDevice.getPorts().size()).append("\n");
                    json.append("    }");
                    
                } else if (device instanceof FirewallDevice) {
                    FirewallDevice firewall = (FirewallDevice) device;
                    json.append("    {\n");
                    json.append("      \"type\": \"firewall\",\n");
                    json.append("      \"id\": \"").append(firewall.getId()).append("\",\n");
                    json.append("      \"status\": \"").append(firewall.getStatus()).append("\",\n");
                    json.append("      \"packets_processed\": ").append(firewall.getPacketsProcessed()).append(",\n");
                    json.append("      \"threats_blocked\": ").append(firewall.getThreatsBlocked()).append("\n");
                    json.append("    }");
                    
                } else if (device instanceof LoadBalancer) {
                    LoadBalancer lb = (LoadBalancer) device;
                    json.append("    {\n");
                    json.append("      \"type\": \"load_balancer\",\n");
                    json.append("      \"id\": \"").append(lb.getId()).append("\",\n");
                    json.append("      \"status\": \"").append(lb.getStatus()).append("\",\n");
                    json.append("      \"requests_per_second\": ").append(lb.getTotalRequestsPerSecond()).append(",\n");
                    json.append("      \"response_time\": ").append(lb.getResponseTime()).append("\n");
                    json.append("    }");
                    
                } else {
                    // DISASTER REPEATS: Missing device types silently ignored
                    json.append("    { \"type\": \"unknown\", \"id\": \"").append(device.getId()).append("\" }");
                }
                
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
        
        // NIGHTMARE MULTIPLIES: CSV Report - TRIPLE DUPLICATE LOGIC!
        public String generateCsvReport(List<NetworkDevice> devices) {
            StringBuilder csv = new StringBuilder();
            csv.append("Device ID,Type,Status,Specific Data\n");
            
            for (NetworkDevice device : devices) {
                // SAME HORROR, DIFFERENT FORMAT - NO END IN SIGHT!
                if (device instanceof Router) {
                    Router router = (Router) device;
                    csv.append(router.getId()).append(",ROUTER,").append(router.getStatus()).append(",")
                       .append("CPU:").append(router.getCpuUtilization()).append("%")
                       .append(" Routes:").append(router.getRoutingTableSize()).append("\n");
                       
                } else if (device instanceof Switch) {
                    Switch switchDevice = (Switch) device;
                    csv.append(switchDevice.getId()).append(",SWITCH,").append(switchDevice.getStatus()).append(",")
                       .append("Ports:").append(switchDevice.getPorts().size())
                       .append(" VLANs:").append(switchDevice.getVlans().size()).append("\n");
                       
                } else if (device instanceof FirewallDevice) {
                    FirewallDevice firewall = (FirewallDevice) device;
                    csv.append(firewall.getId()).append(",FIREWALL,").append(firewall.getStatus()).append(",")
                       .append("Threats:").append(firewall.getThreatsBlocked())
                       .append(" Rules:").append(firewall.getSecurityRules().size()).append("\n");
                       
                } else if (device instanceof LoadBalancer) {
                    LoadBalancer lb = (LoadBalancer) device;
                    csv.append(lb.getId()).append(",LOAD_BALANCER,").append(lb.getStatus()).append(",")
                       .append("RPS:").append(lb.getTotalRequestsPerSecond())
                       .append(" RT:").append(lb.getResponseTime()).append("ms\n");
                       
                } else {
                    csv.append(device.getId()).append(",UNKNOWN,").append(device.getStatus()).append(",N/A\n");
                }
            }
            
            return csv.toString();
        }
        
        // FINAL NIGHTMARE: Performance analysis - QUADRUPLE DUPLICATE LOGIC!
        public Map<String, Object> generatePerformanceAnalysis(List<NetworkDevice> devices) {
            Map<String, Object> analysis = new HashMap<>();
            
            for (NetworkDevice device : devices) {
                // THE HORROR CONTINUES WITH PERFORMANCE METRICS...
                if (device instanceof Router) {
                    Router router = (Router) device;
                    analysis.put(router.getId() + "_cpu", router.getCpuUtilization());
                    analysis.put(router.getId() + "_routes", router.getRoutingTableSize());
                    analysis.put(router.getId() + "_performance_score", calculateRouterPerformance(router));
                    
                } else if (device instanceof Switch) {
                    Switch switchDevice = (Switch) device;
                    analysis.put(switchDevice.getId() + "_mac_table", switchDevice.getMacTableSize());
                    analysis.put(switchDevice.getId() + "_active_ports", 
                        switchDevice.getPorts().stream().mapToLong(p -> "ACTIVE".equals(p.getStatus()) ? 1 : 0).sum());
                    analysis.put(switchDevice.getId() + "_performance_score", calculateSwitchPerformance(switchDevice));
                    
                } else if (device instanceof FirewallDevice) {
                    FirewallDevice firewall = (FirewallDevice) device;
                    analysis.put(firewall.getId() + "_packets", firewall.getPacketsProcessed());
                    analysis.put(firewall.getId() + "_threats", firewall.getThreatsBlocked());
                    analysis.put(firewall.getId() + "_performance_score", calculateFirewallPerformance(firewall));
                    
                } else if (device instanceof LoadBalancer) {
                    LoadBalancer lb = (LoadBalancer) device;
                    analysis.put(lb.getId() + "_rps", lb.getTotalRequestsPerSecond());
                    analysis.put(lb.getId() + "_response_time", lb.getResponseTime());
                    analysis.put(lb.getId() + "_performance_score", calculateLoadBalancerPerformance(lb));
                }
                // What about new device types? They're silently ignored again!
            }
            
            return analysis;
        }
        
        // Performance calculation methods (also duplicated logic...)
        private double calculateRouterPerformance(Router router) {
            return 100.0 - router.getCpuUtilization(); // Simple performance metric
        }
        
        private double calculateSwitchPerformance(Switch switchDevice) {
            long activePorts = switchDevice.getPorts().stream().mapToLong(p -> "ACTIVE".equals(p.getStatus()) ? 1 : 0).sum();
            return (double) activePorts / switchDevice.getPorts().size() * 100;
        }
        
        private double calculateFirewallPerformance(FirewallDevice firewall) {
            return Math.max(0, 100.0 - (firewall.getThreatsBlocked() * 2.0)); // Lower is better for threats
        }
        
        private double calculateLoadBalancerPerformance(LoadBalancer lb) {
            return Math.max(0, 100.0 - (lb.getResponseTime() / 10.0)); // Lower response time is better
        }
    }
}