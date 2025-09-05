package com.topdon.lib.ui.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog

class SeekDialog private constructor(private val context: Context) {
    
    private var title: String = "Dialog"
    private var message: String = ""
    private var saturation: Int = 0
    private var positiveListener: ((Int) -> Unit)? = null
    private var cancelListener: (() -> Unit)? = null
    private var realTimeListener: ((Int) -> Unit)? = null
    
    companion object {
        @JvmStatic
        fun getInstance(context: Context): SeekDialog {
            return SeekDialog(context)
        }
    }
    
    class Builder(private val context: Context) {
        private val dialog = SeekDialog(context)
        
        fun setTitle(title: String): Builder {
            dialog.title = title
            return this
        }
        
        fun setMessage(messageRes: Int): Builder {
            dialog.message = context.getString(messageRes)
            return this
        }
        
        fun setMessage(message: String): Builder {
            dialog.message = message
            return this
        }
        
        fun setSaturation(saturation: Int): Builder {
            dialog.saturation = saturation
            return this
        }
        
        fun setPositiveListener(textRes: Int, listener: (Int) -> Unit): Builder {
            dialog.positiveListener = listener
            return this
        }
        
        fun setListener(listener: (Int) -> Unit): Builder {
            dialog.realTimeListener = listener
            return this
        }
        
        fun create(): SeekDialog {
            return dialog
        }
    }
    
    fun setTitle(title: String): SeekDialog {
        this.title = title
        return this
    }
    
    fun setPositiveListener(listener: (Int) -> Unit): SeekDialog {
        this.positiveListener = listener
        return this
    }
    
    fun setCancelListener(listener: () -> Unit): SeekDialog {
        this.cancelListener = listener
        return this
    }
    
    fun show() {
        create().show()
    }
    
    fun create(): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> 
                positiveListener?.invoke(saturation)
            }
            .setNegativeButton("Cancel") { dialog, _ -> 
                cancelListener?.invoke()
                dialog.dismiss() 
            }
            .create()
    }
}