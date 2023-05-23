package com.vodovoz.app.ui.model

import com.yandex.mapkit.geometry.Point

class DeliveryZoneUI(
    val deliveryTime: String? = null,
    val color: String? = null,
    val pointList: List<Point>,
    val isCenter: Boolean
)