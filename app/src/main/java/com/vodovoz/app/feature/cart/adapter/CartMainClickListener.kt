package com.vodovoz.app.feature.cart.adapter

import com.vodovoz.app.ui.model.ProductUI

interface CartMainClickListener {

    fun onSwapProduct(productUI: ProductUI)
    fun onChooseBtnClick()

    fun onApplyPromoCodeClick(code: String)

    fun onGoToCatalogClick()

    fun showSnack(message: String)
}