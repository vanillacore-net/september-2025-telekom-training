# SOLID Principles

Die SOLID Prinzipien sind fundamentale Designprinzipien für objektorientierte Software-Entwicklung.

## **S - Single Responsibility Principle (SRP)**

Eine Klasse sollte nur einen Grund haben, sich zu ändern.

### **Telekom Beispiel - Violation:**
```java
// Diese Klasse hat zu viele Verantwortlichkeiten
public class TelekomKunde {
    private String kundennummer;
    private String name;
    private String email;
    
    // Geschäftslogik
    public boolean istPremiumKunde() {
        return getRechnungsbetragLetztesMonate() > 100;
    }
    
    // Datenbankzugriff - FALSCH!
    public void save() {
        Connection conn = DriverManager.getConnection(/*...*/);
        // SQL Insert/Update
    }
    
    // Email-Versendung - FALSCH!
    public void sendeWillkommensEmail() {
        EmailService.send(this.email, "Willkommen bei Telekom");
    }
    
    // Formatierung - FALSCH!
    public String toJSON() {
        return "{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}";
    }
}
```

### **Fix mit Pattern-Trennung:**
```java
// Domain Model - nur Geschäftslogik
public class TelekomKunde {
    private final String kundennummer;
    private final String name;
    private final String email;
    private final BigDecimal rechnungsbetrag;
    
    public boolean istPremiumKunde() {
        return rechnungsbetrag.compareTo(new BigDecimal("100")) > 0;
    }
}

// Repository Pattern - Datenzugriff
public interface KundenRepository {
    void save(TelekomKunde kunde);
    TelekomKunde findByKundennummer(String nummer);
}

// Service Layer - Koordination
public class KundenService {
    private final KundenRepository repository;
    private final EmailService emailService;
    
    public void registriereNeukunde(TelekomKunde kunde) {
        repository.save(kunde);
        emailService.sendeWillkommen(kunde);
    }
}

// Formatter - Darstellung
public class KundenJsonFormatter {
    public String format(TelekomKunde kunde) {
        return new JsonBuilder()
            .add("name", kunde.getName())
            .add("email", kunde.getEmail())
            .build();
    }
}
```

## **O - Open/Closed Principle (OCP)**

Software-Entitäten sollten offen für Erweiterung, aber geschlossen für Modifikation sein.

### **Telekom Beispiel - Violation:**
```java
// Bei jedem neuen Tariftyp muss diese Klasse geändert werden
public class TarifRechner {
    public BigDecimal berechne(String tariftyp, int datenvolumen) {
        switch (tariftyp) {
            case "BASIC":
                return new BigDecimal("29.99");
            case "PREMIUM":
                return new BigDecimal("49.99");
            case "UNLIMITED":
                return new BigDecimal("79.99");
            // Neue Tarife erfordern Code-Änderung hier!
            default:
                throw new IllegalArgumentException("Unbekannter Tarif");
        }
    }
}
```

### **Fix mit Strategy Pattern:**
```java
// Interface für Erweiterbarkeit
public interface TarifStrategy {
    BigDecimal berechneMonatsgebuehr(int datenvolumen);
    String getTarifName();
}

// Konkrete Implementierungen
public class BasicTarif implements TarifStrategy {
    @Override
    public BigDecimal berechneMonatsgebuehr(int datenvolumen) {
        return new BigDecimal("29.99");
    }
    
    @Override
    public String getTarifName() {
        return "BASIC";
    }
}

public class PremiumTarif implements TarifStrategy {
    @Override
    public BigDecimal berechneMonatsgebuehr(int datenvolumen) {
        BigDecimal grundgebuehr = new BigDecimal("49.99");
        if (datenvolumen > 20) {
            BigDecimal zusatzkosten = new BigDecimal(datenvolumen - 20)
                .multiply(new BigDecimal("0.50"));
            return grundgebuehr.add(zusatzkosten);
        }
        return grundgebuehr;
    }
    
    @Override
    public String getTarifName() {
        return "PREMIUM";
    }
}

// Rechner ist geschlossen für Modifikation
public class TelekomTarifRechner {
    private final Map<String, TarifStrategy> tarife;
    
    public TelekomTarifRechner(List<TarifStrategy> tarifStrategies) {
        this.tarife = tarifStrategies.stream()
            .collect(Collectors.toMap(
                TarifStrategy::getTarifName,
                strategy -> strategy
            ));
    }
    
    public BigDecimal berechne(String tariftyp, int datenvolumen) {
        TarifStrategy strategy = tarife.get(tariftyp);
        if (strategy == null) {
            throw new IllegalArgumentException("Unbekannter Tarif: " + tariftyp);
        }
        return strategy.berechneMonatsgebuehr(datenvolumen);
    }
}

// Neue Tarife durch einfache Erweiterung - keine Modifikation nötig
public class UnlimitedTarif implements TarifStrategy {
    @Override
    public BigDecimal berechneMonatsgebuehr(int datenvolumen) {
        return new BigDecimal("79.99"); // Flatrate
    }
    
    @Override
    public String getTarifName() {
        return "UNLIMITED";
    }
}
```

## **L - Liskov Substitution Principle (LSP)**

Objekte einer Superklasse sollten durch Objekte ihrer Subklassen ersetzbar sein, ohne die Korrektheit des Programms zu beeinträchtigen.

### **Telekom Beispiel - Violation:**
```java
// Basis-Klasse
public class TelekomVertrag {
    protected BigDecimal grundgebuehr;
    
    public BigDecimal berechneMonatskosten() {
        return grundgebuehr;
    }
    
    public void kuendige() {
        // Standard Kündigungsverfahren
        setStatus("GEKUENDIGT");
    }
}

// Verletzt LSP - Subklasse wirft Exception wo Superklasse keine wirft
public class LaufzeitVertrag extends TelekomVertrag {
    private LocalDate vertragsende;
    
    @Override
    public void kuendige() {
        if (LocalDate.now().isBefore(vertragsende)) {
            // VIOLATION: Superklasse würde keine Exception werfen!
            throw new VorzeitigeKuendigungException("Vertrag läuft noch bis " + vertragsende);
        }
        super.kuendige();
    }
}
```

### **Fix mit korrektem Verhalten:**
```java
// Basis-Klasse definiert Contract
public abstract class TelekomVertrag {
    protected BigDecimal grundgebuehr;
    protected String status;
    
    public BigDecimal berechneMonatskosten() {
        return grundgebuehr;
    }
    
    // Template Method - definiert den Algorithmus
    public final KuendigungResult kuendige() {
        if (!istKuendigbar()) {
            return KuendigungResult.failure("Kündigung nicht möglich");
        }
        
        fuehreKuendigungDurch();
        return KuendigungResult.success();
    }
    
    protected abstract boolean istKuendigbar();
    protected abstract void fuehreKuendigungDurch();
}

// LSP-konforme Implementierung
public class LaufzeitVertrag extends TelekomVertrag {
    private LocalDate vertragsende;
    
    @Override
    protected boolean istKuendigbar() {
        return LocalDate.now().isAfter(vertragsende) || 
               LocalDate.now().isEqual(vertragsende);
    }
    
    @Override
    protected void fuehreKuendigungDurch() {
        this.status = "GEKUENDIGT";
        // Spezifische Laufzeitvertrag-Logik
    }
}

public class FlexVertrag extends TelekomVertrag {
    @Override
    protected boolean istKuendigbar() {
        return true; // Jederzeit kündbar
    }
    
    @Override
    protected void fuehreKuendigungDurch() {
        this.status = "GEKUENDIGT";
        // Spezifische Flex-Vertrag-Logik
    }
}

// Result-Objekt für sichere Rückgabe
public class KuendigungResult {
    private final boolean success;
    private final String message;
    
    public static KuendigungResult success() {
        return new KuendigungResult(true, null);
    }
    
    public static KuendigungResult failure(String message) {
        return new KuendigungResult(false, message);
    }
    
    // Getters und private Constructor
}
```

## **I - Interface Segregation Principle (ISP)**

Clients sollten nicht gezwungen werden, von Interfaces abzuhängen, die sie nicht verwenden.

### **Telekom Beispiel - Violation:**
```java
// Fat Interface - zu viele Verantwortlichkeiten
public interface TelekomKundenManager {
    // Kundendaten
    void updateKundendaten(String kundennummer, Kundendaten daten);
    Kunde getKunde(String kundennummer);
    
    // Vertragsmanagement  
    void erstelleVertrag(String kundennummer, VertragsDaten daten);
    void kuendigeVertrag(String vertragsnummer);
    
    // Rechnungsstellung
    void erstelleRechnung(String kundennummer);
    List<Rechnung> getRechnungen(String kundennummer);
    
    // Support
    void erstelleSupportTicket(String kundennummer, String problem);
    void schliessSupportTicket(String ticketId);
    
    // Analytics - nicht alle Clients brauchen das!
    Map<String, Object> getKundenAnalytics(String kundennummer);
    List<String> getChurnRiskKunden();
}
```

### **Fix mit Interface Segregation:**
```java
// Getrennte, kohäsive Interfaces
public interface KundenDatenManager {
    void updateKundendaten(String kundennummer, Kundendaten daten);
    Kunde getKunde(String kundennummer);
}

public interface VertragsManager {
    void erstelleVertrag(String kundennummer, VertragsDaten daten);
    void kuendigeVertrag(String vertragsnummer);
}

public interface RechnungsManager {
    void erstelleRechnung(String kundennummer);
    List<Rechnung> getRechnungen(String kundennummer);
}

public interface SupportManager {
    void erstelleSupportTicket(String kundennummer, String problem);
    void schliessSupportTicket(String ticketId);
}

public interface KundenAnalytics {
    Map<String, Object> getKundenAnalytics(String kundennummer);
    List<String> getChurnRiskKunden();
}

// Implementierung kombiniert nur benötigte Interfaces
public class StandardKundenService 
    implements KundenDatenManager, VertragsManager, RechnungsManager {
    // Implementiert nur die für Standard-Kunden nötigen Funktionen
}

public class AnalyticsKundenService 
    implements KundenDatenManager, KundenAnalytics {
    // Implementiert nur Kundendaten + Analytics
}

// Clients hängen nur von dem ab, was sie brauchen
public class KundenPortalController {
    private final KundenDatenManager kundenManager;
    private final RechnungsManager rechnungsManager;
    
    // Braucht kein Support oder Analytics Interface!
    public KundenPortalController(KundenDatenManager kundenManager, 
                                 RechnungsManager rechnungsManager) {
        this.kundenManager = kundenManager;
        this.rechnungsManager = rechnungsManager;
    }
}
```

## **D - Dependency Inversion Principle (DIP)**

1. High-level Module sollten nicht von Low-level Modulen abhängen. Beide sollten von Abstraktionen abhängen.
2. Abstraktionen sollten nicht von Details abhängen. Details sollten von Abstraktionen abhängen.

### **Telekom Beispiel - Violation:**
```java
// High-level Modul hängt direkt von Low-level Implementierung ab
public class TelekomKundenService {
    private MySQLKundenRepository repository; // Direkte Abhängigkeit!
    private SMTPEmailService emailService;   // Direkte Abhängigkeit!
    
    public TelekomKundenService() {
        // Konkrete Implementierungen hart verdrahtet
        this.repository = new MySQLKundenRepository();
        this.emailService = new SMTPEmailService();
    }
    
    public void registriereNeukunde(Kunde kunde) {
        // Validierung
        if (kunde.getEmail() == null) {
            throw new IllegalArgumentException("Email erforderlich");
        }
        
        // Speicherung - fest verdrahtet mit MySQL
        repository.save(kunde);
        
        // Email - fest verdrahtet mit SMTP
        emailService.send(kunde.getEmail(), "Willkommen bei Telekom");
    }
}

// Low-level Module
public class MySQLKundenRepository {
    public void save(Kunde kunde) {
        // MySQL spezifische Implementierung
    }
}

public class SMTPEmailService {
    public void send(String email, String message) {
        // SMTP spezifische Implementierung
    }
}
```

### **Fix mit Dependency Injection:**
```java
// Abstraktionen definieren
public interface KundenRepository {
    void save(Kunde kunde);
    Optional<Kunde> findByEmail(String email);
}

public interface EmailService {
    void sendWelcomeMessage(String email, String kundenname);
    void sendNotification(String email, String subject, String message);
}

// High-level Modul hängt nur von Abstraktionen ab
public class TelekomKundenService {
    private final KundenRepository repository;
    private final EmailService emailService;
    
    // Dependency Injection über Constructor
    public TelekomKundenService(KundenRepository repository, 
                               EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }
    
    public void registriereNeukunde(Kunde kunde) {
        validateKunde(kunde);
        
        // Arbeitet mit Abstraktionen
        repository.save(kunde);
        emailService.sendWelcomeMessage(kunde.getEmail(), kunde.getName());
    }
    
    private void validateKunde(Kunde kunde) {
        if (kunde.getEmail() == null || kunde.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email erforderlich");
        }
    }
}

// Low-level Module implementieren Abstraktionen
@Repository
public class MySQLKundenRepository implements KundenRepository {
    @Override
    public void save(Kunde kunde) {
        // MySQL spezifische Implementierung
    }
    
    @Override
    public Optional<Kunde> findByEmail(String email) {
        // MySQL Query
        return Optional.empty();
    }
}

@Service
public class TelekomEmailService implements EmailService {
    private final SMTPTemplate smtpTemplate;
    
    @Override
    public void sendWelcomeMessage(String email, String kundenname) {
        String subject = "Willkommen bei Telekom, " + kundenname;
        String template = loadWelcomeTemplate();
        String message = template.replace("{NAME}", kundenname);
        
        smtpTemplate.send(email, subject, message);
    }
    
    @Override
    public void sendNotification(String email, String subject, String message) {
        smtpTemplate.send(email, subject, message);
    }
    
    private String loadWelcomeTemplate() {
        // Template laden
        return "Hallo {NAME}, willkommen bei Telekom!";
    }
}

// Configuration für Dependency Injection
@Configuration
public class TelekomConfiguration {
    
    @Bean
    public KundenRepository kundenRepository() {
        return new MySQLKundenRepository();
    }
    
    @Bean
    public EmailService emailService() {
        return new TelekomEmailService();
    }
    
    @Bean
    public TelekomKundenService kundenService(KundenRepository repository, 
                                             EmailService emailService) {
        return new TelekomKundenService(repository, emailService);
    }
}
```

### **Testbarkeit durch DIP:**
```java
// Unit Test wird einfach durch Mocking
@ExtendWith(MockitoExtension.class)
public class TelekomKundenServiceTest {
    
    @Mock
    private KundenRepository repository;
    
    @Mock
    private EmailService emailService;
    
    private TelekomKundenService service;
    
    @BeforeEach
    void setUp() {
        service = new TelekomKundenService(repository, emailService);
    }
    
    @Test
    void registriereNeukunde_SendsWelcomeEmail() {
        // Given
        Kunde kunde = new Kunde("Max Mustermann", "max@example.com");
        
        // When
        service.registriereNeukunde(kunde);
        
        // Then
        verify(repository).save(kunde);
        verify(emailService).sendWelcomeMessage("max@example.com", "Max Mustermann");
    }
    
    @Test
    void registriereNeukunde_ThrowsExceptionForInvalidEmail() {
        // Given
        Kunde kunde = new Kunde("Max Mustermann", null);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> service.registriereNeukunde(kunde));
        
        verify(repository, never()).save(any());
        verify(emailService, never()).sendWelcomeMessage(any(), any());
    }
}
```

## **Pattern-Integration**

SOLID Prinzipien führen natürlich zu bewährten Design Patterns:

- **SRP** → **Command Pattern**, **Strategy Pattern**
- **OCP** → **Strategy Pattern**, **Template Method Pattern**, **Decorator Pattern**
- **LSP** → **Template Method Pattern**, **Factory Pattern**
- **ISP** → **Adapter Pattern**, **Facade Pattern**
- **DIP** → **Dependency Injection**, **Factory Pattern**, **Abstract Factory**

## **Tool-Support für SOLID**

### **Code-Analyse**
- **SonarQube**: Erkennt SOLID-Verletzungen automatisch
- **ArchUnit**: Architektur-Tests für Dependency Rules
- **SpotBugs**: Findet problematische Abhängigkeiten

### **Dependency Injection Frameworks**
- **Spring Framework**: @Autowired, @Component, @Service
- **Google Guice**: Leichtgewichtige DI
- **CDI**: Java EE Standard

### **Testing**
- **Mockito**: Mock-Framework für DIP-basierte Tests
- **TestNG/JUnit**: Unit Tests mit Dependency Injection

## **Telekom Best Practices**

### **Service Layer Pattern**
```java
@Service
public class TelekomVertragService {
    private final VertragRepository repository;
    private final KundenService kundenService;
    private final TarifService tarifService;
    
    // Constructor Injection
    public TelekomVertragService(VertragRepository repository,
                                KundenService kundenService,
                                TarifService tarifService) {
        this.repository = repository;
        this.kundenService = kundenService;  
        this.tarifService = tarifService;
    }
    
    @Transactional
    public Vertrag erstelleVertrag(VertragAnfrage anfrage) {
        // Single Responsibility: nur Koordination
        Kunde kunde = kundenService.validateKunde(anfrage.getKundennummer());
        Tarif tarif = tarifService.findTarif(anfrage.getTarifCode());
        
        Vertrag vertrag = new VertragFactory().erstelle(kunde, tarif);
        return repository.save(vertrag);
    }
}
```

### **Repository Pattern mit SOLID**
```java
// Interface Segregation
public interface VertragRepository extends BasicRepository<Vertrag> {
    // Basis-Operationen
}

public interface VertragQueryRepository {
    List<Vertrag> findAktiveVertraege(String kundennummer);
    List<Vertrag> findAuslaufendeVertraege(LocalDate stichtag);
}

// Implementierung kombiniert Interfaces
@Repository
public class JpaVertragRepository 
    implements VertragRepository, VertragQueryRepository {
    // Implementierung
}
```