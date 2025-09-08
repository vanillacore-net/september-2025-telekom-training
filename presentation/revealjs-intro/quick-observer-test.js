const { chromium } = require('playwright');

async function quickObserverTest() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  await page.setViewportSize({ width: 1920, height: 1080 });
  await page.goto('file://' + __dirname + '/index.html');
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(1000);
  
  // Go directly to Observer Pattern slide (index 8)
  await page.evaluate(() => {
    Reveal.slide(8, 0, 0);
  });
  await page.waitForTimeout(500);
  
  // Take screenshot
  await page.screenshot({ path: 'observer-test.png', fullPage: false });
  
  // Check specific fragment visibility
  const firstFragment = await page.locator('pre.fragment[data-fragment-index="0"]');
  const isVisible = await firstFragment.isVisible();
  
  console.log(`First Observer Pattern fragment visible: ${isVisible}`);
  
  await browser.close();
}

quickObserverTest().catch(console.error);