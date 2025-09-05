# Docker Setup für Architecture Training

Dieses Projekt verwendet Docker für eine konsistente Entwicklungsumgebung und Build-Pipeline.

## Übersicht

- **Base Image**: OpenJDK 11 (slim)
- **Build Tool**: Maven 3.8+
- **Multi-Stage Build**: Optimiert für kleinere Images
- **Dependency Caching**: Beschleunigt wiederholte Builds
- **Development Support**: Live-Reload für lokale Entwicklung

## Schnellstart

```bash
# Docker Image bauen
./docker-build.sh build

# Tests ausführen
./docker-build.sh test

# Vollständige CI/CD Pipeline
./docker-build.sh ci

# Entwicklungsumgebung starten
./docker-build.sh dev
```

## Detaillierte Anweisungen

### 1. Docker Image bauen

```bash
# Standard Build
docker-compose build

# Build ohne Cache (clean build)
docker-compose build --no-cache

# Über das Script
./docker-build.sh build        # Mit Cache
./docker-build.sh build-clean  # Ohne Cache
```

### 2. Tests ausführen

```bash
# Unit Tests
docker-compose run --rm maven-build mvn test

# Alle Tests mit Verification
docker-compose run --rm maven-build mvn clean verify

# Über das Script
./docker-build.sh test  # Nur Tests
./docker-build.sh ci    # Vollständige Pipeline
```

### 3. Entwicklungsumgebung

```bash
# Development Container starten
docker-compose up -d maven-dev

# Interaktive Shell öffnen
docker-compose exec maven-dev bash

# Container stoppen
docker-compose down
```

### 4. CI/CD Pipeline

Für Continuous Integration verwenden Sie:

```bash
# Vollständige Pipeline
./docker-build.sh ci

# Was passiert:
# 1. Clean build (kein Cache)
# 2. Dependency download
# 3. Kompilierung aller Module
# 4. Unit Tests
# 5. Integration Tests
# 6. Checkstyle Validation
```

## Docker Services

### maven-build
- **Zweck**: Standard Build und Test Ausführung
- **Volumes**: Source code, Maven cache, Build outputs
- **Command**: Konfigurierbar

### maven-dev  
- **Zweck**: Entwicklungsumgebung
- **Features**: Live reload, persistente Shell
- **Ports**: 8080 (für Web-Anwendungen)
- **Command**: Keep-alive für interaktive Nutzung

### maven-ci
- **Zweck**: CI/CD Pipeline
- **Features**: Clean build, keine Volumes
- **Command**: `mvn clean verify -B`

## Volume Management

### Maven Cache
```bash
# Cache Volume erstellen (automatisch)
docker volume create examples_maven-cache

# Cache leeren
docker volume rm examples_maven-cache

# Cache Größe prüfen
docker system df -v
```

### Build Outputs
- `./target/` - Root project outputs
- `./day*-examples/target/` - Module-spezifische outputs

## Optimierungen

### Build Performance
- **Layer Caching**: POM-Dateien werden vor Source Code kopiert
- **Dependency Caching**: Maven Dependencies werden in named volume gecached
- **Multi-Stage Build**: Minimiert finales Image

### Development Workflow
```bash
# 1. Erstmaliger Setup
./docker-build.sh build
./docker-build.sh dev

# 2. Code ändern (lokal)
# Dateien werden automatisch in Container gemountet

# 3. Tests im Container ausführen
docker-compose exec maven-dev mvn test

# 4. Oder komplette Verification
docker-compose exec maven-dev mvn verify
```

## Troubleshooting

### Häufige Probleme

#### Docker Build Fehler
```bash
# Cache leeren und neu bauen
./docker-build.sh clean
./docker-build.sh build-clean
```

#### Maven Dependency Probleme
```bash
# Maven Cache leeren
docker volume rm examples_maven-cache
./docker-build.sh build
```

#### Permission Probleme
```bash
# Auf macOS/Linux
sudo chown -R $USER:$USER target/
sudo chown -R $USER:$USER day*-examples/target/
```

#### Container läuft nicht
```bash
# Status prüfen
docker-compose ps

# Logs anzeigen
./docker-build.sh logs

# Container neu starten
docker-compose restart maven-dev
```

### Debug Commands

```bash
# Container Status
docker-compose ps

# Resource Usage
docker stats

# Image Informationen
docker images | grep architecture-training

# Volume Informationen
docker volume ls
docker volume inspect examples_maven-cache

# Netzwerk Informationen
docker network ls
docker network inspect examples_default
```

### Performance Monitoring

```bash
# Build Zeit messen
time ./docker-build.sh ci

# Container Resources überwachen
docker stats architecture-training-maven

# Disk Usage
docker system df
```

## Best Practices

### Für Entwickler
1. **Cache nutzen**: Führen Sie `./docker-build.sh build` nur einmal aus
2. **Dev Environment**: Nutzen Sie `maven-dev` für tägliche Arbeit
3. **Tests früh**: Führen Sie Tests vor jedem Commit aus
4. **Clean Builds**: Verwenden Sie CI-Pipeline vor Release

### Für CI/CD
1. **Clean Builds**: Immer `build-clean` in CI verwenden
2. **Resource Limits**: Setzen Sie Memory-Limits für Container
3. **Parallel Builds**: Nutzen Sie Docker BuildKit für Speed
4. **Security Scanning**: Integrieren Sie Image Scanning

### Für Production
1. **Multi-Stage**: Verwenden Sie runtime stage für Deployment
2. **Security**: Scannen Sie Images auf Vulnerabilities
3. **Monitoring**: Implementieren Sie Health Checks
4. **Updates**: Halten Sie Base Images aktuell

## Wartung

### Regelmäßige Aufgaben
```bash
# System aufräumen (wöchentlich)
./docker-build.sh clean
docker system prune -a

# Base Images aktualisieren (monatlich)
docker pull openjdk:11-jdk-slim
docker pull openjdk:11-jre-slim
./docker-build.sh build-clean

# Volumes prüfen (monatlich)
docker volume ls
docker system df -v
```

## Unterstützung

Bei Problemen oder Fragen:

1. Prüfen Sie die Logs: `./docker-build.sh logs`
2. Dokumentation lesen: Diese README
3. Script Hilfe: `./docker-build.sh help`
4. Container debuggen: `./docker-build.sh shell`

## Changelog

- **v1.0.0** (2025-09-05): Initial Docker Setup
  - Multi-stage Dockerfile
  - Docker Compose mit 3 Services
  - Build Script mit CI/CD Pipeline
  - Comprehensive Documentation