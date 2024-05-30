package com.vodovoz.app.ui.model

sealed class QuestionUI(
    val name: String = "",
    val code: String = "",
    val isRequired: Boolean = true
) {
    class InputAnswer(
        name: String,
        code: String,
        isRequired: Boolean,
        var defaultAnswer: String
    ) : QuestionUI(
        name = name,
        code = code,
        isRequired = isRequired
    )
    class SingleAnswer(
        name: String,
        code: String,
        isRequired: Boolean,
        val answerUIChoicesList: List<AnswerUI>,
        val linkList: List<LinkUI>,
        var answerUI: AnswerUI? = null
    ) : QuestionUI(
        name = name,
        code = code,
        isRequired = isRequired
    )
    class MultiAnswer(
        name: String,
        code: String,
        isRequired: Boolean,
        val linkList: List<LinkUI>,
        val answerUIChoicesList: List<AnswerUI>,
        val answerUIList: MutableList<AnswerUI> = mutableListOf()
    ) : QuestionUI(
        name = name,
        code = code,
        isRequired = isRequired
    )
}
