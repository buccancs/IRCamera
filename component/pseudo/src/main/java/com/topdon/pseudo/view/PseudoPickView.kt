package com.topdon.pseudo.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.topdon.pseudo.R
import kotlin.math.abs

/**
 * [Chinese text]Pseudo colorSettings[Chinese text]in progress, [Chinese text] 7 [Chinese text]circular[Chinese text] View.
 *
 * [Chinese text]: 
 * - [reset] [Chinese text]
 * - [refreshColor] [Chinese text]in progress[Chinese text]circular[Chinese text]Settings[Chinese text]
 * - [add] [Chinese text]circular[Chinese text]
 * - [del] [Chinese text]in progresscircular[Chinese text]
 * - [isCurrentOnlyLimit] [Chinese text]in progresscircular[Chinese text]: ([Chinese text] || [Chinese text]) && [Chinese text]
 *
 * Created by LCG on 2024/10/15.
 */
class PseudoPickView : View {
    companion object {
        @CheckResult
        private fun IntArray.add(index: Int, element: Int): IntArray {
            val newArray = IntArray(this.size + 1)
            System.arraycopy(this, 0, newArray, 0, index)
            newArray[index] = element
            System.arraycopy(this, index, newArray, index + 1, this.size - index)
            return newArray
        }
        @CheckResult
        private fun FloatArray.add(index: Int, element: Float): FloatArray {
            val newArray = FloatArray(this.size + 1)
            System.arraycopy(this, 0, newArray, 0, index)
            newArray[index] = element
            System.arraycopy(this, index, newArray, index + 1, this.size - index)
            return newArray
        }
        @CheckResult
        private fun IntArray.removeAt(index: Int): IntArray {
            val newArray = IntArray(this.size - 1)
            System.arraycopy(this, 0, newArray, 0, index)
            System.arraycopy(this, index + 1, newArray, index, this.size - index - 1)
            return newArray
        }
        @CheckResult
        private fun FloatArray.removeAt(index: Int): FloatArray {
            val newArray = FloatArray(this.size - 1)
            System.arraycopy(this, 0, newArray, 0, index)
            System.arraycopy(this, index + 1, newArray, index, this.size - index - 1)
            return newArray
        }
    }

    /**
     * [Chinese text]used by[Chinese text] Paint.
     */
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * [Chinese text]circular[Chinese text]used by[Chinese text] Pint.
     */
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * circular[Chinese text]in progress[Chinese text] Drawable.
     */
    private val selectYesDrawable: Drawable
    /**
     * circular[Chinese text]in progress[Chinese text] Drawable.
     */
    private val selectNotDrawable: Drawable

    /**
     * [Chinese text]in progress[Chinese text]eventlistener.
     */
    var onSelectChangeListener: ((selectIndex: Int) -> Unit)? = null

    /**
     * [Chinese text]in progress[Chinese text]circular[Chinese text]in progress[Chinese text] index.
     */
    var selectIndex = 0
    /**
     * [Chinese text]circular[Chinese text], [Chinese text]circular[Chinese text], [Chinese text].
     * [Chinese text] place [Chinese text], [Chinese text] place [Chinese text] zAltitude [Chinese text].
     * size [Chinese text] [actualColors], [zAltitudes], [places] [Chinese text]. 
     */
    var sourceColors: IntArray = intArrayOf(0xff0000ff.toInt(), 0xffff0000.toInt(), 0xffffff00.toInt())
    /**
     * [Chinese text]circular[Chinese text], [Chinese text]circular[Chinese text], [Chinese text].
     */
    var actualColors: IntArray = intArrayOf(0xff0000ff.toInt(), 0xffff0000.toInt(), 0xffffff00.toInt())
    /**
     * [Chinese text]circular[Chinese text] z [Chinese text], [Chinese text]circular[Chinese text]. 
     */
    var zAltitudes: IntArray = intArrayOf(0, 0, 0)
    /**
     * [Chinese text]circular[Chinese text].
     */
    var places: FloatArray = floatArrayOf(0f, 0.5f, 1f)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes:Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        selectYesDrawable = ContextCompat.getDrawable(context, R.drawable.svg_pseudo_triangle_select)!!
        selectNotDrawable = ContextCompat.getDrawable(context, R.drawable.svg_pseudo_triangle_not_select)!!
        selectYesDrawable.setBounds(0, 0, SizeUtils.dp2px(16f), SizeUtils.dp2px(10f))
        selectNotDrawable.setBounds(0, 0, SizeUtils.dp2px(16f), SizeUtils.dp2px(10f))
    }

    /**
     * [Chinese text].
     * @param selectIndex [Chinese text]in progress[Chinese text]circular[Chinese text] index
     * @param colors [Chinese text]circular[Chinese text]
     * @param zAltitudes [Chinese text]circular[Chinese text] z [Chinese text]
     * @param places [Chinese text]circular[Chinese text]
     */
    fun reset(selectIndex: Int, colors: IntArray, zAltitudes: IntArray, places: FloatArray) {
        this.selectIndex = selectIndex
        this.sourceColors = colors
        this.zAltitudes = zAltitudes
        this.places = places
        refreshActualColors()
        barPaint.shader = LinearGradient(barRect.left, 0f, barRect.right, 0f, actualColors, places, Shader.TileMode.CLAMP)
        invalidate()
        onSelectChangeListener?.invoke(selectIndex)
    }

    /**
     * [Chinese text]in progress[Chinese text]Settings[Chinese text]
     */
    fun refreshColor(@ColorInt color: Int) {
        sourceColors[selectIndex] = color
        actualColors[selectIndex] = color
        refreshActualColors()
        barPaint.shader = LinearGradient(barRect.left, 0f, barRect.right, 0f, actualColors, places, Shader.TileMode.CLAMP)
        invalidate()
    }

    /**
     * [Chinese text] [Chinese text], [Chinese text], [Chinese text], [Chinese text] [Chinese text], [Chinese text].
     */
    private var addCount = 0
    /**
     * [Chinese text]circular[Chinese text]
     */
    fun add() {
        if (sourceColors.size >= 7) {// [Chinese text]7[Chinese text]circular[Chinese text]
            return
        }
        addCount++
        if (addCount > 4) {
            addCount = 1
        }
        val addColor: Int = when (addCount) {
            1 -> 0xff00ff00.toInt()
            2 -> 0xff000000.toInt()
            3 -> 0xffffffff.toInt()
            else -> 0xff982abc.toInt()
        }
        var addIndex = 0
        for (i in places.size - 1 downTo 1) {
            val place = places[i]
            if (place > 0.75f) {
                addIndex = i
            } else if (place < 0.75f) {
                break
            } else {
                addIndex = i + 1
                break
            }
        }

        sourceColors = sourceColors.add(addIndex, addColor)
        zAltitudes = zAltitudes.add(addIndex, calculateZAltitude(0.75f))
        places = places.add(addIndex, 0.75f)
        selectIndex = addIndex
        refreshActualColors()
        barPaint.shader = LinearGradient(barRect.left, 0f, barRect.right, 0f, actualColors, places, Shader.TileMode.CLAMP)
        invalidate()
        onSelectChangeListener?.invoke(selectIndex)
    }

    /**
     * [Chinese text]in progresscircular[Chinese text].
     */
    fun del() {
        if (sourceColors.size <= 3) {
            return
        }
        if (isCurrentOnlyLimit()) {// only[Chinese text]not allowed[Chinese text]
            return
        }

        sourceColors = sourceColors.removeAt(selectIndex)
        zAltitudes = zAltitudes.removeAt(selectIndex)
        places = places.removeAt(selectIndex)
        selectIndex = 0
        for (i in zAltitudes.indices) {
            if (zAltitudes[i] >= zAltitudes[selectIndex]) {
                selectIndex = i
            }
        }
        refreshActualColors()
        barPaint.shader = LinearGradient(barRect.left, 0f, barRect.right, 0f, actualColors, places, Shader.TileMode.CLAMP)
        invalidate()
        onSelectChangeListener?.invoke(selectIndex)
    }

    /**
     * [Chinese text]in progresscircular[Chinese text]: ([Chinese text] || [Chinese text]) && [Chinese text]
     */
    fun isCurrentOnlyLimit(): Boolean {
        val place: Float = places[selectIndex]
        if (place == 0f || place == 1f) {// [Chinese text], [Chinese text]
            for (i in places.indices) {
                if (i != selectIndex && places[i] == place) {
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     * [Chinese text]circular[Chinese text], [Chinese text], z [Chinese text]high[Chinese text], [Chinese text].
     */
    private fun refreshActualColors() {
        if (actualColors.size != sourceColors.size) {
            actualColors = IntArray(sourceColors.size)
        }
        System.arraycopy(sourceColors, 0, actualColors, 0, sourceColors.size)
        for (i in places.size - 1 downTo 1) {
            if (places[i - 1] == places[i]) {
                actualColors[i - 1] = actualColors[i]
            }
        }
    }

    /**
     * [Chinese text] place [Chinese text] ZAltitude.
     */
    private fun calculateZAltitude(place: Float): Int {
        var result = 0
        val gap: Float = selectRadius * 2 / barRect.width()
        for (i in places.indices) {
            if (abs(places[i] - place) <= gap) {
                result = result.coerceAtLeast(zAltitudes[i] + 1)
            }
        }
        return result
    }

    /**
     * [Chinese text] Rect.
     */
    private val barRect = RectF()
    /**
     * [Chinese text]circular[Chinese text]in progress[Chinese text], [Chinese text] px.
     */
    private val selectRadius: Int = SizeUtils.dp2px(12f)

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        barRect.set(selectRadius.toFloat(), 0f, (widthSize - selectRadius).toFloat(), ((widthSize - selectRadius * 2) * 30 / 311f).toInt().toFloat())
        barPaint.shader = LinearGradient(barRect.left, 0f, barRect.right, 0f, actualColors, places, Shader.TileMode.CLAMP)

        // 2dp [Chinese text]
        val wantHeight: Int = barRect.height().toInt() + SizeUtils.dp2px(2f) + selectNotDrawable.bounds.height() + selectRadius * 2

        // [Chinese text] UNSPECIFIED [Chinese text], [Chinese text]; high[Chinese text] wrap_content [Chinese text], [Chinese text]
        setMeasuredDimension(widthSize, wantHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // [Chinese text]Pseudo color[Chinese text]
        val barRadius = SizeUtils.dp2px(4f).toFloat()
        canvas.drawRoundRect(barRect.left, 0f, barRect.right, barRect.bottom, barRadius, barRadius, barPaint)

        canvas.translate(0f, barRect.bottom + SizeUtils.dp2px(2f))
        val strokeWidth: Float = SizeUtils.dp2px(1.5f).toFloat()
        val circleRadius: Float = (selectRadius - strokeWidth * 2).toInt().toFloat()

        var minZAltitude = 0
        var maxZAltitude = 0
        for (altitude in zAltitudes) {
            minZAltitude = minZAltitude.coerceAtMost(altitude)
            maxZAltitude = maxZAltitude.coerceAtLeast(altitude)
        }
        for (altitude in minZAltitude .. maxZAltitude) {
            for (i in zAltitudes.indices) {
                if (zAltitudes[i] == altitude) {
                    val x: Float = barRect.left + barRect.width() * places[i]
                    val y: Float = (selectNotDrawable.bounds.height() + selectRadius).toFloat()
                    if (i == selectIndex) {
                        circlePaint.color = 0xffffffff.toInt()
                        canvas.drawCircle(x, y, selectRadius.toFloat(), circlePaint)
                        circlePaint.color = 0xff16131e.toInt()
                        canvas.drawCircle(x, y, selectRadius - strokeWidth, circlePaint)
                    }

                    circlePaint.color = actualColors[i]
                    canvas.drawCircle(x, y, circleRadius, circlePaint)

                    canvas.save()
                    canvas.translate(x - selectNotDrawable.bounds.width() / 2, 0f)
                    (if (i == selectIndex) selectYesDrawable else selectNotDrawable).draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    /**
     * Touch Down [Chinese text] x [Chinese text], for[Chinese text]swipe[Chinese text], [Chinese text]swipe. 
     */
    private var downX = 0
    /**
     * [Chinese text] Touch event.
     */
    private var handleTouch = false
    /**
     * [Chinese text]in progress[Chinese text], [Chinese text]swipe. 
     */
    private var canDrag = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouch = false
                canDrag = false
                downX = event.x.toInt()

                // [Chinese text]point[Chinese text]range[Chinese text]high[Chinese text]circular[Chinese text] index
                var targetIndex = -1
                for (i in places.indices) {
                    val centerX: Int = (barRect.left + barRect.width() * places[i]).toInt()
                    if (downX >= centerX - selectRadius && downX <= centerX + selectRadius) {// [Chinese text]circular[Chinese text]range[Chinese text]
                        if (targetIndex == -1) {
                            targetIndex = i
                            continue
                        }
                        if (zAltitudes[i] >= zAltitudes[targetIndex]) {
                            targetIndex = i
                        }
                    }
                }
                if (targetIndex >= 0) {
                    zAltitudes[targetIndex] = calculateZAltitude(places[targetIndex])
                    selectIndex = targetIndex
                    invalidate()
                    handleTouch = true
                    canDrag = !isCurrentOnlyLimit()
                    onSelectChangeListener?.invoke(selectIndex)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x.coerceAtLeast(barRect.left).coerceAtMost(barRect.right).toInt()
                if (canDrag) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    val oldPlace: Float = places[selectIndex]
                    val newPlace: Float = (x - barRect.left) / barRect.width()
                    if (newPlace == oldPlace) {// [Chinese text], [Chinese text]
                        return handleTouch
                    }
                    val currentColor: Int = sourceColors[selectIndex]
                    val oldIndex: Int = selectIndex
                    var newIndex: Int = selectIndex
                    if (oldPlace < newPlace) {// [Chinese text]
                        for (i in places.indices) {
                            if (places[i] <= newPlace) {
                                newIndex = i
                            } else {
                                break
                            }
                        }
                    } else {// [Chinese text]
                        for (i in places.size - 1 downTo 0) {
                            val place = places[i]
                            if (place > newPlace) {
                                newIndex = i
                            } else if (place < newPlace) {
                                break
                            } else {
                                newIndex = i + 1
                                break
                            }
                        }
                    }
                    if (newIndex < oldIndex) {
                        System.arraycopy(sourceColors, newIndex, sourceColors, newIndex + 1, oldIndex - newIndex)
                        System.arraycopy(zAltitudes, newIndex, zAltitudes, newIndex + 1, oldIndex - newIndex)
                        System.arraycopy(places, newIndex, places, newIndex + 1, oldIndex - newIndex)
                        selectIndex = newIndex
                        sourceColors[newIndex] = currentColor
                    } else if (newIndex > oldIndex) {
                        System.arraycopy(sourceColors, oldIndex + 1, sourceColors, oldIndex, newIndex - oldIndex)
                        System.arraycopy(zAltitudes, oldIndex + 1, zAltitudes, oldIndex, newIndex - oldIndex)
                        System.arraycopy(places, oldIndex + 1, places, oldIndex, newIndex - oldIndex)
                        selectIndex = newIndex
                        sourceColors[newIndex] = currentColor
                    }
                    places[newIndex] = newPlace
                    zAltitudes[newIndex] = calculateZAltitude(newPlace)
                    refreshActualColors()
                    barPaint.shader = LinearGradient(barRect.left, 0f, barRect.right, 0f, actualColors, places, Shader.TileMode.CLAMP)
                    invalidate()
                }
            }
        }
        return handleTouch
    }
}