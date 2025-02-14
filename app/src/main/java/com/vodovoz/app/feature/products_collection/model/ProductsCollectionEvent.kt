package com.vodovoz.app.feature.products_collection.model

sealed class ProductsCollectionEvent {
    data object GoBack : ProductsCollectionEvent()


}