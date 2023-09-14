package com.vodovoz.app.feature.questionnaires.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderQuestionnaireTypeBinding
import com.vodovoz.app.ui.model.QuestionnaireTypeUI

class QuestionnaireTypesFlowAdapter(
    private val onQuestionnaireTypeClickListener: (String) -> Unit
) : RecyclerView.Adapter<QuestionnaireTypeFlowViewHolder>() {

    var questionnaireTypeList = listOf<QuestionnaireTypeUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = QuestionnaireTypeFlowViewHolder(
        binding = ViewHolderQuestionnaireTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false),
        onQuestionnaireTypeClickListener = onQuestionnaireTypeClickListener
    )

    override fun onBindViewHolder(
        holder: QuestionnaireTypeFlowViewHolder,
        position: Int
    ) = holder.onBind(questionnaireTypeList[position])

    override fun getItemCount() = questionnaireTypeList.size

}