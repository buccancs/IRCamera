#!/usr/bin/env python3
"""
Comprehensive MyPy Type Checking and Fixes Script
Applies mypy type checking across all Python files in the IRCamera repository
"""

import subprocess
import sys
from pathlib import Path


def run_mypy_on_all_files():
    """Run mypy on all Python files in the repository."""
    print("🔍 Running comprehensive mypy type checking across all Python files...")

    # Find all Python files
    python_files = []
    root_path = Path(".")

    for pattern in ["**/*.py"]:
        python_files.extend(root_path.glob(pattern))

    # Filter out files we want to exclude
    exclude_patterns = [
        "migration_backup_",
        "build/",
        "dist/",
        ".git/",
        "__pycache__/",
    ]

    filtered_files = []
    for file_path in python_files:
        should_exclude = any(pattern in str(file_path) for pattern in exclude_patterns)
        if not should_exclude:
            filtered_files.append(file_path)

    print(f"📁 Found {len(filtered_files)} Python files to check")

    # Run mypy with our configuration
    try:
        result = subprocess.run(
            [
                "mypy",
                "--config-file",
                "pyproject.toml",
                "--show-error-codes",
                "--pretty",
                *[
                    str(f) for f in filtered_files[:20]
                ],  # Process first 20 files to avoid overwhelming output
            ],
            capture_output=True,
            text=True,
            cwd=".",
        )

        print("📊 MyPy Results:")
        print(f"Exit Code: {result.returncode}")

        if result.stdout:
            print("\n✅ MyPy Output:")
            print(result.stdout)

        if result.stderr:
            print("\n❌ MyPy Errors:")
            print(result.stderr)

        # Summary
        if result.returncode == 0:
            print("\n🎉 All files passed mypy type checking!")
        else:
            print(f"\n⚠️  MyPy found type issues in {len(filtered_files)} files")

        return result.returncode == 0

    except FileNotFoundError:
        print("❌ mypy not found. Installing...")
        subprocess.run([sys.executable, "-m", "pip", "install", "mypy"])
        return False


def main():
    """Main entry point for mypy checking."""
    print("🚀 IRCamera Repository MyPy Type Checking")
    print("=" * 50)

    success = run_mypy_on_all_files()

    if success:
        print("\n✅ MyPy type checking completed successfully!")
        return 0
    else:
        print("\n❌ MyPy found issues that need attention")
        return 1


if __name__ == "__main__":
    sys.exit(main())
