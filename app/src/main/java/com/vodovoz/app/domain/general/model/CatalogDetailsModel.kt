package com.vodovoz.app.domain.general.model

data class CatalogDetailsModel(
    val banners: List<BannerModel>,
    val categories: List<ParentCategoryModel>,
)

data class ParentCategoryModel(
    val id: Long,
    val name: String,
    val picture: String,
    val action: DataAllAction?,
    val parentId: Int?,
    val depthLevel: Int,
    val subcategoriesQuantity: Int,
    val childCategories: List<ParentCategoryModel>
)
