# Java Projekt Setup - Telekom Architecture Training

## Überblick

Dieses Multi-Module Maven Projekt organisiert die Code-Beispiele für das 4-tägige Architecture Training. Jeder Tag hat sein eigenes Maven-Modul mit einer konsistenten Struktur und gemeinsamen Abhängigkeiten.

## Projektstruktur

```
examples/
├── pom.xml                     # Parent POM
├── checkstyle.xml              # Checkstyle Konfiguration
├── .gitignore                  # Git Ignorierungsregeln
├── README-SETUP.md             # Diese Datei
├── day1-examples/              # Creational Patterns
│   ├── pom.xml
│   └── src/main/java/
│   └── src/test/java/
├── day2-examples/              # Structural Patterns
│   ├── pom.xml
│   └── src/main/java/
│   └── src/test/java/
├── day3-examples/              # Behavioral Patterns
│   ├── pom.xml
│   └── src/main/java/
│   └── src/test/java/
└── day4-examples/              # Advanced Patterns
    ├── pom.xml
    └── src/main/java/
    └── src/test/java/
```

## Voraussetzungen

### Software-Anforderungen
- **Java 11+** (getestet mit Java 11, 17, und 21)
- **Maven 3.6.3+**
- **IDE**: IntelliJ IDEA oder Eclipse (optional)

### Installation prüfen
```bash
# Java Version prüfen
java -version

# Maven Version prüfen
mvn -version
```

## Schnellstart

### 1. Projekt kompilieren und testen
```bash
# In das examples/ Verzeichnis wechseln
cd examples/

# Alle Module kompilieren und testen
mvn clean install
```

### 2. Einzelne Module
```bash
# Nur Tag 1 Beispiele kompilieren
mvn clean compile -pl day1-examples

# Nur Tag 2 Tests ausführen
mvn test -pl day2-examples

# Bestimmtes Modul kompilieren und testen
mvn clean install -pl day3-examples
```

### 3. Code-Style prüfen
```bash
# Checkstyle für alle Module
mvn checkstyle:check

# Checkstyle nur für ein Modul
mvn checkstyle:check -pl day1-examples
```

## Maven Konfiguration

### Parent POM Features
- **Java 11 Kompatibilität**: Compiler und Runtime konfiguriert
- **Dependency Management**: Zentrale Verwaltung aller Abhängigkeiten
- **Plugin Management**: Einheitliche Plugin-Konfiguration
- **Code Quality**: Checkstyle Integration
- **Testing**: JUnit 5 und Mockito vorkonfiguriert

### Wichtige Abhängigkeiten
- **JUnit 5** (`5.10.0`): Modernes Testing Framework
- **Mockito** (`5.5.0`): Mocking Framework für Unit Tests
- **AssertJ** (`3.24.2`): Fluent Assertions für bessere Test-Lesbarkeit

### Maven Plugins
- **Compiler Plugin**: Java 11+ Kompilierung
- **Surefire Plugin**: Unit Test Ausführung
- **Failsafe Plugin**: Integration Tests
- **Checkstyle Plugin**: Code-Style Überprüfung

## IDE Setup

### IntelliJ IDEA
1. **Projekt öffnen**: `File > Open` → `examples/` Verzeichnis wählen
2. **Maven Import**: IntelliJ erkennt automatisch die Maven-Struktur
3. **Java SDK**: Project Structure → Project → Project SDK auf Java 11+ setzen
4. **Checkstyle Plugin**:
   - `File > Settings > Plugins` → "CheckStyle-IDEA" installieren
   - `File > Settings > Tools > Checkstyle` → Konfigurationsdatei hinzufügen: `checkstyle.xml`

### Eclipse
1. **Projekt importieren**: `File > Import > Existing Maven Projects`
2. **Root Directory**: `examples/` Verzeichnis wählen
3. **Java Build Path**: Rechtsklick auf Projekt → Properties → Java Build Path → Java 11+ einstellen
4. **Checkstyle Plugin**:
   - Eclipse Marketplace → "Checkstyle Plugin" installieren
   - Project Properties → Checkstyle → `checkstyle.xml` konfigurieren

## Code-Style Standards

### Checkstyle Regeln
Die Checkstyle-Konfiguration basiert auf bewährten Java-Standards mit Telekom-spezifischen Anpassungen:

- **Zeilenlänge**: Maximal 120 Zeichen
- **Einrückung**: 4 Leerzeichen (keine Tabs)
- **Import-Organisation**: Gruppierung nach java.*, javax.*, org.*, com.*
- **Javadoc**: Erforderlich für public Klassen und Methoden
- **Naming Conventions**: CamelCase für Klassen, camelCase für Methoden/Variablen

### Code-Qualität Metriken
- **Zyklomatische Komplexität**: Max. 15 pro Methode
- **Methodenlänge**: Max. 150 Zeilen
- **Klassengröße**: Max. 1500 NCSS
- **Parameter-Anzahl**: Max. 8 Parameter pro Methode

## Testing

### Unit Tests mit JUnit 5
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

class ExampleTest {
    
    @Test
    @DisplayName("Should demonstrate basic test setup")
    void shouldDemonstrateBasicTest() {
        // Given
        String expected = "Hello World";
        
        // When
        String actual = "Hello World";
        
        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
```

### Mocking mit Mockito
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MockingExampleTest {
    
    @Mock
    private SomeService mockService;
    
    @Test
    void shouldDemonstrateMocking() {
        // Given
        when(mockService.getValue()).thenReturn("mocked value");
        
        // When
        String result = mockService.getValue();
        
        // Then
        assertThat(result).isEqualTo("mocked value");
    }
}
```

## Nützliche Maven Commands

### Development Workflow
```bash
# Kompilieren ohne Tests
mvn compile

# Nur Tests ausführen
mvn test

# Tests überspringen
mvn install -DskipTests

# Bestimmte Tests ausführen
mvn test -Dtest=ExampleTest

# Clean Build
mvn clean compile test
```

### Code Quality
```bash
# Checkstyle Report generieren
mvn checkstyle:checkstyle

# Site mit allen Reports generieren
mvn site

# Dependency Tree anzeigen
mvn dependency:tree
```

### Debugging
```bash
# Debug-Informationen anzeigen
mvn compile -X

# Maven Help Plugin
mvn help:effective-pom
mvn help:active-profiles
```

## Fehlerbehebung

### Häufige Probleme

**Problem**: `Java version not compatible`
**Lösung**: 
```bash
# Java Version prüfen
java -version
# JAVA_HOME setzen
export JAVA_HOME=/path/to/java11+
```

**Problem**: `Tests fail with ClassNotFoundException`
**Lösung**: 
```bash
# Dependencies aktualisieren
mvn dependency:resolve
# Clean Build
mvn clean install
```

**Problem**: `Checkstyle violations`
**Lösung**: 
```bash
# Report anzeigen
mvn checkstyle:check
# Violations in target/checkstyle-result.xml prüfen
```

### IDE-spezifische Probleme

**IntelliJ**: Project SDK nicht korrekt
- File → Project Structure → Project → Project SDK auf Java 11+ setzen

**Eclipse**: Build Path Errors
- Rechtsklick auf Projekt → Maven → Reload Projects

## Erweiterte Konfiguration

### Profile für verschiedene Umgebungen
```xml
<!-- In pom.xml Profile hinzufügen -->
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <checkstyle.skip>true</checkstyle.skip>
        </properties>
    </profile>
</profiles>
```

### Verwendung:
```bash
mvn install -Pdev
```

## Support

Bei Fragen oder Problemen:
1. **Dokumentation prüfen**: Diese README und Maven/Java Dokumentation
2. **Maven Logs analysieren**: `mvn -X` für verbose Ausgabe
3. **IDE Logs prüfen**: Console/Error Logs in der IDE
4. **Training Team kontaktieren**: Für spezifische Fragen zum Setup

## Lizenz

Dieses Projekt ist für interne Telekom Trainings konzipiert und unterliegt den entsprechenden Unternehmensrichtlinien.