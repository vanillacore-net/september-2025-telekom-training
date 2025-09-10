const { chromium } = require('playwright');

async function takeScreenshotOption2() {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Set viewport to match presentation resolution
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  try {
    console.log('📸 Taking screenshot of Option 2: No logo on content slides');
    
    // Navigate to day 1 presentation content slide
    await page.goto('http://localhost:3000/p/day1-design-patterns#/3', { 
      waitUntil: 'networkidle', 
      timeout: 10000 
    });
    
    // Wait for reveal.js to fully render
    await page.waitForTimeout(3000);
    
    // Hide any UI controls for clean screenshot
    await page.addStyleTag({ content: `
      .reveal .controls,
      .reveal .progress,
      .reveal .slide-number {
        display: none !important;
      }
    ` });
    
    // Take screenshot
    await page.screenshot({
      path: '.playwright-mcp/option2-no-logo.png',
      fullPage: false,
      clip: { x: 0, y: 0, width: 1920, height: 1080 }
    });
    
    console.log('✅ Option 2 screenshot saved: .playwright-mcp/option2-no-logo.png');
    
  } catch (error) {
    console.error('❌ Error taking screenshot:', error.message);
  }
  
  await browser.close();
}

takeScreenshotOption2();