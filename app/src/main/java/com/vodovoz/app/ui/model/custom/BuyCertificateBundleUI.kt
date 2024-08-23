package com.vodovoz.app.ui.model.custom

import androidx.compose.runtime.Immutable

@Immutable
data class BuyCertificateBundleUI(
    val title: String = "",
    val payment: BuyCertificatePaymentUI,
    val certificateInfo: BuyCertificatePropertyUI? = null,
    val typeList: List<BuyCertificateTypeUI>? = null,
)
