# Presentation Visual Specifications

## Reference Screenshots Analysis

### 1. Title Slide (title.png)
**Logo:**
- Position: Centered horizontally, upper third of slide
- Size: Approximately 120px wide (square aspect ratio)
- CORE logo with orange background and white text

**Title Text:**
- Font: Open Sans Regular (weight 400) - NOT BOLD
- Size: Approximately 60-70px
- Color: Dark gray (#333 or similar)
- Position: Centered, below logo with significant spacing

**Subtitle (if present):**
- Font: Open Sans Light (weight 300)
- Size: Approximately 30-40px
- Color: Medium gray (#666)
- Position: Below title

### 2. Section Slide (section.png)
**Logo:**
- Position: Centered horizontally, upper third
- Size: Same as title slide (~120px)

**Section Title:**
- Font: Open Sans Regular (weight 400)
- Size: Large, similar to title slide
- Color: Dark gray
- Position: Centered below logo

### 3. Single Column Slide (single-column.png)
**Logo:**
- Position: TOP RIGHT CORNER (not center!)
- Size: Smaller, approximately 80px wide
- CORE square logo variant

**Heading:**
- Font: Open Sans Regular (weight 400)
- Size: Approximately 50px
- Color: Dark gray/black
- Position: Left-aligned, top of content area

**Bullet Points:**
- Font: Open Sans Light (weight 300)
- Size: Approximately 24-28px
- Color: Dark gray
- Bullet style: Simple dots
- Line spacing: Generous (1.5-2x)
- First bullet: Always visible
- Remaining bullets: Fragment animation

### 4. Two Columns Slide (two-columns.png)
**Logo:**
- Position: TOP RIGHT CORNER
- Size: ~80px square variant

**Main Heading:**
- Font: Open Sans Regular (weight 400)
- Size: ~50px
- Position: Top left

**Column Headers:**
- Font: Open Sans Regular (weight 400)
- Size: ~36px
- Color: Dark gray

**Column Content:**
- Font: Open Sans Light (weight 300)
- Size: ~24px
- Equal column widths
- Good spacing between columns

### 5. Half-Picture Slide (single-column-short-text-image.png)
**Layout:**
- Left side: Text content (50%)
- Right side: Image/graphic (50%)

**Logo:**
- May be integrated into image side or omitted

**Text Formatting:**
- Same as single column for text side

### 6. Full-Picture Slide (image-only.png)
**Layout:**
- Full background image
- Text overlay with semi-transparent background

**Text Overlay:**
- Position: Bottom right or strategically placed
- Background: White with opacity
- Text: High contrast for readability

## Critical Requirements

### Fonts
- **Headlines**: Open Sans Regular (400) - NEVER BOLD
- **Body Text**: Open Sans Light (300)
- **Code**: Source Code Pro (if needed)

### Colors
- **Primary**: Telekom Orange #D9931C
- **Text**: Dark gray #333
- **Subtitle**: Medium gray #666
- **Background**: White #FFFFFF

### Fragment Behavior
- First line/bullet: Always visible (no fragment class)
- Subsequent items: class="fragment" for reveal animation
- Total fragments per slide varies (typically 4-7)

### Logo Variations
- Vertical logo: For title/section slides (centered)
- Square logo: For content slides (top-right corner)
- Size varies by context (120px center, 80px corner)

### Spacing
- Generous whitespace throughout
- Clear hierarchy through spacing
- Consistent margins and padding

## Implementation Notes

1. These specifications MUST be embedded in all PRBs for presentation frameworks
2. Validation MUST check against these specifications
3. Screenshots MUST be taken for comparison
4. Font weights are CRITICAL - no bold headlines
5. Logo positioning varies by slide type
6. Fragment behavior is mandatory for proper reveal

## Files Required
- VanillaCore_Vertical.png (for title/section slides)
- VanillaCore_Square.png (for corner placement)
- Both logos should be provided in img/ directory