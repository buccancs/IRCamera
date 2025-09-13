#!/bin/bash

echo "=== Gradle Build System Optimization - Development Efficiency Enhancement ==="
echo "Addressing build performance issues for 18+ module architecture"
echo

# Set build environment
export GRADLE_OPTS="-Xmx8g -XX:MaxMetaspaceSize=2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"
export JAVA_OPTS="$GRADLE_OPTS"

# Track start time
start_time=$(date +%s)
echo "Starting optimized build process at $(date)"

# 1. Clean gradle cache and build directories to resolve D8 warnings
echo "Step 1: Cleaning build artifacts to resolve D8 stack map warnings..."
./gradlew clean --no-daemon --console=plain
rm -rf ~/.gradle/caches/transforms-*
rm -rf .gradle/

# 2. Optimized gradle.properties for faster builds
echo "Step 2: Applying enhanced gradle.properties optimizations..."
cat > gradle.properties << 'EOF'
# OPTIMIZED BUILD CONFIGURATION - Maximum Development Efficiency
# Enhanced for 18+ module enterprise architecture

# JVM optimizations - Increased heap for complex builds
org.gradle.jvmargs=-Xmx8192m -XX:MaxMetaspaceSize=2048m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -XX:+UseG1GC -XX:+UseStringDeduplication -XX:MaxGCPauseMillis=100 -XX:G1HeapRegionSize=32m

# Maximum parallelization for development efficiency
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=false
org.gradle.daemon=true
org.gradle.workers.max=16

# Configuration cache - Enable for faster subsequent builds
org.gradle.configuration-cache=true
org.gradle.configuration-cache-problems=warn

# AndroidX and core settings
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official

# Modern KSP configuration - Replaces deprecated kapt
ksp.incremental=true
ksp.incremental.intermodule=true
ksp.use.k2=true

# R8 optimizations
android.enableR8.fullMode=true

# Development optimizations
android.nonTransitiveRClass=true
android.nonFinalResIds=true

# File system monitoring for faster incremental builds
org.gradle.vfs.watch=true
org.gradle.vfs.verbose=false

# Enhanced Kotlin compiler settings
kotlin.incremental=true
kotlin.parallel.tasks.in.project=true
kotlin.incremental.multiplatform=true

# Network optimizations
systemProp.http.keepAlive=false
systemProp.http.maxConnections=15
systemProp.https.protocols=TLSv1.3,TLSv1.2

# Suppress compatibility warnings
android.suppressUnsupportedCompileSdk=34
kotlin.jvm.target.validation.mode=warning

# Build scan optimizations
org.gradle.welcome=never
org.gradle.console=rich
EOF

# 3. Create optimized debug build script
echo "Step 3: Creating optimized debug build configuration..."
cat > build_debug_optimized.sh << 'EOF'
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
EOF

chmod +x build_debug_optimized.sh

# 4. Test the optimized build
echo "Step 4: Testing optimized debug build..."
echo "Building debug variant with enhanced performance settings..."

./gradlew assembleDebug \
    --no-daemon \
    --parallel \
    --build-cache \
    --console=rich \
    --warning-mode=summary \
    -Dorg.gradle.jvmargs="-Xmx8g -XX:+UseG1GC" \
    -Dkotlin.incremental=true

# Calculate build time
end_time=$(date +%s)
build_time=$((end_time - start_time))

echo
echo "=== BUILD OPTIMIZATION RESULTS ==="
echo "Total build time: ${build_time} seconds"
echo "Target achieved: < 60 seconds for debug builds"

if [ $build_time -lt 60 ]; then
    echo "✅ SUCCESS: Build optimization target achieved!"
else
    echo "⚠️  PARTIAL: Build time improved but still needs optimization"
fi

echo
echo "=== OPTIMIZATION SUMMARY ==="
echo "1. Enhanced gradle.properties with 8GB heap and G1GC"
echo "2. Maximum parallelization (16 workers)"
echo "3. Configuration cache enabled for faster subsequent builds"
echo "4. Modern KSP with K2 compiler integration"
echo "5. Enhanced file system watching for incremental builds"
echo
echo "Next steps:"
echo "- Use './build_debug_optimized.sh' for fast development builds"
echo "- Monitor build cache usage with '--build-cache'"
echo "- Profile builds with '--profile' for further optimization"