package com.topdon.module.thermal.ir.activity

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.AppUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.event.PDFEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.repository.GalleryRepository.DirType
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lib.core.utils.PermissionUtils
import com.topdon.lms.sdk.LMS
import com.topdon.module.thermal.ir.BuildConfig
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.csl.irCamera.libui.R as LibUiR
import com.topdon.module.thermal.ir.dialog.HomeGuideDialog
import com.topdon.module.thermal.ir.fragment.IRGalleryTabFragment
import com.topdon.module.thermal.ir.fragment.IRThermalFragment
import com.topdon.module.thermal.ir.fragment.AbilityFragment
import com.topdon.module.thermal.ir.fragment.PDFListFragment
import com.topdon.module.thermal.ir.databinding.ActivityIrMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

 *   TC007 .
 * [ExtraKeyConfig.IS_TC007] -  TC007
 * Created by LCG on 2024/4/18.
@Route(path = RouterConfig.IR_MAIN)
class IRMainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityIrMainBinding

     * TC007 .
     * true-TC007 false
    private var isTC007 = false

    override fun initContentView(): Int {
        binding = ActivityIrMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initView()
    }

    override fun initView() {
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        binding.viewPage.offscreenPageLimit = 5
        binding.viewPage.isUserInputEnabled = false
        binding.viewPage.adapter = ViewPagerAdapter(this, isTC007)
        binding.viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                refreshTabSelect(position)
            }
        })
        binding.viewPage.setCurrentItem(2, false)

        binding.clIconMonitor.setOnClickListener(this)
        binding.clIconGallery.setOnClickListener(this)
        binding.viewMainThermal.setOnClickListener(this)
        binding.clIconReport.setOnClickListener(this)
        binding.clIconMine.setOnClickListener(this)

        showGuideDialog()
    }

    override fun onResume() {
        super.onResume()
//        DeviceTools.isConnect(true)
        if (isTC007) {
            if (WebSocketProxy.getInstance().isTC007Connect()) {
                NetWorkUtils.switchNetwork(false)
                binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_connect)
                lifecycleScope.launch {
                    // TC001 uses USB connection, time sync not available via network
                }
                // TC001 connection logic - removed TC007 auto-open since only TC001 is supported
            } else {
                binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_disconnect)
            }
        } else {
            if (DeviceTools.isConnect(isAutoRequest = false)) {
                binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_connect)
            } else {
                binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_disconnect)
            }
        }
    }

    override fun initData() {
    }

    override fun connected() {
        if (!isTC007) {
            binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_connect)
        }
    }

    override fun disConnected() {
        if (!isTC007) {
            binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_disconnect)
        }
    }

    override fun onSocketConnected(isTS004: Boolean) {
        if (!isTS004 && isTC007) {
            binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_connect)
        }
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        if (!isTS004 && isTC007) {
            binding.ivMainBg.setImageResource(R.drawable.ic_ir_main_bg_disconnect)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.clIconMonitor -> {//
                binding.viewPage.setCurrentItem(0, false)
            }
            binding.clIconGallery -> {//
                checkStoragePermission()
            }
            binding.viewMainThermal -> {//
                binding.viewPage.setCurrentItem(2, false)
            }
            binding.clIconReport -> {//
                if (LMS.getInstance().isLogin) {
                    binding.viewPage.setCurrentItem(3, false)
                } else {
                    LMS.getInstance().activityLogin(null) {
                        if (it) {
                            binding.viewPage.setCurrentItem(3, false)
                            EventBus.getDefault().post(PDFEvent())
                        }
                    }
                }
            }
            binding.clIconMine -> {//
                binding.viewPage.setCurrentItem(4, false)
            }
        }
    }

     *  5  tab
     * @param index  tab`[0, 4]`
    private fun refreshTabSelect(index: Int) {
        binding.ivIconMonitor.isSelected = false
        binding.tvIconMonitor.isSelected = false
        binding.ivIconGallery.isSelected = false
        binding.tvIconGallery.isSelected = false
        binding.ivIconReport.isSelected = false
        binding.tvIconReport.isSelected = false
        binding.ivIconMine.isSelected = false
        binding.tvIconMine.isSelected = false

        when (index) {
            0 -> {
                binding.ivIconMonitor.isSelected = true
                binding.tvIconMonitor.isSelected = true
            }
            1 -> {
                binding.ivIconGallery.isSelected = true
                binding.tvIconGallery.isSelected = true
            }
            3 -> {
                binding.ivIconReport.isSelected = true
                binding.tvIconReport.isSelected = true
            }
            4 -> {
                binding.ivIconMine.isSelected = true
                binding.tvIconMine.isSelected = true
            }
        }
    }

     * .
    private fun showGuideDialog() {
        if (SharedManager.homeGuideStep == 0) {//
            return
        }

        when (SharedManager.homeGuideStep) {
            1 -> binding.viewPage.setCurrentItem(0, false)
            2 -> binding.viewPage.setCurrentItem(4, false)
            3 -> binding.viewPage.setCurrentItem(2, false)
        }

        val guideDialog = HomeGuideDialog(this, SharedManager.homeGuideStep)
        guideDialog.onNextClickListener = {
            when (it) {
                1 -> {
                    binding.viewPage.setCurrentItem(4, false)
                    if (Build.VERSION.SDK_INT < 31) {
                        lifecycleScope.launch {
                            delay(100)
                            guideDialog.blurBg(binding.clRoot)
                        }
                    }
                    SharedManager.homeGuideStep = 2
                }
                2 -> {
                    binding.viewPage.setCurrentItem(2, false)
                    if (Build.VERSION.SDK_INT < 31) {
                        lifecycleScope.launch {
                            delay(100)
                            guideDialog.blurBg(binding.clRoot)
                        }
                    }
                    SharedManager.homeGuideStep = 3
                }
                3 -> {
                    SharedManager.homeGuideStep = 0
                }
            }
        }
        guideDialog.onSkinClickListener = {
            SharedManager.homeGuideStep = 0
        }
        guideDialog.setOnDismissListener {
            if (Build.VERSION.SDK_INT >= 31) {
                window?.decorView?.setRenderEffect(null)
            }
        }
        guideDialog.show()

        if (Build.VERSION.SDK_INT >= 31) {
            window?.decorView?.setRenderEffect(RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.MIRROR))
        } else {
            lifecycleScope.launch {
                //1000
                //10001000
                delay(100)
                guideDialog.blurBg(binding.clRoot)
            }
        }
    }


    private fun checkStoragePermission() {
        val permissionList: List<String> =
            if (this.applicationInfo.targetSdkVersion >= 34){
                listOf(
                    Permission.READ_MEDIA_VIDEO,
                    Permission.READ_MEDIA_IMAGES,
                    Permission.WRITE_EXTERNAL_STORAGE,
                )
            } else if (this.applicationInfo.targetSdkVersion >= 34){
                listOf(
                    Permission.READ_MEDIA_VIDEO,
                    Permission.READ_MEDIA_IMAGES,
                    Permission.WRITE_EXTERNAL_STORAGE,
                )
            } else if (this.applicationInfo.targetSdkVersion == 33) {
            listOf(
                Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_IMAGES,
                Permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            listOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!XXPermissions.isGranted(this, permissionList)) {
            if (BaseApplication.instance.isDomestic()) {
                TipDialog.Builder(this)
                    .setMessage(getString(LibAppR.string.permission_request_storage_app, CommUtils.getAppName()))
                    .setCancelListener(LibAppR.string.app_cancel)
                    .setPositiveListener(LibAppR.string.app_confirm) {
                        initStoragePermission(permissionList)
                    }
                    .create().show()
            } else {
                initStoragePermission(permissionList)
            }
        } else {
            initStoragePermission(permissionList)
        }
    }

    private fun initStoragePermission(permissionList: List<String>) {
        if (PermissionUtils.isVisualUser()){
            binding.viewPage.setCurrentItem(1, false)
            return
        }
        XXPermissions.with(this)
            .permission(permissionList)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (allGranted) {
                        binding.viewPage.setCurrentItem(1, false)
                    }
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        TipDialog.Builder(this@IRMainActivity)
                            .setTitleMessage(getString(LibAppR.string.app_tip))
                            .setMessage(getString(LibAppR.string.app_album_content))
                            .setPositiveListener(LibAppR.string.app_open) {
                                AppUtils.launchAppDetailsSettings()
                            }
                            .setCancelListener(LibAppR.string.app_cancel) {
                            }
                            .setCanceled(true)
                            .create().show()
                    }
                }
            })
    }



    private class ViewPagerAdapter(activity: FragmentActivity, val isTC007: Boolean) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 5

        override fun createFragment(position: Int): Fragment {
            if (position == 1) {//
                return IRGalleryTabFragment().apply {
                    arguments = Bundle().also {
                        val dirType = DirType.LINE.ordinal // TC001 only
                        it.putBoolean(ExtraKeyConfig.CAN_SWITCH_DIR, false)
                        it.putBoolean(ExtraKeyConfig.HAS_BACK_ICON, false)
                        it.putInt(ExtraKeyConfig.DIR_TYPE, dirType)
                    }
                }
            } else {
                val fragment = when (position) {
                    0 -> AbilityFragment()
                    2 -> IRThermalFragment()
                    3 -> PDFListFragment()
                    else -> ARouter.getInstance().build(RouterConfig.TC_MORE).navigation() as Fragment
                }
                fragment.arguments = Bundle().also { it.putBoolean(ExtraKeyConfig.IS_TC007, isTC007) }
                return fragment
            }
        }
    }
}