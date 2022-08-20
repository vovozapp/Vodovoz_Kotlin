package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PayMethodEntity
import com.vodovoz.app.ui.model.PayMethodUI

object PayMethodMapper {

    fun List<PayMethodEntity>.mapToUI(): List<PayMethodUI> = mutableListOf<PayMethodUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }
    fun PayMethodEntity.mapToUI() = PayMethodUI(
        id = id,
        name = name
    )

}