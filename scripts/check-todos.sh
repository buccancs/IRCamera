#!/bin/bash
# Check TODO/FIXME formatting and suggest issue tracking
# Ensures proper development task management

set -e

has_untracked_todos=false
excluded_paths="migration_backup|test|spec|charting"

echo "🔍 Checking TODO/FIXME comment formatting..."

# Find TODO/FIXME comments that might need better tracking
while IFS= read -r -d '' file; do
    if [[ ! "$file" =~ $excluded_paths ]]; then
        # Look for TODO/FIXME without issue references or proper formatting
        if grep -n "TODO\|FIXME" "$file" | grep -v -E "(TODO:|FIXME:|#[0-9]+|Issue #|Fix:|Implement:)" > /dev/null 2>&1; then
            echo "⚠️  Found untracked TODO/FIXME in: $file"
            grep -n "TODO\|FIXME" "$file" | grep -v -E "(TODO:|FIXME:|#[0-9]+|Issue #|Fix:|Implement:)" | head -2
            echo ""
            has_untracked_todos=true
        fi
    fi
done < <(find . -name "*.java" -o -name "*.kt" -o -name "*.py" -print0)

if [ "$has_untracked_todos" = true ]; then
    echo "💡 Consider improving TODO/FIXME comments with:"
    echo "   - TODO: Issue #123 - Implement feature X"
    echo "   - FIXME: Fix performance issue in component Y"
    echo "   - TODO: Implement stereo calibration using cv2.stereoCalibrate()"
    echo "   See CODING_STANDARDS.md for detailed guidelines."
    echo ""
    echo "ℹ️  Note: This is a warning, not an error. Well-documented TODOs are acceptable."
else
    echo "✅ All TODO/FIXME comments are properly formatted."
fi