const puppeteer = require('puppeteer');
const path = require('path');

async function navigateDebug() {
    const browser = await puppeteer.launch({ headless: false });
    const page = await browser.newPage();
    await page.setViewport({ width: 1920, height: 1080 });
    
    const htmlPath = path.resolve(__dirname, 'index.html');
    await page.goto(`file://${htmlPath}`);
    
    // Check current slide info
    for (let i = 0; i < 6; i++) {
        const slideInfo = await page.evaluate(() => {
            const currentSlide = document.querySelector('.reveal .slides section.present');
            const h1 = currentSlide?.querySelector('h1');
            const h2 = currentSlide?.querySelector('h2');
            const classes = Array.from(currentSlide?.classList || []);
            
            return {
                slideNumber: Reveal.getIndices().h + 1,
                h1: h1?.textContent?.trim(),
                h2: h2?.textContent?.trim(),
                classes: classes
            };
        });
        
        console.log(`Slide ${i + 1}:`, JSON.stringify(slideInfo, null, 2));
        
        if (slideInfo.classes.includes('two-columns')) {
            console.log('Found two-columns slide!');
            await page.screenshot({ path: `slide-${i + 1}-two-columns.png` });
            break;
        }
        
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
    }
    
    await browser.close();
}

navigateDebug().catch(console.error);