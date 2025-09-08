const { chromium } = require('@playwright/test');
const path = require('path');
const fs = require('fs');

async function validateSlides() {
    const browser = await chromium.launch({
        headless: true,
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    
    const page = await browser.newPage();
    
    // Set viewport to standard presentation size
    await page.setViewportSize({ width: 1280, height: 720 });
    
    // Create screenshots directory
    const screenshotsDir = './.playwright-mcp';
    if (!fs.existsSync(screenshotsDir)) {
        fs.mkdirSync(screenshotsDir, { recursive: true });
    }
    
    // Navigate to the presentation
    const htmlPath = path.resolve(__dirname, 'index.html');
    await page.goto(`file://${htmlPath}`);
    
    // Wait for RevealJS to initialize
    await page.waitForFunction(() => window.Reveal && window.Reveal.isReady());
    await page.waitForTimeout(1000); // Additional wait for CSS to settle
    
    console.log('Starting slide validation with screenshots...');
    
    const slides = [
        { name: 'slide-1-title', description: 'Title Slide' },
        { name: 'slide-2-section', description: 'Section Slide' },
        { name: 'slide-3-single-column', description: 'Single Column Content' },
        { name: 'slide-4-two-columns', description: 'Two Column Layout' },
        { name: 'slide-5-half-picture', description: 'Half Picture Layout' },
        { name: 'slide-6-full-picture', description: 'Full Picture Slide' }
    ];
    
    let allPassed = true;
    
    for (let i = 0; i < slides.length; i++) {
        const slide = slides[i];
        
        console.log(`\n--- Validating ${slide.description} (${i + 1}/6) ---`);
        
        // Navigate to specific slide
        await page.evaluate((slideIndex) => {
            window.Reveal.slide(slideIndex, 0);
        }, i);
        
        // Wait for slide transition
        await page.waitForTimeout(500);
        
        // Take screenshot
        const screenshotPath = path.join(screenshotsDir, `fixed-${slide.name}.png`);
        await page.screenshot({
            path: screenshotPath,
            fullPage: false
        });
        
        console.log(`✅ Screenshot saved: ${screenshotPath}`);
        
        // Validate specific slide elements
        const validationResults = await validateSlideLayout(page, i, slide.description);
        
        if (!validationResults.passed) {
            allPassed = false;
            console.log(`❌ ${slide.description} failed validation:`);
            validationResults.issues.forEach(issue => console.log(`   - ${issue}`));
        } else {
            console.log(`✅ ${slide.description} passed all layout validations`);
        }
    }
    
    await browser.close();
    
    console.log('\n=== VALIDATION SUMMARY ===');
    if (allPassed) {
        console.log('🎉 ALL SLIDES PASSED VALIDATION');
        console.log('   - All layouts are working correctly');
        console.log('   - Content is properly positioned');
        console.log('   - Screenshots saved for verification');
        return true;
    } else {
        console.log('💥 SOME SLIDES FAILED VALIDATION');
        console.log('   - Check individual slide results above');
        console.log('   - Review screenshots for visual confirmation');
        return false;
    }
}

async function validateSlideLayout(page, slideIndex, slideDescription) {
    const issues = [];
    
    try {
        // Get slide element
        const slideSelector = '.reveal .slides > section.present';
        const slide = await page.$(slideSelector);
        
        if (!slide) {
            issues.push('Could not find active slide element');
            return { passed: false, issues };
        }
        
        // Check if slide is visible
        const isVisible = await slide.isVisible();
        if (!isVisible) {
            issues.push('Slide is not visible');
        }
        
        // Validate based on slide type
        switch (slideIndex) {
            case 0: // Title slide
                await validateTitleSlide(page, issues);
                break;
            case 1: // Section slide
                await validateSectionSlide(page, issues);
                break;
            case 2: // Single column
                await validateSingleColumnSlide(page, issues);
                break;
            case 3: // Two columns
                await validateTwoColumnSlide(page, issues);
                break;
            case 4: // Half picture
                await validateHalfPictureSlide(page, issues);
                break;
            case 5: // Full picture
                await validateFullPictureSlide(page, issues);
                break;
        }
        
    } catch (error) {
        issues.push(`Validation error: ${error.message}`);
    }
    
    return { passed: issues.length === 0, issues };
}

async function validateTitleSlide(page, issues) {
    // Check if title elements are present and centered
    const titleSlide = await page.$('.title-slide');
    if (!titleSlide) {
        issues.push('Title slide container not found');
        return;
    }
    
    const logo = await page.$('.title-slide .logo');
    const h1 = await page.$('.title-slide h1');
    const h2 = await page.$('.title-slide h2');
    
    if (!logo) issues.push('Title slide logo missing');
    if (!h1) issues.push('Title slide H1 missing');
    if (!h2) issues.push('Title slide H2 missing');
}

async function validateSectionSlide(page, issues) {
    const sectionSlide = await page.$('.section-slide');
    if (!sectionSlide) {
        issues.push('Section slide container not found');
        return;
    }
    
    const logo = await page.$('.section-slide .logo');
    const h1 = await page.$('.section-slide h1');
    
    if (!logo) issues.push('Section slide logo missing');
    if (!h1) issues.push('Section slide H1 missing');
}

async function validateSingleColumnSlide(page, issues) {
    const singleColumn = await page.$('.single-column');
    if (!singleColumn) {
        issues.push('Single column container not found');
        return;
    }
    
    const logoCorner = await page.$('.single-column .logo-corner');
    const h2 = await page.$('.single-column h2');
    const ul = await page.$('.single-column ul');
    
    if (!logoCorner) issues.push('Single column corner logo missing');
    if (!h2) issues.push('Single column H2 missing');
    if (!ul) issues.push('Single column list missing');
    
    // Check if logo is in top-right corner
    if (logoCorner) {
        const logoBox = await logoCorner.boundingBox();
        if (logoBox && logoBox.y > 50) {
            issues.push('Corner logo not positioned at top of slide');
        }
    }
    
    // Check if content starts near top
    if (h2) {
        const h2Box = await h2.boundingBox();
        if (h2Box && h2Box.y > 100) {
            issues.push('Content appears too far from top of slide');
        }
    }
}

async function validateTwoColumnSlide(page, issues) {
    const twoColumns = await page.$('.two-columns');
    if (!twoColumns) {
        issues.push('Two columns container not found');
        return;
    }
    
    const logoCorner = await page.$('.two-columns .logo-corner');
    const h2 = await page.$('.two-columns h2');
    const columnsContainer = await page.$('.columns-container');
    const columns = await page.$$('.column');
    
    if (!logoCorner) issues.push('Two columns corner logo missing');
    if (!h2) issues.push('Two columns H2 missing');
    if (!columnsContainer) issues.push('Columns container missing');
    if (columns.length !== 2) issues.push(`Expected 2 columns, found ${columns.length}`);
    
    // Check positioning
    if (h2) {
        const h2Box = await h2.boundingBox();
        if (h2Box && h2Box.y > 100) {
            issues.push('Two columns content appears too far from top');
        }
    }
}

async function validateHalfPictureSlide(page, issues) {
    const halfPicture = await page.$('.half-picture');
    if (!halfPicture) {
        issues.push('Half picture container not found');
        return;
    }
    
    const textContent = await page.$('.half-picture .text-content');
    const imageContent = await page.$('.half-picture .image-content');
    
    if (!textContent) issues.push('Half picture text content missing');
    if (!imageContent) issues.push('Half picture image content missing');
    
    // Check if they're side by side
    if (textContent && imageContent) {
        const textBox = await textContent.boundingBox();
        const imageBox = await imageContent.boundingBox();
        
        if (textBox && imageBox) {
            // Text should be on the left, image on the right
            // Allow some margin of error for positioning
            if (textBox.x >= (imageBox.x - 10)) {
                issues.push('Text and image content not arranged side by side correctly');
            }
        }
    }
}

async function validateFullPictureSlide(page, issues) {
    const fullPicture = await page.$('.full-picture');
    if (!fullPicture) {
        issues.push('Full picture container not found');
        return;
    }
    
    const placeholderImage = await page.$('.full-picture .placeholder-image');
    const textOverlay = await page.$('.full-picture .text-overlay');
    
    if (!placeholderImage) issues.push('Full picture placeholder image missing');
    if (!textOverlay) issues.push('Full picture text overlay missing');
}

// Run the validation
validateSlides().then(success => {
    process.exit(success ? 0 : 1);
}).catch(error => {
    console.error('Validation failed:', error);
    process.exit(1);
});