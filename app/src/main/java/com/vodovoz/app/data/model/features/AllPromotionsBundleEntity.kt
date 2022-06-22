package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.FilterValueEntity
import com.vodovoz.app.data.model.common.PromotionEntity
import com.vodovoz.app.data.model.common.PromotionFilterEntity

class AllPromotionsBundleEntity(
    val promotionFilterEntityList: List<PromotionFilterEntity>,
    val promotionEntityList: List<PromotionEntity>
)