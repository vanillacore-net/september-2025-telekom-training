# Code Quality Checkliste

Eine systematische Checkliste für Code Reviews im Telekom-Umfeld.

## **Pre-Review Checkliste (Entwickler)**

### **Funktionalität**
- [ ] Code erfüllt alle definierten Requirements
- [ ] Alle Edge Cases sind behandelt
- [ ] Error Handling ist vollständig implementiert
- [ ] Eingabevalidierung ist vorhanden
- [ ] Business Logic ist korrekt umgesetzt

### **Tests**
- [ ] Unit Tests vorhanden und erfolgreich
- [ ] Test Coverage mindestens 80%
- [ ] Integration Tests bei API-Änderungen
- [ ] Mock-Objekte korrekt verwendet
- [ ] Test-Daten sind realistisch (Telekom-Szenarien)

### **Code Quality**
- [ ] Keine Code-Duplikate (DRY Prinzip)
- [ ] Funktionen haben maximal 20 Zeilen
- [ ] Klassen haben maximal 300 Zeilen
- [ ] Komplexität (Cyclomatic) unter 10 pro Methode
- [ ] Naming Conventions befolgt

## **Review Checkliste (Reviewer)**

### **SOLID Principles**

#### **Single Responsibility Principle**
- [ ] Jede Klasse hat nur eine Verantwortlichkeit
- [ ] Methoden haben einen klaren, einzelnen Zweck
- [ ] Business Logic ist von Infrastructure getrennt
- [ ] Validation ist in eigene Klassen extrahiert

**Warnsignale:**
```java
// WARNUNG: Zu viele Verantwortlichkeiten
public class TelekomKunde {
    public void save() { /* Datenbankzugriff */ }
    public void sendEmail() { /* Email-Versendung */ }
    public String toJSON() { /* Serialisierung */ }
    public boolean validate() { /* Validation */ }
}
```

#### **Open/Closed Principle**
- [ ] Neue Funktionalität durch Erweiterung, nicht Modifikation
- [ ] Interfaces für verschiedene Implementierungen verwendet
- [ ] Strategy Pattern bei unterschiedlichen Algorithmen
- [ ] Template Method Pattern bei ähnlichen Abläufen

**Warnsignale:**
```java
// WARNUNG: Switch-Statement für Tarife
public BigDecimal berechne(String tariftyp) {
    switch (tariftyp) {
        case "BASIC": return new BigDecimal("29.99");
        // Bei jedem neuen Tarif muss hier Code geändert werden!
    }
}
```

#### **Liskov Substitution Principle**
- [ ] Subklassen können Superklassen vollständig ersetzen
- [ ] Keine stärkeren Preconditions in Subklassen
- [ ] Keine schwächeren Postconditions in Subklassen
- [ ] Exception-Verhalten ist konsistent

**Warnsignale:**
```java
// WARNUNG: Subklasse wirft Exception wo Superklasse keine wirft
public class LaufzeitVertrag extends Vertrag {
    @Override
    public void kuendige() {
        if (inLaufzeit()) {
            throw new Exception("Nicht kündbar"); // LSP Verletzung!
        }
        super.kuendige();
    }
}
```

#### **Interface Segregation Principle**
- [ ] Interfaces sind kohäsiv und focused
- [ ] Keine "fat interfaces" mit vielen Methoden
- [ ] Clients implementieren nur benötigte Methoden
- [ ] Interface-Aufspaltung bei unterschiedlichen Client-Bedürfnissen

**Warnsignale:**
```java
// WARNUNG: Fat Interface
public interface TelekomManager {
    void manageKunden();
    void manageVertraege();
    void manageBilling();
    void manageSupport();  // Zu viele Verantwortlichkeiten
}
```

#### **Dependency Inversion Principle**
- [ ] High-level Module hängen von Abstraktionen ab
- [ ] Dependency Injection wird verwendet
- [ ] Keine direkten new-Aufrufe für Dependencies
- [ ] Testbarkeit durch Mock-Interfaces gegeben

**Warnsignale:**
```java
// WARNUNG: Direkte Abhängigkeit
public class KundenService {
    private MySQLRepository repo = new MySQLRepository(); // Fest verdrahtet!
}
```

### **Clean Code Prinzipien**

#### **Naming**
- [ ] Klassen: Substantive (TelekomKunde, VertragService)
- [ ] Methoden: Verben (berechneMonatsgebuehr, sendeWillkommen)
- [ ] Boolean-Methoden: is/has/can Prefix (istAktiv, hatPremiumStatus)
- [ ] Konstanten: UPPER_SNAKE_CASE
- [ ] Variablen: camelCase und aussagekräftig

**Good Examples:**
```java
public class TelekomVertragService {
    private static final int MAX_VERTRAEGE_PRO_KUNDE = 5;
    
    public boolean istVertragKuendbar(String vertragsnummer) { }
    public BigDecimal berechneMonatsgebuehr(Tarif tarif) { }
}
```

#### **Functions**
- [ ] Funktionen sind klein (< 20 Zeilen)
- [ ] Ein Abstraktionslevel pro Funktion
- [ ] Maximal 3 Parameter (sonst Parameter Object)
- [ ] Keine boolean Parameter (Split in zwei Methoden)
- [ ] Pure Functions wo möglich (keine Seiteneffekte)

**Bad Example:**
```java
// Zu komplex, zu viele Parameter
public void verarbeiteVertrag(String kundennummer, String tarifcode, 
    boolean istNeukunde, boolean sendEmail, boolean createBill, 
    LocalDate startdatum, BigDecimal rabatt) {
    // 50+ Zeilen Code...
}
```

**Good Fix:**
```java
public void verarbeiteVertrag(VertragAnfrage anfrage) {
    validateAnfrage(anfrage);
    Vertrag vertrag = erstelleVertrag(anfrage);
    aktiviereVertrag(vertrag);
    
    if (anfrage.istEmailErwuenscht()) {
        sendeBestaetigung(vertrag);
    }
}
```

#### **Comments**
- [ ] Code ist selbstdokumentierend
- [ ] Kommentare erklären "Warum", nicht "Was"
- [ ] Komplexe Geschäftsregeln sind dokumentiert
- [ ] TODO-Kommentare haben Ticket-Referenz
- [ ] Keine auskommentierter Code

**Good Comments:**
```java
// Wartezeit wegen Telekom API Rate Limiting (max 10 Requests/sec)
Thread.sleep(100);

// Workaround für Legacy System Bug #TEL-4521
// TODO: Entfernen nach System-Upgrade (Ticket TEL-4890)
if (response.getStatus().equals("UNKNOWN")) {
    response.setStatus("SUCCESS");
}
```

### **Architecture & Design**

#### **Layer Separation**
- [ ] Controller enthält nur Request/Response-Handling
- [ ] Service Layer enthält Business Logic
- [ ] Repository Layer nur für Datenzugriff
- [ ] DTOs für externe Schnittstellen
- [ ] Domain Models für Business Logic

**Telekom Example:**
```java
// Controller Layer
@RestController
public class VertragController {
    public ResponseEntity<VertragDto> erstelleVertrag(@RequestBody VertragAnfrageDto anfrage) {
        Vertrag vertrag = vertragService.erstelleVertrag(mapper.toDomain(anfrage));
        return ok(mapper.toDto(vertrag));
    }
}

// Service Layer  
@Service
public class VertragService {
    public Vertrag erstelleVertrag(VertragAnfrage anfrage) {
        // Business Logic
    }
}

// Repository Layer
@Repository
public class VertragRepository {
    public Vertrag save(Vertrag vertrag) {
        // Datenzugriff
    }
}
```

#### **Error Handling**
- [ ] Spezifische Exception-Typen
- [ ] Keine leeren catch-Blocks
- [ ] Proper Exception-Propagation
- [ ] Global Exception Handler für REST APIs
- [ ] Logging bei Exceptions

```java
// Good Exception Handling
public class TelekomVertragService {
    public Vertrag findeVertrag(String nummer) throws VertragNotFoundException {
        return repository.findByNummer(nummer)
            .orElseThrow(() -> new VertragNotFoundException(
                "Vertrag nicht gefunden: " + nummer));
    }
}

@ControllerAdvice
public class TelekomExceptionHandler {
    @ExceptionHandler(VertragNotFoundException.class)
    public ResponseEntity<ErrorDto> handleVertragNotFound(VertragNotFoundException ex) {
        logger.warn("Vertrag nicht gefunden: {}", ex.getMessage());
        return ResponseEntity.status(404)
            .body(new ErrorDto("VERTRAG_NOT_FOUND", ex.getMessage()));
    }
}
```

### **Performance & Security**

#### **Performance**
- [ ] Database Queries sind optimiert (keine N+1 Probleme)
- [ ] Lazy Loading korrekt verwendet
- [ ] Caching wo sinnvoll (Redis/Memory)
- [ ] Connection Pooling konfiguriert
- [ ] Bulk-Operations für große Datenmengen

#### **Security**
- [ ] Input Validation auf allen Ebenen
- [ ] SQL Injection Prevention (PreparedStatements)
- [ ] XSS Prevention (Output Encoding)
- [ ] Authentication/Authorization implementiert
- [ ] Sensitive Daten werden nicht geloggt
- [ ] HTTPS für alle externen Kommunikation

**Telekom Security Example:**
```java
@RestController
public class KundenController {
    
    @PreAuthorize("hasRole('TELEKOM_AGENT') or @kundenService.istEigenerKunde(#kundennummer, authentication.name)")
    public KundeDto getKunde(@PathVariable @Valid @KundennummerFormat String kundennummer) {
        // Autorisierung + Validation
        return kundenService.getKunde(kundennummer);
    }
}
```

## **Tool-basierte Quality Gates**

### **SonarQube Rules**
- [ ] Code Coverage > 80%
- [ ] Duplicated Lines < 3%
- [ ] Maintainability Rating A
- [ ] Reliability Rating A
- [ ] Security Rating A
- [ ] Keine Critical oder Blocker Issues

### **CheckStyle Configuration**
```xml
<!-- Telekom CheckStyle Rules -->
<module name="FileLength">
    <property name="max" value="300"/>
</module>
<module name="MethodLength">
    <property name="max" value="20"/>
</module>
<module name="ParameterNumber">
    <property name="max" value="3"/>
</module>
<module name="CyclomaticComplexity">
    <property name="max" value="10"/>
</module>
```

### **SpotBugs/PMD Integration**
- [ ] Keine High-Priority Bugs
- [ ] Performance-Warnings überprüft
- [ ] Security-Warnings behandelt
- [ ] Unused Code entfernt

## **Review Process**

### **Before Review**
1. **Automated Checks**: CI/CD Pipeline erfolgreich
2. **Self-Review**: Entwickler prüft eigenen Code
3. **Documentation**: README/JavaDoc aktualisiert
4. **Test Results**: Alle Tests green

### **During Review**
1. **Functionality**: Logik und Requirements prüfen
2. **Design**: Architecture und Patterns bewerten
3. **Quality**: Clean Code und SOLID prüfen
4. **Performance**: Bottlenecks identifizieren
5. **Security**: Sicherheitslücken suchen

### **Review Comments Classification**

**CRITICAL**: Muss behoben werden (Security, Funktionalität)
```java
// CRITICAL: SQL Injection möglich
String sql = "SELECT * FROM kunden WHERE id = " + kundennummer; // GEFÄHRLICH!
```

**MAJOR**: Sollte behoben werden (Performance, Maintainability)
```java
// MAJOR: Performance Problem - N+1 Query
for (Vertrag vertrag : vertraege) {
    List<Rechnung> rechnungen = rechnungService.findByVertrag(vertrag.getId());
}
```

**MINOR**: Nice-to-have (Style, Naming)
```java
// MINOR: Besserer Name wäre "berechneMonatsgebuehr"
public BigDecimal calc(Tarif t) { }
```

**SUGGESTION**: Verbesserungsvorschlag
```java
// SUGGESTION: Builder Pattern würde die Lesbarkeit verbessern
Vertrag vertrag = new VertragBuilder()
    .mitKunde(kunde)
    .mitTarif(tarif)
    .mitLaufzeit(24)
    .build();
```

## **Post-Review Actions**

### **Developer Actions**
- [ ] Alle CRITICAL und MAJOR Comments behoben
- [ ] Code erneut getestet
- [ ] Review-Response dokumentiert
- [ ] Neue Tests für Bugfixes geschrieben

### **Reviewer Actions**
- [ ] Fixes überprüft
- [ ] Approval erteilt
- [ ] Lessons Learned dokumentiert
- [ ] Team-Wissen geteilt

## **Quality Metrics Dashboard**

### **Team Metrics**
- Code Coverage Trend
- Review Durchlaufzeit
- Defect Rate nach Deployment
- Technical Debt Ratio
- Performance Benchmarks

### **Individual Metrics**
- Review Participation Rate
- Comment Resolution Time
- Code Quality Score
- Knowledge Sharing Score

## **Continuous Improvement**

### **Retrospective Questions**
- Welche Code-Probleme kommen häufig vor?
- Wo braucht das Team mehr Training?
- Welche Tools können den Review-Process verbessern?
- Wie können wir Quality Gates automatisieren?

### **Team Learning**
- **Code Review Sessions**: Gemeinsame Review-Runden
- **Best Practice Sharing**: Gute Lösungen dokumentieren
- **Architecture Decision Records**: Wichtige Entscheidungen festhalten
- **Internal Tech Talks**: Wissen innerhalb Team teilen