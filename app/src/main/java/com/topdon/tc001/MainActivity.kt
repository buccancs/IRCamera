package com.topdon.tc001

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.topdon.lib.core.navigation.NavigationManager
import com.blankj.utilcode.util.AppUtils
import com.elvishew.xlog.XLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// Note: SupHelp library integration is not included in this build configuration
import com.example.thermal_lite.activity.IRThermalLiteActivity
import com.csl.irCamera.R
import com.topdon.tc001.gsr.GSRQuickRecordingActivity
import com.topdon.tc001.sensors.gsr.GSRSensorRecorder
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
// Note: HIK thermal camera activity implementation is module-specific
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.event.TS004ResetEvent
import com.topdon.lib.core.bean.event.WinterClickEvent
import com.topdon.lib.core.bean.event.device.DevicePermissionEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.AppConfig
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.FirmwareUpDialog
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.dialog.TipOtgDialog
import com.topdon.lib.core.ktbase.BaseBindingActivity
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.tools.ConstantLanguages
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.PermissionUtils
import com.topdon.lib.core.viewmodel.VersionViewModel
import com.topdon.lms.sdk.LMS
import com.topdon.module.thermal.ir.activity.IRThermalNightActivity
import com.topdon.module.thermal.ir.activity.IRThermalPlusActivity
import com.topdon.module.thermal.ir.fragment.IRGalleryTabFragment
import com.topdon.module.user.fragment.MineFragment
import com.topdon.tc001.app.App
import com.topdon.tc001.fragment.MainFragment
import com.topdon.tc001.utils.AppVersionUtil
import com.csl.irCamera.R
import com.csl.irCamera.BuildConfig
import com.csl.irCamera.databinding.ActivityMainBinding
// Zoho dependencies commented out - not available in build
// import com.zoho.commons.LauncherModes
// import com.zoho.commons.LauncherProperties
// import com.zoho.salesiqembed.ZohoSalesIQ
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : BaseBindingActivity<ActivityMainBinding>(), View.OnClickListener {
    
    private companion object {
        private const val PERMISSION_INIT_DATA = 0
        private const val PERMISSION_GALLERY = 1
        private const val PERMISSION_CONNECT = 2
        private const val DEFAULT_TAB_INDEX = 1
        private const val OFFSCREEN_PAGE_LIMIT = 3
    }
    
    private val versionViewModel: VersionViewModel by viewModels()
    private var checkPermissionType: Int = -1

    private val hasShownClause: Boolean get() = SharedManager.getHasShowClause()
    private val isConnected: Boolean get() = DeviceTools.isConnect()
    private val webSocket = WebSocketProxy.getInstance()
    
    override fun initView() {
        if (!hasShownClause) {
            navigateToClause()
            return
        }

        setupApplication()
        setupViewPager()
        setupClickListeners()
        initializeServices()
        handleDeviceConnections()
    }

    private fun navigateToClause() {
        NavigationManager.build(RouterConfig.CLAUSE).navigation(this)
        finish()
    }

    private fun setupApplication() {
        logInfo()
        lifecycleScope.launch(Dispatchers.IO) {
            // Note: SupHelp AI upscaler integration is not included in this build
        }
        copyFile("SR.pb", File(filesDir, "SR.pb"))
        BaseApplication.instance.clearDb()
    }

    private fun setupViewPager() {
        binding.viewPage.apply {
            offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
            isUserInputEnabled = false
            adapter = ViewPagerAdapter(this@MainActivity)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) = refreshTabSelect(position)
            })
            if (savedInstanceState == null) {
                setCurrentItem(DEFAULT_TAB_INDEX, false)
            }
        }
        binding.viewMinePoint.isVisible = !SharedManager.hasClickWinter
    }

    private fun setupClickListeners() {
        with(binding) {
            clIconGallery.setOnClickListener(this@MainActivity)
            viewMain.setOnClickListener(this@MainActivity)
            clIconMine.setOnClickListener(this@MainActivity)
        }
    }

    private fun initializeServices() {
        App.instance.initWebSocket()
        
        if (BaseApplication.instance.isDomestic()) {
            checkAppVersion(true)
        } else {
            versionViewModel.checkVersion()
        }
    }

    private fun handleDeviceConnections() {
        val noDevicesConnected = !SharedManager.hasTcLine && !SharedManager.hasTS004 && !SharedManager.hasTC007
        
        if (noDevicesConnected) {
            when {
                isConnected && !webSocket.isConnected() -> 
                    navigateToThermal(RouterConfig.IR_MAIN, false)
                webSocket.isTS004Connect() -> 
                    NavigationManager.build(RouterConfig.IR_MONOCULAR).navigation(this)
                webSocket.isTC007Connect() -> 
                    navigateToThermal(RouterConfig.IR_MAIN, true)
            }
        }

        updateConnectionFlags()
    }

    private fun navigateToThermal(route: String, isTC007: Boolean) {
        NavigationManager.build(route)
            .withBoolean(ExtraKeyConfig.IS_TC007, isTC007)
            .navigation(this)
    }

    private fun updateConnectionFlags() {
        if (isConnected) SharedManager.hasTcLine = true
        if (webSocket.isTS004Connect()) SharedManager.hasTS004 = true
        if (webSocket.isTC007Connect()) SharedManager.hasTC007 = true
    }

    override fun onStart() {
        super.onStart()

        // Activity operation
        versionViewModel.updateLiveData.observe(this) {
            FirmwareUpDialog(this).apply {
                titleStr = getString(com.topdon.lib.core.R.string.update_new_version)
                sizeStr = it.versionNo
                contentStr = it.description
                isShowCancel = !it.isForcedUpgrade
                onConfirmClickListener = {
                    updateApk(it.downPageUrl)
                }
                onCancelClickListener = {
                    SharedManager.setVersionCheckDate(System.currentTimeMillis()) // Activity operation
                }
            }.show()
        }
    }

    private fun updateApk(url: String) {
        if (applicationInfo.targetSdkVersion < Build.VERSION_CODES.P) {

            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            intent.data = Uri.parse(url)
            startActivity(intent)
        } else {
            if (AppUtils.isAppInstalled("com.android.vending")) {
                try {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    intent.data = Uri.parse(AppConfig.GOOGLE_APK_MARKET_URL)
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    intent.data = Uri.parse(AppConfig.GOOGLE_APK_URL)
                    startActivity(intent)
                }
            } else {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse(AppConfig.GOOGLE_APK_URL)
                startActivity(intent)
            }
        }
    }

    private var resetTipsDialog: TipDialog? = null

    private fun showResetTipsDialog() {
        disconnectDialog?.dismiss()
        if (resetTipsDialog == null) {
            resetTipsDialog =
                TipDialog.Builder(this)
                    .setMessage(R.string.device_reset_alert)
                    .setPositiveListener(R.string.app_got_it) {
                    }
                    .create()
        }
        resetTipsDialog?.show()
    }

    private var disconnectDialog: TipDialog? = null

    private fun dialogDisconnect() {
        if (resetTipsDialog?.isShowing == true) {
            return
        }
        if (disconnectDialog == null) {
            disconnectDialog =
                TipDialog.Builder(this)
                    .setMessage(R.string.device_disconnect_alert)
                    .setPositiveListener(R.string.app_got_it) {
                    }
                    .create()
        }
        disconnectDialog?.show()
    }

    private fun copyFile(
        filename: String,
        targetFile: File,
    ) {
        if (targetFile.exists()) { // Activity operation
            return
        }
        try {
            val inputStream = assets.open(filename)
            val outputStream: OutputStream = FileOutputStream(targetFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun initData() {
        checkPermissionType = 0
        checkCameraPermission()
        
        // Log that PC-to-Phone communication support is available
        Log.i("MainActivity", "✅ PC-to-Phone communication integration available - RecordingService supports network control")
    }

    override fun onResume() {
        super.onResume()
        LMS.getInstance().language = ConstantLanguages.ENGLISH
//        DeviceTools.isConnect(true)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.clIconGallery -> { // Gallery
                checkPermissionType = 1
                checkStoragePermission()
            }
            binding.viewMain -> { // Activity operation
                binding.viewPage.setCurrentItem(1, false)
            }
            binding.clIconMine -> { // Activity operation
                binding.viewPage.setCurrentItem(2, false)
            }
        }
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?,
    ): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TipDialog.Builder(this)
                .setMessage(getString(R.string.main_exit, CommUtils.getAppName()))
                .setCancelListener(R.string.app_no)
                .setPositiveListener(R.string.app_yes) {
                    BaseApplication.instance.exitAll()
                    finish()
                }
                .create().show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getDevicePermission(event: DevicePermissionEvent) {
        DeviceTools.requestUsb(this, 0, event.device)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWinterClick(event: WinterClickEvent) {
        viewMinePoint.isVisible = false
    }

    private fun refreshTabSelect(index: Int) {
        binding.ivIconGallery.isSelected = false
        binding.tvIconGallery.isSelected = false
        binding.ivIconMine.isSelected = false
        binding.tvIconMine.isSelected = false
        binding.ivBottomMainBg.setImageResource(R.drawable.ic_main_bg_not_select)

        when (index) {
            0 -> { // Gallery
                binding.ivIconGallery.isSelected = true
                binding.tvIconGallery.isSelected = true
            }
            1 -> {
                binding.ivBottomMainBg.setImageResource(R.drawable.ic_main_bg_select)
            }
            2 -> { // Set flag
                binding.ivIconMine.isSelected = true
                binding.tvIconMine.isSelected = true
            }
        }
    }

    override fun connected() {
        if (SharedManager.isConnectAutoOpen) {
            checkPermissionType = 2
            checkCameraPermission()
        }
    }

    private var tipOtgDialog: TipOtgDialog? = null

    override fun disConnected() {
        if (WebSocketProxy.getInstance().isTS004Connect()) {
            NavigationManager.build(RouterConfig.IR_MONOCULAR).navigation(this)
        }
        // Activity operationOTGactivity
        if (tipOtgDialog != null && tipOtgDialog!!.isShowing) {
            return
        }
        if (SharedManager.isTipOTG && !BaseApplication.instance.hasOtgShow) {
            tipOtgDialog =
                TipOtgDialog.Builder(this)
                    .setMessage(R.string.tip_otg)
                    .setPositiveListener(R.string.app_confirm) {
                        SharedManager.isTipOTG = !it
                    }
                    .create()
            tipOtgDialog?.show()
            BaseApplication.instance.hasOtgShow = true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTS004ResetEvent(event: TS004ResetEvent) {
        showResetTipsDialog()
    }

    override fun onSocketConnected(isTS004: Boolean) {
        disconnectDialog?.dismiss()
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED) && isTS004) { // TC007activity
            dialogDisconnect()
        }
    }

    private class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    IRGalleryTabFragment().apply {
                        arguments =
                            Bundle().also {
                                it.putBoolean(ExtraKeyConfig.CAN_SWITCH_DIR, true)
                                it.putBoolean(ExtraKeyConfig.HAS_BACK_ICON, false)
                                it.putInt(ExtraKeyConfig.DIR_TYPE, GalleryRepository.DirType.LINE.ordinal)
                            }
                    }
                }
                1 -> MainFragment()
                else -> MineFragment()
            }
        }
    }

    private fun getNeedPermissionList(): SparseArray<List<String>> {
        val cameraPermissions = listOf(Manifest.permission.CAMERA)
        val storagePermissions = when {
            applicationInfo.targetSdkVersion >= 33 -> listOf(
                Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_IMAGES,
                Permission.WRITE_EXTERNAL_STORAGE,
            )
            else -> listOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
        }
        
        return SparseArray<List<String>>().apply {
            append(R.string.permission_request_camera_app, cameraPermissions)
            append(R.string.permission_request_storage_app, storagePermissions)
        }
    }

    private fun checkCameraPermission() {
        val cameraPermissions = getNeedPermissionList()[R.string.permission_request_camera_app]
        
        if (PermissionUtils.isVisualUser() || XXPermissions.isGranted(this, cameraPermissions)) {
            initCameraPermission()
            return
        }
        
        if (BaseApplication.instance.isDomestic()) {
            if (SharedManager.getMainPermissionsState()) return
            
            TipDialog.Builder(this)
                .setMessage(getString(R.string.permission_request_camera_app, CommUtils.getAppName()))
                .setCancelListener(R.string.app_cancel)
                .setPositiveListener(R.string.app_confirm) { initCameraPermission() }
                .create().show()
        } else {
            initCameraPermission()
        }
    }

    /**
     * activity
     */
    private fun initCameraPermission() {
        XXPermissions.with(this)
            .permission(getNeedPermissionList()[R.string.permission_request_camera_app])
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        if (allGranted) {
                            checkStoragePermission()
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (BaseApplication.instance.isDomestic()) {
                            SharedManager.setMainPermissionsState(true)
                        }
                        if (doNotAskAgain) {
                            // Activity operation
                            TipDialog.Builder(this@MainActivity)
                                .setTitleMessage(getString(R.string.app_tip))
                                .setMessage(
                                    if (PermissionUtils.hasCameraPermission()) {
                                        getString(R.string.app_album_content)
                                    } else {
                                        getString(R.string.app_camera_content)
                                    },
                                )
                                .setPositiveListener(R.string.app_open) {
                                    AppUtils.launchAppDetailsSettings()
                                }
                                .setCancelListener(R.string.app_cancel) {
                                }
                                .setCanceled(true)
                                .create().show()
                        }
                    }
                },
            )
    }

    private fun checkStoragePermission() {
        if (!XXPermissions.isGranted(this, getNeedPermissionList()[R.string.permission_request_storage_app])) {
            if (BaseApplication.instance.isDomestic()) {
                TipDialog.Builder(this)
                    .setMessage(getString(R.string.permission_request_storage_app, CommUtils.getAppName()))
                    .setCancelListener(R.string.app_cancel)
                    .setPositiveListener(R.string.app_confirm) {
                        initStoragePermission()
                    }
                    .create().show()
            } else {
                initStoragePermission()
            }
        } else {
            initStoragePermission()
        }
    }

    /**
     * activity
     */
    private fun initStoragePermission() {
        if (PermissionUtils.isVisualUser()) {
            jumpIRActivity()
            return
        }
        XXPermissions.with(this)
            .permission(
                getNeedPermissionList()[R.string.permission_request_storage_app],
            )
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        if (allGranted) {
                            jumpIRActivity()
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // Activity operation
                            TipDialog.Builder(this@MainActivity)
                                .setTitleMessage(getString(R.string.app_tip))
                                .setMessage(getString(R.string.app_album_content))
                                .setPositiveListener(R.string.app_open) {
                                    AppUtils.launchAppDetailsSettings()
                                }
                                .setCancelListener(R.string.app_cancel) {
                                }
                                .setCanceled(true)
                                .create().show()
                        }
                    }
                },
            )
    }

    fun jumpIRActivity() {
        when (checkPermissionType) {
            0 -> {
                DeviceTools.isConnect(isSendConnectEvent = true)
            }
            1 -> {
                viewPage.setCurrentItem(0, false)
            }
            2 -> {
                if (DeviceTools.isTC001PlusConnect()) {
                    NavigationManager.build(RouterConfig.IR_MAIN).navigation(this@MainActivity)
                    startActivityForResult(Intent(this@MainActivity, IRThermalPlusActivity::class.java), 101)
                } else if (DeviceTools.isTC001LiteConnect()) {
                    NavigationManager.build(RouterConfig.IR_MAIN).navigation(this@MainActivity)
                    startActivityForResult(Intent(this@MainActivity, IRThermalLiteActivity::class.java), 101)
                } else if (DeviceTools.isHikConnect()) {
                    NavigationManager.build(RouterConfig.IR_MAIN).navigation(this@MainActivity)
                    // Note: Using IRThermalNightActivity as fallback for HIK thermal devices
                    startActivityForResult(Intent(this@MainActivity, IRThermalNightActivity::class.java), 101)
                } else {
                    NavigationManager.build(RouterConfig.IR_MAIN).navigation(this@MainActivity)
                    startActivityForResult(Intent(this@MainActivity, IRThermalNightActivity::class.java), 101)
                }
            }
        }
    }

    private var appVersionUtil: AppVersionUtil? = null

    private fun checkAppVersion(isShow: Boolean) {
        if (appVersionUtil == null) {
            appVersionUtil = AppVersionUtil(this, object : AppVersionUtil.DotIsShowListener {
                override fun isShow(show: Boolean) {}
                override fun version(version: String) {}
            })
        }
        appVersionUtil?.checkVersion(isShow)
    }
    
    /**
     * Launch GSR Quick Recording Activity
     * This provides direct access to the GSR recording functionality from the main app
     */
    fun launchGSRRecording() {
        // Check GSR permissions first
        if (GSRSensorRecorder.hasRequiredPermissions(this)) {
            GSRQuickRecordingActivity.start(this)
        } else {
            // Show permission explanation and launch settings if needed
            TipDialog.Builder(this)
                .setTitleMessage("GSR Recording Permissions")
                .setMessage("GSR recording requires Bluetooth and location permissions. Enable them in settings?")
                .setPositiveListener("Settings") {
                    GSRQuickRecordingActivity.start(this)
                }
                .setCancelListener("Cancel") { }
                .create().show()
        }
    }
}
