# Modul 5: Prototype Pattern - Effiziente Objektklonierung in Enterprise-Konfigurationen

## Lernziele
- Prototype Pattern für kostspielige Objekterzeugung verstehen
- Deep vs. Shallow Copy Problematik meistern
- Configuration-Cloning für komplexe Enterprise-Settings
- Performance-Optimierung durch intelligente Objektkopierung

## 1. Problem-Motivation

### Ausgangssituation: Komplexe Service-Konfiguration
In Enterprise-Umgebungen müssen häufig ähnliche, aber leicht unterschiedliche Service-Konfigurationen erstellt werden. Die Initialisierung dieser Konfigurationsobjekte ist aufwändig und zeitkritisch.

### Herausforderungen
- **Kostspielige Objekterzeugung**: Database-Connections, SSL-Certificates, Complex Validation
- **Template-basierte Konfiguration**: 90% identisch, 10% variabel
- **Runtime-Configuration**: Dynamische Erstellung zur Laufzeit erforderlich
- **Memory-Effizienz**: Sharing von unveränderlichen Teilen

### Problematischer Code - Expensive Object Creation

```java
@Service
public class ServiceConfigurationManager {
    
    // Anti-Pattern: Expensive Recreation für ähnliche Konfigurationen
    public ServiceConfiguration createDevConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // Expensive Database Lookups für Defaults (200ms+)
        config.setDatabaseSettings(loadDatabaseDefaults());
        config.setSecuritySettings(loadSecurityDefaults());
        config.setCachingSettings(loadCachingDefaults());
        
        // Expensive SSL Certificate Validation (500ms+)
        config.setSslCertificate(validateAndLoadSslCert("dev-cert.pem"));
        
        // Expensive Service Discovery (300ms+)
        config.setServiceEndpoints(discoverAvailableServices());
        
        // Environment-specific anpassungen
        config.setEnvironment("DEV");
        config.setLogLevel("DEBUG");
        config.setDatabaseUrl("dev-database-url");
        
        return config;
    }
    
    public ServiceConfiguration createTestConfiguration() {
        ServiceConfiguration config = new ServiceConfiguration();
        
        // DUPLICATE: Gleiche expensive Operations!
        config.setDatabaseSettings(loadDatabaseDefaults());    // 200ms
        config.setSecuritySettings(loadSecurityDefaults());    // 200ms
        config.setCachingSettings(loadCachingDefaults());      // 200ms
        config.setSslCertificate(validateAndLoadSslCert("test-cert.pem")); // 500ms
        config.setServiceEndpoints(discoverAvailableServices()); // 300ms
        
        // Nur diese Werte sind unterschiedlich
        config.setEnvironment("TEST");
        config.setLogLevel("INFO");
        config.setDatabaseUrl("test-database-url");
        
        return config;
    }
    
    public ServiceConfiguration createProdConfiguration() {
        // NOCH MEHR DUPLICATION...
        // Gleiche expensive Operations: 1400ms+ für jede Erstellung!
    }
}
```

**Performance-Problem**: Jede Konfigurationserstellung dauert 1.4+ Sekunden für identische Operationen!

## 2. Prototype Pattern - Struktur

Das Prototype Pattern erstellt neue Objekte durch Klonen eines prototypischen Objekts, statt durch normale Instanziierung.

### Prototype Pattern UML-Struktur
```
┌─────────────────┐       ┌─────────────────┐
│    <<interface>>│       │   ConcreteProto │
│   Prototype     │◄──────┤   type1         │
├─────────────────┤       ├─────────────────┤
│ + clone()       │       │ + clone()       │
└─────────────────┘       │ + setSpecific() │
        ▲                 └─────────────────┘
        │
┌─────────────────┐       ┌─────────────────┐
│  ConcreteProto  │       │    Client       │
│  type2          │       ├─────────────────┤
├─────────────────┤       │ - prototype     │
│ + clone()       │       │ + operation()   │
│ + setSpecific() │       └─────────────────┘
└─────────────────┘
```

**Kernidee**: "Kopiere bestehende Objekte, statt neue von Grund auf zu erstellen"

## 3. Prototype Pattern Implementation - Configuration Cloning

### Cloneable Configuration Hierarchy

```java
// Prototype Interface für Configuration-Objekte
public abstract class ServiceConfiguration implements Cloneable {
    private String environment;
    private String logLevel;
    private String databaseUrl;
    private DatabaseSettings databaseSettings;
    private SecuritySettings securitySettings;
    private CachingSettings cachingSettings;
    private SSLCertificate sslCertificate;
    private ServiceEndpoints serviceEndpoints;
    
    // Template Method für Cloning mit Deep Copy Control
    @Override
    public ServiceConfiguration clone() {
        try {
            ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
            
            // Deep Copy für mutable, komplexe Objekte
            cloned.databaseSettings = this.databaseSettings.deepCopy();
            cloned.securitySettings = this.securitySettings.deepCopy();
            cloned.cachingSettings = this.cachingSettings.deepCopy();
            
            // Shared Reference für immutable, expensive Objekte
            cloned.sslCertificate = this.sslCertificate; // SSL Cert ist immutable
            cloned.serviceEndpoints = this.serviceEndpoints; // Service Discovery ist immutable
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Builder-style Setters für fluent Configuration
    public ServiceConfiguration withEnvironment(String env) {
        this.environment = env;
        return this;
    }
    
    public ServiceConfiguration withLogLevel(String level) {
        this.logLevel = level;
        return this;
    }
    
    public ServiceConfiguration withDatabaseUrl(String url) {
        this.databaseUrl = url;
        return this;
    }
}
```

### Concrete Prototype - Environment-specific Configurations

```java
@Component
@Scope("prototype")
public class TelekomServiceConfiguration extends ServiceConfiguration {
    
    // Template-spezifische Initialisierung
    public TelekomServiceConfiguration() {
        // Diese expensive Operations passieren nur EINMAL pro Template
    }
    
    @Override
    public TelekomServiceConfiguration clone() {
        return (TelekomServiceConfiguration) super.clone();
    }
    
    // Factory Method für Template Creation
    public static TelekomServiceConfiguration createBaseTemplate() {
        TelekomServiceConfiguration template = new TelekomServiceConfiguration();
        
        // Expensive Operations nur einmal für Template
        template.setDatabaseSettings(loadDatabaseDefaults());      // 200ms
        template.setSecuritySettings(loadSecurityDefaults());      // 200ms  
        template.setCachingSettings(loadCachingDefaults());        // 200ms
        template.setSslCertificate(loadSslCertificate());          // 500ms
        template.setServiceEndpoints(discoverServiceEndpoints());  // 300ms
        // Total: 1400ms - aber nur EINMAL!
        
        return template;
    }
}
```

## 4. Configuration Registry Pattern - Prototype Management

### Centralized Prototype Registry

```java
@Service
public class ConfigurationPrototypeRegistry {
    private final Map<String, ServiceConfiguration> prototypes = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initializePrototypes() {
        // Template Prototypes werden EINMAL erstellt und gecached
        ServiceConfiguration baseTemplate = TelekomServiceConfiguration.createBaseTemplate();
        prototypes.put("TELEKOM_BASE", baseTemplate);
        
        // Weitere Templates können hinzugefügt werden
        ServiceConfiguration microserviceTemplate = createMicroserviceTemplate(baseTemplate);
        prototypes.put("MICROSERVICE_BASE", microserviceTemplate);
    }
    
    // Fast Cloning - 1400ms → 2ms!
    public ServiceConfiguration createConfiguration(String templateName, 
                                                   ConfigurationCustomizer customizer) {
        ServiceConfiguration prototype = prototypes.get(templateName);
        if (prototype == null) {
            throw new IllegalArgumentException("Unknown template: " + templateName);
        }
        
        // Clone ist sehr schnell (shallow + selective deep copy)
        ServiceConfiguration cloned = prototype.clone();
        
        // Customization anwenden
        customizer.customize(cloned);
        
        return cloned;
    }
    
    // Functional Interface für Configuration Customization
    @FunctionalInterface
    public interface ConfigurationCustomizer {
        void customize(ServiceConfiguration config);
    }
}
```

## 5. Prototype Pattern Usage - Performance Optimization

### Optimized Configuration Factory

```java
@Service
public class OptimizedConfigurationManager {
    private final ConfigurationPrototypeRegistry registry;
    
    public OptimizedConfigurationManager(ConfigurationPrototypeRegistry registry) {
        this.registry = registry;
    }
    
    // DEV Configuration - von 1400ms auf 2ms!
    public ServiceConfiguration createDevConfiguration() {
        return registry.createConfiguration("TELEKOM_BASE", config -> {
            config.withEnvironment("DEV")
                  .withLogLevel("DEBUG")
                  .withDatabaseUrl("dev-database-url");
        });
    }
    
    // TEST Configuration - von 1400ms auf 2ms!
    public ServiceConfiguration createTestConfiguration() {
        return registry.createConfiguration("TELEKOM_BASE", config -> {
            config.withEnvironment("TEST")
                  .withLogLevel("INFO")
                  .withDatabaseUrl("test-database-url");
        });
    }
    
    // PRODUCTION Configuration - von 1400ms auf 2ms!
    public ServiceConfiguration createProdConfiguration() {
        return registry.createConfiguration("TELEKOM_BASE", config -> {
            config.withEnvironment("PROD")
                  .withLogLevel("WARN")
                  .withDatabaseUrl("prod-database-url");
        });
    }
    
    // Microservice-specific Configuration
    public ServiceConfiguration createMicroserviceConfiguration(String serviceName) {
        return registry.createConfiguration("MICROSERVICE_BASE", config -> {
            config.withEnvironment("MICROSERVICE")
                  .withServiceName(serviceName);
        });
    }
}
```

### Performance Benchmark Results

```java
@Component
public class PrototypePatternBenchmark {
    
    @Benchmark
    public void measureConfigurationCreation() {
        // Ohne Prototype Pattern: 1400ms pro Configuration
        long start1 = System.nanoTime();
        ServiceConfiguration config1 = new ServiceConfigurationManager().createDevConfiguration();
        long duration1 = (System.nanoTime() - start1) / 1_000_000; // ~1400ms
        
        // Mit Prototype Pattern: 2ms pro Configuration
        long start2 = System.nanoTime();
        ServiceConfiguration config2 = optimizedManager.createDevConfiguration();
        long duration2 = (System.nanoTime() - start2) / 1_000_000; // ~2ms
        
        // Performance Improvement: 99.86%!
        System.out.println("Improvement: " + (duration1 / (float)duration2) + "x faster");
    }
}
```

## 6. Deep vs. Shallow Copy Strategie

### Intelligent Copy Strategy

```java
public abstract class ServiceConfiguration implements Cloneable {
    
    @Override
    public ServiceConfiguration clone() {
        try {
            ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
            
            // SHALLOW COPY für immutable, expensive Objects
            // SSL Certificate ist immutable und expensive zu laden
            cloned.sslCertificate = this.sslCertificate;
            
            // Service Endpoints sind immutable Discovery-Ergebnisse
            cloned.serviceEndpoints = this.serviceEndpoints;
            
            // DEEP COPY für mutable Objects
            cloned.databaseSettings = cloneDatabaseSettings();
            cloned.securitySettings = cloneSecuritySettings();
            cloned.cachingSettings = cloneCachingSettings();
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning failed", e);
        }
    }
    
    // Selective Deep Copy Methods
    private DatabaseSettings cloneDatabaseSettings() {
        if (this.databaseSettings == null) return null;
        return DatabaseSettings.builder()
            .connectionPool(this.databaseSettings.getConnectionPool()) // Shared Pool
            .timeout(this.databaseSettings.getTimeout())
            .retryPolicy(this.databaseSettings.getRetryPolicy().copy()) // Mutable
            .build();
    }
}
```

## 7. Prototype Pattern Anti-Patterns vermeiden

### Anti-Pattern: Shallow Copy Bugs

```java
// SCHLECHT: Unbeabsichtigtes Object Sharing
@Override
public ServiceConfiguration clone() {
    try {
        ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
        // FEHLER: List wird geshared zwischen allen Clones!
        // cloned.serviceEndpoints = this.serviceEndpoints; // Referenz geteilt!
        return cloned;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}

// KORREKT: Controlled Sharing vs. Deep Copy
@Override
public ServiceConfiguration clone() {
    try {
        ServiceConfiguration cloned = (ServiceConfiguration) super.clone();
        
        // Bewusste Entscheidung: SSL Certificate ist immutable → Sharing OK
        cloned.sslCertificate = this.sslCertificate;
        
        // Bewusste Entscheidung: Service Liste ist mutable → Deep Copy
        cloned.serviceEndpoints = new ArrayList<>(this.serviceEndpoints);
        
        return cloned;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```

### Anti-Pattern: Clone Construction Mixing

```java
// SCHLECHT: Constructor Logic im Clone
@Override  
public ServiceConfiguration clone() {
    // FEHLER: Constructor-Logic wiederholt expensive Operations!
    return new ServiceConfiguration(this.environment, this.logLevel); // Expensive!
}

// KORREKT: Pure Cloning ohne Constructor
@Override
public ServiceConfiguration clone() {
    try {
        // Nur Memory Copy - keine Business Logic
        return (ServiceConfiguration) super.clone();
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```

## 8. Testing von Prototype Pattern

### Clone Correctness Tests

```java
@Test
class PrototypePatternTest {
    
    @Test
    void testCloneIndependence() {
        // Given: Original configuration
        ServiceConfiguration original = registry.getPrototype("TELEKOM_BASE");
        
        // When: Clone und Modify
        ServiceConfiguration cloned = original.clone();
        cloned.withDatabaseUrl("modified-url");
        
        // Then: Original bleibt unverändert
        assertThat(original.getDatabaseUrl()).isNotEqualTo("modified-url");
        assertThat(cloned.getDatabaseUrl()).isEqualTo("modified-url");
    }
    
    @Test 
    void testSharedImmutableObjects() {
        ServiceConfiguration config1 = registry.createConfiguration("TELEKOM_BASE", c -> {});
        ServiceConfiguration config2 = registry.createConfiguration("TELEKOM_BASE", c -> {});
        
        // SSL Certificate sollte geshared werden (immutable + expensive)
        assertThat(config1.getSslCertificate()).isSameAs(config2.getSslCertificate());
        
        // Database Settings sollten separiert werden (mutable)
        assertThat(config1.getDatabaseSettings()).isNotSameAs(config2.getDatabaseSettings());
        assertThat(config1.getDatabaseSettings()).isEqualTo(config2.getDatabaseSettings());
    }
    
    @Test
    void testPerformanceImprovement() {
        // Measure prototype-based creation
        long start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            registry.createConfiguration("TELEKOM_BASE", c -> c.withEnvironment("TEST" + i));
        }
        long prototypeDuration = System.nanoTime() - start;
        
        // Should be significantly faster than constructor-based creation
        assertThat(prototypeDuration).isLessThan(Duration.ofMillis(100).toNanos());
    }
}
```

## 9. Architektur-Integration: Prototype + Factory Pattern

### Factory mit Prototype Backend

```java
@Component
public class ConfigurationFactory {
    private final ConfigurationPrototypeRegistry prototypeRegistry;
    
    // Factory Method Pattern + Prototype Pattern Kombination
    public ServiceConfiguration createConfiguration(EnvironmentType env) {
        return switch (env) {
            case DEVELOPMENT -> prototypeRegistry.createConfiguration("TELEKOM_BASE", 
                config -> config.withEnvironment("DEV").withLogLevel("DEBUG"));
                
            case TESTING -> prototypeRegistry.createConfiguration("TELEKOM_BASE",
                config -> config.withEnvironment("TEST").withLogLevel("INFO"));
                
            case PRODUCTION -> prototypeRegistry.createConfiguration("TELEKOM_BASE",
                config -> config.withEnvironment("PROD").withLogLevel("WARN"));
                
            case MICROSERVICE -> prototypeRegistry.createConfiguration("MICROSERVICE_BASE",
                config -> config.withEnvironment("MICROSERVICE"));
        };
    }
}
```

## Fazit: Prototype Pattern Nutzen

### Wann Prototype Pattern verwenden:
✅ **Expensive Object Creation**: SSL-Validierung, Database-Discovery, Complex Initialization  
✅ **Template-based Configuration**: 90% identisch, 10% variabel  
✅ **Runtime Object Creation**: Dynamic Configuration zur Laufzeit  
✅ **Performance kritisch**: Latenz-sensitive Object Creation  

### Wann NICHT verwenden:
❌ **Simple Objects**: Plain DTOs, Simple Value Objects  
❌ **Always Unique**: Objekte ohne Template-Charakter  
❌ **No Creation Cost**: Triviale Constructor ohne expensive Operations  

### Architektur-Benefit:
- **99%+ Performance-Verbesserung** bei expensive Object Creation
- **Memory-Effizienz** durch intelligent Sharing
- **Template-driven Configuration** für konsistente Settings
- **Loose Coupling** zwischen Object Creation und Business Logic

---

**Nächstes Modul**: Strukturelle Patterns - Adapter Pattern für Legacy-Integration ohne Domain-Verschmutzung