# IRCamera - Module Documentation

## Overview

The IRCamera application is built using a modular architecture with 8 distinct modules, each serving specific functionality within the thermal imaging ecosystem. This document provides detailed information about each module's purpose, functionality, and technical implementation.

## Module Architecture Summary

```
IRCamera Application
├── app/                    (640 files) - Main application module
├── component/              (297 files) - Feature component modules
│   ├── thermal-ir/        (199 files) - Advanced IR processing  
│   ├── CommonComponent/   (44 files)  - Shared components
│   ├── house/             (29 files)  - Building inspection tools
│   ├── user/              (15 files)  - User account management
│   ├── pseudo/            (6 files)   - Color rendering/palettes
│   └── transfer/          (4 files)   - Data transfer utilities
└── libmatrix/             (15 files)  - Matrix operations library
```

**Total:** 952 Java/Kotlin source files with 131,944 lines of code

---

## Core Module

### 📱 app/ (Main Application Module)
**Files:** 640 • **Package:** com.topdon.*

The main application module containing the core infrastructure, device integrations, and primary business logic.

#### Key Components:
- **Device Integration:** USB thermal camera communication (TC001, TC007, TS004)
- **BLE Module:** Bluetooth Low Energy device management
- **Commons:** Shared utilities and helper classes  
- **Library Integrations:** Core libraries and native SDK wrappers
- **Main Activities:** Entry points and navigation controllers

#### Key Packages:
- `com.topdon.ble/` - Bluetooth device scanning and management
- `com.topdon.commons/` - Shared utilities and constants
- `com.topdon.lib/` - Core library integrations
- `com.topdon.tc001/`, `com.topdon.tc004/` - Device-specific implementations
- `com.infisense.*/` - USB camera integration libraries

#### Functionality:
- Application initialization and dependency injection
- Device discovery and connection management
- Core thermal processing pipeline
- Navigation and routing between modules
- Native library integration (56 .so files)

---

## Feature Component Modules

### 🔬 thermal-ir/ (Advanced IR Processing)
**Files:** 199 • **Package:** com.topdon.module.thermal.ir

Advanced infrared processing module providing sophisticated thermal analysis capabilities.

#### Key Features:
- **Advanced Image Processing:** Enhanced thermal image rendering and analysis
- **Custom Popups/UI:** Specialized user interface components for thermal settings
- **Camera Controls:** Advanced camera parameter adjustment
- **Gallery Management:** Enhanced media management with IR-specific features
- **Real-time Processing:** Live thermal data analysis and visualization

#### Key Components:
- Advanced thermal rendering algorithms
- IR-specific image processing pipelines
- Real-time thermal stream processing
- Temperature calibration and correction algorithms
- Multi-format thermal data export capabilities

#### Technical Implementation:
- Custom OpenCV integrations for image processing
- Advanced thermal calibration algorithms
- Real-time parameter adjustment capabilities
- Enhanced thermal data export formats
- Support for TC001, TC007, and TS004 cameras

#### Applications:
- Professional thermal analysis
- Industrial temperature monitoring
- Research and development workflows
- Advanced thermal imaging applications

---

### 🔧 CommonComponent/ (Shared Components)
**Files:** 44 • **Package:** com.energy.commoncomponent

Shared utilities and common components used across multiple modules.

#### Key Features:
- **Common Constants:** Application-wide constant definitions
- **Device Type Management:** Device model and capability definitions
- **Rotation Utilities:** Screen orientation and image rotation handling
- **Shared Data Structures:** Common beans and data models
- **Base Classes:** Foundation classes for activities and fragments

#### Key Components:
- `Const` - Application constants and configuration values
- `DeviceType` - Device model definitions and capabilities
- `RotateDegree` - Image and screen rotation utilities
- Shared enums and data structures
- Common UI components and utilities

#### Shared Functionality:
- Cross-module communication protocols
- Common UI components and styles
- Shared data validation utilities
- Universal error handling and logging
- Device-specific configuration management

---

### 🏠 house/ (Building Inspection)
**Files:** 29 • **Package:** com.topdon.house

Specialized module for building and infrastructure thermal inspection workflows.

#### Key Features:
- **Building Analysis:** Thermal inspection tools for construction and maintenance
- **Inspection Workflows:** Guided processes for different building assessment types
- **Report Generation:** Building-specific thermal analysis reports
- **Template Management:** Pre-configured inspection templates
- **Data Visualization:** Specialized charts and graphs for building analysis

#### Key Components:
- Building-specific thermal analysis algorithms
- Construction industry workflow templates
- Infrastructure assessment tools
- Specialized reporting capabilities
- Integration with thermal-ir module for advanced processing

#### Applications:
- HVAC system analysis and optimization
- Insulation inspection and efficiency assessment
- Electrical system thermal monitoring
- Water leak detection and plumbing analysis
- Energy efficiency audits and compliance
- Construction quality control

---

### 👤 user/ (User Account Management)
**Files:** 15 • **Package:** com.topdon.module.user

User account management, preferences, and application settings.

#### Key Features:
- **User Authentication:** Registration, login, and account management
- **Temperature Units:** Celsius/Fahrenheit unit switching
- **Auto-save Settings:** Automatic data saving preferences
- **Electronic Manual:** In-app documentation and help system
- **User Preferences:** Personalized application settings
- **Cloud Integration:** User account synchronization

#### Key Activities:
- User registration and authentication workflows
- Profile management and settings
- Temperature unit configuration
- Automatic saving preferences
- Built-in help and documentation system

#### Configuration Options:
- Temperature measurement units (°C/°F)
- Automatic saving intervals and triggers
- Default thermal color palettes
- Device-specific user preferences
- Export format settings and defaults
- Cloud synchronization preferences

---

### 🎨 pseudo/ (Color Rendering & Palettes)
**Files:** 6 • **Package:** com.topdon.pseudo

Color processing module managing thermal image rendering and custom color palette configurations.

#### Key Features:
- **Custom Color Palettes:** User-defined thermal color schemes
- **Pseudo-color Processing:** Temperature-to-color mapping algorithms
- **Rendering Configuration:** Advanced color rendering parameter controls
- **Temperature Visualization:** Enhanced thermal data representation
- **Real-time Palette Switching:** Dynamic color scheme changes

#### Key Components:
- `PseudoSetActivity` - Custom color palette configuration interface
- `CustomPseudoBean` - Color rendering configuration data structure
- Advanced color mapping algorithms
- Real-time palette switching capabilities
- Temperature range visualization tools

#### Technical Implementation:
- HSV/RGB color space conversions
- Temperature range to color mapping
- Custom gradient generation and management
- Real-time rendering performance optimization
- Integration with thermal-ir for live processing

---

### 📡 transfer/ (Data Transfer)
**Files:** 4 • **Package:** com.topdon.transfer

Data transfer and synchronization module for sharing thermal data across devices and platforms.

#### Key Features:
- **Data Export:** Thermal data export in multiple formats
- **Cloud Sync:** Remote data synchronization capabilities
- **File Transfer:** Local and network file transfer utilities
- **Transfer Dialogs:** User interface for transfer operations
- **Format Conversion:** Multi-format thermal data conversion

#### Key Components:
- `TransferDialog` - File transfer interface and progress tracking
- Network transfer protocols and handlers
- Data compression and optimization algorithms
- Transfer status monitoring and error handling
- Cloud storage integration utilities

#### Supported Formats:
- Native thermal data formats (.tir, .thermal)
- Standard image formats (PNG, JPEG with thermal metadata)
- Video formats with thermal data preservation
- CSV temperature data export for analysis
- Cloud storage integrations (configurable providers)

---

## Support Module

### 🧮 libmatrix/ (Matrix Operations)
**Files:** 15 • **Package:** Matrix computation library

Mathematical computation library providing matrix operations for thermal data processing.

#### Features:
- **Matrix Operations:** Linear algebra operations for thermal data processing
- **Image Processing:** Matrix-based image transformation utilities
- **Thermal Calculations:** Temperature data mathematical processing algorithms
- **Performance Optimization:** Optimized matrix computation for real-time processing
- **Data Analysis:** Statistical analysis tools for thermal datasets

#### Key Functionality:
- Matrix multiplication and linear algebra operations
- Image transformation matrices for thermal data
- Temperature calibration and correction calculations
- Statistical analysis of thermal measurements
- Performance-optimized algorithms for real-time processing

---

## Module Dependencies and Integration

### Inter-module Communication
- **Repository Pattern:** Data layer abstraction across modules
- **Shared Preferences:** Configuration sharing between modules
- **EventBus:** Cross-module event communication
- **Dependency Injection:** Module navigation and service injection

### Common Dependencies
- **AndroidX Libraries:** Modern Android component libraries
- **RxJava:** Reactive programming for asynchronous operations
- **Retrofit:** Network communication for cloud features and user authentication
- **Room Database:** Local data persistence and caching
- **OpenCV:** Advanced image processing capabilities
- **Kotlin Coroutines:** Asynchronous programming and threading

### Native Libraries Integration
All modules utilize shared native libraries for thermal processing:
- **libHCUSBSDK.so:** USB thermal camera SDK
- **libopencv_java4.so:** OpenCV computer vision library
- **FFmpeg libraries:** Video processing and encoding
- **Custom thermal processing libraries:** Proprietary thermal algorithms
- **Matrix computation libraries:** Optimized mathematical operations

## Module Build Configuration

Each module is configured as an Android library with:
- **Gradle 8.0+** build system with Kotlin DSL
- **Android 14 (API 34)** target SDK
- **Android 7.0 (API 24)** minimum SDK
- **Kotlin and Java 8** language support with modern syntax
- **R8/ProGuard** code optimization and obfuscation
- **Prod flavor** with debug and release build variants

### Available Build Tasks
```bash
# Component modules
./gradlew :component:thermal-ir:assembleProdDebug
./gradlew :component:user:assembleProdRelease
./gradlew :component:CommonComponent:assembleDebug

# Main application
./gradlew :app:assembleProdRelease
./gradlew :app:bundleProdRelease

# Support library
./gradlew :libmatrix:assembleRelease
```

## Development Guidelines

### Adding New Modules
1. Create module in `component/` directory
2. Configure `build.gradle` with shared dependencies from `depend.gradle`
3. Register module in `settings.gradle`
4. Implement proper module interfaces and contracts
5. Add comprehensive module documentation

### Module Best Practices
- Follow MVVM architecture pattern with Repository layer
- Use dependency injection for cross-module communication
- Implement proper error handling and user feedback
- Add comprehensive unit and integration tests
- Document public APIs and interfaces thoroughly
- Follow Android architecture guidelines and best practices
- Ensure compatibility with supported thermal camera devices

### Testing Guidelines
- Unit tests for business logic and data processing
- Integration tests for module interactions
- UI tests for user interface components
- Hardware tests with actual thermal camera devices
- Performance tests for thermal processing algorithms

---

## Performance Considerations

### Thermal Processing Optimization
- **Native Libraries:** 56 native .so files for optimized processing
- **Multi-threading:** Coroutines for non-blocking thermal data processing
- **Memory Management:** Efficient bitmap and thermal data handling
- **Real-time Processing:** Optimized algorithms for live thermal streaming
- **Device-specific Optimization:** Tailored processing for TC001, TC007, TS004

### Module Loading Strategy
- **Lazy Loading:** Modules loaded on-demand to reduce startup time
- **Resource Management:** Efficient memory usage across modules
- **Background Processing:** Non-UI operations handled in background threads
- **Caching Strategy:** Intelligent caching for frequently accessed thermal data

---

*Last updated: Generated from repository analysis of 952 source files and 131,944 lines of code*