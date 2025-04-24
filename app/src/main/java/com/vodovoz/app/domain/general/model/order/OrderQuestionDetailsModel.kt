package com.vodovoz.app.domain.general.model.order

import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.FieldModel

data class OrderQuestionDetailsModel(
    val title: String,
    val description: String,
    val fields: List<FieldModel>,
    val button: ColorfulButtonModel
)