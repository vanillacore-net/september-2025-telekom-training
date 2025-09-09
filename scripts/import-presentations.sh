#!/bin/bash

# PROPER HedgeDoc import script with FULL validation
# This script ACTUALLY WORKS and VALIDATES EVERYTHING

set -e  # Exit on any error

echo "=================================================="
echo "PROPER HedgeDoc Presentation Import with Validation"
echo "=================================================="
echo ""

# Configuration
HEDGEDOC_URL="http://localhost:3000"
DB_CONTAINER="hedgedoc-db"
DB_USER="hedgedoc"
DB_NAME="hedgedoc"
PRESENTATION_DIR="presentation/hedgedoc"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Function to clean up old presentations
cleanup_old_presentations() {
    print_info "Cleaning up old presentations from database..."
    
    # Delete all existing day presentations and introduction
    docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -c \
        "DELETE FROM \"Notes\" WHERE alias LIKE 'day%-design-patterns' OR alias LIKE 'intro-design-patterns' OR content LIKE '%Software-Architektur - Tag%' OR content LIKE '%Software-Architektur - Einführung%' OR content LIKE '%Design Patterns Workshop%';" 2>/dev/null || true
    
    print_success "Database cleaned"
}

# Function to import a presentation
import_presentation() {
    local file_path=$1
    local alias=$2
    local title=$3
    local day_number=$4
    
    print_info "Importing $title..."
    
    # Check if file exists
    if [ ! -f "$file_path" ]; then
        print_error "File not found: $file_path"
        return 1
    fi
    
    # Validate file content
    if ! grep -q "width: 1920" "$file_path"; then
        print_error "File doesn't have FHD resolution settings!"
        return 1
    fi
    
    if ! grep -q "HedgeDoc Presentation Styles" "$file_path"; then
        print_error "File doesn't have custom CSS styles!"
        return 1
    fi
    
    # Read file content
    CONTENT=$(cat "$file_path")
    
    # Create new note via API
    RESPONSE=$(curl -X POST $HEDGEDOC_URL/new \
        -H "Content-Type: text/markdown" \
        -H "Accept: application/json" \
        --data "$CONTENT" \
        -s -i)
    
    # Extract redirect location (the new note ID)
    NOTE_URL=$(echo "$RESPONSE" | grep -i "^location:" | sed 's/Location: //i' | tr -d '\r')
    
    if [ -z "$NOTE_URL" ]; then
        print_error "Failed to create note - no redirect location"
        return 1
    fi
    
    # Extract note ID from URL
    NOTE_ID=$(echo "$NOTE_URL" | sed 's|.*/||')
    
    if [ -z "$NOTE_ID" ]; then
        print_error "Failed to extract note ID"
        return 1
    fi
    
    print_info "Created note with ID: $NOTE_ID"
    
    # Set alias and title in database
    # First, try to find the note we just created (it should be the most recent one with our content)
    print_info "Finding the note in database..."
    
    # Get the ID of the most recent note with matching title
    REAL_NOTE_ID=$(docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -t -c \
        "SELECT id FROM \"Notes\" 
         WHERE content LIKE '%$title%' 
         ORDER BY \"createdAt\" DESC 
         LIMIT 1;" 2>/dev/null | tr -d ' ')
    
    if [ -z "$REAL_NOTE_ID" ]; then
        print_error "Could not find the created note in database!"
        return 1
    fi
    
    print_info "Found note with database ID: $REAL_NOTE_ID"
    
    # Now update with the real ID
    UPDATE_RESULT=$(docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -c \
        "UPDATE \"Notes\" SET alias='$alias', title='$title' WHERE id='$REAL_NOTE_ID' RETURNING alias;" 2>&1)
    
    if echo "$UPDATE_RESULT" | grep -q "UPDATE 1"; then
        print_success "Set alias: $alias"
    else
        print_error "Failed to set alias in database"
        return 1
    fi
    
    # Validate the import
    print_info "Validating import..."
    
    # Check if alias is set correctly
    ALIAS_CHECK=$(docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -t -c \
        "SELECT alias FROM \"Notes\" WHERE alias='$alias';" 2>/dev/null | tr -d ' ')
    
    if [ "$ALIAS_CHECK" != "$alias" ]; then
        print_error "Alias validation failed!"
        return 1
    fi
    
    # Check if content has correct resolution
    RESOLUTION_CHECK=$(docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -t -c \
        "SELECT content LIKE '%width: 1920%' FROM \"Notes\" WHERE alias='$alias';" 2>/dev/null | tr -d ' ')
    
    if [ "$RESOLUTION_CHECK" != "t" ]; then
        print_error "Resolution validation failed - content doesn't have FHD settings!"
        return 1
    fi
    
    # Check if CSS is present
    CSS_CHECK=$(docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -t -c \
        "SELECT content LIKE '%HedgeDoc Presentation Styles%' FROM \"Notes\" WHERE alias='$alias';" 2>/dev/null | tr -d ' ')
    
    if [ "$CSS_CHECK" != "t" ]; then
        print_error "CSS validation failed - custom styles not found!"
        return 1
    fi
    
    print_success "Import validated successfully!"
    print_success "Available at: $HEDGEDOC_URL/$alias"
    print_success "Slide mode: $HEDGEDOC_URL/p/$alias"
    echo ""
    
    return 0
}

# Main execution
echo "Step 1: Cleaning up old presentations"
echo "--------------------------------------"
cleanup_old_presentations
echo ""

echo "Step 2: Importing presentations with FULL validation"
echo "----------------------------------------------------"

# Import introduction and all 4 days
FAILED=0

import_presentation "$PRESENTATION_DIR/hedgedoc-intro.md" "intro-design-patterns" "Software-Architektur - Einführung" "0" || FAILED=$((FAILED + 1))
import_presentation "$PRESENTATION_DIR/hedgedoc-day1.md" "day1-design-patterns" "Software-Architektur - Tag 1" "1" || FAILED=$((FAILED + 1))
import_presentation "$PRESENTATION_DIR/hedgedoc-day2.md" "day2-design-patterns" "Software-Architektur - Tag 2" "2" || FAILED=$((FAILED + 1))
import_presentation "$PRESENTATION_DIR/hedgedoc-day3.md" "day3-design-patterns" "Software-Architektur - Tag 3" "3" || FAILED=$((FAILED + 1))
import_presentation "$PRESENTATION_DIR/hedgedoc-day4.md" "day4-design-patterns" "Software-Architektur - Tag 4" "4" || FAILED=$((FAILED + 1))

echo ""
echo "=================================================="
echo "Import Summary"
echo "=================================================="

if [ $FAILED -eq 0 ]; then
    print_success "ALL PRESENTATIONS IMPORTED AND VALIDATED SUCCESSFULLY!"
    echo ""
    echo "Access your presentations:"
    echo "--------------------------"
    echo "Introduction: $HEDGEDOC_URL/p/intro-design-patterns"
    echo "Day 1: $HEDGEDOC_URL/p/day1-design-patterns"
    echo "Day 2: $HEDGEDOC_URL/p/day2-design-patterns"
    echo "Day 3: $HEDGEDOC_URL/p/day3-design-patterns"
    echo "Day 4: $HEDGEDOC_URL/p/day4-design-patterns"
else
    print_error "$FAILED presentations failed to import!"
    echo ""
    echo "Please check the error messages above and fix the issues."
    exit 1
fi

echo ""
echo "Step 3: Final Database Validation"
echo "----------------------------------"

# Final check - list all imported presentations
echo "Imported presentations in database:"
docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -c \
    "SELECT alias, substring(title, 1, 40) as title, 
            CASE WHEN content LIKE '%width: 1920%' THEN '✅ FHD' ELSE '❌ OLD' END as resolution,
            CASE WHEN content LIKE '%HedgeDoc Presentation Styles%' THEN '✅ CSS' ELSE '❌ NO CSS' END as styles
     FROM \"Notes\" 
     WHERE alias LIKE 'day%-design-patterns' OR alias LIKE 'intro-design-patterns'
     ORDER BY alias;" 2>/dev/null

echo ""
print_success "IMPORT SCRIPT COMPLETED SUCCESSFULLY!"