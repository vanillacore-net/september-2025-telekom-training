package com.telekom.architecture.training.day4.interpreter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.architecture.training.day4.interpreter.NetworkConfigurationDSLFixed.*;

import java.util.List;

/**
 * Test suite for Interpreter Pattern implementation
 * 
 * Tests demonstrate:
 * - Clean separation between parsing, interpretation, and execution
 * - Easy addition of new commands without modifying existing code
 * - Proper error handling with meaningful messages
 * - Context-aware validation during interpretation
 * - Type-safe command validation
 */
class InterpreterPatternTest {
    
    private NetworkConfigurationInterpreter interpreter;
    private ConfigurationParser parser;
    
    @BeforeEach
    void setUp() {
        interpreter = new NetworkConfigurationInterpreter();
        parser = new ConfigurationParser();
    }
    
    @Test
    @DisplayName("Simple interface command should be parsed and interpreted correctly")
    void shouldParseSimpleInterfaceCommand() throws Exception {
        String config = "interface eth0 ip 192.168.1.1 subnet 255.255.255.0";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        assertThat(expressions).hasSize(1);
        assertThat(expressions.get(0)).isInstanceOf(InterfaceExpression.class);
        assertThat(expressions.get(0).getDescription()).contains("Interface configuration: eth0");
    }
    
    @Test
    @DisplayName("Interface command with optional parameters should be parsed correctly")
    void shouldParseInterfaceCommandWithOptionalParameters() throws Exception {
        String config = "interface eth1 ip 10.0.0.1 subnet 255.255.255.0 status UP mtu 9000";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        assertThat(expressions).hasSize(1);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        expressions.get(0).interpret(context);
        
        assertThat(context.hasErrors()).isFalse();
        assertThat(context.hasInterface("eth1")).isTrue();
        
        NetworkInterface networkInterface = context.getInterface("eth1");
        assertThat(networkInterface.getIpAddress()).isEqualTo("10.0.0.1");
        assertThat(networkInterface.getSubnet()).isEqualTo("255.255.255.0");
        assertThat(networkInterface.getStatus()).isEqualTo("UP");
        assertThat(networkInterface.getMtu()).isEqualTo(9000);
    }
    
    @Test
    @DisplayName("Route command should be parsed and interpreted correctly")
    void shouldParseRouteCommand() throws Exception {
        String config = "route add 0.0.0.0/0 via 192.168.1.254 dev eth0 metric 1";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        assertThat(expressions).hasSize(1);
        assertThat(expressions.get(0)).isInstanceOf(RouteExpression.class);
        
        InterpretationContext context = new InterpretationContext();
        // Add interface for validation
        context.addInterface(new NetworkInterface("eth0"));
        
        expressions.get(0).validate(context, 1);
        expressions.get(0).interpret(context);
        
        assertThat(context.hasErrors()).isFalse();
        assertThat(context.getRoutingRules()).hasSize(1);
        
        RoutingRule rule = context.getRoutingRules().get(0);
        assertThat(rule.getDestination()).isEqualTo("0.0.0.0/0");
        assertThat(rule.getGateway()).isEqualTo("192.168.1.254");
        assertThat(rule.getInterfaceName()).isEqualTo("eth0");
        assertThat(rule.getMetric()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Security policy command should be parsed correctly")
    void shouldParseSecurityPolicyCommand() throws Exception {
        String config = "security policy web_access ALLOW protocol TCP from any to 192.168.1.100 port 80";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        assertThat(expressions).hasSize(1);
        assertThat(expressions.get(0)).isInstanceOf(SecurityPolicyExpression.class);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        expressions.get(0).interpret(context);
        
        assertThat(context.hasErrors()).isFalse();
        assertThat(context.getSecurityPolicies()).hasSize(1);
        
        SecurityPolicy policy = context.getSecurityPolicies().get(0);
        assertThat(policy.getName()).isEqualTo("web_access");
        assertThat(policy.getAction()).isEqualTo("ALLOW");
        assertThat(policy.getProtocol()).isEqualTo("TCP");
        assertThat(policy.getSourceIp()).isEqualTo("any");
        assertThat(policy.getDestinationIp()).isEqualTo("192.168.1.100");
        assertThat(policy.getPort()).isEqualTo("80");
    }
    
    @Test
    @DisplayName("QoS command should be parsed and interpreted correctly")
    void shouldParseQosCommand() throws Exception {
        String config = """
            interface eth0 ip 192.168.1.1 subnet 255.255.255.0
            qos bandwidth eth0 1000Mbps priority 5
            """;
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        assertThat(expressions).hasSize(2);
        assertThat(expressions.get(1)).isInstanceOf(QosExpression.class);
        
        InterpretationContext context = new InterpretationContext();
        
        // Interpret interface first
        expressions.get(0).validate(context, 1);
        expressions.get(0).interpret(context);
        
        // Then QoS
        expressions.get(1).validate(context, 2);
        expressions.get(1).interpret(context);
        
        assertThat(context.hasErrors()).isFalse();
        
        NetworkInterface networkInterface = context.getInterface("eth0");
        assertThat(networkInterface.getBandwidth()).isEqualTo(1000);
        assertThat(networkInterface.getPriority()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("Complete configuration script should be interpreted successfully")
    void shouldInterpretCompleteConfigurationScript() {
        String config = """
            # Network configuration for router R001
            interface eth0 ip 192.168.1.1 subnet 255.255.255.0 status UP
            interface eth1 ip 10.0.1.1 subnet 255.255.255.0 status UP mtu 9000
            
            route add 0.0.0.0/0 via 192.168.1.254 dev eth0 metric 1
            route add 10.0.0.0/8 via 10.0.1.254 dev eth1 metric 2
            
            security policy web_access ALLOW protocol TCP from any to 192.168.1.100 port 80
            security policy ssh_access ALLOW protocol TCP from 192.168.1.0/24 to any port 22
            security policy deny_all DENY
            
            qos bandwidth eth0 1000Mbps priority 5
            qos bandwidth eth1 10000Mbps priority 3
            """;
        
        InterpretationContext context = interpreter.executeConfiguration(config);
        
        assertThat(context.hasErrors()).isFalse();
        
        // Verify interfaces
        assertThat(context.getAllInterfaces()).hasSize(2);
        assertThat(context.hasInterface("eth0")).isTrue();
        assertThat(context.hasInterface("eth1")).isTrue();
        
        // Verify routes
        assertThat(context.getRoutingRules()).hasSize(2);
        
        // Verify security policies
        assertThat(context.getSecurityPolicies()).hasSize(3);
        
        // Verify QoS settings were applied to interfaces
        NetworkInterface eth0 = context.getInterface("eth0");
        NetworkInterface eth1 = context.getInterface("eth1");
        
        assertThat(eth0.getBandwidth()).isEqualTo(1000);
        assertThat(eth0.getPriority()).isEqualTo(5);
        assertThat(eth1.getBandwidth()).isEqualTo(10000);
        assertThat(eth1.getPriority()).isEqualTo(3);
        assertThat(eth1.getMtu()).isEqualTo(9000);
    }
    
    @Test
    @DisplayName("Invalid IP address should trigger validation error")
    void shouldDetectInvalidIpAddress() throws Exception {
        String config = "interface eth0 ip 999.999.999.999 subnet 255.255.255.0";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Invalid IP address format"));
    }
    
    @Test
    @DisplayName("Invalid subnet should trigger validation error")
    void shouldDetectInvalidSubnet() throws Exception {
        String config = "interface eth0 ip 192.168.1.1 subnet 999.999.999.999";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Invalid subnet format"));
    }
    
    @Test
    @DisplayName("Invalid MTU should trigger validation error")
    void shouldDetectInvalidMtu() throws Exception {
        String config = "interface eth0 ip 192.168.1.1 subnet 255.255.255.0 mtu 50000";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("MTU must be between 68 and 9000"));
    }
    
    @Test
    @DisplayName("Route to nonexistent interface should trigger validation warning")
    void shouldWarnAboutNonexistentInterface() throws Exception {
        String config = "route add 0.0.0.0/0 via 192.168.1.254 dev eth99 metric 1";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Interface not found: eth99"));
    }
    
    @Test
    @DisplayName("Invalid security action should trigger validation error")
    void shouldDetectInvalidSecurityAction() throws Exception {
        String config = "security policy test_policy INVALID";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Invalid action: INVALID"));
    }
    
    @Test
    @DisplayName("Invalid protocol should trigger validation error")
    void shouldDetectInvalidProtocol() throws Exception {
        String config = "security policy test_policy ALLOW protocol INVALID";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Unsupported protocol: INVALID"));
    }
    
    @Test
    @DisplayName("Invalid port number should trigger validation error")
    void shouldDetectInvalidPortNumber() throws Exception {
        String config = "security policy test_policy ALLOW port 70000";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Port must be between 1 and 65535"));
    }
    
    @Test
    @DisplayName("Unknown command should trigger parse error")
    void shouldDetectUnknownCommand() {
        String config = "unknown_command param1 param2";
        
        assertThatThrownBy(() -> parser.parseConfiguration(config))
            .isInstanceOf(ConfigurationParseException.class)
            .hasMessageContaining("Unknown command: unknown_command");
    }
    
    @Test
    @DisplayName("Malformed interface command should trigger parse error")
    void shouldDetectMalformedInterfaceCommand() {
        String config = "interface eth0 ip"; // Missing IP address
        
        assertThatThrownBy(() -> parser.parseConfiguration(config))
            .isInstanceOf(ConfigurationParseException.class)
            .hasMessageContaining("Interface command requires at least");
    }
    
    @Test
    @DisplayName("Malformed route command should trigger parse error")
    void shouldDetectMalformedRouteCommand() {
        String config = "route add 0.0.0.0/0"; // Missing via clause
        
        assertThatThrownBy(() -> parser.parseConfiguration(config))
            .isInstanceOf(ConfigurationParseException.class)
            .hasMessageContaining("Route command format");
    }
    
    @Test
    @DisplayName("QoS on nonexistent interface should trigger validation error")
    void shouldDetectQosOnNonexistentInterface() throws Exception {
        String config = "qos bandwidth nonexistent_interface 1000Mbps";
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        expressions.get(0).validate(context, 1);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Interface not found: nonexistent_interface"));
    }
    
    @Test
    @DisplayName("Bandwidth unit parsing should work correctly")
    void shouldParseBandwidthUnitsCorrectly() throws Exception {
        String config = """
            interface eth0 ip 192.168.1.1 subnet 255.255.255.0
            interface eth1 ip 192.168.2.1 subnet 255.255.255.0
            qos bandwidth eth0 1000Mbps
            qos bandwidth eth1 5Gbps
            """;
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        InterpretationContext context = new InterpretationContext();
        
        // Interpret all expressions
        for (int i = 0; i < expressions.size(); i++) {
            expressions.get(i).validate(context, i + 1);
            expressions.get(i).interpret(context);
        }
        
        assertThat(context.hasErrors()).isFalse();
        
        NetworkInterface eth0 = context.getInterface("eth0");
        NetworkInterface eth1 = context.getInterface("eth1");
        
        assertThat(eth0.getBandwidth()).isEqualTo(1000); // 1000 Mbps
        assertThat(eth1.getBandwidth()).isEqualTo(5000); // 5 Gbps = 5000 Mbps
    }
    
    @Test
    @DisplayName("Comments and empty lines should be ignored")
    void shouldIgnoreCommentsAndEmptyLines() throws Exception {
        String config = """
            # This is a comment
            
            interface eth0 ip 192.168.1.1 subnet 255.255.255.0
            # Another comment
            
            route add 0.0.0.0/0 via 192.168.1.254
            
            # Final comment
            """;
        
        List<ConfigurationExpression> expressions = parser.parseConfiguration(config);
        
        // Should only parse the actual commands, ignoring comments and empty lines
        assertThat(expressions).hasSize(2);
    }
    
    @Test
    @DisplayName("Configuration validation should provide helpful line numbers")
    void shouldProvideHelpfulLineNumbersInValidation() {
        String config = """
            interface eth0 ip 192.168.1.1 subnet 255.255.255.0
            interface eth1 ip 999.999.999.999 subnet 255.255.255.0
            route add 0.0.0.0/0 via 192.168.1.254
            """;
        
        InterpretationContext context = interpreter.executeConfiguration(config);
        
        assertThat(context.hasErrors()).isTrue();
        assertThat(context.getErrors()).anyMatch(error -> 
            error.contains("Line 2") && error.contains("Invalid IP address"));
    }
    
    @Test
    @DisplayName("Full interpreter should handle configuration with warnings")
    void shouldHandleConfigurationWithWarnings() {
        String config = """
            interface eth0 ip 192.168.1.1 subnet 255.255.255.0
            security policy test_policy ALLOW on eth1
            """;
        
        InterpretationContext context = interpreter.executeConfiguration(config);
        
        // Should complete successfully but have warnings
        assertThat(context.hasErrors()).isFalse();
        assertThat(context.getWarnings()).isNotEmpty();
        assertThat(context.getWarnings()).anyMatch(warning -> 
            warning.contains("Interface eth1 not yet defined"));
    }
}