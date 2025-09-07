/**
 * Font Verification Script for Telekom Theme
 * 
 * This script verifies that the correct fonts and font weights are applied:
 * - Headlines: Open Sans Regular (font-weight: 400) - NOT BOLD
 * - Content: Open Sans Light (font-weight: 300)
 * - Code: Source Code Pro
 * - Colors: Telekom Orange #D9931C
 */

function verifyTelekomTheme() {
  console.log('=== Telekom Theme Font Verification ===');
  
  // Check if running in browser
  if (typeof window === 'undefined') {
    console.log('This script should be run in a browser with a loaded presentation');
    return;
  }
  
  const results = {};
  
  // Check headline fonts
  const headlines = document.querySelectorAll('h1, h2, h3, h4, h5, h6');
  if (headlines.length > 0) {
    const h1Style = window.getComputedStyle(headlines[0]);
    results.headlineFontFamily = h1Style.fontFamily;
    results.headlineFontWeight = h1Style.fontWeight;
    
    console.log(`Headlines Font Family: ${results.headlineFontFamily}`);
    console.log(`Headlines Font Weight: ${results.headlineFontWeight}`);
    
    // Verify font weight is 400 (regular, not bold)
    if (results.headlineFontWeight === '400' || results.headlineFontWeight === 'normal') {
      console.log('✅ Headlines correctly use Regular font weight (400)');
    } else {
      console.log(`❌ Headlines use incorrect font weight: ${results.headlineFontWeight} (should be 400)`);
    }
  }
  
  // Check content text fonts
  const contentElements = document.querySelectorAll('p, li');
  if (contentElements.length > 0) {
    const contentStyle = window.getComputedStyle(contentElements[0]);
    results.contentFontFamily = contentStyle.fontFamily;
    results.contentFontWeight = contentStyle.fontWeight;
    
    console.log(`Content Font Family: ${results.contentFontFamily}`);
    console.log(`Content Font Weight: ${results.contentFontWeight}`);
    
    // Verify font weight is 300 (light)
    if (results.contentFontWeight === '300') {
      console.log('✅ Content correctly uses Light font weight (300)');
    } else {
      console.log(`❌ Content uses incorrect font weight: ${results.contentFontWeight} (should be 300)`);
    }
  }
  
  // Check code blocks
  const codeElements = document.querySelectorAll('code, pre code');
  if (codeElements.length > 0) {
    const codeStyle = window.getComputedStyle(codeElements[0]);
    results.codeFontFamily = codeStyle.fontFamily;
    
    console.log(`Code Font Family: ${results.codeFontFamily}`);
    
    if (results.codeFontFamily.includes('Source Code Pro')) {
      console.log('✅ Code correctly uses Source Code Pro');
    } else {
      console.log(`❌ Code uses incorrect font: ${results.codeFontFamily} (should include Source Code Pro)`);
    }
  }
  
  // Check color scheme
  const telekomElements = document.querySelectorAll('h1, h2');
  if (telekomElements.length > 0) {
    const colorStyle = window.getComputedStyle(telekomElements[0]);
    results.headlineColor = colorStyle.color;
    
    console.log(`Headline Color: ${results.headlineColor}`);
    
    // Convert to hex if needed for comparison
    const telekomOrange = 'rgb(217, 147, 28)'; // #D9931C in RGB
    if (results.headlineColor === telekomOrange) {
      console.log('✅ Headlines use correct Telekom Orange color');
    } else {
      console.log(`ℹ️ Headline color: ${results.headlineColor} (should be ${telekomOrange})`);
    }
  }
  
  console.log('\n=== Font Verification Summary ===');
  console.log('Expected:');
  console.log('- Headlines: Open Sans Regular (font-weight: 400) - NOT BOLD');
  console.log('- Content: Open Sans Light (font-weight: 300)');
  console.log('- Code: Source Code Pro');
  console.log('- Colors: Telekom Orange #D9931C');
  
  return results;
}

// Auto-run when script is loaded in browser
if (typeof window !== 'undefined') {
  // Wait for DOM and fonts to load
  window.addEventListener('load', function() {
    setTimeout(verifyTelekomTheme, 1000);
  });
}

// Export for Node.js usage
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { verifyTelekomTheme };
}