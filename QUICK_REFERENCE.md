# IRCamera Repository - Quick Reference (TC001)

## Folder Responsibilities Summary

| 📁 Folder | 🏗️ Type | 📝 Description | ⚡ Status |
|----------|---------|----------------|----------|
| `app/` | Application | Main Android app module for TC001 | ✅ Active |
| `libapp/` | Core Library | Shared functionality, repositories, file management | 📄 Has Config |
| `component/` | Features | Modular components for TC001 (thermal, user, UI, etc.) | 📄 Has Config |
| `↳ thermal/` | Feature | Base thermal functionality for TC001 | 📄 Has Config |
| `↳ thermal-ir/` | Feature | IR thermal processing with video | 📄 Has Config |
| `↳ thermal-lite/` | Feature | Lightweight thermal features | 📄 Has Config |
| `↳ user/` | Feature | User management and authentication | 📄 Has Config |
| `↳ house/` | Feature | Home screen and navigation | 📄 Has Config |
| `↳ edit3d/` | Feature | 3D editing capabilities | 📄 Has Config |
| `↳ CommonComponent/` | Feature | Shared component utilities | 📄 Has Config |
| `LocalRepo/` | Custom Libs | Project-specific libraries | ⚠️ Partial |
| `↳ libcommon/` | Library | Common utility functions | ✅ Active |
| `↳ libirutils/` | Library | Infrared utility libraries | 📄 Has Config |
| `libmatrix/` | Processing | Matrix operations for imaging | 📄 Has Config |
| `RangeSeekBar/` | UI Widget | Custom range selection component | 📄 Has Config |
| `buildSrc/` | Build Tools | Custom Gradle build logic | 🔧 Build System |
| `gradle/` | Build Config | Gradle wrapper and settings | 🔧 Build System |

**Legend:**
- ✅ **Active**: Currently included in Gradle build
- 📄 **Has Config**: Contains build.gradle but not in settings.gradle  
- ⚠️ **Partial**: Some submodules active
- 🔧 **Build System**: Build infrastructure

## Supported Device

| Device | Module | Description |
|--------|---------|-------------|
| **TC001** | `app/` | Primary thermal camera (main app) |

## Key Technologies Stack

- **🤖 Android**: Native Android development
- **🎯 Kotlin/Java**: Primary languages
- **🏗️ Gradle**: Modular build system  
- **🗃️ Room**: Database ORM (in libapp)
- **🎮 OpenGL**: Graphics rendering
- **📹 GSY Video**: Video playback
- **🔥 Firebase**: Analytics & Crashlytics
- **🌡️ TC001 SDK**: Thermal imaging SDK for TC001 device

## Quick Actions

```bash
# View all modules
./gradlew projects

# Build debug APK
./gradlew :app:assembleProdDebug

# Build for TC001 Google Play
build_apk_google_script.bat

# Build for TC001 Topdon
build_apk_topdon_script.bat
```

## Next Steps

1. 📖 Read the [detailed folder structure guide](FOLDER_STRUCTURE.md)
2. 🏗️ See [README.md](README.md) for full TC001 project information
3. 🔧 Check `settings.gradle` to enable additional modules if needed