package com.vodovoz.app.ui.model

class ShippingInfoBundleUI(
    val id: Long = 0,
    val parkingPrice: Int = 0,
    val extraShippingPrice: Int = 0,
    val commonShippingPrice: Int = 0,
    val name: String = "",
    val shippingPrice: Int = 0,
    val todayShippingPrice: Int = 0,
    val todayShippingInfo: String = "",
    val shippingIntervalUIList: List<ShippingIntervalUI>,
    val payMethodUIList: List<PayMethodUI> = listOf(),
)