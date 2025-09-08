# Migration from RevealJS to Marp - Technical Notes

This document explains the technical decisions made during the migration from RevealJS to Marp.

## Migration Approach

### 1. Content Extraction
- Extracted all German content from `index.html`
- Preserved exact text, structure, and speaker notes
- Maintained slide order and hierarchy

### 2. Layout Matching
- Analyzed RevealJS CSS (`custom.css`) for exact measurements
- Replicated responsive design patterns
- Maintained logo positioning and typography

### 3. Theme Development
- Created `telekom-theme.css` with exact visual matching
- Used same viewport-based sizing (vw, vh units)
- Preserved color scheme and spacing

## Key Technical Differences

### RevealJS vs Marp Structure

**RevealJS Approach:**
```html
<section class="single-column content-slide">
  <img src="img/logo.png" class="logo-corner">
  <h2>Title</h2>
  <p>Content</p>
  <aside class="notes">Speaker notes</aside>
</section>
```

**Marp Approach:**
```markdown
<!-- _class: content-slide single-column -->
![corner logo class:corner-logo](./assets/img/logo.png)

## Title
Content

<!--
Speaker notes
-->
```

### Logo Handling
- **RevealJS**: CSS positioning of IMG elements
- **Marp**: CSS classes applied to markdown images
- **Solution**: Used Marp's `class:` syntax for logo positioning

### Fragment Animation
- **RevealJS**: Complex fragment system with data attributes
- **Marp**: Simplified approach, removed complex fragments
- **Trade-off**: Some progressive reveal lost for maintainability

### Two-Column Layout
- **RevealJS**: CSS Grid with complex responsive rules
- **Marp**: HTML divs with CSS Flexbox (maintained in markdown)
- **Result**: Same visual layout, cleaner markup

## Layout Preservation Details

### Typography Hierarchy
```css
/* Exact matches from RevealJS */
h1: 4.5vw (title slides)
h2: 2.8vw (content headers)  
h3: 2.3vw (subheadings)
p, li: 1.9vw (body text)
code: 1.6vw (code blocks)
```

### Logo Positioning
```css
/* Corner logo - matches RevealJS exactly */
position: fixed;
top: 0;
right: 3vw;
width: 8vh;
height: 8vh;
```

### Content Areas
```css
/* Header area - matches RevealJS */
height: 8vh (logo height)
margin-top: 9vh (content starts below)
padding: 0 4vw 4vh 4vw (side margins)
```

## Responsive Design Maintenance

All responsive breakpoints and scaling preserved:

```css
/* 4K screens */
@media (min-width: 2560px) { /* clamp values */ }

/* Mobile screens */  
@media (max-width: 768px) { /* mobile adaptations */ }
```

## Code Block Handling

### RevealJS Complex Fragment System
- Multiple `<pre>` blocks with fragments
- Complex overlay positioning
- JavaScript-driven transitions

### Marp Simplified Approach
- Single code block per slide
- Maintained syntax highlighting
- Cleaner, more maintainable structure

## Build Process

### RevealJS Build
- Manual HTML editing
- Complex fragment coordination
- Requires presentation framework knowledge

### Marp Build
```bash
# Simple commands
marp slides.md --html --theme themes/telekom-theme.css
marp slides.md --pdf --theme themes/telekom-theme.css
```

## Advantages Achieved

1. **Content/Design Separation**
   - Content: `slides.md` (non-technical editing)
   - Design: `telekom-theme.css` (technical styling)

2. **Maintainability**
   - Version control friendly markdown
   - Clear documentation structure
   - Simplified build process

3. **Multiple Output Formats**
   - HTML presentation
   - PDF export
   - Consistent styling across formats

4. **Developer Experience**
   - Live reload during development
   - Watch mode for automatic rebuilds
   - Clean error messages

## Visual Validation

All slide types tested and validated:
- ✅ Title slide (centered logo, proper typography)
- ✅ Section slide (centered layout)
- ✅ Single column (corner logo, left-aligned content)
- ✅ Two column (responsive grid, proper spacing)
- ✅ Half picture (50/50 split, proper header positioning)
- ✅ Code embedded (text + code layout)
- ✅ Code standalone (full-width code blocks)
- ✅ Full picture (background with overlay text)

## Future Maintenance

### Content Updates
1. Edit `slides.md` only
2. Run `npm run build`
3. Deploy `dist/` folder

### Design Changes
1. Modify `themes/telekom-theme.css`
2. Test with `npm run serve`
3. Build when satisfied

### Adding New Slide Types
1. Add CSS classes to theme
2. Document in README.md
3. Test with sample content

This migration successfully maintains all visual aspects while dramatically improving maintainability and editing experience.