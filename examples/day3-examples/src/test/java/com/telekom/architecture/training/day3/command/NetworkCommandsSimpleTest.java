package com.telekom.architecture.training.day3.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.architecture.training.day3.command.NetworkCommandsFixed.*;

/**
 * Simplified test for Command Pattern demonstration
 */
class NetworkCommandsSimpleTest {
    
    private NetworkDevice networkDevice;
    private NetworkCommandInvoker invoker;
    
    @BeforeEach
    void setUp() {
        networkDevice = new NetworkDevice("test-device");
        invoker = new NetworkCommandInvoker();
    }
    
    @Test
    @DisplayName("Should execute connect device command")
    void shouldExecuteConnectCommand() {
        // Start disconnected
        networkDevice.disconnect();
        assertThat(networkDevice.isConnected()).isFalse();
        
        NetworkCommand connectCommand = new ConnectDeviceCommand(networkDevice);
        invoker.executeCommand(connectCommand);
        
        assertThat(networkDevice.isConnected()).isTrue();
    }
    
    @Test
    @DisplayName("Should undo connect command")
    void shouldUndoConnectCommand() {
        // Start disconnected
        networkDevice.disconnect();
        assertThat(networkDevice.isConnected()).isFalse();
        
        // Connect via command
        NetworkCommand connectCommand = new ConnectDeviceCommand(networkDevice);
        invoker.executeCommand(connectCommand);
        assertThat(networkDevice.isConnected()).isTrue();
        
        // Undo connect
        invoker.undo();
        assertThat(networkDevice.isConnected()).isFalse();
    }
    
    @Test
    @DisplayName("Should execute add configuration command")
    void shouldExecuteAddConfigCommand() {
        NetworkCommand configCommand = new AddConfigurationCommand(networkDevice, "hostname=test-device");
        invoker.executeCommand(configCommand);
        
        assertThat(networkDevice.getConfiguration()).contains("hostname=test-device");
    }
    
    @Test
    @DisplayName("Should undo add configuration")
    void shouldUndoAddConfiguration() {
        NetworkCommand configCommand = new AddConfigurationCommand(networkDevice, "ip=10.0.0.1");
        invoker.executeCommand(configCommand);
        
        assertThat(networkDevice.getConfiguration()).contains("ip=10.0.0.1");
        
        invoker.undo();
        assertThat(networkDevice.getConfiguration()).doesNotContain("ip=10.0.0.1");
    }
    
    @Test
    @DisplayName("Should execute enable interface command")
    void shouldExecuteEnableInterfaceCommand() {
        NetworkCommand enableCommand = new EnableInterfaceCommand(networkDevice, "eth0");
        invoker.executeCommand(enableCommand);
        
        assertThat(networkDevice.getInterfaces()).contains("eth0");
    }
    
    @Test
    @DisplayName("Should execute reboot command")
    void shouldExecuteRebootCommand() {
        NetworkCommand rebootCommand = new RebootDeviceCommand(networkDevice);
        invoker.executeCommand(rebootCommand);
        
        assertThat(networkDevice.getStatus()).isEqualTo("ONLINE");
    }
    
    @Test
    @DisplayName("Should maintain command history")
    void shouldMaintainCommandHistory() {
        NetworkCommand connect = new ConnectDeviceCommand(networkDevice);
        NetworkCommand config = new AddConfigurationCommand(networkDevice, "test=value");
        
        invoker.executeCommand(connect);
        invoker.executeCommand(config);
        
        // Check we can undo multiple times
        invoker.undo();
        invoker.undo();
        
        // After undoing both, should be able to execute new commands
        NetworkCommand newConfig = new AddConfigurationCommand(networkDevice, "new=value");
        invoker.executeCommand(newConfig);
        
        assertThat(networkDevice.getConfiguration()).contains("new=value");
    }
    
    @Test
    @DisplayName("Should support command validation")
    void shouldSupportCommandValidation() {
        // Connect command should only work on disconnected devices
        NetworkCommand connectCommand = new ConnectDeviceCommand(networkDevice);
        assertThat(connectCommand.validate()).isFalse(); // Device starts connected
        
        // Disconnect first
        networkDevice.disconnect();
        assertThat(connectCommand.validate()).isTrue(); // Now it should be valid
        
        // Disconnect command should only work on connected devices
        NetworkCommand disconnectCommand = new DisconnectDeviceCommand(networkDevice);
        assertThat(disconnectCommand.validate()).isFalse(); // Device is disconnected
        
        networkDevice.connect();
        assertThat(disconnectCommand.validate()).isTrue(); // Now it should be valid
    }
    
    @Test
    @DisplayName("Should execute macro commands")
    void shouldExecuteMacroCommands() {
        // Start with disconnected device
        networkDevice.disconnect();
        
        java.util.List<NetworkCommand> commands = new java.util.ArrayList<>();
        commands.add(new ConnectDeviceCommand(networkDevice));
        commands.add(new AddConfigurationCommand(networkDevice, "hostname=macro-device"));
        commands.add(new EnableInterfaceCommand(networkDevice, "eth0"));
        
        MacroCommand macro = new MacroCommand("Device Setup", commands);
        invoker.executeCommand(macro);
        
        assertThat(networkDevice.isConnected()).isTrue();
        assertThat(networkDevice.getConfiguration()).contains("hostname=macro-device");
        assertThat(networkDevice.getInterfaces()).contains("eth0");
    }
    
    @Test
    @DisplayName("Should handle empty undo stack gracefully")
    void shouldHandleEmptyUndoStack() {
        // Try to undo when no commands have been executed
        // This should not throw an exception
        assertThatNoException().isThrownBy(() -> invoker.undo());
    }
}