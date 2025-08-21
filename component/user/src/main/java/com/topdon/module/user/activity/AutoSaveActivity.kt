package com.topdon.module.user.activity

import androidx.appcompat.widget.SwitchCompat
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R

/**
 * 自动保存到手机
 */
class AutoSaveActivity :BaseActivity(){

    override fun initContentView() = R.layout.activity_auto_save

    override fun initView() {
        val settingItemSaveSelect = findViewById<SwitchCompat>(R.id.setting_item_save_select)
        
        settingItemSaveSelect.isChecked = SharedManager.is04AutoSync
        settingItemSaveSelect.setOnCheckedChangeListener { _, isChecked ->
            SharedManager.is04AutoSync = isChecked
        }
    }

    override fun initData() {
    }
}