package com.vodovoz.app.feature.certificate_activation.model

sealed interface CertificateActivationUiState {

    data object Loading: CertificateActivationUiState
    data object Details: CertificateActivationUiState
    data object Error: CertificateActivationUiState
    data class CertificateActivated(val message: String) : CertificateActivationUiState

}