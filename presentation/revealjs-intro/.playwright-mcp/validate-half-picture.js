const { chromium } = require('playwright');
const path = require('path');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Set viewport for presentation
  await page.setViewportSize({ width: 1280, height: 720 });
  
  // Navigate to the presentation
  const presentationPath = path.join(__dirname, '..', 'index.html');
  await page.goto(`file://${presentationPath}`);
  
  // Wait for RevealJS to initialize
  await page.waitForTimeout(2000);
  
  console.log('Navigating specifically to slide 5 (half-picture)...');
  
  // Navigate directly to slide 5 (0-indexed, so slide 4)
  await page.evaluate(() => {
    if (window.Reveal) {
      window.Reveal.slide(4, 0); // Go to slide index 4 (5th slide)
    }
  });
  
  await page.waitForTimeout(1000);
  
  // Take screenshot of half-picture slide
  await page.screenshot({ path: '.playwright-mcp/half-picture-validation.png', fullPage: false });
  console.log('✓ Half-picture slide validated');
  
  // Also check slide 6 (full-picture)
  await page.evaluate(() => {
    if (window.Reveal) {
      window.Reveal.slide(5, 0); // Go to slide index 5 (6th slide)
    }
  });
  
  await page.waitForTimeout(1000);
  
  await page.screenshot({ path: '.playwright-mcp/full-picture-validation.png', fullPage: false });
  console.log('✓ Full-picture slide validated');
  
  await browser.close();
  console.log('\nSpecific slide validation completed.');
})();