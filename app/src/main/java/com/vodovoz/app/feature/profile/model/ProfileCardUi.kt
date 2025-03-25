package com.vodovoz.app.feature.profile.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.ProfileCardModel
import com.vodovoz.app.util.fromHexOrUnspecified

@Immutable
data class ProfileCardUi(
    val title: String,
    val titleColor: Color,
    val description: String,
    val descriptionColor: Color,
    val imageUrl: String,
    val id: String,
)

fun List<ProfileCardModel>.mapToUi(): List<ProfileCardUi>{
    return map{ it.toUi() }
}

fun ProfileCardModel.toUi(): ProfileCardUi{
    return ProfileCardUi(
        title = title,
        titleColor = Color.fromHexOrUnspecified(titleColor),
        description = description,
        descriptionColor = Color.fromHexOrUnspecified(descriptionColor),
        imageUrl = imageUrl,
        id = id
    )
}