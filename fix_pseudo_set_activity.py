#!/usr/bin/env python3
"""
Fix Chinese comments in PseudoSetActivity.kt to use proper comment syntax.
"""

import re


def fix_pseudo_set_activity():
    """Fix the PseudoSetActivity.kt file to add proper comment markers."""

    filepath = (
        "component/pseudo/src/main/java/com/topdon/pseudo/activity/PseudoSetActivity.kt"
    )

    try:
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()

        lines = content.split("\n")
        fixed_lines = []

        for line in lines:
            # Check if line has Chinese characters but no comment marker at the start
            if (
                re.search(r"[\u4e00-\u9fff]", line)
                and not line.strip().startswith("//")
                and not line.strip().startswith("*")
            ):
                # If the line starts with Chinese characters (not in a string),
                    add comment marker
                if re.match(r"^\s*[\u4e00-\u9fff]", line):
                    # Get the indentation
                    indentation = re.match(r"^(\s*)", line).group(1)
                    content_without_indent = line[len(indentation) :]
                    fixed_line = f"{indentation}// {content_without_indent}"
                    fixed_lines.append(fixed_line)
                    print(f"Fixed: {line}")
                    print(f"  -> {fixed_line}")
                else:
                    fixed_lines.append(line)
            else:
                fixed_lines.append(line)

        new_content = "\n".join(fixed_lines)

        # Write the fixed content back
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(new_content)

        print(f"✓ Fixed {filepath}")
        return True

    except Exception as e:
        print(f"Error processing {filepath}: {e}")
        return False


if __name__ == "__main__":
    fix_pseudo_set_activity()
