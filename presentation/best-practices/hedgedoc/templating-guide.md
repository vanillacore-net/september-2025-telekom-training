# HedgeDoc Templating System - Comprehensive Guide

## Overview

HedgeDoc provides powerful templating capabilities for creating consistent, professional presentations and documents. This guide covers the complete templating system, from basic templates to advanced customization techniques for presentations, training materials, and corporate documentation.

## Understanding HedgeDoc Templating

### Template Types

HedgeDoc supports several types of templates:

1. **Presentation Templates**: Reveal.js-based slide templates
2. **Document Templates**: Structured document layouts
3. **Note Templates**: Quick-start templates for common note types
4. **Theme Templates**: Visual styling and branding templates

### Template Storage and Organization

```
project/
├── templates/
│   ├── presentations/
│   │   ├── training-workshop.md
│   │   ├── architecture-review.md
│   │   └── status-update.md
│   ├── documents/
│   │   ├── technical-spec.md
│   │   ├── meeting-notes.md
│   │   └── project-proposal.md
│   ├── themes/
│   │   ├── corporate-theme.css
│   │   ├── technical-theme.css
│   │   └── minimal-theme.css
│   └── components/
│       ├── slide-layouts.md
│       ├── code-blocks.md
│       └── diagrams.md
```

## Presentation Template System

### Basic Presentation Template Structure

#### Master Template: `training-workshop.md`

```markdown
---
title: ${WORKSHOP_TITLE}
description: ${WORKSHOP_DESCRIPTION}
author: ${PRESENTER_NAME}
date: ${PRESENTATION_DATE}
tags: training, workshop, ${TOPIC_TAG}
slideOptions:
  theme: corporate
  transition: slide
  backgroundTransition: fade
  center: true
  progress: true
  controls: true
  slideNumber: true
  history: true
---

<style>
/* Corporate Branding */
:root {
  --primary-color: #007acc;
  --secondary-color: #6c757d;
  --accent-color: #28a745;
  --background-color: #ffffff;
  --text-color: #333333;
}

.reveal .slides section .corporate-header {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  color: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.reveal .slides section .highlight-box {
  background-color: rgba(0, 122, 204, 0.1);
  border-left: 5px solid var(--primary-color);
  padding: 20px;
  margin: 20px 0;
  border-radius: 5px;
}

.reveal .slides section .code-demo {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
}

.reveal .slides section .two-column {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
}

@media (max-width: 768px) {
  .reveal .slides section .two-column {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}
</style>

<!-- TITLE SLIDE TEMPLATE -->
# ${WORKSHOP_TITLE}
## ${WORKSHOP_SUBTITLE}

<div class="corporate-header">
<h3>Workshop Overview</h3>
<ul>
<li><strong>Presenter:</strong> ${PRESENTER_NAME}</li>
<li><strong>Duration:</strong> ${WORKSHOP_DURATION}</li>
<li><strong>Date:</strong> ${PRESENTATION_DATE}</li>
<li><strong>Level:</strong> ${SKILL_LEVEL}</li>
</ul>
</div>

**Speaker Notes:** Welcome participants, introduce yourself, and set expectations for the workshop. Mention any prerequisites and available resources.

---

<!-- AGENDA TEMPLATE -->
# Workshop Agenda

<div class="highlight-box">
<h3>Learning Objectives</h3>
<ul>
<li>${OBJECTIVE_1}</li>
<li>${OBJECTIVE_2}</li>
<li>${OBJECTIVE_3}</li>
</ul>
</div>

## Schedule

<div class="two-column">
<div>

### Morning Session
- **9:00-9:15** Welcome & Introductions
- **9:15-10:30** ${TOPIC_1}
- **10:30-10:45** Break
- **10:45-12:00** ${TOPIC_2}

</div>
<div>

### Afternoon Session
- **13:00-14:30** ${TOPIC_3}
- **14:30-14:45** Break
- **14:45-16:00** ${TOPIC_4}
- **16:00-16:30** Q&A & Wrap-up

</div>
</div>

**Speaker Notes:** Review agenda and timing. Ask about participant experience levels and adjust accordingly.

---

<!-- SECTION SEPARATOR TEMPLATE -->
# ${SECTION_TITLE}
## ${SECTION_SUBTITLE}

<div class="corporate-header">
<h3>In This Section</h3>
<ul>
<li>${SECTION_POINT_1}</li>
<li>${SECTION_POINT_2}</li>
<li>${SECTION_POINT_3}</li>
</ul>
</div>

---

<!-- CONCEPT INTRODUCTION TEMPLATE -->
# ${CONCEPT_NAME}

<div class="highlight-box">
<strong>Definition:</strong> ${CONCEPT_DEFINITION}
</div>

<div class="two-column">
<div>

## Key Benefits
- ${BENEFIT_1}
- ${BENEFIT_2}
- ${BENEFIT_3}

## Use Cases
- ${USE_CASE_1}
- ${USE_CASE_2}
- ${USE_CASE_3}

</div>
<div>

## Common Challenges
- ${CHALLENGE_1}
- ${CHALLENGE_2}
- ${CHALLENGE_3}

## Best Practices
- ${PRACTICE_1}
- ${PRACTICE_2}
- ${PRACTICE_3}

</div>
</div>

**Speaker Notes:** ${SPEAKER_NOTES_CONCEPT}

---

<!-- CODE EXAMPLE TEMPLATE -->
# Implementation Example

<div class="code-demo">
<h3>${EXAMPLE_TITLE}</h3>

\`\`\`${LANGUAGE}
${CODE_EXAMPLE}
\`\`\`

<div class="highlight-box">
<strong>Key Points:</strong>
<ul>
<li>${CODE_POINT_1}</li>
<li>${CODE_POINT_2}</li>
<li>${CODE_POINT_3}</li>
</ul>
</div>
</div>

**Speaker Notes:** Walk through the code example step by step. Highlight the key concepts and explain any complex parts. Encourage questions.

---

<!-- HANDS-ON EXERCISE TEMPLATE -->
# Hands-On Exercise

<div class="corporate-header">
<h3>Exercise: ${EXERCISE_TITLE}</h3>
<p><strong>Time:</strong> ${EXERCISE_TIME} minutes</p>
</div>

## Objective
${EXERCISE_OBJECTIVE}

## Instructions
1. ${INSTRUCTION_1}
2. ${INSTRUCTION_2}
3. ${INSTRUCTION_3}
4. ${INSTRUCTION_4}

<div class="highlight-box">
<strong>Success Criteria:</strong>
<ul>
<li>${SUCCESS_CRITERION_1}</li>
<li>${SUCCESS_CRITERION_2}</li>
<li>${SUCCESS_CRITERION_3}</li>
</ul>
</div>

**Speaker Notes:** Circulate and help participants. Be ready to provide hints or additional examples for common issues.

---

<!-- BREAK SLIDE TEMPLATE -->
# Break Time ☕

<div class="corporate-header" style="text-align: center;">
<h2>15 Minute Break</h2>
<p>We'll resume at ${BREAK_RESUME_TIME}</p>
</div>

**Networking Opportunity**
- Connect with other participants
- Ask questions about previous topics
- Stretch and refresh

**Speaker Notes:** Use break time to gauge participant engagement and adjust pace if needed.

---

<!-- Q&A TEMPLATE -->
# Questions & Discussion

<div class="corporate-header">
<h3>Open Discussion</h3>
<p>Share your thoughts, challenges, and questions</p>
</div>

## Discussion Topics
- ${DISCUSSION_TOPIC_1}
- ${DISCUSSION_TOPIC_2}
- ${DISCUSSION_TOPIC_3}

<div class="highlight-box">
<strong>Remember:</strong> No question is too basic. Everyone benefits from shared learning.
</div>

**Speaker Notes:** Encourage participation. Have backup questions ready if the audience is quiet. Connect questions back to real-world scenarios.

---

<!-- WRAP-UP TEMPLATE -->
# Key Takeaways

<div class="two-column">
<div>

## What We Covered
- ${TAKEAWAY_1}
- ${TAKEAWAY_2}
- ${TAKEAWAY_3}
- ${TAKEAWAY_4}

## Skills Developed
- ${SKILL_1}
- ${SKILL_2}
- ${SKILL_3}

</div>
<div>

## Next Steps
- ${NEXT_STEP_1}
- ${NEXT_STEP_2}
- ${NEXT_STEP_3}

## Resources
- ${RESOURCE_1}
- ${RESOURCE_2}
- ${RESOURCE_3}

</div>
</div>

**Speaker Notes:** Summarize the main points and encourage continued learning. Provide contact information for follow-up questions.

---

<!-- CONTACT SLIDE TEMPLATE -->
# Thank You!

<div class="corporate-header" style="text-align: center;">
<h2>Stay Connected</h2>
</div>

## Contact Information
- **Email:** ${PRESENTER_EMAIL}
- **LinkedIn:** ${PRESENTER_LINKEDIN}
- **GitHub:** ${PRESENTER_GITHUB}
- **Company:** ${PRESENTER_COMPANY}

## Resources and Links
- **Presentation:** ${PRESENTATION_LINK}
- **Code Examples:** ${CODE_REPOSITORY}
- **Documentation:** ${DOCUMENTATION_LINK}

<div class="highlight-box">
<strong>Feedback:</strong> Your feedback helps improve future sessions. Please share your thoughts!
</div>

**Speaker Notes:** Thank participants for their time and engagement. Encourage them to reach out with questions or for continued discussion.
```

### Template Variable System

#### Variable Categories

1. **Presenter Variables**
   - `${PRESENTER_NAME}` - Speaker's full name
   - `${PRESENTER_EMAIL}` - Contact email
   - `${PRESENTER_LINKEDIN}` - LinkedIn profile
   - `${PRESENTER_GITHUB}` - GitHub profile
   - `${PRESENTER_COMPANY}` - Company or organization

2. **Workshop Variables**
   - `${WORKSHOP_TITLE}` - Main workshop title
   - `${WORKSHOP_SUBTITLE}` - Secondary title
   - `${WORKSHOP_DESCRIPTION}` - Brief description
   - `${WORKSHOP_DURATION}` - Total duration
   - `${SKILL_LEVEL}` - Target skill level

3. **Content Variables**
   - `${SECTION_TITLE}` - Section heading
   - `${CONCEPT_NAME}` - Key concept being taught
   - `${CODE_EXAMPLE}` - Code snippet
   - `${LANGUAGE}` - Programming language

4. **Dynamic Variables**
   - `${PRESENTATION_DATE}` - Current date or scheduled date
   - `${TOPIC_TAG}` - Generated from content
   - `${BREAK_RESUME_TIME}` - Calculated break end time

#### Variable Substitution Methods

##### Method 1: Manual Replacement

```bash
#!/bin/bash
# create-presentation.sh

TEMPLATE_FILE="templates/presentations/training-workshop.md"
OUTPUT_FILE="presentations/design-patterns-workshop.md"

# Define variables
WORKSHOP_TITLE="Design Patterns Mastery"
WORKSHOP_SUBTITLE="Building Robust Software Architecture"
PRESENTER_NAME="John Doe"
PRESENTER_EMAIL="john.doe@example.com"
PRESENTATION_DATE=$(date +"%B %d, %Y")

# Create presentation from template
cp "$TEMPLATE_FILE" "$OUTPUT_FILE"

# Replace variables
sed -i "s/\${WORKSHOP_TITLE}/$WORKSHOP_TITLE/g" "$OUTPUT_FILE"
sed -i "s/\${WORKSHOP_SUBTITLE}/$WORKSHOP_SUBTITLE/g" "$OUTPUT_FILE"
sed -i "s/\${PRESENTER_NAME}/$PRESENTER_NAME/g" "$OUTPUT_FILE"
sed -i "s/\${PRESENTER_EMAIL}/$PRESENTER_EMAIL/g" "$OUTPUT_FILE"
sed -i "s/\${PRESENTATION_DATE}/$PRESENTATION_DATE/g" "$OUTPUT_FILE"

echo "Presentation created: $OUTPUT_FILE"
```

##### Method 2: Configuration File

Create `presentation-config.yaml`:

```yaml
presentation:
  title: "Design Patterns Mastery"
  subtitle: "Building Robust Software Architecture"
  description: "Comprehensive workshop on software design patterns"
  duration: "8 hours"
  skill_level: "Intermediate to Advanced"
  date: "2025-09-15"
  
presenter:
  name: "John Doe"
  email: "john.doe@example.com"
  linkedin: "linkedin.com/in/johndoe"
  github: "github.com/johndoe"
  company: "Tech Solutions Inc."

content:
  topics:
    - "Creational Patterns"
    - "Structural Patterns"
    - "Behavioral Patterns"
    - "Real-world Applications"
  
  objectives:
    - "Master fundamental design patterns"
    - "Implement patterns in real projects"
    - "Recognize when to apply patterns"

resources:
  presentation_link: "https://hedgedoc.example.com/design-patterns"
  code_repository: "https://github.com/johndoe/design-patterns-workshop"
  documentation_link: "https://design-patterns.dev"
```

Python script for template processing:

```python
#!/usr/bin/env python3
# process-template.py

import yaml
import re
from pathlib import Path
from datetime import datetime

class TemplateProcessor:
    def __init__(self, config_file):
        with open(config_file, 'r') as f:
            self.config = yaml.safe_load(f)
    
    def process_template(self, template_file, output_file):
        template_content = Path(template_file).read_text()
        
        # Process variables
        processed_content = self.replace_variables(template_content)
        
        # Write output
        Path(output_file).write_text(processed_content)
        print(f"Generated: {output_file}")
    
    def replace_variables(self, content):
        # Build variable dictionary
        variables = {}
        
        # Flatten nested config
        variables.update(self.flatten_dict(self.config, ''))
        
        # Add dynamic variables
        variables['PRESENTATION_DATE'] = datetime.now().strftime("%B %d, %Y")
        variables['CURRENT_YEAR'] = datetime.now().year
        
        # Replace variables in content
        for key, value in variables.items():
            pattern = f"${{{key.upper()}}}"
            content = content.replace(pattern, str(value))
        
        return content
    
    def flatten_dict(self, d, parent_key=''):
        items = []
        for k, v in d.items():
            new_key = f"{parent_key}_{k}".upper() if parent_key else k.upper()
            if isinstance(v, dict):
                items.extend(self.flatten_dict(v, new_key).items())
            elif isinstance(v, list):
                for i, item in enumerate(v):
                    items.append((f"{new_key}_{i+1}", item))
            else:
                items.append((new_key, v))
        return dict(items)

# Usage
if __name__ == "__main__":
    processor = TemplateProcessor("presentation-config.yaml")
    processor.process_template(
        "templates/presentations/training-workshop.md",
        "presentations/design-patterns-workshop.md"
    )
```

## Custom Theme Templates

### Corporate Theme Template

Create `themes/corporate-theme.css`:

```css
/* Corporate Theme for HedgeDoc Presentations */

/* Color Palette */
:root {
  --primary-brand: #003366;      /* Deep corporate blue */
  --secondary-brand: #0066cc;    /* Bright blue */
  --accent-brand: #ff6600;       /* Orange accent */
  --success-color: #28a745;      /* Green for positive */
  --warning-color: #ffc107;      /* Yellow for attention */
  --danger-color: #dc3545;       /* Red for critical */
  --text-dark: #212529;          /* Dark text */
  --text-light: #6c757d;         /* Light text */
  --background-light: #f8f9fa;   /* Light background */
  --background-white: #ffffff;   /* Pure white */
  --border-color: #dee2e6;       /* Border color */
}

/* Base Styles */
.reveal {
  font-family: 'Source Sans Pro', 'Helvetica Neue', Arial, sans-serif;
  font-size: 36px;
  font-weight: normal;
  color: var(--text-dark);
  background-color: var(--background-white);
}

/* Headings */
.reveal h1, .reveal h2, .reveal h3, .reveal h4, .reveal h5, .reveal h6 {
  margin: 0 0 20px 0;
  color: var(--primary-brand);
  font-family: 'Source Sans Pro', 'Helvetica Neue', Arial, sans-serif;
  font-weight: 600;
  line-height: 1.2;
  letter-spacing: normal;
  text-transform: none;
  text-shadow: none;
  word-wrap: break-word;
}

.reveal h1 {
  font-size: 2.5em;
  border-bottom: 3px solid var(--secondary-brand);
  padding-bottom: 20px;
}

.reveal h2 {
  font-size: 1.8em;
  color: var(--secondary-brand);
}

.reveal h3 {
  font-size: 1.4em;
  color: var(--text-dark);
}

/* Links */
.reveal a {
  color: var(--secondary-brand);
  text-decoration: none;
  transition: color 0.15s ease;
}

.reveal a:hover {
  color: var(--accent-brand);
  text-decoration: underline;
}

/* Text Elements */
.reveal p {
  margin: 20px 0;
  line-height: 1.6;
}

.reveal ul, .reveal ol {
  margin: 20px 0;
}

.reveal li {
  margin: 8px 0;
}

/* Code Styling */
.reveal code {
  font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
  color: var(--primary-brand);
  background-color: var(--background-light);
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.85em;
}

.reveal pre {
  display: block;
  position: relative;
  width: 90%;
  margin: 20px auto;
  text-align: left;
  font-size: 0.55em;
  line-height: 1.4;
  word-wrap: break-word;
  box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.3);
}

.reveal pre code {
  display: block;
  padding: 20px;
  overflow: auto;
  max-height: 400px;
  word-wrap: normal;
  background-color: #f8f8f8;
  border-left: 4px solid var(--secondary-brand);
  border-radius: 8px;
}

/* Tables */
.reveal table {
  margin: auto;
  border-collapse: collapse;
  border-spacing: 0;
  background-color: var(--background-white);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.reveal table th {
  font-weight: bold;
  background-color: var(--primary-brand);
  color: white;
  padding: 12px;
  border: 1px solid var(--border-color);
}

.reveal table td {
  text-align: left;
  padding: 12px;
  border: 1px solid var(--border-color);
}

.reveal table tr:nth-child(even) {
  background-color: var(--background-light);
}

/* Blockquotes */
.reveal blockquote {
  display: block;
  position: relative;
  width: 70%;
  margin: 20px auto;
  padding: 20px;
  font-style: italic;
  background: rgba(0, 51, 102, 0.05);
  border-left: 4px solid var(--primary-brand);
  border-radius: 5px;
}

/* Images */
.reveal img {
  margin: 20px 0;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* Custom Corporate Components */
.corporate-header {
  background: linear-gradient(135deg, var(--primary-brand), var(--secondary-brand));
  color: white;
  padding: 30px;
  border-radius: 10px;
  margin: 20px 0;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.corporate-header h1,
.corporate-header h2,
.corporate-header h3 {
  color: white;
  margin-bottom: 10px;
}

.highlight-box {
  background-color: rgba(0, 102, 204, 0.1);
  border-left: 6px solid var(--secondary-brand);
  padding: 25px;
  margin: 25px 0;
  border-radius: 8px;
  position: relative;
}

.highlight-box::before {
  content: "💡";
  position: absolute;
  top: -5px;
  left: 20px;
  background-color: var(--secondary-brand);
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.8em;
}

.success-box {
  background-color: rgba(40, 167, 69, 0.1);
  border-left: 6px solid var(--success-color);
  padding: 25px;
  margin: 25px 0;
  border-radius: 8px;
}

.warning-box {
  background-color: rgba(255, 193, 7, 0.1);
  border-left: 6px solid var(--warning-color);
  padding: 25px;
  margin: 25px 0;
  border-radius: 8px;
}

.danger-box {
  background-color: rgba(220, 53, 69, 0.1);
  border-left: 6px solid var(--danger-color);
  padding: 25px;
  margin: 25px 0;
  border-radius: 8px;
}

/* Layout Components */
.two-column {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
  margin: 30px 0;
}

.three-column {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 30px;
  align-items: start;
  margin: 30px 0;
}

.sidebar-main {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 40px;
  align-items: start;
  margin: 30px 0;
}

/* Code Demo Box */
.code-demo {
  background-color: var(--background-light);
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 25px;
  margin: 25px 0;
  position: relative;
}

.code-demo::before {
  content: "💻 Code Example";
  position: absolute;
  top: -12px;
  left: 20px;
  background-color: var(--primary-brand);
  color: white;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.7em;
  font-weight: bold;
}

/* Exercise Box */
.exercise-box {
  background: linear-gradient(135deg, rgba(255, 102, 0, 0.1), rgba(255, 102, 0, 0.05));
  border: 2px solid var(--accent-brand);
  border-radius: 12px;
  padding: 30px;
  margin: 30px 0;
  position: relative;
}

.exercise-box::before {
  content: "✋ Hands-On Exercise";
  position: absolute;
  top: -12px;
  left: 20px;
  background-color: var(--accent-brand);
  color: white;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.7em;
  font-weight: bold;
}

/* Progress Indicators */
.progress-bar {
  background-color: var(--background-light);
  height: 8px;
  border-radius: 4px;
  margin: 20px 0;
  overflow: hidden;
}

.progress-fill {
  background: linear-gradient(90deg, var(--primary-brand), var(--secondary-brand));
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

/* Responsive Design */
@media (max-width: 768px) {
  .reveal {
    font-size: 28px;
  }
  
  .two-column,
  .three-column,
  .sidebar-main {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .corporate-header {
    padding: 20px;
  }
  
  .highlight-box,
  .code-demo,
  .exercise-box {
    padding: 20px;
  }
}

/* Print Styles */
@media print {
  .reveal {
    background: white;
    color: black;
    font-size: 12pt;
  }
  
  .corporate-header {
    background: white !important;
    color: black !important;
    border: 2px solid black;
  }
  
  .highlight-box,
  .success-box,
  .warning-box,
  .danger-box {
    background: white !important;
    border-left-color: black !important;
  }
}

/* Animation Classes */
.fade-in {
  animation: fadeIn 0.6s ease-in;
}

.slide-in-left {
  animation: slideInLeft 0.6s ease-out;
}

.slide-in-right {
  animation: slideInRight 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInLeft {
  from { transform: translateX(-50px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

@keyframes slideInRight {
  from { transform: translateX(50px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}
```

### Technical Theme Template

Create `themes/technical-theme.css`:

```css
/* Technical Theme for Developer-Focused Presentations */

:root {
  --primary-tech: #2d3748;        /* Dark slate */
  --secondary-tech: #4a5568;      /* Medium slate */
  --accent-tech: #00d9ff;         /* Cyan accent */
  --code-bg: #1a202c;             /* Dark code background */
  --code-text: #e2e8f0;           /* Light code text */
  --success-tech: #38a169;        /* Green */
  --warning-tech: #d69e2e;        /* Yellow */
  --error-tech: #e53e3e;          /* Red */
}

.reveal {
  font-family: 'Inter', 'SF Pro Text', system-ui, sans-serif;
  font-size: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.reveal h1, .reveal h2, .reveal h3 {
  color: var(--accent-tech);
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.reveal pre code {
  background-color: var(--code-bg);
  color: var(--code-text);
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.6em;
  line-height: 1.5;
  padding: 25px;
  border-radius: 12px;
  border: 1px solid var(--secondary-tech);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

/* Syntax Highlighting for Technical Theme */
.reveal pre code .keyword { color: #ff79c6; }
.reveal pre code .string { color: #f1fa8c; }
.reveal pre code .comment { color: #6272a4; }
.reveal pre code .function { color: #50fa7b; }
.reveal pre code .number { color: #bd93f9; }

.tech-terminal {
  background-color: #0d1117;
  color: #c9d1d9;
  font-family: 'JetBrains Mono', monospace;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #30363d;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.5);
}

.tech-terminal::before {
  content: "$ ";
  color: var(--accent-tech);
  font-weight: bold;
}
```

## Component Templates

### Reusable Slide Components

#### Code Block Component

```markdown
<!-- CODE_BLOCK_COMPONENT -->
<div class="code-demo">
<h3>${CODE_TITLE}</h3>

\`\`\`${LANGUAGE}
${CODE_CONTENT}
\`\`\`

<div class="highlight-box">
<strong>Key Points:</strong>
<ul>
<li>${POINT_1}</li>
<li>${POINT_2}</li>
<li>${POINT_3}</li>
</ul>
</div>
</div>

**Speaker Notes:** ${CODE_SPEAKER_NOTES}
```

#### Comparison Component

```markdown
<!-- COMPARISON_COMPONENT -->
<div class="two-column">
<div>

## ${LEFT_TITLE}
${LEFT_CONTENT}

### ${LEFT_SUBTITLE}
- ${LEFT_POINT_1}
- ${LEFT_POINT_2}
- ${LEFT_POINT_3}

</div>
<div>

## ${RIGHT_TITLE}
${RIGHT_CONTENT}

### ${RIGHT_SUBTITLE}
- ${RIGHT_POINT_1}
- ${RIGHT_POINT_2}
- ${RIGHT_POINT_3}

</div>
</div>

**Speaker Notes:** ${COMPARISON_NOTES}
```

#### Exercise Component

```markdown
<!-- EXERCISE_COMPONENT -->
<div class="exercise-box">
<h3>Exercise: ${EXERCISE_TITLE}</h3>
<p><strong>Time:</strong> ${EXERCISE_TIME} minutes</p>

## Objective
${EXERCISE_OBJECTIVE}

## Instructions
${EXERCISE_INSTRUCTIONS}

<div class="success-box">
<strong>Success Criteria:</strong>
<ul>
<li>${SUCCESS_1}</li>
<li>${SUCCESS_2}</li>
<li>${SUCCESS_3}</li>
</ul>
</div>
</div>

**Speaker Notes:** ${EXERCISE_SPEAKER_NOTES}
```

## Template Management System

### Template Repository Structure

```
hedgedoc-templates/
├── README.md
├── template-config.yaml
├── scripts/
│   ├── generate-presentation.py
│   ├── validate-template.sh
│   └── deploy-template.sh
├── presentations/
│   ├── workshop-training/
│   │   ├── template.md
│   │   ├── config.yaml
│   │   └── assets/
│   ├── architecture-review/
│   │   ├── template.md
│   │   ├── config.yaml
│   │   └── assets/
│   └── status-update/
│       ├── template.md
│       ├── config.yaml
│       └── assets/
├── themes/
│   ├── corporate/
│   │   ├── theme.css
│   │   ├── assets/
│   │   └── fonts/
│   ├── technical/
│   │   ├── theme.css
│   │   ├── assets/
│   │   └── fonts/
│   └── minimal/
│       ├── theme.css
│       ├── assets/
│       └── fonts/
├── components/
│   ├── layouts/
│   ├── code-blocks/
│   ├── diagrams/
│   └── interactive/
└── examples/
    ├── design-patterns-workshop/
    ├── microservices-architecture/
    └── agile-retrospective/
```

### Template Generation Workflow

#### Advanced Template Processor

```python
#!/usr/bin/env python3
# advanced-template-processor.py

import yaml
import json
import re
from pathlib import Path
from datetime import datetime, timedelta
from typing import Dict, Any, List
import argparse

class AdvancedTemplateProcessor:
    def __init__(self):
        self.variables = {}
        self.functions = {
            'date': self.format_date,
            'time': self.format_time,
            'duration': self.calculate_duration,
            'progress': self.calculate_progress,
            'include': self.include_file,
            'repeat': self.repeat_content
        }
    
    def load_config(self, config_file: str):
        """Load configuration from YAML file"""
        config_path = Path(config_file)
        if config_path.exists():
            with open(config_path, 'r') as f:
                config = yaml.safe_load(f)
                self.variables.update(self.flatten_dict(config))
        else:
            raise FileNotFoundError(f"Configuration file not found: {config_file}")
    
    def process_template(self, template_file: str, output_file: str, config_file: str = None):
        """Process template with variable substitution and functions"""
        if config_file:
            self.load_config(config_file)
        
        template_content = Path(template_file).read_text()
        
        # Process functions first
        processed_content = self.process_functions(template_content)
        
        # Then process variables
        processed_content = self.process_variables(processed_content)
        
        # Write output
        Path(output_file).write_text(processed_content)
        print(f"Generated presentation: {output_file}")
    
    def process_functions(self, content: str) -> str:
        """Process function calls in template"""
        function_pattern = r'\$\{(\w+)\((.*?)\)\}'
        
        def replace_function(match):
            func_name = match.group(1)
            args_str = match.group(2)
            
            if func_name in self.functions:
                # Parse arguments
                args = [arg.strip().strip('"\'') for arg in args_str.split(',') if arg.strip()]
                return str(self.functions[func_name](*args))
            else:
                return match.group(0)  # Return unchanged if function not found
        
        return re.sub(function_pattern, replace_function, content)
    
    def process_variables(self, content: str) -> str:
        """Process variable substitution"""
        for key, value in self.variables.items():
            pattern = f"${{{key.upper()}}}"
            content = content.replace(pattern, str(value))
        
        return content
    
    def flatten_dict(self, d: Dict[str, Any], parent_key: str = '') -> Dict[str, Any]:
        """Flatten nested dictionary"""
        items = []
        for k, v in d.items():
            new_key = f"{parent_key}_{k}".upper() if parent_key else k.upper()
            if isinstance(v, dict):
                items.extend(self.flatten_dict(v, new_key).items())
            elif isinstance(v, list):
                for i, item in enumerate(v):
                    items.append((f"{new_key}_{i+1}", item))
            else:
                items.append((new_key, v))
        return dict(items)
    
    # Template functions
    def format_date(self, date_str: str = None, format_str: str = "%B %d, %Y") -> str:
        """Format date string"""
        if date_str:
            date_obj = datetime.strptime(date_str, "%Y-%m-%d")
        else:
            date_obj = datetime.now()
        return date_obj.strftime(format_str)
    
    def format_time(self, time_str: str, format_str: str = "%H:%M") -> str:
        """Format time string"""
        time_obj = datetime.strptime(time_str, "%H:%M")
        return time_obj.strftime(format_str)
    
    def calculate_duration(self, start_time: str, end_time: str) -> str:
        """Calculate duration between two times"""
        start = datetime.strptime(start_time, "%H:%M")
        end = datetime.strptime(end_time, "%H:%M")
        duration = end - start
        
        hours = duration.seconds // 3600
        minutes = (duration.seconds % 3600) // 60
        
        if hours > 0:
            return f"{hours}h {minutes}m" if minutes > 0 else f"{hours}h"
        else:
            return f"{minutes}m"
    
    def calculate_progress(self, current: str, total: str) -> str:
        """Calculate progress percentage"""
        current_num = int(current)
        total_num = int(total)
        percentage = (current_num / total_num) * 100
        return f"{percentage:.0f}%"
    
    def include_file(self, file_path: str) -> str:
        """Include content from another file"""
        try:
            return Path(file_path).read_text()
        except FileNotFoundError:
            return f"<!-- File not found: {file_path} -->"
    
    def repeat_content(self, content: str, count: str) -> str:
        """Repeat content multiple times"""
        return content * int(count)

def main():
    parser = argparse.ArgumentParser(description='Advanced HedgeDoc Template Processor')
    parser.add_argument('template', help='Template file path')
    parser.add_argument('output', help='Output file path')
    parser.add_argument('--config', help='Configuration file path')
    
    args = parser.parse_args()
    
    processor = AdvancedTemplateProcessor()
    processor.process_template(args.template, args.output, args.config)

if __name__ == "__main__":
    main()
```

### Template Validation System

```bash
#!/bin/bash
# validate-template.sh

TEMPLATE_FILE="$1"
CONFIG_FILE="$2"

if [ -z "$TEMPLATE_FILE" ]; then
    echo "Usage: $0 <template_file> [config_file]"
    exit 1
fi

echo "Validating template: $TEMPLATE_FILE"

# Check if template file exists
if [ ! -f "$TEMPLATE_FILE" ]; then
    echo "❌ Template file not found: $TEMPLATE_FILE"
    exit 1
fi

# Validate YAML frontmatter
echo "Checking YAML frontmatter..."
if head -20 "$TEMPLATE_FILE" | grep -q "^---$"; then
    echo "✅ YAML frontmatter found"
else
    echo "⚠️  No YAML frontmatter detected"
fi

# Check for required variables
echo "Checking for unresolved variables..."
UNRESOLVED=$(grep -o '\${[^}]*}' "$TEMPLATE_FILE" | sort -u)
if [ -n "$UNRESOLVED" ]; then
    echo "⚠️  Unresolved variables found:"
    echo "$UNRESOLVED"
else
    echo "✅ No unresolved variables"
fi

# Validate reveal.js slideOptions
echo "Checking reveal.js configuration..."
if grep -q "slideOptions:" "$TEMPLATE_FILE"; then
    echo "✅ SlideOptions configuration found"
else
    echo "⚠️  No slideOptions configuration"
fi

# Check for speaker notes
SPEAKER_NOTES=$(grep -c "**Speaker Notes:" "$TEMPLATE_FILE")
if [ "$SPEAKER_NOTES" -gt 0 ]; then
    echo "✅ Speaker notes found ($SPEAKER_NOTES occurrences)"
else
    echo "⚠️  No speaker notes found"
fi

# Validate CSS styles
echo "Checking CSS styles..."
if grep -q "<style>" "$TEMPLATE_FILE"; then
    echo "✅ CSS styles found"
else
    echo "⚠️  No CSS styles found"
fi

# Check for accessibility considerations
echo "Checking accessibility..."
ALT_TEXT_COUNT=$(grep -c 'alt="' "$TEMPLATE_FILE")
if [ "$ALT_TEXT_COUNT" -gt 0 ]; then
    echo "✅ Alt text found for images ($ALT_TEXT_COUNT occurrences)"
else
    echo "⚠️  No alt text found for images"
fi

# Validate configuration file if provided
if [ -n "$CONFIG_FILE" ] && [ -f "$CONFIG_FILE" ]; then
    echo "Validating configuration file: $CONFIG_FILE"
    if python3 -c "import yaml; yaml.safe_load(open('$CONFIG_FILE'))" 2>/dev/null; then
        echo "✅ Configuration file is valid YAML"
    else
        echo "❌ Configuration file is not valid YAML"
        exit 1
    fi
fi

echo "Template validation completed ✅"
```

## Integration with HedgeDoc

### Template Deployment

#### Automated Template Deployment

```bash
#!/bin/bash
# deploy-template.sh

TEMPLATE_DIR="$1"
HEDGEDOC_URL="$2"
HEDGEDOC_TOKEN="$3"

if [ -z "$TEMPLATE_DIR" ] || [ -z "$HEDGEDOC_URL" ] || [ -z "$HEDGEDOC_TOKEN" ]; then
    echo "Usage: $0 <template_directory> <hedgedoc_url> <hedgedoc_token>"
    exit 1
fi

echo "Deploying templates from: $TEMPLATE_DIR"
echo "Target HedgeDoc instance: $HEDGEDOC_URL"

# Find all template files
find "$TEMPLATE_DIR" -name "*.md" -type f | while read -r template_file; do
    echo "Deploying: $template_file"
    
    # Get relative path for note title
    RELATIVE_PATH=$(realpath --relative-to="$TEMPLATE_DIR" "$template_file")
    NOTE_TITLE="Template: ${RELATIVE_PATH%.*}"
    
    # Create note via API
    RESPONSE=$(curl -s -X POST "$HEDGEDOC_URL/api/notes" \
        -H "Authorization: Bearer $HEDGEDOC_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
            \"title\": \"$NOTE_TITLE\",
            \"content\": $(jq -Rs . < "$template_file"),
            \"tags\": [\"template\", \"presentation\"]
        }")
    
    if echo "$RESPONSE" | jq -e '.id' > /dev/null; then
        NOTE_ID=$(echo "$RESPONSE" | jq -r '.id')
        echo "✅ Created note: $HEDGEDOC_URL/$NOTE_ID"
    else
        echo "❌ Failed to create note for $template_file"
    fi
done

echo "Template deployment completed"
```

### Template Usage Workflow

#### Creating Presentations from Templates

1. **Select Template**: Choose appropriate template from repository
2. **Configure Variables**: Create or modify configuration file
3. **Generate Presentation**: Run template processor
4. **Review Output**: Validate generated presentation
5. **Upload to HedgeDoc**: Deploy to HedgeDoc instance
6. **Customize**: Make final adjustments in HedgeDoc editor
7. **Present**: Use HedgeDoc presentation mode

#### Example Workflow Commands

```bash
# 1. Generate presentation from template
python3 scripts/advanced-template-processor.py \
    templates/presentations/workshop-training/template.md \
    output/my-workshop.md \
    --config my-workshop-config.yaml

# 2. Validate generated presentation
./scripts/validate-template.sh output/my-workshop.md

# 3. Deploy to HedgeDoc
./scripts/deploy-template.sh output/ https://hedgedoc.example.com $HEDGEDOC_TOKEN

# 4. Backup template and generated presentations
./scripts/backup-templates.sh
```

This comprehensive templating guide provides everything needed to create, manage, and deploy professional presentation templates with HedgeDoc, from basic variable substitution to advanced component systems and automated deployment workflows.