#!/usr/bin/env node

/**
 * Build script to convert training markdown content to reveal.js presentations
 * Processes all training modules and creates individual presentations
 */

const fs = require('fs').promises;
const path = require('path');
const { marked } = require('marked');

const CONFIG = {
  scriptDir: '../../../script',
  templatesDir: './templates',
  presentationsDir: './presentations',
  templateFile: 'presentation-template.html'
};

/**
 * Parse markdown content and extract sections
 */
function parseMarkdownContent(content) {
  const sections = [];
  const lines = content.split('\n');
  let currentSection = [];
  let sectionTitle = '';
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    
    // Detect new sections (H1 or H2 headers)
    if (line.match(/^#{1,2}\s+/)) {
      // Save previous section
      if (currentSection.length > 0) {
        sections.push({
          title: sectionTitle,
          content: currentSection.join('\n')
        });
      }
      
      // Start new section
      sectionTitle = line.replace(/^#{1,2}\s+/, '');
      currentSection = [line];
    } else {
      currentSection.push(line);
    }
  }
  
  // Add last section
  if (currentSection.length > 0) {
    sections.push({
      title: sectionTitle,
      content: currentSection.join('\n')
    });
  }
  
  return sections;
}

/**
 * Convert markdown section to reveal.js slide
 */
function sectionToSlide(section) {
  let slide = `<section data-markdown>\n<textarea data-template>\n`;
  
  // Process content for reveal.js
  let content = section.content;
  
  // Convert code blocks to proper format
  content = content.replace(/```(\w+)?\n([\s\S]*?)```/g, (match, lang, code) => {
    lang = lang || 'text';
    return `\`\`\`${lang}\n${code}\`\`\``;
  });
  
  // Add speaker notes for complex sections
  if (content.includes('### ')) {
    const parts = content.split('### ');
    const mainContent = parts[0];
    const speakerNotes = parts.slice(1).join('\n### ');
    
    content = mainContent;
    if (speakerNotes.trim()) {
      content += '\n\nNote:\n' + speakerNotes;
    }
  }
  
  // Handle long content by splitting into multiple slides
  const contentParts = content.split('\n\n');
  if (contentParts.length > 8) {
    // Split into multiple slides
    const slideParts = [];
    let currentPart = [];
    
    for (const part of contentParts) {
      currentPart.push(part);
      if (currentPart.length >= 4 && part.includes('```')) {
        slideParts.push(currentPart.join('\n\n'));
        currentPart = [];
      }
    }
    
    if (currentPart.length > 0) {
      slideParts.push(currentPart.join('\n\n'));
    }
    
    return slideParts.map(part => 
      `<section data-markdown>\n<textarea data-template>\n${part}\n</textarea>\n</section>`
    ).join('\n\n');
  }
  
  slide += content;
  slide += '\n</textarea>\n</section>';
  
  return slide;
}

/**
 * Create section divider slide
 */
function createSectionDivider(title, subtitle = '') {
  return `<section data-markdown class="section-divider">
<textarea data-template>
# ${title}

${subtitle ? `## ${subtitle}` : ''}

---
</textarea>
</section>`;
}

/**
 * Build presentation from training module
 */
async function buildPresentation(moduleDir, moduleName) {
  try {
    const files = await fs.readdir(moduleDir);
    const mdFiles = files.filter(file => file.endsWith('.md')).sort();
    
    if (mdFiles.length === 0) {
      console.log(`No markdown files found in ${moduleDir}`);
      return;
    }
    
    const sections = [];
    
    // Add module introduction divider
    sections.push(createSectionDivider(moduleName.replace(/-/g, ' ').toUpperCase()));
    
    // Process each markdown file
    for (const file of mdFiles) {
      const filePath = path.join(moduleDir, file);
      const content = await fs.readFile(filePath, 'utf-8');
      const fileSections = parseMarkdownContent(content);
      
      // Add file divider if multiple files
      if (mdFiles.length > 1) {
        const fileTitle = file.replace(/^\d+-/, '').replace(/\.md$/, '').replace(/-/g, ' ');
        sections.push(createSectionDivider(fileTitle, 'Practical Implementation'));
      }
      
      // Convert sections to slides
      for (const section of fileSections) {
        if (section.content.trim()) {
          sections.push(sectionToSlide(section));
        }
      }
    }
    
    // Load template
    const templatePath = path.join(CONFIG.templatesDir, CONFIG.templateFile);
    const template = await fs.readFile(templatePath, 'utf-8');
    
    // Generate presentation
    const title = moduleName.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    const subtitle = 'Design Patterns & Architecture';
    const date = new Date().toLocaleDateString('de-DE');
    
    const presentation = template
      .replace(/\{\{TITLE\}\}/g, title)
      .replace(/\{\{SUBTITLE\}\}/g, subtitle)
      .replace(/\{\{DATE\}\}/g, date)
      .replace(/\{\{CONTENT_SECTIONS\}\}/g, sections.join('\n\n'));
    
    // Write presentation file
    const outputPath = path.join(CONFIG.presentationsDir, `${moduleName}.html`);
    await fs.writeFile(outputPath, presentation, 'utf-8');
    
    console.log(`✅ Built presentation: ${outputPath}`);
    return outputPath;
    
  } catch (error) {
    console.error(`Error building presentation for ${moduleName}:`, error);
  }
}

/**
 * Main build process
 */
async function buildAll() {
  try {
    // Ensure output directory exists
    await fs.mkdir(CONFIG.presentationsDir, { recursive: true });
    
    // Find all training modules
    const scriptPath = path.resolve(CONFIG.scriptDir);
    const dirs = await fs.readdir(scriptPath);
    const moduleDirs = [];
    
    for (const dir of dirs) {
      const dirPath = path.join(scriptPath, dir);
      const stat = await fs.stat(dirPath);
      if (stat.isDirectory()) {
        moduleDirs.push({ name: dir, path: dirPath });
      }
    }
    
    console.log(`Found ${moduleDirs.length} training modules`);
    
    // Build presentations for each module
    const presentations = [];
    for (const module of moduleDirs) {
      const result = await buildPresentation(module.path, module.name);
      if (result) {
        presentations.push(result);
      }
    }
    
    // Create index.html with links to all presentations
    const indexContent = `<!doctype html>
<html>
<head>
  <title>Telekom Architecture Training - Presentations</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="../css/telekom-theme.css">
  <style>
    body { 
      font-family: 'Source Sans Pro', Arial, sans-serif; 
      max-width: 800px; 
      margin: 0 auto; 
      padding: 40px;
      background: #ffffff;
    }
    h1 { color: #FF6B00; }
    .presentation-list { list-style: none; padding: 0; }
    .presentation-list li { 
      margin: 20px 0; 
      padding: 15px; 
      background: #f5f5f5; 
      border-radius: 8px;
      border-left: 4px solid #FF6B00;
    }
    .presentation-list a { 
      text-decoration: none; 
      color: #333; 
      font-weight: 600;
      font-size: 18px;
    }
    .presentation-list a:hover { color: #FF6B00; }
    .actions { margin: 30px 0; }
    .btn {
      display: inline-block;
      padding: 12px 24px;
      margin: 5px;
      background: #FF6B00;
      color: white;
      text-decoration: none;
      border-radius: 6px;
      font-weight: 600;
    }
    .btn:hover { background: #E55100; }
  </style>
</head>
<body>
  <h1>🎯 Telekom Architecture Training Presentations</h1>
  
  <div class="actions">
    <a href="../scripts/export-pdf.js" class="btn">📄 Export All to PDF</a>
    <a href="../docker/" class="btn">🐳 Docker Deploy</a>
  </div>
  
  <ul class="presentation-list">
${presentations.map(file => {
  const name = path.basename(file, '.html');
  const displayName = name.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
  return `    <li><a href="${path.basename(file)}">${displayName}</a></li>`;
}).join('\n')}
  </ul>
  
  <p><strong>Built:</strong> ${new Date().toLocaleString('de-DE')}</p>
</body>
</html>`;
    
    const indexPath = path.join(CONFIG.presentationsDir, 'index.html');
    await fs.writeFile(indexPath, indexContent);
    
    console.log(`\n🎉 Build complete! Created ${presentations.length} presentations`);
    console.log(`📋 Index: ${indexPath}`);
    
  } catch (error) {
    console.error('Build failed:', error);
    process.exit(1);
  }
}

// Run build if called directly
if (require.main === module) {
  buildAll();
}

module.exports = { buildAll, buildPresentation };