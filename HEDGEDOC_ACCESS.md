# HedgeDoc Presentation Access Guide

The Telekom Architecture training presentations are now directly accessible through HedgeDoc at the URLs below. No copy/paste required!

## Direct Presentation Access

### Day 1 - Design Patterns Fundamentals
- **Edit/View Mode**: http://localhost:3000/day1-telekom-architecture
- **Slide Mode**: http://localhost:3000/p/day1-telekom-architecture

### Day 2 - Advanced Design Patterns
- **Edit/View Mode**: http://localhost:3000/day2-telekom-architecture
- **Slide Mode**: http://localhost:3000/p/day2-telekom-architecture

### Day 3 - Architectural Patterns
- **Edit/View Mode**: http://localhost:3000/day3-telekom-architecture
- **Slide Mode**: http://localhost:3000/p/day3-telekom-architecture

### Day 4 - System Integration
- **Edit/View Mode**: http://localhost:3000/day4-telekom-architecture
- **Slide Mode**: http://localhost:3000/p/day4-telekom-architecture

## How to Use

1. **Start HedgeDoc**: Run `docker-compose up -d` in the project root
2. **Import Presentations**: Run `./import-presentations.sh` to import all presentations
3. **Access Presentations**: Use the URLs above to directly access presentations
4. **Slide Mode**: Add `/p/` before the presentation name for full-screen slide mode

## Features

- **Direct Access**: Presentations load immediately without manual import
- **Edit Mode**: Full HedgeDoc editing capabilities
- **Slide Mode**: RevealJS-powered presentation mode with navigation
- **Collaborative**: Multiple users can view/edit simultaneously
- **Persistent**: Presentations remain available across container restarts

## Troubleshooting

If presentations aren't visible:
1. Check HedgeDoc is running: `docker-compose ps`
2. Re-import presentations: `./import-presentations.sh`
3. Check HedgeDoc logs: `docker logs hedgedoc-dev`

The presentations are automatically imported when you run the import script and persist in the HedgeDoc database.