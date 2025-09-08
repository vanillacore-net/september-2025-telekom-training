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
  
  console.log('Taking AFTER screenshots...');
  
  // Slide 1: Title slide (should now be centered)
  await page.screenshot({ path: '.playwright-mcp/fixed-slide-1-title.png', fullPage: false });
  console.log('✓ Slide 1 (Title) - checking vertical centering fix');
  
  // Navigate to next slides and screenshot each
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/fixed-slide-2-section.png', fullPage: false });
  console.log('✓ Slide 2 (Section)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/fixed-slide-3-single-column.png', fullPage: false });
  console.log('✓ Slide 3 (Single Column)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/fixed-slide-4-two-columns.png', fullPage: false });
  console.log('✓ Slide 4 (Two Columns)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/fixed-slide-5-half-picture.png', fullPage: false });
  console.log('✓ Slide 5 (Half Picture)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/fixed-slide-6-full-picture.png', fullPage: false });
  console.log('✓ Slide 6 (Full Picture)');
  
  await browser.close();
  console.log('\nAFTER screenshots completed. Ready for validation...');
})();