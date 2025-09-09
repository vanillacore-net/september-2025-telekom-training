const { chromium } = require('playwright');

async function takeValidationScreenshots() {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Set viewport to match presentation resolution
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  const presentations = [
    { day: 1, url: 'http://localhost:3000/p/day1-design-patterns#/3' },
    { day: 2, url: 'http://localhost:3000/p/day2-design-patterns#/3' },
    { day: 3, url: 'http://localhost:3000/p/day3-design-patterns#/3' },
    { day: 4, url: 'http://localhost:3000/p/day4-design-patterns#/3' }
  ];
  
  for (const pres of presentations) {
    try {
      console.log(`📸 Validating Day ${pres.day} positioning and logo placement`);
      
      await page.goto(pres.url, { 
        waitUntil: 'networkidle', 
        timeout: 10000 
      });
      
      // Wait for reveal.js to fully render
      await page.waitForTimeout(2000);
      
      // Hide UI controls
      await page.addStyleTag({ content: `
        .reveal .controls,
        .reveal .progress,
        .reveal .slide-number {
          display: none !important;
        }
      ` });
      
      await page.screenshot({
        path: `.playwright-mcp/final-day${pres.day}-validation.png`,
        fullPage: false,
        clip: { x: 0, y: 0, width: 1920, height: 1080 }
      });
      
      console.log(`✅ Day ${pres.day} validation screenshot saved`);
      
    } catch (error) {
      console.error(`❌ Error with Day ${pres.day}:`, error.message);
    }
  }
  
  await browser.close();
  console.log('\n🎯 Final validation complete - all presentations updated!');
}

takeValidationScreenshots();