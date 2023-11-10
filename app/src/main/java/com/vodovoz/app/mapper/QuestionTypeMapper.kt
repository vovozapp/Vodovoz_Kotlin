package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.QuestionTypeEntity
import com.vodovoz.app.ui.model.QuestionnaireTypeUI

object QuestionTypeMapper {

    fun List<QuestionTypeEntity>.mapToUI(): List<QuestionnaireTypeUI> =
        mutableListOf<QuestionnaireTypeUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun QuestionTypeEntity.mapToUI() = QuestionnaireTypeUI(
        name = name,
        type = type
    )

}