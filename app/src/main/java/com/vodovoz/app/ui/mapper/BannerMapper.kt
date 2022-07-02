package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.ui.model.BannerUI

object BannerMapper {

    fun List<BannerEntity>.mapToUI(): List<BannerUI> = mutableListOf<BannerUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun BannerEntity.mapToUI() = BannerUI(
        id = id,
        detailPicture = detailPicture,
        actionEntity = actionEntity
    )

}