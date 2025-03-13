package com.vodovoz.app.feature.certificate_activation.model

sealed interface CertificateActivationEvent {

    data object GoBack: CertificateActivationEvent

}