# Service Families: Hard-coded Creation → Abstract Factory Pattern

## Übersicht

Dieses Beispiel zeigt die Transformation von hard-coded Service-Erstellung in ein sauberes Abstract Factory Pattern, das kohärente Service-Familien garantiert.

## Initial Version Problems

### Hard-coded Service Creation Anti-Pattern

```java
public class TelekomServiceProvider {
    public InternetService createInternetService(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "DSL":
                DslService dslService = new DslService();
                dslService.setTechnology("ADSL2+");
                dslService.setMaxSpeed(250);
                // ... viele hardcoded Konfigurationen
                return dslService;
            case "MOBILE":
                // ... noch mehr hardcoded Logic
            case "FIBER":
                // ... und noch mehr...
        }
    }
    
    // DUPLICATE LOGIC in createPhoneService(), createTvService()
}
```

### Identifizierte Code-Smells

1. **Giant Switch Statements**: In jeder create-Method
2. **Code Duplication**: Ähnliche Patterns überall wiederholt
3. **Hard-coded Configuration**: Keine Flexibilität
4. **Tight Coupling**: Provider kennt alle konkreten Services
5. **Open/Closed Violation**: Neue Service-Familie = alle Methoden ändern
6. **No Service Family Cohesion**: Services können inkompatibel sein

### Konkrete Probleme

```java
// PROBLEM: Was garantiert Kompatibilität?
InternetService dslInternet = provider.createInternetService("DSL");
PhoneService mobilePhone = provider.createPhoneService("MOBILE"); // Mismatch!
TvService fiberTv = provider.createTvService("FIBER");            // Chaos!

// Kein Zusammenhang zwischen Services - können inkompatibel sein!
```

## Fixed Version Solution

### Abstract Factory Pattern Struktur

```java
// Abstract Factory - definiert Interface für Service-Familien
public abstract class TelekomServiceFactory {
    // Factory Methods für Service-Familie
    public abstract InternetService createInternetService();
    public abstract PhoneService createPhoneService(); 
    public abstract TvService createTvService();
    
    // Template Methods für Bundle Creation
    public ServiceBundle createCompleteBundle() {
        ServiceBundle bundle = new ServiceBundle(getServiceFamilyName());
        bundle.addService(createInternetService());
        bundle.addService(createPhoneService());
        bundle.addService(createTvService());
        bundle.setDiscountPercentage(getBundleDiscount());
        return bundle;
    }
    
    // Static Factory Method für Factory Selection
    public static TelekomServiceFactory getFactory(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "DSL": return new DslServiceFactory();
            case "MOBILE": return new MobileServiceFactory();
            case "FIBER": return new FiberServiceFactory();
            default: throw new IllegalArgumentException("Unknown service type");
        }
    }
}
```

### Concrete Factories für Service-Familien

```java
// Concrete Factory für DSL Familie
public class DslServiceFactory extends TelekomServiceFactory {
    @Override
    public InternetService createInternetService() {
        return new DslInternetService(); // Kohärent mit DSL-Familie
    }
    
    @Override  
    public PhoneService createPhoneService() {
        return new DslPhoneService(); // Kompatibel mit DSL Infrastructure
    }
    
    @Override
    public TvService createTvService() {
        return new DslTvService(); // IPTV über DSL
    }
    
    @Override
    protected String getServiceFamilyName() { return "DSL_FAMILY"; }
    @Override
    protected double getBundleDiscount() { return 15.0; }
}
```

### Service Family Cohesion

```java
// DSL Service Familie - alle Services arbeiten zusammen
public class DslInternetService implements InternetService {
    private final String technology = "ADSL2+/VDSL2";
    private final String installationType = "TECHNICIAN_REQUIRED";
    private final String equipment = "Fritz!Box 7590";
    // ... DSL-spezifische Konfiguration
}

public class DslPhoneService implements PhoneService {
    private final String technology = "ISDN/VoIP über DSL";
    private final String installationType = "WITH_DSL_INSTALLATION"; // Kohärent!
    private final String equipment = "Included in Fritz!Box";        // Wiederverwendung!
    // ... DSL Phone spezifische Konfiguration
}

public class DslTvService implements TvService {
    private final String technology = "IPTV über DSL";
    private final String installationType = "MAGENTA_TV_BOX";
    private final String streamingCapability = "LIMITED_BY_DSL_BANDWIDTH"; // Realistisch!
    // ... DSL TV spezifische Konfiguration
}
```

## Refactoring Schritte

### Schritt 1: Abstract Product Interfaces definieren
```java
// Clean Interface Definition
public interface InternetService {
    String getTechnology();
    int getMaxSpeed();
    String getInstallationType();
    String getEquipment(); 
    String getServiceInfo();
    double getBasicPrice();
}
```

### Schritt 2: Abstract Factory erstellen
```java
public abstract class TelekomServiceFactory {
    // Factory Methods - abstract
    public abstract InternetService createInternetService();
    public abstract PhoneService createPhoneService();
    public abstract TvService createTvService();
    
    // Template Methods - konkrete Implementation
    public ServiceBundle createCompleteBundle() { /* ... */ }
}
```

### Schritt 3: Service-Familien identifizieren
- **DSL Familie**: ADSL/VDSL Internet + ISDN/VoIP Phone + IPTV
- **Mobile Familie**: LTE/5G Internet + GSM/LTE Phone + Mobile TV App  
- **Fiber Familie**: FTTH Internet + VoIP Phone + 4K IPTV

### Schritt 4: Concrete Factories implementieren
```java
public class MobileServiceFactory extends TelekomServiceFactory {
    @Override
    public InternetService createInternetService() {
        return new MobileInternetService(); // LTE/5G Technology
    }
    
    @Override
    public PhoneService createPhoneService() {
        return new MobilePhoneService(); // GSM/LTE Technology
    }
    
    @Override
    public TvService createTvService() {
        return new MobileTvService(); // Streaming App
    }
}
```

### Schritt 5: Concrete Products implementieren
```java
// Mobile Service Familie - technologisch kohärent
public class MobileInternetService implements InternetService {
    private final String technology = "LTE/5G";
    private final String installationType = "SIM_ACTIVATION";
    private final String availabilityCheck = "COVERAGE_BASED";
}
```

### Schritt 6: Client Code refactoring
```java
// Vorher: Hard-coded, fehleranfällig
TelekomServiceProvider provider = new TelekomServiceProvider();
InternetService internet = provider.createInternetService("DSL");
PhoneService phone = provider.createPhoneService("MOBILE"); // Incompatible!

// Nachher: Kohärente Service-Familie garantiert
TelekomServiceFactory dslFactory = TelekomServiceFactory.getFactory("DSL");
ServiceBundle bundle = dslFactory.createCompleteBundle(); // Alle Services kompatibel!
```

## Architektur Verbesserungen

### Vorher (Hard-coded)
```
TelekomServiceProvider
├── createInternetService()
│   ├── Switch Statement (DSL/MOBILE/FIBER)
│   ├── Hard-coded DSL Configuration
│   ├── Hard-coded Mobile Configuration  
│   └── Hard-coded Fiber Configuration
├── createPhoneService()
│   ├── Duplicate Switch Statement
│   ├── Duplicate Mapping Logic
│   └── No Service Cohesion
└── createTvService()
    ├── More Duplication
    └── No Family Guarantees
```

### Nachher (Abstract Factory)
```
TelekomServiceFactory [Abstract]
├── createInternetService() [abstract]
├── createPhoneService() [abstract] 
├── createTvService() [abstract]
└── createCompleteBundle() [template method]

DslServiceFactory
├── createInternetService() → DslInternetService
├── createPhoneService() → DslPhoneService
└── createTvService() → DslTvService

MobileServiceFactory  
├── createInternetService() → MobileInternetService
├── createPhoneService() → MobilePhoneService
└── createTvService() → MobileTvService

FiberServiceFactory
├── createInternetService() → FiberInternetService  
├── createPhoneService() → FiberPhoneService
└── createTvService() → FiberTvService
```

## Design Patterns Angewendet

### 1. Abstract Factory Pattern
- **Intent**: Familien verwandter Objekte erstellen ohne konkrete Klassen zu spezifizieren
- **Application**: Service-Familien (DSL/Mobile/Fiber) mit garantierter Kompatibilität
- **Benefit**: Kohärente Produkt-Familien, erweiterbar, austauschbar

### 2. Template Method Pattern
- **Intent**: Algorithmus-Skelett definieren, Schritte an Subklassen delegieren
- **Application**: `createCompleteBundle()` definiert Bundle-Erstellung, Factory Methods sind variabel
- **Benefit**: Code Reuse für Bundle Logic, konsistente Bundle-Struktur

### 3. Factory Method Pattern
- **Intent**: Objekterzeugung an Subklassen delegieren
- **Application**: `getFactory()` erstellt passende Concrete Factory
- **Benefit**: Factory Selection abstrahiert, erweiterbar

## Service Family Cohesion Benefits

### Technische Kohärenz
```java
// DSL Familie - alles arbeitet über DSL Infrastructure
DslInternetService:    "ADSL2+/VDSL2" + "Fritz!Box 7590"
DslPhoneService:       "VoIP über DSL" + "Included in Fritz!Box"  
DslTvService:          "IPTV über DSL" + "Limited by DSL Bandwidth"
```

### Installation Kohärenz
```java
// DSL Familie - koordinierte Installation
DslInternetService:    installationType = "TECHNICIAN_REQUIRED"
DslPhoneService:       installationType = "WITH_DSL_INSTALLATION" 
DslTvService:          installationType = "MAGENTA_TV_BOX"
// → Ein Techniker-Termin für alles!
```

### Business Logic Kohärenz
```java
// Fiber Familie - Premium Services mit höheren Preisen aber besserer Performance
FiberInternetService:  1000 Mbps für 49.95 EUR
FiberPhoneService:     HD Voice + Video Calls für 24.95 EUR
FiberTvService:        4K Premium für 34.95 EUR
// → Bundle Discount 20% (höher als DSL 15%)
```

## Erweiterbarkeit Demo

### Neue Service-Familie hinzufügen
```java
// 1. Neue Concrete Factory
public class SatelliteServiceFactory extends TelekomServiceFactory {
    @Override
    public InternetService createInternetService() {
        return new SatelliteInternetService(); // Sky-based Internet
    }
    
    @Override
    public PhoneService createPhoneService() {
        return new SatellitePhoneService(); // Emergency Sat Phone
    }
    
    @Override
    public TvService createTvService() {
        return new SatelliteTvService(); // Satellite TV Broadcast
    }
    
    @Override
    protected String getServiceFamilyName() { return "SATELLITE_FAMILY"; }
    @Override  
    protected double getBundleDiscount() { return 25.0; } // Premium pricing
}

// 2. Factory Selection erweitern
case "SATELLITE": return new SatelliteServiceFactory();

// 3. Sofort verwendbar - keine Änderung an bestehendem Code!
ServiceBundle satelliteBundle = TelekomServiceFactory
    .getFactory("SATELLITE")
    .createCompleteBundle();
```

## Testing Verbesserungen

### Vorher: Schwer testbar
```java
@Test
void testHardcodedProvider() {
    TelekomServiceProvider provider = new TelekomServiceProvider();
    // Muss alle Switch Cases testen
    // Schwer Service-Kompatibilität zu validieren
    // Viele Edge Cases und Parameter-Kombinationen
}
```

### Nachher: Granulare Tests
```java
@Test
void testDslServiceFamily() {
    TelekomServiceFactory factory = new DslServiceFactory();
    
    InternetService internet = factory.createInternetService();
    PhoneService phone = factory.createPhoneService();
    TvService tv = factory.createTvService();
    
    // Test Service Family Cohesion
    assertThat(factory.areServicesCompatible(internet, phone, tv)).isTrue();
    assertThat(phone.getInstallationType()).contains("DSL");
    assertThat(tv.getTechnology()).contains("DSL");
}

@Test
void testServiceFamilyPricing() {
    TelekomServiceFactory dslFactory = new DslServiceFactory();
    TelekomServiceFactory fiberFactory = new FiberServiceFactory();
    
    ServiceBundle dslBundle = dslFactory.createCompleteBundle();
    ServiceBundle fiberBundle = fiberFactory.createCompleteBundle();
    
    assertThat(fiberBundle.calculateTotalPrice())
        .isGreaterThan(dslBundle.calculateTotalPrice());
}
```

## Key Takeaways

### SOLID Principles Applied
- **S**: Single Responsibility - jede Factory für eine Service-Familie
- **O**: Open/Closed - neue Familien ohne Änderung bestehender Factories
- **L**: Liskov Substitution - alle Factories austauschbar über Abstract Factory
- **I**: Interface Segregation - clean Product Interfaces
- **D**: Dependency Inversion - Client abhängig von Abstract Factory

### Business Benefits
1. **Service Cohesion**: Technisch kompatible Service-Kombinationen  
2. **Installation Efficiency**: Koordinierte Installation und Setup
3. **Pricing Consistency**: Kohärente Pricing-Strategien per Familie
4. **Marketing Clarity**: Klare Produkt-Linien für Kunden
5. **Technical Support**: Specialized Support pro Service-Familie

### Development Benefits  
1. **No Code Duplication**: Mapping Logic nur einmal pro Familie
2. **Type Safety**: Compiler garantiert Service-Familie Kohärenz
3. **Easy Extension**: Neue Service-Familien ohne Breaking Changes
4. **Clean Testing**: Isolated testing pro Service-Familie
5. **Maintainable**: Klare Verantwortlichkeiten und Strukturen

## Diskussionspunkte für Training

1. **Wann ist Abstract Factory das richtige Pattern?**
2. **Wie identifiziert man Produkt-Familien?**
3. **Was ist der Unterschied zu Factory Method?**
4. **Wie garantiert man Service-Kompatibilität?**
5. **Welche Trade-offs hat Abstract Factory?**

## Weiterführende Übungen

1. **Business Extension**: Implementiere Business vs. Consumer Service-Familien
2. **Configuration Factory**: Mache Service Configuration über Factory steuerbar
3. **Dynamic Pricing**: Implementiere unterschiedliche Pricing-Strategien pro Familie
4. **Service Validation**: Erweiterte Compatibility-Checks zwischen Services
5. **Bundle Optimization**: Intelligente Bundle-Zusammenstellung basierend auf Kundenprofil