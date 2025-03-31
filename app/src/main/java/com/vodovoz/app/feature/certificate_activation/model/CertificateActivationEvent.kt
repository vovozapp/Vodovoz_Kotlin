package com.vodovoz.app.feature.certificate_activation.model

sealed interface CertificateActivationEvent {
    data class GoToWebView(val url: String) : CertificateActivationEvent

    data object GoBack : CertificateActivationEvent

}