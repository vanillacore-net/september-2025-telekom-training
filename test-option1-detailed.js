const { chromium } = require('playwright');

async function takeDetailedScreenshots() {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Set viewport to match presentation resolution
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  try {
    console.log('📸 Taking detailed screenshots of Option 1');
    
    // Navigate to day 1 presentation - slide with more content
    await page.goto('http://localhost:3000/p/day1-design-patterns#/3', { 
      waitUntil: 'networkidle', 
      timeout: 10000 
    });
    
    // Wait a moment for reveal.js to fully render
    await page.waitForTimeout(3000);
    
    // Hide any UI controls for clean screenshot
    await page.addStyleTag({ content: `
      .reveal .controls,
      .reveal .progress,
      .reveal .slide-number {
        display: none !important;
      }
    ` });
    
    // Take screenshot of content-heavy slide
    await page.screenshot({
      path: '.playwright-mcp/option1-content-slide.png',
      fullPage: false,
      clip: { x: 0, y: 0, width: 1920, height: 1080 }
    });
    
    console.log('✅ Content slide screenshot saved: .playwright-mcp/option1-content-slide.png');
    
    // Check if logo element exists and is visible
    const logoExists = await page.locator('.vanilla-logo').count() > 0;
    console.log('🔍 Logo element exists:', logoExists);
    
    if (logoExists) {
      const logoVisible = await page.locator('.vanilla-logo').isVisible();
      console.log('👁️ Logo is visible:', logoVisible);
      
      const logoPosition = await page.locator('.vanilla-logo').boundingBox();
      console.log('📍 Logo position:', logoPosition);
    }
    
  } catch (error) {
    console.error('❌ Error taking screenshots:', error.message);
  }
  
  await browser.close();
}

takeDetailedScreenshots();