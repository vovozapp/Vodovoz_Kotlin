package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.CertificatePropertyEntity
import com.vodovoz.app.ui.model.custom.CertificatePropertyUI


fun List<CertificatePropertyEntity>.mapToUI(): List<CertificatePropertyUI> =
    mutableListOf<CertificatePropertyUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

fun CertificatePropertyEntity.mapToUI() = CertificatePropertyUI(
    title = title,
    textToField = textToField,
    text = text,
    buttonText = buttonText,
    buttonColor = buttonColor,
    underButtonText = underButtonText,
)