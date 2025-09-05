# Refactoring Philosophie
*Dauer: 15-20 Minuten*

## 🎯 Lernziele
- Refactoring als kontinuierlichen Prozess verstehen
- Boy Scout Rule kennenlernen und anwenden
- Wissen, wann und wie refactoriert wird
- Refactoring als Weg zu Patterns verstehen

## 🔄 Was ist Refactoring? (5 Minuten)

### Definition nach Martin Fowler:
> *"Refactoring is the process of changing a software system in such a way that it does not alter the external behavior of the code yet improves its internal structure."*

### Schlüsselelemente:
1. **Verhalten bleibt gleich** - Funktionalität ändert sich nicht
2. **Struktur wird besser** - Code wird sauberer, verständlicher, wartbarer
3. **Kleine Schritte** - Viele kleine, sichere Änderungen
4. **Tests als Sicherheitsnetz** - Verhalten wird automatisch überprüft

### Was Refactoring NICHT ist:
- Bugfixes (das ändert Verhalten)
- Neue Features (das erweitert Verhalten)  
- Performance-Optimierung (das kann Verhalten beeinflussen)
- Rewrite (zu riskant und zu groß)

## Die Boy Scout Rule (5-7 Minuten)

### Ursprung: Boy Scouts of America
> *"Try and leave this world a little better than you found it."*

### Übertragung auf Software nach Uncle Bob:
> *"Always leave the campground cleaner than you found it."*  
> *"Always check a module in cleaner than when you checked it out."*

### Praktische Anwendung:

#### Bei jedem Code-Touch:
1. **Verstehe** was der Code macht
2. **Verbessere** eine Kleinigkeit  
3. **Prüfe** dass alles noch funktioniert
4. **Committe** die Verbesserung

#### Konkrete Boy Scout Aktionen:
```text
Variable umbenennen (temp → elapsedTimeInDays)
Magic Number extrahieren (7 → DAYS_PER_WEEK)
Long Method aufteilen
Duplicate Code extrahieren
Unused Code entfernen
Kommentare durch self-documenting code ersetzen
```

### Praktisches Beispiel:
```text
Situation: Bug-Fix in alter Service-Klasse

Vor Boy Scout Rule:
- Fix den Bug
- Code bleibt messy
- Nächster Entwickler hat gleichen Kampf

Mit Boy Scout Rule:
- Fix den Bug  
- Benenne 2-3 kryptische Variablen um
- Extrahiere eine lange Methode
- Nächster Entwickler findet sich besser zurecht
```

## Wann refactoren? (5-7 Minuten)

### Die "Rule of Three" nach Martin Fowler:
1. **Das erste Mal** - mache es einfach
2. **Das zweite Mal** - ärgere dich über die Duplikation, aber mache es trotzdem
3. **Das dritte Mal** - refactore!

### Refactoring-Trigger:

#### 1. **Wenn du Code verstehen musst**
- Bevor du einen Bug fixst
- Bevor du ein Feature hinzufügst
- Beim Code Review

#### 2. **Wenn du Duplikation siehst**
- Copy-Paste Code
- Ähnliche Methoden in verschiedenen Klassen
- Wiederkehrende Patterns

#### 3. **Wenn Code "riecht" (Code Smells)**
- Long Method (>20 Zeilen)
- Large Class (>200 Zeilen)
- Long Parameter List (>3 Parameter)
- Duplicate Code
- Comments explaining complex code

#### 4. **Beim Hinzufügen von Features**
- Mache den Code erst änderungsfreundlich
- Füge dann das Feature hinzu
- "Make the change easy, then make the easy change"

### Gute Refactoring-Zeitpunkte:
- **Sprint Planning**: "Diese Story braucht Refactoring-Zeit"
- **Bug-Fixing**: Immer mit Boy Scout Rule kombinieren
- **Code Reviews**: Verbesserungen vorschlagen und umsetzen
- **Technische Stories**: Dedicated Refactoring-Zeit

## Wie refactoren? (5-7 Minuten)

### Die Refactoring-Sicherheitsmaßnahmen:

#### 1. **Tests zuerst**
```text
Vor Refactoring:
☐ Verstehe das aktuelle Verhalten
☐ Schreibe Tests für das Verhalten (falls nicht vorhanden)
☐ Alle Tests sind grün

Während Refactoring:
☐ Nach jedem Schritt: Tests laufen lassen
☐ Immer grün bleiben

Nach Refactoring:
☐ Alle Tests noch grün
☐ Neues Verhalten getestet (falls hinzugefügt)
```

#### 2. **Kleine Schritte**
```text
SCHLECHT: Alles auf einmal ändern
GUT: Ein Schritt nach dem anderen

Beispiel - Long Method Refactoring:
1. Extrahiere erste Methode → Test
2. Extrahiere zweite Methode → Test  
3. Benenne Parameter um → Test
4. Extrahiere dritte Methode → Test
```

#### 3. **IDE-Unterstützung nutzen**
Moderne IDEs können viel automatisch und sicher machen:
- Extract Method
- Rename Variable/Method/Class
- Move Method/Field
- Inline Variable/Method

### Refactoring-Patterns (häufige Operationen):

#### Extract Method
```text
// Vorher: Long Method
public void processOrder(Order order) {
    // validate order (10 lines)
    // calculate shipping (15 lines) 
    // update inventory (12 lines)
    // send confirmation (8 lines)
}

// Nachher: Mehrere fokussierte Methoden
public void processOrder(Order order) {
    validateOrder(order);
    calculateShipping(order);
    updateInventory(order);
    sendConfirmation(order);
}
```

#### Replace Magic Number with Named Constant
```text
// Vorher
if (customer.getOrderCount() > 10) { ... }

// Nachher  
private static final int PREMIUM_CUSTOMER_THRESHOLD = 10;
if (customer.getOrderCount() > PREMIUM_CUSTOMER_THRESHOLD) { ... }
```

## Refactoring als Weg zu Patterns (3-5 Minuten)

### Patterns entstehen durch Refactoring:

#### Typischer Ablauf:
1. **Duplikation** entsteht natürlich
2. **Refactoring** macht Gemeinsamkeiten sichtbar
3. **Pattern Recognition** - "Das ist ein bekanntes Problem"
4. **Pattern Application** - Bekannte Lösung anwenden

### Beispiel: Strategy Pattern durch Refactoring

#### Evolution:
```text
1. Ein if-else für Zahlungsarten
2. Zweites if-else an anderer Stelle
3. Duplikation durch Extract Method reduzieren
4. Ähnliches Pattern an dritter Stelle
5. Erkennung: "Das ist Strategy Pattern!"
6. Refactoring zu Strategy Pattern
```

### Wichtige Erkenntnis:
**Patterns werden durch Refactoring eingeführt, nicht von Anfang an geplant!**

## Refactoring-Fallen vermeiden

### Häufige Fehler:
1. **Big Bang Refactoring**: Alles auf einmal ändern
2. **Refactoring ohne Tests**: Kein Sicherheitsnetz
3. **Perfectionism**: Endlos optimieren
4. **Wrong Timing**: Refactoring unter Zeitdruck
5. **Changing Behavior**: Versehentlich Verhalten ändern

### Enterprise-Kontext:
- **Legacy Systems**: Extra vorsichtig, mehr Tests
- **Live Systems**: Graduelle Änderungen
- **Compliance**: Dokumentation der Änderungen
- **Team Size**: Koordination bei großen Teams

## Kernbotschaften
1. **Refactoring ist kontinuierlich**, nicht ein einmaliges Event
2. **Boy Scout Rule** macht Refactoring zur Gewohnheit
3. **Kleine, sichere Schritte** sind besser als große Sprünge
4. **Patterns entstehen durch Refactoring**, nicht durch Planung
5. **Tests sind essenziell** für sicheres Refactoring

## Diskussionsfragen
1. "Wann haben Sie zuletzt Boy Scout Rule angewendet?"
2. "Was hindert Sie daran, kontinuierlich zu refactoren?"
3. "Wie überzeuge ich Management, Zeit für Refactoring zu geben?"
4. "Welche Tools nutzen Sie für sicheres Refactoring?"

---
*Mit diesem Refactoring-Mindset sind wir bereit für den eigentlichen Workshop - wir wissen jetzt, dass wir Patterns durch evolutionäre Verbesserung entdecken!*