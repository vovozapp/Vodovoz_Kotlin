package com.vodovoz.app.domain.general.model

data class StoryModel(
    val id: Int,
    val image: String,
    val actionWithButtonList: List<ActionWithButtonModel>,
)

data class ActionWithButtonModel(
    val action: VodovozAction,
    val colorfulButton: ColorfulButtonModel
)

data class ColorfulButtonModel(
    val name: String,
    val backgroundColor: String,
    val textColor: String,
)



