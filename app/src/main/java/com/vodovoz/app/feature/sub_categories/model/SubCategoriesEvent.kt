package com.vodovoz.app.feature.sub_categories.model

import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi

sealed interface SubCategoriesEvent {
    data object GoToSearch : SubCategoriesEvent
    data object GoBack : SubCategoriesEvent

    data class GoToProductList(val categoryId: Int) : SubCategoriesEvent

    data class GoToSubCategories(val category: CatalogCategoryUi): SubCategoriesEvent


}