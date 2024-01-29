package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.ActivateCertificateBundleEntity
import com.vodovoz.app.ui.model.custom.ActivateCertificateBundleUI

object ActivateCertificateBundleMapper {

    fun ActivateCertificateBundleEntity.mapToUI() = ActivateCertificateBundleUI(
        title = title,
        details = details,
        certificatePropertyUIList = certificatePropertyEntityList.mapToUI()
    )

}