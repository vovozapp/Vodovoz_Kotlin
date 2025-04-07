package com.vodovoz.app.design_system.composables.text_fields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.text.PhoneNumberVisualTransformation
import com.vodovoz.app.util.formatRussianPhoneNumber

@Composable
fun VodovozTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
    hint: String = "",
    label: String? = null,
    supportingText: String? = null,
    prefix: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
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
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        onValueChange = { newTextFieldValue ->
            onValueChange(newTextFieldValue)
        },
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
            if (!label.isNullOrEmpty()) {
                Text(
                    text = label,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(48.dp)
                    .border(
                        width = 1.dp,
                        color = if (isError) MaterialTheme.colorScheme.error else if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                prefix?.let {
                    Text(
                        modifier = Modifier.padding(end = 6.dp),
                        text = prefix,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = maxLines
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    this@Row.AnimatedVisibility(value.text.isEmpty(), exit = fadeOut(snap(0))) {
                        Text(
                            text = hint,
                            color = MaterialTheme.colorScheme.surfaceTint,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            maxLines = maxLines,
                            overflow = TextOverflow.Ellipsis
                        )

                    }

                    innerTextField()
                }
                trailingIcon?.let { trailingIcon() }
            }

            if (!supportingText.isNullOrEmpty()) {
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
    prefix: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    var textFieldValueState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = value))
    }
    val textFieldValue = textFieldValueState.copy(text = value)
    val haveFocus by interactionSource.collectIsFocusedAsState()
    val russianPhoneCode = stringResource(id = R.string.russian_phone_code)

    LaunchedEffect(haveFocus) {
        if (haveFocus && !textFieldValue.text.startsWith(russianPhoneCode) && visualTransformation is PhoneNumberVisualTransformation) {
            val newText = formatRussianPhoneNumber(value)
            textFieldValueState = textFieldValueState.copy(
                text = newText,
                selection = TextRange(russianPhoneCode.length)
            )
            onValueChange(newText)
        }
    }

    SideEffect {
        if (
            textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            textFieldValueState = textFieldValue
        }
    }

    var lastTextValue by remember(value) { mutableStateOf(value) }

    VodovozTextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { newTextFieldValueState ->
            val newText = formatRussianPhoneNumber(newTextFieldValueState.text)
            val newSelection =
                if (newTextFieldValueState.selection.start < russianPhoneCode.length && visualTransformation is PhoneNumberVisualTransformation) {
                    TextRange(russianPhoneCode.length)
                } else newTextFieldValueState.selection

            textFieldValueState = newTextFieldValueState.copy(
                text = newText,
                selection = newSelection
            )

            val stringChangedSinceLastInvocation = lastTextValue != newText
            lastTextValue = newText

            if (stringChangedSinceLastInvocation) {
                onValueChange(newText)
            }
        },
        supportingText = supportingText,
        label = label,
        textStyle = textStyle,
        hint = hint,
        prefix = prefix,
        enabled = enabled,
        readOnly = readOnly,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        isError = isError,
        singleLine = singleLine,
        interactionSource = interactionSource,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        maxLines = maxLines,
        minLines = minLines

    )

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
                supportingText = "",
                isError = false,
                prefix = "от"
            )
        }
    }
}
