package com.vodovoz.app.domain.general.model

data class ProductsSectionModel(
    val title: String,
    val sortingTitle: String,
    val productsQuantityText: String,
    val sorting: List<SortModel>,
    val products: List<ProductModel>,
    val categories: List<CategoryModel>,
    val share: ShareModel? = null
)

data class ShareModel(
    val url: String,
    val text: String
)