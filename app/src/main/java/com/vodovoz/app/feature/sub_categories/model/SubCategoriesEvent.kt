package com.vodovoz.app.feature.sub_categories.model

import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi

sealed interface SubCategoriesEvent {
    data object GoToSearch : SubCategoriesEvent
    data object GoBack : SubCategoriesEvent

    data class GoToProductList(val categoryId: Long) : SubCategoriesEvent

    data class GoToSubCategories(val category: CatalogCategoryUi): SubCategoriesEvent
    data class ActivateDataAllAction(val action: DataAllAction) : SubCategoriesEvent


}