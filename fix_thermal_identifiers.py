#!/usr/bin/env python3

"""
Fix standalone identifier issues in thermal fragment files
"""

import os

def fix_thermal_files():
    """Fix standalone identifiers that are causing compilation errors"""
    
    files_to_fix = [
        "component/thermal/src/main/java/com/topdon/module/thermal/fragment/MonitorThermalFragment.kt",
        "component/thermal/src/main/java/com/topdon/module/thermal/fragment/ThermalFragment.kt"
    ]
    
    # Dictionary of problematic identifiers and their fixes
    identifier_fixes = {
        'startRecord': '// startRecord - method call converted to comment',
        'stopRecord': '// stopRecord - method call converted to comment', 
        'getpoint': '// getpoint - method call converted to comment',
        'getline': '// getline - method call converted to comment',
        'getarea': '// getarea - method call converted to comment',
        'refreshimage': '// refreshimage - method call converted to comment',
        'recording': '// recording - method call converted to comment',
        'addpoint': '// addpoint - method call converted to comment',
        'addline': '// addline - method call converted to comment',
        'addtemperature': '// addtemperature - method call converted to comment',
        'setpseudo-color': '// setpseudo-color - method call converted to comment',
        'rotation': '// rotation - method call converted to comment',
        'imageEnhance': '// imageEnhance - method call converted to comment',
        'disabled': '// disabled - method call converted to comment',
        'Open': '// Open - method call converted to comment'
    }
    
    for file_path in files_to_fix:
        if not os.path.exists(file_path):
            print(f"Warning: {file_path} not found")
            continue
            
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # Fix standalone identifiers at the beginning of lines
            for identifier, replacement in identifier_fixes.items():
                # Match identifier at start of line (with optional whitespace)
                import re
                pattern = f'^(\s*){re.escape(identifier)}(\s*)$'
                content = re.sub(pattern, f'\\1{replacement}\\2', content, flags=re.MULTILINE)
                
                # Also match cases where it's followed by method call syntax but broken
                pattern = f'^(\s*){re.escape(identifier)}(\.[a-zA-Z_][a-zA-Z0-9_]*)?(\s*)$'
                content = re.sub(pattern, f'\\1{replacement}\\3', content, flags=re.MULTILINE)
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ Fixed identifiers in {file_path}")
            else:
                print(f"✓ No changes needed in {file_path}")
                
        except Exception as e:
            print(f"❌ Error processing {file_path}: {e}")

if __name__ == "__main__":
    print("🔧 Fixing thermal fragment identifier issues...")
    fix_thermal_files()
    print("✅ Thermal fragment fixes complete!")