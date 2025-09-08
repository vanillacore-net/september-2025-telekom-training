# Markdown Presentation Tools Comparison Matrix

**Research Date**: 2025-09-08  
**Research Context**: Finding replacement for broken Marp implementation

## Evaluation Criteria

| Criteria | Weight | Description |
|----------|--------|-------------|
| **Layout Support** | High | Two-column, half-picture, code embedding |
| **Theme Customization** | High | Custom CSS, typography control |
| **Markdown Purity** | Medium | Clean markdown without HTML mixing |
| **Progressive Reveal** | Medium | Fragment/step-by-step reveals |
| **Maintenance** | High | Active development, security, dependencies |
| **Export Options** | Medium | PDF, HTML, presentation formats |
| **Learning Curve** | Medium | Setup complexity, documentation |
| **Performance** | Low | Build speed, runtime performance |

## Tool Comparison Matrix

| Feature | **Marp (Current)** | **Slidev** | **reveal-md** | **mdx-deck** | **Obsidian Slides** |
|---------|-------------------|------------|---------------|--------------|-------------------|
| **Layout Support** | ❌ Broken | ✅ Excellent | ✅ Good | ⚠️ Limited | ✅ Good |
| **Two-Column** | ❌ CSS Issues | ✅ Native | ✅ CSS Grid | ⚠️ Manual | ✅ RevealJS |
| **Half-Picture** | ❌ HTML Required | ✅ `image-right` | ✅ CSS Flex | ❌ Complex | ✅ RevealJS |
| **Code Embedding** | ✅ Works | ✅ Excellent | ✅ Good | ✅ Good | ✅ Good |
| **Theme System** | ⚠️ Limited | ✅ Vue/CSS | ✅ Full CSS | ⚠️ JS Themes | ✅ CSS |
| **Markdown Purity** | ⚠️ HTML Mixed | ✅ Clean + YAML | ✅ Clean + HTML | ❌ MDX/JSX | ✅ Clean |
| **Progressive Reveal** | ⚠️ Limited | ✅ `v-click` | ✅ Fragments | ✅ Components | ✅ Fragments |
| **Active Development** | ✅ Active | ✅ Very Active | ✅ Active | ❌ Deprecated | ✅ Active |
| **Security/Dependencies** | ✅ Good | ✅ Modern | ⚠️ Some Warnings | ❌ Many Warnings | ✅ Good |
| **Export PDF** | ✅ Native | ✅ Built-in | ✅ Built-in | ⚠️ Complex | ✅ RevealJS |
| **Export HTML** | ✅ Native | ✅ SPA/Static | ✅ Static | ✅ Built-in | ❌ Obsidian Only |
| **Setup Complexity** | 🟢 Simple | 🟡 Medium | 🟢 Simple | 🔴 Complex | 🟢 Simple |
| **Documentation** | 🟢 Good | 🟢 Excellent | 🟢 Good | 🔴 Outdated | 🟢 Good |
| **TypeScript Support** | ❌ No | ✅ Native | ❌ No | ⚠️ Requires Setup | ❌ No |

## Detailed Analysis

### 1. Marp (Current Implementation)
**Status**: BROKEN - Critical layout issues

**Issues Identified**:
- Two-column layouts don't work properly with CSS classes
- Complex layouts require HTML mixing in markdown
- Theme system limitations prevent proper responsive design
- Fixed viewport units (vw/vh) cause scaling issues

**Evidence**:
- `<!-- _class: content-slide two-columns -->` doesn't apply layouts
- `<div class="columns-container">` required for two-column content
- CSS constraints make complex layouts fragile

### 2. Slidev (Vue-based) ⭐ TOP RECOMMENDATION
**Status**: EXCELLENT - Addresses all major issues

**Strengths**:
- **Layout System**: Built-in layouts (`two-cols`, `image-right`, etc.)
- **Clean Markdown**: Pure markdown with YAML frontmatter
- **Vue Components**: Can use Vue components when needed
- **Progressive Reveal**: Elegant `v-click` system
- **Theme System**: Full Vue/CSS customization
- **Modern Stack**: Vite, TypeScript, modern dependencies
- **Export**: PDF, SPA, static HTML

**Test Results** (http://localhost:3001):
- Two-column layout works perfectly with `layout: two-cols`
- Image layouts work with `layout: image-right`
- Code highlighting excellent with Shiki
- Smooth transitions and animations
- Clean, professional output

**Migration Path**:
- Convert Marp slides to Slidev format
- Replace `<!-- _class -->` with `layout:` in frontmatter  
- Remove HTML divs, use native layouts
- Add progressive reveals with `v-click`

### 3. reveal-md (RevealJS + Markdown)
**Status**: GOOD - Proven solution

**Strengths**:
- **Proven Framework**: RevealJS is battle-tested
- **Full RevealJS Features**: All layouts and features available
- **CSS Control**: Complete CSS customization
- **Fragment System**: Robust progressive reveals

**Test Results** (http://localhost:3002):
- Two-column layouts work with CSS Grid
- Fragment system works well
- Code highlighting functional
- Some HTML required for complex layouts

**Concerns**:
- Still requires HTML for complex layouts
- Less modern than Slidev
- Some npm audit warnings

### 4. mdx-deck (React/MDX)
**Status**: DEPRECATED - Not recommended

**Critical Issues**:
- **Maintenance**: Largely unmaintained
- **Dependencies**: 133+ vulnerabilities in npm audit
- **Complexity**: Requires React/JSX knowledge
- **Build Issues**: Failed to start properly

**Decision**: REJECTED due to security and maintenance concerns

### 5. Obsidian Slides Plugin
**Status**: LIMITED - Obsidian dependency

**Limitations**:
- **Obsidian Required**: Must use Obsidian app
- **Export Limitations**: Cannot export standalone HTML easily
- **Distribution Issues**: Harder to share presentations
- **Not tested**: Requires Obsidian setup

**Decision**: NOT SUITABLE for standalone presentations

## Final Recommendation

### Winner: Slidev 🏆

**Reasons**:
1. **Solves Core Problems**: Clean two-column, half-picture layouts
2. **Modern Architecture**: Vite, Vue 3, TypeScript support
3. **Developer Experience**: Excellent docs, active community
4. **Layout System**: Native layout components eliminate HTML hacks
5. **Progressive Enhancement**: Can add Vue components when needed
6. **Export Options**: PDF, SPA, static HTML all work well

### Migration Strategy

**Phase 1: Setup** (30 minutes)
- Install Slidev in presentation directory
- Create theme matching Telekom design
- Setup build scripts

**Phase 2: Content Migration** (2 hours)
- Convert existing slides to Slidev format
- Replace HTML layouts with native layouts
- Add progressive reveals where beneficial

**Phase 3: Enhancement** (1 hour)
- Optimize themes and styling
- Add any custom components needed
- Test export functionality

**Phase 4: Validation** (30 minutes)
- Verify all layouts work correctly
- Test PDF export
- Confirm responsive design

### Alternative: reveal-md

If Slidev proves too complex, `reveal-md` is a solid fallback:
- Proven RevealJS foundation
- Good layout support  
- Simpler than Slidev
- Faster migration from current state

## Implementation Next Steps

1. **Create Slidev Implementation**: Convert current slides to Slidev
2. **Screenshot Comparison**: Document visual differences
3. **Performance Testing**: Verify export quality
4. **Final Recommendation**: Confirm Slidev or suggest reveal-md

---

**Research Complete**: Slidev recommended for implementation in next PRB