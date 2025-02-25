package com.vodovoz.app.feature.preorder.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.KeyboardType
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.PreOrderSectionModel

@Immutable
data class PreOrderSectionUi(
    val title: String,
    val fields: List<FieldUi>,
    val colorfulButton: ColorfulButtonUi,
) {
    companion object {
        val Empty = PreOrderSectionUi("", emptyList(), ColorfulButtonUi.Empty)
    }
}

@Immutable
data class FieldUi(
    val id: String,
    val label: String,
    val value: String,
    val keyboardType: KeyboardType,
    val isRequired: Boolean,
    val isError: Boolean,
    val readOnly: Boolean,
    val supportingText: String,
)

fun PreOrderSectionModel.toUi(): PreOrderSectionUi {
    return PreOrderSectionUi(
        title = title,
        fields = fields.map { field -> field.toUi() },
        colorfulButton = colorfulButton.toUi()
    )
}

fun FieldModel.toUi(): FieldUi {
    return FieldUi(
        id = id,
        label = title,
        value = value,
        keyboardType = when (valueType) {
            "text" -> KeyboardType.Text
            "phone" -> KeyboardType.Phone
            "email" -> KeyboardType.Email
            "number" -> KeyboardType.Number
            else -> KeyboardType.Unspecified
        },
        isRequired = isRequired,
        isError = false,
        readOnly = readOnly,
        supportingText = supportingText
    )
}

fun FieldUi.toDomain(): FieldModel {
    return FieldModel(
        id = id,
        value = value,
        valueType = when (keyboardType) {
            KeyboardType.Text -> "text"
            KeyboardType.Phone -> "phone"
            KeyboardType.Email -> "email"
            KeyboardType.Number -> "number"
            else -> "text"
        },
        isRequired = isRequired,
        readOnly = readOnly,
        supportingText = supportingText,
        title = label
    )
}