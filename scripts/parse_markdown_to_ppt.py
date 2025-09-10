#!/usr/bin/env python3
"""
Script to parse the HedgeDoc markdown presentation and convert it to PowerPoint format.
This script creates a structured representation that can be used to generate PowerPoint slides.
"""

import re
import yaml
from pathlib import Path
from dataclasses import dataclass
from typing import List, Optional, Dict, Any

@dataclass
class SlideContent:
    title: str
    content: List[str]
    speaker_notes: Optional[str] = None
    fragments: List[Dict[str, Any]] = None
    slide_type: str = "content"  # title, content, code
    
    def __post_init__(self):
        if self.fragments is None:
            self.fragments = []

class MarkdownToPPTParser:
    def __init__(self, markdown_file: Path):
        self.markdown_file = markdown_file
        self.slides = []
        
    def parse(self) -> List[SlideContent]:
        """Parse the markdown file and return structured slide content"""
        with open(self.markdown_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Split by slide separators
        slide_sections = content.split('---')
        
        for i, section in enumerate(slide_sections):
            section = section.strip()
            if not section:
                continue
                
            # Skip YAML frontmatter
            if i == 0 and section.startswith('<!--'):
                continue
                
            slide = self._parse_slide_section(section)
            if slide:
                self.slides.append(slide)
        
        return self.slides
    
    def _parse_slide_section(self, section: str) -> Optional[SlideContent]:
        """Parse individual slide section"""
        lines = section.split('\n')
        
        # Find title (first # or ##)
        title = ""
        content_lines = []
        speaker_notes = ""
        in_notes = False
        fragments = []
        
        for line in lines:
            line = line.strip()
            
            # Check for speaker notes
            if line.startswith('Note:'):
                in_notes = True
                speaker_notes = line[5:].strip()
                continue
            elif in_notes and line:
                speaker_notes += "\n" + line
                continue
            elif in_notes and not line:
                in_notes = False
                continue
            
            # Skip empty lines and HTML div tags
            if not line or line.startswith('<div') or line.startswith('</div>'):
                continue
            
            # Extract title
            if line.startswith('#') and not title:
                title = re.sub(r'^#+\s*', '', line)
                continue
            
            # Check for fragments
            if '<!-- .element: class="fragment"' in line:
                fragment_match = re.search(r'data-fragment-index="(\d+)"', line)
                index = int(fragment_match.group(1)) if fragment_match else len(fragments)
                clean_line = re.sub(r'\s*<!-- .element:.*? -->', '', line)
                fragments.append({
                    'content': clean_line,
                    'index': index
                })
                content_lines.append(clean_line)
            else:
                content_lines.append(line)
        
        if not title and not content_lines:
            return None
        
        # Determine slide type
        slide_type = "content"
        if any("```" in line for line in content_lines):
            slide_type = "code"
        elif title and len(content_lines) <= 2:
            slide_type = "title"
        
        return SlideContent(
            title=title,
            content=[line for line in content_lines if line],
            speaker_notes=speaker_notes if speaker_notes else None,
            fragments=fragments,
            slide_type=slide_type
        )

def main():
    """Main function to parse markdown and generate slide structure"""
    markdown_path = Path("/Users/karsten/Nextcloud/Work/Training/2025-09_Telekom_Architecture/presentations/powerpoint/hedgedoc-intro.md")
    
    parser = MarkdownToPPTParser(markdown_path)
    slides = parser.parse()
    
    print(f"Parsed {len(slides)} slides:")
    for i, slide in enumerate(slides, 1):
        print(f"\nSlide {i}: {slide.title}")
        print(f"Type: {slide.slide_type}")
        print(f"Content lines: {len(slide.content)}")
        print(f"Fragments: {len(slide.fragments)}")
        if slide.speaker_notes:
            print(f"Has speaker notes: {len(slide.speaker_notes)} chars")
        if slide.content:
            print("First content line:", slide.content[0][:100] + "..." if len(slide.content[0]) > 100 else slide.content[0])

if __name__ == "__main__":
    main()