package com.topdon.module.user.model

import com.blankj.utilcode.util.Utils
import com.topdon.module.user.R
import com.csl.irCamera.libapp.R as LibAppR

object FaqRepository {

    /**
     * Function description.
     */
    fun getQuestionList(isTS001: Boolean): ArrayList<QuestionData> = if (isTS001) arrayListOf(
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question1),
            answer = Utils.getApp().getString(LibAppR.string.answer1)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question2),
            answer = Utils.getApp().getString(LibAppR.string.answer2)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question3),
            answer = Utils.getApp().getString(LibAppR.string.answer3)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question4),
            answer = Utils.getApp().getString(LibAppR.string.answer4)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question5),
            answer = Utils.getApp().getString(LibAppR.string.answer5)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question6),
            answer = Utils.getApp().getString(LibAppR.string.answer6)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question7),
            answer = Utils.getApp().getString(LibAppR.string.answer7)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.question8),
            answer = Utils.getApp().getString(LibAppR.string.answer8)
        )
    ) else arrayListOf(
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.ts004_faq_q1),
            answer = Utils.getApp().getString(LibAppR.string.ts004_faq_a1)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.ts004_faq_q2),
            answer = Utils.getApp().getString(LibAppR.string.ts004_faq_a2)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.ts004_faq_q3),
            answer = Utils.getApp().getString(LibAppR.string.ts004_faq_a3)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.ts004_faq_q4),
            answer = Utils.getApp().getString(LibAppR.string.ts004_faq_a4)
        ),
        QuestionData(
            question = Utils.getApp().getString(LibAppR.string.ts004_faq_q5),
            answer = Utils.getApp().getString(LibAppR.string.ts004_faq_a5)
        ),
    )
}

data class QuestionData(
    /** question property */
    val question: String,
    /** answer property */
    val answer: String
)