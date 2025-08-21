package com.topdon.module.user.fragment
import com.topdon.tc001.R

import android.view.View
import androidx.core.view.isVisible
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.module.user.R
import kotlinx.android.synthetic.main.fragment_more.*

/**
 * 插件式 "更多" 页面
 * 
 * 支持TC001Plus等其他插件式设备 (TS004/TC007 support removed)
 */
class MoreFragment : BaseFragment(), View.OnClickListener {

    override fun initContentView() = R.layout.fragment_more

    override fun initView() {
        setting_item_model.setOnClickListener(this)//温度修正
        setting_item_correction.setOnClickListener(this)//图像校正
        setting_item_dual.setOnClickListener(this)//双光校正
        setting_item_unit.setOnClickListener(this)//温度单位

        // Hide removed device-specific elements (TS004/TC007 support removed)
        setting_version.isVisible = false
        setting_device_information.isVisible = false
        setting_reset.isVisible = false
        
        // Only show dual light correction for TC001Plus
        setting_item_dual.isVisible = DeviceTools.isTC001PlusConnect()

        // Auto-open settings for connected devices
        setting_item_auto_show.isChecked = SharedManager.isConnectAutoOpen
        setting_item_auto_show.setOnCheckedChangeListener { _, isChecked ->
            SharedManager.isConnectAutoOpen = isChecked
        }

        // Configuration save settings
        setting_item_config_select.isChecked = SaveSettingUtil.isSaveSetting
        setting_item_config_select.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                TipDialog.Builder(requireContext())
                    .setMessage(R.string.save_setting_tips)
                    .setPositiveListener(R.string.app_ok) {
                        SaveSettingUtil.isSaveSetting = true
                    }
                    .setCancelListener(R.string.app_cancel) {
                        setting_item_config_select.isChecked = false
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
        setting_item_dual.isVisible = DeviceTools.isTC001PlusConnect()
    }

    override fun disConnected() {
        setting_item_dual.isVisible = false
    }

    override fun onClick(v: View?) {
       when(v){
           setting_item_model -> {//温度修正
               // TODO: Replace IR setting navigation - startActivity(Intent(requireContext(), IRSettingActivity::class.java))
           }
           setting_item_dual->{
               // TODO: Replace manual start navigation - startActivity(Intent(requireContext(), ManualStartActivity::class.java))
           }
           setting_item_unit -> {//温度单位
               startActivity(Intent(requireContext(), com.topdon.module.user.activity.UnitActivity::class.java))
           }
           setting_item_correction->{//图像校正
               // TODO: Replace correction navigation - startActivity(Intent(requireContext(), IRCorrectionActivity::class.java))
           }
       }
    }
}