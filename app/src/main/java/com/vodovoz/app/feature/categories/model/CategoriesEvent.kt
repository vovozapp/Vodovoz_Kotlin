package com.vodovoz.app.feature.categories.model

import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi

sealed interface CategoriesEvent {

    data class GoBackWithArguments(val currentCategory: CategoryUi) : CategoriesEvent

    data object GoBack: CategoriesEvent

}