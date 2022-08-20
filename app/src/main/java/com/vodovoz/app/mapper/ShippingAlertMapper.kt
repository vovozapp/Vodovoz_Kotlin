package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ShippingAlertEntity
import com.vodovoz.app.ui.model.ShippingAlertUI

object ShippingAlertMapper {

    fun List<ShippingAlertEntity>.mapToUI(): List<ShippingAlertUI> = mutableListOf<ShippingAlertUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }
    fun ShippingAlertEntity.mapToUI() = ShippingAlertUI(
        id = id,
        name = name
    )

}