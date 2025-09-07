# Telekom Presentation System Docker Container
# Multi-stage build for optimized production image

FROM node:18-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install all dependencies (including dev)
RUN npm ci

# Copy source files
COPY . .

# Build presentations
RUN npm run build

# Production stage
FROM nginx:alpine AS production

# Install Node.js for dynamic content serving
RUN apk add --no-cache nodejs npm

# Copy custom nginx configuration
COPY docker/nginx.conf /etc/nginx/nginx.conf

# Create app directory
WORKDIR /app

# Copy built presentations and assets
COPY --from=builder /app/presentations /usr/share/nginx/html/presentations
COPY --from=builder /app/css /usr/share/nginx/html/css
COPY --from=builder /app/js /usr/share/nginx/html/js
COPY --from=builder /app/assets /usr/share/nginx/html/assets
COPY --from=builder /app/node_modules/reveal.js/dist /usr/share/nginx/html/node_modules/reveal.js/dist
COPY --from=builder /app/node_modules/reveal.js/plugin /usr/share/nginx/html/node_modules/reveal.js/plugin

# Copy package.json for runtime info
COPY --from=builder /app/package.json /app/

# Create main index page
RUN echo '<!doctype html>\
<html>\
<head>\
  <title>Telekom Architecture Training</title>\
  <meta charset="utf-8">\
  <meta name="viewport" content="width=device-width, initial-scale=1.0">\
  <style>\
    body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 40px; }\
    h1 { color: #FF6B00; }\
    .logo { max-width: 200px; margin-bottom: 20px; }\
    .presentations { list-style: none; padding: 0; }\
    .presentations li { margin: 15px 0; padding: 15px; background: #f5f5f5; border-radius: 8px; border-left: 4px solid #FF6B00; }\
    .presentations a { text-decoration: none; color: #333; font-weight: 600; font-size: 18px; }\
    .presentations a:hover { color: #FF6B00; }\
    .info { background: #e8f4fd; padding: 20px; border-radius: 8px; margin: 20px 0; }\
  </style>\
</head>\
<body>\
  <h1>🎯 Telekom Architecture Training</h1>\
  \
  <div class="info">\
    <h3>📋 Available Presentations</h3>\
    <p>Select a training module below to view the presentation:</p>\
  </div>\
  \
  <ul class="presentations">\
    <li><a href="/presentations/">📚 All Presentations</a></li>\
    <li><a href="/presentations/day1.html">📘 Day 1: Factory & Builder Patterns</a></li>\
    <li><a href="/presentations/day2.html">📗 Day 2: Structural Patterns</a></li>\
    <li><a href="/presentations/day3.html">📙 Day 3: Behavioral Patterns</a></li>\
    <li><a href="/presentations/day4.html">📕 Day 4: Advanced Patterns</a></li>\
    <li><a href="/presentations/intro.html">🚀 Introduction & Overview</a></li>\
  </ul>\
  \
  <div class="info">\
    <p><strong>🐳 Container Info:</strong> Telekom Architecture Training Presentations</p>\
    <p><strong>📅 Built:</strong> '$(date)'</p>\
    <p><strong>🔧 Version:</strong> 1.0.0</p>\
  </div>\
</body>\
</html>' > /usr/share/nginx/html/index.html

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Expose port
EXPOSE 80

# Labels for better container management
LABEL maintainer="telekom-architecture-training@telekom.de"
LABEL version="1.0.0"
LABEL description="Telekom Architecture Training Presentations"
LABEL org.opencontainers.image.title="Telekom Presentations"
LABEL org.opencontainers.image.description="Reveal.js based presentations for Telekom Architecture Training"
LABEL org.opencontainers.image.vendor="Deutsche Telekom"

# Start nginx
CMD ["nginx", "-g", "daemon off;"]