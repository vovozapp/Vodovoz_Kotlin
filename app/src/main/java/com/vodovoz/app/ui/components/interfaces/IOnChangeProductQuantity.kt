package com.vodovoz.app.ui.components.interfaces

fun interface IOnChangeProductQuantity {
    fun onChangeProductQuantity(productId: Long, quantity: Int)
}