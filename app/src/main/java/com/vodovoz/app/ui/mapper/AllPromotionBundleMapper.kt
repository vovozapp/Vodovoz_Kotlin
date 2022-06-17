package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.features.AllPromotionBundleEntity
import com.vodovoz.app.ui.mapper.PromotionFilterMapper.mapToUI
import com.vodovoz.app.ui.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.model.custom.AllPromotionBundleUI

object AllPromotionBundleMapper {

    fun AllPromotionBundleEntity.mapToUI() = AllPromotionBundleUI(
        promotionUIList = promotionEntityList.mapToUI(),
        promotionFilterUIList = promotionFilterEntityList.mapToUI()
    )

}