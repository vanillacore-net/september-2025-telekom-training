package com.telekom.training.day1.prototype.fixed;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * PROTOTYPE PATTERN: Effiziente Objektklonierung für kostspielige Initialisierung
 * 
 * Lösung: Prototype Pattern mit intelligenter Clone-Strategie:
 * - Template-Objekte werden EINMAL erstellt (1200ms)
 * - Alle weiteren Instanzen durch schnelles Cloning (2ms)
 * - Selective Deep Copy: nur mutable Parts werden kopiert
 * - Immutable Parts werden geshared (SSL Cert, Service Discovery)
 * 
 * Performance: 1200ms → 2ms = 99.83% Verbesserung!
 */
public class ServiceConfiguration implements Cloneable {
    private String environment;
    private String logLevel;
    private String databaseUrl;
    private DatabaseSettings databaseSettings;
    private SecuritySettings securitySettings;
    private List<String> serviceEndpoints;
    private SSLCertificate sslCertificate;
    
    // Private Constructor - nur für interne Verwendung
    private ServiceConfiguration() {
    }
    
    // Template Factory Method - EXPENSIVE aber nur einmal pro Template!
    public static ServiceConfiguration createBaseTemplate() {
        System.out.println("⚙️ Creating base template... (expensive operations)");
        ServiceConfiguration template = new ServiceConfiguration();
        
        // EXPENSIVE Operations - aber nur EINMAL pro Template!
        template.databaseSettings = loadDatabaseDefaults();        // 200ms
        template.securitySettings = loadSecurityDefaults();        // 200ms
        template.serviceEndpoints = discoverServiceEndpoints();    // 300ms
        template.sslCertificate = validateAndLoadSSL();            // 500ms
        // Total: 1200ms - aber nur einmal!
        
        System.out.println("✅ Base template created with expensive operations completed");
        return template;
    }
    
    // Prototype Pattern - Intelligent Clone Implementation
    @Override
    public ServiceConfiguration clone() {
        try {
            ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
            
            // DEEP COPY für mutable objects die pro Instanz unterschiedlich sein können
            cloned.serviceEndpoints = new ArrayList<>(this.serviceEndpoints);
            
            // SHALLOW COPY für immutable, expensive objects
            // SSL Certificate ist immutable und teuer zu validieren → Sharing OK
            cloned.sslCertificate = this.sslCertificate;
            
            // Database/Security Settings sind immutable value objects → Sharing OK
            cloned.databaseSettings = this.databaseSettings;
            cloned.securitySettings = this.securitySettings;
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Builder-style methods für fluent configuration
    public ServiceConfiguration withEnvironment(String environment) {
        this.environment = environment;
        return this;
    }
    
    public ServiceConfiguration withLogLevel(String logLevel) {
        this.logLevel = logLevel;
        return this;
    }
    
    public ServiceConfiguration withDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        return this;
    }
    
    // Expensive Operations - werden nur einmal pro Template aufgerufen
    private static DatabaseSettings loadDatabaseDefaults() {
        simulateExpensiveOperation(200); // Database Connection Discovery
        return new DatabaseSettings("default-pool", 30, 5);
    }
    
    private static SecuritySettings loadSecurityDefaults() {
        simulateExpensiveOperation(200); // Security Policy Loading
        return new SecuritySettings("AES-256", "TLS-1.3", true);
    }
    
    private static List<String> discoverServiceEndpoints() {
        simulateExpensiveOperation(300); // Service Registry Lookup
        List<String> endpoints = new ArrayList<>();
        endpoints.add("https://api.telekom.de/customers");
        endpoints.add("https://api.telekom.de/billing");
        endpoints.add("https://api.telekom.de/notifications");
        return endpoints;
    }
    
    private static SSLCertificate validateAndLoadSSL() {
        simulateExpensiveOperation(500); // Certificate Validation
        return new SSLCertificate("CN=*.telekom.de", "2025-12-31");
    }
    
    private static void simulateExpensiveOperation(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Getters 
    public String getEnvironment() { return environment; }
    public String getLogLevel() { return logLevel; }
    public String getDatabaseUrl() { return databaseUrl; }
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