package com.topdon.module.thermal.ir.activity

import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.libcom.bean.SaveSettingBean

/**
\1 thermal imaging Activity，.
 *
 * Created by LCG on 2023/12/6.
 */
/**
 * Base i r activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
abstract class BaseIRActivity : BaseActivity() {
    /**
\1savesetconfiguration.
     */
    protected val saveSetBean = SaveSettingBean()
}
