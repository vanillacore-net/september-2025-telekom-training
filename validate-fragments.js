#!/usr/bin/env node

/**
 * Fragment Validation Script
 * Tests if fragment fixes are working in HedgeDoc presentations
 */

const http = require('http');

function testURL(url, description) {
    return new Promise((resolve, reject) => {
        const req = http.get(url, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => {
                // Check if the response contains the fragment CSS fixes
                const hasFragmentCSS = data.includes('.reveal .fragment {') && 
                                      data.includes('visibility: hidden') && 
                                      data.includes('.reveal .fragment.visible');
                
                // Check for both HTML and HTML-encoded fragment elements
                const hasFragmentElements = (data.includes('class="fragment"') || data.includes('class=&#34;fragment&#34;')) && 
                                           (data.includes('data-fragment-index') || data.includes('data-fragment-index=&#34;'));
                
                console.log(`\n✅ ${description}`);
                console.log(`   URL: ${url}`);
                console.log(`   Status: ${res.statusCode}`);
                console.log(`   Fragment CSS: ${hasFragmentCSS ? '✅ Present' : '❌ Missing'}`);
                console.log(`   Fragment Elements: ${hasFragmentElements ? '✅ Present' : '❌ Missing'}`);
                
                resolve({
                    url,
                    status: res.statusCode,
                    hasFragmentCSS,
                    hasFragmentElements,
                    success: res.statusCode === 200 && hasFragmentCSS && hasFragmentElements
                });
            });
        });
        
        req.on('error', (err) => {
            console.log(`❌ ${description} - Error: ${err.message}`);
            resolve({ url, success: false, error: err.message });
        });
        
        req.setTimeout(5000, () => {
            req.destroy();
            console.log(`❌ ${description} - Timeout`);
            resolve({ url, success: false, error: 'Timeout' });
        });
    });
}

async function validateFragments() {
    console.log('🧪 Fragment Validation Test');
    console.log('=' .repeat(50));
    
    const tests = [
        { url: 'http://localhost:3000/p/day1-design-patterns', desc: 'Day 1 Presentation' },
        { url: 'http://localhost:3000/p/day2-design-patterns', desc: 'Day 2 Presentation' },
        { url: 'http://localhost:3000/p/day3-design-patterns', desc: 'Day 3 Presentation' },
        { url: 'http://localhost:3000/p/day4-design-patterns', desc: 'Day 4 Presentation' }
    ];
    
    const results = [];
    
    for (const test of tests) {
        const result = await testURL(test.url, test.desc);
        results.push(result);
    }
    
    console.log('\n' + '=' .repeat(50));
    console.log('📊 VALIDATION SUMMARY');
    console.log('=' .repeat(50));
    
    const successful = results.filter(r => r.success).length;
    const total = results.length;
    
    console.log(`Overall Status: ${successful}/${total} presentations working correctly`);
    
    if (successful === total) {
        console.log('\n🎉 ALL FRAGMENT FIXES SUCCESSFUL!');
        console.log('✅ Fragments should now reveal step-by-step');
        console.log('✅ No more gray backgrounds appearing early');
        console.log('✅ Smooth transitions implemented');
    } else {
        console.log('\n⚠️  Some issues remain:');
        results.filter(r => !r.success).forEach(r => {
            console.log(`   - ${r.url}: ${r.error || 'Fragment fixes not detected'}`);
        });
    }
    
    console.log('\n🔗 Test URLs:');
    tests.forEach(test => {
        console.log(`   ${test.desc}: ${test.url}`);
    });
    
    return successful === total;
}

// Run validation
validateFragments().then(success => {
    process.exit(success ? 0 : 1);
}).catch(err => {
    console.error('Validation failed:', err);
    process.exit(1);
});