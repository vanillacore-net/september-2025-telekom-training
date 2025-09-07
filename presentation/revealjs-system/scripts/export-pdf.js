#!/usr/bin/env node

/**
 * PDF Export script using Puppeteer
 * Exports reveal.js presentations to PDF with proper text selection
 * Alternative to decktape with better text handling
 */

const puppeteer = require('puppeteer');
const fs = require('fs').promises;
const path = require('path');

const CONFIG = {
  presentationsDir: './presentations',
  outputDir: './pdf-exports',
  width: 1920,
  height: 1080,
  timeout: 30000,
  waitForSelector: '.reveal .slides',
  pdfOptions: {
    format: 'A4',
    landscape: true,
    printBackground: true,
    margin: {
      top: '10mm',
      bottom: '10mm',
      left: '10mm',
      right: '10mm'
    },
    preferCSSPageSize: false
  }
};

/**
 * Export single presentation to PDF
 */
async function exportToPDF(htmlFile, browser) {
  try {
    const baseName = path.basename(htmlFile, '.html');
    const outputPath = path.join(CONFIG.outputDir, `${baseName}.pdf`);
    
    console.log(`🔄 Exporting ${baseName}...`);
    
    const page = await browser.newPage();
    
    // Set viewport for consistent rendering
    await page.setViewport({
      width: CONFIG.width,
      height: CONFIG.height
    });
    
    // Load the presentation
    const filePath = `file://${path.resolve(htmlFile)}`;
    await page.goto(filePath, {
      waitUntil: 'networkidle0',
      timeout: CONFIG.timeout
    });
    
    // Wait for reveal.js to initialize
    await page.waitForSelector(CONFIG.waitForSelector, {
      timeout: CONFIG.timeout
    });
    
    // Add PDF-specific styles
    await page.addStyleTag({
      content: `
        .reveal .slides section {
          page-break-inside: avoid;
          break-inside: avoid;
        }
        
        .reveal .slides section[data-markdown] {
          display: block !important;
          opacity: 1 !important;
          transform: none !important;
        }
        
        .fragment {
          opacity: 1 !important;
        }
        
        .slide-logo {
          display: block !important;
          position: fixed !important;
          opacity: 0.8 !important;
        }
        
        @page {
          margin: 10mm;
        }
        
        /* Ensure code blocks don't break across pages */
        pre {
          page-break-inside: avoid;
          break-inside: avoid;
        }
        
        /* Better table handling */
        table {
          page-break-inside: avoid;
          break-inside: avoid;
        }
      `
    });
    
    // Execute JavaScript to prepare for PDF
    await page.evaluate(() => {
      // Make all fragments visible
      document.querySelectorAll('.fragment').forEach(fragment => {
        fragment.classList.add('visible');
      });
      
      // Ensure all slides are rendered
      if (window.Reveal) {
        window.Reveal.sync();
      }
      
      // Wait a moment for any animations
      return new Promise(resolve => setTimeout(resolve, 1000));
    });
    
    // Generate PDF
    await page.pdf({
      path: outputPath,
      ...CONFIG.pdfOptions
    });
    
    await page.close();
    
    console.log(`✅ Exported: ${outputPath}`);
    return outputPath;
    
  } catch (error) {
    console.error(`❌ Failed to export ${htmlFile}:`, error.message);
    return null;
  }
}

/**
 * Alternative export using print-pdf mode (reveal.js native)
 */
async function exportWithPrintPDF(htmlFile, browser) {
  try {
    const baseName = path.basename(htmlFile, '.html');
    const outputPath = path.join(CONFIG.outputDir, `${baseName}-print.pdf`);
    
    console.log(`🖨️  Print-PDF export: ${baseName}...`);
    
    const page = await browser.newPage();
    
    await page.setViewport({
      width: CONFIG.width,
      height: CONFIG.height
    });
    
    // Load with print-pdf parameter
    const filePath = `file://${path.resolve(htmlFile)}?print-pdf`;
    await page.goto(filePath, {
      waitUntil: 'networkidle0',
      timeout: CONFIG.timeout
    });
    
    // Wait for reveal.js to process print mode
    await page.waitForSelector('.reveal .slides', {
      timeout: CONFIG.timeout
    });
    
    // Wait for print-pdf processing
    await page.waitForFunction(() => {
      return window.Reveal && window.Reveal.isReady();
    }, { timeout: CONFIG.timeout });
    
    await page.pdf({
      path: outputPath,
      format: 'A4',
      landscape: true,
      printBackground: true,
      margin: { top: 0, bottom: 0, left: 0, right: 0 }
    });
    
    await page.close();
    
    console.log(`✅ Print-PDF exported: ${outputPath}`);
    return outputPath;
    
  } catch (error) {
    console.error(`❌ Failed print-pdf export ${htmlFile}:`, error.message);
    return null;
  }
}

/**
 * Export all presentations
 */
async function exportAll() {
  try {
    console.log('🚀 Starting PDF export process...');
    
    // Ensure output directory exists
    await fs.mkdir(CONFIG.outputDir, { recursive: true });
    
    // Find all HTML presentations
    const files = await fs.readdir(CONFIG.presentationsDir);
    const htmlFiles = files.filter(file => 
      file.endsWith('.html') && file !== 'index.html'
    );
    
    if (htmlFiles.length === 0) {
      console.log('❌ No presentation files found');
      return;
    }
    
    console.log(`📚 Found ${htmlFiles.length} presentations to export`);
    
    // Launch Puppeteer
    const browser = await puppeteer.launch({
      headless: 'new',
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-web-security',
        '--allow-file-access-from-files'
      ]
    });
    
    const results = [];
    const printResults = [];
    
    // Export each presentation (both methods)
    for (const file of htmlFiles) {
      const filePath = path.join(CONFIG.presentationsDir, file);
      
      // Standard export
      const result = await exportToPDF(filePath, browser);
      if (result) results.push(result);
      
      // Print-PDF export
      const printResult = await exportWithPrintPDF(filePath, browser);
      if (printResult) printResults.push(printResult);
    }
    
    await browser.close();
    
    // Create export summary
    const summaryContent = `# PDF Export Summary

Generated: ${new Date().toLocaleString('de-DE')}

## Standard PDF Exports (${results.length})
${results.map(file => `- ${path.basename(file)}`).join('\n')}

## Print-PDF Exports (${printResults.length})
${printResults.map(file => `- ${path.basename(file)}`).join('\n')}

## Usage Notes

**Standard PDFs**: Better text selection, proper styling, good for sharing
**Print-PDF**: Native reveal.js export, slide-optimized layout

**Recommended**: Use standard PDFs for document sharing, print-PDFs for slide-by-slide viewing.
`;
    
    const summaryPath = path.join(CONFIG.outputDir, 'export-summary.md');
    await fs.writeFile(summaryPath, summaryContent);
    
    console.log(`\n🎉 Export complete!`);
    console.log(`📁 Standard PDFs: ${results.length}`);
    console.log(`🖨️  Print PDFs: ${printResults.length}`);
    console.log(`📋 Summary: ${summaryPath}`);
    
  } catch (error) {
    console.error('❌ Export failed:', error);
    process.exit(1);
  }
}

/**
 * Export specific presentation
 */
async function exportOne(filename) {
  const filePath = path.join(CONFIG.presentationsDir, filename);
  
  try {
    await fs.access(filePath);
  } catch {
    console.error(`❌ File not found: ${filePath}`);
    return;
  }
  
  await fs.mkdir(CONFIG.outputDir, { recursive: true });
  
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });
  
  await exportToPDF(filePath, browser);
  await exportWithPrintPDF(filePath, browser);
  
  await browser.close();
}

// CLI interface
if (require.main === module) {
  const args = process.argv.slice(2);
  
  if (args.length === 0) {
    exportAll();
  } else if (args[0] === '--help' || args[0] === '-h') {
    console.log(`
PDF Export Tool for Telekom Presentations

Usage:
  node export-pdf.js              Export all presentations
  node export-pdf.js filename.html Export specific presentation
  node export-pdf.js --help       Show this help

Examples:
  node export-pdf.js day1.html
  node export-pdf.js intro.html
`);
  } else {
    exportOne(args[0]);
  }
}

module.exports = { exportAll, exportOne };