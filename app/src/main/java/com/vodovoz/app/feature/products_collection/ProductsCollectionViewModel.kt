package com.vodovoz.app.feature.products_collection

import com.vodovoz.app.feature.products_collection.model.ProductsCollectionEvent
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionState
import com.vodovoz.app.ui.mvi.MviViewModel

class ProductsCollectionViewModel : MviViewModel<ProductsCollectionState, ProductsCollectionEvent>(
    ProductsCollectionState()
) {



}