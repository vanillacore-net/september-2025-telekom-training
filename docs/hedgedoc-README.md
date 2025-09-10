# HedgeDoc Local Development Setup

This setup provides a basic HedgeDoc environment for local development and presentation testing.

## Quick Start

### Prerequisites
- Docker
- Docker Compose

### Starting HedgeDoc

```bash
# Start the services
./start-hedgedoc.sh

# OR manually
docker compose up -d
```

### Access HedgeDoc
- URL: http://localhost:3000
- Anonymous access: Enabled for development
- Anonymous editing: Enabled

### Stopping HedgeDoc

```bash
# Stop the services
./stop-hedgedoc.sh

# OR manually
docker compose down
```

## Architecture

- **HedgeDoc**: Main application (Port 3000)
- **PostgreSQL**: Database backend
- **Volume Mounts**: 
  - `./presentation` mounted to HedgeDoc for presentation files
  - Persistent storage for uploads and database

## Development Features

- Anonymous access enabled
- Anonymous editing allowed
- Presentation files accessible
- Simple PostgreSQL database
- Persistent data storage

## Files

- `docker-compose.yml`: Service definitions
- `.env`: Environment configuration
- `start-hedgedoc.sh`: Quick start script
- `stop-hedgedoc.sh`: Quick stop script

## Configuration

Basic development configuration in `.env`:
- Database: PostgreSQL (containerized)
- Port: 3000 (exposed)
- Anonymous access: Enabled
- Session secret: Development only

## Security Notice

This setup is for LOCAL DEVELOPMENT ONLY:
- Weak passwords used
- Anonymous access enabled
- No SSL/HTTPS
- Development secrets

DO NOT use in production!