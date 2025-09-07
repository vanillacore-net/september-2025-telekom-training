# Reveal.js Intro Presentation Setup

This directory contains the reveal.js setup for the Telekom Architecture Training presentation.

## Directory Structure

```
revealjs-intro/
├── css/
│   └── custom.css          # Custom styling with 16:9 aspect ratio
├── img/
│   └── VanillaCore_Vertical.png  # CORE logo
├── js/
│   └── config.js           # Reveal.js configuration
├── index.html              # Main presentation file
├── README.md               # This file
└── test-loading.html       # Verification page
```

## Features

- ✅ **16:9 Aspect Ratio**: Configured for widescreen presentations
- ✅ **Reveal.js 4.x**: Loaded from CDN for latest features
- ✅ **Fragment Support**: Line-by-line reveal with first line always visible
- ✅ **CORE Logo**: Integrated from screenshots directory
- ✅ **Custom Styling**: Telekom colors and professional theme
- ✅ **Proper Configuration**: All plugins and settings configured

## Usage

1. Open `index.html` in a web browser
2. Use arrow keys or click controls to navigate
3. Press `ESC` for slide overview
4. Press `?` for help

## Development

- **Add slides**: Edit the `<div class="slides">` section in `index.html`
- **Modify styling**: Edit `css/custom.css`
- **Change configuration**: Edit `js/config.js`
- **Add images**: Place files in `img/` directory

## Fragment Guidelines

- First line of each slide is ALWAYS visible (not a fragment)
- Use `class="fragment"` for subsequent elements that should appear step-by-step
- Example:
  ```html
  <section>
    <h2>Title</h2>                        <!-- Always visible -->
    <p>First point</p>                    <!-- Always visible -->
    <p class="fragment">Second point</p>  <!-- Fragment -->
    <p class="fragment">Third point</p>   <!-- Fragment -->
  </section>
  ```

## Verification

Open `test-loading.html` to verify the setup is complete and working correctly.