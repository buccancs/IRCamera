package com.topdon.lib.ui.widget.seekbar

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Range seek bar widget - stub implementation for compilation
 */
class RangeSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var leftValue: Float = 0f
    private var rightValue: Float = 100f
    private var minValue: Float = 0f
    private var maxValue: Float = 100f
    private var listener: OnRangeChangedListener? = null
    
    companion object {
        const val TEMP_MODE_MIN = 1
        const val TEMP_MODE_MAX = 2
        const val TEMP_MODE_INTERVAL = 3
    }
    
    /**
     * Set range changed listener
     */
    fun setOnRangeChangedListener(listener: OnRangeChangedListener?) {
        this.listener = listener
    }
    
    /**
     * Set range values
     */
    fun setRangeValues(minValue: Float, maxValue: Float) {
        this.minValue = minValue
        this.maxValue = maxValue
        this.leftValue = minValue
        this.rightValue = maxValue
        invalidate()
    }
    
    /**
     * Set selected range
     */
    fun setSelectedRange(leftValue: Float, rightValue: Float) {
        this.leftValue = leftValue.coerceIn(minValue, maxValue)
        this.rightValue = rightValue.coerceIn(minValue, maxValue)
        invalidate()
        listener?.onRangeChanged(this, this.leftValue, this.rightValue, false)
    }
    
    /**
     * Get left value
     */
    fun getLeftValue(): Float = leftValue
    
    /**
     * Get right value  
     */
    fun getRightValue(): Float = rightValue
    
    /**
     * Set range and progress values 
     */
    fun setRangeAndPro(minVal: Float, maxVal: Float, leftVal: Float, rightVal: Float) {
        this.minValue = minVal
        this.maxValue = maxVal
        this.leftValue = leftVal.coerceIn(minVal, maxVal)
        this.rightValue = rightVal.coerceIn(minVal, maxVal)
        invalidate()
        listener?.onRangeChanged(this, this.leftValue, this.rightValue, false)
    }
    
    /**
     * Get min value
     */
    fun getMinValue(): Float = minValue
    
    /**
     * Get max value
     */
    fun getMaxValue(): Float = maxValue
    
    /**
     * Set left value
     */
    fun setLeftValue(value: Float) {
        this.leftValue = value.coerceIn(minValue, rightValue)
        invalidate()
        listener?.onRangeChanged(this, leftValue, rightValue, false)
    }
    
    /**
     * Set right value
     */
    fun setRightValue(value: Float) {
        this.rightValue = value.coerceIn(leftValue, maxValue)
        invalidate()
        listener?.onRangeChanged(this, leftValue, rightValue, false)
    }
    
    /**
     * Set indicator text decimal format - stub implementation
     */
    fun setIndicatorTextDecimalFormat(format: String) {
        // Stub implementation for compilation - would format indicator text
    }
}