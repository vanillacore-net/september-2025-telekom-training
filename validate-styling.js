const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function validateStyling() {
  console.log('🚀 Starting title slide styling validation...\n');
  
  // Ensure screenshots directory exists
  const screenshotDir = './screenshots/hedgedoc-validation';
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
      await page.waitForTimeout(2000); // Give time for styling to apply
      
      // Take screenshot
      const screenshotPath = path.join(screenshotDir, `${presentation.name}-validation-fixed.png`);
      await page.screenshot({ 
        path: screenshotPath,
        fullPage: false 
      });
      console.log(`   📸 Screenshot: ${screenshotPath}`);
      
      // Validate styling requirements
      const validationResults = await page.evaluate(() => {
        const results = {
          darkBackgroundRemoved: true,
          textColorCorrect: true,
          logoNoBorder: true,
          logoCentered: true
        };
        
        // Check workshop header background
        const workshopHeader = document.querySelector('.workshop-header');
        if (workshopHeader) {
          const styles = window.getComputedStyle(workshopHeader);
          const bgColor = styles.backgroundColor;
          // Check if background is white/light (not dark gradient)
          results.darkBackgroundRemoved = bgColor === 'rgb(255, 255, 255)' || bgColor === 'rgba(0, 0, 0, 0)';
          
          // Check text color
          const color = styles.color;
          results.textColorCorrect = color === 'rgb(51, 51, 51)'; // #333
        }
        
        // Check logo styling
        const logo = document.querySelector('.vanilla-logo img');
        if (logo) {
          const logoStyles = window.getComputedStyle(logo);
          results.logoNoBorder = logoStyles.border === 'none' || logoStyles.border === '0px none rgb(51, 51, 51)';
        }
        
        // Check logo centering
        const logoContainer = document.querySelector('.title-slide .vanilla-logo');
        if (logoContainer) {
          const logoStyles = window.getComputedStyle(logoContainer);
          results.logoCentered = logoStyles.textAlign === 'center' && logoStyles.position === 'static';
        }
        
        return results;
      });
      
      // Report validation results
      console.log(`   ✅ Dark background removed: ${validationResults.darkBackgroundRemoved ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Text color (#333): ${validationResults.textColorCorrect ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Logo no border: ${validationResults.logoNoBorder ? 'PASS' : 'FAIL'}`);
      console.log(`   ✅ Logo centered: ${validationResults.logoCentered ? 'PASS' : 'FAIL'}`);
      
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
    console.log('\n🎉 All styling requirements successfully implemented!');
    console.log('   • Dark backgrounds removed');
    console.log('   • Text color changed to #333');
    console.log('   • Logo borders removed');
    console.log('   • Logos centered above titles');
  } else {
    console.log('\n⚠️  Some validation checks failed. Please review the changes.');
  }
  
  return allPassed;
}

// Run validation
validateStyling()
  .then((success) => {
    process.exit(success ? 0 : 1);
  })
  .catch((error) => {
    console.error('Validation failed:', error);
    process.exit(1);
  });