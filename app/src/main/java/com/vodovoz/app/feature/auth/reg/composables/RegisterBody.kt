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
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.design_system.composables.button.VodovozButtonsColumn
import com.vodovoz.app.design_system.composables.decoration.AgreementRow
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextFieldsColumn
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.feature.preorder.model.FieldUi


@Suppress("NonSkippableComposable")
@Composable
fun RegisterBody(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    buttons: List<ColorfulButtonUi>,
    showAgreements: Boolean,
    agreementChecked: Boolean,
    agreementTextHtml: String,
    onFieldChange: (FieldUi, FieldUi) -> Unit,
    onRegister: () -> Unit,
    onHyperlinkClick: (String, Int) -> Unit,
    onAgreementCheck: (Boolean) -> Unit,
    onButtonClick: (ColorfulButtonUi) -> Unit,
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

        if (showAgreements) {
            AgreementRow(
                modifier = Modifier.padding(top = 24.dp),
                checked = agreementChecked,
                htmlText = agreementTextHtml,
                onCheckedChange = onAgreementCheck,
                onUrlClick = onHyperlinkClick
            )
        }

        VodovozButtonsColumn(
            modifier = Modifier.padding(vertical = 24.dp),
            buttons = buttons
        ) { btn ->
            onButtonClick(btn)
        }
    }
}