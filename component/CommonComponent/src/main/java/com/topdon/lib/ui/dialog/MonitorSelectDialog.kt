package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context

/**
 * Simplified MonitorSelectDialog for compatibility
 */
class MonitorSelectDialog(context: Context) : Dialog(context) {

    interface OnClickListener {
        fun onClick(select: Int)
    }

    class Builder(private val context: Context) {
        private var title: String = ""
        private var onFirstStepConfirmListener: (() -> Unit)? = null
        private var onSecondStepConfirmListener: ((Int) -> Unit)? = null
        private var onPositiveListener: OnClickListener? = null
        
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }
        
        fun setOnFirstStepConfirmListener(listener: () -> Unit): Builder {
            onFirstStepConfirmListener = listener
            return this
        }
        
        fun setOnSecondStepConfirmListener(listener: (Int) -> Unit): Builder {
            onSecondStepConfirmListener = listener
            return this
        }
        
        fun setPositiveListener(listener: OnClickListener): Builder {
            onPositiveListener = listener
            return this
        }
        
        fun build(): MonitorSelectDialog {
            return MonitorSelectDialog(context).apply {
                // Apply listener logic here when showing dialog
                onPositiveListener?.onClick(1) // Default selection
            }
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