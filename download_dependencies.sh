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

echo "All dependencies downloaded successfully!"

# Copy to appropriate lib directories
echo "Copying dependencies to lib directories..."
cp "$DEPS_DIR/library_1.0.aar" "libir/libs/"
cp "$DEPS_DIR/suplib-release.aar" "libir/libs/"
cp "$DEPS_DIR/ai-upscale-release.aar" "libir/libs/"
cp "$DEPS_DIR/lms_international-3.90.009.0.aar" "libapp/libs/"
cp "$DEPS_DIR/libAC020sdk_USB_IR_1.1.1_2408291439.aar" "app/libs/"

echo "Dependencies setup complete!"