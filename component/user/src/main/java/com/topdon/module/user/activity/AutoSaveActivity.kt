package com.topdon.module.user.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R
import com.topdon.module.user.databinding.ActivityAutoSaveBinding

/**
 * 自动保存到手机
 */
@Route(path = RouterConfig.AUTO_SAVE)
class AutoSaveActivity :BaseActivity(){

    private lateinit var binding: ActivityAutoSaveBinding

    override fun initContentView(): Int {
        binding = ActivityAutoSaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return R.layout.activity_auto_save
    }

    override fun initView() {
        binding.settingItemSaveSelect.isChecked = SharedManager.is04AutoSync
        binding.settingItemSaveSelect.setOnCheckedChangeListener { _, isChecked ->
            SharedManager.is04AutoSync = isChecked
        }
    }

    override fun initData() {
    }
}