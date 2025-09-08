#!/bin/bash
# Check for Chinese characters in code comments
# This script helps maintain internationalization standards

set -e

has_chinese=false
excluded_paths="migration_backup|test|spec|charting"

echo "🔍 Checking for Chinese characters in code comments..."

# Find files with Chinese characters, excluding test files and migrations
while IFS= read -r -d '' file; do
    if [[ ! "$file" =~ $excluded_paths ]]; then
        # Use Python for reliable Unicode detection
        if python3 -c "
import re
import sys
try:
    with open('$file', 'r', encoding='utf-8') as f:
        content = f.read()
    if re.search(r'[\u4e00-\u9fff]', content):
        sys.exit(0)  # Found Chinese characters
    else:
        sys.exit(1)  # No Chinese characters
except:
    sys.exit(1)
" 2>/dev/null; then
            echo "❌ Chinese characters found in: $file"
            python3 -c "
import re
try:
    with open('$file', 'r', encoding='utf-8') as f:
        for i, line in enumerate(f, 1):
            if re.search(r'[\u4e00-\u9fff]', line):
                print(f'{i}:{line.strip()}')
                if i > 3:  # Limit output
                    break
except:
    pass
" 2>/dev/null
            echo ""
            has_chinese=true
        fi
    fi
done < <(find . -name "*.java" -o -name "*.kt" -o -name "*.py" -print0)

if [ "$has_chinese" = true ]; then
    echo "💡 Please translate Chinese comments to English for international accessibility."
    echo "   See CODING_STANDARDS.md for translation guidelines."
    exit 1
else
    echo "✅ No Chinese characters found in code comments."
fi