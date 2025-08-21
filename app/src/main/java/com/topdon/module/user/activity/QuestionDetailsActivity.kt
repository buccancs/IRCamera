package com.topdon.module.user.activity

import android.widget.TextView
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.tc001.R

class QuestionDetailsActivity : BaseActivity() {

    override fun initContentView() = R.layout.activity_question_details

    override fun initView() {
        val questionDetailsTitle = findViewById<TextView>(R.id.question_details_title)
        val questionDetailsContent = findViewById<TextView>(R.id.question_details_content)
        questionDetailsTitle.text = intent.getStringExtra("question")
        questionDetailsContent.text = intent.getStringExtra("answer")
    }

    override fun initData() {

    }

}