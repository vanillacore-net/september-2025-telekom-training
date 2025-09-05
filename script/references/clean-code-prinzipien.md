# Clean Code Prinzipien

## Grundlegende Clean Code Prinzipien

### **DRY (Don't Repeat Yourself)**

Jeder Wissensbereich sollte eine einzige, eindeutige und autoritäre Repräsentation im System haben.

**Beispiel - Violation:**
```java
// Telekom Kundenservice - schlecht
public class TelekomKundenService {
    public void aktiviereVertrag(String kundennummer) {
        // Kundendaten validieren
        if (kundennummer == null || kundennummer.trim().isEmpty() 
            || !kundennummer.matches("\\d{10}")) {
            throw new IllegalArgumentException("Ungültige Kundennummer");
        }
        // ... Aktivierung
    }
    
    public void kuendigeVertrag(String kundennummer) {
        // Kundendaten validieren - DUPLIKAT!
        if (kundennummer == null || kundennummer.trim().isEmpty() 
            || !kundennummer.matches("\\d{10}")) {
            throw new IllegalArgumentException("Ungültige Kundennummer");
        }
        // ... Kündigung
    }
}
```

**Fix mit Pattern:**
```java
public class TelekomKundenService {
    private final KundenValidator validator;
    
    public void aktiviereVertrag(String kundennummer) {
        validator.validateKundennummer(kundennummer);
        // ... Aktivierung
    }
    
    public void kuendigeVertrag(String kundennummer) {
        validator.validateKundennummer(kundennummer);
        // ... Kündigung
    }
}

// Strategy Pattern für Validation
public class KundenValidator {
    public void validateKundennummer(String kundennummer) {
        if (kundennummer == null || kundennummer.trim().isEmpty() 
            || !kundennummer.matches("\\d{10}")) {
            throw new IllegalArgumentException("Ungültige Kundennummer");
        }
    }
}
```

### **KISS (Keep It Simple, Stupid)**

Wähle die einfachste Lösung, die funktioniert. Komplexität nur dort, wo wirklich nötig.

**Beispiel - Violation:**
```java
// Überkompliziert für einfache Telekom Tarifberechnung
public class TarifberechnungFactory {
    public ITarifCalculator createCalculator(String tariftyp) {
        return TarifCalculatorBuilder.newBuilder()
                .withType(TarifType.fromString(tariftyp))
                .withValidation(new TarifValidationStrategy())
                .withLogger(LoggerFactory.getLogger(this.getClass()))
                .build();
    }
}
```

**Fix:**
```java
// Einfach und klar
public class TarifRechner {
    public BigDecimal berechneMonatsgebuehr(String tariftyp, int datenvolumen) {
        switch (tariftyp.toUpperCase()) {
            case "BASIC": return new BigDecimal("29.99");
            case "PREMIUM": return new BigDecimal("49.99");
            case "UNLIMITED": return new BigDecimal("79.99");
            default: throw new IllegalArgumentException("Unbekannter Tarif: " + tariftyp);
        }
    }
}
```

### **YAGNI (You Aren't Gonna Need It)**

Implementiere Features erst, wenn sie tatsächlich benötigt werden.

**Beispiel - Violation:**
```java
// Premature Optimization für Telekom Billing
public class RechnungsGenerator {
    // Niemand hat nach XML Export gefragt!
    public void exportToXML(Rechnung rechnung) { /* nicht implementiert */ }
    public void exportToJSON(Rechnung rechnung) { /* nicht implementiert */ }
    public void exportToCSV(Rechnung rechnung) { /* nicht implementiert */ }
    
    // Aktuell nur PDF benötigt
    public void exportToPDF(Rechnung rechnung) {
        // Implementierung
    }
}
```

**Fix:**
```java
public class RechnungsGenerator {
    // Nur das implementieren, was aktuell gebraucht wird
    public void exportToPDF(Rechnung rechnung) {
        // Implementierung
    }
    
    // Weitere Formate werden hinzugefügt, wenn Bedarf entsteht
}
```

## **Naming Conventions**

### **Klassen und Interfaces**

**Good:**
```java
// Klar und selbsterklärend
public class TelekomVertragService { }
public class KundenDatenRepository { }
public interface RechnungsExporter { }
```

**Bad:**
```java
// Vage und missverständlich
public class Manager { }
public class Helper { }
public class Util { }
```

### **Methoden**

**Good:**
```java
public boolean istVertragAktiv(String vertragsnummer) { }
public void sendeWillkommensEmail(String email) { }
public BigDecimal berechneMonatsgebuehr(Tarif tarif) { }
```

**Bad:**
```java
public boolean check(String s) { }
public void process() { }
public BigDecimal calc(Object o) { }
```

## **Funktionen und Methoden**

### **Single Responsibility**

Jede Funktion sollte genau eine Aufgabe erfüllen.

**Violation:**
```java
// Macht zu viel auf einmal
public void verarbeiteNeukunde(Kunde kunde) {
    // 1. Validierung
    validateKunde(kunde);
    
    // 2. Speicherung
    kundenRepository.save(kunde);
    
    // 3. Email versenden
    emailService.sendeWillkommen(kunde.getEmail());
    
    // 4. Vertrag erstellen
    vertragService.erstelleStandardVertrag(kunde);
    
    // 5. Logging
    logger.info("Neukunde verarbeitet: " + kunde.getKundennummer());
}
```

**Fix mit Command Pattern:**
```java
public class NeukundenService {
    public void verarbeiteNeukunde(Kunde kunde) {
        List<Command> commands = Arrays.asList(
            new ValidateKundeCommand(kunde),
            new SaveKundeCommand(kunde),
            new SendWelcomeEmailCommand(kunde),
            new CreateContractCommand(kunde),
            new LogProcessingCommand(kunde)
        );
        
        commandProcessor.execute(commands);
    }
}
```

### **Parameter Anzahl**

Halte die Anzahl der Parameter niedrig (idealerweise ≤ 3).

**Violation:**
```java
public void erstelleVertrag(String kundennummer, String vorname, 
    String nachname, String email, String telefon, String strasse, 
    String hausnummer, String plz, String ort, String tariftyp) {
    // Zu viele Parameter!
}
```

**Fix mit Builder Pattern:**
```java
public class VertragBuilder {
    public static VertragBuilder fuerKunde(String kundennummer) {
        return new VertragBuilder(kundennummer);
    }
    
    public VertragBuilder mitPersonalien(Personalien personalien) { /* */ }
    public VertragBuilder mitAdresse(Adresse adresse) { /* */ }
    public VertragBuilder mitTarif(String tariftyp) { /* */ }
    
    public Vertrag erstelle() { /* */ }
}

// Verwendung:
Vertrag vertrag = VertragBuilder.fuerKunde("1234567890")
    .mitPersonalien(personalien)
    .mitAdresse(adresse)
    .mitTarif("PREMIUM")
    .erstelle();
```

## **Code Kommentare**

### **Wann kommentieren**

**Good - Intention erklären:**
```java
// Wartezeit für Telekom API Rate Limiting
Thread.sleep(1000);

// Workaround für Legacy System Bug #TEL-4521
if (vertrag.getStatus().equals("UNKNOWN")) {
    vertrag.setStatus("AKTIV");
}
```

**Bad - Offensichtliches kommentieren:**
```java
// Increment i by 1
i++;

// Set name to "Telekom"
company.setName("Telekom");
```

## **Error Handling**

### **Spezifische Exceptions**

**Good:**
```java
public class KundenService {
    public Kunde findeKunde(String kundennummer) throws KundeNotFoundException {
        Kunde kunde = repository.findByKundennummer(kundennummer);
        if (kunde == null) {
            throw new KundeNotFoundException("Kunde nicht gefunden: " + kundennummer);
        }
        return kunde;
    }
}

public class KundeNotFoundException extends Exception {
    public KundeNotFoundException(String message) {
        super(message);
    }
}
```

**Bad:**
```java
public Kunde findeKunde(String kundennummer) {
    Kunde kunde = repository.findByKundennummer(kundennummer);
    if (kunde == null) {
        throw new RuntimeException("Error"); // Viel zu unspezifisch!
    }
    return kunde;
}
```

## **Tool-Support**

### **Statische Code-Analyse**
- **SonarQube**: Automatische Clean Code Regel-Prüfung
- **SpotBugs**: Findet potentielle Bugs
- **PMD**: Code Quality Rules
- **Checkstyle**: Formatierung und Konventionen

### **IDE-Plugins**
- **IntelliJ IDEA**: Built-in Code Inspections
- **Eclipse**: FindBugs Plugin
- **VS Code**: SonarLint Extension

### **Build-Integration**
```xml
<!-- Maven Plugin für kontinuierliche Qualitätsprüfung -->
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>
```

## **Pattern-Bezug**

Clean Code Prinzipien arbeiten Hand in Hand mit Design Patterns:

- **DRY** wird durch **Template Method Pattern** umgesetzt
- **KISS** führt zu **Strategy Pattern** statt komplexer Vererbung
- **Single Responsibility** wird durch **Command Pattern** erreicht
- **Parameter Reduction** nutzt **Builder Pattern**

## **Telekom-spezifische Anwendung**

### **Kundennummern-Validation**
```java
// Clean und wiederverwendbar
public class TelekomKundennummer {
    private static final Pattern VALID_PATTERN = Pattern.compile("\\d{10}");
    private final String nummer;
    
    public TelekomKundennummer(String nummer) {
        if (!isValid(nummer)) {
            throw new IllegalArgumentException("Ungültige Telekom Kundennummer: " + nummer);
        }
        this.nummer = nummer;
    }
    
    private boolean isValid(String nummer) {
        return nummer != null && VALID_PATTERN.matcher(nummer).matches();
    }
}
```

### **Tarif-Berechnung**
```java
// Klare Struktur für komplexe Geschäftslogik
public class TelekomTarifrechner {
    private final Map<String, TarifStrategy> tarifeStrategies;
    
    public BigDecimal berechneMonatskosten(String tariftyp, Verbrauch verbrauch) {
        TarifStrategy strategy = tarifeStrategies.get(tariftyp);
        if (strategy == null) {
            throw new UnbekanterTarifException("Unbekannter Tarif: " + tariftyp);
        }
        return strategy.berechne(verbrauch);
    }
}
```