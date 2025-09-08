const { chromium } = require('playwright');

(async () => {
  console.log('🔍 COMPREHENSIVE FINAL VALIDATION - RevealJS Presentation');
  console.log('Resolution: 1920x1080 (FHD)');
  
  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });
  await page.goto(`file://${process.cwd()}/index.html`);
  
  try {
    await page.waitForSelector('.reveal .slides section', { timeout: 5000 });
    console.log('✅ Presentation loaded successfully');
    
    const slideCount = await page.evaluate(() => {
      return document.querySelectorAll('.reveal .slides section').length;
    });
    
    console.log(`\n📊 Found ${slideCount} slides to validate`);
    
    const validationResults = [];
    
    // Test each slide
    for (let i = 0; i < slideCount; i++) {
      await page.evaluate((slideIndex) => {
        window.Reveal.slide(slideIndex);
      }, i);
      await page.waitForTimeout(500);
      
      console.log(`\n🔍 Validating Slide ${i + 1}...`);
      
      const slideValidation = await page.evaluate(() => {
        const currentSlide = document.querySelector('.present');
        if (!currentSlide) return { valid: false, error: 'No current slide' };
        
        const result = { 
          valid: true, 
          slideType: 'unknown',
          issues: [],
          textAlignment: 'unknown'
        };
        
        // Identify slide type
        if (currentSlide.querySelector('.title-slide')) {
          result.slideType = 'title';
        } else if (currentSlide.querySelector('.section-slide')) {
          result.slideType = 'section';
        } else if (currentSlide.classList.contains('code-slide')) {
          result.slideType = 'code';
        } else if (currentSlide.classList.contains('half-picture')) {
          result.slideType = 'half-picture';
        } else if (currentSlide.querySelector('.full-picture')) {
          result.slideType = 'full-picture';
        } else if (currentSlide.classList.contains('single-column')) {
          result.slideType = 'single-column';
        } else if (currentSlide.classList.contains('two-columns')) {
          result.slideType = 'two-columns';
        }
        
        // Check text overflow
        const textElements = currentSlide.querySelectorAll('h1, h2, h3, p, li');
        textElements.forEach(el => {
          const rect = el.getBoundingClientRect();
          if (rect.right > window.innerWidth || rect.bottom > window.innerHeight) {
            result.issues.push(`Text overflow: ${el.tagName}`);
            result.valid = false;
          }
        });
        
        // Check text alignment for code slides
        if (result.slideType === 'code') {
          const textSection = currentSlide.querySelector('.text-section');
          const introText = textSection ? textSection.querySelector('p') : null;
          if (introText) {
            const style = window.getComputedStyle(introText);
            result.textAlignment = style.textAlign;
            if (style.textAlign !== 'left') {
              result.issues.push(`Code slide intro text not left-aligned: ${style.textAlign}`);
              result.valid = false;
            }
          }
        }
        
        // Check for proper positioning
        const codeBlocks = currentSlide.querySelectorAll('pre');
        codeBlocks.forEach((pre, index) => {
          const rect = pre.getBoundingClientRect();
          const style = window.getComputedStyle(pre);
          
          if (rect.height > window.innerHeight * 0.9) {
            result.issues.push(`Code block ${index + 1} too large for viewport`);
          }
          
          if (style.overflow !== 'auto') {
            result.issues.push(`Code block ${index + 1} missing auto overflow`);
          }
        });
        
        return result;
      });
      
      validationResults.push({
        slideNumber: i + 1,
        ...slideValidation
      });
      
      if (slideValidation.valid) {
        console.log(`   ✅ ${slideValidation.slideType} slide - Valid`);
        if (slideValidation.textAlignment !== 'unknown') {
          console.log(`      Text alignment: ${slideValidation.textAlignment}`);
        }
      } else {
        console.log(`   ❌ ${slideValidation.slideType} slide - Issues found:`);
        slideValidation.issues.forEach(issue => console.log(`      - ${issue}`));
      }
      
      // Take screenshot
      await page.screenshot({ 
        path: `.playwright-mcp/final-validation-slide-${i + 1}.png`,
        fullPage: false
      });
    }
    
    console.log('\n' + '='.repeat(70));
    console.log('COMPREHENSIVE VALIDATION SUMMARY');
    console.log('='.repeat(70));
    
    const validSlides = validationResults.filter(r => r.valid);
    const invalidSlides = validationResults.filter(r => !r.valid);
    
    console.log(`✅ Valid slides: ${validSlides.length}/${slideCount}`);
    console.log(`❌ Issues found in: ${invalidSlides.length}/${slideCount} slides`);
    
    if (invalidSlides.length === 0) {
      console.log('\n🎉 ALL SLIDES PASS VALIDATION!');
      console.log('The presentation is ready for professional use.');
    } else {
      console.log('\n⚠️  Issues that need attention:');
      invalidSlides.forEach(slide => {
        console.log(`\nSlide ${slide.slideNumber} (${slide.slideType}):`);
        slide.issues.forEach(issue => console.log(`  - ${issue}`));
      });
    }
    
    // Summary by slide type
    console.log('\n📊 SLIDE TYPE SUMMARY:');
    const slideTypes = {};
    validationResults.forEach(result => {
      if (!slideTypes[result.slideType]) {
        slideTypes[result.slideType] = { total: 0, valid: 0 };
      }
      slideTypes[result.slideType].total++;
      if (result.valid) slideTypes[result.slideType].valid++;
    });
    
    Object.keys(slideTypes).forEach(type => {
      const { total, valid } = slideTypes[type];
      const status = valid === total ? '✅' : '❌';
      console.log(`${status} ${type}: ${valid}/${total} valid`);
    });
    
    console.log(`\n📸 Screenshots saved: .playwright-mcp/final-validation-slide-[1-${slideCount}].png`);
    
  } catch (error) {
    console.error('❌ VALIDATION ERROR:', error.message);
  }
  
  await browser.close();
  
})().catch(console.error);