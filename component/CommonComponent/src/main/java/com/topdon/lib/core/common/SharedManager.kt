package com.topdon.lib.core.common

import android.content.Context
import android.content.SharedPreferences
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
    
    // Select fence type for thermal monitoring
    fun getSelectFenceType(): Int = getInt("select_fence_type", 0)
    fun setSelectFenceType(type: Int) = setInt("select_fence_type", type)
    
    // Thermal measurement settings
    fun getThermalMeasureType(): Int = getInt("thermal_measure_type", 0)
    fun setThermalMeasureType(type: Int) = setInt("thermal_measure_type", type)
    
    // Device connection settings
    fun getConnectedDeviceType(): String = getString("connected_device_type", "TC001")
    fun setConnectedDeviceType(type: String) = setString("connected_device_type", type)
}