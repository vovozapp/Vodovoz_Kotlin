package com.vodovoz.app.feature.cart.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.cart.CartPresentItemModel
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class CartPresentItemUi(
    val id: Long,
    val name: String,
    val image: String,
): Parcelable {
    companion object {
        val Empty = CartPresentItemUi(-1, "", " ")
    }
}

fun List<CartPresentItemModel>.mapToUi(): List<CartPresentItemUi> {
    return map { it.toUi() }
}

fun CartPresentItemModel.toUi(): CartPresentItemUi {
    return CartPresentItemUi(id, name, image)
}
