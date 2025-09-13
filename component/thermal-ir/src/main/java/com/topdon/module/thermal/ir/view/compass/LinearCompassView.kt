package com.topdon.module.thermal.ir.view.compass

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.drawToBitmap
import com.blankj.utilcode.util.SizeUtils
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.utils.getPixelLinear
import com.topdon.module.thermal.ir.utils.getValuesBetween
import com.topdon.module.thermal.ir.utils.realX
import com.topdon.module.thermal.ir.utils.realY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
\1View
 */
/**
 * Custom Linear compass view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class LinearCompassView : View {
    private val paint = Paint()
    private val textPaint = Paint()
    private val markerPaint = Paint()
    private val shortLinePaint = Paint()
    private val longLinePaint = Paint()
    private val positionPaint = Paint()
    private lateinit var canvas: Canvas

    private var lineColor: Int = Color.WHITE
    private var textColor: Int = Color.WHITE
    private var shortLineColor: Int = Color.WHITE
    private var longLineColor: Int = Color.WHITE
    private var positionColor: Int = Color.WHITE
    private var centerAzimuthColor = Color.WHITE
    private var textSize: Float = SizeUtils.sp2px(13f).toFloat()
    private var shortLineSize = SizeUtils.sp2px(0.5f).toFloat()
    private var longLineSize = SizeUtils.sp2px(0.5f).toFloat()
    private var positionSize = SizeUtils.sp2px(11f).toFloat()
    private var markerSize = SizeUtils.sp2px(2f).toFloat()
    private var backgroundColor = Color.BLACK

    private var lastDrawTime = 0L // 
    private var step = 1000 / 10 // 
    private val scope = CoroutineScope(EmptyCoroutineContext)
    var curBitmap: Bitmap? = null // viewbitmap

    constructor(context: Context) : this(context, null) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.LinearCompassView, 0, 0)
        lineColor = attributes.getColor(R.styleable.LinearCompassView_lineColor, Color.WHITE)
        textColor = attributes.getColor(R.styleable.LinearCompassView_textColor, Color.WHITE)
        backgroundColor =
            attributes.getColor(R.styleable.LinearCompassView_backgroundColor, Color.BLACK)
        shortLineColor =
            attributes.getColor(R.styleable.LinearCompassView_shortLineColor, Color.WHITE)
        longLineColor =
            attributes.getColor(R.styleable.LinearCompassView_longLineColor, Color.WHITE)
        positionColor =
            attributes.getColor(R.styleable.LinearCompassView_positionColor, Color.WHITE)
        centerAzimuthColor =
            attributes.getColor(R.styleable.LinearCompassView_compassMarkerColor, Color.WHITE)
        shortLineSize =
            attributes.getDimension(
                R.styleable.LinearCompassView_shortLineSize,
                SizeUtils.sp2px(0.5f).toFloat(),
            )
        longLineSize =
            attributes.getDimension(
                R.styleable.LinearCompassView_longLineSize,
                SizeUtils.sp2px(0.5f).toFloat(),
            )
        positionSize =
            attributes.getDimension(
                R.styleable.LinearCompassView_positionSize,
                SizeUtils.sp2px(11f).toFloat(),
            )
        markerSize =
            attributes.getDimension(
                R.styleable.LinearCompassView_markerSize,
                SizeUtils.sp2px(2f).toFloat(),
            )
        attributes.recycle()
        initView()
    }

    private fun initView() {
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 1f
        paint.isAntiAlias = true

        textPaint.color = textColor
        textPaint.textSize = textSize
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.isAntiAlias = true
        textPaint.strokeWidth = 1f

        markerPaint.color = centerAzimuthColor
        markerPaint.strokeWidth = markerSize
        markerPaint.style = Paint.Style.FILL_AND_STROKE
        markerPaint.isAntiAlias = true

        shortLinePaint.color = shortLineColor
        shortLinePaint.strokeWidth = shortLineSize
        shortLinePaint.style = Paint.Style.STROKE
        shortLinePaint.isAntiAlias = true

        longLinePaint.color = longLineColor
        longLinePaint.strokeWidth = longLineSize
        longLinePaint.style = Paint.Style.STROKE
        longLinePaint.isAntiAlias = true

        positionPaint.color = positionColor
        positionPaint.textSize = positionSize
        positionPaint.style = Paint.Style.FILL_AND_STROKE
        positionPaint.isAntiAlias = true
        positionPaint.strokeWidth = 1f
    }

    private var showAzimuthArrow = true

    private var azimuth = 0f
    private var range = 180f

    private var text: String = ""Test Data""
        }

    private fun toPixel(bearing: Float): Float {
        return getPixelLinear(
            bearing,
            azimuth,
            width.toFloat(),
            range,
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }
}
