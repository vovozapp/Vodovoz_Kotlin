package com.vodovoz.app.feature.certificate_activation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.certificate_activation.composables.CertificateActivationBody
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationState
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationUiState

@Composable
fun CertificateActivationScreen(
    viewModel: CertificateActivationViewModel,
    viewState: CertificateActivationState,
) {
    Scaffold(
        topBar = {
            VodovozTopBar(onBack = { viewModel.navigateBack() }, title = viewState.title)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (viewState.uiState) {
            CertificateActivationUiState.Details -> {
                CertificateActivationBody(
                    modifier = Modifier.padding(paddingValues),
                    descriptionHtml = viewState.descriptionHtml,
                    secondDescriptionHtml = viewState.secondDescriptionHtml,
                    button = viewState.activationButton,
                    field = viewState.field,
                    buttonEnabled = viewState.activationButtonEnabled,
                    buttonIsLoading = viewState.activationButtonIsLoading,
                    onCertificateActivate = {
                        viewModel.activateCertificate()
                    },
                    onFieldValueChange = { field, newValue ->
                        viewModel.changeFieldValue(field, newValue)
                    }
                )
            }
            CertificateActivationUiState.Loading -> {
                LoadingPlaceholder(modifier = Modifier.padding(paddingValues))
            }

            else -> {

            }
        }

    }
}