const { chromium } = require('playwright');

async function takeScreenshot() {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Set viewport to match presentation resolution
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  try {
    console.log('📸 Taking screenshot of Option 1: Small logo in top-right corner');
    
    // Navigate to day 1 presentation content slide (skip title slide)
    await page.goto('http://localhost:3000/p/day1-design-patterns#/2', { 
      waitUntil: 'networkidle', 
      timeout: 10000 
    });
    
    // Wait a moment for reveal.js to fully render
    await page.waitForTimeout(2000);
    
    // Hide any UI controls for clean screenshot
    await page.addStyleTag({ content: `
      .reveal .controls,
      .reveal .progress,
      .reveal .slide-number {
        display: none !important;
      }
    ` });
    
    await page.screenshot({
      path: '.playwright-mcp/option1-top-right-logo.png',
      fullPage: false,
      clip: { x: 0, y: 0, width: 1920, height: 1080 }
    });
    
    console.log('✅ Screenshot saved: .playwright-mcp/option1-top-right-logo.png');
    
  } catch (error) {
    console.error('❌ Error taking screenshot:', error.message);
  }
  
  await browser.close();
}

takeScreenshot();