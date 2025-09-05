package com.telekom.training.day1.legacyadapter.fixed;

import com.telekom.training.day1.legacyadapter.initial.LegacyBillingSystem;
import com.telekom.training.day1.legacyadapter.initial.OldInvoiceRecord;
import com.telekom.training.day1.legacyadapter.initial.LegacyBillingException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ADAPTER PATTERN: Legacy Billing System Adapter
 * 
 * Adaptiert das Legacy Billing System an das moderne BillingService Interface.
 * 
 * Verbesserungen:
 * - Isolation der Legacy Concerns
 * - Zentrale Type Conversion Logic  
 * - Anti-Corruption Layer
 * - Exception Translation
 * - Clean Separation zwischen Modern und Legacy
 * - Testbar durch Interface
 */
public class LegacyBillingAdapter implements BillingService {
    
    private final LegacyBillingSystem legacySystem;
    
    public LegacyBillingAdapter(LegacyBillingSystem legacySystem) {
        this.legacySystem = legacySystem;
    }
    
    @Override
    public Invoice createInvoice(CreateInvoiceRequest request) {
        try {
            // Type Conversions - zentral in Adapter
            Date creationDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
            int dueDays = (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), request.getDueDate());
            
            // Legacy Parameter Mapping - isoliert im Adapter
            OldInvoiceRecord legacyInvoice = legacySystem.createBillForCustomer(
                request.getCustomerId(),
                request.getAmount(),
                creationDate,
                mapInvoiceTypeToLegacy(request.getInvoiceType()),
                mapPaymentMethodToLegacy(request.getPaymentMethod()),
                dueDays
            );
            
            // Convert Legacy Response to Modern
            return convertLegacyToModernInvoice(legacyInvoice);
            
        } catch (LegacyBillingException e) {
            // Exception Translation - Legacy zu Modern
            throw new BillingServiceException("Failed to create invoice", e);
        }
    }
    
    @Override
    public List<Invoice> findInvoices(InvoiceSearchRequest request) {
        // Type Conversions
        Date fromDate = request.getFromDate() != null ?
            Date.from(request.getFromDate().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date toDate = request.getToDate() != null ?
            Date.from(request.getToDate().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        
        // Legacy Status Mapping
        String legacyStatusFilter = mapInvoiceStatusToLegacy(request.getStatusFilter());
        
        // Legacy System Call
        List<OldInvoiceRecord> legacyInvoices = legacySystem.findInvoicesForCustomer(
            request.getCustomerId(), fromDate, toDate, legacyStatusFilter
        );
        
        // Convert Collection
        return legacyInvoices.stream()
            .map(this::convertLegacyToModernInvoice)
            .collect(Collectors.toList());
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        Date paymentDate = Date.from(java.time.LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        
        // Legacy System Call
        boolean success = legacySystem.processPaymentForInvoice(
            request.getInvoiceId(),
            request.getAmount(),
            paymentDate,
            request.getPaymentReference()
        );
        
        // Interpret Legacy Boolean Result
        return interpretLegacyPaymentResult(request.getInvoiceId(), request.getAmount(), success);
    }
    
    @Override
    public InvoiceDetails getInvoiceDetails(String invoiceId) {
        // Legacy System Call - String-based API
        String legacyReport = legacySystem.getInvoiceStatusReport(invoiceId);
        
        // Handle Legacy Error Format
        if (legacyReport.startsWith("ERROR:")) {
            throw new BillingServiceException("Invoice not found: " + invoiceId);
        }
        
        // For this example, we'd need to parse the string report or find the invoice differently
        // In real world, we'd probably need a different legacy method or parse the report
        return findInvoiceDetailsFromLegacySystem(invoiceId);
    }
    
    // CENTRALIZED MAPPING LOGIC - keine Code Duplication mehr
    
    private String mapInvoiceTypeToLegacy(InvoiceType modernType) {
        switch (modernType) {
            case MONTHLY_BILL: return "MONTHLY";
            case INSTALLATION: return "INSTALL";
            case ADJUSTMENT: return "ADJUST";
            case TERMINATION: return "TERM";
            case OTHER:
            default: return "OTHER";
        }
    }
    
    private InvoiceType mapInvoiceTypeFromLegacy(String legacyType) {
        switch (legacyType) {
            case "MONTHLY": return InvoiceType.MONTHLY_BILL;
            case "INSTALL": return InvoiceType.INSTALLATION;
            case "ADJUST": return InvoiceType.ADJUSTMENT;
            case "TERM": return InvoiceType.TERMINATION;
            default: return InvoiceType.OTHER;
        }
    }
    
    private String mapPaymentMethodToLegacy(PaymentMethod modernMethod) {
        switch (modernMethod) {
            case DIRECT_DEBIT: return "DD";
            case BANK_TRANSFER: return "BT";
            case CREDIT_CARD: return "CC";
            case PAYPAL: return "PP";
            case OTHER:
            default: return "OTHER";
        }
    }
    
    private PaymentMethod mapPaymentMethodFromLegacy(String legacyMethod) {
        switch (legacyMethod) {
            case "DD": return PaymentMethod.DIRECT_DEBIT;
            case "BT": return PaymentMethod.BANK_TRANSFER;
            case "CC": return PaymentMethod.CREDIT_CARD;
            case "PP": return PaymentMethod.PAYPAL;
            default: return PaymentMethod.OTHER;
        }
    }
    
    private String mapInvoiceStatusToLegacy(InvoiceStatus modernStatus) {
        switch (modernStatus) {
            case OPEN: return "PENDING";
            case PAID: return "PAID";
            case PARTIALLY_PAID: return "PARTIAL_PAID";
            case ALL:
            default: return "ALL";
        }
    }
    
    private InvoiceStatus mapInvoiceStatusFromLegacy(String legacyStatus) {
        switch (legacyStatus) {
            case "PENDING": return InvoiceStatus.OPEN;
            case "PAID": return InvoiceStatus.PAID;
            case "PARTIAL_PAID": return InvoiceStatus.PARTIALLY_PAID;
            default: return InvoiceStatus.OPEN;
        }
    }
    
    // CENTRALIZED TYPE CONVERSION
    
    private LocalDate convertLegacyDateToModern(Date legacyDate) {
        return legacyDate != null ?
            legacyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
    
    private Invoice convertLegacyToModernInvoice(OldInvoiceRecord legacy) {
        return new Invoice(
            legacy.invoiceId,
            legacy.customerId,
            legacy.totalAmount,
            convertLegacyDateToModern(legacy.creationDate),
            convertLegacyDateToModern(legacy.dueDate),
            mapInvoiceTypeFromLegacy(legacy.invoiceType),
            mapPaymentMethodFromLegacy(legacy.paymentMethod),
            mapInvoiceStatusFromLegacy(legacy.status)
        );
    }
    
    private InvoiceDetails convertLegacyToModernInvoiceDetails(OldInvoiceRecord legacy) {
        return new InvoiceDetails(
            legacy.invoiceId,
            legacy.customerId,
            legacy.totalAmount,
            convertLegacyDateToModern(legacy.creationDate),
            convertLegacyDateToModern(legacy.dueDate),
            mapInvoiceTypeFromLegacy(legacy.invoiceType),
            mapPaymentMethodFromLegacy(legacy.paymentMethod),
            mapInvoiceStatusFromLegacy(legacy.status),
            convertLegacyDateToModern(legacy.paymentDate),
            legacy.paidAmount,
            legacy.paymentReference
        );
    }
    
    // BUSINESS LOGIC INTERPRETATION - macht Legacy Boolean verständlich
    
    private PaymentResult interpretLegacyPaymentResult(String invoiceId, double paymentAmount, boolean legacySuccess) {
        if (!legacySuccess) {
            // Hier müssten wir prüfen warum es fehlgeschlagen ist
            // Das ist ein Problem des Legacy Systems - unklares Error Handling
            return new PaymentResult(false, PaymentStatus.INVOICE_NOT_FOUND, 
                "Payment failed - invoice may not exist or payment was partial", 0.0);
        }
        
        // Bei Success müssen wir prüfen ob es Full oder Partial Payment war
        // Vereinfacht für Demo - normalerweise würde man die Invoice nochmal abfragen
        return new PaymentResult(true, PaymentStatus.FULL_PAYMENT, "Payment processed successfully", 0.0);
    }
    
    private InvoiceDetails findInvoiceDetailsFromLegacySystem(String invoiceId) {
        // Simplified: find invoice in legacy system records
        // In real implementation, this would need proper legacy system access
        throw new BillingServiceException("Invoice details lookup not fully implemented for demo");
    }
}

/**
 * Modern Exception für Billing Service
 */
class BillingServiceException extends RuntimeException {
    public BillingServiceException(String message) {
        super(message);
    }
    
    public BillingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}