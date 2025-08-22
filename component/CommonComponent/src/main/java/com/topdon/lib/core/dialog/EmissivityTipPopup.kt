package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity

/**
 * Emissivity tip popup - stub implementation for compilation
 */
class EmissivityTipPopup(private val context: Context, private val showTitle: Boolean = true) : Dialog(context) {
    
    override fun show() {
        // Stub implementation
    }
    
    fun setOnConfirmListener(listener: () -> Unit) {
        // Stub implementation
    }
    
    fun setEmissivity(value: Float) {
        // Stub implementation  
    }
    
    fun setDataBean(environment: Float, distance: Float, radiation: Float, materialText: String): EmissivityTipPopup {
        // Stub implementation for setting data bean
        return this
    }
    
    fun build(): EmissivityTipPopup {
        // Build the popup
        return this
    }
    
    fun showAsDropDown(anchorView: android.view.View, xoff: Int, yoff: Int, gravity: Int) {
        // Show popup as dropdown
    }
}