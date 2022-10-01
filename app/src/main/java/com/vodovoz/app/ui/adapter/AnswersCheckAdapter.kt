package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAnswerCheckBinding
import com.vodovoz.app.ui.model.AnswerUI
import com.vodovoz.app.ui.view_holder.AnswerCheckViewHolder

class AnswersCheckAdapter(
    private val onSelectAnswer: (AnswerUI) -> Unit
) : RecyclerView.Adapter<AnswerCheckViewHolder>() {

    var answerUIList = listOf<AnswerUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AnswerCheckViewHolder(
        binding = ViewHolderAnswerCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onSelectAnswer = onSelectAnswer
    )

    override fun onBindViewHolder(
        holder: AnswerCheckViewHolder,
        position: Int
    ) = holder.onBind(answerUIList[position])

    override fun getItemCount() = answerUIList.size

}