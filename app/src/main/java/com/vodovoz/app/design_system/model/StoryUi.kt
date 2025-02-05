package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.ActionWithButtonModel
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.StoryModel
import com.vodovoz.app.domain.general.model.VodovozAction
import com.vodovoz.app.util.fromHexOrTransparent

@Immutable
data class StoryUi(
    val id: Int,
    val image: String,
    val actionWithButtonList: List<ActionWithButtonUi>,
)


@Immutable
data class ActionWithButtonUi(
    val action: VodovozAction,
    val colorfulButton: ColorfulButtonUi,
) {
    companion object {
        val Empty = ActionWithButtonUi(VodovozAction.Unknown("", ""), ColorfulButtonUi.Empty)
    }
}

@Immutable
data class ColorfulButtonUi(
    val name: String,
    val backgroundColor: Color,
    val textColor: Color,
) {
    companion object {
        val Empty = ColorfulButtonUi("", Color.Transparent, Color.Transparent)
    }
}

fun List<StoryModel>.mapToUi(): List<StoryUi> {
    return mapNotNull { storyModel -> storyModel.toUi() }
}

fun StoryModel.toUi(): StoryUi {
    return StoryUi(
        id = id,
        image = image,
        actionWithButtonList = actionWithButtonList.map { it.toUi() }
    )
}

fun ActionWithButtonModel.toUi(): ActionWithButtonUi {
    return ActionWithButtonUi(
        action = action,
        colorfulButton = colorfulButton.toUi()
    )
}

fun ColorfulButtonModel.toUi(): ColorfulButtonUi {
    return ColorfulButtonUi(
        name = name,
        backgroundColor = Color.fromHexOrTransparent(backgroundColor),
        textColor = Color.fromHexOrTransparent(textColor)
    )
}
