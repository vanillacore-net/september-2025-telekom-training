# STORY-015: Trainer-Präsentation und Folien

## Story
Als Trainer benötige ich eine Präsentation zur visuellen Unterstützung des Workshops mit Diagrammen und Übersichten.

## Umfang

### Präsentations-Inhalte
- Pattern-Struktur Diagramme
- Architektur-Schemas
- Code-Smell Visualisierungen
- Refactoring-Schritte
- Pattern-Vergleiche
- Decision-Trees

### Folien-Kategorien
- Einführungs-Folien pro Tag
- Pattern-Übersicht Folien
- Live-Coding Platzhalter
- Diskussions-Trigger
- Zusammenfassungen
- Next-Steps

## Akzeptanzkriterien

### Inhaltliche Anforderungen
- [ ] UML-Diagramme für alle Patterns
- [ ] Architektur-Übersichten
- [ ] Before/After Vergleiche
- [ ] Metriken-Visualisierungen
- [ ] Entscheidungshilfen

### Gestaltung
- [ ] Einheitliches Design
- [ ] Telekom Branding (optional)
- [ ] Gut lesbare Schriften
- [ ] Code-Highlighting
- [ ] Animationen sparsam

### Praktikabilität
- [ ] PDF Export möglich
- [ ] Notizen für Trainer
- [ ] Zeitangaben pro Abschnitt
- [ ] Backup ohne Animationen

## Deliverables
```
./presentation/
├── slides/
│   ├── day1-slides.pptx
│   ├── day2-slides.pptx
│   ├── day3-slides.pptx
│   └── day4-slides.pptx
├── diagrams/
│   ├── pattern-structures/
│   ├── architecture-schemas/
│   └── decision-trees/
├── handouts/
│   └── pattern-overview.pdf
└── trainer-notes.md
```

## Abhängigkeiten
- Benötigt: STORY-001 bis STORY-004
- Nutzt: STORY-007 (Architektur-Patterns)

## Priorität
**NIEDRIG** - Nice-to-have

## Geschätzter Aufwand
8 Stunden