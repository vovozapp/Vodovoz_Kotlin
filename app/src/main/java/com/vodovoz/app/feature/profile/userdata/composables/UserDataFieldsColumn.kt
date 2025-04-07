package com.vodovoz.app.feature.profile.userdata.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.text.PhoneNumberVisualTransformation
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun UserDataFieldsColumn(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    onFieldValueChange: (FieldUi, String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        fields.forEachIndexed { index, field ->
            key(field.id) {
                VodovozTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = field.value,
                    onValueChange = { newValue ->
                        onFieldValueChange(field, newValue)
                    },
                    isError = field.isError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = field.keyboardType,
                        imeAction = if (fields.lastIndex == index) ImeAction.Done else ImeAction.Next,
                    ),
                    supportingText = field.supportingText,
                    readOnly = field.readOnly,
                    label = field.label,
                    hint = field.hint,
                    visualTransformation = if (field.keyboardType == KeyboardType.Phone) PhoneNumberVisualTransformation()
                    else VisualTransformation.None,
                    singleLine = true,
                )
            }
        }
    }

}