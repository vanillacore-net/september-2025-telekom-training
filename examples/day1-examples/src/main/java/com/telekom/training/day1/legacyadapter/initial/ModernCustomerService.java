package com.telekom.training.day1.legacyadapter.initial;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ANTI-PATTERN: Direct Legacy System Integration
 * 
 * Diese Klasse zeigt die Probleme von direkter Integration mit Legacy-Systemen:
 * 
 * Code-Smells:
 * - Direct coupling zu Legacy System
 * - Type Conversions überall (LocalDate <-> Date)
 * - Legacy Exception Handling verstreut
 * - Inkonsistente APIs gemischt
 * - Schwer testbar wegen Legacy Dependencies
 * - Legacy Concerns durchsickern ins moderne System
 * - Code Duplication bei Type Conversions
 * - Tight Coupling - schwer zu ersetzen
 */
public class ModernCustomerService {
    
    private LegacyBillingSystem legacyBilling; // TIGHT COUPLING!
    
    public ModernCustomerService(LegacyBillingSystem legacyBilling) {
        this.legacyBilling = legacyBilling;
    }
    
    /**
     * ANTI-PATTERN: Legacy Concerns sickern durch
     */
    public String createCustomerInvoice(String customerId, double amount, LocalDate dueDate, 
                                       String invoiceType, String paymentMethod) {
        try {
            // TYPE CONVERSION überall! 
            Date legacyCreationDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            Date legacyDueDate = Date.from(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            // Legacy Parameter Mapping - fehleranfällig
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            int dueDays = (int) daysBetween;
            
            // DIRECT LEGACY CALL - Tight Coupling
            OldInvoiceRecord legacyInvoice = legacyBilling.createBillForCustomer(
                customerId, amount, legacyCreationDate, 
                mapToLegacyInvoiceType(invoiceType),  // More mapping logic
                mapToLegacyPaymentMethod(paymentMethod),  // More mapping logic
                dueDays
            );
            
            // Return Legacy Data - keine Abstraktion!
            return legacyInvoice.invoiceId;
            
        } catch (LegacyBillingException e) {
            // LEGACY EXCEPTION HANDLING - unschön
            throw new RuntimeException("Failed to create invoice: " + e.getMessage() + " [" + e.getErrorCode() + "]");
        }
    }
    
    /**
     * ANTI-PATTERN: Type Conversion Logic überall verstreut
     */
    public List<InvoiceSummary> getCustomerInvoices(String customerId, LocalDate fromDate, LocalDate toDate) {
        // TYPE CONVERSIONS wieder...
        Date legacyFromDate = fromDate != null ? 
            Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date legacyToDate = toDate != null ? 
            Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        
        // DIRECT LEGACY CALL
        List<OldInvoiceRecord> legacyInvoices = legacyBilling.findInvoicesForCustomer(
            customerId, legacyFromDate, legacyToDate, "ALL"
        );
        
        // TYPE CONVERSION zurück zu modernen Types
        return legacyInvoices.stream()
            .map(this::convertLegacyInvoiceToModern)  // Conversion Logic überall
            .collect(Collectors.toList());
    }
    
    /**
     * ANTI-PATTERN: Boolean Return Values aus Legacy System
     */
    public boolean processPayment(String invoiceId, double amount, String paymentReference) {
        // TYPE CONVERSION
        Date paymentDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        
        // DIRECT LEGACY CALL - boolean return ist nicht aussagekräftig
        boolean success = legacyBilling.processPaymentForInvoice(invoiceId, amount, paymentDate, paymentReference);
        
        // Legacy Boolean Logic - was bedeutet false? Not found? Partial payment?
        if (!success) {
            // Wir wissen nicht warum es fehlgeschlagen ist!
            System.err.println("Payment processing failed for invoice: " + invoiceId);
        }
        
        return success;
    }
    
    /**
     * ANTI-PATTERN: String-based Reports aus Legacy System
     */
    public String getInvoiceReport(String invoiceId) {
        // DIRECT LEGACY CALL - String-based API
        String legacyReport = legacyBilling.getInvoiceStatusReport(invoiceId);
        
        // Legacy String Parsing - sehr fragile!
        if (legacyReport.startsWith("ERROR:")) {
            throw new RuntimeException(legacyReport);
        }
        
        // Return Legacy String - keine moderne Datenstruktur
        return "=== Modern Invoice Report ===\n" + legacyReport;
    }
    
    // DUPLICATE MAPPING LOGIC - sollte abstrahiert werden
    private String mapToLegacyInvoiceType(String modernType) {
        switch (modernType.toUpperCase()) {
            case "MONTHLY_BILL": return "MONTHLY";
            case "INSTALLATION": return "INSTALL";
            case "ADJUSTMENT": return "ADJUST";
            case "TERMINATION": return "TERM";
            default: return "OTHER";
        }
    }
    
    private String mapToLegacyPaymentMethod(String modernMethod) {
        switch (modernMethod.toUpperCase()) {
            case "DIRECT_DEBIT": return "DD";
            case "BANK_TRANSFER": return "BT";
            case "CREDIT_CARD": return "CC";
            case "PAYPAL": return "PP";
            default: return "OTHER";
        }
    }
    
    // CONVERSION LOGIC - überall verstreut
    private InvoiceSummary convertLegacyInvoiceToModern(OldInvoiceRecord legacy) {
        InvoiceSummary modern = new InvoiceSummary();
        modern.invoiceId = legacy.invoiceId;
        modern.customerId = legacy.customerId;
        modern.amount = legacy.totalAmount;
        
        // TYPE CONVERSIONS
        modern.creationDate = legacy.creationDate.toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        modern.dueDate = legacy.dueDate.toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Status Mapping
        modern.status = mapLegacyStatusToModern(legacy.status);
        
        // Optional Fields
        if (legacy.paymentDate != null) {
            modern.paymentDate = legacy.paymentDate.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        }
        modern.paidAmount = legacy.paidAmount;
        
        return modern;
    }
    
    private String mapLegacyStatusToModern(String legacyStatus) {
        switch (legacyStatus) {
            case "PENDING": return "OPEN";
            case "PAID": return "PAID";
            case "PARTIAL_PAID": return "PARTIALLY_PAID";
            default: return "UNKNOWN";
        }
    }
}

// Modern Data Structure - aber wird vermischt mit Legacy verwendet
class InvoiceSummary {
    public String invoiceId;
    public String customerId;
    public double amount;
    public LocalDate creationDate;
    public LocalDate dueDate;
    public String status;
    public LocalDate paymentDate;
    public double paidAmount;
    
    @Override
    public String toString() {
        return String.format("Invoice{id='%s', customer='%s', amount=%.2f, status='%s'}", 
                           invoiceId, customerId, amount, status);
    }
}