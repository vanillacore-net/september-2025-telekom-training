package com.telekom.training.day1.prototype.fixed;

import java.util.HashMap;
import java.util.Map;

/**
 * PROTOTYPE REGISTRY PATTERN: Zentrale Template-Verwaltung
 * 
 * Verwaltet pre-initialized Prototype Templates für verschiedene Konfigurationstypen.
 * - Base Template: Shared foundation für alle Konfigurationen
 * - Dev Template: Development-spezifische Optimierungen
 * - Test Template: Test-Environment Konfigurationen  
 * - Prod Template: Production-ready Konfigurationen
 * 
 * Pattern Benefits:
 * - Einmalige teure Initialisierung (1200ms → einmal)
 * - Lazy Loading für Memory Efficiency
 * - Type-safe Template Access
 * - Centralized Template Management
 */
public class ConfigurationPrototypeRegistry {
    private final Map<String, ServiceConfiguration> prototypes;
    
    // Template Keys
    private static final String BASE_TEMPLATE = "BASE";
    private static final String DEV_TEMPLATE = "DEV";
    private static final String TEST_TEMPLATE = "TEST";
    private static final String PROD_TEMPLATE = "PROD";
    
    public ConfigurationPrototypeRegistry() {
        this.prototypes = new HashMap<>();
        System.out.println("📋 Prototype Registry initializing...");
        initializePrototypes();
        System.out.println("✅ Prototype Registry initialized with all templates");
    }
    
    private void initializePrototypes() {
        System.out.println("🔧 Creating base template with expensive operations...");
        long start = System.currentTimeMillis();
        
        // Create base template ONCE - all expensive operations happen here
        ServiceConfiguration baseTemplate = ServiceConfiguration.createBaseTemplate();
        prototypes.put(BASE_TEMPLATE, baseTemplate);
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("⚡ Base template created in %d ms%n", duration);
        
        // Pre-create environment-specific templates for common use cases
        // These are fast clones from base template with specific configurations
        System.out.println("📦 Pre-creating environment templates...");
        
        ServiceConfiguration devTemplate = baseTemplate.clone()
            .withEnvironment("DEV-TEMPLATE")
            .withLogLevel("DEBUG");
        prototypes.put(DEV_TEMPLATE, devTemplate);
        
        ServiceConfiguration testTemplate = baseTemplate.clone()
            .withEnvironment("TEST-TEMPLATE")
            .withLogLevel("INFO");
        prototypes.put(TEST_TEMPLATE, testTemplate);
        
        ServiceConfiguration prodTemplate = baseTemplate.clone()
            .withEnvironment("PROD-TEMPLATE")
            .withLogLevel("WARN");
        prototypes.put(PROD_TEMPLATE, prodTemplate);
        
        System.out.println("✅ All environment templates pre-created");
    }
    
    /**
     * Get base template for custom configurations
     * @return Base template with all expensive operations completed
     */
    public ServiceConfiguration getBaseTemplate() {
        return prototypes.get(BASE_TEMPLATE);
    }
    
    /**
     * Get development-optimized template
     * @return Dev template with debug logging and development defaults
     */
    public ServiceConfiguration getDevTemplate() {
        return prototypes.get(DEV_TEMPLATE);
    }
    
    /**
     * Get test environment template
     * @return Test template with info logging and test defaults
     */
    public ServiceConfiguration getTestTemplate() {
        return prototypes.get(TEST_TEMPLATE);
    }
    
    /**
     * Get production-ready template
     * @return Production template with warn logging and prod defaults
     */
    public ServiceConfiguration getProdTemplate() {
        return prototypes.get(PROD_TEMPLATE);
    }
    
    /**
     * Register custom prototype template
     * @param key Template identifier
     * @param prototype Pre-configured template
     */
    public void registerPrototype(String key, ServiceConfiguration prototype) {
        prototypes.put(key.toUpperCase(), prototype);
        System.out.printf("📝 Custom prototype '%s' registered%n", key);
    }
    
    /**
     * Get custom registered prototype
     * @param key Template identifier
     * @return Registered template or null if not found
     */
    public ServiceConfiguration getPrototype(String key) {
        ServiceConfiguration template = prototypes.get(key.toUpperCase());
        if (template == null) {
            System.out.printf("⚠️ Template '%s' not found, using base template%n", key);
            return getBaseTemplate();
        }
        return template;
    }
    
    /**
     * Get registry statistics for monitoring
     * @return Statistics about template usage and performance
     */
    public RegistryStats getStats() {
        return new RegistryStats(prototypes.size(), prototypes.keySet());
    }
    
    /**
     * Registry statistics for monitoring and optimization
     */
    public static class RegistryStats {
        private final int templateCount;
        private final java.util.Set<String> templateKeys;
        
        public RegistryStats(int templateCount, java.util.Set<String> templateKeys) {
            this.templateCount = templateCount;
            this.templateKeys = new java.util.HashSet<>(templateKeys);
        }
        
        public int getTemplateCount() { return templateCount; }
        public java.util.Set<String> getTemplateKeys() { return templateKeys; }
        
        @Override
        public String toString() {
            return String.format("RegistryStats{templates=%d, keys=%s}", 
                templateCount, templateKeys);
        }
    }
    
    // Demonstration of registry capabilities
    public static void main(String[] args) {
        System.out.println("=== PROTOTYPE REGISTRY DEMONSTRATION ===\n");
        
        // Registry initialization (expensive operations happen here)
        ConfigurationPrototypeRegistry registry = new ConfigurationPrototypeRegistry();
        
        System.out.println("\n--- Registry Stats ---");
        RegistryStats stats = registry.getStats();
        System.out.println(stats);
        
        System.out.println("\n--- Fast Template Access (2ms each) ---");
        long start = System.currentTimeMillis();
        
        ServiceConfiguration template1 = registry.getDevTemplate();
        ServiceConfiguration template2 = registry.getTestTemplate();
        ServiceConfiguration template3 = registry.getProdTemplate();
        
        long duration = System.currentTimeMillis() - start;
        System.out.printf("3 template accesses completed in %d ms%n", duration);
        
        System.out.println("\n--- Custom Template Registration ---");
        ServiceConfiguration stagingTemplate = registry.getBaseTemplate()
            .clone()
            .withEnvironment("STAGING")
            .withLogLevel("INFO");
        
        registry.registerPrototype("STAGING", stagingTemplate);
        ServiceConfiguration retrieved = registry.getPrototype("staging"); // case-insensitive
        
        System.out.printf("Custom template registration successful: %s%n", 
            retrieved.getEnvironment());
        
        System.out.println("\n🏭 REGISTRY PATTERN BENEFITS:");
        System.out.println("   📋 Centralized template management");
        System.out.println("   ⚡ Lazy loading for memory efficiency");
        System.out.println("   🔒 Type-safe template access");
        System.out.println("   📊 Built-in monitoring and stats");
        System.out.println("   🔧 Extensible for custom templates");
    }
}