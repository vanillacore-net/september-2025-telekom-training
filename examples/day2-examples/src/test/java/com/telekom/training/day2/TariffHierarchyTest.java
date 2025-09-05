package com.telekom.training.day2;

import com.telekom.training.day2.tariffhierarchy.fixed.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test demonstrating Composite pattern for uniform handling of 
 * single tariffs and bundles
 */
class TariffHierarchyTest {

    @Test
    @DisplayName("Single tariffs work uniformly")
    void testSingleTariffs() {
        TariffComponent mobile = new SingleTariff("Mobile Premium", new BigDecimal("39.99"), "Mobilfunk");
        TariffComponent internet = new SingleTariff("DSL 250", new BigDecimal("49.99"), "Internet");
        
        // Uniform interface for single tariffs
        assertEquals(new BigDecimal("39.99"), mobile.getPrice());
        assertEquals(new BigDecimal("49.99"), internet.getPrice());
        
        // Both can be treated the same way
        assertNotNull(mobile.getName());
        assertNotNull(internet.getName());
    }

    @Test
    @DisplayName("Bundles contain components uniformly")
    void testBundles() {
        // Create individual tariffs
        TariffComponent mobile = new SingleTariff("Mobile Basic", new BigDecimal("29.99"), "Mobilfunk");
        TariffComponent internet = new SingleTariff("DSL 100", new BigDecimal("39.99"), "Internet");
        TariffComponent tv = new SingleTariff("TV Kompakt", new BigDecimal("19.99"), "TV");
        
        // Create bundle with discount
        TariffBundle familyBundle = new TariffBundle("Familie Komplett", new BigDecimal("10.00"));
        familyBundle.add(mobile);
        familyBundle.add(internet);
        familyBundle.add(tv);
        
        // Bundle calculates total price minus discount
        BigDecimal expected = new BigDecimal("29.99").add(new BigDecimal("39.99")).add(new BigDecimal("19.99")).subtract(new BigDecimal("10.00"));
        assertEquals(expected, familyBundle.getPrice());
        assertEquals("Familie Komplett", familyBundle.getName());
    }

    @Test
    @DisplayName("Nested bundles work recursively")
    void testNestedBundles() {
        // Individual services
        TariffComponent mobile1 = new SingleTariff("Mobile Line 1", new BigDecimal("29.99"), "Mobilfunk");
        TariffComponent mobile2 = new SingleTariff("Mobile Line 2", new BigDecimal("19.99"), "Mobilfunk");
        
        // Mobile bundle
        TariffBundle mobileBundle = new TariffBundle("Doppel Mobile", new BigDecimal("5.00"));
        mobileBundle.add(mobile1);
        mobileBundle.add(mobile2);
        
        // Internet services
        TariffComponent internet = new SingleTariff("Glasfaser 1000", new BigDecimal("59.99"), "Internet");
        TariffComponent tv = new SingleTariff("TV Premium", new BigDecimal("29.99"), "TV");
        
        // Top-level bundle containing other bundle
        TariffBundle masterBundle = new TariffBundle("Alles Inklusive", new BigDecimal("15.00"));
        masterBundle.add(mobileBundle);  // Adding a bundle to a bundle!
        masterBundle.add(internet);
        masterBundle.add(tv);
        
        // Recursive calculation:
        // Mobile bundle: (29.99 + 19.99) - 5.00 = 44.98
        // Master bundle: 44.98 + 59.99 + 29.99 - 15.00 = 119.96
        assertEquals(new BigDecimal("119.96"), masterBundle.getPrice());
    }

    @Test
    @DisplayName("Uniform handling without type checking")
    void testUniformHandling() {
        // Create mixed collection - no Object casting needed!
        TariffComponent single = new SingleTariff("Einzeltarif", new BigDecimal("29.99"), "Test");
        
        TariffBundle bundle = new TariffBundle("Bundle", new BigDecimal("5.00"));
        bundle.add(new SingleTariff("Sub-Tariff", new BigDecimal("19.99"), "Test"));
        
        // Process them uniformly - no if-else or instanceof!
        TariffComponent[] tariffs = {single, bundle};
        
        BigDecimal total = BigDecimal.ZERO;
        for (TariffComponent tariff : tariffs) {
            // Same method call for both singles and bundles!
            total = total.add(tariff.getPrice());
        }
        
        // single: 29.99, bundle: (19.99 - 5.00) = 14.99, total = 44.98
        assertEquals(new BigDecimal("44.98"), total);
    }

    @Test
    @DisplayName("Tree structure traversal")
    void testTreeStructure() {
        // Build complex hierarchy
        TariffComponent mobile = new SingleTariff("Mobile", new BigDecimal("30.00"), "Mobilfunk");
        TariffComponent internet = new SingleTariff("Internet", new BigDecimal("40.00"), "Internet");
        
        TariffBundle subBundle = new TariffBundle("Basis Paket", new BigDecimal("5.00"));
        subBundle.add(mobile);
        subBundle.add(internet);
        
        TariffComponent tv = new SingleTariff("TV", new BigDecimal("25.00"), "TV");
        
        TariffBundle mainBundle = new TariffBundle("Komplett Paket", new BigDecimal("10.00"));
        mainBundle.add(subBundle);
        mainBundle.add(tv);
        
        // Verify structure can be traversed
        assertEquals(2, mainBundle.getComponents().size()); // subBundle + tv = 2 direct children
        
        // Total calculation works recursively
        // subBundle: (30 + 40) - 5 = 65
        // mainBundle: 65 + 25 - 10 = 80
        assertEquals(new BigDecimal("80.00"), mainBundle.getPrice());
    }
}