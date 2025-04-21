package com.vodovoz.app.feature.cart.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.cart.CartPresentModel
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class CartPresentUi(
    val id: Long,
    val title: String,
    val description: String,
    val image: String,
    val maxPresentPrice: Int,
    val button: ColorfulButtonUi?,
    val popupWindow: CartPresentPopupWindowUi?,
): Parcelable {
    companion object {
        val Empty = CartPresentUi(-1, "", "", "", 0, null, null)
    }
}

fun CartPresentModel.toUi(): CartPresentUi{
    return CartPresentUi(
        id, title, description, image, leftToGift, button?.toUi(), popupWindow?.toUi()
    )
}
