package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity

class CartBundleEntity(
    val giftMessage: String? = null,
    val infoMessage: String = "",
    val availableProductEntityList: List<ProductEntity> = listOf(),
    val notAvailableProductEntityList: List<ProductEntity> = listOf(),
    val giftProductEntityList: List<ProductEntity> = listOf(),
    val bestForYouCategoryDetailEntity: CategoryDetailEntity? = null
)