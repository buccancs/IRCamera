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
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.activity.IRThermalNightActivity
import com.topdon.module.thermal.ir.activity.IRThermalPlusActivity
import com.topdon.module.thermal.databinding.FragmentThermalIrBinding

class IRThermalFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentThermalIrBinding? = null
    private val binding get() = _binding!!

    // Only TC001 is supported now - no need for device type differentiation

    override fun initContentView() = R.layout.fragment_thermal_ir

    override fun initView() {
        _binding = FragmentThermalIrBinding.inflate(layoutInflater)
        
        binding.titleView.setTitleText(getString(R.string.tc_has_line_device))

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
        cl_connect.isVisible = true
        cl_not_connect.isVisible = false
    }

    override fun disConnected() {
        // For TC001 USB connection
        cl_connect.isVisible = false
        cl_not_connect.isVisible = true
    }

    override fun onSocketConnected(isTS004: Boolean) {
        // TC001 doesn't use socket connections - handled via USB
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        // TC001 doesn't use socket connections - handled via USB
    }

    /**
     * 主动检测连接设备
     */
    private fun checkConnect() {
        if (DeviceTools.isConnect(isAutoRequest = false)) {
            connected()
        } else {
            disConnected()
            if (DeviceTools.findUsbDevice() != null) {//找到设备,但不能连接
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
                    //没有接入设备不需要提示，有系统授权提示框
                    if (DeviceTools.findUsbDevice() == null) {
                        activity?.let {
                            TipDialog.Builder(it)
                                .setMessage(R.string.device_connect_tip)
                                .setPositiveListener(R.string.app_confirm)
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
                                        //拒绝授权并且不再提醒
                                        context?.let {
                                            TipDialog.Builder(it)
                                                .setTitleMessage(getString(R.string.app_tip))
                                                .setMessage(getString(R.string.app_camera_content))
                                                .setPositiveListener(R.string.app_open) {
                                                    AppUtils.launchAppDetailsSettings()
                                                }
                                                .setCancelListener(R.string.app_cancel) {
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
    // 针对android10 usb连接问题,提供android 27版本
    private fun showConnectTip() {
        // targetSdk高于27且android os为10
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
                .setMessage(getString(R.string.tip_target_sdk))
                .setPositiveListener(R.string.app_confirm) {
                    val url = "https://www.topdon.com/pages/pro-down?fuzzy=TC001"
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }.setCancelListener(R.string.app_cancel, {
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
                        .setMessage(getString(R.string.permission_request_storage_app, CommUtils.getAppName()))
                        .setCancelListener(R.string.app_cancel)
                        .setPositiveListener(R.string.app_confirm) {
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

    /**
     * 动态申请权限
     */
    private fun initStoragePermission(permissionList: List<String>) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
