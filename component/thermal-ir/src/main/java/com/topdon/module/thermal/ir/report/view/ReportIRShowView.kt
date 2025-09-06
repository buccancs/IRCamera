package com.topdon.module.thermal.ir.report.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.report.bean.ReportIRBean
import com.topdon.module.thermal.ir.report.bean.ReportTempBean
import com.topdon.module.thermal.databinding.ViewReportIrShowBinding
import com.topdon.module.thermal.databinding.ItemReportIrShowBinding

/**
 * 一项红外数据预览 View.
 *
 * 包含一张图片对应的 全图、点、线、面 预览信息.
 */
class ReportIRShowView: LinearLayout {
    companion object {
        private const val TYPE_FULL = 0 //全图
        private const val TYPE_POINT = 1//点
        private const val TYPE_LINE = 2 //线
        private const val TYPE_RECT = 3 //面
    }

    private val binding: ViewReportIrShowBinding

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val inflater = View.inflate(context, R.layout.view_report_ir_show, this)
        binding = ViewReportIrShowBinding.bind(inflater)

        initTitleText(binding.clFull, TYPE_FULL, 0)

        initTitleText(binding.clPoint1, TYPE_POINT, 0)
        initTitleText(binding.clPoint2, TYPE_POINT, 1)
        initTitleText(binding.clPoint3, TYPE_POINT, 2)
        initTitleText(binding.clPoint4, TYPE_POINT, 3)
        initTitleText(binding.clPoint5, TYPE_POINT, 4)

        initTitleText(binding.clLine1, TYPE_LINE, 0)
        initTitleText(binding.clLine2, TYPE_LINE, 1)
        initTitleText(binding.clLine3, TYPE_LINE, 2)
        initTitleText(binding.clLine4, TYPE_LINE, 3)
        initTitleText(binding.clLine5, TYPE_LINE, 4)

        initTitleText(binding.clRect1, TYPE_RECT, 0)
        initTitleText(binding.clRect2, TYPE_RECT, 1)
        initTitleText(binding.clRect3, TYPE_RECT, 2)
        initTitleText(binding.clRect4, TYPE_RECT, 3)
        initTitleText(binding.clRect5, TYPE_RECT, 4)
    }

    private fun initTitleText(itemRoot: View, type: Int, index: Int) {
        val itemBinding = ItemReportIrShowBinding.bind(itemRoot)
        itemBinding.tvTitle.isVisible = index == 0
        itemBinding.tvTitle.text = when (type) {
            TYPE_FULL -> context.getString(R.string.thermal_full_rect)
            TYPE_POINT -> context.getString(R.string.thermal_point) + "(P)"
            TYPE_LINE -> context.getString(R.string.thermal_line) + "(L)"
            else -> context.getString(R.string.thermal_rect) + "(R)"
        }
        itemBinding.tvAverageTitle.text = when (type) {
            TYPE_FULL, TYPE_POINT -> "" //全图、点没有平均温
            TYPE_LINE -> "L${index + 1} " + context.getString(R.string.album_report_mean_temperature)
            else -> "R${index + 1} " + context.getString(R.string.album_report_mean_temperature)
        }
        itemBinding.tvExplainTitle.text = when (type) {
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

        getItemChild(binding.clFull, result)

        getItemChild(binding.clPoint1, result)
        getItemChild(binding.clPoint2, result)
        getItemChild(binding.clPoint3, result)
        getItemChild(binding.clPoint4, result)
        getItemChild(binding.clPoint5, result)

        getItemChild(binding.clLine1, result)
        getItemChild(binding.clLine2, result)
        getItemChild(binding.clLine3, result)
        getItemChild(binding.clLine4, result)
        getItemChild(binding.clLine5, result)

        getItemChild(binding.clRect1, result)
        getItemChild(binding.clRect2, result)
        getItemChild(binding.clRect3, result)
        getItemChild(binding.clRect4, result)
        getItemChild(binding.clRect5, result)
        return result
    }

    private fun getItemChild(itemRoot: View, resultList: ArrayList<View>) {
        if (itemRoot.isVisible) {
            val itemBinding = ItemReportIrShowBinding.bind(itemRoot)
            if (itemBinding.clRange.isVisible) {
                resultList.add(itemBinding.clRange)
            }
            if (itemBinding.clAverage.isVisible) {
                resultList.add(itemBinding.clAverage)
            }
            if (itemBinding.clExplain.isVisible) {
                resultList.add(itemBinding.clExplain)
            }
        }
    }

    fun setImageDrawable(drawable: Drawable?) {
        val isLand = (drawable?.intrinsicWidth ?: 0) > (drawable?.intrinsicHeight ?: 0)
        val width = (ScreenUtil.getScreenWidth(context) * (if (isLand) 234 else 175) / 375f).toInt()
        val height = (width * (drawable?.intrinsicHeight ?: 0).toFloat() / (drawable?.intrinsicWidth ?: 1)).toInt()
        val layoutParams = binding.ivImage.layoutParams
        layoutParams.width = width
        layoutParams.height = height
        binding.ivImage.layoutParams = layoutParams
        binding.ivImage.setImageDrawable(drawable)
    }

    fun refreshData(isFirst: Boolean, isLast: Boolean, reportIRBean: ReportIRBean) {
        binding.tvHead.isVisible = isFirst
        binding.viewNotHead.isVisible = !isFirst
        binding.viewImageBg.setBackgroundResource(if (isFirst) R.drawable.layer_report_ir_show_top_bg else R.drawable.layer_report_ir_show_item_bg)
        binding.clImage.setPadding(0, if (isFirst) SizeUtils.dp2px(20f) else 0, 0, 0)

        refreshItem(binding.clFull, reportIRBean.full_graph_data, TYPE_FULL, 0)

        val pointList = reportIRBean.point_data
        for (i in pointList.indices) {
            when (i) {
                0 -> refreshItem(binding.clPoint1, pointList[i], TYPE_POINT, i)
                1 -> refreshItem(binding.clPoint2, pointList[i], TYPE_POINT, i)
                2 -> refreshItem(binding.clPoint3, pointList[i], TYPE_POINT, i)
                3 -> refreshItem(binding.clPoint4, pointList[i], TYPE_POINT, i)
                4 -> refreshItem(binding.clPoint5, pointList[i], TYPE_POINT, i)
            }
        }
        binding.clPoint2.tvTitle.isVisible = !binding.clPoint1.root.isVisible
        binding.clPoint3.tvTitle.isVisible = !binding.clPoint1.root.isVisible && !binding.clPoint2.root.isVisible
        binding.clPoint4.tvTitle.isVisible = !binding.clPoint1.root.isVisible && !binding.clPoint2.root.isVisible && !binding.clPoint3.root.isVisible
        binding.clPoint5.tvTitle.isVisible = !binding.clPoint1.root.isVisible && !binding.clPoint2.root.isVisible && !binding.clPoint3.root.isVisible && !binding.clPoint4.root.isVisible

        val lineList = reportIRBean.line_data
        for (i in lineList.indices) {
            when (i) {
                0 -> refreshItem(binding.clLine1, lineList[i], TYPE_LINE, i)
                1 -> refreshItem(binding.clLine2, lineList[i], TYPE_LINE, i)
                2 -> refreshItem(binding.clLine3, lineList[i], TYPE_LINE, i)
                3 -> refreshItem(binding.clLine4, lineList[i], TYPE_LINE, i)
                4 -> refreshItem(binding.clLine5, lineList[i], TYPE_LINE, i)
            }
        }
        binding.clLine2.tvTitle.isVisible = !binding.clLine1.root.isVisible
        binding.clLine3.tvTitle.isVisible = !binding.clLine1.root.isVisible && !binding.clLine2.root.isVisible
        binding.clLine4.tvTitle.isVisible = !binding.clLine1.root.isVisible && !binding.clLine2.root.isVisible && !binding.clLine3.root.isVisible
        binding.clLine5.tvTitle.isVisible = !binding.clLine1.root.isVisible && !binding.clLine2.root.isVisible && !binding.clLine3.root.isVisible && !binding.clLine4.root.isVisible

        val rectList = reportIRBean.surface_data
        for (i in rectList.indices) {
            when (i) {
                0 -> refreshItem(binding.clRect1, rectList[i], TYPE_RECT, i)
                1 -> refreshItem(binding.clRect2, rectList[i], TYPE_RECT, i)
                2 -> refreshItem(binding.clRect3, rectList[i], TYPE_RECT, i)
                3 -> refreshItem(binding.clRect4, rectList[i], TYPE_RECT, i)
                4 -> refreshItem(binding.clRect5, rectList[i], TYPE_RECT, i)
            }
        }
        binding.clRect2.tvTitle.isVisible = !binding.clRect1.root.isVisible
        binding.clRect3.tvTitle.isVisible = !binding.clRect1.root.isVisible && !binding.clRect2.root.isVisible
        binding.clRect4.tvTitle.isVisible = !binding.clRect1.root.isVisible && !binding.clRect2.root.isVisible && !binding.clRect3.root.isVisible
        binding.clRect5.tvTitle.isVisible = !binding.clRect1.root.isVisible && !binding.clRect2.root.isVisible && !binding.clRect3.root.isVisible && !binding.clRect4.root.isVisible

        // 把最后一条分割线藏起来
        if (rectList.isNotEmpty()) {
            when (rectList.size) {
                1 -> hideLastLine(isLast, binding.clRect1, rectList[0], TYPE_RECT)
                2 -> hideLastLine(isLast, binding.clRect2, rectList[1], TYPE_RECT)
                3 -> hideLastLine(isLast, binding.clRect3, rectList[2], TYPE_RECT)
                4 -> hideLastLine(isLast, binding.clRect4, rectList[3], TYPE_RECT)
                5 -> hideLastLine(isLast, binding.clRect5, rectList[4], TYPE_RECT)
            }
            return
        }
        if (lineList.isNotEmpty()) {
            when (lineList.size) {
                1 -> hideLastLine(isLast, binding.clLine1, lineList[0], TYPE_LINE)
                2 -> hideLastLine(isLast, binding.clLine2, lineList[1], TYPE_LINE)
                3 -> hideLastLine(isLast, binding.clLine3, lineList[2], TYPE_LINE)
                4 -> hideLastLine(isLast, binding.clLine4, lineList[3], TYPE_LINE)
                5 -> hideLastLine(isLast, binding.clLine5, lineList[4], TYPE_LINE)
            }
            return
        }
        if (pointList.isNotEmpty()) {
            when (pointList.size) {
                1 -> hideLastLine(isLast, binding.clPoint1, pointList[0], TYPE_POINT)
                2 -> hideLastLine(isLast, binding.clPoint2, pointList[1], TYPE_POINT)
                3 -> hideLastLine(isLast, binding.clPoint3, pointList[2], TYPE_POINT)
                4 -> hideLastLine(isLast, binding.clPoint4, pointList[3], TYPE_POINT)
                5 -> hideLastLine(isLast, binding.clPoint5, pointList[4], TYPE_POINT)
            }
            return
        }
        hideLastLine(isLast, binding.clFull, reportIRBean.full_graph_data, TYPE_FULL)
    }

    private fun hideLastLine(isLast: Boolean, itemBinding: ItemReportIrShowBinding, tempBean: ReportTempBean?, type: Int) {
        if (tempBean == null) {
            return
        }
        if (tempBean.isExplainOpen()) {
            itemBinding.viewLineExplain.isVisible = false
            itemBinding.clExplain.setPadding(0, 0, 0, SizeUtils.dp2px(if (isLast) 12f else 20f))
            if (isLast) {
                itemBinding.clExplain.setBackgroundResource(R.drawable.layer_report_ir_show_bottom_bg)
            }
            return
        }
        if ((type == TYPE_LINE || type == TYPE_RECT) && tempBean.isAverageOpen()) {
            itemBinding.viewLineAverage.isVisible = false
            itemBinding.clAverage.setPadding(0, 0, 0, SizeUtils.dp2px(if (isLast) 12f else 20f))
            if (isLast) {
                itemBinding.clAverage.setBackgroundResource(R.drawable.layer_report_ir_show_bottom_bg)
            }
            return
        }
        itemBinding.viewLineRange.isVisible = false
        itemBinding.clRange.setPadding(0, 0, 0, SizeUtils.dp2px(if (isLast) 12f else 20f))
        if (isLast) {
            itemBinding.clRange.setBackgroundResource(R.drawable.layer_report_ir_show_bottom_bg)
        }
    }

    private fun refreshItem(itemBinding: ItemReportIrShowBinding, tempBean: ReportTempBean?, type: Int, index: Int) {
        if (tempBean == null) {
            itemBinding.root.isVisible = false
            return
        }

        itemBinding.root.isVisible = when (type) {
            TYPE_FULL -> tempBean.isMaxOpen() || tempBean.isMinOpen() || tempBean.isExplainOpen()
            TYPE_POINT -> tempBean.isTempOpen() || tempBean.isExplainOpen()
            else -> tempBean.isMaxOpen() || tempBean.isMinOpen() || tempBean.isAverageOpen() || tempBean.isExplainOpen()
        }
        if (!itemBinding.root.isVisible) {
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

        itemBinding.tvRangeTitle.isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        itemBinding.tvRangeValue.isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        itemBinding.viewLineRange.isVisible = if (type == TYPE_POINT) tempBean.isTempOpen() else tempBean.isMinOpen() || tempBean.isMaxOpen()
        itemBinding.clAverage.isVisible = (type == TYPE_LINE || type == TYPE_RECT) && tempBean.isAverageOpen()
        itemBinding.clExplain.isVisible = tempBean.isExplainOpen()
        itemBinding.tvRangeTitle.text = rangeTitle
        itemBinding.tvRangeValue.text = rangeValue
        itemBinding.tvAverageValue.text = tempBean.mean_temperature
        itemBinding.tvExplainValue.text = tempBean.comment
    }
}