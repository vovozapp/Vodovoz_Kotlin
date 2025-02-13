package com.vodovoz.app.feature.product_comments.model

import com.vodovoz.app.domain.general.model.ProductCommentsInfoModel
import com.vodovoz.app.domain.general.model.SortModel


data class ProductCommentsInfoUi(
    val sorting: List<SortUi>,
    val ratingText: String,
    val commentsCount: Int,
    val commentsCountText: String,
) {
    companion object {
        val Empty = ProductCommentsInfoUi(emptyList(), "", 0, "")
    }
}

fun ProductCommentsInfoModel.toUi(): ProductCommentsInfoUi {
    return ProductCommentsInfoUi(
        sorting = sorting.map { it.toUi() },
        ratingText = ratingText,
        commentsCount = commentsCount,
        commentsCountText = commentsCountText
    )
}

fun ProductCommentsInfoUi.toDomain(): ProductCommentsInfoModel {
    return ProductCommentsInfoModel(
        sorting = sorting.map { sort -> sort.toDomain() },
        ratingText = ratingText,
        commentsCount = commentsCount,
        commentsCountText = commentsCountText
    )
}

data class SortUi(
    val name: String,
    val value: String,
    val order: String,
) {
    companion object {
        val Empty = SortUi("", "", "")
    }
}

fun SortModel.toUi(): SortUi {
    return SortUi(
        name = name,
        value = value,
        order = order
    )
}

fun SortUi.toDomain(): SortModel {
    return SortModel(
        name = name,
        value = value,
        order = order
    )
}