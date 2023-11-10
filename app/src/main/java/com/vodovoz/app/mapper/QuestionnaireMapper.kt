package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.QuestionnaireEntity
import com.vodovoz.app.mapper.QuestionMapper.mapToUI
import com.vodovoz.app.mapper.QuestionTypeMapper.mapToUI
import com.vodovoz.app.ui.model.QuestionnaireUI

object QuestionnaireMapper {

    fun QuestionnaireEntity.mapToUI(): QuestionnaireUI = QuestionnaireUI(
        questionUiTypeList = questionTypeList.mapToUI(),
        questionUiList = questionList.mapToUI()
    )
}