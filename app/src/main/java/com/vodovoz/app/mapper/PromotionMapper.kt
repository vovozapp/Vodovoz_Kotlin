package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PromotionEntity
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionUI

object PromotionMapper  {

    fun List<PromotionEntity>.mapToUI(): List<PromotionUI> = mutableListOf<PromotionUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun PromotionEntity.mapToUI() = PromotionUI(
        id = id,
        name = name,
        detailPicture = detailPicture,
        statusColor = statusColor,
        customerCategory = customerCategory,
        timeLeft = timeLeft,
        productUIList = productEntityList.mapToUI(),
    )

}