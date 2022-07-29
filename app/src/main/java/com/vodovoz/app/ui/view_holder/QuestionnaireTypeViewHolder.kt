package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderQuestionnaireTypeBinding
import com.vodovoz.app.ui.model.QuestionnaireTypeUI
import io.reactivex.rxjava3.subjects.PublishSubject

class QuestionnaireTypeViewHolder(
    private val binding: ViewHolderQuestionnaireTypeBinding,
    private val onQuestionnaireTypeClickSubject: PublishSubject<String>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onQuestionnaireTypeClickSubject.onNext(questionnaireTypeUI.type) }
    }

    private lateinit var questionnaireTypeUI: QuestionnaireTypeUI

    fun onBind(questionnaireTypeUI: QuestionnaireTypeUI) {
        this.questionnaireTypeUI = questionnaireTypeUI
        binding.root.text = questionnaireTypeUI.name
    }
}