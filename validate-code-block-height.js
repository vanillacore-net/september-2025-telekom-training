const { chromium } = require('playwright');

async function validateCodeBlockHeight() {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Set viewport to match presentation resolution
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  console.log('🔍 Validating code block height improvements...');
  
  // Test Day 1 - has long code blocks
  console.log('📸 Capturing Day 1 code block heights...');
  await page.goto('http://localhost:3000/p/day1-design-patterns');
  
  // Wait for presentation to load
  await page.waitForTimeout(3000);
  
  // Navigate to a slide with code blocks (slide 7 has Factory Method implementation)
  for (let i = 0; i < 6; i++) {
    await page.keyboard.press('ArrowRight');
    await page.waitForTimeout(500);
  }
  
  // Take screenshot of code block
  await page.screenshot({ 
    path: '.playwright-mcp/day1-code-height-validation.png',
    fullPage: false
  });
  console.log('✅ Day 1 code block height captured');
  
  // Test Day 2 - has Observer pattern code
  console.log('📸 Capturing Day 2 code block heights...');
  await page.goto('http://localhost:3000/p/day2-design-patterns');
  await page.waitForTimeout(3000);
  
  // Navigate to Observer pattern slide with code
  for (let i = 0; i < 8; i++) {
    await page.keyboard.press('ArrowRight');
    await page.waitForTimeout(500);
  }
  
  await page.screenshot({ 
    path: '.playwright-mcp/day2-code-height-validation.png',
    fullPage: false
  });
  console.log('✅ Day 2 code block height captured');
  
  // Test Day 3 - has Facade pattern code
  console.log('📸 Capturing Day 3 code block heights...');
  await page.goto('http://localhost:3000/p/day3-design-patterns');
  await page.waitForTimeout(3000);
  
  // Navigate to a slide with code blocks
  for (let i = 0; i < 5; i++) {
    await page.keyboard.press('ArrowRight');
    await page.waitForTimeout(500);
  }
  
  await page.screenshot({ 
    path: '.playwright-mcp/day3-code-height-validation.png',
    fullPage: false
  });
  console.log('✅ Day 3 code block height captured');
  
  // Test Day 4 - has Command pattern code
  console.log('📸 Capturing Day 4 code block heights...');
  await page.goto('http://localhost:3000/p/day4-design-patterns');
  await page.waitForTimeout(3000);
  
  // Navigate to Command pattern implementation
  for (let i = 0; i < 7; i++) {
    await page.keyboard.press('ArrowRight');
    await page.waitForTimeout(500);
  }
  
  await page.screenshot({ 
    path: '.playwright-mcp/day4-code-height-validation.png',
    fullPage: false
  });
  console.log('✅ Day 4 code block height captured');
  
  await browser.close();
  console.log('🎯 Code block height validation complete!');
}

validateCodeBlockHeight().catch(console.error);