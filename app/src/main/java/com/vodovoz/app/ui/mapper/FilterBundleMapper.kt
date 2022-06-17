package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.FilterBundleEntity
import com.vodovoz.app.ui.mapper.FilterMapper.mapToUI
import com.vodovoz.app.ui.mapper.FilterPriceMapper.mapToUI
import com.vodovoz.app.ui.model.custom.FilterBundleUI

object FilterBundleMapper  {

    fun FilterBundleEntity.mapToUI() = FilterBundleUI(
        filterPriceUI = filterPriceEntity.mapToUI(),
        filterUIList = filterEntityList.mapToUI().toMutableList()
    )

}