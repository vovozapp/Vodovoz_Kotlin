package com.vodovoz.app.data.model.common

import java.io.Serializable

data class BannerEntity(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val actionEntity: ActionEntity? = null
): Serializable