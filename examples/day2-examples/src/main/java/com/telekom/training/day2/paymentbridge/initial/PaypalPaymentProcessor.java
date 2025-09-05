package com.telekom.training.day2.paymentbridge.initial;

import java.math.BigDecimal;

/**
 * PROBLEM: Each payment provider has its own API and method signatures
 * Client code must know about all different providers and handle them differently
 */
public class PaypalPaymentProcessor {
    
    public PaypalResult processPaypalPayment(String paypalToken, BigDecimal amount, String currency) {
        // Simulate PayPal specific processing
        System.out.println("🏦 Processing PayPal payment: " + amount + " " + currency);
        System.out.println("    Using PayPal token: " + paypalToken);
        
        try {
            Thread.sleep(200); // Simulate PayPal API call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // PayPal specific result format
        return new PaypalResult("PP_" + System.currentTimeMillis(), "COMPLETED", "Payment successful");
    }
    
    public PaypalResult refundPaypalPayment(String paypalTransactionId, BigDecimal amount) {
        System.out.println("🔄 Refunding PayPal payment: " + paypalTransactionId + " amount: " + amount);
        
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return new PaypalResult("REF_" + System.currentTimeMillis(), "REFUNDED", "Refund successful");
    }

    // PayPal specific result class
    public static class PaypalResult {
        private final String transactionId;
        private final String status;
        private final String message;

        public PaypalResult(String transactionId, String status, String message) {
            this.transactionId = transactionId;
            this.status = status;
            this.message = message;
        }

        public String getTransactionId() { return transactionId; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }
}