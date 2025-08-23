package com.topdon.lib.ui.view

import androidx.constraintlayout.widget.ConstraintLayout
import android.graphics.Bitmap
import android.view.View

/**
 * Extension functions for view components
 */

/**
 * Update bitmap for ConstraintLayout
 */
fun ConstraintLayout.updateBitmap() {
    // Stub implementation - bitmap update functionality
    invalidate()
}

/**
 * Update bitmap with specific parameters
 */
fun ConstraintLayout.updateBitmap(bitmap: Bitmap?) {
    // Stub implementation - update with specific bitmap
    invalidate()
}

/**
 * Get View by ID extension
 */
fun <T : View> View.findView(id: Int): T? {
    return findViewById<T>(id)
}

/**
 * Correct coordinate within bounds
 */
fun Float.correct(bound: Int): Int {
    return this.toInt().coerceIn(0, bound)
}

fun Int.correct(bound: Int): Int {
    return this.coerceIn(0, bound)
}