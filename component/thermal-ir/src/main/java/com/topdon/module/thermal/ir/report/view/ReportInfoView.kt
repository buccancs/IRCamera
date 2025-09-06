package com.topdon.module.thermal.ir.report.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.report.bean.ReportConditionBean
import com.topdon.module.thermal.ir.report.bean.ReportInfoBean
import com.topdon.module.thermal.databinding.ViewReportInfoBinding

/**
 * 报告信息 - 预览 View.
 */
class ReportInfoView: LinearLayout {

    private val binding: ViewReportInfoBinding

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        binding = ViewReportInfoBinding.inflate(LayoutInflater.from(context), this, true)
    }

    /**
     * 根据指定的报告信息刷新对应 View.
     */
    fun refreshInfo(reportInfoBean: ReportInfoBean?) {
        binding.tvReportName.text = reportInfoBean?.report_name

        binding.tvReportAuthor.isVisible = reportInfoBean?.is_report_author == 1
        binding.tvReportAuthor.text = reportInfoBean?.report_author

        binding.groupReportPlace.isVisible = reportInfoBean?.is_report_place == 1
        binding.tvReportPlace.text = reportInfoBean?.report_place

        binding.tvReportDate.isVisible = reportInfoBean?.is_report_date == 1
        binding.tvReportDate.text = reportInfoBean?.report_date
    }

    /**
     * 根据指定的检测条件信息刷新对应 View.
     */
    fun refreshCondition(conditionBean: ReportConditionBean?) {
        binding.clReportCondition.isVisible = conditionBean?.is_ambient_humidity == 1
                || conditionBean?.is_ambient_temperature == 1
                || conditionBean?.is_test_distance == 1
                || conditionBean?.is_emissivity == 1

        binding.groupAmbientTemperature.isVisible = conditionBean?.is_ambient_temperature == 1
        binding.tvAmbientTemperature.text = conditionBean?.ambient_temperature
        binding.viewLine1.isVisible = conditionBean?.is_ambient_temperature == 1 &&
                (conditionBean.is_ambient_humidity == 1 || conditionBean.is_test_distance == 1 || conditionBean.is_emissivity == 1)

        binding.groupAmbientHumidity.isVisible = conditionBean?.is_ambient_humidity == 1
        binding.tvAmbientHumidity.text = conditionBean?.ambient_humidity
        binding.viewLine2.isVisible = conditionBean?.is_ambient_humidity == 1 && (conditionBean.is_test_distance == 1 || conditionBean.is_emissivity == 1)

        binding.groupTestDistance.isVisible = conditionBean?.is_test_distance == 1
        binding.tvTestDistance.text = conditionBean?.test_distance
        binding.viewLine3.isVisible = conditionBean?.is_test_distance == 1 && conditionBean.is_emissivity == 1

        binding.groupEmissivity.isVisible = conditionBean?.is_emissivity == 1
        binding.tvEmissivity.text = conditionBean?.emissivity
    }

    /**
     * 获取需要转为 PDF 的所有 View 列表.
     */
    fun getPrintViewList(): ArrayList<View> {
        val result = ArrayList<View>()
        result.add(binding.clTop)
        result.add(binding.clReportCondition)
        return result
    }
}