package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import android.view.ViewGroup
import com.energy.commoncomponent.R

/**
 * Thermal input dialog for temperature and emissivity input
 */
class ThermalInputDialog(context: Context) : Dialog(context) {
    
    private var editText: EditText? = null
    private var titleTextView: TextView? = null
    private var onConfirmListener: ((String) -> Unit)? = null
    
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
        
        titleTextView = TextView(context)
        layout.addView(titleTextView)
        
        editText = EditText(context)
        layout.addView(editText)
        
        val confirmButton = Button(context)
        confirmButton.text = "Confirm"
        confirmButton.setOnClickListener {
            val text = editText?.text?.toString() ?: ""
            onConfirmListener?.invoke(text)
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
    }
    
    fun setTitle(title: String) {
        titleTextView?.text = title
    }
    
    fun setText(text: String) {
        editText?.setText(text)
    }
    
    fun setHint(hint: String) {
        editText?.hint = hint
    }
    
    fun setOnConfirmListener(listener: (String) -> Unit) {
        onConfirmListener = listener
    }
}