package com.vodovoz.app.data.model.common

class CategoryEntity(
    val id: Long? = null,
    val name: String,
    val productAmount: String? = null,
    val shareUrl: String = "",
    val detailPicture: String? = null,
    val primaryFilterName: String? = null,
    val sortTypeList: SortTypeList? = null,
    val primaryFilterValueList: List<FilterValueEntity> = listOf(),
    val subCategoryEntityList: List<CategoryEntity> = listOf(),
    val filterCode: String = ""
)