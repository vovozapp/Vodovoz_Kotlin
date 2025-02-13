package com.vodovoz.app.domain.general.model

data class ProductCommentsInfoModel(
    val sorting: List<SortModel>,
    val ratingText: String,
    val commentsCount: Int,
    val commentsCountText: String,
){
    companion object{
        val Empty = ProductCommentsInfoModel(emptyList(), "", 0, "")
    }
}

data class SortModel(
    val name: String,
    val value: String,
    val order: String,
)
