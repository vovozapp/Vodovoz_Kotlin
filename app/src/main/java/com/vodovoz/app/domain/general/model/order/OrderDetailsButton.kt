package com.vodovoz.app.domain.general.model.order

data class OrderDetailsButtonModel(
    val name: String,
    val image: String,
    val backgroundColor: String,
    val textColor: String,
    val id: String,
    val description: String,
    val popupWindow: AboutOrderPopupWindowModel?,
    val url: String?,
    val browser: Boolean?,
    val driverId: String?,
)