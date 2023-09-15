package com.vodovoz.app.feature.questionnaires.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAnswerRadioBinding
import com.vodovoz.app.ui.model.AnswerUI

class AnswersRadioAdapter(
    private val onSelectAnswer: (AnswerUI) -> Unit
) : RecyclerView.Adapter<AnswerRadioViewHolder>() {

    var answerUIList = listOf<AnswerUI>()
    var lastSelectedAnswer: AnswerUI? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AnswerRadioViewHolder(
        binding = ViewHolderAnswerRadioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) { answerUI ->
        lastSelectedAnswer?.isSelected = false
        if (answerUIList.contains(lastSelectedAnswer)) {
            notifyItemChanged(answerUIList.indexOf(lastSelectedAnswer))
        }
        notifyItemChanged(answerUIList.indexOf(answerUI))
        lastSelectedAnswer = answerUI
        onSelectAnswer(answerUI)
    }

    override fun onBindViewHolder(
        holder: AnswerRadioViewHolder,
        position: Int
    ) = holder.onBind(answerUIList[position])

    override fun getItemCount() = answerUIList.size

}