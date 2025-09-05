# Docker Setup für Architecture Training

Dieses Projekt verwendet Docker für eine konsistente Entwicklungsumgebung und Build-Pipeline.

## Plattformübergreifende Nutzung

Diese Docker-Umgebung funktioniert auf **Windows**, **macOS** und **Linux** ohne zusätzliche Shell-Skripte.

## Übersicht

- **Base Image**: Maven 3.8 mit OpenJDK 17
- **Build Tool**: Maven 3.8+
- **Dependency Caching**: Beschleunigt wiederholte Builds
- **Development Support**: Interaktive Entwicklungsumgebung
- **Plattform-Support**: Windows, macOS, Linux

## Schnellstart (Alle Plattformen)

```bash
# Code kompilieren
docker compose run --rm build

# Tests ausführen
docker compose run --rm test

# Vollständige CI/CD Pipeline
docker compose run --rm ci

# Interaktive Entwicklungsumgebung
docker compose run --rm dev
```

## Detaillierte Anweisungen

### 1. Code kompilieren

```bash
# Alle Module kompilieren
docker compose run --rm build
```

### 2. Tests ausführen

```bash
# Alle Tests
docker compose run --rm test

# Tests für einzelne Tage
docker compose run --rm test-day1
docker compose run --rm test-day2
docker compose run --rm test-day3
docker compose run --rm test-day4
```

### 3. JAR-Dateien erstellen

```bash
# Erstellt ausführbare JARs (ohne Tests)
docker compose run --rm package
```

### 4. Entwicklungsumgebung

```bash
# Interaktive Shell im Container öffnen
docker compose run --rm dev

# Im Container arbeiten:
mvn compile
mvn test -pl day1-examples
mvn package
exit
```

### 5. CI/CD Pipeline

```bash
# Vollständiger Build mit allen Tests
docker compose run --rm ci

# Was passiert:
# 1. Clean build (kein Cache)
# 2. Dependency download
# 3. Kompilierung aller Module
# 4. Unit Tests
# 5. Integration Tests
# 6. Verification
```

## Windows-spezifische Hinweise

### PowerShell
```powershell
# Build ausführen
docker compose run --rm build

# Tests ausführen
docker compose run --rm test

# Interaktive Entwicklung
docker compose run --rm dev
```

### Windows Command Prompt (cmd)
```cmd
REM Build ausführen
docker compose run --rm build

REM Tests ausführen
docker compose run --rm test

REM Interaktive Entwicklung
docker compose run --rm dev
```

## Docker Services

### build
- **Zweck**: Code kompilieren
- **Command**: `mvn clean compile`

### test, test-day1, test-day2, test-day3, test-day4  
- **Zweck**: Tests ausführen
- **Command**: `mvn test` oder modulspezifisch

### package
- **Zweck**: JAR-Dateien erstellen
- **Command**: `mvn clean package -DskipTests`

### dev
- **Zweck**: Interaktive Entwicklung
- **Command**: `/bin/bash`

### ci
- **Zweck**: CI/CD Pipeline
- **Command**: `mvn clean verify`

## Volume Management

### Maven Cache
```bash
# Cache wird automatisch erstellt
# Bei Problemen Cache löschen:
docker volume rm architecture-training-maven-cache

# Cache Größe prüfen
docker system df -v
```

## Erste Schritte für Workshop-Teilnehmer

### 1. Docker installieren

**Windows:**
- Docker Desktop von https://www.docker.com/products/docker-desktop

**macOS:**
- Docker Desktop von https://www.docker.com/products/docker-desktop

**Linux:**
- Docker Engine gemäß Distribution installieren

### 2. Projekt vorbereiten

```bash
# Repository klonen
git clone [repository-url]
cd examples

# Code kompilieren
docker compose run --rm build

# Tests ausführen
docker compose run --rm test
```

### 3. Entwicklung starten

```bash
# Interaktive Umgebung öffnen
docker compose run --rm dev

# Im Container:
mvn compile
mvn test -pl day1-examples
exit
```

## Troubleshooting

### Häufige Probleme

#### "docker compose" nicht gefunden
```bash
# Bei älteren Docker-Versionen:
docker-compose run --rm build  # mit Bindestrich

# Oder Docker Desktop aktualisieren
```

#### Maven Dependency Probleme
```bash
# Maven Cache leeren
docker volume rm architecture-training-maven-cache
docker compose run --rm build
```

#### Berechtigungsfehler (Linux)
```bash
# Mit sudo ausführen
sudo docker compose run --rm build

# Oder User zur docker-Gruppe hinzufügen
sudo usermod -aG docker $USER
```

#### Container läuft bereits
```bash
# Alle Container stoppen
docker compose down

# Neu starten
docker compose run --rm build
```

## Ohne Docker arbeiten

Falls Docker nicht verfügbar ist:

### Voraussetzungen
- JDK 17 oder höher
- Maven 3.8 oder höher

### Befehle
```bash
mvn clean compile
mvn test
mvn package
```