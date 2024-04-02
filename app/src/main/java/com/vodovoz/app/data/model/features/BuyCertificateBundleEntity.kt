package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.BuyCertificatePayment
import com.vodovoz.app.data.model.common.BuyCertificatePropertyEntity

data class BuyCertificateBundleEntity(
    val title: String = "",
    val certificateInfo: BuyCertificatePropertyEntity? = null,
    val typeList: List<BuyCertificateTypeEntity>? = null,
    val payment: BuyCertificatePayment,
)
