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
  
  console.log('Taking BEFORE screenshots...');
  
  // Slide 1: Title slide (main issue - pushed to bottom)
  await page.screenshot({ path: '.playwright-mcp/before-slide-1-title.png', fullPage: false });
  console.log('✓ Slide 1 (Title) - should show vertical centering issue');
  
  // Navigate to next slides and screenshot each
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/before-slide-2-section.png', fullPage: false });
  console.log('✓ Slide 2 (Section)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/before-slide-3-single-column.png', fullPage: false });
  console.log('✓ Slide 3 (Single Column)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/before-slide-4-two-columns.png', fullPage: false });
  console.log('✓ Slide 4 (Two Columns)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/before-slide-5-half-picture.png', fullPage: false });
  console.log('✓ Slide 5 (Half Picture)');
  
  await page.keyboard.press('ArrowRight');
  await page.waitForTimeout(500);
  await page.screenshot({ path: '.playwright-mcp/before-slide-6-full-picture.png', fullPage: false });
  console.log('✓ Slide 6 (Full Picture)');
  
  await browser.close();
  console.log('\nBEFORE screenshots completed. Now analyze the layout issues...');
})();