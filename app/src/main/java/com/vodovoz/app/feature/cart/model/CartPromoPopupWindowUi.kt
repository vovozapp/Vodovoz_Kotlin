package com.vodovoz.app.feature.cart.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.cart.CartPromoPopupWindowModel


@Immutable
data class CartPromoPopupWindowUi(
    val title: String,
    val fieldHint: String,
    val buttonName: String,
    val errorText: String? = null,
){
    companion object {
        val Empty = CartPromoPopupWindowUi("","", "")
    }
}

fun CartPromoPopupWindowModel.toUi(): CartPromoPopupWindowUi{
    return CartPromoPopupWindowUi(
        title, fieldHint, buttonName, errorText
    )
}
