package com.telekom.architecture.training.day4.interpreter;

import java.util.*;

/**
 * FIXED IMPLEMENTATION using Interpreter Pattern
 * 
 * Benefits:
 * - Clean separation between parsing, interpretation, and execution
 * - Easy to add new commands without modifying existing code
 * - Proper error handling with meaningful messages and locations
 * - Composable expressions for complex configurations
 * - Reusable interpretation components
 * - Type-safe command validation
 * 
 * Enterprise Features:
 * - Abstract syntax tree for complex expressions
 * - Context-aware validation during interpretation
 * - Extensible command hierarchy
 * - Detailed error reporting with line numbers and suggestions
 * - Configuration optimization and validation
 * 
 * Trainer Notes:
 * - Show how Interpreter pattern separates concerns cleanly
 * - Demonstrate easy extensibility for new commands
 * - Point out improved error handling and debugging
 * - Show how composable expressions work
 */
public class NetworkConfigurationDSLFixed {
    
    // Enhanced domain model
    public static class NetworkInterface {
        private String name;
        private String ipAddress;
        private String subnet;
        private String status = "DOWN";
        private int mtu = 1500;
        private long bandwidth = 1000; // Mbps
        private int priority = 5;
        
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
        public long getBandwidth() { return bandwidth; }
        public void setBandwidth(long bandwidth) { this.bandwidth = bandwidth; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        @Override
        public String toString() {
            return String.format("%s: %s/%s [%s] MTU:%d BW:%dMbps P:%d", 
                name, ipAddress, subnet, status, mtu, bandwidth, priority);
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
        private String action;
        private String protocol;
        private String sourceIp;
        private String destinationIp;
        private String port;
        private String interfaceName;
        
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
        public String getInterfaceName() { return interfaceName; }
        public void setInterfaceName(String interfaceName) { this.interfaceName = interfaceName; }
        
        @Override
        public String toString() {
            return String.format("Policy %s: %s %s %s->%s:%s on %s", 
                name, action, protocol, sourceIp, destinationIp, port, interfaceName);
        }
    }
    
    // Context for interpretation - holds state and configuration
    public static class InterpretationContext {
        private Map<String, NetworkInterface> interfaces = new HashMap<>();
        private List<RoutingRule> routingRules = new ArrayList<>();
        private List<SecurityPolicy> securityPolicies = new ArrayList<>();
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        
        // Interface management
        public void addInterface(NetworkInterface networkInterface) {
            interfaces.put(networkInterface.getName(), networkInterface);
        }
        
        public NetworkInterface getInterface(String name) {
            return interfaces.get(name);
        }
        
        public boolean hasInterface(String name) {
            return interfaces.containsKey(name);
        }
        
        // Routing management
        public void addRoutingRule(RoutingRule rule) {
            routingRules.add(rule);
        }
        
        // Security management
        public void addSecurityPolicy(SecurityPolicy policy) {
            securityPolicies.add(policy);
        }
        
        // Error handling
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        // Getters
        public Map<String, NetworkInterface> getAllInterfaces() { return new HashMap<>(interfaces); }
        public List<RoutingRule> getRoutingRules() { return new ArrayList<>(routingRules); }
        public List<SecurityPolicy> getSecurityPolicies() { return new ArrayList<>(securityPolicies); }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
        
        public void showConfiguration() {
            System.out.println("=== Network Configuration ===");
            
            System.out.println("Interfaces:");
            interfaces.values().forEach(i -> System.out.println("  " + i));
            
            System.out.println("Routing:");
            routingRules.forEach(r -> System.out.println("  " + r));
            
            System.out.println("Security Policies:");
            securityPolicies.forEach(p -> System.out.println("  " + p));
            
            if (!warnings.isEmpty()) {
                System.out.println("Warnings:");
                warnings.forEach(w -> System.out.println("  ⚠️  " + w));
            }
            
            System.out.println("==============================\n");
        }
    }
    
    // Abstract Expression interface
    public interface ConfigurationExpression {
        void interpret(InterpretationContext context);
        void validate(InterpretationContext context, int lineNumber);
        String getDescription();
    }
    
    // Terminal expressions for basic commands
    
    public static class InterfaceExpression implements ConfigurationExpression {
        private String name;
        private String ipAddress;
        private String subnet;
        private String status;
        private Integer mtu;
        
        public InterfaceExpression(String name, String ipAddress, String subnet) {
            this.name = name;
            this.ipAddress = ipAddress;
            this.subnet = subnet;
        }
        
        public InterfaceExpression withStatus(String status) {
            this.status = status;
            return this;
        }
        
        public InterfaceExpression withMtu(int mtu) {
            this.mtu = mtu;
            return this;
        }
        
        @Override
        public void validate(InterpretationContext context, int lineNumber) {
            // Validate interface name
            if (name == null || name.trim().isEmpty()) {
                context.addError("Line " + lineNumber + ": Interface name cannot be empty");
                return;
            }
            
            // Check for duplicate interface
            if (context.hasInterface(name)) {
                context.addWarning("Line " + lineNumber + ": Interface " + name + " already exists - will be overwritten");
            }
            
            // Validate IP address format
            if (!isValidIpAddress(ipAddress)) {
                context.addError("Line " + lineNumber + ": Invalid IP address format: " + ipAddress);
            }
            
            // Validate subnet
            if (!isValidSubnet(subnet)) {
                context.addError("Line " + lineNumber + ": Invalid subnet format: " + subnet);
            }
            
            // Validate status
            if (status != null && !Arrays.asList("UP", "DOWN").contains(status.toUpperCase())) {
                context.addError("Line " + lineNumber + ": Invalid interface status: " + status + " (must be UP or DOWN)");
            }
            
            // Validate MTU
            if (mtu != null && (mtu < 68 || mtu > 9000)) {
                context.addError("Line " + lineNumber + ": MTU must be between 68 and 9000 bytes: " + mtu);
            }
        }
        
        @Override
        public void interpret(InterpretationContext context) {
            NetworkInterface networkInterface = new NetworkInterface(name);
            networkInterface.setIpAddress(ipAddress);
            networkInterface.setSubnet(subnet);
            
            if (status != null) {
                networkInterface.setStatus(status.toUpperCase());
            }
            
            if (mtu != null) {
                networkInterface.setMtu(mtu);
            }
            
            context.addInterface(networkInterface);
        }
        
        @Override
        public String getDescription() {
            return "Interface configuration: " + name;
        }
        
        private boolean isValidIpAddress(String ip) {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return false;
            
            for (String part : parts) {
                try {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean isValidSubnet(String subnet) {
            String[] parts = subnet.split("\\.");
            if (parts.length != 4) return false;
            
            for (String part : parts) {
                try {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static class RouteExpression implements ConfigurationExpression {
        private String destination;
        private String gateway;
        private String interfaceName;
        private Integer metric;
        
        public RouteExpression(String destination, String gateway) {
            this.destination = destination;
            this.gateway = gateway;
        }
        
        public RouteExpression viaInterface(String interfaceName) {
            this.interfaceName = interfaceName;
            return this;
        }
        
        public RouteExpression withMetric(int metric) {
            this.metric = metric;
            return this;
        }
        
        @Override
        public void validate(InterpretationContext context, int lineNumber) {
            // Validate destination format
            if (!isValidNetworkRange(destination)) {
                context.addError("Line " + lineNumber + ": Invalid destination format: " + destination);
            }
            
            // Validate gateway format
            if (!isValidIpAddress(gateway)) {
                context.addError("Line " + lineNumber + ": Invalid gateway format: " + gateway);
            }
            
            // Validate interface exists if specified
            if (interfaceName != null && !context.hasInterface(interfaceName)) {
                context.addError("Line " + lineNumber + ": Interface not found: " + interfaceName);
            }
            
            // Validate metric range
            if (metric != null && (metric < 1 || metric > 999)) {
                context.addError("Line " + lineNumber + ": Metric must be between 1 and 999: " + metric);
            }
        }
        
        @Override
        public void interpret(InterpretationContext context) {
            RoutingRule rule = new RoutingRule(destination, gateway);
            
            if (interfaceName != null) {
                rule.setInterfaceName(interfaceName);
            }
            
            if (metric != null) {
                rule.setMetric(metric);
            }
            
            context.addRoutingRule(rule);
        }
        
        @Override
        public String getDescription() {
            return "Route configuration: " + destination + " -> " + gateway;
        }
        
        private boolean isValidIpAddress(String ip) {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return false;
            
            for (String part : parts) {
                try {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean isValidNetworkRange(String range) {
            String[] parts = range.split("/");
            if (!isValidIpAddress(parts[0])) return false;
            
            if (parts.length > 1) {
                try {
                    int cidr = Integer.parseInt(parts[1]);
                    return cidr >= 0 && cidr <= 32;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static class SecurityPolicyExpression implements ConfigurationExpression {
        private String name;
        private String action;
        private String protocol;
        private String sourceIp;
        private String destinationIp;
        private String port;
        private String interfaceName;
        
        public SecurityPolicyExpression(String name, String action) {
            this.name = name;
            this.action = action;
        }
        
        public SecurityPolicyExpression withProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }
        
        public SecurityPolicyExpression fromSource(String sourceIp) {
            this.sourceIp = sourceIp;
            return this;
        }
        
        public SecurityPolicyExpression toDestination(String destinationIp) {
            this.destinationIp = destinationIp;
            return this;
        }
        
        public SecurityPolicyExpression onPort(String port) {
            this.port = port;
            return this;
        }
        
        public SecurityPolicyExpression onInterface(String interfaceName) {
            this.interfaceName = interfaceName;
            return this;
        }
        
        @Override
        public void validate(InterpretationContext context, int lineNumber) {
            // Validate policy name
            if (name == null || name.trim().isEmpty()) {
                context.addError("Line " + lineNumber + ": Policy name cannot be empty");
            }
            
            // Validate action
            if (!Arrays.asList("ALLOW", "DENY", "LOG").contains(action)) {
                context.addError("Line " + lineNumber + ": Invalid action: " + action + " (must be ALLOW, DENY, or LOG)");
            }
            
            // Validate protocol if specified
            if (protocol != null && !Arrays.asList("TCP", "UDP", "ICMP", "IP").contains(protocol)) {
                context.addError("Line " + lineNumber + ": Unsupported protocol: " + protocol);
            }
            
            // Validate IP addresses if specified
            if (sourceIp != null && !isValidNetworkRange(sourceIp)) {
                context.addError("Line " + lineNumber + ": Invalid source IP format: " + sourceIp);
            }
            
            if (destinationIp != null && !isValidNetworkRange(destinationIp)) {
                context.addError("Line " + lineNumber + ": Invalid destination IP format: " + destinationIp);
            }
            
            // Validate port if specified
            if (port != null) {
                try {
                    int portNum = Integer.parseInt(port);
                    if (portNum < 1 || portNum > 65535) {
                        context.addError("Line " + lineNumber + ": Port must be between 1 and 65535: " + portNum);
                    }
                } catch (NumberFormatException e) {
                    context.addError("Line " + lineNumber + ": Invalid port number: " + port);
                }
            }
            
            // Validate interface if specified
            if (interfaceName != null && !context.hasInterface(interfaceName)) {
                context.addWarning("Line " + lineNumber + ": Interface " + interfaceName + " not yet defined");
            }
        }
        
        @Override
        public void interpret(InterpretationContext context) {
            SecurityPolicy policy = new SecurityPolicy(name, action);
            
            if (protocol != null) policy.setProtocol(protocol);
            if (sourceIp != null) policy.setSourceIp(sourceIp);
            if (destinationIp != null) policy.setDestinationIp(destinationIp);
            if (port != null) policy.setPort(port);
            if (interfaceName != null) policy.setInterfaceName(interfaceName);
            
            context.addSecurityPolicy(policy);
        }
        
        @Override
        public String getDescription() {
            return "Security policy: " + name + " (" + action + ")";
        }
        
        private boolean isValidNetworkRange(String range) {
            if ("any".equalsIgnoreCase(range)) return true;
            
            String[] parts = range.split("/");
            if (!isValidIpAddress(parts[0])) return false;
            
            if (parts.length > 1) {
                try {
                    int cidr = Integer.parseInt(parts[1]);
                    return cidr >= 0 && cidr <= 32;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean isValidIpAddress(String ip) {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return false;
            
            for (String part : parts) {
                try {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static class QosExpression implements ConfigurationExpression {
        private String interfaceName;
        private long bandwidth;
        private Integer priority;
        
        public QosExpression(String interfaceName, long bandwidth) {
            this.interfaceName = interfaceName;
            this.bandwidth = bandwidth;
        }
        
        public QosExpression withPriority(int priority) {
            this.priority = priority;
            return this;
        }
        
        @Override
        public void validate(InterpretationContext context, int lineNumber) {
            // Validate interface exists
            if (!context.hasInterface(interfaceName)) {
                context.addError("Line " + lineNumber + ": Interface not found: " + interfaceName);
            }
            
            // Validate bandwidth
            if (bandwidth <= 0) {
                context.addError("Line " + lineNumber + ": Bandwidth must be positive: " + bandwidth);
            }
            
            // Validate priority
            if (priority != null && (priority < 1 || priority > 10)) {
                context.addError("Line " + lineNumber + ": Priority must be between 1 and 10: " + priority);
            }
        }
        
        @Override
        public void interpret(InterpretationContext context) {
            NetworkInterface networkInterface = context.getInterface(interfaceName);
            if (networkInterface != null) {
                networkInterface.setBandwidth(bandwidth);
                if (priority != null) {
                    networkInterface.setPriority(priority);
                }
            }
        }
        
        @Override
        public String getDescription() {
            return "QoS configuration: " + interfaceName + " (" + bandwidth + "Mbps)";
        }
    }
    
    // Non-terminal expressions for complex compositions
    
    public static class CompositeExpression implements ConfigurationExpression {
        private List<ConfigurationExpression> expressions = new ArrayList<>();
        
        public void addExpression(ConfigurationExpression expression) {
            expressions.add(expression);
        }
        
        @Override
        public void validate(InterpretationContext context, int lineNumber) {
            for (ConfigurationExpression expression : expressions) {
                expression.validate(context, lineNumber);
            }
        }
        
        @Override
        public void interpret(InterpretationContext context) {
            for (ConfigurationExpression expression : expressions) {
                expression.interpret(context);
            }
        }
        
        @Override
        public String getDescription() {
            return "Composite configuration with " + expressions.size() + " commands";
        }
    }
    
    // Parser for building expression tree
    public static class ConfigurationParser {
        
        public List<ConfigurationExpression> parseConfiguration(String configScript) throws ConfigurationParseException {
            List<ConfigurationExpression> expressions = new ArrayList<>();
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
                    ConfigurationExpression expression = parseLine(line, lineNumber);
                    if (expression != null) {
                        expressions.add(expression);
                    }
                } catch (Exception e) {
                    throw new ConfigurationParseException(
                        "Parse error at line " + lineNumber + " [" + line + "]: " + e.getMessage(), 
                        lineNumber, line, e);
                }
            }
            
            return expressions;
        }
        
        private ConfigurationExpression parseLine(String line, int lineNumber) throws Exception {
            String[] tokens = line.split("\\s+");
            if (tokens.length == 0) return null;
            
            String command = tokens[0].toLowerCase();
            
            switch (command) {
                case "interface":
                    return parseInterfaceCommand(tokens, lineNumber);
                case "route":
                    return parseRouteCommand(tokens, lineNumber);
                case "security":
                    return parseSecurityCommand(tokens, lineNumber);
                case "qos":
                    return parseQosCommand(tokens, lineNumber);
                default:
                    throw new IllegalArgumentException("Unknown command: " + command + 
                        ". Supported commands: interface, route, security, qos");
            }
        }
        
        private ConfigurationExpression parseInterfaceCommand(String[] tokens, int lineNumber) throws Exception {
            if (tokens.length < 6) {
                throw new IllegalArgumentException("Interface command requires at least: interface <name> ip <address> subnet <mask>");
            }
            
            String name = tokens[1];
            String ipAddress = null;
            String subnet = null;
            String status = null;
            Integer mtu = null;
            
            // Parse key-value pairs
            for (int i = 2; i < tokens.length - 1; i += 2) {
                String key = tokens[i].toLowerCase();
                String value = tokens[i + 1];
                
                switch (key) {
                    case "ip":
                        ipAddress = value;
                        break;
                    case "subnet":
                        subnet = value;
                        break;
                    case "status":
                        status = value;
                        break;
                    case "mtu":
                        mtu = Integer.parseInt(value);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown interface parameter: " + key);
                }
            }
            
            if (ipAddress == null || subnet == null) {
                throw new IllegalArgumentException("Interface command must specify both ip and subnet");
            }
            
            InterfaceExpression expr = new InterfaceExpression(name, ipAddress, subnet);
            if (status != null) expr.withStatus(status);
            if (mtu != null) expr.withMtu(mtu);
            
            return expr;
        }
        
        private ConfigurationExpression parseRouteCommand(String[] tokens, int lineNumber) throws Exception {
            if (tokens.length < 5 || !tokens[1].equals("add")) {
                throw new IllegalArgumentException("Route command format: route add <destination> via <gateway> [dev <interface>] [metric <value>]");
            }
            
            String destination = tokens[2];
            if (!tokens[3].equals("via")) {
                throw new IllegalArgumentException("Route command must use 'via' keyword");
            }
            String gateway = tokens[4];
            
            RouteExpression expr = new RouteExpression(destination, gateway);
            
            // Parse optional parameters
            for (int i = 5; i < tokens.length - 1; i += 2) {
                String key = tokens[i].toLowerCase();
                String value = tokens[i + 1];
                
                switch (key) {
                    case "dev":
                        expr.viaInterface(value);
                        break;
                    case "metric":
                        expr.withMetric(Integer.parseInt(value));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown route parameter: " + key);
                }
            }
            
            return expr;
        }
        
        private ConfigurationExpression parseSecurityCommand(String[] tokens, int lineNumber) throws Exception {
            if (tokens.length < 4 || !tokens[1].equals("policy")) {
                throw new IllegalArgumentException("Security command format: security policy <name> <action> [protocol <proto>] [from <source>] [to <dest>] [port <port>] [on <interface>]");
            }
            
            String name = tokens[2];
            String action = tokens[3].toUpperCase();
            
            SecurityPolicyExpression expr = new SecurityPolicyExpression(name, action);
            
            // Parse optional parameters
            for (int i = 4; i < tokens.length - 1; i += 2) {
                String key = tokens[i].toLowerCase();
                String value = tokens[i + 1];
                
                switch (key) {
                    case "protocol":
                        expr.withProtocol(value.toUpperCase());
                        break;
                    case "from":
                        expr.fromSource(value);
                        break;
                    case "to":
                        expr.toDestination(value);
                        break;
                    case "port":
                        expr.onPort(value);
                        break;
                    case "on":
                        expr.onInterface(value);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown security parameter: " + key);
                }
            }
            
            return expr;
        }
        
        private ConfigurationExpression parseQosCommand(String[] tokens, int lineNumber) throws Exception {
            if (tokens.length < 4 || !tokens[1].equals("bandwidth")) {
                throw new IllegalArgumentException("QoS command format: qos bandwidth <interface> <bandwidth>[Mbps|Gbps] [priority <value>]");
            }
            
            String interfaceName = tokens[2];
            String bandwidthStr = tokens[3];
            
            // Parse bandwidth with unit
            long bandwidth;
            if (bandwidthStr.endsWith("Gbps")) {
                bandwidth = Long.parseLong(bandwidthStr.substring(0, bandwidthStr.length() - 4)) * 1000;
            } else if (bandwidthStr.endsWith("Mbps")) {
                bandwidth = Long.parseLong(bandwidthStr.substring(0, bandwidthStr.length() - 4));
            } else {
                bandwidth = Long.parseLong(bandwidthStr); // Assume Mbps
            }
            
            QosExpression expr = new QosExpression(interfaceName, bandwidth);
            
            // Parse optional parameters
            for (int i = 4; i < tokens.length - 1; i += 2) {
                String key = tokens[i].toLowerCase();
                String value = tokens[i + 1];
                
                if (key.equals("priority")) {
                    expr.withPriority(Integer.parseInt(value));
                } else {
                    throw new IllegalArgumentException("Unknown QoS parameter: " + key);
                }
            }
            
            return expr;
        }
    }
    
    // Custom exception for parse errors
    public static class ConfigurationParseException extends Exception {
        private final int lineNumber;
        private final String line;
        
        public ConfigurationParseException(String message, int lineNumber, String line, Throwable cause) {
            super(message, cause);
            this.lineNumber = lineNumber;
            this.line = line;
        }
        
        public int getLineNumber() { return lineNumber; }
        public String getLine() { return line; }
    }
    
    // Interpreter engine
    public static class NetworkConfigurationInterpreter {
        
        public InterpretationContext executeConfiguration(String configScript) {
            System.out.println("🔧 Interpreting network configuration...\n");
            
            InterpretationContext context = new InterpretationContext();
            
            try {
                ConfigurationParser parser = new ConfigurationParser();
                List<ConfigurationExpression> expressions = parser.parseConfiguration(configScript);
                
                System.out.println("📋 Parsed " + expressions.size() + " configuration commands");
                
                // Validation phase
                System.out.println("🔍 Validating configuration...");
                int lineNumber = 1;
                for (ConfigurationExpression expression : expressions) {
                    expression.validate(context, lineNumber++);
                }
                
                // Check for errors before interpretation
                if (context.hasErrors()) {
                    System.err.println("❌ Configuration validation failed:");
                    for (String error : context.getErrors()) {
                        System.err.println("   " + error);
                    }
                    return context;
                }
                
                // Show warnings if any
                if (!context.getWarnings().isEmpty()) {
                    System.out.println("⚠️  Configuration warnings:");
                    for (String warning : context.getWarnings()) {
                        System.out.println("   " + warning);
                    }
                }
                
                // Interpretation phase
                System.out.println("⚙️  Interpreting configuration...");
                for (ConfigurationExpression expression : expressions) {
                    System.out.println("   → " + expression.getDescription());
                    expression.interpret(context);
                }
                
                System.out.println("✅ Configuration interpreted successfully\n");
                context.showConfiguration();
                
                // Simulate applying configuration
                applyConfiguration(context);
                
            } catch (ConfigurationParseException e) {
                context.addError("Parse error at line " + e.getLineNumber() + " [" + e.getLine() + "]: " + e.getMessage());
                System.err.println("❌ Configuration parsing failed:");
                System.err.println("   Line " + e.getLineNumber() + ": " + e.getMessage());
                System.err.println("   Command: " + e.getLine());
                
                // Provide helpful suggestions
                provideParseSuggestions(e.getLine());
                
            } catch (Exception e) {
                context.addError("Unexpected error: " + e.getMessage());
                System.err.println("❌ Unexpected error during configuration interpretation:");
                System.err.println("   " + e.getMessage());
            }
            
            return context;
        }
        
        private void applyConfiguration(InterpretationContext context) {
            System.out.println("🚀 Applying configuration to network devices...");
            
            // Simulate applying interfaces
            for (NetworkInterface networkInterface : context.getAllInterfaces().values()) {
                System.out.println("  → Configuring interface: " + networkInterface.getName() + 
                    " (" + networkInterface.getIpAddress() + ")");
            }
            
            // Simulate applying routes
            for (RoutingRule rule : context.getRoutingRules()) {
                System.out.println("  → Adding route: " + rule.getDestination() + " via " + rule.getGateway());
            }
            
            // Simulate applying security policies
            for (SecurityPolicy policy : context.getSecurityPolicies()) {
                System.out.println("  → Applying security policy: " + policy.getName() + " (" + policy.getAction() + ")");
            }
            
            System.out.println("✅ Configuration applied successfully to all devices");
        }
        
        private void provideParseSuggestions(String line) {
            System.err.println("\n💡 Suggestions:");
            
            String lowerLine = line.toLowerCase();
            if (lowerLine.startsWith("interface")) {
                System.err.println("   Interface syntax: interface <name> ip <address> subnet <mask> [status UP|DOWN] [mtu <bytes>]");
                System.err.println("   Example: interface eth0 ip 192.168.1.1 subnet 255.255.255.0 status UP");
            } else if (lowerLine.startsWith("route")) {
                System.err.println("   Route syntax: route add <destination> via <gateway> [dev <interface>] [metric <value>]");
                System.err.println("   Example: route add 10.0.0.0/24 via 192.168.1.254 dev eth0 metric 1");
            } else if (lowerLine.startsWith("security")) {
                System.err.println("   Security syntax: security policy <name> <ALLOW|DENY|LOG> [protocol <TCP|UDP>] [from <source>] [to <dest>] [port <port>]");
                System.err.println("   Example: security policy web_access ALLOW protocol TCP from any to 192.168.1.100 port 80");
            } else if (lowerLine.startsWith("qos")) {
                System.err.println("   QoS syntax: qos bandwidth <interface> <bandwidth>Mbps [priority <1-10>]");
                System.err.println("   Example: qos bandwidth eth0 1000Mbps priority 5");
            } else {
                System.err.println("   Supported commands: interface, route, security, qos");
                System.err.println("   Use # for comments");
            }
        }
    }
}