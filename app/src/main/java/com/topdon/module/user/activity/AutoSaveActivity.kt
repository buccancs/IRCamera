package com.topdon.module.user.activity

import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.view.MySettingItem
import com.topdon.tc001.R

class AutoSaveActivity :BaseActivity(){

    override fun initContentView() = R.layout.activity_auto_save

    override fun initView() {
        val settingItemSaveSelect = findViewById<MySettingItem>(R.id.setting_item_save_select)
        settingItemSaveSelect.isChecked = SharedManager.is04AutoSync
        settingItemSaveSelect.setOnCheckedChangeListener { _, isChecked ->
            SharedManager.is04AutoSync = isChecked
        }
    }

    override fun initData() {
    }
}