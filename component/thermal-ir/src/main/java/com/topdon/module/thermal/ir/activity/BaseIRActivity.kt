package com.topdon.module.thermal.ir.activity

import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.libcom.bean.SaveSettingBean

/**
 * activity activity Activity，activity.
 *
 * Created by LCG on 2023/12/6.
 */
abstract class BaseIRActivity : BaseActivity() {
    /**
     * activitySettingsactivity.
     */
    protected val saveSetBean = SaveSettingBean()
}