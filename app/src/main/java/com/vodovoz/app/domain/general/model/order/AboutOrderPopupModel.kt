package com.vodovoz.app.domain.general.model.order

data class AboutOrderPopupWindowModel(
    val title: String,
    val items: List<AboutOrderItemModel>
)

data class AboutOrderItemModel(
    val image: String,
    val name: String,
    val description: String
)