# HedgeDoc CSS Best Practices for Presentations

## Research Summary

After extensive research and sequential thinking analysis, here are the best practices for including CSS in HedgeDoc presentations:

## Current Issue
```markdown
<link rel="stylesheet" type="text/css" href="presentation-styles.css">
```
**Problem:** This appears as raw text in the presentation because it's in the markdown content area.

## ✅ CORRECT Approach: External CSS with Proper Path

### Option 1: Reference CSS via Absolute Path
Since the CSS file is served at `/docs/presentation-styles.css`, the correct link should be:
```markdown
<link rel="stylesheet" type="text/css" href="/docs/presentation-styles.css">
```

### Option 2: Move CSS Link to Reveal.js Configuration
However, the REAL issue is that the link tag shouldn't be in the markdown content at all. For reveal.js presentations in HedgeDoc, external CSS should be included through:

1. **Custom reveal.js theme** in slideOptions
2. **OR inline styles** if absolutely necessary
3. **OR proper mounting and path reference**

## Actual Solution for HedgeDoc

### The Problem
The `<link>` tag appears as visible text because reveal.js/HedgeDoc doesn't process link tags in the markdown content area.

### Solution: Use Existing Style Block
The presentations already have a `<style>` block after the YAML frontmatter. We need to:
1. Remove any `<link>` tags from the markdown content
2. Import the CSS content from `/docs/presentation-styles.css` into the existing `<style>` block
3. Ensure proper fonts (Open Sans), font-size, and left-alignment

### Implementation Approach
Copy the CSS rules from `presentation-styles.css` into the existing `<style>` block:

```markdown
---
title: Entwurfsmuster-Workshop - Tag 1
slideOptions:
  theme: white
  transition: slide
---

<style>
/* Import Google Fonts */
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;600;700&family=Source+Code+Pro:wght@400;600&display=swap');

/* Global presentation styles */
.reveal .slides {
  font-family: 'Open Sans', sans-serif !important;
  font-size: 28px !important;
  text-align: left !important;
  color: #333333 !important;
}

/* Headers */
.reveal h1, .reveal h2, .reveal h3, .reveal h4 {
  font-family: 'Open Sans', sans-serif !important;
  color: #333333 !important;
  text-align: left !important;
}

/* Code blocks */
.reveal pre, .reveal code {
  font-family: 'Source Code Pro', monospace !important;
  font-size: 20px !important;
}

/* Lists */
.reveal ul, .reveal ol {
  text-align: left !important;
  display: block !important;
}

/* Prevent content overflow */
.reveal .slides section {
  width: 100% !important;
  height: 100% !important;
  overflow: auto !important;
}
</style>

# First slide content...
```

## Why This Approach?

1. **Self-contained:** CSS is embedded in the presentation file
2. **Reveal.js compatible:** Reveal.js properly processes inline style tags
3. **No external dependencies:** No file path or mounting issues
4. **Portable:** Presentation works anywhere HedgeDoc is deployed
5. **Version controlled:** CSS changes tracked with content

## Implementation Requirements

### Font Requirements
- **Text Font:** Open Sans (Google Fonts)
- **Code Font:** Source Code Pro (Google Fonts)
- **Font Size:** 28px for body text, 20px for code
- **Color:** #333333 for all text
- **Alignment:** Left-aligned (not centered)

### CSS Specificity
- Use `!important` to override reveal.js defaults
- Target `.reveal` classes for proper specificity
- Apply to `.slides` container for global effect

### Content Overflow Fix
- Set width/height to 100%
- Enable overflow: auto for long content
- Prevent text from being cut off

## Files to Update
All four presentation files need this fix:
- `presentation/hedgedoc/hedgedoc-day1.md`
- `presentation/hedgedoc/hedgedoc-day2.md`
- `presentation/hedgedoc/hedgedoc-day3.md`
- `presentation/hedgedoc/hedgedoc-day4.md`

## Implementation Steps
1. Remove the `<link rel="stylesheet">` line
2. Add the `<style>` block after YAML frontmatter
3. Include all CSS rules as shown above
4. Re-import presentations to HedgeDoc
5. Validate styling is applied correctly