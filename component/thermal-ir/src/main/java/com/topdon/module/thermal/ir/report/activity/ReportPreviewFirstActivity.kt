package com.topdon.module.thermal.ir.report.activity

import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.ReportInfoBean

/**
 * 生成报告第1步的预览界面.
 *
 * 需要传递
 * - 必选：报告信息 [ExtraKeyConfig.REPORT_INFO]
 * - 可选：检测条件 [ExtraKeyConfig.REPORT_CONDITION]
 */
// Legacy ARouter route annotation - now using NavigationManager
class ReportPreviewFirstActivity: BaseActivity() {


    override fun initContentView() = R.layout.activity_report_preview_first

    override fun initView() {
        title_view.setLeftDrawable(R.drawable.svg_arrow_left_e8)
        title_view.setLeftClickListener {
            finish()
        }

        val reportInfoBean: ReportInfoBean? = intent.getParcelableExtra(ExtraKeyConfig.REPORT_INFO)
        report_info_view.refreshInfo(reportInfoBean)
        report_info_view.refreshCondition(intent.getParcelableExtra(ExtraKeyConfig.REPORT_CONDITION))

        if (reportInfoBean?.is_report_watermark == 1) {
            watermark_view.watermarkText = reportInfoBean.report_watermark
        }
    }

    override fun initData() {
    }
}