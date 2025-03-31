package com.vodovoz.app.feature.profile.change_password.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.decoration.PasswordIcon
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextFieldsColumn
import com.vodovoz.app.design_system.text.PhoneNumberVisualTransformation
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun ChangePasswordBody(
    modifier: Modifier = Modifier,
    fields: List<FieldUi>,
    buttonLoading: Boolean,
    buttonEnabled: Boolean,
    onFieldChange: (FieldUi, FieldUi) -> Unit,
    onUpdatePasswordClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .imePadding()
            .systemBarsPadding()
            .consumeWindowInsets(WindowInsets.ime)
            .consumeWindowInsets(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        VodovozTextFieldsColumn(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            fields = fields,
            onFieldChange = { field, updatedField ->
                onFieldChange(field, updatedField)
            },
            onDone = {
                onUpdatePasswordClick()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        VodovozButton(
            modifier = Modifier.padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            text = stringResource(R.string.change),
            enabled = buttonEnabled,
            isLoading = buttonLoading,
            onClick = {
                onUpdatePasswordClick()
            },
        )

    }

}