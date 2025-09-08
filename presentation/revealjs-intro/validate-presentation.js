const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

async function validatePresentation() {
  let browser;
  try {
    browser = await puppeteer.launch({
      headless: false,
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--window-size=1920,1080',
        '--disable-web-security',
        '--allow-running-insecure-content'
      ]
    });
    
    const page = await browser.newPage();
    await page.setViewport({ width: 1920, height: 1080 });
    
    // Ensure the directory exists
    const screenshotDir = './validation-screenshots';
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    console.log('🌐 Navigating to presentation...');
    await page.goto('http://localhost:8000', { 
      waitUntil: 'networkidle0',
      timeout: 30000 
    });
    
    // Wait for RevealJS to initialize
    await page.waitForSelector('.reveal', { timeout: 10000 });
    await new Promise(resolve => setTimeout(resolve, 2000)); // Give RevealJS time to fully initialize
    
    console.log('📋 Starting comprehensive validation...');
    
    // Get total number of slides
    const totalSlides = await page.evaluate(() => {
      return Reveal.getTotalSlides();
    });
    
    console.log(`🎯 Found ${totalSlides} slides to validate`);
    
    let issues = [];
    
    // Validate each slide
    for (let i = 0; i < totalSlides; i++) {
      console.log(`📸 Validating slide ${i + 1}/${totalSlides}...`);
      
      // Navigate to slide
      await page.evaluate((slideIndex) => {
        Reveal.slide(slideIndex, 0);
      }, i);
      
      await new Promise(resolve => setTimeout(resolve, 1000)); // Wait for slide transition
      
      // Get slide info
      const slideInfo = await page.evaluate(() => {
        const currentSlide = Reveal.getCurrentSlide();
        const classList = Array.from(currentSlide.classList);
        return {
          classes: classList,
          hasCodeStandalone: classList.includes('code-standalone'),
          hasCodeEmbedded: currentSlide.querySelector('.code-embedded') !== null,
          hasTwoColumns: classList.includes('two-columns'),
          hasSingleColumn: classList.includes('single-column'),
          isTitle: classList.includes('title-slide') || currentSlide.querySelector('.title-slide') !== null,
          isSection: classList.includes('section-slide') || currentSlide.querySelector('.section-slide') !== null,
          isHalfPicture: classList.includes('half-picture'),
          isFullPicture: classList.includes('full-picture') || currentSlide.querySelector('.full-picture') !== null
        };
      });
      
      console.log(`   Slide ${i + 1}: ${Object.keys(slideInfo).filter(key => slideInfo[key] && key !== 'classes').join(', ') || 'content-slide'}`);
      
      // Take screenshot
      const filename = `slide-${String(i + 1).padStart(2, '0')}-${slideInfo.isTitle ? 'title' : slideInfo.isSection ? 'section' : slideInfo.hasCodeStandalone ? 'code-standalone' : slideInfo.hasCodeEmbedded ? 'code-embedded' : slideInfo.hasTwoColumns ? 'two-columns' : slideInfo.hasSingleColumn ? 'single-column' : slideInfo.isHalfPicture ? 'half-picture' : slideInfo.isFullPicture ? 'full-picture' : 'content'}.png`;
      
      await page.screenshot({
        path: path.join(screenshotDir, filename),
        fullPage: false
      });
      
      // Check for specific issues based on slide type
      const slideIssues = await page.evaluate((slideIndex, slideInfo) => {
        const issues = [];
        const currentSlide = Reveal.getCurrentSlide();
        
        // Check for text overflow
        const textElements = currentSlide.querySelectorAll('h1, h2, h3, p, li');
        textElements.forEach((el, idx) => {
          const rect = el.getBoundingClientRect();
          if (rect.right > window.innerWidth - 20) { // 20px buffer
            issues.push(`Text overflow detected on element ${idx}: "${el.textContent.substring(0, 50)}..."`);
          }
        });
        
        // Check code blocks specifically for full width if standalone
        if (slideInfo.hasCodeStandalone) {
          const codeBlocks = currentSlide.querySelectorAll('pre');
          codeBlocks.forEach((pre, idx) => {
            const rect = pre.getBoundingClientRect();
            const viewportWidth = window.innerWidth;
            
            // For standalone code blocks, check if they use most of the viewport width
            const widthUsed = rect.width / viewportWidth;
            if (widthUsed < 0.90) { // Should use at least 90% of viewport width
              issues.push(`Standalone code block ${idx} not using full width: ${Math.round(widthUsed * 100)}% of viewport (expected >90%)`);
            }
            
            if (rect.right > viewportWidth + 5) { // Allow small overhang for scrollbars
              issues.push(`Code block ${idx} extends beyond viewport by ${Math.round(rect.right - viewportWidth)}px`);
            }
          });
        }
        
        // Check alignment for content slides
        if (slideInfo.hasSingleColumn || slideInfo.hasTwoColumns || slideInfo.hasCodeEmbedded) {
          const headings = currentSlide.querySelectorAll('h2, h3');
          headings.forEach((heading, idx) => {
            const styles = window.getComputedStyle(heading);
            if (styles.textAlign !== 'left') {
              issues.push(`Heading ${idx} not left-aligned: "${heading.textContent.substring(0, 30)}..."`);
            }
          });
        }
        
        // Check for significant overlapping elements (disabled due to false positives)
        // Note: Overlap detection disabled as it generates too many false positives
        // with RevealJS's complex nested DOM structure
        
        return issues.map(issue => `Slide ${slideIndex + 1}: ${issue}`);
      }, i, slideInfo);
      
      issues = issues.concat(slideIssues);
    }
    
    console.log('✅ Validation complete!');
    console.log(`📁 Screenshots saved to: ${path.resolve(screenshotDir)}`);
    
    if (issues.length === 0) {
      console.log('🎉 No issues found! The presentation looks great.');
    } else {
      console.log(`⚠️  Found ${issues.length} issues:`);
      issues.forEach((issue, idx) => {
        console.log(`   ${idx + 1}. ${issue}`);
      });
    }
    
    // Generate validation report
    const report = {
      timestamp: new Date().toISOString(),
      totalSlides,
      issues,
      screenshots: fs.readdirSync(screenshotDir).filter(f => f.endsWith('.png'))
    };
    
    fs.writeFileSync('./validation-report.json', JSON.stringify(report, null, 2));
    console.log('📊 Validation report saved to: validation-report.json');
    
    return issues;
    
  } catch (error) {
    console.error('❌ Validation failed:', error);
    throw error;
  } finally {
    if (browser) {
      await browser.close();
    }
  }
}

// Run validation if this file is executed directly
if (require.main === module) {
  validatePresentation()
    .then(issues => {
      process.exit(issues.length > 0 ? 1 : 0);
    })
    .catch(error => {
      console.error('Fatal error:', error);
      process.exit(1);
    });
}

module.exports = { validatePresentation };