package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.DefaultSearchDataBundleEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.model.custom.DefaultSearchDataBundleUI

object DefaultSearchDataBundleMapper {

    fun DefaultSearchDataBundleEntity.mapToUI() = DefaultSearchDataBundleUI(
        popularQueryList = popularQueryList,
        popularProductsCategoryDetailUI = popularProductsCategoryDetailEntity.mapToUI()
    )

}