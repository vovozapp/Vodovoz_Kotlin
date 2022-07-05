package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.AboutServicesBundleEntity
import com.vodovoz.app.ui.mapper.ServiceMapper.mapToUI
import com.vodovoz.app.ui.model.custom.AboutServicesBundleUI

object AboutServicesBundleMapper {

    fun AboutServicesBundleEntity.mapToUI() = AboutServicesBundleUI(
        title = title,
        detail = detail,
        serviceUIList = serviceEntityList.mapToUI()
    )

}