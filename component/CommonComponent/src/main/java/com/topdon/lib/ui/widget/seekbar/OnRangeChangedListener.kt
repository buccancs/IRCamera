package com.topdon.lib.ui.widget.seekbar

/**
 * Range changed listener interface - stub implementation for compilation
 */
interface OnRangeChangedListener {
    
    /**
     * Called when range is changed
     * @param view The RangeSeekBar view
     * @param leftValue The left value of the range
     * @param rightValue The right value of the range
     * @param fromUser True if the change was initiated by the user
     * @param tempMode The temperature mode
     */
    fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, fromUser: Boolean, tempMode: Int = 0)
    
    /**
     * Called when start tracking touch
     */
    fun onStartTrackingTouch(view: RangeSeekBar) {
        // Default implementation - can be overridden
    }
    
    /**
     * Called when stop tracking touch
     */
    fun onStopTrackingTouch(view: RangeSeekBar) {
        // Default implementation - can be overridden
    }
}