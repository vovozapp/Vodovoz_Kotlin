package com.vodovoz.app.data.config

import com.vodovoz.app.data.model.common.ShippingAlertEntity
import com.vodovoz.app.mapper.ShippingAlertMapper.mapToUI

object ShippingAlertConfig {

    val shippingAlertEntityList = listOf<ShippingAlertEntity>(
        ShippingAlertEntity(
            id = 1,
            "За 30 минут"
        ),
        ShippingAlertEntity(
            id = 2,
            "За 60 минут"
        ),
        ShippingAlertEntity(
            id = 3,
            "За 90 минут"
        ),
        ShippingAlertEntity(
            id = 4,
            "За 120 минут"
        )
    )

    val shippingAlertEntityListUI = listOf<ShippingAlertEntity>(
        ShippingAlertEntity(
            id = 1,
            "За 30 минут"
        ),
        ShippingAlertEntity(
            id = 2,
            "За 60 минут"
        ),
        ShippingAlertEntity(
            id = 3,
            "За 90 минут"
        ),
        ShippingAlertEntity(
            id = 4,
            "За 120 минут"
        )
    ).mapToUI()

}