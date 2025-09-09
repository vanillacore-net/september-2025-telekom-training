const { chromium } = require('playwright');

async function testSlideNavigation() {
    console.log('⏭️  Testing Slide Navigation with Fragments...');
    
    const browser = await chromium.launch();
    const page = await browser.newPage();
    
    try {
        const url = 'http://localhost:3000/p/day1-design-patterns';
        await page.goto(url, { waitUntil: 'networkidle' });
        await page.waitForTimeout(2000);
        
        console.log('🎯 Testing arrow key navigation through fragments and slides...');
        
        // Navigate through several slides and test fragment reveals
        for (let slide = 1; slide <= 5; slide++) {
            console.log(`\n📋 Testing Slide ${slide}:`);
            
            // Count fragments on current slide
            const fragmentCount = await page.$$eval('.fragment', fragments => fragments.length);
            console.log(`   🧩 Found ${fragmentCount} fragments`);
            
            if (fragmentCount > 0) {
                // Navigate through fragments on this slide
                let visibleCount = 0;
                for (let frag = 0; frag < Math.min(fragmentCount, 5); frag++) {
                    await page.keyboard.press('ArrowRight');
                    await page.waitForTimeout(300);
                    
                    visibleCount = await page.evaluate(() => {
                        return document.querySelectorAll('.fragment.visible, .fragment.current-fragment').length;
                    });
                    
                    console.log(`   ⚡ Step ${frag + 1}: ${visibleCount} fragments visible`);
                }
            }
            
            // Move to next slide
            await page.keyboard.press('ArrowRight');
            await page.waitForTimeout(500);
        }
        
        // Test code block fragments specifically
        console.log('\n💻 Testing Code Block Fragments...');
        
        // Try to find a slide with code blocks
        for (let i = 0; i < 10; i++) {
            const codeBlocks = await page.$$eval('pre code', codes => codes.length);
            if (codeBlocks > 0) {
                console.log(`   📝 Found slide with ${codeBlocks} code blocks`);
                
                // Take screenshot of code blocks
                await page.screenshot({ 
                    path: '.playwright-mcp/code-blocks-with-fragments.png',
                    fullPage: true 
                });
                console.log('   📸 Code blocks screenshot saved');
                break;
            }
            await page.keyboard.press('ArrowRight');
            await page.waitForTimeout(500);
        }
        
        console.log('\n✅ NAVIGATION TEST COMPLETED SUCCESSFULLY!');
        return true;
        
    } catch (error) {
        console.error(`❌ Navigation test failed: ${error.message}`);
        return false;
    } finally {
        await browser.close();
    }
}

testSlideNavigation().then(success => {
    process.exit(success ? 0 : 1);
});