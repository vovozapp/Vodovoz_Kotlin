package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.data.model.features.PastPurchasesHeaderBundleEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.custom.FavoriteProductsHeaderBundleUI
import com.vodovoz.app.ui.model.custom.PastPurchasesHeaderBundleUI

object PastPurchasesHeaderBundleMapper {

    fun PastPurchasesHeaderBundleEntity.mapToUI() = PastPurchasesHeaderBundleUI(
        availableTitle = availableTitle,
        notAvailableTitle = notAvailableTitle,
        favoriteCategoryUI = favoriteCategoryEntity?.mapToUI()
    )

}