package com.vodovoz.app.domain.general.model

data class QuestionnairesWelcomeDetailsModel(
    val title: String,
    val image: String,
    val header: String,
    val description: String,
    val buttons: List<ColorfulButtonModel>
)
