# Refactoring-Checkliste - Telekom Workshop

## 🎯 Zweck
Praktische Checkliste für systematisches Refactoring im Telekom-Kontext. Für tägliche Anwendung und Workshop-Aktivitäten optimiert.

---

## 📋 PRE-REFACTORING CHECKLIST

### ☑️ Sicherheits-Check (MANDATORY)
- [ ] **Tests vorhanden**: >70% Code Coverage
- [ ] **CI/CD Pipeline**: Läuft grün
- [ ] **Feature Branch**: Separate Branch für Refactoring
- [ ] **Backup**: Git Stash oder Commit vor Beginn
- [ ] **Zeit eingeplant**: Mindestens 2h am Stück
- [ ] **Team informiert**: Pair/Mob Programming koordiniert

### ☑️ Qualitäts-Baseline erfassen
- [ ] **SonarQube Scan**: Aktuelle Metriken dokumentiert
- [ ] **Performance Baseline**: Laufzeiten gemessen
- [ ] **Error Rate**: Aktuelle Bug-Rate erfasst
- [ ] **Test Suite Duration**: Ausführungszeit notiert

### ☑️ Scope Definition
- [ ] **Ein Code Smell**: Nicht mehrere gleichzeitig
- [ ] **Klare Boundaries**: Was wird NICHT geändert
- [ ] **Rollback Plan**: Wie machen wir es rückgängig?
- [ ] **Success Criteria**: Messbare Verbesserung definiert

---

## 🔍 CODE SMELL DETECTION CHECKLIST

### ☑️ Method-Level Smells
- [ ] **Long Method**: >30 Zeilen oder >10 Complexity
- [ ] **Too Many Parameters**: >4 Parameter
- [ ] **Duplicate Code**: Identische Blöcke >6 Zeilen
- [ ] **Comments**: Erklären WAS statt WARUM
- [ ] **Magic Numbers**: Hardcoded Werte ohne Erklärung

**Quick Check:**
```bash
# Telekom Standard: Methoden-Komplexität prüfen
find . -name "*.java" -exec wc -l {} \; | sort -nr | head -20
```

### ☑️ Class-Level Smells
- [ ] **Large Class**: >400 Zeilen
- [ ] **God Object**: >20 öffentliche Methoden
- [ ] **Feature Envy**: Nutzt mehr fremde als eigene Daten
- [ ] **Data Class**: Nur Getter/Setter ohne Logik
- [ ] **Refused Bequest**: Subklasse überschreibt Parent-Verhalten

**Quick Check:**
```bash
# Klassen-Größe analysieren
find . -name "*.java" -exec grep -l "public class" {} \; | 
    xargs wc -l | sort -nr
```

### ☑️ Architecture-Level Smells
- [ ] **Shotgun Surgery**: Änderung betrifft >5 Klassen
- [ ] **Inappropriate Intimacy**: Package-private Zugriffe
- [ ] **Message Chains**: customer.getX().getY().getZ()
- [ ] **Middle Man**: Klasse delegiert nur weiter

---

## 🔧 REFACTORING EXECUTION CHECKLIST

### ☑️ Extract Method (für Long Methods)
- [ ] **Code-Segment identifiziert**: 5-15 Zeilen zusammengehörig
- [ ] **Lokale Variablen analysiert**: Was wird zu Parameter?
- [ ] **Return Value definiert**: Was gibt neue Methode zurück?
- [ ] **Meaningful Name**: Beschreibt WAS die Methode tut
- [ ] **Single Responsibility**: Methode tut genau eine Sache
- [ ] **Tests angepasst**: Private Methoden indirekt getestet

**Telekom-Naming Conventions:**
```java
// ✅ GOOD: Verben für Aktionen
validateCustomerOrder()
calculateMonthlyDiscount()
sendBillingNotification()

// ❌ BAD: Substantive oder zu generisch
customerValidation()
calculation()
process()
```

### ☑️ Extract Class (für God Objects)
- [ ] **Verantwortlichkeiten identifiziert**: Klare funktionale Gruppen
- [ ] **Dependencies analysiert**: Was muss mit der neuen Klasse?
- [ ] **Interface definiert**: Wie kommunizieren die Klassen?
- [ ] **Data Migration**: Welche Felder wandern mit?
- [ ] **Constructor Design**: Dependency Injection berücksichtigt
- [ ] **Tests aufgeteilt**: Pro Verantwortlichkeit separate Tests

**Extraktion Prioritäten:**
1. 🔴 **Kritisch**: Security, Payment, Authentication  
2. 🟠 **Hoch**: Business Logic, Validation
3. 🟡 **Mittel**: Reporting, Logging, Utilities
4. 🟢 **Niedrig**: UI, Formatting, Constants

### ☑️ Replace Conditional with Polymorphism
- [ ] **Switch Cases analysiert**: >3 Cases = Kandidat
- [ ] **Strategy Interface entworfen**: Gemeinsame Operationen
- [ ] **Concrete Strategies**: Eine Klasse pro Case
- [ ] **Factory/Registry**: Automatische Strategy-Selection
- [ ] **Spring Integration**: @Component Annotation für Auto-Discovery
- [ ] **Extensibility Test**: Neuer Case = nur neue Klasse

**Strategy Pattern Checklist:**
```java
// ✅ Checkliste für Strategy Interface
public interface DiscountStrategy {
    double calculate(Customer customer);        // ✅ Core Operation  
    boolean isApplicable(CustomerType type);   // ✅ Applicability Check
    String getDescription();                   // ✅ Human Readable
    int getPriority();                        // ✅ Conflict Resolution
}
```

### ☑️ Move Method (für Feature Envy)
- [ ] **Data Usage analysiert**: Welche Klasse nutzt Daten am meisten?
- [ ] **Cohesion bewertet**: Passt Methode thematisch zur Zielklasse?
- [ ] **Dependencies geprüft**: Entstehen zirkuläre Abhängigkeiten?
- [ ] **Access Level**: Public/Package-private nach Bedarf
- [ ] **Original Methode**: Wrapper oder komplett entfernen?

---

## ⚡ QUICK WINS CHECKLIST (15-30 Min Refactorings)

### ☑️ Magic Numbers eliminieren
- [ ] **Constants Class**: TelekomConstants.java erstellt
- [ ] **Meaningful Names**: PRIVATE_CUSTOMER_DISCOUNT statt 0.05
- [ ] **Grouping**: Thematisch verwandte Constants gruppiert
- [ ] **Configuration**: In application.properties ausgelagert?
- [ ] **Documentation**: Javadoc mit Begründung

### ☑️ Method Signature vereinfachen  
- [ ] **Parameter Objekte**: >4 Parameter in Objekt gekapselt
- [ ] **Boolean Parameters**: Durch Enum oder separate Methoden ersetzt
- [ ] **Null Parameters**: Optional<> oder Null Object Pattern
- [ ] **Order Dependencies**: Logische Parameter-Reihenfolge

### ☑️ Dead Code entfernen
- [ ] **IDE Analysis**: "Unused" Warnings beachtet
- [ ] **Git History**: Letzter Zugriff vor >6 Monaten?
- [ ] **@Deprecated**: Removal-Plan dokumentiert
- [ ] **Legacy Comments**: Auskommentierter Code entfernt
- [ ] **Imports**: Unused Imports aufgeräumt

```bash
# Telekom Dead Code Detection
# Findet nie verwendete öffentliche Methoden
grep -r "public.*(" src/main/ | grep -v "Test" | 
  cut -d: -f2 | sort | uniq -c | sort -nr
```

---

## 🧪 TEST-DRIVEN REFACTORING CHECKLIST

### ☑️ Test Preparation
- [ ] **Characterization Tests**: Verhalten vor Refactoring dokumentiert
- [ ] **Integration Tests**: End-to-End Szenarien abgedeckt
- [ ] **Performance Tests**: Baseline für kritische Pfade
- [ ] **Test Data**: Repräsentative Telekom-Testdaten

### ☑️ Red-Green-Refactor Cycle
- [ ] **🔴 Red**: Tests laufen grün (Baseline)
- [ ] **🟡 Refactor**: Eine kleine Änderung durchgeführt
- [ ] **🟢 Green**: Alle Tests laufen noch grün
- [ ] **📊 Measure**: Metriken verglichen
- [ ] **🔄 Next**: Nächster kleiner Schritt oder fertig

### ☑️ Test Quality nach Refactoring
- [ ] **Coverage Maintained**: Mindestens gleiche Coverage wie vorher
- [ ] **Test Speed**: Tests nicht langsamer geworden
- [ ] **Test Clarity**: Tests sind verständlicher
- [ ] **Brittle Tests vermieden**: Tests nicht zu eng gekoppelt

---

## 📊 QUALITY METRICS CHECKLIST

### ☑️ Obligatorische Metriken (Telekom Standard)
- [ ] **Cyclomatic Complexity**: Maximal 10 pro Methode
- [ ] **Method Length**: Maximal 30 Zeilen
- [ ] **Class Length**: Maximal 400 Zeilen  
- [ ] **Parameter Count**: Maximal 4 pro Methode
- [ ] **Nesting Depth**: Maximal 3 Ebenen

### ☑️ Erweiterte Qualitätsmetriken
- [ ] **Code Duplication**: <3% (SonarQube)
- [ ] **Maintainability Index**: >20 (Visual Studio)
- [ ] **Technical Debt**: <5% (SonarQube)
- [ ] **LCOM**: <2 (Lack of Cohesion of Methods)

### ☑️ Performance Metriken
- [ ] **Build Time**: Nicht schlechter als vorher
- [ ] **Test Execution**: Nicht langsamer geworden
- [ ] **Memory Usage**: Heap-Verbrauch gemessen
- [ ] **Response Time**: API-Endpunkte gemessen

**Telekom SonarQube Quality Gates:**
```yaml
# .sonarqube/quality-gates.json
{
  "reliability_rating": "A",
  "security_rating": "A", 
  "sqale_rating": "A",
  "coverage": 80.0,
  "duplicated_lines_density": 3.0
}
```

---

## 🚀 POST-REFACTORING CHECKLIST

### ☑️ Integration & Deployment
- [ ] **Merge Request**: Code Review mit Fokus auf Qualität
- [ ] **CI/CD Pipeline**: Alle Stages grün
- [ ] **Performance Tests**: Production-like Testing
- [ ] **Feature Flags**: Graduelle Auslieferung wenn möglich
- [ ] **Rollback Plan**: Hotfix-Strategie dokumentiert

### ☑️ Team Communication
- [ ] **Refactoring Summary**: Was wurde gemacht und warum
- [ ] **Knowledge Sharing**: Team-Session oder Confluence-Page
- [ ] **Best Practices**: Learnings für künftige Refactorings
- [ ] **Tool Updates**: Neue IDE-Settings oder Plugins geteilt

### ☑️ Documentation Update
- [ ] **Architecture Docs**: Diagramme aktualisiert wenn nötig
- [ ] **API Documentation**: OpenAPI/Swagger updated
- [ ] **Developer Guide**: Neue Patterns dokumentiert
- [ ] **Changelog**: User-facing Änderungen kommuniziert

---

## 🎯 WORKSHOP-SPEZIFISCHE CHECKLISTEN

### ☑️ Hands-On Exercise Setup (pro Team)
- [ ] **Git Repository**: Fork von Workshop-Repo erstellt
- [ ] **IDE Setup**: SonarLint und Checkstyle konfiguriert
- [ ] **Baseline Measurement**: Initial metrics erfasst
- [ ] **Role Assignment**: Driver/Navigator definiert
- [ ] **Timer**: Pomodoro-Timer für 25-Min-Sprints

### ☑️ Live Demo Preparation
- [ ] **Code Smell Selection**: Repräsentativer, verständlicher Fall
- [ ] **Before/After**: Screenshots für Metrics vorbereitet  
- [ ] **Timing**: Max. 15 Min pro Refactoring-Demo
- [ ] **Interaction**: Fragen-Slots eingeplant
- [ ] **Tools**: Screen-Recording für Replay

### ☑️ Team Exercise Evaluation
- [ ] **Metrics Comparison**: Vorher/Nachher Side-by-Side
- [ ] **Lessons Learned**: Was war schwierig, was einfach?
- [ ] **Tool Feedback**: Welche Tools haben geholfen?
- [ ] **Process Feedback**: Welche Schritte waren unnötig?
- [ ] **Next Steps**: Was würden sie als nächstes refactoren?

---

## ⚠️ REFACTORING ANTI-PATTERNS (AVOID!)

### 🚫 Big Bang Refactoring
- [ ] **Nicht tun**: Alles auf einmal refactoren
- [ ] **Stattdessen**: Baby Steps mit kontinuierlicher Integration
- [ ] **Telekom-Regel**: Max. 200 Zeilen pro Refactoring-PR

### 🚫 Refactoring ohne Tests
- [ ] **Nicht tun**: "Ist nur ein kleines Refactoring"
- [ ] **Stattdessen**: Immer zuerst Characterization Tests
- [ ] **Telekom-Standard**: Min. 70% Coverage vor Refactoring

### 🚫 Premature Abstraction
- [ ] **Nicht tun**: "Wir brauchen vielleicht mal Flexibilität"
- [ ] **Stattdessen**: YAGNI - Refactoren wenn echter Bedarf
- [ ] **Rule of Three**: Erst beim 3. Duplikat abstrahieren

### 🚫 Performance-ignoring Refactoring
- [ ] **Nicht tun**: Refactoring ohne Performance-Check
- [ ] **Stattdessen**: Performance-Tests in CI/CD Pipeline
- [ ] **Telekom-SLA**: Response-Zeit darf nicht schlechter werden

---

## 🏆 SUCCESS STORIES TEMPLATE

### ✅ Refactoring Success Documentation
```markdown
## Refactoring: [Smell] → [Technique] 
**Date**: 2025-01-09
**Duration**: 2.5h
**Team**: [Names]

### Problem
- Code Smell: Long Method in CustomerService.processOrder()
- Impact: 67 lines, Complexity 15, untestbar

### Solution  
- Technique: Extract Method + Extract Class
- 3 neue Methoden, 1 neue Klasse (OrderProcessor)

### Metrics
| Metric | Before | After | Improvement |
|--------|--------|--------|-------------|
| Lines | 67 | 12 | -82% |
| Complexity | 15 | 3 | -80% |
| Coverage | 45% | 92% | +47% |

### Lessons Learned
- Baby Steps wichtiger als gedacht
- Characterization Tests waren goldwert
- SonarQube Live-Feedback sehr hilfreich

### Recommendation
- Technique funktioniert gut für ähnliche Cases
- Tool-Setup am Anfang Zeit investieren
- Code Review für Refactorings ist essentiell
```

---

## 📱 MOBILE QUICK-REFERENCE CARD

### 🔍 Code Smell Detection (30 sec)
1. **Lines**: >30? → Extract Method
2. **Parameters**: >4? → Parameter Object  
3. **Switch**: >3 Cases? → Polymorphism
4. **Classes**: >400 LOC? → Extract Class
5. **Duplicates**: >6 Lines? → Extract Method

### ⚡ Quick Fixes (2-5 Min)
1. **Magic Number** → Constant
2. **Long Name** → Shorter + Javadoc
3. **Dead Code** → Delete  
4. **Unused Import** → Remove
5. **TODO/FIXME** → Task erstellen

### 🧪 Test Checklist (1 Min)
1. **Coverage** >70%? ✅
2. **All Green**? ✅  
3. **No Warnings**? ✅
4. **Build Clean**? ✅
5. **Git Clean**? ✅

### 📊 Quality Gates (30 sec)
- **Complexity**: ≤10 ✅
- **Length**: ≤30 LOC ✅
- **Parameters**: ≤4 ✅
- **Duplication**: <3% ✅
- **Coverage**: >80% ✅

---

**🎯 Telekom Workshop Motto:**
> "Clean Code ist nicht das Ziel - wartbarer Code ist das Ziel. Clean Code ist das Mittel."

**⏱️ Time Management:**
- **Analysis**: 20% der Zeit
- **Refactoring**: 60% der Zeit  
- **Testing**: 20% der Zeit

**🤝 Team Rules:**
- Pair Programming für komplexe Refactorings
- Code Review für alle Refactoring-PRs
- Metrics-basierte Entscheidungen
- Learning über Perfektion