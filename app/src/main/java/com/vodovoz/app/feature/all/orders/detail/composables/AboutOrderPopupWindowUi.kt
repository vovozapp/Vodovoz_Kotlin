package com.vodovoz.app.feature.all.orders.detail.composables

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.order.AboutOrderItemModel
import com.vodovoz.app.domain.general.model.order.AboutOrderPopupWindowModel

@Immutable
data class AboutOrderPopupWindowUi(
    val title: String,
    val items: List<AboutOrderItemUi>
)

@Immutable
data class AboutOrderItemUi(
    val image: String,
    val name: String,
    val description: String
)

fun AboutOrderPopupWindowModel.toUi(): AboutOrderPopupWindowUi{
    return AboutOrderPopupWindowUi(
        title = title,
        items = items.map { it.toUi() }
    )
}

fun AboutOrderItemModel.toUi(): AboutOrderItemUi{
    return AboutOrderItemUi(
        name = name,
        image = image,
        description = description
    )
}