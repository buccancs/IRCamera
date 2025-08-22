package com.topdon.libcom.dialog

import android.app.Dialog
import android.content.Context

/**
 * Color picker dialog - stub implementation for compilation
 */
class ColorPickDialog(context: Context) : Dialog(context) {
    
    private var selectedColor: Int = 0xFF000000.toInt()
    private var onColorSelectedListener: ((Int) -> Unit)? = null
    
    override fun show() {
        // Stub implementation
    }
    
    fun setOnColorSelectedListener(listener: (Int) -> Unit) {
        this.onColorSelectedListener = listener
    }
    
    fun setInitialColor(color: Int) {
        this.selectedColor = color
    }
    
    fun getSelectedColor(): Int {
        return selectedColor
    }
    
    /**
     * Set available colors
     */
    fun setColors(colors: IntArray) {
        // Stub implementation
    }
    
    /**
     * Set dialog title
     */
    fun setTitle(title: String) {
        // Stub implementation  
    }
}