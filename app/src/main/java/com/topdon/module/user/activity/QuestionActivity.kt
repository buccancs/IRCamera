package com.topdon.module.user.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.tc001.R
import com.topdon.module.user.model.QuestionData
import com.topdon.module.user.model.FaqRepository
import java.util.ArrayList

class QuestionActivity : BaseActivity() {

    override fun initContentView() = R.layout.activity_question

    override fun initView() {
        val questionRecycler = findViewById<RecyclerView>(R.id.question_recycler)
        val adapter = MyAdapter(FaqRepository.getQuestionList(intent.getBooleanExtra("isTS001", false)))
        adapter.onItemClickListener = {
            val intent = Intent(this, com.topdon.module.user.activity.QuestionDetailsActivity::class.java)
            intent.putExtra("question", it.question)
            intent.putExtra("answer", it.answer)
            startActivity(intent)
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
                val itemQuestionInfo = holder.rootView.findViewById<TextView>(R.id.item_question_info)
                val itemQuestionLay = holder.rootView.findViewById<View>(R.id.item_question_lay)
                itemQuestionInfo.text = questionList[position].question
                itemQuestionLay.setOnClickListener {
                    onItemClickListener?.invoke(questionList[position])
                }
            }
        }

        private class ItemHolder(val rootView: View) : RecyclerView.ViewHolder(rootView)
    }
}