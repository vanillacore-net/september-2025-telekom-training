#!/bin/bash

# Script to import presentations into HedgeDoc
# This script waits for HedgeDoc to be ready then imports all presentations

# Wait for HedgeDoc to be ready
echo "Waiting for HedgeDoc to be ready..."
until curl -s http://localhost:3000/status > /dev/null; do
  sleep 2
  echo "Still waiting for HedgeDoc..."
done

echo "HedgeDoc is ready! Starting presentation import..."

# Import all presentations with friendly names
echo "Importing Day 1 presentation..."
curl -X POST -H "Content-Type: text/markdown" \
  --data-binary @/hedgedoc/public/day1.md \
  http://localhost:3000/new/day1-telekom-architecture 2>/dev/null

echo "Importing Day 2 presentation..."
curl -X POST -H "Content-Type: text/markdown" \
  --data-binary @/hedgedoc/public/day2.md \
  http://localhost:3000/new/day2-telekom-architecture 2>/dev/null

echo "Importing Day 3 presentation..."
curl -X POST -H "Content-Type: text/markdown" \
  --data-binary @/hedgedoc/public/day3.md \
  http://localhost:3000/new/day3-telekom-architecture 2>/dev/null

echo "Importing Day 4 presentation..."
curl -X POST -H "Content-Type: text/markdown" \
  --data-binary @/hedgedoc/public/day4.md \
  http://localhost:3000/new/day4-telekom-architecture 2>/dev/null

echo "All presentations imported successfully!"
echo ""
echo "Presentations are now available at:"
echo "Day 1: http://localhost:3000/day1-telekom-architecture"
echo "Day 2: http://localhost:3000/day2-telekom-architecture"
echo "Day 3: http://localhost:3000/day3-telekom-architecture"
echo "Day 4: http://localhost:3000/day4-telekom-architecture"
echo ""
echo "Slide modes are available at:"
echo "Day 1: http://localhost:3000/p/day1-telekom-architecture"
echo "Day 2: http://localhost:3000/p/day2-telekom-architecture"
echo "Day 3: http://localhost:3000/p/day3-telekom-architecture"
echo "Day 4: http://localhost:3000/p/day4-telekom-architecture"