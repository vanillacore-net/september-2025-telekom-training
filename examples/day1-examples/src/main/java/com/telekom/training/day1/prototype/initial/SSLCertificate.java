package com.telekom.training.day1.prototype.initial;

public class SSLCertificate {
    private final String subject;
    private final String expiryDate;
    
    public SSLCertificate(String subject, String expiryDate) {
        this.subject = subject;
        this.expiryDate = expiryDate;
    }
    
    public String getSubject() { return subject; }
    public String getExpiryDate() { return expiryDate; }
    
    @Override
    public String toString() {
        return String.format("SSLCertificate{subject='%s', expires='%s'}", subject, expiryDate);
    }
}