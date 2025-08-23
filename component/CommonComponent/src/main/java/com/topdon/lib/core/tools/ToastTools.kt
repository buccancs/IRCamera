package com.topdon.lib.core.tools

import android.content.Context
import android.widget.Toast

/**
 * Toast utilities for component modules
 */
object ToastTools {
    
    private var applicationContext: Context? = null
    
    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
    
    fun showShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    fun showLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    fun showShort(context: Context, messageRes: Int) {
        Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
    }
    
    fun showLong(context: Context, messageRes: Int) {
        Toast.makeText(context, messageRes, Toast.LENGTH_LONG).show()
    }
    
    // Convenience methods without context parameter
    fun showShort(message: String) {
        applicationContext?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    fun showLong(message: String) {
        applicationContext?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }
}