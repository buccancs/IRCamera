#!/usr/bin/env python3

import os
import re
import glob

def fix_syntax_errors():
    """Fix common syntax errors in thermal component files"""
    
    thermal_dir = "component/thermal/src/main/java/com/topdon/module/thermal"
    
    # Find all Kotlin files
    kotlin_files = []
    for root, dirs, files in os.walk(thermal_dir):
        for file in files:
            if file.endswith('.kt'):
                kotlin_files.append(os.path.join(root, file))
    
    print(f"Found {len(kotlin_files)} Kotlin files to process")
    
    for file_path in kotlin_files:
        print(f"Processing: {file_path}")
        
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Fix common syntax issues
        
        # 1. Fix loose Chinese comments that should be proper comments
        content = re.sub(r'^([^/\n]*)([\u4e00-\u9fff]+.*?)(\s*$)', r'\1// \2\3', content, flags=re.MULTILINE)
        
        # 2. Fix TODO comments with improper line breaks
        content = re.sub(r'// (.+) // TODO: Review this (line|comment)', r'// \1', content)
        
        # 3. Fix Chinese text outside of strings/comments
        patterns_to_fix = [
            (r'^(\s*)([^"/\n]*[\u4e00-\u9fff][^"/\n]*)\s*$', r'\1// \2'),
            (r'^(\s*)([^"/\n]*[\u4e00-\u9fff][^"/\n]*)\s*(\w+)', r'\1// \2\n\1\3'),
        ]
        
        for pattern, replacement in patterns_to_fix:
            content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
        
        # 4. Fix malformed when statements (add missing case labels)
        # This is more complex and needs manual review, but we can detect them
        if 'when (' in content and content.count('{') != content.count('}'):
            print(f"  WARNING: Potential when statement issue in {file_path}")
        
        # 5. Fix loose statements that should be in functions/classes
        lines = content.split('\n')
        fixed_lines = []
        in_class = False
        in_function = False
        brace_count = 0
        
        for i, line in enumerate(lines):
            stripped = line.strip()
            
            # Track class/function context
            if 'class ' in line and not line.strip().startswith('//'):
                in_class = True
            elif 'fun ' in line and not line.strip().startswith('//'):
                in_function = True
            
            # Count braces to track scope
            brace_count += line.count('{') - line.count('}')
            
            # Check for loose statements
            if (not in_class and not in_function and 
                stripped and not stripped.startswith('//') and 
                not stripped.startswith('package') and 
                not stripped.startswith('import') and
                '=' in stripped and 
                brace_count == 0):
                # This might be a loose statement - comment it out
                fixed_lines.append('    // ' + line)
                print(f"  Fixed loose statement at line {i+1}: {stripped[:50]}...")
            else:
                fixed_lines.append(line)
        
        content = '\n'.join(fixed_lines)
        
        # Write back if changes were made
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  ✅ Fixed syntax issues in {file_path}")
        else:
            print(f"  ➡️ No changes needed in {file_path}")

if __name__ == "__main__":
    fix_syntax_errors()
    print("Syntax fixing complete!")