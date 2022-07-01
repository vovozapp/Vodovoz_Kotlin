package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.ProductDetailBundleEntity
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.mapper.CommentMapper.mapToUI
import com.vodovoz.app.ui.mapper.ProductDetailMapper.mapToUI
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.mapper.ServiceMapper.mapToUI
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI

object ProductDetailBundleMapper {
    fun ProductDetailBundleEntity.mapToUI() = ProductDetailBundleUI(
          productDetailUI = productDetailEntity.mapToUI(),
          serviceUIList = serviceEntityList.mapToUI(),
          categoryUI = categoryEntity.mapToUI(),
          commentUIList = commentEntityList.mapToUI(),
          searchWordList = searchWordList,
          maybeLikeProductUIList = maybeLikeProductEntityList.mapToUI(),
          promotionUIList = promotionEntityList.mapToUI(),
          recommendProductUIList = recommendProductEntityList.mapToUI(),
          buyWithProductUIList = buyWithProductEntityList.mapToUI()
    )

}