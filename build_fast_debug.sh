#!/bin/bash

# Fast debug build script with optimized settings
echo "🚀 IRCamera Fast Debug Build"
echo "============================="

# Set environment for fastest possible build
export GRADLE_OPTS="-Xmx8g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC -XX:+UseStringDeduplication"

# Build only the main app module with debug variant
echo "📦 Building debug APK (app module only)..."

# Use optimized gradle command
./gradlew :app:assembleDebug \
  --no-daemon \
  --parallel \
  --max-workers=16 \
  --build-cache \
  --configuration-cache \
  --no-scan \
  --quiet \
  -x lint \
  -x lintVitalDebug \
  -x test \
  -x testDebug \
  -x check

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ Debug APK build completed successfully!"
    echo "📁 APK location: app/build/outputs/apk/debug/"
    ls -la app/build/outputs/apk/debug/*.apk 2>/dev/null || echo "⚠️ APK not found in expected location"
else
    echo "❌ Build failed with exit code: $EXIT_CODE"
    echo "💡 Try running with --stacktrace for more details"
fi

echo ""
echo "⏱️  Build completed in $(date)"