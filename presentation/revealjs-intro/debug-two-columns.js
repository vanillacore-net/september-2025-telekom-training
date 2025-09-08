const puppeteer = require('puppeteer');
const path = require('path');

async function debugTwoColumns() {
    console.log('🔍 Debugging two-column layout...');
    
    const browser = await puppeteer.launch({
        headless: false,
        defaultViewport: { width: 1920, height: 1080 },
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    
    const page = await browser.newPage();
    await page.setViewport({ width: 1920, height: 1080 });
    
    const htmlPath = path.resolve(__dirname, 'index.html');
    await page.goto(`file://${htmlPath}`, { waitUntil: 'networkidle0' });
    
    // Navigate to two-column slide (slide 5 - "Patterns Übersicht")
    console.log('Navigating to two-column slide...');
    for (let i = 0; i < 4; i++) {
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 300));
    }
    
    // Take screenshot
    await page.screenshot({ path: 'debug-two-column-slide.png', fullPage: true });
    
    // Check the layout properties
    const layoutInfo = await page.evaluate(() => {
        const container = document.querySelector('.columns-container');
        const columns = document.querySelectorAll('.column');
        
        if (!container) return { error: 'No columns-container found' };
        
        const containerStyles = window.getComputedStyle(container);
        const containerRect = container.getBoundingClientRect();
        
        const columnInfo = Array.from(columns).map((col, index) => {
            const styles = window.getComputedStyle(col);
            const rect = col.getBoundingClientRect();
            return {
                index,
                width: styles.width,
                flex: styles.flex,
                display: styles.display,
                actualWidth: rect.width,
                actualHeight: rect.height,
                left: rect.left,
                top: rect.top
            };
        });
        
        return {
            container: {
                display: containerStyles.display,
                flexDirection: containerStyles.flexDirection,
                justifyContent: containerStyles.justifyContent,
                alignItems: containerStyles.alignItems,
                gap: containerStyles.gap,
                width: containerStyles.width,
                actualWidth: containerRect.width,
                actualHeight: containerRect.height
            },
            columns: columnInfo
        };
    });
    
    console.log('📊 Layout Analysis:');
    console.log('Container:', JSON.stringify(layoutInfo.container, null, 2));
    console.log('Columns:', JSON.stringify(layoutInfo.columns, null, 2));
    
    // Check if columns are side by side
    if (layoutInfo.columns && layoutInfo.columns.length === 2) {
        const col1 = layoutInfo.columns[0];
        const col2 = layoutInfo.columns[1];
        const sideBySide = Math.abs(col1.top - col2.top) < 50; // Within 50px vertically
        const actuallyHorizontal = col2.left > col1.left + (col1.actualWidth * 0.5);
        
        console.log('🔍 Column Positioning:');
        console.log(`Column 1: left=${col1.left}, top=${col1.top}, width=${col1.actualWidth}`);
        console.log(`Column 2: left=${col2.left}, top=${col2.top}, width=${col2.actualWidth}`);
        console.log(`Side by side: ${sideBySide} (top difference: ${Math.abs(col1.top - col2.top)}px)`);
        console.log(`Horizontally arranged: ${actuallyHorizontal}`);
        
        if (!sideBySide || !actuallyHorizontal) {
            console.log('❌ Columns are NOT properly side-by-side!');
        } else {
            console.log('✅ Columns are properly side-by-side!');
        }
    }
    
    await browser.close();
}

debugTwoColumns().catch(console.error);