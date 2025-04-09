package com.vodovoz.app.domain.general.model.login

import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.FieldModel

data class AuthDetailsModel(
    val title: String,
    val description: String,
    val fields: List<FieldModel>,
    val hasAgreement: Boolean,
    val buttons: List<ColorfulButtonModel>
)