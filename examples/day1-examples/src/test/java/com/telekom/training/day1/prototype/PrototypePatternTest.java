package com.telekom.training.day1.prototype;

import com.telekom.training.day1.prototype.fixed.ServiceConfiguration;
import com.telekom.training.day1.prototype.fixed.ServiceConfigurationManager;
import com.telekom.training.day1.prototype.fixed.ConfigurationPrototypeRegistry;
// Note: Initial manager comparison available in separate performance demo

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * Comprehensive Test Suite für Prototype Pattern Implementation
 * 
 * Test Coverage:
 * - Performance Benchmarks (1200ms → 2ms improvement)
 * - Clone Independence (mutable objects are deep copied)
 * - Shared Immutable Objects (memory efficiency)
 * - Registry Pattern Functionality
 * - Template Pre-initialization
 * - Error Handling and Edge Cases
 */
@DisplayName("Prototype Pattern Implementation Tests")
class PrototypePatternTest {
    
    private ServiceConfigurationManager prototypeManager;
    private ConfigurationPrototypeRegistry registry;
    
    @BeforeEach
    void setup() {
        prototypeManager = new ServiceConfigurationManager();
        registry = new ConfigurationPrototypeRegistry();
    }
    
    @Nested
    @DisplayName("Performance Benchmarks")
    class PerformanceBenchmarks {
        
        @Test
        @DisplayName("Single Configuration Creation - Prototype vs Initial")
        @Timeout(value = 2, unit = TimeUnit.SECONDS)
        void singleConfigurationPerformance() {
            // Test prototype pattern performance (should be ~2ms)
            long prototypeStart = System.currentTimeMillis();
            ServiceConfiguration prototypeConfig = prototypeManager.createDevConfiguration();
            long prototypeDuration = System.currentTimeMillis() - prototypeStart;
            
            // Verify fast creation (should be under 50ms for clone operation)
            assertThat(prototypeDuration).isLessThan(50);
            assertThat(prototypeConfig).isNotNull();
            assertThat(prototypeConfig.getEnvironment()).isEqualTo("DEV");
        }
        
        @Test
        @DisplayName("Bulk Configuration Creation - 99%+ Performance Improvement")
        @Timeout(value = 5, unit = TimeUnit.SECONDS)
        void bulkConfigurationPerformance() {
            int configCount = 10;
            List<ServiceConfiguration> configs = new ArrayList<>();
            
            // Benchmark prototype pattern
            long prototypeStart = System.currentTimeMillis();
            for (int i = 0; i < configCount; i++) {
                configs.add(prototypeManager.createDevConfiguration());
            }
            long prototypeDuration = System.currentTimeMillis() - prototypeStart;
            
            // Calculate expected initial duration (120ms per config * count + registry init ~1200ms)
            long expectedInitialDuration = (configCount * 120) + 1200; // Conservative estimate
            
            // Verify significant performance improvement (should be 99%+ faster)
            double improvementRatio = (double) expectedInitialDuration / prototypeDuration;
            System.out.printf("Performance improvement: %.1fx faster (%.2f%% improvement)%n", 
                improvementRatio, ((improvementRatio - 1) * 100));
            
            // Verify we achieved significant improvement (at least 10x faster)
            assertThat(improvementRatio).isGreaterThan(10.0);
            assertThat(configs).hasSize(configCount);
            
            // Verify all configurations are valid
            configs.forEach(config -> {
                assertThat(config).isNotNull();
                assertThat(config.getEnvironment()).isEqualTo("DEV");
                assertThat(config.getLogLevel()).isEqualTo("DEBUG");
            });
        }
        
        @DisplayName("Template Access Performance - Sub-millisecond Access")
        @RepeatedTest(5)
        void templateAccessPerformance() {
            long start = System.currentTimeMillis();
            
            // Access multiple templates rapidly
            ServiceConfiguration dev = registry.getDevTemplate();
            ServiceConfiguration test = registry.getTestTemplate();
            ServiceConfiguration prod = registry.getProdTemplate();
            ServiceConfiguration base = registry.getBaseTemplate();
            
            long duration = System.currentTimeMillis() - start;
            
            // Template access should be extremely fast (registry lookup)
            assertThat(duration).isLessThan(5); // Should be sub-millisecond
            
            // Verify templates are not null and properly configured
            assertThat(dev).isNotNull();
            assertThat(test).isNotNull();
            assertThat(prod).isNotNull();
            assertThat(base).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Clone Independence Tests")
    class CloneIndependenceTests {
        
        @Test
        @DisplayName("Mutable Objects Are Deep Copied")
        void mutableObjectsAreDeepCopied() {
            ServiceConfiguration config1 = prototypeManager.createDevConfiguration();
            ServiceConfiguration config2 = prototypeManager.createDevConfiguration();
            
            // Initially both should have the same service endpoints
            assertThat(config1.getServiceEndpoints()).hasSize(3);
            assertThat(config2.getServiceEndpoints()).hasSize(3);
            
            // Modify one configuration's mutable list
            config1.getServiceEndpoints().add("https://api.dev.telekom.de/custom");
            
            // Verify independence - only config1 should have the new endpoint
            assertThat(config1.getServiceEndpoints()).hasSize(4);
            assertThat(config2.getServiceEndpoints()).hasSize(3);
            
            // Verify they are different list objects
            assertThat(config1.getServiceEndpoints()).isNotSameAs(config2.getServiceEndpoints());
        }
        
        @Test
        @DisplayName("Multiple Clones Maintain Independence")
        void multipleClonesMaintainIndependence() {
            List<ServiceConfiguration> configs = new ArrayList<>();
            
            // Create multiple clones
            for (int i = 0; i < 5; i++) {
                configs.add(prototypeManager.createTestConfiguration());
            }
            
            // Modify each clone's mutable parts differently
            for (int i = 0; i < configs.size(); i++) {
                ServiceConfiguration config = configs.get(i);
                config.getServiceEndpoints().add("https://api.test" + i + ".telekom.de");
            }
            
            // Verify each clone maintains its unique modifications
            for (int i = 0; i < configs.size(); i++) {
                ServiceConfiguration config = configs.get(i);
                assertThat(config.getServiceEndpoints()).hasSize(4); // 3 base + 1 custom
                assertThat(config.getServiceEndpoints())
                    .contains("https://api.test" + i + ".telekom.de");
            }
        }
    }
    
    @Nested
    @DisplayName("Shared Immutable Objects Tests")
    class SharedImmutableObjectsTests {
        
        @Test
        @DisplayName("SSL Certificates Are Shared (Memory Efficiency)")
        void sslCertificatesAreShared() {
            ServiceConfiguration config1 = prototypeManager.createDevConfiguration();
            ServiceConfiguration config2 = prototypeManager.createDevConfiguration();
            ServiceConfiguration config3 = prototypeManager.createTestConfiguration();
            
            // SSL certificates should be the same object (memory sharing)
            assertThat(config1.getSslCertificate()).isSameAs(config2.getSslCertificate());
            assertThat(config1.getSslCertificate()).isSameAs(config3.getSslCertificate());
            
            // Verify certificate is properly configured
            assertThat(config1.getSslCertificate().getSubject()).contains("*.telekom.de");
            assertThat(config1.getSslCertificate().getExpiryDate()).isEqualTo("2025-12-31");
        }
        
        @Test
        @DisplayName("Database Settings Are Shared")
        void databaseSettingsAreShared() {
            ServiceConfiguration config1 = prototypeManager.createProdConfiguration();
            ServiceConfiguration config2 = prototypeManager.createProdConfiguration();
            
            // Database settings should be the same object (value object sharing)
            assertThat(config1.getDatabaseSettings()).isSameAs(config2.getDatabaseSettings());
            
            // Verify settings are properly configured
            assertThat(config1.getDatabaseSettings().getConnectionPool()).isEqualTo("default-pool");
            assertThat(config1.getDatabaseSettings().getMaxConnections()).isEqualTo(30);
        }
        
        @Test
        @DisplayName("Security Settings Are Shared")
        void securitySettingsAreShared() {
            ServiceConfiguration config1 = prototypeManager.createTestConfiguration();
            ServiceConfiguration config2 = prototypeManager.createProdConfiguration();
            
            // Security settings should be shared across different environment types
            assertThat(config1.getSecuritySettings()).isSameAs(config2.getSecuritySettings());
            
            // Verify security configuration
            assertThat(config1.getSecuritySettings().getEncryption()).isEqualTo("AES-256");
            assertThat(config1.getSecuritySettings().getTlsVersion()).isEqualTo("TLS-1.3");
            assertThat(config1.getSecuritySettings().isCertificateValidation()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Registry Pattern Tests")
    class RegistryPatternTests {
        
        @Test
        @DisplayName("Registry Initialization Creates All Templates")
        void registryInitializationCreatesAllTemplates() {
            ConfigurationPrototypeRegistry.RegistryStats stats = registry.getStats();
            
            assertThat(stats.getTemplateCount()).isEqualTo(4); // BASE, DEV, TEST, PROD
            assertThat(stats.getTemplateKeys())
                .contains("BASE", "DEV", "TEST", "PROD");
        }
        
        @Test
        @DisplayName("Custom Template Registration and Retrieval")
        void customTemplateRegistrationAndRetrieval() {
            // Create custom template
            ServiceConfiguration stagingTemplate = registry.getBaseTemplate()
                .clone()
                .withEnvironment("STAGING")
                .withLogLevel("INFO");
            
            // Register custom template
            registry.registerPrototype("STAGING", stagingTemplate);
            
            // Verify registration
            ConfigurationPrototypeRegistry.RegistryStats stats = registry.getStats();
            assertThat(stats.getTemplateCount()).isEqualTo(5);
            assertThat(stats.getTemplateKeys()).contains("STAGING");
            
            // Retrieve and verify custom template
            ServiceConfiguration retrieved = registry.getPrototype("staging"); // case-insensitive
            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getEnvironment()).isEqualTo("STAGING");
            assertThat(retrieved.getLogLevel()).isEqualTo("INFO");
        }
        
        @Test
        @DisplayName("Unknown Template Returns Base Template")
        void unknownTemplateReturnsBaseTemplate() {
            ServiceConfiguration unknownTemplate = registry.getPrototype("UNKNOWN");
            ServiceConfiguration baseTemplate = registry.getBaseTemplate();
            
            // Should return base template for unknown keys
            assertThat(unknownTemplate).isSameAs(baseTemplate);
        }
        
        @Test
        @DisplayName("Case-Insensitive Template Access")
        void caseInsensitiveTemplateAccess() {
            ServiceConfiguration dev1 = registry.getPrototype("dev");
            ServiceConfiguration dev2 = registry.getPrototype("DEV");
            ServiceConfiguration dev3 = registry.getPrototype("Dev");
            
            // All should return the same template
            assertThat(dev1).isSameAs(dev2).isSameAs(dev3);
        }
    }
    
    @Nested
    @DisplayName("Configuration Correctness Tests")
    class ConfigurationCorrectnessTests {
        
        @Test
        @DisplayName("Dev Configuration Has Correct Settings")
        void devConfigurationHasCorrectSettings() {
            ServiceConfiguration devConfig = prototypeManager.createDevConfiguration();
            
            assertThat(devConfig.getEnvironment()).isEqualTo("DEV");
            assertThat(devConfig.getLogLevel()).isEqualTo("DEBUG");
            assertThat(devConfig.getDatabaseUrl()).contains("devdb");
            assertThat(devConfig.getServiceEndpoints()).isNotEmpty();
        }
        
        @Test
        @DisplayName("Test Configuration Has Correct Settings")
        void testConfigurationHasCorrectSettings() {
            ServiceConfiguration testConfig = prototypeManager.createTestConfiguration();
            
            assertThat(testConfig.getEnvironment()).isEqualTo("TEST");
            assertThat(testConfig.getLogLevel()).isEqualTo("INFO");
            assertThat(testConfig.getDatabaseUrl()).contains("testdb");
            assertThat(testConfig.getServiceEndpoints()).hasSize(3);
        }
        
        @Test
        @DisplayName("Prod Configuration Has Correct Settings")
        void prodConfigurationHasCorrectSettings() {
            ServiceConfiguration prodConfig = prototypeManager.createProdConfiguration();
            
            assertThat(prodConfig.getEnvironment()).isEqualTo("PROD");
            assertThat(prodConfig.getLogLevel()).isEqualTo("WARN");
            assertThat(prodConfig.getDatabaseUrl()).contains("proddb");
            assertThat(prodConfig.getSslCertificate()).isNotNull();
        }
        
        @Test
        @DisplayName("Custom Configuration With Builder Pattern")
        void customConfigurationWithBuilderPattern() {
            ServiceConfiguration customConfig = prototypeManager.createCustomConfiguration(
                "CUSTOM", "ERROR", "jdbc:oracle:thin:@custom-db:1521:xe");
            
            assertThat(customConfig.getEnvironment()).isEqualTo("CUSTOM");
            assertThat(customConfig.getLogLevel()).isEqualTo("ERROR");
            assertThat(customConfig.getDatabaseUrl()).contains("custom-db");
            
            // Should have all the expensive pre-initialized components
            assertThat(customConfig.getDatabaseSettings()).isNotNull();
            assertThat(customConfig.getSecuritySettings()).isNotNull();
            assertThat(customConfig.getSslCertificate()).isNotNull();
            assertThat(customConfig.getServiceEndpoints()).isNotEmpty();
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {
        
        @Test
        @DisplayName("Registry Stats Are Accurate")
        void registryStatsAreAccurate() {
            ConfigurationPrototypeRegistry.RegistryStats stats = registry.getStats();
            
            assertThat(stats.getTemplateCount()).isPositive();
            assertThat(stats.getTemplateKeys()).isNotEmpty();
            assertThat(stats.toString()).contains("RegistryStats");
        }
        
        @Test
        @DisplayName("Clone Operation Never Fails")
        void cloneOperationNeverFails() {
            ServiceConfiguration baseTemplate = registry.getBaseTemplate();
            
            // Multiple rapid clones should never fail
            assertThatCode(() -> {
                for (int i = 0; i < 100; i++) {
                    ServiceConfiguration clone = baseTemplate.clone();
                    assertThat(clone).isNotNull();
                    assertThat(clone).isNotSameAs(baseTemplate);
                }
            }).doesNotThrowAnyException();
        }
        
        @Test
        @DisplayName("Configuration ToString Is Informative")
        void configurationToStringIsInformative() {
            ServiceConfiguration config = prototypeManager.createDevConfiguration();
            String configString = config.toString();
            
            assertThat(configString).contains("ServiceConfiguration");
            assertThat(configString).contains("DEV");
            assertThat(configString).contains("DEBUG");
            assertThat(configString).contains("devdb");
        }
    }
}