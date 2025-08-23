package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import com.topdon.lib.core.common.CarDetectInfo

/**
 * Car detection dialog - stub implementation for compilation
 */
class CarDetectDialog(
    context: Context,
    private val onSelectListener: ((CarDetectInfo) -> Unit)? = null
) : Dialog(context) {
    
    override fun show() {
        // Stub implementation - simulate user selection
        onSelectListener?.invoke(
            CarDetectInfo(
                item = "Temperature Alert",
                description = "Car temperature detection",
                temperature = "50~80"
            )
        )
    }
    
    fun setOnConfirmListener(listener: () -> Unit) {
        // Stub implementation
    }
    
    fun setTitle(title: String) {
        // Stub implementation
    }
    
    fun setMessage(message: String) {
        // Stub implementation
    }
}