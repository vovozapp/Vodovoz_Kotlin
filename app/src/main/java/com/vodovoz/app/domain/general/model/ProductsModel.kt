package com.vodovoz.app.domain.general.model

data class ProductsSectionModel(
    val title: String,
    val sortingTitle: String,
    val sorting: List<SortModel>,
    val products: List<ProductModel>
)