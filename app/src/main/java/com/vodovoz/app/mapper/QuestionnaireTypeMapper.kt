package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.QuestionnaireTypeEntity
import com.vodovoz.app.ui.model.QuestionnaireTypeUI

object QuestionnaireTypeMapper {

    fun List<QuestionnaireTypeEntity>.mapToUI(): List<QuestionnaireTypeUI> =
        mutableListOf<QuestionnaireTypeUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun QuestionnaireTypeEntity.mapToUI() = QuestionnaireTypeUI(
        name = name,
        type = type
    )

}