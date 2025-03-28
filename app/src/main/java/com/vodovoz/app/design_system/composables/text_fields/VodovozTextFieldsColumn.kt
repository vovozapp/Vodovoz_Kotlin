package com.vodovoz.app.design_system.composables.text_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.decoration.PasswordIcon
import com.vodovoz.app.design_system.text.PhoneNumberVisualTransformation
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun VodovozTextFieldsColumn(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    onFieldChange: (currentField: FieldUi, newField: FieldUi) -> Unit,
    onDone: KeyboardActionScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        fields.forEachIndexed { index, field ->
            val isMessage = field.id.contains("message", true)

            key(field.id) {
                VodovozTextField(
                    value = field.value,
                    onValueChange = { newValue ->
                        onFieldChange(field, field.copy(value = newValue))
                    },
                    isError = field.isError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = field.keyboardType,
                        imeAction = if (fields.lastIndex == index) ImeAction.Done else ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = onDone,
                    ),
                    readOnly = field.readOnly,
                    label = field.label,
                    hint = field.hint,
                    singleLine = !isMessage,
                    maxLines = if (isMessage) 3 else 1,
                    minLines = if (isMessage) 2 else 1,
                    supportingText = field.supportingText,
                    visualTransformation = when (field.keyboardType) {
                        KeyboardType.Phone -> PhoneNumberVisualTransformation()
                        KeyboardType.Password -> if (!field.isValueVisible) {
                            PasswordVisualTransformation('•')
                        } else VisualTransformation.None

                        else -> VisualTransformation.None
                    },
                    trailingIcon = {
                        if(field.keyboardType == KeyboardType.Password){
                            PasswordIcon(valueIsVisible = field.isValueVisible) {
                                onFieldChange(field, field.copy(isValueVisible = !field.isValueVisible))
                            }
                        }
                    }
                )
            }
        }
    }
}