const { chromium } = require('playwright');

async function validateCriticalSlides() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  // Set viewport for presentation mode (1920x1080)
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  // Load the presentation
  await page.goto('file://' + __dirname + '/index.html');
  await page.waitForLoadState('networkidle');
  
  // Wait for Reveal.js to initialize
  await page.waitForTimeout(1000);
  
  // Get total slides count
  const totalSlides = await page.evaluate(() => {
    return Reveal.getTotalSlides();
  });
  console.log(`Total slides: ${totalSlides}`);
  
  // Navigate to Factory Method slide (should be slide index 6 for slide 7)
  await page.evaluate(() => {
    Reveal.slide(6, 0, 0); // Go to slide 7 (Factory Method)
  });
  await page.waitForTimeout(500);
  
  // Take screenshot of Factory Method slide
  await page.screenshot({ 
    path: 'factory-method-slide-7.png',
    fullPage: false
  });
  
  console.log('Factory Method slide screenshot saved');
  
  // Check title positioning
  const factoryMethodTitle = await page.locator('h2').first();
  const titleBounds = await factoryMethodTitle.boundingBox();
  console.log(`Factory Method title position:`, titleBounds);
  
  // Navigate to Observer Pattern slide (should be slide index 7 for slide 8)
  await page.evaluate(() => {
    Reveal.slide(7, 0, 0); // Go to slide 8 (Observer Pattern)
  });
  await page.waitForTimeout(500);
  
  // Take screenshot of Observer Pattern slide (initial state)
  await page.screenshot({ 
    path: 'observer-pattern-slide-8-initial.png',
    fullPage: false
  });
  
  console.log('Observer Pattern slide screenshot saved');
  
  // Check if first code fragment is visible by default
  const allCodeBlocks = await page.locator('pre').count();
  const visibleCodeBlocks = await page.locator('pre:visible').count();
  const fragmentCodeBlocks = await page.locator('pre.fragment').count();
  const visibleFragments = await page.locator('pre.fragment.visible').count();
  
  console.log(`Observer Pattern analysis:`);
  console.log(`- Total code blocks: ${allCodeBlocks}`);
  console.log(`- Visible code blocks: ${visibleCodeBlocks}`);
  console.log(`- Fragment code blocks: ${fragmentCodeBlocks}`);
  console.log(`- Visible fragments: ${visibleFragments}`);
  
  // Try to trigger first fragment
  await page.keyboard.press('ArrowDown');
  await page.waitForTimeout(300);
  
  await page.screenshot({ 
    path: 'observer-pattern-slide-8-first-fragment.png',
    fullPage: false
  });
  
  console.log('Observer Pattern first fragment screenshot saved');
  
  await browser.close();
}

validateCriticalSlides().catch(console.error);