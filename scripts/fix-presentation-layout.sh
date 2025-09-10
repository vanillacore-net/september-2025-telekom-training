#!/bin/bash

# Fix presentation layout - reduce font sizes to fit content

echo "Fixing presentation layout to prevent content overflow..."

# Update all 4 presentation files with proper font sizing
for day in 1 2 3 4; do
    FILE="../presentation/hedgedoc/hedgedoc-day${day}.md"
    echo "Updating Day ${day}..."
    
    # Replace the base font size from 34px to something more reasonable like 20px
    sed -i '' 's/font-size: 34px/font-size: 20px/g' "$FILE"
    
    # Update heading sizes proportionally
    sed -i '' 's/font-size: 3\.4em/font-size: 2.0em/g' "$FILE"  # h1
    sed -i '' 's/font-size: 2\.6em/font-size: 1.6em/g' "$FILE"  # h2
    sed -i '' 's/font-size: 2\.3em/font-size: 1.4em/g' "$FILE"  # h3
    
    # Update padding/margins proportionally
    sed -i '' 's/padding: 38px/padding: 20px/g' "$FILE"
    sed -i '' 's/margin-top: 2em/margin-top: 1.2em/g' "$FILE"
    sed -i '' 's/margin-top: 1\.5em/margin-top: 0.9em/g' "$FILE"
    sed -i '' 's/margin-bottom: 1em/margin-bottom: 0.6em/g' "$FILE"
    
    echo "✅ Day ${day} updated"
done

echo ""
echo "Now re-importing presentations with fixed layout..."
cd /Users/karsten/Nextcloud/Work/Training/2025-09_Telekom_Architecture/scripts
./import-presentations.sh

echo "✅ Layout fixes complete!"