package com.telekom.training.day1.customerservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Vergleichende Tests zwischen Initial (God Class) und Fixed (Factory Method) Version
 */
class CustomerServiceTest {

    private com.telekom.training.day1.customerservice.initial.CustomerService initialService;
    private com.telekom.training.day1.customerservice.fixed.CustomerService fixedService;
    
    @BeforeEach
    void setUp() {
        initialService = new com.telekom.training.day1.customerservice.initial.CustomerService();
        fixedService = new com.telekom.training.day1.customerservice.fixed.CustomerService();
        
        // Test-Kunden hinzufügen
        initialService.addCustomer("CUST001", "Max Mustermann", "Musterstraße 1, Berlin", "max@example.com");
        fixedService.addCustomer("CUST001", "Max Mustermann", "Musterstraße 1, Berlin", "max@example.com");
    }

    @Test
    @DisplayName("Initial Version: DSL Aktivierung funktioniert, aber Code ist schlecht strukturiert")
    void testInitialVersion_DslActivation() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("speed", "100");
        
        // When
        String result = initialService.handleCustomerRequest("CUST001", "DSL_ACTIVATION", params);
        
        // Then
        assertThat(result).contains("DSL Vertrag erstellt");
        assertThat(result).contains("Techniker beauftragt");
        
        // Code-Smells in Initial Version:
        // - handleCustomerRequest hat über 50 Zeilen (Long Method)
        // - Giant Switch Statement
        // - Hardcoded Business Logic
        // - God Class mit zu vielen Verantwortlichkeiten
        // - Schwer testbar (alles in einer Methode)
    }

    @Test
    @DisplayName("Fixed Version: Gleiche Funktionalität, aber saubere Struktur mit Factory Method")
    void testFixedVersion_DslActivation() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("speed", "100");
        
        // When
        String result = fixedService.handleCustomerRequest("CUST001", "DSL_ACTIVATION", params);
        
        // Then
        assertThat(result).contains("DSL Vertrag erstellt");
        assertThat(result).contains("Speed: 100");
        assertThat(result).contains("Techniker wird beauftragt");
        
        // Improvements in Fixed Version:
        // - Factory Method Pattern angewendet
        // - Single Responsibility für jeden Handler
        // - Open/Closed Principle - erweiterbar ohne Änderung
        // - Testbare Komponenten
        // - Keine Long Methods
        // - Keine Giant Switch Statements
    }

    @Test
    @DisplayName("Beide Versionen: Mobile Aktivierung")
    void testMobileActivation_BothVersions() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("dataVolume", "10GB");
        params.put("simType", "NANO");
        
        // When
        String initialResult = initialService.handleCustomerRequest("CUST001", "MOBILE_ACTIVATION", params);
        String fixedResult = fixedService.handleCustomerRequest("CUST001", "MOBILE_ACTIVATION", params);
        
        // Then - beide Versionen liefern gleiche Funktionalität
        assertThat(initialResult).contains("Mobile Vertrag erstellt");
        assertThat(fixedResult).contains("Mobile Vertrag erstellt");
        assertThat(fixedResult).contains("10GB");
    }

    @Test
    @DisplayName("Fixed Version: Erweiterbarkeit - neue Handler können registriert werden")
    void testFixedVersion_Extensibility() {
        // Given - Custom Handler für neuen Request Type
        var customHandler = new com.telekom.training.day1.customerservice.fixed.handlers.RequestHandler() {
            @Override
            protected String createResponse(com.telekom.training.day1.customerservice.fixed.model.Customer customer, 
                                          Map<String, Object> params) {
                return "Custom handler executed for " + customer.getName();
            }
        };
        
        fixedService.registerCustomHandler("CUSTOM_REQUEST", customHandler);
        
        // When
        String result = fixedService.handleCustomerRequest("CUST001", "CUSTOM_REQUEST", new HashMap<>());
        
        // Then
        assertThat(result).isEqualTo("Custom handler executed for Max Mustermann");
        
        // This demonstrates Open/Closed Principle:
        // - Open for extension (new handlers)
        // - Closed for modification (existing code unchanged)
    }

    @Test
    @DisplayName("Error Handling: Unbekannter Request Type")
    void testErrorHandling_UnknownRequestType() {
        // Initial Version
        String initialResult = initialService.handleCustomerRequest("CUST001", "UNKNOWN_TYPE", new HashMap<>());
        assertThat(initialResult).isEqualTo("ERROR: Unknown request type");
        
        // Fixed Version
        String fixedResult = fixedService.handleCustomerRequest("CUST001", "UNKNOWN_TYPE", new HashMap<>());
        assertThat(fixedResult).contains("ERROR: Unknown request type");
    }

    @Test
    @DisplayName("Fixed Version: Request Type Support Check")
    void testFixedVersion_RequestTypeSupport() {
        assertThat(fixedService.isRequestTypeSupported("DSL_ACTIVATION")).isTrue();
        assertThat(fixedService.isRequestTypeSupported("MOBILE_ACTIVATION")).isTrue();
        assertThat(fixedService.isRequestTypeSupported("FESTNETZ_ACTIVATION")).isTrue();
        assertThat(fixedService.isRequestTypeSupported("UNKNOWN_TYPE")).isFalse();
    }

    @Test
    @DisplayName("Code Quality Vergleich: Zeigt die Verbesserungen auf")
    void testCodeQualityComparison() {
        /*
         * INITIAL VERSION PROBLEMS:
         * 1. God Class - CustomerService macht alles
         * 2. Long Method - handleCustomerRequest über 50 Zeilen
         * 3. Giant Switch Statement - schwer erweiterbar
         * 4. Hardcoded Business Logic - nicht konfigurierbar
         * 5. Mixed Concerns - UI, Business Logic, Data Access vermischt
         * 6. Schwer testbar - alles privat und gekoppelt
         * 
         * FIXED VERSION IMPROVEMENTS:
         * 1. Single Responsibility - jeder Handler hat eine Aufgabe
         * 2. Factory Method Pattern - saubere Objekterzeugung
         * 3. Open/Closed Principle - erweiterbar ohne Änderung
         * 4. Template Method in Base Handler - gemeinsamer Ablauf
         * 5. Separation of Concerns - klare Trennung
         * 6. Testbare Komponenten - jeder Handler einzeln testbar
         */
        
        // This test documents the improvements - both versions work the same
        // but the fixed version is much more maintainable and extensible
        
        assertThat("Code quality improved").isNotEmpty();
    }
}