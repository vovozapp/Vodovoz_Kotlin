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

fun PreOrderSectionModel.toUi(): PreOrderSectionUi {
    return PreOrderSectionUi(
        title = title,
        fields = fields.map { field -> field.toUi() },
        colorfulButton = colorfulButton.toUi()
    )
}

enum class FieldValidationResult {
    VALID,
    INVALID,
    NOT_APPLICABLE;

    companion object {
        fun from(isValid: Boolean): FieldValidationResult {
            return if (isValid) VALID else INVALID
        }
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
    val isValueVisible: Boolean = true,
) {
    companion object {
        val Empty = FieldUi(
            id = "",
            label = "",
            value = "",
            keyboardType = KeyboardType.Text,
            isRequired = false,
            isError = false,
            readOnly = false,
            supportingText = ""
        )
    }
}



fun interface FieldValidator {
    fun isValid(field: FieldUi): FieldValidationResult
}


val NoRequiredValidator = FieldValidator { field ->
    return@FieldValidator when {
        !field.isRequired && field.value.isNotBlank() -> FieldValidationResult.VALID
        else -> FieldValidationResult.NOT_APPLICABLE
    }
}

val KeyboardTypeValidator = FieldValidator { field ->
    val value = field.value

    return@FieldValidator when (field.keyboardType) {
        KeyboardType.Text -> {
            FieldValidationResult.from(value.length in 2..100 && value.isNotBlank())
        }

        KeyboardType.Phone -> {
            FieldValidationResult.from(value.isValidRussianPhoneNumber())
        }

        KeyboardType.Email -> {
            FieldValidationResult.from(FieldValidationsSettings.EMAIL_REGEX.matches(value))
        }

        KeyboardType.Password -> {
            FieldValidationResult.from(value.length in PASSWORD_LENGTH)
        }

        else -> FieldValidationResult.NOT_APPLICABLE
    }
}

val NameValidator = FieldValidator { field ->
    val value = field.value
    when {
        value.contains("name") -> {
            FieldValidationResult.from(value.length in 3..30 && value.isNotBlank())
        }

        else -> FieldValidationResult.NOT_APPLICABLE
    }
}

fun List<FieldUi>.updateFieldValueAndResetErrors(field: FieldUi, newValue: String): List<FieldUi> {
    val fieldIndex = indexOfFirst { field.id == it.id }
    return toMutableList()
        .apply { set(fieldIndex, this[fieldIndex].copy(value = newValue)) }
        .map { it.copy(isError = false) }
}

fun List<FieldUi>.updateFieldAndResetErrors(field: FieldUi, newField: FieldUi): List<FieldUi> {
    val fieldIndex = indexOfFirst { field.id == it.id }
    return toMutableList()
        .apply { set(fieldIndex, newField) }
        .map { it.copy(isError = false) }
}




fun List<FieldUi>.checkFields(
    putErrors: Boolean = false,
    validators: List<FieldValidator> = listOf(NoRequiredValidator, NameValidator, KeyboardTypeValidator),
    onResult: (List<FieldUi>, isValid: Boolean) -> Unit = { p1, p2 -> },
): Boolean {
    var isValidFields = true

    val newFields = map { field ->
        val currentValidator = validators.firstOrNull { fieldValidator ->
            fieldValidator.isValid(field) != FieldValidationResult.NOT_APPLICABLE
        } ?: return@map field

        val isValid = currentValidator.isValid(field) == FieldValidationResult.VALID

        if (!isValid) {
            isValidFields = false
            if (putErrors) return@map field.copy(isError = true)
        }
        field
    }

    onResult(newFields, isValidFields)

    return isValidFields
}


fun FieldModel.toUi(): FieldUi {

    val keyboardType = when {
        id.contains("email") -> {
            KeyboardType.Email
        }

        id.contains("phone") -> {
            KeyboardType.Phone
        }

        id.contains("pass") -> {
            KeyboardType.Password
        }

        id == "data" || id == "date" -> {
            KeyboardType.Decimal
        }

        else -> {
            when (valueType.lowercase()) {
                "text" -> KeyboardType.Text
                "phone" -> KeyboardType.Phone
                "email" -> KeyboardType.Email
                "number" -> KeyboardType.Number
                "password" -> KeyboardType.Password
                "date" -> KeyboardType.Decimal
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
        supportingText = supportingText,
        hint = hint
    )
}

fun List<FieldUi>.mapToDomain(): List<FieldModel>{
    return map { it.toDomain() }
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