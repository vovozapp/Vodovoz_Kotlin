package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.ShippingInfoBundleEntity
import com.vodovoz.app.mapper.PayMethodMapper.mapToUI
import com.vodovoz.app.mapper.ShippingIntervalMapper.mapToUI
import com.vodovoz.app.ui.model.ShippingInfoBundleUI

object ShippingInfoBundleMapper {

    fun ShippingInfoBundleEntity.mapToUI() = ShippingInfoBundleUI(
        id = id,
        parkingPrice = parkingPrice,
        extraShippingPrice = extraShippingPrice,
        commonShippingPrice = commonShippingPrice,
        name = name,
        todayShippingPrice = todayShippingPrice,
        todayShippingInfo = todayShippingInfo,
        shippingPrice = shippingPrice,
        shippingIntervalUIList = shippingIntervalEntityList.mapToUI(),
        payMethodUIList = payMethodEntityList.mapToUI()
    )

}