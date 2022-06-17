package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.FilterEntity
import com.vodovoz.app.ui.model.FilterUI

object FilterMapper  {

    fun FilterEntity.mapToUI() = FilterUI(
        code = code,
        name = name
    )

    fun List<FilterEntity>.mapToUI(): List<FilterUI> = mutableListOf<FilterUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

}