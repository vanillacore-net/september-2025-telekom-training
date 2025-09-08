const { chromium } = require('playwright');

async function validatePreciseSlides() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  // Set viewport for presentation mode (1920x1080)
  await page.setViewportSize({ width: 1920, height: 1080 });
  
  // Load the presentation
  await page.goto('file://' + __dirname + '/index.html');
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(1000);
  
  // List all slide titles for debugging
  const slideTitles = await page.evaluate(() => {
    const slides = document.querySelectorAll('section');
    return Array.from(slides).map((slide, index) => {
      const title = slide.querySelector('h1, h2');
      return `Slide ${index}: ${title ? title.textContent.trim() : 'No title'}`;
    });
  });
  
  console.log('All slides:');
  slideTitles.forEach(title => console.log(title));
  
  // Find Factory Method slide by searching for the exact title
  const factoryMethodIndex = await page.evaluate(() => {
    const slides = document.querySelectorAll('section');
    for (let i = 0; i < slides.length; i++) {
      const title = slides[i].querySelector('h2');
      if (title && title.textContent.includes('Factory Method Pattern')) {
        return i;
      }
    }
    return -1;
  });
  
  console.log(`Factory Method slide found at index: ${factoryMethodIndex}`);
  
  if (factoryMethodIndex >= 0) {
    await page.evaluate((index) => {
      Reveal.slide(index, 0, 0);
    }, factoryMethodIndex);
    await page.waitForTimeout(500);
    
    await page.screenshot({ 
      path: 'factory-method-fixed.png',
      fullPage: false
    });
    console.log('Factory Method screenshot saved');
    
    // Check if code is visible
    const codeVisible = await page.locator('pre code').isVisible();
    console.log(`Factory Method code visible: ${codeVisible}`);
  }
  
  // Find Observer Pattern slide
  const observerIndex = await page.evaluate(() => {
    const slides = document.querySelectorAll('section');
    for (let i = 0; i < slides.length; i++) {
      const title = slides[i].querySelector('h2');
      if (title && title.textContent.includes('Observer Pattern')) {
        return i;
      }
    }
    return -1;
  });
  
  console.log(`Observer Pattern slide found at index: ${observerIndex}`);
  
  if (observerIndex >= 0) {
    await page.evaluate((index) => {
      Reveal.slide(index, 0, 0);
    }, observerIndex);
    await page.waitForTimeout(500);
    
    await page.screenshot({ 
      path: 'observer-pattern-fixed.png',
      fullPage: false
    });
    console.log('Observer Pattern screenshot saved');
    
    // Check fragment visibility
    const allFragments = await page.locator('pre.fragment').count();
    const visibleFragments = await page.locator('pre.fragment:visible').count();
    const firstFragmentVisible = await page.locator('pre.fragment[data-fragment-index="0"]:visible').count();
    
    console.log(`Observer Pattern fragments: ${allFragments} total, ${visibleFragments} visible, first fragment visible: ${firstFragmentVisible}`);
  }
  
  await browser.close();
}

validatePreciseSlides().catch(console.error);