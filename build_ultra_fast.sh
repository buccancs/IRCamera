#!/bin/bash

# Ultra-fast build optimization for development
# Targets sub-30 second builds for rapid iteration

echo "🚀 Ultra-Fast Build Optimization Script"
echo "Target: <30 seconds for debug builds"

# Step 1: Configure ultra-fast build settings
cat > gradle.properties.fast << 'EOF'
# Ultra-fast development build configuration
org.gradle.jvmargs=-Xmx12g -XX:MaxMetaspaceSize=3g -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
org.gradle.workers.max=20
org.gradle.configuration-cache=true
android.useAndroidX=true
android.enableJetifier=true
ksp.incremental=true
ksp.use.k2=true
android.enableR8.fullMode=false
android.nonTransitiveRClass=true
android.nonFinalResIds=true
org.gradle.vfs.watch=true
kotlin.incremental=true
kotlin.parallel.tasks.in.project=true
org.gradle.console=plain
EOF

# Step 2: Backup current config and apply ultra-fast
cp gradle.properties gradle.properties.backup
cp gradle.properties.fast gradle.properties

# Step 3: Clean build artifacts selectively
echo "Cleaning build artifacts..."
rm -rf .gradle/configuration-cache/
./gradlew clean --no-daemon >/dev/null 2>&1

# Step 4: Build only essential modules for validation
echo "Building core modules only..."
time ./gradlew :app:assembleDebug --parallel --no-daemon --build-cache

# Step 5: Restore original config if needed
if [ "$1" = "--restore" ]; then
    cp gradle.properties.backup gradle.properties
    echo "Original gradle.properties restored"
fi

echo "Build optimization complete!"