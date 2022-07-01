package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.features.CartBundleEntity
import com.vodovoz.app.ui.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.custom.CartBundleUI

object CartBundleMapper {

    fun CartBundleEntity.mapUoUI() = CartBundleUI(
        giftMessage = giftMessage,
        availableProductUIList = availableProductEntityList.mapToUI(),
        notAvailableProductUIList = notAvailableProductEntityList.mapToUI(),
        giftProductUIList = giftProductEntityList.mapToUI(),
        bestForYouCategoryDetailUI = bestForYouCategoryDetailEntity?.mapToUI()
    )

}