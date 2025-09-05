# IRCamera Dependency Management

This document explains how large dependencies are managed to keep the repository size under 100MB.

## Overview

Large AAR and native library files have been moved out of the repository and are downloaded at build time to reduce repository size from 668MB to under 100MB.

## Affected Dependencies

The following large files are downloaded during build:

### libir/libs/
- `library_1.0.aar` (62MB) - Core thermal processing library
- `suplib-release.aar` (30MB) - Thermal processing support
- `ai-upscale-release.aar` (11MB) - AI upscaling functionality

### libapp/libs/
- `lms_international-3.90.009.0.aar` (44MB) - LMS SDK international version

### app/libs/
- `libAC020sdk_USB_IR_1.1.1_2408291439.aar` (20MB) - USB IR camera SDK

## Build Process

1. **Automatic Download**: Run `./download_dependencies.sh` before building
2. **Manual Setup**: Dependencies are downloaded to `external-deps/` directory
3. **Copy**: Files are copied to appropriate `libs/` directories
4. **Build**: Normal Gradle build process continues

## Setup Instructions

### First Time Setup
```bash
# Make the script executable
chmod +x download_dependencies.sh

# Download all dependencies
./download_dependencies.sh

# Build the project
./gradlew assembleDebug
```

### CI/CD Integration
Add this step to your build pipeline before the Gradle build:
```bash
./download_dependencies.sh
```

## Gitignore Entries

The following entries in `.gitignore` prevent these large files from being committed:

```
# Large dependency files - downloaded at build time
external-deps/
libir/libs/library_1.0.aar
libir/libs/suplib-release.aar
libir/libs/ai-upscale-release.aar
libapp/libs/lms_international-3.90.009.0.aar
app/libs/libAC020sdk_USB_IR_1.1.1_2408291439.aar
```

## Fallback Behavior

The build system includes conditional logic to handle missing dependencies gracefully:
- If large AAR files are missing, they are simply skipped in the build
- Essential functionality may be reduced, but the app will still compile
- Download the dependencies for full functionality

## Size Reduction Achieved

- **Original Size**: 668MB
- **Current Size**: ~100MB (after removing large dependencies)
- **Reduction**: ~568MB (85% size reduction)

## Troubleshooting

### Build Failures
If build fails due to missing dependencies:
1. Run `./download_dependencies.sh`
2. Check internet connection
3. Verify all files are downloaded to `external-deps/`

### Missing Functionality
If certain features don't work:
1. Ensure all dependencies are downloaded
2. Check that files exist in the respective `libs/` directories
3. Clean and rebuild the project

## Development Workflow

For active development:
1. Download dependencies once: `./download_dependencies.sh`
2. Dependencies persist in local `libs/` directories
3. Normal incremental builds work as expected
4. Only re-download if dependencies are updated or removed