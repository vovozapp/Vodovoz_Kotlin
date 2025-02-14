package com.vodovoz.app.domain.general.model

import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.toUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toUi

data class ProductsSectionUi(
    val title: String,
    val sortingTitle: String,
    val products: List<ProductUi>,
    val sorting: List<SortUi>,
) {
    companion object {
        val Empty = ProductsSectionUi("", "", emptyList(), emptyList())
    }
}


fun ProductsSectionModel.toDomain(): ProductsSectionUi {
    return ProductsSectionUi(
        title, sortingTitle, products.map { it.toUi() }, sorting.map { it.toUi() }
    )
}