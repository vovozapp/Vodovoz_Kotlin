package com.vodovoz.app.feature.questionnaires.viewholders

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderQusetionInputAnswerBinding
import com.vodovoz.app.ui.model.QuestionUI

class QuestionInputAnswerViewHolder(
    private val binding: ViewHolderQusetionInputAnswerBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.answer.doAfterTextChanged { questionUI.defaultAnswer = it.toString() }
    }

    private lateinit var questionUI: QuestionUI.InputAnswer

    fun onBind(questionUI: QuestionUI.InputAnswer) {
        this.questionUI = questionUI

        val question = StringBuilder().apply {
            append(questionUI.name)
            if(questionUI.isRequired ){
                append("*")
            }
        }.toString()
        binding.questions.text = question
        binding.answer.setText(questionUI.defaultAnswer)
    }

}