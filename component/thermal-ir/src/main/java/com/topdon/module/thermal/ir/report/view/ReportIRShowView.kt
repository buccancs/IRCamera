package com.topdon.module.thermal.ir.report.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.ReportIRBean
import com.topdon.module.thermal.ir.report.bean.ReportTempBean
import com.topdon.lib.core.R as LibR

/**
 * Custom Report i r show view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class ReportIRShowView : LinearLayout {
    companion object {
        private const val TYPE_FULL = 0 // Full image
        private const val TYPE_POINT = 1 // 
        private const val TYPE_LINE = 2 // 
        private const val TYPE_RECT = 3 // 
    }

    // View references - migrated from synthetic views
    private lateinit var clImage: View
    private lateinit var clFull: View
    private lateinit var clPoint1: View
    private lateinit var clPoint2: View
    private lateinit var clPoint3: View
    private lateinit var clPoint4: View
    private lateinit var clPoint5: View
    private lateinit var clLine1: View
    private lateinit var clLine2: View
    private lateinit var clLine3: View
    private lateinit var clLine4: View
    private lateinit var clLine5: View
    private lateinit var clRect1: View
    private lateinit var clRect2: View
    private lateinit var clRect3: View
    private lateinit var clRect4: View
    private lateinit var clRect5: View

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflate(context, R.layout.view_report_ir_show, this)
        initViews()
        initTitleTexts()
    }

    private fun initViews() {
        clImage = findViewById(R.id.cl_image)
        clFull = findViewById(R.id.cl_full)
        clPoint1 = findViewById(R.id.cl_point1)
        clPoint2 = findViewById(R.id.cl_point2)
        clPoint3 = findViewById(R.id.cl_point3)
        clPoint4 = findViewById(R.id.cl_point4)
        clPoint5 = findViewById(R.id.cl_point5)
        clLine1 = findViewById(R.id.cl_line1)
        clLine2 = findViewById(R.id.cl_line2)
        clLine3 = findViewById(R.id.cl_line3)
        clLine4 = findViewById(R.id.cl_line4)
        clLine5 = findViewById(R.id.cl_line5)
        clRect1 = findViewById(R.id.cl_rect1)
        clRect2 = findViewById(R.id.cl_rect2)
        clRect3 = findViewById(R.id.cl_rect3)
        clRect4 = findViewById(R.id.cl_rect4)
        clRect5 = findViewById(R.id.cl_rect5)
    }

    private fun initTitleTexts() {
        initTitleText(clFull, TYPE_FULL, 0)

        initTitleText(clPoint1, TYPE_POINT, 0)
        initTitleText(clPoint2, TYPE_POINT, 1)
        initTitleText(clPoint3, TYPE_POINT, 2)
        initTitleText(clPoint4, TYPE_POINT, 3)
        initTitleText(clPoint5, TYPE_POINT, 4)

        initTitleText(clLine1, TYPE_LINE, 0)
        initTitleText(clLine2, TYPE_LINE, 1)
        initTitleText(clLine3, TYPE_LINE, 2)
        initTitleText(clLine4, TYPE_LINE, 3)
        initTitleText(clLine5, TYPE_LINE, 4)

        initTitleText(clRect1, TYPE_RECT, 0)
        initTitleText(clRect2, TYPE_RECT, 1)
        initTitleText(clRect3, TYPE_RECT, 2)
        initTitleText(clRect4, TYPE_RECT, 3)
        initTitleText(clRect5, TYPE_RECT, 4)
    }

    private fun initTitleText(
        itemRoot: View,
        type: Int,
        index: Int,
    ) {
        val tvTitle = itemRoot.findViewById<TextView>(R.id.tv_title)
        val tvAverageTitle = itemRoot.findViewById<TextView>(R.id.tv_average_title)
        val tvExplainTitle = itemRoot.findViewById<TextView>(R.id.tv_explain_title)

        tvTitle.isVisible = index == 0
        tvTitle.text =
            when (type) {
                TYPE_FULL -> context.getString(LibR.string.thermal_full_rect)
                TYPE_POINT -> context.getString(LibR.string.thermal_point) + "(P)"
                TYPE_LINE -> context.getString(LibR.string.thermal_line) + "(L)"
                else -> context.getString(LibR.string.thermal_rect) + "(R)"
            }
        tvAverageTitle.text =
            when (type) {
                TYPE_FULL, TYPE_POINT -> "" // Full image and points have no average temperature
                TYPE_LINE -> "L${index + 1} " + context.getString(LibR.string.album_report_mean_temperature)
                else -> "R${index + 1} " + context.getString(LibR.string.album_report_mean_temperature)
            }
        tvExplainTitle.text =
            when (type) {
                TYPE_FULL -> context.getString(LibR.string.album_report_comment)
                TYPE_POINT -> "P${index + 1} " + context.getString(LibR.string.album_report_comment)
                TYPE_LINE -> "L${index + 1} " + context.getString(LibR.string.album_report_comment)
                else -> "R${index + 1} "Test Data"P${index + 1} " + context.getString(LibR.string.chart_temperature)
            } else {
                val prefix =
                    when (type) {
                        TYPE_LINE -> "L${index + 1} "
                        TYPE_RECT -> "R${index + 1} "
                        else -> ""
                    }
                prefix +
                    if (tempBean.isMinOpen() && tempBean.isMaxOpen()) {
                        context.getString(LibR.string.chart_temperature_low) + "-" + context.getString(LibR.string.chart_temperature_high)
                    } else if (tempBean.isMinOpen()) {
                        context.getString(LibR.string.chart_temperature_low)
                    } else {
                        context.getString(LibR.string.chart_temperature_high)
                    }
            }
        val rangeValue =
            if (type == TYPE_POINT) {
                tempBean.temperature
            } else {
                if (tempBean.isMinOpen() && tempBean.isMaxOpen()) {
                    tempBean.min_temperature + "~" + tempBean.max_temperature
                } else if (tempBean.isMinOpen()) {
                    tempBean.min_temperature
                } else {
                    tempBean.max_temperature
                }
            }

        val tvRangeTitle = itemRoot.findViewById<TextView>(R.id.tv_range_title)
        val tvRangeValue = itemRoot.findViewById<TextView>(R.id.tv_range_value)
        val viewLineRange = itemRoot.findViewById<View>(R.id.view_line_range)
        val clAverage = itemRoot.findViewById<View>(R.id.cl_average)
        val clExplain = itemRoot.findViewById<View>(R.id.cl_explain)
        val tvAverageValue = itemRoot.findViewById<TextView>(R.id.tv_average_value)
        val tvExplainValue = itemRoot.findViewById<TextView>(R.id.tv_explain_value)

        tvRangeTitle.isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        tvRangeValue.isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        viewLineRange.isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        clAverage.isVisible = (type == TYPE_LINE || type == TYPE_RECT) && tempBean.isAverageOpen()
        clExplain.isVisible = tempBean.isExplainOpen()
        tvRangeTitle.text = rangeTitle
        tvRangeValue.text = rangeValue
        tvAverageValue.text = tempBean.mean_temperature
        tvExplainValue.text = tempBean.comment
    }
}
