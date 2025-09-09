#!/usr/bin/env python3
"""
Fix reveal timing by adding data-fragment-index to all fragment elements
"""

import re
import os
import glob

def fix_fragments_in_file(file_path):
    """Fix all fragment timing in a single file"""
    print(f"Processing {file_path}...")
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Track changes
    changes = 0
    
    # Split content into slides
    slides = content.split('\n---\n')
    
    for i, slide in enumerate(slides):
        # Find all fragments in this slide that don't have data-fragment-index
        fragment_pattern = r'<!-- \.element: class="fragment"(?! data-fragment-index) -->'
        fragments = re.findall(fragment_pattern, slide)
        
        if fragments:
            # Add sequential indexing to fragments in this slide
            index = 1
            def add_index(match):
                nonlocal index
                result = f'<!-- .element: class="fragment" data-fragment-index="{index}" -->'
                index += 1
                return result
            
            slides[i] = re.sub(fragment_pattern, add_index, slide)
            changes += len(fragments)
    
    # Also fix fragments that are part of list items and other elements
    content = '\n---\n'.join(slides)
    
    # Fix list item fragments without index
    list_fragment_pattern = r'(- .+?) <!-- \.element: class="fragment"(?! data-fragment-index) -->'
    
    # Split by slides again and process each slide independently  
    slides = content.split('\n---\n')
    
    for i, slide in enumerate(slides):
        matches = list(re.finditer(list_fragment_pattern, slide))
        if matches:
            # Process in reverse order to maintain positions
            for j, match in enumerate(reversed(matches)):
                index = len(matches) - j
                replacement = f'{match.group(1)} <!-- .element: class="fragment" data-fragment-index="{index}" -->'
                slide = slide[:match.start()] + replacement + slide[match.end():]
                changes += 1
            slides[i] = slide
    
    content = '\n---\n'.join(slides)
    
    if changes > 0:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"  Fixed {changes} fragments in {os.path.basename(file_path)}")
    else:
        print(f"  No fragments to fix in {os.path.basename(file_path)}")

def main():
    # Get all presentation files (both hedgedoc and original)
    files = glob.glob('presentation/hedgedoc/hedgedoc-day*.md') + glob.glob('presentation/day*.md')
    
    print("Fixing reveal timing in presentation files...")
    
    for file_path in files:
        fix_fragments_in_file(file_path)
    
    print("\nFragment timing fix completed!")

if __name__ == '__main__':
    main()