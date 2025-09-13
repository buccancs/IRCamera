package com.topdon.module.thermal.ir.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
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
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.navigation.NavigationManager
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.activity.IRThermalNightActivity
import com.topdon.module.thermal.ir.activity.IRThermalPlusActivity

/**
 * I r thermal fragment for thermal imaging components.
 * Handles specific UI sections and user interactions.
 */
class IRThermalFragment : BaseFragment(), View.OnClickListener {
    /**
\1interface， TC007 device.
\1true-TC007 false-device
     */
    private var isTC007 = false

    // View declarations
    private lateinit var titleView: com.topdon.lib.core.view.TitleView
    private lateinit var clOpenThermal: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var tvMainEnter: android.widget.TextView
    private lateinit var cl07ConnectTips: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var tv07Connect: android.widget.TextView
    private lateinit var animationView: com.airbnb.lottie.LottieAnimationView
    private lateinit var clNotConnect: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var clConnect: androidx.constraintlayout.widget.ConstraintLayout

    override fun initContentView() = R.layout.fragment_thermal_ir

    override fun initView() {
        // Initialize views
        titleView = requireView().findViewById(R.id.title_view)
        clOpenThermal = requireView().findViewById(R.id.cl_open_thermal)
        tvMainEnter = requireView().findViewById(R.id.tv_main_enter)
        cl07ConnectTips = requireView().findViewById(R.id.cl_07_connect_tips)
        tv07Connect = requireView().findViewById(R.id.tv_07_connect)
        animationView = requireView().findViewById(R.id.animation_view)
        clNotConnect = requireView().findViewById(R.id.cl_not_connect)
        clConnect = requireView().findViewById(R.id.cl_connect)

        isTC007 = arguments?.getBoolean(ExtraKeyConfig.IS_TC007, false) ?: false
        titleView.setTitleText(if (isTC007) "TC007" else getString(R.string.tc_has_line_device))

        clOpenThermal.setOnClickListener(this)
        tvMainEnter.setOnClickListener(this)
        cl07ConnectTips.setOnClickListener(this)
        tv07Connect.setOnClickListener(this)

        tvMainEnter.isVisible = !isTC007
        cl07ConnectTips.isVisible = isTC007
        tv07Connect.isVisible = isTC007

        if (isTC007) {
            animationView.setAnimation("TC007AnimationJSON.json")
            clNotConnect.isVisible = !WebSocketProxy.getInstance().isTC007Connect()
            clConnect.isVisible = WebSocketProxy.getInstance().isTC007Connect()
        } else {
            animationView.setAnimation("TDAnimationJSON.json"Test Data"isTS004"Test Data"https://www.topdon.com/pages/pro-down?fuzzy=TS001"
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
        val permissionList: List<String> =
            if (activity?.applicationInfo?.targetSdkVersion!! >= 34)
                {
                    listOf(
                        Permission.READ_MEDIA_VIDEO,
                        Permission.READ_MEDIA_IMAGES,
                        Permission.WRITE_EXTERNAL_STORAGE,
                    )
                } else if (activity?.applicationInfo?.targetSdkVersion!! >= 33) {
                listOf(
                    Permission.READ_MEDIA_VIDEO,
                    Permission.READ_MEDIA_IMAGES,
                    Permission.WRITE_EXTERNAL_STORAGE,
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
\1
     */
    private fun initStoragePermission(permissionList: List<String>) {
    }
}
