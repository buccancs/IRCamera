# IRCamera - Thermal Imaging Application

A modern Android thermal imaging application built with advanced thermal processing capabilities, supporting multiple thermal camera devices including TC001, TC007, and TS004 models.

## 🚀 Key Features

### Thermal Imaging
- **Multi-Device Support**: Supports TC001, TC007, and TS004 thermal camera devices
- **Real-time Thermal Imaging**: Live thermal image capture and processing
- **Temperature Measurement**: Accurate temperature readings with configurable units (Celsius/Fahrenheit)
- **Advanced Thermal Analysis**: Sophisticated thermal analysis tools with monitoring capabilities
- **Image/Video Capture**: Capture thermal images and videos with metadata
- **3D Visualization**: Advanced 3D thermal data representation and analysis

### User Interface & Management
- **User Account System**: Comprehensive user registration and authentication
- **Settings Management**: Configurable temperature units, auto-save preferences, and thermal settings
- **Gallery Management**: Organized media management with advanced filtering
- **Data Transfer**: Cloud synchronization and local data sharing capabilities

### Modern Architecture
- **Kotlin 2.0**: Built with the latest Kotlin compiler for improved performance
- **KSP Integration**: Kotlin Symbol Processing for fast annotation processing
- **Modern UI Components**: Replaced deprecated synthetic views with findViewById pattern
- **Modular Design**: Clean, maintainable architecture with separated components

## 📱 System Requirements

- **Android Version**: Android 7.0 (API level 24) or higher
- **Hardware**: TC001, TC007, or TS004 thermal camera device required
- **Storage**: Minimum 200MB available storage for app and thermal data
- **RAM**: 4GB+ recommended for optimal thermal processing performance
- **Network**: Internet connection required for user authentication and cloud features

## 🛠️ Technical Architecture

### Core Components
```
IRCamera/
├── app/                    # Main application (640 files)
├── component/
│   ├── CommonComponent/    # Shared utilities (44 files)
│   ├── thermal-ir/        # Advanced thermal processing (199 files)
│   ├── house/             # Building inspection features (29 files)
│   ├── pseudo/            # Color processing (6 files)
│   ├── transfer/          # Data transfer utilities (4 files)
│   └── user/             # User management (15 files)
├── libmatrix/             # Matrix processing library (15 files)
└── external/             # External dependencies
```

**Total:** 952 Java/Kotlin source files with 131,944 lines of code

### Technology Stack
- **Language**: Kotlin with Java interoperability
- **UI Framework**: Android Views with modern findViewById pattern
- **Database**: Room database for local data storage
- **Image Processing**: OpenCV and custom thermal processing libraries
- **Network**: Retrofit for API communication and cloud synchronization
- **Architecture**: MVVM with Repository pattern and LiveData

## 🔧 Installation & Setup

### Prerequisites
1. Android Studio Hedgehog or newer
2. JDK 17 or higher
3. Compatible thermal camera device (TC001, TC007, or TS004)

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
./gradlew assembleProdRelease
./gradlew bundleProdRelease
```

## 📊 Usage Guide

### First Launch
1. Connect your thermal camera device (TC001, TC007, or TS004)
2. Create user account or sign in
3. Grant necessary permissions (Camera, Storage)
4. The app will automatically detect and configure your thermal camera

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
- **User Account**: Manage profile and authentication settings
- **Temperature Unit**: Choose between Celsius/Fahrenheit
- **Auto Save**: Configure automatic saving to device storage
- **Cloud Sync**: Enable/disable cloud data synchronization
- **Thermal Settings**: Adjust thermal sensitivity and color palettes

### Data Management
Thermal images and videos can be stored both locally and synchronized to cloud storage:
- **Local Path**: `/storage/emulated/0/Android/data/com.topdon.tc001/`
- **Cloud Storage**: Configurable cloud synchronization for backup and sharing
- **Format**: Thermal data saved with metadata for comprehensive analysis
- **Export**: Share thermal images with embedded temperature data
- **Transfer**: Data transfer utilities for sharing between devices

## 🔒 Privacy & Security

### User Account Security
- **Secure Authentication**: User registration and login system
- **Data Encryption**: Encrypted local storage for sensitive thermal data
- **Permission Management**: Granular control over app permissions
- **Cloud Privacy**: Optional cloud synchronization with privacy controls

### Data Protection
- **Local Storage**: Primary data storage remains on device
- **Secure Transfer**: Encrypted data transfer capabilities  
- **User Control**: Full control over data sharing and cloud sync
- **Thermal Data Security**: Temperature data protected with metadata encryption

## 🚀 Recent Major Updates

### Current Architecture
- **Modern Android Development**: Updated to current Android development standards
- **Multi-Device Support**: Enhanced support for TC001, TC007, and TS004 cameras
- **Advanced Thermal Processing**: Sophisticated thermal analysis with OpenCV integration
- **User Management System**: Comprehensive user authentication and account management

### Enhanced Features
- **Cloud Integration**: Optional cloud synchronization for data backup and sharing
- **Advanced UI Components**: Modern Android UI with enhanced user experience
- **Improved Performance**: Optimized thermal processing with native library integration
- **Building Inspection Tools**: Specialized tools for construction and maintenance workflows

## 🛠️ Development

### Code Structure
The project follows a modular architecture with 8 main modules:

```kotlin
// Modern thermal processing with device support
class ThermalActivity : BaseActivity() {
    override fun initView() {
        val temperatureText = findViewById<TextView>(R.id.tv_temperature)
        val captureButton = findViewById<Button>(R.id.btn_capture)
        // Supports TC001, TC007, and TS004 devices
    }
}
```

### Building Components
Each component can be built independently:
```bash
./gradlew :component:thermal-ir:assembleProdDebug
./gradlew :component:user:assembleProdDebug
./gradlew :component:CommonComponent:assembleDebug
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
1. **Multi-Device Support**: Ensure new features support TC001, TC007, and TS004 cameras
2. **Modern Android**: Use current Android development practices and libraries
3. **User Experience**: Maintain intuitive user interface and smooth workflows
4. **Thermal Processing**: Optimize for real-time thermal data processing
5. **Modular Design**: Follow established modular architecture patterns

### Code Quality
- Use modern findViewById pattern for view binding
- Follow established MVVM and Repository patterns
- Implement proper error handling and user feedback
- Maintain comprehensive thermal data processing capabilities
- Ensure proper user authentication and data security

### Pull Request Process
1. Test with supported thermal camera devices (TC001, TC007, TS004)
2. Verify user authentication and cloud features work correctly
3. Ensure thermal processing performance is maintained
4. Update documentation for new features
5. Follow established modular architecture patterns

## 📄 License

This project is proprietary software developed for TC001 thermal camera devices.

## 📞 Support

For technical support or questions:
- Create an issue in the GitHub repository
- Ensure compatible thermal camera device for hardware issues
- Check user account and permissions for app functionality issues
- Verify cloud connectivity for synchronization problems

## 🔄 Version History

### Current Version
- **Multi-device support**: TC001, TC007, and TS004 thermal cameras
- **User account system**: Registration, authentication, and profile management
- **Advanced thermal processing**: Enhanced IR analysis with OpenCV integration
- **Cloud integration**: Optional data synchronization and backup
- **Building inspection tools**: Specialized workflows for construction analysis
- **Modern Android architecture**: MVVM, Repository pattern, and modular design

### Key Features
- 952 Java/Kotlin source files with 131,944 lines of code
- 8 modular components for specialized functionality
- 56 native libraries for optimized thermal processing
- Advanced 3D thermal visualization capabilities
- Comprehensive data transfer and cloud sync options

---

**IRCamera** - Professional thermal imaging for Android with multi-device support and advanced analysis capabilities.