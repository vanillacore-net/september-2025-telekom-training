package com.telekom.training.day2.paymentbridge.fixed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bridge Pattern: Refined Abstraction
 * Payment processor with enhanced logging and auditing
 */
public class LoggingPaymentProcessor extends PaymentProcessor {

    public LoggingPaymentProcessor(PaymentImplementation implementation) {
        super(implementation);
    }

    @Override
    public PaymentResult processPayment(String token, BigDecimal amount, String currency) {
        String maskedToken = maskToken(token);
        System.out.println("📝 AUDIT: Payment request at " + LocalDateTime.now());
        System.out.println("    Provider: " + implementation.getProviderName());
        System.out.println("    Amount: " + amount + " " + currency);
        System.out.println("    Token: " + maskedToken);
        
        long startTime = System.currentTimeMillis();
        PaymentResult result = implementation.processPayment(token, amount, currency);
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("📝 AUDIT: Payment result in " + duration + "ms");
        System.out.println("    Success: " + result.isSuccess());
        System.out.println("    Transaction ID: " + result.getTransactionId());
        
        return result;
    }

    @Override
    public PaymentResult refundPayment(String transactionId, BigDecimal amount) {
        System.out.println("📝 AUDIT: Refund request at " + LocalDateTime.now());
        System.out.println("    Provider: " + implementation.getProviderName());
        System.out.println("    Transaction ID: " + transactionId);
        System.out.println("    Amount: " + amount);
        
        long startTime = System.currentTimeMillis();
        PaymentResult result = implementation.refundPayment(transactionId, amount);
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("📝 AUDIT: Refund result in " + duration + "ms");
        System.out.println("    Success: " + result.isSuccess());
        
        return result;
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "***" + token.substring(token.length() - 4);
    }
}