package com.vodovoz.app.domain.general.model

data class PreOrderSectionModel(
    val title: String,
    val fields: List<FieldModel>,
    val colorfulButton: ColorfulButtonModel,
)

data class FieldModel(
    val id: String,
    val label: String,
    val value: String,
    val valueType: String,
    val isRequired: Boolean,
    val readOnly: Boolean,
    val supportingText: String,
    val hint: String = ""
)
