package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.ui.model.HistoryUI

object HistoryMapper {

    fun List<HistoryEntity>.mapToUI(): List<HistoryUI> = mutableListOf<HistoryUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun HistoryEntity.mapToUI() = HistoryUI(
        id = id,
        detailPicture = detailPicture
    )

}