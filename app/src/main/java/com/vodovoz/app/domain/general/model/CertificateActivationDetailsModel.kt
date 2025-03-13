package com.vodovoz.app.domain.general.model

import com.vodovoz.app.data.vodovoz_service.model.COLORFUL_KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.CertificateFieldDTO
import com.vodovoz.app.design_system.model.ColorfulButtonUi

data class CertificateActivationDetailsModel(
    val title: String,
    val field: FieldModel,
    val textHtml: String,
    val textUnderButtonHtml: String,
    val button: ColorfulButtonModel,
)
