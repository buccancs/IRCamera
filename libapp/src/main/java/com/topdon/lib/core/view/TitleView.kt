package com.topdon.lib.core.view

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.R

/**
 * view View.
 *
 * view：
 * - view View [tvLeft]，view
 * - view View 1 [tvRight1]
 * - view View 2 [tvRight2]，view
 * - view View 3 [tvRight3]，view
 * - view [tvTitle]，view，view
 *
 * view 16sp， #ffffff，view padding 0dp，view padding 12dp；
 * view 24dp，view；
 *
 * view ?attr/actionBarSize.
 *
 * Created by LCG on 2023/10/19.
 */
open class TitleView : ViewGroup {
    companion object {
        /**
         * view，view 48dp，Current View view 48dp.
         */
        private const val ICON_SIZE = 48f
    }

    /**
     * view.
     *
     * true-view false-view
     */
    private val isTitleCenter: Boolean

    /**
     * Currentview actionBarSize，view measure view.
     */
    private val actionBarSize: Int

    /**
     * view View.
     */
    protected var tvLeft: MyTextView? = null

    /**
     * view View 1.
     */
    protected var tvRight1: MyTextView? = null

    /**
     * view View 2.
     */
    protected var tvRight2: MyTextView? = null

    /**
     * view View 3.
     */
    protected var tvRight3: MyTextView? = null

    /**
     * view.
     */
    protected var tvTitle: MyTextView? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
    ) {
        val typedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        actionBarSize = typedArray.getDimensionPixelSize(0, 0)
        typedArray.recycle()

        initView()

        tvTitle?.setPadding(0)
        tvTitle?.isVisible = true
        tvTitle?.maxLines = 2
        tvTitle?.ellipsize = TextUtils.TruncateAt.END

        val a = context.obtainStyledAttributes(attrs, R.styleable.TitleView, defStyleAttr, 0)

        tvLeft?.text = a.getText(R.styleable.TitleView_leftText)
        tvLeft?.setOnlyDrawableStart(a.getDrawable(R.styleable.TitleView_leftDrawable))
        tvLeft?.isVisible = tvLeft?.text?.isNotEmpty() == true || tvLeft!!.hasAnyDrawable()
        val leftColor: ColorStateList? = a.getColorStateList(R.styleable.TitleView_leftTextColor)
        if (leftColor != null) {
            tvLeft?.setTextColor(leftColor)
        }
        if (a.getBoolean(R.styleable.TitleView_isInitLeft, true)) {
            tvLeft?.isVisible = true
            tvLeft?.setOnlyDrawableStart(R.drawable.ic_back_white_svg)
            tvLeft?.setOnClickListener {
                if (context is Activity) {
                    context.finish()
                }
            }
        }

        tvRight1?.text = a.getText(R.styleable.TitleView_rightText)
        tvRight1?.setOnlyDrawableStart(a.getDrawable(R.styleable.TitleView_rightDrawable))
        tvRight1?.isVisible = tvRight1?.text?.isNotEmpty() == true || tvRight1!!.hasAnyDrawable()
        val rightColor: ColorStateList? = a.getColorStateList(R.styleable.TitleView_rightTextColor)
        if (rightColor != null) {
            tvRight1?.setTextColor(rightColor)
        }

        // view 2、3 View view，viewSettingsview
        tvRight2?.setOnlyDrawableStart(a.getDrawable(R.styleable.TitleView_right2Drawable))
        tvRight2?.isVisible = tvRight2!!.hasAnyDrawable()
        tvRight3?.setOnlyDrawableStart(a.getDrawable(R.styleable.TitleView_right3Drawable))
        tvRight3?.isVisible = tvRight3!!.hasAnyDrawable()

        isTitleCenter = a.getBoolean(R.styleable.TitleView_isTitleCenter, false)
        tvTitle?.text = a.getText(R.styleable.TitleView_titleText)
        tvTitle?.gravity = if (isTitleCenter) Gravity.CENTER else (Gravity.CENTER_VERTICAL or Gravity.START)
        a.recycle()
    }

    open fun initView() {
        tvLeft = addTextView(context)
        tvRight1 = addTextView(context)
        tvRight2 = addTextView(context)
        tvRight3 = addTextView(context)
        tvTitle = addTextView(context)
    }

    /**
     * view TextView viewCurrent View view.
     */
    fun addTextView(
        context: Context,
        padding: Float,
        imgHeight: Float,
    ): MyTextView {
        val textView = MyTextView(context)
        textView.isVisible = false
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.textSize = 16f
        textView.setTextColor(0xffffffff.toInt())
        textView.setPadding(SizeUtils.dp2px(padding))
        textView.setDrawableHeightPx(SizeUtils.dp2px(imgHeight))
        addView(textView)
        return textView
    }

    fun addTextView(context: Context): MyTextView {
        return addTextView(context, 12f, 24f)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        // view
        var maxHeight = actionBarSize.coerceAtLeast(SizeUtils.dp2px(ICON_SIZE))
        for (i in 0 until childCount) {
            val childView: View = getChildAt(i)
            if (childView != tvTitle && childView.visibility != View.GONE) {
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)
                maxHeight = maxHeight.coerceAtLeast(childView.measuredHeight)
            }
        }

        // view UNSPECIFIED view，view
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxHeight)

        // view View
        for (i in 0 until childCount) {
            val childView: View = getChildAt(i)
            if (childView != tvTitle && childView.visibility != View.GONE) {
                val widthSpec = MeasureSpec.makeMeasureSpec(childView.measuredWidth, MeasureSpec.EXACTLY)
                childView.measure(widthSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY))
            }
        }

        // view
        if (isTitleCenter) { // view
            val leftSize = if (tvLeft!!.isVisible) tvLeft?.measuredWidth else SizeUtils.dp2px(ICON_SIZE)
            var rightSize = 0
            if (tvRight1!!.isVisible) {
                rightSize += tvRight1!!.measuredWidth
            }
            if (tvRight2!!.isVisible) {
                rightSize += tvRight2!!.measuredWidth
            }
            if (tvRight3!!.isVisible) {
                rightSize += tvRight3!!.measuredWidth
            }
            if (rightSize == 0) { // view，view ICON_SIZE view margin
                rightSize = SizeUtils.dp2px(ICON_SIZE)
            }
            val titleWidth = measuredWidth - leftSize!!.coerceAtLeast(rightSize) * 2
            val widthSpec = MeasureSpec.makeMeasureSpec(titleWidth.coerceAtLeast(0), MeasureSpec.EXACTLY)
            tvTitle?.measure(widthSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY))
        } else { // view
            var titleWidth = measuredWidth
            titleWidth -= if (tvLeft!!.isVisible) tvLeft!!.measuredWidth else SizeUtils.dp2px(ICON_SIZE)
            titleWidth -= if (tvRight1!!.isVisible) tvRight1!!.measuredWidth else SizeUtils.dp2px(ICON_SIZE)
            if (tvRight2!!.isVisible) {
                titleWidth -= tvRight2!!.measuredWidth
            }
            if (tvRight3!!.isVisible) {
                titleWidth -= tvRight3!!.measuredWidth
            }
            val widthSpec = MeasureSpec.makeMeasureSpec(titleWidth.coerceAtLeast(0), MeasureSpec.EXACTLY)
            tvTitle?.measure(widthSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY))
        }
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int,
    ) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (!child.isVisible) {
                continue
            }
            val childWidth = child.measuredWidth
            when (child) {
                tvLeft -> child.layout(0, 0, childWidth, measuredHeight)
                tvRight1 -> child.layout(measuredWidth - childWidth, 0, measuredWidth, measuredHeight)
                tvRight2 -> {
                    val right = measuredWidth - tvRight1!!.measuredWidth
                    child.layout(right - tvRight2!!.measuredWidth, 0, right, measuredHeight)
                }
                tvRight3 -> {
                    val right = measuredWidth - tvRight1!!.measuredWidth - tvRight2!!.measuredWidth
                    child.layout(right - tvRight3!!.measuredWidth, 0, right, measuredHeight)
                }
                tvTitle -> {
                    if (isTitleCenter) {
                        val margin = (measuredWidth - childWidth) / 2
                        child.layout(margin, 0, margin + childWidth, measuredHeight)
                    } else {
                        val left = if (tvLeft!!.isVisible) tvLeft!!.measuredWidth else SizeUtils.dp2px(ICON_SIZE)
                        child.layout(left, 0, left + childWidth, measuredHeight)
                    }
                }
            }
        }
    }

    /**
     * Settingsview.
     */
    fun setTitleText(
        @StringRes resId: Int,
    ) {
        tvTitle?.setText(resId)
        tvTitle?.invalidate()
    }

    /**
     * Settingsview.
     */
    fun setTitleText(title: CharSequence?) {
        tvTitle?.text = title
        tvTitle?.invalidate()
    }

    /**
     * Settingsview View view.
     * NoteviewSettingsviewSettingsview Gone，view。
     */
    var isLeftVisible: Boolean
        get() = tvLeft!!.isVisible
        set(value) {
            if (tvLeft?.isVisible != value) {
                tvLeft?.isVisible = value
                requestLayout()
            }
        }

    /**
     * view View viewSettingsviewSpecifiedview.
     */
    fun setLeftDrawable(
        @DrawableRes resId: Int,
    ) {
        tvLeft?.isVisible = resId != 0 || tvLeft?.text?.isNotEmpty() == true
        tvLeft?.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
        requestLayout()
    }

    /**
     * view View viewSettingsviewSpecifiedview.
     */
    fun setLeftText(
        @StringRes resId: Int,
    ) {
        tvLeft?.setText(resId)
        tvLeft?.isVisible = true
        requestLayout()
    }

    /**
     * view View viewSettingsviewSpecifiedview.
     */
    fun setLeftText(text: CharSequence?) {
        tvLeft?.text = text
        tvLeft?.isVisible = text?.isNotEmpty() == true || tvLeft!!.hasAnyDrawable()
        requestLayout()
    }

    /**
     * Settingsview View view.
     */
    fun setLeftClickListener(leftClickListener: OnClickListener?) {
        tvLeft?.setOnClickListener(leftClickListener)
    }

    /**
     * Settingsview View view.
     * NoteviewSettingsviewSettingsview Gone，view。
     */
    var isRightVisible: Boolean
        get() = tvRight1!!.isVisible
        set(value) {
            if (tvRight1?.isVisible != value) {
                tvRight1?.isVisible = value
                requestLayout()
            }
        }

    /**
     * view View viewSettingsviewSpecifiedview.
     */
    fun setRightDrawable(
        @DrawableRes resId: Int,
    ) {
        tvRight1?.isVisible = resId != 0 || tvRight1?.text?.isNotEmpty() == true
        tvRight1?.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
        requestLayout()
    }

    /**
     * view View viewSettingsviewSpecifiedview.
     */
    fun setRightText(
        @StringRes resId: Int,
    ) {
        tvRight1?.setText(resId)
        tvRight1?.isVisible = true
        requestLayout()
    }

    /**
     * view View viewSettingsviewSpecifiedview.
     */
    fun setRightText(text: CharSequence?) {
        tvRight1?.text = text
        tvRight1?.isVisible = text?.isNotEmpty() == true || tvRight1!!.hasAnyDrawable()
        requestLayout()
    }

    /**
     * Settingsview View view.
     */
    fun setRightClickListener(rightClickListener: OnClickListener?) {
        tvRight1?.setOnClickListener(rightClickListener)
    }

    /**
     * view View 2 viewSettingsviewSpecifiedview.
     */
    fun setRight2Drawable(
        @DrawableRes resId: Int,
    ) {
        tvRight2?.isVisible = resId != 0 || tvRight2?.text?.isNotEmpty() == true
        tvRight2?.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
        requestLayout()
    }

    /**
     * Settingsview View 2 view.
     */
    fun setRight2ClickListener(right2ClickListener: OnClickListener?) {
        tvRight2?.setOnClickListener(right2ClickListener)
    }

    /**
     * view View 3 viewSettingsviewSpecifiedview.
     */
    fun setRight3Drawable(
        @DrawableRes resId: Int,
    ) {
        tvRight3?.isVisible = resId != 0 || tvRight3?.text?.isNotEmpty() == true
        tvRight3?.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
        requestLayout()
    }

    /**
     * Settingsview View 3 view.
     */
    fun setRight3ClickListener(right3ClickListener: OnClickListener?) {
        tvRight3?.setOnClickListener(right3ClickListener)
    }
}
