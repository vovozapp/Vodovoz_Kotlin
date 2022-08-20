package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ShippingIntervalEntity
import com.vodovoz.app.ui.model.ShippingIntervalUI

object ShippingIntervalMapper {

    fun List<ShippingIntervalEntity>.mapToUI(): List<ShippingIntervalUI> =
        mutableListOf<ShippingIntervalUI>().also { uiList ->
            forEach { uiList.add(it.mapToUI()) }
    }

    fun ShippingIntervalEntity.mapToUI() = ShippingIntervalUI(
        id = id,
        name = name
    )

}