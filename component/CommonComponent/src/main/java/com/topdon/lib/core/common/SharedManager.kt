package com.topdon.lib.core.common

import android.content.Context
import android.content.SharedPreferences
import com.topdon.lib.core.bean.ContinuousBean
import com.topdon.lib.core.bean.WatermarkBean
import com.blankj.utilcode.util.SPUtils

/**
 * SharedManager for component modules
 * Contains shared preferences functionality needed by components
 */
object SharedManager {
    
    private const val PREF_NAME = "IRCamera_Settings"
    private var prefs: SharedPreferences? = null
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Initialize SPUtils for blankj compatibility
        SPUtils.getInstance().put("__init", true)
    }
    
    // Auto sync property needed by user component
    var is04AutoSync: Boolean
        get() = SPUtils.getInstance().getBoolean("is04AutoSync", false)
        set(value) = SPUtils.getInstance().put("is04AutoSync", value)
    
    // Basic string preferences
    fun setString(key: String, value: String) {
        SPUtils.getInstance().put(key, value)
    }
    
    fun getString(key: String, defaultValue: String = ""): String {
        return SPUtils.getInstance().getString(key, defaultValue)
    }
    
    // Basic boolean preferences  
    fun setBoolean(key: String, value: Boolean) {
        SPUtils.getInstance().put(key, value)
    }
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return SPUtils.getInstance().getBoolean(key, defaultValue)
    }
    
    // Basic int preferences
    fun setInt(key: String, value: Int) {
        SPUtils.getInstance().put(key, value)
    }
    
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return SPUtils.getInstance().getInt(key, defaultValue)
    }
    
    // Basic float preferences
    fun setFloat(key: String, value: Float) {
        SPUtils.getInstance().put(key, value)
    }
    
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return prefs?.getFloat(key, defaultValue) ?: defaultValue
    }
    
    // User ID for local-only mode
    fun getUserId(): String = "local"
    
    // Language settings
    fun getLanguage(): String = getString("language", "en")
    fun setLanguage(language: String) = setString("language", language)
    
    // Unit settings (Celsius/Fahrenheit)
    fun getTemperatureUnit(): String = getString("temp_unit", "C")
    fun setTemperatureUnit(unit: String) = setString("temp_unit", unit)
    
    // Legacy temperature methods for compatibility (0=Fahrenheit, 1=Celsius)
    fun getTemperature(): Int = if (getTemperatureUnit() == "C") 1 else 0
    fun setTemperature(value: Int) = setTemperatureUnit(if (value == 1) "C" else "F")
    
    // Select fence type for thermal monitoring
    fun getSelectFenceType(): Int = getInt("select_fence_type", 0)
    fun setSelectFenceType(type: Int) = setInt("select_fence_type", type)
    
    // Thermal measurement settings
    fun getThermalMeasureType(): Int = getInt("thermal_measure_type", 0)
    fun setThermalMeasureType(type: Int) = setInt("thermal_measure_type", type)
    
    // Device connection settings
    fun getConnectedDeviceType(): String = getString("connected_device_type", "TC001")
    fun setConnectedDeviceType(type: String) = setString("connected_device_type", type)
    
    // Winter click tracking
    var hasClickWinter: Boolean
        get() = getBoolean("has_click_winter", false)
        set(value) = setBoolean("has_click_winter", value)
    
    // Device SN tracking
    fun getDeviceSn(): String = getString("device_sn", "")
    fun setDeviceSn(sn: String) = setString("device_sn", sn)
    
    // Last connected device SN
    fun getLastConnectSn(): String = getString("last_connect_sn", "")
    fun setLastConnectSn(sn: String) = setString("last_connect_sn", sn)
    
    // Car detect info
    fun getCarDetectInfo(): CarDetectInfo {
        return CarDetectInfo(
            item = getString("car_detect_item", "Default"),
            description = getString("car_detect_description", "Car detection information"),
            temperature = getString("car_detect_temperature", "40~70")
        )
    }
    
    fun setCarDetectInfo(info: CarDetectInfo) {
        setString("car_detect_item", info.item)
        setString("car_detect_description", info.description)
    }
    
    // High temperature tip tracking
    var isTipHighTemp: Boolean
        get() = getBoolean("is_tip_high_temp", true)
        set(value) = setBoolean("is_tip_high_temp", value)
    
    // Two light feature
    fun isOpenTwoLight(): Boolean = getBoolean("is_open_two_light", false)
    fun setOpenTwoLight(value: Boolean) = setBoolean("is_open_two_light", value)
    
    // Emissivity tips visibility
    fun isHideEmissivityTips(): Boolean = getBoolean("is_hide_emissivity_tips", false)
    fun isHideEmissivityTips(value: Boolean) = setBoolean("is_hide_emissivity_tips", value)
    
    // Picture in picture tip
    var isTipPinP: Boolean
        get() = getBoolean("is_tip_pinp", true)
        set(value) = setBoolean("is_tip_pinp", value)
    
    // Trend tips visibility
    var isNeedShowTrendTips: Boolean
        get() = getBoolean("is_need_show_trend_tips", true)
        set(value) = setBoolean("is_need_show_trend_tips", value)
    
    // Continuous measurement bean
    var continuousBean: ContinuousBean
        get() = try {
            // In a real implementation, this would deserialize from JSON
            ContinuousBean.createDefault()
        } catch (e: Exception) {
            ContinuousBean.createDefault()
        }
        set(value) = setString("continuous_bean", value.toString())
    
    // Tip shutter state
    var isTipShutter: Boolean
        get() = getBoolean("is_tip_shutter", false)  
        set(value) = setBoolean("is_tip_shutter", value)
    
    // Watermark configuration
    var watermarkBean: WatermarkBean
        get() = try {
            // In a real implementation, this would deserialize from JSON
            WatermarkBean.createDefault()
        } catch (e: Exception) {
            WatermarkBean.createDefault()
        }
        set(value) = setString("watermark_bean", value.toString())
        
    // Base host URL configuration - stub implementation
    fun setBaseHost(url: String) {
        setString("base_host_url", url)
    }
    
    fun getBaseHost(): String = getString("base_host_url", "")
    
    /**
     * Get time zone for display
     * @return timezone string (e.g. "UTC", "America/New_York")
     */
    fun getShowZone(): String = getString("show_zone", "UTC")
}

/**
 * Data class for car detection information
 */
data class CarDetectInfo(
    val item: String,
    val description: String,
    val temperature: String = "40~70" // Default temperature range
)