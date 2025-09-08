# HedgeDoc Docker Setup - Production Guide

## Overview

HedgeDoc is a real-time collaborative markdown editor perfect for creating presentations with reveal.js integration. This guide provides production-ready Docker deployment configurations with security hardening and performance optimization.

## Quick Start

### Minimal Docker Compose

```yaml
version: '3.8'

services:
  hedgedoc:
    image: quay.io/hedgedoc/hedgedoc:1.9.9
    container_name: hedgedoc
    environment:
      - CMD_DB_URL=postgres://hedgedoc:${DB_PASSWORD}@database:5432/hedgedoc
      - CMD_DOMAIN=${DOMAIN}
      - CMD_URL_ADDPORT=false
      - CMD_PROTOCOL_USESSL=true
    ports:
      - "3000:3000"
    volumes:
      - uploads:/hedgedoc/public/uploads
    depends_on:
      - database
    restart: unless-stopped

  database:
    image: postgres:15-alpine
    container_name: hedgedoc-db
    environment:
      - POSTGRES_USER=hedgedoc
      - POSTGRES_PASSWORD=${DB_PASSWORD}
      - POSTGRES_DB=hedgedoc
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  uploads:
  postgres_data:
```

### Environment File (.env)

```bash
# Domain Configuration
DOMAIN=hedgedoc.yourdomain.com

# Database
DB_PASSWORD=your_secure_password_here
POSTGRES_PASSWORD=your_secure_password_here

# SSL Configuration
CMD_PROTOCOL_USESSL=true
CMD_URL_ADDPORT=false
CMD_HSTS_ENABLE=true
CMD_HSTS_MAX_AGE=31536000

# Session Security
CMD_SESSION_SECRET=your_64_character_random_secret_here
CMD_SESSION_LIFE=604800000
```

## Production Configuration

### Complete Docker Compose with Reverse Proxy

```yaml
version: '3.8'

services:
  # Nginx Reverse Proxy
  nginx:
    image: nginx:1.25-alpine
    container_name: hedgedoc-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
      - nginx_cache:/var/cache/nginx
    depends_on:
      - hedgedoc
    restart: unless-stopped

  # HedgeDoc Application
  hedgedoc:
    image: quay.io/hedgedoc/hedgedoc:1.9.9
    container_name: hedgedoc
    environment:
      # Database Configuration
      - CMD_DB_URL=postgres://hedgedoc:${DB_PASSWORD}@database:5432/hedgedoc
      
      # Domain Configuration
      - CMD_DOMAIN=${DOMAIN}
      - CMD_URL_ADDPORT=false
      - CMD_PROTOCOL_USESSL=true
      
      # Security Configuration
      - CMD_SESSION_SECRET=${SESSION_SECRET}
      - CMD_SESSION_LIFE=${SESSION_LIFE}
      - CMD_HSTS_ENABLE=true
      - CMD_HSTS_MAX_AGE=31536000
      - CMD_CSP_ENABLE=true
      
      # Upload Configuration
      - CMD_ALLOW_ANONYMOUS=false
      - CMD_ALLOW_ANONYMOUS_EDITS=false
      - CMD_ALLOW_FREEURL=true
      - CMD_DEFAULT_PERMISSION=limited
      
      # Image Upload
      - CMD_IMAGE_UPLOAD_TYPE=filesystem
      - CMD_ALLOW_GRAVATAR=false
      
      # Performance
      - CMD_WORKER_PROCESSES=2
    volumes:
      - uploads:/hedgedoc/public/uploads
      - ./custom:/hedgedoc/public/custom:ro
    depends_on:
      - database
      - redis
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:3000/status"]
      interval: 30s
      timeout: 10s
      retries: 3

  # PostgreSQL Database
  database:
    image: postgres:15-alpine
    container_name: hedgedoc-db
    environment:
      - POSTGRES_USER=hedgedoc
      - POSTGRES_PASSWORD=${DB_PASSWORD}
      - POSTGRES_DB=hedgedoc
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./postgresql.conf:/etc/postgresql/postgresql.conf:ro
    command: postgres -c config_file=/etc/postgresql/postgresql.conf
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U hedgedoc"]
      interval: 30s
      timeout: 5s
      retries: 3

  # Redis for Session Storage
  redis:
    image: redis:7-alpine
    container_name: hedgedoc-redis
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_data:/data
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 30s
      timeout: 3s
      retries: 3

  # Backup Service
  backup:
    image: prodrigestivill/postgres-backup-local:15
    container_name: hedgedoc-backup
    environment:
      - POSTGRES_HOST=database
      - POSTGRES_DB=hedgedoc
      - POSTGRES_USER=hedgedoc
      - POSTGRES_PASSWORD=${DB_PASSWORD}
      - BACKUP_KEEP_DAYS=30
      - BACKUP_KEEP_WEEKS=8
      - BACKUP_KEEP_MONTHS=6
    volumes:
      - ./backups:/backups
    depends_on:
      - database
    restart: unless-stopped

volumes:
  uploads:
  postgres_data:
  redis_data:
  nginx_cache:

networks:
  default:
    driver: bridge
```

### Nginx Configuration

```nginx
# nginx.conf
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    # Security Headers
    add_header X-Frame-Options SAMEORIGIN always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    # Gzip Compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1000;
    gzip_types text/plain text/css text/javascript application/javascript application/json;
    
    # Rate Limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=5r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=1r/s;
    
    # Caching
    proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=hedgedoc:10m max_size=1g;
    
    upstream hedgedoc {
        server hedgedoc:3000;
    }
    
    server {
        listen 80;
        server_name your.domain.com;
        return 301 https://$server_name$request_uri;
    }
    
    server {
        listen 443 ssl http2;
        server_name your.domain.com;
        
        # SSL Configuration
        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
        ssl_prefer_server_ciphers off;
        ssl_session_cache shared:SSL:10m;
        
        # HSTS
        add_header Strict-Transport-Security "max-age=63072000" always;
        
        # Client Max Body Size (for uploads)
        client_max_body_size 50M;
        
        # WebSocket Support
        location /socket.io/ {
            proxy_pass http://hedgedoc;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # API Rate Limiting
        location /api/ {
            limit_req zone=api burst=10 nodelay;
            proxy_pass http://hedgedoc;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # Login Rate Limiting
        location /login {
            limit_req zone=login burst=3 nodelay;
            proxy_pass http://hedgedoc;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # Static Assets Caching
        location /uploads/ {
            proxy_cache hedgedoc;
            proxy_cache_valid 200 1d;
            proxy_pass http://hedgedoc;
        }
        
        # Main Application
        location / {
            proxy_pass http://hedgedoc;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_read_timeout 300s;
            proxy_connect_timeout 75s;
        }
    }
}
```

### PostgreSQL Performance Configuration

```ini
# postgresql.conf
# Memory Configuration
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB

# Checkpoint Configuration
checkpoint_segments = 8
checkpoint_completion_target = 0.9
wal_buffers = 16MB

# Performance
random_page_cost = 1.1
effective_io_concurrency = 200

# Logging
log_min_duration_statement = 1000
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '
log_checkpoints = on
log_connections = on
log_disconnections = on
log_lock_waits = on

# Security
ssl = on
```

## Environment Variables Reference

### Core Configuration

```bash
# Application
CMD_DOMAIN=your.domain.com
CMD_URL_ADDPORT=false
CMD_PROTOCOL_USESSL=true
CMD_PORT=3000

# Database
CMD_DB_URL=postgres://user:password@host:port/database

# Session Management
CMD_SESSION_SECRET=64_character_random_string
CMD_SESSION_LIFE=604800000  # 7 days in milliseconds
CMD_SESSION_STORE=redis
CMD_REDIS_URL=redis://:password@redis:6379/0

# Security
CMD_HSTS_ENABLE=true
CMD_HSTS_MAX_AGE=31536000
CMD_CSP_ENABLE=true
CMD_CSP_REPORT_URI=https://your.domain.com/csp-report

# Authentication
CMD_OAUTH2_PROVIDERNAME=OAuth2
CMD_OAUTH2_CLIENT_ID=your_client_id
CMD_OAUTH2_CLIENT_SECRET=your_client_secret
CMD_OAUTH2_BASEURL=https://oauth.provider.com
CMD_OAUTH2_TOKEN_URL=https://oauth.provider.com/token
CMD_OAUTH2_AUTHORIZATION_URL=https://oauth.provider.com/authorize
CMD_OAUTH2_USER_PROFILE_URL=https://oauth.provider.com/user

# LDAP Authentication
CMD_LDAP_URL=ldap://ldap.example.com
CMD_LDAP_BINDDN=cn=admin,dc=example,dc=com
CMD_LDAP_BINDCREDENTIALS=admin_password
CMD_LDAP_SEARCHBASE=ou=users,dc=example,dc=com
CMD_LDAP_SEARCHFILTER=(uid={{username}})
CMD_LDAP_SEARCHATTRIBUTES=displayName,mail
CMD_LDAP_USERIDFIELD=uid

# Upload Configuration
CMD_IMAGE_UPLOAD_TYPE=filesystem
CMD_ALLOW_GRAVATAR=false
CMD_MAX_DOC_LENGTH=100000
```

### Performance Settings

```bash
# Worker Configuration
CMD_WORKER_PROCESSES=2

# Rate Limiting
CMD_RATELIMIT_ENABLE=true
CMD_RATELIMIT_REQUESTS=300
CMD_RATELIMIT_DURATION=60000

# Cache Settings
CMD_CACHE_ENGINE=redis
CMD_CACHE_TTL=3600
```

## Volume Management

### Data Persistence Strategy

```yaml
volumes:
  # Application uploads
  uploads:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: /opt/hedgedoc/uploads
  
  # Database data
  postgres_data:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: /opt/hedgedoc/postgres
  
  # Redis data
  redis_data:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: /opt/hedgedoc/redis
```

### Backup Strategy

#### Database Backup Script

```bash
#!/bin/bash
# backup-db.sh

BACKUP_DIR="/opt/hedgedoc/backups"
DATE=$(date +%Y%m%d_%H%M%S)
CONTAINER="hedgedoc-db"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Database backup
docker exec "$CONTAINER" pg_dump -U hedgedoc hedgedoc | gzip > "$BACKUP_DIR/hedgedoc_db_$DATE.sql.gz"

# Upload files backup
tar -czf "$BACKUP_DIR/hedgedoc_uploads_$DATE.tar.gz" /opt/hedgedoc/uploads/

# Cleanup old backups (keep 30 days)
find "$BACKUP_DIR" -name "*.gz" -mtime +30 -delete

# Verify backup
if [ -f "$BACKUP_DIR/hedgedoc_db_$DATE.sql.gz" ]; then
    echo "Backup completed: $BACKUP_DIR/hedgedoc_db_$DATE.sql.gz"
else
    echo "Backup failed!"
    exit 1
fi
```

#### Automated Backup with Cron

```bash
# Add to crontab: crontab -e
# Daily backup at 2 AM
0 2 * * * /opt/hedgedoc/scripts/backup-db.sh >> /var/log/hedgedoc-backup.log 2>&1
```

### Restore Procedure

```bash
#!/bin/bash
# restore-db.sh

BACKUP_FILE="$1"
CONTAINER="hedgedoc-db"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file.sql.gz>"
    exit 1
fi

# Stop HedgeDoc
docker-compose stop hedgedoc

# Restore database
gunzip -c "$BACKUP_FILE" | docker exec -i "$CONTAINER" psql -U hedgedoc -d hedgedoc

# Restart services
docker-compose start hedgedoc

echo "Restore completed"
```

## Security Hardening

### SSL/TLS Configuration

```bash
# Generate self-signed certificate (development only)
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout ssl/key.pem \
  -out ssl/cert.pem \
  -subj "/C=US/ST=State/L=City/O=Organization/CN=your.domain.com"

# For production, use Let's Encrypt
certbot certonly --webroot -w /var/www/html -d your.domain.com
```

### Security Headers

Add to nginx configuration:

```nginx
# Security headers
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self' wss:; media-src 'self'; object-src 'none'; frame-ancestors 'self';" always;
```

### Firewall Configuration

```bash
# UFW rules
ufw allow ssh
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable

# Docker-specific rules
ufw route allow in on docker0
ufw route allow out on docker0
```

## Monitoring and Logging

### Health Checks

```yaml
healthcheck:
  test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:3000/status"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 40s
```

### Logging Configuration

```yaml
services:
  hedgedoc:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### Prometheus Monitoring

```yaml
# docker-compose.monitoring.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3001:3000"
    volumes:
      - grafana_data:/var/lib/grafana
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin

volumes:
  prometheus_data:
  grafana_data:
```

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   ```bash
   # Check database connectivity
   docker exec hedgedoc-db pg_isready -U hedgedoc
   
   # View database logs
   docker logs hedgedoc-db
   ```

2. **Upload Problems**
   ```bash
   # Check upload directory permissions
   docker exec hedgedoc ls -la /hedgedoc/public/uploads
   
   # Fix permissions
   docker exec hedgedoc chown -R hedgedoc:hedgedoc /hedgedoc/public/uploads
   ```

3. **Memory Issues**
   ```bash
   # Monitor resource usage
   docker stats
   
   # Increase memory limits
   # Add to docker-compose.yml under hedgedoc service:
   deploy:
     resources:
       limits:
         memory: 1G
       reservations:
         memory: 512M
   ```

### Log Analysis

```bash
# View application logs
docker logs hedgedoc --tail 100 -f

# Database logs
docker logs hedgedoc-db --tail 100 -f

# Nginx access logs
docker exec hedgedoc-nginx tail -f /var/log/nginx/access.log
```

## Deployment Checklist

### Pre-deployment

- [ ] Domain DNS configured
- [ ] SSL certificates obtained
- [ ] Environment variables configured
- [ ] Database credentials generated
- [ ] Backup strategy implemented
- [ ] Firewall rules configured

### Post-deployment

- [ ] Health checks passing
- [ ] SSL/TLS working correctly
- [ ] Authentication functional
- [ ] Upload functionality tested
- [ ] Backup restoration verified
- [ ] Monitoring alerts configured
- [ ] Performance benchmarked

### Maintenance Tasks

- [ ] Regular security updates
- [ ] Database optimization
- [ ] Log rotation configured
- [ ] Backup verification
- [ ] SSL certificate renewal
- [ ] Performance monitoring