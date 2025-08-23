package com.topdon.module.user.activity

import android.os.Bundle
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.common.FeedBackBean
import com.topdon.module.user.R

/**
 * Feedback activity for user feedback functionality
 * Simplified version for local mode
 */
class FeedbackActivity : BaseActivity() {

    companion object {
        const val FEEDBACKBEAN = "feedbackBean"
    }

    override fun initContentView(): Int = R.layout.activity_feedback

    override fun initView() {
        // Initialize feedback UI - simplified
    }

    override fun initData() {
        val feedbackBean = intent.getSerializableExtra(FEEDBACKBEAN) as? FeedBackBean
        // Handle feedback data - simplified for local mode
    }
}