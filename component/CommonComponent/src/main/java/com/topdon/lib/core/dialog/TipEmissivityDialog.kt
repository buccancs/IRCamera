package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context

/**
 * Tip emissivity dialog - stub implementation for compilation
 */
class TipEmissivityDialog(context: Context) : Dialog(context) {
    
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
}