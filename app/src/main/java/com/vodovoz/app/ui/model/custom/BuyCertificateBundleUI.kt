package com.vodovoz.app.ui.model.custom

data class BuyCertificateBundleUI(
    val title: String = "",
    val payment: BuyCertificatePaymentUI,
    val buyCertificatePropertyUIList: List<BuyCertificatePropertyUI>
)
