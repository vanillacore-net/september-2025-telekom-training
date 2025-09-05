package com.telekom.training.day2.paymentbridge.initial;

import java.math.BigDecimal;

/**
 * PROBLEM: Client code tightly coupled to specific payment providers
 * Must handle each provider differently with ugly if-else chains
 */
public class OrderService {
    
    private final PaypalPaymentProcessor paypalProcessor;
    private final StripePaymentHandler stripeHandler;

    public OrderService() {
        this.paypalProcessor = new PaypalPaymentProcessor();
        this.stripeHandler = new StripePaymentHandler();
    }

    // PROBLEM: Must handle each provider differently!
    public String processPayment(String paymentMethod, String token, BigDecimal amount, String currency) {
        
        if ("PAYPAL".equals(paymentMethod)) {
            // PayPal specific handling
            PaypalPaymentProcessor.PaypalResult result = 
                paypalProcessor.processPaypalPayment(token, amount, currency);
            
            if ("COMPLETED".equals(result.getStatus())) {
                return result.getTransactionId();
            } else {
                throw new RuntimeException("PayPal payment failed: " + result.getMessage());
            }
            
        } else if ("STRIPE".equals(paymentMethod)) {
            // Stripe specific handling - different API!
            int amountInCents = amount.multiply(new BigDecimal("100")).intValue();
            StripePaymentHandler.StripeResponse response = 
                stripeHandler.chargeCard(token, amountInCents, currency);
            
            if (response.isSuccess()) {
                return response.getId();
            } else {
                throw new RuntimeException("Stripe payment failed: " + response.getErrorMessage());
            }
            
        } else {
            throw new RuntimeException("Unknown payment method: " + paymentMethod);
        }
        
        // What if we add Apple Pay, Google Pay, Bank Transfer?
        // More if-else blocks with different APIs!
    }

    // PROBLEM: More provider-specific handling for refunds
    public boolean refundPayment(String paymentMethod, String transactionId, BigDecimal amount) {
        
        if ("PAYPAL".equals(paymentMethod)) {
            PaypalPaymentProcessor.PaypalResult result = 
                paypalProcessor.refundPaypalPayment(transactionId, amount);
            return "REFUNDED".equals(result.getStatus());
            
        } else if ("STRIPE".equals(paymentMethod)) {
            int amountInCents = amount.multiply(new BigDecimal("100")).intValue();
            StripePaymentHandler.StripeResponse response = 
                stripeHandler.createRefund(transactionId, amountInCents);
            return response.isSuccess();
            
        } else {
            throw new RuntimeException("Unknown payment method: " + paymentMethod);
        }
    }
}