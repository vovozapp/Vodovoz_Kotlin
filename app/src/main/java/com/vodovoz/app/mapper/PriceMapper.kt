package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PriceEntity
import com.vodovoz.app.ui.model.PriceUI

object PriceMapper  {

    fun List<PriceEntity>.mapToUI(): List<PriceUI> = mutableListOf<PriceUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun PriceEntity.mapToUI() = PriceUI(
        currentPrice = price,
        oldPrice = oldPrice,
        requiredAmount = requiredAmount,
        requiredAmountTo = requiredAmountTo
    )

}