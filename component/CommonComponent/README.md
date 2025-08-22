# CommonComponent Documentation

## Overview
CommonComponent is the foundational module of the IRCamera application, providing shared utilities, base classes, and common functionality used across all other components. This module has achieved **BUILD SUCCESSFUL** status and serves as the stable foundation for the entire application architecture.

## Architecture

### Core Package Structure
```
com.topdon.lib.core/
├── bean/               # Data models and configuration beans
├── common/             # Shared utilities and managers
├── dialog/             # Reusable dialog components
├── utils/              # Utility classes for common operations
├── view/               # Base view classes and custom components
└── ui/
    └── widget/         # Custom UI widgets for thermal applications
```

## Key Components

### 1. Data Models (Bean Package)

#### CameraItemBean
Configuration bean for thermal camera support with menu item functionality:
```kotlin
data class CameraItemBean(
    var type: Int = 0,           // Menu item type
    var time: Int = 0,           // Delay time for capture
    var isSel: Boolean = false   // Selection state
) {
    // Cycling through delay times
    fun changeDelayType()
    
    companion object {
        const val TYPE_SETTING = 100    // Settings menu
        const val TYPE_DELAY = 101      // Delay capture
        const val TYPE_ZDKM = 102       // Auto shutter
        const val TYPE_SDKM = 103       // Manual shutter
        const val TYPE_AUDIO = 104      // Audio recording
    }
}
```

#### ContinuousBean
Configuration for continuous monitoring functionality:
```kotlin
data class ContinuousBean(
    var isOpen: Boolean = false,
    var count: Int = 5,
    var continuaTime: Int = 1000,
    var interval: Int = 1000
)
```

#### WatermarkBean
Professional watermark configuration for thermal images:
```kotlin
data class WatermarkBean(
    var isOpen: Boolean = false,
    var title: String = "",
    var address: String = "",
    var isAddTime: Boolean = true,
    var textSize: Float = 24f,
    var textColor: Int = 0xFFFFFFFF.toInt()
)
```

### 2. Shared Management (Common Package)

#### SharedManager
Centralized application settings and configuration management:
```kotlin
object SharedManager {
    // Temperature settings
    var temperatureUnit: String
    var isAutoSave: Boolean
    
    // Thermal features
    var continuousBean: ContinuousBean
    var watermarkBean: WatermarkBean
    var isTipShutter: Boolean
    
    // UI preferences
    var isNeedShowTrendTips: Boolean
    var isTipPinP: Boolean
}
```

### 3. Utility Classes

#### BitmapUtils
Comprehensive bitmap processing for thermal images:
```kotlin
class BitmapUtils {
    companion object {
        fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap
        fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap
        fun mergeBitmap(background: Bitmap?, overlay: Bitmap?, x: Int, y: Int): Bitmap?
        fun mergeBitmapByView(background: Bitmap?, overlay: Bitmap?, view: View?): Bitmap?
        fun drawCenterLable(bitmap: Bitmap?, text: String): Bitmap?
    }
}
```

#### ImageUtils
Professional image management and storage:
```kotlin
class ImageUtils {
    companion object {
        fun saveImageToApp(bitmap: Bitmap): String?
        fun saveBitmapToFile(bitmap: Bitmap, file: File, quality: Int = 90): Boolean
        fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap
        fun getNowTime(): String
    }
}
```

#### TimeTool
Time formatting and management utilities:
```kotlin
object TimeTool {
    fun getNowTime(format: String = "yyyy-MM-dd HH:mm:ss"): String
    fun getCurrentTimestamp(): Long
    fun formatTimestamp(timestamp: Long, format: String): String
    fun getFileTimeString(): String
}
```

### 4. Custom UI Widgets

#### LiteSurfaceView
Optimized surface view for thermal camera rendering:
```kotlin
class LiteSurfaceView : SurfaceView {
    var scaleBitmap: Bitmap?
    var temperatureRegionMode: Int
    
    fun mergeBitmap(bitmap1: Bitmap?, bitmap2: Bitmap?): Bitmap?
    fun setTemperatureDisplay(enabled: Boolean)
    fun updatePreview()
    
    companion object {
        const val REGION_MODE_CLEAN = 0
        const val REGION_MODE_OVERLAY = 1
    }
}
```

#### TemperatureView
Advanced temperature measurement and visualization:
```kotlin
class TemperatureView : View {
    var temperatureRegionMode: Int
    var isShowFull: Boolean
    val regionAndValueBitmap: Bitmap?
    
    fun setTemperature(tempBytes: ByteArray?)
    fun setImageSize(width: Int, height: Int, context: Context)
    fun setSyncimage(syncImage: Any?)
    fun setUseIRISP(useIRISP: Boolean)
    fun clear()
    fun start()
}
```

#### ThermalRecyclerView
Specialized RecyclerView for thermal UI components:
```kotlin
class ThermalRecyclerView : RecyclerView {
    var isVideoMode: Boolean
    
    fun setTwoLightSelected(type: TwoLightType, selected: Boolean)
    fun updateThermalData(data: ByteArray)
}
```

#### Other Custom Views
- **CameraPreviewView**: Real-time camera preview with thermal overlay
- **MenuView**: Professional thermal camera menu system  
- **ChartTrendView**: Temperature trend visualization
- **BitmapConstraintLayout**: Bitmap-aware layout container

### 5. Dialog Components

#### NotTipsSelectDialog
Professional dialog for user confirmations:
```kotlin
class NotTipsSelectDialog(context: Context) : Dialog(context) {
    fun setOnConfirmListener(listener: (Boolean) -> Unit): NotTipsSelectDialog
    fun setTipsResId(resId: Int): NotTipsSelectDialog
    fun setOptions(options: List<String>)
    fun setOnItemSelectListener(listener: (Int) -> Unit)
}
```

## Integration Guide

### Adding CommonComponent Dependency
In your component's `build.gradle`:
```gradle
dependencies {
    implementation project(':component:CommonComponent')
}
```

### Using Shared Utilities
```kotlin
// Bitmap processing
val processedBitmap = BitmapUtils.mergeBitmap(background, overlay, 0, 0)

// Settings management
SharedManager.temperatureUnit = "°C"
val watermarkConfig = SharedManager.watermarkBean

// Time utilities
val timestamp = TimeTool.getNowTime()
```

### Custom View Integration
```kotlin
// In your layout XML
<com.topdon.lib.ui.widget.temperature.TemperatureView
    android:id="@+id/temperatureView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

// In your Activity/Fragment
val temperatureView = findViewById<TemperatureView>(R.id.temperatureView)
temperatureView.setTemperature(thermalData)
temperatureView.setImageSize(320, 240, this)
```

## Testing

CommonComponent includes comprehensive unit tests covering:

### Utility Testing
- **BitmapUtils Tests**: Image processing operations
- **SharedManager Tests**: Configuration persistence
- **TimeTool Tests**: Time formatting accuracy

### UI Component Testing  
- **TemperatureView Tests**: Temperature display accuracy
- **Dialog Tests**: User interaction validation

### Integration Testing
- **Cross-component Communication**: Settings propagation
- **Resource Management**: Memory usage optimization

## Performance Considerations

### Memory Management
- Efficient bitmap processing with recycling
- Lazy initialization of UI components
- Proper cleanup of resources

### Optimization Features
- View caching for frequently accessed components
- Background processing for heavy operations
- Efficient data binding patterns

## API Documentation

### Public Interfaces
All public classes and methods are fully documented with:
- Parameter descriptions and types
- Return value specifications  
- Usage examples
- Performance considerations

### Extension Points
CommonComponent provides extension interfaces for:
- Custom temperature listeners
- Dialog event handling
- Bitmap processing pipelines
- Settings validation

## Version Compatibility

### Android Support
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Architecture Support**: ARM, ARM64, x86, x86_64

### Dependency Compatibility
- **Kotlin**: 2.0+ compatible
- **AndroidX**: Full support
- **Modern Libraries**: EventBus, Glide, MPAndroidChart

## Migration Guide

### From Legacy Versions
1. Update import statements to new package structure
2. Replace deprecated synthetic imports with findViewById
3. Update dialog usage patterns to new API
4. Migrate settings to SharedManager

### Breaking Changes
- Synthetic imports removed (use findViewById)
- Dialog API simplified and improved
- Settings management centralized
- Custom view APIs standardized

---

**CommonComponent** serves as the robust foundation for the IRCamera application, providing professional-grade utilities and components with comprehensive testing and documentation.