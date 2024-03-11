package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.QuickQueryBundle
import com.vodovoz.app.data.model.common.QuickSearchDataBundleEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.model.custom.QuickQueryBundleUI
import com.vodovoz.app.ui.model.custom.QuickSearchDataBundleUI

object QuickSearchDataBundleMapper {

    fun QuickSearchDataBundleEntity.mapToUI() = QuickSearchDataBundleUI(
        quickQueryBundleUI = quickQueryBundle?.mapToUI(),
        quickProductsCategoryDetailUI = quickProductsCategoryDetailEntity?.mapToUI()

    )

    fun QuickQueryBundle.mapToUI() = QuickQueryBundleUI(
        name, errorText, quickQueryList
    )
}