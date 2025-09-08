#!/usr/bin/env python3
"""
Screenshot capture script for presentation tools comparison
"""

import os
import time
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

def setup_driver():
    """Setup Chrome driver with appropriate options"""
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    options.add_argument('--window-size=1920,1080')
    
    try:
        driver = webdriver.Chrome(options=options)
        return driver
    except Exception as e:
        print(f"Error setting up Chrome driver: {e}")
        return None

def capture_presentation(url, tool_name, output_dir):
    """Capture screenshots of a presentation tool"""
    print(f"\n=== Capturing {tool_name} ===")
    
    driver = setup_driver()
    if not driver:
        print(f"Failed to setup driver for {tool_name}")
        return False
    
    try:
        # Navigate to the presentation
        print(f"Loading {url}")
        driver.get(url)
        
        # Wait for the page to load
        time.sleep(3)
        
        # Take screenshot of title slide
        title_path = f"{output_dir}/{tool_name.lower()}-slide-1-title.png"
        driver.save_screenshot(title_path)
        print(f"Saved: {title_path}")
        
        # Try to navigate to different slides based on tool
        if tool_name.lower() == 'slidev':
            # Slidev uses arrow keys or space
            driver.find_element(By.TAG_NAME, 'body').click()
            time.sleep(0.5)
            
            # Navigate through slides
            for slide_num in range(2, 7):
                driver.execute_script("window.dispatchEvent(new KeyboardEvent('keydown', {'key': 'ArrowRight'}))")
                time.sleep(1)
                screenshot_path = f"{output_dir}/{tool_name.lower()}-slide-{slide_num}.png"
                driver.save_screenshot(screenshot_path)
                print(f"Saved: {screenshot_path}")
                
        elif tool_name.lower() == 'reveal-md':
            # RevealJS uses arrow keys
            body = driver.find_element(By.TAG_NAME, 'body')
            body.click()
            time.sleep(0.5)
            
            for slide_num in range(2, 7):
                driver.execute_script("Reveal.next()")
                time.sleep(1)
                screenshot_path = f"{output_dir}/{tool_name.lower()}-slide-{slide_num}.png"
                driver.save_screenshot(screenshot_path)
                print(f"Saved: {screenshot_path}")
                
        elif tool_name.lower() == 'mdx-deck':
            # MDX-deck uses arrow keys
            body = driver.find_element(By.TAG_NAME, 'body')
            body.click()
            time.sleep(0.5)
            
            for slide_num in range(2, 7):
                driver.execute_script("window.dispatchEvent(new KeyboardEvent('keydown', {'key': 'ArrowRight'}))")
                time.sleep(1)
                screenshot_path = f"{output_dir}/{tool_name.lower()}-slide-{slide_num}.png"
                driver.save_screenshot(screenshot_path)
                print(f"Saved: {screenshot_path}")
        
        return True
        
    except Exception as e:
        print(f"Error capturing {tool_name}: {e}")
        return False
        
    finally:
        driver.quit()

def capture_marp_current():
    """Capture current Marp implementation"""
    print("\n=== Capturing Current Marp Implementation ===")
    
    marp_html_path = "/Users/karsten/Nextcloud/Work/Training/2025-09_Telekom_Architecture/presentation/marp-presentation/dist/index.html"
    
    if not os.path.exists(marp_html_path):
        print(f"Marp HTML not found at {marp_html_path}")
        return False
    
    driver = setup_driver()
    if not driver:
        print("Failed to setup driver for Marp")
        return False
    
    try:
        # Load the HTML file
        driver.get(f"file://{marp_html_path}")
        time.sleep(3)
        
        # Capture first slide
        screenshot_path = f"{OUTPUT_DIR}/marp-current-slide-1.png"
        driver.save_screenshot(screenshot_path)
        print(f"Saved: {screenshot_path}")
        
        # Try to navigate through slides (Marp uses arrow keys)
        body = driver.find_element(By.TAG_NAME, 'body')
        body.click()
        time.sleep(0.5)
        
        for slide_num in range(2, 7):
            driver.execute_script("window.dispatchEvent(new KeyboardEvent('keydown', {'key': 'ArrowRight'}))")
            time.sleep(1)
            screenshot_path = f"{OUTPUT_DIR}/marp-current-slide-{slide_num}.png"
            driver.save_screenshot(screenshot_path)
            print(f"Saved: {screenshot_path}")
        
        return True
        
    except Exception as e:
        print(f"Error capturing Marp: {e}")
        return False
        
    finally:
        driver.quit()

# Configuration
OUTPUT_DIR = "/Users/karsten/Nextcloud/Work/Training/2025-09_Telekom_Architecture/presentation/research/screenshots"

# Ensure output directory exists
os.makedirs(OUTPUT_DIR, exist_ok=True)

def main():
    """Main function to capture all presentations"""
    print("Starting presentation tool screenshot capture...")
    
    # URLs to capture
    presentations = [
        ("http://localhost:3001", "Slidev"),
        ("http://localhost:3002", "Reveal-MD"), 
        ("http://localhost:3003", "MDX-Deck")
    ]
    
    # Capture current Marp first
    capture_marp_current()
    
    # Capture other tools
    for url, tool_name in presentations:
        try:
            capture_presentation(url, tool_name, OUTPUT_DIR)
        except Exception as e:
            print(f"Failed to capture {tool_name}: {e}")
        
        # Small delay between tools
        time.sleep(2)
    
    print(f"\nAll screenshots saved to: {OUTPUT_DIR}")
    print("Screenshot capture complete!")

if __name__ == "__main__":
    main()