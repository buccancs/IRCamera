package com.topdon.lib.core.ui

import android.content.Context
import androidx.annotation.StringRes

/**
 * Toast utility class for showing messages
 */
object TToast {
    
    fun show(context: Context, message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    fun show(context: Context, @StringRes messageRes: Int) {
        android.widget.Toast.makeText(context, messageRes, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    fun showShort(context: Context, message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    fun showShort(context: Context, @StringRes messageRes: Int) {
        android.widget.Toast.makeText(context, messageRes, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    fun showLong(context: Context, message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
    }
    
    fun showLong(context: Context, @StringRes messageRes: Int) {
        android.widget.Toast.makeText(context, messageRes, android.widget.Toast.LENGTH_LONG).show()
    }
    
    // Legacy method names for compatibility
    fun shortToast(context: Context, message: String) = showShort(context, message)
    fun shortToast(context: Context, @StringRes messageRes: Int) = showShort(context, messageRes)
    fun longToast(context: Context, message: String) = showLong(context, message)  
    fun longToast(context: Context, @StringRes messageRes: Int) = showLong(context, messageRes)
}