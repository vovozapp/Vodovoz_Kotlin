package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.CertificatePropertyEntity

class ActivateCertificateBundleEntity(
    val title: String = "",
    val details: String = "",
    val certificatePropertyEntityList: List<CertificatePropertyEntity>
)