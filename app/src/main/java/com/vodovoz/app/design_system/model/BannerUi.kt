package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.BannerModel
import com.vodovoz.app.domain.general.model.VodovozAction

@Immutable
data class BannerUi(
    val id: Int,
    val name: String,
    val detailPicture: String,
    val action: VodovozAction,
    val advertising: AboutAdvertisingUi?,
)

fun List<BannerModel>.mapToUi(): List<BannerUi> {
    return map { it.toUi() }
}

fun BannerModel.toUi(): BannerUi {
    return BannerUi(
        id, name, detailPicture, action, advertising?.toUi()
    )
}