package com.telekom.training.day2.paymentbridge.initial;

import java.math.BigDecimal;

/**
 * PROBLEM: Completely different API than PayPal
 * Different method names, parameters, return types
 */
public class StripePaymentHandler {
    
    public StripeResponse chargeCard(String cardToken, int amountInCents, String currencyCode) {
        // Stripe uses cents, not decimal amounts!
        System.out.println("💳 Processing Stripe payment: " + amountInCents + " cents " + currencyCode);
        System.out.println("    Using card token: " + cardToken);
        
        try {
            Thread.sleep(150); // Simulate Stripe API call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Stripe specific result format
        return new StripeResponse("ch_" + System.currentTimeMillis(), true, null, "succeeded");
    }
    
    public StripeResponse createRefund(String chargeId, int amountInCents) {
        System.out.println("🔄 Creating Stripe refund for: " + chargeId + " amount: " + amountInCents + " cents");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return new StripeResponse("re_" + System.currentTimeMillis(), true, null, "succeeded");
    }

    // Stripe specific response class with different structure
    public static class StripeResponse {
        private final String id;
        private final boolean success;
        private final String errorMessage;
        private final String status;

        public StripeResponse(String id, boolean success, String errorMessage, String status) {
            this.id = id;
            this.success = success;
            this.errorMessage = errorMessage;
            this.status = status;
        }

        public String getId() { return id; }
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public String getStatus() { return status; }
    }
}