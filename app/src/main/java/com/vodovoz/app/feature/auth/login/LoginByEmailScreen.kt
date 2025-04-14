package com.vodovoz.app.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.auth.login.composables.LoginByEmailBody
import com.vodovoz.app.feature.auth.login.model.LoginByEmailState

@Composable
fun LoginByEmailScreen(
    viewModel: LoginByEmailViewModel,
    viewState: LoginByEmailState,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = viewState.title
        )
        LoginByEmailBody(
            modifier = Modifier.weight(1f),
            fields = viewState.fields,
            description = viewState.description,
            buttons = viewState.buttons,
            showAgreement = viewState.showAgreement,
            agreementHtml = viewState.agreementHtml,
            agreementChecked = viewState.agreementChecked,
            onHyperlinkClick = { url, titleIndex ->
                viewModel.openAgreementUrl(url, titleIndex)
            },
            onAgreementCheck = { checked ->
                viewModel.checkAgreement(checked)
            },
            onButtonClick = { button ->
                viewModel.activateButton(button)
            },
            onFieldChange = { field, updatedField ->
                viewModel.changeField(field, updatedField)
            },
            onForgotPasswordClick = {
                viewModel.navigateToRecoveryPassword()
            }
        )

    }
}