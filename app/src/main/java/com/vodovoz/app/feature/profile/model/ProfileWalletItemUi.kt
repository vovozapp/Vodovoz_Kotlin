package com.vodovoz.app.feature.profile.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.ProfileWalletItemModel
import com.vodovoz.app.domain.general.model.ProfileWalletPopupWindowModel
import com.vodovoz.app.util.fromHexOrUnspecified

@Immutable
data class ProfileWalletItemUi(
    val backgroundColor: Color,
    val title: String,
    val titleColor: Color,
    val description: String,
    val descriptionColor: Color,
    val imageUrl: String,
    val id: String,
    val popupWindow: ProfileWalletPopupWindowUi? = null,
)

fun List<ProfileWalletItemModel>.mapToUi(): List<ProfileWalletItemUi>{
    return map { it.toUi() }
}

fun ProfileWalletItemModel.toUi(): ProfileWalletItemUi {
    return ProfileWalletItemUi(
        backgroundColor = Color.fromHexOrUnspecified(background),
        title = title,
        titleColor = Color.fromHexOrUnspecified(titleColor),
        description = description,
        descriptionColor = Color.fromHexOrUnspecified(descriptionColor),
        imageUrl = imageUrl,
        id = id,
        popupWindow = popupWindow?.toUi()
    )
}

@Immutable
data class ProfileWalletPopupWindowUi(
    val title: String,
    val text: String,
)

fun ProfileWalletPopupWindowModel.toUi(): ProfileWalletPopupWindowUi {
    return ProfileWalletPopupWindowUi(
        title = title,
        text = text
    )
}