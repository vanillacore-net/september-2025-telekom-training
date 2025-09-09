const { chromium } = require('playwright');

async function validateCodeBlockImprovements() {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Set viewport to match presentation resolution
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  console.log('🔍 Validating code block height improvements...');
  
  // Test Day 1 - Factory Method implementation slide (has long code)
  console.log('📸 Capturing Day 1 Factory Method code...');
  await page.goto('http://localhost:3000/p/day1-design-patterns#/7', { 
    waitUntil: 'networkidle' 
  });
  await page.waitForTimeout(2000);
  
  await page.screenshot({ 
    path: '.playwright-mcp/day1-factory-code-validation.png',
    fullPage: false
  });
  console.log('✅ Day 1 Factory Method code captured');
  
  // Test Day 1 - Builder Pattern implementation (slide 21)
  console.log('📸 Capturing Day 1 Builder Pattern code...');
  await page.goto('http://localhost:3000/p/day1-design-patterns#/21', { 
    waitUntil: 'networkidle' 
  });
  await page.waitForTimeout(2000);
  
  await page.screenshot({ 
    path: '.playwright-mcp/day1-builder-code-validation.png',
    fullPage: false
  });
  console.log('✅ Day 1 Builder Pattern code captured');
  
  // Test Day 2 - Observer Pattern implementation 
  console.log('📸 Capturing Day 2 Observer Pattern code...');
  await page.goto('http://localhost:3000/p/day2-design-patterns#/9', { 
    waitUntil: 'networkidle' 
  });
  await page.waitForTimeout(2000);
  
  await page.screenshot({ 
    path: '.playwright-mcp/day2-observer-code-validation.png',
    fullPage: false
  });
  console.log('✅ Day 2 Observer Pattern code captured');
  
  // Test Day 3 - Facade Pattern implementation
  console.log('📸 Capturing Day 3 Facade Pattern code...');
  await page.goto('http://localhost:3000/p/day3-design-patterns#/6', { 
    waitUntil: 'networkidle' 
  });
  await page.waitForTimeout(2000);
  
  await page.screenshot({ 
    path: '.playwright-mcp/day3-facade-code-validation.png',
    fullPage: false
  });
  console.log('✅ Day 3 Facade Pattern code captured');
  
  // Test Day 4 - Command Pattern implementation
  console.log('📸 Capturing Day 4 Command Pattern code...');
  await page.goto('http://localhost:3000/p/day4-design-patterns#/8', { 
    waitUntil: 'networkidle' 
  });
  await page.waitForTimeout(2000);
  
  await page.screenshot({ 
    path: '.playwright-mcp/day4-command-code-validation.png',
    fullPage: false
  });
  console.log('✅ Day 4 Command Pattern code captured');
  
  await browser.close();
  console.log('🎯 Code block validation complete - improved heights should be visible!');
}

validateCodeBlockImprovements().catch(console.error);