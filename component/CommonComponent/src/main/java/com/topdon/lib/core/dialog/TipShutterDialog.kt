package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.text.SpannableString

/**
 * Tip shutter dialog - stub implementation for compilation
 */
class TipShutterDialog(context: Context) : Dialog(context) {
    
    override fun show() {
        // Stub implementation
    }
    
    fun setOnConfirmListener(listener: () -> Unit) {
        // Stub implementation
    }
    
    fun setShutterInfo(info: String) {
        // Stub implementation
    }
    
    fun showShutterCalibration() {
        // Stub implementation for shutter calibration
    }
    
    class Builder(private val context: Context) {
        private var title: Int = 0
        private var message: SpannableString? = null
        private var cancelListener: ((Boolean) -> Unit)? = null
        
        fun setTitle(titleResId: Int): Builder {
            this.title = titleResId
            return this
        }
        
        fun setMessage(message: SpannableString): Builder {
            this.message = message
            return this
        }
        
        fun setCancelListener(listener: (Boolean) -> Unit): Builder {
            this.cancelListener = listener
            return this
        }
        
        fun create(): TipShutterDialog {
            return TipShutterDialog(context)
        }
    }
}