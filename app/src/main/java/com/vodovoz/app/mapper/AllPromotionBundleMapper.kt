package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.AllPromotionsBundleEntity
import com.vodovoz.app.mapper.PromotionFilterMapper.mapToUI
import com.vodovoz.app.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.model.custom.AllPromotionBundleUI

object AllPromotionBundleMapper {

    fun AllPromotionsBundleEntity.mapToUI() = AllPromotionBundleUI(
        promotionUIList = promotionEntityList.mapToUI(),
        promotionFilterUIList = promotionFilterEntityList.mapToUI()
    )

}