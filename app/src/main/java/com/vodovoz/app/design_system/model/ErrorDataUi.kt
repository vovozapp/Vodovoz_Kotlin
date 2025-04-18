package com.vodovoz.app.design_system.model

import com.vodovoz.app.domain.general.model.ErrorDataModel

data class ErrorDataUi(
    val title: String,
    val headerHtml: String,
    val descriptionHtml: String,
    val imageUrl: String,
    val button: ColorfulButtonUi? = null,
){
    companion object{
        val Empty = ErrorDataUi("", "","","")
    }
}

fun ErrorDataModel.toUi(): ErrorDataUi{
    return ErrorDataUi(
        title, headerHtml, descriptionHtml, imageUrl, button?.toUi()
    )
}