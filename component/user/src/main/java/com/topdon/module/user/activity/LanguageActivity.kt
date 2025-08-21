package com.topdon.module.user.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.tools.ConstantLanguages
import com.topdon.module.user.R
import com.topdon.module.user.adapter.LanguageAdapter
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.view.BaseTitleView

class LanguageActivity : BaseActivity() {

    private val adapter by lazy { LanguageAdapter(this) }

    private var selectIndex = 0
    
    private lateinit var titleView: BaseTitleView
    private lateinit var languageRecycler: RecyclerView

    override fun initContentView() = R.layout.activity_language

    override fun initView() {
        titleView = findViewById(R.id.title_view)
        languageRecycler = findViewById(R.id.language_recycler)
        
        titleView.setRightClickListener {//保存
            val localeStr = ConstantLanguages.ENGLISH
            setResult(RESULT_OK, Intent().also { it.putExtra("localeStr", localeStr) })
            finish()
        }

        languageRecycler.layoutManager = LinearLayoutManager(this)
        languageRecycler.adapter = adapter
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
        val selectIndex = 0  // Always English
        adapter.setSelect(selectIndex)
        this.selectIndex = selectIndex
    }

}

