#!/bin/bash

# Upload presentations to HedgeDoc using the API
# Requires CMD_ALLOW_FREEURL=true on HedgeDoc instance

set -e

# Configuration
HEDGEDOC_URL="http://localhost:3000"
PRESENTATION_DIR="../presentation/hedgedoc"

# Function to upload presentation via API
upload_presentation() {
    local file_path="$1"
    local alias="$2"
    local title="$3"
    
    if [ ! -f "$file_path" ]; then
        echo "Error: File $file_path not found"
        return 1
    fi
    
    echo "Uploading $title to alias: $alias"
    
    # Read the markdown content
    content=$(cat "$file_path")
    
    # Create note with custom alias using POST to /new/<alias>
    response=$(curl -X POST \
        -H "Content-Type: text/markdown" \
        --data-raw "$content" \
        -w "\n%{http_code}" \
        -s \
        "${HEDGEDOC_URL}/new/${alias}")
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "302" ] || [ "$http_code" = "200" ]; then
        echo "✅ Successfully uploaded: ${HEDGEDOC_URL}/${alias}"
    else
        echo "❌ Failed to upload $title (HTTP $http_code)"
        echo "Response: $response"
        return 1
    fi
}

# Main upload process
echo "Starting HedgeDoc presentation upload via API..."
echo "Target: $HEDGEDOC_URL"
echo ""

# Upload all presentations with their aliases
upload_presentation "$PRESENTATION_DIR/hedgedoc-intro.md" "intro-design-patterns" "Software-Architektur - Einführung"
upload_presentation "$PRESENTATION_DIR/hedgedoc-day1.md" "day1-design-patterns" "Software-Architektur - Tag 1: Erzeugungsmuster"
upload_presentation "$PRESENTATION_DIR/hedgedoc-day2.md" "day2-design-patterns" "Software-Architektur - Tag 2: Strukturmuster"
upload_presentation "$PRESENTATION_DIR/hedgedoc-day3.md" "day3-design-patterns" "Software-Architektur - Tag 3: Verhaltensmuster"
upload_presentation "$PRESENTATION_DIR/hedgedoc-day4.md" "day4-design-patterns" "Software-Architektur - Tag 4: Erweiterte Muster"

echo ""
echo "Upload complete!"
echo ""
echo "Access presentations at:"
echo "  - Intro: ${HEDGEDOC_URL}/intro-design-patterns"
echo "  - Day 1: ${HEDGEDOC_URL}/day1-design-patterns"
echo "  - Day 2: ${HEDGEDOC_URL}/day2-design-patterns"
echo "  - Day 3: ${HEDGEDOC_URL}/day3-design-patterns"
echo "  - Day 4: ${HEDGEDOC_URL}/day4-design-patterns"