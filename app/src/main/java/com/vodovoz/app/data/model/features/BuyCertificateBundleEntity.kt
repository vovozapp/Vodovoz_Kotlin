package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.BuyCertificatePayment
import com.vodovoz.app.data.model.common.BuyCertificatePropertyEntity

data class BuyCertificateBundleEntity(
    val title: String = "",
    val buyCertificatePropertyEntityList: List<BuyCertificatePropertyEntity>,
    val payment: BuyCertificatePayment
)
