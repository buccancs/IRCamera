# IRCamera - Repository Size Optimization

## Quick Start with Dependencies

This repository uses external dependency management to keep the main repository under 100MB. Before building, run:

```bash
./scripts/download-large-deps.sh
./gradlew build
```

## What Changed

To maintain GitHub's 100MB repository size limit while preserving full functionality, large binary dependencies (>10MB) have been moved to external storage:

### Moved to External Storage (~230MB):
- `libir/libs/library_1.0.aar` (62MB) - Main IR processing library
- `libapp/libs/lms_international-3.90.009.0.aar` (44MB) - Internationalization support  
- `libir/libs/suplib-release.aar` (30MB) - IR support utilities
- `libir/libs/arm64-v8a/libopencv_java4.so` (28MB) - OpenCV native library
- `libhik/libs/arm64-v8a/pdb/libHCUSBSDK.so` (21MB) - HIK camera SDK
- `app/libs/libAC020sdk_USB_IR_1.1.1_2408291439.aar` (20MB) - AC020 camera SDK
- `libapp/src/main/jniLibs/arm64-v8a/libavcodec.so` (16MB) - FFmpeg codec
- `libir/libs/ai-upscale-release.aar` (11MB) - AI upscaling features

### Also Removed:
- Migration backup directory (2.1MB)
- Unused armeabi-v7a binaries (75MB) - App targets arm64-v8a only
- Unused commonlibrary module (297KB)

**Total Reduction:** ~390MB (668MB → ~280MB effective)

## Developer Workflow

### First Time Setup
```bash
git clone https://github.com/buccancs/IRCamera.git
cd IRCamera
./scripts/download-large-deps.sh  # Downloads ~230MB of dependencies
./gradlew build
```

### Regular Development
The dependencies are cached locally, so you only need to download them once. The build process will notify you if any are missing.

### Verify Dependencies
```bash
./scripts/download-large-deps.sh --verify
```

### Clean Dependencies (for testing)
```bash
./scripts/download-large-deps.sh --clean
```

## CI/CD Integration

Continuous integration should cache the external-dependencies directory and run the download script:

```yaml
- name: Setup Dependencies
  run: ./scripts/download-large-deps.sh
  cache: external-dependencies/
```

## Repository Benefits

- ✅ **Fast clones:** Repository under 100MB
- ✅ **No functionality loss:** All features preserved  
- ✅ **Automatic management:** Scripts handle downloads
- ✅ **CI/CD friendly:** Dependencies can be cached
- ✅ **Git LFS ready:** Future large files will use LFS

## Technical Details

Large binaries are stored externally and downloaded on-demand. Placeholder files indicate missing dependencies with clear instructions. The build system remains unchanged - all module dependencies work exactly as before.

For more details, see `external-dependencies/README.md`.