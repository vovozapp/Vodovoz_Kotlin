package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.QuestionEntity
import com.vodovoz.app.mapper.LinkMapper.mapToUI
import com.vodovoz.app.ui.model.AnswerUI
import com.vodovoz.app.ui.model.QuestionUI

object QuestionMapper {

    fun List<QuestionEntity>.mapToUI(): List<QuestionUI> =
        mutableListOf<QuestionUI>().also { uiList ->
            forEach { uiList.add(it.mapToUI()) }
        }

    fun QuestionEntity.mapToUI(): QuestionUI = when (this) {
        is QuestionEntity.MultiAnswer -> QuestionUI.MultiAnswer(
            name = name,
            code = code,
            isRequired = isRequired,
            answerUIChoicesList = mutableListOf<AnswerUI>().also { uiList ->
                answerList.forEach {
                    uiList.add(AnswerUI(it, false))
                }
            }.toList()
        )
        is QuestionEntity.InputAnswer -> QuestionUI.InputAnswer(
            name = name,
            code = code,
            isRequired = isRequired,
            defaultAnswer = defaultAnswer
        )
        is QuestionEntity.SingleAnswer -> QuestionUI.SingleAnswer(
            name = name,
            code = code,
            isRequired = isRequired,
            answerUIChoicesList = mutableListOf<AnswerUI>().also { uiList ->
                answerList.forEach {
                    uiList.add(AnswerUI(it, false))
                }
            }.toList(),
            linkList = linkList.mapToUI()
        )
    }
}