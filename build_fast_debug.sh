#!/bin/bash
echo "⚡ Fast Debug Build"
start=$(date +%s)
./gradlew assembleDebug \
    --no-daemon \
    --parallel \
    --build-cache \
    --configuration-cache \
    --console=rich \
    -Dorg.gradle.jvmargs="-Xmx8g -XX:+UseG1GC" \
    -Dkotlin.incremental=true
end=$(date +%s)
echo "✅ Debug build completed in $((end - start)) seconds"
