package com.vodovoz.app.ui.model

data class CategoryMainUI (
    val name: String,
    val detailPicture: String?,
    val categoryList: List<CategoryUI> = emptyList(),
)
