package com.topdon.libcom.dialog

import android.app.Dialog
import android.content.Context
import com.topdon.lib.core.bean.AlarmBean

/**
 * Temperature alarm setting dialog - stub implementation for compilation
 */
class TempAlarmSetDialog(context: Context) : Dialog(context) {
    
    private var minTemperature: Float = 0f
    private var maxTemperature: Float = 100f
    private var onConfirmListener: ((Float, Float) -> Unit)? = null
    
    // Properties required by IRThermalLiteActivity
    var onSaveListener: ((AlarmBean) -> Unit)? = null
    var alarmBean: AlarmBean? = null
    
    override fun show() {
        // Stub implementation
    }
    
    fun setOnConfirmListener(listener: (Float, Float) -> Unit) {
        this.onConfirmListener = listener
    }
    
    fun setTemperatureRange(minTemp: Float, maxTemp: Float) {
        this.minTemperature = minTemp
        this.maxTemperature = maxTemp
    }
    
    fun getMinTemperature(): Float {
        return minTemperature
    }
    
    fun getMaxTemperature(): Float {
        return maxTemperature
    }
    
    /**
     * Set temperature unit
     */
    fun setTemperatureUnit(unit: String) {
        // Stub implementation
    }
    
    /**
     * Enable/disable alarm
     */
    fun setAlarmEnabled(enabled: Boolean) {
        // Stub implementation
    }
}