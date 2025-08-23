package com.topdon.module.user.activity

import android.content.Intent
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.ConstantLanguages
import com.topdon.module.user.R
import com.topdon.lib.core.view.BaseTitleView

class LanguageActivity : BaseActivity() {
    
    private lateinit var titleView: BaseTitleView

    override fun initContentView() = R.layout.activity_language

    override fun initView() {
        titleView = findViewById(R.id.title_view)
        
        titleView.setRightClickListener {
            // Always return English - no language selection needed
            val localeStr = ConstantLanguages.ENGLISH
            setResult(RESULT_OK, Intent().also { it.putExtra("localeStr", localeStr) })
            finish()
        }
    }

    override fun initData() {

    }

}

