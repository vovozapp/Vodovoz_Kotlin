package com.vodovoz.app.feature.cart.model

import com.vodovoz.app.domain.general.model.cart.CartButtonModel

data class CartButtonUi(
    val id: String,
    val image: String,
    val name: String
){
    companion object{
        val Empty = CartButtonUi("","","")
    }
}

fun CartButtonModel.toUi(): CartButtonUi{
    return CartButtonUi(
        id, image, name
    )
}
