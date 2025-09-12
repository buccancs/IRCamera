#!/bin/bash
# Development Build Script - Optimized for fast iteration
# Use this for day-to-day development instead of full release builds

echo "🚀 Starting optimized development build..."

# Clean previous debug builds only
./gradlew clean --parallel --build-cache

# Build debug variant only for faster development
echo "📱 Building debug APK for development..."
time ./gradlew assembleDebug \
  --parallel \
  --build-cache \
  --configuration-cache \
  --daemon \
  --max-workers=16 \
  --profile

echo "✅ Development build complete!"
echo "📍 Debug APK location: app/build/outputs/apk/debug/app-debug.apk"