package com.topdon.lib.core.tools

import android.content.Context
import android.widget.Toast

/**
 * Toast utilities for component modules
 */
object ToastTools {
    
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
}