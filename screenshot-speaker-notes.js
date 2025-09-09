#!/usr/bin/env node

/**
 * Take screenshots of speaker notes functionality
 */

const puppeteer = require('puppeteer');
const fs = require('fs').promises;

const HEDGEDOC_URL = 'http://localhost:3000';

async function captureScreenshots() {
    console.log('📸 Capturing Speaker Notes Screenshots');
    console.log('=====================================\n');
    
    const browser = await puppeteer.launch({
        headless: 'new',
        args: ['--no-sandbox', '--disable-setuid-sandbox'],
        defaultViewport: { width: 1920, height: 1080 }
    });
    
    try {
        // Screenshot of day1 presentation with speaker notes
        await captureDay1Screenshots(browser);
        
        console.log('✅ Screenshots captured successfully!');
        console.log('\nSaved screenshots:');
        console.log('- .playwright-mcp/day1-speaker-notes-demo.png');
        
    } catch (error) {
        console.error('❌ Screenshot capture failed:', error.message);
        process.exit(1);
    } finally {
        await browser.close();
    }
}

async function captureDay1Screenshots(browser) {
    const page = await browser.newPage();
    
    try {
        // Load day1 presentation
        const slideUrl = `${HEDGEDOC_URL}/p/day1-design-patterns`;
        console.log(`📋 Loading: ${slideUrl}`);
        
        await page.goto(slideUrl, { 
            waitUntil: 'networkidle0',
            timeout: 30000 
        });
        
        // Wait for reveal.js to initialize
        await page.waitForSelector('.reveal', { timeout: 10000 });
        
        // Navigate to a slide with speaker notes (around slide 5-6)
        for (let i = 0; i < 5; i++) {
            await page.keyboard.press('ArrowRight');
            await new Promise(resolve => setTimeout(resolve, 300));
        }
        
        // Wait for slide transition
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Take screenshot of normal presentation view
        await page.screenshot({
            path: '.playwright-mcp/day1-speaker-notes-demo.png',
            fullPage: false,
            quality: 90
        });
        
        console.log('✅ Captured day1 presentation screenshot');
        
    } catch (error) {
        console.log(`❌ Error capturing screenshots: ${error.message}`);
    } finally {
        await page.close();
    }
}

// Run screenshot capture
if (require.main === module) {
    captureScreenshots().catch(console.error);
}

module.exports = { captureScreenshots };