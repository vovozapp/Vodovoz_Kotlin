package com.vodovoz.app.data.model.common

class QuestionnaireEntity (
    val questionTypeList: List<QuestionTypeEntity> = listOf(),
    val questionList: List<QuestionEntity> = listOf()
)