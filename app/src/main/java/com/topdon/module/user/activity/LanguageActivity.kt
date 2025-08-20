package com.topdon.module.user.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.tools.ConstantLanguages
import com.topdon.tc001.R
import com.topdon.module.user.adapter.LanguageAdapter
import kotlinx.android.synthetic.main.activity_language.*

class LanguageActivity : BaseActivity() {

    private val adapter by lazy { LanguageAdapter(this) }

    private var selectIndex = 0

    override fun initContentView() = R.layout.activity_language

    override fun initView() {
        title_view.setRightClickListener {//保存
            val localeStr = ConstantLanguages.ENGLISH
            setResult(RESULT_OK, Intent().also { it.putExtra("localeStr", localeStr) })
            finish()
        }

        language_recycler.layoutManager = LinearLayoutManager(this)
        language_recycler.adapter = adapter
        adapter.listener = object : LanguageAdapter.ItemOnClickListener {
            override fun onClick(position: Int) {
                adapter.setSelect(position)
                selectIndex = position
            }
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        showLanguage()
    }

    private fun showLanguage() {
        val selectIndex = 0 // Always English
        adapter.setSelect(selectIndex)
        this.selectIndex = selectIndex
    }

}

