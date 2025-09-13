#!/bin/bash
echo "=== OPTIMIZED DEBUG BUILD - Fast Development Iteration ==="
start=$(date +%s)

# Pre-build validation
if [ ! -f "gradlew" ]; then
    echo "Error: Not in project root directory"
    exit 1
fi

# Build with optimized flags for debug development
echo "Building debug variant with maximum performance optimizations..."
./gradlew assembleDebug \
    --no-daemon \
    --parallel \
    --build-cache \
    --configuration-cache \
    --console=rich \
    --warning-mode=summary \
    -Dorg.gradle.jvmargs="-Xmx8g -XX:+UseG1GC" \
    -Dkotlin.incremental=true \
    -Dkotlin.parallel.tasks.in.project=true

end=$(date +%s)
echo "Debug build completed in $((end - start)) seconds"
