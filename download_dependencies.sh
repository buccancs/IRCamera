#!/bin/bash

# Download Dependencies Script for IRCamera Project
# This script downloads large AAR dependencies at build time to reduce repository size

set -e

DEPS_DIR="external-deps"
BASE_URL="https://github.com/buccancs/IRCamera-Dependencies/releases/download/v1.0"

# Create dependencies directory
mkdir -p $DEPS_DIR

echo "Downloading large dependencies..."

# Function to download with retry
download_with_retry() {
    local url=$1
    local output=$2
    local max_attempts=3
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        echo "Attempt $attempt: Downloading $output..."
        if curl -L -o "$output" "$url"; then
            echo "Successfully downloaded $output"
            return 0
        else
            echo "Download failed (attempt $attempt/$max_attempts)"
            if [ $attempt -eq $max_attempts ]; then
                echo "ERROR: Failed to download $output after $max_attempts attempts"
                return 1
            fi
            attempt=$((attempt + 1))
            sleep 2
        fi
    done
}

# Download large AAR files to external-deps directory
download_with_retry "$BASE_URL/library_1.0.aar" "$DEPS_DIR/library_1.0.aar"
download_with_retry "$BASE_URL/suplib-release.aar" "$DEPS_DIR/suplib-release.aar"
download_with_retry "$BASE_URL/ai-upscale-release.aar" "$DEPS_DIR/ai-upscale-release.aar"
download_with_retry "$BASE_URL/lms_international-3.90.009.0.aar" "$DEPS_DIR/lms_international-3.90.009.0.aar"
download_with_retry "$BASE_URL/libAC020sdk_USB_IR_1.1.1_2408291439.aar" "$DEPS_DIR/libAC020sdk_USB_IR_1.1.1_2408291439.aar"

# Download remaining large dependencies
download_with_retry "$BASE_URL/libusbdualsdk_1.3.4_2406271906_standard.aar" "$DEPS_DIR/libusbdualsdk_1.3.4_2406271906_standard.aar"
download_with_retry "$BASE_URL/libcommon_1.2.0_24052117.aar" "$DEPS_DIR/libcommon_1.2.0_24052117.aar"
download_with_retry "$BASE_URL/auth-number-2.13.2.1.aar" "$DEPS_DIR/auth-number-2.13.2.1.aar"

# Download large PDF assets
download_with_retry "$BASE_URL/TS004.pdf" "$DEPS_DIR/TS004.pdf"
download_with_retry "$BASE_URL/TC001.pdf" "$DEPS_DIR/TC001.pdf"

# Download large drawable resources
download_with_retry "$BASE_URL/ic_report_bg_top.png" "$DEPS_DIR/ic_report_bg_top.png"
download_with_retry "$BASE_URL/ic_main_connect_bg.webp" "$DEPS_DIR/ic_main_connect_bg.webp"

# Download x86 architecture native libraries (optional)
download_with_retry "$BASE_URL/x86_libyuv.so" "$DEPS_DIR/x86_libyuv.so"
download_with_retry "$BASE_URL/x86_64_libyuv.so" "$DEPS_DIR/x86_64_libyuv.so"

echo "All dependencies downloaded successfully!"

# Copy to appropriate lib directories
echo "Copying dependencies to lib directories..."
cp "$DEPS_DIR/library_1.0.aar" "libir/libs/"
cp "$DEPS_DIR/suplib-release.aar" "libir/libs/"
cp "$DEPS_DIR/ai-upscale-release.aar" "libir/libs/"
cp "$DEPS_DIR/lms_international-3.90.009.0.aar" "libapp/libs/"
cp "$DEPS_DIR/libAC020sdk_USB_IR_1.1.1_2408291439.aar" "app/libs/"
cp "$DEPS_DIR/libusbdualsdk_1.3.4_2406271906_standard.aar" "libir/libs/"
cp "$DEPS_DIR/libcommon_1.2.0_24052117.aar" "app/libs/"
cp "$DEPS_DIR/auth-number-2.13.2.1.aar" "libapp/libs/"

# Copy assets
echo "Copying large assets..."
mkdir -p app/src/main/assets
cp "$DEPS_DIR/TS004.pdf" "app/src/main/assets/"
cp "$DEPS_DIR/TC001.pdf" "app/src/main/assets/"

# Copy drawable resources
mkdir -p component/thermal-ir/src/main/res/drawable-xxhdpi
cp "$DEPS_DIR/ic_report_bg_top.png" "component/thermal-ir/src/main/res/drawable-xxhdpi/"
cp "$DEPS_DIR/ic_main_connect_bg.webp" "component/thermal-ir/src/main/res/drawable-xxhdpi/"

# Copy x86 native libraries (optional - only if needed)
if [ -f "$DEPS_DIR/x86_libyuv.so" ]; then
    mkdir -p libmatrix/libs/x86
    cp "$DEPS_DIR/x86_libyuv.so" "libmatrix/libs/x86/libyuv.so"
fi
if [ -f "$DEPS_DIR/x86_64_libyuv.so" ]; then
    mkdir -p libmatrix/libs/x86_64
    cp "$DEPS_DIR/x86_64_libyuv.so" "libmatrix/libs/x86_64/libyuv.so"
fi

echo "Dependencies setup complete!"