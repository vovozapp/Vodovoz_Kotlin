package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.FreeShippingDaysInfoBundleEntity
import com.vodovoz.app.ui.model.FreeShippingDaysInfoBundleUI

object FreeShippingDaysInfoBundleMapper {

    fun FreeShippingDaysInfoBundleEntity.mapToUI() = FreeShippingDaysInfoBundleUI(
        title = title,
        info = info
    )

}