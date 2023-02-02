package com.vodovoz.app.ui.model

data class CategoryDetailUI(
    val id: Long? = null,
    val name: String,
    val productAmount: Int,
    val productUIList: List<ProductUI>
)