package com.telekom.training.day1.legacyadapter.initial;

import java.util.Date;

/**
 * Legacy Data Structure - Public for Adapter access
 */
public class OldInvoiceRecord {
    public String invoiceId;
    public String customerId;
    public double totalAmount;
    public Date creationDate;
    public Date dueDate;
    public String invoiceType;
    public String paymentMethod;
    public String status;
    public Date paymentDate;
    public double paidAmount;
    public String paymentReference;
}