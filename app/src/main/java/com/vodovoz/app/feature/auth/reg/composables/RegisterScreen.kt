package com.vodovoz.app.feature.auth.reg.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
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
fun RegisterScreen(viewModel: RegFlowViewModel, viewState: RegFlowViewModel.RegState, snackbarHostState: SnackbarHostState) {
    Scaffold(
        topBar = {
            VodovozTopBar(
                onBack = {
                    viewModel.navigateBack()
                },
                title = viewState.title.ifEmpty { stringResource(id = R.string.registration) }
            )
        },
        snackbarHost = {
            VodovozSnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
        ) {
            when (viewState.uiState) {
                RegFlowViewModel.UiState.Error -> {
                    NetworkErrorPlaceholder { viewModel.fetchFields() }
                }

                RegFlowViewModel.UiState.Loading -> {
                    LoadingPlaceholder()
                }

                RegFlowViewModel.UiState.Success -> {
                    RegisterBody(
                        fields = viewState.fields,
                        buttonEnabled = viewState.buttonEnabled,
                        buttonLoading = viewState.buttonLoading,
                        onFieldValueChange = { field, newValue ->
                            viewModel.changeFieldValue(field, newValue)
                        },
                        onFieldVisibilityChange = { field ->
                            viewModel.changeFieldVisibility(field)
                        },
                        onRegister = {
                            viewModel.register()
                        }
                    )
                }
            }
        }
    }
}