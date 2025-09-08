#!/usr/bin/env node

/**
 * Validation script for Marp presentation build
 * Ensures all components are working correctly
 */

const fs = require('fs');
const path = require('path');

console.log('🔍 Validating Marp presentation build...\n');

const checks = [
    {
        name: 'Theme file exists',
        path: 'themes/telekom-theme.css',
        test: (filepath) => fs.existsSync(filepath)
    },
    {
        name: 'Slides content exists',
        path: 'slides.md',
        test: (filepath) => fs.existsSync(filepath)
    },
    {
        name: 'Logo assets exist',
        path: 'assets/img',
        test: (dirpath) => {
            const required = ['VanillaCore_Vertical.png', 'VanillaCore_Square.png'];
            return fs.existsSync(dirpath) && 
                   required.every(file => fs.existsSync(path.join(dirpath, file)));
        }
    },
    {
        name: 'HTML build exists',
        path: 'dist/index.html',
        test: (filepath) => fs.existsSync(filepath)
    },
    {
        name: 'PDF build exists', 
        path: 'dist/presentation.pdf',
        test: (filepath) => fs.existsSync(filepath)
    },
    {
        name: 'Package.json is valid',
        path: 'package.json',
        test: (filepath) => {
            try {
                const pkg = JSON.parse(fs.readFileSync(filepath, 'utf8'));
                return pkg.scripts && pkg.scripts['build:html'] && pkg.scripts['build:pdf'];
            } catch (e) {
                return false;
            }
        }
    },
    {
        name: 'Theme contains required classes',
        path: 'themes/telekom-theme.css',
        test: (filepath) => {
            const css = fs.readFileSync(filepath, 'utf8');
            const requiredClasses = [
                'title-slide',
                'section-slide', 
                'content-slide',
                'single-column',
                'two-columns',
                'half-picture',
                'code-embedded',
                'code-standalone',
                'full-picture'
            ];
            return requiredClasses.every(cls => css.includes(cls));
        }
    },
    {
        name: 'Slides contain all required slide types',
        path: 'slides.md',
        test: (filepath) => {
            const content = fs.readFileSync(filepath, 'utf8');
            const requiredTypes = [
                '_class: title-slide',
                '_class: section-slide',
                '_class: content-slide single-column',
                '_class: content-slide two-columns',
                '_class: content-slide half-picture',
                '_class: content-slide code-embedded',
                '_class: content-slide code-standalone',
                '_class: full-picture'
            ];
            return requiredTypes.every(type => content.includes(type));
        }
    }
];

let passed = 0;
let failed = 0;

checks.forEach(check => {
    const result = check.test(check.path);
    const status = result ? '✅' : '❌';
    console.log(`${status} ${check.name}`);
    
    if (result) {
        passed++;
    } else {
        failed++;
        console.log(`   └─ Failed: ${check.path}`);
    }
});

console.log(`\n📊 Results: ${passed} passed, ${failed} failed`);

if (failed === 0) {
    console.log('\n🎉 All validation checks passed! Marp presentation is ready.');
    process.exit(0);
} else {
    console.log('\n❌ Some validation checks failed. Please review and fix.');
    process.exit(1);
}