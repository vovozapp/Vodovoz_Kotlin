package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.DefaultSearchDataBundleEntity
import com.vodovoz.app.data.model.common.QuickSearchDataBundleEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.model.custom.DefaultSearchDataBundleUI
import com.vodovoz.app.ui.model.custom.QuickSearchDataBundleUI

object QuickSearchDataBundleMapper {

    fun QuickSearchDataBundleEntity.mapToUI() = QuickSearchDataBundleUI(
        quickQueryList = quickQueryList,
        quickProductsCategoryDetailUI = quickProductsCategoryDetailEntity?.mapToUI()
    )
}