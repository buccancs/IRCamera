package com.topdon.module.user.activity

import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.tc001.R
import kotlinx.android.synthetic.main.activity_auto_save.*

class AutoSaveActivity :BaseActivity(){

    override fun initContentView() = R.layout.activity_auto_save

    override fun initView() {
        setting_item_save_select.isChecked = SharedManager.is04AutoSync
        setting_item_save_select.setOnCheckedChangeListener { _, isChecked ->
            SharedManager.is04AutoSync = isChecked
        }
    }

    override fun initData() {
    }
}