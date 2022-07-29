package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.DiscountCardPropertyEntity

class ActivateDiscountCardBundleEntity(
    val title: String = "",
    val details: String = "",
    val discountCardPropertyEntityList: List<DiscountCardPropertyEntity>
)