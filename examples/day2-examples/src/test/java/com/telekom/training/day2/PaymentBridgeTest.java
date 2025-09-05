package com.telekom.training.day2;

import com.telekom.training.day2.paymentbridge.fixed.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test demonstrating Bridge pattern for decoupling abstraction from implementation
 */
class PaymentBridgeTest {

    @Test
    @DisplayName("PayPal implementation works with different processors")
    void testPaypalWithDifferentProcessors() {
        PaymentImplementation paypal = new PaypalImplementation();
        
        // Test with simple processor
        PaymentProcessor simpleProcessor = new SimplePaymentProcessor(paypal);
        PaymentResult result1 = simpleProcessor.processPayment("paypal_token_123", 
            new BigDecimal("29.99"), "EUR");
        
        assertTrue(result1.isSuccess());
        assertEquals("PayPal", result1.getProviderName());
        assertNotNull(result1.getTransactionId());
        
        // Test with logging processor - same implementation, different abstraction
        PaymentProcessor loggingProcessor = new LoggingPaymentProcessor(paypal);
        PaymentResult result2 = loggingProcessor.processPayment("paypal_token_456", 
            new BigDecimal("49.99"), "EUR");
        
        assertTrue(result2.isSuccess());
        assertEquals("PayPal", result2.getProviderName());
        // Same provider, different processor behavior
    }

    @Test
    @DisplayName("Stripe implementation works with different processors")
    void testStripeWithDifferentProcessors() {
        PaymentImplementation stripe = new StripeImplementation();
        
        // Test with simple processor
        PaymentProcessor simpleProcessor = new SimplePaymentProcessor(stripe);
        PaymentResult result1 = simpleProcessor.processPayment("stripe_token_123", 
            new BigDecimal("39.99"), "USD");
        
        assertTrue(result1.isSuccess());
        assertEquals("Stripe", result1.getProviderName());
        assertNotNull(result1.getTransactionId());
        
        // Test with logging processor
        PaymentProcessor loggingProcessor = new LoggingPaymentProcessor(stripe);
        PaymentResult result2 = loggingProcessor.processPayment("stripe_token_456", 
            new BigDecimal("59.99"), "USD");
        
        assertTrue(result2.isSuccess());
        assertEquals("Stripe", result2.getProviderName());
    }

    @Test
    @DisplayName("Bridge pattern allows runtime switching of implementations")
    void testRuntimeSwitching() {
        PaymentImplementation paypal = new PaypalImplementation();
        PaymentImplementation stripe = new StripeImplementation();
        
        // Same processor can work with different implementations
        PaymentProcessor processor = new SimplePaymentProcessor(paypal);
        
        PaymentResult paypalResult = processor.processPayment("token1", 
            new BigDecimal("25.00"), "EUR");
        assertEquals("PayPal", paypalResult.getProviderName());
        
        // Switch implementation at runtime
        processor = new SimplePaymentProcessor(stripe);
        
        PaymentResult stripeResult = processor.processPayment("token2", 
            new BigDecimal("25.00"), "EUR");
        assertEquals("Stripe", stripeResult.getProviderName());
        
        // Same interface, different behavior
    }

    @Test
    @DisplayName("Refunds work uniformly across providers")
    void testUniformRefunds() {
        PaymentImplementation paypal = new PaypalImplementation();
        PaymentImplementation stripe = new StripeImplementation();
        
        PaymentProcessor paypalProcessor = new SimplePaymentProcessor(paypal);
        PaymentProcessor stripeProcessor = new SimplePaymentProcessor(stripe);
        
        // Process payments
        PaymentResult paypalPayment = paypalProcessor.processPayment("paypal_token", 
            new BigDecimal("50.00"), "EUR");
        PaymentResult stripePayment = stripeProcessor.processPayment("stripe_token", 
            new BigDecimal("50.00"), "EUR");
        
        // Refund through same interface
        PaymentResult paypalRefund = paypalProcessor.refundPayment(
            paypalPayment.getTransactionId(), new BigDecimal("50.00"));
        PaymentResult stripeRefund = stripeProcessor.refundPayment(
            stripePayment.getTransactionId(), new BigDecimal("50.00"));
        
        // Uniform handling despite different providers
        assertTrue(paypalRefund.isSuccess());
        assertTrue(stripeRefund.isSuccess());
        assertEquals("PayPal", paypalRefund.getProviderName());
        assertEquals("Stripe", stripeRefund.getProviderName());
    }

    @Test
    @DisplayName("Logging processor adds behavior without changing implementation")
    void testLoggingProcessorBehavior() {
        PaymentImplementation paypal = new PaypalImplementation();
        
        // Compare simple vs logging processor
        PaymentProcessor simpleProcessor = new SimplePaymentProcessor(paypal);
        PaymentProcessor loggingProcessor = new LoggingPaymentProcessor(paypal);
        
        long startSimple = System.currentTimeMillis();
        PaymentResult simpleResult = simpleProcessor.processPayment("token", 
            new BigDecimal("30.00"), "EUR");
        long simpleTime = System.currentTimeMillis() - startSimple;
        
        long startLogging = System.currentTimeMillis();
        PaymentResult loggingResult = loggingProcessor.processPayment("token", 
            new BigDecimal("30.00"), "EUR");
        long loggingTime = System.currentTimeMillis() - startLogging;
        
        // Same result from same implementation
        assertEquals(simpleResult.getProviderName(), loggingResult.getProviderName());
        assertTrue(simpleResult.isSuccess());
        assertTrue(loggingResult.isSuccess());
        
        // But logging adds behavior (and slight overhead)
        // The logging processor should take slightly more time due to logging operations
        System.out.println("Simple processor time: " + simpleTime + "ms");
        System.out.println("Logging processor time: " + loggingTime + "ms");
    }

    @Test
    @DisplayName("Bridge pattern enables easy extension with new providers")
    void testExtensibilityDemo() {
        // Simulate adding a new payment provider
        PaymentImplementation mockNewProvider = new PaymentImplementation() {
            @Override
            public PaymentResult processPayment(String token, BigDecimal amount, String currency) {
                return new PaymentResult("MOCK_" + System.currentTimeMillis(), true, 
                    "Mock payment successful", "MockPay");
            }
            
            @Override
            public PaymentResult refundPayment(String transactionId, BigDecimal amount) {
                return new PaymentResult("MOCK_REF_" + System.currentTimeMillis(), true, 
                    "Mock refund successful", "MockPay");
            }
            
            @Override
            public String getProviderName() {
                return "MockPay";
            }
        };
        
        // New provider works immediately with existing processors
        PaymentProcessor simpleProcessor = new SimplePaymentProcessor(mockNewProvider);
        PaymentProcessor loggingProcessor = new LoggingPaymentProcessor(mockNewProvider);
        
        PaymentResult result1 = simpleProcessor.processPayment("mock_token", 
            new BigDecimal("15.00"), "EUR");
        PaymentResult result2 = loggingProcessor.processPayment("mock_token", 
            new BigDecimal("15.00"), "EUR");
        
        assertEquals("MockPay", result1.getProviderName());
        assertEquals("MockPay", result2.getProviderName());
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        
        // No changes needed to existing code - that's the power of Bridge pattern!
    }
}