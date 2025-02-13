package com.vodovoz.app.feature.products_collection.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.feature.home.model.ProductUi

@Immutable
data class ProductsCollectionState(
    val products: List<ProductUi> = emptyList()
)