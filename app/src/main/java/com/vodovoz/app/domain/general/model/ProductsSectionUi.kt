package com.vodovoz.app.domain.general.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.feature.home.model.toUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toUi

@Immutable
data class ProductsSectionUi(
    val title: String,
    val sortingTitle: String,
    val productsQuantityText: String,
    val products: List<ProductUi>,
    val sorting: List<SortUi>,
    val categories: List<CategoryUi>,
) {
    companion object {
        val Empty = ProductsSectionUi("", "", "", emptyList(), emptyList(), emptyList())
    }
}


fun ProductsSectionModel.toUi(): ProductsSectionUi {
    return ProductsSectionUi(
        title,
        sortingTitle,
        productsQuantityText,
        products.map { it.toUi() },
        sorting.map { it.toUi() },
        categories.map { it.toUi() }
    )
}