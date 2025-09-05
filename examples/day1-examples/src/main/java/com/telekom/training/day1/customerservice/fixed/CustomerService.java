package com.telekom.training.day1.customerservice.fixed;

import com.telekom.training.day1.customerservice.fixed.model.Customer;
import com.telekom.training.day1.customerservice.fixed.handlers.RequestHandler;
import java.util.Map;
import java.util.HashMap;

/**
 * REFACTORED CustomerService using Factory Method Pattern
 * 
 * Improvements:
 * - Single Responsibility: Nur noch für Koordination zuständig
 * - Open/Closed Principle: Erweiterbar ohne Änderung (neue Handler)
 * - Factory Method Pattern: Delegation an spezialisierte Handler
 * - Keine Long Methods mehr
 * - Keine Giant Switch Statements
 * - Testbar durch Dependency Injection möglich
 * - Klare Trennung von Concerns
 */
public class CustomerService {
    
    private final Map<String, Customer> customers = new HashMap<>();
    private final RequestHandlerFactory handlerFactory;
    
    public CustomerService() {
        this.handlerFactory = new RequestHandlerFactory();
    }
    
    // Testability: Constructor Injection für Testing
    public CustomerService(RequestHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }
    
    /**
     * Haupteinstiegspunkt - jetzt viel sauberer und kürzer
     */
    public String handleCustomerRequest(String customerId, String requestType, Map<String, Object> params) {
        // Kunde suchen
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return "ERROR: Customer not found";
        }
        
        try {
            // Factory Method Pattern: Handler erstellen und delegieren
            RequestHandler handler = RequestHandlerFactory.createHandler(requestType);
            return handler.handleRequest(customer, params);
            
        } catch (IllegalArgumentException e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Customer Management - saubere Trennung
     */
    public void addCustomer(String customerId, String name, String address, String email) {
        Customer customer = new Customer(customerId, name, address, email);
        customers.put(customerId, customer);
    }
    
    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }
    
    /**
     * Hilfsmethode für neue Handler Registration
     */
    public void registerCustomHandler(String requestType, RequestHandler handler) {
        RequestHandlerFactory.registerHandler(requestType, handler);
    }
    
    /**
     * Prüft ob Request Type unterstützt wird
     */
    public boolean isRequestTypeSupported(String requestType) {
        return RequestHandlerFactory.isSupported(requestType);
    }
}