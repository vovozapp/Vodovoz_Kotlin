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


fun OrderWithMenuModel.toUi(): OrderWithMenuUi {
    return OrderWithMenuUi(order?.toUi(), menuItems.map { it.toUi() })
}

fun OrderModel.toUi(): OrderUi {
    return OrderUi(
        orderId = orderId,
        title = title,
        text = description,
        price = price,
        borderColor = Color.fromHexOrNull(borderColorHex)
    )
}

fun MenuItemModel.toUi(): MenuItemUi {
    return MenuItemUi(
        image = this.picture,
        title = title,
        description = description,
        type = type.toUi(),
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
    val type: MenuItemTypeUi,
    val borderColor: Color?,
)

enum class MenuItemTypeUi(val id: String) {
    History("history"), Payment("oplata"), None("")
}

fun MenuItemTypeModel.toUi(): MenuItemTypeUi{
    return MenuItemTypeUi.entries.firstOrNull { it.id == id } ?: MenuItemTypeUi.None
}