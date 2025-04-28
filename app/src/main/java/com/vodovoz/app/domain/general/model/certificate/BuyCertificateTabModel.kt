package com.vodovoz.app.domain.general.model.certificate

import com.vodovoz.app.domain.general.model.FieldModel

data class BuyCertificateTabModel(
    val id: Int,
    val name: String,
    val fields: List<FieldModel>
)
