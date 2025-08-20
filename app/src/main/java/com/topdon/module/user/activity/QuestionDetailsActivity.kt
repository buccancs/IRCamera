package com.topdon.module.user.activity

import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.tc001.R
import kotlinx.android.synthetic.main.activity_question_details.*

class QuestionDetailsActivity : BaseActivity() {

    override fun initContentView() = R.layout.activity_question_details

    override fun initView() {
        question_details_title.text = intent.getStringExtra("question")
        question_details_content.text = intent.getStringExtra("answer")
    }

    override fun initData() {

    }

}