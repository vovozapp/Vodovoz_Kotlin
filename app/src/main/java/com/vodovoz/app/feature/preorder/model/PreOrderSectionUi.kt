package com.vodovoz.app.feature.preorder.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.KeyboardType
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.PreOrderSectionModel
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.FieldValidationsSettings.PASSWORD_LENGTH
import com.vodovoz.app.util.isValidRussianPhoneNumber

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

fun interface FieldValidator {
    fun validate(field: FieldUi): Boolean
}

val KeyboardTypeValidator = FieldValidator { field ->

    val value = field.value

    return@FieldValidator when(field.keyboardType){
        KeyboardType.Text -> {
            value.length in 2..100 && value.isNotBlank()
        }
        KeyboardType.Phone -> {
            value.isValidRussianPhoneNumber()
        }
        KeyboardType.Email -> {
            FieldValidationsSettings.EMAIL_REGEX.matches(value)
        }

        KeyboardType.Password -> {
            value.length in PASSWORD_LENGTH
        }

        else -> true
    }
}

val NameValidator = FieldValidator { field ->
    val value = field.value
    when{
        value.contains("name") -> {
            value.length in 3..30 && value.isNotBlank()
        }
        else -> true
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
    val hint: String = "",
    val isValueVisible: Boolean = true
)

fun PreOrderSectionModel.toUi(): PreOrderSectionUi {
    return PreOrderSectionUi(
        title = title,
        fields = fields.map { field -> field.toUi() },
        colorfulButton = colorfulButton.toUi()
    )
}

fun FieldModel.toUi(): FieldUi {

    val keyboardType = when{
        id.contains("email") -> {
            KeyboardType.Email
        }
        id.contains("phone") -> {
            KeyboardType.Phone
        }
        id.contains("pass") -> {
            KeyboardType.Password
        }
        else -> {
            when (valueType.lowercase()) {
                "text" -> KeyboardType.Text
                "phone" -> KeyboardType.Phone
                "email" -> KeyboardType.Email
                "number" -> KeyboardType.Number
                "password" -> KeyboardType.Password
                else -> KeyboardType.Unspecified
            }
        }
    }

    return FieldUi(
        id = id,
        label = label,
        value = value,
        keyboardType = keyboardType,
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
            KeyboardType.Password -> "password"
            else -> "text"
        },
        isRequired = isRequired,
        readOnly = readOnly,
        supportingText = supportingText,
        label = label
    )
}