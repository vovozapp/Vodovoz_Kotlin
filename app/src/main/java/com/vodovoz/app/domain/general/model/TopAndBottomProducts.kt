package com.vodovoz.app.domain.general.model

data class TopAndBottomSectionsModel(
    val topSection: SectionModel<CategoryWithProductsModel>,
    val bottomSection: SectionModel<CategoryWithProductsModel>,
)


data class SectionModel<E>(
    val title: String,
    val items: List<E>,
    val button: ButtonModel?,
)

data class ButtonModel(
    val name: String,
    val action: ButtonInfo,
)


data class CategoryWithProductsModel(
    val id: Long,
    val name: String,
    val products: List<ProductModel>,
)
