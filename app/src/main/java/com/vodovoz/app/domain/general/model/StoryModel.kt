package com.vodovoz.app.domain.general.model

data class StoryModel(
    val id: Long,
    val image: String,
    val actionWithButtonList: List<ActionWithButtonModel>,
    val viewed: Boolean
)

data class ActionWithButtonModel(
    val action: VodovozAction,
    val colorfulButton: ColorfulButtonModel
)

data class ColorfulButtonModel(
    val name: String,
    val backgroundColor: String,
    val textColor: String,
    val id: String = ""
){
    companion object{
        val Empty = ColorfulButtonModel("","", "", "")
    }
}



