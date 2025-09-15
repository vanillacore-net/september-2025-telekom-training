# HedgeDoc Import Best Practice

## CRITICAL: Proper Import Procedure

### 1. PRE-IMPORT VALIDATION
**ALWAYS validate source files BEFORE import:**
```bash
# Check files exist and have content
for file in presentation/hedgedoc/hedgedoc-day*.md; do
  echo "Checking $file: $(wc -l $file)"
  head -5 "$file"
done
```

### 2. DATABASE CLEANUP
**MANDATORY: Clean database completely before import:**
```bash
# Delete ALL existing notes to avoid conflicts
docker exec hedgedoc-db psql -U hedgedoc -d hedgedoc -c "DELETE FROM \"Notes\";"

# Verify deletion
docker exec hedgedoc-db psql -U hedgedoc -d hedgedoc -c "SELECT COUNT(*) FROM \"Notes\";"
```

### 3. IMPORT PROCESS
**Use the WORKING import script:**
```bash
# Import using the validated script
./scripts/import-presentations-working.sh

# The script MUST:
# - Use curl with proper content type
# - Capture the note ID from response
# - Set aliases correctly in database
```

### 4. POST-IMPORT VALIDATION
**CRITICAL: ALWAYS validate after import:**
```bash
# Check database entries
docker exec hedgedoc-db psql -U hedgedoc -d hedgedoc -c \
  "SELECT shortid, alias, title, LENGTH(content) as content_length FROM \"Notes\" ORDER BY \"createdAt\" DESC;"

# Verify content exists (MUST be > 0)
# All 4 presentations should have:
# - Proper aliases (day1-design-patterns, etc.)
# - Content length > 20000 characters
# - Correct titles
```

### 5. BROWSER VALIDATION
**MANDATORY: Test in browser:**
```bash
# Check each presentation loads
for i in 1 2 3 4; do
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:3000/day$i-design-patterns
done

# All should return 200 OK
```

### 6. CONTENT VALIDATION
**Verify actual content is visible:**
```bash
# Check for German content
curl -s http://localhost:3000/day1-design-patterns | grep -c "Tag 1"
# Should return > 0

# Take screenshot for visual validation
node validate-presentations.js
```

## COMMON FAILURES AND FIXES

### Problem: Empty content (0 bytes)
**Cause:** Import script not reading files correctly
**Fix:** Ensure script uses `cat "$file"` and proper curl data format

### Problem: No aliases set
**Cause:** Database update failing
**Fix:** Use shortid from response, not UUID

### Problem: Content not showing in browser
**Cause:** Old cached content
**Fix:** Clear browser cache, restart HedgeDoc if needed

### Problem: Import creates duplicates
**Cause:** Not cleaning database first
**Fix:** ALWAYS delete all notes before import

## IMPORT SCRIPT REQUIREMENTS

The import script MUST:
1. Read file content with `cat`
2. Use `curl -X POST` with proper headers
3. Extract note ID from Location header
4. Update database with alias using extracted ID
5. Verify each import succeeded

## VALIDATION CHECKLIST

Before declaring import successful:
- [ ] Database shows 4 entries with content > 20KB each
- [ ] All aliases are set correctly
- [ ] Browser loads all 4 presentations
- [ ] Content is visible (not empty)
- [ ] German text is present
- [ ] CSS styles are applied
- [ ] No error messages in logs

## DO NOT PROCEED WITHOUT VALIDATION!

NEVER assume import worked without checking ALL validation points above.