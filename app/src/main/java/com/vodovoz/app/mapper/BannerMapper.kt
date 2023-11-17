package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.ui.model.BannerUI

object BannerMapper {

    fun List<BannerEntity>.mapToUI() = map { it.mapToUI() }

    fun BannerEntity.mapToUI() = BannerUI(
        id = id,
        detailPicture = detailPicture,
        actionEntity = actionEntity,
        advEntity = bannerAdvEntity
    )

}