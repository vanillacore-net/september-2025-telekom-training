package com.telekom.architecture.training.day3.chainofresponsibility;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * FIXED IMPLEMENTATION using CHAIN OF RESPONSIBILITY PATTERN
 * 
 * Benefits:
 * - Each handler has single responsibility
 * - Easy to add, remove, or reorder handlers
 * - Loose coupling between request sender and handlers
 * - Flexible and configurable processing pipeline
 * - Better testability of individual processing steps
 * 
 * Trainer Notes:
 * - Show how Chain of Responsibility eliminates monolithic processing
 * - Demonstrate easy extensibility and configuration
 * - Point out improved separation of concerns
 * - Show how order of handlers can be easily changed
 */
public class RequestProcessingFixed {
    
    // Copy enums from initial implementation
    public enum RequestType {
        DEVICE_CONFIG, NETWORK_TOPOLOGY, MONITORING_DATA, MAINTENANCE_SCHEDULING
    }
    
    public enum RequestStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, UNAUTHORIZED
    }
    
    // Enhanced request context that travels through the chain
    public static class RequestContext {
        private final NetworkRequest request;
        private final Map<String, Object> metadata;
        private final List<String> processingSteps;
        private final long startTime;
        
        public RequestContext(NetworkRequest request) {
            this.request = request;
            this.metadata = new HashMap<>();
            this.processingSteps = new ArrayList<>();
            this.startTime = System.currentTimeMillis();
        }
        
        public NetworkRequest getRequest() { return request; }
        public Map<String, Object> getMetadata() { return metadata; }
        public List<String> getProcessingSteps() { return new ArrayList<>(processingSteps); }
        public long getStartTime() { return startTime; }
        
        public void addMetadata(String key, Object value) {
            metadata.put(key, value);
        }
        
        public void addProcessingStep(String step) {
            processingSteps.add(LocalDateTime.now() + ": " + step);
        }
    }
    
    // Copy NetworkRequest class
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
    
    // Handler result
    public static class HandlerResult {
        private final boolean shouldContinue;
        private final ProcessingResponse response; // null if should continue
        private final String message;
        
        public HandlerResult(boolean shouldContinue, String message) {
            this.shouldContinue = shouldContinue;
            this.response = null;
            this.message = message;
        }
        
        public HandlerResult(ProcessingResponse response, String message) {
            this.shouldContinue = false;
            this.response = response;
            this.message = message;
        }
        
        public boolean shouldContinue() { return shouldContinue; }
        public ProcessingResponse getResponse() { return response; }
        public String getMessage() { return message; }
        
        public static HandlerResult continueProcessing(String message) {
            return new HandlerResult(true, message);
        }
        
        public static HandlerResult stopWithResponse(ProcessingResponse response, String message) {
            return new HandlerResult(response, message);
        }
    }
    
    // Abstract Handler class
    public static abstract class RequestHandler {
        protected RequestHandler nextHandler;
        
        public RequestHandler setNext(RequestHandler handler) {
            this.nextHandler = handler;
            return handler;
        }
        
        public ProcessingResponse handle(RequestContext context) {
            HandlerResult result = process(context);
            
            context.addProcessingStep(getHandlerName() + ": " + result.getMessage());
            
            if (!result.shouldContinue()) {
                return result.getResponse();
            }
            
            if (nextHandler != null) {
                return nextHandler.handle(context);
            }
            
            // No more handlers and no response - this shouldn't happen in a well-designed chain
            return new ProcessingResponse(
                context.getRequest().getId(),
                RequestStatus.FAILED,
                "Request not handled by any handler in the chain",
                null,
                "NO_HANDLER",
                System.currentTimeMillis() - context.getStartTime()
            );
        }
        
        protected abstract HandlerResult process(RequestContext context);
        protected abstract String getHandlerName();
    }
    
    // Concrete Handler: Authentication
    public static class AuthenticationHandler extends RequestHandler {
        private final Map<String, String> validUsers;
        
        public AuthenticationHandler() {
            this.validUsers = new HashMap<>();
            validUsers.put("admin", "ADMIN");
            validUsers.put("operator", "OPERATOR");
            validUsers.put("viewer", "VIEWER");
        }
        
        @Override
        protected HandlerResult process(RequestContext context) {
            NetworkRequest request = context.getRequest();
            
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                return HandlerResult.stopWithResponse(
                    new ProcessingResponse(
                        request.getId(),
                        RequestStatus.UNAUTHORIZED,
                        "User ID is required",
                        null,
                        "AUTH_MISSING_USER",
                        System.currentTimeMillis() - context.getStartTime()
                    ),
                    "Authentication failed - no user ID"
                );
            }
            
            if (!validUsers.containsKey(request.getUserId())) {
                return HandlerResult.stopWithResponse(
                    new ProcessingResponse(
                        request.getId(),
                        RequestStatus.UNAUTHORIZED,
                        "Invalid user ID",
                        null,
                        "AUTH_INVALID_USER",
                        System.currentTimeMillis() - context.getStartTime()
                    ),
                    "Authentication failed - invalid user"
                );
            }
            
            // Add authentication metadata for next handlers
            context.addMetadata("userRole", validUsers.get(request.getUserId()));
            context.addMetadata("authenticated", true);
            
            return HandlerResult.continueProcessing("User authenticated successfully");
        }
        
        @Override
        protected String getHandlerName() {
            return "AuthenticationHandler";
        }
    }
    
    // Concrete Handler: Authorization
    public static class AuthorizationHandler extends RequestHandler {
        @Override
        protected HandlerResult process(RequestContext context) {
            NetworkRequest request = context.getRequest();
            String userRole = (String) context.getMetadata().get("userRole");
            
            if (!isAuthorizedForRequestType(userRole, request.getType())) {
                return HandlerResult.stopWithResponse(
                    new ProcessingResponse(
                        request.getId(),
                        RequestStatus.UNAUTHORIZED,
                        "Insufficient permissions for request type: " + request.getType(),
                        null,
                        "AUTHZ_INSUFFICIENT_PERMS",
                        System.currentTimeMillis() - context.getStartTime()
                    ),
                    "Authorization failed - insufficient permissions"
                );
            }
            
            context.addMetadata("authorized", true);
            context.addMetadata("authorizedFor", request.getType());
            
            return HandlerResult.continueProcessing("User authorized for request type");
        }
        
        private boolean isAuthorizedForRequestType(String role, RequestType type) {
            switch (role) {
                case "ADMIN":
                    return true;
                case "OPERATOR":
                    return type != RequestType.NETWORK_TOPOLOGY;
                case "VIEWER":
                    return type == RequestType.MONITORING_DATA;
                default:
                    return false;
            }
        }
        
        @Override
        protected String getHandlerName() {
            return "AuthorizationHandler";
        }
    }
    
    // Concrete Handler: Rate Limiting
    public static class RateLimitingHandler extends RequestHandler {
        private final Map<String, Integer> rateLimitCounts;
        
        public RateLimitingHandler() {
            this.rateLimitCounts = new HashMap<>();
        }
        
        @Override
        protected HandlerResult process(RequestContext context) {
            NetworkRequest request = context.getRequest();
            String userRole = (String) context.getMetadata().get("userRole");
            
            String rateLimitKey = request.getUserId() + ":" + request.getSourceIp();
            int currentCount = rateLimitCounts.getOrDefault(rateLimitKey, 0);
            int limit = getRateLimitForUser(userRole);
            
            if (currentCount >= limit) {
                return HandlerResult.stopWithResponse(
                    new ProcessingResponse(
                        request.getId(),
                        RequestStatus.FAILED,
                        "Rate limit exceeded (" + currentCount + "/" + limit + ")",
                        null,
                        "RATE_LIMIT_EXCEEDED",
                        System.currentTimeMillis() - context.getStartTime()
                    ),
                    "Rate limit exceeded"
                );
            }
            
            rateLimitCounts.put(rateLimitKey, currentCount + 1);
            context.addMetadata("rateLimitCount", currentCount + 1);
            context.addMetadata("rateLimitMax", limit);
            
            return HandlerResult.continueProcessing("Rate limit check passed");
        }
        
        private int getRateLimitForUser(String role) {
            switch (role) {
                case "ADMIN": return 1000;
                case "OPERATOR": return 500;
                case "VIEWER": return 100;
                default: return 10;
            }
        }
        
        @Override
        protected String getHandlerName() {
            return "RateLimitingHandler";
        }
    }
    
    // Concrete Handler: Input Validation
    public static class ValidationHandler extends RequestHandler {
        @Override
        protected HandlerResult process(RequestContext context) {
            NetworkRequest request = context.getRequest();
            String validationError = validateRequestPayload(request);
            
            if (validationError != null) {
                return HandlerResult.stopWithResponse(
                    new ProcessingResponse(
                        request.getId(),
                        RequestStatus.FAILED,
                        "Input validation failed: " + validationError,
                        null,
                        "VALIDATION_FAILED",
                        System.currentTimeMillis() - context.getStartTime()
                    ),
                    "Input validation failed"
                );
            }
            
            context.addMetadata("validated", true);
            
            return HandlerResult.continueProcessing("Input validation passed");
        }
        
        private String validateRequestPayload(NetworkRequest request) {
            Map<String, Object> payload = request.getPayload();
            
            switch (request.getType()) {
                case DEVICE_CONFIG:
                    if (!payload.containsKey("deviceId")) return "Device ID is required";
                    if (!payload.containsKey("config")) return "Configuration data is required";
                    break;
                case NETWORK_TOPOLOGY:
                    if (!payload.containsKey("topology")) return "Topology data is required";
                    break;
                case MONITORING_DATA:
                    if (!payload.containsKey("deviceId")) return "Device ID is required";
                    if (!payload.containsKey("timestamp")) return "Timestamp is required";
                    break;
                case MAINTENANCE_SCHEDULING:
                    if (!payload.containsKey("deviceId")) return "Device ID is required";
                    if (!payload.containsKey("scheduledTime")) return "Scheduled time is required";
                    break;
            }
            
            return null;
        }
        
        @Override
        protected String getHandlerName() {
            return "ValidationHandler";
        }
    }
    
    // Concrete Handler: Business Logic Processing (Final Handler)
    public static class BusinessLogicHandler extends RequestHandler {
        @Override
        protected HandlerResult process(RequestContext context) {
            NetworkRequest request = context.getRequest();
            
            try {
                Map<String, Object> businessResult = processBusinessLogic(request);
                Map<String, Object> response = transformResponse(context, businessResult);
                
                return HandlerResult.stopWithResponse(
                    new ProcessingResponse(
                        request.getId(),
                        RequestStatus.COMPLETED,
                        "Request processed successfully",
                        response,
                        null,
                        System.currentTimeMillis() - context.getStartTime()
                    ),
                    "Business logic executed successfully"
                );
                
            } catch (Exception e) {
                return HandlerResult.stopWithResponse(
                    new ProcessingResponse(
                        request.getId(),
                        RequestStatus.FAILED,
                        "Processing error: " + e.getMessage(),
                        null,
                        "PROCESSING_ERROR",
                        System.currentTimeMillis() - context.getStartTime()
                    ),
                    "Business logic failed: " + e.getMessage()
                );
            }
        }
        
        private Map<String, Object> processBusinessLogic(NetworkRequest request) {
            Map<String, Object> result = new HashMap<>();
            
            switch (request.getType()) {
                case DEVICE_CONFIG:
                    result.put("configurationApplied", true);
                    result.put("deviceId", request.getPayload().get("deviceId"));
                    break;
                case NETWORK_TOPOLOGY:
                    result.put("topologyUpdated", true);
                    result.put("nodesAffected", 15);
                    break;
                case MONITORING_DATA:
                    result.put("monitoringData", generateMonitoringData());
                    break;
                case MAINTENANCE_SCHEDULING:
                    result.put("maintenanceScheduled", true);
                    result.put("maintenanceId", "MAINT-" + System.currentTimeMillis());
                    break;
            }
            
            result.put("timestamp", LocalDateTime.now());
            return result;
        }
        
        private Map<String, Object> transformResponse(RequestContext context, Map<String, Object> businessResult) {
            Map<String, Object> response = new HashMap<>(businessResult);
            response.put("requestId", context.getRequest().getId());
            response.put("requestType", context.getRequest().getType().toString());
            response.put("processedBy", "ChainOfResponsibility");
            response.put("processingSteps", context.getProcessingSteps());
            response.put("processingMetadata", context.getMetadata());
            
            return response;
        }
        
        private Map<String, Object> generateMonitoringData() {
            Map<String, Object> data = new HashMap<>();
            data.put("cpuUsage", (int)(Math.random() * 100));
            data.put("memoryUsage", (int)(Math.random() * 100));
            data.put("networkThroughput", (int)(Math.random() * 1000));
            return data;
        }
        
        @Override
        protected String getHandlerName() {
            return "BusinessLogicHandler";
        }
    }
    
    // Copy ProcessingResponse class
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
        
        public String getRequestId() { return requestId; }
        public RequestStatus getStatus() { return status; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return new HashMap<>(data); }
        public String getErrorCode() { return errorCode; }
        public long getProcessingTimeMs() { return processingTimeMs; }
    }
    
    // Request Processor that uses the Chain
    public static class ChainRequestProcessor {
        private final RequestHandler handlerChain;
        
        public ChainRequestProcessor() {
            this.handlerChain = buildHandlerChain();
        }
        
        // Alternative constructor for custom chain configuration
        public ChainRequestProcessor(RequestHandler customChain) {
            this.handlerChain = customChain;
        }
        
        public ProcessingResponse processRequest(NetworkRequest request) {
            RequestContext context = new RequestContext(request);
            context.addProcessingStep("Request received by ChainRequestProcessor");
            
            return handlerChain.handle(context);
        }
        
        private RequestHandler buildHandlerChain() {
            RequestHandler authenticationHandler = new AuthenticationHandler();
            RequestHandler authorizationHandler = new AuthorizationHandler();
            RequestHandler rateLimitingHandler = new RateLimitingHandler();
            RequestHandler validationHandler = new ValidationHandler();
            RequestHandler businessLogicHandler = new BusinessLogicHandler();
            
            // Build the chain - order matters!
            authenticationHandler
                .setNext(authorizationHandler)
                .setNext(rateLimitingHandler)
                .setNext(validationHandler)
                .setNext(businessLogicHandler);
            
            return authenticationHandler;
        }
        
        // Method to get a custom configured chain (for testing or different configurations)
        public static RequestHandler buildCustomChain(List<RequestHandler> handlers) {
            if (handlers.isEmpty()) {
                throw new IllegalArgumentException("Handler list cannot be empty");
            }
            
            RequestHandler firstHandler = handlers.get(0);
            RequestHandler currentHandler = firstHandler;
            
            for (int i = 1; i < handlers.size(); i++) {
                currentHandler = currentHandler.setNext(handlers.get(i));
            }
            
            return firstHandler;
        }
    }
}