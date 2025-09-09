package com.topdon.module.thermal.ir.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.sdkisp.LibIRTemp
import com.energy.iruvc.sdkisp.LibIRTemp.TemperatureSampleResult
import com.energy.iruvc.utils.CommonParams.IRPROCSRCFMTType
import com.energy.iruvc.utils.Line
import com.infisense.usbir.utils.TempDrawHelper.Companion.correct
import com.infisense.usbir.utils.TempUtil

/**
 * [CN_TEXT]Point/Line/Area[CN_TEXT] View.
 *
 * Created by LCG on 2024/12/19.
 */
class TemperatureHikView : TemperatureBaseView {
    /**
     * [CN_TEXT]
     */
    @Volatile
    private var tempInfo = TempInfo()

    /**
     * [CN_TEXT].
     */
    private var libIRTemp = LibIRTemp()
    /**
     * [CN_TEXT].
     */
    private var calculateThread: CalculateThread? = null


    /**
     * [CN_TEXT]RotateAngle，[CN_TEXT] 0、90、180、270，[CN_TEXT] 270
     */
    @Volatile
    var rotateAngle: Int = 270
        set(value) {
            field = value
            val isPortrait = value == 90 || value == 270
            setImageSize(if (isPortrait) 192 else 256, if (isPortrait) 256 else 192)
        }

    /**
     * [CN_TEXT]，[CN_TEXT] **Celsius**
     */
    @Volatile
    var onTempChangeListener: ((min: Float, max: Float) -> Unit)? = null

    /**
     * [CN_TEXT]，[CN_TEXT]Celsius.
     */
    var onTrendChangeListener: ((tempList: List<Float>) -> Unit)? = null

    /**
     * Temperature measurement[CN_TEXT]，[CN_TEXT]Celsius.
     */
    var onTempResultListener: ((tempInfo: TempInfo) -> Unit)? = null


    /**
     * [CN_TEXT] onMeasure [CN_TEXT]，[CN_TEXT] [CN_TEXT] [CN_TEXT]，[CN_TEXT] onMeasure [CN_TEXT]。
     */
    private var wantAddPoint: Point? = null
    /**
     * [CN_TEXT] onMeasure [CN_TEXT]，[CN_TEXT] [CN_TEXT] [CN_TEXT]，[CN_TEXT] onMeasure [CN_TEXT]。
     */
    private var wantAddLine: Line? = null
    /**
     * [CN_TEXT] onMeasure [CN_TEXT]，[CN_TEXT] [CN_TEXT] [CN_TEXT]，[CN_TEXT] onMeasure [CN_TEXT]。
     */
    private var wantAddRect: Rect? = null
    /**
     * [CN_TEXT] [CN_TEXT] [CN_TEXT]
     */
    fun addSourcePoint(point: Point) {
        if (xScale > 0 && yScale > 0) {
            synchronized(this) {
                if (pointList.size == maxCount) {//[CN_TEXT]
                    pointList.removeAt(0)
                }
                pointList.add(Point((point.x * xScale).toInt(), (point.y * yScale).toInt()))
            }
            invalidate()
        } else {
            wantAddPoint = point
        }
    }
    /**
     * [CN_TEXT] [CN_TEXT] [CN_TEXT]
     */
    fun addSourceLine(line: Line) {
        if (xScale > 0 && yScale > 0) {
            val start = Point((line.start.x * xScale).toInt(), (line.start.y * yScale).toInt())
            val end = Point((line.end.x * xScale).toInt(), (line.end.y * yScale).toInt())
            synchronized(this) {
                if (lineList.size == maxCount) {//[CN_TEXT]
                    lineList.removeAt(0)
                }
                lineList.add(Line(start, end))
            }
            invalidate()
        } else {
            wantAddLine = line
        }
    }
    /**
     * [CN_TEXT] [CN_TEXT] [CN_TEXT]
     */
    fun addSourceRect(rect: Rect) {
        if (xScale > 0 && yScale > 0) {
            val left = (rect.left * xScale).toInt()
            val right = (rect.right * xScale).toInt()
            val top = (rect.top * yScale).toInt()
            val bottom = (rect.bottom * yScale).toInt()
            synchronized(this) {
                if (rectList.size == maxCount) {//[CN_TEXT]
                    rectList.removeAt(0)
                }
                rectList.add(Rect(left, top, right, bottom))
            }
            invalidate()
        } else {
            wantAddRect = rect
        }
    }


    /**
     * [CN_TEXT]Rotate[CN_TEXT].
     */
    private val imageRes = LibIRProcess.ImageRes_t()

    /**
     * [CN_TEXT]，[CN_TEXT] 1 [CN_TEXT] 1 [CN_TEXT].
     */
    private var beforeTime: Long = 0


    /**
     * [CN_TEXT]Rotate[CN_TEXT].
     */
    private val sourceTempArray = ByteArray(256 * 192 * 2)
    /**
     * Rotate[CN_TEXT]，[CN_TEXT]，[CN_TEXT] [libIRTemp] [CN_TEXT]，[CN_TEXT]
     */
    private val rotateTempArray = ByteArray(256 * 192 * 2)
    /**
     * [CN_TEXT]
     */
    fun refreshTemp(newData: ByteArray) {
        val currentTime: Long = System.currentTimeMillis()
        if (currentTime - beforeTime > 1000) {
            beforeTime = currentTime

            System.arraycopy(newData, 0, sourceTempArray, 0, sourceTempArray.size)
            when (rotateAngle) {
                90 -> LibIRProcess.rotateLeft90(sourceTempArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_Y14, rotateTempArray)
                180 -> LibIRProcess.rotate180(sourceTempArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_Y14, rotateTempArray)
                270 -> LibIRProcess.rotateRight90(sourceTempArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_Y14, rotateTempArray)
                else  -> System.arraycopy(sourceTempArray, 0, rotateTempArray, 0, rotateTempArray.size)
            }

            libIRTemp.setTempData(rotateTempArray)
            if (mode != Mode.CLEAR) {
                calculateThread?.calculateTemp()
            }
        }
    }


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        imageRes.width = 256.toChar()
        imageRes.height = 192.toChar()
        setImageSize(192, 256)
        if (context is ComponentActivity) {
            context.lifecycle.addObserver(MyLifecycleObserver())
        }
    }

    override fun setImageSize(imageWidth: Int, imageHeight: Int) {
        super.setImageSize(imageWidth, imageHeight)
        libIRTemp = LibIRTemp(imageWidth, imageHeight)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        wantAddPoint?.let {
            addSourcePoint(it)
            wantAddPoint = null
        }
        wantAddLine?.let {
            addSourceLine(it)
            wantAddLine = null
        }
        wantAddRect?.let {
            addSourceRect(it)
            wantAddRect = null
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        //[CN_TEXT]Historical legacy，2D[CN_TEXT]
        //2D[CN_TEXT]Settings，[CN_TEXT]，[CN_TEXT]；[CN_TEXT]、[CN_TEXT]、[CN_TEXT]
        //[CN_TEXT]，[CN_TEXT]
        //[CN_TEXT]
        if (isShowFull || pointList.isNotEmpty() || lineList.isNotEmpty() || rectList.isNotEmpty()) {
            drawPoint(canvas, Point(width / 2, height / 2))
            tempInfo.center?.let {
                drawTempText(canvas, width / 2, height / 2, it.maxTemperature)
            }
        }

        //[CN_TEXT]、[CN_TEXT]High temperature
        if (isShowFull) {
            tempInfo.full?.let {
                val minX: Int = (it.minTemperaturePixel.x * xScale).toInt()
                val minY: Int = (it.minTemperaturePixel.y * yScale).toInt()
                drawCircle(canvas, minX, minY, false)
                drawTempText(canvas, minX, minY, it.minTemperature)

                val maxX: Int = (it.maxTemperaturePixel.x * xScale).toInt()
                val maxY: Int = (it.maxTemperaturePixel.y * yScale).toInt()
                drawCircle(canvas, maxX, maxY, true)
                drawTempText(canvas, maxX, maxY, it.maxTemperature)
            }
        }

        //[CN_TEXT]
        for (i in pointList.indices) {
            val point: Point = pointList[i]
            drawPoint(canvas, point)
            if (i < tempInfo.pointResults.size) {
                drawCircle(canvas, point.x, point.y, true)
                drawTempText(canvas, point.x, point.y, tempInfo.pointResults[i].maxTemperature)
            }
        }
        operatePoint?.let { drawPoint(canvas, it) }

        //[CN_TEXT]
        for (i in lineList.indices) {
            drawLine(canvas, lineList[i])
            if (i < tempInfo.lineResults.size) {
                val result: TemperatureSampleResult = tempInfo.lineResults[i]
                val maxX: Int = (result.maxTemperaturePixel.x * xScale).correct(width)
                val maxY: Int = (result.maxTemperaturePixel.y * yScale).correct(height)
                val minX: Int = (result.minTemperaturePixel.x * xScale).correct(width)
                val minY: Int = (result.minTemperaturePixel.y * yScale).correct(height)
                drawCircle(canvas, maxX, maxY, true)
                drawCircle(canvas, minX, minY, false)
                drawTempText(canvas, maxX, maxY, result.maxTemperature)
                drawTempText(canvas, minX, minY, result.minTemperature)
            }
        }
        operateLine?.let { drawLine(canvas, it) }

        //[CN_TEXT]
        for (i in rectList.indices) {
            drawRect(canvas, rectList[i])
            if (i < tempInfo.rectResults.size) {
                val result: TemperatureSampleResult = tempInfo.rectResults[i]
                val maxX: Int = (result.maxTemperaturePixel.x * xScale).correct(width)
                val maxY: Int = (result.maxTemperaturePixel.y * yScale).correct(height)
                val minX: Int = (result.minTemperaturePixel.x * xScale).correct(width)
                val minY: Int = (result.minTemperaturePixel.y * yScale).correct(height)
                drawCircle(canvas, maxX, maxY, true)
                drawCircle(canvas, minX, minY, false)
                drawTempText(canvas, maxX, maxY, result.maxTemperature)
                drawTempText(canvas, minX, minY, result.minTemperature)
            }
        }
        operateRect?.let { drawRect(canvas, it) }

        //[CN_TEXT]
        trendLine?.let {
            drawLine(canvas, it)
            drawTrendText(canvas, it)
            val result: TemperatureSampleResult = tempInfo.trend ?: return@let
            val maxX: Int = (result.maxTemperaturePixel.x * xScale).correct(width)
            val maxY: Int = (result.maxTemperaturePixel.y * yScale).correct(height)
            val minX: Int = (result.minTemperaturePixel.x * xScale).correct(width)
            val minY: Int = (result.minTemperaturePixel.y * yScale).correct(height)
            drawCircle(canvas, maxX, maxY, true)
            drawCircle(canvas, minX, minY, false)
            drawTempText(canvas, maxX, maxY, result.maxTemperature)
            drawTempText(canvas, minX, minY, result.minTemperature)
        }
        operateTrend?.let {
            drawLine(canvas, it)
            drawTrendText(canvas, it)
        }
    }

    /**
     * [CN_TEXT].
     */
    private inner class CalculateThread : HandlerThread("Calculate Thread") {
        private val mainHandler = Handler(Looper.getMainLooper())
        private var currentHandler: Handler? = null

        override fun start() {
            super.start()
            val looper: Looper = getLooper() ?: return
            currentHandler = MyHandler(looper)
        }

        override fun quit(): Boolean {
            mainHandler.removeCallbacksAndMessages(null)
            return super.quit()
        }

        fun calculateTemp() {
            currentHandler?.sendEmptyMessage(0)
        }

        private inner class MyHandler(looper: Looper) : Handler(looper) {
            override fun handleMessage(msg: Message) {
                val fullResult = libIRTemp.getTemperatureOfRect(Rect(0, 0, imageWidth, imageHeight))
                mainHandler.post {
                    onTempChangeListener?.invoke(fullResult.minTemperature, fullResult.maxTemperature)
                }

                if (mode == Mode.CLEAR) {
                    return
                }

                val centerResult = if (isShowFull) libIRTemp.getTemperatureOfPoint(Point(imageWidth / 2, imageHeight / 2)) else null

                var trendResult: TemperatureSampleResult? = null
                trendLine?.let {
                    val startPoint = Point((it.start.x / xScale).toInt(), (it.start.y / yScale).toInt())
                    val endPoint = Point((it.end.x / xScale).toInt(), (it.end.y / yScale).toInt())
                    try {
                        trendResult = libIRTemp.getTemperatureOfLine(Line(startPoint, endPoint))
                    } catch (_: IllegalArgumentException) {
                        //[CN_TEXT] View [CN_TEXT] xScale、yScale [CN_TEXT]，[CN_TEXT]Point/Line/Area[CN_TEXT]
                        //[CN_TEXT] [CN_TEXT] scale [CN_TEXT]，[CN_TEXT]，[CN_TEXT]
                    }

                    val tempList: List<Float> = TempUtil.getLineTemps(startPoint, endPoint, rotateTempArray, imageWidth)
                    mainHandler.post {
                        onTrendChangeListener?.invoke(tempList)
                    }
                }

                val pointList: List<Point> = getPointListSafe()
                val pointResultList: ArrayList<TemperatureSampleResult> = ArrayList(pointList.size)
                for (point in pointList) {
                    val sourcePoint = Point((point.x / xScale).toInt(), (point.y / yScale).toInt())
                    try {
                        pointResultList.add(libIRTemp.getTemperatureOfPoint(sourcePoint))
                    } catch (_: IllegalArgumentException) {
                        //[CN_TEXT] View [CN_TEXT] xScale、yScale [CN_TEXT]，[CN_TEXT]Point/Line/Area[CN_TEXT]
                        //[CN_TEXT] [CN_TEXT] scale [CN_TEXT]，[CN_TEXT]，[CN_TEXT]
                    }
                }

                val lineList: List<Line> = getLineListSafe()
                val lineResultList: ArrayList<TemperatureSampleResult> = ArrayList(lineList.size)
                for (line in lineList) {
                    val sourceLine = Line(
                        Point((line.start.x / xScale).toInt(), (line.start.y / yScale).toInt()),
                        Point((line.end.x / xScale).toInt(), (line.end.y / yScale).toInt())
                    )
                    try {
                        lineResultList.add(libIRTemp.getTemperatureOfLine(sourceLine))
                    } catch (_: IllegalArgumentException) {
                        //[CN_TEXT] View [CN_TEXT] xScale、yScale [CN_TEXT]，[CN_TEXT]Point/Line/Area[CN_TEXT]
                        //[CN_TEXT] [CN_TEXT] scale [CN_TEXT]，[CN_TEXT]，[CN_TEXT]
                    }
                }

                val rectList: List<Rect> = getRectListSafe()
                val rectResultList: ArrayList<TemperatureSampleResult> = ArrayList(rectList.size)
                for (rect in rectList) {
                    val sourceRect = Rect(
                        (rect.left / xScale).toInt(),
                        (rect.top / yScale).toInt(),
                        (rect.right / xScale).toInt(),
                        (rect.bottom / yScale).toInt()
                    )
                    try {
                        rectResultList.add(libIRTemp.getTemperatureOfRect(sourceRect))
                    } catch (_: IllegalArgumentException) {
                        //[CN_TEXT] View [CN_TEXT] xScale、yScale [CN_TEXT]，[CN_TEXT]Point/Line/Area[CN_TEXT]
                        //[CN_TEXT] [CN_TEXT] scale [CN_TEXT]，[CN_TEXT]，[CN_TEXT]
                    }
                }

                tempInfo = TempInfo(centerResult, if (isShowFull) fullResult else null, trendResult, pointResultList, lineResultList, rectResultList)
                mainHandler.post {
                    onTempResultListener?.invoke(tempInfo)
                }
                postInvalidate()
            }
        }
    }

    private inner class MyLifecycleObserver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            calculateThread = CalculateThread()
            calculateThread?.start()
        }

        override fun onStop(owner: LifecycleOwner) {
            calculateThread?.quit()
            calculateThread = null
        }
    }

    /**
     * Point/Line/Area[CN_TEXT]，[CN_TEXT] View [CN_TEXT]，[CN_TEXT]Celsius
     */
    data class TempInfo(
        val center: TemperatureSampleResult? = null,
        val full: TemperatureSampleResult? = null,
        val trend: TemperatureSampleResult? = null,
        val pointResults: List<TemperatureSampleResult> = ArrayList(0),
        val lineResults: List<TemperatureSampleResult> = ArrayList(0),
        val rectResults: List<TemperatureSampleResult> = ArrayList(0),
    )
}