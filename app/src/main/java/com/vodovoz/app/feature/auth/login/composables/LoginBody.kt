package com.vodovoz.app.feature.auth.login.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
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
fun LoginBody(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    description: String,
    showAgreements: Boolean,
    showRegisterText: Boolean,
    agreementChecked: Boolean,
    subscribeChecked: Boolean,
    agreementTextHtml: String,
    buttons: List<ColorfulButtonUi>,
    onFieldChange: (FieldUi, FieldUi) -> Unit,
    onButtonClick: (ColorfulButtonUi) -> Unit,
    onHyperlinkClick: (String, Int) -> Unit,
    onAgreementCheck: (Boolean) -> Unit,
    onSubscribeCheck: (Boolean) -> Unit,
    onRegisterTextClick: () -> Unit,
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
            onDone = {  }
        )


        Column(
            modifier = Modifier.padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showAgreements) {
                AgreementRow(
                    checked = agreementChecked,
                    htmlText = agreementTextHtml,
                    onCheckedChange = onAgreementCheck,
                    onUrlClick = onHyperlinkClick
                )
            }
            AgreementRow(
                checked = subscribeChecked,
                htmlText = stringResource(id = R.string.subscribe_on_mailing_list),
                onCheckedChange = onSubscribeCheck,
                onUrlClick = onHyperlinkClick
            )
        }

        Column(modifier = Modifier.padding(vertical = 24.dp),verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

        if (showRegisterText) {
            RegisterRow(modifier = Modifier.padding(bottom = 16.dp)) { onRegisterTextClick() }
        }
    }
}

@Composable
private fun RegisterRow(modifier: Modifier = Modifier, onRegisterClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable { onRegisterClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.have_not_account_text),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = R.string.create_account_btn_text),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onRegisterClick() }
        )
    }
}