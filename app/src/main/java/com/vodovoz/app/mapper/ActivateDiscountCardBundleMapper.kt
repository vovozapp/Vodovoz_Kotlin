package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.ActivateDiscountCardBundleEntity
import com.vodovoz.app.mapper.DiscountCardPropertyMapper.mapToUI
import com.vodovoz.app.ui.model.custom.ActivateDiscountCardBundleUI

object ActivateDiscountCardBundleMapper {

    fun ActivateDiscountCardBundleEntity.mapToUI() = ActivateDiscountCardBundleUI(
        title = title,
        details = details,
        discountCardPropertyUIList = discountCardPropertyEntityList.mapToUI()
    )

}