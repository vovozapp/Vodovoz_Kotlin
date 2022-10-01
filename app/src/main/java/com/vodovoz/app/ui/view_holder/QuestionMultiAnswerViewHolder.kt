package com.vodovoz.app.ui.view_holder

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderQuestionBinding
import com.vodovoz.app.ui.adapter.AnswersCheckAdapter
import com.vodovoz.app.ui.model.QuestionUI

class QuestionMultiAnswerViewHolder(
    private val binding: ViewHolderQuestionBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = AnswersCheckAdapter { answerUI ->
        when(answerUI.isSelected) {
            true -> questionUI.answerUIList.remove(answerUI)
            false -> questionUI.answerUIList.add(answerUI)
        }
    }

    init {
        binding.answersRecycler.layoutManager = LinearLayoutManager(context)
        binding.answersRecycler.adapter = adapter
    }

    private lateinit var questionUI: QuestionUI.MultiAnswer

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(questionUI: QuestionUI.MultiAnswer) {
        this.questionUI = questionUI

        binding.question.text = questionUI.name

        adapter.answerUIList = questionUI.answerUIChoicesList
        adapter.notifyDataSetChanged()
    }

}