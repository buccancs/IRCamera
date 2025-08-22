package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context

/**
 * Not tips select dialog - stub implementation for compilation
 */
class NotTipsSelectDialog(context: Context) : Dialog(context) {
    
    override fun show() {
        // Stub implementation
    }
    
    fun setOnConfirmListener(listener: () -> Unit) {
        // Stub implementation
    }
    
    fun setOnConfirmListener(listener: (Boolean) -> Unit): NotTipsSelectDialog {
        // Stub implementation - return this for method chaining
        return this
    }
    
    fun setTipsResId(resId: Int): NotTipsSelectDialog {
        // Stub implementation - return this for method chaining
        return this
    }
    
    fun setOptions(options: List<String>) {
        // Stub implementation
    }
    
    fun setOnItemSelectListener(listener: (Int) -> Unit) {
        // Stub implementation
    }
}