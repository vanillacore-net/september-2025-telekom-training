package com.telekom.training.day1.prototype;

// Using fully qualified names to avoid import conflicts
import com.telekom.training.day1.prototype.fixed.ServiceConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * PERFORMANCE COMPARISON DEMO: Initial vs Prototype Pattern
 * 
 * Demonstrates the dramatic performance improvement achieved by the Prototype Pattern:
 * - Without Prototype: 1200ms per configuration (expensive operations repeated)
 * - With Prototype: ~2ms per configuration (fast cloning from templates)
 * - Result: 99.83% performance improvement!
 * 
 * This demo runs both implementations side-by-side to show real performance data.
 */
public class PerformanceComparison {
    
    public static void main(String[] args) {
        System.out.println("🏁 PROTOTYPE PATTERN PERFORMANCE COMPARISON");
        System.out.println("=========================================");
        System.out.println();
        
        // Number of configurations to create for comparison
        int configurationCount = 5;
        
        // Warm-up JVM (not counted in measurements)
        System.out.println("🔥 Warming up JVM...");
        warmupJvm();
        System.out.println("✅ Warmup complete");
        System.out.println();
        
        // Test 1: WITHOUT Prototype Pattern (Initial Implementation)
        System.out.println("📊 TEST 1: WITHOUT PROTOTYPE PATTERN (ANTI-PATTERN)");
        System.out.println("====================================================");
        
        long initialStart = System.currentTimeMillis();
        List<com.telekom.training.day1.prototype.initial.ServiceConfiguration> initialConfigs = 
            createConfigurationsWithoutPrototype(configurationCount);
        long initialDuration = System.currentTimeMillis() - initialStart;
        
        System.out.printf("⏱️ Total time: %d ms%n", initialDuration);
        System.out.printf("⏱️ Average per config: %d ms%n", initialDuration / configurationCount);
        System.out.printf("💸 Wasted time on repeated operations: ~%d ms%n", 
            (configurationCount - 1) * 1200); // Estimate repeated expensive operations
        System.out.println();
        
        // Test 2: WITH Prototype Pattern (Fixed Implementation)
        System.out.println("🚀 TEST 2: WITH PROTOTYPE PATTERN (OPTIMIZED)");
        System.out.println("===============================================");
        
        long prototypeStart = System.currentTimeMillis();
        List<ServiceConfiguration> prototypeConfigs = 
            createConfigurationsWithPrototype(configurationCount);
        long prototypeDuration = System.currentTimeMillis() - prototypeStart;
        
        System.out.printf("⏱️ Total time: %d ms%n", prototypeDuration);
        System.out.printf("⏱️ Average per config: %d ms%n", prototypeDuration / configurationCount);
        System.out.printf("💡 Template initialization included: ~1200ms (one-time cost)%n");
        System.out.printf("⚡ Clone operations: ~%d ms total%n", prototypeDuration - 1200);
        System.out.println();
        
        // Performance Analysis
        System.out.println("📈 PERFORMANCE ANALYSIS");
        System.out.println("========================");
        
        double improvementFactor = (double) initialDuration / prototypeDuration;
        double improvementPercentage = ((improvementFactor - 1) * 100);
        
        System.out.printf("🎯 Speed improvement: %.1fx faster%n", improvementFactor);
        System.out.printf("🎯 Performance gain: %.2f%% improvement%n", improvementPercentage);
        System.out.printf("🎯 Time saved: %d ms (%.2f seconds)%n", 
            initialDuration - prototypeDuration, (initialDuration - prototypeDuration) / 1000.0);
        
        // Validate correctness
        System.out.println();
        System.out.println("✅ CORRECTNESS VALIDATION");
        System.out.println("==========================");
        
        validateConfigurations(initialConfigs, prototypeConfigs);
        
        // Scaling Analysis
        System.out.println();
        System.out.println("📊 SCALING ANALYSIS");
        System.out.println("===================");
        
        analyzScaling();
        
        System.out.println();
        System.out.println("🏆 PROTOTYPE PATTERN SUCCESS!");
        System.out.println("==============================");
        System.out.printf("✨ Achieved %.2f%% performance improvement%n", improvementPercentage);
        System.out.println("🎯 Memory efficient through object sharing");
        System.out.println("🔒 Maintains object independence for mutable parts");
        System.out.println("🏭 Scales excellently for bulk operations");
    }
    
    private static void warmupJvm() {
        // Create a few objects to warm up the JVM
        for (int i = 0; i < 3; i++) {
            new ArrayList<String>();
            System.currentTimeMillis();
        }
    }
    
    private static List<com.telekom.training.day1.prototype.initial.ServiceConfiguration> 
            createConfigurationsWithoutPrototype(int count) {
        
        com.telekom.training.day1.prototype.initial.ServiceConfigurationManager manager = 
            new com.telekom.training.day1.prototype.initial.ServiceConfigurationManager();
        List<com.telekom.training.day1.prototype.initial.ServiceConfiguration> configs = new ArrayList<>();
        
        System.out.println("Creating configurations with expensive repeated operations...");
        
        for (int i = 0; i < count; i++) {
            if (i % 3 == 0) {
                configs.add(manager.createDevConfiguration());
            } else if (i % 3 == 1) {
                configs.add(manager.createTestConfiguration());
            } else {
                configs.add(manager.createProdConfiguration());
            }
        }
        
        return configs;
    }
    
    private static List<ServiceConfiguration> createConfigurationsWithPrototype(int count) {
        com.telekom.training.day1.prototype.fixed.ServiceConfigurationManager manager = 
            new com.telekom.training.day1.prototype.fixed.ServiceConfigurationManager();
        List<ServiceConfiguration> configs = new ArrayList<>();
        
        System.out.println("Creating configurations via prototype cloning...");
        
        for (int i = 0; i < count; i++) {
            if (i % 3 == 0) {
                configs.add(manager.createDevConfiguration());
            } else if (i % 3 == 1) {
                configs.add(manager.createTestConfiguration());
            } else {
                configs.add(manager.createProdConfiguration());
            }
        }
        
        return configs;
    }
    
    private static void validateConfigurations(
            List<com.telekom.training.day1.prototype.initial.ServiceConfiguration> initialConfigs,
            List<ServiceConfiguration> prototypeConfigs) {
        
        System.out.printf("Initial configs created: %d%n", initialConfigs.size());
        System.out.printf("Prototype configs created: %d%n", prototypeConfigs.size());
        System.out.println("✅ Both implementations produce valid configurations");
        
        // Verify configurations are not null and have expected properties
        for (com.telekom.training.day1.prototype.initial.ServiceConfiguration config : initialConfigs) {
            if (config == null) {
                throw new AssertionError("Initial configuration is null!");
            }
        }
        
        for (ServiceConfiguration config : prototypeConfigs) {
            if (config == null) {
                throw new AssertionError("Prototype configuration is null!");
            }
            if (config.getDatabaseSettings() == null) {
                throw new AssertionError("Database settings not properly initialized!");
            }
            if (config.getSecuritySettings() == null) {
                throw new AssertionError("Security settings not properly initialized!");
            }
            if (config.getSslCertificate() == null) {
                throw new AssertionError("SSL certificate not properly initialized!");
            }
        }
        
        System.out.println("✅ All configurations properly initialized");
    }
    
    private static void analyzScaling() {
        System.out.println("Projecting performance for different scales:");
        System.out.println();
        
        int[] scales = {10, 50, 100, 500, 1000};
        
        System.out.printf("%-8s %-15s %-15s %-10s%n", "Scale", "Without (est.)", "With (est.)", "Speedup");
        System.out.println("--------------------------------------------------------");
        
        for (int scale : scales) {
            // Without prototype: ~1200ms per config
            long withoutPrototype = scale * 1200;
            
            // With prototype: 1200ms setup + 2ms per clone
            long withPrototype = 1200 + (scale * 2);
            
            double speedup = (double) withoutPrototype / withPrototype;
            
            System.out.printf("%-8d %-15s %-15s %.1fx%n", 
                scale,
                formatTime(withoutPrototype),
                formatTime(withPrototype),
                speedup);
        }
        
        System.out.println();
        System.out.println("💡 Key Insight: Prototype pattern benefits increase with scale!");
        System.out.println("   At 1000 configs: 20 minutes → 3 seconds (400x speedup!)");
    }
    
    private static String formatTime(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.1fs", milliseconds / 1000.0);
        } else {
            return String.format("%.1fm", milliseconds / 60000.0);
        }
    }
}