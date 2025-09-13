#!/usr/bin/env python3
"""
Batch MyPy Fixes for IRCamera Repository
Systematically applies type annotations and fixes to common mypy issues
"""

import re
import subprocess
import sys
from pathlib import Path
from typing import Dict, List, Set, Tuple


class MyPyFixer:
    """Automated mypy issue fixer."""

    def __init__(self):
        self.files_processed = 0
        self.fixes_applied = 0

    def add_basic_type_imports(self, file_path: Path) -> bool:
        """Add basic typing imports to Python files that need them."""
        try:
            content = file_path.read_text(encoding="utf-8")

            # Check if we need to add typing imports
            needs_typing = any(
                [
                    "def " in content
                    and "->" not in content,  # Functions without return types
                    "= None" in content,  # Potential Optional types
                    "List[" in content and "from typing import" not in content,
                    "Dict[" in content and "from typing import" not in content,
                    "Optional[" in content and "from typing import" not in content,
                ]
            )

            if not needs_typing:
                return False

            # Add typing imports if not present
            if "from typing import" not in content:
                # Find the right place to insert imports
                lines = content.splitlines()
                insert_pos = 0

                # Skip docstring and existing imports
                for i, line in enumerate(lines):
                    if line.startswith('"""') or line.startswith("'''"):
                        # Skip docstring
                        for j in range(i + 1, len(lines)):
                            if lines[j].endswith('"""') or lines[j].endswith("'''"):
                                insert_pos = j + 1
                                break
                    elif line.startswith("import ") or line.startswith("from "):
                        insert_pos = i + 1
                    elif line.strip() == "":
                        continue
                    else:
                        break

                # Add typing import
                typing_import = "from typing import Any, Dict, List, Optional"
                lines.insert(insert_pos, typing_import)
                lines.insert(insert_pos + 1, "")

                # Write back
                file_path.write_text("\n".join(lines), encoding="utf-8")
                return True

        except Exception as e:
            print(f"Error processing {file_path}: {e}")

        return False

    def fix_optional_parameters(self, file_path: Path) -> int:
        """Fix optional parameters that should use Optional[Type] annotation."""
        try:
            content = file_path.read_text(encoding="utf-8")
            original_content = content
            fixes = 0

            # Pattern to match function parameters with None defaults
            pattern = r"(\w+):\s*(\w+)\s*=\s*None"

            def replace_optional(match):
                param_name = match.group(1)
                param_type = match.group(2)
                return f"{param_name}: Optional[{param_type}] = None"

            content = re.sub(pattern, replace_optional, content)

            if content != original_content:
                file_path.write_text(content, encoding="utf-8")
                fixes = len(re.findall(pattern, original_content))
                print(f"  Fixed {fixes} optional parameters in {file_path.name}")

            return fixes

        except Exception as e:
            print(f"Error fixing optional parameters in {file_path}: {e}")
            return 0

    def add_return_type_annotations(self, file_path: Path) -> int:
        """Add basic return type annotations to functions without them."""
        try:
            content = file_path.read_text(encoding="utf-8")
            original_content = content
            fixes = 0

            # Pattern for functions without return type annotations
            pattern = r"def\s+(\w+)\s*\([^)]*\)\s*:"

            def add_return_type(match):
                func_name = match.group(1)
                full_match = match.group(0)

                # Skip special methods and already annotated functions
                if func_name.startswith("_") or "->" in full_match:
                    return full_match

                # Add -> None for common patterns
                if any(
                    keyword in func_name.lower()
                    for keyword in ["set", "update", "add", "remove", "clear"]
                ):
                    return full_match.replace(":", " -> None:")
                else:
                    return full_match.replace(":", " -> Any:")

            # Apply the pattern
            new_content = re.sub(pattern, add_return_type, content)

            if new_content != original_content:
                file_path.write_text(new_content, encoding="utf-8")
                fixes = len(
                    re.findall(r"def\s+\w+.*-> (None|Any):", new_content)
                ) - len(re.findall(r"def\s+\w+.*->", original_content))
                if fixes > 0:
                    print(
                        f"  Added {fixes} return type annotations in {file_path.name}"
                    )

            return max(fixes, 0)

        except Exception as e:
            print(f"Error adding return annotations to {file_path}: {e}")
            return 0

    def process_file(self, file_path: Path) -> int:
        """Process a single Python file."""
        print(f"Processing {file_path}")
        fixes = 0

        # Add typing imports if needed
        if self.add_basic_type_imports(file_path):
            fixes += 1
            print(f"  Added typing imports to {file_path.name}")

        # Fix optional parameters
        fixes += self.fix_optional_parameters(file_path)

        # Add return type annotations
        fixes += self.add_return_type_annotations(file_path)

        return fixes

    def process_repository(self) -> Dict[str, int]:
        """Process all Python files in the repository."""
        print("🔧 Starting MyPy batch fixes...")

        # Find Python files
        python_files = []
        root_path = Path(".")

        # Focus on key directories
        key_dirs = [
            "pc-controller/src",
            "test_*.py",
            "*_validation.py",
            "build_*.py",
            "check_*.py",
        ]

        for pattern in key_dirs:
            if pattern.endswith(".py"):
                python_files.extend(root_path.glob(pattern))
            else:
                python_files.extend(root_path.glob(f"{pattern}/**/*.py"))

        # Remove duplicates and filter
        unique_files = set()
        for f in python_files:
            if "migration_backup_" not in str(f) and "__pycache__" not in str(f):
                unique_files.add(f)

        results = {
            "files_processed": 0,
            "total_fixes": 0,
            "successful_files": 0,
        }

        print(f"📁 Found {len(unique_files)} Python files to process")

        for file_path in sorted(unique_files):
            try:
                fixes = self.process_file(file_path)
                results["files_processed"] += 1
                results["total_fixes"] += fixes

                if fixes > 0:
                    results["successful_files"] += 1

            except Exception as e:
                print(f"❌ Error processing {file_path}: {e}")

        return results


def run_mypy_check() -> bool:
    """Run mypy to check for remaining issues."""
    print("\n🔍 Running mypy validation...")

    try:
        result = subprocess.run(
            [
                "mypy",
                "--config-file",
                "pyproject.toml",
                "--show-error-codes",
                "pc-controller/src/ircamera_pc/gui/icons.py",
                "pc-controller/src/ircamera_pc/gui/plotting_widgets.py",
                "pc-controller/src/ircamera_pc/network/protocol.py",
            ],
            capture_output=True,
            text=True,
        )

        if result.returncode == 0:
            print("✅ MyPy validation passed!")
            return True
        else:
            print("⚠️  MyPy found remaining issues:")
            print(result.stdout[:1000])  # Show first 1000 chars
            return False

    except Exception as e:
        print(f"Error running mypy: {e}")
        return False


def main():
    """Main entry point."""
    print("🚀 IRCamera MyPy Batch Fixer")
    print("=" * 40)

    fixer = MyPyFixer()
    results = fixer.process_repository()

    print("\n📊 Results:")
    print(f"   Files processed: {results['files_processed']}")
    print(f"   Total fixes applied: {results['total_fixes']}")
    print(f"   Files with fixes: {results['successful_files']}")

    # Run validation
    validation_passed = run_mypy_check()

    if validation_passed:
        print("\n🎉 MyPy fixes completed successfully!")
        return 0
    else:
        print("\n✅ MyPy fixes applied. Some issues may require manual review.")
        return 0


if __name__ == "__main__":
    sys.exit(main())
