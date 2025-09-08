# HedgeDoc Design Patterns Workshop Presentations

This directory contains HedgeDoc-compatible presentation files converted from the original RevealJS presentations. All presentations have been optimized for HedgeDoc's reveal.js integration with custom styling and interactive features.

## Files Overview

### Core Presentations
- **`hedgedoc-day1.md`** - Day 1: Design Patterns Fundamentals (Singleton & Factory Method)
- **`hedgedoc-day2.md`** - Day 2: Structural Patterns (Adapter, Decorator, Facade, Composite, Proxy)
- **`hedgedoc-day3.md`** - Day 3: Behavioral Patterns (Observer, Strategy, Command, Template Method, Iterator, Chain of Responsibility)
- **`hedgedoc-day4.md`** - Day 4: Advanced Patterns (MVC/MVP/MVVM, Microservices, Performance, Testing)

### Supporting Files
- **`hedgedoc-template.md`** - Reusable template for creating new HedgeDoc presentations
- **`presentation-styles.css`** - Custom CSS with Open Sans fonts and neutral styling
- **`README.md`** - This documentation file

## Key Features

### ✅ Styling Requirements Met
- **Open Sans font** for all text content
- **Source Code Pro font** for all code blocks
- **Left-aligned text** throughout presentations (#333333 foreground color)
- **Neutral colors** (#333333 text color)
- **Responsive design** optimized for different screen sizes

### ✅ Interactive Elements
- **Fragment animations** for progressive disclosure using `<!-- .element: class="fragment" -->`
- **Two-column layouts** for comparison slides
- **Interactive questions** and discussion prompts
- **Progress indicators** showing workshop progression
- **Pattern definition boxes** for key concepts
- **Code highlighting** with step-by-step reveals

### ✅ HedgeDoc Integration
- **Presentation metadata** properly configured in YAML frontmatter
- **reveal.js slideOptions** optimized for HedgeDoc
- **Custom CSS linking** for consistent styling
- **Speaker notes** formatted for HedgeDoc presentation mode

## Usage Instructions

### 1. Import to HedgeDoc
1. Copy the content of any `.md` file
2. Create a new document in HedgeDoc
3. Paste the content
4. Ensure the CSS file is accessible (upload to web server or use CDN)
5. Switch to presentation mode

### 2. CSS File Setup
The `presentation-styles.css` file needs to be accessible via HTTP/HTTPS for the presentations to load correctly:

**Option 1: Host on Web Server**
```html
<link rel="stylesheet" type="text/css" href="https://your-domain.com/presentation-styles.css">
```

**Option 2: Local Development**
```html
<link rel="stylesheet" type="text/css" href="./presentation-styles.css">
```

**Option 3: Inline Styles**
Copy the CSS content directly into a `<style>` tag in each presentation file.

### 3. Presentation Navigation
- **Arrow keys**: Navigate between slides
- **Space/Enter**: Advance through fragments
- **ESC**: Overview mode
- **F**: Fullscreen mode
- **S**: Speaker notes (if supported)

## Content Structure

### Day 1: Fundamentals
- Design pattern introduction and theory
- Singleton pattern with thread-safe implementations
- Factory Method pattern with enterprise examples
- Hands-on exercises with payment systems

### Day 2: Structural Patterns
- Adapter pattern for legacy system integration
- Decorator pattern for cross-cutting concerns
- Facade pattern for complex subsystem management
- Composite pattern for hierarchical structures
- Proxy pattern for security and caching

### Day 3: Behavioral Patterns
- Observer pattern for event-driven architectures
- Strategy pattern for algorithm flexibility
- Command pattern with undo/redo functionality
- Template Method for workflow structures
- Iterator and Chain of Responsibility patterns
- Modern JavaScript implementations

### Day 4: Advanced Topics
- Architectural patterns (MVC, MVP, MVVM)
- Microservice patterns (Service Discovery, Circuit Breaker)
- Performance patterns (Caching, Object Pooling)
- Testing patterns (Mocks, Stubs, Test Doubles)
- Pattern combinations and anti-patterns

## Technical Specifications

### Slide Configuration
```yaml
slideOptions:
  theme: white
  transition: slide
  backgroundTransition: fade
  center: false              # Left-aligned content
  progress: true
  controls: true
  keyboard: true
  fragments: true            # Enable fragment animations
  width: 1024
  height: 768
  margin: 0.1
```

### Custom CSS Classes
- `.two-column` - Two-column grid layout
- `.pattern-definition` - Special boxes for pattern definitions
- `.highlight-box` - Emphasis boxes (default, accent, success, warning)
- `.interactive-question` - Interactive question containers
- `.code-example` - Code example containers
- `.progress-indicator` - Progress tracking displays
- `.workshop-header` - Special header styling for title slides

### Fragment Animation Examples
```markdown
- This appears immediately
- This appears on click <!-- .element: class="fragment" -->
- This fades up <!-- .element: class="fragment fade-up" -->
- This highlights in red <!-- .element: class="fragment highlight-red" -->
```

## Customization

### Modifying Colors
Edit the CSS variables in `presentation-styles.css`:
```css
:root {
    --primary-font: 'Open Sans', sans-serif;
    --code-font: 'Source Code Pro', monospace;
    --text-color: #333333;
    --accent-color: #333333; /* Text Color */
    --secondary-color: #333333; /* Text Color */
}
```

### Adding New Slides
Use the `hedgedoc-template.md` as a starting point for new presentations. It includes:
- Pre-configured metadata
- Standard slide layouts
- Fragment animation examples
- Custom CSS class demonstrations

## Troubleshooting

### Common Issues
1. **CSS not loading**: Verify the CSS file path in the `<link>` tag
2. **Fonts not displaying**: Ensure Google Fonts are accessible
3. **Fragments not working**: Check `fragments: true` in slideOptions
4. **Layout issues**: Verify CSS classes are correctly applied

### Browser Compatibility
- **Chrome/Edge**: Full support
- **Firefox**: Full support
- **Safari**: Full support
- **Mobile browsers**: Responsive design optimized

## Best Practices

1. **Test presentations** in HedgeDoc before presenting
2. **Use fragments sparingly** - too many can slow pacing
3. **Keep code examples focused** - break large examples into multiple slides
4. **Include speaker notes** for complex topics
5. **Test on different screen sizes** for responsive behavior

## Support and Maintenance

For questions or issues with the HedgeDoc presentations:
- Review the HedgeDoc presentation documentation
- Check reveal.js documentation for advanced features
- Test in a local HedgeDoc instance before production use
- Keep backup copies of presentation files

---

**Generated with Claude Code for Design Patterns Workshop**
**Last Updated: January 2025**