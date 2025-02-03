package com.vodovoz.app.domain.general.model

import com.vodovoz.app.feature.home.model.CategoryWithProductsUi

data class TopAndBottomSectionsModel(
    val topSection: SectionModel,
    val bottomSection: SectionModel,
)

data class SectionModel(
    val name: String?,
    val showAllId: Int?,
    val categoryWithProductsList: List<CategoryWithProductsModel>
)


data class CategoryWithProductsModel(
    val id: Long,
    val name: String,
    val products: List<ProductModel>,
)
