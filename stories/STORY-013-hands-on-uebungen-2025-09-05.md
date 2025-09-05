# STORY-013: Hands-On Übungen für Teilnehmer

## Story
Als Trainer benötige ich vorbereitete Hands-On Übungen, damit Teilnehmer Patterns selbstständig anwenden können.

## Umfang

### Übungs-Typen
- Refactoring-Übungen: Code-Smells mit Patterns lösen
- Erweiterungs-Übungen: Features pattern-basiert ergänzen
- Design-Übungen: Pattern-Auswahl für Probleme
- Review-Übungen: Pattern-Anwendung bewerten

### Schwierigkeitsgrade
- Einsteiger: Geführte Übungen mit Hinweisen
- Fortgeschritten: Selbstständige Pattern-Wahl
- Experten: Pattern-Kombinationen

## Akzeptanzkriterien

### Übungs-Qualität
- [ ] 10-12 Übungen vorbereitet
- [ ] Starter-Code lauffähig
- [ ] Tests vorgegeben
- [ ] Musterlösungen vollständig
- [ ] Zeitschätzungen realistisch

### Didaktik
- [ ] Klare Aufgabenstellung
- [ ] Schrittweise Hinweise
- [ ] Bewertungskriterien definiert
- [ ] Diskussionspunkte vorbereitet

### Praxisbezug
- [ ] Telekom-Szenarien
- [ ] Realistische Probleme
- [ ] Verschiedene Domains

## Deliverables
```
./exercises/
├── factory-method-exercise/
│   ├── aufgabenstellung.md
│   ├── starter-code/
│   ├── tests/
│   ├── hints/
│   ├── solution/
│   └── trainer-guide.md
├── decorator-exercise/
├── strategy-exercise/
├── observer-exercise/
├── command-exercise/
├── state-exercise/
├── composite-exercise/
├── adapter-exercise/
├── proxy-exercise/
├── mediator-exercise/
├── pattern-selection-exercise/
└── code-review-exercise/
    └── [alle analog]
```

## Abhängigkeiten
- Benötigt: STORY-009 bis STORY-012
- Ergänzt: Alle Content-Stories

## Priorität
**MITTEL** - Interaktivität

## Geschätzter Aufwand
12 Stunden