package com.vodovoz.app.feature.categories.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi

@Immutable
data class CategoriesState(
    val currentCategory: CategoryUi = CategoryUi.Empty.copy(name = "isn't name"),
    val categories: List<CategoryUi> = emptyList(),
    val showApplyButton: Boolean = false
)
