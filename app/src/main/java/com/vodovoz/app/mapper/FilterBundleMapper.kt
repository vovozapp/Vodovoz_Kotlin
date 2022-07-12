package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.FilterBundleEntity
import com.vodovoz.app.mapper.FilterMapper.mapToUI
import com.vodovoz.app.mapper.FilterPriceMapper.mapToUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI

object FilterBundleMapper  {

    fun FilterBundleEntity.mapToUI() = FiltersBundleUI(
        filterPriceUI = filterPriceEntity.mapToUI(),
        filterUIList = filterEntityList.mapToUI().toMutableList()
    )

}