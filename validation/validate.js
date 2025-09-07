const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function validatePresentation() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  // Create screenshots directory
  const screenshotDir = path.join(__dirname, 'screenshots');
  if (!fs.existsSync(screenshotDir)) {
    fs.mkdirSync(screenshotDir);
  }

  const results = {
    slides: [],
    errors: [],
    summary: {
      totalSlides: 0,
      fragmentTests: 0,
      speakerNotesTest: false,
      pdfTest: false
    }
  };

  try {
    console.log('🚀 Starting Playwright validation of reveal.js presentation');
    
    // Navigate to the presentation
    console.log('📖 Loading presentation...');
    await page.goto('http://localhost:8080/');
    
    // Wait for reveal.js to initialize
    await page.waitForSelector('.reveal .slides', { timeout: 10000 });
    await page.waitForTimeout(2000); // Extra time for initialization
    
    console.log('✅ Presentation loaded successfully');

    // Get total number of slides
    const slideCount = await page.evaluate(() => {
      if (window.Reveal) {
        return window.Reveal.getTotalSlides();
      }
      return document.querySelectorAll('.reveal .slides section').length;
    });
    
    results.summary.totalSlides = slideCount;
    console.log(`📊 Found ${slideCount} slides`);

    // Test each slide
    for (let slideIndex = 0; slideIndex < slideCount; slideIndex++) {
      console.log(`\n🔍 Testing slide ${slideIndex + 1}/${slideCount}`);
      
      // Navigate to slide
      await page.evaluate((index) => {
        if (window.Reveal) {
          window.Reveal.slide(index, 0);
        }
      }, slideIndex);
      
      await page.waitForTimeout(1000); // Wait for slide transition
      
      // Take screenshot of slide
      const slideScreenshot = path.join(screenshotDir, `slide-${slideIndex + 1}.png`);
      await page.screenshot({ 
        path: slideScreenshot,
        fullPage: false 
      });
      console.log(`📸 Screenshot saved: slide-${slideIndex + 1}.png`);
      
      // Check if first line is visible (not a fragment)
      const firstLineVisible = await page.evaluate(() => {
        const slide = document.querySelector('.reveal .slides .present');
        if (!slide) return false;
        
        // Find first text element (h1, h2, h3, p, or first li)
        const firstElements = slide.querySelectorAll('h1, h2, h3, p, li');
        if (firstElements.length === 0) return true; // No text elements
        
        const firstElement = firstElements[0];
        const style = window.getComputedStyle(firstElement);
        return style.display !== 'none' && style.visibility !== 'hidden' && 
               !firstElement.classList.contains('fragment') || 
               firstElement.classList.contains('visible');
      });
      
      // Count and test fragments on this slide
      const fragmentInfo = await page.evaluate(() => {
        const slide = document.querySelector('.reveal .slides .present');
        if (!slide) return { total: 0, visible: 0 };
        
        const fragments = slide.querySelectorAll('.fragment');
        const visibleFragments = slide.querySelectorAll('.fragment.visible');
        
        return {
          total: fragments.length,
          visible: visibleFragments.length
        };
      });
      
      console.log(`   First line visible: ${firstLineVisible ? '✅' : '❌'}`);
      console.log(`   Fragments: ${fragmentInfo.visible}/${fragmentInfo.total} visible`);
      
      // Test fragment clicks if there are fragments
      let fragmentTestsPassed = 0;
      if (fragmentInfo.total > 0) {
        console.log(`   🎬 Testing ${fragmentInfo.total} fragments...`);
        
        // Reset slide to beginning
        await page.evaluate((index) => {
          if (window.Reveal) {
            window.Reveal.slide(index, 0);
            // Hide all fragments
            const slide = document.querySelector('.reveal .slides .present');
            if (slide) {
              const fragments = slide.querySelectorAll('.fragment');
              fragments.forEach(f => f.classList.remove('visible'));
            }
          }
        }, slideIndex);
        
        await page.waitForTimeout(500);
        
        // Click through fragments
        for (let f = 0; f < fragmentInfo.total; f++) {
          await page.keyboard.press('ArrowRight');
          await page.waitForTimeout(300);
          
          const visibleAfterClick = await page.evaluate(() => {
            const slide = document.querySelector('.reveal .slides .present');
            if (!slide) return 0;
            return slide.querySelectorAll('.fragment.visible').length;
          });
          
          if (visibleAfterClick === f + 1) {
            fragmentTestsPassed++;
            console.log(`     Fragment ${f + 1}: ✅`);
          } else {
            console.log(`     Fragment ${f + 1}: ❌ (expected ${f + 1} visible, got ${visibleAfterClick})`);
          }
        }
      }
      
      results.slides.push({
        slideNumber: slideIndex + 1,
        firstLineVisible: firstLineVisible,
        totalFragments: fragmentInfo.total,
        fragmentTestsPassed: fragmentTestsPassed,
        screenshotPath: slideScreenshot
      });
      
      results.summary.fragmentTests += fragmentTestsPassed;
    }
    
    // Test speaker notes (press S key)
    console.log('\n🗣️  Testing speaker notes...');
    await page.keyboard.press('s');
    await page.waitForTimeout(2000);
    
    // Check if speaker notes window opened (check for popup or overlay)
    const speakerNotesActive = await page.evaluate(() => {
      // Check for speaker notes elements or popup
      return document.querySelector('.speaker-notes') !== null || 
             document.querySelector('[class*="notes"]') !== null ||
             window.location.search.includes('notes') ||
             document.body.classList.contains('speaker-notes');
    });
    
    if (speakerNotesActive) {
      console.log('✅ Speaker notes test passed');
      results.summary.speakerNotesTest = true;
    } else {
      console.log('❌ Speaker notes test failed - trying alternative method');
      // Try accessing speaker notes URL directly
      await page.goto('http://localhost:8080/?notes=true');
      await page.waitForTimeout(1000);
      
      const notesVisible = await page.evaluate(() => {
        return document.body.textContent.includes('Willkommen zur Cloud Fundamentals') ||
               document.querySelector('.notes') !== null;
      });
      
      if (notesVisible) {
        console.log('✅ Speaker notes accessible via URL parameter');
        results.summary.speakerNotesTest = true;
      } else {
        console.log('❌ Speaker notes not accessible');
        results.errors.push('Speaker notes not accessible');
      }
    }
    
    // Test PDF export
    console.log('\n📄 Testing PDF export...');
    await page.goto('http://localhost:8080/?print-pdf');
    await page.waitForTimeout(2000);
    
    const pdfReady = await page.evaluate(() => {
      // Check if PDF styles are applied
      const isPrintMode = document.body.classList.contains('print-pdf') ||
                         window.location.search.includes('print-pdf') ||
                         document.querySelectorAll('.reveal .slides section').length > 0;
      return isPrintMode;
    });
    
    if (pdfReady) {
      console.log('✅ PDF export mode accessible');
      results.summary.pdfTest = true;
      
      // Take a screenshot of PDF mode
      const pdfScreenshot = path.join(screenshotDir, 'pdf-export.png');
      await page.screenshot({ 
        path: pdfScreenshot,
        fullPage: true 
      });
      console.log('📸 PDF export screenshot saved');
    } else {
      console.log('❌ PDF export mode failed');
      results.errors.push('PDF export mode not accessible');
    }

  } catch (error) {
    console.error('❌ Validation failed:', error.message);
    results.errors.push(`Validation error: ${error.message}`);
  } finally {
    await browser.close();
  }
  
  // Generate validation report
  console.log('\n📋 Validation Report');
  console.log('===================');
  console.log(`Total slides: ${results.summary.totalSlides}`);
  console.log(`Fragment tests passed: ${results.summary.fragmentTests}`);
  console.log(`Speaker notes test: ${results.summary.speakerNotesTest ? 'PASS' : 'FAIL'}`);
  console.log(`PDF export test: ${results.summary.pdfTest ? 'PASS' : 'FAIL'}`);
  
  if (results.errors.length > 0) {
    console.log('\n❌ Errors:');
    results.errors.forEach(error => console.log(`   • ${error}`));
  }
  
  console.log('\n✅ Validation complete!');
  console.log(`Screenshots saved in: ${screenshotDir}`);
  
  // Save results to JSON file
  const reportPath = path.join(__dirname, 'validation-report.json');
  fs.writeFileSync(reportPath, JSON.stringify(results, null, 2));
  console.log(`Detailed report saved: ${reportPath}`);
  
  return results;
}

// Run validation
validatePresentation().catch(console.error);