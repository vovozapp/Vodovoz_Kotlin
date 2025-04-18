package com.vodovoz.app.domain.general.model.cart

data class CartPromoPopupWindowModel(
    val title: String,
    val fieldHint: String,
    val buttonName: String,
    val errorText: String?
){
    companion object{
        val Empty = CartPromoPopupWindowModel("","","", null)
    }
}
