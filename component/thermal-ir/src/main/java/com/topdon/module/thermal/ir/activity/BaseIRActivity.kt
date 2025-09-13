package com.topdon.module.thermal.ir.activity

import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.libcom.bean.SaveSettingBean

/**
英菲 插件式thermal imaging统一父 Activity，抽取相同逻辑到此处.
 *
 * Created by LCG on 2023/12/6.
 */
/**
 * Base i r activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
abstract class BaseIRActivity : BaseActivity() {
    /**
     * saveset开关影响的相关configuration项.
     */
    protected val saveSetBean = SaveSettingBean()
    
    override fun initContentView(): Int {
        // Default implementation - can be overridden by subclasses
        return 0 // Placeholder layout ID
    }
    
    override fun initView() {
        // Default implementation - can be overridden by subclasses
    }
    
    override fun initData() {
        // Default implementation - can be overridden by subclasses
    }
}
