package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.BottomCartEntity
import com.vodovoz.app.ui.model.BottomCartUI

object BottomCartMapper {

    fun BottomCartEntity.mapToUI() = BottomCartUI(
        totalSum = totalSum,
        quantity = quantity,
        productCount = productCount,
    )
}