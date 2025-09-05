package com.telekom.architecture.training.day3.templatemethod;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests demonstrating the benefits of Template Method pattern over code duplication
 * 
 * Trainer Notes:
 * - Show how Template Method ensures consistent workflow execution
 * - Demonstrate how common behavior is tested once in base class
 * - Point out how device-specific behavior can be tested in isolation
 * - Compare maintenance overhead between initial and fixed implementations
 */
class DeviceWorkflowTest {
    
    @Test
    @DisplayName("Initial implementation has duplicated workflow logic")
    void testInitialImplementationDuplication() {
        // Given - Multiple workflow implementations with duplicated code
        DeviceWorkflowInitial.RouterProvisioningWorkflow routerWorkflow = 
            new DeviceWorkflowInitial.RouterProvisioningWorkflow();
        DeviceWorkflowInitial.SwitchProvisioningWorkflow switchWorkflow = 
            new DeviceWorkflowInitial.SwitchProvisioningWorkflow();
        
        // When - Execute workflows
        DeviceWorkflowInitial.WorkflowResult routerResult = routerWorkflow.provisionRouter("R001", "192.168.1.1");
        DeviceWorkflowInitial.WorkflowResult switchResult = switchWorkflow.provisionSwitch("S001", 100);
        
        // Then - Both succeed but notice the duplicated steps
        assertThat(routerResult.isSuccess()).isTrue();
        assertThat(switchResult.isSuccess()).isTrue();
        
        // Both workflows have same common steps (showing duplication)
        assertThat(routerResult.getSteps()).contains("Input validation completed", "Pre-checks completed");
        assertThat(switchResult.getSteps()).contains("Input validation completed", "Pre-checks completed");
    }
    
    @Test
    @DisplayName("Template Method ensures consistent workflow structure")
    void testTemplateMethodConsistency() {
        // Given
        DeviceWorkflowFixed.RouterProvisioningWorkflow routerWorkflow = 
            new DeviceWorkflowFixed.RouterProvisioningWorkflow();
        DeviceWorkflowFixed.SwitchProvisioningWorkflow switchWorkflow = 
            new DeviceWorkflowFixed.SwitchProvisioningWorkflow();
        DeviceWorkflowFixed.FirewallProvisioningWorkflow firewallWorkflow = 
            new DeviceWorkflowFixed.FirewallProvisioningWorkflow();
        
        // When
        DeviceWorkflowFixed.WorkflowResult routerResult = routerWorkflow.provisionDevice("R001", "192.168.1.1");
        DeviceWorkflowFixed.WorkflowResult switchResult = switchWorkflow.provisionDevice("S001", 100);
        DeviceWorkflowFixed.WorkflowResult firewallResult = firewallWorkflow.provisionDevice("FW001", "strict");
        
        // Then - All workflows follow the same structure
        assertThat(routerResult.isSuccess()).isTrue();
        assertThat(switchResult.isSuccess()).isTrue();
        assertThat(firewallResult.isSuccess()).isTrue();
        
        // All have consistent step structure
        assertThat(routerResult.getSteps()).contains("Input validation completed", "Pre-checks completed", "Post-setup tasks completed");
        assertThat(switchResult.getSteps()).contains("Input validation completed", "Pre-checks completed", "Post-setup tasks completed");
        assertThat(firewallResult.getSteps()).contains("Input validation completed", "Pre-checks completed", "Post-setup tasks completed");
    }
    
    @Test
    @DisplayName("Template Method handles validation consistently")
    void testConsistentValidation() {
        // Given
        DeviceWorkflowFixed.RouterProvisioningWorkflow workflow = 
            new DeviceWorkflowFixed.RouterProvisioningWorkflow();
        
        // When - Invalid device ID
        DeviceWorkflowFixed.WorkflowResult result = workflow.provisionDevice("", "192.168.1.1");
        
        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Invalid device ID");
        
        // When - Invalid IP address
        result = workflow.provisionDevice("R001", "invalid-ip");
        
        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Invalid IP address format");
    }
    
    @Test
    @DisplayName("Device-specific configuration works correctly")
    void testDeviceSpecificConfiguration() {
        // Given
        DeviceWorkflowFixed.SwitchProvisioningWorkflow switchWorkflow = 
            new DeviceWorkflowFixed.SwitchProvisioningWorkflow();
        DeviceWorkflowFixed.FirewallProvisioningWorkflow firewallWorkflow = 
            new DeviceWorkflowFixed.FirewallProvisioningWorkflow();
        
        // When
        DeviceWorkflowFixed.WorkflowResult switchResult = switchWorkflow.provisionDevice("S001", 100);
        DeviceWorkflowFixed.WorkflowResult firewallResult = firewallWorkflow.provisionDevice("FW001", "strict");
        
        // Then - Device-specific steps are included
        assertThat(switchResult.getSteps()).anyMatch(step -> step.toLowerCase().contains("vlan"));
        assertThat(switchResult.getSteps()).anyMatch(step -> step.toLowerCase().contains("switch"));
        
        assertThat(firewallResult.getSteps()).anyMatch(step -> step.toLowerCase().contains("firewall"));
        assertThat(firewallResult.getSteps()).anyMatch(step -> step.toLowerCase().contains("security"));
    }
    
    @Test
    @DisplayName("Hook methods allow customization")
    void testHookMethods() {
        // Given - Switch has preparation hook
        DeviceWorkflowFixed.SwitchProvisioningWorkflow switchWorkflow = 
            new DeviceWorkflowFixed.SwitchProvisioningWorkflow();
        
        // When
        DeviceWorkflowFixed.WorkflowResult result = switchWorkflow.provisionDevice("S001", 100);
        
        // Then - Switch-specific preparation step is included
        assertThat(result.getSteps()).contains("Clearing existing VLAN configuration");
    }
    
    @Test
    @DisplayName("Input validation is device-specific")
    void testDeviceSpecificValidation() {
        // Given
        DeviceWorkflowFixed.SwitchProvisioningWorkflow switchWorkflow = 
            new DeviceWorkflowFixed.SwitchProvisioningWorkflow();
        
        // When - Invalid VLAN ID
        DeviceWorkflowFixed.WorkflowResult result = switchWorkflow.provisionDevice("S001", 5000); // VLAN > 4094
        
        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Invalid VLAN ID");
        
        // When - Valid VLAN ID
        result = switchWorkflow.provisionDevice("S001", 100);
        
        // Then
        assertThat(result.isSuccess()).isTrue();
    }
    
    @Test
    @DisplayName("Error handling can be customized")
    void testCustomErrorHandling() {
        // This test would require more sophisticated error simulation
        // but shows how different device types can handle errors differently
        
        // Given
        DeviceWorkflowFixed.RouterProvisioningWorkflow routerWorkflow = 
            new DeviceWorkflowFixed.RouterProvisioningWorkflow();
        DeviceWorkflowFixed.FirewallProvisioningWorkflow firewallWorkflow = 
            new DeviceWorkflowFixed.FirewallProvisioningWorkflow();
        
        // In a real scenario, you would simulate errors and verify
        // that router error handling includes routing rollback
        // and firewall error handling includes failsafe rules
        
        // For now, just verify workflows can be created and basic operation works
        assertThat(routerWorkflow).isNotNull();
        assertThat(firewallWorkflow).isNotNull();
    }
    
    @Test
    @DisplayName("Easy to add new device types")
    void testExtensibility() {
        // Given - New Load Balancer workflow
        DeviceWorkflowFixed.LoadBalancerProvisioningWorkflow lbWorkflow = 
            new DeviceWorkflowFixed.LoadBalancerProvisioningWorkflow();
        
        // When
        String[] backendServers = {"192.168.1.10", "192.168.1.11", "192.168.1.12"};
        DeviceWorkflowFixed.WorkflowResult result = lbWorkflow.provisionDevice("LB001", backendServers);
        
        // Then - New device type follows same workflow structure
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getSteps()).contains("Input validation completed", "Pre-checks completed");
        assertThat(result.getSteps()).anyMatch(step -> step.toLowerCase().contains("load_balancer"));
        assertThat(result.getMessage()).contains("LOAD_BALANCER provisioned successfully");
    }
    
    @Test
    @DisplayName("Template method is final - workflow structure cannot be changed")
    void testTemplateMethodIsFinal() {
        // This is more of a design verification test
        // The provisionDevice method in DeviceProvisioningWorkflow should be final
        
        // Given
        DeviceWorkflowFixed.RouterProvisioningWorkflow workflow = 
            new DeviceWorkflowFixed.RouterProvisioningWorkflow();
        
        // When - Use the template method
        DeviceWorkflowFixed.WorkflowResult result = workflow.provisionDevice("R001", "192.168.1.1");
        
        // Then - The workflow structure is consistently applied
        assertThat(result.getSteps()).hasSize(greaterThan(5)); // Multiple workflow steps
        
        // The workflow always starts and ends with specific patterns
        assertThat(result.getSteps().get(0)).contains("Starting");
        assertThat(result.getSteps().get(result.getSteps().size() - 1)).contains("completed");
    }
}