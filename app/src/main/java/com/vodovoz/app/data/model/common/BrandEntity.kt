package com.vodovoz.app.data.model.common

data class BrandEntity(
    val id: Long,
    val name: String,
    var detailPicture: String,
    var hasList: Boolean
)