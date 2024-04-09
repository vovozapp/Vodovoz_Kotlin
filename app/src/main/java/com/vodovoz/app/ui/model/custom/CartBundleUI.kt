package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.data.parser.response.cart.MessageTextBasket
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI

class CartBundleUI(
    val giftMessageBottom: MessageTextBasket? = null,
    val giftMessage: MessageTextBasket? = null,
    val infoMessage: MessageTextBasket? = null,
    val availableProductUIList: List<ProductUI> = listOf(),
    val notAvailableProductUIList: List<ProductUI> = listOf(),
    val giftProductUI: GiftProductUI? = null,
    val bestForYouCategoryDetailUI: CategoryDetailUI? = null,
    val giftTitleBottom: String? = null,
)