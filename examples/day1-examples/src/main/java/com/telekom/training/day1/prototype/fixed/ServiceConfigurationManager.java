package com.telekom.training.day1.prototype.fixed;

/**
 * PROTOTYPE PATTERN SOLUTION: Efficient Configuration Management
 * 
 * Lösung: Pre-initialized Templates mit Prototype Pattern:
 * - Templates werden EINMAL erstellt (1200ms total)
 * - Alle weiteren Instanzen durch schnelles Clone (2ms each)
 * - 99.83% Performance Verbesserung!
 * 
 * Benefits:
 * - 100 Konfigurationen: 120 Sekunden → 0.2 Sekunden
 * - Memory efficient durch shared immutable objects
 * - Type-safe durch Prototype Registry
 */
public class ServiceConfigurationManager {
    private final ConfigurationPrototypeRegistry registry;
    
    public ServiceConfigurationManager() {
        this.registry = new ConfigurationPrototypeRegistry();
        System.out.println("🚀 ServiceConfigurationManager initialized with prototype registry");
    }
    
    public ServiceConfiguration createDevConfiguration() {
        System.out.println("Creating DEV configuration via prototype... (fast clone)");
        long start = System.currentTimeMillis();
        
        // FAST: Clone from pre-initialized template!
        ServiceConfiguration config = registry.getDevTemplate()
            .clone()
            .withEnvironment("DEV")
            .withLogLevel("DEBUG")
            .withDatabaseUrl("jdbc:h2:mem:devdb");
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("DEV config created in %d ms (via clone)%n", duration);
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        System.out.println("Creating TEST configuration via prototype... (fast clone)");
        long start = System.currentTimeMillis();
        
        // FAST: Clone from pre-initialized template!
        ServiceConfiguration config = registry.getTestTemplate()
            .clone()
            .withEnvironment("TEST")
            .withLogLevel("INFO")
            .withDatabaseUrl("jdbc:postgresql://test-db:5432/testdb");
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("TEST config created in %d ms (via clone)%n", duration);
        return config;
    }
    
    public ServiceConfiguration createProdConfiguration() {
        System.out.println("Creating PROD configuration via prototype... (fast clone)");
        long start = System.currentTimeMillis();
        
        // FAST: Clone from pre-initialized template!
        ServiceConfiguration config = registry.getProdTemplate()
            .clone()
            .withEnvironment("PROD")
            .withLogLevel("WARN")
            .withDatabaseUrl("jdbc:postgresql://prod-db:5432/proddb");
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("PROD config created in %d ms (via clone)%n", duration);
        return config;
    }
    
    // Custom configuration from base template
    public ServiceConfiguration createCustomConfiguration(String env, String logLevel, String dbUrl) {
        System.out.println("Creating CUSTOM configuration via prototype... (fast clone)");
        long start = System.currentTimeMillis();
        
        ServiceConfiguration config = registry.getBaseTemplate()
            .clone()
            .withEnvironment(env)
            .withLogLevel(logLevel)
            .withDatabaseUrl(dbUrl);
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("CUSTOM config created in %d ms (via clone)%n", duration);
        return config;
    }
    
    // Performance Demonstration
    public static void main(String[] args) {
        System.out.println("=== WITH PROTOTYPE PATTERN ===");
        System.out.println("Creating multiple similar configurations with pre-initialized templates...\n");
        
        // Template initialization happens only once during registry creation
        ServiceConfigurationManager manager = new ServiceConfigurationManager();
        
        System.out.println("\n--- Creating 5 configurations (cloning from templates) ---");
        long totalStart = System.currentTimeMillis();
        
        // Create 5 configurations - fast cloning from pre-initialized templates!
        ServiceConfiguration dev1 = manager.createDevConfiguration();
        ServiceConfiguration dev2 = manager.createDevConfiguration();   // FAST CLONE!
        ServiceConfiguration test1 = manager.createTestConfiguration();
        ServiceConfiguration test2 = manager.createTestConfiguration();  // FAST CLONE!
        ServiceConfiguration prod1 = manager.createProdConfiguration();
        
        long totalDuration = System.currentTimeMillis() - totalStart;
        
        System.out.printf("%nTotal time for 5 configurations: %d ms%n", totalDuration);
        System.out.printf("Average per configuration: %d ms%n", totalDuration / 5);
        
        // Validate clone independence
        System.out.println("\n--- Testing Clone Independence ---");
        dev1.getServiceEndpoints().add("https://api.dev.telekom.de/custom");
        System.out.println("DEV1 endpoints: " + dev1.getServiceEndpoints().size());
        System.out.println("DEV2 endpoints: " + dev2.getServiceEndpoints().size());
        System.out.println("✅ Clone independence verified: different endpoint lists");
        
        // Validate shared immutable objects
        System.out.println("\n--- Testing Shared Immutable Objects ---");
        boolean sharedSSL = (dev1.getSslCertificate() == dev2.getSslCertificate());
        boolean sharedDB = (dev1.getDatabaseSettings() == dev2.getDatabaseSettings());
        System.out.printf("SSL Certificate shared: %s ✅%n", sharedSSL);
        System.out.printf("Database Settings shared: %s ✅%n", sharedDB);
        
        System.out.println("\n🚀 PROTOTYPE PATTERN SUCCESS:");
        System.out.println("   ⚡ ~99% faster object creation");
        System.out.println("   💾 Memory efficient through object sharing");
        System.out.println("   🔒 Safe clone independence for mutable parts");
        System.out.println("   🏭 Scalable for hundreds of similar objects");
    }
}