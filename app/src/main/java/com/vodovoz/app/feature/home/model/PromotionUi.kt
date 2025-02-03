package com.vodovoz.app.feature.home.model

import com.vodovoz.app.domain.general.model.PromotionModel

data class PromotionUi(
    val id: Int,
    val picture: String,
)

fun PromotionModel.mapToUi(): PromotionUi {
    return PromotionUi(
        id,
        detailPicture
    )
}
