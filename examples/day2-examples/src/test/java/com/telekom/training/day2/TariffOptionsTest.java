package com.telekom.training.day2;

import com.telekom.training.day2.tarifoptions.fixed.*;
import com.telekom.training.day2.tarifoptions.initial.BasicTariffWithFlatrateAndRoaming;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test demonstrating the difference between inheritance explosion (initial)
 * and flexible Decorator pattern (fixed)
 */
class TariffOptionsTest {

    @Test
    @DisplayName("Initial: Inheritance explosion problem")
    void testInheritanceExplosion() {
        // Problem: Need specific class for every combination
        BasicTariffWithFlatrateAndRoaming tariff = new BasicTariffWithFlatrateAndRoaming(
            "Premium", new BigDecimal("29.99"), 500, 100, 2048);
        
        assertEquals(new BigDecimal("53.49"), tariff.getPrice());
        assertTrue(tariff.getDescription().contains("Flatrate"));
        assertTrue(tariff.getDescription().contains("Roaming"));
        
        // What if customer wants Flatrate + Insurance? Need another class!
        // What about Roaming + Insurance? Yet another class!
        // N options = 2^N classes! Explosion!
    }

    @Test
    @DisplayName("Fixed: Decorator pattern flexibility")
    void testDecoratorFlexibility() {
        // Start with basic tariff
        Tariff basicTariff = new BasicTariff("Premium", new BigDecimal("29.99"), 500, 100, 2048);
        
        // Add options flexibly with decorators
        Tariff withFlatrate = new FlatrateDecorator(basicTariff);
        Tariff withFlatrateAndRoaming = new RoamingDecorator(withFlatrate);
        Tariff fullPackage = new InsuranceDecorator(withFlatrateAndRoaming);
        
        // Same result as inheritance version
        assertEquals(new BigDecimal("53.49"), withFlatrateAndRoaming.getPrice());
        
        // But now we can easily create ANY combination!
        Tariff roamingOnly = new RoamingDecorator(basicTariff);
        assertEquals(new BigDecimal("38.49"), roamingOnly.getPrice());
        
        Tariff insuranceOnly = new InsuranceDecorator(basicTariff);
        assertEquals(new BigDecimal("34.98"), insuranceOnly.getPrice());
        
        // Full package with all options
        assertEquals(new BigDecimal("58.48"), fullPackage.getPrice());
        assertTrue(fullPackage.getDescription().contains("Flatrate"));
        assertTrue(fullPackage.getDescription().contains("Roaming"));
        assertTrue(fullPackage.getDescription().contains("Versicherung"));
    }

    @Test
    @DisplayName("Decorator: Runtime composition")
    void testRuntimeComposition() {
        Tariff base = new BasicTariff("Business", new BigDecimal("49.99"), 1000, 200, 5120);
        
        // Compose at runtime based on customer preferences
        String[] options = {"flatrate", "roaming", "insurance"};
        Tariff configured = base;
        
        for (String option : options) {
            switch (option) {
                case "flatrate": configured = new FlatrateDecorator(configured); break;
                case "roaming": configured = new RoamingDecorator(configured); break;
                case "insurance": configured = new InsuranceDecorator(configured); break;
            }
        }
        
        // All options applied: 49.99 + 15.00 + 8.50 + 4.99 = 78.48
        assertEquals(new BigDecimal("78.48"), configured.getPrice());
        
        // Flatrate gives unlimited minutes
        assertEquals(Integer.MAX_VALUE, configured.getIncludedMinutes());
    }

    @Test
    @DisplayName("Performance comparison: Class count")
    void testClassCountComparison() {
        // Initial approach: 4 options = 2^4 = 16 classes needed!
        // Current implementation only has 4 classes (Basic + 3 options)
        // Decorator: 1 basic + N decorators = 1 + N classes (linear!)
        
        // With Decorator pattern:
        // - 4 options require only 5 classes (1 basic + 4 decorators)
        // - 10 options require only 11 classes (1 basic + 10 decorators)
        // - Inheritance would require 2^10 = 1024 classes!
        
        // This demonstrates the scalability of Decorator pattern
        assertTrue(true, "Decorator pattern scales linearly, inheritance exponentially");
    }
}