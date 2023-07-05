package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.parser.response.cart.MessageTextBasket

class CartBundleEntity(
    val giftMessageBottom: MessageTextBasket? = null,
    val giftMessage: MessageTextBasket? = null,
    val infoMessage: MessageTextBasket? = null,
    val availableProductEntityList: List<ProductEntity> = listOf(),
    val notAvailableProductEntityList: List<ProductEntity> = listOf(),
    val giftProductEntityList: List<ProductEntity> = listOf(),
    val bestForYouCategoryDetailEntity: CategoryDetailEntity? = null
)