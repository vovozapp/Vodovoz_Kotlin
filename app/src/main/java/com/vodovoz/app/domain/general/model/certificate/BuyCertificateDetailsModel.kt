package com.vodovoz.app.domain.general.model.certificate

import com.vodovoz.app.domain.general.model.ColorfulButtonModel

data class BuyCertificateDetailsModel(
    val codes: BuyCertificateCodesModel,
    val title: String,
    val certificatesTitle: String,
    val certificates: List<CertificateModel>,
    val tabs: List<BuyCertificateTabModel>,
    val button: ColorfulButtonModel,
    val paymentTitle: String,
    val paymentTypes: List<PaymentTypeModel>,
    val faq: FAQModel,
)
