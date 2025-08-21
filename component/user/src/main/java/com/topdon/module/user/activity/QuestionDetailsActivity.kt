package com.topdon.module.user.activity

import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R
import android.widget.TextView

/**
 * FAQ - 一项 FAQ 详情
 */
class QuestionDetailsActivity : BaseActivity() {

    private lateinit var questionDetailsTitle: TextView
    private lateinit var questionDetailsContent: TextView

    override fun initContentView() = R.layout.activity_question_details

    override fun initView() {
        questionDetailsTitle = findViewById(R.id.question_details_title)
        questionDetailsContent = findViewById(R.id.question_details_content)
        
        questionDetailsTitle.text = intent.getStringExtra("question")
        questionDetailsContent.text = intent.getStringExtra("answer")
    }

    override fun initData() {

    }

}