package com.telekom.training.day2.paymentbridge.fixed;

/**
 * Unified result class for all payment operations
 */
public class PaymentResult {
    private final String transactionId;
    private final boolean success;
    private final String message;
    private final String providerName;

    public PaymentResult(String transactionId, boolean success, String message, String providerName) {
        this.transactionId = transactionId;
        this.success = success;
        this.message = message;
        this.providerName = providerName;
    }

    public String getTransactionId() { return transactionId; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getProviderName() { return providerName; }

    @Override
    public String toString() {
        return String.format("PaymentResult{transactionId='%s', success=%s, message='%s', provider='%s'}", 
            transactionId, success, message, providerName);
    }
}