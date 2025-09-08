package com.topdon.module.thermal.ir.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.AppUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.utils.CommUtils
import com.topdon.module.thermal.ir.BuildConfig
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.activity.IRThermalNightActivity
import com.topdon.module.thermal.ir.activity.IRThermalPlusActivity
import com.topdon.module.thermal.ir.databinding.FragmentThermalIrBinding

class IRThermalFragment : BaseFragment(), View.OnClickListener {

    // Only TC001 is supported now - no need for device type differentiation

    private var _binding: FragmentThermalIrBinding? = null
    private val binding get() = _binding!!

    override fun initContentView(): Int {
        _binding = FragmentThermalIrBinding.inflate(layoutInflater)
        return R.layout.fragment_thermal_ir
    }

    override fun initView() {
        binding.titleView.setTitleText(getString(LibAppR.string.tc_has_line_device))

        binding.clOpenThermal.setOnClickListener(this)
        binding.tvMainEnter.setOnClickListener(this)

        // Only show TC001 (line device) UI elements
        binding.tvMainEnter.isVisible = true
        binding.cl07ConnectTips.isVisible = false
        binding.tv07Connect.isVisible = false

        binding.animationView.setAnimation("TDAnimationJSON.json")
        checkConnect()
        
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                // For TC001 USB connection, no need to switch networks
                NetWorkUtils.connectivityManager.bindProcessToNetwork(null)
            }
        })
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        // Only TC001 is supported now
        checkConnect()
    }

    override fun connected() {
        SharedManager.hasTcLine = true
        // TC001 USB connection
        binding.clConnect.isVisible = true
        binding.clNotConnect.isVisible = false
    }

    override fun disConnected() {
        // For TC001 USB connection
        binding.clConnect.isVisible = false
        binding.clNotConnect.isVisible = true
    }

    override fun onSocketConnected(isTS004: Boolean) {
        // TC001 doesn't use socket connections - handled via USB
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        // TC001 doesn't use socket connections - handled via USB
    }

    private fun checkConnect() {
        if (DeviceTools.isConnect(isAutoRequest = false)) {
            connected()
        } else {
            disConnected()
            if (DeviceTools.findUsbDevice() != null) {//,
                showConnectTip()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.clOpenThermal -> {
                // Only TC001 is supported
                if (DeviceTools.isTC001PlusConnect()) {
                    startActivityForResult(Intent(requireContext(), IRThermalPlusActivity::class.java), 101)
                } else if(DeviceTools.isTC001LiteConnect()){
                    ARouter.getInstance().build(RouterConfig.IR_TCLITE).navigation(activity,101)
                } else {
                    startActivityForResult(Intent(requireContext(), IRThermalNightActivity::class.java), 101)
                }
            }
            binding.tvMainEnter -> {
                if (!DeviceTools.isConnect()) {
                    if (DeviceTools.findUsbDevice() == null) {
                        activity?.let {
                            TipDialog.Builder(it)
                                .setMessage(LibAppR.string.device_connect_tip)
                                .setPositiveListener(LibAppR.string.app_confirm)
                                .create().show()
                        }
                    } else {
                        XXPermissions.with(this)
                            .permission(listOf(
                                Permission.CAMERA
                            ))
                            .request(object : OnPermissionCallback {
                                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                                    if (allGranted) {
                                        showConnectTip()
                                    }
                                }

                                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                                    if (doNotAskAgain) {
                                        context?.let {
                                            TipDialog.Builder(it)
                                                .setTitleMessage(getString(LibAppR.string.app_tip))
                                                .setMessage(getString(LibAppR.string.app_camera_content))
                                                .setPositiveListener(LibAppR.string.app_open) {
                                                    AppUtils.launchAppDetailsSettings()
                                                }
                                                .setCancelListener(LibAppR.string.app_cancel) {
                                                }
                                                .setCanceled(true)
                                                .create().show()
                                        }
                                    }
                                }
                            })
                    }
                }
            }
            // Removed TC007 connection handlers - only TC001 (USB) is supported
        }
    }

    private var tipConnectDialog: TipDialog? = null

    private var isCancelUpdateVersion = false
    // android10 usb,android 27
    private fun showConnectTip() {
        // targetSdk27android os10
        if (requireContext().applicationInfo.targetSdkVersion >= Build.VERSION_CODES.P &&
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
        ) {
            if (isCancelUpdateVersion) {
                return
            }
            if (tipConnectDialog != null && tipConnectDialog!!.isShowing) {
                return
            }
            tipConnectDialog = TipDialog.Builder(requireContext())
                .setMessage(getString(LibAppR.string.tip_target_sdk))
                .setPositiveListener(LibAppR.string.app_confirm) {
                    val url = "https://www.topdon.com/pages/pro-down?fuzzy=TC001"
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }.setCancelListener(LibAppR.string.app_cancel, {
                    isCancelUpdateVersion = true
                })
                .create()
            tipConnectDialog?.show()
        }
    }

    private fun checkStoragePermission() {
        val permissionList: List<String> = if (activity?.applicationInfo?.targetSdkVersion!! >= 34){
            listOf(
                Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_IMAGES,
                Permission.WRITE_EXTERNAL_STORAGE
            )
        } else if (activity?.applicationInfo?.targetSdkVersion!! >= 33) {
            listOf(
                Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_IMAGES,
                Permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            listOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!XXPermissions.isGranted(requireContext(), permissionList)) {
            if (BaseApplication.instance.isDomestic()) {
                context?.let {
                    TipDialog.Builder(it)
                        .setMessage(getString(LibAppR.string.permission_request_storage_app, CommUtils.getAppName()))
                        .setCancelListener(LibAppR.string.app_cancel)
                        .setPositiveListener(LibAppR.string.app_confirm) {
                            initStoragePermission(permissionList)
                        }
                        .create().show()
                }
            } else {
                initStoragePermission(permissionList)
            }
        } else {
            initStoragePermission(permissionList)
        }
    }

    private fun initStoragePermission(permissionList: List<String>) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
