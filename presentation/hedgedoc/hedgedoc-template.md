---
title: Design Patterns Workshop - Template
description: HedgeDoc Presentation Template for Design Patterns Workshop
tags: design-patterns, workshop, architecture, training
slideOptions:
  theme: white
  transition: slide
  backgroundTransition: fade
  center: false
  progress: true
  controls: true
  mouseWheel: false
  history: true
  keyboard: true
  overview: true
  touch: true
  fragments: true
  width: 1024
  height: 768
  margin: 0.1
  minScale: 0.5
  maxScale: 2.0
---

<div class="workshop-header">

# Design Patterns Workshop
## [Workshop Day/Topic]
### Design Patterns Workshop

</div>

---

# Template Usage Instructions

## This Template Provides:
- Pre-configured slide options for optimal HedgeDoc presentation
- Custom CSS styling with Open Sans and Source Code Pro fonts
- Left-aligned text with #333333 foreground color
- Fragment animation classes ready to use
- Two-column layout classes
- Speaker notes styling

## Key Features:
- **Typography**: Open Sans for text, Source Code Pro for code
- **Colors**: #333333 text color throughout
- **Layout**: Left-aligned content, responsive design
- **Animations**: Fragment reveals for interactive presentations

---

# Slide Structure Examples

## Basic Slide
```markdown
# Slide Title
## Optional Subtitle

Content goes here with left alignment
- Bullet points work well
- Use fragments for reveals <!-- .element: class="fragment" -->

**Speaker Notes:** Add speaker notes using this pattern.
```

## Two-Column Layout
```markdown
# Comparison Slide

<div class="two-column">
<div>

## Left Column
- Point 1
- Point 2
- Point 3

</div>
<div>

## Right Column
- Counter-point 1
- Counter-point 2
- Counter-point 3

</div>
</div>
```

---

# Fragment Animation Examples

## Basic Fragments
- This appears immediately
- This appears on next click <!-- .element: class="fragment" -->
- This appears on third click <!-- .element: class="fragment fade-up" -->
- This slides in from left <!-- .element: class="fragment fade-left" -->

## Advanced Fragments
<div class="fragment highlight-red">This text highlights in red</div>
<div class="fragment highlight-green">This text highlights in green</div>
<div class="fragment highlight-blue">This text highlights in blue</div>

---

# Code Example Structure

## Pattern Implementation

```java
public class SingletonExample {
    private static SingletonExample instance; // fragment
    
    private SingletonExample() { // fragment
        // Private constructor // fragment
    } // fragment
    
    public static SingletonExample getInstance() { // fragment
        if (instance == null) { // fragment
            instance = new SingletonExample(); // fragment
        } // fragment
        return instance; // fragment
    } // fragment
} // fragment
```

**Speaker Notes:** Walk through the implementation step by step using fragments.

---

# Special Content Boxes

## Pattern Definition
<div class="pattern-definition">

#### Singleton Pattern
**Intent**: Ensure a class has only one instance and provide global access to it.

**Problem**: You need exactly one instance of a class, and clients need global access to it.

**Solution**: Make the class responsible for keeping track of its sole instance.

</div>

## Highlight Boxes
<div class="highlight-box">
**Key Concept**: This is an important point that needs emphasis.
</div>

<div class="highlight-box accent">
**Critical Warning**: This is a critical warning or important note.
</div>

---

# Interactive Elements

<div class="interactive-question">

## Quick Question
#### Which pattern ensures only one instance?

<div class="interactive-options">
<div>A) Factory Pattern</div>
<div>B) Observer Pattern</div>
<div>C) Singleton Pattern</div>
<div>D) Strategy Pattern</div>
</div>

</div>

<div class="fragment highlight-green" style="text-align: center; margin-top: 2em;">
**Answer: C) Singleton Pattern**
</div>

---

# Progress Tracking

<div class="progress-indicator">
<div class="progress-step completed">✅ Introduction</div>
<div class="progress-step current">📍 Current Topic</div>
<div class="progress-step pending">⏳ Next Topic</div>
<div class="progress-step pending">⏳ Final Topic</div>
</div>

## Current Focus: [Topic Name]
- Learning objective 1
- Learning objective 2
- Hands-on exercise

---

# Speaker Notes Patterns

## Standard Speaker Notes
**Speaker Notes:** Use this pattern for standard speaking notes. Include timing, interaction cues, and key points to emphasize.

## Detailed Speaker Notes
<div class="speaker-notes">
This is an alternative speaker notes format that appears as a visible callout box. Use this for notes that participants should see, such as references, additional resources, or take-home messages.
</div>

---

# Template Customization

## Slide Options Configuration
```yaml
slideOptions:
  theme: white              # Choose: white, black, league, beige, sky, night
  transition: slide         # Choose: none, fade, slide, convex, concave, zoom
  center: false            # Keep false for left-aligned content
  fragments: true          # Enable fragment animations
  width: 1024             # Presentation width
  height: 768             # Presentation height
```

## Custom CSS Classes Available
- `.two-column` - Two-column grid layout
- `.pattern-definition` - Special boxes for pattern definitions
- `.highlight-box` - Emphasis boxes (default, accent, success, warning)
- `.interactive-question` - Interactive question containers
- `.speaker-notes` - Visible speaker notes
- `.progress-indicator` - Progress tracking display
- `.workshop-header` - Special header styling

---

# Template Ready for Use

## To Use This Template:
1. Copy this file as your starting point
2. Replace placeholder content with your actual content
3. Update the title and description in the front matter
4. Add your slides following the provided patterns
5. Test fragment animations and responsive layout

## Remember:
- Keep content left-aligned
- Use fragments for progressive disclosure
- Include speaker notes for presentation flow
- Test on different screen sizes
- Validate HedgeDoc compatibility

**Speaker Notes:** This template is ready for immediate use. Customize the front matter metadata and replace placeholder content with your workshop materials.