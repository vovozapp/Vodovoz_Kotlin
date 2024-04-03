package com.vodovoz.app.data.model.common

data class BrandsMainEntity(
    val name: String,
    val brandsList: List<BrandEntity> = listOf(),
)
