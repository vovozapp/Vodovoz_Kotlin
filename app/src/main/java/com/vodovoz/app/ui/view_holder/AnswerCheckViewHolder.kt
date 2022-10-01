package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAnswerCheckBinding
import com.vodovoz.app.ui.model.AnswerUI

class AnswerCheckViewHolder(
    private val binding: ViewHolderAnswerCheckBinding,
    private val onSelectAnswer: (AnswerUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.check.setOnCheckedChangeListener { _, isChecked ->
            answerUI.isSelected = isChecked
            onSelectAnswer(answerUI)
        }
    }

    private lateinit var answerUI: AnswerUI

    fun onBind(answerUI: AnswerUI) {
        this.answerUI = answerUI
        binding.answer.text = answerUI.text
        binding.check.isChecked = answerUI.isSelected
    }

}