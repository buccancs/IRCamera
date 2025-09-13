#!/bin/bash

# Optimized debug build script for IRCamera multi-sensor recording app
# This script prioritizes development speed over comprehensive validation

set -e

echo "🚀 Starting optimized debug build..."

# Set performance environment variables
export GRADLE_OPTS="-Xmx8g -XX:MaxMetaspaceSize=2g -XX:+UseG1GC -XX:MaxGCPauseMillis=100"
export JAVA_OPTS="-Xmx4g"

# Build configuration for development speed
BUILD_TYPE="debug"
SKIP_TESTS=true
PARALLEL_BUILDS=true
MAX_WORKERS=8

echo "📋 Build Configuration:"
echo "  - Build Type: $BUILD_TYPE"
echo "  - Skip Tests: $SKIP_TESTS" 
echo "  - Parallel: $PARALLEL_BUILDS"
echo "  - Max Workers: $MAX_WORKERS"
echo ""

# Clean previous build artifacts that might be causing issues
echo "🧹 Cleaning build artifacts..."
./gradlew clean --no-daemon --quiet

# Run optimized debug build
echo "🔨 Building app debug variant..."
start_time=$(date +%s)

./gradlew app:assembleDebug \
  --no-daemon \
  --parallel \
  --max-workers=$MAX_WORKERS \
  --no-build-cache \
  --configure-on-demand \
  --quiet \
  -x lintDebug \
  -x testDebugUnitTest \
  -x connectedDebugAndroidTest \
  ${SKIP_TESTS:+-x test}

end_time=$(date +%s)
duration=$((end_time - start_time))

echo ""
echo "✅ Debug build completed!"
echo "⏱️  Build time: ${duration} seconds"

# Verify APK was created
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo "📱 APK generated: $APK_PATH ($APK_SIZE)"
    
    # Basic APK validation
    echo "🔍 APK validation:"
    if aapt dump badging "$APK_PATH" >/dev/null 2>&1; then
        echo "   ✅ APK structure valid"
    else
        echo "   ❌ APK structure invalid"
    fi
else
    echo "❌ APK not found at $APK_PATH"
    exit 1
fi

echo ""
echo "🎯 Build optimization results:"
echo "   - Target time: <60s for debug builds"
echo "   - Actual time: ${duration}s" 
if [ $duration -lt 60 ]; then
    echo "   ✅ Performance target achieved!"
else
    echo "   ⚠️  Performance target missed (need further optimization)"
fi

echo ""
echo "📝 Next steps:"
echo "   1. Install APK: adb install -r $APK_PATH"
echo "   2. Test on Samsung S22 with thermal camera + GSR sensors"
echo "   3. Validate PC-Phone communication"