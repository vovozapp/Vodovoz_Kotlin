package com.vodovoz.app.feature.questionnaires.adapter

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderQuestionnaireTypeBinding
import com.vodovoz.app.ui.model.QuestionnaireTypeUI

class QuestionnaireTypeFlowViewHolder(
    private val binding: ViewHolderQuestionnaireTypeBinding,
    private val onQuestionnaireTypeClickListener: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onQuestionnaireTypeClickListener(questionnaireTypeUI.type) }
    }

    private lateinit var questionnaireTypeUI: QuestionnaireTypeUI

    fun onBind(questionnaireTypeUI: QuestionnaireTypeUI) {
        this.questionnaireTypeUI = questionnaireTypeUI
        binding.root.text = questionnaireTypeUI.name
    }
}