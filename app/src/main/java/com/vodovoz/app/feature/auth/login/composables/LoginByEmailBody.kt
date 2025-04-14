package com.vodovoz.app.feature.auth.login.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.design_system.composables.decoration.AgreementRow
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextFieldsColumn
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun LoginByEmailBody(
    modifier: Modifier = Modifier,
    description: String,
    fields: List<FieldUi>,
    buttons: List<ColorfulButtonUi>,
    showAgreement: Boolean,
    agreementHtml: String,
    agreementChecked: Boolean,
    onAgreementCheck: (Boolean) -> Unit,
    onHyperlinkClick: (String, Int) -> Unit,
    onFieldChange: (FieldUi, FieldUi) -> Unit,
    onButtonClick: (ColorfulButtonUi) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = description,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )

        VodovozTextFieldsColumn(
            modifier = Modifier.padding(top = 24.dp),
            fields = fields,
            onFieldChange = onFieldChange,
            onDone = { }
        )

        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onForgotPasswordClick),
            text = stringResource(id = R.string.forgot_password),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )

        if (showAgreement) {
            AgreementRow(
                modifier = Modifier.padding(top = 24.dp),
                checked = agreementChecked,
                htmlText = agreementHtml,
                onCheckedChange = onAgreementCheck,
                onUrlClick = onHyperlinkClick
            )
        }

        Column(
            modifier = Modifier.padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            buttons.forEach { button ->
                VodovozButton(
                    text = button.name,
                    onClick = { onButtonClick(button) },
                    colors = VodovozButtonDefaults.primaryColors().copy(
                        containerColor = button.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary },
                        contentColor = button.textColor.takeOrElse { MaterialTheme.colorScheme.background }
                    ),
                    enabled = button.enabled,
                    isLoading = button.loading,
                )
            }
        }

    }
}
