package com.vodovoz.app.domain.general.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.toUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toUi

@Immutable
data class ProductsSectionUi(
    val title: String,
    val sortingTitle: String,
    val productsQuantity: Int,
    val products: List<ProductUi>,
    val sorting: List<SortUi>,
    val categories: List<CategoryUi>,
) {
    companion object {
        val Empty = ProductsSectionUi("", "", -1, emptyList(), emptyList(), emptyList())
    }
}


fun ProductsSectionModel.toUi(): ProductsSectionUi {
    return ProductsSectionUi(
        title,
        sortingTitle,
        productsQuantity,
        products.map { it.toUi() },
        sorting.map { it.toUi() },
        categories.map { it.toUi() }
    )
}