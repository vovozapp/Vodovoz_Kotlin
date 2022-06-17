package com.vodovoz.app.data.model.common

class CategoryEntity(
    val id: Long? = null,
    val name: String,
    val productAmount: String? = null,
    val detailPicture: String? = null,
    val primaryFilterName: String? = null,
    val primaryFilterValueList: List<FilterValueEntity> = listOf(),
    val subCategoryEntityList: List<CategoryEntity> = listOf()
)