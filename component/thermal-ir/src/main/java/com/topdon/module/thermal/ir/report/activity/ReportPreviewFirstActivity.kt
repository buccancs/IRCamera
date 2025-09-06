package com.topdon.module.thermal.ir.report.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.report.bean.ReportInfoBean
import com.topdon.module.thermal.databinding.ActivityReportPreviewFirstBinding

/**
 * 生成报告第1步的预览界面.
 *
 * 需要传递
 * - 必选：报告信息 [ExtraKeyConfig.REPORT_INFO]
 * - 可选：检测条件 [ExtraKeyConfig.REPORT_CONDITION]
 */
@Route(path = RouterConfig.REPORT_PREVIEW_FIRST)
class ReportPreviewFirstActivity: BaseActivity() {

    private lateinit var binding: ActivityReportPreviewFirstBinding

    override fun initContentView(): Int {
        binding = ActivityReportPreviewFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun initView() {
        binding.titleView.setLeftDrawable(R.drawable.svg_arrow_left_e8)
        binding.titleView.setLeftClickListener {
            finish()
        }

        val reportInfoBean: ReportInfoBean? = intent.getParcelableExtra(ExtraKeyConfig.REPORT_INFO)
        binding.reportInfoView.refreshInfo(reportInfoBean)
        binding.reportInfoView.refreshCondition(intent.getParcelableExtra(ExtraKeyConfig.REPORT_CONDITION))

        if (reportInfoBean?.is_report_watermark == 1) {
            binding.watermarkView.watermarkText = reportInfoBean.report_watermark
        }
    }

    override fun initData() {
    }
}