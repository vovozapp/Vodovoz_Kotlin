package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.PayMethodEntity
import com.vodovoz.app.data.model.common.ShippingIntervalEntity

class ShippingInfoBundleEntity(
    val id: Long = 0,
    val parkingPrice: Int = 0,
    val extraShippingPrice: Int = 0,
    val commonShippingPrice: Int = 0,
    val name: String = "",
    val shippingPrice: Int = 0,
    val todayShippingPrice: Int = 0,
    val todayShippingInfo: String = "",
    val shippingIntervalEntityList: List<ShippingIntervalEntity>,
    val payMethodEntityList: List<PayMethodEntity> = listOf(),
    val isNewUser: Boolean = false
)