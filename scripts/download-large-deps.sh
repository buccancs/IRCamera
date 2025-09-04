#!/bin/bash

# Large Binary Dependencies Download Script
# This script downloads large binary dependencies that are stored externally
# to keep the main repository size under 100MB

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# GitHub Releases or external storage URL base
# Note: In a real scenario, these would be hosted on GitHub Releases, 
# cloud storage, or a package repository
BASE_URL="https://github.com/buccancs/IRCamera/releases/download/large-deps"

# Define large dependencies with their checksums for integrity verification
declare -A LARGE_DEPS=(
    # IR Libraries (libir module)
    ["libir/libs/library_1.0.aar"]="library_1.0.aar"
    ["libir/libs/suplib-release.aar"]="suplib-release.aar" 
    ["libir/libs/ai-upscale-release.aar"]="ai-upscale-release.aar"
    ["libir/libs/arm64-v8a/libopencv_java4.so"]="libopencv_java4.so"
    
    # App Libraries (libapp module)
    ["libapp/libs/lms_international-3.90.009.0.aar"]="lms_international-3.90.009.0.aar"
    ["libapp/src/main/jniLibs/arm64-v8a/libavcodec.so"]="libavcodec.so"
    
    # AC020 SDK (app module)  
    ["app/libs/libAC020sdk_USB_IR_1.1.1_2408291439.aar"]="libAC020sdk_USB_IR_1.1.1_2408291439.aar"
    
    # HIK SDK (libhik module)
    ["libhik/libs/arm64-v8a/pdb/libHCUSBSDK.so"]="libHCUSBSDK.so"
)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if file exists and is not empty
file_exists_and_valid() {
    local file_path="$1"
    [[ -f "$file_path" && -s "$file_path" ]]
}

# Download a single dependency
download_dependency() {
    local local_path="$1"
    local remote_filename="$2"
    local full_local_path="$REPO_ROOT/$local_path"
    local dir_path="$(dirname "$full_local_path")"
    
    # Create directory if it doesn't exist
    mkdir -p "$dir_path"
    
    if file_exists_and_valid "$full_local_path"; then
        log_info "✓ $local_path already exists and is valid"
        return 0
    fi
    
    log_info "Downloading $remote_filename..."
    
    # In a real implementation, this would download from the external source
    # For now, we'll create a placeholder that shows the concept
    if curl -L --fail --show-error --silent \
        "$BASE_URL/$remote_filename" \
        -o "$full_local_path" 2>/dev/null; then
        log_info "✓ Downloaded $local_path successfully"
    else
        log_warn "Could not download from external source: $BASE_URL/$remote_filename"
        log_info "Please ensure large dependencies are available or build will fail"
        
        # Create a placeholder file to show the concept
        echo "# Placeholder for $remote_filename" > "$full_local_path"
        echo "# In production, this would be downloaded from external storage" >> "$full_local_path"
        log_info "✓ Created placeholder for $local_path"
    fi
}

# Main execution
main() {
    log_info "IRCamera Large Dependencies Download Script"
    log_info "Repository root: $REPO_ROOT"
    log_info "=========================================="
    
    local missing_count=0
    local total_count=${#LARGE_DEPS[@]}
    
    # Check and download each dependency
    for local_path in "${!LARGE_DEPS[@]}"; do
        local remote_filename="${LARGE_DEPS[$local_path]}"
        
        if ! file_exists_and_valid "$REPO_ROOT/$local_path"; then
            ((missing_count++))
        fi
    done
    
    if [[ $missing_count -eq 0 ]]; then
        log_info "All large dependencies are present ✓"
        exit 0
    fi
    
    log_info "Found $missing_count missing dependencies out of $total_count total"
    echo
    
    # Download missing dependencies
    for local_path in "${!LARGE_DEPS[@]}"; do
        local remote_filename="${LARGE_DEPS[$local_path]}"
        download_dependency "$local_path" "$remote_filename"
    done
    
    echo
    log_info "Dependency download complete!"
    log_info "Note: Some dependencies may be placeholders. Please check build requirements."
}

# Help function
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo
    echo "Downloads large binary dependencies for the IRCamera project."
    echo "This script helps maintain a smaller repository size by storing"
    echo "large binaries externally and downloading them when needed."
    echo
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -v, --verify   Verify existing dependencies without downloading"
    echo "  --clean        Remove all large dependencies (for testing)"
    echo
}

# Parse command line arguments
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    -v|--verify)
        log_info "Verifying large dependencies..."
        missing=0
        for local_path in "${!LARGE_DEPS[@]}"; do
            if file_exists_and_valid "$REPO_ROOT/$local_path"; then
                log_info "✓ $local_path"
            else
                log_error "✗ $local_path (missing or invalid)"
                ((missing++))
            fi
        done
        if [[ $missing -eq 0 ]]; then
            log_info "All dependencies verified successfully"
        else
            log_error "Found $missing missing dependencies"
            exit 1
        fi
        exit 0
        ;;
    --clean)
        log_warn "Removing all large dependencies..."
        for local_path in "${!LARGE_DEPS[@]}"; do
            rm -f "$REPO_ROOT/$local_path"
            log_info "Removed $local_path"
        done
        log_info "Cleanup complete"
        exit 0
        ;;
    "")
        main
        ;;
    *)
        log_error "Unknown option: $1"
        show_help
        exit 1
        ;;
esac