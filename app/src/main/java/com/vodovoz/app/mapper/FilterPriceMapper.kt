package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.FilterPriceEntity
import com.vodovoz.app.ui.model.FilterPriceUI

object FilterPriceMapper  {

    fun FilterPriceEntity.mapToUI() = FilterPriceUI(
        minPrice = minPrice,
        maxPrice = maxPrice
    )

}