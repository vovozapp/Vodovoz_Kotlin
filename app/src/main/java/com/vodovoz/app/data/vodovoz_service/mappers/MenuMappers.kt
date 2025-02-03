package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toVodovozImage
import com.vodovoz.app.data.vodovoz_service.model.MENU_DTO
import com.vodovoz.app.data.vodovoz_service.model.OrderMenuDTO
import com.vodovoz.app.data.vodovoz_service.model.ZAKAZ_DTO
import com.vodovoz.app.domain.general.model.MenuItemModel
import com.vodovoz.app.domain.general.model.MenuItemTypeModel
import com.vodovoz.app.domain.general.model.OrderModel
import com.vodovoz.app.domain.general.model.OrderWithMenuModel

fun OrderMenuDTO.mapToDomain(): OrderWithMenuModel {
    return OrderWithMenuModel(
        order = ZAKAZ?.mapToDomain(),
        menuItems = MENU?.mapNotNull { menuDto -> menuDto?.mapToDomain() } ?: emptyList()

    )
}

fun ZAKAZ_DTO.mapToDomain(): OrderModel? {
    return OrderModel(
        orderId = this.IDZAKAZ ?: return null,
        title = this.ZAGALOVOK ?: return null,
        description = this.OPISANIE ?: return null,
        borderColorHex = BORDERCOLOR ?: "",
        price = this.PRICE ?: return null
    )
}

fun MENU_DTO.mapToDomain(): MenuItemModel? {
    return MenuItemModel(
        type = this.IDKLYCH?.mapToMenuItemTypeModel() ?: MenuItemTypeModel.None,
        picture = this.KARTINKA?.toVodovozImage() ?: return null,
        title = this.TITLE ?: return null,
        description = this.OPISANIE ?: return null,
        borderColorHex = this.BORDERCOLOR ?: ""
    )
}

fun String.mapToMenuItemTypeModel(): MenuItemTypeModel {
    return when (this) {
        "oplata" -> MenuItemTypeModel.Payment
        "history" -> MenuItemTypeModel.History
        else -> MenuItemTypeModel.None
    }
}