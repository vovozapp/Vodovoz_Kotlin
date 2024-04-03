package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ProductDetailsBundleEntity
import com.vodovoz.app.feature.all.comments.model.CommentImage
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.mapper.ProductDetailMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.mapper.ServiceMapper.mapToUI
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI

object ProductDetailBundleMapper {
    fun ProductDetailsBundleEntity.mapToUI() = ProductDetailBundleUI(
        productDetailUI = productDetailEntity.mapToUI(),
        serviceUIList = serviceEntityList.mapToUI(),
        categoryUI = categoryEntity.mapToUI(),
        commentImages = commentImages?.map {
            CommentImage(it.ID, it.SRC)
        } ?: emptyList(),
        commentUIList = commentEntityList.mapToUI(),
        searchWordList = searchWordList,
        maybeLikeProductUIList = maybeLikeProductEntityList.mapToUI(),
        promotionsAction = promotionsActionEntity?.mapToUI(),
        recommendProductUIList = recommendProductEntityList.mapToUI(),
        buyWithProductUIList = buyWithProductEntityList.mapToUI(),
        replacementProductsCategoryDetail = replacementProductsCategoryDetail?.mapToUI()
    )

}