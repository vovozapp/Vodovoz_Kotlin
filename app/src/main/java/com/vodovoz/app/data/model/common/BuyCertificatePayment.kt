package com.vodovoz.app.data.model.common

data class BuyCertificatePayment(
    val code: String,
    val payMethods: List<PayMethodEntity>,
    val name: String,
    val required: String
)