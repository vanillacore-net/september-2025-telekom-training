const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({ headless: true });
  const page = await browser.newPage();
  await page.setViewport({ width: 1920, height: 1080 });

  // Title slide
  await page.goto('http://localhost:3010', { waitUntil: 'networkidle2' });
  await page.screenshot({ path: 'validation/1-title-slide.png' });

  // Section slide  
  await page.goto('http://localhost:3010/2', { waitUntil: 'networkidle2' });
  await page.screenshot({ path: 'validation/2-section-slide.png' });

  // Content slide
  await page.goto('http://localhost:3010/3', { waitUntil: 'networkidle2' });
  await page.screenshot({ path: 'validation/3-content-slide.png' });

  // Two-column slide
  await page.goto('http://localhost:3010/4', { waitUntil: 'networkidle2' });
  await page.screenshot({ path: 'validation/4-two-column-slide.png' });

  // Image-right slide
  await page.goto('http://localhost:3010/5', { waitUntil: 'networkidle2' });
  await page.screenshot({ path: 'validation/5-image-right-slide.png' });

  await browser.close();
  console.log('Screenshots saved to validation/ directory');
})();