package com.topdon.module.thermal.ir.report.activity

import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.ReportInfoBean
import com.topdon.lib.core.view.MainTitleView
import com.topdon.module.thermal.ir.report.view.ReportInfoView

/**
 * 生成报告第1步的预览界面.
 *
 * 需要传递
 * - 必选：报告信息 [ExtraKeyConfig.REPORT_INFO]
 * - 可选：检测条件 [ExtraKeyConfig.REPORT_CONDITION]
 */
class ReportPreviewFirstActivity: BaseActivity() {


    override fun initContentView() = R.layout.activity_report_preview_first

    override fun initView() {
        val titleView = findViewById<MainTitleView>(R.id.title_view)
        titleView.setLeftDrawable(R.drawable.svg_arrow_left_e8)
        titleView.setLeftClickListener {
            finish()
        }

        val reportInfoBean: ReportInfoBean? = intent.getParcelableExtra(ExtraKeyConfig.REPORT_INFO)
        val reportInfoView = findViewById<ReportInfoView>(R.id.report_info_view)
        reportInfoView.refreshInfo(reportInfoBean)
        reportInfoView.refreshCondition(intent.getParcelableExtra(ExtraKeyConfig.REPORT_CONDITION))

        if (reportInfoBean?.is_report_watermark == 1) {
            watermark_view.watermarkText = reportInfoBean.report_watermark
        }
    }

    override fun initData() {
    }
}