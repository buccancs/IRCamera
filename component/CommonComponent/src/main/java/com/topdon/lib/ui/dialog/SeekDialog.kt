package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.widget.SeekBar
import android.widget.LinearLayout
import android.widget.Button
import android.view.ViewGroup
import com.energy.commoncomponent.R

/**
 * Seek dialog for thermal camera settings
 */
class SeekDialog(context: Context) : Dialog(context) {
    
    private var seekBar: SeekBar? = null
    private var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
    private var onConfirmListener: ((Int) -> Unit)? = null
    
    init {
        initViews()
    }
    
    private fun initViews() {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        seekBar = SeekBar(context)
        layout.addView(seekBar)
        
        val confirmButton = Button(context)
        confirmButton.text = "Confirm"
        confirmButton.setOnClickListener {
            onConfirmListener?.invoke(seekBar?.progress ?: 0)
            dismiss()
        }
        layout.addView(confirmButton)
        
        val cancelButton = Button(context)
        cancelButton.text = "Cancel"
        cancelButton.setOnClickListener {
            dismiss()
        }
        layout.addView(cancelButton)
        
        setContentView(layout)
        seekBar?.setOnSeekBarChangeListener(onSeekBarChangeListener)
    }
    
    fun setMax(max: Int) {
        seekBar?.max = max
    }
    
    fun setProgress(progress: Int) {
        seekBar?.progress = progress
    }
    
    fun setOnSeekBarChangeListener(listener: SeekBar.OnSeekBarChangeListener) {
        onSeekBarChangeListener = listener
        seekBar?.setOnSeekBarChangeListener(listener)
    }
    
    fun setOnConfirmListener(listener: (Int) -> Unit) {
        onConfirmListener = listener
    }
}