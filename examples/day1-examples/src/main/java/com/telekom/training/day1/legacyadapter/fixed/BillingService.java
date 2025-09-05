package com.telekom.training.day1.legacyadapter.fixed;

import java.time.LocalDate;
import java.util.List;

/**
 * Target Interface - Moderne Billing Service API
 * 
 * Definiert die gewünschte moderne API ohne Legacy Concerns.
 * Clean, konsistent, verwendet moderne Java Types.
 */
public interface BillingService {
    
    /**
     * Erstellt eine neue Rechnung für einen Kunden
     */
    Invoice createInvoice(CreateInvoiceRequest request);
    
    /**
     * Sucht Rechnungen für einen Kunden
     */
    List<Invoice> findInvoices(InvoiceSearchRequest request);
    
    /**
     * Verarbeitet eine Zahlung für eine Rechnung
     */
    PaymentResult processPayment(PaymentRequest request);
    
    /**
     * Holt detaillierte Informationen zu einer Rechnung
     */
    InvoiceDetails getInvoiceDetails(String invoiceId);
}

// Modern Request Objects - Clean API
class CreateInvoiceRequest {
    private final String customerId;
    private final double amount;
    private final LocalDate dueDate;
    private final InvoiceType invoiceType;
    private final PaymentMethod paymentMethod;
    
    public CreateInvoiceRequest(String customerId, double amount, LocalDate dueDate, 
                               InvoiceType invoiceType, PaymentMethod paymentMethod) {
        this.customerId = customerId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.invoiceType = invoiceType;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public InvoiceType getInvoiceType() { return invoiceType; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
}

class InvoiceSearchRequest {
    private final String customerId;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final InvoiceStatus statusFilter;
    
    public InvoiceSearchRequest(String customerId, LocalDate fromDate, LocalDate toDate, InvoiceStatus statusFilter) {
        this.customerId = customerId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.statusFilter = statusFilter;
    }
    
    // Getters
    public String getCustomerId() { return customerId; }
    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate() { return toDate; }
    public InvoiceStatus getStatusFilter() { return statusFilter; }
}

class PaymentRequest {
    private final String invoiceId;
    private final double amount;
    private final String paymentReference;
    
    public PaymentRequest(String invoiceId, double amount, String paymentReference) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentReference = paymentReference;
    }
    
    // Getters
    public String getInvoiceId() { return invoiceId; }
    public double getAmount() { return amount; }
    public String getPaymentReference() { return paymentReference; }
}

// Modern Response Objects - Clean Data Structures
class Invoice {
    private final String invoiceId;
    private final String customerId;
    private final double amount;
    private final LocalDate creationDate;
    private final LocalDate dueDate;
    private final InvoiceType invoiceType;
    private final PaymentMethod paymentMethod;
    private final InvoiceStatus status;
    
    public Invoice(String invoiceId, String customerId, double amount, LocalDate creationDate,
                   LocalDate dueDate, InvoiceType invoiceType, PaymentMethod paymentMethod, InvoiceStatus status) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.amount = amount;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.invoiceType = invoiceType;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }
    
    // Getters
    public String getInvoiceId() { return invoiceId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public LocalDate getCreationDate() { return creationDate; }
    public LocalDate getDueDate() { return dueDate; }
    public InvoiceType getInvoiceType() { return invoiceType; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public InvoiceStatus getStatus() { return status; }
    
    @Override
    public String toString() {
        return String.format("Invoice{id='%s', customer='%s', amount=%.2f, status='%s'}", 
                           invoiceId, customerId, amount, status);
    }
}

class InvoiceDetails extends Invoice {
    private final LocalDate paymentDate;
    private final double paidAmount;
    private final String paymentReference;
    
    public InvoiceDetails(String invoiceId, String customerId, double amount, LocalDate creationDate,
                         LocalDate dueDate, InvoiceType invoiceType, PaymentMethod paymentMethod, 
                         InvoiceStatus status, LocalDate paymentDate, double paidAmount, String paymentReference) {
        super(invoiceId, customerId, amount, creationDate, dueDate, invoiceType, paymentMethod, status);
        this.paymentDate = paymentDate;
        this.paidAmount = paidAmount;
        this.paymentReference = paymentReference;
    }
    
    // Additional Getters
    public LocalDate getPaymentDate() { return paymentDate; }
    public double getPaidAmount() { return paidAmount; }
    public String getPaymentReference() { return paymentReference; }
}

class PaymentResult {
    private final boolean success;
    private final PaymentStatus paymentStatus;
    private final String message;
    private final double remainingAmount;
    
    public PaymentResult(boolean success, PaymentStatus paymentStatus, String message, double remainingAmount) {
        this.success = success;
        this.paymentStatus = paymentStatus;
        this.message = message;
        this.remainingAmount = remainingAmount;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getMessage() { return message; }
    public double getRemainingAmount() { return remainingAmount; }
}

// Modern Enums - Type Safety
enum InvoiceType {
    MONTHLY_BILL, INSTALLATION, ADJUSTMENT, TERMINATION, OTHER
}

enum PaymentMethod {
    DIRECT_DEBIT, BANK_TRANSFER, CREDIT_CARD, PAYPAL, OTHER
}

enum InvoiceStatus {
    OPEN, PAID, PARTIALLY_PAID, CANCELLED, ALL
}

enum PaymentStatus {
    FULL_PAYMENT, PARTIAL_PAYMENT, OVERPAYMENT, INVOICE_NOT_FOUND
}