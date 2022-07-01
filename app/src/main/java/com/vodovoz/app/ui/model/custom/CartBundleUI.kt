package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI

class CartBundleUI(
    val giftMessage: String? = null,
    val availableProductUIList: List<ProductUI> = listOf(),
    val notAvailableProductUIList: List<ProductUI> = listOf(),
    val giftProductUIList: List<ProductUI> = listOf(),
    val bestForYouCategoryDetailUI: CategoryDetailUI? = null
)