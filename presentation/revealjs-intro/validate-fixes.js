const puppeteer = require('puppeteer');
const path = require('path');

async function validateAllFixes() {
    console.log('🔧 Validating ALL critical fixes for RevealJS presentation...\n');
    
    const browser = await puppeteer.launch({
        headless: false,
        defaultViewport: { width: 1920, height: 1080 },
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    
    const page = await browser.newPage();
    await page.setViewport({ width: 1920, height: 1080 });
    
    const htmlPath = path.resolve(__dirname, 'index.html');
    await page.goto(`file://${htmlPath}`, { waitUntil: 'networkidle0' });
    
    let allTestsPassed = true;
    const results = [];
    
    try {
        // Test 1: Two-Column Layout Fix
        console.log('🔍 Testing Two-Column Layout...');
        
        // Navigate to two-column slide (slide 4)
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const columnsContainer = await page.$('.columns-container');
        const containerStyles = await page.evaluate(el => {
            const computed = window.getComputedStyle(el);
            return {
                display: computed.display,
                flexDirection: computed.flexDirection,
                width: computed.width
            };
        }, columnsContainer);
        
        const columns = await page.$$('.column');
        const columnWidths = [];
        for (let col of columns) {
            const width = await page.evaluate(el => {
                const computed = window.getComputedStyle(el);
                return {
                    width: computed.width,
                    flex: computed.flex,
                    display: computed.display
                };
            }, col);
            columnWidths.push(width);
        }
        
        const twoColumnLayoutPassed = 
            containerStyles.display === 'flex' &&
            containerStyles.flexDirection === 'row' &&
            columns.length === 2 &&
            columnWidths.every(col => col.display === 'flex');
            
        results.push({
            test: '1. Two-Column Layout',
            passed: twoColumnLayoutPassed,
            details: `Container: ${containerStyles.display}/${containerStyles.flexDirection}, Columns: ${columns.length}, Each column flex: ${columnWidths.map(c => c.flex).join(', ')}`
        });
        
        if (!twoColumnLayoutPassed) allTestsPassed = false;
        
        // Test 2: Fragment Order Fix
        console.log('🔍 Testing Fragment Order...');
        
        const fragments = await page.$$('[data-fragment-index]');
        const fragmentIndices = [];
        for (let fragment of fragments) {
            const index = await page.evaluate(el => el.getAttribute('data-fragment-index'), fragment);
            const text = await page.evaluate(el => el.textContent.trim(), fragment);
            fragmentIndices.push({ index: parseInt(index), text });
        }
        
        // Check if indices are sequential (1,2,3,4,5,6,7,8,9)
        const expectedIndices = [1,2,3,4,5,6,7,8,9];
        const actualIndices = fragmentIndices.map(f => f.index).sort((a,b) => a-b);
        const fragmentOrderPassed = JSON.stringify(expectedIndices) === JSON.stringify(actualIndices);
        
        results.push({
            test: '2. Fragment Order',
            passed: fragmentOrderPassed,
            details: `Expected: [1,2,3,4,5,6,7,8,9], Actual: [${actualIndices.join(',')}]`
        });
        
        if (!fragmentOrderPassed) allTestsPassed = false;
        
        // Test 3: Section Slide Text Alignment
        console.log('🔍 Testing Section Slide Alignment...');
        
        // Navigate to section slide (slide 2)
        await page.keyboard.press('Home');
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const sectionSlide = await page.$('.section-slide');
        const sectionH1 = await page.$('.section-slide h1');
        
        const sectionStyles = await page.evaluate(el => {
            const computed = window.getComputedStyle(el);
            return {
                textAlign: computed.textAlign,
                alignItems: computed.alignItems
            };
        }, sectionSlide);
        
        const h1Styles = await page.evaluate(el => {
            const computed = window.getComputedStyle(el);
            return {
                textAlign: computed.textAlign
            };
        }, sectionH1);
        
        const sectionAlignmentPassed = 
            sectionStyles.textAlign === 'left' && 
            h1Styles.textAlign === 'left' &&
            sectionStyles.alignItems === 'flex-start';
            
        results.push({
            test: '3. Section Slide Left-Alignment',
            passed: sectionAlignmentPassed,
            details: `Section textAlign: ${sectionStyles.textAlign}, H1 textAlign: ${h1Styles.textAlign}, alignItems: ${sectionStyles.alignItems}`
        });
        
        if (!sectionAlignmentPassed) allTestsPassed = false;
        
        // Test 4: Title/Content Positioning Consistency
        console.log('🔍 Testing Title/Content Positioning...');
        
        // Check single-column slide positioning
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const singleColumnH2 = await page.$('.single-column h2');
        const singleColumnStyles = await page.evaluate(el => {
            const computed = window.getComputedStyle(el);
            const rect = el.getBoundingClientRect();
            return {
                position: computed.position,
                top: computed.top,
                left: computed.left,
                actualTop: rect.top,
                actualLeft: rect.left,
                textAlign: computed.textAlign
            };
        }, singleColumnH2);
        
        const positioningConsistentPassed = 
            singleColumnStyles.position === 'absolute' &&
            singleColumnStyles.top === '0px' &&
            singleColumnStyles.left === '4vw' &&
            singleColumnStyles.textAlign === 'left';
            
        results.push({
            test: '4. Title/Content Positioning',
            passed: positioningConsistentPassed,
            details: `Position: ${singleColumnStyles.position}, Top: ${singleColumnStyles.top}, Left: ${singleColumnStyles.left}, TextAlign: ${singleColumnStyles.textAlign}`
        });
        
        if (!positioningConsistentPassed) allTestsPassed = false;
        
        // Test 5: Speaker Notes
        console.log('🔍 Testing Speaker Notes...');
        
        const speakerNotes = await page.$$('aside.notes');
        const notesCount = speakerNotes.length;
        
        // Check if pressing 'S' opens speaker view (this might open a new window/tab)
        const initialPages = await browser.pages();
        await page.keyboard.press('KeyS');
        await new Promise(resolve => setTimeout(resolve, 1000));
        const afterPages = await browser.pages();
        
        const speakerNotesWorking = notesCount > 0 && afterPages.length > initialPages.length;
        
        results.push({
            test: '5. Speaker Notes',
            passed: speakerNotesWorking,
            details: `Notes found: ${notesCount}, Speaker view opened: ${afterPages.length > initialPages.length}`
        });
        
        if (!speakerNotesWorking) allTestsPassed = false;
        
        // Take screenshots for visual verification
        console.log('📸 Taking screenshots for visual verification...');
        
        // Screenshot 1: Two-column slide
        await page.keyboard.press('Home');
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight');
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
        await page.screenshot({ path: 'validation-two-column-fixed.png', fullPage: true });
        
        // Screenshot 2: Section slide
        await page.keyboard.press('Home');
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
        await page.screenshot({ path: 'validation-section-slide-fixed.png', fullPage: true });
        
        // Screenshot 3: Single column slide
        await page.keyboard.press('ArrowRight');
        await new Promise(resolve => setTimeout(resolve, 500));
        await page.screenshot({ path: 'validation-single-column-fixed.png', fullPage: true });
        
    } catch (error) {
        console.error('❌ Error during validation:', error);
        allTestsPassed = false;
    }
    
    // Display results
    console.log('\n📊 VALIDATION RESULTS:');
    console.log('=' .repeat(60));
    
    results.forEach(result => {
        const status = result.passed ? '✅ PASSED' : '❌ FAILED';
        console.log(`${status} - ${result.test}`);
        console.log(`   Details: ${result.details}\n`);
    });
    
    console.log('=' .repeat(60));
    const overallStatus = allTestsPassed ? '🎉 ALL TESTS PASSED' : '⚠️  SOME TESTS FAILED';
    console.log(`OVERALL: ${overallStatus}`);
    
    if (allTestsPassed) {
        console.log('\n✨ All critical issues have been fixed!');
        console.log('✅ Two-column layout is now horizontal side-by-side');
        console.log('✅ Fragment order is correct (1-9 sequential)');
        console.log('✅ Section slides are left-aligned');
        console.log('✅ Title/content positioning is consistent');
        console.log('✅ Speaker notes are available (press S key)');
        console.log('\n🚀 Presentation is ready for use at 1920x1080!');
    } else {
        console.log('\n🔧 Some issues still need attention. Check the details above.');
    }
    
    await browser.close();
    return allTestsPassed;
}

// Run validation if script is executed directly
if (require.main === module) {
    validateAllFixes().catch(console.error);
}

module.exports = validateAllFixes;