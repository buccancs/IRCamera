#!/bin/bash

# Fast core module build script - Excludes problematic thermal-ir module
# Focuses on critical components: main app, GSR recording, PC communication

echo "🚀 Building core modules (excluding thermal-ir)..."
echo "Target: Validate main functionality without thermal-ir compilation issues"

./gradlew \
  :app:compileDebugKotlin \
  :component:gsr-recording:compileDebugKotlin \
  :BleModule:compileDebugKotlin \
  :libapp:compileDebugKotlin \
  :libcom:compileDebugKotlin \
  :libui:compileDebugKotlin \
  --no-daemon \
  --warning-mode=none \
  -q

if [ $? -eq 0 ]; then
    echo "✅ Core modules compilation successful!"
    echo "📊 Ready for PC-Phone communication testing"
else
    echo "❌ Core modules compilation failed"
    exit 1
fi