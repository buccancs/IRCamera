package com.topdon.lib.ui.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog

class SeekDialog private constructor(private val context: Context) {
    
    companion object {
        @JvmStatic
        fun getInstance(context: Context): SeekDialog {
            return SeekDialog(context)
        }
    }
    
    fun setTitle(title: String): SeekDialog {
        return this
    }
    
    fun setPositiveListener(listener: (Int) -> Unit): SeekDialog {
        return this
    }
    
    fun setCancelListener(listener: () -> Unit): SeekDialog {
        return this
    }
    
    fun create(): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle("Dialog")
            .setPositiveButton("OK") { _, _ -> }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
    }
}