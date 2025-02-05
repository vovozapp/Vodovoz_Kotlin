package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.ActionWithButtonModel
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.StoryModel
import com.vodovoz.app.domain.general.model.VodovozAction
import com.vodovoz.app.util.fromHexOrNull

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
)

@Immutable
data class ColorfulButtonUi(
    val name: String,
    val backgroundColor: Color,
    val textColor: Color,
)

fun List<StoryModel>.mapToUi(): List<StoryUi> {
    return mapNotNull { storyModel -> storyModel.toUi() }
}

fun StoryModel.toUi(): StoryUi {
    return StoryUi(
        id = id,
        image = image,
        actionWithButtonList = actionWithButtonList.mapNotNull { it.toUi() }
    )
}

fun ActionWithButtonModel.toUi(): ActionWithButtonUi? {
    return ActionWithButtonUi(
        action = action,
        colorfulButton = colorfulButton.toUi() ?: return null
    )
}

fun ColorfulButtonModel.toUi(): ColorfulButtonUi? {
    return ColorfulButtonUi(
        name = name,
        backgroundColor = Color.fromHexOrNull(backgroundColor) ?: return null,
        textColor = Color.fromHexOrNull(textColor) ?: return null
    )
}
