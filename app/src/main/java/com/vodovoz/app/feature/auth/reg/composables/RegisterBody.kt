package com.vodovoz.app.feature.auth.reg.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
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

                    @Composable
                    fun PasswordIcon() {
                        Icon(
                            painter = if (field.isValueVisible) painterResource(id = R.drawable.icon_eye)
                            else painterResource(id = R.drawable.icon_eye_open),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onFieldVisibilityChange(field)
                                },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

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
                                PasswordIcon()
                            }
                        }
                    )

                }
            }


        }

        VodovozButton(
            modifier = Modifier.padding(top = 24.dp),
            text = stringResource(R.string.register_button_text),
            isLoading = buttonLoading,
            onClick = { onRegister() },
            enabled = buttonEnabled
        )
    }
}