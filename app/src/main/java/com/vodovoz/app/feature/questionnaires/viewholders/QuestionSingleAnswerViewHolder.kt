package com.vodovoz.app.feature.questionnaires.viewholders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.QuestiaonariesLinkViewHolderBinding
import com.vodovoz.app.databinding.ViewHolderQuestionBinding
import com.vodovoz.app.feature.questionnaires.adapters.inner.AnswersRadioAdapter
import com.vodovoz.app.ui.model.LinkUI
import com.vodovoz.app.ui.model.QuestionUI

class QuestionSingleAnswerViewHolder(
    private val binding: ViewHolderQuestionBinding,
    private val onLinkClick: (LinkUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = AnswersRadioAdapter { answerUI ->
        questionUI.answerUI = answerUI
    }

    init {
        binding.answersRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.answersRecycler.adapter = adapter
    }

    private lateinit var questionUI: QuestionUI.SingleAnswer

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(questionUI: QuestionUI.SingleAnswer) {
        this.questionUI = questionUI

        val question = StringBuilder().apply {
            append(questionUI.name)
            if(questionUI.isRequired ){
                append("*")
            }
        }.toString()
        binding.question.text = question

        binding.linksRecycler.removeAllViews()
        for(link in questionUI.linkList)  {
            val linkBinding = QuestiaonariesLinkViewHolderBinding.inflate(
                LayoutInflater.from(binding.linksRecycler.context),
                binding.linksRecycler,
                false)
            linkBinding.root.text  = link.name
            linkBinding.root.setOnClickListener {
                onLinkClick(link)
            }

            binding.linksRecycler.addView(
                linkBinding.root
            )
        }

        adapter.answerUIList = questionUI.answerUIChoicesList
        adapter.notifyDataSetChanged()
    }

}