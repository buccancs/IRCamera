package com.jaygoo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Compatibility class for DefRangeSeekBar
 * This is a placeholder implementation to maintain compatibility
 * TODO: Implement actual RangeSeekBar functionality if needed
 */
class DefRangeSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var onRangeChangedListener: OnRangeChangedListener? = null
    private var progress: Float = 0f

    fun setOnRangeChangedListener(listener: OnRangeChangedListener?) {
        this.onRangeChangedListener = listener
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        // TODO: Update UI
    }

    fun getProgress(): Float {
        return progress
    }
}

interface OnRangeChangedListener {
    fun onRangeChanged(view: DefRangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean)
    fun onStartTrackingTouch(view: DefRangeSeekBar?, isLeft: Boolean)
    fun onStopTrackingTouch(view: DefRangeSeekBar?, isLeft: Boolean)
}