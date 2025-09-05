package com.telekom.training.day2.paymentbridge.fixed;

import java.math.BigDecimal;

/**
 * Bridge Pattern: Refined Abstraction
 * Simple payment processor without additional features
 */
public class SimplePaymentProcessor extends PaymentProcessor {

    public SimplePaymentProcessor(PaymentImplementation implementation) {
        super(implementation);
    }

    @Override
    public PaymentResult processPayment(String token, BigDecimal amount, String currency) {
        System.out.println("🔄 Processing payment via " + implementation.getProviderName());
        return implementation.processPayment(token, amount, currency);
    }

    @Override
    public PaymentResult refundPayment(String transactionId, BigDecimal amount) {
        System.out.println("🔄 Processing refund via " + implementation.getProviderName());
        return implementation.refundPayment(transactionId, amount);
    }
}