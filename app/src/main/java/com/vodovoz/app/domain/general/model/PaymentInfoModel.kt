package com.vodovoz.app.domain.general.model

data class PaymentInfoModel(
    val id: Int,
    val name: String,
    val browser: Boolean,
    val url: String,
)
