package com.vodovoz.app.feature.products_collection.model

sealed class ProductsCollectionEvent {
    data class GoToProductDetails(val productId: Long) : ProductsCollectionEvent()
    data class GoToProductAnalogs(val productId: Long) : ProductsCollectionEvent()

    data object GoBack : ProductsCollectionEvent()


}