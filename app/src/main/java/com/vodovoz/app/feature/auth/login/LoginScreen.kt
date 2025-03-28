package com.vodovoz.app.feature.auth.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.auth.login.composables.LoginBody

@Suppress("NonSkippableComposable")
@Composable
fun LoginScreen(viewModel: LoginFlowViewModel, viewState: LoginFlowViewModel.LoginState) {
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = viewState.title
        )
        LoginBody(
            fields = viewState.fields,
            description = viewState.description,
            mainButton = viewState.mainButton,
            mainButtonEnabled = viewState.mainButtonEnabled,
            mainButtonLoading = viewState.mainButtonLoading,
            navigationButton = viewState.navigationButton,
            showAgreements = viewState.showAgreements,
            agreementTextHtml = viewState.agreementTextHtml,
            agreementChecked = viewState.agreementChecked,
            subscribeChecked = viewState.subscribeChecked,
            onFieldChange = { field, updatedField ->
                viewModel.changeField(field, updatedField)
            },
            onMainButtonClick = {

            },
            onNavigationButtonClick = {
                viewModel.navigateToLoginByEmail()
            },
            onAgreementCheck = {
                viewModel.checkAgreement(it)
            },
            onHyperlinkClick = { url, index ->
                viewModel.openAgreementUrl(url, index)
            },
            onSubscribeCheck = {
                viewModel.checkSubscribe(it)
            }
        )
    }
}