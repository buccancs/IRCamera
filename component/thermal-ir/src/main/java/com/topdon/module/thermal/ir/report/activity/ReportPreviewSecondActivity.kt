package com.topdon.module.thermal.ir.report.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.bean.event.ReportCreateEvent
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseViewModelActivity
import com.topdon.lib.core.tools.FileTools
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.libcom.PDFHelp
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.utils.StringUtils
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.thermal.ir.report.view.ReportIRShowView
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.csl.irCamera.libui.R as LibUiR
import com.topdon.module.thermal.ir.report.bean.ReportBean
import com.topdon.module.thermal.ir.report.viewmodel.UpReportViewModel
import com.topdon.module.thermal.ir.databinding.ActivityReportPreviewSecondBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File

 * 2.
 * TC007: [ExtraKeyConfig.IS_TC007]
 * [ExtraKeyConfig.REPORT_BEAN]
@Route(path = RouterConfig.REPORT_PREVIEW_SECOND)
class ReportPreviewSecondActivity: BaseViewModelActivity<UpReportViewModel>(), View.OnClickListener {

    private lateinit var binding: ActivityReportPreviewSecondBinding

     * TC007 .
     * true-TC007 false
    private var isTC007 = false

     * .
    private var reportBean: ReportBean? = null

     *  PDF
    private var pdfFilePath: String? = null


    override fun initContentView(): Int {
        binding = ActivityReportPreviewSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0 // Not used when setContentView is called directly
    }

    override fun providerVMClass() = UpReportViewModel::class.java

    override fun initView() {
        reportBean = intent.getParcelableExtra(ExtraKeyConfig.REPORT_BEAN)
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        binding.titleView.setTitleText(LibAppR.string.album_edit_preview)
        binding.titleView.setLeftDrawable(LibUiR.drawable.svg_arrow_left_e8)
        binding.titleView.setRightDrawable(R.drawable.ic_report_exit_svg)
        binding.titleView.setLeftClickListener {
            finish()
        }
        binding.titleView.setRightClickListener {
            TipDialog.Builder(this)
                .setMessage(LibAppR.string.album_report_exit_tips)
                .setPositiveListener(LibAppR.string.app_ok){
                    EventBus.getDefault().post(ReportCreateEvent())
                    finish()
                }
                .setCancelListener(LibAppR.string.app_cancel){
                }
                .setCanceled(false)
                .create().show()
        }

        binding.reportInfoView.refreshInfo(reportBean?.report_info)
        binding.reportInfoView.refreshCondition(reportBean?.detection_condition)

        if (reportBean?.report_info?.is_report_watermark == 1) {
            binding.watermarkView.watermarkText = reportBean?.report_info?.report_watermark
        }

        val irList = reportBean?.infrared_data
        if (irList != null) {
            for (i in irList.indices) {
                val reportShowView = ReportIRShowView(this)
                reportShowView.refreshData(i == 0, i == irList.size - 1, irList[i])
                lifecycleScope.launch {
                    val drawable = GlideLoader.getDrawable(this@ReportPreviewSecondActivity, irList[i].picture_url)
                    reportShowView.setImageDrawable(drawable)
                }
                binding.llContent.addView(reportShowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }

        binding.tvToPdf.setOnClickListener(this)
        binding.tvComplete.setOnClickListener(this)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                //  TS004TC007
                if (WebSocketProxy.getInstance().isConnected()) {
                    NetWorkUtils.connectivityManager.bindProcessToNetwork(null)
                }
            }
        })
    }

    override fun initData() {
        viewModel.commonBeanLD.observe(this) {
            dismissCameraLoading()
            if (it.code == LMS.SUCCESS) {
                EventBus.getDefault().post(ReportCreateEvent())
                ARouter.getInstance().build(RouterConfig.REPORT_LIST)
                    .withBoolean(ExtraKeyConfig.IS_TC007, isTC007)
                    .navigation(this)
                finish()
            } else {
                ToastUtils.showShort(StringUtils.getResString(this, it.code.toString()))
            }
        }
        viewModel.exceptionLD.observe(this) {
            dismissCameraLoading()
            requestError(it)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvToPdf -> {//PDF
                saveWithPDF()
            }
            binding.tvComplete -> {//

                if (LMS.getInstance().isLogin) {
                    if (!NetworkUtils.isConnected()) {
                        TToast.shortToast(this, LibAppR.string.setting_http_error)
                        return
                    }
                    showCameraLoading()
                    viewModel.upload(isTC007, reportBean)
                } else {
                    LMS.getInstance().activityLogin()
                }
            }
        }
    }

    private fun saveWithPDF() {
        if (TextUtils.isEmpty(pdfFilePath)) {
            showCameraLoading()
            lifecycleScope.launch(Dispatchers.IO) {
                val name = reportBean?.report_info?.report_number
                if (name != null) {
                    if (File(FileConfig.getPdfDir() + "/$name.pdf").exists() &&
                        !TextUtils.isEmpty(pdfFilePath)) {
                        lifecycleScope.launch {
                            dismissCameraLoading()
                            actionShare()
                        }
                        return@launch
                    }
                }
                pdfFilePath = PDFHelp.savePdfFileByListView(name?:System.currentTimeMillis().toString(),
                    binding.scrollView, getPrintViewList(),binding.watermarkView)
                lifecycleScope.launch {
                    binding.tvToPdf.text = getString(LibAppR.string.battery_share)
                    dismissCameraLoading()
                    actionShare()
                }
            }
        } else {
            actionShare()
        }
    }

    private fun actionShare() {
        val uri = FileTools.getUri(File(pdfFilePath!!))
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "application/pdf"
        startActivity(Intent.createChooser(shareIntent, getString(LibAppR.string.battery_share)))
    }

     *  PDF  View .
     * View .
    private fun getPrintViewList(): ArrayList<View> {
        val result = ArrayList<View>()
        result.add(binding.reportInfoView)
        val childCount = binding.llContent.childCount
        for (i in 0 until childCount) {
            val childView = binding.llContent.getChildAt(i)
            if (childView is ReportIRShowView) {
                result.addAll(childView.getPrintViewList())
            }
        }
        return result
    }
}