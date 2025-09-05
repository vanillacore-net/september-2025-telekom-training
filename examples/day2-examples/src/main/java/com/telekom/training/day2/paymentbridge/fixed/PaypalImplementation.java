package com.telekom.training.day2.paymentbridge.fixed;

import com.telekom.training.day2.paymentbridge.initial.PaypalPaymentProcessor;
import java.math.BigDecimal;

/**
 * Bridge Pattern: Concrete Implementation
 * Adapter for PayPal API to common interface
 */
public class PaypalImplementation implements PaymentImplementation {
    
    private final PaypalPaymentProcessor paypalProcessor;

    public PaypalImplementation() {
        this.paypalProcessor = new PaypalPaymentProcessor();
    }

    @Override
    public PaymentResult processPayment(String token, BigDecimal amount, String currency) {
        try {
            PaypalPaymentProcessor.PaypalResult result = 
                paypalProcessor.processPaypalPayment(token, amount, currency);
            
            boolean success = "COMPLETED".equals(result.getStatus());
            return new PaymentResult(result.getTransactionId(), success, result.getMessage(), "PayPal");
            
        } catch (Exception e) {
            return new PaymentResult(null, false, "PayPal error: " + e.getMessage(), "PayPal");
        }
    }

    @Override
    public PaymentResult refundPayment(String transactionId, BigDecimal amount) {
        try {
            PaypalPaymentProcessor.PaypalResult result = 
                paypalProcessor.refundPaypalPayment(transactionId, amount);
            
            boolean success = "REFUNDED".equals(result.getStatus());
            return new PaymentResult(result.getTransactionId(), success, result.getMessage(), "PayPal");
            
        } catch (Exception e) {
            return new PaymentResult(null, false, "PayPal refund error: " + e.getMessage(), "PayPal");
        }
    }

    @Override
    public String getProviderName() {
        return "PayPal";
    }
}