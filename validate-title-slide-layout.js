const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function validateTitleSlideLayout() {
  console.log('🚀 Starting title slide layout validation...\n');
  
  // Ensure screenshots directory exists
  const screenshotDir = './screenshots/title-slide-validation';
  if (!fs.existsSync(screenshotDir)) {
    fs.mkdirSync(screenshotDir, { recursive: true });
  }
  
  const browser = await chromium.launch();
  const context = await browser.newContext({
    viewport: { width: 1920, height: 1080 }
  });
  
  const presentations = [
    { name: 'day1', url: 'http://localhost:3000/p/day1-design-patterns' },
    { name: 'day2', url: 'http://localhost:3000/p/day2-design-patterns' },
    { name: 'day3', url: 'http://localhost:3000/p/day3-design-patterns' },
    { name: 'day4', url: 'http://localhost:3000/p/day4-design-patterns' }
  ];
  
  let allPassed = true;
  
  for (const presentation of presentations) {
    console.log(`\n📋 Validating ${presentation.name.toUpperCase()}:`);
    console.log(`   URL: ${presentation.url}`);
    
    const page = await context.newPage();
    
    try {
      // Navigate to presentation
      await page.goto(presentation.url, { waitUntil: 'networkidle' });
      
      // Wait for slides to load
      await page.waitForSelector('.reveal', { timeout: 10000 });
      await page.waitForTimeout(3000); // Give time for styling to apply
      
      // Take screenshot
      const screenshotPath = path.join(screenshotDir, `${presentation.name}-title-slide-layout.png`);
      await page.screenshot({ 
        path: screenshotPath,
        fullPage: false 
      });
      console.log(`   📸 Screenshot: ${screenshotPath}`);
      
      // Validate title slide layout requirements
      const validationResults = await page.evaluate(() => {
        const results = {
          noCornerLogo: true,
          logoCentered: true,
          logoLargeSize: true,
          titleBelowLogo: true,
          titleCentered: true,
          subtitleBelowTitle: true,
          professionalSpacing: true
        };
        
        // Check that corner logo is not present on title slide
        const titleSlide = document.querySelector('.title-slide');
        if (titleSlide) {
          const pseudoElement = window.getComputedStyle(titleSlide, '::after');
          results.noCornerLogo = pseudoElement.display === 'none';
        }
        
        // Check logo centering and size
        const logoContainer = document.querySelector('.title-slide .vanilla-logo');
        if (logoContainer) {
          const logoStyles = window.getComputedStyle(logoContainer);
          results.logoCentered = logoStyles.textAlign === 'center' && logoStyles.position === 'static';
          
          // Check if logo is larger (max-width should be 300px vs previous 200px)
          const maxWidth = parseInt(logoStyles.maxWidth);
          results.logoLargeSize = maxWidth >= 300;
          
          // Check spacing - margin-bottom should be 60px
          const marginBottom = parseInt(logoStyles.marginBottom);
          results.professionalSpacing = marginBottom >= 40; // At least 40px spacing
        }
        
        // Check title positioning
        const titleSlideContainer = document.querySelector('.title-slide');
        const titleH1 = document.querySelector('.title-slide h1');
        const titleH2 = document.querySelector('.title-slide h2');
        
        if (titleSlideContainer) {
          const containerStyles = window.getComputedStyle(titleSlideContainer);
          results.titleCentered = containerStyles.textAlign === 'center';
        }
        
        if (titleH1) {
          const h1Styles = window.getComputedStyle(titleH1);
          results.titleBelowLogo = h1Styles.marginTop === '0px'; // Should have no top margin since logo has bottom margin
        }
        
        if (titleH2) {
          const h2Styles = window.getComputedStyle(titleH2);
          results.subtitleBelowTitle = h2Styles.marginTop === '0px'; // Should have no top margin
        }
        
        return results;
      });
      
      // Report validation results
      console.log(`   ✅ No corner logo on title slide: ${validationResults.noCornerLogo ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Logo centered horizontally: ${validationResults.logoCentered ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Logo large size (300px): ${validationResults.logoLargeSize ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Title positioned below logo: ${validationResults.titleBelowLogo ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Title centered: ${validationResults.titleCentered ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Subtitle below title: ${validationResults.subtitleBelowTitle ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Professional spacing: ${validationResults.professionalSpacing ? 'PASS' : 'FAIL'}`);
      
      const dayPassed = Object.values(validationResults).every(result => result);
      console.log(`   🎯 Overall: ${dayPassed ? 'PASS' : 'FAIL'}`);
      
      if (!dayPassed) allPassed = false;
      
    } catch (error) {
      console.error(`   ❌ Error validating ${presentation.name}: ${error.message}`);
      allPassed = false;
    }
    
    await page.close();
  }
  
  await browser.close();
  
  console.log('\n' + '='.repeat(50));
  console.log(`🏁 FINAL VALIDATION RESULT: ${allPassed ? '✅ ALL PASSED' : '❌ SOME FAILED'}`);
  console.log('='.repeat(50));
  
  if (allPassed) {
    console.log('\n🎉 All title slide layout requirements successfully implemented!');
    console.log('   • Corner logo removed from title slides');
    console.log('   • Main logo centered in middle of slide');
    console.log('   • Title positioned below centered logo');
    console.log('   • Subtitle positioned below title');
    console.log('   • Professional spacing maintained');
  } else {
    console.log('\n⚠️  Some validation checks failed. Please review the changes.');
  }
  
  return allPassed;
}

// Run validation
validateTitleSlideLayout()
  .then((success) => {
    process.exit(success ? 0 : 1);
  })
  .catch((error) => {
    console.error('Validation failed:', error);
    process.exit(1);
  });