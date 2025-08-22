package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context

/**
 * Tip emissivity dialog - stub implementation for compilation
 */
class TipEmissivityDialog(context: Context) : Dialog(context) {
    
    var onDismissListener: ((Boolean) -> Unit)? = null
    
    override fun show() {
        // Stub implementation
    }
    
    fun setOnConfirmListener(listener: () -> Unit) {
        // Stub implementation
    }
    
    fun setEmissivityValue(value: Float) {
        // Stub implementation
    }
    
    fun getEmissivityValue(): Float {
        return 0.95f // Default emissivity value
    }
    
    class Builder(private val context: Context) {
        private var environment: String = ""
        private var distance: String = ""
        private var radiation: String = ""
        private var text: String = ""
        
        fun setDataBean(environment: Any, distance: Any, radiation: Any, text: String): Builder {
            this.environment = environment.toString()
            this.distance = distance.toString()
            this.radiation = radiation.toString()
            this.text = text
            return this
        }
        
        fun create(): TipEmissivityDialog {
            return TipEmissivityDialog(context)
        }
    }
}