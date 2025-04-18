package com.vodovoz.app.domain.general.model.cart

data class CartOrderSummaryUi(
    val finalPriceText: String,
    val productsPriceText: String,
    val discountText: String,
    val depositText: String,
    val presentText: String,
){
    companion object{
        val Empty = CartOrderSummaryUi("","","","", "")
    }
}

fun CartOrderSummaryModel.toUi(): CartOrderSummaryUi {
    return CartOrderSummaryUi(
        finalPriceText,
        productsPriceText,
        discountText,
        depositText,
        presentText
    )
}
