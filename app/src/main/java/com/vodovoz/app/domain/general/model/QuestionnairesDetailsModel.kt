package com.vodovoz.app.domain.general.model

data class QuestionnairesDetailsModel(
    val title: String,
    val items: List<QuestionnairesItemModel>,
    val button: ColorfulButtonModel
)

data class QuestionnairesItemModel(
    val name: String,
    val hint: String,
    val code: String,
    val type: String,
    val required: Boolean,
    val multiple: Boolean,
    val value: String,
    val values: List<String>,
    val conditions: List<ConditionModel>,
)

data class ConditionModel(
    val id: String,
    val text: String,
    val url: String
)
