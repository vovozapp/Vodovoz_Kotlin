package com.vodovoz.app.feature.all.orders.detail.model

data class DriverPointsEntity(
    val IndexOrder: String? = null,
    val Interval_zakaz: String? = null,
    val OrderNumber: String? = null,
    val Position: Position? = null,
    val Priblizitelnoe_vremya: String? = null,
    val DriverDirection: String? = null,
)

data class Position(
    val Latitude: String,
    val Longitude: String
)