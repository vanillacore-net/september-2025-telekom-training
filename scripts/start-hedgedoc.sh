#!/bin/bash

# HedgeDoc Local Development Start Script
echo "🚀 Starting HedgeDoc for local development..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null 2>&1; then
    echo "❌ Docker Compose is not available. Please install Docker Compose."
    exit 1
fi

# Use docker-compose or docker compose based on availability
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

echo "📦 Pulling latest HedgeDoc image..."
$DOCKER_COMPOSE pull hedgedoc

echo "🔧 Starting HedgeDoc container..."
$DOCKER_COMPOSE up -d

echo "⏳ Waiting for HedgeDoc to be ready..."
sleep 10

# Check if container is running
if [ "$(docker inspect -f '{{.State.Running}}' hedgedoc-dev 2>/dev/null)" == "true" ]; then
    echo "✅ HedgeDoc is now running!"
    echo ""
    echo "🌐 Access HedgeDoc at: http://localhost:3000"
    echo "📁 Presentation files are mounted from: ./presentation/"
    echo "📝 Anonymous editing is enabled for development"
    echo ""
    echo "💡 Tip: Use 'docker-compose logs -f hedgedoc' to view logs"
    echo "🛑 Tip: Use './stop-hedgedoc.sh' to stop the service"
else
    echo "❌ Failed to start HedgeDoc. Check logs with: $DOCKER_COMPOSE logs hedgedoc"
    exit 1
fi