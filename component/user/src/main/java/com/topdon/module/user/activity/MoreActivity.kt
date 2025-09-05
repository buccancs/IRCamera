package com.topdon.module.user.activity

import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.ConfirmSelectDialog
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.http.tool.DownloadTool
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.viewmodel.FirmwareViewModel
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.user.R
import com.topdon.module.user.dialog.DownloadProDialog
import com.topdon.module.user.dialog.FirmwareInstallDialog
import com.topdon.lib.core.dialog.FirmwareUpDialog
import com.topdon.module.user.databinding.ActivityMoreBinding
import com.topdon.module.user.databinding.LayoutUpgradeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat

/**
 * TS004 的 “更多” 页面.
 */
@Route(path = RouterConfig.TC_MORE)
class MoreActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMoreBinding
    private val firmwareViewModel: FirmwareViewModel by viewModels()

    override fun initContentView() = R.layout.activity_more

    override fun initView() {
        val titleView = findViewById<View>(R.id.title_view)
        binding = ActivityMoreBinding.bind(titleView.parent as View)
        
        binding.settingDeviceInformation.setOnClickListener(this)
        binding.settingTisr.setOnClickListener(this)
        binding.settingStorageSpace.setOnClickListener(this)
        binding.settingReset.setOnClickListener(this)
        binding.settingVersion.setOnClickListener(this)
        binding.settingDisconnect.setOnClickListener(this)
        binding.settingAutoSave.setOnClickListener(this)

        /*if (Build.VERSION.SDK_INT < 29) {//低于 Android10
            setting_version.isVisible = false
        }*/
        // 2024-5-30 09:16 TS004项目APP沟通群决定，3.30版本先把固件升级隐藏
        binding.settingVersion.isVisible = false
    }

    override fun initData() {
        updateVersion()

        firmwareViewModel.firmwareDataLD.observe(this) {
            binding.tvUpgradePoint.isVisible = it != null
            dismissCameraLoading()
            if (it == null) {//请求成功但没有固件升级包，即已是最新
                ToastUtils.showShort(R.string.setting_firmware_update_latest_version)
            } else {
                showFirmwareUpDialog(it)
            }
        }
        firmwareViewModel.failLD.observe(this) {
            dismissCameraLoading()
            TToast.shortToast(this, if (it) R.string.upgrade_bind_error else R.string.http_code_z5000)
            binding.tvUpgradePoint.isVisible = false
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.settingDeviceInformation -> {//设备信息
                ARouter.getInstance()
                    .build(RouterConfig.DEVICE_INFORMATION)
                    .withBoolean(ExtraKeyConfig.IS_TC007, false)
                    .navigation(this@MoreActivity)
            }
            binding.settingTisr -> {//设置超分
                ARouter.getInstance().build(RouterConfig.TISR).navigation(this@MoreActivity)
            }
            binding.settingAutoSave -> {//自动保存到手机
                ARouter.getInstance().build(RouterConfig.AUTO_SAVE).navigation(this@MoreActivity)
            }
            binding.settingStorageSpace -> {//TS004储存空间
                ARouter.getInstance().build(RouterConfig.STORAGE_SPACE).navigation(this@MoreActivity)
            }
            binding.settingVersion -> {//固件版本
                //由于双通道方案存在问题，V3.30临时使用 apk 内置固件升级包，此处注释强制登录逻辑
//                if (LMS.getInstance().isLogin) {
                    val firmwareData = firmwareViewModel.firmwareDataLD.value
                    if (firmwareData != null) {
                        showFirmwareUpDialog(firmwareData)
                    } else {
                        XLog.i("TC001 固件升级 - 点击查询")
                        showCameraLoading()
                        firmwareViewModel.queryFirmware()
                    }
//                } else {
//                    LMS.getInstance().activityLogin()
//                }
            }
            binding.settingReset -> {//恢复出厂设置
                restoreFactory()
            }
            binding.settingDisconnect -> {//断开连接
                ARouter.getInstance().build(RouterConfig.IR_MORE_HELP)
                    .withInt(Constants.SETTING_CONNECTION_TYPE, Constants.SETTING_DISCONNECTION)
                    .navigation(this@MoreActivity)
            }
        }
    }

    /**
     * 显示固件升级提示弹框.
     */
    private fun showFirmwareUpDialog(firmwareData: FirmwareViewModel.FirmwareData) {
        val dialog = FirmwareUpDialog(this)
        dialog.titleStr = "${getString(R.string.update_new_version)} ${firmwareData.version}"
        dialog.sizeStr = "${getString(R.string.detail_len)}: ${getFileSizeStr(firmwareData.size)}"
        dialog.contentStr = firmwareData.updateStr
        dialog.isShowRestartTips = true
        dialog.onConfirmClickListener = {
            //由于双通道方案存在问题，V3.30临时使用 apk 内置固件升级包，此处注释下载逻辑
            //downloadFirmware(firmwareData)
            installFirmware(FileConfig.getFirmwareFile(firmwareData.downUrl))
        }
        dialog.show()
    }

    private fun getFileSizeStr(size: Long): String = if (size < 1024) {
        "${size}B"
    } else if (size < 1024 * 1024) {
        DecimalFormat("#.0").format(size.toDouble() / 1024) + "KB"
    } else if (size < 1024 * 1024 * 1024) {
        DecimalFormat("#.0").format(size.toDouble() / 1024 / 1024) + "MB"
    } else {
        DecimalFormat("#.0").format(size.toDouble() / 1024 / 1024 / 1024) + "GB"
    }

    /**
     * 下载指定固件升级包
     */
    private fun downloadFirmware(firmwareData: FirmwareViewModel.FirmwareData) {
        lifecycleScope.launch {
            XLog.d("TS004 固件升级 - 开始下载固件升级包")
            val progressDialog = DownloadProDialog(this@MoreActivity)
            progressDialog.show()

            val file = FileConfig.getFirmwareFile("TS004${firmwareData.version}.zip")
            val isSuccess = DownloadTool.download(firmwareData.downUrl, file) { current, total ->
                progressDialog.refreshProgress(current, total)
            }
            progressDialog.dismiss()
            if (isSuccess) {
                XLog.d("TS004 固件升级 - 固件升级包下载成功，即将开始安装")
                installFirmware(file)
            } else {
                XLog.w("TS004 固件升级 - 固件升级包下载失败!")
                showReDownloadDialog(firmwareData)
            }
        }
    }

    private fun installFirmware(file: File) {
        lifecycleScope.launch {
            XLog.d("TS004 固件升级 - 开始安装固件升级包")
            val installDialog = FirmwareInstallDialog(this@MoreActivity)
            installDialog.show()

            val isSuccess = false // TC001 uses USB connection, firmware update not available via network
            installDialog.dismiss()
            if (isSuccess) {
                XLog.d("TS004 固件升级 - 固件升级包发送往 TS004 成功，即将断开连接")
                (application as BaseApplication).disconnectWebSocket()
                ARouter.getInstance().build(RouterConfig.MAIN).navigation(this@MoreActivity)
                finish()
            } else {
                TToast.shortToast(this@MoreActivity, R.string.operation_failed_tips)
            }
        }
    }

    private fun showReInstallDialog(file: File) {
        val dialog = ConfirmSelectDialog(this)
        dialog.setShowIcon(true)
        dialog.setTitleRes(R.string.ts004_install_tips)
        dialog.setCancelText(R.string.ts004_install_cancel)
        dialog.setConfirmText(R.string.ts004_install_continue)
        dialog.onConfirmClickListener = {
            installFirmware(file)
        }
        dialog.show()
    }

    private fun showReDownloadDialog(firmwareData: FirmwareViewModel.FirmwareData) {
        val dialog = ConfirmSelectDialog(this)
        dialog.setShowIcon(true)
        dialog.setTitleRes(R.string.ts004_download_tips)
        dialog.setCancelText(R.string.ts004_download_cancel)
        dialog.setConfirmText(R.string.ts004_download_continue)
        dialog.onConfirmClickListener = {
            downloadFirmware(firmwareData)
        }
        dialog.show()
    }

    private fun updateVersion() {
        lifecycleScope.launch {
            // TC001 uses USB connection, version info not available via network
            binding.itemSettingBottomText.text = getString(R.string.setting_firmware_update_version) + "V" + "N/A"
        }
    }

    private fun restoreFactory() {
        TipDialog.Builder(this)
            .setTitleMessage(getString(R.string.ts004_reset_tip1, "TS004"))
            .setMessage(getString(R.string.ts004_reset_tip2))
            .setPositiveListener(R.string.app_ok) {
                resetAll()
            }
            .setCancelListener(R.string.app_cancel) {
            }
            .setCanceled(true)
            .create().show()
    }

    private fun resetAll() {
        showLoadingDialog(R.string.ts004_reset_tip3)
        lifecycleScope.launch {
            XLog.i("准备调用恢复出厂设置接口")
            val isSuccess = false // TC001 uses USB connection, factory reset not available via network
            XLog.i("恢复出厂设置接口调用 ${if (isSuccess) "成功" else "失败"}")
            if (isSuccess) {
                TToast.shortToast(this@MoreActivity, R.string.ts004_reset_tip4)
                (application as BaseApplication).disconnectWebSocket()
                ARouter.getInstance().build(RouterConfig.MAIN).navigation(this@MoreActivity)
                finish()
            } else {
                TToast.shortToast(this@MoreActivity, R.string.operation_failed_tips)
            }
            delay(500)
            dismissLoadingDialog()
        }
    }
}