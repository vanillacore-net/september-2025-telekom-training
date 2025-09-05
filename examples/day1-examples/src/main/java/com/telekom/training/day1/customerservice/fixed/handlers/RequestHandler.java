package com.telekom.training.day1.customerservice.fixed.handlers;

import com.telekom.training.day1.customerservice.fixed.model.Customer;
import java.util.Map;

/**
 * Factory Method Pattern: Abstract Creator
 * 
 * Definiert das Interface für alle Request Handler.
 * Jeder konkrete Handler implementiert die Factory Method createResponse().
 */
public abstract class RequestHandler {
    
    /**
     * Template Method - definiert den Ablauf
     */
    public final String handleRequest(Customer customer, Map<String, Object> params) {
        // Validation
        if (!isValidRequest(customer, params)) {
            return "ERROR: Invalid request parameters";
        }
        
        // Factory Method Call - wird von Subklassen implementiert
        return createResponse(customer, params);
    }
    
    /**
     * Factory Method - wird von konkreten Handlern überschrieben
     */
    protected abstract String createResponse(Customer customer, Map<String, Object> params);
    
    /**
     * Hook Method - kann von Subklassen überschrieben werden
     */
    protected boolean isValidRequest(Customer customer, Map<String, Object> params) {
        return customer != null && params != null;
    }
    
    /**
     * Helper method für Contract ID Generation
     */
    protected String generateContractId() {
        return "CTR-" + System.currentTimeMillis();
    }
}