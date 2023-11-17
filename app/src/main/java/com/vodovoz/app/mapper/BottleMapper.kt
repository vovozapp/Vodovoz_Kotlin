package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BottleEntity
import com.vodovoz.app.ui.model.BottleUI

object BottleMapper {

    fun List<BottleEntity>.mapToUI() = map { it.mapToUI() }

    fun BottleEntity.mapToUI() = BottleUI(
        id = id,
        name = name
    )

}