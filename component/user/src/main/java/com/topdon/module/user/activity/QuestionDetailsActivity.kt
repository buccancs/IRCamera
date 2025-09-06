package com.topdon.module.user.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R
import com.topdon.module.user.databinding.ActivityQuestionDetailsBinding

/**
 * FAQ - 一项 FAQ 详情
 */
@Route(path = RouterConfig.QUESTION_DETAILS)
class QuestionDetailsActivity : BaseActivity() {
    
    private lateinit var binding: ActivityQuestionDetailsBinding

    override fun initContentView() = R.layout.activity_question_details

    override fun initView() {
        binding = ActivityQuestionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.questionDetailsTitle.text = intent.getStringExtra("question")
        binding.questionDetailsContent.text = intent.getStringExtra("answer")
    }

    override fun initData() {

    }

}