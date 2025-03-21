package com.vodovoz.app.feature.sub_categories.model

import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi

data class SubCategoriesState(
    val catalogCategory: CatalogCategoryUi = CatalogCategoryUi.Empty,
    val searchQuery: String = ""
)