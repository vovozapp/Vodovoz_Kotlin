package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.CartBundleEntity
import com.vodovoz.app.data.model.features.GiftProductEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.custom.CartBundleUI
import com.vodovoz.app.ui.model.custom.GiftProductUI

object CartBundleMapper {

    fun CartBundleEntity.mapUoUI() = CartBundleUI(
        giftMessageBottom = giftMessageBottom,
        giftMessage = giftMessage,
        infoMessage = infoMessage,
        availableProductUIList = availableProductEntityList.mapToUI(),
        notAvailableProductUIList = notAvailableProductEntityList.mapToUI(),
        giftProductUI = giftProductEntity?.mapToUI(),
        bestForYouCategoryDetailUI = bestForYouCategoryDetailEntity?.mapToUI(),
        giftTitleBottom = giftTitleBottom
    )

    fun GiftProductEntity.mapToUI() = GiftProductUI(
        title = title,
        productsList = productsList.mapToUI()
    )

}