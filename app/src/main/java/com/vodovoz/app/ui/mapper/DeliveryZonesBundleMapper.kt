package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.features.DeliveryZonesBundleEntity
import com.vodovoz.app.ui.mapper.DeliveryZoneMapper.mapToUI
import com.vodovoz.app.ui.model.custom.DeliveryZonesBundleUI

object DeliveryZonesBundleMapper {

    fun DeliveryZonesBundleEntity.mapToUI() = DeliveryZonesBundleUI(
        aboutDeliveryTimeUrl = aboutDeliveryTimeUrl,
        deliveryZoneUIList = deliveryZoneEntityList.mapToUI()
    )

}