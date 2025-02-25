package com.vodovoz.app.design_system.composables.text_fields

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme


@Composable
fun VodovozTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
    hint: String = "",
    label: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        modifier = modifier.fillMaxWidth(),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines
    ) { innerTextField ->
        Column {
            label?.let {
                Text(
                    text = label,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .heightIn(48.dp)
                    .border(
                        width = 1.dp,
                        color = if (isError) MaterialTheme.colorScheme.error else if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty() && !isFocused) {

                    Text(
                        text = hint,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                    )
                }
                innerTextField()
            }
            supportingText?.let {
                Text(
                    text = supportingText,
                    color = if (!isError) {
                        MaterialTheme.colorScheme.surfaceTint
                    } else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun VodovozTextFieldPreview() {
    VodovozTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(300.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {

            VodovozTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = "",
                hint = "Введите значение",
                onValueChange = {},
                label = "Поле ввода",
                supportingText = "Текст в помощь",
                isError = true
            )
        }
    }
}
