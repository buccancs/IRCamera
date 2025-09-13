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
\1temperature View.
 *
 * Created by LCG on 2024/12/19.
 */
/**
 * Custom Temperature hik view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class TemperatureHikView : TemperatureBaseView {
    /**
\1drawingtemperature
     */
    @Volatile
    private var tempInfo = TempInfo()

    /**
\1calculationtemperature.
     */
    private var libIRTemp = LibIRTemp()

    /**
\1calculationtemperature.
     */
    private var calculateThread: CalculateThread? = null

    /**
\1thermal imagingrotation， 0、90、180、270， 270
     */
    @Volatile
    var rotateAngle: Int = 270
        set(value) {
            field = value
            val isPortrait = value == 90 || value == 270
            setImageSize(if (isPortrait) 192 else 256, if (isPortrait) 256 else 192)
        }

    /**
\1temperature，temperature ****
     */
    @Volatile
    var onTempChangeListener: ((min: Float, max: Float) -> Unit)? = null

    /**
\1temperaturedata，.
     */
    var onTrendChangeListener: ((tempList: List<Float>) -> Unit)? = null

    /**
\1temperature measurement，.
     */
    var onTempResultListener: ((tempInfo: TempInfo) -> Unit)? = null

    /**
\1 onMeasure ，save temperature scale ， onMeasure 。
     */
    private var wantAddPoint: Point? = null

    /**
\1 onMeasure ，save temperature scale ， onMeasure 。
     */
    private var wantAddLine: Line? = null

    /**
\1 onMeasure ，save temperature scale ， onMeasure 。
     */
    private var wantAddRect: Rect? = null

    /**
\1 temperature scale 
     */
    fun addSourcePoint(point: Point) {
        if (xScale > 0 && yScale > 0) {
            synchronized(this) {
                if (pointList.size == maxCount) { // 
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
\1 temperature scale 
     */
    fun addSourceLine(line: Line) {
        if (xScale > 0 && yScale > 0) {
            val start = Point((line.start.x * xScale).toInt(), (line.start.y * yScale).toInt())
            val end = Point((line.end.x * xScale).toInt(), (line.end.y * yScale).toInt())
            synchronized(this) {
                if (lineList.size == maxCount) { // 
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
\1 temperature scale 
     */
    fun addSourceRect(rect: Rect) {
        if (xScale > 0 && yScale > 0) {
            val left = (rect.left * xScale).toInt()
            val right = (rect.right * xScale).toInt()
            val top = (rect.top * yScale).toInt()
            val bottom = (rect.bottom * yScale).toInt()
            synchronized(this) {
                if (rectList.size == maxCount) { // 
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
\1temperaturerotationparameter.
     */
    private val imageRes = LibIRProcess.ImageRes_t()

    /**
\1temperaturearray， 1  1 .
     */
    private var beforeTime: Long = 0

    /**
\1rotationtemperaturearray.
     */
    private val sourceTempArray = ByteArray(256 * 192 * 2)

    /**
\1rotationtemperaturearray，， [libIRTemp] data，
     */
    private val rotateTempArray = ByteArray(256 * 192 * 2)

    /**
\1temperaturedata
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
                else -> System.arraycopy(sourceTempArray, 0, rotateTempArray, 0, rotateTempArray.size)
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

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
    ) {
        imageRes.width = 256.toChar()
        imageRes.height = 192.toChar()
        setImageSize(192, 256)
        if (context is ComponentActivity) {
            context.lifecycle.addObserver(MyLifecycleObserver())
        }
    }

    override fun setImageSize(
        imageWidth: Int,
        imageHeight: Int,
    ) {
        super.setImageSize(imageWidth, imageHeight)
        libIRTemp = LibIRTemp(imageWidth, imageHeight)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
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

    @SuppressLint("DrawAllocation"Test Data"Calculate Thread") {
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
\1Text View Text xScale、yScale Text，TextdrawingText
\1Text Text scale TextcalculationtemperatureText，TexttemperatureText，Text
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
\1Text View Text xScale、yScale Text，TextdrawingText
\1Text Text scale TextcalculationtemperatureText，TexttemperatureText，Text
                    }
                }

                val lineList: List<Line> = getLineListSafe()
                val lineResultList: ArrayList<TemperatureSampleResult> = ArrayList(lineList.size)
                for (line in lineList) {
                    val sourceLine =
                        Line(
                            Point((line.start.x / xScale).toInt(), (line.start.y / yScale).toInt()),
                            Point((line.end.x / xScale).toInt(), (line.end.y / yScale).toInt()),
                        )
                    try {
                        lineResultList.add(libIRTemp.getTemperatureOfLine(sourceLine))
                    } catch (_: IllegalArgumentException) {
\1Text View Text xScale、yScale Text，TextdrawingText
\1Text Text scale TextcalculationtemperatureText，TexttemperatureText，Text
                    }
                }

                val rectList: List<Rect> = getRectListSafe()
                val rectResultList: ArrayList<TemperatureSampleResult> = ArrayList(rectList.size)
                for (rect in rectList) {
                    val sourceRect =
                        Rect(
                            (rect.left / xScale).toInt(),
                            (rect.top / yScale).toInt(),
                            (rect.right / xScale).toInt(),
                            (rect.bottom / yScale).toInt(),
                        )
                    try {
                        rectResultList.add(libIRTemp.getTemperatureOfRect(sourceRect))
                    } catch (_: IllegalArgumentException) {
\1Text View Text xScale、yScale Text，TextdrawingText
\1Text Text scale TextcalculationtemperatureText，TexttemperatureText，Text
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
\1full imagetemperaturecalculation， View ，
     */
/**
 * Temp info utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
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
