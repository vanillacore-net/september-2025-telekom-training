const fs = require('fs');
const path = require('path');

// List of presentation files to validate
const presentationFiles = [
    'presentation/hedgedoc/hedgedoc-day1.md',
    'presentation/hedgedoc/hedgedoc-day2.md',
    'presentation/hedgedoc/hedgedoc-day3.md',
    'presentation/hedgedoc/hedgedoc-day4.md'
];

// Expected patterns for each day
const expectedPatterns = {
    'hedgedoc-day1.md': ['Factory Method', 'Abstract Factory', 'Builder', 'Prototype', 'Singleton'],
    'hedgedoc-day2.md': ['Adapter', 'Decorator', 'Facade', 'Composite', 'Proxy'],
    'hedgedoc-day3.md': ['Observer', 'Strategy', 'Command', 'Template Method', 'Iterator'],
    'hedgedoc-day4.md': ['MVC', 'MVP', 'MVVM', 'Microservices']
};

console.log('🔍 Validating Pattern Slide Restructuring...\n');

let totalPatterns = 0;
let restructuredPatterns = 0;
let validationResults = [];

presentationFiles.forEach(filePath => {
    const fileName = path.basename(filePath);
    console.log(`📄 Checking ${fileName}...`);
    
    if (!fs.existsSync(filePath)) {
        console.log(`❌ File not found: ${filePath}`);
        return;
    }
    
    const content = fs.readFileSync(filePath, 'utf8');
    const patterns = expectedPatterns[fileName] || [];
    
    patterns.forEach(pattern => {
        totalPatterns++;
        
        // Check for "Was passt hier nicht?" sections
        const wasPasstPattern = new RegExp(`# ${pattern}[\\s\\S]*?## Was passt hier nicht\\?`, 'i');
        const conceptPattern = new RegExp(`## ${pattern}[\\s\\S]*?Konzept`, 'i');
        const solutionPattern = new RegExp(`## Lösung`, 'i');
        const exercisePattern = new RegExp(`## Übung`, 'i');
        
        const hasWasPasst = wasPasstPattern.test(content);
        const hasConcept = conceptPattern.test(content);
        const hasSolution = solutionPattern.test(content);
        
        if (hasWasPasst && hasConcept && hasSolution) {
            restructuredPatterns++;
            console.log(`  ✅ ${pattern} - Properly restructured`);
        } else {
            console.log(`  ❌ ${pattern} - Missing sections:`);
            if (!hasWasPasst) console.log(`      - Was passt hier nicht?`);
            if (!hasConcept) console.log(`      - Konzept section`);
            if (!hasSolution) console.log(`      - Lösung section`);
        }
        
        validationResults.push({
            file: fileName,
            pattern: pattern,
            restructured: hasWasPasst && hasConcept && hasSolution
        });
    });
    
    console.log('');
});

// Summary
console.log('📊 VALIDATION SUMMARY');
console.log('====================');
console.log(`Total Patterns: ${totalPatterns}`);
console.log(`Restructured: ${restructuredPatterns}`);
console.log(`Remaining: ${totalPatterns - restructuredPatterns}`);
console.log(`Success Rate: ${Math.round((restructuredPatterns/totalPatterns) * 100)}%`);

if (restructuredPatterns === totalPatterns) {
    console.log('\n🎉 ALL PATTERNS SUCCESSFULLY RESTRUCTURED!');
    process.exit(0);
} else {
    console.log('\n⚠️  Pattern restructuring incomplete');
    
    // Show which patterns still need work
    console.log('\nPending patterns:');
    validationResults
        .filter(r => !r.restructured)
        .forEach(r => console.log(`  - ${r.file}: ${r.pattern}`));
    
    process.exit(1);
}