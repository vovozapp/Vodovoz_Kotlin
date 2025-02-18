package com.vodovoz.app.feature.categories.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.feature.home.model.PopularCategoryUi

@Immutable
data class CategoriesState(
    val currentCategory: PopularCategoryUi = PopularCategoryUi.Empty.copy(name = "isn't name"),
    val categories: List<PopularCategoryUi> = emptyList()
)
