# IRCamera - TC001 Thermal Camera Application

An Android application for the TC001 thermal infrared camera, providing comprehensive thermal imaging capabilities.

## Project Overview

This is a modular Android application specifically designed for the TC001 thermal camera device. The app features real-time thermal imaging, temperature measurement, image/video capture, and data analysis tools.

## Supported Device

- **TC001**: Primary thermal camera device

## Key Features

- 🌡️ Real-time thermal imaging and temperature measurement
- 📸 Photo and video capture with thermal overlays
- 📊 Temperature data analysis and reporting
- 🔄 Multi-device support and connectivity
- 📱 User account management and data sync
- 🏠 Home dashboard with device status
- 🎥 Video playback and analysis tools
- 📄 PDF report generation

## Repository Structure

For detailed information about the project structure and what each folder contains, see [FOLDER_STRUCTURE.md](FOLDER_STRUCTURE.md).

## Architecture

The project follows a modular Android architecture:

- **Main App**: Core application logic and UI
- **libapp**: Shared library with common functionality
- **Components**: Feature-specific modules (thermal imaging, user management, etc.)
- **Device Libraries**: Hardware-specific integrations

## Build Variants

The TC001 application supports multiple build variants:

- **Google Play**: Consumer version for TC001
- **Topdon**: OEM version for TC001
- **Regional**: China and international variants
- **Environment**: Development, beta, and production builds

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24 (API level 24) or higher
- NDK version 21.3.6528147
- Gradle 7.1.3 or later

### Current Build Status

The project currently has a simplified Gradle configuration that includes:
- ✅ Main `app` module (active)  
- ✅ `LocalRepo:libcommon` library (active)

Other modules have build configurations but are not currently included in the main build. See [FOLDER_STRUCTURE.md](FOLDER_STRUCTURE.md) for complete module details.

### Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync the project with Gradle files
4. Use the provided build scripts:
   - `build_apk_google_script.bat` for Google Play variant
   - `build_apk_topdon_script.bat` for Topdon variant

### Build Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew :app:assembleProdDebug

# Build release APK
./gradlew :app:assembleProdRelease
```

## Technologies Used

- **Android**: Native Android development
- **Kotlin & Java**: Primary programming languages
- **Gradle**: Build system and dependency management
- **Room**: Local database ORM
- **OpenGL**: Graphics rendering for thermal overlays
- **GSY Video Player**: Video playback functionality
- **Firebase**: Crashlytics and analytics
- **TC001 SDK**: Thermal imaging SDK for TC001 device

## License

This project is proprietary software for the TC001 thermal camera application.

## Support

For technical support and documentation, refer to the [folder structure guide](FOLDER_STRUCTURE.md) to understand the codebase organization.