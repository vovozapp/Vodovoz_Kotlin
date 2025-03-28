package com.vodovoz.app.feature.auth.login.composables

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
import androidx.compose.material3.Text
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
fun LoginBody(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    description: String,
    mainButton: ColorfulButtonUi,
    mainButtonEnabled: Boolean,
    mainButtonLoading: Boolean,
    navigationButton: ColorfulButtonUi,
    showAgreements: Boolean,
    agreementChecked: Boolean,
    subscribeChecked: Boolean,
    agreementTextHtml: String,
    onFieldChange: (FieldUi, FieldUi) -> Unit,
    onMainButtonClick: () -> Unit,
    onNavigationButtonClick: () -> Unit,
    onHyperlinkClick: (String, Int) -> Unit,
    onAgreementCheck: (Boolean) -> Unit,
    onSubscribeCheck: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )

        VodovozTextFieldsColumn(
            modifier = Modifier.padding(top = 24.dp),
            fields = fields,
            onFieldChange = onFieldChange,
            onDone = {

            }
        )


        if (showAgreements) {
            Column(
                modifier = Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AgreementRow(
                    checked = agreementChecked,
                    htmlText = agreementTextHtml,
                    onCheckedChange = onAgreementCheck,
                    onUrlClick = onHyperlinkClick
                )

                AgreementRow(
                    checked = subscribeChecked,
                    htmlText = stringResource(id = R.string.subscribe_on_mailing_list),
                    onCheckedChange = onSubscribeCheck,
                    onUrlClick = onHyperlinkClick
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        VodovozButton(
            text = mainButton.name,
            onClick = onMainButtonClick,
            colors = VodovozButtonDefaults.primaryColors().copy(
                containerColor = mainButton.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary },
                contentColor = mainButton.textColor.takeOrElse { MaterialTheme.colorScheme.background }
            ),
            enabled = mainButtonEnabled,
            isLoading = mainButtonLoading
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