package com.vodovoz.app.feature.auth.reg.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.decoration.PasswordIcon
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.text.PhoneNumberVisualTransformation
import com.vodovoz.app.feature.preorder.model.FieldUi


@Suppress("NonSkippableComposable")
@Composable
fun RegisterBody(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    buttonEnabled: Boolean,
    buttonLoading: Boolean,
    onFieldValueChange: (FieldUi, String) -> Unit,
    onFieldVisibilityChange: (FieldUi) -> Unit,
    onRegister: () -> Unit,
) {
    Column(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            fields.forEachIndexed { index, field ->
                key(field.id) {

                    VodovozTextField(
                        value = field.value,
                        onValueChange = { newValue ->
                            onFieldValueChange(field, newValue)
                        },
                        isError = field.isError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = field.keyboardType,
                            imeAction = if (fields.lastIndex == index) ImeAction.Done else ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onRegister() },
                        ),
                        supportingText = field.supportingText.ifEmpty { null },
                        readOnly = field.readOnly,
                        label = field.label.ifEmpty { null },
                        visualTransformation = when (field.keyboardType) {
                            KeyboardType.Phone -> PhoneNumberVisualTransformation()
                            KeyboardType.Password -> if (!field.isValueVisible) {
                                PasswordVisualTransformation('•')
                            } else {
                                VisualTransformation.None
                            }

                            else -> VisualTransformation.None
                        },
                        singleLine = true,
                        hint = field.hint,
                        trailingIcon = {
                            if (field.keyboardType == KeyboardType.Password) {
                                PasswordIcon(valueIsVisible = field.isValueVisible) {
                                    onFieldVisibilityChange(field)
                                }
                            }
                        }
                    )

                }
            }


        }


        Spacer(modifier = Modifier.weight(1f))

        VodovozButton(
            modifier = Modifier.padding(vertical = 24.dp),
            text = stringResource(R.string.register_button_text),
            isLoading = buttonLoading,
            onClick = { onRegister() },
            enabled = buttonEnabled
        )
    }
}