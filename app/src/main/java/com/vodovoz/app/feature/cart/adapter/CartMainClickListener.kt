package com.vodovoz.app.feature.cart.adapter

interface CartMainClickListener {

    fun onSwapProduct(id: Long)
    fun onChooseBtnClick()

    fun onApplyPromoCodeClick(code: String)
}