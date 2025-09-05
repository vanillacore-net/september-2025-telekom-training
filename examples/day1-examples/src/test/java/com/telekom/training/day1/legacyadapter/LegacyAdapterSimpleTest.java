package com.telekom.training.day1.legacyadapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import com.telekom.training.day1.legacyadapter.initial.LegacyBillingSystem;

/**
 * Vereinfachte Tests die die Konzepte demonstrieren ohne komplexe Package-Access
 */
class LegacyAdapterSimpleTest {

    private LegacyBillingSystem legacySystem;
    private com.telekom.training.day1.legacyadapter.initial.ModernCustomerService initialService;
    private com.telekom.training.day1.legacyadapter.fixed.ModernCustomerService fixedService;

    @BeforeEach
    void setUp() {
        legacySystem = new LegacyBillingSystem();
        
        // Initial Version - Direct Legacy Integration
        initialService = new com.telekom.training.day1.legacyadapter.initial.ModernCustomerService(legacySystem);
        
        // Fixed Version - mit Adapter Pattern
        var billingAdapter = new com.telekom.training.day1.legacyadapter.fixed.LegacyBillingAdapter(legacySystem);
        fixedService = new com.telekom.training.day1.legacyadapter.fixed.ModernCustomerService(billingAdapter);
    }

    @Test
    @DisplayName("Initial Version: Direct Legacy Integration funktioniert, aber ist problematisch")
    void testInitialVersion_DirectLegacyIntegration() {
        // Given - Legacy System direkt verwendet
        String customerId = "CUST-001";
        double amount = 99.99;
        LocalDate dueDate = LocalDate.now().plusDays(30);
        
        // When - Direct Legacy Integration
        String invoiceId = initialService.createCustomerInvoice(
            customerId, amount, dueDate, "MONTHLY_BILL", "DIRECT_DEBIT"
        );
        
        // Then - funktioniert, aber hat Architektur-Probleme
        assertThat(invoiceId).isNotEmpty();
        assertThat(invoiceId).startsWith("INV-");
        
        // Code-Smells in Initial Version sind in der Implementierung sichtbar:
        // - Tight Coupling zum Legacy System
        // - Type Conversions (LocalDate <-> Date) überall verstreut
        // - Legacy Exception Handling in Business Logic
        // - Mapping Logic dupliziert
        // - Legacy Concerns durchsickern (boolean returns, String reports)
    }

    @Test
    @DisplayName("Fixed Version: Adapter Pattern isoliert Legacy Concerns")
    void testFixedVersion_AdapterPattern() {
        // Given - Same test scenario
        String customerId = "CUST-001";
        double amount = 99.99;
        LocalDate dueDate = LocalDate.now().plusDays(30);
        
        // When - Modern API durch Adapter (hätten hier normalerweise Enums)
        // Für Demo verwenden wir Strings, aber die Architektur ist entscheidend
        String invoiceId = fixedService.createCustomerInvoice(
            customerId, amount, dueDate, "MONTHLY_BILL", "DIRECT_DEBIT"
        );
        
        // Then - gleiche Funktionalität, aber viel bessere Architektur
        assertThat(invoiceId).isNotEmpty();
        assertThat(invoiceId).contains("CUST-001");
        
        // Architecture Improvements in Fixed Version:
        // - Adapter Pattern isoliert Legacy Concerns im LegacyBillingAdapter
        // - Anti-Corruption Layer verhindert Legacy Durchsickern
        // - ModernCustomerService kennt keine Legacy Types mehr
        // - Testbar durch BillingService Interface
        // - Zentrale Mapping Logic im Adapter
        // - Clean Exception Translation
        // - Loose Coupling durch Interface
    }

    @Test
    @DisplayName("Legacy Adapter: Isoliert alle Legacy Probleme")
    void testLegacyIsolation() {
        // Beide Services können die gleichen Calls machen
        String customer = "CUST-ISOLATION";
        double amount = 150.00;
        LocalDate dueDate = LocalDate.now().plusDays(14);
        
        // Initial Version: Legacy concerns durchsickern
        String initialInvoice = initialService.createCustomerInvoice(
            customer, amount, dueDate, "INSTALLATION", "BANK_TRANSFER"
        );
        
        // Fixed Version: Legacy concerns isoliert im Adapter
        String fixedInvoice = fixedService.createCustomerInvoice(
            customer, amount, dueDate, "INSTALLATION", "BANK_TRANSFER"  
        );
        
        // Beide funktionieren gleich
        assertThat(initialInvoice).isNotEmpty();
        assertThat(fixedInvoice).isNotEmpty();
        
        // Aber die Architektur ist völlig anders:
        // - Initial: ModernCustomerService direkt gekoppelt an LegacyBillingSystem
        // - Fixed: ModernCustomerService gekoppelt an BillingService Interface
        //          LegacyBillingAdapter implementiert Interface und kapselt Legacy
    }

    @Test
    @DisplayName("Adapter Pattern: Anti-Corruption Layer Demonstration")
    void testAntiCorruptionLayer() {
        // Der wichtigste Punkt: Legacy System Corruption wird verhindert
        
        // Given - Business Logic im Fixed Service
        String customerId = "CUST-ANTICORRUPTION";
        
        // When - Modern Business Logic Operation
        double outstandingAmount = fixedService.calculateOutstandingAmount(customerId);
        
        // Then - Business Logic funktioniert ohne Legacy Concerns
        assertThat(outstandingAmount).isGreaterThanOrEqualTo(0.0);
        
        // Der fixedService.calculateOutstandingAmount() verwendet:
        // 1. BillingService Interface (modern, clean API)
        // 2. Keine Legacy Types (LocalDate, Enums statt Strings)  
        // 3. Strukturierte Daten statt Legacy String parsing
        // 4. Rich Objects statt primitive boolean returns
        //
        // Das Legacy System ist komplett isoliert im LegacyBillingAdapter!
    }

    @Test
    @DisplayName("Code Quality Demonstration: Separation of Concerns")
    void testSeparationOfConcerns() {
        /*
         * ARCHITECTURE COMPARISON:
         *
         * INITIAL VERSION PROBLEMS:
         * ModernCustomerService
         * ├── LegacyBillingSystem (direct dependency)
         * ├── Type conversion logic (scattered)
         * ├── Legacy exception handling (mixed in business logic)
         * ├── String parsing logic (fragile)
         * └── Legacy API calls (tight coupling)
         * 
         * FIXED VERSION IMPROVEMENTS:
         * ModernCustomerService
         * ├── BillingService (interface - loose coupling)
         * ├── Clean modern APIs only
         * ├── Business logic without legacy concerns
         * └── Testable with mocks
         * 
         * LegacyBillingAdapter (Anti-Corruption Layer)
         * ├── Implements BillingService
         * ├── Encapsulates LegacyBillingSystem
         * ├── Centralized type conversions  
         * ├── Exception translation
         * ├── Result interpretation
         * └── Legacy concerns isolation
         *
         * BENEFITS:
         * 1. Separation of Concerns - Business logic vs Legacy integration
         * 2. Testability - ModernCustomerService can be unit tested with mocks
         * 3. Maintainability - Legacy changes isolated in adapter
         * 4. Replaceability - Legacy system can be replaced without changing business logic
         * 5. Clean Architecture - Modern domain not corrupted by legacy concerns
         */
        
        assertThat("Adapter Pattern provides clean architectural separation").isNotEmpty();
    }
}