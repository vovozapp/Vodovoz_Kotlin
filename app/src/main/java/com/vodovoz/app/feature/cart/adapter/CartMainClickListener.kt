package com.vodovoz.app.feature.cart.adapter

interface CartMainClickListener {

    fun onProductClick(id: Long)
    fun onChangeCartQuantity(id: Long, quantity: Int)
    fun onChangeFavoriteStatus(id: Long, status: Boolean)
    fun onNotifyWhenBeAvailable(id: Long, name: String, picture: String)

    fun onSwapProduct(id: Long)
    fun onChooseBtnClick()
}