package com.topdon.lib.core.common

import android.content.Context
import android.content.SharedPreferences

/**
 * Simplified SharedManager for component modules
 * Contains basic shared preferences functionality
 */
object SharedManager {
    
    private const val PREF_NAME = "IRCamera_Settings"
    private var prefs: SharedPreferences? = null
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    // Basic string preferences
    fun setString(key: String, value: String) {
        prefs?.edit()?.putString(key, value)?.apply()
    }
    
    fun getString(key: String, defaultValue: String = ""): String {
        return prefs?.getString(key, defaultValue) ?: defaultValue
    }
    
    // Basic boolean preferences  
    fun setBoolean(key: String, value: Boolean) {
        prefs?.edit()?.putBoolean(key, value)?.apply()
    }
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs?.getBoolean(key, defaultValue) ?: defaultValue
    }
    
    // Basic int preferences
    fun setInt(key: String, value: Int) {
        prefs?.edit()?.putInt(key, value)?.apply()
    }
    
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs?.getInt(key, defaultValue) ?: defaultValue
    }
    
    // Basic float preferences
    fun setFloat(key: String, value: Float) {
        prefs?.edit()?.putFloat(key, value)?.apply()
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