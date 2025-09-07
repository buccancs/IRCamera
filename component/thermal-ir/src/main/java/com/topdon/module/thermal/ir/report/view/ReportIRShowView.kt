package com.topdon.module.thermal.ir.report.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.ReportIRBean
import com.topdon.module.thermal.ir.report.bean.ReportTempBean
import com.topdon.module.thermal.ir.databinding.ViewReportIrShowBinding
import com.topdon.module.thermal.ir.databinding.ItemReportIrShowBinding

/**
 * 一项红外数据预览 View.
 *
 * 包含一张图片对应的 全图、点、线、面 预览信息.
 */
class ReportIRShowView: LinearLayout {

    private val binding: ViewReportIrShowBinding

    companion object {
        private const val TYPE_FULL = 0 //全图
        private const val TYPE_POINT = 1//点
        private const val TYPE_LINE = 2 //线
        private const val TYPE_RECT = 3 //面
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        binding = ViewReportIrShowBinding.inflate(LayoutInflater.from(context), this, true)

        initTitleText(binding.clFull.root, TYPE_FULL, 0)

        initTitleText(binding.clPoint1.root, TYPE_POINT, 0)
        initTitleText(binding.clPoint2.root, TYPE_POINT, 1)
        initTitleText(binding.clPoint3.root, TYPE_POINT, 2)
        initTitleText(binding.clPoint4.root, TYPE_POINT, 3)
        initTitleText(binding.clPoint5.root, TYPE_POINT, 4)

        initTitleText(binding.clLine1.root, TYPE_LINE, 0)
        initTitleText(binding.clLine2.root, TYPE_LINE, 1)
        initTitleText(binding.clLine3.root, TYPE_LINE, 2)
        initTitleText(binding.clLine4.root, TYPE_LINE, 3)
        initTitleText(binding.clLine5.root, TYPE_LINE, 4)

        initTitleText(binding.clRect1.root, TYPE_RECT, 0)
        initTitleText(binding.clRect2.root, TYPE_RECT, 1)
        initTitleText(binding.clRect3.root, TYPE_RECT, 2)
        initTitleText(binding.clRect4.root, TYPE_RECT, 3)
        initTitleText(binding.clRect5.root, TYPE_RECT, 4)
    }

    private fun initTitleText(itemRoot: View, type: Int, index: Int) {
        // Use findViewById to access views from included layout for compatibility
        val tvTitle = itemRoot.findViewById<TextView>(R.id.tv_title)
        val tvAverageTitle = itemRoot.findViewById<TextView>(R.id.tv_average_title)  
        val tvExplainTitle = itemRoot.findViewById<TextView>(R.id.tv_explain_title)
        
        tvTitle.isVisible = index == 0
        tvTitle.text = when (type) {
            TYPE_FULL -> context.getString(R.string.thermal_full_rect)
            TYPE_POINT -> context.getString(R.string.thermal_point) + "(P)"
            TYPE_LINE -> context.getString(R.string.thermal_line) + "(L)"
            else -> context.getString(R.string.thermal_rect) + "(R)"
        }
        tvAverageTitle.text = when (type) {
            TYPE_FULL, TYPE_POINT -> "" //全图、点没有平均温
            TYPE_LINE -> "L${index + 1} " + context.getString(R.string.album_report_mean_temperature)
            else -> "R${index + 1} " + context.getString(R.string.album_report_mean_temperature)
        }
        tvExplainTitle.text = when (type) {
            TYPE_FULL -> context.getString(R.string.album_report_comment)
            TYPE_POINT -> "P${index + 1} " + context.getString(R.string.album_report_comment)
            TYPE_LINE -> "L${index + 1} " + context.getString(R.string.album_report_comment)
            else -> "R${index + 1} " + context.getString(R.string.album_report_comment)
        }
    }

    /**
     * 获取需要转为 PDF 的所有 View 列表.
     */
    fun getPrintViewList(): ArrayList<View> {
        val result = ArrayList<View>()
        result.add(binding.clImage)

        getItemChild(binding.clFull.root, result)

        getItemChild(binding.clPoint1.root, result)
        getItemChild(binding.clPoint2.root, result)
        getItemChild(binding.clPoint3.root, result)
        getItemChild(binding.clPoint4.root, result)
        getItemChild(binding.clPoint5.root, result)

        getItemChild(binding.clLine1.root, result)
        getItemChild(binding.clLine2.root, result)
        getItemChild(binding.clLine3.root, result)
        getItemChild(binding.clLine4.root, result)
        getItemChild(binding.clLine5.root, result)

        getItemChild(binding.clRect1.root, result)
        getItemChild(binding.clRect2.root, result)
        getItemChild(binding.clRect3.root, result)
        getItemChild(binding.clRect4.root, result)
        getItemChild(binding.clRect5.root, result)
        return result
    }

    private fun getItemChild(itemRoot: View, resultList: ArrayList<View>) {
        if (itemRoot.isVisible) {
            val clRange = itemRoot.findViewById<View>(R.id.cl_range)
            if (clRange.isVisible) {
                resultList.add(clRange)
            }
            val clAverage = itemRoot.findViewById<View>(R.id.cl_average)
            if (clAverage.isVisible) {
                resultList.add(clAverage)
            }
            val clExplain = itemRoot.findViewById<View>(R.id.cl_explain)
            if (clExplain.isVisible) {
                resultList.add(clExplain)
            }
        }
    }

    fun setImageDrawable(drawable: Drawable?) {
        val isLand = (drawable?.intrinsicWidth ?: 0) > (drawable?.intrinsicHeight ?: 0)
        val width = (ScreenUtil.getScreenWidth(context) * (if (isLand) 234 else 175) / 375f).toInt()
        val height = (width * (drawable?.intrinsicHeight ?: 0).toFloat() / (drawable?.intrinsicWidth ?: 1)).toInt()
        val layoutParams = iv_image.layoutParams
        layoutParams.width = width
        layoutParams.height = height
        iv_image.layoutParams = layoutParams
        iv_image.setImageDrawable(drawable)
    }

    fun refreshData(isFirst: Boolean, isLast: Boolean, reportIRBean: ReportIRBean) {
        tv_head.isVisible = isFirst
        view_not_head.isVisible = !isFirst
        view_image_bg.setBackgroundResource(if (isFirst) R.drawable.layer_report_ir_show_top_bg else R.drawable.layer_report_ir_show_item_bg)
        cl_image.setPadding(0, if (isFirst) SizeUtils.dp2px(20f) else 0, 0, 0)

        refreshItem(binding.clFull.root, reportIRBean.full_graph_data, TYPE_FULL, 0)

        val pointList = reportIRBean.point_data
        for (i in pointList.indices) {
            when (i) {
                0 -> refreshItem(cl_point1, pointList[i], TYPE_POINT, i)
                1 -> refreshItem(cl_point2, pointList[i], TYPE_POINT, i)
                2 -> refreshItem(cl_point3, pointList[i], TYPE_POINT, i)
                3 -> refreshItem(cl_point4, pointList[i], TYPE_POINT, i)
                4 -> refreshItem(cl_point5, pointList[i], TYPE_POINT, i)
            }
        }
        cl_point2.tv_title.isVisible = !cl_point1.isVisible
        cl_point3.tv_title.isVisible = !cl_point1.isVisible && !cl_point2.isVisible
        cl_point4.tv_title.isVisible = !cl_point1.isVisible && !cl_point2.isVisible && !cl_point3.isVisible
        cl_point5.tv_title.isVisible = !cl_point1.isVisible && !cl_point2.isVisible && !cl_point3.isVisible && !cl_point4.isVisible

        val lineList = reportIRBean.line_data
        for (i in lineList.indices) {
            when (i) {
                0 -> refreshItem(cl_line1, lineList[i], TYPE_LINE, i)
                1 -> refreshItem(cl_line2, lineList[i], TYPE_LINE, i)
                2 -> refreshItem(cl_line3, lineList[i], TYPE_LINE, i)
                3 -> refreshItem(cl_line4, lineList[i], TYPE_LINE, i)
                4 -> refreshItem(cl_line5, lineList[i], TYPE_LINE, i)
            }
        }
        cl_line2.tv_title.isVisible = !cl_line1.isVisible
        cl_line3.tv_title.isVisible = !cl_line1.isVisible && !cl_line2.isVisible
        cl_line4.tv_title.isVisible = !cl_line1.isVisible && !cl_line2.isVisible && !cl_line3.isVisible
        cl_line5.tv_title.isVisible = !cl_line1.isVisible && !cl_line2.isVisible && !cl_line3.isVisible && !cl_line4.isVisible

        val rectList = reportIRBean.surface_data
        for (i in rectList.indices) {
            when (i) {
                0 -> refreshItem(cl_rect1, rectList[i], TYPE_RECT, i)
                1 -> refreshItem(cl_rect2, rectList[i], TYPE_RECT, i)
                2 -> refreshItem(cl_rect3, rectList[i], TYPE_RECT, i)
                3 -> refreshItem(cl_rect4, rectList[i], TYPE_RECT, i)
                4 -> refreshItem(cl_rect5, rectList[i], TYPE_RECT, i)
            }
        }
        cl_rect2.tv_title.isVisible = !cl_rect1.isVisible
        cl_rect3.tv_title.isVisible = !cl_rect1.isVisible && !cl_rect2.isVisible
        cl_rect4.tv_title.isVisible = !cl_rect1.isVisible && !cl_rect2.isVisible && !cl_rect3.isVisible
        cl_rect5.tv_title.isVisible = !cl_rect1.isVisible && !cl_rect2.isVisible && !cl_rect3.isVisible && !cl_rect4.isVisible

        // 把最后一条分割线藏起来
        if (rectList.isNotEmpty()) {
            when (rectList.size) {
                1 -> hideLastLine(isLast, cl_rect1, rectList[0], TYPE_RECT)
                2 -> hideLastLine(isLast, cl_rect2, rectList[1], TYPE_RECT)
                3 -> hideLastLine(isLast, cl_rect3, rectList[2], TYPE_RECT)
                4 -> hideLastLine(isLast, cl_rect4, rectList[3], TYPE_RECT)
                5 -> hideLastLine(isLast, cl_rect5, rectList[4], TYPE_RECT)
            }
            return
        }
        if (lineList.isNotEmpty()) {
            when (lineList.size) {
                1 -> hideLastLine(isLast, cl_line1, lineList[0], TYPE_LINE)
                2 -> hideLastLine(isLast, cl_line2, lineList[1], TYPE_LINE)
                3 -> hideLastLine(isLast, cl_line3, lineList[2], TYPE_LINE)
                4 -> hideLastLine(isLast, cl_line4, lineList[3], TYPE_LINE)
                5 -> hideLastLine(isLast, cl_line5, lineList[4], TYPE_LINE)
            }
            return
        }
        if (pointList.isNotEmpty()) {
            when (pointList.size) {
                1 -> hideLastLine(isLast, cl_point1, pointList[0], TYPE_POINT)
                2 -> hideLastLine(isLast, cl_point2, pointList[1], TYPE_POINT)
                3 -> hideLastLine(isLast, cl_point3, pointList[2], TYPE_POINT)
                4 -> hideLastLine(isLast, cl_point4, pointList[3], TYPE_POINT)
                5 -> hideLastLine(isLast, cl_point5, pointList[4], TYPE_POINT)
            }
            return
        }
        hideLastLine(isLast, binding.clFull.root, reportIRBean.full_graph_data, TYPE_FULL)
    }

    private fun hideLastLine(isLast: Boolean, itemRoot: View, tempBean: ReportTempBean?, type: Int) {
        if (tempBean == null) {
            return
        }
        if (tempBean.isExplainOpen()) {
            itemRoot.findViewById<View>(R.id.view_line_explain).isVisible = false
            val clExplain = itemRoot.findViewById<View>(R.id.cl_explain)
            clExplain.setPadding(0, 0, 0, SizeUtils.dp2px(if (isLast) 12f else 20f))
            if (isLast) {
                clExplain.setBackgroundResource(R.drawable.layer_report_ir_show_bottom_bg)
            }
            return
        }
        if ((type == TYPE_LINE || type == TYPE_RECT) && tempBean.isAverageOpen()) {
            itemRoot.findViewById<View>(R.id.view_line_average).isVisible = false
            val clAverage = itemRoot.findViewById<View>(R.id.cl_average)
            clAverage.setPadding(0, 0, 0, SizeUtils.dp2px(if (isLast) 12f else 20f))
            if (isLast) {
                clAverage.setBackgroundResource(R.drawable.layer_report_ir_show_bottom_bg)
            }
            return
        }
        itemRoot.findViewById<View>(R.id.view_line_range).isVisible = false
        val clRange = itemRoot.findViewById<View>(R.id.cl_range)
        clRange.setPadding(0, 0, 0, SizeUtils.dp2px(if (isLast) 12f else 20f))
        if (isLast) {
            clRange.setBackgroundResource(R.drawable.layer_report_ir_show_bottom_bg)
        }
    }

    private fun refreshItem(itemRoot: View, tempBean: ReportTempBean?, type: Int, index: Int) {
        if (tempBean == null) {
            itemRoot.isVisible = false
            return
        }

        itemRoot.isVisible = when (type) {
            TYPE_FULL -> tempBean.isMaxOpen() || tempBean.isMinOpen() || tempBean.isExplainOpen()
            TYPE_POINT -> tempBean.isTempOpen() || tempBean.isExplainOpen()
            else -> tempBean.isMaxOpen() || tempBean.isMinOpen() || tempBean.isAverageOpen() || tempBean.isExplainOpen()
        }
        if (!itemRoot.isVisible) {
            return
        }

        val rangeTitle = if (type == TYPE_POINT) {
            "P${index + 1} " + context.getString(R.string.chart_temperature)
        } else {
            val prefix = when (type) {
                TYPE_LINE -> "L${index + 1} "
                TYPE_RECT -> "R${index + 1} "
                else -> ""
            }
            prefix + if (tempBean.isMinOpen() && tempBean.isMaxOpen()) {
                context.getString(R.string.chart_temperature_low) + "-" + context.getString(R.string.chart_temperature_high)
            } else if (tempBean.isMinOpen()) {
                context.getString(R.string.chart_temperature_low)
            } else {
                context.getString(R.string.chart_temperature_high)
            }
        }
        val rangeValue = if (type == TYPE_POINT) {
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

        itemRoot.findViewById<View>(R.id.tv_range_title).isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        itemRoot.findViewById<View>(R.id.tv_range_value).isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        itemRoot.findViewById<View>(R.id.view_line_range).isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        itemRoot.findViewById<View>(R.id.cl_average).isVisible = (type == TYPE_LINE || type == TYPE_RECT) && tempBean.isAverageOpen()
        itemRoot.findViewById<View>(R.id.cl_explain).isVisible = tempBean.isExplainOpen()
        itemRoot.findViewById<TextView>(R.id.tv_range_title).text = rangeTitle
        itemRoot.findViewById<TextView>(R.id.tv_range_value).text = rangeValue
        itemRoot.findViewById<TextView>(R.id.tv_average_value).text = tempBean.mean_temperature
        itemRoot.findViewById<TextView>(R.id.tv_explain_value).text = tempBean.comment
    }
}