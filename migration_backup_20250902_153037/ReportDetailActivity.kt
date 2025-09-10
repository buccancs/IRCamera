package com.topdon.module.thermal.ir.report.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.FileTools
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.libcom.PDFHelp
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.ReportBean
import com.topdon.module.thermal.ir.report.view.ReportIRShowView
import kotlinx.android.synthetic.main.activity_report_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

    private fun getPrintViewList(): ArrayList<View> {
        val result = ArrayList<View>()
        result.add(report_info_view)
        val childCount = ll_content.childCount
        for (i in 0 until childCount) {
            val childView = ll_content.getChildAt(i)
            if (childView is ReportIRShowView) {
                result.addAll(childView.getPrintViewList())
            }
        }
        return result
    }
}
