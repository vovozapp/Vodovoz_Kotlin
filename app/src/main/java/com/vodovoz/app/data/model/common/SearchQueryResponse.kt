package com.vodovoz.app.data.model.common

data class SearchQueryResponse(
    val productList: List<ProductEntity> = listOf(),
    val deepLink: String = "",
    val id: String = "",
)
