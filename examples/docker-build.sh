#!/bin/bash

# Docker Build Script for Architecture Training Project
# Author: DevOps Team
# Description: Automates Docker build, test, and deployment processes

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="architecture-training"
IMAGE_NAME="telekom/${PROJECT_NAME}"
COMPOSE_FILE="docker-compose.yml"

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

show_usage() {
    cat << EOF
Usage: $0 [OPTION]

Docker Build Script for Architecture Training Project

OPTIONS:
    build           Build Docker images
    build-clean     Build Docker images with no cache
    test            Run tests in container
    dev             Start development environment
    ci              Run CI/CD pipeline (clean build + test)
    clean           Clean up containers and images
    logs            Show container logs
    shell           Open interactive shell in container
    help            Show this help message

EXAMPLES:
    $0 build        # Build the Docker image
    $0 test         # Run all tests
    $0 dev          # Start development environment
    $0 ci           # Full CI/CD pipeline
    $0 clean        # Clean up everything

EOF
}

build_image() {
    log_info "Building Docker image..."
    if [ "$1" = "clean" ]; then
        docker-compose build --no-cache
    else
        docker-compose build
    fi
    log_success "Docker image built successfully"
}

run_tests() {
    log_info "Running tests in container..."
    docker-compose run --rm maven-build mvn test -Dcheckstyle.skip=true -B
    local exit_code=$?
    if [ $exit_code -eq 0 ]; then
        log_success "All tests passed!"
    else
        log_error "Tests failed with exit code $exit_code"
        exit $exit_code
    fi
}

run_full_build() {
    log_info "Running full build..."
    docker-compose run --rm maven-build mvn verify -Dcheckstyle.skip=true -B
    local exit_code=$?
    if [ $exit_code -eq 0 ]; then
        log_success "Full build completed successfully!"
    else
        log_error "Build failed with exit code $exit_code"
        exit $exit_code
    fi
}

start_dev_environment() {
    log_info "Starting development environment..."
    docker-compose up -d maven-dev
    log_success "Development environment started"
    log_info "Access container with: $0 shell"
    log_info "Stop with: docker-compose down"
}

run_ci_pipeline() {
    log_info "Running CI/CD Pipeline..."
    log_info "Step 1: Clean build"
    build_image clean
    
    log_info "Step 2: Run tests"
    run_tests
    
    log_info "Step 3: Full build with verification"
    run_full_build
    
    log_success "CI/CD Pipeline completed successfully!"
}

cleanup() {
    log_info "Cleaning up containers and images..."
    docker-compose down -v --remove-orphans
    docker system prune -f
    log_success "Cleanup completed"
}

show_logs() {
    log_info "Showing container logs..."
    docker-compose logs -f
}

open_shell() {
    log_info "Opening interactive shell..."
    docker-compose exec maven-dev bash || docker-compose run --rm maven-build bash
}

# Verify Docker is running
if ! docker info >/dev/null 2>&1; then
    log_error "Docker is not running. Please start Docker first."
    exit 1
fi

# Parse command line arguments
case "${1:-help}" in
    build)
        build_image
        ;;
    build-clean)
        build_image clean
        ;;
    test)
        run_tests
        ;;
    dev)
        start_dev_environment
        ;;
    ci)
        run_ci_pipeline
        ;;
    clean)
        cleanup
        ;;
    logs)
        show_logs
        ;;
    shell)
        open_shell
        ;;
    help|--help|-h)
        show_usage
        ;;
    *)
        log_error "Unknown option: $1"
        show_usage
        exit 1
        ;;
esac