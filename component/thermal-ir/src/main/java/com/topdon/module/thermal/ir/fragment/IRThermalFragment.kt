package com.topdon.module.thermal.ir.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.*
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.AppUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.view.MainTitleView
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.utils.CommUtils
import com.topdon.module.thermal.ir.BuildConfig
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.activity.IRThermalNightActivity
import com.topdon.module.thermal.ir.activity.IRThermalPlusActivity

class IRThermalFragment : BaseFragment(), View.OnClickListener {

    // Only TC001 supported - TC007 support removed

    override fun initContentView() = R.layout.fragment_thermal_ir

    override fun initView() {
        // Always use TC001 settings
        title_view.setTitleText(getString(R.string.tc_has_line_device))

        cl_open_thermal.setOnClickListener(this)
        tv_main_enter.setOnClickListener(this)

        // Only show TC001 UI elements
        tv_main_enter.isVisible = true
        cl_07_connect_tips.isVisible = false // TC007 UI hidden
        tv_07_connect.isVisible = false // TC007 UI hidden

        // Always use TC001 animation
        animation_view.setAnimation("TDAnimationJSON.json")
        checkConnect()
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                // Only TC001 supported - no special network switching needed
                if (WebSocketProxy.getInstance().isConnected()) {
                    NetWorkUtils.switchNetwork(true)
                } else {
                    NetWorkUtils.connectivityManager.bindProcessToNetwork(null)
                }
            }
        })
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        // Always check connect for TC001
        checkConnect()
    }

    override fun connected() {
        SharedManager.hasTcLine = true
        // Always show connected state for TC001
        cl_connect.isVisible = true
        cl_not_connect.isVisible = false
    }

    override fun disConnected() {
        // Always show disconnected state for TC001
        cl_connect.isVisible = false
        cl_not_connect.isVisible = true
    }

    override fun onSocketConnected(isTS004: Boolean) {
        // TC007/TS004 support removed - only TC001 supported
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        // TC007/TS004 support removed - only TC001 supported
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
            cl_open_thermal -> {
                // Only TC001 devices supported
                if (DeviceTools.isTC001PlusConnect()) {
                    startActivityForResult(Intent(requireContext(), IRThermalPlusActivity::class.java), 101)
                } else if (DeviceTools.isTC001LiteConnect()) {
            // TODO: Replace RouterConfig reference with direct navigation
                    } else if (DeviceTools.isHikConnect()) {
            // TODO: Replace RouterConfig reference with direct navigation
                    } else {
                        startActivityForResult(Intent(requireContext(), IRThermalNightActivity::class.java), 101)
                    }
                }
            }
            tv_main_enter -> {
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
            // TC007 click handlers removed - only TC001 supported
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
                    val url = "https://www.topdon.com/pages/pro-down?fuzzy=TS001"
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



}
