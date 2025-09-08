const { chromium } = require('playwright');

async function validateObserverPattern() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  // Set viewport for presentation mode (1920x1080)
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  // Load the presentation
  await page.goto('file://' + __dirname + '/index.html');
  
  // Navigate to slide 8 (Observer Pattern - 0-indexed, so slide 7)
  await page.locator('body').press('End'); // Go to last slide
  await page.locator('body').press('Home'); // Go to first slide
  
  // Navigate to Observer Pattern slide (slide 8 = index 7)
  for (let i = 0; i < 7; i++) {
    await page.locator('body').press('ArrowRight');
    await page.waitForTimeout(300);
  }
  
  // Take screenshot of Observer Pattern slide
  await page.screenshot({ 
    path: 'observer-pattern-validation.png',
    fullPage: false
  });
  
  console.log('Observer Pattern screenshot saved as observer-pattern-validation.png');
  
  // Check if any code is visible (first fragment should be visible by default)
  const codeElements = await page.locator('pre code').count();
  const visibleCodeElements = await page.locator('pre.fragment.visible, pre.fragment.current-fragment, pre:not(.fragment)').count();
  
  console.log(`Total code blocks: ${codeElements}`);
  console.log(`Visible code blocks: ${visibleCodeElements}`);
  
  // Navigate to Factory Method slide (slide 7 = index 6)
  await page.locator('body').press('ArrowLeft'); 
  await page.waitForTimeout(300);
  
  // Take screenshot of Factory Method slide
  await page.screenshot({ 
    path: 'factory-method-validation.png',
    fullPage: false
  });
  
  console.log('Factory Method screenshot saved as factory-method-validation.png');
  
  await browser.close();
}

validateObserverPattern().catch(console.error);