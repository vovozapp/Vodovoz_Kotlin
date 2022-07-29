package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderQuestionnaireTypeBinding
import com.vodovoz.app.ui.model.QuestionnaireTypeUI
import com.vodovoz.app.ui.view_holder.QuestionnaireTypeViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class QuestionnaireTypesAdapter(
    private val onQuestionnaireTypeClickSubject: PublishSubject<String>
) : RecyclerView.Adapter<QuestionnaireTypeViewHolder>() {

    var questionnaireTypeList = listOf<QuestionnaireTypeUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = QuestionnaireTypeViewHolder(
        binding = ViewHolderQuestionnaireTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false),
        onQuestionnaireTypeClickSubject = onQuestionnaireTypeClickSubject
    )

    override fun onBindViewHolder(
        holder: QuestionnaireTypeViewHolder,
        position: Int
    ) = holder.onBind(questionnaireTypeList[position])

    override fun getItemCount() = questionnaireTypeList.size

}