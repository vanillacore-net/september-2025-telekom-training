package com.telekom.training.day2.paymentbridge.fixed;

import java.math.BigDecimal;

/**
 * Bridge Pattern: Implementation
 * Defines the interface for implementation classes
 */
public interface PaymentImplementation {
    PaymentResult processPayment(String token, BigDecimal amount, String currency);
    PaymentResult refundPayment(String transactionId, BigDecimal amount);
    String getProviderName();
}