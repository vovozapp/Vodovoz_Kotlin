package com.vodovoz.app.data.model.common

sealed class QuestionEntity(
    val name: String = "",
    val code: String = "",
    val isRequired: Boolean = true,
) {
    class InputAnswer(
        name: String,
        code: String,
        isRequired: Boolean,
        val defaultAnswer: String,
    ) : QuestionEntity(
        name = name,
        code = code,
        isRequired = isRequired
    )

    class SingleAnswer(
        name: String,
        code: String,
        isRequired: Boolean,
        val answerList: List<String>,
        val linkList: List<LinkEntity>,
    ) : QuestionEntity(
        name = name,
        code = code,
        isRequired = isRequired
    )

    class MultiAnswer(
        name: String,
        code: String,
        isRequired: Boolean,
        val linkList: List<LinkEntity>,
        val answerList: List<String>,
    ) : QuestionEntity(
        name = name,
        code = code,
        isRequired = isRequired
    )
}
