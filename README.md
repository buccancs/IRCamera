# IRCamera - Thermal Infrared Camera Android Application

## Overview

IRCamera is a comprehensive Android application for thermal infrared (IR) camera devices, supporting multiple TOPDON thermal camera models including TC001, TC007, and TS004. The application provides advanced thermal imaging capabilities, image analysis tools, gallery management, and device-specific functionality for professional thermal imaging workflows.

**Version:** 1.10.000  
**Package:** com.csl.irCamera  
**Target SDK:** 34 (Android 14)  
**Minimum SDK:** 24 (Android 7.0)

## Supported Devices

- **TC001** - Primary thermal camera device
- **TC007** - Advanced thermal camera with enhanced features
- **TS004** - Thermal imaging device with remote capabilities
- **LINE** - Line thermal imaging devices
- **HIK** - HIK thermal camera integration

## Key Features

### 🌡️ Thermal Imaging
- Real-time thermal imaging and temperature measurement
- Advanced thermal analysis with temperature mapping
- Isothermal analysis and temperature threshold detection
- Multiple thermal display modes and color palettes

### 📷 Image & Video Management
- Comprehensive gallery management for thermal images and videos
- Image editing capabilities with thermal overlay
- Export functionality for thermal data
- Multi-format support (thermal images, regular photos, videos)

### 🔧 Device Management
- USB device connection and management
- Firmware update capabilities
- Device calibration and correction tools
- Factory reset functionality

### 📊 Data Analysis
- Temperature monitoring with historical charts
- Data logging and export capabilities
- Measurement tools (point, line, area temperature analysis)
- Advanced thermal analysis algorithms

### 🌐 Connectivity
- Remote device support (TS004 remote capabilities)
- WebSocket integration for real-time data
- HTTP API integration with LMS (License Management System)
- Network-based device communication

## Repository Structure

```
IRCamera/
├── app/                          # Main Android application module
│   ├── src/main/java/
│   │   ├── com/topdon/tc001/    # Main application components
│   │   │   ├── MainActivity.kt   # Primary activity
│   │   │   ├── SplashActivity.kt # Launch screen
│   │   │   └── app/              # Application class
│   │   ├── com/topdon/lib/core/ # Core library components
│   │   │   ├── repository/       # Data layer (TC007, TS004, Gallery)
│   │   │   ├── viewmodel/        # MVVM ViewModels
│   │   │   ├── config/           # Configuration management
│   │   │   ├── http/             # Network layer
│   │   │   ├── bean/             # Data models
│   │   │   └── utils/            # Utility classes
│   │   ├── com/topdon/module/   # Feature modules
│   │   │   └── thermal/         # Thermal imaging components
│   │   ├── com/infisense/       # Thermal camera SDK integration
│   │   │   ├── usbir/           # USB IR camera interface
│   │   │   └── usbdual/         # Dual camera support
│   │   └── com/example/         # Example implementations
│   │       └── thermal_lite/    # Lightweight thermal features
│   └── libs/                    # Third-party libraries and SDKs
├── component/                   # Modular components (9 modules)
│   ├── thermal/                 # Core thermal imaging module
│   ├── thermal-ir/              # Advanced IR features
│   ├── thermal-lite/            # Lightweight thermal module  
│   ├── house/                   # House inspection module
│   ├── pseudo/                  # Pseudo color processing
│   ├── edit3d/                  # 3D editing capabilities
│   ├── transfer/                # Data transfer module
│   ├── user/                    # User management
│   └── CommonComponent/         # Shared components
├── buildSrc/                    # Build logic and dependencies
├── RangeSeekBar/               # Custom UI component
├── libmatrix/                   # Matrix processing library
└── commonlibrary/              # Common utility library
```

## Architecture

### Design Patterns
- **MVVM (Model-View-ViewModel)** - Clean separation of UI and business logic
- **Repository Pattern** - Centralized data access layer
- **Observer Pattern** - Event-driven updates using LiveData
- **Dependency Injection** - Using Android Architecture Components

### Key Components

#### Data Layer (Repositories)
- **GalleryRepository** - Manages thermal images and videos storage/retrieval
- **TC007Repository** - Handles TC007 device communication and control
- **TS004Repository** - Manages TS004 device operations and remote functionality
- **LmsRepository** - Integrates with License Management System APIs

#### Network Layer
- **HTTP Integration** - RESTful API communication
- **WebSocket Support** - Real-time data streaming
- **Device Communication** - USB and network-based device protocols

#### File Management
- **FileConfig** - Centralized file path and storage configuration
- **FolderUtil** - Directory structure management for different data types
- **Gallery Management** - Organized storage for thermal images, PDFs, logs, and more

### Storage Structure
```
/DCIM/
├── TC001/           # TC001 device images
├── TC007/           # TC007 device images  
├── TS004/           # TS004 device images
└── TopInfrared/     # Application images

/ExternalFiles/
├── Gallery/         # User gallery
├── DataLog/         # Diagnostic and measurement logs
├── Firmware/        # Device firmware files
├── History/         # Operation history
└── Log/             # Application logs
```

## Build Configuration

### Prerequisites
- **Android Studio** Arctic Fox (2020.3.1) or later
- **Gradle** 7.5
- **Java** 17 or later
- **Kotlin** 1.7.20
- **NDK** 21.3.6528147 (for native libraries)

### Dependencies
- **AndroidX** - Modern Android support libraries
- **Room** - Local database management
- **Retrofit** - HTTP client for API communication
- **Glide** - Image loading and caching
- **RxJava** - Reactive programming
- **OpenCV** - Computer vision processing
- **Firebase** - Analytics and crash reporting

### Build Variants
- **prodDebug** - Development build with debugging enabled
- **prodRelease** - Production release build

### Native Libraries
- **libircmd.so** - IR command processing
- **libirparse.so** - IR data parsing
- **libirprocess.so** - IR image processing
- **libUSBUVCCamera.so** - USB camera interface
- **libopencv_java4.so** - OpenCV image processing

## Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/buccancs/IRCamera.git
cd IRCamera
```

### 2. Configure Environment
1. Install Android Studio and required SDKs
2. Configure NDK path in `local.properties`:
   ```properties
   ndk.dir=/path/to/android-ndk
   sdk.dir=/path/to/android-sdk
   ```

### 3. Build Project
```bash
# Debug build
./gradlew assembleProdDebug

# Release build  
./gradlew assembleProdRelease

# Generate bundle for Play Store
./gradlew bundleProdRelease
```

### 4. Install on Device
```bash
# Install debug APK
./gradlew installProdDebug

# Or install manually
adb install app/build/outputs/apk/prod/debug/IRCamera_1.10.000.*.apk
```

## Key Features by Module

### Core Application (`app/`)
- Main activity and navigation
- Device connection management
- Gallery and file management
- Settings and configuration

### Thermal Module (`component/thermal/`)
- Core thermal imaging algorithms
- Temperature measurement and analysis
- Thermal data processing and visualization

### Thermal-IR Module (`component/thermal-ir/`)
- Advanced IR image processing
- Enhanced thermal analysis features
- Professional measurement tools

### House Module (`component/house/`)
- Building inspection workflows
- Report generation
- House-specific thermal analysis

### User Module (`component/user/`)
- User authentication and management
- Account settings and preferences
- License management integration

## Device Integration

### USB Device Support
The application supports USB thermal camera devices with automatic detection:
```xml
<intent-filter>
    <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
</intent-filter>
```

### Supported Operations
- **Image Capture** - Single frame and continuous capture modes
- **Video Recording** - Thermal video with temperature data
- **Temperature Measurement** - Point, line, and area measurements
- **Device Calibration** - Automatic and manual calibration procedures
- **Firmware Updates** - Over-the-air and USB firmware updates

## File Formats

### Supported Input
- **.ir** - Native thermal image format
- **.jpg/.png** - Standard image formats
- **.mp4** - Video recordings
- **.pdf** - Documentation and reports

### Export Formats
- **Thermal Images** - Enhanced thermal data with overlays
- **Temperature Data** - CSV/Excel format for analysis
- **Reports** - PDF format with thermal analysis
- **Raw Data** - Binary thermal sensor data

## Configuration

### Device Types
```kotlin
enum class DirType {
    LINE,           // Line thermal cameras
    TC007,          // TC007 thermal camera
    TS004_LOCALE,   // Local TS004 device
    TS004_REMOTE,   // Remote TS004 device
}
```

### File Paths
The application manages various file storage locations based on Android version and device capabilities, automatically handling scoped storage requirements.

## Permissions

Required permissions for full functionality:
- **USB_HOST** - USB device communication
- **BLUETOOTH** - Bluetooth device connectivity
- **READ_EXTERNAL_STORAGE** - Access to saved files
- **WRITE_EXTERNAL_STORAGE** - File creation and management
- **CAMERA** - Camera access for dual-mode devices
- **INTERNET** - Network communication for remote features

## Troubleshooting

### Common Issues

1. **USB Device Not Detected**
   - Check USB OTG cable connection
   - Verify device compatibility in `ir_device_filter.xml`
   - Ensure proper permissions granted

2. **Thermal Images Not Saving**
   - Check storage permissions
   - Verify available storage space
   - Check file path configuration in `FileConfig`

3. **Firmware Update Failed**
   - Ensure stable USB connection
   - Check device battery level
   - Verify firmware file integrity

### Debug Mode
Enable debug logging by setting log level in application settings or through developer options.

## Contributing

1. Follow Android development best practices
2. Maintain MVVM architecture patterns
3. Add appropriate documentation for new features
4. Test on supported thermal camera devices
5. Update version numbers in `depend.gradle`

## License

This project contains proprietary thermal imaging algorithms and device-specific SDKs. Please refer to individual license files for component-specific licensing terms.

---

**Developed by TOPDON Technology**  
For technical support and device compatibility questions, contact the development team.