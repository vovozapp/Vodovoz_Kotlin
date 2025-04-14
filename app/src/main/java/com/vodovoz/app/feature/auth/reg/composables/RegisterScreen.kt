package com.vodovoz.app.feature.auth.reg.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.auth.reg.RegFlowViewModel

@Suppress("NonSkippableComposable")
@Composable
fun RegisterScreen(
    viewModel: RegFlowViewModel,
    viewState: RegFlowViewModel.RegState,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        topBar = {
            VodovozTopBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                title = viewState.title.ifEmpty { stringResource(id = R.string.registration) },
                onBack = {
                    viewModel.navigateBack()
                }
            )
        },
        snackbarHost = {
            VodovozSnackbarHost(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                hostState = snackbarHostState,
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .windowInsetsPadding(WindowInsets.navigationBars),
        ) {
            when (viewState.uiState) {
                RegFlowViewModel.UiState.Error -> {
                    NetworkErrorPlaceholder { viewModel.fetchRegisterDetails() }
                }

                RegFlowViewModel.UiState.Loading -> {
                    LoadingPlaceholder()
                }

                RegFlowViewModel.UiState.Success -> {
                    RegisterBody(
                        fields = viewState.fields,
                        showAgreements = viewState.showAgreement,
                        agreementChecked = viewState.agreementChecked,
                        agreementTextHtml = viewState.agreementTextHtml,
                        buttons = viewState.buttons,
                        onHyperlinkClick = { url, urlIndex ->
                            viewModel.openAgreementUrl(url, urlIndex)
                        },
                        onAgreementCheck = { checked ->
                            viewModel.checkAgreement(checked)
                        },
                        onFieldChange = { field, updatedField ->
                            viewModel.changeField(field, updatedField)
                        },
                        onRegister = {
                            viewModel.register()
                        },
                        onButtonClick = { button ->
                            viewModel.activateButton(button)
                        }
                    )
                }
            }
        }
    }
}