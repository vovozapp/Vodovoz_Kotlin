package com.vodovoz.app.domain.general.model

data class CancelOrderDetailsModel(
    val title: String,
    val description: String,
    val warningText: String,
    val checkboxesNames: List<String>,
    val field: FieldModel?,
    val button: ColorfulButtonModel,
)
