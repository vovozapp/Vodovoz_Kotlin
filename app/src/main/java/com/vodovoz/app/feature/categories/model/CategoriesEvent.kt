package com.vodovoz.app.feature.categories.model

import com.vodovoz.app.feature.home.model.PopularCategoryUi

sealed interface CategoriesEvent {

    data class GoBackWithArguments(val currentCategory: PopularCategoryUi) : CategoriesEvent

    data object GoBack: CategoriesEvent

}