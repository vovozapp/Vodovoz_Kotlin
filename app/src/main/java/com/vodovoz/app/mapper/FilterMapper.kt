package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.FilterEntity
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.ValuesUI

object FilterMapper  {

    fun FilterEntity.mapToUI() = FilterUI(
        code = code,
        name = name,
        type = type,
        values = if(values != null) {
            ValuesUI(
                values.min,
                values.max
            )
        } else {
            null
        }

    )

    fun List<FilterEntity>.mapToUI(): List<FilterUI> = mutableListOf<FilterUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

}