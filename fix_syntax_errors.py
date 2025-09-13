#!/usr/bin/env python3
"""
Fix syntax errors created by the batch mypy script
"""

import re
from pathlib import Path


def fix_parameter_syntax(file_path: Path):
    """Fix invalid parameter syntax like 'param -> Type: default'."""
    try:
        content = file_path.read_text(encoding="utf-8")
        original_content = content

        # Pattern to match invalid syntax: param -> Type: default
        pattern = r"(\w+) -> ([^:]+): ([^,)]+)"

        def fix_param(match):
            param_name = match.group(1).strip()
            param_type = match.group(2).strip()
            default_value = match.group(3).strip()

            return f"{param_name}: {param_type} = {default_value}"

        # Apply fix
        content = re.sub(pattern, fix_param, content)

        # Also fix cases without default values
        pattern2 = r"(\w+) -> ([^:,)]+):"

        def fix_param_no_default(match):
            param_name = match.group(1).strip()
            param_type = match.group(2).strip()

            # Check if this is actually a parameter (not a function definition)
            if param_name in ["de", "return", "i", "for", "while", "class"]:
                return match.group(0)

            return f"{param_name}: {param_type},"

        content = re.sub(pattern2, fix_param_no_default, content)

        if content != original_content:
            file_path.write_text(content, encoding="utf-8")
            print(f"Fixed syntax in {file_path}")
            return True

    except Exception as e:
        print(f"Error fixing {file_path}: {e}")

    return False


def main():
    """Fix syntax errors in all affected files."""
    print("🔧 Fixing parameter syntax errors...")

    # Find Python files with potential issues
    root_path = Path("pc-controller/src")

    files_fixed = 0
    for py_file in root_path.rglob("*.py"):
        if fix_parameter_syntax(py_file):
            files_fixed += 1

    print(f"✅ Fixed syntax in {files_fixed} files")


if __name__ == "__main__":
    main()
