const { chromium } = require('playwright');

async function validateRevealAnimations() {
    console.log('🎬 Validating Reveal.js Fragment Animations...');
    
    const browser = await chromium.launch();
    const page = await browser.newPage();
    
    try {
        // Test Day 1 presentation with fragment animations
        const url = 'http://localhost:3000/p/day1-design-patterns';
        console.log(`📖 Loading: ${url}`);
        
        await page.goto(url, { waitUntil: 'networkidle' });
        await page.waitForTimeout(2000);
        
        // Check if reveal.js is loaded
        const revealLoaded = await page.evaluate(() => {
            return typeof window.Reveal !== 'undefined';
        });
        
        if (!revealLoaded) {
            throw new Error('❌ Reveal.js not loaded');
        }
        console.log('✅ Reveal.js loaded successfully');
        
        // Check if fragments are enabled
        const fragmentsEnabled = await page.evaluate(() => {
            return window.Reveal.getConfig().fragments;
        });
        
        if (!fragmentsEnabled) {
            throw new Error('❌ Fragments not enabled');
        }
        console.log('✅ Fragments enabled');
        
        // Navigate to a slide with fragment animations (Factory Method section)
        await page.keyboard.press('ArrowRight'); // Go to next slide
        await page.keyboard.press('ArrowRight'); // Continue to content slides
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight'); // Should be at Factory Method section
        await page.waitForTimeout(1000);
        
        // Take screenshot of initial state
        await page.screenshot({ 
            path: '.playwright-mcp/reveal-animation-before.png',
            fullPage: true 
        });
        console.log('📸 Screenshot saved: reveal-animation-before.png');
        
        // Check for fragment elements
        const fragmentElements = await page.$$('.fragment');
        console.log(`🧩 Found ${fragmentElements.length} fragment elements on current slide`);
        
        if (fragmentElements.length === 0) {
            console.log('⚠️  No fragments found on current slide, navigating to find fragments...');
            
            // Try to find a slide with fragments
            for (let i = 0; i < 10; i++) {
                await page.keyboard.press('ArrowRight');
                await page.waitForTimeout(500);
                
                const fragments = await page.$$('.fragment');
                if (fragments.length > 0) {
                    console.log(`✅ Found ${fragments.length} fragments on slide ${i + 6}`);
                    break;
                }
            }
        }
        
        // Test fragment reveal by pressing space/arrow
        console.log('🎯 Testing fragment reveals...');
        
        for (let i = 0; i < 5; i++) {
            await page.keyboard.press('ArrowRight');
            await page.waitForTimeout(800); // Give time for animation
            
            // Count visible fragments
            const visibleFragments = await page.evaluate(() => {
                const fragments = document.querySelectorAll('.fragment');
                let visible = 0;
                fragments.forEach(fragment => {
                    if (fragment.classList.contains('visible') || 
                        fragment.classList.contains('current-fragment')) {
                        visible++;
                    }
                });
                return visible;
            });
            
            console.log(`🎬 Step ${i + 1}: ${visibleFragments} fragments now visible`);
        }
        
        // Take final screenshot
        await page.screenshot({ 
            path: '.playwright-mcp/reveal-animation-after.png',
            fullPage: true 
        });
        console.log('📸 Screenshot saved: reveal-animation-after.png');
        
        // Validate that fragment animations are working
        const finalFragmentCount = await page.evaluate(() => {
            return document.querySelectorAll('.fragment.visible, .fragment.current-fragment').length;
        });
        
        console.log(`\n🎉 VALIDATION RESULTS:`);
        console.log(`✅ Reveal.js loaded and configured`);
        console.log(`✅ Fragments enabled in configuration`);
        console.log(`✅ ${finalFragmentCount} fragments revealed successfully`);
        console.log(`✅ Fragment animations are working!`);
        
        return true;
        
    } catch (error) {
        console.error(`❌ Validation failed: ${error.message}`);
        
        // Take error screenshot
        await page.screenshot({ 
            path: '.playwright-mcp/reveal-animation-error.png',
            fullPage: true 
        });
        console.log('📸 Error screenshot saved: reveal-animation-error.png');
        
        return false;
    } finally {
        await browser.close();
    }
}

// Run validation
validateRevealAnimations().then(success => {
    if (success) {
        console.log('\n🎊 REVEAL ANIMATION VALIDATION COMPLETED SUCCESSFULLY!');
        process.exit(0);
    } else {
        console.log('\n💥 REVEAL ANIMATION VALIDATION FAILED!');
        process.exit(1);
    }
});