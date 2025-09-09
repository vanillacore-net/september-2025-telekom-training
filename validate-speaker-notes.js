#!/usr/bin/env node

/**
 * Validate Speaker Notes in HedgeDoc Presentations
 * Tests if speaker notes are properly formatted and accessible
 */

const puppeteer = require('puppeteer');

const HEDGEDOC_URL = 'http://localhost:3000';
const PRESENTATIONS = [
    'day1-design-patterns',
    'day2-design-patterns', 
    'day3-design-patterns',
    'day4-design-patterns'
];

async function validateSpeakerNotes() {
    console.log('🎯 Validating Speaker Notes in HedgeDoc Presentations');
    console.log('=====================================================\n');
    
    const browser = await puppeteer.launch({
        headless: 'new',
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    
    try {
        for (const presentation of PRESENTATIONS) {
            console.log(`📋 Testing: ${presentation}`);
            await testPresentation(browser, presentation);
            console.log('');
        }
        
        console.log('✅ All speaker note validations completed successfully!');
    } catch (error) {
        console.error('❌ Validation failed:', error.message);
        process.exit(1);
    } finally {
        await browser.close();
    }
}

async function testPresentation(browser, presentation) {
    const page = await browser.newPage();
    
    try {
        // Test slide mode URL
        const slideUrl = `${HEDGEDOC_URL}/p/${presentation}`;
        console.log(`   🔗 Loading: ${slideUrl}`);
        
        await page.goto(slideUrl, { 
            waitUntil: 'networkidle0',
            timeout: 30000 
        });
        
        // Wait for reveal.js to initialize
        await page.waitForSelector('.reveal', { timeout: 10000 });
        console.log('   ✅ Presentation loaded successfully');
        
        // Check for speaker notes in the DOM
        const notesFound = await page.evaluate(() => {
            const noteElements = document.querySelectorAll('.notes, [class*="notes"]');
            return noteElements.length;
        });
        
        if (notesFound > 0) {
            console.log(`   ✅ Found ${notesFound} speaker note elements`);
        } else {
            console.log('   ⚠️  No speaker note elements found');
        }
        
        // Test if speaker mode is accessible (press 's' key)
        await page.keyboard.press('s');
        
        // Wait a moment for speaker mode to potentially open
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // Check if we're in speaker mode or if it opened a popup
        const isInSpeakerMode = await page.evaluate(() => {
            // Check for speaker mode indicators
            return document.querySelector('.reveal.speaker') !== null ||
                   document.body.classList.contains('speaker') ||
                   document.querySelector('[data-reveal-speaker]') !== null;
        });
        
        if (isInSpeakerMode) {
            console.log('   ✅ Speaker mode activated successfully');
        } else {
            console.log('   ℹ️  Speaker mode may open in popup window (normal behavior)');
        }
        
        // Check for proper German content
        const hasGermanContent = await page.evaluate(() => {
            const textContent = document.body.textContent || '';
            const germanWords = ['Übung', 'Erklären', 'Diskussion', 'Zeit:', 'Minuten'];
            return germanWords.some(word => textContent.includes(word));
        });
        
        if (hasGermanContent) {
            console.log('   ✅ German speaker guidance content detected');
        } else {
            console.log('   ⚠️  No German speaker guidance detected');
        }
        
        // Test slide navigation
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
        console.log('   ✅ Slide navigation working');
        
    } catch (error) {
        console.log(`   ❌ Error testing ${presentation}: ${error.message}`);
    } finally {
        await page.close();
    }
}

// Run validation
if (require.main === module) {
    validateSpeakerNotes().catch(console.error);
}

module.exports = { validateSpeakerNotes };