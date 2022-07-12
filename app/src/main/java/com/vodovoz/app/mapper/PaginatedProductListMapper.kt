package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PaginatedProductListEntity
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.custom.BrandProductListUI

object PaginatedProductListMapper {

    fun PaginatedProductListEntity.mapToUI() = BrandProductListUI(
        pageAmount = pageAmount,
        productUIList = productEntityList.mapToUI()
    )

}