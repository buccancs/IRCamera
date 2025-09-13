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
\1pseudo-colorset， 7 color block View.
 *
\1：
\1- [reset] color value
\1- [refreshColor] color blockset
\1- [add] color block
\1- [del] color block
\1- [isCurrentOnlyLimit] color block：( || ) && 
 *
 * Created by LCG on 2024/10/15.
 */
class PseudoPickView : View {
    companion object {
        @CheckResult
        private fun IntArray.add(
            index: Int,
            element: Int,
        ): IntArray {
            val newArray = IntArray(this.size + 1)
            System.arraycopy(this, 0, newArray, 0, index)
            newArray[index] = element
            System.arraycopy(this, index, newArray, index + 1, this.size - index)
            return newArray
        }

        @CheckResult
        private fun FloatArray.add(
            index: Int,
            element: Float,
        ): FloatArray {
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
\1drawing Paint.
     */
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
\1drawingcolor block Pint.
     */
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
\1color block Drawable.
     */
    private val selectYesDrawable: Drawable

    /**
\1color block Drawable.
     */
    private val selectNotDrawable: Drawable

    /**
\1color block.
     */
    var onSelectChangeListener: ((selectIndex: Int) -> Unit)? = null

    /**
\1color block index.
     */
    var selectIndex = 0

    /**
\1color block，color block，arraysavearray.
\1 place ， place  zAltitude .
\1size  [actualColors]、[zAltitudes]、[places] 。
     */
    var sourceColors: IntArray = intArrayOf(0xff0000ff.toInt(), 0xffff0000.toInt(), 0xffffff00.toInt())

    /**
\1color block，color block，arraysavearray.
     */
    var actualColors: IntArray = intArrayOf(0xff0000ff.toInt(), 0xffff0000.toInt(), 0xffffff00.toInt())

    /**
\1color block z altitudearray，color block。
     */
    var zAltitudes: IntArray = intArrayOf(0, 0, 0)

    /**
\1color blockarray.
     */
    var places: FloatArray = floatArrayOf(0f, 0.5f, 1f)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
    ) {
        selectYesDrawable = ContextCompat.getDrawable(context, R.drawable.svg_pseudo_triangle_select)!!
        selectNotDrawable = ContextCompat.getDrawable(context, R.drawable.svg_pseudo_triangle_not_select)!!
        selectYesDrawable.setBounds(0, 0, SizeUtils.dp2px(16f), SizeUtils.dp2px(10f))
        selectNotDrawable.setBounds(0, 0, SizeUtils.dp2px(16f), SizeUtils.dp2px(10f))
    }

    /**
\1color valueconfiguration.
\1@param selectIndex color block index
\1@param colors color blockarray
\1@param zAltitudes color block z altitudearray
\1@param places color blockarray
     */
    fun reset(
        selectIndex: Int,
        colors: IntArray,
        zAltitudes: IntArray,
        places: FloatArray,
    ) {
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
\1color valueset
     */
    fun refreshColor(
        @ColorInt color: Int,
    ) {
        sourceColors[selectIndex] = color
        actualColors[selectIndex] = color
        refreshActualColors()
        barPaint.shader = LinearGradient(barRect.left, 0f, barRect.right, 0f, actualColors, places, Shader.TileMode.CLAMP)
        invalidate()
    }

    /**
\1 、、、 ，.
     */
    private var addCount = 0

    /**
\1color block
     */
    fun add() {
        if (sourceColors.size >= 7) { // 7
            return
        }
        addCount++
        if (addCount > 4) {
            addCount = 1
        }
        val addColor: Int =
            when (addCount) {
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
\1color block.
     */
    fun del() {
        if (sourceColors.size <= 3) {
            return
        }
        if (isCurrentOnlyLimit()) { // 
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
\1color block：( || ) && 
     */
    fun isCurrentOnlyLimit(): Boolean {
        val place: Float = places[selectIndex]
        if (place == 0f || place == 1f) { // ，
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
\1color block、、z ，array.
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
\1 place calculation ZAltitude.
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
\1 Rect.
     */
    private val barRect = RectF()

    /**
\1color block， px.
     */
    private val selectRadius: Int = SizeUtils.dp2px(12f)

    @SuppressLint("DrawAllocation"Test Data"ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouch = false
                canDrag = false
                downX = event.x.toInt()

\1TextaltitudeTextcolor block index
                var targetIndex = -1
                for (i in places.indices) {
                    val centerX: Int = (barRect.left + barRect.width() * places[i]).toInt()
                    if (downX >= centerX - selectRadius && downX <= centerX + selectRadius) { // 
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
                    if (newPlace == oldPlace) { // ，
                        return handleTouch
                    }
                    val currentColor: Int = sourceColors[selectIndex]
                    val oldIndex: Int = selectIndex
                    var newIndex: Int = selectIndex
                    if (oldPlace < newPlace) { // 
                        for (i in places.indices) {
                            if (places[i] <= newPlace) {
                                newIndex = i
                            } else {
                                break
                            }
                        }
                    } else { // 
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
