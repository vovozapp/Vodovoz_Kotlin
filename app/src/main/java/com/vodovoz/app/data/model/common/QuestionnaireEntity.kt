package com.vodovoz.app.data.model.common

class QuestionnaireEntity (
    val message: String = "",
    val questionTypeList: List<QuestionTypeEntity> = listOf(),
    val questionList: List<QuestionEntity> = listOf()
)