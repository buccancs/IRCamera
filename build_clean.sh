#!/bin/bash
echo "🧹 Clean Build"
./gradlew clean
echo "⚡ Starting fresh build..."
start=$(date +%s)
./gradlew assembleDebug \
    --no-daemon \
    --parallel \
    --build-cache \
    --console=rich
end=$(date +%s)
echo "✅ Clean build completed in $((end - start)) seconds"
