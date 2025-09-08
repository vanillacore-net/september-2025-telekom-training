# Telekom Design Patterns Training - Marp Presentation

This is the Marp version of the Telekom Design Patterns Workshop presentation. It provides **clean separation between content (markdown) and design (CSS theme)**, making it much easier to maintain and edit than the original RevealJS HTML version.

## 🚀 Quick Start

### Prerequisites
- Node.js and npm installed
- Marp CLI installed globally: `npm install -g @marp-team/marp-cli`

### Build Commands

```bash
# Build HTML presentation
npm run build:html

# Build PDF presentation  
npm run build:pdf

# Build both HTML and PDF
npm run build

# Watch mode - auto-rebuild on changes
npm run watch

# Serve presentation with live reload
npm run serve
```

## 📁 Directory Structure

```
marp-presentation/
├── slides.md                    # Main content file (EDIT THIS!)
├── themes/
│   └── telekom-theme.css        # Custom Marp theme
├── assets/
│   └── img/                     # Logo images
├── dist/                        # Generated presentations
│   ├── index.html              # HTML version
│   └── presentation.pdf        # PDF version
├── docs/                        # Documentation
└── package.json                # Build scripts
```

## ✏️ Editing Content

**The key advantage**: All content is in `slides.md` - just edit this markdown file!

### Slide Types and Classes

1. **Title Slide**
```markdown
<!-- _class: title-slide -->
![Logo](./assets/img/VanillaCore_Vertical.png)
# Title Here
## Subtitle Here
```

2. **Section Slide**
```markdown
<!-- _class: section-slide -->
![Logo](./assets/img/VanillaCore_Vertical.png)
# Section Title
```

3. **Single Column Content**
```markdown
<!-- _class: content-slide single-column -->
![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Slide Title
Content goes here...
- Bullet points
- More content
```

4. **Two Column Content**
```markdown
<!-- _class: content-slide two-columns -->
![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Slide Title

<div class="columns-container">
<div class="column">
Left column content
</div>
<div class="column">
Right column content
</div>
</div>
```

5. **Half Picture Layout**
```markdown
<!-- _class: content-slide half-picture -->
![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

<div class="text-content">
## Title
Text content here
</div>

<div class="image-content">
![Image](./assets/img/image.png)
</div>
```

6. **Code Slide - Embedded**
```markdown
<!-- _class: content-slide code-embedded -->
![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Code Title

<div class="code-embedded">
<div class="text-section">
Explanation text here
</div>
<div class="code-section">

\`\`\`javascript
// Code goes here
\`\`\`

</div>
</div>
```

7. **Code Slide - Standalone**
```markdown
<!-- _class: content-slide code-standalone -->
![corner logo class:corner-logo](./assets/img/VanillaCore_Square.png)

## Code Title

\`\`\`java
// Full-width code block
\`\`\`
```

8. **Full Picture Slide**
```markdown
<!-- _class: full-picture -->

<div class="placeholder-image warehouse"></div>

<div class="text-overlay">
## Title
Description text
</div>
```

### Speaker Notes

Add speaker notes using HTML comments:

```markdown
<!--
Your speaker notes go here.
They will be included in the presentation for reference.
-->
```

## 🎨 Design System

The theme exactly matches the RevealJS design:

- **Typography**: Open Sans font family
- **Colors**: 
  - Primary text: #333333 (dark gray)
  - Secondary text: #666666 (medium gray)
  - Background: White
- **Logo Placement**: 
  - Center logos for title/section slides
  - Corner logos (top-right) for content slides
- **Responsive**: Scales properly for different screen sizes

## 🔧 Customization

### Modifying the Theme

Edit `themes/telekom-theme.css` to customize:
- Colors and typography
- Layout spacing
- Logo positioning
- Animation effects

### Adding New Slide Types

1. Add CSS classes to the theme file
2. Use `<!-- _class: your-class -->` in markdown
3. Apply custom styling as needed

### Changing Logos

Replace images in `assets/img/`:
- `VanillaCore_Vertical.png` - Title/section slides
- `VanillaCore_Square.png` - Corner logo for content slides

## 📋 Content Maintenance Workflow

1. **Edit Content**: Modify `slides.md` with your changes
2. **Preview**: Run `npm run serve` to see changes live
3. **Build**: Run `npm run build` to generate final files
4. **Deploy**: Use the files in `dist/` folder

## 🆚 Advantages over RevealJS Version

✅ **Separated Concerns**: Content in markdown, design in CSS
✅ **Easy Editing**: Just edit markdown - no HTML knowledge needed
✅ **Version Control Friendly**: Clean markdown diffs
✅ **Multiple Outputs**: HTML and PDF from same source
✅ **Faster Development**: Live reload and watch mode
✅ **Maintainable**: Clear structure and documentation

## 🛠️ Troubleshooting

### Build Issues
- Ensure Marp CLI is installed globally
- Check that all image paths exist
- Validate markdown syntax

### Layout Issues
- Verify CSS class names match theme
- Check HTML structure in embedded layouts
- Ensure proper closing of div tags

### Logo Not Showing
- Check image file paths
- Ensure images exist in `assets/img/`
- Verify CSS background-image URLs

## 📚 Further Reading

- [Marp Documentation](https://marp.app/)
- [Marp CLI Usage](https://github.com/marp-team/marp-cli)
- [CSS Theming Guide](https://marpit.marp.app/theme-css)