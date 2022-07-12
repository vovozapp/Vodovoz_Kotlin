package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.DeliveryZoneEntity
import com.vodovoz.app.ui.model.DeliveryZoneUI
import com.yandex.mapkit.geometry.Point

object DeliveryZoneMapper {

    fun List<DeliveryZoneEntity>.mapToUI(): List<DeliveryZoneUI> = mutableListOf<DeliveryZoneUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun DeliveryZoneEntity.mapToUI() = DeliveryZoneUI(
        deliveryTime = deliveryTime,
        color = color,
        pointList = mutableListOf<Point>().apply {
            pointEntityList.forEach { pointEntity ->
                add(Point(pointEntity.latitude!!, pointEntity.longitude!!))
            }
        }.toList()
    )
}