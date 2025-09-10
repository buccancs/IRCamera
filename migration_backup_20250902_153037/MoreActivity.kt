package com.topdon.module.user.activity

import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.event.TS004ResetEvent
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.ConfirmSelectDialog
import com.topdon.lib.core.dialog.FirmwareUpDialog
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.http.tool.DownloadTool
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.repository.TS004Repository
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.viewmodel.FirmwareViewModel
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.user.R
import com.topdon.module.user.dialog.DownloadProDialog
import com.topdon.module.user.dialog.FirmwareInstallDialog
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.android.synthetic.main.layout_upgrade.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat

    private fun showFirmwareUpDialog(firmwareData: FirmwareViewModel.FirmwareData) {
        val dialog = FirmwareUpDialog(this)
        dialog.titleStr = "${getString(R.string.update_new_version)} ${firmwareData.version}"
        dialog.sizeStr = "${getString(R.string.detail_len)}: ${getFileSizeStr(firmwareData.size)}"
        dialog.contentStr = firmwareData.updateStr
        dialog.isShowRestartTips = true
        dialog.onConfirmClickListener = {
            // 由于双通道方案存在问题，V3.30临时使用 apk 内置固件升级包，此处注释下载逻辑
            // downloadFirmware(firmwareData)
            installFirmware(FileConfig.getFirmwareFile(firmwareData.downUrl))
        }
        dialog.show()
    }

    private fun getFileSizeStr(size: Long): String =
        if (size < 1024) {
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
            val isSuccess =
                DownloadTool.download(firmwareData.downUrl, file) { current, total ->
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

            val isSuccess = TS004Repository.updateFirmware(file)
            installDialog.dismiss()
            if (isSuccess) {
                XLog.d("TS004 固件升级 - 固件升级包发送往 TS004 成功，即将断开连接")
                (application as BaseApplication).disconnectWebSocket()
                ARouter.getInstance().build(RouterConfig.MAIN).navigation(this@MoreActivity)
                finish()
            } else {
                XLog.w("TS004 固件升级 - 固件升级包发送往 TS004 失败!")
                showReInstallDialog(file)
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
            val versionBean = TS004Repository.getVersion()
            if (versionBean?.isSuccess() == true) {
                item_setting_bottom_text.text = getString(
                    R.string.setting_firmware_update_version,
                ) + "V" + versionBean.data?.firmware
            } else {
                TToast.shortToast(this@MoreActivity, R.string.operation_failed_tips)
            }
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
            val isSuccess = TS004Repository.getResetAll()
            XLog.i("恢复出厂设置接口调用 ${if (isSuccess) "成功" else "失败"}")
            if (isSuccess) {
                TToast.shortToast(this@MoreActivity, R.string.ts004_reset_tip4)
                (application as BaseApplication).disconnectWebSocket()
                EventBus.getDefault().post(TS004ResetEvent())
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
