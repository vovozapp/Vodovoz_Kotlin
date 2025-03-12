package com.vodovoz.app.feature.products_collection.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.ProductsSectionUi
import com.vodovoz.app.feature.product_comments.model.SortUi

@Immutable
data class ProductsCollectionState(
    val productsSection: ProductsSectionUi = ProductsSectionUi.Empty,
    val currentSort: SortUi = SortUi.Empty,
    val showSortOptionsBottomSheet: Boolean = false,
    val isGridView: Boolean = true,
    val uiState: ProductsCollectionUiState = ProductsCollectionUiState.Loading
)

sealed interface ProductsCollectionUiState{

    data object Loading: ProductsCollectionUiState

    data object Success: ProductsCollectionUiState


}