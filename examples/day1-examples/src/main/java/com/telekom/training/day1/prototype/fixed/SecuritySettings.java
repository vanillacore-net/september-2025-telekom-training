package com.telekom.training.day1.prototype.fixed;

/**
 * Immutable Value Object - kann sicher geshared werden zwischen Clones
 */
public class SecuritySettings {
    private final String encryption;
    private final String tlsVersion;
    private final boolean certificateValidation;
    
    public SecuritySettings(String encryption, String tlsVersion, boolean certificateValidation) {
        this.encryption = encryption;
        this.tlsVersion = tlsVersion;
        this.certificateValidation = certificateValidation;
    }
    
    public String getEncryption() { return encryption; }
    public String getTlsVersion() { return tlsVersion; }
    public boolean isCertificateValidation() { return certificateValidation; }
    
    @Override
    public String toString() {
        return String.format("SecuritySettings{encryption='%s', tls='%s', certValidation=%b}", 
            encryption, tlsVersion, certificateValidation);
    }
}