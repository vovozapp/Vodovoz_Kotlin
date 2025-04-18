package com.vodovoz.app.design_system.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.ActionWithButtonModel
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.StoryModel
import com.vodovoz.app.domain.general.model.VodovozAction
import com.vodovoz.app.util.fromHexOrUnspecified
import kotlinx.parcelize.Parcelize

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
@Parcelize
data class ColorfulButtonUi(
    val name: String,
    val backgroundColorValue: ULong,
    val textColorValue: ULong,
    val id: String = "",
    val enabled: Boolean = true,
    val loading: Boolean = false,
): Parcelable {


    val backgroundColor: Color get() = Color(backgroundColorValue)
    val textColor: Color get() = Color(textColorValue)

    companion object {
        val Empty = ColorfulButtonUi("", Color.Unspecified.value, Color.Unspecified.value)
    }
}

fun List<ColorfulButtonUi>.updateButton(
    buttonId: String,
    newButton: (ColorfulButtonUi) -> ColorfulButtonUi,
): List<ColorfulButtonUi> {
    return map { button ->
        if (button.id == buttonId) newButton(button)
        else button
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
            StoryPage(image, action.toUi(), 5_000)
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
        backgroundColorValue = Color.fromHexOrUnspecified(backgroundColor).value,
        textColorValue = Color.fromHexOrUnspecified(textColor).value,
        id = id,
    )
}

@JvmName("mapToColorfulButtonUiList")
fun List<ColorfulButtonModel>.mapToUi(): List<ColorfulButtonUi> {
    return map { it.toUi() }
}

