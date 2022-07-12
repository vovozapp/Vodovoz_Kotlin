package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ProductDetailEntity
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.mapper.PriceMapper.mapToUI
import com.vodovoz.app.mapper.PropertiesGroupMapper.mapToUI
import com.vodovoz.app.ui.model.ProductDetailUI

object ProductDetailMapper  {

    fun ProductDetailEntity.mapToUI() = ProductDetailUI(
        id = id,
        name = name,
        previewText = previewText,
        shareUrl = shareUrl,
        detailText = detailText,
        isFavorite = isFavorite,
        youtubeVideoCode = youtubeVideoCode,
        rating = rating,
        status = status,
        statusColor = statusColor,
        consumerInfo = consumerInfo,
        pricePerUnit = pricePerUnit,
        cartQuantity = cartQuantity,
        commentsAmount = commentsAmount,
        brandUI = brandEntity?.mapToUI(),
        priceUIList = priceEntityList.mapToUI(),
        propertiesGroupUIList = propertiesGroupEntityList.mapToUI(),
        detailPictureList = detailPictureList,
    )

}