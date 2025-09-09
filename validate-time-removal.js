const { chromium } = require('playwright');

async function validateTimeRemoval() {
    console.log('🔍 Validating time information removal from presentations...');
    
    const browser = await chromium.launch({ headless: true });
    const context = await browser.newContext();
    
    const presentations = [
        { name: 'Day 1', url: 'http://localhost:3000/p/day1-design-patterns' },
        { name: 'Day 2', url: 'http://localhost:3000/p/day2-design-patterns' },
        { name: 'Day 3', url: 'http://localhost:3000/p/day3-design-patterns' },
        { name: 'Day 4', url: 'http://localhost:3000/p/day4-design-patterns' }
    ];
    
    const timePatterns = [
        /\(\d+\s*[Mm]in\)/,           // (10 min), (15 Min)
        /\(\d+\s*[Mm]inuten\)/,       // (10 Minuten)
        /Exercise Time:\s*\d+/,        // Exercise Time: 90
        /Zeit:\s*\d+/,                 // Zeit: 15
        /Time:\s*\d+/,                 // Time: 45
        /\d+\s*minutes/i,              // 90 minutes
        /\d+\s*Minuten/                // 90 Minuten
    ];
    
    let allPassed = true;
    
    for (const presentation of presentations) {
        console.log(`\n📄 Checking ${presentation.name}...`);
        
        const page = await context.newPage();
        
        try {
            await page.goto(presentation.url, { waitUntil: 'networkidle' });
            await page.waitForTimeout(3000); // Wait for slide rendering
            
            // Get all visible text content
            const visibleText = await page.textContent('body');
            
            // Check for time patterns in visible content
            let foundTimeReferences = [];
            
            for (const pattern of timePatterns) {
                const matches = visibleText.match(new RegExp(pattern.source, 'gi'));
                if (matches) {
                    // Filter out code-related time references
                    const filtered = matches.filter(match => {
                        const context = visibleText.substring(
                            Math.max(0, visibleText.indexOf(match) - 50),
                            visibleText.indexOf(match) + match.length + 50
                        ).toLowerCase();
                        
                        // Skip if it's in code context
                        return !context.includes('cache_ttl') && 
                               !context.includes('// fragment') &&
                               !context.includes('timeout') &&
                               !context.includes('ttl');
                    });
                    
                    foundTimeReferences.push(...filtered);
                }
            }
            
            if (foundTimeReferences.length > 0) {
                console.log(`❌ ${presentation.name}: Found visible time references:`);
                foundTimeReferences.forEach(ref => console.log(`   - ${ref}`));
                allPassed = false;
            } else {
                console.log(`✅ ${presentation.name}: No visible time references found`);
            }
            
        } catch (error) {
            console.log(`❌ ${presentation.name}: Error - ${error.message}`);
            allPassed = false;
        } finally {
            await page.close();
        }
    }
    
    await browser.close();
    
    console.log('\n='.repeat(50));
    if (allPassed) {
        console.log('✅ SUCCESS: All presentations are clean of visible time information!');
    } else {
        console.log('❌ FAILURE: Some presentations still contain visible time information');
    }
    console.log('='.repeat(50));
    
    return allPassed;
}

validateTimeRemoval().catch(console.error);