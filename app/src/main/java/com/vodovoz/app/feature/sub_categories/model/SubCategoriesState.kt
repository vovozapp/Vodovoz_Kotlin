package com.vodovoz.app.feature.sub_categories.model

import com.vodovoz.app.design_system.model.ParentCategoryUi

data class SubCategoriesState(
    val catalogCategory: ParentCategoryUi = ParentCategoryUi.Empty,
    val searchQuery: String = ""
)