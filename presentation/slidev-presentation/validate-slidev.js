import { chromium } from 'playwright';
import fs from 'fs';
import path from 'path';

(async () => {
  console.log('Starting Slidev validation...');
  
  const browser = await chromium.launch({ headless: false });
  const context = await browser.newContext({
    viewport: { width: 1920, height: 1080 }
  });
  const page = await context.newPage();
  
  // Create validation directory
  const validationDir = '.playwright-mcp';
  if (!fs.existsSync(validationDir)) {
    fs.mkdirSync(validationDir, { recursive: true });
  }
  
  try {
    // Navigate to Slidev presentation
    console.log('Navigating to http://localhost:3010...');
    await page.goto('http://localhost:3010', { waitUntil: 'networkidle' });
    
    // Wait for content to load
    await page.waitForTimeout(2000);
    
    // Take screenshot of title slide
    console.log('Taking screenshot of title slide...');
    await page.screenshot({ 
      path: path.join(validationDir, 'current-slide-1-title.png'),
      fullPage: false 
    });
    
    // Navigate through slides and take screenshots
    const slides = [
      { num: 2, name: 'section', desc: 'Agenda slide' },
      { num: 3, name: 'single-column', desc: 'Single column content' },
      { num: 4, name: 'two-columns', desc: 'Two column layout' },
      { num: 5, name: 'half-picture', desc: 'Half picture layout' },
      { num: 6, name: 'code-embedded', desc: 'Code embedded layout' }
    ];
    
    for (const slide of slides) {
      // Navigate to specific slide
      await page.goto(`http://localhost:3010/${slide.num}`, { waitUntil: 'networkidle' });
      await page.waitForTimeout(1000);
      
      console.log(`Taking screenshot of slide ${slide.num}: ${slide.desc}...`);
      await page.screenshot({ 
        path: path.join(validationDir, `current-slide-${slide.num}-${slide.name}.png`),
        fullPage: false 
      });
    }
    
    console.log('Validation complete! Screenshots saved to .playwright-mcp/');
    
  } catch (error) {
    console.error('Validation failed:', error);
  } finally {
    await browser.close();
  }
})();