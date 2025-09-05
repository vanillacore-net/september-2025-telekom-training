package com.telekom.architecture.training.day3.chainofresponsibility;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * INITIAL (PROBLEMATIC) IMPLEMENTATION
 * 
 * Problems:
 * - Monolithic request processor with all validation logic
 * - Hard to add new validation steps
 * - Tight coupling between different processing concerns
 * - Difficult to test individual processing steps
 * - Violation of Single Responsibility Principle
 * 
 * Trainer Notes:
 * - Show how processing logic becomes unmanageable
 * - Demonstrate difficulty in changing processing order
 * - Point out code duplication and scattered validation
 */
public class RequestProcessingInitial {
    
    public enum RequestType {
        DEVICE_CONFIG, NETWORK_TOPOLOGY, MONITORING_DATA, MAINTENANCE_SCHEDULING
    }
    
    public enum RequestStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, UNAUTHORIZED
    }
    
    public static class NetworkRequest {
        private final String id;
        private final RequestType type;
        private final String userId;
        private final String sourceIp;
        private final Map<String, Object> payload;
        private final Map<String, String> headers;
        private final LocalDateTime timestamp;
        
        public NetworkRequest(String id, RequestType type, String userId, String sourceIp, Map<String, Object> payload) {
            this.id = id;
            this.type = type;
            this.userId = userId;
            this.sourceIp = sourceIp;
            this.payload = payload != null ? new HashMap<>(payload) : new HashMap<>();
            this.headers = new HashMap<>();
            this.timestamp = LocalDateTime.now();
        }
        
        // Getters
        public String getId() { return id; }
        public RequestType getType() { return type; }
        public String getUserId() { return userId; }
        public String getSourceIp() { return sourceIp; }
        public Map<String, Object> getPayload() { return new HashMap<>(payload); }
        public Map<String, String> getHeaders() { return new HashMap<>(headers); }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        public void addHeader(String key, String value) {
            headers.put(key, value);
        }
    }
    
    public static class ProcessingResponse {
        private final String requestId;
        private final RequestStatus status;
        private final String message;
        private final Map<String, Object> data;
        private final String errorCode;
        private final long processingTimeMs;
        
        public ProcessingResponse(String requestId, RequestStatus status, String message, 
                                Map<String, Object> data, String errorCode, long processingTimeMs) {
            this.requestId = requestId;
            this.status = status;
            this.message = message;
            this.data = data != null ? new HashMap<>(data) : new HashMap<>();
            this.errorCode = errorCode;
            this.processingTimeMs = processingTimeMs;
        }
        
        // Getters
        public String getRequestId() { return requestId; }
        public RequestStatus getStatus() { return status; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return new HashMap<>(data); }
        public String getErrorCode() { return errorCode; }
        public long getProcessingTimeMs() { return processingTimeMs; }
    }
    
    /**
     * PROBLEMATIC: Monolithic request processor containing all processing logic
     */
    public static class RequestProcessor {
        private final Map<String, String> userRoles = new HashMap<>();
        private final Map<String, Integer> rateLimitCounts = new HashMap<>();
        
        public RequestProcessor() {
            // Initialize some test users
            userRoles.put("admin", "ADMIN");
            userRoles.put("operator", "OPERATOR");
            userRoles.put("viewer", "VIEWER");
        }
        
        /**
         * PROBLEMATIC: All processing logic in one massive method
         */
        public ProcessingResponse processRequest(NetworkRequest request) {
            long startTime = System.currentTimeMillis();
            
            try {
                // STEP 1: Authentication - Hardcoded in method
                if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                    return new ProcessingResponse(request.getId(), RequestStatus.UNAUTHORIZED, 
                        "User ID is required", null, "AUTH_MISSING_USER", 
                        System.currentTimeMillis() - startTime);
                }
                
                if (!userRoles.containsKey(request.getUserId())) {
                    return new ProcessingResponse(request.getId(), RequestStatus.UNAUTHORIZED, 
                        "Invalid user ID", null, "AUTH_INVALID_USER", 
                        System.currentTimeMillis() - startTime);
                }
                
                // STEP 2: Authorization - Another hardcoded block
                String userRole = userRoles.get(request.getUserId());
                if (!isAuthorizedForRequestType(userRole, request.getType())) {
                    return new ProcessingResponse(request.getId(), RequestStatus.UNAUTHORIZED, 
                        "Insufficient permissions for request type: " + request.getType(), 
                        null, "AUTHZ_INSUFFICIENT_PERMS", System.currentTimeMillis() - startTime);
                }
                
                // STEP 3: Rate Limiting - Yet another hardcoded block
                String rateLimitKey = request.getUserId() + ":" + request.getSourceIp();
                int currentCount = rateLimitCounts.getOrDefault(rateLimitKey, 0);
                
                if (currentCount >= getRateLimitForUser(userRole)) {
                    return new ProcessingResponse(request.getId(), RequestStatus.FAILED, 
                        "Rate limit exceeded", null, "RATE_LIMIT_EXCEEDED", 
                        System.currentTimeMillis() - startTime);
                }
                
                rateLimitCounts.put(rateLimitKey, currentCount + 1);
                
                // STEP 4: Input Validation - More hardcoded logic
                String validationError = validateRequestPayload(request);
                if (validationError != null) {
                    return new ProcessingResponse(request.getId(), RequestStatus.FAILED, 
                        "Input validation failed: " + validationError, null, "VALIDATION_FAILED", 
                        System.currentTimeMillis() - startTime);
                }
                
                // STEP 5: Business Logic Processing - Even more hardcoded logic
                Map<String, Object> result = processBusinessLogic(request);
                
                // STEP 6: Response Transformation - Final hardcoded block
                Map<String, Object> transformedResult = transformResponse(request, result);
                
                return new ProcessingResponse(request.getId(), RequestStatus.COMPLETED, 
                    "Request processed successfully", transformedResult, null, 
                    System.currentTimeMillis() - startTime);
                    
            } catch (Exception e) {
                return new ProcessingResponse(request.getId(), RequestStatus.FAILED, 
                    "Processing error: " + e.getMessage(), null, "PROCESSING_ERROR", 
                    System.currentTimeMillis() - startTime);
            }
        }
        
        /**
         * PROBLEMATIC: Authorization logic scattered throughout the class
         */
        private boolean isAuthorizedForRequestType(String role, RequestType type) {
            switch (role) {
                case "ADMIN":
                    return true; // Admin can do everything
                case "OPERATOR":
                    return type != RequestType.NETWORK_TOPOLOGY; // Operators cannot modify topology
                case "VIEWER":
                    return type == RequestType.MONITORING_DATA; // Viewers can only read monitoring data
                default:
                    return false;
            }
        }
        
        /**
         * PROBLEMATIC: Rate limiting logic hardcoded
         */
        private int getRateLimitForUser(String role) {
            switch (role) {
                case "ADMIN":
                    return 1000;
                case "OPERATOR":
                    return 500;
                case "VIEWER":
                    return 100;
                default:
                    return 10;
            }
        }
        
        /**
         * PROBLEMATIC: Validation logic becomes very complex
         */
        private String validateRequestPayload(NetworkRequest request) {
            Map<String, Object> payload = request.getPayload();
            
            switch (request.getType()) {
                case DEVICE_CONFIG:
                    if (!payload.containsKey("deviceId")) {
                        return "Device ID is required for device configuration";
                    }
                    if (!payload.containsKey("config")) {
                        return "Configuration data is required";
                    }
                    // More device config validation...
                    break;
                    
                case NETWORK_TOPOLOGY:
                    if (!payload.containsKey("topology")) {
                        return "Topology data is required";
                    }
                    // More topology validation...
                    break;
                    
                case MONITORING_DATA:
                    if (!payload.containsKey("deviceId")) {
                        return "Device ID is required for monitoring data";
                    }
                    if (!payload.containsKey("timestamp")) {
                        return "Timestamp is required for monitoring data";
                    }
                    // More monitoring validation...
                    break;
                    
                case MAINTENANCE_SCHEDULING:
                    if (!payload.containsKey("deviceId")) {
                        return "Device ID is required for maintenance scheduling";
                    }
                    if (!payload.containsKey("scheduledTime")) {
                        return "Scheduled time is required";
                    }
                    // More maintenance validation...
                    break;
                    
                default:
                    return "Unknown request type";
            }
            
            return null; // No validation errors
        }
        
        /**
         * PROBLEMATIC: Business logic handling all request types
         */
        private Map<String, Object> processBusinessLogic(NetworkRequest request) {
            Map<String, Object> result = new HashMap<>();
            
            switch (request.getType()) {
                case DEVICE_CONFIG:
                    // Simulate device configuration
                    result.put("configurationApplied", true);
                    result.put("deviceId", request.getPayload().get("deviceId"));
                    result.put("timestamp", LocalDateTime.now());
                    break;
                    
                case NETWORK_TOPOLOGY:
                    // Simulate topology update
                    result.put("topologyUpdated", true);
                    result.put("nodesAffected", 15);
                    result.put("timestamp", LocalDateTime.now());
                    break;
                    
                case MONITORING_DATA:
                    // Simulate monitoring data retrieval
                    result.put("monitoringData", generateMonitoringData());
                    result.put("timestamp", LocalDateTime.now());
                    break;
                    
                case MAINTENANCE_SCHEDULING:
                    // Simulate maintenance scheduling
                    result.put("maintenanceScheduled", true);
                    result.put("maintenanceId", "MAINT-" + System.currentTimeMillis());
                    result.put("timestamp", LocalDateTime.now());
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown request type: " + request.getType());
            }
            
            return result;
        }
        
        /**
         * PROBLEMATIC: Response transformation logic also hardcoded
         */
        private Map<String, Object> transformResponse(NetworkRequest request, Map<String, Object> businessResult) {
            Map<String, Object> response = new HashMap<>(businessResult);
            response.put("requestId", request.getId());
            response.put("requestType", request.getType().toString());
            response.put("processedBy", "RequestProcessor");
            response.put("processingTimestamp", LocalDateTime.now());
            
            return response;
        }
        
        private Map<String, Object> generateMonitoringData() {
            Map<String, Object> data = new HashMap<>();
            data.put("cpuUsage", (int)(Math.random() * 100));
            data.put("memoryUsage", (int)(Math.random() * 100));
            data.put("networkThroughput", (int)(Math.random() * 1000));
            return data;
        }
    }
}