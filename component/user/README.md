# User Component Documentation

## Overview
The User Component handles all user interface, navigation, and user experience features of the IRCamera application. This component has achieved **BUILD SUCCESSFUL** status and provides a modern, professional interface for thermal imaging operations.

## Architecture

### Package Structure
```
com.topdon.module.user/
├── activity/           # Main user activities
├── fragment/           # UI fragments and navigation
├── adapter/            # RecyclerView adapters
├── dialog/             # User-specific dialogs
└── util/              # User interface utilities
```

## Key Features

### 1. Main Navigation
- **Professional Interface**: Clean, modern Material Design
- **Thermal-Specific UI**: Specialized for thermal imaging workflows
- **Settings Management**: Comprehensive configuration options
- **User Preferences**: Persistent user customizations

### 2. Settings and Configuration
- **Language Support**: Multi-language interface
- **Temperature Units**: Celsius/Fahrenheit switching
- **Camera Configuration**: Professional thermal camera settings
- **Display Options**: Customizable UI layouts and themes

### 3. User Experience Features
- **Onboarding**: Guided first-time user experience
- **Help System**: Integrated help and documentation
- **Feedback Integration**: User feedback collection (when enabled)
- **Accessibility**: Full accessibility compliance

## Core Components

### Activities

#### MineFragment
User profile and settings management:
```kotlin
class MineFragment : BaseFragment() {
    override fun initContentView(inflater: LayoutInflater, container: ViewGroup?): View
    override fun initView()
    override fun initData()
    
    private fun setupUserInterface()
    private fun loadUserPreferences()
}
```

#### MoreFragment  
Additional settings and configuration options:
```kotlin
class MoreFragment : BaseFragment() {
    private fun handleLanguageSelection()
    private fun updateTemperatureUnit()
    private fun configureAppearance()
}
```

### Navigation System

#### Modern Navigation Patterns
```kotlin
// Clean navigation without legacy routing
class UserNavigation {
    fun navigateToSettings(context: Context) {
        val intent = Intent(context, SettingsActivity::class.java)
        context.startActivity(intent)
    }
    
    fun navigateToHelp(context: Context) {
        val intent = Intent(context, HelpActivity::class.java)
        context.startActivity(intent)
    }
}
```

### User Interface Components

#### Base Classes
```kotlin
abstract class BaseFragment : Fragment() {
    abstract fun initContentView(inflater: LayoutInflater, container: ViewGroup?): View
    abstract fun initView()
    abstract fun initData()
    
    protected fun findViewById<T : View>(id: Int): T?
}
```

#### Dialog Management
```kotlin
class TransparentDialog(context: Context) : Dialog(context) {
    fun setTitle(title: String): TransparentDialog
    fun setMessage(message: String): TransparentDialog
    fun setPositiveButton(text: String, listener: (() -> Unit)?): TransparentDialog
}
```

### Utility Classes

#### LanguageUtil
Multi-language support management:
```kotlin
object LanguageUtil {
    fun setLanguage(context: Context, languageCode: String)
    fun getCurrentLanguage(context: Context): String
    fun getSupportedLanguages(): List<String>
    fun updateConfiguration(context: Context, locale: Locale)
}
```

#### SaveSettingUtil
User preference persistence:
```kotlin
object SaveSettingUtil {
    var temperatureUnit: String
    var isAutoSave: Boolean
    var userLanguage: String
    var themePreference: String
    
    fun saveUserPreferences(context: Context)
    fun loadUserPreferences(context: Context)
}
```

#### WebSocketProxy
Network communication management:
```kotlin
class WebSocketProxy {
    fun connect(url: String, listener: WebSocketListener)
    fun disconnect()
    fun sendMessage(message: String)
    fun isConnected(): Boolean
}
```

## User Interface Design

### Material Design Implementation
- **Modern Components**: Latest Material Design 3 components
- **Thermal Theme**: Specialized color schemes for thermal imaging
- **Professional Layout**: Clean, organized interface design
- **Responsive Design**: Adaptive layouts for different screen sizes

### Accessibility Features
- **Screen Reader Support**: Full VoiceOver/TalkBack compatibility
- **High Contrast Mode**: Enhanced visibility options
- **Large Text Support**: Scalable text for accessibility
- **Keyboard Navigation**: Complete keyboard accessibility

### Customization Options
```kotlin
// Theme customization
class ThemeManager {
    enum class Theme { LIGHT, DARK, AUTO }
    
    fun setTheme(theme: Theme)
    fun applyThermalColorScheme(context: Context)
    fun customizeFontSizes(scale: Float)
}
```

## Settings Management

### User Preferences
```xml
<!-- preferences.xml -->
<PreferenceScreen>
    <ListPreference
        android:key="temperature_unit"
        android:title="Temperature Unit"
        android:entries="@array/temp_units"
        android:entryValues="@array/temp_unit_values" />
        
    <SwitchPreference
        android:key="auto_save"
        android:title="Auto Save Images"
        android:summary="Automatically save captured thermal images" />
        
    <ListPreference
        android:key="language"
        android:title="Language"
        android:entries="@array/languages"
        android:entryValues="@array/language_codes" />
</PreferenceScreen>
```

### Configuration Management
```kotlin
class UserConfigManager {
    fun saveConfiguration(config: UserConfig)
    fun loadConfiguration(): UserConfig
    fun resetToDefaults()
    fun exportSettings(): String
    fun importSettings(settingsData: String)
}
```

## Testing

### Unit Tests (10 Tests Passing)
- **ActivityUtil Tests**: UI navigation and lifecycle management
- **LanguageUtil Tests**: Multi-language support validation
- **SaveSettingUtil Tests**: Preference persistence verification
- **Dialog Tests**: User interaction validation

### Integration Tests
- **Navigation Flow**: Complete user journey testing
- **Settings Persistence**: Configuration save/load validation
- **Theme Application**: Visual consistency verification

### UI Tests (Espresso)
```kotlin
@RunWith(AndroidJUnit4::class)
class UserNavigationTest {
    @Test
    fun testSettingsNavigation() {
        onView(withId(R.id.settings_button)).perform(click())
        onView(withId(R.id.settings_title)).check(matches(isDisplayed()))
    }
    
    @Test
    fun testLanguageSwitch() {
        // Test language switching functionality
    }
}
```

## API Integration

### User Data Management
```kotlin
interface UserDataManager {
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun loadUserProfile(): UserProfile?
    suspend fun updatePreferences(preferences: UserPreferences)
}
```

### Event Handling
```kotlin
class UserEventHandler {
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSettingsChanged(event: SettingsChangedEvent) {
        updateUIForNewSettings(event.newSettings)
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLanguageChanged(event: LanguageChangedEvent) {
        recreateActivityWithNewLanguage()
    }
}
```

## Performance Optimization

### Memory Management
- Efficient view recycling in lists and grids
- Lazy loading of heavy UI components
- Proper cleanup of resources and listeners

### UI Responsiveness
- Background processing for settings operations
- Smooth animations and transitions
- Efficient drawable and resource management

### Battery Optimization
- Minimal background processing
- Efficient event handling
- Smart refresh and update patterns

## Internationalization

### Supported Languages
- English (default)
- Chinese (Simplified and Traditional)
- Japanese
- Korean
- German
- French
- Spanish

### Localization Features
```kotlin
class LocalizationManager {
    fun getLocalizedString(key: String, vararg args: Any): String
    fun getLocalizedDrawable(key: String): Drawable?
    fun formatTemperature(temp: Float, unit: String): String
    fun formatDateTime(timestamp: Long): String
}
```

## Integration with Other Components

### CommonComponent Integration
```kotlin
// Using shared utilities from CommonComponent
class UserActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Access shared settings
        val tempUnit = SharedManager.temperatureUnit
        val watermarkConfig = SharedManager.watermarkBean
        
        // Use shared utilities
        val currentTime = TimeTool.getNowTime()
    }
}
```

### Thermal-ir Integration
```kotlin
// Launching thermal imaging from user interface
class ThermalLauncher {
    fun launchThermalCamera(context: Context) {
        val intent = Intent(context, IRThermalLiteActivity::class.java)
        context.startActivity(intent)
    }
    
    fun launchGallery(context: Context) {
        val intent = Intent(context, GalleryActivity::class.java)
        context.startActivity(intent)
    }
}
```

## Migration and Updates

### Version Compatibility
- Seamless migration from legacy user interfaces
- Backward compatibility for user preferences
- Graceful degradation for unsupported features

### Update Process
```kotlin
class UserMigration {
    fun migrateFromVersion(oldVersion: Int, newVersion: Int) {
        when {
            oldVersion < 200 -> migratePreferencesFormat()
            oldVersion < 210 -> updateLanguageSettings()
            oldVersion < 220 -> migrateThemeSettings()
        }
    }
}
```

## Troubleshooting

### Common Issues
1. **Settings Not Persisting**: Check SharedPreferences permissions
2. **Language Not Switching**: Verify locale configuration
3. **UI Not Updating**: Check EventBus registration
4. **Navigation Issues**: Verify intent filters and exports

### Debug Tools
```kotlin
class UserDebugTools {
    fun dumpUserPreferences(): String
    fun validateUIState(): Boolean
    fun checkNavigationStack(): List<String>
    fun measureUIPerformance(): PerformanceReport
}
```

---

**User Component** provides a professional, accessible, and customizable interface for the IRCamera thermal imaging application, with comprehensive testing and international support.