package com.vodovoz.app.feature.buy_certificate.model

data class BuyCertificateErrorsUi(
    val certificate: Boolean = false,
    val payment: Boolean = false,
){
    companion object {
        val Empty = BuyCertificateErrorsUi()
    }
}
