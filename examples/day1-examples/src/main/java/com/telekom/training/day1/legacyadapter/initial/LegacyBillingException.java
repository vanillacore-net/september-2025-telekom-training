package com.telekom.training.day1.legacyadapter.initial;

/**
 * Legacy Exception - Public for Adapter access
 */
public class LegacyBillingException extends Exception {
    private String errorCode;
    
    public LegacyBillingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}