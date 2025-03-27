package com.vodovoz.app.feature.preorder.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.text.PhoneNumberVisualTransformation
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun PreOrderBody(
    modifier: Modifier = Modifier,
    colorfulButton: ColorfulButtonUi,
    fields: List<FieldUi>,
    snackbarHostState: SnackbarHostState,
    onOrderSend: () -> Unit,
    onFieldValueChange: (FieldUi, String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                fields.forEachIndexed { index, field ->
                    val isMessage = field.id.contains("message", true)

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
                                onDone = {
                                    onOrderSend()
                                },
                            ),
                            supportingText = field.supportingText.ifEmpty { null },
                            readOnly = field.readOnly,
                            label = field.label.ifEmpty { null },
                            visualTransformation = if (field.keyboardType == KeyboardType.Phone) PhoneNumberVisualTransformation() else VisualTransformation.None,
                            singleLine = !isMessage,
                            maxLines = if (isMessage) 3 else 1,
                            minLines = if (isMessage) 2 else 1
                        )
                    }
                }
            }



            VodovozButton(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                text = colorfulButton.name,
                onClick = {
                    onOrderSend()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorfulButton.backgroundColor,
                    contentColor = colorfulButton.textColor
                )
            )

        }

        VodovozSnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

}
