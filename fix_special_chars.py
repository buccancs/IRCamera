#!/usr/bin/env python3
"""
Fix special characters (\1, etc.) in source files that cause compilation errors.
"""

import glob
import re
from pathlib import Path


def fix_special_characters(filepath):
    """Fix special characters in a single file."""
    try:
        with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()

        # Check if file contains problematic characters
        if not re.search(r"\\1", content):
            return False

        original_content = content

        # Remove the \1 character (this is often a text editor artifact)
        content = re.sub(r"\\1", "", content)

        # Remove other problematic characters
        content = re.sub(r"[\x00-\x08\x0b\x0c\x0e-\x1f\x7f]", "", content)

        if content != original_content:
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(content)
            print(f"✓ Fixed special characters in: {filepath}")
            return True

    except Exception as e:
        print(f"Error processing {filepath}: {e}")
        return False

    return False


def main():
    """Main function to process all source files."""
    print("=== Fixing Special Characters in Source Files ===")

    # Define patterns for source files
    source_patterns = ["**/*.kt", "**/*.java"]

    # Exclude patterns
    exclude_patterns = [
        "**/migration_backup_*/**",
        "**/build/**",
        "**/.*/**",
        "**/.git/**",
    ]

    total_files = 0
    modified_files = 0

    for pattern in source_patterns:
        files = glob.glob(pattern, recursive=True)

        for filepath in files:
            # Skip excluded paths
            skip_file = False
            for exclude in exclude_patterns:
                if any(
                    part.startswith(exclude.replace("**/", "").replace("/**", ""))
                    for part in Path(filepath).parts
                ):
                    skip_file = True
                    break

            if skip_file:
                continue

            total_files += 1

            if fix_special_characters(filepath):
                modified_files += 1

    print("\n=== Special Character Fix Complete ===")
    print(f"Files processed: {total_files}")
    print(f"Files modified: {modified_files}")


if __name__ == "__main__":
    main()
