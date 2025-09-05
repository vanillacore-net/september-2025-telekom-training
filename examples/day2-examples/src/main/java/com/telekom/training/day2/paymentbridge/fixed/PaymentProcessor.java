package com.telekom.training.day2.paymentbridge.fixed;

import java.math.BigDecimal;

/**
 * Bridge Pattern: Abstraction
 * Defines the abstraction's interface and maintains reference to implementation
 */
public abstract class PaymentProcessor {
    
    protected final PaymentImplementation implementation;

    public PaymentProcessor(PaymentImplementation implementation) {
        this.implementation = implementation;
    }

    public abstract PaymentResult processPayment(String token, BigDecimal amount, String currency);
    public abstract PaymentResult refundPayment(String transactionId, BigDecimal amount);
    
    public String getProviderName() {
        return implementation.getProviderName();
    }
}