# IRCamera - Module Documentation

## Overview

The IRCamera application is built using a modular architecture with 14 distinct modules, each serving specific functionality within the thermal imaging ecosystem. This document provides detailed information about each module's purpose, functionality, and technical implementation.

## Module Architecture Summary

```
IRCamera Application
├── app/                    (964 files) - Main application module
├── component/              (287 files) - Feature component modules
│   ├── thermal/           (37 files)  - Core thermal imaging
│   ├── thermal-ir/        (145 files) - Advanced IR processing  
│   ├── thermal-lite/      (23 files)  - Lightweight thermal features
│   ├── house/             (29 files)  - Building inspection tools
│   ├── pseudo/            (6 files)   - Color rendering/palettes
│   ├── edit3d/            (6 files)   - 3D editing capabilities
│   ├── transfer/          (4 files)   - Data transfer utilities
│   ├── user/              (19 files)  - User account management
│   └── CommonComponent/   (18 files)  - Shared components
├── RangeSeekBar/          (10 files)  - Custom UI component
├── libmatrix/             (15 files)  - Matrix operations library
└── commonlibrary/         (0 files)   - Common utilities (placeholder)
```

**Total:** 1,277 Java/Kotlin source files with 192,006 lines of code

---

## Core Modules

### 📱 app/ (Main Application Module)
**Files:** 964 • **Package:** com.topdon.*

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

### 🌡️ thermal/ (Core Thermal Imaging)
**Files:** 37 • **Package:** com.topdon.module.thermal

Core thermal imaging functionality providing the foundation for thermal camera operations.

#### Key Features:
- **Real-time Thermal Display:** Live thermal imaging with temperature overlays
- **Temperature Measurement:** Point, line, and area temperature analysis
- **Video Recording:** Thermal video capture and playback
- **Gallery Management:** Thermal image and video organization
- **Connection Management:** Device connectivity and status monitoring
- **Chart/Logging:** Temperature monitoring with historical data visualization

#### Key Activities:
- `ThermalActivity` - Main thermal imaging interface
- `VideoActivity` - Thermal video recording and playback
- `GalleryActivity` - Media gallery for thermal content
- `MonitorActivity` - Real-time temperature monitoring
- `ConnectActivity` - Device connection management
- `LogMPChartActivity` - Temperature data visualization

#### Technical Implementation:
- MVVM architecture with LiveData
- Real-time thermal stream processing
- Temperature calibration and correction
- Multi-format export capabilities

---

### 🔬 thermal-ir/ (Advanced IR Processing)
**Files:** 145 • **Package:** com.topdon.module.thermal.ir

Advanced infrared processing module providing sophisticated thermal analysis capabilities.

#### Key Features:
- **Advanced Image Processing:** Enhanced thermal image rendering and analysis
- **Custom Popups/UI:** Specialized user interface components for thermal settings
- **Camera Controls:** Advanced camera parameter adjustment
- **Gallery Management:** Enhanced media management with IR-specific features
- **Seek Bar Controls:** Precision temperature range adjustments

#### Key Components:
- `GalleryChangePopup` - Advanced gallery interaction controls
- `SeekBarPopup` - Temperature range and sensitivity controls  
- `CameraItemPopup` - Camera-specific configuration interfaces
- Advanced thermal rendering algorithms
- IR-specific image processing pipelines

#### Technical Implementation:
- Custom OpenCV integrations for image processing
- Advanced thermal calibration algorithms
- Real-time parameter adjustment capabilities
- Enhanced thermal data export formats

---

### ⚡ thermal-lite/ (Lightweight Thermal)
**Files:** 23 • **Package:** com.example.thermal_lite

Simplified thermal imaging module designed for basic thermal camera operations with reduced resource usage.

#### Key Features:
- **Simplified Interface:** Streamlined thermal imaging controls
- **Basic Monitoring:** Essential temperature monitoring capabilities
- **Chart Display:** Lightweight temperature data visualization
- **Reduced Overhead:** Optimized for performance on lower-end devices

#### Key Activities:
- `IRMonitorChartLiteActivity` - Simplified thermal monitoring with charts
- Basic thermal image capture and display
- Essential temperature measurement tools

#### Use Cases:
- Entry-level thermal imaging workflows
- Performance-constrained environments
- Quick thermal assessments
- Educational or training scenarios

---

### 🏠 house/ (Building Inspection)
**Files:** 29 • **Package:** com.topdon.house

Specialized module for building and infrastructure thermal inspection workflows.

#### Key Features:
- **Building Analysis:** Thermal inspection tools for construction and maintenance
- **Three-Option Popups:** Specialized selection interfaces for inspection categories
- **Inspection Workflows:** Guided processes for different building assessment types
- **Report Generation:** Building-specific thermal analysis reports

#### Key Components:
- `ThreePickPopup` - Multi-option selection interface for inspection types
- Building-specific thermal analysis algorithms
- Construction industry workflow templates
- Infrastructure assessment tools

#### Applications:
- HVAC system analysis
- Insulation inspection
- Electrical system thermal assessment
- Water leak detection
- Energy efficiency audits

---

### 🎨 pseudo/ (Color Rendering & Palettes)
**Files:** 6 • **Package:** com.topdon.pseudo

Color processing module managing thermal image rendering and custom color palette configurations.

#### Key Features:
- **Custom Color Palettes:** User-defined thermal color schemes
- **Pseudo-color Processing:** Temperature-to-color mapping algorithms
- **Rendering Configuration:** Advanced color rendering parameter controls
- **Temperature Visualization:** Enhanced thermal data representation

#### Key Components:
- `PseudoSetActivity` - Custom color palette configuration interface
- `CustomPseudoBean` - Color rendering configuration data structure
- Advanced color mapping algorithms
- Real-time palette switching capabilities

#### Technical Implementation:
- HSV/RGB color space conversions
- Temperature range to color mapping
- Custom gradient generation
- Real-time rendering performance optimization

---

### 🛠️ edit3d/ (3D Editing Tools)
**Files:** 6 • **Package:** com.topdon.lib.menu

3D editing and visualization capabilities for thermal data manipulation.

#### Key Features:
- **3D Visualization:** Three-dimensional thermal data representation
- **3D Editing Tools:** Spatial manipulation of thermal data
- **Menu Components:** 3D-specific user interface elements
- **Custom Text Views:** Enhanced UI components for 3D workflows

#### Key Components:
- `MarqueeTextView` - Scrolling text display for 3D interface
- 3D thermal data processing utilities
- Spatial analysis tools

#### Applications:
- 3D thermal modeling
- Spatial temperature analysis
- Advanced visualization workflows
- Research and development applications

---

### 📡 transfer/ (Data Transfer)
**Files:** 4 • **Package:** com.topdon.transfer

Data transfer and synchronization module for sharing thermal data across devices and platforms.

#### Key Features:
- **Data Export:** Thermal data export in multiple formats
- **Cloud Sync:** Remote data synchronization capabilities
- **File Transfer:** Local and network file transfer utilities
- **Transfer Dialogs:** User interface for transfer operations

#### Key Components:
- `TransferDialog` - File transfer interface and progress tracking
- Network transfer protocols
- Data compression and optimization
- Transfer status monitoring

#### Supported Formats:
- Native thermal data formats
- Standard image/video formats
- CSV temperature data export
- Cloud storage integrations

---

### 👤 user/ (User Account Management)
**Files:** 19 • **Package:** com.topdon.module.user

User account management, preferences, and application settings.

#### Key Features:
- **Temperature Units:** Celsius/Fahrenheit unit switching
- **Auto-save Settings:** Automatic data saving preferences
- **Electronic Manual:** In-app documentation and help system
- **User Preferences:** Personalized application settings

#### Key Activities:
- `UnitActivity` - Temperature unit configuration
- `AutoSaveActivity` - Automatic saving preferences
- `ElectronicManualActivity` - Built-in help and documentation system
- User profile management
- Application settings and preferences

#### Configuration Options:
- Temperature measurement units
- Automatic saving intervals
- Default thermal palettes
- Device-specific preferences
- Export format settings

---

### 🔧 CommonComponent/ (Shared Components)
**Files:** 18 • **Package:** com.energy.commoncomponent

Shared utilities and common components used across multiple modules.

#### Key Features:
- **Common Constants:** Application-wide constant definitions
- **Device Type Management:** Device model and capability definitions
- **Rotation Utilities:** Screen orientation and image rotation handling
- **Shared Data Structures:** Common beans and data models

#### Key Components:
- `Const` - Application constants and configuration values
- `DeviceType` - Device model definitions and capabilities
- `RotateDegree` - Image and screen rotation utilities
- Shared enums and data structures

#### Shared Functionality:
- Cross-module communication protocols
- Common UI components
- Shared data validation utilities
- Universal error handling

---

## Support Modules

### 📊 RangeSeekBar/ (Custom UI Component)
**Files:** 10 • **Package:** Custom range selection widget

Custom Android UI component providing dual-thumb seek bar functionality for temperature range selection.

#### Features:
- **Dual-thumb Selection:** Minimum and maximum value selection
- **Temperature Ranges:** Specialized for thermal data ranges
- **Custom Styling:** Thermal application-specific appearance
- **Touch Handling:** Optimized touch interaction for thermal workflows

---

### 🧮 libmatrix/ (Matrix Operations)
**Files:** 15 • **Package:** Matrix computation library

Mathematical computation library providing matrix operations for thermal data processing.

#### Features:
- **Matrix Operations:** Linear algebra operations for thermal data
- **Image Processing:** Matrix-based image transformation utilities
- **Thermal Calculations:** Temperature data mathematical processing
- **Performance Optimization:** Optimized matrix computation algorithms

---

## Module Dependencies and Integration

### Inter-module Communication
- **ARouter:** Module navigation and dependency injection
- **EventBus:** Cross-module event communication
- **Shared Preferences:** Configuration sharing between modules
- **Repository Pattern:** Data layer abstraction across modules

### Common Dependencies
- **AndroidX Libraries:** Modern Android component libraries
- **RxJava:** Reactive programming for asynchronous operations
- **Retrofit:** Network communication for cloud features
- **Room Database:** Local data persistence
- **OpenCV:** Advanced image processing capabilities

### Native Libraries Integration
All modules utilize shared native libraries for thermal processing:
- **libHCUSBSDK.so:** USB thermal camera SDK
- **libopencv_java4.so:** OpenCV computer vision library
- **FFmpeg libraries:** Video processing and encoding
- **Custom thermal processing libraries:** Proprietary thermal algorithms

## Module Build Configuration

Each module is configured as an Android library with:
- **Gradle 7.5** build system
- **Android 14 (API 34)** target SDK
- **Android 7.0 (API 24)** minimum SDK
- **Kotlin and Java 8** language support
- **R8/ProGuard** code optimization
- **Kapt** annotation processing for ARouter

## Development Guidelines

### Adding New Modules
1. Create module in `component/` directory
2. Configure `build.gradle` with shared dependencies
3. Register module in `settings.gradle`
4. Implement ARouter navigation paths
5. Add module documentation to this file

### Module Best Practices
- Follow MVVM architecture pattern
- Use Repository pattern for data access
- Implement proper error handling
- Add comprehensive unit tests
- Document public APIs and interfaces
- Follow Android architecture guidelines

---

*Last updated: Generated from repository analysis of 1,277 source files and 192,006 lines of code*