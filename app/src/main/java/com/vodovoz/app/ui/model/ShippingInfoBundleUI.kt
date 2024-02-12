package com.vodovoz.app.ui.model

import com.vodovoz.app.data.model.features.InnerPersonalScore

class ShippingInfoBundleUI(
    val id: Long = 0,
    val parkingPrice: Int = 0,
    val extraShippingPrice: Int = 0,
    val commonShippingPrice: Int = 0,
    val name: String = "",
    val shippingPrice: Int = 0,
    val todayShippingPrice: Int = 0,
    val todayShippingInfo: String = "",
    val innerPersonalScore: InnerPersonalScore? = null,
    val shippingIntervalUIList: List<ShippingIntervalUI>,
    val payMethodUIList: List<PayMethodUI> = listOf(),
    val isNewUser: Boolean = false
)