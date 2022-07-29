package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.LinkEntity
import com.vodovoz.app.ui.model.LinkUI

object LinkMapper {

    fun List<LinkEntity>.mapToUI(): List<LinkUI> = mutableListOf<LinkUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun LinkEntity.mapToUI() = LinkUI(
        name = name,
        link = link
    )

}