package com.vodovoz.app.feature.cart.model

import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.cart.CartPromoButtonModel
import com.vodovoz.app.util.fromHexOrUnspecified


data class CartPromoButtonUi(
    val title: String,
    val text: String,
    val coupon: String,
    val textColor: Color,
    val image: String,
    val id: String,
    val popupWindow: CartPromoPopupWindowUi,
) {
    companion object {
        val Empty = CartPromoButtonUi(
            "",
            "",
            "",
            Color.Unspecified,
            "",
            "",
            CartPromoPopupWindowUi.Empty
        )
    }
}

fun CartPromoButtonModel.toUi(): CartPromoButtonUi{
    return CartPromoButtonUi(
        title, text, coupon,Color.fromHexOrUnspecified(textColor), image, id, popupWindow.toUi()
    )
}
