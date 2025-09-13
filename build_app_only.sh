#!/bin/bash
echo "📱 App Module Only Build"
start=$(date +%s)
./gradlew :app:assembleDebug \
    --no-daemon \
    --parallel \
    --build-cache \
    --configuration-cache
end=$(date +%s) 
echo "✅ App build completed in $((end - start)) seconds"
