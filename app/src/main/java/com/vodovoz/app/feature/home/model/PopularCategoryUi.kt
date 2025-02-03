package com.vodovoz.app.feature.home.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.PopularCategoryModel

@Immutable
data class PopularCategoryUi(
    val image: String,
    val name: String,
    val id: Long,
)

fun PopularCategoryModel.mapToUi(): PopularCategoryUi {
    return PopularCategoryUi(
        image = picture,
        name = name,
        id = id
    )
}