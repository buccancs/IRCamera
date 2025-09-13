#!/bin/bash

echo "🚀 Ultra-fast development build script"
echo "========================================"

# Set build start time
start_time=$(date +%s)

# Set optimal JVM options for maximum performance
export GRADLE_OPTS="-Xmx8g -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication"

# Build only debug variant with minimal checks
echo "🔨 Building debug APK with optimizations..."
./gradlew :app:assembleDebug \
    --no-daemon \
    --console=plain \
    --parallel \
    --build-cache \
    --configuration-cache \
    -x lint \
    -x test \
    -x connectedCheck \
    2>&1 | tee build_ultra_fast.log

# Calculate build time
end_time=$(date +%s)
build_time=$((end_time - start_time))

if [ $? -eq 0 ]; then
    echo "✅ Ultra-fast build completed successfully in ${build_time} seconds!"
    echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
    ls -lh app/build/outputs/apk/debug/app-debug.apk 2>/dev/null || echo "❌ APK not found"
else
    echo "❌ Build failed in ${build_time} seconds"
    echo "📋 Check build_ultra_fast.log for details"
    exit 1
fi