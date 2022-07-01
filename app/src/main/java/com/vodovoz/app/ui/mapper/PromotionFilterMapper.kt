package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.PromotionFilterEntity
import com.vodovoz.app.ui.model.PromotionFilterUI

object PromotionFilterMapper {

    fun List<PromotionFilterEntity>.mapToUI(): List<PromotionFilterUI> = mutableListOf<PromotionFilterUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun PromotionFilterEntity.mapToUI() = PromotionFilterUI(
        id = id,
        name = name,
        code = code
    )

}