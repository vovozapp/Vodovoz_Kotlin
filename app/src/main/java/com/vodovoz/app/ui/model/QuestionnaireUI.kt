package com.vodovoz.app.ui.model

class QuestionnaireUI(
    val message: String = "",
    val questionUiTypeList: List<QuestionnaireTypeUI> = listOf(),
    val questionUiList: List<QuestionUI> = listOf(),
)