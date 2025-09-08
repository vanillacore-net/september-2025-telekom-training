const { chromium } = require('playwright');

async function takeScreenshots(prefix) {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1280, height: 720 });
  
  const url = 'file:///Users/karsten/Nextcloud/Work/Training/2025-09_Telekom_Architecture/presentation/revealjs-intro/index.html';
  await page.goto(url);
  
  // Wait for page to load
  await page.waitForTimeout(2000);
  
  // Take screenshots of all 6 slides
  for (let i = 0; i < 6; i++) {
    const filename = `./.playwright-mcp/${prefix}-slide-${i + 1}${i === 0 ? '-title' : i === 1 ? '-section' : i === 2 ? '-single-column' : i === 3 ? '-two-columns' : i === 4 ? '-half-picture' : '-full-picture'}.png`;
    
    await page.screenshot({ 
      path: filename, 
      fullPage: false 
    });
    
    console.log(`Screenshot ${i + 1}/6 taken: ${filename}`);
    
    // Navigate to next slide (except for last one)
    if (i < 5) {
      await page.keyboard.press('ArrowRight');
      await page.waitForTimeout(500);
    }
  }
  
  await browser.close();
}

// Export for use
module.exports = { takeScreenshots };

// Run if called directly
if (require.main === module) {
  const prefix = process.argv[2] || 'before';
  takeScreenshots(prefix).catch(console.error);
}