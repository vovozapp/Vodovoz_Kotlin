package com.vodovoz.app.feature.certificate_activation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.vodovozTextLinkStyle
import com.vodovoz.app.feature.preorder.model.FieldUi

@Composable
fun CertificateActivationBody(
    modifier: Modifier = Modifier,
    descriptionHtml: String,
    secondDescriptionHtml: String,
    button: ColorfulButtonUi,
    buttonEnabled: Boolean,
    buttonIsLoading: Boolean,
    field: FieldUi,
    onCertificateActivate: () -> Unit,
    onFieldValueChange: (FieldUi, String) -> Unit,
    onHyperlinkClick: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {


        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = AnnotatedString.fromHtml(descriptionHtml, vodovozTextLinkStyle),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )

        VodovozTextField(
            modifier = Modifier.padding(top = 24.dp),
            value = field.value,
            onValueChange = { s ->
                onFieldValueChange(field, s)
            },
            hint = field.hint,
            label = field.label,
            isError = field.isError,
            readOnly = field.readOnly,
            keyboardOptions = KeyboardOptions(
                keyboardType = field.keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onCertificateActivate() }
            ),
            supportingText = field.supportingText,
        )

        val buttonBackgroundColor =
            button.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary }
        val buttonTextColor = button.textColor.takeOrElse { MaterialTheme.colorScheme.background }

        VodovozButton(
            modifier = Modifier.padding(top = 24.dp),
            text = button.name,
            onClick = { onCertificateActivate() },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBackgroundColor,
                contentColor = buttonTextColor,
                disabledContentColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = ExtendedTheme.colorScheme.primaryVariant
            ),
            enabled = buttonEnabled,
            isLoading = buttonIsLoading
        )

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = AnnotatedString.fromHtml(
                secondDescriptionHtml,
                vodovozTextLinkStyle
            ) { linkAnnotation ->
                if (linkAnnotation is LinkAnnotation.Url) onHyperlinkClick(
                    linkAnnotation.url
                )
            },
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.labelSmall
        )
    }
}