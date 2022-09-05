package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BottleEntity
import com.vodovoz.app.ui.model.BottleUI

object BottleMapper {

    fun List<BottleEntity>.mapToUI(): List<BottleUI> = mutableListOf<BottleUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun BottleEntity.mapToUI() = BottleUI(
        id = id,
        name = name
    )

}