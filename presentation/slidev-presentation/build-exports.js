import { exec } from 'child_process';
import { promisify } from 'util';
import { mkdirSync, existsSync } from 'fs';

const execAsync = promisify(exec);

const DIST_DIR = './dist';
const EXPORTS_DIR = './exports';

// Ensure directories exist
mkdirSync(DIST_DIR, { recursive: true });
mkdirSync(EXPORTS_DIR, { recursive: true });

async function runCommand(command, description) {
  console.log(`\n🔄 ${description}...`);
  try {
    const { stdout, stderr } = await execAsync(command);
    if (stdout) console.log(stdout);
    if (stderr && !stderr.includes('Warning')) console.error(stderr);
    console.log(`✅ ${description} completed successfully`);
    return true;
  } catch (error) {
    console.error(`❌ ${description} failed:`, error.message);
    return false;
  }
}

async function buildExports() {
  console.log('🚀 Building Slidev exports...\n');
  
  // Build static HTML version
  const htmlSuccess = await runCommand(
    'npx slidev build --base /telekom-architecture/ --out dist/html',
    'Building static HTML export'
  );
  
  // Export to PDF
  const pdfSuccess = await runCommand(
    'npx slidev export --pdf --output exports/design-patterns-workshop.pdf --timeout 60000',
    'Exporting to PDF'
  );
  
  // Export to PowerPoint (PPTX) - if supported
  const pptxSuccess = await runCommand(
    'npx slidev export --pptx --output exports/design-patterns-workshop.pptx --timeout 60000',
    'Exporting to PowerPoint (PPTX)'
  );
  
  // Build SPA version
  const spaSuccess = await runCommand(
    'npx slidev build --spa --out dist/spa',
    'Building SPA version'
  );
  
  console.log('\n📊 Export Summary:');
  console.log(`• HTML Export: ${htmlSuccess ? '✅ SUCCESS' : '❌ FAILED'}`);
  console.log(`• PDF Export: ${pdfSuccess ? '✅ SUCCESS' : '❌ FAILED'}`);
  console.log(`• PPTX Export: ${pptxSuccess ? '✅ SUCCESS' : '❌ FAILED'}`);
  console.log(`• SPA Export: ${spaSuccess ? '✅ SUCCESS' : '❌ FAILED'}`);
  
  if (htmlSuccess) {
    console.log('\n📂 HTML Export Location: ./dist/html/');
    console.log('   📄 Open ./dist/html/index.html in browser');
  }
  
  if (pdfSuccess) {
    console.log('\n📂 PDF Export Location: ./exports/design-patterns-workshop.pdf');
  }
  
  if (pptxSuccess) {
    console.log('\n📂 PPTX Export Location: ./exports/design-patterns-workshop.pptx');
  }
  
  if (spaSuccess) {
    console.log('\n📂 SPA Export Location: ./dist/spa/');
    console.log('   🌐 Can be served from any web server');
  }
  
  console.log('\n🎉 Build process completed!');
  console.log('\n📋 Usage Instructions:');
  console.log('• Development: npm run serve');
  console.log('• View exports: Open files in ./dist/ and ./exports/');
  console.log('• Screenshots: Available in ./screenshots/');
}

buildExports().catch(console.error);