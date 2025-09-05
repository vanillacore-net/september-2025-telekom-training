package com.telekom.training.day1.legacyadapter.initial;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * LEGACY SYSTEM - Altes Billing System
 * 
 * Simuliert ein altes System mit schlechter API:
 * - Verwendet veraltete Datentypen (Date statt LocalDate)
 * - Inkonsistente Naming Conventions
 * - Komplizierte Parameter-Strukturen
 * - Unklare Error Handling
 * - Legacy Exception Types
 */
public class LegacyBillingSystem {
    
    private List<OldInvoiceRecord> invoiceRecords = new ArrayList<>();
    private int nextInvoiceId = 1000;
    
    // Legacy Method mit schlechter API
    public OldInvoiceRecord createBillForCustomer(String custId, double amt, 
                                                 Date billDate, String billType, 
                                                 String payType, int dueDays) throws LegacyBillingException {
        
        // Legacy validation mit unchecked exceptions
        if (custId == null || custId.length() < 3) {
            throw new LegacyBillingException("INVALID_CUSTOMER_ID", "Customer ID must be at least 3 characters");
        }
        
        if (amt <= 0) {
            throw new LegacyBillingException("INVALID_AMOUNT", "Amount must be positive");
        }
        
        // Legacy Business Logic
        OldInvoiceRecord invoice = new OldInvoiceRecord();
        invoice.invoiceId = "INV-" + (nextInvoiceId++);
        invoice.customerId = custId;
        invoice.totalAmount = amt;
        invoice.creationDate = billDate;
        invoice.invoiceType = billType;
        invoice.paymentMethod = payType;
        
        // Legacy Date Calculation
        Date dueDate = new Date(billDate.getTime() + (dueDays * 24L * 60L * 60L * 1000L));
        invoice.dueDate = dueDate;
        
        invoice.status = "PENDING";
        invoice.paymentReference = generateOldPaymentRef(invoice.invoiceId, custId);
        
        invoiceRecords.add(invoice);
        return invoice;
    }
    
    // Legacy Method mit verwirrenden Parametern
    public List<OldInvoiceRecord> findInvoicesForCustomer(String custId, Date fromDate, 
                                                         Date toDate, String statusFilter) {
        List<OldInvoiceRecord> result = new ArrayList<>();
        
        for (OldInvoiceRecord record : invoiceRecords) {
            if (!record.customerId.equals(custId)) {
                continue;
            }
            
            if (fromDate != null && record.creationDate.before(fromDate)) {
                continue;
            }
            
            if (toDate != null && record.creationDate.after(toDate)) {
                continue;
            }
            
            if (statusFilter != null && !statusFilter.equals("ALL") && !record.status.equals(statusFilter)) {
                continue;
            }
            
            result.add(record);
        }
        
        return result;
    }
    
    // Legacy Method mit boolean return für Success/Failure
    public boolean processPaymentForInvoice(String invoiceId, double paidAmount, 
                                           Date paymentDate, String paymentRef) {
        for (OldInvoiceRecord record : invoiceRecords) {
            if (record.invoiceId.equals(invoiceId)) {
                if (paidAmount >= record.totalAmount) {
                    record.status = "PAID";
                    record.paymentDate = paymentDate;
                    record.paidAmount = paidAmount;
                    record.paymentReference = paymentRef;
                    return true;
                } else {
                    record.status = "PARTIAL_PAID";
                    record.paidAmount = paidAmount;
                    record.paymentDate = paymentDate;
                    return false; // Partial payment
                }
            }
        }
        return false; // Invoice not found
    }
    
    // Legacy Method mit String-based Error Reporting
    public String getInvoiceStatusReport(String invoiceId) {
        for (OldInvoiceRecord record : invoiceRecords) {
            if (record.invoiceId.equals(invoiceId)) {
                StringBuilder report = new StringBuilder();
                report.append("Invoice: ").append(record.invoiceId).append("\n");
                report.append("Customer: ").append(record.customerId).append("\n");
                report.append("Amount: ").append(record.totalAmount).append("\n");
                report.append("Status: ").append(record.status).append("\n");
                report.append("Due Date: ").append(record.dueDate).append("\n");
                if (record.paymentDate != null) {
                    report.append("Paid Date: ").append(record.paymentDate).append("\n");
                    report.append("Paid Amount: ").append(record.paidAmount).append("\n");
                }
                return report.toString();
            }
        }
        return "ERROR: Invoice not found";
    }
    
    // Legacy Utility Method
    private String generateOldPaymentRef(String invoiceId, String customerId) {
        return "PAY-" + invoiceId.substring(4) + "-" + customerId.substring(0, Math.min(3, customerId.length()));
    }
}

// Legacy classes are now in separate files for better organization