package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.FilterValueEntity
import com.vodovoz.app.ui.model.FilterValueUI

object FilterValueMapper {

    fun FilterValueEntity.mapToUI() = FilterValueUI(
        id = id,
        value = value
    )

    fun List<FilterValueEntity>.mapToUI(): List<FilterValueUI> = mutableListOf<FilterValueUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

}