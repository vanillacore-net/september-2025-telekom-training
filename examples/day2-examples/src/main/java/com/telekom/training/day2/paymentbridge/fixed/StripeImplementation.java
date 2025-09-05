package com.telekom.training.day2.paymentbridge.fixed;

import com.telekom.training.day2.paymentbridge.initial.StripePaymentHandler;
import java.math.BigDecimal;

/**
 * Bridge Pattern: Concrete Implementation
 * Adapter for Stripe API to common interface
 */
public class StripeImplementation implements PaymentImplementation {
    
    private final StripePaymentHandler stripeHandler;

    public StripeImplementation() {
        this.stripeHandler = new StripePaymentHandler();
    }

    @Override
    public PaymentResult processPayment(String token, BigDecimal amount, String currency) {
        try {
            // Convert to Stripe's expected format (cents)
            int amountInCents = amount.multiply(new BigDecimal("100")).intValue();
            
            StripePaymentHandler.StripeResponse response = 
                stripeHandler.chargeCard(token, amountInCents, currency);
            
            String message = response.isSuccess() ? "Payment successful" : response.getErrorMessage();
            return new PaymentResult(response.getId(), response.isSuccess(), message, "Stripe");
            
        } catch (Exception e) {
            return new PaymentResult(null, false, "Stripe error: " + e.getMessage(), "Stripe");
        }
    }

    @Override
    public PaymentResult refundPayment(String transactionId, BigDecimal amount) {
        try {
            // Convert to Stripe's expected format (cents)
            int amountInCents = amount.multiply(new BigDecimal("100")).intValue();
            
            StripePaymentHandler.StripeResponse response = 
                stripeHandler.createRefund(transactionId, amountInCents);
            
            String message = response.isSuccess() ? "Refund successful" : response.getErrorMessage();
            return new PaymentResult(response.getId(), response.isSuccess(), message, "Stripe");
            
        } catch (Exception e) {
            return new PaymentResult(null, false, "Stripe refund error: " + e.getMessage(), "Stripe");
        }
    }

    @Override
    public String getProviderName() {
        return "Stripe";
    }
}