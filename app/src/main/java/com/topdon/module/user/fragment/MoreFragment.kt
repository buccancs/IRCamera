package com.topdon.module.user.fragment

import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.ui.SettingNightView
import com.topdon.tc001.R
import androidx.appcompat.widget.SwitchCompat

/**
 * 插件式 "更多" 页面
 * 
 * 支持TC001Plus等其他插件式设备 (TS004/TC007 support removed)
 */
class MoreFragment : BaseFragment(), View.OnClickListener {

    private lateinit var settingItemModel: SettingNightView
    private lateinit var settingItemCorrection: SettingNightView
    private lateinit var settingItemDual: SettingNightView
    private lateinit var settingItemUnit: SettingNightView
    private lateinit var settingVersion: View
    private lateinit var settingDeviceInformation: View
    private lateinit var settingReset: View
    private lateinit var settingItemAutoShow: SwitchCompat
    private lateinit var settingItemConfigSelect: SwitchCompat

    override fun initContentView() = R.layout.fragment_more

    override fun initView() {
        settingItemModel = findViewById(R.id.setting_item_model)
        settingItemCorrection = findViewById(R.id.setting_item_correction)
        settingItemDual = findViewById(R.id.setting_item_dual)
        settingItemUnit = findViewById(R.id.setting_item_unit)
        settingVersion = findViewById(R.id.setting_version)
        settingDeviceInformation = findViewById(R.id.setting_device_information)
        settingReset = findViewById(R.id.setting_reset)
        settingItemAutoShow = findViewById(R.id.setting_item_auto_show)
        settingItemConfigSelect = findViewById(R.id.setting_item_config_select)

        settingItemModel.setOnClickListener(this)//温度修正
        settingItemCorrection.setOnClickListener(this)//图像校正
        settingItemDual.setOnClickListener(this)//双光校正
        settingItemUnit.setOnClickListener(this)//温度单位

        // Hide removed device-specific elements (TS004/TC007 support removed)
        settingVersion.isVisible = false
        settingDeviceInformation.isVisible = false
        settingReset.isVisible = false
        
        // Only show dual light correction for TC001Plus
        settingItemDual.isVisible = DeviceTools.isTC001PlusConnect()

        // Auto-open settings for connected devices
        settingItemAutoShow.isChecked = SharedManager.isConnectAutoOpen
        settingItemAutoShow.setOnCheckedChangeListener { _, isChecked ->
            SharedManager.isConnectAutoOpen = isChecked
        }

        // Configuration save settings
        settingItemConfigSelect.isChecked = SaveSettingUtil.isSaveSetting
        settingItemConfigSelect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                TipDialog.Builder(requireContext())
                    .setMessage(R.string.save_setting_tips)
                    .setPositiveListener(R.string.app_ok) {
                        SaveSettingUtil.isSaveSetting = true
                    }
                    .setCancelListener(R.string.app_cancel) {
                        settingItemConfigSelect.isChecked = false
                    }
                    .setCanceled(false)
                    .create().show()
            } else {
                SaveSettingUtil.reset()
                SaveSettingUtil.isSaveSetting = false
            }
        }
    }

    override fun initData() {
    }

    override fun connected() {
        settingItemDual.isVisible = DeviceTools.isTC001PlusConnect()
    }

    override fun disConnected() {
        settingItemDual.isVisible = false
    }

    override fun onClick(v: View?) {
       when(v){
           settingItemModel -> {//温度修正
               // TODO: Replace IR setting navigation - startActivity(Intent(requireContext(), IRSettingActivity::class.java))
           }
           settingItemDual->{
               // TODO: Replace manual start navigation - startActivity(Intent(requireContext(), ManualStartActivity::class.java))
           }
           settingItemUnit -> {//温度单位
               startActivity(Intent(requireContext(), com.topdon.module.user.activity.UnitActivity::class.java))
           }
           settingItemCorrection->{//图像校正
               // TODO: Replace correction navigation - startActivity(Intent(requireContext(), IRCorrectionActivity::class.java))
           }
       }
    }
}