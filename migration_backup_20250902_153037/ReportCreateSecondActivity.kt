package com.topdon.module.thermal.ir.report.activity

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.bean.event.ReportCreateEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.*
import kotlinx.android.synthetic.main.activity_report_create_second.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

    private fun buildReportTempBeanList(type: Int): ArrayList<ReportTempBean> {
        val size =
            when (type) {
                1 -> imageTempBean?.pointList?.size ?: 0
                2 -> imageTempBean?.lineList?.size ?: 0
                else -> imageTempBean?.rectList?.size ?: 0
            }
        val resultList = ArrayList<ReportTempBean>(size)
        for (i in 0 until size) {
            val reportTempView =
                when (type) {
                    1 -> { // 点
                        when (i) {
                            0 -> report_temp_view_point1
                            1 -> report_temp_view_point2
                            2 -> report_temp_view_point3
                            3 -> report_temp_view_point4
                            else -> report_temp_view_point5
                        }
                    }
                    2 -> { // 线
                        when (i) {
                            0 -> report_temp_view_line1
                            1 -> report_temp_view_line2
                            2 -> report_temp_view_line3
                            3 -> report_temp_view_line4
                            else -> report_temp_view_line5
                        }
                    }
                    else -> { // 面
                        when (i) {
                            0 -> report_temp_view_rect1
                            1 -> report_temp_view_rect2
                            else -> report_temp_view_rect3
                        }
                    }
                }
            val reportTempBean =
                if (type == 1) { // 点的数据封装不太一样
                    ReportTempBean(
                        if (reportTempView.getMaxInput().isNotEmpty()) reportTempView.getMaxInput() + UnitTools.showUnit() else "",
                        if (reportTempView.isSwitchMaxCheck() && reportTempView.getMaxInput().isNotEmpty()) 1 else 0,
                        reportTempView.getExplainInput(),
                        if (reportTempView.isSwitchExplainCheck() && reportTempView.getExplainInput().isNotEmpty()) 1 else 0,
                    )
                } else {
                    ReportTempBean(
                        if (reportTempView.getMaxInput().isNotEmpty()) reportTempView.getMaxInput() + UnitTools.showUnit() else "",
                        if (reportTempView.isSwitchMaxCheck() && reportTempView.getMaxInput().isNotEmpty()) 1 else 0,
                        if (reportTempView.getMinInput().isNotEmpty()) reportTempView.getMinInput() + UnitTools.showUnit() else "",
                        if (reportTempView.isSwitchMinCheck() && reportTempView.getMinInput().isNotEmpty()) 1 else 0,
                        reportTempView.getExplainInput(),
                        if (reportTempView.isSwitchExplainCheck() && reportTempView.getExplainInput().isNotEmpty()) 1 else 0,
                        if (reportTempView.getAverageInput().isNotEmpty()) reportTempView.getAverageInput() + UnitTools.showUnit() else "",
                        if (reportTempView.isSwitchAverageCheck() && reportTempView.getAverageInput().isNotEmpty()) 1 else 0,
                    )
                }
            resultList.add(reportTempBean)
        }
        return resultList
    }
}
