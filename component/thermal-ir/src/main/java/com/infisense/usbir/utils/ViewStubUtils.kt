package com.infisense.usbir.utils

import android.view.View

/**
 * Stub ViewStub utilities for compatibility
 */
object ViewStubUtils {
    
    /**
     * Show or hide view stub
     */
    fun showViewStub(view: View, show: Boolean, callback: (() -> Unit)? = null) {
        view.visibility = if (show) View.VISIBLE else View.GONE
        callback?.invoke()
    }
}