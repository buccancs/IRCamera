package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context

/**
 * Simplified TipDialog stub for component modules
 * Component modules should use simple Toast messages instead of complex dialogs
 */
class TipDialog(context: Context) : Dialog(context) {
    
    fun showTip(title: String, message: String, confirmText: String = "OK", onConfirm: () -> Unit = {}) {
        // Simplified implementation - components should use showToast from BaseActivity instead
        dismiss()
        onConfirm()
    }
    
    fun showTip(titleRes: Int, messageRes: Int, confirmTextRes: Int = android.R.string.ok, onConfirm: () -> Unit = {}) {
        // Simplified implementation
        dismiss()
        onConfirm()
    }
    
    override fun show() {
        super.show()
    }
    
    class Builder(private val context: Context) {
        private var message: String = ""
        private var messageRes: Int = 0
        private var titleMessage: String = ""
        private var positiveListener: (() -> Unit)? = null
        private var negativeListener: (() -> Unit)? = null
        
        fun setMessage(messageRes: Int): Builder {
            this.messageRes = messageRes
            this.message = context.getString(messageRes)
            return this
        }
        
        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }
        
        fun setTitleMessage(title: String): Builder {
            this.titleMessage = title
            return this
        }
        
        fun setPositiveListener(textRes: Int, listener: () -> Unit): Builder {
            this.positiveListener = listener
            return this
        }
        
        fun setNegativeListener(textRes: Int, listener: () -> Unit): Builder {
            this.negativeListener = listener
            return this
        }
        
        fun setCancelListener(textRes: Int, listener: () -> Unit): Builder {
            this.negativeListener = listener
            return this
        }
        
        fun create(): TipDialog {
            return TipDialog(context)
        }
        
        fun show() {
            // Simplified implementation - just call the positive listener
            positiveListener?.invoke()
        }
    }
}