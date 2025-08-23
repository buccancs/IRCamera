package com.topdon.module.user.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R
import com.topdon.module.user.model.QuestionData
import com.topdon.module.user.model.FaqRepository
import java.util.ArrayList

/**
 * FAQ
 */
class QuestionActivity : BaseActivity() {

    private lateinit var questionRecycler: RecyclerView

    override fun initContentView() = R.layout.activity_question

    override fun initView() {
        questionRecycler = findViewById(R.id.question_recycler)
        
        val adapter = MyAdapter(FaqRepository.getQuestionList(intent.getBooleanExtra("isTS001", false)))
        adapter.onItemClickListener = {
            // TODO: Replace ARouter navigation - Intent
            // TODO: Replace RouterConfig reference with direct navigation
// TODO_FIX_AROUTER:                 .withString("question", it.question)
// TODO_FIX_AROUTER:                 .withString("answer", it.answer)
// TODO_FIX_AROUTER:                 .navigation(this)
        }

        questionRecycler.layoutManager = LinearLayoutManager(this)
        questionRecycler.adapter = adapter
    }

    override fun initData() {

    }


    private class MyAdapter(private val questionList: ArrayList<QuestionData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var onItemClickListener: ((data: QuestionData) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false))
        }

        override fun getItemCount(): Int = questionList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemHolder) {
                val questionInfo = holder.rootView.findViewById<TextView>(R.id.item_question_info)
                val questionLay = holder.rootView.findViewById<ConstraintLayout>(R.id.item_question_lay)
                
                questionInfo.text = questionList[position].question
                questionLay.setOnClickListener {
                    onItemClickListener?.invoke(questionList[position])
                }
            }
        }

        private class ItemHolder(val rootView: View) : RecyclerView.ViewHolder(rootView)
    }
}