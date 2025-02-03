package com.vodovoz.app.feature.home.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.MenuItemModel
import com.vodovoz.app.domain.general.model.MenuItemTypeModel
import com.vodovoz.app.domain.general.model.OrderModel
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.util.fromHexOrNull

@Immutable
data class OrderWithMenuUi(
    val order: OrderUi? = null,
    val menuItems: List<MenuItemUi>,
){
    companion object {
        val Empty = OrderWithMenuUi(menuItems = emptyList())
    }
}


fun OrderWithMenuModel.mapToUi(): OrderWithMenuUi {
    return OrderWithMenuUi(order?.mapToUi(), menuItems.map { it.mapToUi() })
}

fun OrderModel.mapToUi(): OrderUi {
    return OrderUi(
        orderId = orderId,
        title = title,
        text = description,
        price = price,
        borderColor = Color.fromHexOrNull(borderColorHex)
    )
}

fun MenuItemModel.mapToUi(): MenuItemUi {
    return MenuItemUi(
        image = this.picture,
        title = title,
        description = description,
        type = type,
        borderColor = Color.fromHexOrNull(borderColorHex)
    )
}



@Immutable
data class OrderUi(
    val orderId: Int,
    val title: String,
    val text: String,
    val price: String,
    val borderColor: Color?,
)

@Immutable
data class MenuItemUi(
    val image: String,
    val title: String,
    val description: String,
    val type: MenuItemTypeModel,
    val borderColor: Color?,
)