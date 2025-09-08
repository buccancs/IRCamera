package com.topdon.module.user.fragment

import android.os.Build
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.common.WifiSaveSettingUtil
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.ConfirmSelectDialog
import com.topdon.lib.core.dialog.FirmwareUpDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.http.tool.DownloadTool
import com.topdon.lib.core.repository.ProductBean
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.viewmodel.FirmwareViewModel
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.user.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.user.databinding.FragmentMoreBinding
import com.topdon.module.user.databinding.LayoutUpgradeBinding
import com.topdon.module.user.dialog.DownloadProDialog
import com.topdon.module.user.dialog.FirmwareInstallDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat

 *  “”
 * [ExtraKeyConfig.IS_TC007] -  TC007
@Route(path = RouterConfig.TC_MORE)
class MoreFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    
    private var _upgradeBinding: LayoutUpgradeBinding? = null
    private val upgradeBinding get() = _upgradeBinding!!

     * TC007 .
     * true-TC007 false
    private var isTC007 = false
     * TC007  ViewModel.
    private val firmwareViewModel: FirmwareViewModel by viewModels()

    override fun initContentView() = R.layout.fragment_more

    override fun initView() {
        _binding = FragmentMoreBinding.bind(requireView())
        _upgradeBinding = binding.settingVersion
        
        isTC007 = arguments?.getBoolean(ExtraKeyConfig.IS_TC007, false) ?: false

        binding.settingItemModel.setOnClickListener(this)//
        binding.settingItemCorrection.setOnClickListener(this)//
        binding.settingItemDual.setOnClickListener(this)//
        binding.settingItemUnit.setOnClickListener(this)//
        upgradeBinding.root.setOnClickListener(this) //TC007
        binding.settingDeviceInformation.setOnClickListener(this)//TC007
        binding.settingReset.setOnClickListener(this)//TC007

        // 2024/5/23 TC007
        binding.settingReset.isVisible = false

        upgradeBinding.root.isVisible = isTC007 && Build.VERSION.SDK_INT >= 29
        binding.settingDeviceInformation.isVisible = isTC007
        binding.settingItemDual.isVisible = !isTC007 && DeviceTools.isTC001PlusConnect()

        if (isTC007) {
            refresh07Connect(WebSocketProxy.getInstance().isTC007Connect())
        }

        binding.settingItemAutoShow.isChecked = if (isTC007) SharedManager.isConnect07AutoOpen else SharedManager.isConnectAutoOpen
        binding.settingItemAutoShow.setOnCheckedChangeListener { _, isChecked ->
            if (isTC007) {
                SharedManager.isConnect07AutoOpen = isChecked
            } else {
                SharedManager.isConnectAutoOpen = isChecked
            }
        }

        binding.settingItemConfigSelect.isChecked = if (isTC007) WifiSaveSettingUtil.isSaveSetting else SaveSettingUtil.isSaveSetting
        binding.settingItemConfigSelect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                TipDialog.Builder(requireContext())
                    .setMessage(LibAppR.string.save_setting_tips)
                    .setPositiveListener(LibAppR.string.app_ok) {
                        if (isTC007){
                            WifiSaveSettingUtil.isSaveSetting = true
                        }else{
                            SaveSettingUtil.isSaveSetting = true
                        }
                    }
                    .setCancelListener(LibAppR.string.app_cancel) {
                        binding.settingItemConfigSelect.isChecked = false
                    }
                    .setCanceled(false)
                    .create().show()
            } else {
                if (isTC007){
                    WifiSaveSettingUtil.reset()
                    WifiSaveSettingUtil.isSaveSetting = false
                }else{
                    SaveSettingUtil.reset()
                    SaveSettingUtil.isSaveSetting = false
                }
            }
        }

        firmwareViewModel.firmwareDataLD.observe(this) {
            upgradeBinding.tvUpgradePoint.isVisible = it != null
            dismissLoadingDialog()
            if (it == null) {//，
                ToastUtils.showShort(LibAppR.string.setting_firmware_update_latest_version)
            } else {
                showFirmwareUpDialog(it)
            }
        }
        firmwareViewModel.failLD.observe(this) {
            dismissLoadingDialog()
            TToast.shortToast(requireContext(), if (it) LibAppR.string.upgrade_bind_error else LibAppR.string.http_code_else)
            upgradeBinding.tvUpgradePoint.isVisible = false
        }
    }

    override fun initData() {
    }

    override fun connected() {
        binding.settingItemDual.isVisible = !isTC007 && DeviceTools.isTC001PlusConnect()
    }

    override fun disConnected() {
        binding.settingItemDual.isVisible = false
    }

    override fun onSocketConnected(isTS004: Boolean) {
        if (!isTS004 && isTC007) {
            refresh07Connect(true)
        }
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        if (!isTS004 && isTC007) {
            refresh07Connect(false)
        }
    }

    override fun onClick(v: View?) {
       when(v){
           binding.settingItemModel -> {//
               ARouter.getInstance().build(RouterConfig.IR_SETTING).withBoolean(ExtraKeyConfig.IS_TC007, isTC007).navigation(requireContext())
           }
           binding.settingItemDual->{
               ARouter.getInstance().build(RouterConfig.MANUAL_START).navigation(requireContext())
           }
           binding.settingItemUnit -> {//
               ARouter.getInstance().build(RouterConfig.UNIT).navigation(requireContext())
           }
           binding.settingItemCorrection->{//
               ARouter.getInstance().build(RouterConfig.IR_CORRECTION).withBoolean(ExtraKeyConfig.IS_TC007, isTC007).navigation(requireContext())
           }
           upgradeBinding.root -> {//TC007
               //V3.30 apk
//               if (LMS.getInstance().isLogin) {
                   val firmwareData = firmwareViewModel.firmwareDataLD.value
                   if (firmwareData != null) {
                       showFirmwareUpDialog(firmwareData)
                   } else {
                       XLog.i("TC001 - ")
                       showLoadingDialog()
                       firmwareViewModel.queryFirmware()
                   }
//               } else {
//                   LMS.getInstance().activityLogin()
//               }
           }
           binding.settingDeviceInformation -> {//TC007
               if (WebSocketProxy.getInstance().isTC007Connect()) {
                   ARouter.getInstance()
                       .build(RouterConfig.DEVICE_INFORMATION)
                       .withBoolean(ExtraKeyConfig.IS_TC007, true)
                       .navigation(requireContext())
               }
           }
           binding.settingReset -> {//TC007
               if (WebSocketProxy.getInstance().isTC007Connect()) {
                   restoreFactory()
               }
           }
       }
    }


     *  TC007 .
    private fun refresh07Connect(isConnect: Boolean) {
        binding.settingDeviceInformation.isRightArrowVisible = isConnect
        binding.settingDeviceInformation.setRightTextId(if (isConnect) 0 else LibAppR.string.app_no_connect)
        binding.settingReset.isRightArrowVisible = isConnect
        binding.settingReset.setRightTextId(if (isConnect) 0 else LibAppR.string.app_no_connect)
        upgradeBinding.tvRightText.isVisible = isConnect

        if (isConnect) {
            lifecycleScope.launch {
                // TC001 uses USB connection, version info not available via network
                upgradeBinding.itemSettingBottomText.text = getString(LibAppR.string.setting_firmware_update_version) + "V" + "N/A"
            }
        } else {
            upgradeBinding.itemSettingBottomText.setText(LibAppR.string.setting_firmware_update_version)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _upgradeBinding = null
    }


     * .
    private fun showFirmwareUpDialog(firmwareData: FirmwareViewModel.FirmwareData) {
        val dialog = FirmwareUpDialog(requireContext())
        dialog.titleStr = "${getString(LibAppR.string.update_new_version)} ${firmwareData.version}"
        dialog.sizeStr = "${getString(LibAppR.string.detail_len)}: ${getFileSizeStr(firmwareData.size)}"
        dialog.contentStr = firmwareData.updateStr
        dialog.isShowRestartTips = true
        dialog.onConfirmClickListener = {
            //V3.30 apk
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

    private fun downloadFirmware(firmwareData: FirmwareViewModel.FirmwareData) {
        lifecycleScope.launch {
            val progressDialog = DownloadProDialog(requireContext())
            progressDialog.show()

            val file = File(requireContext().getExternalFilesDir("firmware"), "TC007${firmwareData.version}.zip")
            val isSuccess = DownloadTool.download(firmwareData.downUrl, file) { current, total ->
                progressDialog.refreshProgress(current, total)
            }
            progressDialog.dismiss()
            if (isSuccess) {
                installFirmware(file)
            } else {
                showReDownloadDialog(firmwareData)
            }
        }
    }

    private fun installFirmware(file: File) {
        lifecycleScope.launch {
            XLog.d("TC007 - ")
            val installDialog = FirmwareInstallDialog(requireContext())
            installDialog.show()

            val isSuccess = false // TC001 uses USB connection, firmware update not available via network
            installDialog.dismiss()
            if (isSuccess) {
                XLog.d("TC007 - TC007 ，")
                (requireActivity().application as BaseApplication).disconnectWebSocket()
                TipDialog.Builder(requireContext())
                    .setTitleMessage(getString(LibAppR.string.app_tip))
                    .setMessage(LibAppR.string.firmware_up_success)
                    .setPositiveListener(LibAppR.string.app_confirm) {
                        ARouter.getInstance().build(RouterConfig.MAIN).navigation(requireContext())
                        requireActivity().finish()
                    }
                    .setCancelListener(LibAppR.string.app_cancel) {

                    }
                    .create().show()
            } else {
                XLog.w("TC007 - TC007 !")
                showReInstallDialog(file)
            }
        }
    }

    private fun showReInstallDialog(file: File) {
        val dialog = ConfirmSelectDialog(requireContext())
        dialog.setShowIcon(true)
        dialog.setTitleRes(LibAppR.string.ts004_install_tips)
        dialog.setCancelText(LibAppR.string.ts004_install_cancel)
        dialog.setConfirmText(LibAppR.string.ts004_install_continue)
        dialog.onConfirmClickListener = {
            installFirmware(file)
        }
        dialog.show()
    }

    private fun showReDownloadDialog(firmwareData: FirmwareViewModel.FirmwareData) {
        val dialog = ConfirmSelectDialog(requireContext())
        dialog.setShowIcon(true)
        dialog.setTitleRes(LibAppR.string.ts004_download_tips)
        dialog.setCancelText(LibAppR.string.ts004_download_cancel)
        dialog.setConfirmText(LibAppR.string.ts004_download_continue)
        dialog.onConfirmClickListener = {
            downloadFirmware(firmwareData)
        }
        dialog.show()
    }


    private fun restoreFactory() {
        TipDialog.Builder(requireContext())
            .setTitleMessage(getString(LibAppR.string.ts004_reset_tip1, "TC007"))
            .setMessage(getString(LibAppR.string.ts004_reset_tip2))
            .setPositiveListener(LibAppR.string.app_ok) {
                resetAll()
            }
            .setCancelListener(LibAppR.string.app_cancel) {
            }
            .setCanceled(true)
            .create().show()
    }


    private fun resetAll() {
        showLoadingDialog(LibAppR.string.ts004_reset_tip3)
        lifecycleScope.launch {
            val isSuccess = false // TC001 uses USB connection, factory reset not available via network
            if (isSuccess) {
                XLog.d("TC007 ，")
                TToast.shortToast(requireContext(), LibAppR.string.ts004_reset_tip4)
                (requireActivity().application as BaseApplication).disconnectWebSocket()
                ARouter.getInstance().build(RouterConfig.MAIN).navigation(requireContext())
                requireActivity().finish()
            } else {
                TToast.shortToast(requireContext(), LibAppR.string.operation_failed_tips)
            }
            delay(500)
            dismissLoadingDialog()
        }
    }
}
