# IRCamera Repository - Folder Structure Guide

This document explains the responsibility and purpose of each top-level folder in the IRCamera repository.

## Overview

The IRCamera project is an Android application for thermal infrared cameras, built with a modular architecture. The main application supports multiple thermal imaging devices (TC001, TC007, TS004, etc.) and provides various thermal imaging capabilities.

## Quick Reference Table

| Folder | Type | Primary Responsibility |
|--------|------|----------------------|
| `app/` | Application | Main Android application module and entry point |
| `libapp/` | Library | Core shared functionality and infrastructure |
| `component/` | Feature Modules | Feature-specific components (thermal, user, UI, etc.) |
| `LocalRepo/` | Custom Libraries | Project-specific utilities and device libraries |
| `libhik/` | Integration | Hikvision thermal camera integration |
| `libmatrix/` | Processing | Matrix operations and image processing |
| `RangeSeekBar/` | UI Component | Custom range selection widget |
| `buildSrc/` | Build System | Custom build logic and Gradle plugins |
| `commonlibrary/` | Shared Resources | Additional common libraries |
| `gradle/` | Build Config | Gradle wrapper and configuration |

## Top-Level Folder Structure

### 📱 **app/**
- **Purpose**: Main Android application module
- **Contents**: 
  - Primary application entry point (`com.topdon.tc001.app.App`)
  - Main AndroidManifest.xml with app permissions and configuration
  - Application-level resources, assets, and UI components
  - Build configuration for the primary application
- **Key Files**: `AndroidManifest.xml`, `build.gradle`, Application class
- **Dependencies**: Depends on `libapp` and various `component` modules

### 🔧 **libapp/**
- **Purpose**: Core shared library module
- **Contents**:
  - Common functionality shared across all components
  - Core repository classes (`TS004Repository`, `GalleryRepository`)
  - Configuration classes (`FileConfig`) for file management
  - Database schemas and Room database configuration
  - Base infrastructure and utilities
- **Key Features**: File management, network repositories, gallery handling
- **Dependencies**: Base library that other modules depend on

### 🧩 **component/**
- **Purpose**: Feature-specific modules organized by functionality
- **Sub-modules**:
  - **thermal*/** - Various thermal imaging implementations:
    - `thermal07/`: TC007 device thermal imaging
    - `thermal-ir/`: Infrared thermal processing with video playback
    - `thermal-lite/`: Lightweight thermal features
    - `thermal04/`: TC004/TS004 device thermal imaging
    - `thermal-hik/`: Hikvision thermal camera integration
    - `thermal/`: Base thermal functionality
  - `user/`: User management and authentication
  - `house/`: Home screen and main navigation
  - `edit3d/`: 3D editing capabilities
  - `pseudo/`: Pseudo/simulation features
  - `transfer/`: Data transfer and file operations
  - `CommonComponent/`: Shared component utilities
- **Architecture**: Each component is an independent Android library module

### 📚 **LocalRepo/**
- **Purpose**: Local repository for custom libraries and utilities
- **Sub-directories**:
  - `libcommon/`: Common utility libraries
  - `libac020/`: AC020 device-specific libraries
  - `libirutils/`: Infrared utility libraries and tools
- **Usage**: Contains project-specific libraries not available in public repositories

### 🏢 **libhik/**
- **Purpose**: Hikvision device integration library
- **Contents**: APIs and utilities for Hikvision thermal cameras
- **Functionality**: Device communication and thermal data processing for Hikvision hardware

### 🔢 **libmatrix/**
- **Purpose**: Matrix processing library
- **Contents**: Mathematical operations and matrix calculations
- **Usage**: Image processing, thermal data calculations, and geometric transformations

### 🎚️ **RangeSeekBar/**
- **Purpose**: Custom UI component
- **Contents**: Range selection seekbar widget
- **Usage**: Temperature range selection and other range-based inputs

### 🔨 **buildSrc/**
- **Purpose**: Build script source code and custom build logic
- **Contents**: Custom Gradle plugins and build utilities
- **Usage**: Centralizes build logic and custom build tasks

### 📦 **commonlibrary/**
- **Purpose**: Additional common libraries and shared resources
- **Contents**: Shared utilities not specific to the core app functionality

### 🚀 **Build Configuration Files**

#### Root Level Build Files:
- `settings.gradle`: Project structure and module inclusion
- `build.gradle`: Root project build configuration
- `depend.gradle`: Global dependency management and version control
- `gradle.properties`: Project-wide Gradle properties

#### Build Scripts:
- `build_apk_google_script.bat`: Automated build for Google Play variant
- `build_apk_topdon_script.bat`: Automated build for Topdon variant
- `build_release_*_script.bat`: Release build automation

### 📁 **gradle/**
- **Purpose**: Gradle wrapper and configuration
- **Contents**: Gradle wrapper files for consistent build environment

## Architecture Overview

```
IRCamera Project
├── app (Main Application) [ACTIVE MODULE]
├── libapp (Core Library) [HAS BUILD CONFIG]
├── component/ (Feature Modules) [MULTIPLE BUILD CONFIGS]
│   ├── thermal*/ (Thermal Imaging)
│   ├── user/ (User Management)
│   ├── house/ (Home Screen)
│   └── ... (Other Features)
├── LocalRepo/ (Custom Libraries) [PARTIALLY ACTIVE]
│   └── libcommon/ [ACTIVE MODULE]
├── libhik/ (Hikvision Integration) [HAS BUILD CONFIG]
├── libmatrix/ (Matrix Processing) [HAS BUILD CONFIG]
└── RangeSeekBar/ (UI Component) [HAS BUILD CONFIG]
```

## Current Module Status

Based on the `settings.gradle` configuration, only these modules are currently active in the Gradle build:
- ✅ `app` - Main application (active)
- ✅ `LocalRepo:libcommon` - Common utilities library (active)

The following folders contain `build.gradle` files but are not included in `settings.gradle`:
- 📄 `libapp` - Core library (has build config, not included)
- 📄 `component/*` - All component modules (have build configs, not included)
- 📄 `libhik`, `libmatrix`, `RangeSeekBar` - Additional libraries (have build configs, not included)

This suggests the project structure is designed for modular development, but the current build configuration only includes the essential modules.

## Key Technologies

- **Android**: Native Android application
- **Kotlin/Java**: Primary development languages  
- **Gradle**: Build system with modular configuration
- **Room**: Database ORM (in libapp)
- **Thermal Imaging**: Multiple device support (TC001, TC007, TS004, Hikvision)
- **Modular Architecture**: Feature-based component separation

## Build Variants

The project supports multiple build variants:
- **Google Play**: Consumer version for Google Play Store
- **Topdon**: OEM version for Topdon devices
- **Regional variants**: Inside China and international versions
- **Environment variants**: dev, beta, prod

## Getting Started

1. The main application entry point is in `app/`
2. Core functionality is provided by `libapp/`
3. Feature-specific code is organized in `component/` modules
4. Device-specific integrations are in dedicated lib modules
5. Build scripts automate compilation for different variants