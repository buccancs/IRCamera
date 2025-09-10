package com.topdon.module.thermal.ir.report.activity

import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.bean.event.ReportCreateEvent
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.navigation.NavigationManager
import com.topdon.lib.core.tools.ConstantLanguages
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.*
import com.topdon.module.thermal.ir.report.view.ReportIRInputView
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
                    1 -> { // Activity logic
                        when (i) {
                            0 -> reportTempViewPoint1
                            1 -> reportTempViewPoint2
                            2 -> reportTempViewPoint3
                            3 -> reportTempViewPoint4
                            else -> reportTempViewPoint5
                        }
                    }
                    2 -> { // Activity logic
                        when (i) {
                            0 -> reportTempViewLine1
                            1 -> reportTempViewLine2
                            2 -> reportTempViewLine3
                            3 -> reportTempViewLine4
                            else -> reportTempViewLine5
                        }
                    }
                    else -> { // Activity logic
                        when (i) {
                            0 -> reportTempViewRect1
                            1 -> reportTempViewRect2
                            else -> reportTempViewRect3
                        }
                    }
                }
            val reportTempBean =
                if (type == 1) { // Activity logic
                    ReportTempBean(
                        if ((reportTempView as? ReportIRInputView)?.getMaxInput()?.isNotEmpty() == true) (reportTempView as ReportIRInputView).getMaxInput() + UnitTools.showUnit() else "",
                        if ((reportTempView as? ReportIRInputView)?.isSwitchMaxCheck() == true && (reportTempView as ReportIRInputView).getMaxInput().isNotEmpty()) 1 else 0,
                        (reportTempView as? ReportIRInputView)?.getExplainInput() ?: "",
                        if ((reportTempView as? ReportIRInputView)?.isSwitchExplainCheck() == true && (reportTempView as ReportIRInputView).getExplainInput().isNotEmpty()) 1 else 0,
                    )
                } else {
                    ReportTempBean(
                        if ((reportTempView as? ReportIRInputView)?.getMaxInput()?.isNotEmpty() == true) (reportTempView as ReportIRInputView).getMaxInput() + UnitTools.showUnit() else "",
                        if ((reportTempView as? ReportIRInputView)?.isSwitchMaxCheck() == true && (reportTempView as ReportIRInputView).getMaxInput().isNotEmpty()) 1 else 0,
                        if ((reportTempView as? ReportIRInputView)?.getMinInput()?.isNotEmpty() == true) (reportTempView as ReportIRInputView).getMinInput() + UnitTools.showUnit() else "",
                        if ((reportTempView as? ReportIRInputView)?.isSwitchMinCheck() == true && (reportTempView as ReportIRInputView).getMinInput().isNotEmpty()) 1 else 0,
                        (reportTempView as? ReportIRInputView)?.getExplainInput() ?: "",
                        if ((reportTempView as? ReportIRInputView)?.isSwitchExplainCheck() == true && (reportTempView as ReportIRInputView).getExplainInput().isNotEmpty()) 1 else 0,
                        if ((reportTempView as? ReportIRInputView)?.getAverageInput()?.isNotEmpty() == true) (reportTempView as ReportIRInputView).getAverageInput() + UnitTools.showUnit() else "",
                        if ((reportTempView as? ReportIRInputView)?.isSwitchAverageCheck() == true && (reportTempView as ReportIRInputView).getAverageInput().isNotEmpty()) 1 else 0,
                    )
                }
            resultList.add(reportTempBean)
        }
        return resultList
    }
}
