const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

(async () => {
  console.log('🔍 CRITICAL ISSUES VALIDATION - RevealJS Presentation');
  console.log('Resolution: 1920x1080 (FHD)');
  
  const browser = await chromium.launch({
    headless: false
  });
  
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });
  await page.goto(`file://${process.cwd()}/index.html`);
  
  const issues = [];
  
  try {
    // Wait for presentation to load
    await page.waitForSelector('.reveal .slides section', { timeout: 5000 });
    
    console.log('\n1. ✅ Presentation loaded successfully');
    
    // Check for text overflow issues
    console.log('\n2. 🔍 Checking for text overflow issues...');
    
    const overflowElements = await page.evaluate(() => {
      const elements = document.querySelectorAll('h1, h2, h3, p, li');
      const overflowIssues = [];
      
      elements.forEach((el, index) => {
        const rect = el.getBoundingClientRect();
        const parentRect = el.closest('section').getBoundingClientRect();
        
        // Check if text exceeds viewport bounds
        if (rect.right > window.innerWidth || rect.bottom > window.innerHeight) {
          overflowIssues.push({
            element: el.tagName,
            text: el.textContent.substring(0, 50) + '...',
            position: { right: rect.right, bottom: rect.bottom },
            viewport: { width: window.innerWidth, height: window.innerHeight }
          });
        }
      });
      
      return overflowIssues;
    });
    
    if (overflowElements.length > 0) {
      console.log('❌ TEXT OVERFLOW DETECTED:');
      overflowElements.forEach(issue => {
        console.log(`   ${issue.element}: "${issue.text}"`);
        console.log(`   Position: right=${issue.position.right}px, bottom=${issue.position.bottom}px`);
        console.log(`   Viewport: ${issue.viewport.width}x${issue.viewport.height}`);
      });
      issues.push('Text overflow detected');
    } else {
      console.log('✅ No text overflow issues found');
    }
    
    // Navigate to code slide to check alignment
    console.log('\n3. 🔍 Checking code slide text alignment...');
    
    // Find code slide
    await page.evaluate(() => {
      const slides = document.querySelectorAll('.reveal .slides section');
      for (let i = 0; i < slides.length; i++) {
        const slide = slides[i];
        if (slide.classList.contains('code-slide') || slide.querySelector('pre code')) {
          window.Reveal.slide(i);
          break;
        }
      }
    });
    
    await page.waitForTimeout(1000);
    
    const codeSlideIssues = await page.evaluate(() => {
      const codeSlide = document.querySelector('.present.code-slide');
      if (!codeSlide) return [];
      
      const issues = [];
      const h2 = codeSlide.querySelector('h2');
      const introText = codeSlide.querySelector('p, .text-section p');
      
      if (h2) {
        const style = window.getComputedStyle(h2);
        if (style.textAlign !== 'left') {
          issues.push(`Code slide H2 text-align: ${style.textAlign} (should be left)`);
        }
      }
      
      if (introText) {
        const style = window.getComputedStyle(introText);
        if (style.textAlign !== 'left') {
          issues.push(`Code slide intro text-align: ${style.textAlign} (should be left)`);
        }
      }
      
      return issues;
    });
    
    if (codeSlideIssues.length > 0) {
      console.log('❌ CODE SLIDE ALIGNMENT ISSUES:');
      codeSlideIssues.forEach(issue => console.log(`   ${issue}`));
      issues.push('Code slide text alignment issues');
    } else {
      console.log('✅ Code slide text alignment is correct');
    }
    
    // Check for scrolling/overlap issues
    console.log('\n4. 🔍 Checking for code block scrolling/overlap issues...');
    
    const scrollingIssues = await page.evaluate(() => {
      const codeBlocks = document.querySelectorAll('pre code');
      const issues = [];
      
      codeBlocks.forEach((code, index) => {
        const pre = code.parentElement;
        const rect = pre.getBoundingClientRect();
        const computedStyle = window.getComputedStyle(pre);
        
        // Check for overlapping issues
        if (computedStyle.position === 'absolute' || computedStyle.zIndex < 0) {
          issues.push(`Code block ${index + 1}: Positioning issue (position: ${computedStyle.position}, z-index: ${computedStyle.zIndex})`);
        }
        
        // Check for proper scrolling setup
        if (rect.height > window.innerHeight * 0.8 && computedStyle.overflow !== 'auto' && computedStyle.overflow !== 'scroll') {
          issues.push(`Code block ${index + 1}: Large block without proper scrolling (overflow: ${computedStyle.overflow})`);
        }
        
        // Check if multiple code blocks overlap
        const allCodeBlocks = Array.from(document.querySelectorAll('pre'));
        const overlapping = allCodeBlocks.filter(other => {
          if (other === pre) return false;
          const otherRect = other.getBoundingClientRect();
          return (rect.left < otherRect.right && rect.right > otherRect.left && 
                  rect.top < otherRect.bottom && rect.bottom > otherRect.top);
        });
        
        if (overlapping.length > 0) {
          issues.push(`Code block ${index + 1}: Overlapping with ${overlapping.length} other code blocks`);
        }
      });
      
      return issues;
    });
    
    if (scrollingIssues.length > 0) {
      console.log('❌ CODE SCROLLING/OVERLAP ISSUES:');
      scrollingIssues.forEach(issue => console.log(`   ${issue}`));
      issues.push('Code scrolling/overlap issues');
    } else {
      console.log('✅ No code scrolling/overlap issues found');
    }
    
    // Take validation screenshots
    console.log('\n5. 📸 Taking validation screenshots...');
    
    // Screenshot: Regular content slide
    await page.evaluate(() => {
      const slides = document.querySelectorAll('.reveal .slides section');
      for (let i = 0; i < slides.length; i++) {
        const slide = slides[i];
        if (slide.classList.contains('single-column') || slide.classList.contains('two-columns')) {
          window.Reveal.slide(i);
          break;
        }
      }
    });
    await page.waitForTimeout(500);
    await page.screenshot({ path: '.playwright-mcp/critical-validation-content-slide.png' });
    
    // Screenshot: Code slide
    await page.evaluate(() => {
      const slides = document.querySelectorAll('.reveal .slides section');
      for (let i = 0; i < slides.length; i++) {
        const slide = slides[i];
        if (slide.classList.contains('code-slide') || slide.querySelector('pre code')) {
          window.Reveal.slide(i);
          break;
        }
      }
    });
    await page.waitForTimeout(500);
    await page.screenshot({ path: '.playwright-mcp/critical-validation-code-slide.png' });
    
    // Screenshot: Navigate through slides to check flow
    const slideCount = await page.evaluate(() => {
      return document.querySelectorAll('.reveal .slides section').length;
    });
    
    console.log(`\n6. 🔄 Navigating through ${slideCount} slides to check for issues...`);
    
    for (let i = 0; i < slideCount; i++) {
      await page.evaluate((slideIndex) => {
        window.Reveal.slide(slideIndex);
      }, i);
      await page.waitForTimeout(200);
      
      // Check for any visible issues during navigation
      const navigationIssues = await page.evaluate(() => {
        const issues = [];
        const currentSlide = document.querySelector('.present');
        
        if (!currentSlide) {
          issues.push('No current slide detected during navigation');
        } else {
          // Check if slide content is visible
          const content = currentSlide.querySelector('h1, h2, p, li, pre');
          if (content) {
            const rect = content.getBoundingClientRect();
            if (rect.width === 0 || rect.height === 0) {
              issues.push('Slide content has zero dimensions');
            }
          }
        }
        
        return issues;
      });
      
      if (navigationIssues.length > 0) {
        console.log(`❌ Slide ${i + 1} issues:`, navigationIssues);
        issues.push(`Slide ${i + 1} navigation issues`);
      }
    }
    
    console.log('✅ Navigation check completed');
    
  } catch (error) {
    console.error('❌ VALIDATION ERROR:', error.message);
    issues.push(`Validation error: ${error.message}`);
  }
  
  await browser.close();
  
  // Summary
  console.log('\n' + '='.repeat(60));
  console.log('CRITICAL ISSUES VALIDATION SUMMARY');
  console.log('='.repeat(60));
  
  if (issues.length === 0) {
    console.log('✅ NO CRITICAL ISSUES DETECTED');
    console.log('The presentation appears to be working correctly at FHD resolution.');
  } else {
    console.log('❌ CRITICAL ISSUES FOUND:');
    issues.forEach((issue, index) => {
      console.log(`${index + 1}. ${issue}`);
    });
    console.log('\nThese issues need to be addressed in the CSS.');
  }
  
  console.log('\n📸 Screenshots saved:');
  console.log('- .playwright-mcp/critical-validation-content-slide.png');
  console.log('- .playwright-mcp/critical-validation-code-slide.png');
  
})().catch(console.error);