package com.telekom.architecture.training.day4.interpreter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INITIAL IMPLEMENTATION - Configuration Parsing Horror
 * 
 * Problems demonstrated:
 * - Complex regex-based parsing that's brittle and unmaintainable
 * - No separation between parsing logic and execution logic
 * - Error handling scattered throughout parsing code
 * - Difficult to extend with new configuration commands
 * - No syntax validation until runtime
 * - Debugging nightmares with complex regular expressions
 * 
 * Business Impact:
 * - Network engineers waste hours debugging configuration syntax
 * - Simple configuration changes require complex regex modifications
 * - No validation feedback until configuration is applied
 * - Configuration errors discovered at runtime cause outages
 * - Knowledge locked in undocumented regex patterns
 * 
 * Trainer Notes:
 * - Show how regex parsing becomes unmaintainable
 * - Demonstrate how adding new commands is painful
 * - Point out the lack of proper error reporting
 * - Show debugging difficulties with complex patterns
 */
public class NetworkConfigurationDSLInitial {
    
    public static class NetworkInterface {
        private String name;
        private String ipAddress;
        private String subnet;
        private String status = "DOWN";
        private int mtu = 1500;
        
        public NetworkInterface(String name) {
            this.name = name;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getSubnet() { return subnet; }
        public void setSubnet(String subnet) { this.subnet = subnet; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getMtu() { return mtu; }
        public void setMtu(int mtu) { this.mtu = mtu; }
        
        @Override
        public String toString() {
            return String.format("%s: %s/%s [%s] MTU:%d", name, ipAddress, subnet, status, mtu);
        }
    }
    
    public static class RoutingRule {
        private String destination;
        private String gateway;
        private String interfaceName;
        private int metric = 1;
        
        public RoutingRule(String destination, String gateway) {
            this.destination = destination;
            this.gateway = gateway;
        }
        
        // Getters and setters
        public String getDestination() { return destination; }
        public String getGateway() { return gateway; }
        public String getInterfaceName() { return interfaceName; }
        public void setInterfaceName(String interfaceName) { this.interfaceName = interfaceName; }
        public int getMetric() { return metric; }
        public void setMetric(int metric) { this.metric = metric; }
        
        @Override
        public String toString() {
            return String.format("Route: %s -> %s via %s (metric: %d)", 
                destination, gateway, interfaceName != null ? interfaceName : "default", metric);
        }
    }
    
    public static class SecurityPolicy {
        private String name;
        private String action; // ALLOW, DENY, LOG
        private String protocol;
        private String sourceIp;
        private String destinationIp;
        private String port;
        
        public SecurityPolicy(String name, String action) {
            this.name = name;
            this.action = action;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public String getAction() { return action; }
        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        public String getSourceIp() { return sourceIp; }
        public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
        public String getDestinationIp() { return destinationIp; }
        public void setDestinationIp(String destinationIp) { this.destinationIp = destinationIp; }
        public String getPort() { return port; }
        public void setPort(String port) { this.port = port; }
        
        @Override
        public String toString() {
            return String.format("Policy %s: %s %s %s->%s:%s", 
                name, action, protocol, sourceIp, destinationIp, port);
        }
    }
    
    public static class NetworkConfiguration {
        private Map<String, NetworkInterface> interfaces = new HashMap<>();
        private List<RoutingRule> routingRules = new ArrayList<>();
        private List<SecurityPolicy> securityPolicies = new ArrayList<>();
        
        public void addInterface(NetworkInterface networkInterface) {
            interfaces.put(networkInterface.getName(), networkInterface);
        }
        
        public void addRoutingRule(RoutingRule rule) {
            routingRules.add(rule);
        }
        
        public void addSecurityPolicy(SecurityPolicy policy) {
            securityPolicies.add(policy);
        }
        
        public NetworkInterface getInterface(String name) {
            return interfaces.get(name);
        }
        
        public Map<String, NetworkInterface> getAllInterfaces() { return new HashMap<>(interfaces); }
        public List<RoutingRule> getRoutingRules() { return new ArrayList<>(routingRules); }
        public List<SecurityPolicy> getSecurityPolicies() { return new ArrayList<>(securityPolicies); }
        
        public void showConfiguration() {
            System.out.println("=== Network Configuration ===");
            
            System.out.println("Interfaces:");
            interfaces.values().forEach(i -> System.out.println("  " + i));
            
            System.out.println("Routing:");
            routingRules.forEach(r -> System.out.println("  " + r));
            
            System.out.println("Security Policies:");
            securityPolicies.forEach(p -> System.out.println("  " + p));
            
            System.out.println("==============================\n");
        }
    }
    
    // HORRIFIC: Configuration parser using complex regex patterns
    public static class NetworkConfigurationParser {
        
        // NIGHTMARE: Complex regex patterns that are impossible to maintain
        private static final String INTERFACE_PATTERN = 
            "interface\\s+([a-zA-Z0-9]+)\\s+ip\\s+([0-9.]+)\\s+subnet\\s+([0-9.]+)(?:\\s+status\\s+(UP|DOWN))?(?:\\s+mtu\\s+(\\d+))?";
            
        private static final String ROUTE_PATTERN = 
            "route\\s+add\\s+([0-9./]+)\\s+via\\s+([0-9.]+)(?:\\s+dev\\s+([a-zA-Z0-9]+))?(?:\\s+metric\\s+(\\d+))?";
            
        private static final String SECURITY_PATTERN = 
            "security\\s+policy\\s+([a-zA-Z0-9_]+)\\s+(ALLOW|DENY|LOG)(?:\\s+protocol\\s+([A-Z]+))?(?:\\s+from\\s+([0-9./]+))?(?:\\s+to\\s+([0-9./]+))?(?:\\s+port\\s+(\\d+))?";
            
        private static final String FIREWALL_PATTERN = 
            "firewall\\s+(enable|disable)\\s+([a-zA-Z0-9_]+)(?:\\s+on\\s+([a-zA-Z0-9]+))?";
            
        private static final String QOS_PATTERN = 
            "qos\\s+bandwidth\\s+([a-zA-Z0-9]+)\\s+(\\d+)([MG]?)bps(?:\\s+priority\\s+(\\d+))?";
        
        // HORROR: Compiled patterns that make debugging impossible
        private static final Pattern interfaceRegex = Pattern.compile(INTERFACE_PATTERN, Pattern.CASE_INSENSITIVE);
        private static final Pattern routeRegex = Pattern.compile(ROUTE_PATTERN, Pattern.CASE_INSENSITIVE);
        private static final Pattern securityRegex = Pattern.compile(SECURITY_PATTERN, Pattern.CASE_INSENSITIVE);
        private static final Pattern firewallRegex = Pattern.compile(FIREWALL_PATTERN, Pattern.CASE_INSENSITIVE);
        private static final Pattern qosRegex = Pattern.compile(QOS_PATTERN, Pattern.CASE_INSENSITIVE);
        
        public NetworkConfiguration parseConfiguration(String configScript) throws Exception {
            NetworkConfiguration config = new NetworkConfiguration();
            
            String[] lines = configScript.split("\\n");
            int lineNumber = 0;
            
            for (String line : lines) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                try {
                    // NIGHTMARE: Try each regex pattern until one matches
                    if (parseInterfaceCommand(line, config)) {
                        continue;
                    } else if (parseRouteCommand(line, config)) {
                        continue;
                    } else if (parseSecurityCommand(line, config)) {
                        continue;
                    } else if (parseFirewallCommand(line, config)) {
                        continue;
                    } else if (parseQosCommand(line, config)) {
                        continue;
                    } else {
                        // HORROR: Generic error message with no context
                        throw new RuntimeException("Unknown command syntax at line " + lineNumber + ": " + line);
                    }
                    
                } catch (Exception e) {
                    // DISASTER: Error handling that provides no useful information
                    throw new RuntimeException("Parse error at line " + lineNumber + " [" + line + "]: " + e.getMessage(), e);
                }
            }
            
            return config;
        }
        
        // HORRIFIC: Interface parsing with complex regex matching
        private boolean parseInterfaceCommand(String line, NetworkConfiguration config) {
            Matcher matcher = interfaceRegex.matcher(line);
            if (matcher.matches()) {
                try {
                    String interfaceName = matcher.group(1);
                    String ipAddress = matcher.group(2);
                    String subnet = matcher.group(3);
                    String status = matcher.group(4) != null ? matcher.group(4) : "DOWN";
                    int mtu = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : 1500;
                    
                    NetworkInterface networkInterface = new NetworkInterface(interfaceName);
                    networkInterface.setIpAddress(ipAddress);
                    networkInterface.setSubnet(subnet);
                    networkInterface.setStatus(status);
                    networkInterface.setMtu(mtu);
                    
                    config.addInterface(networkInterface);
                    return true;
                    
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid MTU value in interface command", e);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing interface command", e);
                }
            }
            return false;
        }
        
        // NIGHTMARE CONTINUES: Route parsing with even more complex logic
        private boolean parseRouteCommand(String line, NetworkConfiguration config) {
            Matcher matcher = routeRegex.matcher(line);
            if (matcher.matches()) {
                try {
                    String destination = matcher.group(1);
                    String gateway = matcher.group(2);
                    String interfaceName = matcher.group(3); // Optional
                    String metricStr = matcher.group(4); // Optional
                    
                    // Validate destination format (basic check)
                    if (!destination.matches("\\d+\\.\\d+\\.\\d+\\.\\d+(/\\d+)?")) {
                        throw new RuntimeException("Invalid destination format: " + destination);
                    }
                    
                    // Validate gateway format
                    if (!gateway.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                        throw new RuntimeException("Invalid gateway format: " + gateway);
                    }
                    
                    RoutingRule rule = new RoutingRule(destination, gateway);
                    if (interfaceName != null) {
                        rule.setInterfaceName(interfaceName);
                    }
                    if (metricStr != null) {
                        int metric = Integer.parseInt(metricStr);
                        if (metric < 1 || metric > 999) {
                            throw new RuntimeException("Metric must be between 1 and 999: " + metric);
                        }
                        rule.setMetric(metric);
                    }
                    
                    config.addRoutingRule(rule);
                    return true;
                    
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid metric value in route command", e);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing route command: " + e.getMessage(), e);
                }
            }
            return false;
        }
        
        // HORROR ESCALATES: Security policy parsing with nested conditions
        private boolean parseSecurityCommand(String line, NetworkConfiguration config) {
            Matcher matcher = securityRegex.matcher(line);
            if (matcher.matches()) {
                try {
                    String policyName = matcher.group(1);
                    String action = matcher.group(2);
                    String protocol = matcher.group(3); // Optional
                    String sourceIp = matcher.group(4); // Optional
                    String destinationIp = matcher.group(5); // Optional
                    String port = matcher.group(6); // Optional
                    
                    SecurityPolicy policy = new SecurityPolicy(policyName, action);
                    
                    if (protocol != null) {
                        if (!Arrays.asList("TCP", "UDP", "ICMP", "IP").contains(protocol)) {
                            throw new RuntimeException("Unsupported protocol: " + protocol);
                        }
                        policy.setProtocol(protocol);
                    }
                    
                    if (sourceIp != null) {
                        // Basic IP validation (this gets complex fast)
                        if (!isValidIpRange(sourceIp)) {
                            throw new RuntimeException("Invalid source IP format: " + sourceIp);
                        }
                        policy.setSourceIp(sourceIp);
                    }
                    
                    if (destinationIp != null) {
                        if (!isValidIpRange(destinationIp)) {
                            throw new RuntimeException("Invalid destination IP format: " + destinationIp);
                        }
                        policy.setDestinationIp(destinationIp);
                    }
                    
                    if (port != null) {
                        int portNum = Integer.parseInt(port);
                        if (portNum < 1 || portNum > 65535) {
                            throw new RuntimeException("Port must be between 1 and 65535: " + portNum);
                        }
                        policy.setPort(port);
                    }
                    
                    config.addSecurityPolicy(policy);
                    return true;
                    
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid port number in security command", e);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing security command: " + e.getMessage(), e);
                }
            }
            return false;
        }
        
        // DISASTER: Firewall parsing that duplicates security logic
        private boolean parseFirewallCommand(String line, NetworkConfiguration config) {
            Matcher matcher = firewallRegex.matcher(line);
            if (matcher.matches()) {
                try {
                    String action = matcher.group(1);
                    String ruleName = matcher.group(2);
                    String interfaceName = matcher.group(3); // Optional
                    
                    // Create a security policy for firewall rules (DUPLICATION!)
                    String policyAction = action.equals("enable") ? "ALLOW" : "DENY";
                    SecurityPolicy policy = new SecurityPolicy("firewall_" + ruleName, policyAction);
                    
                    if (interfaceName != null) {
                        // This is where the regex approach breaks down - we need to reference interfaces
                        NetworkInterface networkInterface = config.getInterface(interfaceName);
                        if (networkInterface == null) {
                            throw new RuntimeException("Interface not found: " + interfaceName);
                        }
                        // But we have no good way to link them in this approach!
                    }
                    
                    config.addSecurityPolicy(policy);
                    return true;
                    
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing firewall command: " + e.getMessage(), e);
                }
            }
            return false;
        }
        
        // NIGHTMARE FINALE: QoS parsing with unit conversions
        private boolean parseQosCommand(String line, NetworkConfiguration config) {
            Matcher matcher = qosRegex.matcher(line);
            if (matcher.matches()) {
                try {
                    String interfaceName = matcher.group(1);
                    String bandwidthStr = matcher.group(2);
                    String unit = matcher.group(3); // M or G
                    String priorityStr = matcher.group(4); // Optional
                    
                    // Complex bandwidth conversion logic
                    long bandwidth = Long.parseLong(bandwidthStr);
                    if ("G".equals(unit)) {
                        bandwidth *= 1000; // Convert to Mbps
                    } else if (unit == null || unit.isEmpty()) {
                        // Default to Mbps - but this is implicit knowledge!
                    }
                    
                    // Check if interface exists
                    NetworkInterface networkInterface = config.getInterface(interfaceName);
                    if (networkInterface == null) {
                        throw new RuntimeException("Interface not found: " + interfaceName);
                    }
                    
                    // We have no good way to store QoS settings in our current model!
                    // This is where the lack of proper interpretation structure shows
                    System.out.println("QoS configured for " + interfaceName + ": " + bandwidth + 
                        "Mbps" + (priorityStr != null ? " (priority " + priorityStr + ")" : ""));
                    
                    return true;
                    
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid bandwidth value in QoS command", e);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing QoS command: " + e.getMessage(), e);
                }
            }
            return false;
        }
        
        // HELPER: Complex IP validation that should be part of interpretation
        private boolean isValidIpRange(String ipRange) {
            // This becomes complex fast and duplicates logic
            String[] parts = ipRange.split("/");
            String ip = parts[0];
            
            // Basic IP validation
            String[] octets = ip.split("\\.");
            if (octets.length != 4) return false;
            
            for (String octet : octets) {
                try {
                    int num = Integer.parseInt(octet);
                    if (num < 0 || num > 255) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            
            // Subnet validation if present
            if (parts.length > 1) {
                try {
                    int subnet = Integer.parseInt(parts[1]);
                    if (subnet < 0 || subnet > 32) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            
            return true;
        }
    }
    
    // PROBLEMATIC: Configuration executor tightly coupled with parsing
    public static class NetworkConfigurationExecutor {
        
        public void executeConfiguration(String configScript) {
            System.out.println("🔧 Executing network configuration...\n");
            
            try {
                NetworkConfigurationParser parser = new NetworkConfigurationParser();
                NetworkConfiguration config = parser.parseConfiguration(configScript);
                
                System.out.println("✅ Configuration parsed successfully");
                config.showConfiguration();
                
                // Apply configuration (simplified simulation)
                applyConfiguration(config);
                
            } catch (Exception e) {
                // HORROR: Generic error handling with regex stack traces
                System.err.println("❌ Configuration execution failed:");
                System.err.println("   " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("   Caused by: " + e.getCause().getMessage());
                }
                
                // The poor network engineer has to debug regex patterns...
                System.err.println("\n🔍 Debugging help:");
                System.err.println("   - Check command syntax against documentation");
                System.err.println("   - Validate IP addresses and network ranges");
                System.err.println("   - Ensure proper spacing in commands");
                System.err.println("   - Check for typos in interface names");
            }
        }
        
        private void applyConfiguration(NetworkConfiguration config) {
            System.out.println("Applying configuration to network devices...");
            
            // Simulate applying interfaces
            for (NetworkInterface networkInterface : config.getAllInterfaces().values()) {
                System.out.println("  → Configuring interface: " + networkInterface.getName());
                // In reality, this would make system calls or API calls
            }
            
            // Simulate applying routes
            for (RoutingRule rule : config.getRoutingRules()) {
                System.out.println("  → Adding route: " + rule.getDestination());
            }
            
            // Simulate applying security policies
            for (SecurityPolicy policy : config.getSecurityPolicies()) {
                System.out.println("  → Applying security policy: " + policy.getName());
            }
            
            System.out.println("✅ Configuration applied successfully");
        }
    }
}