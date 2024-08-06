package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BlockEntity
import com.vodovoz.app.data.model.common.ProductDetailEntity
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.mapper.PriceMapper.mapToUI
import com.vodovoz.app.mapper.PropertiesGroupMapper.mapToUI
import com.vodovoz.app.ui.model.BlockUI
import com.vodovoz.app.ui.model.ButtonUI
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
        rutubeVideoCode = rutubeVideoCode,
        isAvailable = isAvailable,
        rating = rating,
        status = status,
        leftItems = leftItems,
        statusColor = statusColor,
        consumerInfo = consumerInfo,
        pricePerUnit = pricePerUnit,
        cartQuantity = cartQuantity,
        commentsAmount = commentsAmount,
        commentsAmountText = commentsAmountText,
        brandUI = brandEntity?.mapToUI(),
        priceUIList = priceEntityList.mapToUI(),
        propertiesGroupUIList = propertiesGroupEntityList.mapToUI(),
        detailPictureList = detailPictureList,
        blockList = blockList.mapToUI()
    )

    fun List<BlockEntity>.mapToUI() = this.map { BlockUI(
        description = it.description,
        button = ButtonUI(name = it.button.name, background = it.button.background, textColor = it.button.textColor),
        productId = it.productId,
        extProductId = it.extProductId
    ) }

}