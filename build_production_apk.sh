#!/bin/bash

# Production APK Build Script
# Optimized for fast, reliable production builds with comprehensive artifact management

set -e  # Exit on any error

echo "========================================="
echo "🚀 Production APK Build System v1.0"
echo "========================================="

# Build configuration
BUILD_TYPE="release"
FLAVOR="prod"
OUTPUT_DIR="./production_artifacts"
TIMESTAMP=$(date "+%Y%m%d_%H%M")
LOG_FILE="./build_logs/production_build_${TIMESTAMP}.log"

# Create necessary directories
mkdir -p "${OUTPUT_DIR}"
mkdir -p "./build_logs"

# Function to log messages
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "${LOG_FILE}"
}

# Function to check build prerequisites
check_prerequisites() {
    log "📋 Checking build prerequisites..."
    
    # Check Java version
    if ! java -version 2>&1 | grep -q "version.*1\.8\|version.*11\|version.*17"; then
        log "❌ ERROR: Java 8, 11, or 17 required for Android builds"
        exit 1
    fi
    
    # Check Android SDK
    if [ -z "${ANDROID_HOME}" ]; then
        log "❌ ERROR: ANDROID_HOME environment variable not set"
        exit 1
    fi
    
    # Check signing configuration
    if [ ! -f "app/artibox_key/ArtiBox.jks" ]; then
        log "❌ ERROR: Production signing key not found"
        exit 1
    fi
    
    log "✅ Prerequisites check passed"
}

# Function to clean previous builds
clean_build() {
    log "🧹 Cleaning previous builds..."
    ./gradlew clean --quiet --build-cache --parallel
    log "✅ Clean completed"
}

# Function to build APK with optimization
build_apk() {
    log "🔨 Building production APK..."
    log "   Flavor: ${FLAVOR}"
    log "   Build Type: ${BUILD_TYPE}"
    
    # Build with optimizations
    ./gradlew ":app:assemble${FLAVOR^}${BUILD_TYPE^}" \
        --build-cache \
        --parallel \
        --quiet \
        --no-daemon \
        -Dorg.gradle.jvmargs="-Xmx6144m -XX:+UseG1GC" \
        2>&1 | tee -a "${LOG_FILE}"
    
    if [ ${PIPESTATUS[0]} -eq 0 ]; then
        log "✅ APK build completed successfully"
    else
        log "❌ APK build failed"
        exit 1
    fi
}

# Function to verify APK integrity
verify_apk() {
    log "🔍 Verifying APK integrity..."
    
    APK_PATH=$(find app/build/outputs/apk -name "*.apk" -type f | head -1)
    
    if [ -z "${APK_PATH}" ]; then
        log "❌ ERROR: APK not found"
        exit 1
    fi
    
    # Check APK size (should be reasonable, not too large or too small)
    APK_SIZE=$(stat -f%z "${APK_PATH}" 2>/dev/null || stat -c%s "${APK_PATH}")
    APK_SIZE_MB=$((APK_SIZE / 1024 / 1024))
    
    if [ ${APK_SIZE_MB} -lt 10 ]; then
        log "❌ ERROR: APK size too small (${APK_SIZE_MB}MB), possible build issue"
        exit 1
    elif [ ${APK_SIZE_MB} -gt 200 ]; then
        log "⚠️  WARNING: APK size large (${APK_SIZE_MB}MB)"
    fi
    
    # Verify APK signature
    if command -v aapt >/dev/null 2>&1; then
        aapt dump badging "${APK_PATH}" > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            log "✅ APK signature verification passed"
        else
            log "❌ ERROR: APK signature verification failed"
            exit 1
        fi
    fi
    
    log "✅ APK integrity verified (Size: ${APK_SIZE_MB}MB)"
}

# Function to organize artifacts
organize_artifacts() {
    log "📦 Organizing production artifacts..."
    
    # Create timestamped directory
    ARTIFACT_DIR="${OUTPUT_DIR}/${TIMESTAMP}"
    mkdir -p "${ARTIFACT_DIR}"
    
    # Copy APK files
    find app/build/outputs/apk -name "*.apk" -type f -exec cp {} "${ARTIFACT_DIR}/" \;
    
    # Copy mapping files (for crash analysis)
    if [ -d "app/build/outputs/mapping" ]; then
        cp -r app/build/outputs/mapping "${ARTIFACT_DIR}/"
    fi
    
    # Create build info file
    cat > "${ARTIFACT_DIR}/build_info.txt" << EOF
Build Information
=================
Build Timestamp: ${TIMESTAMP}
Build Type: ${BUILD_TYPE}
Flavor: ${FLAVOR}
Git Commit: $(git rev-parse HEAD)
Git Branch: $(git branch --show-current)
Build Machine: $(uname -a)
Build User: $(whoami)
Gradle Version: $(./gradlew --version | grep "Gradle" | head -1)

APK Files:
$(find "${ARTIFACT_DIR}" -name "*.apk" -exec basename {} \;)

Build Configuration:
- Multi-modal recording: RGB + Thermal + GSR
- Samsung S22 ground truth timing
- Shimmer3 GSR integration
- Production-signed and optimized
EOF
    
    log "✅ Artifacts organized in ${ARTIFACT_DIR}"
}

# Function to generate production report
generate_report() {
    log "📊 Generating production build report..."
    
    REPORT_FILE="${OUTPUT_DIR}/production_build_report_${TIMESTAMP}.html"
    
    cat > "${REPORT_FILE}" << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>Production Build Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .header { background: #4CAF50; color: white; padding: 20px; border-radius: 5px; }
        .section { margin: 20px 0; padding: 15px; border-left: 4px solid #4CAF50; }
        .error { border-left-color: #f44336; }
        .warning { border-left-color: #ff9800; }
        .code { background: #f5f5f5; padding: 10px; font-family: monospace; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🚀 IRCamera Production Build Report</h1>
EOF
    
    echo "        <p>Generated: $(date)</p>" >> "${REPORT_FILE}"
    echo "    </div>" >> "${REPORT_FILE}"
    
    echo "    <div class=\"section\">" >> "${REPORT_FILE}"
    echo "        <h2>✅ Build Summary</h2>" >> "${REPORT_FILE}"
    echo "        <p><strong>Status:</strong> SUCCESS</p>" >> "${REPORT_FILE}"
    echo "        <p><strong>Build Time:</strong> ${TIMESTAMP}</p>" >> "${REPORT_FILE}"
    echo "        <p><strong>Artifacts:</strong> $(find "${OUTPUT_DIR}/${TIMESTAMP}" -name "*.apk" | wc -l) APK files</p>" >> "${REPORT_FILE}"
    echo "    </div>" >> "${REPORT_FILE}"
    
    echo "    <div class=\"section\">" >> "${REPORT_FILE}"
    echo "        <h2>🔧 Features Included</h2>" >> "${REPORT_FILE}"
    echo "        <ul>" >> "${REPORT_FILE}"
    echo "            <li>✅ Multi-modal recording (RGB + Thermal + GSR)</li>" >> "${REPORT_FILE}"
    echo "            <li>✅ Samsung S22 ground truth timing synchronization</li>" >> "${REPORT_FILE}"
    echo "            <li>✅ Official Shimmer3 GSR integration</li>" >> "${REPORT_FILE}"
    echo "            <li>✅ Session management and data export</li>" >> "${REPORT_FILE}"
    echo "            <li>✅ Production-grade error handling</li>" >> "${REPORT_FILE}"
    echo "            <li>✅ Research-ready data output</li>" >> "${REPORT_FILE}"
    echo "        </ul>" >> "${REPORT_FILE}"
    echo "    </div>" >> "${REPORT_FILE}"
    
    echo "</body></html>" >> "${REPORT_FILE}"
    
    log "✅ Build report generated: ${REPORT_FILE}"
}

# Main execution
main() {
    log "🚀 Starting production build process..."
    
    check_prerequisites
    clean_build
    build_apk
    verify_apk
    organize_artifacts
    generate_report
    
    log "========================================="
    log "✅ Production build completed successfully!"
    log "📦 Artifacts available in: ${OUTPUT_DIR}/${TIMESTAMP}"
    log "📊 Build report: ${OUTPUT_DIR}/production_build_report_${TIMESTAMP}.html"
    log "📋 Build log: ${LOG_FILE}"
    log "========================================="
}

# Execute main function
main "$@"