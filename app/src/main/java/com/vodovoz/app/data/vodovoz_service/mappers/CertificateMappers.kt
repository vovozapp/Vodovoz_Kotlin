package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.CertificateActivationDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.CertificateButtonDTO
import com.vodovoz.app.data.vodovoz_service.model.CertificateFieldDTO
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.domain.general.model.CertificateActivationDetailsModel
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.FieldModel

fun CertificateActivationDetailsDTO.toDomain(): CertificateActivationDetailsModel {
    return CertificateActivationDetailsModel(
        title = title ?: "",
        field = field?.toDomain()
            ?: throw IllegalArgumentException("CertificateActivationDetailsDTO field can't be null"),
        textHtml = text ?: "",
        textUnderButtonHtml = textUnderButton ?: "",
        button = button?.toDomain()
            ?: throw IllegalArgumentException("CertificateActivationDetailsDTO button can't be null")
    )
}

fun CertificateFieldDTO.toDomain(): FieldModel? {
    return FieldModel(
        id = CODE ?: return null,
        label = "",
        value = "",
        isRequired = OBYZATELNO == "Y",
        valueType = POLE ?: "text",
        hint = TEXT_V_POLE ?: "",
        readOnly = false,
        supportingText = ""
    )
}

fun CertificateButtonDTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = TITLE ?: "",
        backgroundColor = BACKGROUND ?: "",
        textColor = "",
    )
}