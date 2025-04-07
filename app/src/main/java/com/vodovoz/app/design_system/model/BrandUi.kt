package com.vodovoz.app.design_system.model

import com.vodovoz.app.domain.general.model.BrandModel

data class BrandUi(
    val name: String,
    val id: Long,
    val picture: String,
    val url: String
)

fun BrandModel.toUi(): BrandUi{
    return BrandUi(
        name = name,
        id = id,
        picture = picture,
        url = pageUrl
    )
}
