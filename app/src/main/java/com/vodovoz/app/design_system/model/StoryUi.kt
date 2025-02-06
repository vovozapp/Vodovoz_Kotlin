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
    val id: Long,
    val image: String,
    val pages: List<StoryPage>,
    val viewed: Boolean,
)

@Immutable
data class StoryPage(
    val image: String,
    val actionWithButton: ActionWithButtonUi,
    val durationMillis: Int,
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
        pages = actionWithButtonList.map { action ->
            StoryPage(image, action.toUi(), 6_000)
        },
        viewed = viewed
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
