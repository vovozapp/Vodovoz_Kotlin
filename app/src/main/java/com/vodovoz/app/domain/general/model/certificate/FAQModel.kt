package com.vodovoz.app.domain.general.model.certificate

data class FAQModel(
    val name: String,
    val image: String,
    val items: List<FAQItemModel>
)
