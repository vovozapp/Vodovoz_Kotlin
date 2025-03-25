package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.TextButtonModel
import com.vodovoz.app.util.fromHexOrUnspecified

@Immutable
data class TextButtonUi(
    val text: String,
    val textColor: Color
){
    companion object{
        val Empty = TextButtonUi("",Color.Unspecified)
    }
}

fun TextButtonModel.toUi(): TextButtonUi{
    return TextButtonUi(
        text = text,
        textColor = Color.fromHexOrUnspecified(textColor)
    )
}
