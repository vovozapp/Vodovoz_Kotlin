package com.vodovoz.app.feature.auth.reg.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun RegisterBody(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    navigationButton: ColorfulButtonUi,
    button: ColorfulButtonUi,
    showAgreements: Boolean,
    agreementChecked: Boolean,
    subscribeChecked: Boolean,
    agreementTextHtml: String,
    buttonEnabled: Boolean,
    buttonLoading: Boolean,
    onFieldChange: (FieldUi, FieldUi) -> Unit,
    onRegister: () -> Unit,
    onHyperlinkClick: (String, Int) -> Unit,
    onAgreementCheck: (Boolean) -> Unit,
    onSubscribeCheck: (Boolean) -> Unit,
    onMainButtonClick: () -> Unit,
    onNavigationButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        VodovozTextFieldsColumn(
            modifier = Modifier.padding(top = 8.dp),
            fields = fields,
            onFieldChange = { field, updatedField ->
                onFieldChange(field, updatedField)
            },
            onDone = { onRegister() }
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


        Spacer(modifier = Modifier.height(24.dp))

        VodovozButton(
            text = button.name,
            onClick = onMainButtonClick,
            colors = VodovozButtonDefaults.primaryColors().copy(
                containerColor = button.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary },
                contentColor = button.textColor.takeOrElse { MaterialTheme.colorScheme.background }
            ),
            enabled = buttonEnabled,
            isLoading = buttonLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        VodovozButton(
            modifier = Modifier.padding(bottom = 24.dp),
            text = navigationButton.name,
            onClick = onNavigationButtonClick,
            colors = VodovozButtonDefaults.secondaryColors().copy(
                containerColor = navigationButton.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primaryContainer },
                contentColor = navigationButton.textColor.takeOrElse { MaterialTheme.colorScheme.primary }
            )
        )
    }
}