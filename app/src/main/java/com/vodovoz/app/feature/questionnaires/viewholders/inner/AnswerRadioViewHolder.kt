package com.vodovoz.app.feature.questionnaires.viewholders.inner

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAnswerRadioBinding
import com.vodovoz.app.ui.model.AnswerUI

class AnswerRadioViewHolder(
    private val binding: ViewHolderAnswerRadioBinding,
    private val onSelectAnswer: (AnswerUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.radio.setOnCheckedChangeListener { _, isChecked ->
            if (!answerUI.isSelected && isChecked) {
                answerUI.isSelected = isChecked
                onSelectAnswer(answerUI)
            }
        }
    }

    private lateinit var answerUI: AnswerUI

    fun onBind(answerUI: AnswerUI) {
        this.answerUI = answerUI
        binding.answer.text = answerUI.text
        binding.radio.isChecked = answerUI.isSelected
    }

}