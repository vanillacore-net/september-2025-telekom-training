# STORY-010: Tag 2 - Code-Beispiele Structural Patterns

## Story
Als Trainer benötige ich Code-Beispiele für Tag 2, um Structural Patterns praktisch demonstrieren zu können.

## Umfang

### Tarif-Options System
- Initial: Vererbungs-Explosion
- Smells: Combinatorial Explosion
- Fixed: Decorator Pattern

### Tarif-Strukturen Hierarchie
- Initial: Uneinheitliche Behandlung
- Smells: Type Checking überall
- Fixed: Composite Pattern

### Customer Data Caching
- Initial: Teure DB-Abfragen
- Smells: Performance Bottleneck
- Fixed: Proxy mit Caching

### Payment Processing
- Initial: Provider-spezifischer Code
- Smells: Tight Coupling
- Fixed: Bridge Pattern

## Akzeptanzkriterien

### Code-Qualität
- [ ] Decorator kombinierbar demonstriert
- [ ] Composite-Hierarchie traversierbar
- [ ] Proxy-Performance messbar
- [ ] Bridge-Abstraktion austauschbar
- [ ] Memory-Optimierung bei Flyweight

### Dokumentation
- [ ] Pattern-Kombinationen erklärt
- [ ] Performance-Metriken dokumentiert
- [ ] Architektur-Bezug hergestellt
- [ ] Hands-On Übungen vorbereitet

### Praxisbezug
- [ ] Tarif-System realistisch
- [ ] Performance-Probleme authentisch
- [ ] Provider-Integration praxisnah

## Deliverables
```
./examples/day2/
├── tariff-options/
│   ├── initial/
│   ├── fixed/
│   ├── tests/
│   └── trainer-notes.md
├── tariff-hierarchy/
│   └── [analog]
├── customer-caching/
│   └── [analog]
└── payment-bridge/
    └── [analog]
```

## Abhängigkeiten
- Unterstützt: STORY-002 (Tag 2 Content)
- Baut auf: STORY-009 (Tag 1 Beispiele)

## Priorität
**HOCH** - Structural Patterns Demo

## Geschätzter Aufwand
8 Stunden