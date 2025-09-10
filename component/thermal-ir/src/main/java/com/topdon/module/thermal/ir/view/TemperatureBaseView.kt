package com.topdon.module.thermal.ir.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.SizeUtils
import com.energy.iruvc.utils.Line
import com.infisense.usbir.utils.TempDrawHelper
import com.infisense.usbir.utils.TempDrawHelper.Companion.correct
import com.infisense.usbir.utils.TempDrawHelper.Companion.correctPoint
import com.topdon.lib.core.tools.UnitTools
import com.topdon.module.thermal.ir.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

    protected fun drawPoint(
        canvas: Canvas,
        point: Point,
    ) {
        helper.drawPoint(canvas, point.x, point.y)
    }

    protected fun drawCircle(
        canvas: Canvas,
        x: Int,
        y: Int,
        isMax: Boolean,
    ) {
        helper.drawCircle(canvas, x, y, isMax)
    }

    protected fun drawTempText(
        canvas: Canvas,
        x: Int,
        y: Int,
        temp: Float,
    ) {
        helper.drawTempText(canvas, UnitTools.showC(temp), width, x, y)
    }

    /**
     * view View view，viewSpecifiedview "A"、"B" view。
     *
     * Note，view x、y view，view。
     */
    protected fun drawTrendText(
        canvas: Canvas,
        line: Line,
    ) {
        helper.drawTrendText(canvas, width, height, line.start.x, line.start.y, line.end.x, line.end.y)
    }

    /**
     * view View view，Specifiedview (x,y) view，viewSpecifiedview。
     * view，view.
     */
    protected fun drawPointName(
        canvas: Canvas,
        name: String,
        point: Point,
    ) {
        // view，viewCurrent View view，view(192x256)view
        // viewCurrentview，view，view
        val x = ((point.x / xScale).toInt() * xScale).toInt()
        val y = ((point.y / yScale).toInt() * yScale).toInt()
        helper.drawPointName(canvas, name, width, height, x, y)
    }

    /**
     * view View view，Specifiedview view view，
     * viewSpecifiedview，view。
     */
    protected fun drawLineName(
        canvas: Canvas,
        name: String,
        line: Line,
    ) {
        val startX = ((line.start.x / xScale).toInt() * xScale).toInt()
        val startY = ((line.start.y / yScale).toInt() * yScale).toInt()
        val stopX = ((line.end.x / xScale).toInt() * xScale).toInt()
        val stopY = ((line.end.y / yScale).toInt() * yScale).toInt()
        helper.drawPointRectName(canvas, name, width, height, startX, startY, stopX, stopY)
    }

    /**
     * view View view，Specifiedview view view，
     * viewSpecifiedview，view。
     */
    protected fun drawRectName(
        canvas: Canvas,
        name: String,
        rect: Rect,
    ) {
        val left: Int = ((rect.left / xScale).toInt() * xScale).toInt()
        val top: Int = ((rect.top / yScale).toInt() * yScale).toInt()
        val right: Int = ((rect.right / xScale).toInt() * xScale).toInt()
        val bottom: Int = ((rect.bottom / yScale).toInt() * yScale).toInt()
        helper.drawPointRectName(canvas, name, width, height, left, top, right, bottom)
    }

    // **************************************** Touch ****************************************

    private var downX = 0
    private var downY = 0

    /**
     * view Point/Line/Area Mode。
     *
     * true-viewPoint/Line/Area false-viewPoint/Line/Area
     */
    private var isAddAction = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        return when (mode) {
            Mode.POINT -> touchPoint(event)
            Mode.LINE -> touchLine(event, false)
            Mode.RECT -> touchRect(event)
            Mode.TREND -> touchLine(event, true)
            else -> super.onTouchEvent(event)
        }
    }

    // **************************************** view ****************************************

    /**
     * Touch viewCurrentview（view、view）view.
     */
    protected var operatePoint: Point? = null

    private fun touchPoint(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x.correctPoint(width)
                downY = event.y.correctPoint(height)
                val point: Point? = pollPoint(downX, downY)
                isAddAction = point == null
                operatePoint = point ?: Point(downX, downY)
                if (point == null && pointList.size == maxCount) { // View rendering
                    synchronized(this) {
                        pointList.removeAt(0)
                    }
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                operatePoint?.x = event.x.correctPoint(width)
                operatePoint?.y = event.y.correctPoint(height)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val x: Int = event.x.correctPoint(width)
                val y: Int = event.y.correctPoint(height)
                operatePoint?.x = x
                operatePoint?.y = y

                if (isAddAction || abs(x - downX) > DELETE_TOLERANCE || abs(y - downY) > DELETE_TOLERANCE) {
                    synchronized(this) {
                        pointList.add(operatePoint ?: Point())
                    }
                }
                operatePoint = null
                invalidate()

                onPointListener?.invoke(getSourcePointList())
                return true
            }
            else -> return false
        }
    }

    private fun pollPoint(
        x: Int,
        y: Int,
    ): Point? {
        for (i in pointList.size - 1 downTo 0) {
            val point: Point = pointList[i]
            if (point.x in x - TOUCH_TOLERANCE..x + TOUCH_TOLERANCE && point.y in y - TOUCH_TOLERANCE..y + TOUCH_TOLERANCE) {
                return synchronized(this) { pointList.removeAt(i) }
            }
        }
        return null
    }

    // **************************************** view ****************************************

    /**
     * Touch viewCurrentview（view、view）view.
     */
    protected var operateLine: Line? = null

    /**
     * Touch viewCurrentview（view、view）view.
     */
    protected var operateTrend: Line? = null

    private enum class LineMoveType { ALL, START, END, }

    /**
     * view：view、view、view。
     */
    private var lineMoveType = LineMoveType.ALL

    /**
     * view，view DOWN Stateview，view.
     */
    private val downLine: Line = Line(Point(0, 0), Point(0, 0))

    private fun touchLine(
        event: MotionEvent,
        isTrend: Boolean,
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x.correct(width)
                downY = event.y.correct(height)
                val line: Line? = pollLine(downX, downY, isTrend)
                if (line == null) {
                    isAddAction = true
                    if (isTrend) {
                        operateTrend = Line(Point(downX, downY), Point(downX, downY))
                        trendLine = null
                        onTrendOperateListener?.invoke(false)
                    } else {
                        operateLine = Line(Point(downX, downY), Point(downX, downY))
                        if (lineList.size == maxCount) {
                            synchronized(this) {
                                lineList.removeAt(0)
                            }
                        }
                    }
                } else {
                    isAddAction = false
                    if (isTrend) {
                        operateTrend = line
                        onTrendOperateListener?.invoke(false)
                    } else {
                        operateLine = line
                    }
                    downLine.start.set(line.start.x, line.start.y)
                    downLine.end.set(line.end.x, line.end.y)
                    lineMoveType =
                        if (downX > line.start.x - TOUCH_TOLERANCE && downX < line.start.x + TOUCH_TOLERANCE &&
                            downY > line.start.y - TOUCH_TOLERANCE && downY < line.start.y + TOUCH_TOLERANCE
                        ) {
                            LineMoveType.START
                        } else if (downX > line.end.x - TOUCH_TOLERANCE && downX < line.end.x + TOUCH_TOLERANCE &&
                            downY > line.end.y - TOUCH_TOLERANCE && downY < line.end.y + TOUCH_TOLERANCE
                        ) {
                            LineMoveType.END
                        } else {
                            LineMoveType.ALL
                        }
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val x: Int = event.x.correct(width)
                val y: Int = event.y.correct(height)
                if (isAddAction) {
                    (if (isTrend) operateTrend else operateLine)?.end?.x = x
                    (if (isTrend) operateTrend else operateLine)?.end?.y = y
                } else {
                    when (lineMoveType) {
                        LineMoveType.ALL -> { // View rendering
                            val rect: Rect = TempDrawHelper.getRect(width, height)
                            val minX: Int = min(downLine.start.x, downLine.end.x)
                            val maxX: Int = max(downLine.start.x, downLine.end.x)
                            val minY: Int = min(downLine.start.y, downLine.end.y)
                            val maxY: Int = max(downLine.start.y, downLine.end.y)
                            val biasX: Int =
                                if (x < downX) {
                                    max(
                                        x - downX,
                                        rect.left - minX,
                                    )
                                } else {
                                    min(x - downX, rect.right - maxX)
                                }
                            val biasY: Int =
                                if (y < downY) {
                                    max(
                                        y - downY,
                                        rect.top - minY,
                                    )
                                } else {
                                    min(y - downY, rect.bottom - maxY)
                                }
                            (if (isTrend) operateTrend else operateLine)?.start?.set(
                                downLine.start.x + biasX,
                                downLine.start.y + biasY,
                            )
                            (if (isTrend) operateTrend else operateLine)?.end?.set(
                                downLine.end.x + biasX,
                                downLine.end.y + biasY,
                            )
                        }
                        LineMoveType.START -> {
                            (if (isTrend) operateTrend else operateLine)?.start?.x = x
                            (if (isTrend) operateTrend else operateLine)?.start?.y = y
                        }
                        LineMoveType.END -> {
                            (if (isTrend) operateTrend else operateLine)?.end?.x = x
                            (if (isTrend) operateTrend else operateLine)?.end?.y = y
                        }
                    }
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val x: Int = event.x.correct(width)
                val y: Int = event.y.correct(height)
                val line: Line = (if (isTrend) operateTrend else operateLine) ?: Line(Point(), Point())
                if ((line.start.x / xScale).toInt() != (line.end.x / xScale).toInt() || (line.start.y / yScale).toInt() != (line.end.y / yScale).toInt()) {
                    // View rendering
                    if (isAddAction || abs(x - downX) > DELETE_TOLERANCE || abs(y - downY) > DELETE_TOLERANCE) {
                        if (isTrend) {
                            trendLine = line
                            onTrendOperateListener?.invoke(true)
                        } else {
                            synchronized(this) {
                                lineList.add(line)
                            }
                        }
                    }
                }
                operateTrend = null
                operateLine = null
                invalidate()

                if (!isTrend) {
                    onLineListener?.invoke(getSourceLineList())
                }
                return true
            }
            else -> return false
        }
    }

    private fun pollLine(
        x: Int,
        y: Int,
        isTrend: Boolean,
    ): Line? {
        if (isTrend) {
            val resultLine = trendLine
            if (isLineConcat(resultLine, x, y)) {
                trendLine = null
                return resultLine
            }
        } else {
            for (i in lineList.size - 1 downTo 0) {
                val line: Line = lineList[i]
                if (isLineConcat(line, x, y)) {
                    return synchronized(this) { lineList.removeAt(i) }
                }
            }
        }
        return null
    }

    /**
     * viewSpecifiedview (x, y) viewSpecified Line viewSelected.
     */
    private fun isLineConcat(
        line: Line?,
        x: Int,
        y: Int,
    ): Boolean {
        if (line == null) {
            return false
        }
        var tempDistance = (line.end.y - line.start.y) * x - (line.end.x - line.start.x) * y + line.end.x * line.start.y - line.start.x * line.end.y
        tempDistance =
            (
                tempDistance /
                    sqrt(
                        (line.end.y - line.start.y).toDouble().pow(
                            2.0,
                        ) + (line.end.x - line.start.x).toDouble().pow(2.0),
                    )
            ).toInt()
        return abs(
            tempDistance,
        ) < TOUCH_TOLERANCE && x > min(
            line.start.x,
            line.end.x,
        ) - TOUCH_TOLERANCE && x < max(line.start.x, line.end.x) + TOUCH_TOLERANCE
    }

    // **************************************** view ****************************************

    /**
     * Touch viewCurrentview（view、view）view.
     */
    protected var operateRect: Rect? = null

    private enum class RectMoveType { ALL, EDGE, CORNER, }

    /**
     * view：view-view、view4view-view、view4view-view。
     */
    private var rectMoveType = RectMoveType.ALL

    private enum class RectMoveEdge { LEFT, TOP, RIGHT, BOTTOM }

    /**
     * viewModeview，view.
     */
    private var rectMoveEdge = RectMoveEdge.LEFT

    private enum class RectMoveCorner { LT, RT, RB, LB }

    /**
     * viewModeview，view.
     */
    private var rectMoveCorner = RectMoveCorner.LT

    /**
     * view，view DOWN Stateview，view.
     */
    private val downRect = Rect()

    private fun touchRect(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x.correct(width)
                downY = event.y.correct(height)
                val rect: Rect? = pollRect(downX, downY)
                if (rect == null) { // View rendering
                    isAddAction = true
                    operateRect = Rect(downX, downY, downX, downY)
                    if (rectList.size == maxCount) {
                        synchronized(this) {
                            rectList.removeAt(0)
                        }
                    }
                } else { // View rendering - Delete
                    isAddAction = false
                    operateRect = rect
                    downRect.set(rect)
                    when (downX) {
                        in rect.left - TOUCH_TOLERANCE..rect.left + TOUCH_TOLERANCE -> { // Selectedview
                            when (downY) {
                                in rect.top - TOUCH_TOLERANCE..rect.top + TOUCH_TOLERANCE -> { // Selectedview
                                    rectMoveType = RectMoveType.CORNER
                                    rectMoveCorner = RectMoveCorner.LT
                                }
                                in rect.bottom - TOUCH_TOLERANCE..rect.bottom + TOUCH_TOLERANCE -> { // Selectedview
                                    rectMoveType = RectMoveType.CORNER
                                    rectMoveCorner = RectMoveCorner.LB
                                }
                                else -> {
                                    rectMoveType = RectMoveType.EDGE
                                    rectMoveEdge = RectMoveEdge.LEFT
                                }
                            }
                        }
                        in rect.right - TOUCH_TOLERANCE..rect.right + TOUCH_TOLERANCE -> { // Selectedview
                            when (downY) {
                                in rect.top - TOUCH_TOLERANCE..rect.top + TOUCH_TOLERANCE -> { // Selectedview
                                    rectMoveType = RectMoveType.CORNER
                                    rectMoveCorner = RectMoveCorner.RT
                                }
                                in rect.bottom - TOUCH_TOLERANCE..rect.bottom + TOUCH_TOLERANCE -> { // Selectedview
                                    rectMoveType = RectMoveType.CORNER
                                    rectMoveCorner = RectMoveCorner.RB
                                }
                                else -> {
                                    rectMoveType = RectMoveType.EDGE
                                    rectMoveEdge = RectMoveEdge.RIGHT
                                }
                            }
                        }
                        else -> { // View renderingSelected
                            when (downY) {
                                in rect.top - TOUCH_TOLERANCE..rect.top + TOUCH_TOLERANCE -> { // Selectedview
                                    rectMoveType = RectMoveType.EDGE
                                    rectMoveEdge = RectMoveEdge.TOP
                                }
                                in rect.bottom - TOUCH_TOLERANCE..rect.bottom + TOUCH_TOLERANCE -> { // Selectedview
                                    rectMoveType = RectMoveType.EDGE
                                    rectMoveEdge = RectMoveEdge.BOTTOM
                                }
                                else -> {
                                    rectMoveType = RectMoveType.ALL
                                }
                            }
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val x: Int = event.x.correct(width)
                val y: Int = event.y.correct(height)
                if (isAddAction) {
                    operateRect?.set(min(downX, x), min(downY, y), max(downX, x), max(downY, y))
                } else {
                    when (rectMoveType) {
                        RectMoveType.ALL -> { // View rendering
                            val rect: Rect = TempDrawHelper.getRect(width, height)
                            val biasX: Int =
                                if (x < downX) {
                                    max(
                                        x - downX,
                                        rect.left - downRect.left,
                                    )
                                } else {
                                    min(x - downX, rect.right - downRect.right)
                                }
                            val biasY: Int =
                                if (y < downY) {
                                    max(
                                        y - downY,
                                        rect.top - downRect.top,
                                    )
                                } else {
                                    min(y - downY, rect.bottom - downRect.bottom)
                                }
                            operateRect?.set(
                                downRect.left + biasX,
                                downRect.top + biasY,
                                downRect.right + biasX,
                                downRect.bottom + biasY,
                            )
                        }
                        RectMoveType.EDGE ->
                            when (rectMoveEdge) {
                                RectMoveEdge.LEFT -> { // View rendering
                                    operateRect?.left = min(x, downRect.right)
                                    operateRect?.right = max(x, downRect.right)
                                }
                                RectMoveEdge.TOP -> { // View rendering
                                    operateRect?.top = min(y, downRect.bottom)
                                    operateRect?.bottom = max(y, downRect.bottom)
                                }
                                RectMoveEdge.RIGHT -> { // View rendering
                                    operateRect?.right = max(x, downRect.left)
                                    operateRect?.left = min(x, downRect.left)
                                }
                                RectMoveEdge.BOTTOM -> { // View rendering
                                    operateRect?.bottom = max(y, downRect.top)
                                    operateRect?.top = min(y, downRect.top)
                                }
                            }
                        RectMoveType.CORNER ->
                            when (rectMoveCorner) {
                                RectMoveCorner.LT -> { // View rendering
                                    operateRect?.left = min(x, downRect.right)
                                    operateRect?.right = max(x, downRect.right)
                                    operateRect?.top = min(y, downRect.bottom)
                                    operateRect?.bottom = max(y, downRect.bottom)
                                }
                                RectMoveCorner.RT -> { // View rendering
                                    operateRect?.right = max(x, downRect.left)
                                    operateRect?.left = min(x, downRect.left)
                                    operateRect?.top = min(y, downRect.bottom)
                                    operateRect?.bottom = max(y, downRect.bottom)
                                }
                                RectMoveCorner.RB -> { // View rendering
                                    operateRect?.right = max(x, downRect.left)
                                    operateRect?.left = min(x, downRect.left)
                                    operateRect?.bottom = max(y, downRect.top)
                                    operateRect?.top = min(y, downRect.top)
                                }
                                RectMoveCorner.LB -> { // View rendering
                                    operateRect?.left = min(x, downRect.right)
                                    operateRect?.right = max(x, downRect.right)
                                    operateRect?.bottom = max(y, downRect.top)
                                    operateRect?.top = min(y, downRect.top)
                                }
                            }
                    }
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val x: Int = event.x.correct(width)
                val y: Int = event.y.correct(height)
                val rect: Rect = operateRect ?: Rect()
                if ((rect.left / xScale).toInt() != (rect.right / xScale).toInt() &&
                    (rect.top / yScale).toInt() != (rect.bottom / yScale).toInt()
                ) {
                    // View rendering
                    if (isAddAction || abs(x - downX) > DELETE_TOLERANCE || abs(y - downY) > DELETE_TOLERANCE) {
                        synchronized(this) {
                            rectList.add(rect)
                        }
                    }
                }
                operateRect = null
                invalidate()

                onRectListener?.invoke(getSourceRectList())
                return true
            }
            else -> return false
        }
    }

    private fun pollRect(
        x: Int,
        y: Int,
    ): Rect? {
        for (i in rectList.size - 1 downTo 0) {
            val rect: Rect = rectList[i]
            if (rect.left - TOUCH_TOLERANCE < x && rect.right + TOUCH_TOLERANCE > x &&
                rect.top - TOUCH_TOLERANCE < y && rect.bottom + TOUCH_TOLERANCE > y
            ) {
                return synchronized(this) { rectList.removeAt(i) }
            }
        }
        return null
    }
}
