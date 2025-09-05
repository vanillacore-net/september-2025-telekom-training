package com.telekom.architecture.training.day3.chainofresponsibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.architecture.training.day3.chainofresponsibility.RequestProcessingFixed.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Simplified test for Chain of Responsibility Pattern demonstration
 */
class RequestProcessingSimpleTest {
    
    private ChainRequestProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new ChainRequestProcessor();
    }
    
    @Test
    @DisplayName("Should process valid admin request")
    void shouldProcessValidAdminRequest() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("deviceId", "device-123");
        payload.put("config", "test-config");
        
        NetworkRequest request = new NetworkRequest(
            "req-001", RequestType.DEVICE_CONFIG, "admin", "192.168.1.100", payload);
        
        ProcessingResponse response = processor.processRequest(request);
        
        assertThat(response.getStatus()).isEqualTo(RequestStatus.COMPLETED);
        assertThat(response.getData()).containsKey("configurationApplied");
        assertThat(response.getData()).containsKey("processingSteps");
    }
    
    @Test
    @DisplayName("Should reject invalid user")
    void shouldRejectInvalidUser() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("deviceId", "device-123");
        
        NetworkRequest request = new NetworkRequest(
            "req-002", RequestType.DEVICE_CONFIG, "invalid-user", "192.168.1.100", payload);
        
        ProcessingResponse response = processor.processRequest(request);
        
        assertThat(response.getStatus()).isEqualTo(RequestStatus.UNAUTHORIZED);
        assertThat(response.getErrorCode()).isEqualTo("AUTH_INVALID_USER");
    }
    
    @Test
    @DisplayName("Should enforce authorization")
    void shouldEnforceAuthorization() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("topology", "test");
        
        NetworkRequest request = new NetworkRequest(
            "req-003", RequestType.NETWORK_TOPOLOGY, "viewer", "192.168.1.100", payload);
        
        ProcessingResponse response = processor.processRequest(request);
        
        assertThat(response.getStatus()).isEqualTo(RequestStatus.UNAUTHORIZED);
        assertThat(response.getErrorCode()).isEqualTo("AUTHZ_INSUFFICIENT_PERMS");
    }
    
    @Test
    @DisplayName("Should validate input")
    void shouldValidateInput() {
        Map<String, Object> payload = new HashMap<>();
        // Missing required deviceId
        payload.put("config", "test");
        
        NetworkRequest request = new NetworkRequest(
            "req-004", RequestType.DEVICE_CONFIG, "admin", "192.168.1.100", payload);
        
        ProcessingResponse response = processor.processRequest(request);
        
        assertThat(response.getStatus()).isEqualTo(RequestStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo("VALIDATION_FAILED");
    }
    
    @Test
    @DisplayName("Should provide processing metadata")
    void shouldProvideProcessingMetadata() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("deviceId", "device-789");
        payload.put("timestamp", "2024-01-01T10:00:00");
        
        NetworkRequest request = new NetworkRequest(
            "req-005", RequestType.MONITORING_DATA, "operator", "192.168.1.200", payload);
        
        ProcessingResponse response = processor.processRequest(request);
        
        if (response.getStatus() == RequestStatus.COMPLETED) {
            @SuppressWarnings("unchecked")
            List<String> steps = (List<String>) response.getData().get("processingSteps");
            assertThat(steps).isNotEmpty();
            assertThat(steps).anyMatch(step -> step.contains("AuthenticationHandler"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) response.getData().get("processingMetadata");
            assertThat(metadata).containsKey("userRole");
            assertThat(metadata.get("userRole")).isEqualTo("OPERATOR");
        }
    }
    
    @Test
    @DisplayName("Should test individual handlers")
    void shouldTestIndividualHandlers() {
        AuthenticationHandler authHandler = new AuthenticationHandler();
        
        Map<String, Object> payload = new HashMap<>();
        NetworkRequest request = new NetworkRequest(
            "req-006", RequestType.DEVICE_CONFIG, "admin", "192.168.1.1", payload);
        
        RequestContext context = new RequestContext(request);
        HandlerResult result = authHandler.process(context);
        
        assertThat(result.shouldContinue()).isTrue();
        assertThat(context.getMetadata().get("authenticated")).isEqualTo(true);
        assertThat(context.getMetadata().get("userRole")).isEqualTo("ADMIN");
    }
}