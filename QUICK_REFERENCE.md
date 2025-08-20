# IRCamera Repository - Quick Reference

## Folder Responsibilities Summary

| 📁 Folder | 🏗️ Type | 📝 Description | ⚡ Status |
|----------|---------|----------------|----------|
| `app/` | Application | Main Android app module and entry point | ✅ Active |
| `libapp/` | Core Library | Shared functionality, repositories, file management | 📄 Has Config |
| `component/` | Features | Modular components (thermal, user, UI, etc.) | 📄 Has Config |
| `↳ thermal07/` | Feature | TC007 thermal camera integration | 📄 Has Config |
| `↳ thermal-ir/` | Feature | IR thermal processing with video | 📄 Has Config |
| `↳ thermal-lite/` | Feature | Lightweight thermal features | 📄 Has Config |
| `↳ thermal04/` | Feature | TC004/TS004 device support | 📄 Has Config |
| `↳ thermal-hik/` | Feature | Hikvision camera integration | 📄 Has Config |
| `↳ user/` | Feature | User management and authentication | 📄 Has Config |
| `↳ house/` | Feature | Home screen and navigation | 📄 Has Config |
| `↳ edit3d/` | Feature | 3D editing capabilities | 📄 Has Config |
| `↳ CommonComponent/` | Feature | Shared component utilities | 📄 Has Config |
| `LocalRepo/` | Custom Libs | Project-specific libraries | ⚠️ Partial |
| `↳ libcommon/` | Library | Common utility functions | ✅ Active |
| `↳ libac020/` | Library | AC020 device-specific code | 📄 Has Config |
| `↳ libirutils/` | Library | Infrared utility libraries | 📄 Has Config |
| `libhik/` | Integration | Hikvision device APIs | 📄 Has Config |
| `libmatrix/` | Processing | Matrix operations for imaging | 📄 Has Config |
| `RangeSeekBar/` | UI Widget | Custom range selection component | 📄 Has Config |
| `buildSrc/` | Build Tools | Custom Gradle build logic | 🔧 Build System |
| `gradle/` | Build Config | Gradle wrapper and settings | 🔧 Build System |

**Legend:**
- ✅ **Active**: Currently included in Gradle build
- 📄 **Has Config**: Contains build.gradle but not in settings.gradle  
- ⚠️ **Partial**: Some submodules active
- 🔧 **Build System**: Build infrastructure

## Supported Devices

| Device | Module | Description |
|--------|---------|-------------|
| **TC001** | `app/` | Primary thermal camera (main app) |
| **TC007** | `component/thermal07/` | Advanced thermal device |
| **TS004** | `component/thermal04/` | Wireless thermal camera |
| **TC004** | `component/thermal04/` | Thermal imaging device |
| **Hikvision** | `component/thermal-hik/`, `libhik/` | Professional thermal cameras |
| **AC020** | `LocalRepo/libac020/` | Additional device support |

## Key Technologies Stack

- **🤖 Android**: Native Android development
- **🎯 Kotlin/Java**: Primary languages
- **🏗️ Gradle**: Modular build system  
- **🗃️ Room**: Database ORM (in libapp)
- **🎮 OpenGL**: Graphics rendering
- **📹 GSY Video**: Video playback
- **🔥 Firebase**: Analytics & Crashlytics
- **🌡️ Thermal SDKs**: Device-specific thermal imaging

## Quick Actions

```bash
# View all modules
./gradlew projects

# Build debug APK
./gradlew :app:assembleProdDebug

# Build for Google Play
build_apk_google_script.bat

# Build for Topdon
build_apk_topdon_script.bat
```

## Next Steps

1. 📖 Read the [detailed folder structure guide](FOLDER_STRUCTURE.md)
2. 🏗️ See [README.md](README.md) for full project information
3. 🔧 Check `settings.gradle` to enable additional modules if needed