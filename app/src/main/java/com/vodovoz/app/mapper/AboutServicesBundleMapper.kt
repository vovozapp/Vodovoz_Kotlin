package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.AboutServicesBundleEntity
import com.vodovoz.app.mapper.ServiceMapper.mapToUI
import com.vodovoz.app.ui.model.custom.AboutServicesBundleUI

object AboutServicesBundleMapper {

    fun AboutServicesBundleEntity.mapToUI() = AboutServicesBundleUI(
        title = title,
        detail = detail,
        serviceUIList = serviceEntityList.mapToUI()
    )

}