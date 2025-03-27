package com.vodovoz.app.feature.profile.change_password

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
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.profile.change_password.composables.ChangePasswordBody
import com.vodovoz.app.feature.profile.change_password.model.ChangePasswordState

@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    viewState: ChangePasswordState,
    snackbarHostState: SnackbarHostState,
) {

    Scaffold(
        topBar = {
            VodovozTopBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                onBack = { viewModel.navigateBack() },
                title = viewState.title
            )
        },
        snackbarHost = {
            VodovozSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        ChangePasswordBody(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            fields = viewState.fields,
            buttonEnabled = viewState.buttonEnabled,
            buttonLoading = viewState.buttonLoading,
            onFieldValueChange = { field, newValue ->
                viewModel.changeFieldValue(field, newValue)
            },
            onUpdatePasswordClick = {
                viewModel.updatePassword()
            },
            onFieldVisibilityChange = { field, newVisibility ->
                viewModel.changeFieldValueVisibility(field, newVisibility)
            }
        )
    }
}