package com.vodovoz.app.data.model.common

data class SearchQueryHeaderResponse(
    val category: CategoryEntity = CategoryEntity(name = ""),
    val deepLink: String = "",
    val id: String = "",
)
