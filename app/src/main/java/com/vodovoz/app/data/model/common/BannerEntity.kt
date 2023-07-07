package com.vodovoz.app.data.model.common

import com.vodovoz.app.feature.home.viewholders.homebanners.model.BannerAdvEntity
import java.io.Serializable

data class BannerEntity(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val actionEntity: ActionEntity? = null,
    val bannerAdvEntity: BannerAdvEntity? = null
): Serializable