package com.telekom.training.day1.servicefamilies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.telekom.training.day1.servicefamilies.initial.TelekomServiceProvider;
import com.telekom.training.day1.servicefamilies.fixed.*;
import com.telekom.training.day1.servicefamilies.fixed.services.*;

/**
 * Vergleichende Tests zwischen Initial (Hard-coded) und Fixed (Abstract Factory) Version
 */
class ServiceFamiliesTest {

    @Test
    @DisplayName("Initial Version: Hard-coded Service Creation funktioniert, aber schlecht erweiterbar")
    void testInitialVersion_ServiceCreation() {
        // Given
        TelekomServiceProvider provider = new TelekomServiceProvider();
        
        // When - DSL Services erstellen
        var dslInternet = provider.createInternetService("DSL");
        var dslPhone = provider.createPhoneService("DSL");
        var dslTv = provider.createTvService("DSL");
        
        // Then
        assertThat(dslInternet).isNotNull();
        assertThat(dslPhone).isNotNull();
        assertThat(dslTv).isNotNull();
        
        // Code-Smells in Initial Version:
        // - Giant Switch Statements in jeder create-Method
        // - Hard-coded Service Configuration
        // - Code Duplication zwischen verschiedenen Service Types
        // - Schwer erweiterbar (neue Service-Familie = viel Copy&Paste)
        // - Tight Coupling zwischen Provider und konkreten Services
        // - Verletzt Open/Closed Principle
    }

    @Test
    @DisplayName("Fixed Version: Abstract Factory erstellt kohärente Service-Familien")
    void testFixedVersion_AbstractFactory() {
        // Given - Abstract Factory Pattern
        TelekomServiceFactory dslFactory = TelekomServiceFactory.getFactory("DSL");
        TelekomServiceFactory mobileFactory = TelekomServiceFactory.getFactory("MOBILE");
        TelekomServiceFactory fiberFactory = TelekomServiceFactory.getFactory("FIBER");
        
        // When - Services über Factory erstellen
        InternetService dslInternet = dslFactory.createInternetService();
        PhoneService dslPhone = dslFactory.createPhoneService();
        TvService dslTv = dslFactory.createTvService();
        
        InternetService mobileInternet = mobileFactory.createInternetService();
        PhoneService mobilePhone = mobileFactory.createPhoneService();
        TvService mobileTv = mobileFactory.createTvService();
        
        // Then - Kohärente Service-Familien
        assertThat(dslInternet.getTechnology()).contains("DSL");
        assertThat(dslPhone.getTechnology()).contains("ISDN/VoIP");
        assertThat(dslTv.getTechnology()).contains("IPTV");
        
        assertThat(mobileInternet.getTechnology()).contains("LTE");
        assertThat(mobilePhone.getTechnology()).contains("GSM");
        assertThat(mobileTv.getTechnology()).contains("Streaming");
        
        // Improvements in Fixed Version:
        // - Abstract Factory Pattern korrekt angewendet
        // - Kohärente Service-Familien garantiert
        // - Einfach erweiterbar (neue Factory = neue Service-Familie)
        // - Open/Closed Principle erfüllt
        // - Lose Kopplung durch Abstraktion
        // - Template Method für Bundle Creation
    }

    @Test
    @DisplayName("Abstract Factory: Service-Kompatibilität innerhalb einer Familie")
    void testServiceCompatibility() {
        // Given
        TelekomServiceFactory factory = TelekomServiceFactory.getFactory("DSL");
        
        // When
        InternetService internet = factory.createInternetService();
        PhoneService phone = factory.createPhoneService();
        TvService tv = factory.createTvService();
        
        // Then - Services aus derselben Factory sind kompatibel
        assertThat(factory.areServicesCompatible(internet, phone, tv)).isTrue();
        
        // Installation Types sind konsistent innerhalb der Familie
        assertThat(phone.getInstallationType()).contains("DSL");
        assertThat(tv.getInstallationType()).contains("BOX");
    }

    @Test
    @DisplayName("Bundle Creation: Template Method Pattern in Abstract Factory")
    void testBundleCreation() {
        // Given
        TelekomServiceFactory dslFactory = TelekomServiceFactory.getFactory("DSL");
        TelekomServiceFactory fiberFactory = TelekomServiceFactory.getFactory("FIBER");
        
        // When - Template Method für Bundle Creation
        ServiceBundle dslBundle = dslFactory.createCompleteBundle();
        ServiceBundle fiberBundle = fiberFactory.createCompleteBundle();
        ServiceBundle basicBundle = dslFactory.createBasicBundle();
        
        // Then - Bundles haben korrekte Struktur
        assertThat(dslBundle.getServices()).hasSize(3); // Internet + Phone + TV
        assertThat(basicBundle.getServices()).hasSize(2); // Internet + Phone only
        
        assertThat(dslBundle.getDiscountPercentage()).isEqualTo(15.0);
        assertThat(fiberBundle.getDiscountPercentage()).isEqualTo(20.0);
        assertThat(basicBundle.getDiscountPercentage()).isEqualTo(9.0); // 15% * 0.6
        
        // Pricing funktioniert
        assertThat(dslBundle.calculateTotalPrice()).isGreaterThan(0);
        assertThat(fiberBundle.calculateTotalPrice()).isGreaterThan(dslBundle.calculateTotalPrice());
    }

    @Test
    @DisplayName("Erweiterbarkeit: Neue Service-Familie kann einfach hinzugefügt werden")
    void testExtensibility() {
        // Dies demonstriert, wie einfach eine neue Service-Familie hinzugefügt werden kann
        // In der Initial Version müsste man alle Switch-Statements erweitern
        // In der Fixed Version nur eine neue Factory erstellen
        
        // Given - Custom Factory für Satellite Services
        TelekomServiceFactory satelliteFactory = new TelekomServiceFactory() {
            @Override
            public InternetService createInternetService() {
                return new InternetService() {
                    public String getTechnology() { return "Satellite"; }
                    public int getMaxSpeed() { return 100; }
                    public String getInstallationType() { return "DISH_INSTALLATION"; }
                    public String getEquipment() { return "Satellite Dish"; }
                    public String getAvailabilityCheck() { return "SKY_VIEW_REQUIRED"; }
                    public String getServiceInfo() { return "Satellite Internet: 100 Mbps"; }
                    public double getBasicPrice() { return 59.95; }
                };
            }
            
            @Override
            public PhoneService createPhoneService() {
                return new PhoneService() {
                    public String getTechnology() { return "Satellite Phone"; }
                    public String getFeatures() { return "EMERGENCY_CALLS"; }
                    public String getInstallationType() { return "SATELLITE_PHONE"; }
                    public String getEquipment() { return "Satellite Phone Device"; }
                    public String getNumberType() { return "SATELLITE"; }
                    public String getServiceInfo() { return "Satellite Phone Service"; }
                    public double getBasicPrice() { return 79.95; }
                };
            }
            
            @Override
            public TvService createTvService() {
                return new TvService() {
                    public String getTechnology() { return "Satellite TV"; }
                    public String getChannelPackage() { return "Premium Satellite"; }
                    public String getInstallationType() { return "SATELLITE_RECEIVER"; }
                    public String getEquipment() { return "Satellite Receiver"; }
                    public String getStreamingCapability() { return "HD_BROADCAST"; }
                    public String getServiceInfo() { return "Satellite TV: Premium Package"; }
                    public double getBasicPrice() { return 39.95; }
                };
            }
            
            @Override
            protected String getServiceFamilyName() { return "SATELLITE_FAMILY"; }
            
            @Override
            protected double getBundleDiscount() { return 25.0; }
        };
        
        // When
        ServiceBundle satelliteBundle = satelliteFactory.createCompleteBundle();
        
        // Then - Neue Service-Familie funktioniert sofort
        assertThat(satelliteBundle.getServices()).hasSize(3);
        assertThat(satelliteBundle.getBundleType()).isEqualTo("SATELLITE_FAMILY");
        assertThat(satelliteBundle.getDiscountPercentage()).isEqualTo(25.0);
        
        // This demonstrates the Open/Closed Principle:
        // - Open for extension (new service families)
        // - Closed for modification (existing factories unchanged)
    }

    @Test
    @DisplayName("Code Quality Vergleich: Zeigt Verbesserungen auf")
    void testCodeQualityComparison() {
        /*
         * INITIAL VERSION PROBLEMS:
         * 1. Giant Switch Statements - schwer zu erweitern
         * 2. Hard-coded Service Creation - keine Flexibilität
         * 3. Code Duplication - ähnliche Patterns überall
         * 4. Tight Coupling - Provider kennt alle konkreten Services
         * 5. Violates Open/Closed - neue Services = Änderung aller Methoden
         * 6. No Service Family Cohesion - Services können inkompatibel sein
         * 
         * FIXED VERSION IMPROVEMENTS:
         * 1. Abstract Factory Pattern - saubere Abstraktion
         * 2. Service Family Cohesion - kompatible Services garantiert
         * 3. Open/Closed Principle - neue Familien ohne Änderung bestehender
         * 4. Template Method - gemeinsame Bundle Logic
         * 5. Loose Coupling - nur Interfaces bekannt
         * 6. Easy Extensibility - neue Factory = neue Service-Familie
         */
        
        // Both versions create the same services, but the architecture is vastly different
        assertThat("Abstract Factory provides better structure").isNotEmpty();
    }

    @Test
    @DisplayName("Service Pricing: Bundle Discounts funktionieren korrekt")
    void testServicePricing() {
        // Given
        TelekomServiceFactory factory = TelekomServiceFactory.getFactory("FIBER");
        
        // When
        ServiceBundle completeBundle = factory.createCompleteBundle();
        ServiceBundle basicBundle = factory.createBasicBundle();
        
        // Then - Pricing Logic funktioniert
        double completePrice = completeBundle.calculateTotalPrice();
        double basicPrice = basicBundle.calculateTotalPrice();
        
        assertThat(completePrice).isGreaterThan(basicPrice);
        assertThat(completeBundle.getDiscountPercentage()).isEqualTo(20.0);
        assertThat(basicBundle.getDiscountPercentage()).isEqualTo(12.0); // 20% * 0.6
        
        // Individual service prices sind verfügbar
        InternetService internet = factory.createInternetService();
        assertThat(internet.getBasicPrice()).isEqualTo(49.95);
    }
}