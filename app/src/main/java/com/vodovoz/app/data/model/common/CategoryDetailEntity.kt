package com.vodovoz.app.data.model.common

class CategoryDetailEntity(
    val id: Long? = null,
    val name: String,
    val productAmount: Int = 0,
    val productEntityList: List<ProductEntity>
)