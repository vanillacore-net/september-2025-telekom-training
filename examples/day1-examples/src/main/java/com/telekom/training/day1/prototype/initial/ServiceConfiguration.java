package com.telekom.training.day1.prototype.initial;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * PROBLEMATISCHER CODE: Expensive Object Creation ohne Prototype Pattern
 * 
 * Problem: Jede Service-Configuration erfordert kostspielige Initialisierung:
 * - Database Connection Discovery (200ms+)
 * - SSL Certificate Validation (500ms+) 
 * - Service Registry Lookup (300ms+)
 * 
 * Für ähnliche Konfigurationen wird diese teure Arbeit unnötig wiederholt!
 */
public class ServiceConfiguration {
    private String environment;
    private String logLevel;
    private String databaseUrl;
    private DatabaseSettings databaseSettings;
    private SecuritySettings securitySettings;
    private List<String> serviceEndpoints;
    private SSLCertificate sslCertificate;
    
    public ServiceConfiguration() {
        // Constructor wird für jede Instanz aufgerufen - EXPENSIVE!
    }
    
    // Full Constructor - EXPENSIVE für ähnliche Configurations!
    public ServiceConfiguration(String environment, String logLevel, String databaseUrl) {
        this.environment = environment;
        this.logLevel = logLevel;
        this.databaseUrl = databaseUrl;
        
        // EXPENSIVE: Diese Operations passieren bei jeder Instanz!
        this.databaseSettings = loadDatabaseDefaults();        // 200ms
        this.securitySettings = loadSecurityDefaults();        // 200ms  
        this.serviceEndpoints = discoverServiceEndpoints();    // 300ms
        this.sslCertificate = validateAndLoadSSL();            // 500ms
        // Total: 1200ms+ für jede Konfiguration!
    }
    
    // Expensive Operations - simuliert reale Enterprise-Kosten
    private DatabaseSettings loadDatabaseDefaults() {
        simulateExpensiveOperation(200); // Database Connection Discovery
        return new DatabaseSettings("default-pool", 30, 5);
    }
    
    private SecuritySettings loadSecurityDefaults() {
        simulateExpensiveOperation(200); // Security Policy Loading
        return new SecuritySettings("AES-256", "TLS-1.3", true);
    }
    
    private List<String> discoverServiceEndpoints() {
        simulateExpensiveOperation(300); // Service Registry Lookup
        List<String> endpoints = new ArrayList<>();
        endpoints.add("https://api.telekom.de/customers");
        endpoints.add("https://api.telekom.de/billing");
        endpoints.add("https://api.telekom.de/notifications");
        return endpoints;
    }
    
    private SSLCertificate validateAndLoadSSL() {
        simulateExpensiveOperation(500); // Certificate Validation
        return new SSLCertificate("CN=*.telekom.de", "2025-12-31");
    }
    
    private void simulateExpensiveOperation(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Getters and Setters
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    
    public String getDatabaseUrl() { return databaseUrl; }
    public void setDatabaseUrl(String databaseUrl) { this.databaseUrl = databaseUrl; }
    
    public DatabaseSettings getDatabaseSettings() { return databaseSettings; }
    public SecuritySettings getSecuritySettings() { return securitySettings; }
    public List<String> getServiceEndpoints() { return serviceEndpoints; }
    public SSLCertificate getSslCertificate() { return sslCertificate; }
    
    @Override
    public String toString() {
        return String.format("ServiceConfiguration{env='%s', logLevel='%s', dbUrl='%s'}", 
            environment, logLevel, databaseUrl);
    }
}