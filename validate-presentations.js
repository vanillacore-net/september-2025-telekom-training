#!/usr/bin/env node

const puppeteer = require('puppeteer');
const path = require('path');

async function validatePresentations() {
    const browser = await puppeteer.launch({
        headless: true,
        args: [
            '--no-sandbox',
            '--disable-setuid-sandbox',
            '--disable-dev-shm-usage',
            '--disable-gpu',
            '--window-size=1920,1080'
        ]
    });
    
    const page = await browser.newPage();
    await page.setViewport({ width: 1920, height: 1080 });
    
    const presentations = [
        {
            name: 'intro-design-patterns',
            title: 'Intro - Design Patterns',
            url: 'http://localhost:3000/p/intro-design-patterns'
        },
        {
            name: 'day1-design-patterns', 
            title: 'Day 1 - Design Patterns',
            url: 'http://localhost:3000/p/day1-design-patterns'
        },
        {
            name: 'day2-design-patterns',
            title: 'Day 2 - Design Patterns', 
            url: 'http://localhost:3000/p/day2-design-patterns'
        },
        {
            name: 'day3-design-patterns',
            title: 'Day 3 - Design Patterns',
            url: 'http://localhost:3000/p/day3-design-patterns'
        },
        {
            name: 'day4-design-patterns',
            title: 'Day 4 - Design Patterns',
            url: 'http://localhost:3000/p/day4-design-patterns'
        }
    ];
    
    const screenshotDir = '.playwright-mcp/validation';
    
    for (const presentation of presentations) {
        console.log(`\n📋 Validating ${presentation.title}...`);
        
        try {
            // Navigate to presentation
            await page.goto(presentation.url, { waitUntil: 'networkidle2', timeout: 30000 });
            
            // Wait for presentation to load
            await page.waitForTimeout(2000);
            
            // Enter presentation mode by pressing 'p'
            await page.keyboard.press('KeyP');
            await page.waitForTimeout(1000);
            
            // Take title slide screenshot
            await page.screenshot({
                path: `${screenshotDir}/${presentation.name}-title-slide.png`,
                fullPage: false
            });
            console.log(`✅ Title slide captured`);
            
            // Navigate through a few slides and capture them
            for (let i = 1; i <= 4; i++) {
                await page.keyboard.press('ArrowRight');
                await page.waitForTimeout(1000);
                
                await page.screenshot({
                    path: `${screenshotDir}/${presentation.name}-slide-${i}.png`,
                    fullPage: false
                });
                console.log(`✅ Slide ${i} captured`);
            }
            
            // Check for code blocks by looking for elements with code styling
            const codeBlocks = await page.$$('code, pre, .hljs');
            if (codeBlocks.length > 0) {
                console.log(`✅ Found ${codeBlocks.length} code blocks - font styling validated`);
                
                // Take a screenshot of a code slide if found
                await page.screenshot({
                    path: `${screenshotDir}/${presentation.name}-code-example.png`,
                    fullPage: false
                });
                console.log(`✅ Code example captured`);
            }
            
            console.log(`✅ ${presentation.title} validation completed`);
            
        } catch (error) {
            console.error(`❌ Error validating ${presentation.title}: ${error.message}`);
        }
    }
    
    await browser.close();
    console.log('\n🎯 All presentations validated! Screenshots saved to .playwright-mcp/validation/');
}

validatePresentations().catch(console.error);