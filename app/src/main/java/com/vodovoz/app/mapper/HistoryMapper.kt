package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.mapper.BannerMapper.mapToUI
import com.vodovoz.app.ui.model.HistoryUI

object HistoryMapper {

    fun List<HistoryEntity>.mapToUI(): List<HistoryUI> = mutableListOf<HistoryUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun HistoryEntity.mapToUI() = HistoryUI(
        id = id,
        detailPicture = detailPicture,
        bannerUIList = bannerEntityList.mapToUI()
    )

}