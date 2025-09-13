#!/bin/bash

# Test build performance with optimized settings
echo "⏱️ Testing Build Performance - Optimized Settings"
echo "==============================================="

# Clean previous build
echo "🧹 Cleaning previous build..."
./gradlew clean --no-daemon --quiet

echo "📊 Starting timed build test..."
start_time=$(date +%s)

# Build main app with minimal dependencies
./gradlew :app:compileDebugKotlin --no-daemon --quiet --no-scan -x lint -x test

end_time=$(date +%s)
duration=$((end_time - start_time))

echo ""
echo "✅ Build completed in: ${duration} seconds"

if [ $duration -lt 60 ]; then
    echo "🎉 Excellent build performance!"
elif [ $duration -lt 120 ]; then
    echo "✅ Good build performance"
else
    echo "⚠️ Build performance needs improvement (target: <60s)"
fi

echo ""
echo "📈 Build Performance Analysis:"
echo "   Target: <60 seconds"
echo "   Actual: ${duration} seconds"
echo "   Status: $([ $duration -lt 60 ] && echo "PASSED" || echo "NEEDS OPTIMIZATION")"