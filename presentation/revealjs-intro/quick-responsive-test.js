const { chromium } = require('playwright');
const path = require('path');

async function quickResponsiveTest() {
  console.log('🚀 Quick Responsive Design Test');
  
  const browser = await chromium.launch({ headless: true });
  
  const resolutions = [
    { name: 'HD', width: 1280, height: 720 },
    { name: 'FHD', width: 1920, height: 1080 },
    { name: '4K', width: 3840, height: 2160 }
  ];
  
  for (const res of resolutions) {
    console.log(`\n📐 Testing ${res.name} (${res.width}x${res.height})`);
    
    const page = await browser.newPage();
    await page.setViewportSize({ width: res.width, height: res.height });
    
    try {
      await page.goto('file://' + path.resolve(__dirname, 'index.html'), {
        waitUntil: 'networkidle',
        timeout: 10000
      });
      
      // Wait for Reveal.js
      await page.waitForTimeout(1500);
      
      // Test key slides
      const testSlides = [0, 2, 4, 6]; // Title, Single Column, Two Column, Code
      
      for (const slideNum of testSlides) {
        await page.evaluate((slide) => {
          if (window.Reveal) {
            Reveal.slide(slide, 0);
          }
        }, slideNum);
        
        await page.waitForTimeout(300);
        
        // Take screenshot
        const screenshotPath = `./screenshots/quick-${res.name.toLowerCase()}-slide-${slideNum}.png`;
        await page.screenshot({ 
          path: screenshotPath,
          fullPage: false
        });
        
        console.log(`  ✓ Slide ${slideNum} → ${screenshotPath}`);
      }
      
      // Test font sizes
      const fontSizes = await page.evaluate(() => {
        const titleH1 = document.querySelector('.title-slide h1');
        const contentH2 = document.querySelector('.single-column h2');
        const bodyText = document.querySelector('.single-column p');
        
        return {
          titleH1: titleH1 ? window.getComputedStyle(titleH1).fontSize : null,
          contentH2: contentH2 ? window.getComputedStyle(contentH2).fontSize : null,
          bodyText: bodyText ? window.getComputedStyle(bodyText).fontSize : null
        };
      });
      
      console.log('  📏 Font sizes:');
      console.log(`    Title H1: ${fontSizes.titleH1}`);
      console.log(`    Content H2: ${fontSizes.contentH2}`);
      console.log(`    Body Text: ${fontSizes.bodyText}`);
      
    } catch (error) {
      console.log(`  ❌ Error testing ${res.name}: ${error.message}`);
    }
    
    await page.close();
  }
  
  await browser.close();
  
  console.log('\n✅ Quick test complete! Check ./screenshots/ directory');
  console.log('🎯 Manual verification required:');
  console.log('   → Text should scale proportionally across resolutions');
  console.log('   → Logos should maintain aspect ratio');
  console.log('   → No text should overflow containers');
  console.log('   → Code blocks should remain readable at all sizes');
}

// Ensure screenshots directory exists
const fs = require('fs').promises;
async function main() {
  try {
    await fs.mkdir('./screenshots', { recursive: true });
    await quickResponsiveTest();
  } catch (error) {
    console.error('Test failed:', error);
  }
}

main();