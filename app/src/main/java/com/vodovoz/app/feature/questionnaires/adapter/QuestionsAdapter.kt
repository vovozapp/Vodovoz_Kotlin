package com.vodovoz.app.feature.questionnaires.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderQuestionBinding
import com.vodovoz.app.databinding.ViewHolderQusetionInputAnswerBinding
import com.vodovoz.app.ui.model.QuestionUI

class QuestionsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val QUESTION_INPUT_ANSWER = 0
        private const val QUESTION_SINGLE_ANSWER = 1
        private const val QUESTION_MULTI_ANSWER = 2
    }

    var questionUIList = listOf<QuestionUI>()

    override fun getItemViewType(
        position: Int
    ) = when(questionUIList[position]) {
        is QuestionUI.InputAnswer -> QUESTION_INPUT_ANSWER
        is QuestionUI.SingleAnswer -> QUESTION_SINGLE_ANSWER
        is QuestionUI.MultiAnswer -> QUESTION_MULTI_ANSWER
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when(viewType) {
        QUESTION_INPUT_ANSWER -> QuestionInputAnswerViewHolder(
            binding = ViewHolderQusetionInputAnswerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        QUESTION_MULTI_ANSWER -> QuestionMultiAnswerViewHolder(
            binding = ViewHolderQuestionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context = parent.context
        )
        QUESTION_SINGLE_ANSWER -> QuestionSingleAnswerViewHolder(
            binding = ViewHolderQuestionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context = parent.context
        )
        else -> throw Exception("Unknown type")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) = when(val question = questionUIList[position]) {
        is QuestionUI.InputAnswer -> (holder as QuestionInputAnswerViewHolder).onBind(question)
        is QuestionUI.SingleAnswer -> (holder as QuestionSingleAnswerViewHolder).onBind(question)
        is QuestionUI.MultiAnswer -> (holder as QuestionMultiAnswerViewHolder).onBind(question)
    }

    override fun getItemCount() = questionUIList.size

}