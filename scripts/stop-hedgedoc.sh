#!/bin/bash

# HedgeDoc Local Development Stop Script
echo "🛑 Stopping HedgeDoc local development environment..."

# Use docker-compose or docker compose based on availability
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

echo "📦 Stopping HedgeDoc container..."
$DOCKER_COMPOSE down

echo "✅ HedgeDoc has been stopped."
echo ""
echo "💡 Note: Data is preserved in Docker volumes"
echo "🗑️  To remove all data: $DOCKER_COMPOSE down -v"