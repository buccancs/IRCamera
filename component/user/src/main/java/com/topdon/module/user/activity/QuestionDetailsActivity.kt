package com.topdon.module.user.activity

import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R

/**
 * FAQ - 一项 FAQ 详情
 */
// Legacy ARouter route annotation - now using NavigationManager
class QuestionDetailsActivity : BaseActivity() {

    override fun initContentView() = R.layout.activity_question_details

    override fun initView() {
        question_details_title.text = intent.getStringExtra("question")
        question_details_content.text = intent.getStringExtra("answer")
    }

    override fun initData() {

    }

}