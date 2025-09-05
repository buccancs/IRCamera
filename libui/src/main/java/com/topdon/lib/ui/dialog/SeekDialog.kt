package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SeekBar
import com.topdon.lib.ui.R

/**
 * SeekBar dialog for thermal module
 */
class SeekDialog(
    context: Context,
    private val title: String = "",
    private val minValue: Int = 0,
    private val maxValue: Int = 100,
    private val currentValue: Int = 50
) : Dialog(context) {
    
    private var onValueChangedListener: ((Int) -> Unit)? = null
    
    init {
        // Simple implementation using default Android dialog
        setTitle(title)
        // This is a stub implementation - in real app would have custom layout
    }
    
    fun setOnValueChangedListener(listener: (Int) -> Unit) {
        onValueChangedListener = listener
    }
    
    companion object {
        fun show(
            context: Context,
            title: String = "",
            minValue: Int = 0,
            maxValue: Int = 100,
            currentValue: Int = 50,
            onValueChanged: (Int) -> Unit
        ): SeekDialog {
            val dialog = SeekDialog(context, title, minValue, maxValue, currentValue)
            dialog.setOnValueChangedListener(onValueChanged)
            dialog.show()
            return dialog
        }
    }
}