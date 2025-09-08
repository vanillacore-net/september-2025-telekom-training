const { chromium } = require('playwright');

async function quickFactoryTest() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  await page.setViewportSize({ width: 1920, height: 1080 });
  await page.goto('file://' + __dirname + '/index.html');
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(1000);
  
  // Go directly to Factory Method slide (index 7)
  await page.evaluate(() => {
    Reveal.slide(7, 0, 0);
  });
  await page.waitForTimeout(500);
  
  await page.screenshot({ path: 'factory-final-test.png', fullPage: false });
  
  console.log('Factory Method final test screenshot saved');
  
  await browser.close();
}

quickFactoryTest().catch(console.error);