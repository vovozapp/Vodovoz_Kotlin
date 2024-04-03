package com.vodovoz.app.data.model.common

data class CategoryMainEntity(
    val name: String,
    val categoryList: List<CategoryEntity> = listOf()
)
