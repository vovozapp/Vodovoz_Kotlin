package com.vodovoz.app.feature.preorder.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.KeyboardType
import com.vodovoz.app.R
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

val vodovozValidators
    get() = listOf(
        NoRequiredValidator,
        PhoneNumberValidator,
        MessageValidator,
        NameValidator,
        KeyboardTypeValidator
    )

val EmptyTextValidator = FieldValidator { field ->
    return@FieldValidator when {
        field.value.isNotBlank() -> FieldValidationResult.VALID
        field.isRequired && field.value.isEmpty() -> FieldValidationResult.INVALID
        else -> FieldValidationResult.NOT_APPLICABLE
    }
}

val NoRequiredValidator = FieldValidator { field ->
    return@FieldValidator when {
        !field.isRequired && field.value.isBlank() -> FieldValidationResult.VALID
        else -> FieldValidationResult.NOT_APPLICABLE
    }
}

val PhoneNumberValidator = FieldValidator { field ->
    return@FieldValidator when (field.keyboardType) {
        KeyboardType.Phone -> {
            FieldValidationResult.from(field.value.isValidRussianPhoneNumber())
        }

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
        field.id =="name" || field.id == "lastname" -> {
            FieldValidationResult.from(value.length in 3..30 && value.isNotBlank())
        }

        else -> FieldValidationResult.NOT_APPLICABLE
    }
}

val MessageValidator = FieldValidator { field ->
    val value = field.value
    when {
        field.id == "dr127" || field.id.contains("message") -> {
            FieldValidationResult.from(value.length in 15..1000 && value.isNotBlank())
        }

        else -> FieldValidationResult.NOT_APPLICABLE
    }
}

fun List<FieldUi>.updateFieldValueAndResetErrors(field: FieldUi, newValue: String): List<FieldUi> {
    val fieldIndex = indexOfFirst { field.id == it.id }
    return toMutableList()
        .apply { set(fieldIndex, get(fieldIndex).copy(value = newValue)) }
        .map { it.copy(isError = false, supportingText = "") }

}

fun List<FieldUi>.updateFieldAndResetErrors(field: FieldUi, newField: FieldUi): List<FieldUi> {
    val fieldIndex = indexOfFirst { field.id == it.id }
    return toMutableList()
        .apply { set(fieldIndex, newField) }
        .map { it.copy(isError = false, supportingText = "") }
}

fun List<FieldUi>.updateField(field: FieldUi, newField: FieldUi): List<FieldUi> {
    val fieldIndex = indexOfFirst { field.id == it.id }
    return toMutableList().apply { set(fieldIndex, newField) }
}


fun FieldUi.getErrorText(getStringResource: (Int) -> String): String {
    return when {
        id == "email" || keyboardType == KeyboardType.Email -> {
            getStringResource(R.string.supporting_text_email)
        }

        id == "name" || id == "dr123" -> {
            getStringResource(R.string.supporting_text_name)
        }

        id == "lastname" -> {
            getStringResource(R.string.supporting_text_lastname)
        }

        keyboardType == KeyboardType.Password -> {
            getStringResource(R.string.supporting_text_password)
        }

        id == "message" || id == "dr127"-> {
            getStringResource(R.string.supporting_text_message)
        }

        else -> ""
    }
}

inline fun List<FieldUi>.checkFields(
    putErrors: Boolean = false,
    validators: List<FieldValidator> = listOf(
        NoRequiredValidator,
        NameValidator,
        KeyboardTypeValidator
    ),
    getSupportingText: (FieldUi) -> String = { "" },
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
            if (putErrors) return@map field.copy(
                isError = true,
                supportingText = getSupportingText(field)
            )
        }
        field
    }

    onResult(newFields, isValidFields)

    return isValidFields
}

@JvmName("mapToFieldUiList")
fun List<FieldModel>.mapToUi(): List<FieldUi> {
    return map { it.toUi() }
}

fun FieldModel.toUi(): FieldUi {

    val keyboardType = when (id) {
        "email" -> {
            KeyboardType.Email
        }

        "emaildryg" -> {
            KeyboardType.Email
        }

        "dr125" -> {
            KeyboardType.Email
        }

        "dr124" -> {
            KeyboardType.Phone
        }

        "phone" -> {
            KeyboardType.Phone
        }

        "pass" -> {
            KeyboardType.Password
        }

        "parol" -> {
            KeyboardType.Password
        }

        "data", "date" -> {
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
        hint = hint,
        isValueVisible = keyboardType != KeyboardType.Password
    )
}

fun List<FieldUi>.mapToDomain(): List<FieldModel> {
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
        label = label,
        hint = hint
    )
}