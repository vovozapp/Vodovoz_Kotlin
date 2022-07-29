package com.vodovoz.app.ui.view_holder

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentQuestionnairesBinding
import com.vodovoz.app.databinding.ViewHolderQuestionBinding
import com.vodovoz.app.databinding.ViewHolderQusetionInputAnswerBinding
import com.vodovoz.app.ui.adapter.AnswersCheckAdapter
import com.vodovoz.app.ui.adapter.AnswersRadioAdapter
import com.vodovoz.app.ui.model.QuestionUI

class QuestionSingleAnswerViewHolder(
    private val binding: ViewHolderQuestionBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = AnswersRadioAdapter { answerUI ->
        questionUI.answerUI = answerUI
    }

    init {
        binding.answersRecycler.layoutManager = LinearLayoutManager(context)
        binding.answersRecycler.adapter = adapter
    }

    private lateinit var questionUI: QuestionUI.SingleAnswer

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(questionUI: QuestionUI.SingleAnswer) {
        this.questionUI = questionUI

        binding.question.text = questionUI.name

        adapter.answerUIList = questionUI.answerUIChoicesList
        adapter.notifyDataSetChanged()
    }

}