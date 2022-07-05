package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.DeliveryZoneEntity

class DeliveryZonesBundleEntity(
    val aboutDeliveryTimeUrl: String,
    val deliveryZoneEntityList: List<DeliveryZoneEntity>
)