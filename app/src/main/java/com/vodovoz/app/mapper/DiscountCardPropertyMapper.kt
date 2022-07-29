package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.DiscountCardPropertyEntity
import com.vodovoz.app.ui.model.custom.DiscountCardPropertyUI

object DiscountCardPropertyMapper {

    fun List<DiscountCardPropertyEntity>.mapToUI(): List<DiscountCardPropertyUI> =
        mutableListOf<DiscountCardPropertyUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun DiscountCardPropertyEntity.mapToUI() = DiscountCardPropertyUI(
        id = id,
        name = name,
        code = code,
        value = value
    )

}