package com.vodovoz.app.domain.general.model.certificate

import com.vodovoz.app.domain.general.model.PaymentInfoModel
import com.vodovoz.app.domain.general.model.VodovozPlaceholderModel

data class BuyCertificateModel(
    val placeholder: VodovozPlaceholderModel,
    val payment: PaymentInfoModel
)
