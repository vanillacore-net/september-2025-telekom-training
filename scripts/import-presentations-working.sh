#!/bin/bash

# Working script to import presentations into HedgeDoc
echo "Importing presentations into HedgeDoc using working method..."

# Function to create note with content and then set alias via database
create_note_with_alias() {
    local file=$1
    local alias=$2
    local title=$3
    
    echo "Creating $title..."
    
    # Create note with content
    CONTENT=$(cat "$file")
    RESPONSE=$(curl -X POST http://localhost:3000/new \
        -H "Content-Type: text/markdown" \
        --data "$CONTENT" \
        -v 2>&1 | grep "< Location:")
    
    # Extract the note ID from the Location header
    NOTE_ID=$(echo "$RESPONSE" | grep -o 'http://localhost:3000/[^[:space:]]*' | sed 's|http://localhost:3000/||')
    
    if [ -n "$NOTE_ID" ]; then
        # Set alias in database
        docker exec hedgedoc-db psql -U hedgedoc -d hedgedoc -c \
            "UPDATE \"Notes\" SET alias='$alias', title='$title' WHERE id='$NOTE_ID';" > /dev/null
        echo "✅ Created: http://localhost:3000/$alias"
    else
        echo "❌ Failed to create note for $title"
    fi
}

# Import all presentations
create_note_with_alias "presentation/hedgedoc/hedgedoc-day1.md" "day1-design-patterns" "Design Patterns Workshop - Day 1"
create_note_with_alias "presentation/hedgedoc/hedgedoc-day2.md" "day2-design-patterns" "Design Patterns Workshop - Day 2"
create_note_with_alias "presentation/hedgedoc/hedgedoc-day3.md" "day3-design-patterns" "Design Patterns Workshop - Day 3"
create_note_with_alias "presentation/hedgedoc/hedgedoc-day4.md" "day4-design-patterns" "Design Patterns Workshop - Day 4"

echo ""
echo "✅ All presentations imported successfully!"
echo ""
echo "Access your presentations at:"
echo "Day 1: http://localhost:3000/day1-design-patterns"
echo "Day 2: http://localhost:3000/day2-design-patterns"
echo "Day 3: http://localhost:3000/day3-design-patterns"
echo "Day 4: http://localhost:3000/day4-design-patterns"
echo ""
echo "Slide mode (add /p/ before the alias):"
echo "Day 1: http://localhost:3000/p/day1-design-patterns"
echo "Day 2: http://localhost:3000/p/day2-design-patterns"
echo "Day 3: http://localhost:3000/p/day3-design-patterns"
echo "Day 4: http://localhost:3000/p/day4-design-patterns"