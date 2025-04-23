package com.vodovoz.app.domain.general.model.order.composables

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.order.OrderDetailsButtonModel
import com.vodovoz.app.feature.all.orders.detail.composables.AboutOrderPopupWindowUi
import com.vodovoz.app.feature.all.orders.detail.composables.toUi
import com.vodovoz.app.util.fromHexOrUnspecified

sealed class OrderDetailsButtonUi {

    @Immutable
    data class ImageButton(
        val name: String,
        val image: String,
        val backgroundColor: Color,
        val textColor: Color,
        val id: String,
    ) : OrderDetailsButtonUi()

    @Immutable
    data class AboutOrderButton(
        val name: String,
        val description: String,
        val image: String,
        val popupWindow: AboutOrderPopupWindowUi,
    ) : OrderDetailsButtonUi()

    @Immutable
    data class TipsButton(
        val name: String,
        val description: String,
        val image: String,
        val url: String,
        val browser: Boolean,
    ) : OrderDetailsButtonUi()

    @Immutable
    data class PayButton(
        val name: String,
        val backgroundColor: Color,
        val textColor: Color,
        val browser: Boolean,
        val url: String,
    ) : OrderDetailsButtonUi()

    @Immutable
    data class WhereOrderButton(
        val name: String,
        val backgroundColor: Color,
        val textColor: Color,
        val id: String,
        val driverId: String,
    ) : OrderDetailsButtonUi()

}

fun List<OrderDetailsButtonModel>.mapToUi(): List<OrderDetailsButtonUi> {
    return mapNotNull { buttonModel -> buttonModel.toUi() }
}

fun OrderDetailsButtonModel.toUi(): OrderDetailsButtonUi? {
    return when {
        popupWindow != null -> OrderDetailsButtonUi.AboutOrderButton(
            name = name,
            description = description,
            image = image,
            popupWindow = popupWindow.toUi()
        )

        browser != null && image.isNotEmpty() -> OrderDetailsButtonUi.TipsButton(
            name = name,
            description = description,
            image = image,
            url = url ?: "",
            browser = browser
        )

        driverId != null -> OrderDetailsButtonUi.WhereOrderButton(
            name = name,
            backgroundColor = Color.fromHexOrUnspecified(backgroundColor),
            textColor = Color.fromHexOrUnspecified(textColor),
            id = id,
            driverId = driverId
        )


        image.isNotEmpty() -> OrderDetailsButtonUi.ImageButton(
            name = name,
            image = image,
            backgroundColor = Color.fromHexOrUnspecified(backgroundColor),
            textColor = Color.fromHexOrUnspecified(textColor),
            id = id
        )

        backgroundColor.isNotEmpty() -> OrderDetailsButtonUi.PayButton(
            name = name,
            backgroundColor = Color.fromHexOrUnspecified(backgroundColor),
            textColor = Color.fromHexOrUnspecified(textColor),
            browser = browser == true,
            url = url ?: ""
        )

        else -> null
    }
}
