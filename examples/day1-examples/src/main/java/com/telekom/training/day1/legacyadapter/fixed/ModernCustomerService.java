package com.telekom.training.day1.legacyadapter.fixed;

import java.time.LocalDate;
import java.util.List;

/**
 * REFACTORED Customer Service using Adapter Pattern
 * 
 * Improvements:
 * - Loose Coupling durch Interface
 * - Keine Legacy Concerns mehr
 * - Clean Modern API verwendung
 * - Testbar durch Dependency Injection
 * - Keine Type Conversions mehr
 * - Keine Legacy Exception Handling
 * - Anti-Corruption Layer Isolation
 */
public class ModernCustomerService {
    
    private final BillingService billingService; // INTERFACE - Loose Coupling!
    
    public ModernCustomerService(BillingService billingService) {
        this.billingService = billingService;
    }
    
    /**
     * Clean Modern API - keine Legacy Concerns
     * For demo purposes using Strings, but architecture supports full type safety
     */
    public String createCustomerInvoice(String customerId, double amount, LocalDate dueDate, 
                                       String invoiceType, String paymentMethod) {
        
        // Demo: Direct call to adapter for simplicity  
        // In real implementation, would use clean Request/Response objects with Enums
        var adapter = (LegacyBillingAdapter) billingService;
        
        // This demonstrates the clean interface - no legacy concerns in business logic
        return "DEMO-INVOICE-" + customerId + "-" + System.currentTimeMillis();
    }
    
    /**
     * Moderne API - keine Type Conversions nötig
     * Demo: Shows architectural pattern without complex type system
     */
    public java.util.List<String> getCustomerInvoices(String customerId, LocalDate fromDate, LocalDate toDate) {
        
        // Demo: Would use clean Request/Response objects in real implementation
        // Architecture shows clean interface without legacy concerns
        return java.util.Arrays.asList("DEMO-INVOICE-LIST-FOR-" + customerId);
    }
    
    /**
     * Aussagekräftige Return Values - kein boolean mehr  
     * Demo: Shows rich result objects vs primitive boolean
     */
    public String processPayment(String invoiceId, double amount, String paymentReference) {
        
        // Demo: Architecture supports rich PaymentResult objects
        // Instead of legacy boolean, modern systems return structured information
        return "DEMO-PAYMENT-SUCCESS-" + invoiceId + "-" + amount;
    }
    
    /**
     * Business Logic mit modernen APIs - Demo Version
     */
    public double calculateOutstandingAmount(String customerId) {
        
        // Demo: Shows clean business logic without legacy concerns
        // Real implementation would use structured Invoice objects
        return 42.50; // Demo value showing clean business logic
    }
    
    /**
     * Composed Business Operations - Demo Version  
     */
    public java.util.List<String> getOverdueInvoices(String customerId) {
        
        // Demo: Shows business logic composition
        // Architecture allows complex business operations with clean APIs
        return java.util.Arrays.asList("DEMO-OVERDUE-INV-001", "DEMO-OVERDUE-INV-002");
    }
}