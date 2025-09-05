package com.telekom.training.day1.contractbuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

/**
 * Vergleichende Tests zwischen Initial (Long Constructor) und Fixed (Builder Pattern) Version
 */
class ContractBuilderTest {

    @Test
    @DisplayName("Initial Version: Long Constructor funktioniert, aber sehr unlesbar")
    void testInitialVersion_LongConstructor() {
        // Given - schwer lesbare Parameter-Liste
        var initialContract = new com.telekom.training.day1.contractbuilder.initial.TelekomContract(
            "CTR-001",                    // contractId
            "CUST-001",                   // customerId  
            "INTERNET_PHONE_TV",          // contractType
            "100",                        // internetSpeed
            "DSL",                        // internetTechnology
            "+49301234567",               // phoneNumber
            "PREMIUM",                    // phoneFeatures
            "BASIC_TV",                   // tvPackage
            "STANDARD_BOX",               // tvEquipment
            39.95,                        // internetPrice
            19.95,                        // phonePrice
            24.95,                        // tvPrice
            49.99,                        // installationFee
            10.0,                         // discountPercentage
            24,                           // contractDurationMonths
            LocalDate.now().plusDays(7),  // startDate
            "MONTHLY",                    // billingCycle
            "Musterstraße 1, Berlin",     // installationAddress
            "Musterstraße 1, Berlin",     // billingAddress
            "tech@example.com",           // technicalContact
            "Hans Müller",                // salesPerson
            true,                         // autoRenewal
            true,                         // paperlessBilling
            "DIRECT_DEBIT",               // paymentMethod
            "DE123456789",                // bankAccount
            "VIP",                        // priorityLevel
            "SUMMER2024"                  // promotionCode
        );
        
        // Then - funktioniert, aber...
        assertThat(initialContract.getContractId()).isEqualTo("CTR-001");
        assertThat(initialContract.calculateTotalMonthlyPrice()).isEqualTo((39.95 + 19.95 + 24.95) * 0.9);
        
        // Code-Smells in Initial Version:
        // - 27 Parameter in constructor - unmöglich zu merken
        // - Parameter-Reihenfolge ist kritisch und fehleranfällig
        // - Keine Parameter-Namen bei Aufruf - was ist was?
        // - Telescoping Constructor Pattern - viele Überladungen
        // - Schwer erweiterbar - neue Parameter = Breaking Change
        // - Null-Values für optionale Parameter
        // - Validation am Ende - schwer zu debuggen
    }

    @Test
    @DisplayName("Fixed Version: Builder Pattern ist viel lesbarer und flexibler")
    void testFixedVersion_BuilderPattern() {
        // Given - Self-documenting Code mit Fluent Interface
        var fixedContract = com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
            .withContractId("CTR-001")
            .forCustomer("CUST-001")
            .withContractType("INTERNET_PHONE_TV")
            .startingOn(LocalDate.now().plusDays(7))
            .installedAt("Musterstraße 1, Berlin")
            .withInternetSpeed("100")
            .withInternetTechnology("DSL")
            .withInternetPrice(39.95)
            .withPhoneNumber("+49301234567")
            .withPhoneFeatures("PREMIUM")
            .withPhonePrice(19.95)
            .withTvPackage("BASIC_TV")
            .withTvEquipment("STANDARD_BOX")
            .withTvPrice(24.95)
            .withInstallationFee(49.99)
            .withDiscount(10.0)
            .forDuration(24)
            .withBillingCycle("MONTHLY")
            .withTechnicalContact("tech@example.com")
            .soldBy("Hans Müller")
            .withAutoRenewal(true)
            .withPaperlessBilling(true)
            .withPaymentMethod("DIRECT_DEBIT")
            .withBankAccount("DE123456789")
            .withPriority("VIP")
            .withPromotionCode("SUMMER2024")
            .build();
        
        // Then - gleiche Funktionalität, aber viel besser lesbar
        assertThat(fixedContract.getContractId()).isEqualTo("CTR-001");
        assertThat(fixedContract.calculateTotalMonthlyPrice()).isEqualTo((39.95 + 19.95 + 24.95) * 0.9);
        
        // Improvements in Fixed Version:
        // - Self-documenting method names
        // - Fluent Interface - sehr lesbar
        // - Flexible Parameter-Reihenfolge
        // - Optional Parameters mit sinnvollen Defaults
        // - Step-by-step Validation
        // - Immutable objects nach Erstellung
        // - Erweiterbar ohne Breaking Changes
    }

    @Test
    @DisplayName("Builder Pattern: Convenience Methods für häufige Use Cases")
    void testBuilderConvenienceMethods() {
        // Given - Convenience Method für Triple Play Contract
        var triplePlayContract = com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
            .withContractId("CTR-TRIPLE")
            .forCustomer("CUST-TRIPLE")
            .startingOn(LocalDate.now().plusDays(14))
            .installedAt("Beispielstraße 42, München")
            .asTriplePlayContract("250", "+49891234567", "PREMIUM_TV", 49.95, 24.95, 34.95)
            .asPremiumBundle()  // Sets VIP priority, premium features, auto-renewal, etc.
            .build();
        
        // Then
        assertThat(triplePlayContract.getContractType()).isEqualTo("INTERNET_PHONE_TV");
        assertThat(triplePlayContract.getInternetSpeed()).isEqualTo("250");
        assertThat(triplePlayContract.getPhoneNumber()).isEqualTo("+49891234567");
        assertThat(triplePlayContract.getTvPackage()).isEqualTo("PREMIUM_TV");
        assertThat(triplePlayContract.getPriorityLevel()).isEqualTo("VIP");
        assertThat(triplePlayContract.getPhoneFeatures()).isEqualTo("PREMIUM");
        assertThat(triplePlayContract.getDiscountPercentage()).isEqualTo(15.0);
        assertThat(triplePlayContract.isAutoRenewal()).isTrue();
    }

    @Test
    @DisplayName("Builder Pattern: Internet-Only Contract ist einfach zu erstellen")
    void testInternetOnlyContract() {
        // Given - Einfacher Internet-Only Contract
        var internetContract = com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
            .withContractId("CTR-INET")
            .forCustomer("CUST-INET")
            .startingOn(LocalDate.now().plusDays(3))
            .installedAt("Internetstraße 10, Köln")
            .asInternetOnlyContract("50", 29.95)
            .forDuration(12)
            .withPromotionCode("NEWCUSTOMER")
            .build();
        
        // Then
        assertThat(internetContract.getContractType()).isEqualTo("INTERNET_ONLY");
        assertThat(internetContract.getInternetSpeed()).isEqualTo("50");
        assertThat(internetContract.getInternetPrice()).isEqualTo(29.95);
        assertThat(internetContract.getPhonePrice()).isEqualTo(0.0);
        assertThat(internetContract.getTvPrice()).isEqualTo(0.0);
        assertThat(internetContract.getContractDurationMonths()).isEqualTo(12);
        assertThat(internetContract.calculateTotalMonthlyPrice()).isEqualTo(29.95);
    }

    @Test
    @DisplayName("Builder Pattern: Additional Services können hinzugefügt werden")
    void testAdditionalServices() {
        // Given - Contract mit Additional Services
        var serviceContract = com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
            .withContractId("CTR-SERVICES")
            .forCustomer("CUST-SERVICES")
            .startingOn(LocalDate.now().plusDays(5))
            .installedAt("Servicestraße 20, Frankfurt")
            .asTriplePlayContract("100", "+49691234567", "SPORTS_TV", 39.95, 19.95, 29.95)
            .addAdditionalService("SECURITY", "MagentaSecurity Premium")
            .addAdditionalService("STORAGE", "MagentaCloud 1TB")
            .addContractDocument("Terms_and_Conditions.pdf")
            .addContractDocument("Privacy_Policy.pdf")
            .withSpecialConditions("Customer gets free installation due to loyalty program")
            .build();
        
        // Then
        assertThat(serviceContract.getAdditionalServices()).hasSize(2);
        assertThat(serviceContract.getAdditionalServices()).containsEntry("SECURITY", "MagentaSecurity Premium");
        assertThat(serviceContract.getAdditionalServices()).containsEntry("STORAGE", "MagentaCloud 1TB");
        assertThat(serviceContract.getContractDocuments()).hasSize(2);
        assertThat(serviceContract.getSpecialConditions()).contains("loyalty program");
    }

    @Test
    @DisplayName("Builder Pattern: Validation verhindert ungültige Contracts")
    void testBuilderValidation() {
        // Missing required fields
        assertThatThrownBy(() ->
            com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Contract ID is required");

        // Contract Type specific validation
        assertThatThrownBy(() ->
            com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
                .withContractId("CTR-INVALID")
                .forCustomer("CUST-INVALID")
                .withContractType("INTERNET_PHONE")
                .startingOn(LocalDate.now().plusDays(1))
                .installedAt("Test Address")
                // Missing internet speed for INTERNET_PHONE contract
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Internet speed is required");

        // Invalid dates
        assertThatThrownBy(() ->
            com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
                .withContractId("CTR-PASTDATE")
                .forCustomer("CUST-PASTDATE")
                .withContractType("INTERNET_ONLY")
                .startingOn(LocalDate.now().minusDays(1)) // Past date
                .installedAt("Test Address")
                .asInternetOnlyContract("50", 29.95)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Start date cannot be in the past");
    }

    @Test
    @DisplayName("Builder Pattern: Immutability nach Erstellung")
    void testImmutability() {
        // Given
        var contract = com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
            .withContractId("CTR-IMMUTABLE")
            .forCustomer("CUST-IMMUTABLE")
            .startingOn(LocalDate.now().plusDays(1))
            .installedAt("Immutable Street 1")
            .asInternetOnlyContract("100", 39.95)
            .addAdditionalService("TEST", "Test Service")
            .build();
        
        // When - Versuche Collections zu ändern
        var additionalServices = contract.getAdditionalServices();
        var contractDocuments = contract.getContractDocuments();
        
        additionalServices.put("HACK", "Hacked Service");
        contractDocuments.add("Malicious Document");
        
        // Then - Original Contract ist unverändert (defensive copies)
        assertThat(contract.getAdditionalServices()).hasSize(1);
        assertThat(contract.getAdditionalServices()).containsKey("TEST");
        assertThat(contract.getAdditionalServices()).doesNotContainKey("HACK");
        assertThat(contract.getContractDocuments()).hasSize(0);
        assertThat(contract.getContractDocuments()).doesNotContain("Malicious Document");
    }

    @Test
    @DisplayName("Code Quality Vergleich: Builder Pattern vs Long Constructor")
    void testCodeQualityComparison() {
        /*
         * INITIAL VERSION PROBLEMS:
         * 1. Long Parameter List - 27 Parameter in Constructor
         * 2. Parameter Confusion - easy to mix up order
         * 3. Telescoping Constructors - multiple overloaded versions
         * 4. Poor Readability - what does each parameter mean?
         * 5. Hard to Extend - adding parameters breaks existing code
         * 6. Null Parameters - for optional values
         * 7. All-or-Nothing Validation - at the very end
         * 8. Mutable State - can be changed after creation
         * 
         * FIXED VERSION IMPROVEMENTS:
         * 1. Builder Pattern - step by step construction
         * 2. Fluent Interface - self-documenting method calls
         * 3. Flexible Order - set parameters in any order
         * 4. Optional Parameters - with sensible defaults
         * 5. Step-by-step Validation - fail fast approach
         * 6. Immutable Objects - thread-safe and predictable
         * 7. Convenience Methods - for common use cases
         * 8. Easy to Extend - add new parameters without breaking existing code
         */
        
        // This demonstrates how much more readable and maintainable the Builder pattern is
        var readableContract = com.telekom.training.day1.contractbuilder.fixed.TelekomContract.builder()
            .withContractId("READABLE-001")
            .forCustomer("HAPPY-CUSTOMER")
            .startingOn(LocalDate.now().plusWeeks(1))
            .installedAt("Clean Code Street 42")
            .asTriplePlayContract("Gigabit", "+49301111111", "4K Premium", 59.95, 29.95, 39.95)
            .asPremiumBundle()
            .withSpecialConditions("Migrated from competitor - VIP treatment")
            .build();
            
        assertThat(readableContract.getContractId()).isEqualTo("READABLE-001");
        assertThat(readableContract.getPriorityLevel()).isEqualTo("VIP");
        
        // Much easier to read and understand than the 27-parameter constructor!
    }
}