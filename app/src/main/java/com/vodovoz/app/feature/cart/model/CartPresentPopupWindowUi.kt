package com.vodovoz.app.feature.cart.model

import android.os.Parcelable
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.cart.CartPresentPopupWindowModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartPresentPopupWindowUi(
    val items: List<CartPresentItemUi>,
    val button: ColorfulButtonUi
): Parcelable

fun CartPresentPopupWindowModel.toUi(): CartPresentPopupWindowUi{
    return CartPresentPopupWindowUi(
        items = items.mapToUi(),
        button = button.toUi()
    )
}
