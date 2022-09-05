package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.CartBundleEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.custom.CartBundleUI

object CartBundleMapper {

    fun CartBundleEntity.mapUoUI() = CartBundleUI(
        giftMessage = giftMessage,
        infoMessage = infoMessage,
        availableProductUIList = availableProductEntityList.mapToUI(),
        notAvailableProductUIList = notAvailableProductEntityList.mapToUI(),
        giftProductUIList = giftProductEntityList.mapToUI(),
        bestForYouCategoryDetailUI = bestForYouCategoryDetailEntity?.mapToUI()
    )

}