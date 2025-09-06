package com.topdon.module.user.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R
import com.topdon.module.user.model.QuestionData
import com.topdon.module.user.model.FaqRepository
import com.topdon.module.user.databinding.ActivityQuestionBinding
import com.topdon.module.user.databinding.ItemQuestionBinding
import java.util.ArrayList

/**
 * FAQ
 */
@Route(path = RouterConfig.QUESTION)
class QuestionActivity : BaseActivity() {
    
    private lateinit var binding: ActivityQuestionBinding

    override fun initContentView() = R.layout.activity_question

    override fun initView() {
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val adapter = MyAdapter(FaqRepository.getQuestionList(intent.getBooleanExtra("isTS001", false)))
        adapter.onItemClickListener = {
            ARouter.getInstance()
                .build(RouterConfig.QUESTION_DETAILS)
                .withString("question", it.question)
                .withString("answer", it.answer)
                .navigation(this)
        }

        binding.questionRecycler.layoutManager = LinearLayoutManager(this)
        binding.questionRecycler.adapter = adapter
    }

    override fun initData() {

    }


    private class MyAdapter(private val questionList: ArrayList<QuestionData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var onItemClickListener: ((data: QuestionData) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemHolder(binding)
        }

        override fun getItemCount(): Int = questionList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemHolder) {
                holder.binding.itemQuestionInfo.text = questionList[position].question
                holder.binding.itemQuestionLay.setOnClickListener {
                    onItemClickListener?.invoke(questionList[position])
                }
            }
        }

        private class ItemHolder(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root)
    }
}