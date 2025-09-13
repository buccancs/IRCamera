#!/usr/bin/env python3

"""
Fix ThermalEntity property assignment issues in LogViewModel.kt
"""

import re

def fix_thermal_properties():
    file_path = "component/thermal/src/main/java/com/topdon/module/thermal/viewmodel/LogViewModel.kt"
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace .apply { } blocks with direct property assignments
    pattern = r'val entity = ThermalEntity\(\)\.apply \{([^}]+)\}\s*bean\.dataList\.add\(entity\)'
    
    def replace_apply_block(match):
        properties = match.group(1).strip()
        lines = [line.strip() for line in properties.split('\n') if line.strip()]
        
        result = "val entity = ThermalEntity()\n"
        for line in lines:
            if '=' in line and not line.startswith('//'):
                result += f"                            entity.{line}\n"
        result += "                            bean.dataList.add(entity)"
        return result
    
    # Apply the replacement
    content = re.sub(pattern, replace_apply_block, content, flags=re.DOTALL)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("✅ Fixed ThermalEntity property assignment issues")

if __name__ == "__main__":
    fix_thermal_properties()