package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.mapper.PriceMapper.mapToUI
import com.vodovoz.app.ui.model.ProductUI

object ProductMapper {

    fun List<ProductEntity>.mapToUI(): List<ProductUI> = mutableListOf<ProductUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun ProductEntity.mapToUI() = ProductUI(
        id = id,
        name = name,
        isBottle = isBottle,
        detailPicture = detailPicture,
        isFavorite = isFavorite,
        leftItems = leftItems,
        pricePerUnit = pricePerUnit,
        priceList = priceList.mapToUI(),
        status = status,
        statusColor = statusColor,
        rating = rating,
        cartQuantity = cartQuantity,
        commentAmount = commentAmount,
        orderQuantity = orderQuantity,
        depositPrice = depositPrice,
        isGift = isGift,
        replacementProductUIList = replacementProductEntityList.mapToUI(),
        detailPictureList = detailPictureList
    )

}
