package com.vodovoz.app.feature.profile.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.ProfileChatItemModel
import com.vodovoz.app.domain.general.model.ProfileChatsPopupWindowModel
import com.vodovoz.app.domain.general.model.ProfileMenuItemModel


@Immutable
data class ProfileMenuItemUi(
    val text: String,
    val imageUrl: String,
    val id: String,
    val description: String,
    val popupWindow: ProfileChatsPopupWindowUi? = null,
)

@JvmName("mapToProfileMenuItemUiList")
fun List<ProfileMenuItemModel>.mapToUi(): List<ProfileMenuItemUi> {
    return map { it.toUi() }
}

fun ProfileMenuItemModel.toUi(): ProfileMenuItemUi {
    return ProfileMenuItemUi(
        text = text,
        imageUrl = imageUrl,
        id = id,
        description = description,
        popupWindow = popupWindow?.toUi()
    )
}

@Immutable
data class ProfileChatsPopupWindowUi(
    val title: String,
    val description: String,
    val menu: List<ProfileChatItemUi>,
)

fun ProfileChatsPopupWindowModel.toUi(): ProfileChatsPopupWindowUi {
    return ProfileChatsPopupWindowUi(
        title = title,
        description = description,
        menu = menu.mapToUi()
    )
}

data class ProfileChatItemUi(
    val name: String,
    val imageUrl: String,
    val navigationData: String,
)

fun List<ProfileChatItemModel>.mapToUi(): List<ProfileChatItemUi> {
    return mapNotNull { it.toUi() }
}

fun ProfileChatItemModel.toUi(): ProfileChatItemUi {
    return ProfileChatItemUi(
        name = name,
        imageUrl = imageUrl,
        navigationData = transitionData
    )
}


