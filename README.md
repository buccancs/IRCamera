# IRCamera - TC001 Thermal Imaging Application

A modern Android thermal imaging application designed specifically for TC001 thermal cameras, built with Kotlin 2.0 and focused on local-only operation for enhanced privacy and performance.

## 🚀 Key Features

### Thermal Imaging
- **TC001 Device Support**: Optimized exclusively for TC001 thermal camera devices
- **Real-time Thermal Imaging**: Live thermal image capture and processing
- **Temperature Measurement**: Accurate temperature readings with configurable units (Celsius/Fahrenheit)
- **Thermal Analysis**: Advanced thermal analysis tools with monitoring capabilities
- **Image/Video Capture**: Capture thermal images and videos with metadata

### Local-Only Operation
- **No User Authentication**: Completely local operation without network dependencies
- **Privacy First**: All data stays on your device - no cloud storage or remote servers
- **Offline Functionality**: Full functionality without internet connection
- **Local Data Storage**: Thermal images, videos, and settings stored locally

### Modern Architecture
- **Kotlin 2.0**: Built with the latest Kotlin compiler for improved performance
- **KSP Integration**: Kotlin Symbol Processing for fast annotation processing
- **Modern UI Components**: Replaced deprecated synthetic views with findViewById pattern
- **Modular Design**: Clean, maintainable architecture with separated components

## 📱 System Requirements

- **Android Version**: Android 7.0 (API level 24) or higher
- **Hardware**: TC001 thermal camera device required
- **Storage**: Minimum 100MB available storage for app and thermal data
- **RAM**: 4GB+ recommended for optimal performance

## 🛠️ Technical Architecture

### Core Components
```
IRCamera/
├── app/                    # Main application module
├── component/
│   ├── thermal-ir/        # Unified thermal imaging module (TC001)
│   ├── user/             # User interface and settings
│   ├── house/            # House inspection features
│   ├── pseudo/           # Pseudo color processing
│   └── transfer/         # Data transfer utilities
├── libmatrix/            # Matrix processing library
└── depend.gradle         # Dependency management
```

### Technology Stack
- **Language**: Kotlin 2.0.0 
- **Annotation Processing**: KSP (Kotlin Symbol Processing) 2.0.0-1.0.21
- **UI Framework**: Android Views with modern findViewById pattern
- **Database**: Room database for local data storage
- **Image Processing**: Custom thermal processing libraries
- **Coroutines**: kotlinx-coroutines-android 1.7.3 for async operations

## 🔧 Installation & Setup

### Prerequisites
1. Android Studio Arctic Fox or newer
2. JDK 17 or higher
3. TC001 thermal camera device

### Build Instructions
1. **Clone the repository**:
   ```bash
   git clone https://github.com/buccancs/IRCamera.git
   cd IRCamera
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory

3. **Build the project**:
   ```bash
   ./gradlew build
   ```

4. **Install on device**:
   ```bash
   ./gradlew installDebug
   ```

### Release Build
For production builds:
```bash
./gradlew assembleRelease
```

## 📊 Usage Guide

### First Launch
1. Connect your TC001 thermal camera device
2. Grant necessary permissions (Camera, Storage)
3. The app will automatically detect and configure for TC001

### Basic Operations

#### Temperature Measurement
- Point the camera at your target
- Tap on the screen to set measurement points
- View real-time temperature readings
- Switch between Celsius and Fahrenheit in Settings

#### Capture Media
- **Photo**: Tap the camera button for instant thermal image capture
- **Video**: Hold the camera button to record thermal video
- **Gallery**: Access captured content through the gallery tab

#### Settings Configuration
- **Temperature Unit**: Choose between Celsius/Fahrenheit
- **Auto Save**: Configure automatic saving to device storage
- **Language**: Select preferred language interface
- **Thermal Settings**: Adjust thermal sensitivity and color palettes

### Data Management
All captured thermal images and videos are stored locally on your device:
- **Path**: `/storage/emulated/0/Android/data/com.topdon.tc001/`
- **Format**: Thermal data saved with metadata for analysis
- **Export**: Share thermal images with embedded temperature data

## 🔒 Privacy & Security

### Local-Only Architecture
- **No Network Requests**: App operates entirely offline
- **No User Accounts**: No registration or login required
- **Local Data Storage**: All thermal data stays on your device
- **No Telemetry**: No usage analytics or data collection

### Data Protection
- Thermal images contain sensitive temperature information
- All data remains under user control
- No cloud synchronization or backup
- Manual export only when user chooses

## 🚀 Recent Major Updates (v2.0)

### Kotlin 2.0 Migration
- Upgraded from Kotlin 1.x to 2.0.0 for improved performance
- Integrated KSP (Kotlin Symbol Processing) replacing KAPT
- Updated all coroutines dependencies to 1.7.3
- Modern annotation processing for faster builds

### Architecture Modernization
- **Synthetic Views Removal**: Converted 97 files from deprecated `kotlinx.android.synthetic` to modern `findViewById`
- **ARouter Removal**: Replaced complex routing system with standard Android intents
- **Thermal Module Consolidation**: Merged thermal-lite into thermal-ir for simplified TC001-only support
- **User Authentication Removal**: Eliminated all network-dependent login functionality

### Performance Improvements
- **Faster Build Times**: KSP provides 2x faster annotation processing than KAPT
- **Reduced APK Size**: Removed unused routing and authentication libraries
- **Memory Optimization**: Modern view binding reduces memory allocation
- **Startup Performance**: Local-only operation eliminates network delays

## 🛠️ Development

### Code Structure
The project follows a modular architecture with clear separation of concerns:

```kotlin
// Modern view binding pattern (replaces synthetic imports)
class ThermalActivity : BaseActivity() {
    override fun initView() {
        val temperatureText = findViewById<TextView>(R.id.tv_temperature)
        val captureButton = findViewById<Button>(R.id.btn_capture)
        // Clean, explicit view references
    }
}
```

### Building Components
Each component can be built independently:
```bash
./gradlew :component:thermal-ir:build
./gradlew :component:user:build
```

### Testing
Run the test suite:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Code Style
The project follows Kotlin coding conventions:
- Modern Kotlin 2.0 features
- Explicit findViewById instead of synthetic imports
- Coroutines for async operations
- Room database for local storage

## 🤝 Contributing

### Development Guidelines
1. **Kotlin 2.0**: All new code should use Kotlin 2.0 features
2. **Modern Views**: Use findViewById pattern, not synthetic imports  
3. **Local-Only**: No network dependencies in new features
4. **TC001 Focus**: New features should support TC001 devices only
5. **Privacy**: Maintain local-only operation principle

### Code Quality
- All synthetic imports must be converted to findViewById
- Use KSP for annotation processing (not KAPT)
- Follow established coroutines patterns
- Maintain Room database schemas for local storage

### Pull Request Process
1. Ensure all synthetic imports are converted
2. Test with TC001 device hardware
3. Verify no network dependencies added
4. Update documentation for new features

## 📄 License

This project is proprietary software developed for TC001 thermal camera devices.

## 📞 Support

For technical support or questions:
- Create an issue in the GitHub repository
- Ensure TC001 device compatibility for hardware issues
- Check local permissions for app functionality issues

## 🔄 Version History

### v2.0.0 (Current)
- Kotlin 2.0 upgrade with KSP integration
- Complete removal of user authentication (local-only)
- Thermal module consolidation for TC001-only support
- Modern view binding (findViewById) replacing synthetic imports
- ARouter removal - standard Android navigation
- Performance and security improvements

### v1.x
- Legacy versions with network authentication
- Multiple device support (TC001, TC007, TS004)
- KAPT-based annotation processing
- Synthetic view imports (deprecated)

---

**IRCamera v2.0** - Modern thermal imaging for TC001 devices with privacy-first, local-only operation.