package com.topdon.module.thermal.ir.view.compass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Custom Linear compass view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class LinearCompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    private var azimuth: Float = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Minimal compass implementation - just draw a line indicating direction
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) * 0.8f
        
        // Calculate end point based on azimuth
        val endX = centerX + radius * kotlin.math.cos(Math.toRadians(azimuth.toDouble())).toFloat()
        val endY = centerY + radius * kotlin.math.sin(Math.toRadians(azimuth.toDouble())).toFloat()
        
        canvas.drawLine(centerX, centerY, endX, endY, paint)
    }

    fun setAzimuth(bearing: Float) {
        azimuth = bearing
        invalidate()
    }
}