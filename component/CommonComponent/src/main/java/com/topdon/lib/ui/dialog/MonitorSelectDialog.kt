package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context

/**
 * Simplified MonitorSelectDialog for compatibility
 */
class MonitorSelectDialog(context: Context) : Dialog(context) {

    class Builder(private val context: Context) {
        private var onFirstStepConfirmListener: (() -> Unit)? = null
        private var onSecondStepConfirmListener: ((Int) -> Unit)? = null
        private var onPositiveListener: ((Int) -> Unit)? = null
        
        fun setOnFirstStepConfirmListener(listener: () -> Unit): Builder {
            onFirstStepConfirmListener = listener
            return this
        }
        
        fun setOnSecondStepConfirmListener(listener: (Int) -> Unit): Builder {
            onSecondStepConfirmListener = listener
            return this
        }
        
        fun setPositiveListener(listener: (Int) -> Unit): Builder {
            onPositiveListener = listener
            return this
        }
        
        fun build(): MonitorSelectDialog {
            return MonitorSelectDialog(context)
        }
        
        fun create(): MonitorSelectDialog {
            return build()
        }
    }
    
    override fun show() {
        // Simplified show implementation
        super.show()
    }
}