# HedgeDoc Local Development Setup - Simple Guide

## Overview

This guide provides a streamlined approach to setting up HedgeDoc locally for development and testing. Unlike production deployments, this setup prioritizes simplicity and quick startup for local development workflows.

## Prerequisites

- Docker and Docker Compose installed
- At least 2GB RAM available
- Port 3000 available (or configurable)

## Quick Start (5-Minute Setup)

### Minimal Docker Compose

Create a `docker-compose.local.yml` file:

```yaml
version: '3.8'

services:
  hedgedoc:
    image: quay.io/hedgedoc/hedgedoc:1.9.9
    container_name: hedgedoc-local
    environment:
      # Database (SQLite for simplicity)
      - CMD_DB_URL=sqlite:///data/hedgedoc.sqlite
      
      # Basic Configuration
      - CMD_DOMAIN=localhost
      - CMD_URL_ADDPORT=true
      - CMD_PORT=3000
      - CMD_PROTOCOL_USESSL=false
      
      # Development Settings
      - CMD_ALLOW_ANONYMOUS=true
      - CMD_ALLOW_ANONYMOUS_EDITS=true
      - CMD_ALLOW_FREEURL=true
      - CMD_DEFAULT_PERMISSION=freely
      
      # Session (simple secret for development)
      - CMD_SESSION_SECRET=dev-secret-change-in-production
      - CMD_SESSION_LIFE=86400000  # 24 hours
      
      # Uploads
      - CMD_IMAGE_UPLOAD_TYPE=filesystem
      - CMD_ALLOW_GRAVATAR=true
      
    ports:
      - "3000:3000"
    volumes:
      - hedgedoc_data:/hedgedoc/public/uploads
      - hedgedoc_db:/data
    restart: unless-stopped

volumes:
  hedgedoc_data:
  hedgedoc_db:
```

### Environment File (.env.local)

Create a `.env.local` file for easy customization:

```bash
# Basic Configuration
HEDGEDOC_PORT=3000
HEDGEDOC_DOMAIN=localhost

# Development Flags
ALLOW_ANONYMOUS=true
ALLOW_ANONYMOUS_EDITS=true
SESSION_SECRET=dev-secret-please-change

# Upload Settings
MAX_DOC_LENGTH=100000
IMAGE_UPLOAD_TYPE=filesystem
```

### Startup Commands

```bash
# Start HedgeDoc locally
docker-compose -f docker-compose.local.yml up -d

# View logs
docker-compose -f docker-compose.local.yml logs -f

# Stop services
docker-compose -f docker-compose.local.yml down

# Stop and remove all data (fresh start)
docker-compose -f docker-compose.local.yml down -v
```

## Alternative Quick Setup Options

### Option 1: SQLite Single Container

The simplest possible setup using SQLite:

```yaml
version: '3.8'

services:
  hedgedoc:
    image: quay.io/hedgedoc/hedgedoc:1.9.9
    container_name: hedgedoc-simple
    environment:
      - CMD_DB_URL=sqlite:///data/hedgedoc.sqlite
      - CMD_DOMAIN=localhost
      - CMD_URL_ADDPORT=true
      - CMD_PROTOCOL_USESSL=false
      - CMD_ALLOW_ANONYMOUS=true
      - CMD_ALLOW_ANONYMOUS_EDITS=true
      - CMD_SESSION_SECRET=simple-dev-secret
    ports:
      - "3000:3000"
    volumes:
      - ./hedgedoc-data:/data
      - ./hedgedoc-uploads:/hedgedoc/public/uploads
```

### Option 2: With PostgreSQL (More Robust)

```yaml
version: '3.8'

services:
  hedgedoc:
    image: quay.io/hedgedoc/hedgedoc:1.9.9
    container_name: hedgedoc-dev
    environment:
      - CMD_DB_URL=postgres://hedgedoc:devpass@db:5432/hedgedoc
      - CMD_DOMAIN=localhost
      - CMD_URL_ADDPORT=true
      - CMD_PROTOCOL_USESSL=false
      - CMD_ALLOW_ANONYMOUS=true
      - CMD_ALLOW_ANONYMOUS_EDITS=true
      - CMD_SESSION_SECRET=dev-secret-key
    ports:
      - "3000:3000"
    volumes:
      - hedgedoc_uploads:/hedgedoc/public/uploads
    depends_on:
      - db

  db:
    image: postgres:15-alpine
    container_name: hedgedoc-db-dev
    environment:
      - POSTGRES_USER=hedgedoc
      - POSTGRES_PASSWORD=devpass
      - POSTGRES_DB=hedgedoc
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  hedgedoc_uploads:
  postgres_data:
```

## Local Development Configuration

### Development-Friendly Settings

```bash
# Essential development environment variables
CMD_ALLOW_ANONYMOUS=true              # No login required
CMD_ALLOW_ANONYMOUS_EDITS=true        # Anonymous users can edit
CMD_ALLOW_FREEURL=true                # Allow custom note URLs
CMD_DEFAULT_PERMISSION=freely         # Open permissions by default
CMD_PROTOCOL_USESSL=false             # No SSL for local dev
CMD_URL_ADDPORT=true                  # Include port in URLs
CMD_SESSION_LIFE=86400000             # Long session (24h)
CMD_IMAGE_UPLOAD_TYPE=filesystem      # Simple file uploads
CMD_ALLOW_GRAVATAR=true               # Enable avatars
```

### Optional Development Features

```bash
# Useful for development/testing
CMD_DEBUG=true                        # Enable debug logging
CMD_LOGLEVEL=debug                    # Detailed logs
CMD_ALLOW_PDF_EXPORT=true             # Enable PDF export
CMD_ALLOW_PRINT=true                  # Enable print functionality

# Email configuration (for testing)
CMD_EMAIL_FROM_ADDRESS=dev@localhost
CMD_EMAIL_SMTP_HOST=localhost
CMD_EMAIL_SMTP_PORT=1025
```

## Custom Port Configuration

### Using Different Ports

If port 3000 is busy, modify the docker-compose configuration:

```yaml
services:
  hedgedoc:
    ports:
      - "3001:3000"  # Map external 3001 to internal 3000
    environment:
      - CMD_PORT=3000  # Internal port stays 3000
      - CMD_DOMAIN=localhost:3001  # Domain includes external port
```

### Environment-Based Port Configuration

```bash
# In .env.local
HEDGEDOC_EXTERNAL_PORT=3001
HEDGEDOC_INTERNAL_PORT=3000
HEDGEDOC_DOMAIN=localhost:3001
```

```yaml
# In docker-compose.local.yml
services:
  hedgedoc:
    ports:
      - "${HEDGEDOC_EXTERNAL_PORT}:${HEDGEDOC_INTERNAL_PORT}"
    environment:
      - CMD_PORT=${HEDGEDOC_INTERNAL_PORT}
      - CMD_DOMAIN=${HEDGEDOC_DOMAIN}
```

## Data Persistence and Backup

### Simple Backup Strategy

```bash
#!/bin/bash
# backup-local.sh

BACKUP_DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="./backups"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Backup SQLite database (if using SQLite)
if [ -f "./hedgedoc-data/hedgedoc.sqlite" ]; then
    cp "./hedgedoc-data/hedgedoc.sqlite" "$BACKUP_DIR/hedgedoc_${BACKUP_DATE}.sqlite"
    echo "Database backed up: $BACKUP_DIR/hedgedoc_${BACKUP_DATE}.sqlite"
fi

# Backup uploads directory
if [ -d "./hedgedoc-uploads" ]; then
    tar -czf "$BACKUP_DIR/hedgedoc_uploads_${BACKUP_DATE}.tar.gz" ./hedgedoc-uploads/
    echo "Uploads backed up: $BACKUP_DIR/hedgedoc_uploads_${BACKUP_DATE}.tar.gz"
fi

# Cleanup old backups (keep 10 most recent)
ls -t "$BACKUP_DIR" | tail -n +11 | xargs -I {} rm -f "$BACKUP_DIR/{}"

echo "Backup completed: $BACKUP_DATE"
```

### Volume Mapping for Persistence

For easier access to data files:

```yaml
volumes:
  # Map to local directories for easy access
  - ./data/hedgedoc:/data                      # Database and app data
  - ./data/uploads:/hedgedoc/public/uploads    # Uploaded files
  - ./config:/hedgedoc/config:ro               # Configuration files (read-only)
```

## Development Workflow Integration

### Integration with Code Editors

#### VS Code Integration

Create `.vscode/tasks.json`:

```json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Start HedgeDoc",
            "type": "shell",
            "command": "docker-compose -f docker-compose.local.yml up -d",
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always"
            }
        },
        {
            "label": "Stop HedgeDoc",
            "type": "shell",
            "command": "docker-compose -f docker-compose.local.yml down",
            "group": "build"
        },
        {
            "label": "View HedgeDoc Logs",
            "type": "shell",
            "command": "docker-compose -f docker-compose.local.yml logs -f",
            "group": "test",
            "presentation": {
                "reveal": "always"
            }
        }
    ]
}
```

#### Shell Aliases

Add to your shell profile (`.bashrc`, `.zshrc`, etc.):

```bash
# HedgeDoc Development Aliases
alias hd-start='docker-compose -f docker-compose.local.yml up -d'
alias hd-stop='docker-compose -f docker-compose.local.yml down'
alias hd-logs='docker-compose -f docker-compose.local.yml logs -f'
alias hd-restart='docker-compose -f docker-compose.local.yml restart'
alias hd-clean='docker-compose -f docker-compose.local.yml down -v'
alias hd-backup='./backup-local.sh'
```

### Git Integration

#### .gitignore for HedgeDoc Development

```gitignore
# HedgeDoc Local Development
docker-compose.local.yml
.env.local
hedgedoc-data/
hedgedoc-uploads/
backups/
logs/

# Docker volumes
data/
config/local/

# Temporary files
*.tmp
*.log
```

#### Development Branch Workflow

```bash
# Setup development environment
git clone <your-presentation-repo>
cd <your-presentation-repo>

# Create local development branch
git checkout -b local-development

# Setup HedgeDoc
cp docker-compose.local.yml.example docker-compose.local.yml
cp .env.local.example .env.local

# Start development environment
docker-compose -f docker-compose.local.yml up -d

# Access HedgeDoc at http://localhost:3000
```

## Testing and Validation

### Health Check Script

```bash
#!/bin/bash
# health-check.sh

HEDGEDOC_URL="http://localhost:3000"
TIMEOUT=10

echo "Checking HedgeDoc health..."

# Check if service is responding
if curl -s --max-time $TIMEOUT "$HEDGEDOC_URL/status" > /dev/null; then
    echo "✅ HedgeDoc is responding"
else
    echo "❌ HedgeDoc is not responding"
    exit 1
fi

# Check if we can create a note
if curl -s --max-time $TIMEOUT "$HEDGEDOC_URL/new" > /dev/null; then
    echo "✅ Note creation endpoint accessible"
else
    echo "❌ Note creation endpoint not accessible"
    exit 1
fi

# Check Docker container status
CONTAINER_STATUS=$(docker inspect -f '{{.State.Status}}' hedgedoc-local 2>/dev/null)
if [ "$CONTAINER_STATUS" = "running" ]; then
    echo "✅ Docker container is running"
else
    echo "❌ Docker container is not running (Status: $CONTAINER_STATUS)"
    exit 1
fi

echo "🎉 All health checks passed!"
```

### Load Testing for Development

Simple load test script:

```bash
#!/bin/bash
# load-test-simple.sh

HEDGEDOC_URL="http://localhost:3000"
CONCURRENT_USERS=5
TEST_DURATION=30

echo "Running simple load test..."
echo "URL: $HEDGEDOC_URL"
echo "Concurrent users: $CONCURRENT_USERS"
echo "Duration: ${TEST_DURATION}s"

# Using Apache Bench (if available)
if command -v ab > /dev/null; then
    ab -n 100 -c $CONCURRENT_USERS "$HEDGEDOC_URL/"
else
    echo "Apache Bench not available. Install with: apt-get install apache2-utils"
fi
```

## Troubleshooting Common Local Issues

### Port Already in Use

```bash
# Check what's using port 3000
lsof -i :3000

# Kill process using port 3000
kill $(lsof -t -i:3000)

# Or use different port in docker-compose
ports:
  - "3001:3000"
```

### Database Connection Issues

```bash
# Check SQLite database file
ls -la ./hedgedoc-data/hedgedoc.sqlite

# Check PostgreSQL connection (if using)
docker exec hedgedoc-db-dev pg_isready -U hedgedoc

# Reset database (SQLite)
rm ./hedgedoc-data/hedgedoc.sqlite
docker-compose -f docker-compose.local.yml restart
```

### Volume Mount Issues

```bash
# Check volume permissions
ls -la ./hedgedoc-data
ls -la ./hedgedoc-uploads

# Fix permissions (Linux/macOS)
sudo chown -R $(id -u):$(id -g) ./hedgedoc-data
sudo chown -R $(id -u):$(id -g) ./hedgedoc-uploads

# Windows: Use Docker Desktop volume management
```

### Container Startup Issues

```bash
# Check container logs
docker-compose -f docker-compose.local.yml logs hedgedoc

# Check container status
docker ps -a

# Restart specific service
docker-compose -f docker-compose.local.yml restart hedgedoc

# Full reset (removes all data)
docker-compose -f docker-compose.local.yml down -v
docker system prune -f
```

## Development Tips and Best Practices

### Performance Optimization for Development

1. **Use SQLite for Single-User Development**: Faster startup, no additional containers
2. **Bind Mount for Quick Iteration**: Map config files for immediate changes
3. **Disable Unnecessary Features**: Turn off email, external auth for local dev
4. **Use Local Networks**: Keep everything on localhost for speed

### Security Considerations for Development

1. **Never Use Development Config in Production**: Different secrets, permissions
2. **Keep Development Data Isolated**: Separate volumes, different ports
3. **Use Temporary Credentials**: Change default passwords and secrets
4. **Regular Cleanup**: Remove old containers and volumes periodically

### Best Development Practices

1. **Version Control Your Setup**: Include docker-compose and env files
2. **Document Custom Configurations**: Note any special setup requirements
3. **Regular Backups**: Even in development, backup important notes
4. **Monitor Resource Usage**: Keep an eye on Docker resource consumption

## Quick Reference Commands

```bash
# Essential commands for daily development
docker-compose -f docker-compose.local.yml up -d      # Start
docker-compose -f docker-compose.local.yml down       # Stop
docker-compose -f docker-compose.local.yml logs -f    # View logs
docker-compose -f docker-compose.local.yml restart    # Restart
docker-compose -f docker-compose.local.yml down -v    # Clean reset

# Access HedgeDoc
open http://localhost:3000                             # macOS
xdg-open http://localhost:3000                         # Linux

# Container management
docker exec -it hedgedoc-local sh                      # Access container
docker stats                                          # Monitor resources
```

This guide provides everything needed to get HedgeDoc running locally for development and testing purposes, with minimal configuration and maximum simplicity.