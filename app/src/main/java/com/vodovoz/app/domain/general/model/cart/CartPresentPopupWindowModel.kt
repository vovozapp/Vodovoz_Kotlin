package com.vodovoz.app.domain.general.model.cart

import com.vodovoz.app.domain.general.model.ColorfulButtonModel

data class CartPresentPopupWindowModel(
    val items: List<CartPresentItemModel>,
    val button: ColorfulButtonModel
)
