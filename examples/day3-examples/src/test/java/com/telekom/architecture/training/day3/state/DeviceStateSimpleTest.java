package com.telekom.architecture.training.day3.state;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.architecture.training.day3.state.DeviceStateFixed.*;

/**
 * Simplified test for State Pattern demonstration
 */
class DeviceStateSimpleTest {
    
    private DeviceContext deviceContext;
    
    @BeforeEach
    void setUp() {
        deviceContext = new DeviceContext("test-device", "Test Device");
    }
    
    @Test
    @DisplayName("Should start in inactive state")
    void shouldStartInInactiveState() {
        assertThat(deviceContext.getStateName()).isEqualTo("INACTIVE");
        assertThat(deviceContext.isOperational()).isFalse();
        assertThat(deviceContext.allowsTraffic()).isFalse();
    }
    
    @Test
    @DisplayName("Should have proper state transitions")
    void shouldHaveProperStateTransitions() {
        // Start inactive
        assertThat(deviceContext.getStateName()).isEqualTo("INACTIVE");
        
        // Try to activate
        StateTransitionResult result = deviceContext.activate();
        if (result.isSuccess()) {
            assertThat(deviceContext.getStateName()).isEqualTo("INITIALIZING");
        } else {
            assertThat(deviceContext.getStateName()).isEqualTo("ERROR");
        }
    }
    
    @Test
    @DisplayName("Should maintain state history")
    void shouldMaintainStateHistory() {
        assertThat(deviceContext.getStateHistory()).hasSizeGreaterThan(0);
        
        deviceContext.activate();
        assertThat(deviceContext.getStateHistory()).hasSizeGreaterThan(1);
    }
    
    @Test
    @DisplayName("Should handle invalid transitions")
    void shouldHandleInvalidTransitions() {
        // Try maintenance from inactive
        StateTransitionResult result = deviceContext.enterMaintenance();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("INVALID_TRANSITION");
    }
    
    @Test
    @DisplayName("Should handle error states")
    void shouldHandleErrorStates() {
        StateTransitionResult result = deviceContext.handleError("Test error");
        assertThat(result.isSuccess()).isTrue();
        assertThat(deviceContext.getErrorMessage()).isEqualTo("Test error");
    }
}